CREATE TABLE `telefones_clientes` (
                                      `telefone_id` bigint NOT NULL AUTO_INCREMENT,
                                      `telefone_cliente` varchar(255) DEFAULT NULL,
                                      PRIMARY KEY (`telefone_id`),
                                      UNIQUE KEY `UK9c9t0lqkw8vcsl7n7kbf99a7o` (`telefone_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `unidades` (
                            `unidade_id` bigint NOT NULL AUTO_INCREMENT,
                            `unidade_endereco` varchar(255) DEFAULT NULL,
                            `unidade_nome` varchar(100) DEFAULT NULL,
                            `unidade_status` int NOT NULL,
                            PRIMARY KEY (`unidade_id`),
                            UNIQUE KEY `UK3xujks0jrv3es8otowv5dnada` (`unidade_nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `planos` (
                          `planos_id` bigint NOT NULL AUTO_INCREMENT,
                          `planos_created_at` datetime(6) DEFAULT NULL,
                          `planos_limite` int DEFAULT NULL,
                          `planos_descricao` varchar(255) DEFAULT NULL,
                          `planos_status` int DEFAULT NULL,
                          `planos_update_at` datetime(6) DEFAULT NULL,
                          `planos_valor` double DEFAULT NULL,
                          PRIMARY KEY (`planos_id`),
                          UNIQUE KEY `UK883rwil0nufn00mgqod5f7e60` (`planos_descricao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pagamentos` (
                              `pagamento_id` bigint NOT NULL AUTO_INCREMENT,
                              `pagamento_created_at` datetime(6) DEFAULT NULL,
                              `pagamento_descricao` varchar(255) DEFAULT NULL,
                              `pagamento_status` int DEFAULT NULL,
                              `pagamento_update_at` datetime(6) DEFAULT NULL,
                              PRIMARY KEY (`pagamento_id`),
                              UNIQUE KEY `UKmkeyekqcaaf6bvkhqs213ktqp` (`pagamento_descricao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `servicos` (
                            `servico_id` bigint NOT NULL AUTO_INCREMENT,
                            `servico_created_at` datetime(6) DEFAULT NULL,
                            `servico_descricao` varchar(255) DEFAULT NULL,
                            `servico_status` int DEFAULT NULL,
                            `servico_update_at` datetime(6) DEFAULT NULL,
                            `servico_valor` double DEFAULT NULL,
                            PRIMARY KEY (`servico_id`),
                            UNIQUE KEY `UKlam1a7s6xnx5fx7my62rgswh5` (`servico_descricao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `usuarios` (
                            `usuario_id` bigint NOT NULL AUTO_INCREMENT,
                            `usuario_cpf` varchar(255) DEFAULT NULL,
                            `usuario_email` varchar(255) DEFAULT NULL,
                            `enabled` bit(1) NOT NULL,
                            `usuario_grupo` tinyint DEFAULT NULL,
                            `usuario_nome` varchar(255) DEFAULT NULL,
                            `usuario_senha` varchar(255) DEFAULT NULL,
                            `usuario_status` int DEFAULT NULL,
                            `usuarios_unidade` bigint DEFAULT NULL,
                            PRIMARY KEY (`usuario_id`),
                            UNIQUE KEY `UKo0t0b00w1ljg7yi1kxx3a8lb4` (`usuario_cpf`),
                            UNIQUE KEY `UKn8i4ljdjbtb9gjp7p6vdaye1x` (`usuario_email`),
                            KEY `FK6mgxi93sbein8tniudejof5w5` (`usuarios_unidade`),
                            CONSTRAINT `FK6mgxi93sbein8tniudejof5w5` FOREIGN KEY (`usuarios_unidade`) REFERENCES `unidades` (`unidade_id`),
                            CONSTRAINT `usuarios_chk_1` CHECK ((`usuario_grupo` between 0 and 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `clientes` (
                            `cliente_id` bigint NOT NULL AUTO_INCREMENT,
                            `cliente_created` datetime(6) DEFAULT NULL,
                            `cliente_nome` varchar(120) DEFAULT NULL,
                            `cliente_retorno` int DEFAULT NULL,
                            `telefone_id` bigint DEFAULT NULL,
                            `cliente_atendimentos_mes` int DEFAULT NULL,
                            `cliente_data_renovacao` date DEFAULT NULL,
                            `cliente_status` int DEFAULT NULL,
                            `planos_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`cliente_id`),
                            UNIQUE KEY `clientes_unique` (`cliente_nome`),
                            KEY `FK90ilawixhy4cn1jh0v1hbeomd` (`telefone_id`),
                            KEY `FKp265cni3jx6jwninhh7rva4tc` (`planos_id`),
                            CONSTRAINT `FK90ilawixhy4cn1jh0v1hbeomd` FOREIGN KEY (`telefone_id`) REFERENCES `telefones_clientes` (`telefone_id`),
                            CONSTRAINT `FKp265cni3jx6jwninhh7rva4tc` FOREIGN KEY (`planos_id`) REFERENCES `planos` (`planos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `fila_atendimentos` (
                                     `fila_id` bigint NOT NULL AUTO_INCREMENT,
                                     `fila_final_atendimento` time(6) DEFAULT NULL,
                                     `fila_pagamento` varchar(255) DEFAULT NULL,
                                     `fila_horario` time(6) NOT NULL,
                                     `fila_inicio_atendimento` time(6) DEFAULT NULL,
                                     `fila_client` varchar(120) DEFAULT NULL,
                                     `fila_servico` json DEFAULT NULL,
                                     `fila_status` tinyint NOT NULL,
                                     `fila_telefone` varchar(255) DEFAULT NULL,
                                     `fila_usuario_id` bigint DEFAULT NULL,
                                     `fila_delete_at` datetime(6) DEFAULT NULL,
                                     `fila_update_at` datetime(6) DEFAULT NULL,
                                     PRIMARY KEY (`fila_id`),
                                     KEY `FKskt8yguibxvy40f2r08g4ykb6` (`fila_usuario_id`),
                                     CONSTRAINT `FKskt8yguibxvy40f2r08g4ykb6` FOREIGN KEY (`fila_usuario_id`) REFERENCES `usuarios` (`usuario_id`),
                                     CONSTRAINT `fila_atendimentos_chk_1` CHECK ((`fila_status` between 0 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `atendimentos` (
                                `atendimento_id` bigint NOT NULL AUTO_INCREMENT,
                                `atendimento_data` varchar(255) NOT NULL,
                                `atendimento_descricao` varchar(120) DEFAULT NULL,
                                `atendimento_pagamento` varchar(255) NOT NULL,
                                `atendimento_servico` json DEFAULT NULL,
                                `atendimento_status` int NOT NULL,
                                `atendimento_valor` double DEFAULT NULL,
                                `usuario_id` bigint DEFAULT NULL,
                                `atendimento_created_at` datetime(6) DEFAULT NULL,
                                `atendimento_delete_at` datetime(6) DEFAULT NULL,
                                `atendimento_observacao` varchar(500) DEFAULT NULL,
                                `atendimento_update_at` datetime(6) DEFAULT NULL,
                                PRIMARY KEY (`atendimento_id`),
                                KEY `FKt2sos51vrxy8nheoixew07sek` (`usuario_id`),
                                CONSTRAINT `FKt2sos51vrxy8nheoixew07sek` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;