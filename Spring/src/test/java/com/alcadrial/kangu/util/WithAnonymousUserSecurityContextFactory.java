package com.alcadrial.kangu.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

final class WithAnonymousUserSecurityContextFactory implements WithSecurityContextFactory<WithAnonymousUser> {
	
	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
		.getContextHolderStrategy();
	
	@Override
	public SecurityContext createSecurityContext(WithAnonymousUser withUser)
	{
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
		Authentication authentication = new AnonymousAuthenticationToken("key", "anonymousUser", authorities);
		SecurityContext context = securityContextHolderStrategy.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}
	
	@Autowired(required = false)
	void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy)
	{
		this.securityContextHolderStrategy = securityContextHolderStrategy;
	}
}
