Write-Host "Limpando e construindo o projeto..."
mvn clean package -DskipTests
Write-Host "Build completo!"

Write-Host "Iniciando containirização"
docker compose up --build -d

Write-Host "Container criado! Aplicação rodando: http://localhost:8080/swagger-ui/index.html "