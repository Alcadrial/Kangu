package com.alcadrial.kangu.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class RealtimeService {
	
	private final ObjectMapper objectMapper;
	
	private Map<String, Map<String, Consumer<String>>> subscriptions;
	
	{
		subscriptions = new ConcurrentHashMap<>();
	}
	
	@KafkaListener(id = "${spring.application.name}", topics = "messages")
	void listenMessages(String messageParts)
	{
		for (JsonNode message : objectMapper.readTree(messageParts))
		{
			String receiver = message.get("receiver").asString(null);
			if (receiver != null)
			{
				for (Consumer<String> consumer : subscriptions.getOrDefault(receiver, Map.of()).values())
				{
					consumer.accept(messageParts);
				}
			}
		}
	}
	
	public Runnable subscribe(String id, String receiver, Consumer<String> consumer)
	{
		Map<String, Consumer<String>> ss = subscriptions.computeIfAbsent(receiver, _ -> new HashMap<>());
		ss.put(id, consumer);
		return () -> {
			ss.remove(id);
		};
	}
	
	public void unsubscribe(String id, String receiver)
	{
		Map<String, Consumer<String>> ss = subscriptions.get(receiver);
		if (ss != null) ss.remove(id);
	}
}
