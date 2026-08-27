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
package com.iemr.tm.controller.login;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.iemr.tm.service.login.IemrMmuLoginServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("IemrMmuLoginController Test Suite")
class IemrMmuLoginControllerTest {

	@Mock
	private IemrMmuLoginServiceImpl iemrMmuLoginServiceImpl;

	@Mock
	private Authentication authentication;

	private IemrMmuLoginController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked login service")
	void setUp() {
		controller = new IemrMmuLoginController();
		controller.setIemrMmuLoginServiceImpl(iemrMmuLoginServiceImpl);
	}

	private void authenticateAs(String userId) {
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authentication.getPrincipal()).thenReturn(userId);
	}

	@Nested
	@DisplayName("getUserServicePointVanDetails")
	class ServicePointVanDetailsTests {

		@Test
		@DisplayName("getUserServicePointVanDetails should return the van details for an authenticated user")
		void getUserServicePointVanDetails_shouldReturnVanDetails() throws Exception {
			authenticateAs("42");
			when(iemrMmuLoginServiceImpl.getUserServicePointVanDetails(42)).thenReturn("{\"vanID\":7}");

			String result = controller.getUserServicePointVanDetails("{}", authentication);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("vanID"));
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should reject a missing authentication")
		void getUserServicePointVanDetails_shouldRejectMissingAuthentication() throws Exception {
			String result = controller.getUserServicePointVanDetails("{}", null);

			assertTrue(result.contains("Unauthorized access"));
			verify(iemrMmuLoginServiceImpl, never()).getUserServicePointVanDetails(anyInt());
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should reject an unauthenticated principal")
		void getUserServicePointVanDetails_shouldRejectUnauthenticatedPrincipal() throws Exception {
			when(authentication.isAuthenticated()).thenReturn(false);

			assertTrue(controller.getUserServicePointVanDetails("{}", authentication).contains("Unauthorized access"));
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should surface a non-numeric principal as a failure")
		void getUserServicePointVanDetails_shouldSurfaceNonNumericPrincipal() {
			authenticateAs("not-a-number");

			assertTrue(controller.getUserServicePointVanDetails("{}", authentication)
					.contains("Error while getting service points and van data"));
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should surface a service failure")
		void getUserServicePointVanDetails_shouldSurfaceServiceFailure() throws Exception {
			authenticateAs("42");
			when(iemrMmuLoginServiceImpl.getUserServicePointVanDetails(42))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getUserServicePointVanDetails("{}", authentication)
					.contains("Error while getting service points and van data"));
		}
	}

	@Nested
	@DisplayName("getServicepointVillages")
	class ServicePointVillagesTests {

		@Test
		@DisplayName("getServicepointVillages should return the villages for the service point")
		void getServicepointVillages_shouldReturnVillages() throws Exception {
			when(iemrMmuLoginServiceImpl.getServicepointVillages(3)).thenReturn("[{\"villageID\":5}]");

			assertTrue(controller.getServicepointVillages("{\"servicePointID\":3}").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getServicepointVillages should surface a request without the service point id")
		void getServicepointVillages_shouldSurfaceRequestWithoutServicePointId() {
			assertTrue(controller.getServicepointVillages("{}")
					.contains("Error while getting service points and villages"));
		}

		@Test
		@DisplayName("getServicepointVillages should surface a service failure")
		void getServicepointVillages_shouldSurfaceServiceFailure() throws Exception {
			when(iemrMmuLoginServiceImpl.getServicepointVillages(3)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getServicepointVillages("{\"servicePointID\":3}")
					.contains("Error while getting service points and villages"));
		}
	}

	@Nested
	@DisplayName("getUserVanSpDetails")
	class UserVanSpDetailsTests {

		@Test
		@DisplayName("getUserVanSpDetails should return the van and service point details")
		void getUserVanSpDetails_shouldReturnVanAndServicePointDetails() throws Exception {
			authenticateAs("42");
			when(iemrMmuLoginServiceImpl.getUserVanSpDetails(42, 9)).thenReturn("{\"vanID\":7}");

			assertTrue(controller.getUserVanSpDetails("{\"providerServiceMapID\":9}", authentication)
					.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getUserVanSpDetails should reject a missing authentication")
		void getUserVanSpDetails_shouldRejectMissingAuthentication() {
			assertTrue(controller.getUserVanSpDetails("{\"providerServiceMapID\":9}", null)
					.contains("Unauthorized access"));
		}

		@Test
		@DisplayName("getUserVanSpDetails should reject a request without providerServiceMapID")
		void getUserVanSpDetails_shouldRejectRequestWithoutProviderServiceMapId() throws Exception {
			authenticateAs("42");

			String result = controller.getUserVanSpDetails("{}", authentication);

			assertTrue(result.contains("Invalid request"));
			verify(iemrMmuLoginServiceImpl, never()).getUserVanSpDetails(anyInt(), anyInt());
		}

		@Test
		@DisplayName("getUserVanSpDetails should surface a service failure")
		void getUserVanSpDetails_shouldSurfaceServiceFailure() throws Exception {
			authenticateAs("42");
			when(iemrMmuLoginServiceImpl.getUserVanSpDetails(42, 9)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getUserVanSpDetails("{\"providerServiceMapID\":9}", authentication)
					.contains("Error while getting van and service points data"));
		}
	}

	@Nested
	@DisplayName("getUserSpokeDetails")
	class UserSpokeDetailsTests {

		@Test
		@DisplayName("getUserSpokeDetails should return the spoke details for the provider service map")
		void getUserSpokeDetails_shouldReturnSpokeDetails() throws Exception {
			when(iemrMmuLoginServiceImpl.getUserSpokeDetails(9)).thenReturn("[{\"spokeID\":1}]");

			assertTrue(controller.getUserSpokeDetails(9).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getUserSpokeDetails should surface a service failure")
		void getUserSpokeDetails_shouldSurfaceServiceFailure() throws Exception {
			when(iemrMmuLoginServiceImpl.getUserSpokeDetails(9)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getUserSpokeDetails(9).contains("Error occurred while fetching van master"));
		}
	}
}
