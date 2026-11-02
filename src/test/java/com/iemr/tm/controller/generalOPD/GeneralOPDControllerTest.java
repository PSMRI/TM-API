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
package com.iemr.tm.controller.generalOPD;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.gson.JsonObject;
import com.iemr.tm.service.generalOPD.GeneralOPDService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GeneralOPDController Test Suite")
class GeneralOPDControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private GeneralOPDService generalOPDService;

	@InjectMocks
	private GeneralOPDController controller;

	@Nested
	@DisplayName("saveBenGenOPDNurseData")
	class SaveNurseTests {

		@Test
		@DisplayName("saveBenGenOPDNurseData should return the payload produced by the service")
		void saveBenGenOPDNurseData_shouldReturnServicePayload() throws Exception {
			when(generalOPDService.saveNurseData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBenGenOPDNurseData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBenGenOPDNurseData should roll back the visit details when the service fails")
		void saveBenGenOPDNurseData_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(generalOPDService.saveNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenGenOPDNurseData(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(generalOPDService).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenGenOPDNurseData should return the untouched failure response for a null request")
		void saveBenGenOPDNurseData_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBenGenOPDNurseData(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(generalOPDService, never()).saveNurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenGenOPDDoctorData")
	class SaveDoctorTests {

		@Test
		@DisplayName("saveBenGenOPDDoctorData should confirm the save when the service returns an id")
		void saveBenGenOPDDoctorData_shouldConfirmSave() throws Exception {
			when(generalOPDService.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			assertTrue(controller.saveBenGenOPDDoctorData(REQUEST, AUTHORIZATION).contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenGenOPDDoctorData should report an unsuccessful save")
		void saveBenGenOPDDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(generalOPDService.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.saveBenGenOPDDoctorData(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenGenOPDDoctorData should surface a service failure")
		void saveBenGenOPDDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenGenOPDDoctorData(REQUEST, AUTHORIZATION).contains("doctor save failed"));
		}
	}

	@Nested
	@DisplayName("getBenVisitDetailsFrmNurseGOPD")
	class ReadVisitTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseGOPD should return the details for a complete request")
		void getBenVisitDetailsFrmNurseGOPD_shouldReturnDetails() throws Exception {
			when(generalOPDService.getBenVisitDetailsFrmNurseGOPD(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenVisitDetailsFrmNurseGOPD(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseGOPD should reject an incomplete request")
		void getBenVisitDetailsFrmNurseGOPD_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenVisitDetailsFrmNurseGOPD("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseGOPD should surface a service failure")
		void getBenVisitDetailsFrmNurseGOPD_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.getBenVisitDetailsFrmNurseGOPD(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVisitDetailsFrmNurseGOPD(VISIT_REQUEST).contains("Error while getting beneficiary visit data"));
		}
	}

	@Nested
	@DisplayName("getBenHistoryDetails")
	class ReadHistoryTests {

		@Test
		@DisplayName("getBenHistoryDetails should return the details for a complete request")
		void getBenHistoryDetails_shouldReturnDetails() throws Exception {
			when(generalOPDService.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(generalOPDService.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenHistoryDetails(VISIT_REQUEST).contains("Error while getting beneficiary history data"));
		}
	}

	@Nested
	@DisplayName("getBenVitalDetailsFrmNurse")
	class ReadVitalsTests {

		@Test
		@DisplayName("getBenVitalDetailsFrmNurse should return the details for a complete request")
		void getBenVitalDetailsFrmNurse_shouldReturnDetails() throws Exception {
			when(generalOPDService.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(generalOPDService.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVitalDetailsFrmNurse(VISIT_REQUEST).contains("Error while getting beneficiary vital data"));
		}
	}

	@Nested
	@DisplayName("getBenExaminationDetails")
	class ReadExaminationTests {

		@Test
		@DisplayName("getBenExaminationDetails should return the details for a complete request")
		void getBenExaminationDetails_shouldReturnDetails() throws Exception {
			when(generalOPDService.getExaminationDetailsData(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenExaminationDetails(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenExaminationDetails should reject an incomplete request")
		void getBenExaminationDetails_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenExaminationDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenExaminationDetails should surface a service failure")
		void getBenExaminationDetails_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.getExaminationDetailsData(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenExaminationDetails(VISIT_REQUEST).contains("Error while getting beneficiary examination data"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorGeneralOPD")
	class ReadCaseRecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorGeneralOPD should return the details for a complete request")
		void getBenCaseRecordFromDoctorGeneralOPD_shouldReturnDetails() throws Exception {
			when(generalOPDService.getBenCaseRecordFromDoctorGeneralOPD(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorGeneralOPD(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorGeneralOPD should reject an incomplete request")
		void getBenCaseRecordFromDoctorGeneralOPD_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorGeneralOPD("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorGeneralOPD should surface a service failure")
		void getBenCaseRecordFromDoctorGeneralOPD_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.getBenCaseRecordFromDoctorGeneralOPD(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorGeneralOPD(VISIT_REQUEST).contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("updateVisitNurse")
	class UpdateVisitTests {

		@Test
		@DisplayName("updateVisitNurse should confirm the update when a row was changed")
		void updateVisitNurse_shouldConfirmUpdate() throws Exception {
			when(generalOPDService.UpdateVisitDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateVisitNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateVisitNurse should report that nothing was modified")
		void updateVisitNurse_shouldReportNothingModified() throws Exception {
			when(generalOPDService.UpdateVisitDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateVisitNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVisitNurse should surface a service failure")
		void updateVisitNurse_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.UpdateVisitDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateVisitNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateHistoryNurse")
	class UpdateHistoryTests {

		@Test
		@DisplayName("updateHistoryNurse should confirm the update when a row was changed")
		void updateHistoryNurse_shouldConfirmUpdate() throws Exception {
			when(generalOPDService.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateHistoryNurse should report that nothing was modified")
		void updateHistoryNurse_shouldReportNothingModified() throws Exception {
			when(generalOPDService.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateHistoryNurse should surface a service failure")
		void updateHistoryNurse_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.updateBenHistoryDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateVitalNurse")
	class UpdateVitalsTests {

		@Test
		@DisplayName("updateVitalNurse should confirm the update when a row was changed")
		void updateVitalNurse_shouldConfirmUpdate() throws Exception {
			when(generalOPDService.updateBenVitalDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateVitalNurse should report that nothing was modified")
		void updateVitalNurse_shouldReportNothingModified() throws Exception {
			when(generalOPDService.updateBenVitalDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVitalNurse should surface a service failure")
		void updateVitalNurse_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.updateBenVitalDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateGeneralOPDExaminationNurse")
	class UpdateExaminationTests {

		@Test
		@DisplayName("updateGeneralOPDExaminationNurse should confirm the update when a row was changed")
		void updateGeneralOPDExaminationNurse_shouldConfirmUpdate() throws Exception {
			when(generalOPDService.updateBenExaminationDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateGeneralOPDExaminationNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateGeneralOPDExaminationNurse should report that nothing was modified")
		void updateGeneralOPDExaminationNurse_shouldReportNothingModified() throws Exception {
			when(generalOPDService.updateBenExaminationDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateGeneralOPDExaminationNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateGeneralOPDExaminationNurse should surface a service failure")
		void updateGeneralOPDExaminationNurse_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.updateBenExaminationDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateGeneralOPDExaminationNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateGeneralOPDDoctorData")
	class UpdateDoctorTests {

		@Test
		@DisplayName("updateGeneralOPDDoctorData should confirm the update when a row was changed")
		void updateGeneralOPDDoctorData_shouldConfirmUpdate() throws Exception {
			when(generalOPDService.updateGeneralOPDDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(1L);

			assertTrue(controller.updateGeneralOPDDoctorData(REQUEST, AUTHORIZATION).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateGeneralOPDDoctorData should report that nothing was modified")
		void updateGeneralOPDDoctorData_shouldReportNothingModified() throws Exception {
			when(generalOPDService.updateGeneralOPDDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.updateGeneralOPDDoctorData(REQUEST, AUTHORIZATION).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateGeneralOPDDoctorData should surface a service failure")
		void updateGeneralOPDDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(generalOPDService.updateGeneralOPDDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("update failed"));

			assertTrue(controller.updateGeneralOPDDoctorData(REQUEST, AUTHORIZATION).contains("update failed"));
		}
	}
}
