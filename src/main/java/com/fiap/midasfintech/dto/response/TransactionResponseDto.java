package com.fiap.midasfintech.dto.response;

import com.fiap.midasfintech.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDto {

    private Long id;
    private BigDecimal valor;
    private LocalDateTime data;
    private Transaction.TransactionType tipo;
    private String descricao;
    private Long accountId;
    private String accountNome;
}
