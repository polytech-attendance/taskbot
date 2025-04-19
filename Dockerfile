FROM eclipse-temurin:24_36-jre-alpine-3.21
RUN mkdir /opt/app
# Copy into container
COPY ./target/taskbot-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/app/app.jar
# Running application
CMD ["java",  "-jar", "/opt/app/app.jar"]
