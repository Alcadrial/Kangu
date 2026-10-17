package com.alcadrial.kangu;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kangu")
public class Settings {
	
	/**
	 * When true the registration endpoint will accept requests
	 */
	boolean enableRegistration = true;
}
