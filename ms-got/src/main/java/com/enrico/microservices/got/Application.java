package com.enrico.microservices.got;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableTransactionManagement
@EntityScan(basePackages = "com.enrico.microservices.got.entity")
@EnableJpaRepositories(basePackages = "com.enrico.microservices.got.repository")
@EnableEntityLinks
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}