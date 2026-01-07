package com.iemr.tm.utils;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieUtil {

	public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return Optional.of(cookie.getValue());
				}
			}
		}
		return Optional.empty();
	}

	public static String getJwtTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
	    if (cookies == null) {
	        return null; // No cookies present, return null safely
	    }

	    return Arrays.stream(cookies)
	                 .filter(cookie -> "Jwttoken".equals(cookie.getName()))
	                 .map(Cookie::getValue)
	                 .findFirst()
	                 .orElse(null);
	}

	 /**
     * Get auth token from cookies (for legacy support)
     */
    public static String getAuthTokenFromCookie(HttpServletRequest request) {
        return getCookieValue(request, "Authorization").orElse(null);
    }
}
