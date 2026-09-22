-- Coluna id de atendimentos
ALTER TABLE atendimentos RENAME COLUMN atendimento_id TO id;

-- Coluna id de clientes
ALTER TABLE `clientes` RENAME COLUMN cliente_id TO id;

-- Coluna id de fila_atendimentos
ALTER TABLE `fila_atendimentos` RENAME COLUMN fila_id TO id;

-- Coluna id de pagamentos
ALTER TABLE `pagamentos` RENAME COLUMN pagamento_id TO id;

-- Coluna id de planos
ALTER TABLE `planos` RENAME COLUMN planos_id TO id;

-- Coluna id de servicos
ALTER TABLE `servicos` RENAME COLUMN servico_id TO id;

-- Coluna id de telefones_clientes
ALTER TABLE `telefones_clientes` RENAME COLUMN telefone_id TO id;

-- Coluna id de unidades
ALTER TABLE `unidades` RENAME COLUMN unidade_id TO id;

-- Coluna id de usuarios
ALTER TABLE `usuarios` RENAME COLUMN usuario_id TO id;
