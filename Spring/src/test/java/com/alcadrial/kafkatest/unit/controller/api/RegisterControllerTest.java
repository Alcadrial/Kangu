package com.alcadrial.kafkatest.unit.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.ParameterDeclarations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kafkatest.TestConfig;
import com.alcadrial.kafkatest.TestConstants;
import com.alcadrial.kafkatest.TestUtils;
import com.alcadrial.kafkatest.api.RegisterController;
import com.alcadrial.kafkatest.security.SecurityConfig;
import com.alcadrial.kafkatest.service.AuthServiceMock;

@WebMvcTest({SecurityConfig.class, TestConfig.class, RegisterController.class, AuthServiceMock.class})
class RegisterControllerTest {
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
	}
	
	@Test
	public void postValidRegister() throws Exception
	{
		mockMvc
			.perform(post("/api/register").with(csrf().asHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "email", TestConstants.EMAIL, "password", TestConstants.PASSWORD))
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("Location", "/api/user"));
	}
	
	/** <pre>username,password</pre> */
	public static class InvalidRegisterData implements ArgumentsProvider {
		
		@Override
		public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) throws Exception
		{
			return Stream.of(TestConstants.crossProduct(
				new String[] {null, "", TestConstants.USERNAME},
				new String[] {null, "", TestConstants.EMAIL},
				new String[] {null, "", TestConstants.PASSWORD}
			)).limit(26).map(Arguments::of);
		}
	}
	
	@ParameterizedTest
	@ArgumentsSource(InvalidRegisterData.class)
	public void postInvalidRegister(String username, String email, String password) throws Exception
	{
		mockMvc
			.perform(post("/api/register").with(csrf().asHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", username, "email", email, "password", password))
			)
			.andExpect(status().is4xxClientError());
	}
}
