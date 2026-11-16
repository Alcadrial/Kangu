package com.alcadrial.kangu.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import com.alcadrial.kangu.utils.Constants;

@EnableKafka
@Configuration
public class Topics {
	
	@Bean
	NewTopic messages()
	{
		return TopicBuilder.name(Constants.MESSAGES_TOPIC)
			.partitions(10)
			.replicas(1)
			.build();
	}
	
	@Bean
	NewTopic cursors()
	{
		return TopicBuilder.name(Constants.CURSORS_TOPIC)
			.partitions(10)
			.replicas(1)
			.build();
	}
}
