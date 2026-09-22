-- Dropando Foreign Key da tabela Tenant para atendimentos
ALTER TABLE atendimentos
    DROP FOREIGN KEY `fk_tenant_id_atendimentos`;

-- Dropando Foreign Key da tabela Tenant para clientes
ALTER TABLE `clientes`
    DROP FOREIGN KEY `fk_tenant_id_clientes`;

-- Dropando Foreign Key da tabela Tenant para fila_atendimentos
ALTER TABLE `fila_atendimentos`
    DROP FOREIGN KEY `fk_tenant_id_fila`;

-- Dropando Foreign Key da tabela Tenant para pagamentos
ALTER TABLE `pagamentos`
    DROP FOREIGN KEY `fk_tenant_id_pagamentos`;

-- Dropando Foreign Key da tabela Tenant para planos
ALTER TABLE `planos`
    DROP FOREIGN KEY `fk_tenant_id_planos`;

-- Dropando Foreign Key da tabela Tenant para servicos
ALTER TABLE `servicos`
    DROP FOREIGN KEY `fk_tenant_id_servicos`;

-- Dropando Foreign Key da tabela Tenant para telefones_clientes
ALTER TABLE `telefones_clientes`
    DROP FOREIGN KEY `fk_tenant_id_telefone`;

-- Dropando Foreign Key da tabela Tenant para unidades
ALTER TABLE `unidades`
    DROP FOREIGN KEY `fk_tenant_id_unidades`;

-- Dropando Foreign Key da tabela Tenant para usuarios
ALTER TABLE `usuarios`
    DROP FOREIGN KEY `fk_tenant_id_usuarios`;

ALTER TABLE `tenants` DROP COLUMN `tenant_cnpj`;

ALTER TABLE `tenants` ADD COLUMN `tenant_cnpj` VARCHAR(16) NOT NULL UNIQUE;