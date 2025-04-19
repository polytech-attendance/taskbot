FROM eclipse-temurin:24_36-jre-alpine-3.21
RUN mkdir /opt/app
# Copy into container
COPY ./target/**/*jar-with-dependencies.jar /opt/app/app.jar
# Running application
CMD ["java",  "-cp", "/opt/app/japp.jar", "ru.spbstu.ai.Main"]
