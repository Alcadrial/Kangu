package com.alcadrial.kafkatest.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class Listeners {
	
	@KafkaListener(id = "myId", topics = "topic1")
	void listen(String in)
	{
		System.out.println(in);
	}
}
