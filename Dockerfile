FROM java:8
MAINTAINER Marc Dergacz, SkyTIX <marc@skytix.com.au>

WORKDIR /

COPY build/libs/marathon-consul.jar /
COPY application.yaml /

ENTRYPOINT ["java", "-jar", "marathon-consul.jar"]
