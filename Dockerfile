FROM openjdk:8-jdk-alpine
ARG JARFILE=./target/*.jar
COPY ${JARFILE} /app.jar
ENTRYPOINT ["sh","-c","java -jar /app.jar"]
