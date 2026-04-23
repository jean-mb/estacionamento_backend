FROM maven:3.8.7-eclipse-temurin-19 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
ENV DB_PWD=postgres
ENV DB_USER=postgres
ENV DB_NAME=estacionamento
ENV DB_PORT=5432
ENV DB_HOST=postgres
ENV DDL=create-drop

RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM eclipse-temurin:19-jdk
COPY --from=build /usr/src/app/target/*.jar /usr/app/estacionamento-1.0.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/estacionamento-1.0.0-SNAPSHOT.jar"]

