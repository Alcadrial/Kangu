package com.alcadrial.kangu.integration.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.data.UserEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestEntityManager
@Transactional
class LoginTest {
	
	private MockMvc mockMvc;
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
		String password = (@NonNull String) passwordEncoder.encode(TestConstants.PASSWORD);
		entityManager.persist(new UserEntity(TestConstants.USERNAME, TestConstants.EMAIL, password));
	}
	
	@Test
	public void loginWithValidCredentials() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			)
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/api/user"));
	}
	
	@Test
	public void loginWithInvalidCredentials() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD1))
			)
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser
	public void logoutWithAuthority() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}
	
	@Test
	public void logoutWithoutAuthority() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}
}
