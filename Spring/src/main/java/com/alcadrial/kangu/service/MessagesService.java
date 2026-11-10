package com.alcadrial.kangu.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.data.MessageReadCursorEntity;
import com.alcadrial.kangu.data.MessageReadCursorEntity.PrimaryKey;
import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.EmptyMessageException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.properties.MessagesProperties;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.ChatPreviewData;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.MessageReadCursorData;
import com.alcadrial.kangu.repository.MessagesRepository;
import com.alcadrial.kangu.repository.UserRepository;
import com.alcadrial.kangu.utils.Constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MessagesService {
	
	private final UserRepository userRepository;
	private final MessagesRepository messagesRepository;
	private final MessagesReadCursorsRepository messagesReadCursorsRepository;
	private final MessagesProperties messagesProperties;
	
	public MessageEntity[] sendMessage(String sender, String receiver, String message, Instant time) throws MessageTooLongException, UserNotFoundException, BlockedException
	{
		if (message.isBlank()) throw new EmptyMessageException();
		if (message.length() > messagesProperties.getMaxMessageSize()) throw new MessageTooLongException();
		if (!userRepository.existsById(receiver)) throw new UserNotFoundException();
		Optional<Boolean> senderBlockedOptional = messagesReadCursorsRepository.isSenderBlocked(sender, receiver);
		if (senderBlockedOptional.orElse(false)) throw new BlockedException();
		
		//first message in chat
		if (senderBlockedOptional.isEmpty())
		{
			messagesReadCursorsRepository.save(new MessageReadCursorEntity(sender, receiver, Constants.TIME_NEVER, Constants.TIME_NEVER, false));
			messagesReadCursorsRepository.save(new MessageReadCursorEntity(receiver, sender, Constants.TIME_NEVER, Constants.TIME_NEVER, false));
		}
		
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
		messagesReadCursorsRepository.findById(new PrimaryKey(sender, receiver)).ifPresent(e -> e.setBlocked(true));
	}
	
	public void unblockUser(String sender, String receiver)
	{
		messagesReadCursorsRepository.findById(new PrimaryKey(sender, receiver)).ifPresent(e -> e.setBlocked(false));
	}
	
	public static record ChatDetailsAndMessages(
		List<MessageEntity> messages,
		@Nullable MessageReadCursorData details
	) {}
	
	@Transactional(readOnly = true)
	public ChatDetailsAndMessages readChatDetailsAndMessagesByArrivalTimestamp(String sender, String receiver, @Nullable Instant time, int part)
	{
		int limit = messagesProperties.getMaxBulkMessagesCount();
		
		int bit0 = time == null ? 1 : 0;
		int bit1 = sender.equals(receiver) ? 1 : 0;
		int key = bit1 << 1 | bit0;
		
		List<MessageEntity> messages = switch (key) {
			case 0 -> messagesRepository.findMessagesSentOrReceivedBeforeTimeAndPart(sender, receiver, (@NonNull Instant) time, part, limit);
			case 1 -> messagesRepository.findMessagesSentOrReceived(sender, receiver, limit);
			case 2 -> messagesRepository.findMessagesSelfSentBeforeTimeAndPart(sender, (@NonNull Instant) time, part, limit);
			case 3 -> messagesRepository.findMessagesSelfSent(sender, limit);
			default -> throw new IllegalArgumentException("Unexpected value: " + key);
		};
		
		if (key != 1) return new ChatDetailsAndMessages(messages, null);
		
		Instant currentTime = Instant.now();
		MessageReadCursorData data = messagesReadCursorsRepository.findById(sender, receiver).orElse(new MessageReadCursorData(currentTime, currentTime, currentTime, false, false));
		
		return new ChatDetailsAndMessages(messages, data);
	}
	

	@Transactional(readOnly = true)
	public List<ChatPreviewData> getChatsPreviews(String username)
	{
		List<ChatPreviewData> dataWithDuplicates = messagesReadCursorsRepository.getChatPreviewDataWithDuplicates(username);
		HashMap<String, ChatPreviewData> uniqueChats = new HashMap<>();
		for (ChatPreviewData chat : dataWithDuplicates)
		{
			uniqueChats.merge(chat.username(), chat, (v0, v1) -> v0.messageTimestamp().isBefore(v1.messageTimestamp()) ? v1 : v0);
		}
		
		return List.copyOf(uniqueChats.values());
	}
}
