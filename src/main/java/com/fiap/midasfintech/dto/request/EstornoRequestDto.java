package com.fiap.midasfintech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstornoRequestDto {

    @NotNull(message = "Id da transação é obrigatório")
    private Long transacaoId;

    @NotBlank(message = "Motivo é obrigatório")
    private String motivo;
}
