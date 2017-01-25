
package com.krishagni.auth.services.impl;

import java.util.List;

import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.domain.AuthProviderErrorCode;
import com.krishagni.auth.events.AuthDomainDetail;
import com.krishagni.auth.events.AuthDomainSummary;
import com.krishagni.auth.events.ListAuthDomainCriteria;
import com.krishagni.auth.factory.AuthDomainFactory;
import com.krishagni.auth.repository.AuthDaoFactory;
import com.krishagni.auth.services.AuthDomainService;
import com.krishagni.commons.errors.AppException;

public class AuthDomainServiceImpl implements AuthDomainService {
	private AuthDaoFactory authDaoFactory;

	private AuthDomainFactory authDomainFactory;

	public void setAuthDaoFactory(AuthDaoFactory authDaoFactory) {
		this.authDaoFactory = authDaoFactory;
	}

	public void setAuthDomainFactory(AuthDomainFactory authDomainFactory) {
		this.authDomainFactory = authDomainFactory;
	}

	@Override
	public List<AuthDomainSummary> getDomains(ListAuthDomainCriteria criteria) {
		List<AuthDomain> authDomains = authDaoFactory.getAuthDao().getAuthDomains(criteria.maxResults());
		return AuthDomainSummary.from(authDomains);
	}

	@Override
	public AuthDomainDetail registerDomain(AuthDomainDetail authDomainDetail) {
		try {
			AuthDomain authDomain = authDomainFactory.createDomain(authDomainDetail);
			ensureUniqueDomainName(authDomain.getName());
			authDaoFactory.getAuthDao().saveOrUpdate(authDomain);
			return AuthDomainDetail.from(authDomain);
		} catch (Exception e) {
			return AppException.raiseError(e);
		}
	}
	
	@Override
	public AuthDomainDetail updateDomain(AuthDomainDetail authDomainDetail) {
		try {
			AuthDomain existingDomain = authDaoFactory.getAuthDao().getAuthDomainByName(authDomainDetail.getName());
			if (existingDomain == null) {
				throw AppException.userError(AuthProviderErrorCode.DOMAIN_NOT_FOUND, authDomainDetail.getName());
			}
			
			AuthDomain authDomain = authDomainFactory.createDomain(authDomainDetail);
			existingDomain.update(authDomain);
			return AuthDomainDetail.from(existingDomain);
		} catch (Exception e) {
			return AppException.raiseError(e);
		}
	}

	private void ensureUniqueDomainName(String domainName) {
		if (!authDaoFactory.getAuthDao().isUniqueAuthDomainName(domainName)) {
			throw AppException.userError(AuthProviderErrorCode.DUP_DOMAIN_NAME, domainName);
		}
	}
}