package com.fiap.midasfintech.service;

import com.fiap.midasfintech.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    List<Transaction> findAll();

    Transaction update(Long id, Transaction transaction);

    void deleteById(Long id);

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccountIdAndPeriod(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
}
