package com.alcadrial.kangu.data;

import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users_view")
public class UserEntity {
	
	@Id
	private @NonNull String username;
	@Column
	private @NonNull String password;
	@Column
	private @NonNull String email;
	@Column
	private boolean enabled;
	@Column
	private @NonNull LocalDateTime registrationTime;
	@Column
	private @NonNull LocalDateTime lastAccessTime;
	@Column
	private @NonNull LocalDateTime lastActionTime;
	@Column
	@Enumerated
	private @NonNull OnlineStatus onlineStatus;
	
	public UserEntity()
	{
		this("", "", "");
	}
	
	public UserEntity(String username, String password, String email)
	{
		this(username, password, email, true, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), OnlineStatus.ON);
	}
	
	public static record Safe(
		@NonNull String username,
		@NonNull String email,
		@NonNull LocalDateTime registrationTime,
		@NonNull LocalDateTime lastAccessTime,
		@NonNull LocalDateTime lastActionTime,
		@NonNull OnlineStatus onlineStatus
	) {}
}
