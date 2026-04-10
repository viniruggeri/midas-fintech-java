package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.messaging.EstornoNotificacaoPublisher;
import com.fiap.midasfintech.repository.AccountRepository;
import com.fiap.midasfintech.repository.TransactionRepository;
import com.fiap.midasfintech.service.FluxoFinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class FluxoFinanceiroServiceImpl implements FluxoFinanceiroService {

    private static final String ESTORNO_PREFIX = "ESTORNO REF TX-";

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EstornoNotificacaoPublisher estornoNotificacaoPublisher;

    @Override
    public void realizarTransferencia(Long contaOrigemId, Long contaDestinoId, BigDecimal valor, String descricao) {
        validarTransferencia(contaOrigemId, contaDestinoId, valor);

        Account contaOrigem = accountRepository.findById(contaOrigemId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));

        Account contaDestino = accountRepository.findById(contaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência");
        }

        LocalDateTime agora = LocalDateTime.now();
        String descricaoBase = (descricao == null || descricao.trim().isEmpty())
                ? "Transferência entre contas"
                : descricao.trim();

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        accountRepository.save(contaOrigem);
        accountRepository.save(contaDestino);

        Transaction debito = new Transaction();
        debito.setValor(valor);
        debito.setData(agora);
        debito.setTipo(Transaction.TransactionType.DESPESA);
        debito.setDescricao("TRANSFERENCIA ENVIADA - " + descricaoBase + " (destino: " + contaDestino.getNome() + ")");
        debito.setAccount(contaOrigem);

        Transaction credito = new Transaction();
        credito.setValor(valor);
        credito.setData(agora);
        credito.setTipo(Transaction.TransactionType.RECEITA);
        credito.setDescricao("TRANSFERENCIA RECEBIDA - " + descricaoBase + " (origem: " + contaOrigem.getNome() + ")");
        credito.setAccount(contaDestino);

        transactionRepository.save(debito);
        transactionRepository.save(credito);
    }

    @Override
    public void estornarTransacao(Long transacaoId, String motivo) {
        if (transacaoId == null) {
            throw new IllegalArgumentException("Transação é obrigatória");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo do estorno é obrigatório");
        }

        Transaction original = transactionRepository.findById(transacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));

        if (original.getDescricao() != null && original.getDescricao().contains(ESTORNO_PREFIX)) {
            throw new IllegalArgumentException("Não é permitido estornar uma transação de estorno");
        }

        if (transactionRepository.existsByDescricaoContaining(ESTORNO_PREFIX + transacaoId)) {
            throw new IllegalArgumentException("Esta transação já foi estornada");
        }

        Account conta = original.getAccount();
        Transaction.TransactionType tipoEstorno = original.getTipo() == Transaction.TransactionType.RECEITA
                ? Transaction.TransactionType.DESPESA
                : Transaction.TransactionType.RECEITA;

        if (tipoEstorno == Transaction.TransactionType.DESPESA && conta.getSaldo().compareTo(original.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para estornar esta transação");
        }

        BigDecimal novoSaldo = tipoEstorno == Transaction.TransactionType.RECEITA
                ? conta.getSaldo().add(original.getValor())
                : conta.getSaldo().subtract(original.getValor());

        conta.setSaldo(novoSaldo);
        accountRepository.save(conta);

        Transaction estorno = new Transaction();
        estorno.setValor(original.getValor());
        estorno.setData(LocalDateTime.now());
        estorno.setTipo(tipoEstorno);
        estorno.setDescricao(ESTORNO_PREFIX + transacaoId + " | " + motivo.trim());
        estorno.setAccount(conta);
        transactionRepository.save(estorno);

        estornoNotificacaoPublisher.publicarEstornoAprovado(original, estorno, conta, motivo);
    }

    private void validarTransferencia(Long contaOrigemId, Long contaDestinoId, BigDecimal valor) {
        if (contaOrigemId == null) {
            throw new IllegalArgumentException("Conta de origem é obrigatória");
        }

        if (contaDestinoId == null) {
            throw new IllegalArgumentException("Conta de destino é obrigatória");
        }

        if (contaOrigemId.equals(contaDestinoId)) {
            throw new IllegalArgumentException("Conta de origem e destino devem ser diferentes");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
    }
}
