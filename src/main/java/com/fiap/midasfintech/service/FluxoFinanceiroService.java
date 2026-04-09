package com.fiap.midasfintech.service;

import java.math.BigDecimal;

public interface FluxoFinanceiroService {

    void realizarTransferencia(Long contaOrigemId, Long contaDestinoId, BigDecimal valor, String descricao);

    void estornarTransacao(Long transacaoId, String motivo);
}
