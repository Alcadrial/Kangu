package com.alcadrial.kangu.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/login")
@RestController
public class LoginController {
	
	private AuthenticationManager authenticationManager;
	private SecurityContextRepository securityContextRepository;
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response, @Autowired HttpServletRequest request)
	{
		Authentication authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(body.username, body.password);
		try
		{
			authenticationToken = authenticationManager.authenticate(authenticationToken);
		}
		catch (AuthenticationException _)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authenticationToken);
		securityContextRepository.saveContext(securityContext, request, response);
		
		return ResponseEntity.ok().build();
	}
	
	record Request(String username, String password, Optional<Boolean> rememberMe) {}
}
