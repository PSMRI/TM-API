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
package com.iemr.tm.service.ncdscreening;

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
import com.iemr.tm.data.nurse.CommonUtilityClass;
import com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ;
import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.nurse.ncdscreening.IDRSDataRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
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
@DisplayName("NCDScreeningServiceImpl Test Suite")
class NCDScreeningServiceImplTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	private static final String FULL_HISTORY = "{\"pastHistory\":{},\"comorbidConditions\":{},"
			+ "\"medicationHistory\":{\"medicationHistoryList\":[{\"currentMedication\":\"Iron\"}]},"
			+ "\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},"
			+ "\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{},"
			+ "\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{},"
			+ "\"physicalActivityHistory\":{}}";

	/** One answered IDRS question, as the screening form submits it. */
	private static final String IDRS_REQUEST = "{\"beneficiaryRegID\":11,\"questionArray\":"
			+ "[{\"idrsQuestionID\":1,\"answer\":\"Yes\",\"question\":\"Family history?\","
			+ "  \"diseaseQuestionType\":\"Diabetes\"}]}";

	@Mock
	private NCDScreeningNurseServiceImpl ncdScreeningNurseServiceImpl;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;
	@Mock
	private NCDSCreeningDoctorServiceImpl ncdSCreeningDoctorServiceImpl;
	@Mock
	private IDRSDataRepo iDrsDataRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;

	@InjectMocks
	private NCDScreeningServiceImpl service;

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	private JsonObject nurseRequest() {
		return json("{" + "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
				+ "\"visitDetails\":{"
				+ "  \"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                    \"visitCategory\":\"NCD screening\"},"
				+ "  \"chiefComplaints\":[{\"chiefComplaintID\":3}]"
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

	@BeforeEach
	@DisplayName("Wire the collaborators that every save path needs")
	void setUp() throws Exception {
		when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(0);
		when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(VISIT_ID);
		when(commonNurseServiceImpl.generateVisitCode(anyLong(), any(), any())).thenReturn(VISIT_CODE);
		when(commonNurseServiceImpl.saveBenChiefComplaints(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalAnthropometryDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenPastHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenComorbidConditions(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenMedicationHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePersonalHistory(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveAllergyHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenFamilyHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenFamilyHistoryNCDScreening(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBenMenstrualHistory(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveFemaleObstetricHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveImmunizationHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildOptionalVaccineDetail(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildDevelopmentHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildFeedingHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePerinatalHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePhysicalActivity(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveIDRS(any())).thenReturn(1L);
		when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
				anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(), any(),
				anyShort(), any(), any())).thenReturn(1);
	}

	@Nested
	@DisplayName("saveNCDScreeningNurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("saveNCDScreeningNurseData should save the visit, history and vitals")
		void saveNurseData_shouldSaveEverySection() throws Exception {
			String result = service.saveNCDScreeningNurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
		}

		@Test
		@DisplayName("saveNCDScreeningNurseData should report an already saved visit")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			assertTrue(service.saveNCDScreeningNurseData(nurseRequest(), AUTHORIZATION)
					.contains("Data already saved"));
		}

		@Test
		@DisplayName("saveNCDScreeningNurseData should reject a request without visit details")
		void saveNurseData_shouldRejectRequestWithoutVisitDetails() {
			assertThrows(Exception.class, () -> service.saveNCDScreeningNurseData(json("{}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDScreeningNurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(0L);

			assertThrows(RuntimeException.class,
					() -> service.saveNCDScreeningNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDScreeningNurseData should notify a scheduled teleconsultation by SMS")
		void saveNurseData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveNCDScreeningNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), eq(AUTHORIZATION));
		}
	}

	@Nested
	@DisplayName("deleteVisitDetails")
	class DeleteVisitDetailsTests {

		@Test
		@DisplayName("deleteVisitDetails should remove the visit rows for a created visit")
		void deleteVisitDetails_shouldRemoveVisitRows() throws Exception {
			when(benVisitDetailRepo.getVisitCode(BEN_REG_ID, 9)).thenReturn(VISIT_CODE);

			service.deleteVisitDetails(nurseRequest());

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
		@DisplayName("saveNCDScreeningVitalDetails should store the anthropometry and the physical vitals")
		void saveScreeningVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveNCDScreeningVitalDetails(
					json("{\"ncdScreeningDetails\":{\"height_cm\":170}}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveNCDScreeningVitalDetails should report a failure when the physical vitals were not stored")
		void saveScreeningVitals_shouldReportFailureWhenPhysicalVitalsNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(null);

			assertNull(service.saveNCDScreeningVitalDetails(json("{\"ncdScreeningDetails\":{}}"), VISIT_ID,
					VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenNCDCareHistoryDetails should store every captured history section")
		void saveHistory_shouldStoreCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenNCDCareHistoryDetails(json(FULL_HISTORY), VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).saveBenPastHistory(any());
		}

		@Test
		@DisplayName("saveidrsDetails should store the captured IDRS answers")
		void saveIdrsDetails_shouldStoreCapturedAnswers() throws Exception {
			assertEquals(1L, service.saveidrsDetails(json(IDRS_REQUEST), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveidrsDetails should succeed when no IDRS answer was captured")
		void saveIdrsDetails_shouldSucceedWithoutCapturedAnswers() throws Exception {
			assertEquals(1L, service.saveidrsDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("savePhysicalActivityDetails should store the captured physical activity")
		void savePhysicalActivity_shouldStoreCapturedActivity() throws Exception {
			assertEquals(1L, service.savePhysicalActivityDetails(
					json("{\"physicalActivityHistory\":{\"physicalActivityType\":\"Moderate\"}}"), VISIT_ID,
					VISIT_CODE));
		}

		@Test
		@DisplayName("savePhysicalActivityDetails should succeed when no physical activity was captured")
		void savePhysicalActivity_shouldSucceedWithoutCapturedActivity() throws Exception {
			assertEquals(1L, service.savePhysicalActivityDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenNCDCareVitalDetails should store the anthropometry and the physical vitals")
		void saveCareVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveBenNCDCareVitalDetails(json("{\"height_cm\":170}"), VISIT_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests {

		@Test
		@DisplayName("getNCDScreeningDetails should assemble the screening, anthropometry and vitals")
		void getScreeningDetails_shouldAssembleScreeningSections() {
			when(ncdScreeningNurseServiceImpl.getNCDScreeningDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");
			when(commonNurseServiceImpl.getBeneficiaryPhysicalAnthropometryDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn("{}");
			when(commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			String result = service.getNCDScreeningDetails(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("ncdScreeningDetails"));
			assertTrue(result.contains("anthropometryDetails"));
		}

		@Test
		@DisplayName("getNCDScreeningDetails should return an empty payload when a section is missing")
		void getScreeningDetails_shouldReturnEmptyPayloadWhenSectionMissing() {
			when(ncdScreeningNurseServiceImpl.getNCDScreeningDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(null);

			assertEquals("{}", service.getNCDScreeningDetails(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getNcdScreeningVisitCnt should report the next screening visit number")
		void getVisitCount_shouldReportNextVisitNumber() {
			when(beneficiaryFlowStatusRepo.getNcdScreeningVisitCount(BEN_REG_ID)).thenReturn(2L);

			assertTrue(service.getNcdScreeningVisitCnt(BEN_REG_ID).contains("3"));
		}

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseNCDScreening should assemble the visit and the chief complaints")
		void getVisitDetails_shouldAssembleVisitSections() {
			when(commonNurseServiceImpl.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE)).thenReturn("[]");

			assertTrue(service.getBenVisitDetailsFrmNurseNCDScreening(BEN_REG_ID, VISIT_CODE)
					.contains("BenChiefComplaints"));
		}

		@Test
		@DisplayName("getBenHistoryDetails should assemble the stored screening history")
		void getHistoryDetails_shouldAssembleStoredSections() {
			when(commonNurseServiceImpl.getFamilyHistoryDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.BenFamilyHistory());

			assertTrue(service.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE).contains("FamilyHistory"));
		}

		@Test
		@DisplayName("getBenIdrsDetailsFrmNurse should assemble the stored IDRS answers")
		void getIdrsDetails_shouldAssembleStoredAnswers() {
			when(commonNurseServiceImpl.getBeneficiaryIdrsDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.ncdScreening.IDRSData());

			assertTrue(service.getBenIdrsDetailsFrmNurse(BEN_REG_ID, VISIT_CODE).contains("IDRSDetail"));
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
		@DisplayName("getBenNCDScreeningNurseData should assemble the nurse captured sections")
		void getNurseData_shouldAssembleNurseSections() {
			assertTrue(service.getBenNCDScreeningNurseData(BEN_REG_ID, VISIT_CODE).length() > 0);
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDScreening should assemble the whole doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			when(commonNurseServiceImpl.getGraphicalTrendData(anyLong(), anyString())).thenReturn(new HashMap<>());

			String result = service.getBenCaseRecordFromDoctorNCDScreening(BEN_REG_ID, VISIT_CODE);

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
					+ "\"diagnosis\":{\"specialistDiagnosis\":\"Diabetes suspect\"},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");
		}

		@BeforeEach
		void stubDoctorCollaborators() throws Exception {
			when(commonDoctorServiceImpl.saveDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.savePrescriptionDetailsAndGetPrescriptionID(any(), any(), any(), any(), any(),
					any(), any(), any(), any(), any())).thenReturn(4L);
			when(commonNurseServiceImpl.saveBenPrescription(any())).thenReturn(4L);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.saveBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);
		}

		@Test
		@DisplayName("saveDoctorData should save the findings, diagnosis, tests, drugs and referral")
		void saveDoctorData_shouldSaveWholeCaseRecord() throws Exception {
			assertEquals(1L, service.saveDoctorData(doctorRequest(), AUTHORIZATION));

			verify(commonDoctorServiceImpl).saveDocFindings(any());
			verify(commonNurseServiceImpl).saveBenInvestigation(any());
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
	}

	@Nested
	@DisplayName("update endpoints")
	class UpdateTests {

		@Test
		@DisplayName("updateNurseNCDScreeningDetails should update the captured screening details")
		void updateScreeningDetails_shouldUpdateCapturedDetails() throws Exception {
			when(ncdScreeningNurseServiceImpl.updateNCDScreeningDetails(any())).thenReturn(1);

			assertEquals(1, service.updateNurseNCDScreeningDetails(json("{\"beneficiaryRegID\":11}")));
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
		@DisplayName("UpdateIDRSScreen should update the captured IDRS answers")
		void updateIdrsScreen_shouldUpdateCapturedAnswers() throws Exception {
			assertEquals(1L, service.UpdateIDRSScreen(json("{\"idrsDetails\":" + IDRS_REQUEST + "}")));
		}

		@Test
		@DisplayName("UpdateIDRSScreen should return nothing when no IDRS answer was captured")
		void updateIdrsScreen_shouldReturnNothingWithoutCapturedAnswers() throws Exception {
			assertNull(service.UpdateIDRSScreen(json("{}")));
		}

		@Test
		@DisplayName("UpdateNCDScreeningHistory should update the captured screening history")
		void updateScreeningHistory_shouldUpdateCapturedHistory() throws Exception {
			when(commonNurseServiceImpl.updateBenFamilyHistoryNCDScreening(any())).thenReturn(1);
			when(commonNurseServiceImpl.updateBenPhysicalActivityHistoryNCDScreening(any())).thenReturn(1);

			assertEquals(1, service.UpdateNCDScreeningHistory(
					json("{\"familyHistory\":{},\"physicalActivityHistory\":{},\"personalHistory\":{}}")));
		}

		@Test
		@DisplayName("UpdateNCDScreeningHistory should report no change when no history section was captured")
		void updateScreeningHistory_shouldReportNoChangeWithoutCapturedHistory() throws Exception {
			assertEquals(0, service.UpdateNCDScreeningHistory(json("{}")));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("IDRS suspected and confirmed diseases")
	class IdrsDiseaseTests {

		private static final String WITH_DISEASES = "{\"beneficiaryRegID\":11,\"visitCode\":22,\"idrsScore\":40,"
				+ "\"suspectArray\":[\"Diabetes\",\"Hypertension\"],"
				+ "\"confirmArray\":[\"Diabetes\"],"
				+ "\"questionArray\":[{\"idrsQuestionID\":1,\"answer\":\"Yes\",\"question\":\"Family history?\","
				+ "  \"diseaseQuestionType\":\"Diabetes\"},"
				+ " {\"idrsQuestionID\":2,\"answer\":\"No\",\"question\":\"Waist?\","
				+ "  \"diseaseQuestionType\":\"Diabetes\"}]}";

		private static final String WITHOUT_QUESTIONS = "{\"beneficiaryRegID\":11,\"visitCode\":22,\"idrsScore\":40,"
				+ "\"suspectArray\":[\"Diabetes\",\"Hypertension\"],\"confirmArray\":[\"Diabetes\"]}";

		@Test
		@DisplayName("saveidrsDetails should flatten the suspected and confirmed diseases per answered question")
		void saveIdrs_shouldFlattenDiseasesPerQuestion() throws Exception {
			assertEquals(1L, service.saveidrsDetails(json(WITH_DISEASES), VISIT_ID, VISIT_CODE));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.ncdScreening.IDRSData> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.ncdScreening.IDRSData.class);
			org.mockito.Mockito.verify(commonNurseServiceImpl, org.mockito.Mockito.atLeastOnce())
					.saveIDRS(captor.capture());
			com.iemr.tm.data.ncdScreening.IDRSData saved = captor.getValue();
			assertEquals("Diabetes,Hypertension", saved.getSuspectedDisease());
			assertEquals("Diabetes", saved.getConfirmedDisease());
		}

		@Test
		@DisplayName("saveidrsDetails should flatten the diseases when no question was answered")
		void saveIdrs_shouldFlattenDiseasesWithoutQuestions() throws Exception {
			assertEquals(1L, service.saveidrsDetails(json(WITHOUT_QUESTIONS), VISIT_ID, VISIT_CODE));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.ncdScreening.IDRSData> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.ncdScreening.IDRSData.class);
			org.mockito.Mockito.verify(commonNurseServiceImpl).saveIDRS(captor.capture());
			assertEquals("Diabetes,Hypertension", captor.getValue().getSuspectedDisease());
		}

		@Test
		@DisplayName("UpdateIDRSScreen should update the suspected diseases for each answered question")
		void updateIdrs_shouldUpdateSuspectedDiseasesPerQuestion() throws Exception {
			when(iDrsDataRepo.updateSuspectedDiseases(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(1);

			assertEquals(1L, service.UpdateIDRSScreen(json("{\"idrsDetails\":" + WITH_DISEASES + "}")));

			org.mockito.Mockito.verify(iDrsDataRepo, org.mockito.Mockito.atLeastOnce())
					.updateSuspectedDiseases(11L, 22L, "Diabetes,Hypertension");
		}

		@Test
		@DisplayName("UpdateIDRSScreen should update the diseases and the score when no question was answered")
		void updateIdrs_shouldUpdateDiseasesAndScoreWithoutQuestions() throws Exception {
			when(iDrsDataRepo.updateConfirmedDiseases(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(1);
			when(iDrsDataRepo.updateSuspectedDiseases(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(1);
			when(iDrsDataRepo.updateIdrsScore(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt()))
					.thenReturn(1);

			assertEquals(1L, service.UpdateIDRSScreen(json("{\"idrsDetails\":" + WITHOUT_QUESTIONS + "}")));

			org.mockito.Mockito.verify(iDrsDataRepo).updateConfirmedDiseases(11L, 22L, "Diabetes");
			org.mockito.Mockito.verify(iDrsDataRepo, org.mockito.Mockito.atLeastOnce())
					.updateIdrsScore(org.mockito.ArgumentMatchers.eq(11L), org.mockito.ArgumentMatchers.eq(22L),
							org.mockito.ArgumentMatchers.anyInt());
		}

		@Test
		@DisplayName("UpdateIDRSScreen should report nothing when no disease could be updated")
		void updateIdrs_shouldReportNothingWhenNoDiseaseUpdated() throws Exception {
			when(iDrsDataRepo.updateConfirmedDiseases(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(0);
			when(iDrsDataRepo.updateSuspectedDiseases(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(0);
			when(iDrsDataRepo.updateIdrsScore(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt()))
					.thenReturn(0);

			assertNull(service.UpdateIDRSScreen(json("{\"idrsDetails\":" + WITHOUT_QUESTIONS + "}")));
		}
	}
}
