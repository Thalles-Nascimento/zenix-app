#!/bin/bash

set -e
echo "Limpando e construindo o projeto..."

mvn clean package -DskipTests

echo "Build completo!"
echo "Iniciando containirização"
docker compose up --build -d

echo "Container criado! Aplicação rodando: http://localhost:8080/swagger-ui/index.html "