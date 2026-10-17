package com.alcadrial.kafkatest.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/send")
@RestController
public class SendController {
	
	@PostMapping
	void sendMessage(@RequestParam("message") String message)
	{
		System.out.println("Message " + message);
	}
	
	@GetMapping
	String getSendMessage()
	{
		return "redirect:home";
	}
}
