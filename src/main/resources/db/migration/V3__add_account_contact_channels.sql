ALTER TABLE accounts ADD COLUMN email_notificacao VARCHAR(160);
ALTER TABLE accounts ADD COLUMN telefone_sms VARCHAR(20);

UPDATE accounts
SET email_notificacao = LOWER(REPLACE(nome, ' ', '')) || '@midas.local'
WHERE email_notificacao IS NULL;

UPDATE accounts
SET telefone_sms = '+5511990000000'
WHERE telefone_sms IS NULL;
