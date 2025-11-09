package com.fiap.midasfintech.dto.response;

import com.fiap.midasfintech.entity.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionResponseDto extends RepresentationModel<TransactionResponseDto> {

    private Long id;
    private BigDecimal valor;
    private LocalDateTime data;
    private Transaction.TransactionType tipo;
    private String descricao;
    private Long accountId;
    private String accountNome;
}
