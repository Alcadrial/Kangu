package com.alcadrial.kangu.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/login")
@RestController
public class LoginController {
	
	final AuthService authService;
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response, @Autowired HttpServletRequest request)
	{
		Authentication authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(body.username, body.password);
		
		UserEntity user = authService.login(authenticationToken, body.rememberMe.orElse(false), request, response);
		return ResponseEntity.ok(new Response(user.getUsername(), user.getEmail(), authService.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new)));
	}
	
	record Request(String username, String password, Optional<Boolean> rememberMe) {}
	
	record Response(String username, String email, String[] authorities) {}
}
