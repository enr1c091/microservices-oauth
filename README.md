# Building Scalable Container-Ready and Secure Microservices

The full article of this repository can be found [here][article]!!

This repository contains an example of how to design and implement production-ready microservices using Spring Boot and Spring Cloud (Netflix OSS). It also applies some of the best practices for design and implementation of REST APIs like HATEOAS, DTOs, AOP and Swagger annotations for later documentation.

For enhanced security, OAuth2 authentication is added via JWT tokens stored in our MySQL (or any database of your preference) to avoid unwanted access and making sure revoked tokens are indeed... revoked.

Every submodule of this repository has a more detailed README which should help you understanding the concepts applied and how to access each service.

# Installing 

Before you run your application, make sure to configure your JDBC Connection. This sample features [DDL and DML Scripts for MySQL][scripts] that will be used across all projects of this article. Have your MySQL installation or container up & running or else you'll end up getting a few errors. 


# Start Application

1. Start your MySQL Database and make sure all tables are created;
2. Clone this repo. Go it's parent folder of and run the following command:
```sh
$ mvn clean install -DskipTests
```
3. Run the following command for every project in the following order (Eureka > MS-GOT > Auth-Server > Zuul):
```sh
$ mvn spring-boot:run
```
4. Enjoy

# Application Endpoints

| Service | Port | Endpoint |
| ------ | ------ | ------ |
| Eureka | 9091 | http://localhost:9091/
| Auth Server | 9999 | http://localhost:9999/oauth/<resource>
| API | 9093 | http://localhost:9091/ms-got/<resource>
| Zuul | 9092 | http://localhost:9092/<resource>

Since Zuul works as the Edge server, requests for Auth Server (9999) and API Server (9091) should all be made through port 9092. In a production environment Zuul would be in a DMZ exposed to the internet with SSL whilst our microservices would be inaccessible to the outside world. 

# Swagger definition
Springfox 2.9.0 Swagger definition of our endpoints are available at http://localhost:9092/ms-got/swagger-ui.html/ and http://localhost:9092/uaa/swagger-ui.html/


> **Note:**
> This example is using Spring Boot version 2.0.1.RELEASE and Spring Cloud version Finchley.RC1.

> **TO-DO:**
> - Add docker-compose for creating a Stack on Rancher.
> - Add a Feign client for demonstrating how to invoke REST Services besides REST Template.

[scripts]: <https://github.com/enr1c091/microservices-oauth/blob/master/docker-compose/mysql/scripts/oauth2.sql>
[article]: <https://dzone.com/articles/building-scalable-container-ready-and-secure-micro>
