package com.alcadrial.kangu.repository;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.alcadrial.kangu.data.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, String> {
	
	@Nullable
	UserEntity findByUsernameAndPasswordHash(String username, String passwordHash);
}
