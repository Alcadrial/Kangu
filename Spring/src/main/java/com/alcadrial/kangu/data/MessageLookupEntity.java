package com.alcadrial.kangu.data;

import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;

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
@Table(name = "messages")
@IdClass(MessageLookupEntity.PrimaryKey.class)
public class MessageLookupEntity {

	public static record PrimaryKey(
		@NonNull String sender,
		@NonNull String receiver,
		@NonNull LocalDateTime sendTimestamp,
		int partIndex
	) {}
	
	@Id
	private @NonNull String sender;
	@Id
	private @NonNull String receiver;
	@Column
	private @NonNull String message;
	@Id
	private @NonNull LocalDateTime sendTimestamp;
	@Id
	private int partIndex;
	
	public MessageLookupEntity()
	{
		this("", "", "", LocalDateTime.of(2026, 1, 1, 0, 0, 0, 0), 0);
	}
}
