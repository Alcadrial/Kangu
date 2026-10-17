package com.alcadrial.kangu.utils;

import java.util.regex.Pattern;

public class Validation {
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
	
	public static boolean email(String email)
	{
		return EMAIL_PATTERN.matcher(email).matches();
	}
}
