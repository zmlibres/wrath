FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8080
ADD target/wrath.jar wrath.jar
ENTRYPOINT ["java", "-jar","/wrath.jar"]