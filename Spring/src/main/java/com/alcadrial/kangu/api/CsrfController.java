package com.alcadrial.kangu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/csrf")
public class CsrfController {
	
	@GetMapping
	JsonNode getCsrf(@Autowired HttpServletResponse response, @Autowired ObjectMapper mapper, @Autowired CsrfToken csrfToken)
	{
		String token = csrfToken.getToken();
		response.addCookie(new Cookie("XSRF-TOKEN", token));
		return mapper.createObjectNode().put("csrf", token);
	}
}
