package com.krishagni.auth.domain;

import java.util.Date;

import com.krishagni.commons.domain.BaseEntity;
import com.krishagni.commons.domain.IUser;

public class UserApiCallLog extends BaseEntity {
	private IUser user;
	
	private String url;
	
	private String method;
	
	private String responseCode;
	
	private Date callStartTime;
	
	private Date callEndTime;
	
	private LoginAuditLog loginAuditLog;

	public IUser getUser() {
		return user;
	}

	public void setUser(IUser user) {
		this.user = user;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Date getCallStartTime() {
		return callStartTime;
	}

	public void setCallStartTime(Date callStartTime) {
		this.callStartTime = callStartTime;
	}

	public Date getCallEndTime() {
		return callEndTime;
	}

	public void setCallEndTime(Date callEndTime) {
		this.callEndTime = callEndTime;
	}

	public LoginAuditLog getLoginAuditLog() {
		return loginAuditLog;
	}

	public void setLoginAuditLog(LoginAuditLog loginAuditLog) {
		this.loginAuditLog = loginAuditLog;
	}

}
