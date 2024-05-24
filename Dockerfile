FROM openjdk:21-jdk-slim

# JVM Parameters var
ENV JAVA_OPTS=""

# Copy into container
COPY ./target/taskbot-1.0-SNAPSHOT-jar-with-dependencies.jar /app/taskbot-1.0-SNAPSHOT-jar-with-dependencies.jar

# Running applications
ENTRYPOINT ["java", "-cp", "/app/taskbot-1.0-SNAPSHOT-jar-with-dependencies.jar", "ru.spbstu.ai.Main"]
