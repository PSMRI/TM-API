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
package com.iemr.tm.service.cancerScreening;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.repo.registrar.RegistrarRepoBenData;
import com.iemr.tm.repo.tc_consultation.TCRequestModelRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CSServiceImpl Test Suite")
class CSServiceImplTest {

	@Mock
	private CSNurseServiceImpl cSNurseServiceImpl;
	@Mock
	private CSDoctorServiceImpl cSDoctorServiceImpl;
	@Mock
	private CSOncologistServiceImpl csOncologistServiceImpl;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CSCarestreamServiceImpl cSCarestreamServiceImpl;
	@Mock
	private RegistrarRepoBenData registrarRepoBenData;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private TCRequestModelRepo tCRequestModelRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;

	@InjectMocks
	private CSServiceImpl service;

	@Test
	@DisplayName("saveCancerScreeningNurseData should reject a request it cannot act on")
	void saveCancerScreeningNurseData_shouldRejectRequestItCannotActOn() {
		assertThrows(Exception.class, () -> service.saveCancerScreeningNurseData(new com.google.gson.JsonObject(), "{}"));
	}

	@Test
	@DisplayName("deleteVisitDetails should answer for a well formed request")
	void deleteVisitDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.deleteVisitDetails(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("saveBenVisitDetails should answer for a well formed request")
	void saveBenVisitDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenVisitDetails(new com.iemr.tm.data.nurse.BeneficiaryVisitDetail(), org.mockito.Mockito.mock(com.iemr.tm.data.nurse.CommonUtilityClass.class)));
	}

	@Test
	@DisplayName("saveBenHistoryDetails should answer for a well formed request")
	void saveBenHistoryDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenHistoryDetails(new com.google.gson.JsonObject(), 11L, 11L));
	}

	@Test
	@DisplayName("saveBenFamilyHistoryDetails should answer for a well formed request")
	void saveBenFamilyHistoryDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenFamilyHistoryDetails());
	}

	@Test
	@DisplayName("saveBenVitalsDetails should answer for a well formed request")
	void saveBenVitalsDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenVitalsDetails(new com.google.gson.JsonObject(), 11L, 11L));
	}

	@Test
	@DisplayName("UpdateCSHistoryNurseData should answer for a well formed request")
	void UpdateCSHistoryNurseData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.UpdateCSHistoryNurseData(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("updateBenExaminationDetail should answer for a well formed request")
	void updateBenExaminationDetail_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenExaminationDetail(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("updateBenVitalDetail should answer for a well formed request")
	void updateBenVitalDetail_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenVitalDetail(new com.iemr.tm.data.nurse.BenCancerVitalDetail()));
	}

	@Test
	@DisplayName("getBenDataFrmNurseToDocVisitDetailsScreen should answer for a well formed request")
	void getBenDataFrmNurseToDocVisitDetailsScreen_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDataFrmNurseToDocVisitDetailsScreen(11L, 11L));
	}

	@Test
	@DisplayName("getBenDataFrmNurseToDocHistoryScreen should answer for a well formed request")
	void getBenDataFrmNurseToDocHistoryScreen_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDataFrmNurseToDocHistoryScreen(11L, 11L));
	}

	@Test
	@DisplayName("getBenDataFrmNurseToDocVitalScreen should answer for a well formed request")
	void getBenDataFrmNurseToDocVitalScreen_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDataFrmNurseToDocVitalScreen(11L, 11L));
	}

	@Test
	@DisplayName("getBenDataFrmNurseToDocExaminationScreen should answer for a well formed request")
	void getBenDataFrmNurseToDocExaminationScreen_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDataFrmNurseToDocExaminationScreen(11L, 11L));
	}

	@Test
	@DisplayName("saveCancerScreeningDoctorData should answer for a well formed request")
	void saveCancerScreeningDoctorData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveCancerScreeningDoctorData(new com.google.gson.JsonObject(), "{}"));
	}

	@Test
	@DisplayName("saveBenExaminationDetails should answer for a well formed request")
	void saveBenExaminationDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenExaminationDetails(new com.google.gson.JsonObject(), 11L, "{}", 11L, 11L));
	}

	@Test
	@DisplayName("saveBenDiagnosisDetails should answer for a well formed request")
	void saveBenDiagnosisDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenDiagnosisDetails(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("getCancerCasesheetData should answer for a well formed request")
	void getCancerCasesheetData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCancerCasesheetData(new org.json.JSONObject(), "{}"));
	}

	@Test
	@DisplayName("getBenDataForCaseSheet should answer for a well formed request")
	void getBenDataForCaseSheet_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDataForCaseSheet(11L, 11L, 11L, "{}"));
	}

	@Test
	@DisplayName("getBenNurseDataForCaseSheet should answer for a well formed request")
	void getBenNurseDataForCaseSheet_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenNurseDataForCaseSheet(11L, 11L));
	}

	@Test
	@DisplayName("getBenFamilyHistoryData should answer for a well formed request")
	void getBenFamilyHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenFamilyHistoryData(11L));
	}

	@Test
	@DisplayName("getBenPersonalHistoryData should answer for a well formed request")
	void getBenPersonalHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPersonalHistoryData(11L));
	}

	@Test
	@DisplayName("getBenPersonalDietHistoryData should answer for a well formed request")
	void getBenPersonalDietHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPersonalDietHistoryData(11L));
	}

	@Test
	@DisplayName("getBenObstetricHistoryData should answer for a well formed request")
	void getBenObstetricHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenObstetricHistoryData(11L));
	}

	@Test
	@DisplayName("updateCancerDiagnosisDetailsByOncologist should answer for a well formed request")
	void updateCancerDiagnosisDetailsByOncologist_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateCancerDiagnosisDetailsByOncologist(new com.iemr.tm.data.doctor.CancerDiagnosis()));
	}

	@Test
	@DisplayName("getBenDoctorDiagnosisData should answer for a well formed request")
	void getBenDoctorDiagnosisData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDoctorDiagnosisData(11L, 11L));
	}

	@Test
	@DisplayName("getBenCaseRecordFromDoctorCS should answer for a well formed request")
	void getBenCaseRecordFromDoctorCS_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenCaseRecordFromDoctorCS(11L, 11L));
	}

	@Test
	@DisplayName("updateCancerScreeningDoctorData should reject a request it cannot act on")
	void updateCancerScreeningDoctorData_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.updateCancerScreeningDoctorData(new com.google.gson.JsonObject()));
	}

	/** A nurse request carrying a visit, history, examination and vitals. */
	private com.google.gson.JsonObject nurseRequest() {
		return com.google.gson.JsonParser.parseString("{"
				+ "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\",\"sendToDoctorWorklist\":true,"
				+ "\"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                  \"visitCategory\":\"Cancer Screening\"},"
				+ "\"historyDetails\":{\"familyHistory\":{\"diseases\":[{\"cancerDiseaseType\":\"Breast\"}]},\"personalHistory\":{},"
				+ "                    \"pastObstetricHistory\":{}},"
				+ "\"examinationDetails\":{\"signsDetails\":{},\"oralDetails\":{},"
				+ "                        \"breastDetails\":{\"referredToMammogram\":false},"
				+ "                        \"abdominalDetails\":{},\"gynecologicalDetails\":{}},"
				+ "\"vitalsDetails\":{\"height_cm\":170}}").getAsJsonObject();
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("nurse data capture")
	class NurseDataCaptureTests {

		@org.junit.jupiter.api.BeforeEach
		void stubNurseCollaborators() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl.getMaxCurrentdate(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(0);
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryVisitDetails(org.mockito.ArgumentMatchers.any())).thenReturn(3L);
			org.mockito.Mockito.when(commonNurseServiceImpl.generateVisitCode(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(22L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveBenFamilyCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveBenPersonalCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveBenPersonalCancerDietHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveBenObstetricCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveBenVitalDetail(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl.saveCancerSignAndSymptomsData(
					org.mockito.ArgumentMatchers.any(com.iemr.tm.data.doctor.CancerSignAndSymptoms.class)))
					.thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerOralExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerBreastExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerAbdominalExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerGynecologicalExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);
		}

		@Test
		@DisplayName("saveCancerScreeningNurseData should save the visit, history, examination and vitals")
		void saveNurseData_shouldSaveEverySection() throws Exception {
			String result = service.saveCancerScreeningNurseData(nurseRequest(), "Bearer session-token");

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
		}

		@Test
		@DisplayName("saveCancerScreeningNurseData should report an already saved visit")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl.getMaxCurrentdate(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(1);

			assertTrue(service.saveCancerScreeningNurseData(nurseRequest(), "Bearer session-token")
					.contains("Data already saved"));
		}

		@Test
		@DisplayName("saveCancerScreeningNurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryVisitDetails(org.mockito.ArgumentMatchers.any())).thenReturn(0L);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.saveCancerScreeningNurseData(nurseRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("saveBenVisitDetails should return the new visit id and code")
		void saveBenVisitDetails_shouldReturnVisitIdAndCode() throws Exception {
			com.iemr.tm.data.nurse.BeneficiaryVisitDetail visit = new com.iemr.tm.data.nurse.BeneficiaryVisitDetail();
			visit.setBeneficiaryRegID(11L);
			visit.setVisitReason("New Chief Complaint");
			visit.setVisitCategory("Cancer Screening");
			com.iemr.tm.data.nurse.CommonUtilityClass utility = new com.iemr.tm.data.nurse.CommonUtilityClass();
			utility.setVanID(7);
			utility.setSessionID(1);

			java.util.Map<String, Long> result = service.saveBenVisitDetails(visit, utility);

			assertTrue(result.containsKey("visitID"));
			assertTrue(result.containsKey("visitCode"));
		}

		@Test
		@DisplayName("saveBenHistoryDetails should store every captured history section")
		void saveHistory_shouldStoreCapturedSections() throws Exception {
			assertTrue(service.saveBenHistoryDetails(nurseRequest(), 3L, 22L) > 0);
		}

		@Test
		@DisplayName("saveBenHistoryDetails should succeed when no history section was captured")
		void saveHistory_shouldSucceedWithoutCapturedSections() throws Exception {
			assertTrue(service.saveBenHistoryDetails(
					com.google.gson.JsonParser.parseString("{}").getAsJsonObject(), 3L, 22L) > 0);
		}

		@Test
		@DisplayName("saveBenVitalsDetails should store the captured vitals")
		void saveVitals_shouldStoreCapturedVitals() throws Exception {
			assertTrue(service.saveBenVitalsDetails(nurseRequest(), 3L, 22L) > 0);
		}

		@Test
		@DisplayName("saveBenVitalsDetails should succeed when no vitals were captured")
		void saveVitals_shouldSucceedWithoutCapturedVitals() throws Exception {
			assertTrue(service.saveBenVitalsDetails(
					com.google.gson.JsonParser.parseString("{}").getAsJsonObject(), 3L, 22L) > 0);
		}

		@Test
		@DisplayName("saveBenExaminationDetails should store every captured examination section")
		void saveExamination_shouldStoreCapturedSections() throws Exception {
			assertTrue(service.saveBenExaminationDetails(nurseRequest(), 3L, "Bearer session-token", 22L, 5L) > 0);
		}

		@Test
		@DisplayName("saveBenExaminationDetails should succeed when no examination section was captured")
		void saveExamination_shouldSucceedWithoutCapturedSections() throws Exception {
			assertTrue(service.saveBenExaminationDetails(
					com.google.gson.JsonParser.parseString("{}").getAsJsonObject(), 3L, "Bearer session-token", 22L,
					5L) > 0);
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("doctor data capture")
	class DoctorDataCaptureTests {

		private com.google.gson.JsonObject doctorRequest(boolean specialist, boolean withTcRequest) {
			StringBuilder request = new StringBuilder("{\"doctorSignatureFlag\":true,\"diagnosis\":{"
					+ "\"beneficiaryRegID\":11,\"beneficiaryID\":9,\"benVisitID\":3,\"visitCode\":22,"
					+ "\"benFlowID\":5,\"createdBy\":\"tester\",\"vanID\":7,\"serviceID\":4,"
					+ "\"provisionalDiagnosisPrimaryDoctor\":\"suspected\",\"isSpecialist\":" + specialist + "}");
			if (withTcRequest) {
				request.append(",\"tcRequest\":{\"userID\":41,\"specializationID\":3,\"walkIn\":false,"
						+ "\"allocationDate\":\"2026-08-26\",\"fromTime\":\"10:00:00\",\"toTime\":\"10:30:00\"}");
			}
			return com.google.gson.JsonParser.parseString(request.append("}").toString()).getAsJsonObject();
		}

		@org.junit.jupiter.api.BeforeEach
		void stubDoctorCollaborators() throws Exception {
			org.mockito.Mockito.when(cSDoctorServiceImpl
					.saveCancerDiagnosisData(org.mockito.ArgumentMatchers.any())).thenReturn(4L);
			org.mockito.Mockito.when(commonDoctorServiceImpl.callTmForSpecialistSlotBook(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString())).thenReturn(1);
			org.mockito.Mockito.when(teleConsultationServiceImpl
					.createTCRequest(org.mockito.ArgumentMatchers.any())).thenReturn(77L);
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyBoolean()))
					.thenReturn(1);
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataFromSpecialist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyBoolean()))
					.thenReturn(1);
			org.mockito.Mockito.when(tCRequestModelRepo.updateStatusIfConsultationCompleted(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyString())).thenReturn(1);
		}

		@Test
		@DisplayName("saveCancerScreeningDoctorData should save the diagnosis for a general doctor")
		void saveDoctorData_shouldSaveDiagnosisForGeneralDoctor() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(Long.valueOf(4L), service.saveCancerScreeningDoctorData(doctorRequest(false, false),
					"Bearer session-token"));
		}

		@Test
		@DisplayName("saveCancerScreeningDoctorData should route a specialist consultation through the flow update")
		void saveDoctorData_shouldRouteSpecialistConsultation() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(Long.valueOf(4L), service.saveCancerScreeningDoctorData(doctorRequest(true, false),
					"Bearer session-token"));
		}

		@Test
		@DisplayName("saveCancerScreeningDoctorData should book a slot and raise the teleconsultation request")
		void saveDoctorData_shouldBookSlotAndRaiseTcRequest() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(Long.valueOf(4L), service.saveCancerScreeningDoctorData(doctorRequest(false, true),
					"Bearer session-token"));

			org.mockito.Mockito.verify(teleConsultationServiceImpl).createTCRequest(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(sMSGatewayServiceImpl).smsSenderGateway(
					org.mockito.ArgumentMatchers.eq("schedule"), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyString());
		}

		@Test
		@DisplayName("saveCancerScreeningDoctorData should fail when the specialist slot could not be booked")
		void saveDoctorData_shouldFailWhenSlotBookingFails() {
			org.mockito.Mockito.when(commonDoctorServiceImpl.callTmForSpecialistSlotBook(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.saveCancerScreeningDoctorData(doctorRequest(false, true), "Bearer session-token"));
		}

		@Test
		@DisplayName("saveCancerScreeningDoctorData should fail when the beneficiary flow update fails")
		void saveDoctorData_shouldFailWhenFlowUpdateFails() {
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyBoolean()))
					.thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.saveCancerScreeningDoctorData(doctorRequest(false, false), "Bearer session-token"));
		}

		@Test
		@DisplayName("saveBenDiagnosisDetails should succeed when no diagnosis was captured")
		void saveDiagnosis_shouldSucceedWithoutCapturedDiagnosis() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(Long.valueOf(1L), service.saveBenDiagnosisDetails(
					com.google.gson.JsonParser.parseString("{}").getAsJsonObject()));
		}

		@Test
		@DisplayName("updateCancerScreeningDoctorData should close the specialist consultation")
		void updateDoctorData_shouldCloseSpecialistConsultation() throws Exception {
			org.mockito.Mockito.when(cSDoctorServiceImpl
					.updateCancerDiagnosisDetailsByDoctor(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo
					.updateBenFlowAfterTCSpcialistDoneForCanceScreening(org.mockito.ArgumentMatchers.anyLong(),
							org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong()))
					.thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateCancerScreeningDoctorData(doctorRequest(false, false)));
		}

		@Test
		@DisplayName("updateCancerScreeningDoctorData should fail when the flow status update fails")
		void updateDoctorData_shouldFailWhenFlowStatusUpdateFails() {
			org.mockito.Mockito.when(cSDoctorServiceImpl
					.updateCancerDiagnosisDetailsByDoctor(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo
					.updateBenFlowAfterTCSpcialistDoneForCanceScreening(org.mockito.ArgumentMatchers.anyLong(),
							org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong()))
					.thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateCancerScreeningDoctorData(doctorRequest(false, false)));
		}

		@Test
		@DisplayName("updateCancerScreeningDoctorData should fail when the diagnosis could not be updated")
		void updateDoctorData_shouldFailWhenDiagnosisUpdateFails() {
			org.mockito.Mockito.when(cSDoctorServiceImpl
					.updateCancerDiagnosisDetailsByDoctor(org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateCancerScreeningDoctorData(doctorRequest(false, false)));
		}

		@Test
		@DisplayName("updateCancerScreeningDoctorData should reject a null request")
		void updateDoctorData_shouldRejectNullRequest() {
			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateCancerScreeningDoctorData(null));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("captured section updates")
	class CapturedSectionUpdateTests {

		private static final String FULL_HISTORY = "{\"familyHistory\":[{\"beneficiaryRegID\":11,\"visitCode\":22,"
				+ "  \"deleted\":false,\"familyMembers\":[\"Mother\"]}],"
				+ "\"pastObstetricHistory\":{\"beneficiaryRegID\":11,\"visitCode\":22},"
				+ "\"personalHistory\":{\"beneficiaryRegID\":11,\"visitCode\":22}}";

		private static final String FULL_EXAMINATION = "{\"beneficiaryRegID\":11,\"visitCode\":22,"
				+ "\"signsDetails\":{\"cancerSignAndSymptoms\":{\"beneficiaryRegID\":11,\"visitCode\":22,"
				+ "  \"lymphNode_Enlarged\":true},"
				+ "  \"cancerLymphNodeDetails\":[{\"beneficiaryRegID\":11,\"visitCode\":22,"
				+ "    \"lymphNodeName\":\"Cervical\",\"mobility_Left\":true,\"size_Right\":\"2 cm\"}]},"
				+ "\"oralDetails\":{\"beneficiaryRegID\":11,\"visitCode\":22},"
				+ "\"breastDetails\":{\"beneficiaryRegID\":11,\"visitCode\":22},"
				+ "\"abdominalDetails\":{\"beneficiaryRegID\":11,\"visitCode\":22},"
				+ "\"gynecologicalDetails\":{\"beneficiaryRegID\":11,\"visitCode\":22},"
				+ "\"imageCoordinates\":[{\"cancerImageID\":2,\"createdBy\":\"nurse1\","
				+ "  \"markers\":[{\"xCord\":120,\"yCord\":240,\"point\":\"1\"}]}]}";

		private com.google.gson.JsonObject json(String raw) {
			return com.google.gson.JsonParser.parseString(raw).getAsJsonObject();
		}

		@org.junit.jupiter.api.BeforeEach
		void stubUpdateCollaborators() {
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateBeneficiaryFamilyCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateBenObstetricCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateBenPersonalCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateBenPersonalCancerDietHistory(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateSignAndSymptomsExaminationDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateLymphNodeExaminationDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateCancerOralDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateCancerBreastDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateCancerAbdominalExaminationDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateCancerGynecologicalExaminationDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(cSNurseServiceImpl.getCancerExaminationImageAnnotationList(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateCancerExamImgAnotasnDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1);
		}

		@Test
		@DisplayName("UpdateCSHistoryNurseData should update every captured history section")
		void updateHistory_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.UpdateCSHistoryNurseData(json(FULL_HISTORY)));

			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateBeneficiaryFamilyCancerHistory(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateBenPersonalCancerDietHistory(org.mockito.ArgumentMatchers.any());
		}

		@Test
		@DisplayName("UpdateCSHistoryNurseData should succeed when no history section was captured")
		void updateHistory_shouldSucceedWithoutCapturedSections() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.UpdateCSHistoryNurseData(json("{}")));
		}

		@Test
		@DisplayName("UpdateCSHistoryNurseData should succeed when the family history list is empty")
		void updateHistory_shouldSucceedWithEmptyFamilyHistory() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.UpdateCSHistoryNurseData(json("{\"familyHistory\":[]}")));
		}

		@Test
		@DisplayName("UpdateCSHistoryNurseData should report history it could not update")
		void updateHistory_shouldReportUnupdatedHistory() throws Exception {
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateBenObstetricCancerHistory(org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertEquals(0, service.UpdateCSHistoryNurseData(json(FULL_HISTORY)));
		}

		@Test
		@DisplayName("updateBenExaminationDetail should update every captured examination section")
		void updateExamination_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenExaminationDetail(json(FULL_EXAMINATION)));

			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateSignAndSymptomsExaminationDetails(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateLymphNodeExaminationDetails(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateCancerOralDetails(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateCancerBreastDetails(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateCancerAbdominalExaminationDetails(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateCancerGynecologicalExaminationDetails(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(cSNurseServiceImpl)
					.updateCancerExamImgAnotasnDetails(org.mockito.ArgumentMatchers.any());
		}

		@Test
		@DisplayName("updateBenExaminationDetail should succeed when no examination section was captured")
		void updateExamination_shouldSucceedWithoutCapturedSections() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenExaminationDetail(json("{}")));
		}

		@Test
		@DisplayName("updateBenExaminationDetail should report an examination it could not update")
		void updateExamination_shouldReportUnupdatedExamination() throws Exception {
			org.mockito.Mockito.when(cSNurseServiceImpl
					.updateCancerOralDetails(org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertEquals(0, service.updateBenExaminationDetail(json(FULL_EXAMINATION)));
		}

		@Test
		@DisplayName("saveBenExaminationDetails should store the lymph nodes captured with the signs")
		void saveExamination_shouldStoreLymphNodesWithSigns() throws Exception {
			org.mockito.Mockito.when(cSNurseServiceImpl.saveCancerSignAndSymptomsData(
					org.mockito.ArgumentMatchers.any(com.iemr.tm.data.doctor.CancerSignAndSymptoms.class),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl.saveLymphNodeDetails(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerOralExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerBreastExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerAbdominalExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl
					.saveCancerGynecologicalExaminationData(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(cSNurseServiceImpl.saveDocExaminationImageAnnotation(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1L);

			assertTrue(service.saveBenExaminationDetails(
					json("{\"examinationDetails\":" + FULL_EXAMINATION + "}"), 3L, "Bearer session-token", 22L,
					5L) > 0);
		}
	}
}
