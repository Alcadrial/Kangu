package com.alcadrial.kangu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@ConfigurationProperties("kangu.registration")
@Configuration
public class RegistrationProperties {
	
	/**
	 * When true the registration endpoint will accept requests
	 */
	boolean enabled = true;
}
