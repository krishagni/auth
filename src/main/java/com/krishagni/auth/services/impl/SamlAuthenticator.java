package com.krishagni.auth.services.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.RequestMethod;

import com.krishagni.auth.AuthConfig;
import com.krishagni.auth.services.impl.saml.SamlBootstrap;
import com.krishagni.auth.domain.Authenticator;
import com.krishagni.auth.events.LoginDetail;
import com.krishagni.auth.services.UserAuthenticationService;
import com.krishagni.commons.domain.IUser;
import com.krishagni.commons.errors.AppException;

@Configurable
public class SamlAuthenticator extends SimpleUrlAuthenticationSuccessHandler implements Authenticator {

	@Autowired
	private UserAuthenticationService userAuthService;

	@Autowired
	private AuthConfig authConfig;

	public SamlAuthenticator() {
		
	}
	
	public SamlAuthenticator(Map<String, String> props) {
		SamlBootstrap samlBootStrap = new SamlBootstrap(this, props);

		//
		// calling initialize after all beans are injected
		//
		samlBootStrap.initialize();
	}
	
	@Override
	public void authenticate(String username, String password) {
		throw AppException.serverError(new UnsupportedOperationException("Not supported for this implementation"));
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth)
	throws IOException, ServletException {

		IUser user = (IUser) auth.getPrincipal();

		LoginDetail loginDetail = new LoginDetail();
		loginDetail.setIpAddress(req.getRemoteAddr());
		loginDetail.setApiUrl(req.getRequestURI());
		loginDetail.setRequestMethod(RequestMethod.POST.name());

		String encodedToken = userAuthService.generateToken(user, loginDetail);
		Cookie cookie = new Cookie(authConfig.getCookieName(), encodedToken);
		cookie.setMaxAge(-1);
		cookie.setPath(req.getContextPath());
		resp.addCookie(cookie);
	
		getRedirectStrategy().sendRedirect(req, resp, getDefaultTargetUrl());
	}
}
