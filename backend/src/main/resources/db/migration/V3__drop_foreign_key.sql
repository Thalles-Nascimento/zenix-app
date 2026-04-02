-- Removendo Foreign Key de atendimentos
ALTER TABLE atendimentos
    DROP FOREIGN KEY `FKt2sos51vrxy8nheoixew07sek`;

-- Removendo Foreign Key de clientes
ALTER TABLE `clientes`
    DROP FOREIGN KEY `FKp265cni3jx6jwninhh7rva4tc`,
    DROP FOREIGN KEY `FK90ilawixhy4cn1jh0v1hbeomd`;

-- Removendo Foreign Key de fila_atendimentos
ALTER TABLE `fila_atendimentos`
    DROP FOREIGN KEY `FKskt8yguibxvy40f2r08g4ykb6`;

-- Removendo Foreign Key de usuarios
ALTER TABLE `usuarios`
    DROP FOREIGN KEY `FK6mgxi93sbein8tniudejof5w5`;
