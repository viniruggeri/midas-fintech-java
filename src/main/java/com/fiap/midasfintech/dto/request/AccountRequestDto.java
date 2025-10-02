package com.fiap.midasfintech.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Saldo é obrigatório")
    @DecimalMin(value = "0.0", message = "Saldo deve ser positivo")
    private BigDecimal saldo;
}
