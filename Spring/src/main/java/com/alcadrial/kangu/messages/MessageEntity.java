package com.alcadrial.kangu.messages;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.alcadrial.kangu.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @param sender The user that sent this message
 * @param receiver The user that receive this message
 * @param message The raw message, may not be the full message
 * @param mimeType The mime type of this message
 * @param sendTimestamp The timestamp at which this message was received by the server
 * @param partIndex An positive integer used to compose a message split into multiple parts to allow multiple mime types in the same message
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "messages")
@IdClass(MessageEntity.PrimaryKey.class)
public class MessageEntity {

	public static record PrimaryKey(
		String sender,
		String receiver,
		Instant sendTimestamp,
		int partIndex
	) {}
	
	@Id
	private String sender;
	@Id
	private String receiver;
	@Column
	private String message;
	@Column
	private String messageMimeType;
	@Id
	private Instant sendTimestamp;
	@Id
	private int partIndex;
	
	public MessageEntity()
	{
		this("", "", "", "", Constants.TIME_NEVER, 0);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(message, messageMimeType, Integer.valueOf(partIndex), receiver, sendTimestamp, sender);
	}

	@Override
	public boolean equals(@Nullable Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MessageEntity other = (MessageEntity) obj;
		return Objects.equals(message, other.message) && Objects.equals(messageMimeType, other.messageMimeType) && partIndex == other.partIndex && Objects.equals(receiver, other.receiver) && Objects.equals(sendTimestamp, other.sendTimestamp) && Objects.equals(sender, other.sender);
	}
}
