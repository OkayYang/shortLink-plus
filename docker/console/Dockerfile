FROM openjdk:17-slim

WORKDIR /app
COPY jar/console.jar /app/app.jar

EXPOSE 9001

ENTRYPOINT ["java", "-jar", "/app/app.jar"] 