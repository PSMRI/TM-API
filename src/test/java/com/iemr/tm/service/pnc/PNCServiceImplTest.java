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
package com.iemr.tm.service.pnc;

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
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PNCServiceImpl Test Suite")
class PNCServiceImplTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	private static final String FULL_HISTORY = "{\"pastHistory\":{},\"comorbidConditions\":{},"
			+ "\"medicationHistory\":{\"medicationHistoryList\":[{\"currentMedication\":\"Iron\"}]},"
			+ "\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},"
			+ "\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{}}";

	private static final String FULL_EXAMINATION = "{\"generalExamination\":{},\"headToToeExamination\":{},"
			+ "\"gastroIntestinalExamination\":{},\"cardioVascularExamination\":{},"
			+ "\"respiratorySystemExamination\":{},\"centralNervousSystemExamination\":{},"
			+ "\"musculoskeletalSystemExamination\":{},\"genitoUrinarySystemExamination\":{}}";

	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private PNCNurseServiceImpl pncNurseServiceImpl;
	@Mock
	private PNCDoctorServiceImpl pncDoctorServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;

	@InjectMocks
	private PNCServiceImpl service;

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	private JsonObject nurseRequest() {
		return json("{" + "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
				+ "\"visitDetails\":{"
				+ "  \"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                    \"visitCategory\":\"PNC\"},"
				+ "  \"chiefComplaints\":[{\"chiefComplaintID\":3}]"
				+ "},"
				+ "\"pNCDeatils\":{},\"historyDetails\":" + FULL_HISTORY
				+ ",\"vitalDetails\":{\"height_cm\":170},\"examinationDetails\":" + FULL_EXAMINATION + "}");
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
		when(commonNurseServiceImpl.saveBenMenstrualHistory(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveFemaleObstetricHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveImmunizationHistory(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveChildOptionalVaccineDetail(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePhyGeneralExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePhyHeadToToeExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysGastrointestinalExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysCardiovascularExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysRespiratoryExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysCentralNervousExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysMusculoskeletalSystemExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysGenitourinarySystemExamination(any())).thenReturn(1L);
		when(pncNurseServiceImpl.saveBenPncCareDetails(any())).thenReturn(1L);
		when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
				anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(), any(),
				anyShort(), any(), any())).thenReturn(1);
	}

	@Nested
	@DisplayName("savePNCNurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("savePNCNurseData should save the visit, PNC care, history, vitals and examination")
		void saveNurseData_shouldSaveEverySection() throws Exception {
			String result = service.savePNCNurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
		}

		@Test
		@DisplayName("savePNCNurseData should report an already saved visit")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			assertTrue(service.savePNCNurseData(nurseRequest(), AUTHORIZATION).contains("Data already saved"));
		}

		@Test
		@DisplayName("savePNCNurseData should reject a request without visit details")
		void saveNurseData_shouldRejectRequestWithoutVisitDetails() {
			assertThrows(Exception.class, () -> service.savePNCNurseData(json("{}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCNurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(0L);

			assertThrows(RuntimeException.class, () -> service.savePNCNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCNurseData should fail when the PNC care details could not be stored")
		void saveNurseData_shouldFailWhenPncCareNotStored() throws Exception {
			when(pncNurseServiceImpl.saveBenPncCareDetails(any())).thenReturn(null);

			assertThrows(RuntimeException.class, () -> service.savePNCNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCNurseData should notify a scheduled teleconsultation by SMS")
		void saveNurseData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.savePNCNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), eq(AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCNurseData should not notify a walk-in teleconsultation")
		void saveNurseData_shouldNotNotifyWalkInTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString())).thenReturn(teleconsultationRequest(true));

			service.savePNCNurseData(nurseRequest(), AUTHORIZATION);

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
		@DisplayName("saveBenPNCDetails should store the captured PNC care details")
		void savePncDetails_shouldStoreCapturedCareDetails() throws Exception {
			assertEquals(1L, service.saveBenPNCDetails(json("{\"pNCDeatils\":{}}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenPNCDetails should succeed when no PNC care details were captured")
		void savePncDetails_shouldSucceedWithoutCareDetails() throws Exception {
			assertEquals(1L, service.saveBenPNCDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenPNCHistoryDetails should store every captured history section")
		void saveHistory_shouldStoreCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenPNCHistoryDetails(json(FULL_HISTORY), VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).saveBenPastHistory(any());
		}

		@Test
		@DisplayName("saveBenPNCVitalDetails should store the anthropometry and the physical vitals")
		void saveVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveBenPNCVitalDetails(json("{\"height_cm\":170}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenPNCVitalDetails should report a failure when the physical vitals were not stored")
		void saveVitals_shouldReportFailureWhenPhysicalVitalsNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(null);

			assertNull(service.saveBenPNCVitalDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenExaminationDetails should succeed when no examination section was captured")
		void saveExamination_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenExaminationDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenExaminationDetails should store every captured examination section")
		void saveExamination_shouldStoreCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenExaminationDetails(json(FULL_EXAMINATION), VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).savePhyHeadToToeExamination(any());
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNursePNC should assemble the visit and the chief complaints")
		void getVisitDetails_shouldAssembleVisitSections() {
			when(commonNurseServiceImpl.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE)).thenReturn("[]");

			String result = service.getBenVisitDetailsFrmNursePNC(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("PNCNurseVisitDetail"));
			assertTrue(result.contains("BenChiefComplaints"));
		}

		@Test
		@DisplayName("getBenPNCDetailsFrmNursePNC should assemble the PNC care details")
		void getPncDetails_shouldAssemblePncSections() {
			when(pncNurseServiceImpl.getPNCCareDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			assertTrue(service.getBenPNCDetailsFrmNursePNC(BEN_REG_ID, VISIT_CODE).contains("PNCCareDetail"));
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
			when(commonNurseServiceImpl.getBeneficiaryPhysicalAnthropometryDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn("{}");
			when(commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			assertTrue(service.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE).contains("benAnthropometryDetail"));
		}

		@Test
		@DisplayName("getPNCExaminationDetailsData should assemble every stored examination section")
		void getExaminationDetails_shouldAssembleStoredSections() {
			when(commonNurseServiceImpl.getGeneralExaminationData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.PhyGeneralExamination());

			assertTrue(service.getPNCExaminationDetailsData(BEN_REG_ID, VISIT_CODE).contains("generalExamination"));
		}

		@Test
		@DisplayName("getBenPNCNurseData should assemble the nurse captured sections")
		void getNurseData_shouldAssembleNurseSections() {
			assertTrue(service.getBenPNCNurseData(BEN_REG_ID, VISIT_CODE).contains("history"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorPNC should assemble the whole doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			when(commonNurseServiceImpl.getGraphicalTrendData(anyLong(), anyString())).thenReturn(new HashMap<>());

			String result = service.getBenCaseRecordFromDoctorPNC(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("findings"));
			assertTrue(result.contains("LabReport"));
		}
	}

	@Nested
	@DisplayName("savePNCDoctorData")
	class SaveDoctorDataTests {

		private JsonObject doctorRequest() {
			return json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
					+ "\"createdBy\":\"doctor1\",\"doctorSignatureFlag\":true,\"findings\":{},"
					+ "\"diagnosis\":{\"specialistDiagnosis\":\"PNC follow up\"},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");
		}

		@BeforeEach
		void stubDoctorCollaborators() throws Exception {
			when(commonDoctorServiceImpl.saveDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.savePrescriptionDetailsAndGetPrescriptionID(any(), any(), any(), any(), any(),
					any(), any(), any(), any(), any())).thenReturn(4L);
			when(pncDoctorServiceImpl.saveBenPNCDiagnosis(any(), any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.saveBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);
		}

		@Test
		@DisplayName("savePNCDoctorData should save the findings, diagnosis, tests, drugs and referral")
		void saveDoctorData_shouldSaveWholeCaseRecord() throws Exception {
			assertEquals(1L, service.savePNCDoctorData(doctorRequest(), AUTHORIZATION));

			verify(commonDoctorServiceImpl).saveDocFindings(any());
			verify(pncDoctorServiceImpl).saveBenPNCDiagnosis(any(), any());
		}

		@Test
		@DisplayName("savePNCDoctorData should succeed for a case record with only findings and an investigation")
		void saveDoctorData_shouldSucceedForMinimalCaseRecord() throws Exception {
			assertEquals(1L, service.savePNCDoctorData(
					json("{\"beneficiaryRegID\":11,\"findings\":{},\"investigation\":{}}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCDoctorData should fail when the findings section is absent")
		void saveDoctorData_shouldFailWithoutFindings() throws Exception {
			assertThrows(RuntimeException.class,
					() -> service.savePNCDoctorData(json("{\"beneficiaryRegID\":11,\"investigation\":{}}"),
							AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCDoctorData should fail when the beneficiary flow could not be advanced")
		void saveDoctorData_shouldFailWhenFlowNotAdvanced() throws Exception {
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> service.savePNCDoctorData(doctorRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("savePNCDoctorData should notify a scheduled teleconsultation by SMS")
		void saveDoctorData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.savePNCDoctorData(doctorRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(), any(),
					anyString(), any(), eq(AUTHORIZATION));
		}
	}

	@Nested
	@DisplayName("update endpoints")
	class UpdateTests {

		@Test
		@DisplayName("updateBenPNCDetails should succeed when no PNC section was captured")
		void updatePncDetails_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1, service.updateBenPNCDetails(json("{}")));
		}

		@Test
		@DisplayName("updateBenPNCDetails should update the captured PNC care details")
		void updatePncDetails_shouldUpdateCapturedCareDetails() throws Exception {
			when(pncNurseServiceImpl.updateBenPNCCareDetails(any())).thenReturn(1);

			assertEquals(1, service.updateBenPNCDetails(json("{\"PNCDetails\":{}}")));
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
		@DisplayName("updateBenExaminationDetails should succeed when no examination section was captured")
		void updateExamination_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1, service.updateBenExaminationDetails(json("{}")));
		}

		@Test
		@DisplayName("updatePNCDoctorData should return nothing for a null request")
		void updateDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.updatePNCDoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("updatePNCDoctorData should update the whole case record")
		void updateDoctorData_shouldUpdateWholeCaseRecord() throws Exception {
			when(commonDoctorServiceImpl.updateDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.updatePrescription(any())).thenReturn(1);
			when(pncDoctorServiceImpl.updateBenPNCDiagnosis(any())).thenReturn(1);
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

			assertEquals(1L, service.updatePNCDoctorData(request, AUTHORIZATION));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("PNC captured-section updates")
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
