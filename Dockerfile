#FROM amazoncorretto:17.0.7-alpine
#
## Add app user
#ARG APPLICATION_USER=appuser
#RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER
#
## Configure working directory
#RUN mkdir /app && \
#    chown -R $APPLICATION_USER /app
#
#USER 1000
#COPY --chown=1000:1000 target/*.jar /app/app.jar
#
#WORKDIR /app
#
#EXPOSE 6060
#ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]

# ---------- build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- runtime stage ----------
FROM amazoncorretto:17.0.7-alpine

ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

WORKDIR /app
COPY --from=build /build/target/*.jar /app/app.jar
RUN chown -R $APPLICATION_USER /app

USER 1000
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
