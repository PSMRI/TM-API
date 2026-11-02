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
package com.iemr.tm.utils.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.iemr.tm.utils.exception.IEMRException;
import com.iemr.tm.utils.exception.TMException;
import com.iemr.tm.utils.exception.VideoConsultationException;

@DisplayName("OutputResponse Test Suite")
class OutputResponseTest {

	private OutputResponse response;

	@BeforeEach
	@DisplayName("Start from a freshly constructed response")
	void setUp() {
		response = new OutputResponse();
	}

	@Nested
	@DisplayName("default state")
	class DefaultStateTests {

		@Test
		@DisplayName("a new response should default to the generic failure state")
		void newResponse_shouldDefaultToGenericFailure() {
			assertEquals(OutputResponse.GENERIC_FAILURE, response.getStatusCode());
			assertEquals("Failed with generic error", response.getErrorMessage());
			assertEquals("FAILURE", response.getStatus());
			assertFalse(response.isSuccess());
		}

		@Test
		@DisplayName("a new response should carry no data")
		void newResponse_shouldCarryNoData() throws JSONException {
			assertNull(response.getData());
		}
	}

	@Nested
	@DisplayName("setResponse")
	class SetResponseTests {

		@Test
		@DisplayName("setResponse should keep a JSON object payload as-is and mark the call successful")
		void setResponse_shouldKeepJsonObjectPayload() throws JSONException {
			response.setResponse("{\"beneficiaryRegID\":123}");

			assertTrue(response.isSuccess());
			assertEquals(OutputResponse.SUCCESS, response.getStatusCode());
			assertEquals("Success", response.getErrorMessage());
			assertEquals("Success", response.getStatus());
			assertTrue(response.getData().contains("beneficiaryRegID"));
		}

		@Test
		@DisplayName("setResponse should keep a JSON array payload as-is")
		void setResponse_shouldKeepJsonArrayPayload() throws JSONException {
			response.setResponse("[{\"id\":1},{\"id\":2}]");

			assertTrue(response.isSuccess());
			assertTrue(response.getData().startsWith("["));
		}

		@Test
		@DisplayName("setResponse should wrap a plain string payload in a response envelope")
		void setResponse_shouldWrapPlainStringPayload() throws JSONException {
			response.setResponse("data saved successfully");

			assertTrue(response.isSuccess());
			assertTrue(response.getData().contains("response"));
			assertTrue(response.getData().contains("data saved successfully"));
		}

		@Test
		@DisplayName("setResponse should wrap an empty payload without failing")
		void setResponse_shouldWrapEmptyPayload() throws JSONException {
			response.setResponse("");

			assertTrue(response.isSuccess());
			assertTrue(response.getData().contains("response"));
		}
	}

	@Nested
	@DisplayName("setError(Throwable) mapping")
	class SetErrorThrowableTests {

		@Test
		@DisplayName("an IEMRException should map to the user login failure code")
		void setError_shouldMapIemrException() {
			response.setError(new IEMRException("bad credentials"));

			assertEquals(OutputResponse.USERID_FAILURE, response.getStatusCode());
			assertEquals("User login failed", response.getStatus());
			assertEquals("bad credentials", response.getErrorMessage());
		}

		@Test
		@DisplayName("a VideoConsultationException should map to the video consultation code")
		void setError_shouldMapVideoConsultationException() {
			response.setError(new VideoConsultationException("meeting unavailable"));

			assertEquals(OutputResponse.VIDEOCONSULTATION_EXCEPTION, response.getStatusCode());
			assertEquals("Video Consultation integration error", response.getStatus());
			assertEquals("meeting unavailable", response.getErrorMessage());
		}

		@Test
		@DisplayName("a TMException should map to the invalid input code")
		void setError_shouldMapTmException() {
			response.setError(new TMException("invalid visit code"));

			assertEquals(OutputResponse.TM_EXCEPTION, response.getStatusCode());
			assertEquals("Invalid input", response.getStatus());
			assertEquals("invalid visit code", response.getErrorMessage());
		}

		@Test
		@DisplayName("a JSONException should map to the object conversion failure code")
		void setError_shouldMapJsonException() {
			response.setError(new JSONException("not json"));

			assertEquals(OutputResponse.OBJECT_FAILURE, response.getStatusCode());
			assertEquals("Invalid object conversion", response.getStatus());
			assertEquals("Invalid object conversion", response.getErrorMessage());
		}

		@Test
		@DisplayName("a SQLException should map to the internal code exception")
		void setError_shouldMapSqlException() {
			response.setError(new SQLException("deadlock"));

			assertEquals(OutputResponse.CODE_EXCEPTION, response.getStatusCode());
			assertTrue(response.getStatus().startsWith("Failed with internal errors"));
			assertEquals("deadlock", response.getErrorMessage());
		}

		@Test
		@DisplayName("a ParseException should map to the internal code exception")
		void setError_shouldMapParseException() {
			response.setError(new ParseException("bad date", 0));

			assertEquals(OutputResponse.CODE_EXCEPTION, response.getStatusCode());
		}

		@Test
		@DisplayName("a NullPointerException should map to the internal code exception")
		void setError_shouldMapNullPointerException() {
			response.setError(new NullPointerException("npe"));

			assertEquals(OutputResponse.CODE_EXCEPTION, response.getStatusCode());
		}

		@Test
		@DisplayName("an IOException should map to the environment exception")
		void setError_shouldMapIoException() {
			response.setError(new IOException("disk gone"));

			assertEquals(OutputResponse.ENVIRONMENT_EXCEPTION, response.getStatusCode());
			assertTrue(response.getStatus().startsWith("Failed with connection issues"));
		}

		@Test
		@DisplayName("a ConnectException should map to the environment exception")
		void setError_shouldMapConnectException() {
			response.setError(new ConnectException("refused"));

			assertEquals(OutputResponse.ENVIRONMENT_EXCEPTION, response.getStatusCode());
		}

		@Test
		@DisplayName("an unrecognised exception should fall back to the generic failure code")
		void setError_shouldFallBackToGenericFailure() {
			response.setError(new IllegalStateException("boom"));

			assertEquals(OutputResponse.GENERIC_FAILURE, response.getStatusCode());
			assertTrue(response.getStatus().startsWith("Failed with boom"));
			assertEquals("boom", response.getErrorMessage());
		}
	}

	@Nested
	@DisplayName("setError with explicit codes")
	class SetErrorExplicitTests {

		@Test
		@DisplayName("setError(code, message, status) should apply all three values")
		void setError_shouldApplyCodeMessageAndStatus() {
			response.setError(OutputResponse.BAD_REQUEST, "missing field", "Bad Request");

			assertEquals(OutputResponse.BAD_REQUEST, response.getStatusCode());
			assertEquals("missing field", response.getErrorMessage());
			assertEquals("Bad Request", response.getStatus());
		}

		@Test
		@DisplayName("setError(code, message) should reuse the message as the status")
		void setError_shouldReuseMessageAsStatus() {
			response.setError(OutputResponse.PASSWORD_FAILURE, "wrong password");

			assertEquals(OutputResponse.PASSWORD_FAILURE, response.getStatusCode());
			assertEquals("wrong password", response.getErrorMessage());
			assertEquals("wrong password", response.getStatus());
		}
	}

	@Nested
	@DisplayName("serialisation")
	class SerialisationTests {

		@Test
		@DisplayName("toString should emit only the exposed fields")
		void toString_shouldEmitOnlyExposedFields() {
			response.setResponse("{\"id\":7}");

			String json = response.toString();

			assertTrue(json.contains("statusCode"));
			assertTrue(json.contains("errorMessage"));
			assertTrue(json.contains("data"));
			assertFalse(json.contains("logger"));
		}

		@Test
		@DisplayName("toStringWithSerialization should include null fields")
		void toStringWithSerialization_shouldIncludeNullFields() {
			String json = response.toStringWithSerialization();

			assertTrue(json.contains("\"data\":null"));
		}

		@Test
		@DisplayName("toStringWithHttpStatus should return 200 for a successful response")
		void toStringWithHttpStatus_shouldReturnOkForSuccess() {
			response.setResponse("{\"id\":7}");

			ResponseEntity<String> entity = response.toStringWithHttpStatus();

			assertEquals(HttpStatus.OK, entity.getStatusCode());
			assertTrue(entity.getBody().contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("toStringWithHttpStatus should return 500 for a generic failure")
		void toStringWithHttpStatus_shouldReturnServerErrorForGenericFailure() {
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.toStringWithHttpStatus().getStatusCode());
		}

		@Test
		@DisplayName("toStringWithHttpStatus should return 400 for a bad request")
		void toStringWithHttpStatus_shouldReturnBadRequest() {
			response.setError(OutputResponse.BAD_REQUEST, "missing field");

			assertEquals(HttpStatus.BAD_REQUEST, response.toStringWithHttpStatus().getStatusCode());
		}

		@Test
		@DisplayName("toStringWithHttpStatus should return 503 for any other status code")
		void toStringWithHttpStatus_shouldReturnServiceUnavailableForOtherCodes() {
			response.setError(new IEMRException("bad credentials"));

			assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.toStringWithHttpStatus().getStatusCode());
		}
	}
}
