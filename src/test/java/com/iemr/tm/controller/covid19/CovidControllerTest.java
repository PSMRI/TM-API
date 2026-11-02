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
package com.iemr.tm.controller.covid19;

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
import com.iemr.tm.service.covid19.Covid19ServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CovidController Test Suite")
class CovidControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private Covid19ServiceImpl covid19ServiceImpl;

	@InjectMocks
	private CovidController controller;

	@Nested
	@DisplayName("saveBenNCDCareNurseData")
	class SaveNurseTests {

		@Test
		@DisplayName("saveBenNCDCareNurseData should return the payload produced by the service")
		void saveBenNCDCareNurseData_shouldReturnServicePayload() throws Exception {
			when(covid19ServiceImpl.saveCovid19NurseData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBenNCDCareNurseData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBenNCDCareNurseData should roll back the visit details when the service fails")
		void saveBenNCDCareNurseData_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(covid19ServiceImpl.saveCovid19NurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenNCDCareNurseData(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(covid19ServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenNCDCareNurseData should return the untouched failure response for a null request")
		void saveBenNCDCareNurseData_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBenNCDCareNurseData(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(covid19ServiceImpl, never()).saveCovid19NurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenCovidDoctorData")
	class SaveDoctorTests {

		@Test
		@DisplayName("saveBenCovidDoctorData should confirm the save when the service returns an id")
		void saveBenCovidDoctorData_shouldConfirmSave() throws Exception {
			when(covid19ServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			assertTrue(controller.saveBenCovidDoctorData(REQUEST, AUTHORIZATION).contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenCovidDoctorData should report an unsuccessful save")
		void saveBenCovidDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(covid19ServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.saveBenCovidDoctorData(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenCovidDoctorData should surface a service failure")
		void saveBenCovidDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenCovidDoctorData(REQUEST, AUTHORIZATION).contains("doctor save failed"));
		}
	}

	@Nested
	@DisplayName("getBenVisitDetailsFrmNurseCovid")
	class ReadVisitTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseCovid should return the details for a complete request")
		void getBenVisitDetailsFrmNurseCovid19_shouldReturnDetails() throws Exception {
			when(covid19ServiceImpl.getBenVisitDetailsFrmNurseCovid19(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenVisitDetailsFrmNurseCovid19(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseCovid should reject an incomplete request")
		void getBenVisitDetailsFrmNurseCovid19_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenVisitDetailsFrmNurseCovid19("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseCovid should surface a service failure")
		void getBenVisitDetailsFrmNurseCovid19_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.getBenVisitDetailsFrmNurseCovid19(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVisitDetailsFrmNurseCovid19(VISIT_REQUEST).contains("Error while getting beneficiary visit data"));
		}
	}

	@Nested
	@DisplayName("getBenCovidHistoryDetails")
	class ReadHistoryTests {

		@Test
		@DisplayName("getBenCovidHistoryDetails should return the details for a complete request")
		void getBenCovid19HistoryDetails_shouldReturnDetails() throws Exception {
			when(covid19ServiceImpl.getBenCovid19HistoryDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCovid19HistoryDetails(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCovidHistoryDetails should reject an incomplete request")
		void getBenCovid19HistoryDetails_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCovid19HistoryDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCovidHistoryDetails should surface a service failure")
		void getBenCovid19HistoryDetails_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.getBenCovid19HistoryDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCovid19HistoryDetails(VISIT_REQUEST).contains("Error while getting beneficiary history data"));
		}
	}

	@Nested
	@DisplayName("getBenVitalDetailsFrmNurseNCDCare")
	class ReadVitalsTests {

		@Test
		@DisplayName("getBenVitalDetailsFrmNurseNCDCare should return the details for a complete request")
		void getBenVitalDetailsFrmNurseNCDCare_shouldReturnDetails() throws Exception {
			when(covid19ServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenVitalDetailsFrmNurseNCDCare(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurseNCDCare should reject an incomplete request")
		void getBenVitalDetailsFrmNurseNCDCare_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenVitalDetailsFrmNurseNCDCare("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurseNCDCare should surface a service failure")
		void getBenVitalDetailsFrmNurseNCDCare_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVitalDetailsFrmNurseNCDCare(VISIT_REQUEST).contains("Error while getting beneficiary vital data"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorCovid")
	class ReadCaseRecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCovid should return the details for a complete request")
		void getBenCaseRecordFromDoctorCovid19_shouldReturnDetails() throws Exception {
			when(covid19ServiceImpl.getBenCaseRecordFromDoctorCovid19(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorCovid19(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCovid should reject an incomplete request")
		void getBenCaseRecordFromDoctorCovid19_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorCovid19("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCovid should surface a service failure")
		void getBenCaseRecordFromDoctorCovid19_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.getBenCaseRecordFromDoctorCovid19(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorCovid19(VISIT_REQUEST).contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("updateHistoryNurse")
	class UpdateHistoryTests {

		@Test
		@DisplayName("updateHistoryNurse should confirm the update when a row was changed")
		void updateHistoryNurse_shouldConfirmUpdate() throws Exception {
			when(covid19ServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateHistoryNurse should report that nothing was modified")
		void updateHistoryNurse_shouldReportNothingModified() throws Exception {
			when(covid19ServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateHistoryNurse should surface a service failure")
		void updateHistoryNurse_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateHistoryNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateVitalNurse")
	class UpdateVitalsTests {

		@Test
		@DisplayName("updateVitalNurse should confirm the update when a row was changed")
		void updateVitalNurse_shouldConfirmUpdate() throws Exception {
			when(covid19ServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateVitalNurse should report that nothing was modified")
		void updateVitalNurse_shouldReportNothingModified() throws Exception {
			when(covid19ServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVitalNurse should surface a service failure")
		void updateVitalNurse_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateCovidDoctorData")
	class UpdateDoctorTests {

		@Test
		@DisplayName("updateCovidDoctorData should confirm the update when a row was changed")
		void updateCovid19DoctorData_shouldConfirmUpdate() throws Exception {
			when(covid19ServiceImpl.updateCovid19DoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(1L);

			assertTrue(controller.updateCovid19DoctorData(REQUEST, AUTHORIZATION).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateCovidDoctorData should report that nothing was modified")
		void updateCovid19DoctorData_shouldReportNothingModified() throws Exception {
			when(covid19ServiceImpl.updateCovid19DoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.updateCovid19DoctorData(REQUEST, AUTHORIZATION).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateCovidDoctorData should surface a service failure")
		void updateCovid19DoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(covid19ServiceImpl.updateCovid19DoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("update failed"));

			assertTrue(controller.updateCovid19DoctorData(REQUEST, AUTHORIZATION).contains("update failed"));
		}
	}
}
