package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.config.SecurityProperties;
import com.fiap.midasfintech.entity.AppUser;
import com.fiap.midasfintech.entity.RefreshToken;
import com.fiap.midasfintech.repository.RefreshTokenRepository;
import com.fiap.midasfintech.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityProperties securityProperties;

    @Override
    public String criarToken(AppUser appUser) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setAppUser(appUser);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(securityProperties.getJwt().getRefreshExpirationMinutes()));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validarTokenAtivo(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token revogado");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expirado");
        }

        return refreshToken;
    }

    @Override
    public String rotacionarToken(RefreshToken tokenAtual) {
        tokenAtual.setRevoked(true);
        String novoToken = UUID.randomUUID().toString();
        tokenAtual.setReplacedByToken(novoToken);
        refreshTokenRepository.save(tokenAtual);

        RefreshToken novo = new RefreshToken();
        novo.setToken(novoToken);
        novo.setAppUser(tokenAtual.getAppUser());
        novo.setCreatedAt(LocalDateTime.now());
        novo.setExpiresAt(LocalDateTime.now().plusMinutes(securityProperties.getJwt().getRefreshExpirationMinutes()));
        novo.setRevoked(false);
        refreshTokenRepository.save(novo);

        return novoToken;
    }

    @Override
    public void revogarToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(existing -> {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
        });
    }
}
