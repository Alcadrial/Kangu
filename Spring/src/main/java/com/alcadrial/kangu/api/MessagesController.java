package com.alcadrial.kangu.api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
import com.alcadrial.kangu.repository.MessagesRepository.Message;
import com.alcadrial.kangu.service.MessagesService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessagesController {
	
	final MessagesService messagesService;
	
	@PostMapping("/send")
	ResponseEntity<?> postSend(@AuthenticationPrincipal User user, @RequestBody MessageData body) throws Exception
	{
		MessageEntity[] message = messagesService.sendMessage(user.getUsername(), body.receiver(), body.message(), LocalDateTime.now(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))));
		return ResponseEntity.ok(message);
	}
	
	record MessageData(String receiver, String message) {}
	
	@GetMapping("/get")
	ResponseEntity<?> getMessages(@AuthenticationPrincipal User user, @RequestParam("user") String otherUsername, @RequestParam(value = "before", required = false) @Nullable LocalDateTime before, @RequestParam(value = "part", defaultValue = "0") int part)
	{
		List<Message> messages = messagesService.readMessageByArrivalTimestamp(user.getUsername(), otherUsername, before, part);
		
		return ResponseEntity.ok(messages);
	}
}
