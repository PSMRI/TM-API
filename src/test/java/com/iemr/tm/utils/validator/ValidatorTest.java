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
package com.iemr.tm.utils.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.iemr.tm.utils.exception.IEMRException;
import com.iemr.tm.utils.redis.RedisSessionException;
import com.iemr.tm.utils.sessionobject.SessionObject;

@ExtendWith(MockitoExtension.class)
@DisplayName("Validator Test Suite")
class ValidatorTest {

	private static final String KEY = "session-key";
	private static final String IP = "192.168.1.100";
	private static final String OTHER_IP = "192.168.1.101";

	@Mock
	private SessionObject sessionObject;

	private Validator validator;

	@BeforeEach
	@DisplayName("Wire the validator with a mocked session store before each test")
	void setUp() {
		validator = new Validator();
		validator.setSessionObject(sessionObject);
	}

	@AfterEach
	@DisplayName("Reset the shared IP validation flag after each test")
	void tearDown() {
		ReflectionTestUtils.setField(Validator.class, "enableIPValidation", Boolean.FALSE);
	}

	private JSONObject response(String ip) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("loginIPAddress", ip);
		return obj;
	}

	@Nested
	@DisplayName("updateCacheObj")
	class UpdateCacheObjTests {

		@Test
		@DisplayName("updateCacheObj should create a session when no session exists yet")
		void updateCacheObj_shouldCreateSessionWhenNoneExists() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn(null);

			JSONObject result = validator.updateCacheObj(response(IP), KEY, "ipKey");

			assertEquals("login success", result.getString("sessionStatus"));
			assertEquals(KEY, result.getString("key"));
			verify(sessionObject).setSessionObject(anyString(), anyString());
		}

		@Test
		@DisplayName("updateCacheObj should create a session when the stored session is blank")
		void updateCacheObj_shouldCreateSessionWhenStoredSessionIsBlank() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn("   ");

			JSONObject result = validator.updateCacheObj(response(IP), KEY, "ipKey");

			assertEquals("login success", result.getString("sessionStatus"));
		}

		@Test
		@DisplayName("updateCacheObj should refresh the session when the stored IP matches")
		void updateCacheObj_shouldRefreshSessionWhenIpMatches() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn(response(IP).toString());

			JSONObject result = validator.updateCacheObj(response(IP), KEY, "ipKey");

			assertEquals("login success", result.getString("sessionStatus"));
			verify(sessionObject).setSessionObject(anyString(), anyString());
		}

		@Test
		@DisplayName("updateCacheObj should report the other login IP and not overwrite the session")
		void updateCacheObj_shouldReportOtherLoginIp() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn(response(OTHER_IP).toString());

			JSONObject result = validator.updateCacheObj(response(IP), KEY, "ipKey");

			assertEquals("login success, but user logged in from " + OTHER_IP, result.getString("sessionStatus"));
			assertFalse(result.has("loginIPAddress"));
			verify(sessionObject, never()).setSessionObject(anyString(), anyString());
		}

		@Test
		@DisplayName("updateCacheObj should recover from a Redis session failure and still create the session")
		void updateCacheObj_shouldRecoverFromRedisFailure() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenThrow(new RedisSessionException("redis down"));

			JSONObject result = validator.updateCacheObj(response(IP), KEY, "ipKey");

			assertEquals("login success", result.getString("sessionStatus"));
		}
	}

	@Nested
	@DisplayName("getSessionObject")
	class GetSessionObjectTests {

		@Test
		@DisplayName("getSessionObject should delegate to the session store")
		void getSessionObject_shouldDelegateToSessionStore() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn("payload");

			assertEquals("payload", validator.getSessionObject(KEY));
		}

		@Test
		@DisplayName("getSessionObject should propagate a Redis session failure")
		void getSessionObject_shouldPropagateRedisFailure() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenThrow(new RedisSessionException("redis down"));

			assertThrows(RedisSessionException.class, () -> validator.getSessionObject(KEY));
		}
	}

	@Nested
	@DisplayName("checkKeyExists")
	class CheckKeyExistsTests {

		@Test
		@DisplayName("checkKeyExists should accept a live session when IP validation is disabled")
		void checkKeyExists_shouldAcceptLiveSessionWhenIpValidationDisabled() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn(response(IP).toString());

			validator.checkKeyExists(KEY, OTHER_IP);
		}

		@Test
		@DisplayName("checkKeyExists should accept a matching IP when IP validation is enabled")
		void checkKeyExists_shouldAcceptMatchingIpWhenIpValidationEnabled() throws Exception {
			ReflectionTestUtils.setField(Validator.class, "enableIPValidation", Boolean.TRUE);
			when(sessionObject.getSessionObject(KEY)).thenReturn(response(IP).toString());

			validator.checkKeyExists(KEY, IP);
		}

		@Test
		@DisplayName("checkKeyExists should reject a mismatched IP when IP validation is enabled")
		void checkKeyExists_shouldRejectMismatchedIpWhenIpValidationEnabled() throws Exception {
			ReflectionTestUtils.setField(Validator.class, "enableIPValidation", Boolean.TRUE);
			when(sessionObject.getSessionObject(KEY)).thenReturn(response(IP).toString());

			IEMRException thrown = assertThrows(IEMRException.class, () -> validator.checkKeyExists(KEY, OTHER_IP));

			assertTrue(thrown.getMessage().contains("Invalid login key or session is expired"));
		}

		@Test
		@DisplayName("checkKeyExists should reject an expired session")
		void checkKeyExists_shouldRejectExpiredSession() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenReturn(null);

			assertThrows(IEMRException.class, () -> validator.checkKeyExists(KEY, IP));
		}

		@Test
		@DisplayName("checkKeyExists should reject when the session store fails")
		void checkKeyExists_shouldRejectWhenSessionStoreFails() throws Exception {
			when(sessionObject.getSessionObject(KEY)).thenThrow(new RedisSessionException("redis down"));

			assertThrows(IEMRException.class, () -> validator.checkKeyExists(KEY, IP));
		}
	}
}
