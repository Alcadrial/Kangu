package com.alcadrial.kangu.data;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.alcadrial.kangu.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @param sender The user that sent this message
 * @param receiver The user that receive this message
 * @param message The raw message, may not be the full message
 * @param mimeType The mime type of this message
 * @param sendTimestamp The timestamp at which this message was received by the server
 * @param partIndex An positive integer used to compose a message split into multiple parts to allow multiple mime types in the same message
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "messages")
@IdClass(MessageEntity.PrimaryKey.class)
public class MessageEntity {

	public static record PrimaryKey(
		@NonNull String sender,
		@NonNull String receiver,
		@NonNull Instant sendTimestamp,
		int partIndex
	) {}
	
	@Id
	private @NonNull String sender;
	@Id
	private @NonNull String receiver;
	@Column
	private @NonNull String message;
	@Column
	private @NonNull String messageMimeType;
	@Id
	private @NonNull Instant sendTimestamp;
	@Id
	private int partIndex;
	
	public MessageEntity()
	{
		this("", "", "", "", Constants.TIME_NEVER, 0);
	}
}
