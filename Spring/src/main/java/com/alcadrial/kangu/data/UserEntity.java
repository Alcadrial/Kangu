package com.alcadrial.kangu.data;

import org.jspecify.annotations.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
	
	@Id
	private @NonNull String username;
	@Column
	private @NonNull String password;
	@Column
	private @NonNull String email;
	@Column
	private boolean enabled;
	
	public UserEntity()
	{
		this("", "", "", false);
	}
}
