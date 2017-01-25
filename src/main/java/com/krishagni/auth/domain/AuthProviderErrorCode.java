package com.krishagni.auth.domain;

import com.krishagni.commons.errors.ErrorCode;

public enum AuthProviderErrorCode implements ErrorCode {
	TYPE_NOT_SPECIFIED,
	
	DOMAIN_NOT_FOUND,
	
	DOMAIN_NOT_SPECIFIED,
	
	DUP_DOMAIN_NAME,
	
	IMPL_NOT_SPECIFIED,
	
	INVALID_AUTH_IMPL;
	
	@Override
	public String code() {
		return "AUTH_PROVIDER_" + this.name();
	}
}
