
package com.krishagni.auth.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import com.krishagni.auth.domain.AuthErrorCode;
import com.krishagni.auth.domain.Authenticator;
import com.krishagni.commons.errors.AppException;

@Configurable
public class AppAuthenticator implements Authenticator {
	
	@Autowired
	private AuthenticationManager authManager;
	
	public AppAuthenticator(Map<String, String> props) {
		
	}

	@Override
	public void authenticate(String username, String password) {
		try{
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
			authManager.authenticate(authenticationToken);
		} catch(AuthenticationException e) {
			throw AppException.userError(AuthErrorCode.INVALID_CREDENTIALS);
		}
	}
}
