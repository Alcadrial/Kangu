package com.alcadrial.kangu.unit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConfig;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.api.LoginController;
import com.alcadrial.kangu.security.SecurityConfig;
import com.alcadrial.kangu.service.AuthServiceMock;

@WebMvcTest({SecurityConfig.class, TestConfig.class, LoginController.class, AuthServiceMock.class})
class CsrfTests {
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
	}
	
	@Test
	public void loginWhenValidCsrfTokenThenSuccess() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", "user", "password", "password"))
			)
			.andExpect(status().isUnauthorized())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.error").value("User not found"));
	}
	
	@Test
	public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader().useInvalidToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", "user", "password", "password"))
			)
			.andExpect(status().isForbidden());
	}
	
	@Test
	public void loginWhenMissingCsrfTokenThenForbidden() throws Exception
	{
		mockMvc
			.perform(post("/api/login")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", "user", "password", "password"))
			)
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser
	public void logoutWhenValidCsrfTokenThenSuccess() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, "/login?logout"));
	}
}