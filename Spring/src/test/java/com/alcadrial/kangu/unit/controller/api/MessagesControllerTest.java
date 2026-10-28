package com.alcadrial.kangu.unit.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.api.MessagesController;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.service.MessagesService;

@WebMvcTest(MessagesController.class)
class MessagesControllerTest {
	
	private MockMvc mockMvc;
	
	@MockitoBean
	private MessagesService messagesService;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void successSendMessage() throws Exception
	{
		doNothing().when(messagesService).sendMessage(anyString(), anyString(), anyString(), any());
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", TestConstants.USERNAME1, "message", TestConstants.MESSAGE1))
			)
			.andExpect(status().isNoContent());
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void failSendMessageTooLong() throws Exception
	{
		doThrow(MessageTooLongException.class).when(messagesService).sendMessage(anyString(), anyString(), anyString(), any());
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", TestConstants.USERNAME1, "message", TestConstants.MESSAGE1))
			)
			.andExpect(status().isContentTooLarge());
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void failSendMessageUserNotFound() throws Exception
	{
		doThrow(UserNotFoundException.class).when(messagesService).sendMessage(anyString(), anyString(), anyString(), any());
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", TestConstants.USERNAME1, "message", TestConstants.MESSAGE1))
			)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void failSendMessageBlocked() throws Exception
	{
		doThrow(BlockedException.class).when(messagesService).sendMessage(anyString(), anyString(), anyString(), any());
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", TestConstants.USERNAME1, "message", TestConstants.MESSAGE1))
			)
			.andExpect(status().isConflict());
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void readLastMessages() throws Exception
	{
		when(messagesService.readMessageByArrivalTimestamp(eq(TestConstants.USERNAME), eq(TestConstants.USERNAME1), any(), anyInt()))
			.thenReturn(List.of("m0", "m1", "m2", "m3", "m4"));
		
		mockMvc
			.perform(get("/api/messages/get?user=" + TestConstants.USERNAME1))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(Matchers.contains("m0", "m1", "m2", "m3", "m4")));
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void readOldMessages() throws Exception
	{
		when(messagesService.readMessageByArrivalTimestamp(eq(TestConstants.USERNAME), eq(TestConstants.USERNAME1), any(LocalDateTime.class), eq(1)))
			.thenReturn(List.of("m0", "m1", "m2", "m3", "m4"));
		
		mockMvc
			.perform(get("/api/messages/get")
				.queryParam("user", TestConstants.USERNAME1)
				.queryParam("before", LocalDateTime.of(2026, 1, 1, 0, 0).toString())
				.queryParam("part", "1")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(Matchers.contains("m0", "m1", "m2", "m3", "m4")));
	}
}
