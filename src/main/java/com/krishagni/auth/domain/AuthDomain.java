
package com.krishagni.auth.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.krishagni.commons.errors.AppException;

public class AuthDomain {
	private static final Log logger = LogFactory.getLog(AuthDomain.class);

	private static Map<String, Authenticator> authenticatorMap = new HashMap<>();

	private Long id;

	private String name;

	private AuthProvider authProvider;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuthProvider(AuthProvider authProvider) {
		this.authProvider = authProvider;
	}

	public AuthProvider getAuthProvider() {
		return authProvider;
	}

	public Authenticator getAuthenticator() {
		return getAuthProviderInstance(getAuthProvider());
	}
	
	public void update(AuthDomain domain) {
		Map<String, String> newProps = domain.getAuthProvider().getProps();
		Map<String, String> oldProps = getAuthProvider().getProps();
		List<String> oldNames = new ArrayList<>(oldProps.keySet());
		
		for (Map.Entry<String, String> entry: newProps.entrySet()) {
			oldNames.remove(entry.getKey());
			oldProps.put(entry.getKey(), entry.getValue());
		}

		oldNames.forEach(name -> oldProps.remove(name));

		//
		// Removing updated domain's auth provider implementation instance from cached
		// instances so that new instance with new properties can be created
		//
		authenticatorMap.remove(domain.getAuthProvider().getAuthType());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Authenticator getAuthProviderInstance(AuthProvider authProvider) {
		try {
			Authenticator authenticator = authenticatorMap.get(authProvider.getAuthType());
			if (authenticator == null) {
				Class authImplClass = (Class) Class.forName(authProvider.getImplClass());
				authenticator = (Authenticator) authImplClass.getConstructor(Map.class).newInstance(authProvider.getProps());
				authenticatorMap.put(authProvider.getAuthType(), authenticator);
			}
			
			return authenticator;
		} catch (Exception e) {
			logger.error("Error obtaining an instance of auth provider", e);
			throw AppException.userError(AuthProviderErrorCode.INVALID_AUTH_IMPL);
		}
	}
}
