FROM eclipse-temurin:24-jdk-alpine as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/LogParser-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]