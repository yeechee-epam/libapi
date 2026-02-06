#FROM openjdk:17-jdk-alpine
#WORKDIR /app
#COPY target/*.jar app.jar
#EXPOSE 6060
#ENTRYPOINT ["java", "-jar", "app.jar"]
// Source - https://stackoverflow.com/a/76197524
// Posted by Introspective
// Retrieved 2026-02-06, License - CC BY-SA 4.0

FROM amazoncorretto:17.0.7-alpine

# Add app user
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

# Configure working directory
RUN mkdir /app && \
    chown -R $APPLICATION_USER /app

USER 1000

COPY --chown=1000:1000 ./myapp-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app

EXPOSE 6060
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
