package com.fiap.midasfintech.security;

import com.fiap.midasfintech.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final int MIN_SECRET_LENGTH = 32;

    private final SecurityProperties securityProperties;

    private SecretKey signingKey;
    private List<SecretKey> validationKeys;

    @PostConstruct
    public void init() {
        String jwtSecret = securityProperties.getJwt().getSecret();
        String previousSecrets = securityProperties.getJwt().getPreviousSecrets();

        this.signingKey = buildKey(jwtSecret);
        this.validationKeys = new ArrayList<>();
        this.validationKeys.add(signingKey);

        if (previousSecrets != null && !previousSecrets.isBlank()) {
            String[] oldSecrets = previousSecrets.split(",");
            for (String oldSecret : oldSecrets) {
                String normalized = oldSecret.trim();
                if (!normalized.isEmpty()) {
                    this.validationKeys.add(buildKey(normalized));
                }
            }
        }
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        long expirationMinutes = securityProperties.getJwt().getExpirationMinutes();
        Instant expiration = now.plusSeconds(expirationMinutes * 60);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("roles", roles)
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims parseClaims(String token) {
        RuntimeException lastException = null;

        for (SecretKey key : validationKeys) {
            try {
                return Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            } catch (RuntimeException ex) {
                lastException = ex;
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        throw new IllegalStateException("Token JWT inválido");
    }

    public long getAccessExpirationSeconds() {
        return securityProperties.getJwt().getExpirationMinutes() * 60;
    }

    public long getRefreshExpirationSeconds() {
        return securityProperties.getJwt().getRefreshExpirationMinutes() * 60;
    }

    private SecretKey buildKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret não configurado");
        }

        if (secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("JWT secret deve ter pelo menos 32 caracteres");
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
