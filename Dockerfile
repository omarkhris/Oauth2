FROM openjdk:17
COPY target/oauth2-0.0.1-SNAPSHOT.jar oauth2-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/oauth2-0.0.1-SNAPSHOT.jar"]