package com.alcadrial.kangu.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.http.MediaType;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.data.MessageReadCursorEntity;
import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.ChatPreviewData;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.MessageReadCursorData;

import jakarta.persistence.EntityManager;

@DataJpaTest
class MessagesReadCursorsRepositoryTest {

	static final UserEntity SENDER_USER = TestConstants.USER;
	static final UserEntity RECEIVER_USER = TestConstants.USER1;
	static final UserEntity OTHER_USER = TestConstants.USER2;
	static final String SENDER = SENDER_USER.getUsername();
	static final String RECEIVER = RECEIVER_USER.getUsername();
	static final String OTHER = OTHER_USER.getUsername();
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
	
	@Autowired
	private MessagesReadCursorsRepository messagesReadCursorsRepository;
	@Autowired
	private EntityManager entityManager;
	
	private MessageEntity messageOf(String sender, String receiver, String message, Instant time, int part)
	{
		return new MessageEntity(sender, receiver, message, MediaType.TEXT_PLAIN_VALUE, time, part);
	}
	
	@BeforeEach
	void prepare()
	{
		entityManager.persist(SENDER_USER);
		entityManager.persist(RECEIVER_USER);
		entityManager.persist(OTHER_USER);
		
		entityManager.persist(messageOf(SENDER, RECEIVER, MESSAGE, T_0, 0));
		entityManager.persist(messageOf(RECEIVER, SENDER, MESSAGE1, T_1, 0));
		entityManager.persist(messageOf(SENDER, RECEIVER, MESSAGE2, T_2, 0));
		entityManager.persist(messageOf(SENDER, SENDER, MESSAGE3, T_3, 0));
		entityManager.persist(messageOf(SENDER, SENDER, MESSAGE4, T_3, 1));
		entityManager.persist(messageOf(SENDER, SENDER, MESSAGE5, T_3, 2));
		entityManager.persist(messageOf(RECEIVER, SENDER, MESSAGE6, T_4, 0));
		entityManager.persist(messageOf(SENDER, RECEIVER, MESSAGE7, T_5, 0));

		entityManager.persist(new MessageReadCursorEntity(SENDER, RECEIVER, T_6, T_5, false));
		entityManager.persist(new MessageReadCursorEntity(RECEIVER, SENDER, T_8, T_7, true));
		entityManager.persist(new MessageReadCursorEntity(SENDER, SENDER, T_4, T_4, false));
	}
	
	@Test
	void findByIdCursorData()
	{
		assertEquals(new MessageReadCursorData(T_6, T_7, T_5, true, false), messagesReadCursorsRepository.findById(SENDER, RECEIVER).orElse(null));
		assertEquals(new MessageReadCursorData(T_8, T_5, T_7, false, true), messagesReadCursorsRepository.findById(RECEIVER, SENDER).orElse(null));
	}
	
	@Test
	void updateBlockedStatus()
	{
		messagesReadCursorsRepository.updateBlockedStatus(SENDER, RECEIVER, true);
		MessageReadCursorData cursor = messagesReadCursorsRepository.findById(SENDER, RECEIVER).orElse(null);
		assertNotNull(cursor);
		assertEquals(true, cursor.isBlockedBySelf());
		assertEquals(true, cursor.isBlockedByOther());
		
		messagesReadCursorsRepository.updateBlockedStatus(SENDER, RECEIVER, false);
		cursor = messagesReadCursorsRepository.findById(SENDER, RECEIVER).orElse(null);
		assertNotNull(cursor);
		assertEquals(true, cursor.isBlockedBySelf());
		assertEquals(false, cursor.isBlockedByOther());
	}
	
	@Test
	void isBlockedStatus()
	{
		assertEquals(false, messagesReadCursorsRepository.isSenderBlocked(SENDER, RECEIVER).orElse(null));
		assertEquals(true, messagesReadCursorsRepository.isSenderBlocked(RECEIVER, SENDER).orElse(null));
		assertEquals(Optional.empty(), messagesReadCursorsRepository.isSenderBlocked(RECEIVER, RECEIVER));
		assertEquals(false, messagesReadCursorsRepository.isSenderBlocked(SENDER, SENDER).orElse(null));
	}
	
	@Test
	void getChatPreview()
	{
		assertEquals(List.of(), messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(OTHER));
		assertEquals(List.of(
			new ChatPreviewData(SENDER, MESSAGE7, MediaType.TEXT_PLAIN_VALUE, T_5, T_8, T_5, T_7, false, true),
			new ChatPreviewData(SENDER, MESSAGE6, MediaType.TEXT_PLAIN_VALUE, T_4, T_8, T_5, T_7, false, true)
		), messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(RECEIVER));
		assertEquals(List.of(
			new ChatPreviewData(RECEIVER, MESSAGE7, MediaType.TEXT_PLAIN_VALUE, T_5, T_6, T_7, T_5, true, false),
			new ChatPreviewData(RECEIVER, MESSAGE6, MediaType.TEXT_PLAIN_VALUE, T_4, T_6, T_7, T_5, true, false),
			new ChatPreviewData(SENDER, MESSAGE5, MediaType.TEXT_PLAIN_VALUE, T_3, T_4, T_4, T_4, false, false)
		), messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(SENDER));
	}
}
