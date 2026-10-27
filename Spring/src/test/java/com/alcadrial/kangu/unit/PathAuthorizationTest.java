package com.alcadrial.kangu.unit;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.security.SecurityConfig;

@WebMvcTest(PathAuthorizationTest.TestController.class)
@Import(SecurityConfig.class)
class PathAuthorizationTest {
	
	@RestController
	interface TestController {
		
		//public urls
		@GetMapping("/api/csrf")
		ResponseEntity<?> csrfGet();
		@PostMapping("/api/login")
		ResponseEntity<?> loginPost();
		@PostMapping("/api/register")
		ResponseEntity<?> registerPost();
		@GetMapping("/api/user")
		ResponseEntity<?> userGet();
		
		//user urls
		@PostMapping("/api/delete")
		ResponseEntity<?> deletePost();
		@PostMapping("/api/messages/send")
		ResponseEntity<?> messagesSendPost();
		
		//admin routes
		@GetMapping("/api/admin")
		ResponseEntity<?> adminGet();
	}
	
	@Autowired
	MockMvc mockMvc;
	@MockitoBean
	TestController controller;
	
	@Test
	public void successPublicEndpoints() throws Exception
	{
		when(controller.csrfGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/csrf")).andExpect(status().isOk());
		
		when(controller.loginPost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/login").with(csrf())).andExpect(status().isOk());
		
		when(controller.registerPost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/register").with(csrf())).andExpect(status().isOk());
		
		when(controller.userGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/user")).andExpect(status().isOk());
	}
	
	@Test
	public void failPrivateEndpoints() throws Exception
	{
		when(controller.deletePost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/delete").with(csrf())).andExpect(status().isForbidden());
		
		when(controller.messagesSendPost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/messages/send").with(csrf())).andExpect(status().isForbidden());
		
		when(controller.adminGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/admin").with(csrf())).andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(roles = "USER")
	public void successUserEndpoints() throws Exception
	{
		when(controller.deletePost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/delete").with(csrf())).andExpect(status().isOk());
		
		when(controller.messagesSendPost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/messages/send").with(csrf())).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = "USER")
	public void failAdminEndpointsForUsers() throws Exception
	{
		when(controller.adminGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/admin").with(csrf())).andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void successAdminEndpoints() throws Exception
	{
		when(controller.adminGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/admin").with(csrf())).andExpect(status().isOk());
	}
}