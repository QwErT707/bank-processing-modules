FROM openjdk:21-jdk-slim

WORKDIR /app

ARG JAR_FILE

COPY ${JAR_FILE} app.jar

RUN adduser --system --group appuser
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]