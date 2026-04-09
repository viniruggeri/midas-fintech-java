package com.fiap.midasfintech.controller;

import com.fiap.midasfintech.dto.request.AuthRequestDto;
import com.fiap.midasfintech.dto.request.RefreshTokenRequestDto;
import com.fiap.midasfintech.dto.response.AuthTokenResponseDto;
import com.fiap.midasfintech.entity.AppUser;
import com.fiap.midasfintech.entity.RefreshToken;
import com.fiap.midasfintech.repository.AppUserRepository;
import com.fiap.midasfintech.security.AuthRateLimitService;
import com.fiap.midasfintech.security.JwtService;
import com.fiap.midasfintech.service.RefreshTokenService;
import com.fiap.midasfintech.service.SecurityAuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthRateLimitService authRateLimitService;
    private final RefreshTokenService refreshTokenService;
    private final AppUserRepository appUserRepository;
    private final SecurityAuditService securityAuditService;

    @PostMapping("/token")
    public ResponseEntity<AuthTokenResponseDto> generateToken(
            @Valid @RequestBody AuthRequestDto request,
            HttpServletRequest httpRequest) {
        String sourceIp = resolveSourceIp(httpRequest);
        String rateLimitKey = resolveRateLimitKey(httpRequest, request.getUsername());
        if (!authRateLimitService.tryConsume(rateLimitKey)) {
            securityAuditService.registrarEvento("AUTH_RATE_LIMIT", request.getUsername(), sourceIp,
                    "Limite de tentativas excedido", false);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas de autenticação. Tente novamente em instantes.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userDetails);

            AppUser appUser = appUserRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            String refreshToken = refreshTokenService.criarToken(appUser);
            securityAuditService.registrarEvento("AUTH_TOKEN_SUCCESS", userDetails.getUsername(), sourceIp,
                    "Token emitido com sucesso", true);

            return ResponseEntity.ok(new AuthTokenResponseDto(
                    accessToken,
                    "Bearer",
                    jwtService.getAccessExpirationSeconds(),
                    refreshToken,
                    jwtService.getRefreshExpirationSeconds()));
        } catch (BadCredentialsException ex) {
            securityAuditService.registrarEvento("AUTH_TOKEN_FAILURE", request.getUsername(), sourceIp,
                    "Usuário ou senha inválidos", false);
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto request,
            HttpServletRequest httpRequest) {
        String sourceIp = resolveSourceIp(httpRequest);

        RefreshToken refreshToken = refreshTokenService.validarTokenAtivo(request.getRefreshToken());
        String username = refreshToken.getAppUser().getUsername();

        UserDetails userDetails = User.withUsername(username)
                .password(refreshToken.getAppUser().getPassword())
                .authorities(refreshToken.getAppUser().getRoles().stream().map(role -> role.getNome())
                        .toArray(String[]::new))
                .build();

        String accessToken = jwtService.generateToken(userDetails);
        String rotatedRefreshToken = refreshTokenService.rotacionarToken(refreshToken);

        securityAuditService.registrarEvento("AUTH_REFRESH_SUCCESS", username, sourceIp,
                "Refresh token rotacionado", true);

        return ResponseEntity.ok(new AuthTokenResponseDto(
                accessToken,
                "Bearer",
                jwtService.getAccessExpirationSeconds(),
                rotatedRefreshToken,
                jwtService.getRefreshExpirationSeconds()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequestDto request,
            HttpServletRequest httpRequest) {
        String sourceIp = resolveSourceIp(httpRequest);
        refreshTokenService.revogarToken(request.getRefreshToken());
        securityAuditService.registrarEvento("AUTH_LOGOUT", null, sourceIp,
                "Refresh token revogado", true);
        return ResponseEntity.noContent().build();
    }

    private String resolveRateLimitKey(HttpServletRequest request, String username) {
        String sourceIp = resolveSourceIp(request);
        return sourceIp + ":" + username;
    }

    private String resolveSourceIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
