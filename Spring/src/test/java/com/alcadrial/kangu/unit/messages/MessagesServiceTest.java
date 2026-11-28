package com.alcadrial.kangu.unit.messages;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.messages.MessageEntity;
import com.alcadrial.kangu.messages.MessageReadCursorEntity;
import com.alcadrial.kangu.messages.MessagesProperties;
import com.alcadrial.kangu.messages.MessageReadCursorsRepository;
import com.alcadrial.kangu.messages.MessageReadCursorsRepository.ChatPreviewData;
import com.alcadrial.kangu.messages.MessageReadCursorsRepository.MessageReadCursorData;
import com.alcadrial.kangu.messages.MessageRepository;
import com.alcadrial.kangu.messages.MessagesService;
import com.alcadrial.kangu.messages.MessagesService.ChatDetailsAndMessages;
import com.alcadrial.kangu.user.UserRepository;
import com.alcadrial.kangu.utils.Constants;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {
	
	static final String SENDER = TestConstants.USERNAME;
	static final String RECEIVER = TestConstants.USERNAME1;
	static final String OTHER = TestConstants.USERNAME2;
	static final String MESSAGE = TestConstants.MESSAGE;
	static final String MIME = MediaType.TEXT_PLAIN_VALUE;
	
	static final Instant T_0 = TestConstants.TIME_0;
	static final Instant T_1 = TestConstants.TIME_1;
	static final Instant T_2 = TestConstants.TIME_2;
	static final Instant T_3 = TestConstants.TIME_3;
	static final Instant T_4 = TestConstants.TIME_4;
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private MessageRepository messagesRepository;
	@Mock
	private MessageReadCursorsRepository messagesReadCursorsRepository;
	@Mock
	private MessagesProperties messagesProperties;
	@Mock
	private MessageReadCursorEntity messageReadCursorEntity;
	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;
	@InjectMocks
	private MessagesService messagesService;
	
	private ChatPreviewData chatPreviewDataOf(String sender, String message)
	{
		return new ChatPreviewData(sender, message, "", Instant.now(), Instant.now(), Instant.now(), Instant.now(), false, false);
	}
	
	private ChatPreviewData chatPreviewDataOf(String sender, String message, Instant time)
	{
		return new ChatPreviewData(sender, message, "", time, Instant.now(), Instant.now(), Instant.now(), false, false);
	}
	
	@Test
	void successSendMessageSinglePart() throws Exception
	{
		MessageEntity message = new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, T_0, 0);
		MessageEntity[] messages = new MessageEntity[] { message };
		
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.of(false));
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(50);
		when(messagesRepository.save(message)).thenReturn(message);
		
		assertArrayEquals(messages, messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, T_0));
		
		verify(kafkaTemplate).send(Constants.MESSAGES_TOPIC, RECEIVER, messages);
	}
	
	@Test
	void successSendMessageWithoutChat() throws Exception
	{
		MessageEntity message = new MessageEntity(SENDER, RECEIVER, MESSAGE, MediaType.TEXT_PLAIN_VALUE, T_0, 0);
		MessageEntity[] messages = new MessageEntity[] { message };
		
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.empty());
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(50);
		when(messagesRepository.save(message)).thenReturn(message);
		
		assertArrayEquals(messages, messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, T_0));
		
		verify(kafkaTemplate).send(Constants.MESSAGES_TOPIC, RECEIVER, messages);
	}
	
	@Test
	void successSendMessageMultiPart() throws Exception
	{
		MessageEntity[] messages = new MessageEntity[] {
			new MessageEntity(SENDER, RECEIVER, "a".repeat(5), MediaType.TEXT_PLAIN_VALUE, T_0, 0),
			new MessageEntity(SENDER, RECEIVER, "b".repeat(5), MediaType.TEXT_PLAIN_VALUE, T_0, 1),
			new MessageEntity(SENDER, RECEIVER, "c".repeat(5), MediaType.TEXT_PLAIN_VALUE, T_0, 2)
		};
		
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.of(false));
		when(messagesProperties.getMaxMessageBlockSize()).thenReturn(5);
		when(messagesRepository.save(messages[0])).thenReturn(messages[0]);
		when(messagesRepository.save(messages[1])).thenReturn(messages[1]);
		when(messagesRepository.save(messages[2])).thenReturn(messages[2]);
		
		assertArrayEquals(messages, messagesService.sendMessage(SENDER, RECEIVER, "a".repeat(5) + "b".repeat(5) + "c".repeat(5), T_0));

		verify(kafkaTemplate).send(Constants.MESSAGES_TOPIC, RECEIVER, messages);
	}
	
	@Test
	void failSendMessageIfMessageTooLong() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		
		assertThrows(MessageTooLongException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, "a".repeat(101), T_0);
		});
	}
	
	@Test
	void failSendMessageIfReceiverDoesNotExists() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(false);
		
		assertThrows(UserNotFoundException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, T_0);
		});
	}
	
	@Test
	void failSendMessageIfBlockedByReceiver() throws Exception
	{
		when(messagesProperties.getMaxMessageSize()).thenReturn(100);
		when(userRepository.existsById(RECEIVER)).thenReturn(true);
		when(messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER)).thenReturn(Optional.of(true));
		
		assertThrows(BlockedException.class, () -> {
			messagesService.sendMessage(SENDER, RECEIVER, MESSAGE, T_0);
		});
	}
	
	@Test
	void successBlockUser()
	{
		MessageReadCursorEntity.PrimaryKey key = new MessageReadCursorEntity.PrimaryKey(SENDER, RECEIVER);
		
		when(messagesReadCursorsRepository.findById(key)).thenReturn(Optional.of(messageReadCursorEntity));
		
		messagesService.blockUser(SENDER, RECEIVER);
		
		verify(messageReadCursorEntity).setBlocked(true);
		verify(kafkaTemplate).send(Constants.CURSORS_TOPIC, SENDER, messageReadCursorEntity);
	}
	
	@Test
	void successUnblockUser()
	{
		MessageReadCursorEntity.PrimaryKey key = new MessageReadCursorEntity.PrimaryKey(SENDER, RECEIVER);
		
		when(messagesReadCursorsRepository.findById(key)).thenReturn(Optional.of(messageReadCursorEntity));
		
		messagesService.unblockUser(SENDER, RECEIVER);
		
		verify(messageReadCursorEntity).setBlocked(false);
		verify(kafkaTemplate).send(Constants.CURSORS_TOPIC, SENDER, messageReadCursorEntity);
	}
	
	@Test
	void successBlockUnblockNothing()
	{
		MessageReadCursorEntity.PrimaryKey key = new MessageReadCursorEntity.PrimaryKey(SENDER, RECEIVER);
		
		when(messagesReadCursorsRepository.findById(key)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			messagesService.blockUser(SENDER, RECEIVER);
			messagesService.unblockUser(SENDER, RECEIVER);
		});
		
		verifyNoInteractions(kafkaTemplate);
	}
	
	@Test
	void successReadMessageByArrivalTimestamp()
	{
		int limit = 10;
		List<MessageEntity> message0 = List.of(new MessageEntity(SENDER, SENDER, MESSAGE, MIME, T_2, 0));
		List<MessageEntity> message1 = List.of(new MessageEntity(SENDER, RECEIVER, MESSAGE, MIME, T_2, 0));
		List<MessageEntity> message2 = List.of(new MessageEntity(SENDER, SENDER, MESSAGE, MIME, T_0, 0));
		List<MessageEntity> message3 = List.of(new MessageEntity(SENDER, RECEIVER, MESSAGE, MIME, T_0, 0));
		MessageReadCursorData cursor = new MessageReadCursorData(T_0, T_1, T_2, false, false);
		
		when(messagesProperties.getMaxBulkMessagesCount()).thenReturn(limit);
		when(messagesRepository.findMessagesSelfSent(SENDER, limit)).thenReturn(message0);
		when(messagesRepository.findMessagesSentOrReceived(SENDER, RECEIVER, limit)).thenReturn(message1);
		when(messagesRepository.findMessagesSelfSentBeforeTimeAndPart(SENDER, T_1, 1, limit)).thenReturn(message2);
		when(messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(SENDER, RECEIVER, T_1, 1, limit)).thenReturn(message3);
		when(messagesReadCursorsRepository.findById(SENDER, RECEIVER)).thenReturn(Optional.of(cursor));
		
		assertEquals(new ChatDetailsAndMessages(message0, null), messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, SENDER, null, 0));
		assertEquals(new ChatDetailsAndMessages(message1, cursor), messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, null, 0));
		assertEquals(new ChatDetailsAndMessages(message2, null), messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, SENDER, T_1, 1));
		assertEquals(new ChatDetailsAndMessages(message3, null), messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, T_1, 1));
	}
	
	@Test
	void successReadLastMessagesWithoutChat()
	{
		when(messagesProperties.getMaxBulkMessagesCount()).thenReturn(10);
		when(messagesReadCursorsRepository.findById(SENDER, RECEIVER)).thenReturn(Optional.empty());
		
		ChatDetailsAndMessages detailsAndMessages = messagesService.readChatDetailsAndMessagesByArrivalTimestamp(SENDER, RECEIVER, null, 1);
		
		MessageReadCursorData details = detailsAndMessages.details();
		assertNotNull(details);
		assertTrue(details.readTimestampOther().equals(details.readTimestampSelf()), "Timestamps are different");
		assertTrue(details.readTimestampSelf().equals(details.receiveTimestampOther()), "Timestamps are different");
		assertEquals(details.isBlockedBySelf(), false);
		assertEquals(details.isBlockedByOther(), false);
	}
	
	@Test
	void successGetChatPreviewsWhenEmpty()
	{
		when(messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(SENDER)).thenReturn(List.of());
		
		assertEquals(List.of(), messagesService.getChatsPreviews(SENDER));
	}
	
	@Test
	void successGetChatPreviewsWhenZeroDuplicates()
	{
		List<ChatPreviewData> dataWithoutDuplicates = List.of(
			chatPreviewDataOf(RECEIVER, MESSAGE),
			chatPreviewDataOf(OTHER, MESSAGE)
		);
		when(messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(SENDER)).thenReturn(dataWithoutDuplicates);
		
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
		when(messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(SENDER)).thenReturn(dataWithDuplicates);
		
		assertEquals(dataWithoutDuplicates, messagesService.getChatsPreviews(SENDER));
	}
}
