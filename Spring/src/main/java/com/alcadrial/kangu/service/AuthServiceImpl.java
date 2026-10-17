package com.alcadrial.kangu.service;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Primary
@Component
public class AuthServiceImpl implements AuthService {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserEntity register(String username, String email, String password) throws DuplicateKeyException
	{
		String passwordHash = (@NonNull String) passwordEncoder.encode(password);
		
		UserEntity user = userRepository.findById(username).orElse(null);
		
		if (user != null) throw new DuplicateKeyException("User already exists");
		
		return userRepository.save(new UserEntity(username, email, passwordHash));
	}
	
	@Override
	public UserEntity login(String username, String password) throws EntityNotFoundException
	{
		String passwordHash = (@NonNull String) passwordEncoder.encode(password);
		
		UserEntity user = userRepository.findById(username).orElse(null);
		
		if (user == null)
		{
			//avoid timing attacks
			passwordEncoder.matches(password + "0", passwordHash);
			throw new EntityNotFoundException("Wrong username or password");
		}
		if (!passwordEncoder.matches(password, user.getPasswordHash())) throw new EntityNotFoundException("Wrong username or password");
		return user;
	}
}
