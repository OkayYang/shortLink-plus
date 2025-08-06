FROM openjdk:17-slim

WORKDIR /app
COPY jar/auth.jar /app/app.jar

EXPOSE 9200

ENTRYPOINT ["java", "-jar", "/app/app.jar"] 