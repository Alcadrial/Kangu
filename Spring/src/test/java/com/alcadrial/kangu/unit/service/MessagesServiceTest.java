package com.alcadrial.kangu.unit.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.properties.MessagesProperties;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesRepository;
import com.alcadrial.kangu.repository.UserRepository;
import com.alcadrial.kangu.service.MessagesService;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {
	
	static final String USERNAME = TestConstants.USERNAME;
	static final String EMAIL = TestConstants.EMAIL;
	static final String PASSWORD = TestConstants.PASSWORD;

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
	
}
