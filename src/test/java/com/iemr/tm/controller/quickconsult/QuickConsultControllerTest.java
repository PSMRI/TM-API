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
package com.iemr.tm.controller.quickconsult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.gson.JsonObject;
import com.iemr.tm.service.quickConsultation.QuickConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("QuickConsultController Test Suite")
class QuickConsultControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private QuickConsultationServiceImpl quickConsultationServiceImpl;

	private QuickConsultController controller;

	@BeforeEach
	@DisplayName("Wire the controller with mocked services")
	void setUp() {
		controller = new QuickConsultController();
		controller.setQuickConsultationServiceImpl(quickConsultationServiceImpl);
	}

	@Nested
	@DisplayName("saveBenQuickConsultDataNurse")
	class SaveNurseTests {

		@Test
		@DisplayName("saveBenQuickConsultDataNurse should return the payload produced by the service")
		void saveBenQuickConsultDataNurse_shouldReturnServicePayload() throws Exception {
			when(quickConsultationServiceImpl.quickConsultNurseDataInsert(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBenQuickConsultDataNurse(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBenQuickConsultDataNurse should roll back the visit details when the service fails")
		void saveBenQuickConsultDataNurse_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(quickConsultationServiceImpl.quickConsultNurseDataInsert(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenQuickConsultDataNurse(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(quickConsultationServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenQuickConsultDataNurse should return the untouched failure response for a null request")
		void saveBenQuickConsultDataNurse_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBenQuickConsultDataNurse(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(quickConsultationServiceImpl, never()).quickConsultNurseDataInsert(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("getBenDataFrmNurseScrnToDocScrnVisitDetails")
	class ReadVisitTests {

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVisitDetails should return the details for a complete request")
		void getBenDataFrmNurseScrnToDocScrnVisitDetails_shouldReturnDetails() throws Exception {
			when(quickConsultationServiceImpl.getBenDataFrmNurseToDocVisitDetailsScreen(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVisitDetails(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVisitDetails should reject an incomplete request")
		void getBenDataFrmNurseScrnToDocScrnVisitDetails_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVisitDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVisitDetails should surface a service failure")
		void getBenDataFrmNurseScrnToDocScrnVisitDetails_shouldSurfaceServiceFailure() throws Exception {
			when(quickConsultationServiceImpl.getBenDataFrmNurseToDocVisitDetailsScreen(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVisitDetails(VISIT_REQUEST).contains("Error while getting visit data"));
		}
	}

	@Nested
	@DisplayName("getBenVitalDetailsFrmNurse")
	class ReadVitalsTests {

		@Test
		@DisplayName("getBenVitalDetailsFrmNurse should return the details for a complete request")
		void getBenVitalDetailsFrmNurse_shouldReturnDetails() throws Exception {
			when(quickConsultationServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenVitalDetailsFrmNurse(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurse should reject an incomplete request")
		void getBenVitalDetailsFrmNurse_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenVitalDetailsFrmNurse("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurse should surface a service failure")
		void getBenVitalDetailsFrmNurse_shouldSurfaceServiceFailure() throws Exception {
			when(quickConsultationServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVitalDetailsFrmNurse(VISIT_REQUEST).contains("Error while getting vital data"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorQuickConsult")
	class ReadCaseRecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorQuickConsult should return the details for a complete request")
		void getBenCaseRecordFromDoctorQuickConsult_shouldReturnDetails() throws Exception {
			when(quickConsultationServiceImpl.getBenCaseRecordFromDoctorQuickConsult(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorQuickConsult(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorQuickConsult should reject an incomplete request")
		void getBenCaseRecordFromDoctorQuickConsult_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorQuickConsult("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorQuickConsult should surface a service failure")
		void getBenCaseRecordFromDoctorQuickConsult_shouldSurfaceServiceFailure() throws Exception {
			when(quickConsultationServiceImpl.getBenCaseRecordFromDoctorQuickConsult(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorQuickConsult(VISIT_REQUEST).contains("Error while getting doctor data"));
		}
	}
}
