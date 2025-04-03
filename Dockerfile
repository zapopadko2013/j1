FROM openjdk:17-jdk-alpine
LABEL maintainer="author@javatodev.com"
VOLUME /main-app-spring-boot-sec
ADD target/SpringSecurity-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]