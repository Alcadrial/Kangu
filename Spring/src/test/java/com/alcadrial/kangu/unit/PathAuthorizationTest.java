package com.alcadrial.kangu.unit;

import static org.mockito.Mockito.when;
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
		
		//user urls
		@PostMapping("/api/messages/send")
		ResponseEntity<?> messagesSendPost();
		@GetMapping("/api/messages/get")
		ResponseEntity<?> messagesGetGet();
		@GetMapping("/api/messages/chats")
		ResponseEntity<?> messagesChatsGet();
		@GetMapping("/api/user")
		ResponseEntity<?> userGet();
	}
	
	@Autowired
	MockMvc mockMvc;
	@MockitoBean
	TestController controller;
	
	@Test
	public void failPrivateEndpoints() throws Exception
	{
		when(controller.messagesSendPost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/messages/send")).andExpect(status().isUnauthorized());
		
		when(controller.messagesGetGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/messages/get")).andExpect(status().isUnauthorized());
		
		when(controller.messagesChatsGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/messages/chats")).andExpect(status().isUnauthorized());
		
		when(controller.userGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/user")).andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(roles = "USER")
	public void successUserEndpoints() throws Exception
	{
		when(controller.messagesSendPost()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(post("/api/messages/send")).andExpect(status().isOk());
		
		when(controller.messagesGetGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/messages/get")).andExpect(status().isOk());
		
		when(controller.messagesChatsGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/messages/chats")).andExpect(status().isOk());
		
		when(controller.userGet()).thenReturn(ResponseEntity.ok().build());
		mockMvc.perform(get("/api/user")).andExpect(status().isOk());
	}
}