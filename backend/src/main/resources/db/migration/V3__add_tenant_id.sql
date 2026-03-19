-- unidades
ALTER TABLE unidades
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER unidade_id,
    ADD INDEX idx_unidades_tenant (tenant_id),
    ADD CONSTRAINT fk_unidades_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- usuarios
ALTER TABLE usuarios
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER usuario_id,
    ADD INDEX idx_usuarios_tenant (tenant_id),
    ADD CONSTRAINT fk_usuarios_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- clientes
ALTER TABLE clientes
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER cliente_id,
    ADD INDEX idx_clientes_tenant (tenant_id),
    ADD CONSTRAINT fk_clientes_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- servicos
ALTER TABLE servicos
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER servico_id,
    ADD INDEX idx_servicos_tenant (tenant_id),
    ADD CONSTRAINT fk_servicos_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- pagamentos
ALTER TABLE pagamentos
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER pagamento_id,
    ADD INDEX idx_pagamentos_tenant (tenant_id),
    ADD CONSTRAINT fk_pagamentos_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- planos
ALTER TABLE planos
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER planos_id,
    ADD INDEX idx_planos_tenant (tenant_id),
    ADD CONSTRAINT fk_planos_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- atendimentos
ALTER TABLE atendimentos
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER atendimento_id,
    ADD INDEX idx_atendimentos_tenant (tenant_id),
    ADD CONSTRAINT fk_atendimentos_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- fila_atendimentos
ALTER TABLE fila_atendimentos
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER fila_id,
    ADD INDEX idx_fila_tenant (tenant_id),
    ADD CONSTRAINT fk_fila_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);

-- telefones_clientes
ALTER TABLE telefones_clientes
    ADD COLUMN tenant_id CHAR(36) NOT NULL AFTER telefone_id,
    ADD INDEX idx_telefones_tenant (tenant_id),
    ADD CONSTRAINT fk_telefones_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);