package com.fiap.midasfintech.controller;

import com.fiap.midasfintech.dto.request.TransactionRequestDto;
import com.fiap.midasfintech.dto.response.TransactionResponseDto;
import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.service.AccountService;
import com.fiap.midasfintech.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Operações relacionadas às transações financeiras - HATEOAS nível 3")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Criar nova transação", description = "Cria uma nova transação financeira e retorna com links HATEOAS")
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto requestDto) {
        try {
            Transaction transaction = convertToEntity(requestDto);
            Transaction savedTransaction = transactionService.save(transaction);
            TransactionResponseDto responseDto = convertToDtoWithLinks(savedTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas as transações", description = "Retorna todas as transações cadastradas com links HATEOAS")
    public ResponseEntity<CollectionModel<TransactionResponseDto>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAll();
        List<TransactionResponseDto> responseDtos = transactions.stream()
                .map(this::convertToDtoWithLinks)
                .collect(Collectors.toList());

        CollectionModel<TransactionResponseDto> collectionModel = CollectionModel.of(responseDtos);
        collectionModel.add(linkTo(methodOn(TransactionController.class).getAllTransactions()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID", description = "Retorna uma transação específica pelo ID com links HATEOAS")
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(transaction -> ResponseEntity.ok(convertToDtoWithLinks(transaction)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Listar transações por conta", description = "Retorna todas as transações de uma conta específica com links HATEOAS")
    public ResponseEntity<CollectionModel<TransactionResponseDto>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.findByAccountId(accountId);
        List<TransactionResponseDto> responseDtos = transactions.stream()
                .map(this::convertToDtoWithLinks)
                .collect(Collectors.toList());

        CollectionModel<TransactionResponseDto> collectionModel = CollectionModel.of(responseDtos);
        collectionModel.add(linkTo(methodOn(TransactionController.class).getTransactionsByAccountId(accountId)).withSelfRel());
        collectionModel.add(linkTo(methodOn(AccountController.class).getAccountById(accountId)).withRel("account"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/account/{accountId}/paged")
    @Operation(summary = "Listar transações paginadas por conta", description = "Retorna transações de uma conta com paginação e links HATEOAS")
    public ResponseEntity<Page<TransactionResponseDto>> getTransactionsByAccountIdPaged(
            @PathVariable Long accountId,
            Pageable pageable) {
        Page<Transaction> transactions = transactionService.findByAccountId(accountId, pageable);
        Page<TransactionResponseDto> responseDtos = transactions.map(this::convertToDtoWithLinks);
        return ResponseEntity.ok(responseDtos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação", description = "Atualiza os dados de uma transação existente e retorna com links HATEOAS")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto requestDto) {
        try {
            Transaction transaction = convertToEntity(requestDto);
            Transaction updatedTransaction = transactionService.update(id, transaction);
            TransactionResponseDto responseDto = convertToDtoWithLinks(updatedTransaction);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação", description = "Remove uma transação do sistema")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Transaction convertToEntity(TransactionRequestDto dto) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(dto, transaction, "accountId");

        Account account = accountService.findById(dto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        transaction.setAccount(account);

        return transaction;
    }

    private TransactionResponseDto convertToDtoWithLinks(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        BeanUtils.copyProperties(transaction, dto);
        dto.setAccountId(transaction.getAccount().getId());
        dto.setAccountNome(transaction.getAccount().getNome());

        // HATEOAS Links (Nível 3 de Maturidade Richardson)
        dto.add(linkTo(methodOn(TransactionController.class).getTransactionById(transaction.getId())).withSelfRel());
        dto.add(linkTo(methodOn(TransactionController.class).getAllTransactions()).withRel("all-transactions"));
        dto.add(linkTo(methodOn(TransactionController.class).updateTransaction(transaction.getId(), null)).withRel("update"));
        dto.add(linkTo(methodOn(TransactionController.class).deleteTransaction(transaction.getId())).withRel("delete"));
        dto.add(linkTo(methodOn(AccountController.class).getAccountById(transaction.getAccount().getId())).withRel("account"));
        dto.add(linkTo(methodOn(TransactionController.class).getTransactionsByAccountId(transaction.getAccount().getId())).withRel("account-transactions"));

        return dto;
    }
}
