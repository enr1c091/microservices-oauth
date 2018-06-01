# Game of Thrones API

This API is built using Spring Boot 2 / Spring Data JPA / Spring Security OAuth2 / Spring AOP and implementes the [HATEOAS][hateoas] pattern for the designed resources. By implementing this pattern combined to a DTO intermediary layer between our Entities and the HTTP Requests, we make sure to only handle what's really needed for each request and response. All our entities have their relationships lazily fetched therefore making our database requests lighter and avoiding performance issues and comments like "Why is Hibernate and ORMs so slow?". We are not loading our entire database per requests anymore. 

This REST Server behaves like a Microservice built upon Netflix OSS stack, which is plugged into Eureka Server and have it's routes configured through Zuul Gateway in a Dockerized manner or not.

Remember, this project tries to register to Eureka Server as soon as it starts, so in case you don't want Eureka as part of your game just remove it's dependency from `pom.xml` and the respective annotations from `OAuthApplication.java` and `application.yml`.

# Bonus round - AOP

As a bonus, this project features Spring AOP. For this project, there are two interfaces called `LogExecution` and `LogRequest` that become annotations that can be used on top of any method you want to measure and log data like execution time, Remote host IP, Request ID for microservices correlation, and other headers.

As an example, a call to `/people/` endpoint:

```java
@LogExecution
	@LogRequest
	@GetMapping
	@ApiOperation(value = "View a list of all available person", response = Iterable.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	public List<Person> getAll() {
		return personService.listAll();
	}
```

Will result in the following log:

```
2018-06-01 16:19:54 [http-nio-9093-exec-10] INFO  c.e.m.g.controller.PersonController - [null][172.25.0.1][null][null][null]
2018-06-01 16:19:54 [http-nio-9093-exec-10] INFO  c.e.m.g.controller.PersonController - Method getAll executed within 65 miliseconds.
```

# Installing 

Before you run your application, make sure to configure your JDBC Connection. This sample features [DDL and DML Scripts for MySQL][scripts] that will be used across all projects of this article. Have your MySQL installation or container up & running or else you'll end up getting a few errors. 


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
$ docker build -t ms-got .
```

Run your container:
```sh
$ docker run -d -p 9093:9093 --name=ms-got -v /opt/ms-got/logs:/tmp/logs -v /opt/ms-got/config:/tmp/config ms-got
```

Logs generated from SLF4J will be generated in /opt/ms-got/logs folder. Place your custom application properties in /opt/ms-got/config/application.yml optmized for `spring.profiles: docker`. You can use this [file][sample1] as an example.

# Swagger definition
Springfox 2.9.0 Swagger definition of our endpoints are available at http://localhost:9093/ms-got/swagger-ui.html/

# Testing your Server

Access http://localhost:9093/ms-got/people/ to make sure you can query all people in our database.

For educational purposes, the `/people/{id}` endpoint is secured and requires an authenticated user. For accessing secured endpoints, start the Authorization Server and [follow the instructions][auth] to login and issue your access token.

```sh
$ curl -X GET \
  http://localhost:9093/ms-got/people/12 \
  -H 'Authorization: Bearer <access_token>'
```

Response:
```javascript
{
    "name": "Eddard Stark",
    "_links": {
        "self": {
            "href": "http://localhost:9093/ms-got/people/12"
        },
        "house": {
            "href": "http://localhost:9093/ms-got/houses/1"
        }
    }
}
```
   [hateoas]: <https://spring.io/understanding/HATEOAS>
   [auth]: <https://github.com/enr1c091/microservices-oauth/tree/master/auth-server>
   [sample1]: <https://github.com/enr1c091/microservices-oauth/blob/master/docker-compose/ms-got/config/application.yml>
 [scripts]: <https://github.com/enr1c091/microservices-oauth/blob/master/auth-server/src/main/resources/oauth2.sql>  
