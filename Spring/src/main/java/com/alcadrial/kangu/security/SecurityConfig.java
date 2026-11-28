package com.alcadrial.kangu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
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
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter authenticationConverter)
	{
		XorCsrfTokenRequestAttributeHandler requestHandler = new XorCsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName(null);
		http.cors(c -> c.disable())
			.csrf(c -> c.disable())
			.authorizeHttpRequests(c -> c
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer(c -> c
				.jwt(d -> d
					.jwtAuthenticationConverter(authenticationConverter)
				)
			)
			.sessionManagement(c -> c
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		
		return http.build();
	}
	
	@Bean
	JwtGrantedAuthoritiesConverter authoritiesConverter()
	{
		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
		//authoritiesConverter.setAuthorityPrefix(null);
		return authoritiesConverter;
	}
	
	@Bean
	JwtAuthenticationConverter authenticationConverter(JwtGrantedAuthoritiesConverter authoritiesConverter)
	{
		JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
		authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			return authoritiesConverter.convert(jwt);
		});
		return authenticationConverter;
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
