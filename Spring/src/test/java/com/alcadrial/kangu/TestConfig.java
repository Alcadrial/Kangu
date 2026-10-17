package com.alcadrial.kangu;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alcadrial.kangu.service.Jsons;

import tools.jackson.databind.ObjectMapper;

@TestConfiguration
@Import(Jsons.class)
public class TestConfig {
	
	private final Jsons jsons;
	
	TestConfig(Jsons jsons)
	{
		this.jsons = jsons;
	}
	
	@Bean
	ObjectMapper objectMapper()
	{
		return new ObjectMapper();
	}
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	Jsons jsons()
	{
		return jsons;
	}
}
