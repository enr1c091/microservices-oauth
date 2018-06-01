# Eureka Discovery Server

Discovery Server built using Spring Boot 2 + Netflix OSS' Eureka. 

For more information about Eureka check [it's official github page and wiki][eureka-info]

# Start Application

```sh
$ mvn spring-boot:run
```

# Play in Docker

This project is container-ready through our Dockerfile placed in the root of this repository.

Build the application and generate it's jar:
```sh
$ mvn clean install
```
OR
```sh
$ mvn package -B -DskipTests
```

Build you Docker container:
```sh
$ docker build -t eureka .
```

Run your container:
```sh
$ docker run -d -p 9091:9091 --name=eureka -v /opt/eureka/logs:/tmp/logs -v /opt/eureka/config:/tmp/config eureka
```

Logs generated from SLF4J will be generated in /opt/eureka/logs folder. Place your custom application properties in /opt/eureka/config/application.yml optmized for `spring.profiles: docker`. You can use this [file][sample1] as an example.

# Testing your Server

Access [http://localhost:9091][eureka] to check which services are connected to your discovery server.


   [eureka]: <http://localhost:9091/>
   [sample1]: <https://github.com/enr1c091/microservices-oauth/blob/master/docker-compose/eureka/config/application.yml>
   [eureka-info]: <https://github.com/Netflix/eureka>
