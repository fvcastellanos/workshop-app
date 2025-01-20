FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM azul/zulu-openjdk:21-jre-latest AS runner

RUN useradd -m workshop -s /bin/bash && \
    mkdir -p /opt/cavitos/apps && \
    chown -R workshop:workshop /opt/cavitos && \
    chmod -R 755 /opt/cavitos

COPY --from=builder /app/target/*.jar /opt/cavitos/apps/workshop-app.jar

RUN chown workshop:workshop /opt/cavitos/apps/workshop-app.jar && \
    chmod 755 /opt/cavitos/apps/workshop-app.jar

WORKDIR /opt/cavitos/apps

USER workshop

EXPOSE 8080

CMD ["java", "-Xms64M", "-Xmx128M", "-jar", "workshop-api.jar"]