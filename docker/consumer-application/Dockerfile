FROM openjdk:11
RUN mkdir -p deploy
WORKDIR /deploy
COPY ../../consumer/build/libs/consumer-0.0.1-SNAPSHOT.jar consumer.jar
ENTRYPOINT ["java", "-jar", "/deploy/consumer.jar"]

