package com.fiap.midasfintech.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh token é obrigatório")
    private String refreshToken;
}
