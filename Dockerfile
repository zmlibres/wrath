FROM openjdk:17
EXPOSE 8080
ADD target/wrath.jar wrath.jar
ENTRYPOINT ["java", "-jar","/wrath.jar"]