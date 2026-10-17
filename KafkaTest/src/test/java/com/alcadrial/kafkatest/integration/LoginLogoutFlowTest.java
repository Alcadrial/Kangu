package com.alcadrial.kafkatest.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.alcadrial.kafkatest.TestConstants;
import com.alcadrial.kafkatest.TestUtils;
import com.alcadrial.kafkatest.data.UserEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestEntityManager
@Transactional
class LoginLogoutFlowTest {
	
	MockMvc mockMvc;
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@BeforeEach
	public void setUp(@Autowired WebApplicationContext applicationContext)
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
			.apply(springSecurity())
			.build();
		String password = (@NonNull String) passwordEncoder.encode(TestConstants.PASSWORD);
		entityManager.persist(new UserEntity(TestConstants.USERNAME, TestConstants.EMAIL, password));
	}
	
	@Test
	void flow() throws Exception
	{
		mockMvc
			.perform(post("/api/login").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			)
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/api/user"));
		mockMvc
			.perform(get("/api/user").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.principal").value(TestConstants.USERNAME))
			.andExpect(jsonPath("$.isEnabled").value(true))
			.andExpect(jsonPath("$.authorities").value(Matchers.containsInAnyOrder("ROLE_USER")));
		mockMvc
			.perform(post("/api/logout").with(csrf().asHeader())
				.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}
}
