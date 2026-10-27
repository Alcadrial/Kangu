package com.alcadrial.kangu.unit;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.security.SecurityConfig;

@WebMvcTest(CsrfTests.TestController.class)
@Import(SecurityConfig.class)
class CsrfTests {
	
	@RestController
	interface TestController {
		
		@PostMapping("/")
		ResponseEntity<?> post();
	}
	
	@Autowired
	MockMvc mockMvc;
	@MockitoBean
	TestController controller;
	
	@Test
	public void loginWhenValidCsrfTokenThenSuccess() throws Exception
	{
		when(controller.post()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/").with(csrf().asHeader())).andExpect(status().isOk());
	}
	
	@Test
	public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception
	{
		mockMvc.perform(post("/").with(csrf().asHeader().useInvalidToken())).andExpect(status().isForbidden());
	}
	
	@Test
	public void loginWhenMissingCsrfTokenThenForbidden() throws Exception
	{
		mockMvc.perform(post("/")).andExpect(status().isForbidden());
	}
}