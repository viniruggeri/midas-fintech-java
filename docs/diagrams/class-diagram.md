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
        +getTransactions() List~Transaction~
        +setTransactions(List~Transaction~) void
    }
    
    class Transaction {
        -Long id
        -BigDecimal valor
        -LocalDateTime data
        -TransactionType tipo
        -String descricao
        -Account account
        +getId() Long
        +setId(Long) void
        +getValor() BigDecimal
        +setValor(BigDecimal) void
        +getData() LocalDateTime
        +setData(LocalDateTime) void
        +getTipo() TransactionType
        +setTipo(TransactionType) void
        +getDescricao() String
        +setDescricao(String) void
        +getAccount() Account
        +setAccount(Account) void
    }
    
    class TransactionType {
        <<enumeration>>
        RECEITA
        DESPESA
    }
    
    class JpaRepository~T, ID~ {
        <<interface>>
        +save(T) T
        +findById(ID) Optional~T~
        +findAll() List~T~
        +deleteById(ID) void
    }
    
    class AccountRepository {
        <<interface>>
        +findByNome(String) Optional~Account~
    }
    
    class TransactionRepository {
        <<interface>>
        +findByAccountId(Long) List~Transaction~
        +findByAccountIdAndDataBetween(Long, LocalDateTime, LocalDateTime) List~Transaction~
        +findByAccount(Account, Pageable) Page~Transaction~
    }
    
    class AccountService {
        <<interface>>
        +save(Account) Account
        +findById(Long) Optional~Account~
        +findAll() List~Account~
        +update(Long, Account) Account
        +deleteById(Long) void
        +findByNome(String) Optional~Account~
    }
    
    class AccountServiceImpl {
        -AccountRepository accountRepository
        +save(Account) Account
        +findById(Long) Optional~Account~
        +findAll() List~Account~
        +update(Long, Account) Account
        +deleteById(Long) void
        +findByNome(String) Optional~Account~
    }
    
    class TransactionService {
        <<interface>>
        +save(Transaction) Transaction
        +findById(Long) Optional~Transaction~
        +findAll() List~Transaction~
        +update(Long, Transaction) Transaction
        +deleteById(Long) void
        +findByAccountId(Long) List~Transaction~
        +findByAccountIdAndPeriod(Long, LocalDateTime, LocalDateTime) List~Transaction~
        +findByAccountId(Long, Pageable) Page~Transaction~
    }
    
    class TransactionServiceImpl {
        -TransactionRepository transactionRepository
        -AccountRepository accountRepository
        +save(Transaction) Transaction
        +findById(Long) Optional~Transaction~
        +findAll() List~Transaction~
        +update(Long, Transaction) Transaction
        +deleteById(Long) void
        +findByAccountId(Long) List~Transaction~
        +findByAccountIdAndPeriod(Long, LocalDateTime, LocalDateTime) List~Transaction~
        +findByAccountId(Long, Pageable) Page~Transaction~
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
        +getTransactionsByAccountId(Long) ResponseEntity
        +updateTransaction(Long, TransactionRequestDto) ResponseEntity
        +deleteTransaction(Long) ResponseEntity
    }
    
    Account "1" ||--o{ "N" Transaction : possui
    Transaction --> TransactionType : usa
    
    JpaRepository <|-- AccountRepository : extends
    JpaRepository <|-- TransactionRepository : extends
    
    AccountService <|.. AccountServiceImpl : implements
    TransactionService <|.. TransactionServiceImpl : implements
    
    AccountServiceImpl --> AccountRepository : usa
    TransactionServiceImpl --> TransactionRepository : usa
    TransactionServiceImpl --> AccountRepository : usa
    
    AccountController --> AccountService : usa
    TransactionController --> TransactionService : usa
    TransactionController --> AccountService : usa
```
