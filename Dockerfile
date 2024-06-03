## BUILD ##
FROM maven:3.8.3-openjdk-17@sha256:8a66581a077762c8752a9f64f73cdd8c59e9c4446eb810417119e0436b075931 AS build

WORKDIR /app

COPY . .

RUN mvn -q clean package -Dmaven.test.skip=true


## RUN ##
FROM openjdk:17-alpine@sha256:4b6abae565492dbe9e7a894137c966a7485154238902f2f25e9dbd9784383d81

COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]