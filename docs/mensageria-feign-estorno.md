# Mensageria + Feign no Fluxo de Estorno

## Objetivo
Documentar como funciona o envio de notificacao apos estorno, usando:
- Fila JMS (Artemis embutido)
- Feign Client
- Endpoints internos para simulacao de envio de email/sms

## Componentes
- Publicador de evento: `EstornoNotificacaoPublisher`
- Evento de dominio: `EstornoNotificacaoEvent`
- Consumidor JMS: `EstornoNotificacaoConsumer`
- Cliente HTTP: `NotificacaoFeignClient`
- Servico de destino (interno): `NotificacaoInternaController`

## Sequencia do fluxo
1. Admin executa estorno em `/admin/estorno`.
2. `FluxoFinanceiroServiceImpl.estornarTransacao(...)` persiste a transacao de estorno.
3. O service publica evento na fila `estorno.notificacao.queue`.
4. `EstornoNotificacaoConsumer` recebe o evento.
5. O consumer chama Feign para notificacao por canais disponiveis da conta:
   - `POST /internal/notificacoes/email` (se `emailNotificacao` existir)
   - `POST /internal/notificacoes/sms` (se `telefoneSms` existir)
6. O controller interno retorna `202 Accepted` e registra log de envio.

## Configuracao
No `application.yaml`:
- `spring.artemis.mode=embedded`
- `midas.messaging.estorno-queue=estorno.notificacao.queue`
- `midas.notification.base-url=http://localhost:${server.port:8080}`

## Campos de contato na conta
A entidade `Account` foi expandida com:
- `emailNotificacao`
- `telefoneSms`

Esses campos chegam via:
- `AccountRequestDto`
- `AccountResponseDto`

## Banco de dados
A migracao `V3__add_account_contact_channels.sql` adiciona:
- `accounts.email_notificacao`
- `accounts.telefone_sms`

## Testes relacionados
- `FluxoFinanceiroServiceImplTest`
  - valida estorno com publicacao de evento
- `EstornoNotificacaoConsumerTest`
  - valida envio de email/sms quando canais existem
  - valida nao envio quando conta nao possui contatos

## Pergunta comum
O Feign esta chamando servico interno?
- Sim. O `NotificacaoFeignClient` aponta para a propria aplicacao (base-url local), com endpoints internos de notificacao.
- Isso foi escolhido para manter simplicidade de entrega e demonstrar claramente o requisito de cliente Feign.
