package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));
    }

    @Test
    void testSaveAccountSuccess() {
        when(accountRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account savedAccount = accountService.save(testAccount);

        assertNotNull(savedAccount);
        assertEquals("Conta Teste", savedAccount.getNome());
        verify(accountRepository).findByNome("Conta Teste");
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testSaveAccountWithNullName() {
        testAccount.setNome(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.save(testAccount)
        );

        assertEquals("Nome da conta é obrigatório", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testSaveAccountWithEmptyName() {
        testAccount.setNome("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.save(testAccount)
        );

        assertEquals("Nome da conta é obrigatório", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testSaveAccountWithDuplicateName() {
        when(accountRepository.findByNome("Conta Teste")).thenReturn(Optional.of(testAccount));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.save(testAccount)
        );

        assertEquals("Já existe uma conta com este nome", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testFindById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        Optional<Account> foundAccount = accountService.findById(1L);

        assertTrue(foundAccount.isPresent());
        assertEquals("Conta Teste", foundAccount.get().getNome());
        verify(accountRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Account> foundAccount = accountService.findById(999L);

        assertFalse(foundAccount.isPresent());
        verify(accountRepository).findById(999L);
    }

    @Test
    void testFindAll() {
        Account account2 = new Account();
        account2.setId(2L);
        account2.setNome("Conta 2");
        account2.setSaldo(new BigDecimal("500.00"));

        when(accountRepository.findAll()).thenReturn(Arrays.asList(testAccount, account2));

        List<Account> accounts = accountService.findAll();

        assertEquals(2, accounts.size());
        verify(accountRepository).findAll();
    }

    @Test
    void testUpdateAccountSuccess() {
        Account updatedData = new Account();
        updatedData.setNome("Conta Atualizada");
        updatedData.setSaldo(new BigDecimal("2000.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account updatedAccount = accountService.update(1L, updatedData);

        assertEquals("Conta Atualizada", updatedAccount.getNome());
        assertEquals(new BigDecimal("2000.00"), updatedAccount.getSaldo());
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testUpdateAccountNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        Account updatedData = new Account();
        updatedData.setNome("Conta Atualizada");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.update(999L, updatedData)
        );

        assertEquals("Conta não encontrada", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testDeleteByIdSuccess() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        accountService.deleteById(1L);

        verify(accountRepository).existsById(1L);
        verify(accountRepository).deleteById(1L);
    }

    @Test
    void testDeleteByIdNotFound() {
        when(accountRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deleteById(999L)
        );

        assertEquals("Conta não encontrada", exception.getMessage());
        verify(accountRepository, never()).deleteById(any());
    }

    @Test
    void testFindByNome() {
        when(accountRepository.findByNome("Conta Teste")).thenReturn(Optional.of(testAccount));

        Optional<Account> foundAccount = accountService.findByNome("Conta Teste");

        assertTrue(foundAccount.isPresent());
        assertEquals("Conta Teste", foundAccount.get().getNome());
        verify(accountRepository).findByNome("Conta Teste");
    }
}
