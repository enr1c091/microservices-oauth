FROM openjdk:8-jdk-alpine

VOLUME ["/tmp", "/tmp/config", "/tmp/logs"]

ENV TIME_ZONE America/Sao_Paulo

RUN apk --no-cache add \
	tzdata \
    # Set timezone
    && echo "${TIME_ZONE}" > /etc/timezone \ 
	&& ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime 

ADD /target/*.jar app.jar

EXPOSE 9999

ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Djava.awt.headless=true","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar", "--spring.config.location=classpath:/application.yml,file:/tmp/config/application.yml"]