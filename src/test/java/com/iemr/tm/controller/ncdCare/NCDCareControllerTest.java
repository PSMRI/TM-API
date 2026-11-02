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
package com.iemr.tm.controller.ncdCare;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.google.gson.JsonObject;
import com.iemr.tm.service.ncdCare.NCDCareServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("NCDCareController Test Suite")
class NCDCareControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String BEN_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private NCDCareServiceImpl ncdCareServiceImpl;

	private NCDCareController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked NCD care service")
	void setUp() {
		controller = new NCDCareController();
		controller.setNcdCareServiceImpl(ncdCareServiceImpl);
	}

	@Nested
	@DisplayName("saveBenNCDCareNurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("saveBenNCDCareNurseData should return the payload produced by the service")
		void saveBenNCDCareNurseData_shouldReturnServicePayload() throws Exception {
			when(ncdCareServiceImpl.saveNCDCareNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenReturn("{\"benVisitID\":5}");

			String result = controller.saveBenNCDCareNurseData("{\"benVisitID\":5}", AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("benVisitID"));
		}

		@Test
		@DisplayName("saveBenNCDCareNurseData should roll back the visit details when the service fails")
		void saveBenNCDCareNurseData_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(ncdCareServiceImpl.saveNCDCareNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			String result = controller.saveBenNCDCareNurseData("{\"benVisitID\":5}", AUTHORIZATION);

			assertTrue(result.contains("save failed"));
			verify(ncdCareServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenNCDCareNurseData should return the untouched failure response for a null request")
		void saveBenNCDCareNurseData_shouldReturnGenericFailureForNullRequest() throws Exception {
			String result = controller.saveBenNCDCareNurseData(null, AUTHORIZATION);

			assertTrue(result.contains("Failed with generic error"));
			verify(ncdCareServiceImpl, never()).saveNCDCareNurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenNCDCareDoctorData")
	class SaveDoctorDataTests {

		@Test
		@DisplayName("saveBenNCDCareDoctorData should confirm the save when the service returns an id")
		void saveBenNCDCareDoctorData_shouldConfirmSave() throws Exception {
			when(ncdCareServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			String result = controller.saveBenNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenNCDCareDoctorData should report an unsuccessful save")
		void saveBenNCDCareDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(ncdCareServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			String result = controller.saveBenNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION);

			assertTrue(result.contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenNCDCareDoctorData should report an unsuccessful save when no id comes back")
		void saveBenNCDCareDoctorData_shouldReportUnsuccessfulSaveForNullId() throws Exception {
			when(ncdCareServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(null);

			String result = controller.saveBenNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION);

			assertTrue(result.contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenNCDCareDoctorData should surface a service failure")
		void saveBenNCDCareDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION)
					.contains("doctor save failed"));
		}

		@Test
		@DisplayName("saveBenNCDCareDoctorData should surface a malformed request")
		void saveBenNCDCareDoctorData_shouldSurfaceMalformedRequest() {
			assertTrue(controller.saveBenNCDCareDoctorData("not-json", AUTHORIZATION).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests

	{
		@Test
		@DisplayName("getBenVisitDetailsFrmNurseNCDCare should return the visit details for a complete request")
		void getBenVisitDetailsFrmNurseNCDCare_shouldReturnVisitDetails() throws Exception {
			when(ncdCareServiceImpl.getBenVisitDetailsFrmNurseNCDCare(11L, 22L)).thenReturn("{\"visitCode\":22}");

			assertTrue(controller.getBenVisitDetailsFrmNurseNCDCare(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseNCDCare should reject a request with a single field")
		void getBenVisitDetailsFrmNurseNCDCare_shouldRejectSingleFieldRequest() throws Exception {
			String result = controller.getBenVisitDetailsFrmNurseNCDCare("{\"benRegID\":11}");

			assertTrue(result.contains("Invalid request"));
			verify(ncdCareServiceImpl, never()).getBenVisitDetailsFrmNurseNCDCare(anyLong(), anyLong());
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseNCDCare should surface a service failure")
		void getBenVisitDetailsFrmNurseNCDCare_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.getBenVisitDetailsFrmNurseNCDCare(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVisitDetailsFrmNurseNCDCare(BEN_REQUEST)
					.contains("Error while getting beneficiary visit data"));
		}

		@Test
		@DisplayName("getBenNCDCareHistoryDetails should return the history for a complete request")
		void getBenNCDCareHistoryDetails_shouldReturnHistory() throws Exception {
			when(ncdCareServiceImpl.getBenNCDCareHistoryDetails(11L, 22L)).thenReturn("{\"history\":[]}");

			assertTrue(controller.getBenNCDCareHistoryDetails(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenNCDCareHistoryDetails should reject a request without the visit code")
		void getBenNCDCareHistoryDetails_shouldRejectRequestWithoutVisitCode() {
			assertTrue(controller.getBenNCDCareHistoryDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenNCDCareHistoryDetails should surface a service failure")
		void getBenNCDCareHistoryDetails_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.getBenNCDCareHistoryDetails(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenNCDCareHistoryDetails(BEN_REQUEST)
					.contains("Error while getting beneficiary history data"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurseNCDCare should return the vitals for a complete request")
		void getBenVitalDetailsFrmNurseNCDCare_shouldReturnVitals() throws Exception {
			when(ncdCareServiceImpl.getBeneficiaryVitalDetails(11L, 22L)).thenReturn("{\"height\":170}");

			assertTrue(controller.getBenVitalDetailsFrmNurseNCDCare(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurseNCDCare should reject a request without the visit code")
		void getBenVitalDetailsFrmNurseNCDCare_shouldRejectRequestWithoutVisitCode() {
			assertTrue(controller.getBenVitalDetailsFrmNurseNCDCare("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenVitalDetailsFrmNurseNCDCare should surface a service failure")
		void getBenVitalDetailsFrmNurseNCDCare_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.getBeneficiaryVitalDetails(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVitalDetailsFrmNurseNCDCare(BEN_REQUEST)
					.contains("Error while getting beneficiary vital data"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should return the case record for a complete request")
		void getBenCaseRecordFromDoctorNCDCare_shouldReturnCaseRecord() throws Exception {
			when(ncdCareServiceImpl.getBenCaseRecordFromDoctorNCDCare(11L, 22L)).thenReturn("{\"diagnosis\":\"x\"}");

			assertTrue(controller.getBenCaseRecordFromDoctorNCDCare(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should reject an incomplete request")
		void getBenCaseRecordFromDoctorNCDCare_shouldRejectIncompleteRequest() {
			assertTrue(controller.getBenCaseRecordFromDoctorNCDCare("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should surface a service failure")
		void getBenCaseRecordFromDoctorNCDCare_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.getBenCaseRecordFromDoctorNCDCare(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorNCDCare(BEN_REQUEST)
					.contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("update endpoints")
	class UpdateTests {

		@Test
		@DisplayName("updateHistoryNurse should confirm the update when a row was changed")
		void updateHistoryNurse_shouldConfirmUpdate() throws Exception {
			when(ncdCareServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateHistoryNurse("{\"benRegID\":11}").contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateHistoryNurse should report that nothing was modified")
		void updateHistoryNurse_shouldReportNothingModified() throws Exception {
			when(ncdCareServiceImpl.updateBenHistoryDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateHistoryNurse("{\"benRegID\":11}").contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateHistoryNurse should surface a service failure")
		void updateHistoryNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.updateBenHistoryDetails(any(JsonObject.class)))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateHistoryNurse("{\"benRegID\":11}").contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVitalNurse should confirm the update when a row was changed")
		void updateVitalNurse_shouldConfirmUpdate() throws Exception {
			when(ncdCareServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateVitalNurse("{\"benRegID\":11}").contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateVitalNurse should report that nothing was modified")
		void updateVitalNurse_shouldReportNothingModified() throws Exception {
			when(ncdCareServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateVitalNurse("{\"benRegID\":11}").contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVitalNurse should surface a service failure")
		void updateVitalNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.updateBenVitalDetails(any(JsonObject.class)))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateVitalNurse("{\"benRegID\":11}").contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should confirm the update when a row was changed")
		void updateNCDCareDoctorData_shouldConfirmUpdate() throws Exception {
			when(ncdCareServiceImpl.updateNCDCareDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(1L);

			assertTrue(controller.updateNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION)
					.contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should report that nothing was modified")
		void updateNCDCareDoctorData_shouldReportNothingModified() throws Exception {
			when(ncdCareServiceImpl.updateNCDCareDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.updateNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION)
					.contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should report that nothing was modified when no id comes back")
		void updateNCDCareDoctorData_shouldReportNothingModifiedForNullId() throws Exception {
			when(ncdCareServiceImpl.updateNCDCareDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(null);

			assertTrue(controller.updateNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION)
					.contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should surface a service failure")
		void updateNCDCareDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(ncdCareServiceImpl.updateNCDCareDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor update failed"));

			assertTrue(controller.updateNCDCareDoctorData("{\"benRegID\":11}", AUTHORIZATION)
					.contains("doctor update failed"));
		}
	}
}
