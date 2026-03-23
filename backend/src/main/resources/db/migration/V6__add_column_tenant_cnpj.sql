ALTER TABLE tenants
    ADD COLUMN tenant_cnpj VARCHAR(18) UNIQUE NOT NULL AFTER tenant_slug;