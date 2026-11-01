package com.alcadrial.kangu.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/delete")
@RestController
public class DeleteController {
	
	final AuthService authService;
	final SecurityContextRepository securityContextRepository;
	
	@PostMapping
	ResponseEntity<?> post(@RequestBody Request body, @Autowired HttpServletResponse response, @Autowired HttpServletRequest request, @AuthenticationPrincipal User user)
	{
		if (!authService.deleteUserIfPasswordMatches(user.getUsername(), body.password)) throw new BadCredentialsException("Failed to authenticate the user to be deleted");
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(null);
		securityContextRepository.saveContext(securityContext, request, response);
		
		return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI.create("/")).build();
	}
	
	record Request(String password) {}
}
