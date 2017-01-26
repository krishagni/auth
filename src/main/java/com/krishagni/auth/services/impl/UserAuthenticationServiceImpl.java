
package com.krishagni.auth.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.opensaml.saml2.core.Attribute;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

import com.krishagni.auth.AuthConfig;
import com.krishagni.auth.Util;
import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.domain.AuthErrorCode;
import com.krishagni.auth.domain.AuthToken;
import com.krishagni.auth.domain.Authenticator;
import com.krishagni.auth.domain.LoginAuditLog;
import com.krishagni.auth.domain.UserApiCallLog;
import com.krishagni.auth.events.LoginDetail;
import com.krishagni.auth.events.TokenDetail;
import com.krishagni.auth.repository.AuthDaoFactory;
import com.krishagni.auth.services.UserAuthenticationService;
import com.krishagni.commons.domain.IUser;
import com.krishagni.commons.errors.AppException;
import com.krishagni.commons.util.MessageUtil;

public abstract class UserAuthenticationServiceImpl implements UserAuthenticationService {

	private static final Log logger = LogFactory.getLog(UserAuthenticationServiceImpl.class);

	private AuthDaoFactory authDaoFactory;

	private AuthConfig authConfig;

	public void setAuthDaoFactory(AuthDaoFactory authDaoFactory) {
		this.authDaoFactory = authDaoFactory;
	}

	public void setAuthConfig(AuthConfig authConfig) {
		this.authConfig = authConfig;
	}

	@Override
	public Map<String, Object> authenticateUser(LoginDetail loginDetail) {
		IUser user = null;

		try {
			user = getUser(loginDetail.getLoginName(), loginDetail.getDomainName());
			if (user == null) {
				throw AppException.userError(AuthErrorCode.INVALID_CREDENTIALS);
			} else if (user.isLocked()) {
				throw AppException.userError(AuthErrorCode.USER_LOCKED);
			} else if (user.isPasswordExpired()) {
				throw AppException.userError(AuthErrorCode.PASSWD_EXPIRED);
			} else if (!user.isActive()) {
				throw AppException.userError(AuthErrorCode.INVALID_CREDENTIALS);
			}

			Authenticator authenticator = getAuthDomain(user).getAuthenticator();
			authenticator.authenticate(loginDetail.getLoginName(), loginDetail.getPassword());

			Map<String, Object> authDetail = new HashMap<>();
			authDetail.put("user", user);
			
			String authToken = generateToken(user, loginDetail);
			if (authToken != null) {
				authDetail.put("token", authToken);
			}
			
			return authDetail;
		} catch (AppException ae) {
			if (user != null && user.isActive()) {
				insertLoginAudit(user, loginDetail.getIpAddress(), false);
				checkFailedLoginAttempt(user);
			}

			throw ae;
		} catch (Exception e) {
			return AppException.raiseError(e);
		}
	}

	@Override
	public AuthToken validateToken(TokenDetail tokenDetail) {
		try {
			String token = Util.decodeToken(tokenDetail.getToken());

			AuthToken authToken = authDaoFactory.getAuthDao().getAuthTokenByKey(token);
			if (authToken == null) {
				throw AppException.userError(AuthErrorCode.INVALID_TOKEN);
			}

			IUser user = authToken.getUser();
			Date lastCallTime = authDaoFactory.getUserApiCallLogDao().getLatestApiCallTime(user.getId(), token);
			long timeSinceLastApiCall = Calendar.getInstance().getTime().getTime() - lastCallTime.getTime();

			int tokenInactiveInterval = authConfig.getTokenInactiveIntervalInMinutes() * 60 * 1000;
			if (timeSinceLastApiCall > tokenInactiveInterval) {
				authDaoFactory.getAuthDao().deleteAuthToken(authToken);
				throw AppException.userError(AuthErrorCode.TOKEN_EXPIRED);
			}

			if (authConfig.isTokenIpVerified()) {
				if (!tokenDetail.getIpAddress().equals(authToken.getIpAddress())) {
					throw AppException.userError(AuthErrorCode.IP_ADDRESS_CHANGED);
				}
			}

			if (!Hibernate.isInitialized(user)) {
				Hibernate.initialize(user);
			}

			return authToken;
		} catch (Exception e) {
			return AppException.raiseError(e);
		}
	}

	@Override
	public IUser getLoggedInUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (IUser) auth.getPrincipal();
	}

	@Override
	public String generateToken(IUser user, LoginDetail loginDetail) {
		LoginAuditLog loginAuditLog = insertLoginAudit(user, loginDetail.getIpAddress(), true);
		if (loginDetail.isDoNotGenerateToken()) {
			return null;
		}

		String token = UUID.randomUUID().toString();
		AuthToken authToken = new AuthToken();
		authToken.setIpAddress(loginDetail.getIpAddress());
		authToken.setToken(token);
		authToken.setUser(user);
		authToken.setLoginAuditLog(loginAuditLog);
		authDaoFactory.getAuthDao().saveAuthToken(authToken);

		insertApiCallLog(loginDetail, user, loginAuditLog);
		return Util.encodeToken(token);
	}


	@Override
	public void removeToken(String encodedToken) {
		String tokenString = Util.decodeToken(encodedToken);
		try {
			AuthToken token = authDaoFactory.getAuthDao().getAuthTokenByKey(tokenString);
			LoginAuditLog loginAuditLog = token.getLoginAuditLog();
			loginAuditLog.setLogoutTime(Calendar.getInstance().getTime());
			authDaoFactory.getAuthDao().deleteAuthToken(token);
		} catch (Exception e) {	
			AppException.raiseError(e);
		}
	}

	@Override
	public void purgeInactiveTokens() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -authConfig.getTokenInactiveIntervalInMinutes());
		authDaoFactory.getAuthDao().deleteInactiveAuthTokens(cal.getTime());
	}

	@Override
	public UserDetails loadUserByUsername(String username)
	throws UsernameNotFoundException {
		return getUser(username, null);
	}

	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		if (logger.isDebugEnabled()) {
			for (Attribute attr : credential.getAttributes()) {
				logger.debug(String.format(
					"Credential attr: %s (%s) = %s",
					attr.getName(), attr.getFriendlyName(), credential.getAttributeAsString(attr.getName())));
			}
		}

		//
		// The assumption is - there can be only one SAML auth provider
		// We should perhaps use SAML local entity ID
		//
		AuthDomain domain = authDaoFactory.getAuthDao().getAuthDomainByType("saml");

		Map<String, String> props = domain.getAuthProvider().getProps();
		String loginNameAttr = props.get("loginNameAttr");
		String emailAttr     = props.get("emailAddressAttr");

		IUser user = null;
		if (StringUtils.isNotBlank(loginNameAttr)) {
			user = getUser(getCredentialAttrValue(credential, loginNameAttr), domain.getName());
		} else if (StringUtils.isNotBlank(emailAttr)) {
			user = getUserByEmailAddress(getCredentialAttrValue(credential, emailAttr));
		}

		if (user == null) {
			throw new UsernameNotFoundException(MessageUtil.getInstance().getMessage("user_not_found"));
		}

		return user;
	}

	public abstract IUser getUser(String username, String domain);

	public abstract IUser getUserByEmailAddress(String emailAddress);

	public abstract AuthDomain getAuthDomain(IUser user);

	private void checkFailedLoginAttempt(IUser user) {
		int failedLoginAttempts = authConfig.getAllowedFailedLoginAttempts();

		List<LoginAuditLog> logs = authDaoFactory.getAuthDao().getLoginAuditLogsByUser(user.getId(), failedLoginAttempts);
		if (logs.size() < failedLoginAttempts) {
			return;
		}
		
		for (LoginAuditLog log: logs) {
			if (log.isLoginSuccessful()) {
				return;
			}
		}

		user.lock();
	}
	
	private void insertApiCallLog(LoginDetail loginDetail, IUser user, LoginAuditLog loginAuditLog) {
		UserApiCallLog userAuditLog = new UserApiCallLog();
		userAuditLog.setUser(user);
		userAuditLog.setUrl(loginDetail.getApiUrl());
		userAuditLog.setMethod(loginDetail.getRequestMethod());
		userAuditLog.setResponseCode(Integer.toString(HttpStatus.OK.value()));
		userAuditLog.setCallStartTime(Calendar.getInstance().getTime());
		userAuditLog.setCallEndTime(Calendar.getInstance().getTime());
		userAuditLog.setLoginAuditLog(loginAuditLog);
		authDaoFactory.getUserApiCallLogDao().saveOrUpdate(userAuditLog);
	}

	private LoginAuditLog insertLoginAudit(IUser user, String ipAddress, boolean loginSuccessful) {
		LoginAuditLog loginAuditLog = new LoginAuditLog();
		loginAuditLog.setUser(user);
		loginAuditLog.setIpAddress(ipAddress);
		loginAuditLog.setLoginTime(Calendar.getInstance().getTime());
		loginAuditLog.setLogoutTime(null);
		loginAuditLog.setLoginSuccessful(loginSuccessful);
		authDaoFactory.getAuthDao().saveLoginAuditLog(loginAuditLog);
		return loginAuditLog;
	}

	private String getCredentialAttrValue(SAMLCredential credential, String attrName) {
		Attribute attr = credential.getAttributes().stream()
			.filter(a -> attrName.equals(a.getName()) || attrName.equals(a.getFriendlyName()))
			.findFirst().orElse(null);

		if (attr == null) {
			return null;
		}

		return credential.getAttributeAsString(attr.getName());
	}
}
