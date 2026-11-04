package com.alcadrial.kangu.unit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.properties.MessagesProperties;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesRepository;
import com.alcadrial.kangu.repository.UserRepository;
import com.alcadrial.kangu.service.MessagesService;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {

	static final String SENDER = TestConstants.USERNAME;
	static final String RECEIVER = TestConstants.USERNAME1;
	static final String MESSAGE = TestConstants.MESSAGE;
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private MessagesRepository messagesRepository;
	@Mock
	private MessagesReadCursorsRepository messagesReadCursorsRepository;
	@Mock
	private MessagesProperties messagesProperties;
	@InjectMocks
	private MessagesService messagesService;
	
	@Test
	void successSendMessageSinglePart() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(false);
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(50);
		
		LocalDateTime time = LocalDateTime.now();
		messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, time);
		
		verify(messagesRepository).save(new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, time, 0));
	}
	
	@Test
	void successSendMessageMultiPart() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(false);
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(5);
		
		LocalDateTime time = LocalDateTime.now();
		messagesService.sendMessage(SENDER, RECEIVER, "a".repeat(5) + "b".repeat(5) + "c".repeat(5), time);
		
		verify(messagesRepository).save(new MessageEntity(SENDER, RECEIVER, "a".repeat(5), MediaType.TEXT_PLAIN_VALUE, time, 0));
		verify(messagesRepository).save(new MessageEntity(SENDER, RECEIVER, "b".repeat(5), MediaType.TEXT_PLAIN_VALUE, time, 1));
		verify(messagesRepository).save(new MessageEntity(SENDER, RECEIVER, "c".repeat(5), MediaType.TEXT_PLAIN_VALUE, time, 2));
	}
	
	@Test
	void failSendMessageIfMessageTooLong() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		
		assertThrows(MessageTooLongException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, "a".repeat(101), LocalDateTime.now());
		});
	}
	
	@Test
	void failSendMessageIfReceiverDoesNotExists() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(false);
		
		assertThrows(UserNotFoundException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, LocalDateTime.now());
		});
	}
	
	@Test
	void failSendMessageIfBlockedByReceiver() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(true);
		
		assertThrows(BlockedException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, LocalDateTime.now());
		});
	}
	
	@Test
	void successBlockUnblockUser()
	{
		messagesService.blockUser(SENDER, RECEIVER);
		verify(messagesReadCursorsRepository).updateBlockedStatus(SENDER, RECEIVER, true);
		messagesService.unblockUser(SENDER, RECEIVER);
		verify(messagesReadCursorsRepository).updateBlockedStatus(SENDER, RECEIVER, false);
	}
	
	@Test
	void successReadMessageByArrivalTimestamp()
	{
		when(messagesProperties.getMaxBulkMessagesCount()).thenReturn(10);
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, SENDER, null, 0);
		verify(messagesRepository).findMessagesSelfSent(SENDER, 10);
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, null, 0);
		verify(messagesRepository).findMessagesSentOrReceived(SENDER, RECEIVER, 10);
		
		LocalDateTime time = LocalDateTime.now();
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, SENDER, time, 1);
		verify(messagesRepository).findMessagesSelfSentBeforeTimeAndPart(SENDER, time, 1, 10);
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, time, 1);
		verify(messagesRepository).findMessagesSentOrReceivedBeforeTimeAndPart(SENDER, RECEIVER, time, 1, 10);
	}
}
