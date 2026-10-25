package com.alcadrial.kangu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, @Autowired SecurityContextRepository securityContextRepository)
	{
		XorCsrfTokenRequestAttributeHandler requestHandler = new XorCsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName(null);
		http.cors(c -> c.disable())
			.csrf(c -> c
				.csrfTokenRequestHandler(requestHandler)
			)
			.authorizeHttpRequests(c -> c
				.anyRequest().permitAll()
			)
			.securityContext(c -> c
				.securityContextRepository(securityContextRepository)
				.requireExplicitSave(true)
			)
			.logout(c -> c
				.logoutUrl("/api/logout")
				.logoutSuccessUrl("/")
			);
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	SecurityContextRepository securityContextRepository()
	{
		return new DelegatingSecurityContextRepository(
			new HttpSessionSecurityContextRepository(), new RequestAttributeSecurityContextRepository()
		);
	}
}
