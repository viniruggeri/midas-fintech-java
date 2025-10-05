package com.fiap.midasfintech.config;

import com.fiap.midasfintech.entity.Account;
import com.fiap.midasfintech.entity.Transaction;
import com.fiap.midasfintech.repository.AccountRepository;
import com.fiap.midasfintech.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
// este código foi gerado com a ajuda do claude ai
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (accountRepository.count() == 0) {
            log.info("Inicializando dados de teste...");

            // Criar contas
            Account contaCorrente = new Account();
            contaCorrente.setNome("Conta Corrente");
            contaCorrente.setSaldo(new BigDecimal("1000.00"));
            contaCorrente = accountRepository.save(contaCorrente);

            Account contaPoupanca = new Account();
            contaPoupanca.setNome("Conta Poupança");
            contaPoupanca.setSaldo(new BigDecimal("5000.00"));
            contaPoupanca = accountRepository.save(contaPoupanca);

            // Criar transações
            Transaction transaction1 = new Transaction();
            transaction1.setValor(new BigDecimal("500.00"));
            transaction1.setData(LocalDateTime.of(2024, 1, 15, 10, 30));
            transaction1.setTipo(Transaction.TransactionType.RECEITA);
            transaction1.setDescricao("Salário");
            transaction1.setAccount(contaCorrente);
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction();
            transaction2.setValor(new BigDecimal("150.00"));
            transaction2.setData(LocalDateTime.of(2024, 1, 16, 14, 20));
            transaction2.setTipo(Transaction.TransactionType.DESPESA);
            transaction2.setDescricao("Supermercado");
            transaction2.setAccount(contaCorrente);
            transactionRepository.save(transaction2);

            Transaction transaction3 = new Transaction();
            transaction3.setValor(new BigDecimal("200.00"));
            transaction3.setData(LocalDateTime.of(2024, 1, 17, 9, 15));
            transaction3.setTipo(Transaction.TransactionType.RECEITA);
            transaction3.setDescricao("Freelance");
            transaction3.setAccount(contaCorrente);
            transactionRepository.save(transaction3);

            Transaction transaction4 = new Transaction();
            transaction4.setValor(new BigDecimal("80.00"));
            transaction4.setData(LocalDateTime.of(2024, 1, 18, 16, 45));
            transaction4.setTipo(Transaction.TransactionType.DESPESA);
            transaction4.setDescricao("Gasolina");
            transaction4.setAccount(contaCorrente);
            transactionRepository.save(transaction4);

            Transaction transaction5 = new Transaction();
            transaction5.setValor(new BigDecimal("1000.00"));
            transaction5.setData(LocalDateTime.of(2024, 1, 19, 8, 0));
            transaction5.setTipo(Transaction.TransactionType.RECEITA);
            transaction5.setDescricao("Investimento");
            transaction5.setAccount(contaPoupanca);
            transactionRepository.save(transaction5);

            Transaction transaction6 = new Transaction();
            transaction6.setValor(new BigDecimal("300.00"));
            transaction6.setData(LocalDateTime.of(2024, 1, 20, 15, 30));
            transaction6.setTipo(Transaction.TransactionType.DESPESA);
            transaction6.setDescricao("Compras");
            transaction6.setAccount(contaPoupanca);
            transactionRepository.save(transaction6);

            log.info("Dados de teste criados com sucesso!");
        }
    }
}
