package com.fiap.midasfintech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokenResponseDto {

    private String token;
    private String tokenType;
    private long expiresInSeconds;
    private String refreshToken;
    private long refreshExpiresInSeconds;
}
