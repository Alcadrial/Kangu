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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

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
import com.alcadrial.kangu.repository.MessagesRepository.Message;
import com.alcadrial.kangu.service.MessagesService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(MessagesController.class)
class MessagesControllerTest {
	
	private MockMvc mockMvc;
	
	@MockitoBean
	private MessagesService messagesService;
	@Autowired
	private ObjectMapper objectMapper;
	
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
		List<Message> data = List.of(
			new Message("m0", TestConstants.TIME_0, 0),
			new Message("m1", TestConstants.TIME_1, 0),
			new Message("m2", TestConstants.TIME_2, 0),
			new Message("m3", TestConstants.TIME_3, 0),
			new Message("m4", TestConstants.TIME_4, 0)
		);
		when(messagesService.readMessageByArrivalTimestamp(eq(TestConstants.USERNAME), eq(TestConstants.USERNAME1), any(), anyInt()))
			.thenReturn(data);
		
		mockMvc
			.perform(get("/api/messages/get?user=" + TestConstants.USERNAME1))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(data)));
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void readOldMessages() throws Exception
	{
		List<Message> data = List.of(
			new Message("m0", TestConstants.TIME_0, 0),
			new Message("m1", TestConstants.TIME_1, 0),
			new Message("m2", TestConstants.TIME_2, 0),
			new Message("m3", TestConstants.TIME_3, 0),
			new Message("m4", TestConstants.TIME_4, 0)
		);
		when(messagesService.readMessageByArrivalTimestamp(eq(TestConstants.USERNAME), eq(TestConstants.USERNAME1), any(LocalDateTime.class), eq(1)))
			.thenReturn(data);
		
		mockMvc
			.perform(get("/api/messages/get")
				.queryParam("user", TestConstants.USERNAME1)
				.queryParam("before", TestConstants.TIME_4.toString())
				.queryParam("part", "1")
			)
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(data)));
	}
}
