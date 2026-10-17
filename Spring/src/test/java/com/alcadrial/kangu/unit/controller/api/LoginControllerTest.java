package com.alcadrial.kangu.unit.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConfig;
import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.api.LoginController;
import com.alcadrial.kangu.security.SecurityConfig;
import com.alcadrial.kangu.service.AuthServiceMock;

@WebMvcTest({SecurityConfig.class, TestConfig.class, LoginController.class, AuthServiceMock.class})
class LoginControllerTest {
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
	}
	
	@Test
	public void postLogin() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			)
			.andExpect(status().isUnauthorized());
	}
}
