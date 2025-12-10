# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only service pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Argument for port (can be overridden during build)
ARG PORT=8080
ENV SERVER_PORT=${PORT}

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE ${PORT}

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl --fail http://localhost:${PORT}/actuator/health || exit 1

# Set JVM options for container environment
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]