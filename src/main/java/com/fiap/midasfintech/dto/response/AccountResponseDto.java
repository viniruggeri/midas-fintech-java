package com.fiap.midasfintech.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponseDto {

    private Long id;
    private String nome;
    private BigDecimal saldo;
}
