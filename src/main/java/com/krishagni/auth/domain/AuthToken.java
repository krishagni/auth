package com.krishagni.auth.domain;

import com.krishagni.commons.domain.IUser;

public class AuthToken {
	private String token;
	
	private IUser user;
	
	private String ipAddress;
	
	private LoginAuditLog loginAuditLog;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public IUser getUser() {
		return user;
	}

	public void setUser(IUser user) {
		this.user = user;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public LoginAuditLog getLoginAuditLog() {
		return loginAuditLog;
	}

	public void setLoginAuditLog(LoginAuditLog loginAuditLog) {
		this.loginAuditLog = loginAuditLog;
	}
}
