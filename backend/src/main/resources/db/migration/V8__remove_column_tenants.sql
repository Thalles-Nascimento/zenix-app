-- Removendo coluna Tenant de atendimentos
ALTER TABLE atendimentos
    DROP COLUMN `tenant_id`;

-- Removendo coluna Tenant de clientes
ALTER TABLE `clientes`
    DROP COLUMN `tenant_id`;

-- Removendo coluna Tenant de fila_atendimentos
ALTER TABLE `fila_atendimentos`
    DROP COLUMN `tenant_id`;

-- Removendo Foreign Key de usuarios
ALTER TABLE `usuarios`
    DROP FOREIGN KEY `FKeq2qjef9kg8q9gfxessikg61x`;

-- Removendo Foreign Key de unidades
ALTER TABLE `unidades`
    DROP FOREIGN KEY `FKmkd8t7s1yt75sx2mqk4xm9gsn`;

-- Removendo coluna Tenant de usuarios
ALTER TABLE `usuarios`
    DROP COLUMN `tenant_id`;

-- Removendo coluna Tenant de unidades
ALTER TABLE `unidades`
    DROP COLUMN `tenant_id`;