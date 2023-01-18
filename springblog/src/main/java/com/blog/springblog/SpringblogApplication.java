package com.blog.springblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class SpringblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringblogApplication.class, args);
	}

}
