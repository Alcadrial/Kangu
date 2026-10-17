package com.alcadrial.kafkatest.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Topics {
	
	@Bean
	NewTopic topic()
	{
		return TopicBuilder.name("topic1")
			.partitions(10)
			.replicas(1)
			.build();
	}
}
