package com.alcadrial.kangu.utils;

import java.time.Instant;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Constants {
	
	public static final GrantedAuthority ROLE_ADMIN_AUTHORITY = new SimpleGrantedAuthority("ROLE_ADMIN");
	public static final Instant TIME_NEVER = Instant.ofEpochSecond(0);
	
	public static final String MESSAGES_TOPIC = "messages";
	public static final String CURSORS_TOPIC = "cursors";
	
	public static final String AUTHENTICATION_ERROR_CODE = "01";
	public static final String BAD_CREDENTIALS_ERROR_CODE = AUTHENTICATION_ERROR_CODE + "000";
	public static final String DISABLED_ERROR_CODE = AUTHENTICATION_ERROR_CODE + "001";
	public static final String LOCKED_ERROR_CODE = AUTHENTICATION_ERROR_CODE + "002";
	public static final String INVALID_ERROR_CODE = AUTHENTICATION_ERROR_CODE + "003";
	public static final String EXPIRED_CREDENTIALS_ERROR_CODE = AUTHENTICATION_ERROR_CODE + "004";
	
	public static final String AUTHORIZATION_ERROR_CODE = "02";
	public static final String MISSING_AUTHORIZATION_ERROR_CODE = AUTHORIZATION_ERROR_CODE + "000";
	
	public static final String INTERNAL_ERROR_CODE = "10";
	public static final String KNOWN_INTERNAL_ERROR_CODE = INTERNAL_ERROR_CODE + "000";
	
	public static final String GENERIC_ERROR_CODE = "20";
	public static final String DISABLED_SERVICE_ERROR_CODE = GENERIC_ERROR_CODE + "001";
	
	public static final String CLIENT_ERROR_CODE = "21";
	public static final String NOT_FOUND_ERROR_CODE = CLIENT_ERROR_CODE + "000";
	public static final String DUPLICATED_VALUE_ERROR_CODE = CLIENT_ERROR_CODE + "001";
	public static final String VALUE_TOO_BIG_ERROR_CODE = CLIENT_ERROR_CODE + "002";
	public static final String BLOCKED_CHAT_ERROR_CODE = CLIENT_ERROR_CODE + "003";
	public static final String MISSING_DATA_ERROR_CODE = CLIENT_ERROR_CODE + "004";
	
	public static final String UNKNOWN_ERROR_CODE = "FF";
	
	//@Deprecated
	public static final String EMPTY_MESSAGE = "";
}
