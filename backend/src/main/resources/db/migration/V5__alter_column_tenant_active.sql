-- atendimentos: renomeia colunas existentes
ALTER TABLE atendimentos
    RENAME COLUMN atendimento_created_at TO created_at,
    RENAME COLUMN atendimento_update_at TO updated_at,
    RENAME COLUMN atendimento_delete_at TO deleted_at;

-- fila_atendimentos: renomeia existentes e adiciona created_at que faltava
ALTER TABLE fila_atendimentos
    RENAME COLUMN fila_update_at TO updated_at,
    RENAME COLUMN fila_delete_at TO deleted_at,
    ADD COLUMN created_at DATETIME;

-- clientes: renomeia existente e adiciona os que faltavam
ALTER TABLE clientes
    RENAME COLUMN cliente_created TO created_at,
    ADD COLUMN updated_at DATETIME,
    ADD COLUMN deleted_at DATETIME;

-- pagamentos: renomeia existentes e adiciona deleted_at que faltava
ALTER TABLE pagamentos
    RENAME COLUMN pagamento_created_at TO created_at,
    RENAME COLUMN pagamento_update_at TO updated_at,
    ADD COLUMN deleted_at DATETIME;

-- planos: renomeia existentes e adiciona deleted_at que faltava
ALTER TABLE planos
    RENAME COLUMN planos_created_at TO created_at,
    RENAME COLUMN planos_update_at TO updated_at,
    ADD COLUMN deleted_at DATETIME;

-- servicos: renomeia existentes e adiciona deleted_at que faltava
ALTER TABLE servicos
    RENAME COLUMN servico_created_at TO created_at,
    RENAME COLUMN servico_update_at TO updated_at,
    ADD COLUMN deleted_at DATETIME;

-- unidades, usuarios, telefones_clientes: não tinham nenhuma coluna de auditoria
-- precisam receber as três colunas
ALTER TABLE unidades
    ADD COLUMN created_at DATETIME,
    ADD COLUMN updated_at DATETIME,
    ADD COLUMN deleted_at DATETIME;

ALTER TABLE usuarios
    ADD COLUMN created_at DATETIME,
    ADD COLUMN updated_at DATETIME,
    ADD COLUMN deleted_at DATETIME;

ALTER TABLE telefones_clientes
    ADD COLUMN created_at DATETIME,
    ADD COLUMN updated_at DATETIME,
    ADD COLUMN deleted_at DATETIME;