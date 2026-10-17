package com.alcadrial.kafkatest.service;

import org.springframework.stereotype.Component;

import com.alcadrial.kafkatest.data.UserEntity;
import com.alcadrial.kafkatest.exception.DuplicateKeyException;

import jakarta.persistence.EntityNotFoundException;

@Component
public class AuthServiceMock implements AuthService {
	
	@Override
	public UserEntity register(String username, String email, String password) throws DuplicateKeyException
	{
		return new UserEntity(username, email, password);
	}
	
	@Override
	public UserEntity login(String username, String password) throws EntityNotFoundException
	{
		throw new EntityNotFoundException("User not found");
	}
}
