package com.alcadrial.kangu.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Constants {
	
	public static final GrantedAuthority ROLE_ADMIN_AUTHORITY = new SimpleGrantedAuthority("ROLE_ADMIN");
}
