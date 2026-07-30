
-- Alterando nulo da coluna cliente_atendimentos_mes de clientes
ALTER TABLE `clientes`
    MODIFY COLUMN `cliente_atendimentos_mes` INT NULL;