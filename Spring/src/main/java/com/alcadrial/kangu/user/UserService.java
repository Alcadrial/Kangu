package com.alcadrial.kangu.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserEntity get(String username)
	{
		return userRepository.findById(username).orElseThrow();
	}
}
