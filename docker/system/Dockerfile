FROM openjdk:17-slim

WORKDIR /app
COPY jar/system.jar /app/app.jar

EXPOSE 9002

ENTRYPOINT ["java", "-jar", "/app/app.jar"] 