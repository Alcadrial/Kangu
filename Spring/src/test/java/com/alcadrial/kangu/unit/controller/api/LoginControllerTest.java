package com.alcadrial.kangu.unit.controller.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.api.LoginController;
import com.alcadrial.kangu.service.AuthService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(LoginController.class)
class LoginControllerTest {
	
	private MockMvc mockMvc;
	@MockitoBean
	private AuthService authService;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}
	
	@Test
	public void validLoginPost() throws Exception
	{
		when(authService.login(TestConstants.USERNAME, TestConstants.PASSWORD, false)).thenReturn(TestConstants.USER);
		
		mockMvc
			.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			)
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/api/user"));
	}
	
	@Test
	public void invalidLoginPost() throws Exception
	{
		when(authService.login(TestConstants.USERNAME, TestConstants.PASSWORD, false)).thenThrow(EntityNotFoundException.class);
		
		mockMvc
			.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			)
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
}
