package com.alcadrial.kangu;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.alcadrial.kangu.data.UserEntity;

public class TestConstants {
	
	public static final String USERNAME = "user";
	public static final String USERNAME1 = "user1";
	public static final String USERNAME2 = "user2";
	public static final String[] USERNAMES_01 = {USERNAME, USERNAME1};
	public static final String[] ALL_USERNAMES = {USERNAME, USERNAME1, USERNAME2};
	
	public static final String PASSWORD = "password";
	public static final String PASSWORD1 = "password1";
	public static final String PASSWORD2 = "password2";
	public static final String[] PASSWORDS_01 = {PASSWORD, PASSWORD1};
	public static final String[] ALL_PASSWORDS = {PASSWORD, PASSWORD1, PASSWORD2};
	
	public static final String EMAIL = "g@g.g";
	public static final String EMAIL1 = "g1@g.g";
	public static final String EMAIL2 = "g2@g.g";
	public static final String[] EMAILS_01 = {EMAIL, EMAIL1};
	public static final String[] ALL_EMAILS = {EMAIL, EMAIL1, EMAIL2};
	
	public static final UserEntity USER = new UserEntity(USERNAME, PASSWORD, EMAIL, true);
	public static final UserDetails USER_DETAILS = User.withUsername(USERNAME).password(PASSWORD).build();
	
	public static final String[][] CREDENTIALS_01 = crossProduct(USERNAMES_01, PASSWORDS_01);
	public static final String[][] CREDENTIALS_ALL = crossProduct(ALL_USERNAMES, ALL_PASSWORDS);
	
	/** <pre>username,password</pre> */
	public static class Credentials01 implements ArgumentsProvider {
		
		@Override
		public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) throws Exception
		{
			return Stream.of(CREDENTIALS_01).map(Arguments::of);
		}
	}
	
	/** <pre>username,password</pre> */
	public static class CredentialsAll implements ArgumentsProvider {
		
		@Override
		public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) throws Exception
		{
			return Stream.of(CREDENTIALS_ALL).map(Arguments::of);
		}
	}
	
	public static String[][] crossProduct(String[]... testSets)
	{
		if (testSets.length == 0) return new String[][] {};
		int maxPointer = 1;
		for (String[] testSet : testSets) maxPointer *= testSet.length;
		if (maxPointer == 0) return new String[][] {};
		
		String[][] ss = new String[maxPointer][testSets.length];
		int p = 0;
		int[] pointer = new int[testSets.length];
		while (true)
		{
			for (int i = 0; i < testSets.length; i++)
			{
				ss[p][i] = testSets[i][pointer[i]];
			}
			p++;
			int i;
			for (i = testSets.length - 1; i >= 0 && ++pointer[i] == testSets[i].length; i--) pointer[i] = 0;
			if (i == -1) break;
		}
		return ss;
	}
}
