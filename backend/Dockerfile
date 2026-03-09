FROM eclipse-temurin:21-jre-jammy

WORKDIR /zenixapp

COPY target/*.jar /zenixapp/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/zenixapp/app.jar"]
