package com.alcadrial.kangu.user;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	
	final UserService userService;
	
	@GetMapping
	ResponseEntity<?> get(@AuthenticationPrincipal User user, @RequestParam("username") @Nullable String username)
	{
		return ResponseEntity.ok(userService.get(username != null ? username : user.getUsername()));
	}
}
