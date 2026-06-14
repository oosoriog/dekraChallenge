# ---- Build stage ----
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY checkstyle.xml ./
COPY src/ src/
RUN ./mvnw -B -ntp clean package -DskipTests

# ---- Quality stage ----
FROM build AS quality
RUN ./mvnw -B -ntp clean verify -Pquality

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -r -u 1001 -m -d /home/spring spring
USER spring

COPY --from=build /app/target/dekra-challenge-*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
