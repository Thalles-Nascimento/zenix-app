-- Removendo Foreign Key de atendimentos
ALTER TABLE atendimentos
    MODIFY COLUMN `usuario_id` VARCHAR(36) DEFAULT NULL;

-- Removendo Foreign Key de clientes
ALTER TABLE `clientes`
    MODIFY COLUMN `telefone_id` VARCHAR(36) DEFAULT NULL;

ALTER TABLE `clientes`
    MODIFY COLUMN `planos_id` VARCHAR(36) DEFAULT NULL;

-- Removendo Foreign Key de fila_atendimentos
ALTER TABLE `fila_atendimentos`
    MODIFY COLUMN `fila_usuario_id` VARCHAR(36) DEFAULT NULL;

-- Removendo Foreign Key de usuarios
ALTER TABLE `usuarios`
    MODIFY COLUMN `usuarios_unidade` VARCHAR(36) DEFAULT NULL;