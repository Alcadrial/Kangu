package com.alcadrial.kangu.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.data.UserEntity;
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
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response, @Autowired HttpServletRequest request)
	{
		if (body.username.isBlank() || !Validation.email(body.email) || body.password.length() < 8) return ResponseEntity.badRequest().build();
		
		try
		{
			authService.register(body.username, body.email, body.password);

			Authentication authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(body.username, body.password);
			UserEntity user = authService.login(authenticationToken, body.rememberMe.orElse(false), request, response);
			
			return ResponseEntity.ok(new Response(user.getUsername(), user.getEmail(), authService.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new)));
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
	
	record Request(String username, String password, String email, Optional<Boolean> rememberMe) {}
	record Response(String username, String email, String[] authorities) {}
}
