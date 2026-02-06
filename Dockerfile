#FROM openjdk:17-jdk-alpine
FROM eclipse-temurin:25
WORKDIR /libapi
COPY target/*.jar app.jar
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "app.jar"]