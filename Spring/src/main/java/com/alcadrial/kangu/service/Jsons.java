package com.alcadrial.kangu.service;

import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class Jsons {
	
	private JsonNode invalidPayload;
	private JsonNode userNotFound;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	Jsons() {
		invalidPayload = errorJson(objectMapper).put("error", "Invalid Payload");
		userNotFound = errorJson(objectMapper).put("error", "User not found");
	}
	
	ObjectNode errorJson(ObjectMapper mapper)
	{
		return mapper.createObjectNode().put("", "");
	}

	public JsonNode invalidPayloadError()
	{
		return invalidPayload;
	}
	
	public JsonNode userNotFoundError()
	{
		return userNotFound;
	}
}
