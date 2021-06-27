FROM openjdk:8u181-alpine3.8

WORKDIR /

COPY target/crawler-0.1.0-SNAPSHOT-standalone.jar crawler.jar

EXPOSE 3000

CMD env port=$PORT java -jar crawler.jar
