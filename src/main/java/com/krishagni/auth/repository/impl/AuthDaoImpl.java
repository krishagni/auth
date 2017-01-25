package com.krishagni.auth.repository.impl;

import java.util.Date;
import java.util.List;

import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.domain.AuthProvider;
import com.krishagni.auth.domain.AuthToken;
import com.krishagni.auth.domain.LoginAuditLog;
import com.krishagni.auth.repository.AuthDao;
import com.krishagni.commons.repository.AbstractDao;

public class AuthDaoImpl extends AbstractDao<AuthDomain> implements AuthDao {
	@SuppressWarnings("unchecked")
	@Override
	public List<AuthDomain> getAuthDomains(int maxResults) {
		return getCurrentSession().getNamedQuery(GET_AUTH_DOMAINS)
			.setMaxResults(maxResults)
			.list();
	}
	
	@SuppressWarnings(value = {"unchecked"})
	@Override
	public AuthDomain getAuthDomainByName(String domainName) {
		List<AuthDomain> result = getCurrentSession().getNamedQuery(GET_DOMAIN_BY_NAME)
			.setParameter("domainName", domainName)
			.list();
		return result.isEmpty() ? null : result.get(0);
	}
	
	@SuppressWarnings(value = {"unchecked"})
	@Override
	public AuthDomain getAuthDomainByType(String authType) {
		List<AuthDomain> result = getCurrentSession().getNamedQuery(GET_DOMAIN_BY_TYPE)
			.setParameter("authType", authType)
			.list();
		return result.isEmpty() ? null : result.get(0);
	}

	@SuppressWarnings(value = {"unchecked"})
	@Override
	public Boolean isUniqueAuthDomainName(String domainName) {
		List<AuthDomain> result = getCurrentSession().getNamedQuery(GET_DOMAIN_BY_NAME)
			.setParameter("domainName", domainName)
			.list();
		return result.isEmpty();
	}
	
	@SuppressWarnings(value = {"unchecked"})
	@Override
	public AuthProvider getAuthProviderByType(String authType) {
		List<AuthProvider> result = getCurrentSession().getNamedQuery(GET_PROVIDER_BY_TYPE)
			.setParameter("authType", authType)
			.list();
		return result.isEmpty() ? null : result.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AuthToken getAuthTokenByKey(String key) {
		List<AuthToken> tokens = getCurrentSession().getNamedQuery(GET_AUTH_TOKEN_BY_KEY)
			.setParameter("token", key)
			.list();

		return tokens.isEmpty() ? null : tokens.get(0);
	}
	
	@Override
	public void saveAuthToken(AuthToken token) {
		getCurrentSession().saveOrUpdate(token);
	}
	
	@Override
	public void deleteAuthToken(AuthToken token) {
		getCurrentSession().delete(token);
	}
	
	@Override
	public int deleteInactiveAuthTokens(Date latestAccessTime) {
		return getCurrentSession().getNamedQuery(DELETE_INACTIVE_AUTH_TOKENS)
			.setParameter("latestCallTime", latestAccessTime)
			.executeUpdate();
	}
	
	@Override
	public int deleteAuthTokensByUser(List<Long> userIds) {
		return getCurrentSession().getNamedQuery(DELETE_AUTH_TOKENS_BY_USER_ID)
			.setParameterList("ids", userIds)
			.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LoginAuditLog> getLoginAuditLogsByUser(Long userId, int maxResults) {
		return getCurrentSession().getNamedQuery(GET_LOGIN_AUDIT_LOGS_BY_USER_ID)
				.setParameter("userId", userId)
				.setMaxResults(maxResults)
				.list();
	}

	@Override
	public void saveLoginAuditLog(LoginAuditLog log) {
		getCurrentSession().saveOrUpdate(log);
	}
	
	private static final String FQN = AuthDomain.class.getName();
	
	private static final String GET_AUTH_DOMAINS = FQN + ".getAuthDomains";

	private static final String GET_DOMAIN_BY_NAME = FQN + ".getDomainByName";

	private static final String GET_DOMAIN_BY_TYPE = FQN + ".getDomainByType";

	private static final String GET_PROVIDER_BY_TYPE = AuthProvider.class.getName() + ".getProviderByType";

	private static final String AUTH_TOKEN_FQN = AuthToken.class.getName();

	private static final String GET_AUTH_TOKEN_BY_KEY = AUTH_TOKEN_FQN + ".getByKey";
	
	private static final String DELETE_INACTIVE_AUTH_TOKENS = AUTH_TOKEN_FQN + ".deleteInactiveAuthTokens";

	private static final String DELETE_AUTH_TOKENS_BY_USER_ID = AUTH_TOKEN_FQN + ".deleteAuthTokensByUserId";

	private static final String GET_LOGIN_AUDIT_LOGS_BY_USER_ID = LoginAuditLog.class.getName() + ".getLogsByUserId";
	
}
