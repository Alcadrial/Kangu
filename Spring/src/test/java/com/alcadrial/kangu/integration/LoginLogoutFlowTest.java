package com.alcadrial.kangu.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.alcadrial.kangu.TestConstants;
import com.alcadrial.kangu.TestUtils;
import com.alcadrial.kangu.service.AuthService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class LoginLogoutFlowTest {
	
	@Autowired
	private RestTestClient restClient;
	@Autowired
	private AuthService authService;
	
	@BeforeEach
	public void setUp() throws Exception
	{
		authService.deleteUser(TestConstants.USERNAME);
		authService.register(TestConstants.USERNAME, TestConstants.EMAIL, TestConstants.PASSWORD);
		restClient = TestUtils.getSessionAndCsrfTokens(restClient);
	}
	
	record UserSessionData(String principal, boolean isEnabled, boolean isAnonymous, List<String> authorities) {}
	
	record AnonymousSessionData(String principal, boolean isAnonymous, List<String> authorities) {}
	
	@SuppressWarnings("null")
	@Test
	void flow() throws Exception
	{
		restClient
			.get().uri("/api/user")
			.exchange()
			.expectStatus().isOk()
			.expectBody(AnonymousSessionData.class)
			.value(d -> {
				assertEquals("anonymousUser", d.principal);
				assertEquals(true, d.isAnonymous);
				assertTrue(d.authorities.isEmpty());
			});
		restClient
			.post().uri("/api/login")
			.contentType(MediaType.APPLICATION_JSON)
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			.exchangeSuccessfully();
		restClient
			.get().uri("/api/user")
			.exchange()
			.expectStatus().isOk()
			.expectBody(UserSessionData.class)
			.value(d -> {
				assertEquals(TestConstants.USERNAME, d.principal);
				assertEquals(false, d.isAnonymous);
				assertEquals(true, d.isEnabled);
				assertTrue(d.authorities.contains("ROLE_USER"));
			});
		restClient
			.post().uri("/api/logout")
			.exchange()
			.expectStatus().is3xxRedirection();
		restClient
			.get().uri("/api/user")
			.exchange()
			.expectStatus().isOk()
			.expectBody(AnonymousSessionData.class)
			.value(d -> {
				assertEquals("anonymousUser", d.principal);
				assertEquals(true, d.isAnonymous);
				assertTrue(d.authorities.isEmpty());
			});
	}
}
