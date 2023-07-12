FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
COPY target/live-0.0.1-SNAPSHOT.jar app-1.0.0.jar

ENTRYPOINT [ "java", "-jar", "app-1.0.0.jar" ]