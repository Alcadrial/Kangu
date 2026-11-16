package com.alcadrial.kangu.unit.controller.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.api.UserController;
import com.alcadrial.kangu.data.OnlineStatus;
import com.alcadrial.kangu.data.UserEntity;
import com.alcadrial.kangu.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {
	
	private MockMvc mockMvc;
	@MockitoBean
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void getSelfData() throws Exception
	{
		UserEntity.Safe user = new UserEntity.Safe(TestConstants.USERNAME, TestConstants.EMAIL, TestConstants.TIME_0, TestConstants.TIME_1, TestConstants.TIME_2, OnlineStatus.ON);
		when(userService.get(TestConstants.USERNAME)).thenReturn(user);
		
		mockMvc
			.perform(get("/api/user"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(user)));
	}
	
	@Test
	@WithMockUser(username = TestConstants.USERNAME)
	public void getOtherUserData() throws Exception
	{
		UserEntity.Safe user = new UserEntity.Safe(TestConstants.USERNAME1, TestConstants.EMAIL, TestConstants.TIME_0, TestConstants.TIME_1, TestConstants.TIME_2, OnlineStatus.ON);
		when(userService.get(TestConstants.USERNAME1)).thenReturn(user);
		
		mockMvc
			.perform(get("/api/user?username=" + TestConstants.USERNAME1))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(objectMapper.writeValueAsString(user)));
	}
}
