# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle settings.gradle gradle.properties ./
COPY core/ core/
RUN chmod +x gradlew && ./gradlew :core:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd -r agentos && useradd -r -g agentos agentos
COPY --from=builder /workspace/core/build/libs/*.jar app.jar
USER agentos
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
