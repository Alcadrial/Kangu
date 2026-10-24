package com.alcadrial.kangu.service;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.exception.DisabledException;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.properties.RegistrationProperties;
import com.alcadrial.kangu.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
	
	private final UserRepository userRepository;
	private final UserDetailsManager userDetailsManager;
	private final GroupManager groupManager;
	private final UserDetailsPasswordService passwordService;
	private final PasswordEncoder passwordEncoder;
	private final RegistrationProperties properties;
	
	public void register(String username, String email, String password) throws DuplicateKeyException, DisabledException
	{
		if (!properties.isEnabled()) throw new DisabledException();
		
		String passwordHash = (@NonNull String) passwordEncoder.encode(password);
		
		if (userDetailsManager.userExists(username)) throw new DuplicateKeyException("User already exists");
		
		userRepository.createNew(username, passwordHash, email, true);
		groupManager.addUserToGroup(username, "users");
	}
	
	public UserEntity login(String username, String password, boolean rememberMe) throws EntityNotFoundException
	{
		try
		{
			UserDetails user = userDetailsManager.loadUserByUsername(username);
			
			if (!passwordEncoder.matches(password, user.getPassword())) throw new EntityNotFoundException("Wrong password");
			return userRepository.findById(username).get();
		}
		catch (UsernameNotFoundException _)
		{
			//avoid timing attacks
			passwordEncoder.matches(password, password);
			throw new EntityNotFoundException("Username not found");
		}
	}
	
	public void changePassword(String username, String oldPassword, String newPassword)
	{
		try
		{
			UserDetails user = userDetailsManager.loadUserByUsername(username);
			
			if (!passwordEncoder.matches(oldPassword, user.getPassword())) throw new EntityNotFoundException("Wrong username or password");
			
			passwordService.updatePassword(user, newPassword);
		}
		catch (UsernameNotFoundException _)
		{
			//avoid timing attacks
			passwordEncoder.matches(oldPassword, oldPassword);
		}
	}
	
	public void deleteUserIfPasswordMatches(String username, String password)
	{
		Optional<UserEntity> optionalUser = userRepository.findById(username);
		if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword()))
		{
			userDetailsManager.deleteUser(username);
		}
	}
	
	public void deleteUser(String username)
	{
		userDetailsManager.deleteUser(username);
	}
}
