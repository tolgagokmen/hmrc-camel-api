FROM java:8-jdk-alpine

COPY ./target/hmrc-camel-api-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch hmrc-camel-api-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","hmrc-camel-api-0.0.1-SNAPSHOT.jar"]