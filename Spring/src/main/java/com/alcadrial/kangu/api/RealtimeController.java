package com.alcadrial.kangu.api;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.alcadrial.kangu.service.RealtimeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RealtimeController {
	
	final RealtimeService realtimeService;
	final SimpMessagingTemplate template;
	
	@EventListener
	void subscribe(SessionConnectEvent e)
	{
		Principal user = e.getUser();
		if (user == null) return;
		
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(e.getMessage());
		String id = headers.getSessionId();
		if (id == null)
		{
			log.info("ID NOT FOUND");
			return;
		}
		
		realtimeService.subscribe(id, user.getName(), message -> {
			template.convertAndSend("/app/subscribe/messages", message);
		});
	}

	//TODO: unreliable, find a better solution
	@EventListener
	void unsubscribe(SessionDisconnectEvent e)
	{
		Principal user = e.getUser();
		if (user == null) return;
		
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(e.getMessage());
		String id = headers.getSessionId();
		if (id == null) return;
		
		realtimeService.unsubscribe(id, user.getName());
	}
}
