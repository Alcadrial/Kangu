package com.alcadrial.kangu.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.data.MessageReadCursorEntity;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.MessagesCursorNotFoundException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.properties.MessagesProperties;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.ChatPreviewData;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.MessageReadCursorData;
import com.alcadrial.kangu.repository.MessagesRepository;
import com.alcadrial.kangu.repository.UserRepository;
import com.alcadrial.kangu.service.MessagesService;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {
	
	static final String SENDER = TestConstants.USERNAME;
	static final String RECEIVER = TestConstants.USERNAME1;
	static final String OTHER = TestConstants.USERNAME2;
	static final String MESSAGE = TestConstants.MESSAGE;
	
	static final LocalDateTime T_0 = TestConstants.TIME_0;
	static final LocalDateTime T_1 = TestConstants.TIME_1;
	static final LocalDateTime T_2 = TestConstants.TIME_2;
	static final LocalDateTime T_3 = TestConstants.TIME_3;
	static final LocalDateTime T_4 = TestConstants.TIME_4;
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private MessagesRepository messagesRepository;
	@Mock
	private MessagesReadCursorsRepository messagesReadCursorsRepository;
	@Mock
	private MessagesProperties messagesProperties;
	@Mock
	private MessageReadCursorEntity messageReadCursorEntity;
	@InjectMocks
	private MessagesService messagesService;
	
	private ChatPreviewData chatPreviewDataOf(String sender, String message)
	{
		return new ChatPreviewData(sender, message, "", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), false, false);
	}
	private ChatPreviewData chatPreviewDataOf(String sender, String message, LocalDateTime time)
	{
		return new ChatPreviewData(sender, message, "", time, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), false, false);
	}
	
	@Test
	void successSendMessageSinglePart() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.of(false));
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(50);
		
		LocalDateTime time = LocalDateTime.now();
		messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, time);
		
		verify(messagesRepository).save(new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, time, 0));
	}
	
	@Test
	void successSendMessageWithoutChat() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.empty());
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(50);
		
		LocalDateTime time = LocalDateTime.now();
		assertDoesNotThrow(() -> {
			messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, time);
		});
		verify(messagesRepository).save(new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, time, 0));
	}
	
	@Test
	void successSendMessageMultiPart() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.of(false));
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
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.of(true));
		
		assertThrows(BlockedException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, LocalDateTime.now());
		});
	}
	
	@Test
	void successBlockUnblockUser()
	{
		MessageReadCursorEntity.PrimaryKey key = new MessageReadCursorEntity.PrimaryKey(SENDER, RECEIVER);
		when(messagesReadCursorsRepository.findById(eq(key))).thenReturn(Optional.of(messageReadCursorEntity));
		
		messagesService.blockUser(SENDER, RECEIVER);
		verify(messageReadCursorEntity).setBlocked(true);
		messagesService.unblockUser(SENDER, RECEIVER);
		verify(messageReadCursorEntity).setBlocked(false);
	}
	
	@Test
	void successBlockUnblockNothing()
	{
		MessageReadCursorEntity.PrimaryKey key = new MessageReadCursorEntity.PrimaryKey(SENDER, RECEIVER);
		when(messagesReadCursorsRepository.findById(eq(key))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			messagesService.blockUser(SENDER, RECEIVER);
			messagesService.unblockUser(SENDER, RECEIVER);
		});
	}
	
	@Test
	void successReadMessageByArrivalTimestamp()
	{
		LocalDateTime time = LocalDateTime.now();
		int limit = 10;
		when(messagesProperties.getMaxBulkMessagesCount()).thenReturn(limit);
		when(messagesReadCursorsRepository.findById(eq(SENDER), eq(RECEIVER))).thenReturn(Optional.of(new MessageReadCursorData(LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), false, false)));
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, SENDER, null, 0);
		verify(messagesRepository).findMessagesSelfSent(SENDER, limit);
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, null, 0);
		verify(messagesRepository).findMessagesSentOrReceived(SENDER, RECEIVER, limit);
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, SENDER, time, 1);
		verify(messagesRepository).findMessagesSelfSentBeforeTimeAndPart(SENDER, time, 1, limit);
		
		messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, time, 1);
		verify(messagesRepository).findMessagesSentOrReceivedBeforeTimeAndPart(SENDER, RECEIVER, time, 1, limit);
	}
	
	@Test
	void failReadMessageWithoutChat()
	{
		when(messagesProperties.getMaxBulkMessagesCount()).thenReturn(10);
		when(messagesReadCursorsRepository.findById(eq(SENDER), eq(RECEIVER))).thenReturn(Optional.empty());
		
		assertThrows(MessagesCursorNotFoundException.class, () -> {
			messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, LocalDateTime.now(), 1);
		});
	}
	
	@Test
	void successGetChatPreviewsWhenEmpty()
	{
		when(messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(eq(SENDER))).thenReturn(List.of());
		
		assertEquals(List.of(), messagesService.getChatsPreviews(SENDER));
	}
	
	@Test
	void successGetChatPreviewsWhenZeroDuplicates()
	{
		List<ChatPreviewData> dataWithoutDuplicates = List.of(
			chatPreviewDataOf(RECEIVER, MESSAGE),
			chatPreviewDataOf(OTHER, MESSAGE)
		);
		when(messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(eq(SENDER))).thenReturn(dataWithoutDuplicates);

		assertEquals(dataWithoutDuplicates, messagesService.getChatsPreviews(SENDER));
	}
	
	@Test
	void successGetChatPreviewsWhenExistsDuplicates()
	{
		List<ChatPreviewData> dataWithDuplicates = List.of(
			chatPreviewDataOf(RECEIVER, MESSAGE, T_3),
			chatPreviewDataOf(OTHER, MESSAGE, T_1),
			chatPreviewDataOf(OTHER, MESSAGE, T_2),
			chatPreviewDataOf(RECEIVER, MESSAGE, T_0),
			chatPreviewDataOf(SENDER, MESSAGE, T_4)
		);
		List<ChatPreviewData> dataWithoutDuplicates = List.of(
			chatPreviewDataOf(RECEIVER, MESSAGE, T_3),
			chatPreviewDataOf(OTHER, MESSAGE, T_2),
			chatPreviewDataOf(SENDER, MESSAGE, T_4)
		);
		when(messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(eq(SENDER))).thenReturn(dataWithDuplicates);

		assertEquals(dataWithoutDuplicates, messagesService.getChatsPreviews(SENDER));
	}
}
