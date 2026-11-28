package com.alcadrial.kangu.user;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.alcadrial.kangu.utils.Constants;
import com.alcadrial.kangu.utils.OnlineStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @param username
 * @param email
 * @param registrationTime
 * @param lastAccessTime
 * @param lastActionTime
 * @param onlineStatus
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users_view")
public class UserEntity {
	
	@Id
	private String username;
	@Column
	private String email;
	@Column
	private Instant registrationTime;
	@Column
	private Instant lastAccessTime;
	@Column
	private Instant lastActionTime;
	@Column
	@Enumerated
	private OnlineStatus onlineStatus;
	
	public UserEntity()
	{
		this("", "");
	}
	
	public UserEntity(String username, String password)
	{
		this(username, password, Constants.TIME_NEVER, Constants.TIME_NEVER, Constants.TIME_NEVER, OnlineStatus.ON);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(email, lastAccessTime, lastActionTime, onlineStatus, registrationTime, username);
	}
	
	@Override
	public boolean equals(@Nullable Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		UserEntity other = (UserEntity) obj;
		return Objects.equals(email, other.email) && Objects.equals(lastAccessTime, other.lastAccessTime) && Objects.equals(lastActionTime, other.lastActionTime) && onlineStatus == other.onlineStatus && Objects.equals(registrationTime, other.registrationTime) && Objects.equals(username, other.username);
	}
}
