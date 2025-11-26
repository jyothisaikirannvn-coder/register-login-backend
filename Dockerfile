# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

# Package the application
RUN mvn -B -DskipTests package

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render uses PORT env, but this helps local dev)
EXPOSE 8080

# Start the application using Render's PORT
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
