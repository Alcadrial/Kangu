package com.alcadrial.kangu.api;

import java.net.URI;

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

import com.alcadrial.kangu.exception.DisabledException;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.service.AuthService;
import com.alcadrial.kangu.utils.Validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/register")
@RestController
public class RegisterController {
	
	private AuthService authService;
	private AuthenticationManager authenticationManager;
	private SecurityContextRepository securityContextRepository;
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response, @Autowired HttpServletRequest request)
	{
		if (body.username.isBlank() || !Validation.email(body.email) || body.password.length() < 8) return ResponseEntity.badRequest().build();
		
		try
		{
			authService.register(body.username, body.email, body.password);

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
			
			return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create("/api/user")).build();
		}
		catch (DuplicateKeyException _)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		catch (DisabledException _)
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new DisabledResponse("Registration is disabled"));
		}
	}
	
	record Request(String username, String password, String email) {}
}
