package com.fiap.midasfintech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.midasfintech.dto.request.TransactionRequestDto;
import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.service.AccountService;
import com.fiap.midasfintech.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;
    private Transaction testTransaction;
    private TransactionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setNome("Conta Teste");
        testAccount.setSaldo(new BigDecimal("1000.00"));

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setValor(new BigDecimal("100.00"));
        testTransaction.setData(LocalDateTime.of(2024, 1, 15, 10, 30));
        testTransaction.setTipo(Transaction.TransactionType.RECEITA);
        testTransaction.setDescricao("Teste");
        testTransaction.setAccount(testAccount);

        requestDto = new TransactionRequestDto();
        requestDto.setValor(new BigDecimal("100.00"));
        requestDto.setData(LocalDateTime.of(2024, 1, 15, 10, 30));
        requestDto.setTipo(Transaction.TransactionType.RECEITA);
        requestDto.setDescricao("Teste");
        requestDto.setAccountId(1L);
    }

    @Test
    void testCreateTransaction() throws Exception {
        when(accountService.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionService.save(any(Transaction.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.tipo").value("RECEITA"))
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.accountNome").value("Conta Teste"));
    }

    @Test
    void testCreateTransactionWithInvalidData() throws Exception {
        requestDto.setValor(new BigDecimal("-50.00")); // Valor negativo
        requestDto.setTipo(null); // Tipo nulo

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTransactionAccountNotFound() throws Exception {
        when(accountService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTransactionById() throws Exception {
        when(transactionService.findById(1L)).thenReturn(Optional.of(testTransaction));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.tipo").value("RECEITA"));
    }

    @Test
    void testGetTransactionByIdNotFound() throws Exception {
        when(transactionService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTransaction() throws Exception {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(1L);
        updatedTransaction.setValor(new BigDecimal("150.00"));
        updatedTransaction.setTipo(Transaction.TransactionType.DESPESA);
        updatedTransaction.setAccount(testAccount);

        requestDto.setValor(new BigDecimal("150.00"));
        requestDto.setTipo(Transaction.TransactionType.DESPESA);

        when(accountService.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionService.update(eq(1L), any(Transaction.class))).thenReturn(updatedTransaction);

        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(150.00))
                .andExpect(jsonPath("$.tipo").value("DESPESA"));
    }

    @Test
    void testUpdateTransactionNotFound() throws Exception {
        when(accountService.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionService.update(eq(999L), any(Transaction.class)))
                .thenThrow(new IllegalArgumentException("Transação não encontrada"));

        mockMvc.perform(put("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTransactionNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Transação não encontrada"))
                .when(transactionService).deleteById(999L);

        mockMvc.perform(delete("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTransactionById_HateoasLinks() throws Exception {
        when(transactionService.findById(1L)).thenReturn(Optional.of(testTransaction));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.account.href").exists())
                .andExpect(jsonPath("$._links.all-transactions.href").exists());
    }
}
