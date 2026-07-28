
-- Alterando Foreign Key de clientes
ALTER TABLE `clientes`
    ADD CONSTRAINT `fk_planos_id`
        FOREIGN KEY (`planos_id`) REFERENCES planos (id)
            ON UPDATE CASCADE
            ON DELETE SET NULL;