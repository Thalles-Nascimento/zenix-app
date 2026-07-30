
-- Criando um full text index na tabela `clientes` para as colunas `cliente_nome`
create fulltext index search_idx on clientes (cliente_nome);