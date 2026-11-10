package com.alcadrial.kangu.data;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.alcadrial.kangu.utils.Constants;

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
	private @NonNull Instant registrationTime;
	@Column
	private @NonNull Instant lastAccessTime;
	@Column
	private @NonNull Instant lastActionTime;
	@Column
	@Enumerated
	private @NonNull OnlineStatus onlineStatus;
	
	public UserEntity()
	{
		this("", "", "");
	}
	
	public UserEntity(String username, String password, String email)
	{
		this(username, password, email, true, Constants.TIME_NEVER, Constants.TIME_NEVER, Constants.TIME_NEVER, OnlineStatus.ON);
	}
	
	public static record Safe(
		@NonNull String username,
		@NonNull String email,
		@NonNull Instant registrationTime,
		@NonNull Instant lastAccessTime,
		@NonNull Instant lastActionTime,
		@NonNull OnlineStatus onlineStatus
	) {}
}
