# Authorization Server

OAuth2 Authorization Server built using Spring Boot 2 + Spring Security OAuth2. This auth server is responsible for intercepting login requests from external applications and generating [JWT][jwt] tokens containing user credentials.  

This Auth Server behaves like a Microservice built upon Netflix OSS stack, which is plugged into Eureka Server and have it's routes configured through Zuul Gateway in a Dockerized manner or not.

All Access Tokens and Refresh Tokens are stored in a `JdbcTokenStore`, which means the JWT is persisted in our datastore allowing our server to go offline without any hazard to our issued tokens.

It also features a custom logout handler for revoking(deleting from database) access tokens.

Remember, this project tries to register to Eureka Server as soon as it starts, so in case you don't want Eureka as part of your game just remove it's dependency from `pom.xml` and the respective annotations from `OAuthApplication.java` and `application.yml`.

# Installing 

Before you run your application, make sure to configure your JDBC Connection. This sample features [DDL and DML Scripts for MySQL][scripts] that will be used across all projects of this article. Have your MySQL installation or container up & running or else you'll end up getting a few errors. 

You can notice that we have our unique client allowed to issue OAuth2 tokens has it's clientId:Secret as `adminapp`:`password`. 

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
$ docker build -t auth-server .
```

Run your container:
```sh
$ docker run -d -p 9999:9999 --name=auth -v /opt/auth/logs:/tmp/logs -v /opt/auth/config:/tmp/config auth-server
```

Logs generated from SLF4J will be generated in /opt/auth/logs folder. Place your custom application properties in /opt/auth/config/application.yml optmized for `spring.profiles: docker`. You can use this [file][sample1] as an example.

# Swagger definition
Springfox 2.9.0 Swagger definition of our endpoints are available at http://localhost:9999/uaa/swagger-ui.html/

# Testing your Server

SignIn: `POST /oauth/token`

Hint: Base64 Encode our clientId:secret to use our Basic Authentication header.

```sh
$ curl -X POST \
  http://localhost:9999/uaa/oauth/token \
  -H 'authorization: Basic YWRtaW5hcHA6cGFzc3dvcmQ=' \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&username=admin&password=password'
```

Response:
```javascript
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXMvYWRtaW4iLCJtcy91c2VyIiwibXcvYWRtaW5hcHAiXSwidXNlcl9uYW1lIjoiYWRtaW4iLCJzY29wZSI6WyJyb2xlX2FkbWluIl0sImV4cCI6MTUyNzY0NDQwMSwiYXV0aG9yaXRpZXMiOlsicm9sZV9hZG1pbiIsImNhbl91cGRhdGVfdXNlciIsImNhbl9yZWFkX3VzZXIiLCJjYW5fY3JlYXRlX3VzZXIiLCJjYW5fZGVsZXRlX3VzZXIiXSwianRpIjoiNTBmYTE0N2EtNDIxYi00NmE5LWI2MDEtMThhOGJlMWQ5OTAzIiwiZW1haWwiOiJhZG1pbkBleGFtcGxlLmNvbSIsImNsaWVudF9pZCI6ImFkbWluYXBwIn0.6P8lhBiFYnsIuNoUi75hUPClGIggbMiuMmlFlzUiqCk",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXMvYWRtaW4iLCJtcy91c2VyIiwibXcvYWRtaW5hcHAiXSwidXNlcl9uYW1lIjoiYWRtaW4iLCJzY29wZSI6WyJyb2xlX2FkbWluIl0sImF0aSI6IjUwZmExNDdhLTQyMWItNDZhOS1iNjAxLTE4YThiZTFkOTkwMyIsImV4cCI6MTUyNzY0NzEwMSwiYXV0aG9yaXRpZXMiOlsicm9sZV9hZG1pbiIsImNhbl91cGRhdGVfdXNlciIsImNhbl9yZWFkX3VzZXIiLCJjYW5fY3JlYXRlX3VzZXIiLCJjYW5fZGVsZXRlX3VzZXIiXSwianRpIjoiYzNjY2VlOTAtMWY0YS00MjA2LTk5YTEtZTA0ZmUxZDFkZGViIiwiZW1haWwiOiJhZG1pbkBleGFtcGxlLmNvbSIsImNsaWVudF9pZCI6ImFkbWluYXBwIn0.pwR4zbxO0MYsDKsqzVrPAhaIBJEwxMjTfggOU2k4ri4",
    "expires_in": 899,
    "scope": "role_admin",
    "email": "admin@example.com",
    "jti": "50fa147a-421b-46a9-b601-18a8be1d9903"
}
```

Logout: `POST /oauth/logout`

```sh
$ curl -X POST http://localhost:9999/uaa/oauth/logout -H 'authorization: Bearer <access_token>' 
```

Response: Plain 200 (OK) Status Code.

   [jwt]: <https://jwt.io/introduction/>
   [sample1]: <https://github.com/enr1c091/microservices-oauth/blob/master/docker-compose/auth-server/config/application.yml>
 [scripts]: <https://github.com/enr1c091/microservices-oauth/blob/master/auth-server/src/main/resources/oauth2.sql>  
