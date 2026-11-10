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

@Data
@AllArgsConstructor
@Entity
@Table(name = "messages_read_cursors")
@IdClass(MessageReadCursorEntity.PrimaryKey.class)
public class MessageReadCursorEntity {

	public static record PrimaryKey(
		@NonNull String sender,
		@NonNull String receiver
	) {}
	
	@Id
	private @NonNull String sender;
	@Id
	private @NonNull String receiver;
	@Column
	private @NonNull Instant receiveTimestamp;
	@Column
	private @NonNull Instant readTimestamp;
	@Column
	private boolean isBlocked;
	
	public MessageReadCursorEntity()
	{
		this("", "", Constants.TIME_NEVER, Constants.TIME_NEVER, false);
	}
}
