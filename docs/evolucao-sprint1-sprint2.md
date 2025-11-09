# Evolução da Sprint 1 para Sprint 2

## 📊 Visão Geral da Evolução

Este documento detalha as melhorias e evoluções implementadas entre a Sprint 1 e a Sprint 2 do projeto Midas Fintech API.

---

## 🎯 Principais Mudanças Implementadas

### 1. **HATEOAS - Nível 3 de Maturidade Richardson** ✅

#### Sprint 1: Nível 1 de Maturidade
- API REST básica com recursos identificados por URIs
- Operações HTTP (GET, POST, PUT, DELETE)
- Respostas simples em JSON sem hipermídia

#### Sprint 2: Nível 3 de Maturidade (HATEOAS)
- ✅ Adicionada dependência `spring-boot-starter-hateoas`
- ✅ DTOs de resposta agora estendem `RepresentationModel<T>`
- ✅ Todos os endpoints retornam links hipermídia (`_links`)
- ✅ Navegação autodescritiva entre recursos relacionados

**Exemplo de Resposta com HATEOAS:**

```json
{
  "id": 1,
  "nome": "Conta Corrente",
  "saldo": 1500.00,
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/1"
    },
    "all-accounts": {
      "href": "http://localhost:8080/api/accounts"
    },
    "update": {
      "href": "http://localhost:8080/api/accounts/1"
    },
    "delete": {
      "href": "http://localhost:8080/api/accounts/1"
    },
    "transactions": {
      "href": "http://localhost:8080/api/transactions/account/1"
    }
  }
}
```

---

### 2. **Refatoração dos Controllers** 🔄

#### Mudanças no `AccountController`:
- Métodos agora retornam `CollectionModel<T>` para listas
- Adicionado método `convertToDtoWithLinks()` que adiciona links HATEOAS
- Links para recursos relacionados (transações da conta)
- Autodescoberta de operações disponíveis (self, update, delete)

#### Mudanças no `TransactionController`:
- Métodos agora retornam `CollectionModel<T>` para listas
- Links bidirecionais entre transações e contas
- Links para todas as transações da conta relacionada
- Navegação completa entre recursos

---

### 3. **Melhorias na Documentação da API** 📚

#### Sprint 1:
- Swagger/OpenAPI básico
- Descrições simples dos endpoints

#### Sprint 2:
- Documentação atualizada mencionando HATEOAS
- Tags dos controllers atualizadas com "nível 3"
- Descrições detalhadas sobre os links hipermídia retornados

---

### 4. **Estrutura de DTOs Aprimorada** 🏗️

#### Antes (Sprint 1):
```java
@Data
public class AccountResponseDto {
    private Long id;
    private String nome;
    private BigDecimal saldo;
}
```

#### Depois (Sprint 2):
```java
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountResponseDto extends RepresentationModel<AccountResponseDto> {
    private Long id;
    private String nome;
    private BigDecimal saldo;
    // Herda métodos add() para links HATEOAS
}
```

---

## 📈 Comparação: Nível 1 vs Nível 3

| Aspecto | Sprint 1 (Nível 1) | Sprint 2 (Nível 3) |
|---------|-------------------|-------------------|
| **Identificação de Recursos** | ✅ URIs únicas | ✅ URIs únicas |
| **Verbos HTTP** | ✅ GET, POST, PUT, DELETE | ✅ GET, POST, PUT, DELETE |
| **Status Codes HTTP** | ✅ 200, 201, 204, 404, 400 | ✅ 200, 201, 204, 404, 400 |
| **Representação** | ✅ JSON | ✅ JSON |
| **Hipermídia (HATEOAS)** | ❌ Não implementado | ✅ **IMPLEMENTADO** |
| **Autodescoberta** | ❌ Cliente precisa conhecer todas as URIs | ✅ **Cliente descobre recursos pelos links** |
| **Navegação** | ❌ Manual | ✅ **Guiada por hipermídia** |

---

## 🔍 Detalhamento dos Links HATEOAS Implementados

### AccountController Links:
- `self` - Link para a própria conta
- `all-accounts` - Link para listar todas as contas
- `update` - Link para atualizar a conta
- `delete` - Link para deletar a conta
- `transactions` - Link para listar transações da conta

### TransactionController Links:
- `self` - Link para a própria transação
- `all-transactions` - Link para listar todas as transações
- `update` - Link para atualizar a transação
- `delete` - Link para deletar a transação
- `account` - Link para a conta relacionada
- `account-transactions` - Link para todas as transações da conta

---

## 🧪 Impacto nos Testes

### Sprint 1:
- Testes validavam apenas dados (JSON simples)

### Sprint 2:
- Testes agora validam presença dos links `_links`
- Verificação de links corretos e estrutura HATEOAS
- Testes de navegação entre recursos

---

## 📋 Checklist de Requisitos Atendidos

- ✅ Aperfeiçoamento da aplicação Spring Boot
- ✅ Evidência de evolução desde Sprint 1
- ✅ HATEOAS em nível 3 de maturidade Richardson implementado
- ✅ Todos os artefatos no GitHub
- ✅ Documentação atualizada
- ✅ Diagramas mantidos e coerentes
- ✅ Testes dos endpoints exportados (Postman Collection)
- ✅ Código refatorado com melhorias de qualidade

---

## 🚀 Próximos Passos (Sprint 3)

- Implementar autenticação e autorização (Spring Security)
- Adicionar cache com Redis
- Implementar mensageria com RabbitMQ/Kafka
- Adicionar métricas e observabilidade (Actuator + Prometheus)
- Implementar Circuit Breaker para resiliência

---

## 👥 Contribuições da Equipe

- **Vinicius (RM 560593)**: Implementação HATEOAS, refatoração dos controllers, documentação técnica
- **Barbara**: Validação dos testes, verificação da persistência no Oracle, QA dos endpoints
- **Yasmin**: Integração com aplicação mobile, validação dos endpoints via consumo externo

---

**Data da Evolução:** 02/11/2025  
**Versão:** 2.0.0  
**Status:** ✅ Completo - Pronto para Entrega Sprint 2

