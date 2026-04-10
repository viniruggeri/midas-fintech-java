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

---

### 📅 **Planejamento da Sprint 2**
**Período:** 15/10/2025 - 02/11/2025 (18 dias)

| **Data** | **Atividade** | **Responsável** | **Status** | **Entregáveis** |
|----------|---------------|-----------------|------------|-----------------|
| **15/10 - 17/10** | Análise de Requisitos Sprint 2 | Equipe | ✅ Concluído | Documento de evolução, requisitos HATEOAS |
| **17/10 - 20/10** | Estudo HATEOAS e Nível 3 Richardson | Vinicius | ✅ Concluído | POC Spring HATEOAS, exemplos práticos |
| **20/10 - 23/10** | Implementação HATEOAS nos DTOs | Vinicius | ✅ Concluído | RepresentationModel nos DTOs de resposta |
| **23/10 - 26/10** | Refatoração Controllers com Links | Vinicius | ✅ Concluído | AccountController e TransactionController com HATEOAS |
| **26/10 - 28/10** | Testes HATEOAS e Validação QA | Barbara | ✅ Concluído | Testes validando links hipermídia |
| **28/10 - 30/10** | Atualização Documentação e Swagger | Vinicius | ✅ Concluído | Swagger com exemplos HATEOAS |
| **30/10 - 01/11** | Validação Integração Oracle | Barbara | ✅ Concluído | Testes em ambiente produção |
| **01/11 - 02/11** | Preparação Collection Postman v2 | Barbara | ✅ Concluído | Collection atualizada com HATEOAS |
| **02/11** | Documento de Evolução Sprint 1→2 | Vinicius | ✅ Concluído | evolucao-sprint1-sprint2.md |
| **02/11** | Entrega Final Sprint 2 | Equipe | ✅ Concluído | Todos artefatos atualizados |

---

### 📅 **Planejamento da Sprint 3**
**Período:** 10/02/2026 - 10/04/2026 (60 dias)

| **Data** | **Atividade** | **Responsável** | **Status** | **Entregáveis** |
|----------|---------------|-----------------|------------|-----------------|
| **10/02 - 14/02** | Refinamento de requisitos FIAP e planejamento da sprint | Equipe | ✅ Concluído | Backlog da Sprint 3, critérios de aceite |
| **15/02 - 24/02** | Implementação de segurança (Spring Security + JWT) | Vinicius | ✅ Concluído | Cadeias de segurança web/api, filtros JWT |
| **25/02 - 06/03** | Fluxo CLIENT completo (transferência com validações) | Vinicius | ✅ Concluído | Fluxo funcional de transferência e validações de negócio |
| **07/03 - 14/03** | Fluxo ADMIN completo (estorno com trilha segura) | Vinicius | ✅ Concluído | Estorno administrativo com restrição de acesso |
| **14/03 - 20/03** | Mensageria de estorno + integração Feign de notificação | Vinicius | ✅ Concluído | Evento JMS de estorno, consumer e envio interno email/sms |
| **15/03 - 22/03** | OAuth2 GitHub opcional e configuração por ambiente | Vinicius | ✅ Concluído | Login social opcional e variáveis de ambiente documentadas |
| **23/03 - 30/03** | Atualização do frontend Thymeleaf | Equipe | ✅ Concluído | Telas login/dashboard/transferência/admin atualizadas |
| **31/03 - 08/04** | Atualização de documentação, diagramas e collection | Barbara | ✅ Concluído | README, cronograma, diagramas PNG, Postman e HTTP tests |
| **09/04 - 10/04** | Validação final de testes e entrega da sprint | Equipe | ✅ Concluído | Suíte de testes verde e pacote de entrega final |

---

### 📋 **Detalhamento das Responsabilidades por Membro - Sprint 2**

#### **Vinicius** - Tech Lead / IA Engineer
- ✅ **Implementação HATEOAS (Nível 3 Richardson)**
  - Adição da dependência spring-boot-starter-hateoas
  - Refatoração dos DTOs para estender RepresentationModel
  - Implementação de links hipermídia em todos os endpoints
  - Links bidirecionais entre recursos (Account ↔ Transaction)
- ✅ **Refatoração dos Controllers**
  - Métodos retornando CollectionModel para listas
  - Adição de links self, update, delete, relacionamentos
  - Navegação autodescritiva entre recursos
- ✅ **Documentação Técnica**
  - Documento de evolução Sprint 1 → Sprint 2
  - Atualização do README.md
  - Exemplos de resposta HATEOAS

#### **Barbara** - Cloud/QA Engineer / Database Specialist
- ✅ **Testes e Validação HATEOAS**
  - Validação da estrutura _links nas respostas
  - Testes de navegação entre recursos
  - Validação da corretude dos links gerados
- ✅ **Persistência Oracle**
  - Validação em ambiente de produção
  - Testes de carga e performance
  - Verificação de integridade dos dados
- ✅ **Atualização Collection Postman**
  - Novos testes para endpoints HATEOAS
  - Validação automática de links
  - Documentação de exemplos de uso

#### **Yasmin** - Mobile/Backend Developer
- ✅ **Validação Integração Mobile**
  - Consumo da API com HATEOAS no cliente mobile
  - Testes de navegação hipermídia
  - Feedback sobre usabilidade dos links

---

### 🎯 **Marcos de Entrega**

| **Marco** | **Data** | **Entregável** | **Responsável** | **Status** |
|-----------|----------|----------------|-----------------|------------|
| **M1 - Database** | 28/09/2025 | Modelagem + DER | Barbara | ✅ Entregue |
| **M2 - Entidades** | 01/10/2025 | Classes JPA + Mapeamentos | Vinicius | ✅ Entregue |
| **M3 - Persistência** | 05/10/2025 | Repositories + Services | Vinicius | ✅ Entregue |
| **M4 - API REST Nível 1** | 07/10/2025 | Controllers + Endpoints | Vinicius | ✅ Entregue |
| **M5 - Testes Sprint 1** | 09/10/2025 | Suíte de Testes + QA | Barbara | ✅ Entregue |
| **M6 - Entrega Sprint 1** | 10/10/2025 | Documentação Completa | Barbara | ✅ Entregue |
| **M7 - HATEOAS Implementado** | 26/10/2025 | API Nível 3 Richardson | Vinicius | ✅ Entregue |
| **M8 - Testes Sprint 2** | 01/11/2025 | Validação HATEOAS + QA | Barbara | ✅ Entregue |
| **M9 - Entrega Sprint 2** | 02/11/2025 | Evolução Completa | Equipe | ✅ Entregue |

---

### 📊 **Evolução Demonstrada Sprint 1 → Sprint 2**

#### **Sprint 1 (26/09 a 10/10) - Nível 1 Richardson**
- ✅ Domínio básico implementado (Account + Transaction)
- ✅ API REST com URIs e verbos HTTP
- ✅ Persistência em Oracle Database
- ✅ Validações funcionais
- ✅ Testes automatizados básicos
- ✅ Documentação Swagger/OpenAPI

#### **Sprint 2 (15/10 a 02/11) - Nível 3 Richardson**
- ✅ **HATEOAS implementado** (principal evolução)
- ✅ Links hipermídia em todas as respostas
- ✅ Navegação autodescritiva entre recursos
- ✅ CollectionModel para listas
- ✅ Links bidirecionais Account ↔ Transaction
- ✅ Testes validando estrutura HATEOAS
- ✅ Documentação de evolução detalhada

---

### 📈 **Métricas de Qualidade**

| **Métrica** | **Sprint 1** | **Sprint 2** | **Evolução** |
|-------------|-------------|-------------|-------------|
| **Nível Maturidade Richardson** | Nível 1 | Nível 3 | +200% |
| **Cobertura de Testes** | 75% | 82% | +7% |
| **Endpoints Documentados** | 10 | 10 | Mantido |
| **Links HATEOAS por Response** | 0 | 5-6 | +∞ |
| **Navegabilidade da API** | Manual | Autodescritiva | ⭐⭐⭐ |

---

### 🚀 **Roadmap Futuro (Sprint 4 e 5)**

#### Sprint 4 (Planejada):
- Mensageria (RabbitMQ/Kafka)
- Observabilidade (Actuator + Prometheus + Grafana)
- Circuit Breaker (Resilience4j)
- Integração completa com IA/RAG

#### Sprint 5 (Planejada):
- Cache com Redis para leitura intensiva
- Paginação avançada com filtros combinados
- Hardening de segurança para produção
- Melhorias de performance e custo em cloud

---

**Última Atualização:** 10/04/2026  
**Versão do Documento:** 3.0  
**Status Geral:** ✅ Sprint 3 Completa - Pronto para Apresentação
