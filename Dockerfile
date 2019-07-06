FROM registry.opensource.zalan.do/stups/openjdk:1.8.0-162-16

MAINTAINER willi.schoenborn@zalando.de

EXPOSE 8080

COPY target/application.jar /

CMD java $(java-dynamic-memory-opts 70) -XX:-OmitStackTraceInFastThrow -jar /application.jar
