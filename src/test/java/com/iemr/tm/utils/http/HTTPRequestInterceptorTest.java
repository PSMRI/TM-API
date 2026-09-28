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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.iemr.tm.utils.sessionobject.SessionObject;

@ExtendWith(MockitoExtension.class)
@DisplayName("HTTPRequestInterceptor Test Suite")
class HTTPRequestInterceptorTest {

	private static final String TOKEN = "session-token";

	@Mock
	private SessionObject sessionObject;

	private HTTPRequestInterceptor interceptor;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@BeforeEach
	@DisplayName("Wire the interceptor with a mocked session store before each test")
	void setUp() {
		interceptor = new HTTPRequestInterceptor();
		interceptor.setSessionObject(sessionObject);
		ReflectionTestUtils.setField(interceptor, "allowedOrigins", "https://amrit.example.org,http://localhost:*");
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setMethod("POST");
		request.setRequestURI("/anc/save/nurseData");
	}

	@Nested
	@DisplayName("preHandle")
	class PreHandleTests {

		@Test
		@DisplayName("preHandle should let a request without an Authorization header through untouched")
		void preHandle_shouldAllowRequestWithoutAuthorizationHeader() throws Exception {
			assertTrue(interceptor.preHandle(request, response, new Object()));
			verifyNoInteractions(sessionObject);
		}

		@Test
		@DisplayName("preHandle should let a request with a blank Authorization header through untouched")
		void preHandle_shouldAllowRequestWithBlankAuthorizationHeader() throws Exception {
			request.addHeader("Authorization", "");

			assertTrue(interceptor.preHandle(request, response, new Object()));
			verifyNoInteractions(sessionObject);
		}

		@Test
		@DisplayName("preHandle should strip the Bearer prefix and allow a normal API call")
		void preHandle_shouldStripBearerPrefixAndAllowApiCall() throws Exception {
			request.addHeader("Authorization", "Bearer " + TOKEN);

			assertTrue(interceptor.preHandle(request, response, new Object()));
		}

		@Test
		@DisplayName("preHandle should allow an OPTIONS preflight request without inspecting the URI")
		void preHandle_shouldAllowOptionsPreflight() throws Exception {
			request.setMethod("OPTIONS");
			request.addHeader("Authorization", TOKEN);

			assertTrue(interceptor.preHandle(request, response, new Object()));
		}

		@ParameterizedTest
		@ValueSource(strings = { "swagger-ui.html", "index.html", "swagger-initializer.js", "swagger-config", "ui",
				"swagger-resources", "api-docs" })
		@DisplayName("preHandle should allow the documentation endpoints")
		void preHandle_shouldAllowDocumentationEndpoints(String endpoint) throws Exception {
			request.setRequestURI("/" + endpoint);
			request.addHeader("Authorization", TOKEN);

			assertTrue(interceptor.preHandle(request, response, new Object()));
		}

		@Test
		@DisplayName("preHandle should reject the error dispatch endpoint")
		void preHandle_shouldRejectErrorEndpoint() throws Exception {
			request.setRequestURI("/error");
			request.addHeader("Authorization", TOKEN);

			assertFalse(interceptor.preHandle(request, response, new Object()));
		}
	}

	@Nested
	@DisplayName("postHandle")
	class PostHandleTests {

		@Test
		@DisplayName("postHandle should refresh the session for a bare token")
		void postHandle_shouldRefreshSessionForBareToken() throws Exception {
			request.addHeader("Authorization", TOKEN);
			when(sessionObject.getSessionObject(TOKEN)).thenReturn("session-payload");

			interceptor.postHandle(request, response, new Object(), new ModelAndView());

			verify(sessionObject).updateSessionObject(TOKEN, "session-payload");
		}

		@Test
		@DisplayName("postHandle should strip the Bearer prefix before refreshing the session")
		void postHandle_shouldStripBearerPrefixBeforeRefreshingSession() throws Exception {
			request.addHeader("Authorization", "Bearer " + TOKEN);
			when(sessionObject.getSessionObject(TOKEN)).thenReturn("session-payload");

			interceptor.postHandle(request, response, new Object(), new ModelAndView());

			verify(sessionObject).updateSessionObject(TOKEN, "session-payload");
		}

		@Test
		@DisplayName("postHandle should do nothing when no Authorization header is present")
		void postHandle_shouldDoNothingWithoutAuthorizationHeader() throws Exception {
			interceptor.postHandle(request, response, new Object(), new ModelAndView());

			verify(sessionObject, never()).updateSessionObject(anyString(), anyString());
		}

		@Test
		@DisplayName("postHandle should swallow a session store failure")
		void postHandle_shouldSwallowSessionStoreFailure() throws Exception {
			request.addHeader("Authorization", TOKEN);
			when(sessionObject.getSessionObject(TOKEN)).thenThrow(new IllegalStateException("redis down"));

			interceptor.postHandle(request, response, new Object(), new ModelAndView());

			verify(sessionObject, never()).updateSessionObject(anyString(), anyString());
		}
	}

	@Test
	@DisplayName("afterCompletion should complete without touching the response")
	void afterCompletion_shouldCompleteQuietly() throws Exception {
		interceptor.afterCompletion(request, response, new Object(), null);

		assertTrue(response.getHeaderNames().isEmpty());
	}
}
