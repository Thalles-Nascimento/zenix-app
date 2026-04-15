-- Adicionando Foreign Key da tabela Tenant para atendimentos
ALTER TABLE atendimentos
    ADD CONSTRAINT `fk_tenant_id_atendimentos`
        FOREIGN KEY (`usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para clientes
ALTER TABLE `clientes`
    ADD CONSTRAINT `fk_tenant_id_clientes`
        FOREIGN KEY (`telefone_id`) REFERENCES telefones_clientes (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para fila_atendimentos
ALTER TABLE `fila_atendimentos`
    ADD CONSTRAINT `fk_tenant_id_fila`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para pagamentos
ALTER TABLE `pagamentos`
    ADD CONSTRAINT `fk_tenant_id_pagamentos`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para planos
ALTER TABLE `planos`
    ADD CONSTRAINT `fk_tenant_id_planos`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para servicos
ALTER TABLE `servicos`
    ADD CONSTRAINT `fk_tenant_id_servicos`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para telefones_clientes
ALTER TABLE `telefones_clientes`
    ADD CONSTRAINT `fk_tenant_id_telefone`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para unidades
ALTER TABLE `unidades`
    ADD CONSTRAINT `fk_tenant_id_unidades`
        FOREIGN KEY (`fila_usuario_id`) REFERENCES usuarios (id)
            ON UPDATE CASCADE;

-- Adicionando Foreign Key da tabela Tenant para usuarios
ALTER TABLE `usuarios`
    ADD CONSTRAINT `fk_tenant_id_usuarios`
        FOREIGN KEY (`usuarios_unidade`) REFERENCES unidades (id)
            ON UPDATE CASCADE;