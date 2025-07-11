/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.tm.utils.http;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import com.iemr.tm.utils.response.OutputResponse;
import com.iemr.tm.utils.sessionobject.SessionObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HTTPRequestInterceptor implements HandlerInterceptor {
	Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	private SessionObject sessionObject;

	@Autowired
	public void setSessionObject(SessionObject sessionObject) {
		this.sessionObject = sessionObject;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		boolean status = true;
		logger.debug("In preHandle we are Intercepting the Request");
		String authorization = null;
		String preAuth = request.getHeader("Authorization");
		if(null != preAuth && preAuth.contains("Bearer "))
			authorization=preAuth.replace("Bearer ", "");
		else
			authorization = preAuth;
		if (authorization == null || authorization.isEmpty()) {
	        logger.info("Authorization header is null or empty. Skipping HTTPRequestInterceptor.");
	        return true; // Allow the request to proceed without validation
	    }	
		logger.debug("RequestURI::" + request.getRequestURI() + " || Authorization ::" + authorization
				+ " || method :: " + request.getMethod());
		if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
			try {
				String[] requestURIParts = request.getRequestURI().split("/");
				String requestAPI = requestURIParts[requestURIParts.length - 1];
				switch (requestAPI) {
				case "swagger-ui.html":
					break;
				case "index.html":
					break;
				case "swagger-initializer.js":
					break;
				case "swagger-config":
					break;
				case "ui":
					break;
				case "swagger-resources":
					break;
				case "api-docs":
					break;

				case "error":
					status = false;
					break;
				default:
					break;
				}
			} catch (Exception e) {
				OutputResponse output = new OutputResponse();
				output.setError(e);
				response.getOutputStream().print(output.toString());
				response.setContentType(MediaType.APPLICATION_JSON);
				response.setContentLength(output.toString().length());
				response.setHeader("Access-Control-Allow-Origin", "*");
				status = false;
			}
		}
		return status;
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model)
			throws Exception {
		try {
			logger.debug("In postHandle we are Intercepting the Request");
			String authorization = null;
			String postAuth = request.getHeader("Authorization");
			if(null != postAuth && postAuth.contains("Bearer "))
				authorization=postAuth.replace("Bearer ", "");
			else
				authorization = postAuth;
			logger.debug("RequestURI::" + request.getRequestURI() + " || Authorization ::" + authorization);
			if (authorization != null) {
				sessionObject.updateSessionObject(authorization, sessionObject.getSessionObject(authorization));
			}
		} catch (Exception e) {
			logger.debug("postHandle failed with error " + e.getMessage());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3)
			throws Exception {
		logger.debug("In afterCompletion Request Completed");
	}
}