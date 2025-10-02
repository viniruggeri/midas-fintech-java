package com.fiap.midasfintech.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Validator validator;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));
    }

    @Test
    void testTransactionValidCreation() {
        Transaction transaction = new Transaction();
        transaction.setValor(new BigDecimal("100.00"));
        transaction.setData(LocalDateTime.now());
        transaction.setTipo(Transaction.TransactionType.RECEITA);
        transaction.setDescricao("Teste");
        transaction.setAccount(testAccount);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testTransactionValueRequired() {
        Transaction transaction = new Transaction();
        transaction.setData(LocalDateTime.now());
        transaction.setTipo(Transaction.TransactionType.RECEITA);
        transaction.setAccount(testAccount);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertEquals(1, violations.size());

        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Valor é obrigatório", violation.getMessage());
    }

    @Test
    void testTransactionValuePositive() {
        Transaction transaction = new Transaction();
        transaction.setValor(new BigDecimal("0.00"));
        transaction.setData(LocalDateTime.now());
        transaction.setTipo(Transaction.TransactionType.RECEITA);
        transaction.setAccount(testAccount);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertEquals(1, violations.size());

        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Valor deve ser maior que zero", violation.getMessage());
    }

    @Test
    void testTransactionDateRequired() {
        Transaction transaction = new Transaction();
        transaction.setValor(new BigDecimal("100.00"));
        transaction.setTipo(Transaction.TransactionType.RECEITA);
        transaction.setAccount(testAccount);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertEquals(1, violations.size());

        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Data é obrigatória", violation.getMessage());
    }

    @Test
    void testTransactionTypeRequired() {
        Transaction transaction = new Transaction();
        transaction.setValor(new BigDecimal("100.00"));
        transaction.setData(LocalDateTime.now());
        transaction.setAccount(testAccount);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertEquals(1, violations.size());

        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Tipo é obrigatório", violation.getMessage());
    }

    @Test
    void testTransactionAccountRequired() {
        Transaction transaction = new Transaction();
        transaction.setValor(new BigDecimal("100.00"));
        transaction.setData(LocalDateTime.now());
        transaction.setTipo(Transaction.TransactionType.RECEITA);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertEquals(1, violations.size());

        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Conta é obrigatória", violation.getMessage());
    }

    @Test
    void testTransactionTypes() {
        assertEquals(2, Transaction.TransactionType.values().length);
        assertEquals(Transaction.TransactionType.RECEITA, Transaction.TransactionType.valueOf("RECEITA"));
        assertEquals(Transaction.TransactionType.DESPESA, Transaction.TransactionType.valueOf("DESPESA"));
    }

    @Test
    void testTransactionGettersAndSetters() {
        Transaction transaction = new Transaction();
        LocalDateTime now = LocalDateTime.now();

        transaction.setId(1L);
        transaction.setValor(new BigDecimal("150.00"));
        transaction.setData(now);
        transaction.setTipo(Transaction.TransactionType.DESPESA);
        transaction.setDescricao("Compras");
        transaction.setAccount(testAccount);

        assertEquals(1L, transaction.getId());
        assertEquals(new BigDecimal("150.00"), transaction.getValor());
        assertEquals(now, transaction.getData());
        assertEquals(Transaction.TransactionType.DESPESA, transaction.getTipo());
        assertEquals("Compras", transaction.getDescricao());
        assertEquals(testAccount, transaction.getAccount());
    }
}
