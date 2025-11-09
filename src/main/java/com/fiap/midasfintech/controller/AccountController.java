package com.fiap.midasfintech.controller;

import com.fiap.midasfintech.dto.request.AccountRequestDto;
import com.fiap.midasfintech.dto.response.AccountResponseDto;
import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Operações relacionadas às contas financeiras")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Criar nova conta", description = "Cria uma nova conta financeira")
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto requestDto) {
        Account account = convertToEntity(requestDto);
        Account savedAccount = accountService.save(account);
        AccountResponseDto responseDto = convertToDto(savedAccount);
        responseDto.add(linkTo(methodOn(AccountController.class).getAccountById(savedAccount.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas", description = "Retorna todas as contas cadastradas")
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        List<Account> accounts = accountService.findAll();
        List<AccountResponseDto> responseDtos = accounts.stream()
                .map(account -> {
                    AccountResponseDto dto = convertToDto(account);
                    dto.add(linkTo(methodOn(AccountController.class).getAccountById(account.getId())).withSelfRel());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID", description = "Retorna uma conta específica pelo ID")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable Long id) {
        return accountService.findById(id)
                .map(account -> {
                    AccountResponseDto dto = convertToDto(account);
                    dto.add(linkTo(methodOn(AccountController.class).getAccountById(id)).withSelfRel());
                    dto.add(linkTo(methodOn(AccountController.class).getAllAccounts()).withRel("all-accounts"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta", description = "Atualiza os dados de uma conta existente")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDto requestDto) {
        try {
            Account account = convertToEntity(requestDto);
            Account updatedAccount = accountService.update(id, account);
            AccountResponseDto responseDto = convertToDto(updatedAccount);
            responseDto.add(linkTo(methodOn(AccountController.class).getAccountById(id)).withSelfRel());
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta", description = "Remove uma conta do sistema")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Account convertToEntity(AccountRequestDto dto) {
        Account account = new Account();
        BeanUtils.copyProperties(dto, account);
        return account;
    }

    private AccountResponseDto convertToDto(Account account) {
        AccountResponseDto dto = new AccountResponseDto();
        BeanUtils.copyProperties(account, dto);
        return dto;
    }
}
