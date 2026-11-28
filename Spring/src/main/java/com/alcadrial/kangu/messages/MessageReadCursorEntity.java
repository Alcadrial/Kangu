package com.alcadrial.kangu.messages;

import java.time.Instant;

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
 * @param sender
 * @param receiver
 * @param receiveTimestamp
 * @param readTimestamp
 * @param isBlocked
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "messages_read_cursors")
@IdClass(MessageReadCursorEntity.PrimaryKey.class)
public class MessageReadCursorEntity {
	
	public static record PrimaryKey(
		String sender,
		String receiver
	) {}
	
	@Id
	private String sender;
	@Id
	private String receiver;
	@Column
	private Instant receiveTimestamp;
	@Column
	private Instant readTimestamp;
	@Column
	private boolean isBlocked;
	
	public MessageReadCursorEntity()
	{
		this("", "", Constants.TIME_NEVER, Constants.TIME_NEVER, false);
	}
}
