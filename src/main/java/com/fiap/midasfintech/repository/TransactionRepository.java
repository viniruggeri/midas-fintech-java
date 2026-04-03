package com.fiap.midasfintech.repository;

import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccountIdAndDataBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    Page<Transaction> findByAccount(Account account, Pageable pageable);

    boolean existsByDescricaoContaining(String descricao);
}
