FROM registry.opensource.zalan.do/stups/openjdk:latest

MAINTAINER Zalando SE

EXPOSE 8080

ADD target/application.jar /
ADD target/scm-source.json /scm-source.json

CMD java $(java-dynamic-memory-opts 70) -XX:-OmitStackTraceInFastThrow -jar /application.jar
