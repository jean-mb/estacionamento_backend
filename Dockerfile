FROM maven:3.8.7-eclipse-temurin-19 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
ENV DB_PWD=postgres
ENV DB_USER=postgres
ENV DB_NAME=estacionamento
ENV DB_PORT=5432
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM openjdk:19-alpine
COPY --from=build /usr/src/app/target/*.jar /usr/app/estacionamento-1.0.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/estacionamento-1.0.0-SNAPSHOT.jar"]

