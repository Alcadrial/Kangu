package com.alcadrial.kangu.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.exception.DisabledException;
import com.alcadrial.kangu.properties.RegistrationProperties;
import com.alcadrial.kangu.repository.UserRepository;
import com.alcadrial.kangu.service.AuthService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	
	static final String USERNAME = TestConstants.USERNAME;
	static final String EMAIL = TestConstants.EMAIL;
	static final String PASSWORD = TestConstants.PASSWORD;
	
	@Mock(strictness = Strictness.LENIENT)
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserDetailsManager userDetailsManager;
	@Mock
	private GroupManager groupManager;
	@Mock
	private UserDetailsPasswordService passwordService;
	@Mock
	private BytesKeyGenerator keyGenerator;
	@Mock
	private RegistrationProperties registrationProperties;
	@InjectMocks
	private AuthService authService;
	
	@BeforeEach
	void prepare()
	{
		TestUtils.mockPasswordEncoder(passwordEncoder);
	}
	
	@Test
	void successLogin()
	{
		when(userDetailsManager.loadUserByUsername(USERNAME)).thenReturn(TestConstants.USER_DETAILS);
		when(userRepository.findById(USERNAME)).thenReturn(Optional.of(TestConstants.USER));
		
		assertDoesNotThrow(() -> {
			UserEntity user = authService.login(USERNAME, PASSWORD, false);
			assertEquals(USERNAME, user.getUsername(), "Wrong username");
			assertEquals(EMAIL, user.getEmail(), "Wrong email");
		});
	}
	
	@Test
	void failLogin()
	{
		when(userDetailsManager.loadUserByUsername(USERNAME)).thenThrow(UsernameNotFoundException.class);
		
		assertThrows(EntityNotFoundException.class, () -> {
			authService.login(USERNAME, PASSWORD, false);
		});
	}
	
	@Test
	void successRegisterAndLoginIfEnabled()
	{
		when(registrationProperties.isEnabled()).thenReturn(true);
		when(userDetailsManager.userExists(anyString())).thenReturn(false);
		doAnswer(invok -> {
			String username = invok.getArgument(0);
			String password = invok.getArgument(1);
			String email = invok.getArgument(2);
			boolean enabled = invok.getArgument(3);
			when(userDetailsManager.loadUserByUsername(username)).thenReturn(User.withUsername(username).password(password).build());
			when(userRepository.findById(username)).thenReturn(Optional.of(new UserEntity(username, password, email, enabled)));
			return null;
		})
			.when(userRepository)
			.createNew(anyString(), anyString(), anyString(), anyBoolean());
		
		InOrder order = inOrder(userRepository, userDetailsManager, groupManager);

		assertDoesNotThrow(() -> {
			authService.register(USERNAME, EMAIL, PASSWORD);
			UserEntity user = authService.login(USERNAME, PASSWORD, false);
			assertEquals(USERNAME, user.getUsername(), "Wrong username");
			assertEquals(EMAIL, user.getEmail(), "Wrong email");
		});
		
		order.verify(userDetailsManager).userExists(USERNAME);
		order.verify(userRepository).createNew(eq(USERNAME), anyString(), anyString(), anyBoolean());
		order.verify(groupManager).addUserToGroup(USERNAME, "users");
	}
	
	@Test
	void failRegisterIfDisabled()
	{
		assertThrows(DisabledException.class, () -> {
			authService.register(USERNAME, EMAIL, PASSWORD);
		});
	}
}
