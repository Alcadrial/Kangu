package com.alcadrial.kangu.unit.controller.api;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConfig;
import com.alcadrial.kangu.api.UserController;
import com.alcadrial.kangu.security.SecurityConfig;

@WebMvcTest({SecurityConfig.class, TestConfig.class, UserController.class})
class UserControllerTest {
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
	}
	
	@Test
	@WithMockUser(username = "admina", password = "password", roles = {"USER", "ADMIN"})
	public void getAuthenticatedSessionData() throws Exception
	{
		mockMvc
			.perform(get("/api/user"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.principal").value("admina"))
			.andExpect(jsonPath("$.isEnabled").value(true))
			.andExpect(jsonPath("$.authorities").value(Matchers.containsInAnyOrder("ROLE_USER", "ROLE_ADMIN")));
	}
	
	@Test
	public void getUnautenticatedSessionData() throws Exception
	{
		mockMvc
			.perform(get("/api/user"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.principal").value("anonymousUser"))
			.andExpect(jsonPath("$.isAnonymous").value(true))
			.andExpect(jsonPath("$.authorities").value(Matchers.containsInAnyOrder("ROLE_ANONYMOUS")));
	}
}
