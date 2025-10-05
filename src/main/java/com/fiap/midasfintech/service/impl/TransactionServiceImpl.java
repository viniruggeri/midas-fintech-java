package com.fiap.midasfintech.service.impl;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.repository.AccountRepository;
import com.fiap.midasfintech.repository.TransactionRepository;
import com.fiap.midasfintech.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public Transaction save(Transaction transaction) {
        validateTransaction(transaction);

        Account account = transaction.getAccount();
        BigDecimal newBalance = calculateNewBalance(account, transaction);
        account.setSaldo(newBalance);
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction update(Long id, Transaction transaction) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));


        Account account = existingTransaction.getAccount();
        BigDecimal revertedBalance = revertTransactionBalance(account, existingTransaction);

        existingTransaction.setValor(transaction.getValor());
        existingTransaction.setTipo(transaction.getTipo());
        existingTransaction.setDescricao(transaction.getDescricao());
        existingTransaction.setData(transaction.getData());

        BigDecimal newBalance = calculateNewBalance(account, existingTransaction);
        account.setSaldo(newBalance);
        accountRepository.save(account);

        return transactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));

        Account account = transaction.getAccount();
        BigDecimal revertedBalance = revertTransactionBalance(account, transaction);
        account.setSaldo(revertedBalance);
        accountRepository.save(account);

        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByAccountIdAndPeriod(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndDataBetween(accountId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByAccountId(Long accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        return transactionRepository.findByAccount(account, pageable);
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getValor() == null || transaction.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser maior que zero");
        }

        if (transaction.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da transação é obrigatório");
        }

        if (transaction.getData() == null) {
            throw new IllegalArgumentException("Data da transação é obrigatória");
        }

        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Conta é obrigatória");
        }
    }

    private BigDecimal calculateNewBalance(Account account, Transaction transaction) {
        BigDecimal currentBalance = account.getSaldo();
        BigDecimal transactionValue = transaction.getValor();

        return switch (transaction.getTipo()) {
            case RECEITA -> currentBalance.add(transactionValue);
            case DESPESA -> currentBalance.subtract(transactionValue);
        };
    }

    private BigDecimal revertTransactionBalance(Account account, Transaction transaction) {
        BigDecimal currentBalance = account.getSaldo();
        BigDecimal transactionValue = transaction.getValor();

        return switch (transaction.getTipo()) {
            case RECEITA -> currentBalance.subtract(transactionValue);
            case DESPESA -> currentBalance.add(transactionValue);
        };
    }
}
