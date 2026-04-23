FROM maven:3.8.7-eclipse-temurin-19 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM eclipse-temurin:19-jdk
COPY --from=build /usr/src/app/target/*.jar /usr/app/estacionamento-1.0.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/estacionamento-1.0.0-SNAPSHOT.jar"]

