FROM openjdk:8-jdk-alpine
ADD target/springboot-demo.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
