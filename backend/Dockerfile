# ===== Build Stage =====
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build application
RUN gradle clean build -x test --no-daemon

# ===== Runtime Stage =====
FROM amazoncorretto:17-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", "app.jar"]
