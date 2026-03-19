package com.iemr.tm.utils.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private static final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json");
        Map<String, String> errorResponse = Map.of("error" , "Forbidden",
        		"message","Access denied");
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}