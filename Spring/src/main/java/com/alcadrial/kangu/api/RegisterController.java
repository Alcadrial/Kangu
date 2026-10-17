package com.alcadrial.kangu.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.service.AuthService;
import com.alcadrial.kangu.service.Jsons;
import com.alcadrial.kangu.utils.Validation;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.JsonNode;

@RequestMapping("/api/register")
@RestController
public class RegisterController {
	
	private final AuthService authService;
	
	RegisterController(AuthService authService)
	{
		this.authService = authService;
	}
	
	@PostMapping
	ResponseEntity<JsonNode> post(@RequestBody JsonNode body, @Autowired HttpServletResponse response, Jsons jsons)
	{
		String username = body.get("username").asString(null);
		String email = body.get("email").asString(null);
		String password = body.get("password").asString(null);
		
		if (username == null || username.isBlank() || password == null || email == null || !Validation.email(email) || password.length() < 8) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsons.invalidPayloadError());
		
		try
		{
			authService.register(username, email, password);
			return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create("/api/user")).build();
		}
		catch (DuplicateKeyException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsons.userNotFoundError());
		}
	}
}
