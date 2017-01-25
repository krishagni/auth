package com.krishagni.auth;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.codec.Base64;

public class Util {
	private static final Log logger = LogFactory.getLog(Util.class);

	public static String encodeToken(String token) {
		return new String(Base64.encode(token.getBytes()));
	}

	public static String decodeToken(String token) {
		return new String(Base64.decode(token.getBytes()));
	}

	public static String getTokenFromCookie(HttpServletRequest httpReq, String cookieName) {
		String cookieHdr = httpReq.getHeader("Cookie");
		if (StringUtils.isBlank(cookieHdr)) {
			return null;
		}

		String[] cookies = cookieHdr.split(";");
		String authToken = null;
		for (String cookie : cookies) {
			if (!cookie.trim().startsWith(cookieName)) {
				continue;
			}

			String[] authTokenParts = cookie.trim().split("=");
			if (authTokenParts.length == 2) {
				try {
					authToken = URLDecoder.decode(authTokenParts[1], "utf-8");
					if (authToken.startsWith("%") || isQuoted(authToken)) {
						authToken = authToken.substring(1, authToken.length() - 1);
					}
				} catch (Exception e) {
					logger.error("Error obtaining token from cookie", e);
				}

				break;
			}
		}

		return authToken;
	}

	private static boolean isQuoted(String input) {
		if (StringUtils.isBlank(input) || input.length() < 2) {
			return false;
		}

		return (input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"') ||
			(input.charAt(0) == '\'' && input.charAt(input.length() - 1) == '\'');
	}
}
