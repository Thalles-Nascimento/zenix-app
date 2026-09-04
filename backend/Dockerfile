# 1º Stage - Build
FROM maven:4.0.0-rc-4-eclipse-temurin-21-alpine AS stagebuild

WORKDIR /builder

COPY . /builder

# Instala as depências do projeto && Construção do arquivo .jar do Java, com retries => 5 retries, após ocorrer 1min de
# timeout.
RUN mvn dependency:go-offline && mvn clean package -DskipTests \
                                    -Dmaven.wagon.http.retryHandler.count=5 \
                                    -Dmaven.wagon.http.retryHandler.class=default \
                                    -Dmaven.wagon.http.connectionTimeout=60000 \
                                    -Dmaven.wagon.http.readTimeout=60000



# 2º Stage - Run
FROM alpine:latest AS stagerun
RUN apk add --no-cache openjdk21-jre

WORKDIR /zenixapp
# Copia o arquivo onde você configura as suas variáveis de ambiente do banco de dados
COPY .env /zenixapp

COPY --from=stageBuild /builder/target/*.jar /zenixapp/zenixapp.jar

EXPOSE 8080

ENTRYPOINT ["java"]

# O parâmetro '--spring.config.import=file:.env[.properties]' indica um arquivo .env para ser
# usado para inicializar as variáveis de ambiente da aplicação.
CMD ["-jar", "zenixapp.jar", "--spring.config.import=file:.env[.properties]"]
