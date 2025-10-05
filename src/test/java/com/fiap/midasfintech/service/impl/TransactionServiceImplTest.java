package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setValor(new BigDecimal("100.00"));
        testTransaction.setData(LocalDateTime.now());
        testTransaction.setTipo(Transaction.TransactionType.RECEITA);
        testTransaction.setDescricao("Teste");
        testTransaction.setAccount(testAccount);
    }

    @Test
    void testSaveTransactionReceita() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Transaction savedTransaction = transactionService.save(testTransaction);

        assertNotNull(savedTransaction);
        assertEquals(new BigDecimal("100.00"), savedTransaction.getValor());
        // Verifica se o saldo foi atualizado (1000 + 100 = 1100)
        assertEquals(new BigDecimal("1100.00"), testAccount.getSaldo());
        verify(transactionRepository).save(testTransaction);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testSaveTransactionDespesa() {
        testTransaction.setTipo(Transaction.TransactionType.DESPESA);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Transaction savedTransaction = transactionService.save(testTransaction);

        assertNotNull(savedTransaction);
        // Verifica se o saldo foi atualizado (1000 - 100 = 900)
        assertEquals(new BigDecimal("900.00"), testAccount.getSaldo());
        verify(transactionRepository).save(testTransaction);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testSaveTransactionWithNullValue() {
        testTransaction.setValor(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.save(testTransaction)
        );

        assertEquals("Valor da transação deve ser maior que zero", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransactionWithZeroValue() {
        testTransaction.setValor(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.save(testTransaction)
        );

        assertEquals("Valor da transação deve ser maior que zero", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransactionWithNullType() {
        testTransaction.setTipo(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.save(testTransaction)
        );

        assertEquals("Tipo da transação é obrigatório", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransactionWithNullDate() {
        testTransaction.setData(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.save(testTransaction)
        );

        assertEquals("Data da transação é obrigatória", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransactionWithNullAccount() {
        testTransaction.setAccount(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.save(testTransaction)
        );

        assertEquals("Conta é obrigatória", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }


    @Test
    void testUpdateTransactionNotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        Transaction updatedData = new Transaction();
        updatedData.setValor(new BigDecimal("150.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.update(999L, updatedData)
        );

        assertEquals("Transação não encontrada", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testDeleteTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        transactionService.deleteById(1L);

        // Verifica se o saldo foi revertido (era receita de 100, então saldo volta para 900)
        assertEquals(new BigDecimal("900.00"), testAccount.getSaldo());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).deleteById(1L);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testDeleteTransactionNotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.deleteById(999L)
        );

        assertEquals("Transação não encontrada", exception.getMessage());
        verify(transactionRepository, never()).deleteById(any());
    }

    @Test
    void testFindById() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTransaction, result.get());
        verify(transactionRepository).findById(1L);
    }
}
