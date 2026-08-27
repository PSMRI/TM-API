/*
* AMRIT - Accessible Medical Records via Integrated Technologies
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

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HttpUtils Test Suite")
class HttpUtilsTest {

    private static final String URI = "http://localhost:8080/api/resource";

    @Mock
    private RestTemplate restTemplate;

    private HttpUtils httpUtils;

    @BeforeEach
    @DisplayName("Replace the internal RestTemplate with a mock before each test")
    void setUp() {
        httpUtils = new HttpUtils();
        ReflectionTestUtils.setField(httpUtils, "rest", restTemplate);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<HttpEntity<String>> captureRequest(HttpMethod method, ResponseEntity<String> reply) {
        ArgumentCaptor<HttpEntity<String>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(eq(URI), eq(method), captor.capture(), eq(String.class))).thenReturn(reply);
        return captor;
    }

    @Nested
    @DisplayName("GET requests")
    class GetTests

    {
        @Test
        @DisplayName("get should return the response body and record the status")
        void get_shouldReturnBodyAndRecordStatus() {
            when(restTemplate.exchange(eq(URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(new ResponseEntity<>("{\"ok\":true}", HttpStatus.OK));

            assertEquals("{\"ok\":true}", httpUtils.get(URI));
            assertEquals(HttpStatus.OK, httpUtils.getStatus());
        }

        @Test
        @DisplayName("get should send an empty header set when no headers are supplied")
        void get_shouldSendEmptyHeaderSetWhenNoneSupplied() {
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.GET, new ResponseEntity<>("body", HttpStatus.OK));

            httpUtils.get(URI);

            assertNull(captor.getValue().getHeaders().getFirst("Content-Type"));
            assertNull(captor.getValue().getBody());
        }

        @Test
        @DisplayName("get should record a non-OK status returned by the server")
        void get_shouldRecordNonOkStatus() {
            when(restTemplate.exchange(eq(URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

            assertNull(httpUtils.get(URI));
            assertEquals(HttpStatus.NOT_FOUND, httpUtils.getStatus());
        }

        @Test
        @DisplayName("get with headers should forward the supplied Authorization header")
        void get_shouldForwardSuppliedAuthorizationHeader() {
            HashMap<String, Object> header = new HashMap<>();
            header.put(HttpHeaders.AUTHORIZATION, "session-key-123");
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.GET, new ResponseEntity<>("body", HttpStatus.OK));

            assertEquals("body", httpUtils.get(URI, header));
            assertEquals("session-key-123",
                    captor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        }

        @Test
        @DisplayName("get with headers should forward an explicit Content-Type")
        void get_shouldForwardExplicitContentType() {
            HashMap<String, Object> header = new HashMap<>();
            header.put(HttpHeaders.CONTENT_TYPE, "application/xml");
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.GET, new ResponseEntity<>("body", HttpStatus.OK));

            httpUtils.get(URI, header);

            assertEquals("application/xml",
                    captor.getValue().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        }

        @Test
        @DisplayName("get with headers should default the Content-Type to JSON when none is supplied")
        void get_shouldDefaultContentTypeToJson() {
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.GET, new ResponseEntity<>("body", HttpStatus.OK));

            httpUtils.get(URI, new HashMap<>());

            assertEquals("application/json",
                    captor.getValue().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        }

        @Test
        @DisplayName("get should propagate a transport failure to the caller")
        void get_shouldPropagateTransportFailure() {
            when(restTemplate.exchange(eq(URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(new RestClientException("connection refused"));

            assertThrows(RestClientException.class, () -> httpUtils.get(URI));
        }
    }

    @Nested
    @DisplayName("POST requests")
    class PostTests {

        @Test
        @DisplayName("post should send the JSON payload and return the response body")
        void post_shouldSendPayloadAndReturnBody() {
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.POST, new ResponseEntity<>("created", HttpStatus.CREATED));

            assertEquals("created", httpUtils.post(URI, "{\"count\":5}"));
            assertEquals("{\"count\":5}", captor.getValue().getBody());
            assertEquals(HttpStatus.CREATED, httpUtils.getStatus());
        }

        @Test
        @DisplayName("post with headers should forward the supplied Authorization header")
        void post_shouldForwardSuppliedAuthorizationHeader() {
            HashMap<String, Object> header = new HashMap<>();
            header.put(HttpHeaders.AUTHORIZATION, "session-key-123");
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.POST, new ResponseEntity<>("created", HttpStatus.CREATED));

            assertEquals("created", httpUtils.post(URI, "{\"count\":5}", header));
            assertEquals("session-key-123",
                    captor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
            assertEquals("{\"count\":5}", captor.getValue().getBody());
        }

        @Test
        @DisplayName("post with headers should omit the Authorization header when none is supplied")
        void post_shouldOmitAuthorizationHeaderWhenNoneSupplied() {
            ArgumentCaptor<HttpEntity<String>> captor =
                    captureRequest(HttpMethod.POST, new ResponseEntity<>("created", HttpStatus.CREATED));

            httpUtils.post(URI, "{}", new HashMap<>());

            assertNull(captor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        }

        @Test
        @DisplayName("post should record a server error status")
        void post_shouldRecordServerErrorStatus() {
            when(restTemplate.exchange(eq(URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(new ResponseEntity<>("boom", HttpStatus.INTERNAL_SERVER_ERROR));

            httpUtils.post(URI, "{}");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, httpUtils.getStatus());
        }

        @Test
        @DisplayName("post should issue the request against the supplied URI with the POST method")
        void post_shouldIssueRequestWithPostMethod() {
            when(restTemplate.exchange(eq(URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(new ResponseEntity<>("created", HttpStatus.CREATED));

            httpUtils.post(URI, "{}");

            verify(restTemplate).exchange(eq(URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        }
    }

    @Nested
    @DisplayName("Status tracking")
    class StatusTests {

        @Test
        @DisplayName("getStatus should be null until a request has been made")
        void getStatus_shouldBeNullBeforeAnyRequest() {
            assertNull(httpUtils.getStatus());
        }

        @Test
        @DisplayName("setStatus should record the supplied status code")
        void setStatus_shouldRecordSuppliedStatusCode() {
            httpUtils.setStatus(HttpStatus.ACCEPTED);

            assertEquals(HttpStatus.ACCEPTED, httpUtils.getStatus());
        }
    }

    @Nested
    @DisplayName("POST with the whole response")
    class PostWithResponseEntityTests {

        @Test
        @DisplayName("postWithResponseEntity should return the whole response the server sent")
        void postWithResponseEntity_shouldReturnWholeResponse() {
            when(restTemplate.exchange(eq(URI), eq(HttpMethod.POST), org.mockito.ArgumentMatchers.any(),
                    eq(String.class))).thenReturn(new ResponseEntity<>("{\"ok\":true}", HttpStatus.OK));

            ResponseEntity<String> response = httpUtils.postWithResponseEntity(URI, "{}",
                    new java.util.HashMap<>());

            org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            org.junit.jupiter.api.Assertions.assertEquals("{\"ok\":true}", response.getBody());
        }

        @Test
        @DisplayName("postWithResponseEntity should forward the supplied authorization and api key headers")
        void postWithResponseEntity_shouldForwardSuppliedHeaders() {
            ArgumentCaptor<HttpEntity<String>> captor = captureRequest(HttpMethod.POST,
                    new ResponseEntity<>("{}", HttpStatus.OK));
            java.util.HashMap<String, Object> header = new java.util.HashMap<>();
            header.put("Authorization", "Bearer session-token");
            header.put("apiKey", "api-key-1");

            httpUtils.postWithResponseEntity(URI, "{}", header);

            org.junit.jupiter.api.Assertions.assertEquals("Bearer session-token",
                    captor.getValue().getHeaders().getFirst("Authorization"));
            org.junit.jupiter.api.Assertions.assertEquals("api-key-1",
                    captor.getValue().getHeaders().getFirst("apiKey"));
        }
    }
}
