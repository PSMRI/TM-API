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
package com.iemr.tm.controller.pnc;

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
import com.iemr.tm.service.pnc.PNCServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PostnatalCareController Test Suite")
class PostnatalCareControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private PNCServiceImpl pncServiceImpl;

	private PostnatalCareController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked service")
	void setUp() {
		controller = new PostnatalCareController();
		controller.setPncServiceImpl(pncServiceImpl);
	}

	@Nested
	@DisplayName("saveBenPNCNurseData")
	class SavenurseTests {

		@Test
		@DisplayName("saveBenPNCNurseData should return the payload produced by the service")
		void saveBenPNCNurseData_shouldReturnServicePayload() throws Exception {
			when(pncServiceImpl.savePNCNurseData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBenPNCNurseData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBenPNCNurseData should roll back the visit details when the service fails")
		void saveBenPNCNurseData_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(pncServiceImpl.savePNCNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenPNCNurseData(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(pncServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenPNCNurseData should return the untouched failure response for a null request")
		void saveBenPNCNurseData_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBenPNCNurseData(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(pncServiceImpl, never()).savePNCNurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenPNCDoctorData")
	class SavedoctorTests {

		@Test
		@DisplayName("saveBenPNCDoctorData should confirm the save when the service returns an id")
		void saveBenPNCDoctorData_shouldConfirmSave() throws Exception {
			when(pncServiceImpl.savePNCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			assertTrue(controller.saveBenPNCDoctorData(REQUEST, AUTHORIZATION).contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenPNCDoctorData should report an unsuccessful save")
		void saveBenPNCDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(pncServiceImpl.savePNCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.saveBenPNCDoctorData(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenPNCDoctorData should surface a service failure")
		void saveBenPNCDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.savePNCDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenPNCDoctorData(REQUEST, AUTHORIZATION).contains("doctor save failed"));
		}
	}

	@Nested
	@DisplayName("getBenVisitDetailsFrmNursePNC")
	class ReadvisitTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNursePNC should return the details for a complete request")
		void getBenVisitDetailsFrmNursePNC_shouldReturnDetails() throws Exception {
			when(pncServiceImpl.getBenVisitDetailsFrmNursePNC(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenVisitDetailsFrmNursePNC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNursePNC should reject an incomplete request")
		void getBenVisitDetailsFrmNursePNC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenVisitDetailsFrmNursePNC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNursePNC should surface a service failure")
		void getBenVisitDetailsFrmNursePNC_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.getBenVisitDetailsFrmNursePNC(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVisitDetailsFrmNursePNC(VISIT_REQUEST).contains("Error while getting beneficiary visit data"));
		}
	}

	@Nested
	@DisplayName("getBenPNCDetailsFrmNursePNC")
	class ReadpncTests {

		@Test
		@DisplayName("getBenPNCDetailsFrmNursePNC should return the details for a complete request")
		void getBenPNCDetailsFrmNursePNC_shouldReturnDetails() throws Exception {
			when(pncServiceImpl.getBenPNCDetailsFrmNursePNC(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenPNCDetailsFrmNursePNC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPNCDetailsFrmNursePNC should reject an incomplete request")
		void getBenPNCDetailsFrmNursePNC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenPNCDetailsFrmNursePNC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenPNCDetailsFrmNursePNC should surface a service failure")
		void getBenPNCDetailsFrmNursePNC_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.getBenPNCDetailsFrmNursePNC(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenPNCDetailsFrmNursePNC(VISIT_REQUEST).contains("Error while getting beneficiary PNC Care data"));
		}
	}

	@Nested
	@DisplayName("getBenHistoryDetails")
	class ReadhistoryTests {

		@Test
		@DisplayName("getBenHistoryDetails should return the details for a complete request")
		void getBenHistoryDetails_shouldReturnDetails() throws Exception {
			when(pncServiceImpl.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenHistoryDetails(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenHistoryDetails should reject an incomplete request")
		void getBenHistoryDetails_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenHistoryDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenHistoryDetails should surface a service failure")
		void getBenHistoryDetails_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenHistoryDetails(VISIT_REQUEST).contains("Error while getting beneficiary history data"));
		}
	}

	@Nested
	@DisplayName("getBenVitalDetailsFrmNurse")
	class ReadvitalsTests {

		@Test
		@DisplayName("getBenVitalDetailsFrmNurse should return the details for a complete request")
		void getBenVitalDetailsFrmNurse_shouldReturnDetails() throws Exception {
			when(pncServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(pncServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVitalDetailsFrmNurse(VISIT_REQUEST).contains("Error while getting beneficiary vital data"));
		}
	}

	@Nested
	@DisplayName("getBenExaminationDetailsPNC")
	class ReadexaminationTests {

		@Test
		@DisplayName("getBenExaminationDetailsPNC should return the details for a complete request")
		void getBenExaminationDetailsPNC_shouldReturnDetails() throws Exception {
			when(pncServiceImpl.getPNCExaminationDetailsData(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenExaminationDetailsPNC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenExaminationDetailsPNC should reject an incomplete request")
		void getBenExaminationDetailsPNC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenExaminationDetailsPNC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenExaminationDetailsPNC should surface a service failure")
		void getBenExaminationDetailsPNC_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.getPNCExaminationDetailsData(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenExaminationDetailsPNC(VISIT_REQUEST).contains("Error while getting beneficiary examination data"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorPNC")
	class ReadcaserecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorPNC should return the details for a complete request")
		void getBenCaseRecordFromDoctorPNC_shouldReturnDetails() throws Exception {
			when(pncServiceImpl.getBenCaseRecordFromDoctorPNC(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorPNC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorPNC should reject an incomplete request")
		void getBenCaseRecordFromDoctorPNC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorPNC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorPNC should surface a service failure")
		void getBenCaseRecordFromDoctorPNC_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.getBenCaseRecordFromDoctorPNC(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorPNC(VISIT_REQUEST).contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("updatePNCCareNurse")
	class UpdatepncTests {

		@Test
		@DisplayName("updatePNCCareNurse should confirm the update when a row was changed")
		void updatePNCCareNurse_shouldConfirmUpdate() throws Exception {
			when(pncServiceImpl.updateBenPNCDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updatePNCCareNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updatePNCCareNurse should report that nothing was modified")
		void updatePNCCareNurse_shouldReportNothingModified() throws Exception {
			when(pncServiceImpl.updateBenPNCDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updatePNCCareNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updatePNCCareNurse should surface a service failure")
		void updatePNCCareNurse_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.updateBenPNCDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updatePNCCareNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateHistoryNurse")
	class UpdatehistoryTests {

		@Test
		@DisplayName("updateHistoryNurse should confirm the update when a row was changed")
		void updateHistoryNurse_shouldConfirmUpdate() throws Exception {
			when(pncServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateHistoryNurse should report that nothing was modified")
		void updateHistoryNurse_shouldReportNothingModified() throws Exception {
			when(pncServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateHistoryNurse should surface a service failure")
		void updateHistoryNurse_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateVitalNurse")
	class UpdatevitalsTests {

		@Test
		@DisplayName("updateVitalNurse should confirm the update when a row was changed")
		void updateVitalNurse_shouldConfirmUpdate() throws Exception {
			when(pncServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateVitalNurse should report that nothing was modified")
		void updateVitalNurse_shouldReportNothingModified() throws Exception {
			when(pncServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVitalNurse should surface a service failure")
		void updateVitalNurse_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateGeneralOPDExaminationNurse")
	class UpdateexaminationTests {

		@Test
		@DisplayName("updateGeneralOPDExaminationNurse should confirm the update when a row was changed")
		void updateGeneralOPDExaminationNurse_shouldConfirmUpdate() throws Exception {
			when(pncServiceImpl.updateBenExaminationDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateGeneralOPDExaminationNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateGeneralOPDExaminationNurse should report that nothing was modified")
		void updateGeneralOPDExaminationNurse_shouldReportNothingModified() throws Exception {
			when(pncServiceImpl.updateBenExaminationDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateGeneralOPDExaminationNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateGeneralOPDExaminationNurse should surface a service failure")
		void updateGeneralOPDExaminationNurse_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.updateBenExaminationDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateGeneralOPDExaminationNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updatePNCDoctorData")
	class UpdatedoctorTests {

		@Test
		@DisplayName("updatePNCDoctorData should confirm the update when a row was changed")
		void updatePNCDoctorData_shouldConfirmUpdate() throws Exception {
			when(pncServiceImpl.updatePNCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(1L);

			assertTrue(controller.updatePNCDoctorData(REQUEST, AUTHORIZATION).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updatePNCDoctorData should report that nothing was modified")
		void updatePNCDoctorData_shouldReportNothingModified() throws Exception {
			when(pncServiceImpl.updatePNCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.updatePNCDoctorData(REQUEST, AUTHORIZATION).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updatePNCDoctorData should surface a service failure")
		void updatePNCDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(pncServiceImpl.updatePNCDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("update failed"));

			assertTrue(controller.updatePNCDoctorData(REQUEST, AUTHORIZATION).contains("update failed"));
		}
	}
}
