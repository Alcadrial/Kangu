package com.alcadrial.kangu.unit;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.security.SecurityConfig;

@WebMvcTest(SecurityConfig.class)
class CsrfTests {
	
	@RestController
	interface TestController {
		
		@PostMapping("/")
		ResponseEntity<?> post();
	}
	
	private MockMvc mockMvc;
	@MockitoBean
	private TestController controller;
	
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
		when(controller.post()).thenReturn(ResponseEntity.ok().build());
		mockMvc
			.perform(post("/").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", "user", "password", "password"))
			)
			.andExpect(status().isOk());
	}
	
	@Test
	public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception
	{
		mockMvc
			.perform(post("/").with(csrf().asHeader().useInvalidToken()))
			.andExpect(status().isForbidden());
	}
	
	@Test
	public void loginWhenMissingCsrfTokenThenForbidden() throws Exception
	{
		mockMvc
			.perform(post("/"))
			.andExpect(status().isForbidden());
	}
}