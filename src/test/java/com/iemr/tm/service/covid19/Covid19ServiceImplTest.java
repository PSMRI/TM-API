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
package com.iemr.tm.service.covid19;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
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
import com.google.gson.JsonParser;
import com.iemr.tm.data.covid19.Covid19BenFeedback;
import com.iemr.tm.data.nurse.CommonUtilityClass;
import com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ;
import com.iemr.tm.repo.nurse.covid19.Covid19BenFeedbackRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Covid19ServiceImpl Test Suite")
class Covid19ServiceImplTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	private static final String FULL_HISTORY = "{\"pastHistory\":{},\"comorbidConditions\":{},"
			+ "\"medicationHistory\":{\"medicationHistoryList\":[{\"currentMedication\":\"Iron\"}]},"
			+ "\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},"
			+ "\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{},"
			+ "\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{}}";

	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Mock
	private Covid19BenFeedbackRepo covid19BenFeedbackRepo;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;

	@InjectMocks
	private Covid19ServiceImpl service;

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	private JsonObject nurseRequest() {
		return json("{" + "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
				+ "\"visitDetails\":{"
				+ "  \"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                    \"visitCategory\":\"COVID-19 Screening\"},"
				+ "  \"covidDetails\":{\"symptoms\":[\"Fever\",\"Cough\"],\"contactStatus\":[\"Yes\"],"
				+ "                   \"suspectedStatusUI\":\"Suspected\"}"
				+ "},"
				+ "\"historyDetails\":" + FULL_HISTORY + ",\"vitalDetails\":{\"height_cm\":170}}");
	}

	private CommonUtilityClass utility() {
		CommonUtilityClass utility = new CommonUtilityClass();
		utility.setBeneficiaryRegID(BEN_REG_ID);
		utility.setVanID(7);
		utility.setSessionID(1);
		utility.setProviderServiceMapID(9);
		utility.setCreatedBy("nurse1");
		return utility;
	}

	private TeleconsultationRequestOBJ teleconsultationRequest(boolean walkIn) {
		TeleconsultationRequestOBJ request = new TeleconsultationRequestOBJ();
		request.setWalkIn(walkIn);
		request.setUserID(42);
		request.setSpecializationID(2);
		request.setTmRequestID(8L);
		request.setAllocationDate(new Timestamp(1_700_000_000_000L));
		return request;
	}

	private Covid19BenFeedback storedFeedback() {
		Covid19BenFeedback stored = new Covid19BenFeedback();
		stored.setcOVID19ID(5L);
		return stored;
	}

	@BeforeEach
	@DisplayName("Wire the collaborators that every save path needs")
	void setUp() throws Exception {
		when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(0);
		when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(VISIT_ID);
		when(commonNurseServiceImpl.generateVisitCode(anyLong(), any(), any())).thenReturn(VISIT_CODE);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalAnthropometryDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenPastHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenComorbidConditions(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenMedicationHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePersonalHistory(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveAllergyHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenFamilyHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenMenstrualHistory(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveFemaleObstetricHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveImmunizationHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildOptionalVaccineDetail(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildDevelopmentHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildFeedingHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePerinatalHistory(any())).thenReturn(1L);
		when(covid19BenFeedbackRepo.save(any())).thenReturn(storedFeedback());
		when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
				anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(), any(),
				anyShort(), any(), any())).thenReturn(1);
	}

	@Nested
	@DisplayName("saveCovid19NurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("saveCovid19NurseData should save the visit, screening feedback, history and vitals")
		void saveNurseData_shouldSaveEverySection() throws Exception {
			String result = service.saveCovid19NurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
			verify(covid19BenFeedbackRepo).save(any());
		}

		@Test
		@DisplayName("saveCovid19NurseData should save the visit when no screening feedback was captured")
		void saveNurseData_shouldSaveWithoutScreeningFeedback() throws Exception {
			JsonObject request = nurseRequest();
			request.getAsJsonObject("visitDetails").remove("covidDetails");

			assertTrue(service.saveCovid19NurseData(request, AUTHORIZATION).contains("Data saved successfully"));
			verify(covid19BenFeedbackRepo, never()).save(any());
		}

		@Test
		@DisplayName("saveCovid19NurseData should report an already saved visit")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			assertTrue(service.saveCovid19NurseData(nurseRequest(), AUTHORIZATION).contains("Data already saved"));
		}

		@Test
		@DisplayName("saveCovid19NurseData should reject a request without visit details")
		void saveNurseData_shouldRejectRequestWithoutVisitDetails() {
			assertThrows(Exception.class, () -> service.saveCovid19NurseData(json("{}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveCovid19NurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(0L);

			assertThrows(RuntimeException.class, () -> service.saveCovid19NurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveCovid19NurseData should fail when the screening feedback could not be stored")
		void saveNurseData_shouldFailWhenFeedbackNotStored() throws Exception {
			when(covid19BenFeedbackRepo.save(any())).thenReturn(null);

			assertThrows(RuntimeException.class, () -> service.saveCovid19NurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveCovid19NurseData should notify a scheduled teleconsultation by SMS")
		void saveNurseData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveCovid19NurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), eq(AUTHORIZATION));
		}
	}

	@Nested
	@DisplayName("deleteVisitDetails")
	class DeleteVisitDetailsTests {

		@Test
		@DisplayName("deleteVisitDetails should remove the visit and the screening feedback")
		void deleteVisitDetails_shouldRemoveVisitRows() throws Exception {
			when(benVisitDetailRepo.getVisitCode(BEN_REG_ID, 9)).thenReturn(VISIT_CODE);

			service.deleteVisitDetails(nurseRequest());

			verify(covid19BenFeedbackRepo).deleteVisitDetails(VISIT_CODE);
			verify(benVisitDetailRepo).deleteVisitDetails(VISIT_CODE);
		}

		@Test
		@DisplayName("deleteVisitDetails should do nothing for a request without visit details")
		void deleteVisitDetails_shouldDoNothingWithoutVisitDetails() throws Exception {
			service.deleteVisitDetails(json("{}"));

			verify(benVisitDetailRepo, never()).deleteVisitDetails(anyLong());
		}
	}

	@Nested
	@DisplayName("nurse section saves")
	class SectionSaveTests {

		@Test
		@DisplayName("saveBenVisitDetails should return the new visit id and code")
		void saveBenVisitDetails_shouldReturnVisitIdAndCode() throws Exception {
			Map<String, Long> result = service.saveBenVisitDetails(nurseRequest().getAsJsonObject("visitDetails"),
					utility());

			assertEquals(VISIT_ID, result.get("visitID"));
			assertEquals(VISIT_CODE, result.get("visitCode"));
		}

		@Test
		@DisplayName("saveBenVisitDetails should return nothing when the visit was already recorded")
		void saveBenVisitDetails_shouldReturnNothingForAlreadyRecordedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			assertTrue(service.saveBenVisitDetails(nurseRequest().getAsJsonObject("visitDetails"), utility())
					.isEmpty());
		}

		@Test
		@DisplayName("saveBenCovid19HistoryDetails should store every captured history section")
		void saveHistory_shouldStoreCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenCovid19HistoryDetails(json(FULL_HISTORY), VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).saveBenPastHistory(any());
		}

		@Test
		@DisplayName("saveBenCovid19VitalDetails should store the anthropometry and the physical vitals")
		void saveVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveBenCovid19VitalDetails(json("{\"height_cm\":170}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenCovid19VitalDetails should report a failure when the physical vitals were not stored")
		void saveVitals_shouldReportFailureWhenPhysicalVitalsNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(null);

			assertNull(service.saveBenCovid19VitalDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveCovidDetails should flatten the symptom, contact and travel lists before storing")
		void saveCovidDetails_shouldFlattenLists() {
			Covid19BenFeedback feedback = new Covid19BenFeedback();
			feedback.setSuspectedStatusUI("Suspected");
			feedback.setSymptoms(new String[] { "Fever", "Cough" });
			feedback.setContactStatus(new String[] { "Yes", "No" });

			assertEquals(1, service.saveCovidDetails(feedback));
			assertEquals("Fever||Cough", feedback.getSymptoms_db());
			assertEquals("Yes||No", feedback.getcOVID19_contact_history());
		}

		@Test
		@DisplayName("saveCovidDetails should store a feedback without any list")
		void saveCovidDetails_shouldStoreFeedbackWithoutLists() {
			Covid19BenFeedback feedback = new Covid19BenFeedback();
			feedback.setSuspectedStatusUI("Not Suspected");

			assertEquals(1, service.saveCovidDetails(feedback));
		}

		@Test
		@DisplayName("saveCovidDetails should report a failure when nothing was stored")
		void saveCovidDetails_shouldReportFailureWhenNothingStored() {
			when(covid19BenFeedbackRepo.save(any())).thenReturn(null);
			Covid19BenFeedback feedback = new Covid19BenFeedback();
			feedback.setSuspectedStatusUI("Not Suspected");

			assertNull(service.saveCovidDetails(feedback));
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseCovid19 should assemble the visit and the screening feedback")
		void getVisitDetails_shouldAssembleVisitSections() {
			when(covid19BenFeedbackRepo.findByBeneficiaryRegIDAndVisitCode(BEN_REG_ID, VISIT_CODE))
					.thenReturn(storedFeedback());

			String result = service.getBenVisitDetailsFrmNurseCovid19(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("covid19NurseVisitDetail"));
			assertTrue(result.contains("covidDetails"));
		}

		@Test
		@DisplayName("getBenCovid19HistoryDetails should assemble every stored history section")
		void getHistoryDetails_shouldAssembleStoredSections() {
			when(commonNurseServiceImpl.getPastHistoryData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.BenMedHistory());

			assertTrue(service.getBenCovid19HistoryDetails(BEN_REG_ID, VISIT_CODE).contains("PastHistory"));
		}

		@Test
		@DisplayName("getBeneficiaryVitalDetails should assemble the anthropometry and the physical vitals")
		void getVitalDetails_shouldAssembleVitalSections() {
			when(commonNurseServiceImpl.getBeneficiaryPhysicalAnthropometryDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn("{}");
			when(commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			assertTrue(service.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE).contains("benAnthropometryDetail"));
		}

		@Test
		@DisplayName("getBenCovidNurseData should assemble the nurse captured sections")
		void getNurseData_shouldAssembleNurseSections() {
			assertTrue(service.getBenCovidNurseData(BEN_REG_ID, VISIT_CODE).contains("covidDetails"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorCovid19 should assemble the whole doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			when(commonNurseServiceImpl.getGraphicalTrendData(anyLong(), anyString())).thenReturn(new HashMap<>());

			String result = service.getBenCaseRecordFromDoctorCovid19(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("findings"));
			assertTrue(result.contains("LabReport"));
		}
	}

	@Nested
	@DisplayName("saveDoctorData")
	class SaveDoctorDataTests {

		private JsonObject doctorRequest() {
			return json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
					+ "\"createdBy\":\"doctor1\",\"doctorSignatureFlag\":true,\"findings\":{},"
					+ "\"diagnosis\":{\"specialistDiagnosis\":\"Covid suspect\",\"doctorDiagnosis\":\"Covid\"},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");
		}

		@BeforeEach
		void stubDoctorCollaborators() throws Exception {
			when(commonDoctorServiceImpl.saveDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.savePrescriptionCovid(any(), any(), any(), any(), any(), any(), any(), any(),
					any(), any())).thenReturn(4L);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.saveBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);
		}

		@Test
		@DisplayName("saveDoctorData should save the findings, prescription, tests, drugs and referral")
		void saveDoctorData_shouldSaveWholeCaseRecord() throws Exception {
			assertEquals(1L, service.saveDoctorData(doctorRequest(), AUTHORIZATION));

			verify(commonDoctorServiceImpl).saveDocFindings(any());
			verify(commonNurseServiceImpl).saveBenInvestigation(any());
		}

		@Test
		@DisplayName("saveDoctorData should succeed for a case record with only an investigation section")
		void saveDoctorData_shouldSucceedForMinimalCaseRecord() throws Exception {
			assertEquals(1L,
					service.saveDoctorData(json("{\"beneficiaryRegID\":11,\"investigation\":{}}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveDoctorData should return nothing for a null request")
		void saveDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.saveDoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("saveDoctorData should fail when the beneficiary flow could not be advanced")
		void saveDoctorData_shouldFailWhenFlowNotAdvanced() throws Exception {
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> service.saveDoctorData(doctorRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveDoctorData should notify a scheduled teleconsultation by SMS")
		void saveDoctorData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveDoctorData(doctorRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(), any(),
					anyString(), any(), eq(AUTHORIZATION));
		}
	}

	@Nested
	@DisplayName("update endpoints")
	class UpdateTests {

		@Test
		@DisplayName("updateBenHistoryDetails should succeed when no history section was captured")
		void updateHistory_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1, service.updateBenHistoryDetails(json("{}")));
		}

		@Test
		@DisplayName("updateBenVitalDetails should confirm the update when both sections changed")
		void updateVitals_shouldConfirmUpdate() throws Exception {
			when(commonNurseServiceImpl.updateANCAnthropometryDetails(any())).thenReturn(1);
			when(commonNurseServiceImpl.updateANCPhysicalVitalDetails(any())).thenReturn(1);

			assertEquals(1, service.updateBenVitalDetails(json("{\"height_cm\":170}")));
		}

		@Test
		@DisplayName("updateBenVitalDetails should report no change when a section did not change")
		void updateVitals_shouldReportNoChangeWhenSectionUnchanged() throws Exception {
			when(commonNurseServiceImpl.updateANCAnthropometryDetails(any())).thenReturn(0);

			assertEquals(0, service.updateBenVitalDetails(json("{}")));
		}

		@Test
		@DisplayName("updateCovid19DoctorData should return nothing for a null request")
		void updateDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.updateCovid19DoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("updateCovid19DoctorData should update the whole case record")
		void updateDoctorData_shouldUpdateWholeCaseRecord() throws Exception {
			when(commonDoctorServiceImpl.updateDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.updatePrescription(any())).thenReturn(1);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.updateBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);

			JsonObject request = json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,"
					+ "\"isSpecialist\":false,\"findings\":{},\"diagnosis\":{\"prescriptionID\":4},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");

			assertEquals(1L, service.updateCovid19DoctorData(request, AUTHORIZATION));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("COVID-19 captured-section updates")
	class CapturedSectionUpdateTests {

		@Test
		@DisplayName("updateBenHistoryDetails should update every captured history section")
		void updateBenHistoryDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenHistoryDetails(
					com.google.gson.JsonParser.parseString("{\"pastHistory\":{},\"comorbidConditions\":{},\"medicationHistory\":{},\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{},\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{},\"allergyHistory\":{}}").getAsJsonObject()));
		}
	}
}
