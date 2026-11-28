package com.alcadrial.kangu.messages;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@ConfigurationProperties("kangu.messages")
@Configuration
public class MessagesProperties {

	/**
	 * The maximum allowed length of a message accepted by {@link MessagesService}
	 */
	int maxMessageSize = 4000;
	/**
	 * The maximum allowed length of a block of a message allowed by the database,
	 * if it is larger than {@link MessagesProperties#maxMessageSize maxMessageSize} then
	 * the message will be saving in only 1 record
	 */
	int maxMessageBlockSize = 500;
	/**
	 * The maximum number of messages to send in a single response
	 */
	int maxBulkMessagesCount = 50;
}
