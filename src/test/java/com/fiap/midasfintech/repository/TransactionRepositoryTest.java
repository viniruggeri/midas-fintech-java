package com.fiap.midasfintech.repository;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));
        testAccount = entityManager.persistAndFlush(testAccount);

        testTransaction = new Transaction();
        testTransaction.setValor(new BigDecimal("100.00"));
        testTransaction.setData(LocalDateTime.now());
        testTransaction.setTipo(Transaction.TransactionType.RECEITA);
        testTransaction.setDescricao("Teste");
        testTransaction.setAccount(testAccount);
    }

    @Test
    void testSaveTransaction() {
        Transaction savedTransaction = transactionRepository.save(testTransaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(new BigDecimal("100.00"), savedTransaction.getValor());
        assertEquals(Transaction.TransactionType.RECEITA, savedTransaction.getTipo());
        assertEquals(testAccount.getId(), savedTransaction.getAccount().getId());
    }

    @Test
    void testFindByAccountId() {
        entityManager.persistAndFlush(testTransaction);

        // Criar segunda transação para a mesma conta
        Transaction transaction2 = new Transaction();
        transaction2.setValor(new BigDecimal("50.00"));
        transaction2.setData(LocalDateTime.now());
        transaction2.setTipo(Transaction.TransactionType.DESPESA);
        transaction2.setDescricao("Despesa teste");
        transaction2.setAccount(testAccount);
        entityManager.persistAndFlush(transaction2);

        List<Transaction> transactions = transactionRepository.findByAccountId(testAccount.getId());

        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().anyMatch(t -> t.getTipo() == Transaction.TransactionType.RECEITA));
        assertTrue(transactions.stream().anyMatch(t -> t.getTipo() == Transaction.TransactionType.DESPESA));
    }

    @Test
    void testFindByAccountIdAndDataBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        testTransaction.setData(LocalDateTime.now());
        entityManager.persistAndFlush(testTransaction);

        // Transação fora do período
        Transaction oldTransaction = new Transaction();
        oldTransaction.setValor(new BigDecimal("200.00"));
        oldTransaction.setData(LocalDateTime.now().minusDays(2));
        oldTransaction.setTipo(Transaction.TransactionType.RECEITA);
        oldTransaction.setAccount(testAccount);
        entityManager.persistAndFlush(oldTransaction);

        List<Transaction> transactions = transactionRepository
                .findByAccountIdAndDataBetween(testAccount.getId(), startDate, endDate);

        assertEquals(1, transactions.size());
        assertEquals(new BigDecimal("100.00"), transactions.get(0).getValor());
    }

    @Test
    void testFindByAccountIdEmpty() {
        List<Transaction> transactions = transactionRepository.findByAccountId(999L);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testDeleteTransaction() {
        Transaction savedTransaction = entityManager.persistAndFlush(testTransaction);
        Long transactionId = savedTransaction.getId();

        transactionRepository.deleteById(transactionId);
        entityManager.flush();

        assertFalse(transactionRepository.findById(transactionId).isPresent());
    }

    @Test
    void testUpdateTransaction() {
        Transaction savedTransaction = entityManager.persistAndFlush(testTransaction);

        savedTransaction.setValor(new BigDecimal("150.00"));
        savedTransaction.setTipo(Transaction.TransactionType.DESPESA);
        savedTransaction.setDescricao("Atualizada");

        Transaction updatedTransaction = transactionRepository.save(savedTransaction);

        assertEquals(new BigDecimal("150.00"), updatedTransaction.getValor());
        assertEquals(Transaction.TransactionType.DESPESA, updatedTransaction.getTipo());
        assertEquals("Atualizada", updatedTransaction.getDescricao());
    }
}
