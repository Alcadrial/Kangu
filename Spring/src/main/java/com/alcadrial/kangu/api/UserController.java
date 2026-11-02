package com.alcadrial.kangu.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	
	final UserService userService;
	
	@GetMapping
	ResponseEntity<?> get(@AuthenticationPrincipal User user)
	{
		return ResponseEntity.ok(userService.get(user.getUsername()));
	}
	
	@PostMapping("/changeEmail")
	ResponseEntity<?> getChangeEmail(@AuthenticationPrincipal User user, @RequestBody String newEmail)
	{
		userService.changeEmail(user.getUsername(), newEmail);
		return ResponseEntity.ok("success");
	}
}
