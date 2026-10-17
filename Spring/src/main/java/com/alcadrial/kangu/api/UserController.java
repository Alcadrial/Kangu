package com.alcadrial.kangu.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@RequestMapping("/api/user")
@RestController
public class UserController {
	
	private ObjectMapper objectMapper;
	
	UserController(ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}
	
	@GetMapping
	JsonNode get(@CurrentSecurityContext SecurityContext securityContext)
	{
		Authentication auth = securityContext.getAuthentication();
		
		ArrayNode authorities = objectMapper.createArrayNode();
		for (GrantedAuthority authority : auth.getAuthorities()) authorities.add(authority.getAuthority());
		
		return switch (auth.getPrincipal())
		{
			case User user -> objectMapper.createObjectNode()
				.put("principal", user.getUsername())
				.put("isEnabled", user.isEnabled())
				.put("isExpired", !user.isCredentialsNonExpired())
				.put("isLocked", user.isAccountNonLocked())
				.set("authorities", authorities);
			case String user -> objectMapper.createObjectNode()
				.put("principal", user)
				.put("isAnonymous", user.equals("anonymousUser"))
				.set("authorities", authorities);
			default -> objectMapper.createObjectNode()
				.put("principal", auth.getPrincipal().toString())
				.set("authorities", authorities);
		};
	}
}
