FROM openjdk:11
RUN mkdir -p deploy
WORKDIR /deploy
COPY ../../api/build/libs/api-0.0.1-SNAPSHOT.jar api.jar
ENTRYPOINT ["java", "-jar", "/deploy/api.jar"]
EXPOSE 8080

