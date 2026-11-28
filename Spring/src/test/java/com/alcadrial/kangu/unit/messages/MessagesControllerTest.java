package com.alcadrial.kangu.unit.messages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
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
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.MessagesCursorNotFoundException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.messages.MessageEntity;
import com.alcadrial.kangu.messages.MessagesController;
import com.alcadrial.kangu.messages.MessagesService;
import com.alcadrial.kangu.messages.MessageReadCursorsRepository.ChatPreviewData;
import com.alcadrial.kangu.messages.MessageReadCursorsRepository.MessageReadCursorData;
import com.alcadrial.kangu.messages.MessagesService.ChatDetailsAndMessages;
import com.alcadrial.kangu.user.UserEntity;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(MessagesController.class)
class MessagesControllerTest {
	
	static final UserEntity SENDER_USER = TestConstants.USER;
	static final UserEntity RECEIVER_USER = TestConstants.USER1;
	static final String SENDER = TestConstants.USERNAME;
	static final String RECEIVER = TestConstants.USERNAME1;
	static final String RECEIVER1 = TestConstants.USERNAME2;
	static final String EMAIL = TestConstants.EMAIL;
	static final String PASSWORD = TestConstants.PASSWORD;
	
	static final String MESSAGE = TestConstants.MESSAGE;
	static final String MESSAGE1 = TestConstants.MESSAGE1;
	static final String MESSAGE2 = TestConstants.MESSAGE2;
	static final String MESSAGE3 = TestConstants.MESSAGE3;
	static final String MESSAGE4 = TestConstants.MESSAGE4;
	static final String MESSAGE5 = TestConstants.MESSAGE5;
	static final String MESSAGE6 = TestConstants.MESSAGE6;
	static final String MESSAGE7 = TestConstants.MESSAGE7;
	static final String MESSAGE8 = TestConstants.MESSAGE8;
	
	static final Instant T_0 = TestConstants.TIME_0;
	static final Instant T_1 = TestConstants.TIME_1;
	static final Instant T_2 = TestConstants.TIME_2;
	static final Instant T_3 = TestConstants.TIME_3;
	static final Instant T_4 = TestConstants.TIME_4;
	static final Instant T_5 = TestConstants.TIME_5;
	static final Instant T_6 = TestConstants.TIME_6;
	static final Instant T_7 = TestConstants.TIME_7;
	static final Instant T_8 = TestConstants.TIME_8;
	static final Instant T_LAST = TestConstants.TIME_LAST;
	
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
	@WithMockUser(username = SENDER)
	public void successSendMessage() throws Exception
	{
		MessageEntity[] message = {new MessageEntity(SENDER, RECEIVER, MESSAGE1, MediaType.TEXT_PLAIN_VALUE, T_0, 0)};
		
		when(messagesService.sendMessage(eq(SENDER), eq(RECEIVER), eq(MESSAGE1), any()))
			.thenReturn(message);
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", RECEIVER, "message", MESSAGE1))
			)
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(message)));
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void failSendMessageTooLong() throws Exception
	{
		when(messagesService.sendMessage(eq(SENDER), eq(RECEIVER), eq(MESSAGE1), any()))
			.thenThrow(MessageTooLongException.class);
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", RECEIVER, "message", MESSAGE1))
			)
			.andExpect(status().isContentTooLarge());
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void failSendMessageUserNotFound() throws Exception
	{
		when(messagesService.sendMessage(eq(SENDER), eq(RECEIVER), eq(MESSAGE1), any()))
			.thenThrow(UserNotFoundException.class);
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", RECEIVER, "message", MESSAGE1))
			)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void failSendMessageBlocked() throws Exception
	{
		when(messagesService.sendMessage(eq(SENDER), eq(RECEIVER), eq(MESSAGE1), any()))
			.thenThrow(BlockedException.class);
		
		mockMvc
			.perform(post("/api/messages/send")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("receiver", RECEIVER, "message", MESSAGE1))
			)
			.andExpect(status().isConflict());
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void readLastMessages() throws Exception
	{
		ChatDetailsAndMessages data = new ChatDetailsAndMessages(List.of(
			new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, T_0, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE1, MediaType.TEXT_PLAIN_VALUE, T_1, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE2, MediaType.TEXT_PLAIN_VALUE, T_2, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE3, MediaType.TEXT_PLAIN_VALUE, T_3, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE4, MediaType.TEXT_PLAIN_VALUE, T_4, 0)
		), new MessageReadCursorData(T_5, T_6, T_7, false, false));
		
		when(messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, null, 0))
			.thenReturn(data);
		
		mockMvc
			.perform(get("/api/messages/get?user=" + RECEIVER))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(data)));
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void readOldMessages() throws Exception
	{
		ChatDetailsAndMessages data = new ChatDetailsAndMessages(List.of(
			new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, T_0, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE1, MediaType.TEXT_PLAIN_VALUE, T_1, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE2, MediaType.TEXT_PLAIN_VALUE, T_2, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE3, MediaType.TEXT_PLAIN_VALUE, T_3, 0),
			new MessageEntity(SENDER, RECEIVER, MESSAGE4, MediaType.TEXT_PLAIN_VALUE, T_4, 0)
		), new MessageReadCursorData(T_5, T_6, T_7, false, false));
		
		when(messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, T_4, 1))
			.thenReturn(data);
		
		mockMvc
			.perform(get("/api/messages/get")
				.queryParam("user", RECEIVER)
				.queryParam("before", T_4.toString())
				.queryParam("part", "1")
			)
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(data)));
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void failReadMessages() throws Exception
	{
		when(messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, null, 0))
			.thenThrow(MessagesCursorNotFoundException.class);
		
		mockMvc
			.perform(get("/api/messages/get?user=" + RECEIVER))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = SENDER)
	public void getChats() throws Exception
	{
		List<ChatPreviewData> data = List.of(
			new ChatPreviewData(RECEIVER, MESSAGE1, MediaType.TEXT_PLAIN_VALUE, T_0, T_1, T_2, T_3, false, false),
			new ChatPreviewData(RECEIVER1, MESSAGE2, MediaType.TEXT_PLAIN_VALUE, T_4, T_5, T_6, T_7, false, false)
		);
		
		when(messagesService.getChatsPreviews(SENDER))
			.thenReturn(data);
		
		mockMvc
			.perform(get("/api/messages/chats"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(data)));
	}
}
