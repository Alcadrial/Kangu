package com.alcadrial.kangu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.exception.DisabledServiceException;
import com.alcadrial.kangu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserEntity.Safe get(String username)
	{
		return userRepository.findByUsername(username).orElseThrow();
	}
	
	public void changeEmail(String username, String newEmail)
	{
		throw new DisabledServiceException();
	}
}
