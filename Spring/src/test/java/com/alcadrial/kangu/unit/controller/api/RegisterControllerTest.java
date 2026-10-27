package com.alcadrial.kangu.unit.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.hamcrest.Matchers;
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
import com.alcadrial.kangu.api.RegisterController;
import com.alcadrial.kangu.exception.DisabledException;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.service.AuthService;

@WebMvcTest(RegisterController.class)
class RegisterControllerTest {

	private MockMvc mockMvc;
	@MockitoBean
	private AuthService authService;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}
	
	@Test
	public void registerNewUserWithSuccess() throws Exception
	{
		when(authService.login(any(), anyBoolean(), any(), any())).then(_ -> TestConstants.USER);
		mockMvc
			.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "email", TestConstants.EMAIL, "password", TestConstants.PASSWORD))
			)
			.andExpect(status().isOk());
	}
	
	@Test
	public void failToRegisterUserWithDuplicatedUsername() throws Exception
	{
		doThrow(DuplicateKeyException.class).when(authService).register(anyString(), anyString(), anyString());
		
		mockMvc
			.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "email", TestConstants.EMAIL, "password", TestConstants.PASSWORD))
			)
			.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void failToRegisterWhenDisabled() throws Exception
	{
		doThrow(DisabledException.class).when(authService).register(anyString(), anyString(), anyString());
		
		mockMvc
			.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "email", TestConstants.EMAIL, "password", TestConstants.PASSWORD))
			)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message", Matchers.not(Matchers.emptyOrNullString())));
	}
}
