package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.repository.AccountRepository;
import com.fiap.midasfintech.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account save(Account account) {
        if (account.getNome() == null || account.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da conta é obrigatório");
        }

        Optional<Account> existingAccount = accountRepository.findByNome(account.getNome());
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Já existe uma conta com este nome");
        }

        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account update(Long id, Account account) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        existingAccount.setNome(account.getNome());
        existingAccount.setSaldo(account.getSaldo());

        return accountRepository.save(existingAccount);
    }

    @Override
    public void deleteById(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        accountRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByNome(String nome) {
        return accountRepository.findByNome(nome);
    }
}
