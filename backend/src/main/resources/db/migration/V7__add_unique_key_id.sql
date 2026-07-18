-- Coluna id de atendimentos
ALTER TABLE atendimentos
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de clientes
ALTER TABLE `clientes`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de fila_atendimentos
ALTER TABLE `fila_atendimentos`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de pagamentos
ALTER TABLE `pagamentos`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de planos
ALTER TABLE `planos`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de servicos
ALTER TABLE `servicos`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de telefones_clientes
ALTER TABLE `telefones_clientes`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de unidades
ALTER TABLE `unidades`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;

-- Coluna id de usuarios
ALTER TABLE `usuarios`
    MODIFY COLUMN id VARCHAR(36) NOT NULL UNIQUE;