package com.alcadrial.kangu.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.http.MediaType;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.repository.MessagesRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
class MessagesRepositoryTest {

	static final UserEntity SENDER_USER = TestConstants.USER;
	static final UserEntity RECEIVER_USER = TestConstants.USER1;
	static final String SENDER = SENDER_USER.getUsername();
	static final String RECEIVER = RECEIVER_USER.getUsername();
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
	private MessagesRepository messagesRepository;
	@Autowired
	private EntityManager entityManager;
	
	private MessageEntity messageOf(String sender, String receiver, String message, Instant time, int part)
	{
		return new MessageEntity(sender, receiver, message, MediaType.TEXT_PLAIN_VALUE, time, part);
	}
	
	private void assertMessages(List<String> expected, List<MessageEntity> found)
	{
		assertEquals(expected, found.stream().map(MessageEntity::getMessage).toList());
	}
	
	@BeforeEach
	void prepare()
	{
		entityManager.persist(SENDER_USER);
		entityManager.persist(RECEIVER_USER);
		
		entityManager.persist(messageOf(SENDER, RECEIVER, MESSAGE, T_0, 0));
		entityManager.persist(messageOf(RECEIVER, SENDER, MESSAGE1, T_1, 0));
		entityManager.persist(messageOf(SENDER, RECEIVER, MESSAGE2, T_2, 0));
		entityManager.persist(messageOf(SENDER, SENDER, MESSAGE3, T_3, 0));
		entityManager.persist(messageOf(SENDER, SENDER, MESSAGE4, T_3, 1));
		entityManager.persist(messageOf(SENDER, SENDER, MESSAGE5, T_3, 2));
		entityManager.persist(messageOf(RECEIVER, SENDER, MESSAGE6, T_4, 0));
		entityManager.persist(messageOf(SENDER, RECEIVER, MESSAGE7, T_5, 0));
	}
	
	@Test
	void findMessagesSentBeforeTimeAndPart()
	{
		assertMessages(List.of(MESSAGE7, MESSAGE2), messagesRepository.findMessagesSentBeforeTimeAndPart(SENDER, RECEIVER, T_LAST, 0, 2));
		assertMessages(List.of(MESSAGE2), messagesRepository.findMessagesSentBeforeTimeAndPart(SENDER, RECEIVER, T_3, 0, 1));
		assertMessages(List.of(MESSAGE2, MESSAGE), messagesRepository.findMessagesSentBeforeTimeAndPart(SENDER, RECEIVER, T_3, 0, 2));
		assertMessages(List.of(MESSAGE4, MESSAGE3), messagesRepository.findMessagesSentBeforeTimeAndPart(SENDER, SENDER, T_3, 2, 5));
	}
	
	@Test
	void findMessagesSentOrReceivedBeforeTimeAndPart()
	{
		assertMessages(List.of(MESSAGE7, MESSAGE6), messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(SENDER, RECEIVER, T_LAST, 0, 2));
		assertMessages(List.of(MESSAGE7, MESSAGE6), messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(RECEIVER, SENDER, T_LAST, 0, 2));
		assertMessages(List.of(MESSAGE2), messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(SENDER, RECEIVER, T_3, 0, 1));
		assertMessages(List.of(MESSAGE2), messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(RECEIVER, SENDER, T_3, 0, 1));
		assertMessages(List.of(MESSAGE2, MESSAGE1), messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(SENDER, RECEIVER, T_3, 0, 2));
		assertMessages(List.of(MESSAGE2, MESSAGE1), messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(RECEIVER, SENDER, T_3, 0, 2));
	}
	
	@Test
	void findMessagesSelfSentBeforeTimeAndPart()
	{
		assertMessages(List.of(MESSAGE5, MESSAGE4), messagesRepository.findMessagesSelfSentBeforeTimeAndPart(SENDER, T_LAST, 0, 2));
		assertMessages(List.of(), messagesRepository.findMessagesSelfSentBeforeTimeAndPart(SENDER, T_3, 0, 1));
		assertMessages(List.of(MESSAGE3), messagesRepository.findMessagesSelfSentBeforeTimeAndPart(SENDER, T_3, 1, 2));
		assertMessages(List.of(), messagesRepository.findMessagesSelfSentBeforeTimeAndPart(RECEIVER, T_3, 2, 5));
	}
	
	@Test
	void findMessagesSent()
	{
		assertMessages(List.of(MESSAGE7, MESSAGE2), messagesRepository.findMessagesSent(SENDER, RECEIVER, 2));
		assertMessages(List.of(MESSAGE6, MESSAGE1), messagesRepository.findMessagesSent(RECEIVER, SENDER, 5));
		assertMessages(List.of(MESSAGE5, MESSAGE4), messagesRepository.findMessagesSent(SENDER, SENDER, 2));
		assertMessages(List.of(), messagesRepository.findMessagesSent(RECEIVER, RECEIVER, 5));
	}
	
	@Test
	void findMessagesSentOrReceived()
	{
		assertMessages(List.of(MESSAGE7, MESSAGE6, MESSAGE2), messagesRepository.findMessagesSentOrReceived(SENDER, RECEIVER, 3));
		assertMessages(List.of(MESSAGE7, MESSAGE6, MESSAGE2), messagesRepository.findMessagesSentOrReceived(RECEIVER, SENDER, 3));
		assertMessages(List.of(MESSAGE7, MESSAGE6, MESSAGE2, MESSAGE1), messagesRepository.findMessagesSentOrReceived(SENDER, RECEIVER, 4));
		assertMessages(List.of(MESSAGE7, MESSAGE6, MESSAGE2, MESSAGE1), messagesRepository.findMessagesSentOrReceived(RECEIVER, SENDER, 4));
	}
	
	@Test
	void findMessagesSelfSent()
	{
		assertMessages(List.of(MESSAGE5, MESSAGE4, MESSAGE3), messagesRepository.findMessagesSelfSent(SENDER, 3));
		assertMessages(List.of(), messagesRepository.findMessagesSelfSent(RECEIVER, 3));
	}
	
}
