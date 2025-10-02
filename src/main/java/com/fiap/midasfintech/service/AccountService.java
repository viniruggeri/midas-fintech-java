package com.fiap.midasfintech.service;

import com.fiap.midasfintech.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account save(Account account);

    Optional<Account> findById(Long id);

    List<Account> findAll();

    Account update(Long id, Account account);

    void deleteById(Long id);

    Optional<Account> findByNome(String nome);
}
