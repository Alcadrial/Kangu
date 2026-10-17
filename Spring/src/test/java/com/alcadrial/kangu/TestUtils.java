package com.alcadrial.kangu;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class TestUtils {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String plainJson(String... args)
	{
		ObjectNode obj = objectMapper.createObjectNode();
		for (int i = 0; i < args.length / 2; i++) obj.put(args[2 * i], args[2 * i + 1]);
		return obj.toString();
	}
}
