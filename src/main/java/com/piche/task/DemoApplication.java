package com.piche.task;

import com.piche.task.encoder.PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;

import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class DemoApplication {

	@Bean
	public PasswordEncoder passwordEncoder() throws NoSuchAlgorithmException {
		return new PasswordEncoder();
	}

	@Bean
	public IdGenerator idGenerator() {
		return new SimpleIdGenerator();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
