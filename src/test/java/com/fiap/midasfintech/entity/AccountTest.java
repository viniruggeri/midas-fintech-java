package com.fiap.midasfintech.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testAccountValidCreation() {
        Account account = new Account();
        account.setNome("Conta Teste");
        account.setSaldo(new BigDecimal("1000.00"));

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAccountNameRequired() {
        Account account = new Account();
        account.setSaldo(new BigDecimal("1000.00"));

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertEquals(1, violations.size());

        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Nome é obrigatório", violation.getMessage());
    }

    @Test
    void testAccountNameNotBlank() {
        Account account = new Account();
        account.setNome("");
        account.setSaldo(new BigDecimal("1000.00"));

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertEquals(1, violations.size());
    }

    @Test
    void testAccountBalanceRequired() {
        Account account = new Account();
        account.setNome("Conta Teste");

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertEquals(1, violations.size());

        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Saldo é obrigatório", violation.getMessage());
    }

    @Test
    void testAccountBalancePositive() {
        Account account = new Account();
        account.setNome("Conta Teste");
        account.setSaldo(new BigDecimal("-100.00"));

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertEquals(1, violations.size());

        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Saldo deve ser positivo", violation.getMessage());
    }

    @Test
    void testAccountGettersAndSetters() {
        Account account = new Account();
        account.setId(1L);
        account.setNome("Conta Teste");
        account.setSaldo(new BigDecimal("500.00"));

        assertEquals(1L, account.getId());
        assertEquals("Conta Teste", account.getNome());
        assertEquals(new BigDecimal("500.00"), account.getSaldo());
    }

    @Test
    void testAccountEqualsAndHashCode() {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setNome("Conta 1");
        account1.setSaldo(new BigDecimal("1000.00"));

        Account account2 = new Account();
        account2.setId(1L);
        account2.setNome("Conta 1");
        account2.setSaldo(new BigDecimal("1000.00"));

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }
}
