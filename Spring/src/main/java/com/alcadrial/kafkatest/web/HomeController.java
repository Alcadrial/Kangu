package com.alcadrial.kafkatest.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/")
	String home()
	{
		return "home";
	}
}
