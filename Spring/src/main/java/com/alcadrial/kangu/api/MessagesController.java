package com.alcadrial.kangu.api;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alcadrial.kangu.data.MessageEntity;
import com.alcadrial.kangu.repository.MessagesReadCursorsRepository.ChatPreviewData;
import com.alcadrial.kangu.service.MessagesService;
import com.alcadrial.kangu.service.MessagesService.ChatDetailsAndMessages;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessagesController {
	
	final MessagesService messagesService;
	
	@PostMapping("/send")
	ResponseEntity<MessageEntity[]> postSend(@AuthenticationPrincipal User user, @RequestBody MessageData body) throws Exception
	{
		MessageEntity[] message = messagesService.sendMessage(user.getUsername(), body.receiver(), body.message(), Instant.now());
		return ResponseEntity.ok(message);
	}
	
	record MessageData(String receiver, String message) {}
	
	@GetMapping("/get")
	ResponseEntity<ChatDetailsAndMessages> getMessages(@AuthenticationPrincipal User user, @RequestParam("user") String otherUsername, @RequestParam(value = "before", required = false) @Nullable Instant before, @RequestParam(value = "part", defaultValue = "0") int part)
	{
		ChatDetailsAndMessages messages = messagesService.readChatDetailsAndMessagesByArrivalTimestamp(user.getUsername(), otherUsername, before, part);
		return ResponseEntity.ok(messages);
	}
	
	@GetMapping("/chats")
	ResponseEntity<List<ChatPreviewData>> getChats(@AuthenticationPrincipal User user)
	{
		List<ChatPreviewData> messages = messagesService.getChatsPreviews(user.getUsername());
		return ResponseEntity.ok(messages);
	}
}
