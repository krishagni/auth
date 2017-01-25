package com.krishagni.auth.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.krishagni.commons.domain.IUser;

public class LoginAuditLog {
	private Long id;
	
	private IUser user;
	
	private String ipAddress;
	
	private Date loginTime;
	
	private Date logoutTime;
	
	private boolean loginSuccessful;
	
	private Set<UserApiCallLog> apiLogs = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	public boolean isLoginSuccessful() {
		return loginSuccessful;
	}

	public void setLoginSuccessful(boolean loginSuccessful) {
		this.loginSuccessful = loginSuccessful;
	}

	public Set<UserApiCallLog> getApiLogs() {
		return apiLogs;
	}

	public void setApiLogs(Set<UserApiCallLog> apiLogs) {
		this.apiLogs = apiLogs;
	}
	
}
