package com.alcadrial.kangu;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.security.crypto.password.PasswordEncoder;

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
	
	public static void mockPasswordEncoder(PasswordEncoder passwordEncoder)
	{
		when(passwordEncoder.encode(any())).then(invok -> invok.getArgument(0));
		when(passwordEncoder.matches(any(), any())).then(invok -> invok.getArgument(0).equals(invok.getArgument(1)));
	}
}
