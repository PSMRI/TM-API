package com.iemr.tm.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class RestTemplateUtil {
	public static HttpEntity<Object> createRequestEntity(Object body, String authorization) {
        
		HttpServletRequest requestHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String jwtTokenFromCookie = null;
		try {
			jwtTokenFromCookie = CookieUtil.getJwtTokenFromCookie(requestHeader);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8");
        headers.add(HttpHeaders.USER_AGENT, UserAgentContext.getUserAgent());
        headers.add(HttpHeaders.AUTHORIZATION, authorization);
        headers.add("JwtToken",requestHeader.getHeader("JwtToken"));
        headers.add(HttpHeaders.COOKIE, "Jwttoken=" + jwtTokenFromCookie);

        return new HttpEntity<>(body, headers);
    }

}
