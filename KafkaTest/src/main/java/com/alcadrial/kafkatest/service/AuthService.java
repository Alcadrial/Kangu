package com.alcadrial.kafkatest.service;

import org.springframework.stereotype.Service;

import com.alcadrial.kafkatest.data.UserEntity;
import com.alcadrial.kafkatest.exception.DuplicateKeyException;

import jakarta.persistence.EntityNotFoundException;

@Service
public interface AuthService {
	
	UserEntity register(String username, String email, String password) throws DuplicateKeyException;
	UserEntity login(String username, String password) throws EntityNotFoundException;
}
