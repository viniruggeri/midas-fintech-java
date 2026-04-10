package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.messaging.EstornoNotificacaoPublisher;
import com.fiap.midasfintech.repository.AccountRepository;
import com.fiap.midasfintech.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FluxoFinanceiroServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EstornoNotificacaoPublisher estornoNotificacaoPublisher;

    @InjectMocks
    private FluxoFinanceiroServiceImpl fluxoFinanceiroService;

    private Account contaOrigem;
    private Account contaDestino;
    private Transaction transacaoOriginal;

    @BeforeEach
    void setup() {
        contaOrigem = new Account();
        contaOrigem.setId(1L);
        contaOrigem.setNome("Conta Origem");
        contaOrigem.setSaldo(new BigDecimal("1000.00"));

        contaDestino = new Account();
        contaDestino.setId(2L);
        contaDestino.setNome("Conta Destino");
        contaDestino.setSaldo(new BigDecimal("500.00"));

        transacaoOriginal = new Transaction();
        transacaoOriginal.setId(10L);
        transacaoOriginal.setValor(new BigDecimal("200.00"));
        transacaoOriginal.setData(LocalDateTime.now());
        transacaoOriginal.setTipo(Transaction.TransactionType.DESPESA);
        transacaoOriginal.setDescricao("Compra");
        transacaoOriginal.setAccount(contaOrigem);
    }

    @Test
    void deveRealizarTransferenciaComSucesso() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(contaOrigem));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        fluxoFinanceiroService.realizarTransferencia(1L, 2L, new BigDecimal("300.00"), "Pagamento");

        assertEquals(new BigDecimal("700.00"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("800.00"), contaDestino.getSaldo());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void deveFalharQuandoSaldoInsuficienteNaTransferencia() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(contaOrigem));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fluxoFinanceiroService.realizarTransferencia(1L, 2L, new BigDecimal("3000.00"), "Pagamento"));

        assertEquals("Saldo insuficiente para realizar a transferência", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deveFalharQuandoContaOrigemEDestinoForemIguais() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fluxoFinanceiroService.realizarTransferencia(1L, 1L, new BigDecimal("10.00"), "Teste"));

        assertEquals("Conta de origem e destino devem ser diferentes", exception.getMessage());
        verify(accountRepository, never()).findById(any());
    }

    @Test
    void deveEstornarTransacaoComSucesso() {
        when(transactionRepository.findById(10L)).thenReturn(Optional.of(transacaoOriginal));
        when(transactionRepository.existsByDescricaoContaining(contains("ESTORNO REF TX-10"))).thenReturn(false);

        fluxoFinanceiroService.estornarTransacao(10L, "Compra cancelada");

        assertEquals(new BigDecimal("1200.00"), contaOrigem.getSaldo());
        verify(accountRepository).save(contaOrigem);
        verify(transactionRepository).save(any(Transaction.class));
        verify(estornoNotificacaoPublisher).publicarEstornoAprovado(any(Transaction.class), any(Transaction.class),
                any(Account.class), contains("Compra cancelada"));
    }

    @Test
    void deveFalharQuandoTransacaoJaEstornada() {
        when(transactionRepository.findById(10L)).thenReturn(Optional.of(transacaoOriginal));
        when(transactionRepository.existsByDescricaoContaining(contains("ESTORNO REF TX-10"))).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fluxoFinanceiroService.estornarTransacao(10L, "duplicado"));

        assertEquals("Esta transação já foi estornada", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(estornoNotificacaoPublisher, never()).publicarEstornoAprovado(any(), any(), any(), any());
    }
}
