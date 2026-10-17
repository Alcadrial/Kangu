package com.alcadrial.kafkatest.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kafkatest.service.AuthService;
import com.alcadrial.kafkatest.service.Jsons;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.JsonNode;

@RequestMapping("/api/login")
@RestController
public class LoginController {
	
	private final AuthService authService;
	
	LoginController(AuthService authService)
	{
		this.authService = authService;
	}
	
	@PostMapping
	ResponseEntity<JsonNode> post(@RequestBody JsonNode body, @Autowired HttpServletResponse response, Jsons jsons)
	{
		String username = body.get("username").asString(null);
		String password = body.get("password").asString(null);
		
		if (username == null || password == null || password.length() < 8) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsons.invalidPayloadError());
		
		try
		{
			authService.login(username, password);
			return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create("/api/user")).build();
		}
		catch (EntityNotFoundException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsons.userNotFoundError());
		}
	}
}
