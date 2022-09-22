FROM openjdk:17-slim
MAINTAINER Marc Dergacz, SkyTIX <marc@skytix.au>

WORKDIR /

COPY build/libs/marathon-consul.jar /

ENTRYPOINT ["java", "-jar", "marathon-consul.jar"]
