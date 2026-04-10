package com.fiap.midasfintech.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Saldo é obrigatório")
    @DecimalMin(value = "0.0", message = "Saldo deve ser positivo")
    private BigDecimal saldo;

    @Email(message = "Email deve ser válido")
    private String emailNotificacao;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Telefone SMS deve conter 10 a 15 dígitos")
    private String telefoneSms;
}
