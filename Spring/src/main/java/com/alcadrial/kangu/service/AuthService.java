package com.alcadrial.kangu.service;

import org.springframework.stereotype.Service;

import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.exception.DuplicateKeyException;

import jakarta.persistence.EntityNotFoundException;

@Service
public interface AuthService {
	
	UserEntity register(String username, String email, String password) throws DuplicateKeyException;
	UserEntity login(String username, String password) throws EntityNotFoundException;
}
