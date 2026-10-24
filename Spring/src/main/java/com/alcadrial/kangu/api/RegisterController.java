package com.alcadrial.kangu.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.exception.DisabledException;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.service.AuthService;
import com.alcadrial.kangu.utils.Validation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/register")
@RestController
public class RegisterController {
	
	private AuthService authService;
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response)
	{
		if (body.username.isBlank() || !Validation.email(body.email) || body.password.length() < 8) return ResponseEntity.badRequest().build();
		
		try
		{
			authService.register(body.username, body.email, body.password);
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
