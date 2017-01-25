package com.krishagni.auth.services.impl.saml;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.krishagni.auth.AuthConfig;
import com.krishagni.auth.Util;
import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.events.TokenDetail;
import com.krishagni.auth.repository.AuthDaoFactory;
import com.krishagni.auth.services.UserAuthenticationService;

public class SamlFilter extends FilterChainProxy {
	private static final Log logger = LogFactory.getLog(SamlFilter.class);

	private static final String SHOW_ERROR = "/#/alert";

	private AuthDaoFactory authDaoFactory;

	private AuthConfig authConfig;

	private UserAuthenticationService authService;

	public void setAuthDaoFactory(AuthDaoFactory authDaoFactory) {
		this.authDaoFactory = authDaoFactory;
	}

	public void setAuthConfig(AuthConfig authConfig) {
		this.authConfig = authConfig;
	}

	public void setAuthService(UserAuthenticationService authService) {
		this.authService = authService;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	throws IOException, ServletException {
		String appUrl = authConfig.getAppUrl();
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;

		try {
			boolean samlEnabled = enableSaml();
			if (samlEnabled && !isAuthenticated(httpReq)) {
				super.doFilter(request, response, chain);
			} else {
				httpResp.sendRedirect(appUrl + "/");
			}
		} catch (UsernameNotFoundException use) {
			httpResp.sendRedirect(appUrl + SHOW_ERROR + "?redirectTo=login&type=danger&msg=" + use.getMessage());
		} catch (Exception e) {
			logger.error("Error doing SAML based authentication", e);
			httpResp.sendRedirect(appUrl + SHOW_ERROR + "?redirectTo=login&type=danger&msg=" + e.getMessage());
		}
	}
	
	@SuppressWarnings({"deprecation" })
	public void setFilterChain(Filter generatorFilter, Map<String, Filter> filters) {
		List<SecurityFilterChain> filterChains = new ArrayList<>();
		for (Map.Entry<String, Filter> entry : filters.entrySet()) {
			RequestMatcher matcher = new AntPathRequestMatcher(entry.getKey());
			List<Filter> chainFilters = Arrays.asList(generatorFilter, entry.getValue());
			filterChains.add(new DefaultSecurityFilterChain(matcher, chainFilters));
		}

		try {
			Field field = FilterChainProxy.class.getDeclaredField("filterChains");
			field.setAccessible(true);
			field.set(this, filterChains);
		} catch (Exception e) {
			throw new RuntimeException("Error initialising filter chains", e);
		}
	}

	private boolean enableSaml() {
		if (!authConfig.isSamlEnabled()) {
			return false;
		}

		//
		// TODO: This is assuming there will be only one SAML domain
		//
		AuthDomain domain = authDaoFactory.getAuthDao().getAuthDomainByType("saml");
		if (domain != null) {
			//
			// This intialises SAML auth provider
			//
			domain.getAuthenticator();
		}

		return true;
	}

	private boolean isAuthenticated(HttpServletRequest httpReq) {
		String authToken = Util.getTokenFromCookie(httpReq, authConfig.getCookieName());
		if (authToken == null) {
			return false;
		}

		TokenDetail tokenDetail = new TokenDetail();
		tokenDetail.setToken(authToken);
		tokenDetail.setIpAddress(httpReq.getRemoteAddr());
		authService.validateToken(tokenDetail);
		return true;
	}
}