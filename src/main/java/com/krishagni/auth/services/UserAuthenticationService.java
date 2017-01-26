package com.krishagni.auth.services;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import com.krishagni.auth.domain.AuthToken;
import com.krishagni.auth.events.LoginDetail;
import com.krishagni.auth.events.TokenDetail;
import com.krishagni.commons.domain.IUser;
import com.krishagni.commons.events.UserInfo;

public interface UserAuthenticationService extends UserDetailsService, SAMLUserDetailsService {
	Map<String, Object> authenticateUser(LoginDetail loginDetail);
	
	AuthToken validateToken(TokenDetail tokenDetail);
	
	IUser getLoggedInUser();

	String  generateToken(IUser user, LoginDetail loginDetail);

	void removeToken(String token);

	void purgeInactiveTokens();

	String getDefaultAuthDomain();
}