package com.alcadrial.kangu.integration.api;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.properties.RegistrationProperties;
import com.alcadrial.kangu.service.AuthService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
	//"spring.datasource.url=jdbc:tc:@//localhost:1521/FREEPDB1"
})
@Testcontainers
@Transactional
class LoginTest {
	
	private MockMvc mockMvc;
	@Autowired
	private AuthService authService;
	@MockitoBean
	private RegistrationProperties registrationProperties;
	/*
	@Container
	private OracleContainer container = new OracleContainer("gvenzl/oracle-free")
		.withUsername("spring")
		.withDatabaseName("freepbd1")
		.withInitScripts("sql/db.sql", "sql/schema.sql")
		.;*/
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext) throws Exception
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
		when(registrationProperties.isEnabled()).thenReturn(true);
		authService.deleteUser(TestConstants.USERNAME);
		authService.register(TestConstants.USERNAME, TestConstants.EMAIL, TestConstants.PASSWORD);
	}
	
	@Test
	public void successLoginWithValidCredentials() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			)
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/api/user"));
	}
	
	@Test
	public void failLoginWithInvalidCredentials() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD1))
			)
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser
	public void successLogoutWithAuthority() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}
	
	@Test
	public void successLogoutWithoutAuthority() throws Exception
	{
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}
}
