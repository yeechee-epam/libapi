FROM openjdk:17-jdk-alpine
WORKDIR /libapi
COPY target/*.jar app.jar
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "app.jar"]