FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY applications/app-service/build/libs/MsTodoList.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]