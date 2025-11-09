package com.fiap.midasfintech.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountResponseDto extends RepresentationModel<AccountResponseDto> {

    private Long id;
    private String nome;
    private BigDecimal saldo;
}
