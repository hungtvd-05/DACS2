package com.dacs2.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.dacs2.model")
//@ComponentScan(basePackages = {"com.dacs2.controller", "com.dacs2.service"})
@ComponentScan(basePackages = {"com.dacs2.*"})
@EnableJpaRepositories(basePackages = "com.dacs2.repository")
public class EcomApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomApplication.class, args);
	}

}
