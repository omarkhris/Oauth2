FROM openjdk:17

COPY target/oauth2-0.0.1-SNAPSHOT.jar oauth2-0.0.1-SNAPSHOT.jar

# Download the PostgreSQL JDBC driver
RUN curl -o /postgresql-42.3.1.jar https://jdbc.postgresql.org/download/postgresql-42.3.1.jar

ENV CLASSPATH=.:postgresql-42.3.1.jar

# Set environment variables for database connection
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://172.17.0.2:5432/userdb
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=post123
ENV SPRING_DATASOURCE_DRIVER-CLASS-NAME=org.postgresql.Driver
ENV SPRING_JPA_HIBERNATE_DDL-AUTO=create-drop

ENTRYPOINT ["java", "-jar", "/oauth2-0.0.1-SNAPSHOT.jar"]
