# Configurando o arquivo .env

## 1 - Config padrão para Docker
DB_PASSWORD=Zenix_User

DB_USER=zenix_user

SECURITY_KEY=<sua secret_key>

DB_URL=jdbc:mysql://mysql:3306/zenix_db?connectionTimeZone=GMT-03:00&forceConnectionTimeZoneToSession=true

## 2 - Config padrão para rodar localmente
DB_PASSWORD=<sua_senha>

DB_USER=<seu_user>

SECURITY_KEY=<sua secret_key>

DB_URL=jdbc:mysql://localhost:3306/zenix_db?connectionTimeZone=GMT-03:00&forceConnectionTimeZoneToSession=true
