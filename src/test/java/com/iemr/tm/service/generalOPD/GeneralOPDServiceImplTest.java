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
package com.iemr.tm.service.generalOPD;

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
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GeneralOPDServiceImpl Test Suite")
class GeneralOPDServiceImplTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private GeneralOPDDoctorServiceImpl generalOPDDoctorServiceImpl;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;

	@InjectMocks
	private GeneralOPDServiceImpl service;

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	private JsonObject nurseRequest() {
		return json("{" + "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
				+ "\"visitDetails\":{"
				+ "  \"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                    \"visitCategory\":\"General OPD\"},"
				+ "  \"chiefComplaints\":[{\"chiefComplaintID\":3}]"
				+ "},"
				+ "\"historyDetails\":{},\"vitalDetails\":{\"height_cm\":170},"
				+ "\"examinationDetails\":{\"generalExamination\":{}}" + "}");
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
		when(commonNurseServiceImpl.savePhyGeneralExamination(any())).thenReturn(1L);
		when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
				anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(), any(),
				anyShort(), any(), any())).thenReturn(1);
	}

	@Nested
	@DisplayName("saveNurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("saveNurseData should save the visit, history, vitals and examination")
		void saveNurseData_shouldSaveEverySection() throws Exception {
			String result = service.saveNurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
			verify(commonNurseServiceImpl).saveBenChiefComplaints(any());
		}

		@Test
		@DisplayName("saveNurseData should report an already saved visit without touching the history")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			assertTrue(service.saveNurseData(nurseRequest(), AUTHORIZATION).contains("Data already saved"));
		}

		@Test
		@DisplayName("saveNurseData should reject a request without visit details")
		void saveNurseData_shouldRejectRequestWithoutVisitDetails() {
			assertThrows(Exception.class, () -> service.saveNurseData(json("{}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNurseData should reject a null request")
		void saveNurseData_shouldRejectNullRequest() {
			assertThrows(Exception.class, () -> service.saveNurseData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(0L);

			assertThrows(RuntimeException.class, () -> service.saveNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNurseData should fail when a section was not captured")
		void saveNurseData_shouldFailWhenSectionNotCaptured() throws Exception {
			JsonObject request = nurseRequest();
			request.remove("examinationDetails");

			assertThrows(RuntimeException.class, () -> service.saveNurseData(request, AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNurseData should notify a scheduled teleconsultation by SMS")
		void saveNurseData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), eq(AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNurseData should not notify a walk-in teleconsultation")
		void saveNurseData_shouldNotNotifyWalkInTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString())).thenReturn(teleconsultationRequest(true));

			service.saveNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl, never()).smsSenderGateway(anyString(), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), anyString());
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
		@DisplayName("saveBenVisitDetails should return nothing for a payload without visit details")
		void saveBenVisitDetails_shouldReturnNothingWithoutVisitDetails() throws Exception {
			assertTrue(service.saveBenVisitDetails(json("{}"), utility()).isEmpty());
		}

		@Test
		@DisplayName("saveBenGeneralOPDHistoryDetails should succeed when no history section was captured")
		void saveHistory_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenGeneralOPDHistoryDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenGeneralOPDHistoryDetails should store every captured history section")
		void saveHistory_shouldStoreCapturedSections() throws Exception {
			when(commonNurseServiceImpl.saveBenPastHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenComorbidConditions(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenMedicationHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveFemaleObstetricHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenMenstrualHistory(any())).thenReturn(1);
			when(commonNurseServiceImpl.saveBenFamilyHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.savePersonalHistory(any())).thenReturn(1);
			when(commonNurseServiceImpl.saveAllergyHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveChildOptionalVaccineDetail(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveImmunizationHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveChildDevelopmentHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveChildFeedingHistory(any())).thenReturn(1L);
			when(commonNurseServiceImpl.savePerinatalHistory(any())).thenReturn(1L);

			JsonObject history = json("{\"pastHistory\":{},\"comorbidConditions\":{},"
					+ "\"medicationHistory\":{\"medicationHistoryList\":[{\"currentMedication\":\"Metformin\"}]},"
					+ "\"femaleObstetricHistory\":{},\"menstrualHistory\":{},\"familyHistory\":{},"
					+ "\"personalHistory\":{},\"childVaccineDetails\":{},\"immunizationHistory\":{},"
					+ "\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{}}");

			assertEquals(1L, service.saveBenGeneralOPDHistoryDetails(history, VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).saveBenPastHistory(any());
		}

		@Test
		@DisplayName("saveBenGeneralOPDHistoryDetails should report a failure when a section could not be stored")
		void saveHistory_shouldReportFailureWhenSectionNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBenPastHistory(any())).thenReturn(null);

			assertNull(service.saveBenGeneralOPDHistoryDetails(json("{\"pastHistory\":{}}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenVitalDetails should store the anthropometry and the physical vitals")
		void saveVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveBenVitalDetails(json("{\"height_cm\":170}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenVitalDetails should report a failure when the physical vitals were not stored")
		void saveVitals_shouldReportFailureWhenPhysicalVitalsNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(null);

			assertNull(service.saveBenVitalDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenVitalDetails should succeed for a null payload")
		void saveVitals_shouldSucceedForNullPayload() throws Exception {
			assertEquals(1L, service.saveBenVitalDetails(null, VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenExaminationDetails should succeed when no examination section was captured")
		void saveExamination_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenExaminationDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenExaminationDetails should store every captured examination section")
		void saveExamination_shouldStoreCapturedSections() throws Exception {
			when(commonNurseServiceImpl.savePhyHeadToToeExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysGastrointestinalExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysCardiovascularExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysRespiratoryExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysCentralNervousExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysMusculoskeletalSystemExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysGenitourinarySystemExamination(any())).thenReturn(1L);

			JsonObject examination = json("{\"generalExamination\":{},\"headToToeExamination\":{},"
					+ "\"gastroIntestinalExamination\":{},\"cardioVascularExamination\":{},"
					+ "\"respiratorySystemExamination\":{},\"centralNervousSystemExamination\":{},"
					+ "\"musculoskeletalSystemExamination\":{},\"genitoUrinarySystemExamination\":{}}");

			assertEquals(1L, service.saveBenExaminationDetails(examination, VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).savePhyHeadToToeExamination(any());
		}

		@Test
		@DisplayName("saveBenExaminationDetails should report a failure when a section could not be stored")
		void saveExamination_shouldReportFailureWhenSectionNotStored() throws Exception {
			when(commonNurseServiceImpl.savePhyGeneralExamination(any())).thenReturn(null);

			assertNull(service.saveBenExaminationDetails(json("{\"generalExamination\":{}}"), VISIT_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseGOPD should assemble the visit and the chief complaints")
		void getVisitDetails_shouldAssembleVisitSections() {
			when(commonNurseServiceImpl.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE)).thenReturn("[]");

			String result = service.getBenVisitDetailsFrmNurseGOPD(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("GOPDNurseVisitDetail"));
			assertTrue(result.contains("BenChiefComplaints"));
		}

		@Test
		@DisplayName("getBenHistoryDetails should assemble every stored history section")
		void getHistoryDetails_shouldAssembleStoredSections() {
			when(commonNurseServiceImpl.getPastHistoryData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.BenMedHistory());

			assertTrue(service.getBenHistoryDetails(BEN_REG_ID, VISIT_CODE).contains("PastHistory"));
		}

		@Test
		@DisplayName("getBeneficiaryVitalDetails should assemble the anthropometry and the physical vitals")
		void getVitalDetails_shouldAssembleVitalSections() {
			when(commonNurseServiceImpl.getBeneficiaryPhysicalAnthropometryDetails(BEN_REG_ID, VISIT_ID))
					.thenReturn("{}");
			when(commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(BEN_REG_ID, VISIT_ID)).thenReturn("{}");

			String result = service.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_ID);

			assertTrue(result.contains("benAnthropometryDetail"));
			assertTrue(result.contains("benPhysicalVitalDetail"));
		}

		@Test
		@DisplayName("getExaminationDetailsData should assemble every stored examination section")
		void getExaminationDetails_shouldAssembleStoredExaminationSections() {
			when(commonNurseServiceImpl.getGeneralExaminationData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.PhyGeneralExamination());
			when(commonNurseServiceImpl.getGenitourinaryExamination(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.SysGenitourinarySystemExamination());

			String result = service.getExaminationDetailsData(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("generalExamination"));
			assertTrue(result.contains("genitourinaryExamination"));
		}

		@Test
		@DisplayName("getBenGeneralOPDNurseData should assemble the nurse captured sections")
		void getNurseData_shouldAssembleNurseSections() {
			assertTrue(service.getBenGeneralOPDNurseData(BEN_REG_ID, VISIT_CODE).contains("history"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorGeneralOPD should assemble the whole doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			when(commonNurseServiceImpl.getGraphicalTrendData(BEN_REG_ID, "genOPD")).thenReturn(new HashMap<>());

			String result = service.getBenCaseRecordFromDoctorGeneralOPD(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("findings"));
			assertTrue(result.contains("LabReport"));
			assertTrue(result.contains("GraphData"));
		}
	}

	@Nested
	@DisplayName("saveDoctorData")
	class SaveDoctorDataTests {

		private JsonObject doctorRequest() {
			return json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
					+ "\"createdBy\":\"doctor1\",\"doctorSignatureFlag\":true,\"findings\":{},"
					+ "\"diagnosis\":{\"provisionalDiagnosisList\":[{\"term\":\"Fever\",\"conceptID\":\"1\"}]},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");
		}

		@BeforeEach
		void stubDoctorCollaborators() throws Exception {
			when(commonDoctorServiceImpl.saveDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.saveBenPrescription(any())).thenReturn(4L);
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
			verify(commonNurseServiceImpl).saveBenPrescribedDrugsList(any());
			verify(commonDoctorServiceImpl).saveBenReferDetails(any());
		}

		@Test
		@DisplayName("saveDoctorData should succeed for a case record with only an investigation section")
		void saveDoctorData_shouldSucceedForMinimalCaseRecord() throws Exception {
			assertEquals(1L,
					service.saveDoctorData(json("{\"beneficiaryRegID\":11,\"investigation\":{}}"), AUTHORIZATION));

			verify(commonDoctorServiceImpl, never()).saveDocFindings(any());
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
		@DisplayName("UpdateVisitDetails should update the chief complaints")
		void updateVisitDetails_shouldUpdateChiefComplaints() throws Exception {
			when(commonNurseServiceImpl.updateBenChiefComplaints(any())).thenReturn(1);

			assertEquals(1, service.UpdateVisitDetails(
					json("{\"visitDetails\":{},\"chiefComplaints\":[{\"chiefComplaintID\":3}]}")));
		}

		@Test
		@DisplayName("UpdateVisitDetails should report no change for a payload without chief complaints")
		void updateVisitDetails_shouldReportNoChangeWithoutChiefComplaints() throws Exception {
			assertEquals(0, service.UpdateVisitDetails(json("{\"visitDetails\":{}}")));
		}

		@Test
		@DisplayName("UpdateVisitDetails should report no change for a payload without visit details")
		void updateVisitDetails_shouldReportNoChangeWithoutVisitDetails() throws Exception {
			assertEquals(0, service.UpdateVisitDetails(json("{}")));
		}

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
		@DisplayName("updateBenExaminationDetails should succeed when no examination section was captured")
		void updateExamination_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1, service.updateBenExaminationDetails(json("{}")));
		}

		@Test
		@DisplayName("updateGeneralOPDDoctorData should return nothing for a null request")
		void updateDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.updateGeneralOPDDoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("updateGeneralOPDDoctorData should update the whole case record")
		void updateDoctorData_shouldUpdateWholeCaseRecord() throws Exception {
			when(commonDoctorServiceImpl.updateDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.updatePrescription(any())).thenReturn(1);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.updateBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);

			JsonObject request = json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,"
					+ "\"findings\":{},\"diagnosis\":{\"prescriptionID\":4},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");

			assertEquals(1L, service.updateGeneralOPDDoctorData(request, AUTHORIZATION));
		}

		@Test
		@DisplayName("updateGeneralOPDDoctorData should fail when the beneficiary flow could not be advanced")
		void updateDoctorData_shouldFailWhenFlowNotAdvanced() throws Exception {
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> service.updateGeneralOPDDoctorData(
					json("{\"beneficiaryRegID\":11,\"investigation\":{}}"), AUTHORIZATION));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("general OPD captured-section updates")
	class CapturedSectionUpdateTests {

		@Test
		@DisplayName("updateBenHistoryDetails should update every captured history section")
		void updateBenHistoryDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenHistoryDetails(
					com.google.gson.JsonParser.parseString("{\"pastHistory\":{},\"comorbidConditions\":{},\"medicationHistory\":{},\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{},\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{},\"allergyHistory\":{}}").getAsJsonObject()));
		}

		@Test
		@DisplayName("updateBenExaminationDetails should update every captured examination section")
		void updateBenExaminationDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenExaminationDetails(
					com.google.gson.JsonParser.parseString("{\"generalExamination\":{},\"headToToeExamination\":{},\"gastroIntestinalExamination\":{},\"cardioVascularExamination\":{},\"respiratorySystemExamination\":{},\"centralNervousSystemExamination\":{},\"musculoskeletalSystemExamination\":{},\"genitoUrinarySystemExamination\":{},\"obstetricExamination\":{}}").getAsJsonObject()));
		}
	}
}
