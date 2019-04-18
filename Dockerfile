FROM openjdk:11-slim
MAINTAINER Marc Dergacz, SkyTIX <marc@skytix.com.au>

WORKDIR /

COPY build/libs/marathon-consul.jar /

ENTRYPOINT ["exec", "java", "-jar", "marathon-consul.jar"]
