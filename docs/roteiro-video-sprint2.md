# Roteiro para Vídeo de Apresentação - Sprint 2: Midas API

**Objetivo do vídeo:** Apresentar a evolução do projeto Midas API da Sprint 1 para a Sprint 2, com foco na implementação de HATEOAS (Nível 3 de Maturidade de Richardson), e demonstrar o cumprimento dos requisitos da disciplina.

**Duração estimada:** 3-5 minutos.

---

### Bloco 1: Introdução (0 a 45 segundos)

**O que mostrar na tela:** Slide inicial com o logo da Midas, nome da aplicação, e nome dos integrantes.

**Locutor (Voz):**
"Olá, professores! Somos a equipe do projeto Midas Fintech, composta por Vinicius, Barbara e Yasmin."

"Hoje, vamos apresentar a evolução da nossa **proposta tecnológica**: a Midas API, uma API RESTful desenvolvida em Java com Spring Boot. Nosso **público-alvo** são pessoas que buscam uma maneira moderna e automatizada de gerenciar suas finanças. O **problema que solucionamos** é a dificuldade de organizar contas e transações de forma simples e centralizada."

"Neste vídeo, vamos demonstrar a evolução do projeto na Sprint 2, onde o foco foi alcançar o nível 3 de maturidade REST com a implementação de HATEOAS."

---

### Bloco 2: Evolução da Sprint 1 para a Sprint 2 (45 segundos a 2 minutos)

**O que mostrar na tela:**
1.  **Postman/Insomnia:** Mostrar uma requisição GET para uma conta (`/api/accounts/{id}`) e a resposta JSON **sem links** (simulando a Sprint 1).
2.  **Postman/Insomnia:** Mostrar a **mesma requisição** agora no projeto da Sprint 2, destacando a nova seção `_links` na resposta.

**Locutor (Voz):**
"Na Sprint 1, entregamos uma API REST funcional, em conformidade com o nível 1 de maturidade, capaz de realizar operações de CRUD para contas e transações. A resposta era um JSON simples, como este."

*(Mostra a resposta antiga na tela)*

"Para a Sprint 2, o nosso principal avanço foi evoluir a API para o **nível 3 de maturidade de Richardson**, implementando o conceito de **HATEOAS** (Hypermedia as the Engine of Application State)."

"Mas o que isso significa na prática? Significa que nossa API agora é 'autodescobrivel'. Além dos dados, a resposta inclui links que guiam o cliente sobre as próximas ações possíveis, sem que ele precise conhecer previamente todas as URLs."

*(Mostra a nova resposta com a seção `_links`)*

"Vejam a diferença. Ao buscar uma conta, a API agora informa o link para o próprio recurso (`self`) e também um link para acessar a lista de 'todas as contas'. O cliente pode simplesmente seguir esses links para navegar pela API, tornando a integração muito mais robusta e desacoplada."

---

### Bloco 3: Demonstração Técnica (2 minutos a 3 minutos)

**O que mostrar na tela:**
1.  **IntelliJ IDEA:** Mostrar o `pom.xml`, destacando a dependência `spring-boot-starter-hateoas`.
2.  **IntelliJ IDEA:** Mostrar um trecho do `AccountController.java`, apontando para o código que gera os links (ex: `linkTo(methodOn(...))`).
3.  **Navegador:** Abrir a documentação do Swagger (`/swagger-ui.html`) para mostrar os endpoints documentados.

**Locutor (Voz):**
"Para implementar o HATEOAS, adicionamos o `spring-boot-starter-hateoas` ao nosso projeto."

*(Mostra o `pom.xml`)*

"Nos nossos controladores, utilizamos o `WebMvcLinkBuilder` para construir dinamicamente os links, associando-os às nossas respostas. Isso garante que, se uma URL mudar no futuro, os links serão atualizados automaticamente, sem quebrar os clientes."

*(Mostra o trecho do controller)*

"Toda a nossa API continua documentada com o Swagger, facilitando os testes e a integração."

*(Mostra a interface do Swagger UI)*

---

### Bloco 4: Gestão e Documentação (3 minutos a 3:30 minutos)

**O que mostrar na tela:**
1.  **Navegador:** Página do repositório no GitHub.
2.  **Navegador:** Mostrar o arquivo `README.md` atualizado.
3.  **Navegador:** Navegar para a pasta `docs` e mostrar o `cronograma-desenvolvimento.md` e o novo `evolucao-sprint1-sprint2.md`.

**Locutor (Voz):**
"Todo o nosso trabalho está versionado em um repositório público no GitHub, garantindo a gestão de configuração de todos os artefatos."

"O arquivo `README.md` foi atualizado com as instruções de execução e a documentação do projeto. Na pasta `docs`, mantemos o cronograma e um documento detalhando a evolução entre as Sprints, como solicitado."

---

### Bloco 5: Conclusão (3:30 minutos a 4 minutos)

**O que mostrar na tela:** Slide final de agradecimento.

**Locutor (Voz):**
"Nesta Sprint, demonstramos uma evolução clara na qualidade e na arquitetura da nossa API, alcançando o nível 3 de maturidade REST. O código está mais coeso, desacoplado e preparado para futuras expansões."

"Para as próximas Sprints, nosso foco será a implementação de novas funcionalidades, a integração contínua com o banco de dados Oracle em nuvem e o aprimoramento da segurança da API."

"Obrigado pela atenção!"

