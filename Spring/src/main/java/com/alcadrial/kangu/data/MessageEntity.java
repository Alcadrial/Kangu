package com.alcadrial.kangu.data;

import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "messages")
@IdClass(MessageEntity.PrimaryKey.class)
public class MessageEntity {
	
	public static record PrimaryKey(
		@NonNull String sender,
		@NonNull String receiver,
		@NonNull LocalDateTime sendTimestamp,
		int partIndex
	) {}
	
	@Id
	@ManyToOne(optional = false)
	private @NonNull UserEntity sender;
	@Id
	@ManyToOne(optional = false)
	private @NonNull UserEntity receiver;
	@Column
	private @NonNull String senderEncMessage;
	@Column
	private @NonNull String receiverEncMessage;
	@Id
	private @NonNull LocalDateTime sendTimestamp;
	@Id
	private int partIndex;
	
	public MessageEntity()
	{
		this(new UserEntity(), new UserEntity(), "", "", LocalDateTime.of(2026, 1, 1, 0, 0), 0);
	}
}
