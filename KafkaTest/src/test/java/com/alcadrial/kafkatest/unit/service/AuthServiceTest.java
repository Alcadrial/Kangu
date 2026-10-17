package com.alcadrial.kafkatest.unit.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alcadrial.kafkatest.TestConfig;
import com.alcadrial.kafkatest.TestConstants;
import com.alcadrial.kafkatest.data.UserEntity;
import com.alcadrial.kafkatest.exception.DuplicateKeyException;
import com.alcadrial.kafkatest.repository.UserRepository;
import com.alcadrial.kafkatest.service.AuthService;
import com.alcadrial.kafkatest.service.AuthServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@DataJpaTest
@Import({TestConfig.class, AuthServiceImpl.class})
@ParameterizedClass(name = "{arguments}")
@ArgumentsSource(TestConstants.Credentials01.class)
class AuthServiceTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthService authService;
	
	@Parameter(0)
	String username;
	@Parameter(1)
	String password;
	
	@BeforeEach
	void prepare()
	{
		String passwordHash = (@NonNull String) passwordEncoder.encode(TestConstants.PASSWORD);
		userRepository.save(new UserEntity(TestConstants.USERNAME, TestConstants.EMAIL, passwordHash));
	}
	
	@AfterEach
	void teardown()
	{
		userRepository.deleteAll();
	}
	
	@Test
	void login()
	{
		if (isCanLogin(username, password))
		{
			assertThatNoException().as("Unexpected login failure").isThrownBy(() -> {
				UserEntity user = authService.login(username, password);
				assertEquals(username, user.getUsername(), "Wrong username");
				assertEquals(TestConstants.EMAIL, user.getEmail(), "Wrong email");
			});
		}
		else
		{
			assertThrowsExactly(EntityNotFoundException.class, () -> {
				authService.login(username, password);
			}, "Unexpected successful login or exception");
		}
	}
	
	@Test
	void register()
	{
		String email = TestConstants.EMAIL1;
		if (isCanRegister(username))
		{
			assertThatNoException().as("Unexpected registration failure (username=%s, password=%s)", username, password).isThrownBy(() -> {
				UserEntity user = authService.register(username, email, password);
				assertEquals(username, user.getUsername(), "Wrong username");
				assertEquals(email, user.getEmail(), "Wrong email");
			});
		}
		else
		{
			assertThrowsExactly(DuplicateKeyException.class, () -> {
				authService.register(username, email, password);
			}, "Unexpected successful registration or exception");
		}
	}
	
	boolean isCanLogin(String username, String password)
	{
		return username.equals(TestConstants.USERNAME) && password.equals(TestConstants.PASSWORD);
	}
	
	boolean isCanRegister(String username)
	{
		return !username.equals(TestConstants.USERNAME);
	}
}
