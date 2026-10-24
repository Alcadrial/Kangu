package com.alcadrial.kangu;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
public class CommonConfig {

	@Bean
	JdbcUserDetailsManager userDetailsService(@Autowired DataSource source)
	{
		JdbcUserDetailsManager detailsManager = new JdbcUserDetailsManager(source);
		detailsManager.setEnableAuthorities(false);
		detailsManager.setEnableGroups(true);
		detailsManager.setEnableUpdatePassword(false);
		return detailsManager;
	}
}
