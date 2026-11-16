package com.alcadrial.kangu.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry)
	{
		registry.addEndpoint("/api/socket")
			.setAllowedOrigins("*");
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config)
	{
		//TODO: use a bean with @Autowired
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();
		
		config.setApplicationDestinationPrefixes("/app");
		config.enableSimpleBroker("/topic", "/queue")
			.setTaskScheduler(taskScheduler);
	}
}