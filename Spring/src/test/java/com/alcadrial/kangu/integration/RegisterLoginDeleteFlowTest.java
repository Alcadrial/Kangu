package com.alcadrial.kangu.integration;

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
class RegisterLoginDeleteFlowTest {
	
	@Autowired
	private RestTestClient restClient;
	@Autowired
	private AuthService authService;
	
	@BeforeEach
	public void setUp() throws Exception
	{
		authService.deleteUser(TestConstants.USERNAME);
		restClient = TestUtils.getSessionAndCsrfTokens(restClient);
	}
	
	@Test
	void flow() throws Exception
	{
		restClient
			.post().uri("/api/login")
			.contentType(MediaType.APPLICATION_JSON)
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			.exchange()
			.expectStatus().isUnauthorized();
		restClient
			.post().uri("/api/register")
			.contentType(MediaType.APPLICATION_JSON)
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "email", TestConstants.EMAIL, "password", TestConstants.PASSWORD))
			.exchange()
			.expectStatus().is3xxRedirection();
		TestUtils.assertValidUserSession(restClient, TestConstants.USERNAME);
		TestUtils.assertLogout(restClient);
		restClient
			.post().uri("/api/login")
			.contentType(MediaType.APPLICATION_JSON)
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			.exchange()
			.expectStatus().isOk();
		restClient
			.post().uri("/api/delete")
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD1))
			.exchange()
			.expectStatus().isUnauthorized();
		restClient
			.post().uri("/api/delete")
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			.exchange()
			.expectStatus().isOk();
		restClient
			.post().uri("/api/login")
			.contentType(MediaType.APPLICATION_JSON)
			.body(TestUtils.plainJson("username", TestConstants.USERNAME, "password", TestConstants.PASSWORD))
			.exchange()
			.expectStatus().isUnauthorized();
	}
}
