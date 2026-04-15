-- Adicionando coluna Tenant para atendimentos
ALTER TABLE atendimentos ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para clientes
ALTER TABLE `clientes` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para fila_atendimentos
ALTER TABLE `fila_atendimentos` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para pagamentos
ALTER TABLE `pagamentos` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para planos
ALTER TABLE `planos` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para servicos
ALTER TABLE `servicos` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para telefones_clientes
ALTER TABLE `telefones_clientes` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para unidades
ALTER TABLE `unidades` ADD `tenant_id` VARCHAR(36) NOT NULL;

-- Adicionando coluna Tenant para usuarios
ALTER TABLE `usuarios` ADD `tenant_id` VARCHAR(36) NOT NULL;