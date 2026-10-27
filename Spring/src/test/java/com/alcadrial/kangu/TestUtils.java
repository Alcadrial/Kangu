package com.alcadrial.kangu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class TestUtils {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String plainJson(String... args)
	{
		ObjectNode obj = objectMapper.createObjectNode();
		for (int i = 0; i < args.length / 2; i++) obj.put(args[2 * i], args[2 * i + 1]);
		return obj.toString();
	}
	
	public static void mockPasswordEncoder(PasswordEncoder passwordEncoder)
	{
		when(passwordEncoder.encode(any())).then(invok -> invok.getArgument(0));
		when(passwordEncoder.matches(any(), any())).then(invok -> invok.getArgument(0).equals(invok.getArgument(1)));
	}
	
	record CsrfResponse(String csrf) {}
	
	record UserSessionData(String principal, Boolean isEnabled, Boolean isAnonymous, List<String> authorities) {}
	
	@SuppressWarnings("null")
	public static RestTestClient getSessionAndCsrfTokens(RestTestClient restClient)
	{
		EntityExchangeResult<CsrfResponse> r = restClient
			.get().uri("/api/csrf")
			.exchangeSuccessfully()
			.expectBody(CsrfResponse.class)
			.value(c -> {
				assertNotNull(c);
				assertNotNull(c.csrf);
			})
			.returnResult();
		List<ResponseCookie> setCookie = r.getResponseCookies().get("JSESSIONID");
		CsrfResponse responseBody = r.getResponseBody();
		assertNotNull(responseBody);
		assertNotNull(setCookie);
		assertEquals(1, setCookie.size());
		String sessionToken = setCookie.getFirst().getValue();
		String csrfToken = responseBody.csrf();
		return restClient.mutate().defaultHeader("X-CSRF-TOKEN", csrfToken).defaultCookie("JSESSIONID", sessionToken).build();
	}
	
	@SuppressWarnings("null")
	public static void assertValidUserSession(RestTestClient restClient, String principal)
	{
		restClient
			.get().uri("/api/user")
			.exchange()
			.expectStatus().isOk()
			.expectBody(UserSessionData.class)
			.value(d -> {
				assertNotNull(d);
				assertEquals(principal, d.principal);
				assertEquals(false, d.isAnonymous);
				assertEquals(true, d.isEnabled);
				assertTrue(d.authorities.contains("ROLE_USER"));
			});
	}
	
	public static void assertLogout(RestTestClient restClient)
	{
		restClient
			.post().uri("/api/logout")
			.exchange()
			.expectStatus().is3xxRedirection();
	}
}
