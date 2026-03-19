CREATE TABLE tenants (
    tenant_id CHAR(36) PRIMARY KEY,
    tenant_name VARCHAR(100) UNIQUE NOT NULL,
    tenant_slug VARCHAR(50) UNIQUE NOT NULL,
    tenant_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    deleted_at DATETIME
);