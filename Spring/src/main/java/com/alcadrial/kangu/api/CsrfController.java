package com.alcadrial.kangu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/csrf")
public class CsrfController {
	
	@GetMapping
	JsonNode getCsrf(@Autowired ObjectMapper mapper, @Autowired CsrfToken csrfToken)
	{
		return mapper.createObjectNode().put("csrf", csrfToken.getToken());
	}
}
