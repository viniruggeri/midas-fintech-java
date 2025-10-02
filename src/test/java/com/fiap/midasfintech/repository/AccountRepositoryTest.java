package com.fiap.midasfintech.repository;

import com.fiap.midasfintech.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));
    }

    @Test
    void testSaveAccount() {
        Account savedAccount = accountRepository.save(testAccount);

        assertNotNull(savedAccount.getId());
        assertEquals("Conta Teste", savedAccount.getNome());
        assertEquals(new BigDecimal("1000.00"), savedAccount.getSaldo());
    }

    @Test
    void testFindById() {
        Account savedAccount = entityManager.persistAndFlush(testAccount);

        Optional<Account> foundAccount = accountRepository.findById(savedAccount.getId());

        assertTrue(foundAccount.isPresent());
        assertEquals("Conta Teste", foundAccount.get().getNome());
    }

    @Test
    void testFindByNome() {
        entityManager.persistAndFlush(testAccount);

        Optional<Account> foundAccount = accountRepository.findByNome("Conta Teste");

        assertTrue(foundAccount.isPresent());
        assertEquals("Conta Teste", foundAccount.get().getNome());
        assertEquals(new BigDecimal("1000.00"), foundAccount.get().getSaldo());
    }

    @Test
    void testFindByNomeNotFound() {
        Optional<Account> foundAccount = accountRepository.findByNome("Conta Inexistente");

        assertFalse(foundAccount.isPresent());
    }

    @Test
    void testDeleteAccount() {
        Account savedAccount = entityManager.persistAndFlush(testAccount);
        Long accountId = savedAccount.getId();

        accountRepository.deleteById(accountId);
        entityManager.flush();

        Optional<Account> deletedAccount = accountRepository.findById(accountId);
        assertFalse(deletedAccount.isPresent());
    }

    @Test
    void testUpdateAccount() {
        Account savedAccount = entityManager.persistAndFlush(testAccount);

        savedAccount.setNome("Conta Atualizada");
        savedAccount.setSaldo(new BigDecimal("2000.00"));

        Account updatedAccount = accountRepository.save(savedAccount);

        assertEquals("Conta Atualizada", updatedAccount.getNome());
        assertEquals(new BigDecimal("2000.00"), updatedAccount.getSaldo());
    }

    @Test
    void testFindAll() {
        Account account1 = new Account();
        account1.setNome("Conta 1");
        account1.setSaldo(new BigDecimal("500.00"));

        Account account2 = new Account();
        account2.setNome("Conta 2");
        account2.setSaldo(new BigDecimal("1500.00"));

        entityManager.persistAndFlush(account1);
        entityManager.persistAndFlush(account2);

        assertEquals(2, accountRepository.findAll().size());
    }
}
