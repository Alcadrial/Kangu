package com.alcadrial.kangu.api;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.service.AuthService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/login")
@RestController
public class LoginController {
	
	private AuthService authService;
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response)
	{
		if (body.password.length() < 8) return ResponseEntity.badRequest().build();
		
		try
		{
			authService.login(body.username, body.password, body.rememberMe.orElse(false));
			return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create("/api/user")).build();
		}
		catch (EntityNotFoundException _)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	record Request(String username, String password, Optional<Boolean> rememberMe) {}
}
