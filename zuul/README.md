# Zuul Edge Server

Edge Server built using Spring Boot 2 + Netflix OSS' Zuul. 

For more information about Zuul check [it's official github page and wiki][zuul-info]

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
$ docker build -t zuul .
```

Run your container:
```sh
$ docker run -d -p 9092:9092 --name=zuul -v /opt/zuul/logs:/tmp/logs -v /opt/zuul/config:/tmp/config zuul
```

Logs generated from SLF4J will be generated in /opt/zuul/logs folder. Place your custom application properties in /opt/zuul/config/application.yml optmized for `spring.profiles: docker`. You can use this [file][sample1] as an example.

# Testing your Server

Access [http://localhost:9092/ms-got/house][zuul] instead using port 9093 to make sure Zuul's routing incoming requests properly to your back-end services.

# Additional Info

For more information abou Zuul's routing properties, check [this great guide][routing]

   [zuul]: <http://localhost:9092/ms-got/house/>
   [sample1]: <https://github.com/enr1c091/microservices-oauth/blob/master/docker-compose/zuul/config/application.yml>
   [zuul-info]: <https://github.com/Netflix/zuul>
   [routing]: <https://cloud.spring.io/spring-cloud-netflix/multi/multi__router_and_filter_zuul.html>
