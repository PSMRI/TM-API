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
package com.iemr.tm.controller.cancerscreening;

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
import com.iemr.tm.service.cancerScreening.CSServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CancerScreeningController Test Suite")
class CancerScreeningControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";
	private static final String VISIT_REQUEST = "{\"benRegID\":11,\"visitCode\":22}";

	@Mock
	private CSServiceImpl cSServiceImpl;

	private CancerScreeningController controller;

	@BeforeEach
	@DisplayName("Wire the controller with mocked services")
	void setUp() {
		controller = new CancerScreeningController();
		controller.setCancerScreeningServiceImpl(cSServiceImpl);
	}

	@Nested
	@DisplayName("saveBenCancerScreeningNurseData")
	class SaveNurseTests {

		@Test
		@DisplayName("saveBenCancerScreeningNurseData should return the payload produced by the service")
		void saveBenCancerScreeningNurseData_shouldReturnServicePayload() throws Exception {
			when(cSServiceImpl.saveCancerScreeningNurseData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn("{\"visitCode\":\"22\"}");

			String result = controller.saveBenCancerScreeningNurseData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("visitCode"));
		}

		@Test
		@DisplayName("saveBenCancerScreeningNurseData should roll back the visit details when the service fails")
		void saveBenCancerScreeningNurseData_shouldRollBackVisitDetailsOnFailure() throws Exception {
			when(cSServiceImpl.saveCancerScreeningNurseData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenCancerScreeningNurseData(REQUEST, AUTHORIZATION).contains("save failed"));
			verify(cSServiceImpl).deleteVisitDetails(any(JsonObject.class));
		}

		@Test
		@DisplayName("saveBenCancerScreeningNurseData should return the untouched failure response for a null request")
		void saveBenCancerScreeningNurseData_shouldReturnGenericFailureForNullRequest() throws Exception {
			assertTrue(controller.saveBenCancerScreeningNurseData(null, AUTHORIZATION).contains("Failed with generic error"));
			verify(cSServiceImpl, never()).saveCancerScreeningNurseData(any(JsonObject.class), anyString());
		}
	}

	@Nested
	@DisplayName("saveBenCancerScreeningDoctorData")
	class SaveDoctorTests {

		@Test
		@DisplayName("saveBenCancerScreeningDoctorData should confirm the save when the service returns an id")
		void saveBenCancerScreeningDoctorData_shouldConfirmSave() throws Exception {
			when(cSServiceImpl.saveCancerScreeningDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(7L);

			assertTrue(controller.saveBenCancerScreeningDoctorData(REQUEST, AUTHORIZATION).contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveBenCancerScreeningDoctorData should report an unsuccessful save")
		void saveBenCancerScreeningDoctorData_shouldReportUnsuccessfulSave() throws Exception {
			when(cSServiceImpl.saveCancerScreeningDoctorData(any(JsonObject.class), eq(AUTHORIZATION))).thenReturn(0L);

			assertTrue(controller.saveBenCancerScreeningDoctorData(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveBenCancerScreeningDoctorData should surface a service failure")
		void saveBenCancerScreeningDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.saveCancerScreeningDoctorData(any(JsonObject.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("doctor save failed"));

			assertTrue(controller.saveBenCancerScreeningDoctorData(REQUEST, AUTHORIZATION).contains("doctor save failed"));
		}
	}

	@Nested
	@DisplayName("getBenDataFrmNurseScrnToDocScrnVisitDetails")
	class ReadVisitTests {

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVisitDetails should return the details for a complete request")
		void getBenDataFrmNurseScrnToDocScrnVisitDetails_shouldReturnDetails() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocVisitDetailsScreen(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

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
			when(cSServiceImpl.getBenDataFrmNurseToDocVisitDetailsScreen(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVisitDetails(VISIT_REQUEST).contains("Error while getting beneficiary visit data"));
		}
	}

	@Nested
	@DisplayName("getBenDataFrmNurseScrnToDocScrnHistory")
	class ReadHistoryTests {

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnHistory should return the details for a complete request")
		void getBenDataFrmNurseScrnToDocScrnHistory_shouldReturnDetails() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocHistoryScreen(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnHistory(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnHistory should reject an incomplete request")
		void getBenDataFrmNurseScrnToDocScrnHistory_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnHistory("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnHistory should surface a service failure")
		void getBenDataFrmNurseScrnToDocScrnHistory_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocHistoryScreen(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnHistory(VISIT_REQUEST).contains("Error while getting beneficiary history data"));
		}
	}

	@Nested
	@DisplayName("getBenDataFrmNurseScrnToDocScrnVital")
	class ReadVitalsTests {

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVital should return the details for a complete request")
		void getBenDataFrmNurseScrnToDocScrnVital_shouldReturnDetails() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocVitalScreen(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVital(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVital should reject an incomplete request")
		void getBenDataFrmNurseScrnToDocScrnVital_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVital("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnVital should surface a service failure")
		void getBenDataFrmNurseScrnToDocScrnVital_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocVitalScreen(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnVital(VISIT_REQUEST).contains("Error while getting beneficiary vital data"));
		}
	}

	@Nested
	@DisplayName("getBenDataFrmNurseScrnToDocScrnExamination")
	class ReadExaminationTests {

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnExamination should return the details for a complete request")
		void getBenDataFrmNurseScrnToDocScrnExamination_shouldReturnDetails() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocExaminationScreen(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnExamination(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnExamination should reject an incomplete request")
		void getBenDataFrmNurseScrnToDocScrnExamination_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnExamination("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenDataFrmNurseScrnToDocScrnExamination should surface a service failure")
		void getBenDataFrmNurseScrnToDocScrnExamination_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenDataFrmNurseToDocExaminationScreen(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenDataFrmNurseScrnToDocScrnExamination(VISIT_REQUEST).contains("Error while getting beneficiary examination data"));
		}
	}

	@Nested
	@DisplayName("getBenCaseRecordFromDoctorCS")
	class ReadCaseRecordTests {

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCS should return the details for a complete request")
		void getBenCaseRecordFromDoctorCS_shouldReturnDetails() throws Exception {
			when(cSServiceImpl.getBenCaseRecordFromDoctorCS(BEN_REG_ID, VISIT_CODE)).thenReturn("{\"result\":1}");

			assertTrue(controller.getBenCaseRecordFromDoctorCS(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCS should reject an incomplete request")
		void getBenCaseRecordFromDoctorCS_shouldRejectIncompleteRequest() throws Exception {
			assertTrue(controller.getBenCaseRecordFromDoctorCS("{\"benRegID\":11}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCS should surface a service failure")
		void getBenCaseRecordFromDoctorCS_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenCaseRecordFromDoctorCS(BEN_REG_ID, VISIT_CODE)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCaseRecordFromDoctorCS(VISIT_REQUEST).contains("Error while getting beneficiary doctor data"));
		}
	}

	@Nested
	@DisplayName("upodateBenExaminationDetail")
	class UpdateExaminationTests {

		@Test
		@DisplayName("upodateBenExaminationDetail should confirm the update when a row was changed")
		void upodateBenExaminationDetail_shouldConfirmUpdate() throws Exception {
			when(cSServiceImpl.updateBenExaminationDetail(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.upodateBenExaminationDetail(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("upodateBenExaminationDetail should report that nothing was modified")
		void upodateBenExaminationDetail_shouldReportNothingModified() throws Exception {
			when(cSServiceImpl.updateBenExaminationDetail(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.upodateBenExaminationDetail(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("upodateBenExaminationDetail should surface a service failure")
		void upodateBenExaminationDetail_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.updateBenExaminationDetail(any(JsonObject.class))).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.upodateBenExaminationDetail(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateCancerScreeningDoctorData")
	class UpdateDoctorTests {

		@Test
		@DisplayName("updateCancerScreeningDoctorData should confirm the update when a row was changed")
		void updateCancerScreeningDoctorData_shouldConfirmUpdate() throws Exception {
			when(cSServiceImpl.updateCancerScreeningDoctorData(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateCancerScreeningDoctorData(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateCancerScreeningDoctorData should report that nothing was modified")
		void updateCancerScreeningDoctorData_shouldReportNothingModified() throws Exception {
			when(cSServiceImpl.updateCancerScreeningDoctorData(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateCancerScreeningDoctorData(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateCancerScreeningDoctorData should surface a service failure")
		void updateCancerScreeningDoctorData_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.updateCancerScreeningDoctorData(any(JsonObject.class)))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateCancerScreeningDoctorData(REQUEST).contains("db down"));
		}
	}

	@Nested
	@DisplayName("getBenCancerFamilyHistory")
	class ReadFamilyHistoryTests {

		@Test
		@DisplayName("getBenCancerFamilyHistory should return the recorded family history")
		void getBenCancerFamilyHistory_shouldReturnRecordedHistory() throws Exception {
			when(cSServiceImpl.getBenFamilyHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			String result = controller.getBenCancerFamilyHistory("{\"benRegID\":11}");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("columns"));
		}

		@Test
		@DisplayName("getBenCancerFamilyHistory should reject a request with no beneficiary")
		void getBenCancerFamilyHistory_shouldRejectRequestWithoutBeneficiary() throws Exception {
			assertTrue(controller.getBenCancerFamilyHistory("{}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCancerFamilyHistory should surface a service failure")
		void getBenCancerFamilyHistory_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenFamilyHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCancerFamilyHistory("{\"benRegID\":11}")
					.contains("Error while getting beneficiary family history data"));
		}
	}

	@Nested
	@DisplayName("getBenCancerPersonalHistory")
	class ReadPersonalHistoryTests {

		@Test
		@DisplayName("getBenCancerPersonalHistory should return the recorded personal history")
		void getBenCancerPersonalHistory_shouldReturnRecordedHistory() throws Exception {
			when(cSServiceImpl.getBenPersonalHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			String result = controller.getBenCancerPersonalHistory("{\"benRegID\":11}");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("columns"));
		}

		@Test
		@DisplayName("getBenCancerPersonalHistory should reject a request with no beneficiary")
		void getBenCancerPersonalHistory_shouldRejectRequestWithoutBeneficiary() throws Exception {
			assertTrue(controller.getBenCancerPersonalHistory("{}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCancerPersonalHistory should surface a service failure")
		void getBenCancerPersonalHistory_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenPersonalHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCancerPersonalHistory("{\"benRegID\":11}")
					.contains("Error while getting beneficiary personal history data"));
		}
	}

	@Nested
	@DisplayName("getBenCancerPersonalDietHistory")
	class ReadPersonalDietHistoryTests {

		@Test
		@DisplayName("getBenCancerPersonalDietHistory should return the recorded personal diet history")
		void getBenCancerPersonalDietHistory_shouldReturnRecordedHistory() throws Exception {
			when(cSServiceImpl.getBenPersonalDietHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			String result = controller.getBenCancerPersonalDietHistory("{\"benRegID\":11}");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("columns"));
		}

		@Test
		@DisplayName("getBenCancerPersonalDietHistory should reject a request with no beneficiary")
		void getBenCancerPersonalDietHistory_shouldRejectRequestWithoutBeneficiary() throws Exception {
			assertTrue(controller.getBenCancerPersonalDietHistory("{}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCancerPersonalDietHistory should surface a service failure")
		void getBenCancerPersonalDietHistory_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenPersonalDietHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCancerPersonalDietHistory("{\"benRegID\":11}")
					.contains("Error while getting beneficiary personal diet history data"));
		}
	}

	@Nested
	@DisplayName("getBenCancerObstetricHistory")
	class ReadObstetricHistoryTests {

		@Test
		@DisplayName("getBenCancerObstetricHistory should return the recorded obstetric history")
		void getBenCancerObstetricHistory_shouldReturnRecordedHistory() throws Exception {
			when(cSServiceImpl.getBenObstetricHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			String result = controller.getBenCancerObstetricHistory("{\"benRegID\":11}");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("columns"));
		}

		@Test
		@DisplayName("getBenCancerObstetricHistory should reject a request with no beneficiary")
		void getBenCancerObstetricHistory_shouldRejectRequestWithoutBeneficiary() throws Exception {
			assertTrue(controller.getBenCancerObstetricHistory("{}").contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenCancerObstetricHistory should surface a service failure")
		void getBenCancerObstetricHistory_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.getBenObstetricHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenCancerObstetricHistory("{\"benRegID\":11}")
					.contains("Error while getting beneficiary obstetric history data"));
		}
	}

	@Nested
	@DisplayName("updateCSHistoryNurse")
	class UpdateHistoryTests {

		@Test
		@DisplayName("updateCSHistoryNurse should confirm the updated history")
		void updateCSHistoryNurse_shouldConfirmUpdate() throws Exception {
			when(cSServiceImpl.UpdateCSHistoryNurseData(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.updateCSHistoryNurse(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateCSHistoryNurse should report history it could not modify")
		void updateCSHistoryNurse_shouldReportUnmodifiedHistory() throws Exception {
			when(cSServiceImpl.UpdateCSHistoryNurseData(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.updateCSHistoryNurse(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateCSHistoryNurse should surface a service failure")
		void updateCSHistoryNurse_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.UpdateCSHistoryNurseData(any(JsonObject.class)))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateCSHistoryNurse(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("upodateBenVitalDetail")
	class UpdateVitalsTests {

		@Test
		@DisplayName("upodateBenVitalDetail should confirm the updated vitals")
		void upodateBenVitalDetail_shouldConfirmUpdate() throws Exception {
			when(cSServiceImpl.updateBenVitalDetail(any())).thenReturn(1);

			assertTrue(controller.upodateBenVitalDetail(REQUEST).contains("Data updated successfully"));
		}

		@Test
		@DisplayName("upodateBenVitalDetail should report vitals it could not modify")
		void upodateBenVitalDetail_shouldReportUnmodifiedVitals() throws Exception {
			when(cSServiceImpl.updateBenVitalDetail(any())).thenReturn(0);

			assertTrue(controller.upodateBenVitalDetail(REQUEST).contains("Unable to modify data"));
		}

		@Test
		@DisplayName("upodateBenVitalDetail should surface a service failure")
		void upodateBenVitalDetail_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.updateBenVitalDetail(any())).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.upodateBenVitalDetail(REQUEST).contains("Unable to modify data"));
		}
	}

	@Nested
	@DisplayName("updateCancerDiagnosisDetailsByOncologist")
	class UpdateOncologistDiagnosisTests {

		@Test
		@DisplayName("updateCancerDiagnosisDetailsByOncologist should confirm the updated diagnosis")
		void updateOncologistDiagnosis_shouldConfirmUpdate() throws Exception {
			when(cSServiceImpl.updateCancerDiagnosisDetailsByOncologist(any())).thenReturn(1);

			assertTrue(controller.updateCancerDiagnosisDetailsByOncologist(REQUEST)
					.contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateCancerDiagnosisDetailsByOncologist should report a diagnosis it could not modify")
		void updateOncologistDiagnosis_shouldReportUnmodifiedDiagnosis() throws Exception {
			when(cSServiceImpl.updateCancerDiagnosisDetailsByOncologist(any())).thenReturn(0);

			assertTrue(controller.updateCancerDiagnosisDetailsByOncologist(REQUEST)
					.contains("Unable to modify data"));
		}

		@Test
		@DisplayName("updateCancerDiagnosisDetailsByOncologist should surface a service failure")
		void updateOncologistDiagnosis_shouldSurfaceServiceFailure() throws Exception {
			when(cSServiceImpl.updateCancerDiagnosisDetailsByOncologist(any()))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.updateCancerDiagnosisDetailsByOncologist(REQUEST)
					.contains("Unable to modify data"));
		}
	}
}
