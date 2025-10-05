# Cronograma de Desenvolvimento - Midas API
## Sprint Java Advanced - FIAP

### 📅 **Planejamento da Sprint 1**
**Período:** 26/09/2025 - 10/10/2025 (15 dias)

| **Data** | **Atividade** | **Responsável** | **Status** | **Entregáveis** |
|----------|---------------|-----------------|------------|-----------------|
| **26/09 - 28/09** | Análise de Requisitos e Modelagem de Dados | Barbara | ✅ Concluído | DER, Especificação do Database, Constraints |
| **28/09 - 29/09** | Configuração do Ambiente Cloud Azure | Barbara | ✅ Concluído | Configuração Oracle Cloud, Pipelines |
| **29/09 - 01/10** | Entidades JPA e Mapeamento OR | Vinicius | ✅ Concluído | Account.java, Transaction.java, Relacionamentos |
| **01/10 - 02/10** | Configuração do Projeto Spring Boot | Vinicius | ✅ Concluído | pom.xml, application.yaml, profiles |
| **02/10 - 03/10** | Repositories com Generics | Vinicius | ✅ Concluído | BaseRepository, AccountRepository, TransactionRepository |
| **03/10 - 04/10** | Services e Regras de Negócio | Vinicius | ✅ Concluído | AccountService, TransactionService, Validações |
| **04/10 - 05/10** | Implementação de IA/RAG Services | Vinicius | ✅ Concluído | Configuração para futuras integrações de IA |
| **05/10 - 07/10** | Controllers REST Nível 1 Richardson | Vinicius | ✅ Concluído | AccountController, TransactionController, DTOs |
| **06/10 - 07/10** | Planejamento Integração Mobile/.NET | Yasmin | ✅ Concluído | Especificação de APIs, Contratos de Interface |
| **07/10 - 08/10** | Configuração Swagger/OpenAPI | Vinicius | ✅ Concluído | Documentação automática da API |
| **08/10 - 09/10** | Testes Automatizados e QA | Barbara | ✅ Concluído | Suíte de testes, Collection Postman |
| **09/10 - 10/10** | Documentação Final e Compliance | Barbara | ✅ Concluído | README.md, Diagramas, Verificação de Conformidade |

### 📋 **Detalhamento das Responsabilidades por Membro**

#### **Vinicius** - Tech Lead / IA Engineer
- ✅ **Arquitetura da Aplicação Java/Spring Boot**
  - Definição das camadas (Controller, Service, Repository, Entity)
  - Configuração do Spring Boot com profiles (dev/prod)
  - Implementação dos padrões de projeto
- ✅ **Desenvolvimento das Entidades JPA**
  - Mapeamento objeto-relacional com Hibernate
  - Definição de relacionamentos (@OneToMany, @ManyToOne)
  - Validações Bean Validation
- ✅ **Implementação dos Repositories com Generics**
  - JpaRepository<T, ID> com padrão Repository
  - Queries customizadas com Spring Data JPA
  - Métodos de busca específicos
- ✅ **Services e Regras de Negócio**
  - Lógica de negócio para gestão financeira
  - Validações funcionais e de integridade
  - Controle de saldo automático nas transações
- ✅ **Controllers REST (Nível 1 Richardson)**
  - Endpoints RESTful com verbos HTTP adequados
  - DTOs de request/response
  - Tratamento de exceções
- ✅ **Configuração OpenAPI/Swagger**
  - Documentação automática da API
  - Configuração de segurança e metadados
- ✅ **Preparação para Serviços de IA/RAG**
  - Estrutura base para futuras implementações de IA
  - Configurações para integração com serviços de RAG

#### **Barbara** - Cloud/QA Engineer / Database Specialist
- ✅ **Modelagem e Administração de Database**
  - Criação do Diagrama Entidade-Relacionamento (DER)
  - Definição de constraints e relacionamentos
  - Configuração Oracle Database para produção
  - Scripts de inicialização (DDL Query)
- ✅ **Cloud Azure e DevOps**
  - Configuração do ambiente cloud
  - Setup de pipelines de CI/CD
  - Configuração de variáveis de ambiente
- ✅ **Quality Assurance e Testes**
  - Desenvolvimento da suíte de testes automatizados
  - Testes unitários (JUnit + Mockito)
  - Testes de integração (@DataJpaTest, @WebMvcTest)
  - Criação da Collection Postman/Insomnia
  - Validação de persistência e recuperação de dados
- ✅ **Compliance e Documentação**
  - Verificação de conformidade com requisitos
  - Documentação técnica (README.md)
  - Diagramas de arquitetura
  - Cronograma e gestão de entregáveis

#### **Yasmin** - Mobile/Backend Developer
- ✅ **Planejamento de Integração Mobile**
  - Análise dos endpoints da API para consumo mobile
  - Definição de contratos de interface
  - Planejamento da arquitetura mobile
- ✅ **Desenvolvimento .NET (Futuras Sprints)**
  - Preparação para integração com serviços .NET
  - Análise de compatibilidade entre Java e .NET
  - Planejamento de microserviços
- ✅ **Colaboração na API Design**
  - Review dos endpoints REST
  - Sugestões de melhorias para consumo mobile
  - Validação de DTOs para diferentes plataformas

### 🎯 **Marcos de Entrega**

| **Marco** | **Data** | **Entregável** | **Responsável** | **Status** |
|-----------|----------|----------------|-----------------|------------|
| M1 - Database | 28/09/2025 | Modelagem + DER | Barbara | ✅ Entregue |
| M2 - Entidades | 01/10/2025 | Classes JPA + Mapeamentos | Vinicius | ✅ Entregue |
| M3 - Persistência | 05/10/2025 | Repositories + Services | Vinicius | ✅ Entregue |
| M4 - API REST | 07/10/2025 | Controllers + Endpoints | Vinicius | ✅ Entregue |
| M5 - Testes | 09/10/2025 | Suíte de Testes + QA | Barbara | ✅ Entregue |
| M6 - Entrega Final | 10/10/2025 | Documentação Completa | Barbara | ✅ Entregue |

### 📊 **Evolução Demonstrada**
**Sprint 1 (26/09 a 10/10):**
- ✅ Domínio básico implementado (Account + Transaction)
- ✅ CRUD completo funcionando
- ✅ Padrões de projeto aplicados corretamente
- ✅ API REST Nível 1 Richardson implementada
- ✅ Persistência Oracle + H2 configurada
- ✅ Testes automatizados completos
- ✅ Documentação e compliance verificado

### ⚠️ **Riscos Gerenciados**

| **Risco** | **Impacto** | **Mitigação** | **Responsável** | **Status** |
|-----------|-------------|---------------|-----------------|------------|
| Configuração Oracle complexa | Alto | H2 para desenvolvimento + Barbara especialista | Barbara | ✅ Mitigado |
| Integração JPA complexa | Médio | Revisão em pares + testes automatizados | Vinicius/Barbara | ✅ Mitigado |
| Prazos apertados (15 dias) | Alto | Divisão clara de responsabilidades | Equipe | ✅ Mitigado |
| Compliance com requisitos | Alto | Verificação contínua + checklist | Barbara | ✅ Mitigado |

### 📈 **Métricas de Qualidade Atingidas**

- ✅ **POO**: Encapsulamento, herança e polimorfismo aplicados
- ✅ **Coesão**: Cada classe tem responsabilidade única e bem definida
- ✅ **Desacoplamento**: Interfaces, injeção de dependência, camadas isoladas
- ✅ **Padrões de Projeto**: Repository, MVC, DTO, Dependency Injection
- ✅ **REST Nível 1**: URLs únicas, verbos HTTP, códigos de status
- ✅ **Validação Funcional**: Bean Validation em todas as entidades
- ✅ **Cobertura de Testes**: Unitários, integração e API
- ✅ **Documentação**: README completo, diagramas, collection

---
**Documento gerado em:** 05/10/2025  
**Sprint:** 26/09/2025 - 10/10/2025  
**Equipe:** Vinicius (Java/IA), Barbara (Cloud/QA/DB), Yasmin (Mobile/.NET)  
**Versão:** 1.0