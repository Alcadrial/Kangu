package com.alcadrial.kangu.service;

import java.time.LocalDateTime;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.properties.MessagesProperties;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.MessageReadCursorData;
import com.alcadrial.kangu.repository.MessagesRepository;
import com.alcadrial.kangu.repository.MessagesRepository.Message;
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
	
	public MessageEntity[] sendMessage(String sender, String receiver, String message, LocalDateTime time) throws MessageTooLongException, UserNotFoundException, BlockedException
	{
		if (message.length() > messagesProperties.getMaxMessageSize()) throw new MessageTooLongException();
		if (!userRepository.existsById(receiver)) throw new UserNotFoundException();
		if (messagesReadCursorsRepository.isSenderBlocked(sender, receiver)) throw new BlockedException();
		
		int blockSize = messagesProperties.getMaxMessageBlockSize();
		MessageEntity[] blocks = new MessageEntity[Math.ceilDiv(message.length(), blockSize)];
		int partIndex = 0;
		while (blockSize < message.length())
		{
			String block = message.substring(0, blockSize);
			blocks[partIndex] = messagesRepository.save(new MessageEntity(sender, receiver, block, MediaType.TEXT_PLAIN_VALUE, time, partIndex++));
			message = message.substring(blockSize);
		}
		blocks[partIndex] = messagesRepository.save(new MessageEntity(sender, receiver, message, MediaType.TEXT_PLAIN_VALUE, time, partIndex));
		return blocks;
	}
	
	public void blockUser(String sender, String receiver)
	{
		messagesReadCursorsRepository.updateBlockedStatus(sender, receiver, true);
	}
	
	public void unblockUser(String sender, String receiver)
	{
		messagesReadCursorsRepository.updateBlockedStatus(sender, receiver, false);
	}
	
	public static record ChatDetailsAndMessages(
		List<Message> messages,
		@Nullable MessageReadCursorData details
	) {}
	
	public ChatDetailsAndMessages readChatDetailsAndMessagesByArrivalTimestamp(String sender, String receiver, @Nullable LocalDateTime time, int part)
	{
		int limit = messagesProperties.getMaxBulkMessagesCount();
		
		int bit0 = time == null ? 1 : 0;
		int bit1 = sender.equals(receiver) ? 1 : 0;
		int key = bit1 << 1 | bit0;
		
		List<Message> messages = switch (key) {
			case 0 -> messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(sender, receiver, (@NonNull LocalDateTime) time, part, limit);
			case 1 -> messagesRepository.findMessagesSentOrReceived(sender, receiver, limit);
			case 2 -> messagesRepository.findMessagesSelfSentBeforeTimeAndPart(sender, (@NonNull LocalDateTime) time, part, limit);
			case 3 -> messagesRepository.findMessagesSelfSent(sender, limit);
			default -> throw new IllegalArgumentException("Unexpected value: " + key);
		};
		
		if (key != 0) return new ChatDetailsAndMessages(messages, null);
		
		MessageReadCursorData data = messagesReadCursorsRepository.findById(sender, receiver);
		if (data == null) throw new IllegalStateException("Missing read cursor of chat between " + sender + " and " + receiver);
		
		return new ChatDetailsAndMessages(messages, data);
	}
	
	public List<MessageReadCursorData> readChatsOfUser(String username)
	{
		return messagesReadCursorsRepository.findAllUserChats(username);
	}
}
