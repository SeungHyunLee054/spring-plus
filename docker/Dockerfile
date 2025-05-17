FROM gradle:8.12-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /deploy

COPY --from=build /app/build/libs/expert-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "expert-0.0.1-SNAPSHOT.jar"]