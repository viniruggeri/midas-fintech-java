```mermaid
classDiagram
    class Account {
        -Long id
        -String nome
        -BigDecimal saldo
        -List~Transaction~ transactions
        +getId() Long
        +setId(Long) void
        +getNome() String
        +setNome(String) void
        +getSaldo() BigDecimal
        +setSaldo(BigDecimal) void
    }
    
    class Transaction {
        -Long id
        -BigDecimal valor
        -LocalDateTime data
        -TransactionType tipo
        -String descricao
        -Account account
        +getId() Long
        +getValor() BigDecimal
        +getData() LocalDateTime
        +getTipo() TransactionType
        +setTipo(TransactionType) void
    }
    
    class TransactionType {
        <<enumeration>>
        RECEITA
        DESPESA
    }
    
    class AccountService {
        <<interface>>
        +save(Account) Account
        +findById(Long) Optional~Account~
        +findAll() List~Account~
        +update(Long, Account) Account
        +deleteById(Long) void
    }
    
    class AccountServiceImpl {
        -AccountRepository accountRepository
        +save(Account) Account
        +findById(Long) Optional~Account~
        +update(Long, Account) Account
        +deleteById(Long) void
    }
    
    class TransactionService {
        <<interface>>
        +save(Transaction) Transaction
        +findById(Long) Optional~Transaction~
        +findAll() List~Transaction~
        +update(Long, Transaction) Transaction
        +deleteById(Long) void
    }
    
    class TransactionServiceImpl {
        -TransactionRepository transactionRepository
        -AccountRepository accountRepository
        +save(Transaction) Transaction
        +findById(Long) Optional~Transaction~
        +update(Long, Transaction) Transaction
        +deleteById(Long) void
    }
    
    class AccountController {
        -AccountService accountService
        +createAccount(AccountRequestDto) ResponseEntity
        +getAllAccounts() ResponseEntity
        +getAccountById(Long) ResponseEntity
        +updateAccount(Long, AccountRequestDto) ResponseEntity
        +deleteAccount(Long) ResponseEntity
    }
    
    class TransactionController {
        -TransactionService transactionService
        -AccountService accountService
        +createTransaction(TransactionRequestDto) ResponseEntity
        +getAllTransactions() ResponseEntity
        +getTransactionById(Long) ResponseEntity
        +updateTransaction(Long, TransactionRequestDto) ResponseEntity
        +deleteTransaction(Long) ResponseEntity
    }
    
    Account ||--o{ Transaction : possui
    Transaction ||-- TransactionType : tipo
    
    AccountService <|.. AccountServiceImpl : implements
    TransactionService <|.. TransactionServiceImpl : implements
    
    AccountController --> AccountService : uses
    TransactionController --> TransactionService : uses
    TransactionController --> AccountService : uses
```
