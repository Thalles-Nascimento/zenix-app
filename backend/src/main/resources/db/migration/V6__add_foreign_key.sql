-- Adicionando Foreign Key de atendimentos
ALTER TABLE atendimentos
    ADD CONSTRAINT `fk_usuario_id`
        FOREIGN KEY (`usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key de clientes
ALTER TABLE `clientes`
    ADD CONSTRAINT `fk_telefone_id`
        FOREIGN KEY (`telefone_id`) REFERENCES telefones_clientes (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key de clientes
ALTER TABLE `clientes`
    ADD CONSTRAINT `fk_planos_id`
        FOREIGN KEY (`planos_id`) REFERENCES planos (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key de fila_atendimentos
ALTER TABLE `fila_atendimentos`
    ADD CONSTRAINT `fk_usuario_id_fila`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key de usuarios
ALTER TABLE `usuarios`
    ADD CONSTRAINT `fk_unidades_id`
        FOREIGN KEY (`usuarios_unidade`) REFERENCES unidades (id)
            ON UPDATE CASCADE;