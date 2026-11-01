# ==============================================================================
# STAGE 1: Build Executable WAR using JDK 11 + Gradle
# ==============================================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 1. Copy Gradle wrapper and build config first for layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle gradle.properties ./

# Pre-download dependencies into cached Docker layer
RUN ./gradlew dependencies --no-daemon || true

# 2. Copy full source code
COPY . .

# 3. Compile and build production executable WAR
RUN ./gradlew clean bootWar

# ==============================================================================
# STAGE 2: Lightweight Production JRE 11 Runtime
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create system resources directory for HDS-Explorer
RUN mkdir -p /var/lib/hds-explorer

# Copy compiled executable WAR from Stage 1 builder
COPY --from=builder /app/build/libs/*.war /app/hds-explorer-server.war

# Expose HTTP web port
EXPOSE 8080

# Execute standalone web application
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/hds-explorer-server.war"]
