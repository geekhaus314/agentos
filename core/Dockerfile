# Multi-stage Dockerfile for Spring Boot core service
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY ../.. ./
# Use Gradle wrapper to build only the core module
RUN ./gradlew :core:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=/workspace/core/build/libs/core-1.0.0-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} /app/core.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx1g","-jar","/app/core.jar"]
