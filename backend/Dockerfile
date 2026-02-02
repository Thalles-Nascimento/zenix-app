# Definindo o Kit de desenvolvimento Java utilizado na aplicação
FROM eclipse-temurin:17-jdk-jammy

# Definindo o diretório de trabalho no Container
WORKDIR /zenixapp

# Copiando o arquivo .jar Spring Boot no Container
COPY target/*.jar /zenixapp/app.jar

# Expondo a porta da aplicação
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/zenixapp/app.jar"]