package com.fiap.midasfintech.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {

    @NotBlank(message = "Usuário é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
