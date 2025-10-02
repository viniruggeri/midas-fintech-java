package com.fiap.midasfintech.dto.request;

import com.fiap.midasfintech.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequestDto {

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Data é obrigatória")
    private LocalDateTime data;

    @NotNull(message = "Tipo é obrigatório")
    private Transaction.TransactionType tipo;

    private String descricao;

    @NotNull(message = "ID da conta é obrigatório")
    private Long accountId;
}
