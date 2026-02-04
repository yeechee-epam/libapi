package com.example.libapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LibapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibapiApplication.class, args);
	}

}
