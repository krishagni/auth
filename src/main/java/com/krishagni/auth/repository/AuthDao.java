
package com.krishagni.auth.repository;

import java.util.Date;
import java.util.List;

import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.domain.AuthProvider;
import com.krishagni.auth.domain.AuthToken;
import com.krishagni.auth.domain.LoginAuditLog;
import com.krishagni.commons.repository.Dao;

public interface AuthDao extends Dao<AuthDomain> {
	//
	// auth domain
	//
	List<AuthDomain> getAuthDomains(int maxResults);
	
	AuthDomain getAuthDomainByName(String domainName);
	
	AuthDomain getAuthDomainByType(String authType);

	Boolean isUniqueAuthDomainName(String domainName);

	//
	// auth provider object
	//
	AuthProvider getAuthProviderByType(String authType);

	//
	// auth token object
	//
	AuthToken getAuthTokenByKey(String key);
	
	void saveAuthToken(AuthToken token);

	void deleteAuthToken(AuthToken token);

	int deleteInactiveAuthTokens(Date expiresOn);

	int deleteAuthTokensByUser(List<Long> userIds);

	//
	// login audit log object
	//
	List<LoginAuditLog> getLoginAuditLogsByUser(Long userId, int maxResults);
	
	void saveLoginAuditLog(LoginAuditLog log);
}
