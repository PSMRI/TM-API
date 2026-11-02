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
package com.iemr.tm.controller.anc;

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
import com.iemr.tm.service.anc.ANCServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AntenatalCareController Test Suite")
class AntenatalCareControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private ANCServiceImpl ancServiceImpl;

	private AntenatalCareController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked service")
	void setUp() {
		controller = new AntenatalCareController();
		controller.setAncServiceImpl(ancServiceImpl);
	}

	@Nested
	@DisplayName("saveBenANCNurseData")
	class SavenurseTests {

		@Test
		@DisplayName("saveBenANCNurseData should return the payload produced by the service")
		void saveBenANCNurseData_shouldReturnServicePayload() throws Exception {
			when(ancServiceImpl.saveANCNurseData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBenANCNurseData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBenANCNurseData should roll back the visit details when the service fails")
		void saveBenANCNurseData_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(ancServiceImpl.saveANCNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenANCNurseData(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(ancServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenANCNurseData should return the untouched failure response for a null request")
		void saveBenANCNurseData_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBenANCNurseData(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(ancServiceImpl, never()).saveANCNurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenANCDoctorData")
	class SavedoctorTests {

		@Test
		@DisplayName("saveBenANCDoctorData should confirm the save when the service returns an id")
		void saveBenANCDoctorData_shouldConfirmSave() throws Exception {
			when(ancServiceImpl.saveANCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			assertTrue(controller.saveBenANCDoctorData(REQUEST, AUTHORIZATION).contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenANCDoctorData should report an unsuccessful save")
		void saveBenANCDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(ancServiceImpl.saveANCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.saveBenANCDoctorData(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenANCDoctorData should surface a service failure")
		void saveBenANCDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.saveANCDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenANCDoctorData(REQUEST, AUTHORIZATION).contains("doctor save failed"));
		}
	}

	@Nested
	@DisplayName("getBenVisitDetailsFrmNurseANC")
	class ReadvisitTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseANC should return the details for a complete request")
		void getBenVisitDetailsFrmNurseANC_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getBenVisitDetailsFrmNurseANC(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenVisitDetailsFrmNurseANC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseANC should reject an incomplete request")
		void getBenVisitDetailsFrmNurseANC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenVisitDetailsFrmNurseANC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseANC should surface a service failure")
		void getBenVisitDetailsFrmNurseANC_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getBenVisitDetailsFrmNurseANC(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVisitDetailsFrmNurseANC(VISIT_REQUEST).contains("Error while getting beneficiary visit data"));
		}
	}

	@Nested
	@DisplayName("getBenANCDetailsFrmNurseANC")
	class ReadancTests {

		@Test
		@DisplayName("getBenANCDetailsFrmNurseANC should return the details for a complete request")
		void getBenANCDetailsFrmNurseANC_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getBenANCDetailsFrmNurseANC(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenANCDetailsFrmNurseANC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenANCDetailsFrmNurseANC should reject an incomplete request")
		void getBenANCDetailsFrmNurseANC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenANCDetailsFrmNurseANC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenANCDetailsFrmNurseANC should surface a service failure")
		void getBenANCDetailsFrmNurseANC_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getBenANCDetailsFrmNurseANC(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenANCDetailsFrmNurseANC(VISIT_REQUEST).contains("Error while getting beneficiary ANC care data"));
		}
	}

	@Nested
	@DisplayName("getBenANCHistoryDetails")
	class ReadhistoryTests {

		@Test
		@DisplayName("getBenANCHistoryDetails should return the details for a complete request")
		void getBenANCHistoryDetails_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getBenANCHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenANCHistoryDetails(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenANCHistoryDetails should reject an incomplete request")
		void getBenANCHistoryDetails_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenANCHistoryDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenANCHistoryDetails should surface a service failure")
		void getBenANCHistoryDetails_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getBenANCHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenANCHistoryDetails(VISIT_REQUEST).contains("Error while getting beneficiary history data"));
		}
	}

	@Nested
	@DisplayName("getBenANCVitalDetailsFrmNurseANC")
	class ReadvitalsTests {

		@Test
		@DisplayName("getBenANCVitalDetailsFrmNurseANC should return the details for a complete request")
		void getBenANCVitalDetailsFrmNurseANC_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenANCVitalDetailsFrmNurseANC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenANCVitalDetailsFrmNurseANC should reject an incomplete request")
		void getBenANCVitalDetailsFrmNurseANC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenANCVitalDetailsFrmNurseANC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenANCVitalDetailsFrmNurseANC should surface a service failure")
		void getBenANCVitalDetailsFrmNurseANC_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenANCVitalDetailsFrmNurseANC(VISIT_REQUEST).contains("Error while getting beneficiary vital data"));
		}
	}

	@Nested
	@DisplayName("getBenExaminationDetailsANC")
	class ReadexaminationTests {

		@Test
		@DisplayName("getBenExaminationDetailsANC should return the details for a complete request")
		void getBenExaminationDetailsANC_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getANCExaminationDetailsData(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenExaminationDetailsANC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenExaminationDetailsANC should reject an incomplete request")
		void getBenExaminationDetailsANC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenExaminationDetailsANC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenExaminationDetailsANC should surface a service failure")
		void getBenExaminationDetailsANC_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getANCExaminationDetailsData(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenExaminationDetailsANC(VISIT_REQUEST).contains("Error while getting beneficiary examination data"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorANC")
	class ReadcaserecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorANC should return the details for a complete request")
		void getBenCaseRecordFromDoctorANC_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getBenCaseRecordFromDoctorANC(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorANC(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorANC should reject an incomplete request")
		void getBenCaseRecordFromDoctorANC_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorANC("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorANC should surface a service failure")
		void getBenCaseRecordFromDoctorANC_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getBenCaseRecordFromDoctorANC(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorANC(VISIT_REQUEST).contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("getHRPStatus")
	class ReadhrpTests {

		@Test
		@DisplayName("getHRPStatus should return the details for a complete request")
		void getHRPStatus_shouldReturnDetails() throws Exception {
			when(ancServiceImpl.getHRPStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getHRPStatus(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getHRPStatus should reject an incomplete request")
		void getHRPStatus_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getHRPStatus("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getHRPStatus should surface a service failure")
		void getHRPStatus_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.getHRPStatus(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getHRPStatus(VISIT_REQUEST).contains("error in getting HRP status"));
		}
	}

	@Nested
	@DisplayName("updateANCCareNurse")
	class UpdateancTests {

		@Test
		@DisplayName("updateANCCareNurse should confirm the update when a row was changed")
		void updateANCCareNurse_shouldConfirmUpdate() throws Exception {
			when(ancServiceImpl.updateBenANCDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateANCCareNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateANCCareNurse should report that nothing was modified")
		void updateANCCareNurse_shouldReportNothingModified() throws Exception {
			when(ancServiceImpl.updateBenANCDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateANCCareNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateANCCareNurse should surface a service failure")
		void updateANCCareNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.updateBenANCDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateANCCareNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateANCHistoryNurse")
	class UpdatehistoryTests {

		@Test
		@DisplayName("updateANCHistoryNurse should confirm the update when a row was changed")
		void updateANCHistoryNurse_shouldConfirmUpdate() throws Exception {
			when(ancServiceImpl.updateBenANCHistoryDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateANCHistoryNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateANCHistoryNurse should report that nothing was modified")
		void updateANCHistoryNurse_shouldReportNothingModified() throws Exception {
			when(ancServiceImpl.updateBenANCHistoryDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateANCHistoryNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateANCHistoryNurse should surface a service failure")
		void updateANCHistoryNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.updateBenANCHistoryDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateANCHistoryNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateANCVitalNurse")
	class UpdatevitalsTests {

		@Test
		@DisplayName("updateANCVitalNurse should confirm the update when a row was changed")
		void updateANCVitalNurse_shouldConfirmUpdate() throws Exception {
			when(ancServiceImpl.updateBenANCVitalDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateANCVitalNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateANCVitalNurse should report that nothing was modified")
		void updateANCVitalNurse_shouldReportNothingModified() throws Exception {
			when(ancServiceImpl.updateBenANCVitalDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateANCVitalNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateANCVitalNurse should surface a service failure")
		void updateANCVitalNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.updateBenANCVitalDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateANCVitalNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateANCExaminationNurse")
	class UpdateexaminationTests {

		@Test
		@DisplayName("updateANCExaminationNurse should confirm the update when a row was changed")
		void updateANCExaminationNurse_shouldConfirmUpdate() throws Exception {
			when(ancServiceImpl.updateBenANCExaminationDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateANCExaminationNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateANCExaminationNurse should report that nothing was modified")
		void updateANCExaminationNurse_shouldReportNothingModified() throws Exception {
			when(ancServiceImpl.updateBenANCExaminationDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateANCExaminationNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateANCExaminationNurse should surface a service failure")
		void updateANCExaminationNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.updateBenANCExaminationDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateANCExaminationNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateANCDoctorData")
	class UpdatedoctorTests {

		@Test
		@DisplayName("updateANCDoctorData should confirm the update when a row was changed")
		void updateANCDoctorData_shouldConfirmUpdate() throws Exception {
			when(ancServiceImpl.updateANCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(1L);

			assertTrue(controller.updateANCDoctorData(REQUEST, AUTHORIZATION).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateANCDoctorData should report that nothing was modified")
		void updateANCDoctorData_shouldReportNothingModified() throws Exception {
			when(ancServiceImpl.updateANCDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.updateANCDoctorData(REQUEST, AUTHORIZATION).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateANCDoctorData should surface a service failure")
		void updateANCDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(ancServiceImpl.updateANCDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("update failed"));

			assertTrue(controller.updateANCDoctorData(REQUEST, AUTHORIZATION).contains("update failed"));
		}
	}
}
