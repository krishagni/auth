package com.krishagni.auth;

public interface AuthConfig {
	int getAllowedFailedLoginAttempts();
	
	boolean isTokenIpVerified();

	int getTokenInactiveIntervalInMinutes();

	String getAppUrl();

	boolean isSamlEnabled();

	String getCookieName();
}
