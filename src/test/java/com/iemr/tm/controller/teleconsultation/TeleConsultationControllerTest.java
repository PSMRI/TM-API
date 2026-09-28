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
package com.iemr.tm.controller.teleconsultation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import com.google.gson.JsonObject;
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeleConsultationController Test Suite")
class TeleConsultationControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private TeleConsultationController controller;

	@Nested
	@DisplayName("benArrivalStatusUpdater")
	class ArrivalStatusTests {

		@Test
		@DisplayName("benArrivalStatusUpdater should confirm the update when a row was changed")
		void benArrivalStatusUpdater_shouldConfirmUpdate() throws Exception {
			when(teleConsultationServiceImpl.updateBeneficiaryArrivalStatus(REQUEST)).thenReturn(1);

			assertTrue(controller.benArrivalStatusUpdater(REQUEST)
					.contains("Beneficiary arrival status updated successfully."));
		}

		@Test
		@DisplayName("benArrivalStatusUpdater should report a failure when no row was changed")
		void benArrivalStatusUpdater_shouldReportFailureWhenNothingChanged() throws Exception {
			when(teleConsultationServiceImpl.updateBeneficiaryArrivalStatus(REQUEST)).thenReturn(0);

			assertTrue(controller.benArrivalStatusUpdater(REQUEST)
					.contains("Error in updating beneficiary arrival status."));
		}

		@Test
		@DisplayName("benArrivalStatusUpdater should reject a null request")
		void benArrivalStatusUpdater_shouldRejectNullRequest() throws Exception {
			String result = controller.benArrivalStatusUpdater(null);

			assertTrue(result.contains("Invalid request"));
			verify(teleConsultationServiceImpl, never()).updateBeneficiaryArrivalStatus(anyString());
		}

		@Test
		@DisplayName("benArrivalStatusUpdater should surface a service failure")
		void benArrivalStatusUpdater_shouldSurfaceServiceFailure() throws Exception {
			when(teleConsultationServiceImpl.updateBeneficiaryArrivalStatus(REQUEST))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.benArrivalStatusUpdater(REQUEST)
					.contains("Error while updating beneficiary arrival status."));
		}
	}

	@Nested
	@DisplayName("updateBeneficiaryStatusToCancelTCRequest")
	class CancelRequestTests {

		@Test
		@DisplayName("updateBeneficiaryStatusToCancelTCRequest should confirm the cancellation")
		void cancelTCRequest_shouldConfirmCancellation() throws Exception {
			when(teleConsultationServiceImpl.updateBeneficiaryStatusToCancelTCRequest(REQUEST, AUTHORIZATION))
					.thenReturn(1);

			assertTrue(controller.updateBeneficiaryStatusToCancelTCRequest(REQUEST, AUTHORIZATION)
					.contains("Beneficiary TC request cancelled successfully."));
		}

		@Test
		@DisplayName("updateBeneficiaryStatusToCancelTCRequest should report a failed cancellation")
		void cancelTCRequest_shouldReportFailedCancellation() throws Exception {
			when(teleConsultationServiceImpl.updateBeneficiaryStatusToCancelTCRequest(REQUEST, AUTHORIZATION))
					.thenReturn(0);

			assertTrue(controller.updateBeneficiaryStatusToCancelTCRequest(REQUEST, AUTHORIZATION)
					.contains("Teleconsultation cancel request failed."));
		}

		@Test
		@DisplayName("updateBeneficiaryStatusToCancelTCRequest should reject a null request")
		void cancelTCRequest_shouldRejectNullRequest() {
			assertTrue(controller.updateBeneficiaryStatusToCancelTCRequest(null, AUTHORIZATION)
					.contains("Invalid request"));
		}

		@Test
		@DisplayName("updateBeneficiaryStatusToCancelTCRequest should surface a service failure")
		void cancelTCRequest_shouldSurfaceServiceFailure() throws Exception {
			when(teleConsultationServiceImpl.updateBeneficiaryStatusToCancelTCRequest(REQUEST, AUTHORIZATION))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateBeneficiaryStatusToCancelTCRequest(REQUEST, AUTHORIZATION)
					.contains("Error while updating beneficiary status"));
		}
	}

	@Nested
	@DisplayName("checkBeneficiaryStatusToProceedWithSpecialist")
	class CheckStatusTests {

		@Test
		@DisplayName("checkBeneficiaryStatusToProceedWithSpecialist should allow the specialist to proceed")
		void checkStatus_shouldAllowSpecialistToProceed() throws Exception {
			when(teleConsultationServiceImpl.checkBeneficiaryStatusForSpecialistTransaction(REQUEST)).thenReturn(1);

			assertTrue(controller.checkBeneficiaryStatusToProceedWithSpecialist(REQUEST)
					.contains("Specialist can proceed with beneficiary TM session."));
		}

		@Test
		@DisplayName("checkBeneficiaryStatusToProceedWithSpecialist should report a lookup failure")
		void checkStatus_shouldReportLookupFailure() throws Exception {
			when(teleConsultationServiceImpl.checkBeneficiaryStatusForSpecialistTransaction(REQUEST)).thenReturn(0);

			assertTrue(controller.checkBeneficiaryStatusToProceedWithSpecialist(REQUEST)
					.contains("Issue while fetching beneficiary status."));
		}

		@Test
		@DisplayName("checkBeneficiaryStatusToProceedWithSpecialist should reject a null request")
		void checkStatus_shouldRejectNullRequest() {
			assertTrue(controller.checkBeneficiaryStatusToProceedWithSpecialist(null).contains("Invalid request"));
		}

		@Test
		@DisplayName("checkBeneficiaryStatusToProceedWithSpecialist should surface a service failure")
		void checkStatus_shouldSurfaceServiceFailure() throws Exception {
			when(teleConsultationServiceImpl.checkBeneficiaryStatusForSpecialistTransaction(REQUEST))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.checkBeneficiaryStatusToProceedWithSpecialist(REQUEST)
					.contains("Issue while fetching beneficiary status"));
		}
	}

	@Nested
	@DisplayName("createTCRequestForBeneficiary")
	class CreateRequestTests {

		@Test
		@DisplayName("createTCRequestForBeneficiary should confirm the created request")
		void createTCRequest_shouldConfirmCreatedRequest() throws Exception {
			when(teleConsultationServiceImpl.createTCRequestFromWorkList(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenReturn(1);

			assertTrue(controller.createTCRequestForBeneficiary(REQUEST, AUTHORIZATION)
					.contains("Teleconsultation request created successfully."));
		}

		@Test
		@DisplayName("createTCRequestForBeneficiary should report a failed creation")
		void createTCRequest_shouldReportFailedCreation() throws Exception {
			when(teleConsultationServiceImpl.createTCRequestFromWorkList(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenReturn(0);

			assertTrue(controller.createTCRequestForBeneficiary(REQUEST, AUTHORIZATION)
					.contains("Issue while creating Teleconsultation request."));
		}

		@Test
		@DisplayName("createTCRequestForBeneficiary should reject a null request")
		void createTCRequest_shouldRejectNullRequest() {
			assertTrue(controller.createTCRequestForBeneficiary(null, AUTHORIZATION).contains("Invalid request"));
		}

		@Test
		@DisplayName("createTCRequestForBeneficiary should surface a service failure")
		void createTCRequest_shouldSurfaceServiceFailure() throws Exception {
			when(teleConsultationServiceImpl.createTCRequestFromWorkList(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.createTCRequestForBeneficiary(REQUEST, AUTHORIZATION)
					.contains("Issue while creating Teleconsultation request"));
		}
	}

	@Nested
	@DisplayName("getTCSpecialistWorkListNew")
	class WorkListTests {

		private static final String LIST_REQUEST = "{\"psmID\":9,\"date\":\"2024-01-15\"}";

		@Test
		@DisplayName("getTCSpecialistWorkListNew should return the request list for the specialist")
		void getTCSpecialistWorkListNew_shouldReturnRequestList() throws Exception {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(teleConsultationServiceImpl.getTCRequestListBySpecialistIdAndDate(9, 42, "2024-01-15"))
					.thenReturn("[{\"benRegID\":11}]");

			assertTrue(controller.getTCSpecialistWorkListNew(LIST_REQUEST, authentication)
					.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNew should reject a missing authentication")
		void getTCSpecialistWorkListNew_shouldRejectMissingAuthentication() throws Exception {
			String result = controller.getTCSpecialistWorkListNew(LIST_REQUEST, null);

			assertTrue(result.contains("Unauthorized access"));
			verify(teleConsultationServiceImpl, never()).getTCRequestListBySpecialistIdAndDate(anyInt(), anyInt(),
					anyString());
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNew should reject an unauthenticated principal")
		void getTCSpecialistWorkListNew_shouldRejectUnauthenticatedPrincipal() {
			when(authentication.isAuthenticated()).thenReturn(false);

			assertTrue(controller.getTCSpecialistWorkListNew(LIST_REQUEST, authentication)
					.contains("Unauthorized access"));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNew should reject a null request body")
		void getTCSpecialistWorkListNew_shouldRejectNullRequestBody() {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");

			assertTrue(controller.getTCSpecialistWorkListNew(null, authentication)
					.contains("Invalid request, either ProviderServiceMapID or RequestDate is invalid"));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNew should surface a service failure")
		void getTCSpecialistWorkListNew_shouldSurfaceServiceFailure() throws Exception {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(teleConsultationServiceImpl.getTCRequestListBySpecialistIdAndDate(9, 42, "2024-01-15"))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getTCSpecialistWorkListNew(LIST_REQUEST, authentication)
					.contains("Error while getting TC requestList"));
		}
	}

	@Nested
	@DisplayName("startconsultation")
	class StartConsultationTests {

		@Test
		@DisplayName("startconsultation should return the number of updated rows")
		void startconsultation_shouldReturnUpdatedRowCount() {
			when(teleConsultationServiceImpl.startconsultation(11L, 22L)).thenReturn(1);

			assertTrue(controller.startconsultation(REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("startconsultation should leave the generic failure when nothing comes back")
		void startconsultation_shouldLeaveGenericFailureWhenNothingReturned() {
			when(teleConsultationServiceImpl.startconsultation(11L, 22L)).thenReturn(null);

			assertTrue(controller.startconsultation(REQUEST).contains("Failed with generic error"));
		}

		@Test
		@DisplayName("startconsultation should reject a null request")
		void startconsultation_shouldRejectNullRequest() {
			assertTrue(controller.startconsultation(null)
					.contains("Invalid request, either ProviderServiceMapID or UserID or RequestDate is invalid"));
		}

		@Test
		@DisplayName("startconsultation should surface a service failure")
		void startconsultation_shouldSurfaceServiceFailure() {
			when(teleConsultationServiceImpl.startconsultation(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.startconsultation(REQUEST).contains("Error while getting TC requestList"));
		}
	}
}
