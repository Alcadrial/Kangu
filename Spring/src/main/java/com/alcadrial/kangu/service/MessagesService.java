package com.alcadrial.kangu.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcadrial.kangu.data.MessageLookupEntity;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.properties.MessagesProperties;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesRepository;
import com.alcadrial.kangu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MessagesService {
	
	private final UserRepository userRepository;
	private final MessagesRepository messagesRepository;
	private final MessagesReadCursorsRepository messagesReadCursorsRepository;
	private final MessagesProperties messagesProperties;
	
	public void sendMessage(String sender, String receiver, String message, LocalDateTime time) throws MessageTooLongException, UserNotFoundException, BlockedException
	{
		if (message.length() > messagesProperties.getMaxMessageSize()) throw new MessageTooLongException();
		if (!userRepository.existsById(receiver)) throw new UserNotFoundException();
		if (messagesReadCursorsRepository.isSenderBlocked(sender, receiver)) throw new BlockedException();
		
		int blockSize = messagesProperties.getMaxMessageBlockSize();
		int partIndex = 0;
		while (blockSize < message.length())
		{
			String block = message.substring(0, blockSize);
			messagesRepository.save(new MessageLookupEntity(sender, receiver, block, time, partIndex++));
			message = message.substring(blockSize);
		}
		messagesRepository.save(new MessageLookupEntity(sender, receiver, message, time, partIndex));
	}
	
	public void blockUser(String sender, String receiver)
	{
		messagesReadCursorsRepository.updateBlockedStatus(sender, receiver, true);
	}
	
	public void unblockUser(String sender, String receiver)
	{
		messagesReadCursorsRepository.updateBlockedStatus(sender, receiver, false);
	}
	
	public void readMessageByArrivalTimestamp(String sender, String receiver, LocalDateTime time)
	{
		int limit = messagesProperties.getMaxBulkMessagesCount();
		messagesRepository.findMessagesFromSentBeforeTime(receiver, receiver, time, limit);
	}
}
