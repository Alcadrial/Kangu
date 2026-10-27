package com.alcadrial.kangu.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.exception.DisabledException;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.properties.RegistrationProperties;
import com.alcadrial.kangu.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;
	
	public void register(String username, String email, String password) throws DuplicateKeyException, DisabledException
	{
		if (!properties.isEnabled()) throw new DisabledException();
		
		String passwordHash = (@NonNull String) passwordEncoder.encode(password);
		
		if (userDetailsManager.userExists(username)) throw new DuplicateKeyException("User already exists");
		
		userRepository.createNew(username, passwordHash, email, true);
		groupManager.addUserToGroup(username, "users");
	}
	
	public UserEntity login(Authentication authenticationToken, boolean rememberMe, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
	{
		authenticationToken = authenticationManager.authenticate(authenticationToken);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authenticationToken);
		securityContextRepository.saveContext(securityContext, request, response);
		
		return userRepository.findById(authenticationToken.getName()).get();
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication == null) return List.of();
		return authentication.getAuthorities();
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
	
	public boolean deleteUserIfPasswordMatches(String username, String password)
	{
		Optional<UserEntity> optionalUser = userRepository.findById(username);
		if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword()))
		{
			userDetailsManager.deleteUser(username);
			return true;
		}
		return false;
	}
	
	public void deleteUser(String username)
	{
		userDetailsManager.deleteUser(username);
	}
}
