package com.lost.fts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtsApplication.class, args);
	}

}
