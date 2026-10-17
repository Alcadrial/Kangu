package com.alcadrial.kafkatest.unit.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kafkatest.TestConfig;
import com.alcadrial.kafkatest.security.SecurityConfig;

@WebMvcTest({SecurityConfig.class, TestConfig.class})
class LogoutControllerTest {
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
	}
	
	@Test
	public void postLogoutWithoutUser() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader()))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, "/login?logout"));
	}
	
	@Test
	@WithMockUser
	public void postLogoutWithUser() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader()))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, "/login?logout"));
	}
}
