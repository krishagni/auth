package com.krishagni.auth.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.krishagni.commons.domain.BaseEntity;
import com.krishagni.commons.domain.IUser;

public class ForgotPasswordToken extends BaseEntity {
	
	private static final long DEFAULT_EXPIRY_TIME = 1000 * 60 * 60 * 24; //24 hours
	
	private IUser user;

	private String token;
	
	private Date createdDate;
	
	public ForgotPasswordToken() {
		
	}
	
	public ForgotPasswordToken(IUser user) {
		this.user = user;
		this.token = UUID.randomUUID().toString();
		this.createdDate = new Date();
	}
	
	public IUser getUser() {
		return user;
	}

	public void setUser(IUser user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean hasExpired() {
		return (getCreatedDate().getTime() + DEFAULT_EXPIRY_TIME) < Calendar.getInstance().getTimeInMillis();
	}
}
