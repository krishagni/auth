package com.krishagni.auth.factory.impl;

import org.apache.commons.lang3.StringUtils;

import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.domain.AuthProvider;
import com.krishagni.auth.domain.AuthProviderErrorCode;
import com.krishagni.auth.domain.Authenticator;
import com.krishagni.auth.events.AuthDomainDetail;
import com.krishagni.auth.factory.AuthDomainFactory;
import com.krishagni.commons.errors.AppException;

public class AuthDomainFactoryImpl implements AuthDomainFactory {

	@Override
	public AuthDomain createDomain(AuthDomainDetail detail) {
		AuthDomain authDomain = new AuthDomain();
		setDomainName(detail, authDomain);
		setAuthProvider(detail, authDomain);
		return authDomain;
	}
	
	private void setDomainName(AuthDomainDetail detail, AuthDomain authDomain) {
		String domainName = detail.getName();
		if (StringUtils.isBlank(domainName)) {
			throw AppException.userError(AuthProviderErrorCode.DOMAIN_NOT_SPECIFIED);
		}
		
		authDomain.setName(domainName);
	}
	
	private void setAuthProvider(AuthDomainDetail detail, AuthDomain authDomain) {
		String authType = detail.getAuthType();
		if (StringUtils.isBlank(authType)) {
			throw AppException.userError(AuthProviderErrorCode.TYPE_NOT_SPECIFIED);
		}
		
		AuthProvider authProvider = getNewAuthProvider(detail);
		authDomain.setAuthProvider(authProvider);
	}

	private AuthProvider getNewAuthProvider(AuthDomainDetail detail) {
		ensureValidImplClass(detail.getImplClass());
		
		AuthProvider authProvider = new AuthProvider();
		authProvider.setAuthType(detail.getAuthType());
		authProvider.setImplClass(detail.getImplClass());
		authProvider.setProps(detail.getAuthProviderProps());
		return authProvider;
	}

	@SuppressWarnings("rawtypes")
	private Authenticator ensureValidImplClass(String implClass) {
		if (StringUtils.isBlank(implClass)) {
			throw AppException.userError(AuthProviderErrorCode.IMPL_NOT_SPECIFIED);
		}

		try {
			Class authImplClass = (Class) Class.forName(implClass);
			return (Authenticator) authImplClass.newInstance();
		} catch (Exception e) {
			throw AppException.userError(AuthProviderErrorCode.INVALID_AUTH_IMPL);
		}
	}
}
