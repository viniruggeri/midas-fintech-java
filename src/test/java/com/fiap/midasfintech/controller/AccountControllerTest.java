package com.fiap.midasfintech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.midasfintech.dto.request.AccountRequestDto;
import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;
    private AccountRequestDto requestDto;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));

        requestDto = new AccountRequestDto();
        requestDto.setNome("Conta Teste");
        requestDto.setSaldo(new BigDecimal("1000.00"));
    }

    @Test
    void testCreateAccount() throws Exception {
        when(accountService.save(any(Account.class))).thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Conta Teste"))
                .andExpect(jsonPath("$.saldo").value(1000.00));
    }

    @Test
    void testCreateAccountWithInvalidData() throws Exception {
        requestDto.setNome(""); // Nome inválido
        requestDto.setSaldo(new BigDecimal("-100.00")); // Saldo negativo

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllAccounts() throws Exception {
        Account account2 = new Account();
        account2.setId(2L);
        account2.setNome("Conta 2");
        account2.setSaldo(new BigDecimal("500.00"));

        when(accountService.findAll()).thenReturn(Arrays.asList(testAccount, account2));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Conta Teste"))
                .andExpect(jsonPath("$[1].nome").value("Conta 2"));
    }

    @Test
    void testGetAccountById() throws Exception {
        when(accountService.findById(1L)).thenReturn(Optional.of(testAccount));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Conta Teste"))
                .andExpect(jsonPath("$.saldo").value(1000.00));
    }

    @Test
    void testGetAccountByIdNotFound() throws Exception {
        when(accountService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAccount() throws Exception {
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setNome("Conta Atualizada");
        updatedAccount.setSaldo(new BigDecimal("2000.00"));

        requestDto.setNome("Conta Atualizada");
        requestDto.setSaldo(new BigDecimal("2000.00"));

        when(accountService.update(eq(1L), any(Account.class))).thenReturn(updatedAccount);

        mockMvc.perform(put("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Conta Atualizada"))
                .andExpect(jsonPath("$.saldo").value(2000.00));
    }

    @Test
    void testUpdateAccountNotFound() throws Exception {
        when(accountService.update(eq(999L), any(Account.class)))
                .thenThrow(new IllegalArgumentException("Conta não encontrada"));

        mockMvc.perform(put("/api/accounts/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteAccountNotFound() throws Exception {
        when(accountService.deleteById(999L))
                .thenThrow(new IllegalArgumentException("Conta não encontrada"));

        mockMvc.perform(delete("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }
}
