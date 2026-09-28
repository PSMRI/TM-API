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
package com.iemr.tm.controller.videoconsultationcontroller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.iemr.tm.service.videoconsultation.VideoConsultationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoConsultationController Test Suite")
class VideoConsultationControllerTest {

	@Mock
	private VideoConsultationService videoConsultationService;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private VideoConsultationController controller;

	@Nested
	@DisplayName("login")
	class LoginTests {

		@Test
		@DisplayName("login should return the session data when the principal matches the requested user")
		void login_shouldReturnSessionDataForMatchingPrincipal() throws Exception {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(videoConsultationService.login(42L)).thenReturn("{\"sessionId\":\"abc\"}");

			assertTrue(controller.login(42L, authentication).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("login should reject a missing authentication")
		void login_shouldRejectMissingAuthentication() throws Exception {
			String result = controller.login(42L, null);

			assertTrue(result.contains("Unauthorized access"));
			verify(videoConsultationService, never()).login(anyLong());
		}

		@Test
		@DisplayName("login should reject an unauthenticated principal")
		void login_shouldRejectUnauthenticatedPrincipal() {
			when(authentication.isAuthenticated()).thenReturn(false);

			assertTrue(controller.login(42L, authentication).contains("Unauthorized access"));
		}

		@Test
		@DisplayName("login should reject a principal that does not match the requested user")
		void login_shouldRejectMismatchedPrincipal() throws Exception {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("99");

			String result = controller.login(42L, authentication);

			assertTrue(result.contains("Unauthorized access!"));
			verify(videoConsultationService, never()).login(anyLong());
		}

		@Test
		@DisplayName("login should surface a service failure")
		void login_shouldSurfaceServiceFailure() throws Exception {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(videoConsultationService.login(42L)).thenThrow(new IllegalStateException("swymed down"));

			assertTrue(controller.login(42L, authentication).contains("swymed down"));
		}
	}

	@Nested
	@DisplayName("call")
	class CallTests {

		@Test
		@DisplayName("call should return the call details produced by the service")
		void call_shouldReturnCallDetails() throws Exception {
			when(videoConsultationService.callUser(42L, 43L)).thenReturn("{\"callId\":\"c1\"}");

			assertTrue(controller.call(42L, 43L).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("call should surface a service failure")
		void call_shouldSurfaceServiceFailure() throws Exception {
			when(videoConsultationService.callUser(42L, 43L)).thenThrow(new IllegalStateException("swymed down"));

			assertTrue(controller.call(42L, 43L).contains("swymed down"));
		}

		@Test
		@DisplayName("callSwymedAndJitsi should place a video consultation call for the VideoConsultation type")
		void callSwymedAndJitsi_shouldPlaceVideoConsultationCall() throws Exception {
			when(videoConsultationService.callUser(42L, 43L)).thenReturn("{\"callId\":\"c1\"}");

			assertTrue(controller.callSwymedAndJitsi(42L, 43L, "VideoConsultation").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("callSwymedAndJitsi should fall back to a Jitsi call for any other type")
		void callSwymedAndJitsi_shouldFallBackToJitsi() throws Exception {
			when(videoConsultationService.callUserjitsi(42L, 43L)).thenReturn("{\"callId\":\"j1\"}");

			assertTrue(controller.callSwymedAndJitsi(42L, 43L, "Jitsi").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("callSwymedAndJitsi should surface a service failure")
		void callSwymedAndJitsi_shouldSurfaceServiceFailure() throws Exception {
			when(videoConsultationService.callUserjitsi(42L, 43L)).thenThrow(new IllegalStateException("jitsi down"));

			assertTrue(controller.callSwymedAndJitsi(42L, 43L, "Jitsi").contains("jitsi down"));
		}
	}

	@Nested
	@DisplayName("callvan")
	class CallVanTests {

		@Test
		@DisplayName("callvan should return the van call details")
		void callvan_shouldReturnVanCallDetails() throws Exception {
			when(videoConsultationService.callVan(42L, 7)).thenReturn("{\"callId\":\"v1\"}");

			assertTrue(controller.callvan(42L, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("callvan should surface a service failure")
		void callvan_shouldSurfaceServiceFailure() throws Exception {
			when(videoConsultationService.callVan(42L, 7)).thenThrow(new IllegalStateException("swymed down"));

			assertTrue(controller.callvan(42L, 7).contains("swymed down"));
		}

		@Test
		@DisplayName("callVanSwymedAndJitsi should place a Swymed van call for the Swymed type")
		void callVanSwymedAndJitsi_shouldPlaceSwymedVanCall() throws Exception {
			when(videoConsultationService.callVan(42L, 7)).thenReturn("{\"callId\":\"v1\"}");

			assertTrue(controller.callVanSwymedAndJitsi(42L, 7, "Swymed").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("callVanSwymedAndJitsi should fall back to a Jitsi van call for any other type")
		void callVanSwymedAndJitsi_shouldFallBackToJitsi() throws Exception {
			when(videoConsultationService.callVanJitsi(42L, 7)).thenReturn("{\"callId\":\"j1\"}");

			assertTrue(controller.callVanSwymedAndJitsi(42L, 7, "Jitsi").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("callVanSwymedAndJitsi should surface a service failure")
		void callVanSwymedAndJitsi_shouldSurfaceServiceFailure() throws Exception {
			when(videoConsultationService.callVanJitsi(42L, 7)).thenThrow(new IllegalStateException("jitsi down"));

			assertTrue(controller.callVanSwymedAndJitsi(42L, 7, "Jitsi").contains("jitsi down"));
		}
	}

	@Nested
	@DisplayName("logout")
	class LogoutTests {

		@Test
		@DisplayName("logout should return the logout confirmation from the service")
		void logout_shouldReturnLogoutConfirmation() throws Exception {
			when(videoConsultationService.logout()).thenReturn("logged out");

			assertTrue(controller.logout().contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("logout should surface a service failure")
		void logout_shouldSurfaceServiceFailure() throws Exception {
			when(videoConsultationService.logout()).thenThrow(new IllegalStateException("swymed down"));

			assertTrue(controller.logout().contains("swymed down"));
		}
	}
}
