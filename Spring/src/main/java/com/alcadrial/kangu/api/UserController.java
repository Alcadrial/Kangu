package com.alcadrial.kangu.api;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@AllArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	
	private ObjectMapper objectMapper;
	
	@GetMapping
	ResponseEntity<?> get(@AuthenticationPrincipal @Nullable Object principal)
	{
		if (principal == null) return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok(switch (principal)
		{
			case User user -> objectMapper.createObjectNode()
				.put("principal", user.getUsername())
				.put("isEnabled", user.isEnabled())
				.put("isExpired", !user.isCredentialsNonExpired())
				.put("isLocked", user.isAccountNonLocked())
				.set("authorities", objectMapper.createArrayNode()
					.addAll(user.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.map(objectMapper::stringNode)
						.toList()
					));
			case String anonymous -> objectMapper.createObjectNode()
				.put("principal", anonymous)
				.put("isAnonymous", anonymous.equals("anonymous"))
				.set("authorities", objectMapper.createArrayNode());
			default -> objectMapper.createObjectNode()
				.put("principal", principal.toString())
				.set("authorities", objectMapper.createArrayNode());
		});
	}
}
