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
package com.iemr.tm.controller.ncdscreening;

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
import com.iemr.tm.service.ncdscreening.NCDScreeningServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NCDScreeningController Test Suite")
class NCDScreeningControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private NCDScreeningServiceImpl ncdScreeningServiceImpl;

	private NCDScreeningController controller;

	@BeforeEach
	@DisplayName("Wire the controller with mocked services")
	void setUp() {
		controller = new NCDScreeningController();
		controller.setNcdScreeningServiceImpl(ncdScreeningServiceImpl);
	}

	@Nested
	@DisplayName("saveBeneficiaryNCDScreeningDetails")
	class SaveNurseTests {

		@Test
		@DisplayName("saveBeneficiaryNCDScreeningDetails should return the payload produced by the service")
		void saveBeneficiaryNCDScreeningDetails_shouldReturnServicePayload() throws Exception {
			when(ncdScreeningServiceImpl.saveNCDScreeningNurseData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBeneficiaryNCDScreeningDetails(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBeneficiaryNCDScreeningDetails should roll back the visit details when the service fails")
		void saveBeneficiaryNCDScreeningDetails_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(ncdScreeningServiceImpl.saveNCDScreeningNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBeneficiaryNCDScreeningDetails(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(ncdScreeningServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBeneficiaryNCDScreeningDetails should return the untouched failure response for a null request")
		void saveBeneficiaryNCDScreeningDetails_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBeneficiaryNCDScreeningDetails(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(ncdScreeningServiceImpl, never()).saveNCDScreeningNurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenNCDScreeningDoctorData")
	class SaveDoctorTests {

		@Test
		@DisplayName("saveBenNCDScreeningDoctorData should confirm the save when the service returns an id")
		void saveBenNCDScreeningDoctorData_shouldConfirmSave() throws Exception {
			when(ncdScreeningServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			assertTrue(controller.saveBenNCDScreeningDoctorData(REQUEST, AUTHORIZATION).contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenNCDScreeningDoctorData should report an unsuccessful save")
		void saveBenNCDScreeningDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(ncdScreeningServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.saveBenNCDScreeningDoctorData(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenNCDScreeningDoctorData should surface a service failure")
		void saveBenNCDScreeningDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.saveDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenNCDScreeningDoctorData(REQUEST, AUTHORIZATION).contains("doctor save failed"));
		}
	}

	@Nested
	@DisplayName("getNCDScreenigDetails")
	class ReadScreeningTests {

		@Test
		@DisplayName("getNCDScreenigDetails should return the details for a complete request")
		void getNCDScreenigDetails_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getNCDScreeningDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getNCDScreenigDetails(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getNCDScreenigDetails should reject an incomplete request")
		void getNCDScreenigDetails_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getNCDScreenigDetails("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getNCDScreenigDetails should surface a service failure")
		void getNCDScreenigDetails_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.getNCDScreeningDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getNCDScreenigDetails(VISIT_REQUEST).contains("Error while getting NCD Screening data"));
		}
	}

	@Nested
	@DisplayName("getNcdScreeningVisitCount")
	class ReadVisitCountTests {

		@Test
		@DisplayName("getNcdScreeningVisitCount should return the details for the beneficiary")
		void getNcdScreeningVisitCount_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getNcdScreeningVisitCnt(BEN_REG_ID)).thenReturn("{\"result\":1}");

			assertTrue(controller.getNcdScreeningVisitCount(BEN_REG_ID).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getNcdScreeningVisitCount should surface a service failure")
		void getNcdScreeningVisitCount_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.getNcdScreeningVisitCnt(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getNcdScreeningVisitCount(BEN_REG_ID).contains("Error while getting NCD screening Visit Count"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorNCDCare")
	class ReadCaseRecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should return the details for a complete request")
		void getBenCaseRecordFromDoctorNCDCare_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getBenCaseRecordFromDoctorNCDScreening(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorNCDCare(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should reject an incomplete request")
		void getBenCaseRecordFromDoctorNCDCare_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorNCDCare("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should surface a service failure")
		void getBenCaseRecordFromDoctorNCDCare_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.getBenCaseRecordFromDoctorNCDScreening(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorNCDCare(VISIT_REQUEST).contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("getBenVisitDetailsFrmNurseGOPD")
	class ReadVisitTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseGOPD should return the details for a complete request")
		void getBenVisitDetailsFrmNurseGOPD_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getBenVisitDetailsFrmNurseNCDScreening(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(ncdScreeningServiceImpl.getBenVisitDetailsFrmNurseNCDScreening(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVisitDetailsFrmNurseGOPD(VISIT_REQUEST).contains("Error while getting beneficiary visit data"));
		}
	}

	@Nested
	@DisplayName("getBenHistoryDetails")
	class ReadHistoryTests {

		@Test
		@DisplayName("getBenHistoryDetails should return the details for a complete request")
		void getBenHistoryDetails_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(ncdScreeningServiceImpl.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenHistoryDetails(VISIT_REQUEST).contains("Error while getting beneficiary history data"));
		}
	}

	@Nested
	@DisplayName("getBenVitalDetailsFrmNurse")
	class ReadVitalsTests {

		@Test
		@DisplayName("getBenVitalDetailsFrmNurse should return the details for a complete request")
		void getBenVitalDetailsFrmNurse_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(ncdScreeningServiceImpl.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenVitalDetailsFrmNurse(VISIT_REQUEST).contains("Error while getting beneficiary vital data"));
		}
	}

	@Nested
	@DisplayName("getBenIdrsDetailsFrmNurse")
	class ReadIdrsTests {

		@Test
		@DisplayName("getBenIdrsDetailsFrmNurse should return the details for a complete request")
		void getBenIdrsDetailsFrmNurse_shouldReturnDetails() throws Exception {
			when(ncdScreeningServiceImpl.getBenIdrsDetailsFrmNurse(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenIdrsDetailsFrmNurse(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenIdrsDetailsFrmNurse should reject an incomplete request")
		void getBenIdrsDetailsFrmNurse_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenIdrsDetailsFrmNurse("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenIdrsDetailsFrmNurse should surface a service failure")
		void getBenIdrsDetailsFrmNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.getBenIdrsDetailsFrmNurse(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenIdrsDetailsFrmNurse(VISIT_REQUEST).contains("Error while getting beneficiary Idrs data"));
		}
	}

	@Nested
	@DisplayName("updateBeneficiaryNCDScreeningDetails")
	class UpdateScreeningTests {

		@Test
		@DisplayName("updateBeneficiaryNCDScreeningDetails should confirm the update when a row was changed")
		void updateBeneficiaryNCDScreeningDetails_shouldConfirmUpdate() throws Exception {
			when(ncdScreeningServiceImpl.updateNurseNCDScreeningDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateBeneficiaryNCDScreeningDetails(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateBeneficiaryNCDScreeningDetails should report that nothing was modified")
		void updateBeneficiaryNCDScreeningDetails_shouldReportNothingModified() throws Exception {
			when(ncdScreeningServiceImpl.updateNurseNCDScreeningDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateBeneficiaryNCDScreeningDetails(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateBeneficiaryNCDScreeningDetails should surface a service failure")
		void updateBeneficiaryNCDScreeningDetails_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.updateNurseNCDScreeningDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateBeneficiaryNCDScreeningDetails(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateVitalNurse")
	class UpdateVitalsTests {

		@Test
		@DisplayName("updateVitalNurse should confirm the update when a row was changed")
		void updateVitalNurse_shouldConfirmUpdate() throws Exception {
			when(ncdScreeningServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateVitalNurse should report that nothing was modified")
		void updateVitalNurse_shouldReportNothingModified() throws Exception {
			when(ncdScreeningServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateVitalNurse should surface a service failure")
		void updateVitalNurse_shouldSurfaceServiceFailure() throws Exception {
			when(ncdScreeningServiceImpl.updateBenVitalDetails(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateVitalNurse(REQUEST).contains("Unable to modify data"));
		}
	}
}
