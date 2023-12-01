FROM maven:3.8.5-openjdk-17
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT java ${SYS_PROPS} -jar app.jar