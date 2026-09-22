# Zenix App — Backend

<p>
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.0.3" />
  <img src="https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white" alt="MySQL 8" />
  <img src="https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/Docs-Swagger%2FOpenAPI-85EA2D?logo=swagger&logoColor=black" alt="Swagger/OpenAPI" />
</p>

> Para contexto do produto como um todo (domínio, funcionalidades, roadmap), veja o [README raiz](../README.md).

## Sobre este módulo

Este é o backend do Zenix App: uma API REST em Java/Spring Boot responsável pelas regras de negócio, persistência e autenticação do sistema de gestão de barbearias. É consumida pela SPA em [`frontend/`](../frontend).

## Arquitetura

A API segue uma arquitetura em camadas simples e pragmática: `Controller → Service → Repository → Entity`, com DTOs de request/response mapeados a partir das entidades via MapStruct/ModelMapper.

```mermaid
flowchart TD
    Client["Cliente HTTP<br/>SPA / Swagger"]
    Filter["SecurityFilter<br/>valida o JWT e define o tenant"]
    Controller["Controller<br/>@RestController"]
    Service["Service<br/>regras de negócio"]
    Repository["Repository<br/>Spring Data JPA"]
    DB[("MySQL")]

    Client -->|"cookie auth_token"| Filter
    Filter --> Controller
    Controller --> Service
    Service --> Repository
    Repository --> DB
    Controller -.->|"JSON de resposta"| Client
```

<details>
<summary>Ver o fluxo em texto</summary>

```text
Cliente HTTP (SPA / Swagger)
        |  requisição + cookie auth_token
        v
SecurityFilter        -> valida o JWT e popula o TenantContext
        v
Controller            -> @RestController
        v
Service               -> regras de negócio
        v
Repository            -> Spring Data JPA
        v
MySQL

A resposta volta pelo caminho inverso, convertida em DTO
pelo Service e serializada em JSON pelo Controller.
```

</details>

O schema do banco (MySQL) é versionado e aplicado automaticamente na inicialização via **Flyway** (`configs/flyway/FlywayConfig`), a partir das migrations em `src/main/resources/db/migration`.

## Modelo de domínio

```mermaid
erDiagram
    TENANTS ||--o{ UNIDADES : possui
    UNIDADES ||--o{ USUARIOS : possui
    USUARIOS ||--o{ ATENDIMENTO : registra
    USUARIOS ||--o{ FILA : atende
    TELEFONE_CLIENTE ||--o{ CLIENTES : identifica
    PLANOS ||--o{ CLIENTES : assina

    TENANTS {
        string nome
        string slug
    }
    UNIDADES {
        string nome
    }
    USUARIOS {
        string nome
        string grupo
    }
    CLIENTES {
        string nomeCliente
        int totalRetornos
    }
    TELEFONE_CLIENTE {
        string numero
    }
    ATENDIMENTO {
        decimal valor
        string data
    }
    FILA {
        string nomeCliente
        string status
    }
    PLANOS {
        decimal valor
        int limiteAtendimentos
    }
```

<details>
<summary>Ver os relacionamentos em texto</summary>

```text
TENANTS           1 --- N  UNIDADES      (uma barbearia/tenant possui várias unidades)
UNIDADES          1 --- N  USUARIOS      (uma unidade possui vários usuários)
USUARIOS          1 --- N  ATENDIMENTO   (um barbeiro registra vários atendimentos)
USUARIOS          1 --- N  FILA          (um barbeiro atende várias entradas da fila)
TELEFONE_CLIENTE  1 --- N  CLIENTES      (o telefone identifica o cliente)
PLANOS            1 --- N  CLIENTES      (um plano é assinado por vários clientes)

SERVICOS e FORMA_PAGAMENTO são catálogos independentes,
sem chave estrangeira formal.

Todas as entidades de negócio guardam também um tenant_id
(coluna simples, sem FK) usado para o isolamento multi-tenant.
```

</details>

Campos completos de cada entidade estão nas classes em `models/entities/` e nos schemas do Swagger UI.

| Entidade | Representa |
|---|---|
| `Tenants` | Uma barbearia cadastrada no sistema; todas as demais entidades pertencem a um tenant (`tenant_id`). |
| `Unidades` | Uma filial de uma barbearia; agrupa usuários, fila e atendimentos. |
| `Usuarios` | Barbeiro ou administrador, com papel (`grupo`) `ADMIN`/`USER`. |
| `Clientes` | Cliente da barbearia, com contador de retornos e uso do plano no mês. |
| `TelefoneCliente` | Telefone único usado para localizar clientes entre atendimentos. |
| `Atendimento` | Serviço registrado por um barbeiro (valor, forma de pagamento, data). |
| `Fila` | Entrada de um cliente na fila de espera de uma unidade. |
| `Planos` | Plano de assinatura mensal com limite de atendimentos. |
| `Servicos` | Catálogo de serviços oferecidos (nome, valor). |
| `FormaPagamento` | Catálogo de formas de pagamento aceitas. |

`Servicos` e `FormaPagamento` são catálogos consultados pelas demais entidades, sem chave estrangeira formal — a maioria das entidades usa exclusão lógica (`status = 1` ativo / `-1` excluído) em vez de remoção física.

## Endpoint Health Check
- Execute `http://localhost:9090/api/v2/health` para checar a saúde da aplicação

## Stack tecnológica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.3 | Framework da API (`web`, `data-jpa`, `security`, `validation`) |
| Spring Data JPA | — | Persistência e acesso ao banco de dados |
| Spring Security | — | Autenticação e autorização |
| java-jwt (Auth0) | 4.5.0 | Geração e validação de tokens JWT |
| MySQL Connector/J | — | Driver de acesso ao MySQL |
| MapStruct | 1.6.3 | Mapeamento entre entidades e DTOs |
| ModelMapper | 3.1.0 | Mapeamento auxiliar entre entidades e DTOs |
| springdoc-openapi | 2.4.0 | Documentação interativa da API (Swagger UI) |
| Flyway | — | Versionamento e aplicação automática do schema do banco (migrations) |
| Log4j2 | — | Logging assíncrono (via LMAX Disruptor), configurado em `log4j2-spring.xml` |
| Lombok | — | Redução de boilerplate (getters/setters/construtores) |
| H2 | — | Banco em memória usado nos testes |
| Maven | — | Build e gerenciamento de dependências |

## Estrutura de pastas

```
backend/src/main/java/cloud/zenixapp/zenix/
├── configs/
│   ├── exceptions/   # Exceções de domínio customizadas
│   ├── flyway/       # FlywayConfig (aplica as migrations no boot)
│   ├── handlers/     # GlobalExceptionHandler, BindingHandler
│   ├── mappers/      # Mappers MapStruct entre Entity e DTO
│   ├── security/     # SecurityConfig, SecurityFilter
│   ├── utils/        # HelpersLogs, ServicoJsonUtils
│   └── TenantContext.java  # ThreadLocal com o tenant da requisição atual
├── controllers/      # 10 REST controllers, um por recurso
├── models/
│   ├── entities/         # Entidades JPA
│   ├── dtos/requests/     # DTOs de entrada
│   ├── dtos/responses/    # DTOs de saída (subpastas por recurso)
│   └── enums/             # StatusFilaEnum, UsuariosRoleEnum
├── repositories/      # Interfaces Spring Data JPA
└── services/
    └── security/       # TokenService, AuthorizationService

backend/src/main/resources/
└── db/migration/      # Scripts SQL versionados (Flyway)
```

## Autenticação e autorização

- Login (`POST /api/v2/users/login`) gera um JWT assinado com HMAC256 (segredo `SECURITY_KEY`), válido por 2 horas, e o entrega em um **cookie httpOnly** chamado `auth_token` (não é retornado no corpo da resposta). `POST /api/v2/users/logout` expira esse cookie.
- Além do `subject` (e-mail do usuário), o JWT carrega o claim `tenantId`, usado para resolver o multi-tenancy (veja abaixo).
- A cada requisição, o `SecurityFilter` lê esse cookie, valida o token e popula o `SecurityContextHolder` com o usuário autenticado.
- Autorização é feita por papel: `ADMIN` recebe `ROLE_ADMIN` + `ROLE_USER`; `USER` (barbeiro) recebe apenas `ROLE_USER`. Rotas administrativas exigem `hasRole("ADMIN")`.
- Sessão é **stateless** (sem sessão no servidor) — o próprio JWT no cookie é a fonte da verdade a cada requisição.
- CORS liberado para os domínios do frontend: `http://localhost:5173`, `https://app.zenixapp.cloud`, `https://barber.zenixapp.cloud`.

## Multi-tenancy

O sistema é multi-tenant: cada barbearia cadastrada (`Tenants`) tem seus dados isolados dos demais tenants.

- `POST /api/v2/cadastro` cria, em uma única transação, o `Tenant`, a primeira `Unidade` e o `Usuario` `ADMIN` responsável por ela.
- O `SecurityFilter` extrai o claim `tenantId` do JWT validado e o armazena em `TenantContext` (uma `ThreadLocal`), limpando-o ao final da requisição.
- Services e Repositories consultam `TenantContext.getTenantId()` para ler/gravar sempre no escopo do tenant autenticado — as entidades de negócio guardam essa referência na coluna `tenant_id`.

## Endpoints da API

| Recurso      | Base path              | Descrição                                                                    | Acesso                                                |
|--------------|------------------------|------------------------------------------------------------------------------|-------------------------------------------------------|
| Health Check | `/api/v2/health`       | Checagem da saúde da aplicação                                               | Público                                               |
| Cadastro     | `/api/v2/cadastro`     | Cadastro inicial de uma barbearia: cria tenant, unidade e usuário ADMIN      | Público                                               |
| Atendimentos | `/api/v2/atendimentos` | CRUD de atendimentos registrados por barbeiro (hoje, histórico, visão admin) | Autenticado + ADMIN (editar/excluir/visão admin)      |
| Clientes     | `/api/v2/clientes`     | Cadastro, busca por telefone/nome e vínculo com planos                      | Autenticado + ADMIN (listar, editar, excluir, planos) |
| Fila         | `/api/v2/fila`         | Entrada na fila, chamada, finalização e remoção                              | Público (entrar) + Autenticado (operar)               |
| Pagamentos   | `/api/v2/pagamentos`   | Catálogo de formas de pagamento                                              | Público (leitura) + Autenticado (escrita)             |
| Planos       | `/api/v2/planos`       | Catálogo de planos de assinatura mensal                                      | ADMIN                                                 |
| Serviços     | `/api/v2/servicos`     | Catálogo de serviços oferecidos                                              | Público (leitura) + Autenticado (escrita)             |
| Unidades     | `/api/v2/unidades`     | Gestão de unidades/filiais                                                   | Autenticado                                           |
| Usuários     | `/api/v2/users`        | Login/logout, sessão (`/me`), registro e gestão de usuários/barbeiros        | Público (login, listar barbeiros por unidade) + Autenticado (demais) + ADMIN (registro/gestão) |

Com a aplicação rodando, o detalhe completo de cada rota (parâmetros, schemas de request/response) está disponível no Swagger UI: `http://localhost:9090/swagger-ui/index.html`.

## Como executar localmente

### Pré-requisitos

- **Docker instalado e configurado**

### Variáveis de ambiente

- Veja o arquivo `example.env`

| Variável | Descrição |
|---|---|
| `DB_URL` | URL de conexão JDBC com o MySQL |
| `DB_USER` | Usuário do banco de dados |
| `DB_PASSWORD` | Senha do banco de dados |
| `SECURITY_KEY` | Segredo usado para assinar os tokens JWT |

### Docker

O `Dockerfile` é executado em estágios:
- **Stage Build:** Copia os arquivos de `backend`, baixa as dependências e empacota o jar (`mvn clean package`) usando uma imagem
`maven:4.0.0-rc-4-eclipse-temurin-21-alpine` como base. O comando para gerar o jar possui resiliência contra falhas
temporárias.
- **Stage Run:** Estágio de execução da aplicação. Ele roda em cima da imagem do `alpine:latest` que é uma imagem leve
do Linux. Copia o arquivo `.env` e o `.jar` gerado no estágio de build, executando a aplicação com `java -jar zenixapp.jar`.
A leitura do `.env` já é feita pelo próprio `application.properties` (`spring.config.import=file:.env[.properties]`), sem
precisar de parâmetro extra no `CMD`.

O `Docker Compose` está com os dois serviços necessários para rodar a API.


### Subindo a API
- Para executar em background, execute o comando abaixo:
```bash
cd backend
docker compose up -d
```

- Para executar analisando os logs:
```bash
cd backend
docker compose up
```

A API sobe por padrão em `http://localhost:8080/api/v2`.

[//]: # (## Testes automatizados)

[//]: # ()
[//]: # (```bash)

[//]: # (cd backend)

[//]: # (./mvnw test)

[//]: # (```)

[//]: # ()
[//]: # (Os testes de repositório rodam contra um banco H2 em memória &#40;perfil `test`, sem depender de um MySQL real&#41;. Suíte existente:)

[//]: # ()
[//]: # (- `ZenixApplicationTests` &#40;smoke test do contexto Spring&#41;)

[//]: # (- `AtendimentoRepositoryTest` &#40;`@DataJpaTest` sobre `AtendimentoRepository`, com H2&#41;)

[//]: # ()
[//]: # (Ainda **não há testes** para a camada de Services &#40;`ClienteService`, `FilaService`, `PlanosService`, `ServicoService`, `UnidadeService`, `UsuarioService`, `PagamentoService`, `CadastroService`&#41;.)
