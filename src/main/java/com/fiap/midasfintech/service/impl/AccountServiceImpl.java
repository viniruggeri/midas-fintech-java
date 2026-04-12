package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.repository.AccountRepository;
import com.fiap.midasfintech.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account save(Account account) {
        validateAccountPayload(account);
        String normalizedName = account.getNome().trim();
        validateUniqueName(normalizedName, null);
        account.setNome(normalizedName);

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

        validateAccountPayload(account);
        String normalizedName = account.getNome().trim();
        validateUniqueName(normalizedName, id);

        existingAccount.setNome(normalizedName);
        existingAccount.setSaldo(account.getSaldo());
        existingAccount.setEmailNotificacao(account.getEmailNotificacao());
        existingAccount.setTelefoneSms(account.getTelefoneSms());

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

    private void validateAccountPayload(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Dados da conta são obrigatórios");
        }

        if (account.getNome() == null || account.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da conta é obrigatório");
        }

        if (account.getSaldo() == null) {
            throw new IllegalArgumentException("Saldo da conta é obrigatório");
        }

        if (account.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Saldo da conta não pode ser negativo");
        }
    }

    private void validateUniqueName(String normalizedName, Long currentAccountId) {
        Optional<Account> existingAccount = accountRepository.findByNome(normalizedName);
        if (existingAccount.isPresent()
                && (currentAccountId == null || !existingAccount.get().getId().equals(currentAccountId))) {
            throw new IllegalArgumentException("Já existe uma conta com este nome");
        }
    }
}
