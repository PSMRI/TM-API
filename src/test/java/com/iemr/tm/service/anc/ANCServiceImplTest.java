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
package com.iemr.tm.service.anc;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import com.iemr.tm.data.anc.ANCCareDetails;
import com.iemr.tm.data.anc.FemaleObstetricHistory;
import com.iemr.tm.data.nurse.CommonUtilityClass;
import com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ;
import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.nurse.anc.ANCCareRepo;
import com.iemr.tm.repo.nurse.BenAnthropometryRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.nurse.anc.BenMedHistoryRepo;
import com.iemr.tm.repo.nurse.anc.BenMenstrualDetailsRepo;
import com.iemr.tm.repo.nurse.anc.BencomrbidityCondRepo;
import com.iemr.tm.repo.nurse.anc.FemaleObstetricHistoryRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ANCServiceImpl Test Suite")
class ANCServiceImplTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	@Mock
	private ANCNurseServiceImpl ancNurseServiceImpl;
	@Mock
	private ANCDoctorServiceImpl ancDoctorServiceImpl;
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
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private BenAnthropometryRepo benAnthropometryRepo;
	@Mock
	private BenMedHistoryRepo benMedHistoryRepo;
	@Mock
	private BencomrbidityCondRepo bencomrbidityCondRepo;
	@Mock
	private ANCCareRepo ancCareRepo;
	@Mock
	private FemaleObstetricHistoryRepo femaleObstetricHistoryRepo;
	@Mock
	private com.iemr.tm.repo.nurse.anc.ANCDiagnosisRepo aNCDiagnosisRepo;
	@Mock
	private com.iemr.tm.repo.foetalmonitor.FoetalMonitorRepo foetalMonitorRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;
	@Mock
	private BenMenstrualDetailsRepo benMenstrualDetailsRepo;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;

	@InjectMocks
	private ANCServiceImpl service;

	private static final String FULL_HISTORY = "{\"pastHistory\":{},\"comorbidConditions\":{},"
			+ "\"medicationHistory\":{\"medicationHistoryList\":[{\"currentMedication\":\"Iron\"}]},"
			+ "\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},"
			+ "\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{}}";

	private static final String FULL_EXAMINATION = "{\"generalExamination\":{},\"headToToeExamination\":{},"
			+ "\"cardioVascularExamination\":{},\"respiratorySystemExamination\":{},"
			+ "\"centralNervousSystemExamination\":{},\"musculoskeletalSystemExamination\":{},"
			+ "\"genitoUrinarySystemExamination\":{},\"obstetricExamination\":{}}";

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	private JsonObject nurseRequest() {
		return json("{" + "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
				+ "\"visitDetails\":{"
				+ "  \"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                    \"visitCategory\":\"ANC\"},"
				+ "  \"chiefComplaints\":[{\"chiefComplaintID\":3}],\"adherence\":{\"toDrugs\":true},"
				+ "  \"investigation\":{\"laboratoryList\":[{\"testID\":1}]}"
				+ "},"
				+ "\"ancDetails\":{\"ancObstetricDetails\":{},\"ancImmunization\":{}},"
				+ "\"historyDetails\":" + FULL_HISTORY + ",\"vitalDetails\":{\"height_cm\":170},"
				+ "\"examinationDetails\":" + FULL_EXAMINATION + "}");
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
		when(commonNurseServiceImpl.saveBenAdherenceDetails(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveBenInvestigationDetails(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalAnthropometryDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePhyGeneralExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.savePhyHeadToToeExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysCardiovascularExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysRespiratoryExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysCentralNervousExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysMusculoskeletalSystemExamination(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveSysGenitourinarySystemExamination(any())).thenReturn(1L);
		when(ancNurseServiceImpl.saveSysObstetricExamination(any())).thenReturn(1L);
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
		when(ancNurseServiceImpl.saveBenAncCareDetails(any())).thenReturn(1L);
		when(ancNurseServiceImpl.saveAncImmunizationDetails(any())).thenReturn(1L);
		when(foetalMonitorRepo.getFoetalMonitorDetailsByFlowId(any())).thenReturn(new ArrayList<>());
		when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivityANC(any(), anyLong(), anyLong(),
				anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(), any(),
				anyShort(), any(), any(), anyShort())).thenReturn(1);
	}

	@Nested
	@DisplayName("saveANCNurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("saveANCNurseData should save the visit, ANC, history, vitals and examination")
		void saveNurseData_shouldSaveEverySection() throws Exception {
			String result = service.saveANCNurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
		}

		@Test
		@DisplayName("saveANCNurseData should report an already saved visit")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			assertTrue(service.saveANCNurseData(nurseRequest(), AUTHORIZATION).contains("Data already saved"));
		}

		@Test
		@DisplayName("saveANCNurseData should reject a request without visit details")
		void saveNurseData_shouldRejectRequestWithoutVisitDetails() {
			assertThrows(Exception.class, () -> service.saveANCNurseData(json("{}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveANCNurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(0L);

			assertThrows(RuntimeException.class, () -> service.saveANCNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveANCNurseData should fail when the ANC details could not be stored")
		void saveNurseData_shouldFailWhenAncDetailsNotStored() throws Exception {
			when(ancNurseServiceImpl.saveBenAncCareDetails(any())).thenReturn(null);

			assertThrows(RuntimeException.class, () -> service.saveANCNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveANCNurseData should notify a scheduled teleconsultation by SMS")
		void saveNurseData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveANCNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), eq(AUTHORIZATION));
		}

		@Test
		@DisplayName("saveANCNurseData should not notify a walk-in teleconsultation")
		void saveNurseData_shouldNotNotifyWalkInTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString())).thenReturn(teleconsultationRequest(true));

			service.saveANCNurseData(nurseRequest(), AUTHORIZATION);

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
		@DisplayName("saveBenANCDetails should store the obstetric details and the immunisation")
		void saveAncDetails_shouldStoreObstetricAndImmunisation() throws Exception {
			assertEquals(1L, service.saveBenANCDetails(
					json("{\"ancObstetricDetails\":{},\"ancImmunization\":{}}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenANCDetails should report a failure when the obstetric details were not captured")
		void saveAncDetails_shouldReportFailureWithoutObstetricDetails() throws Exception {
			assertNull(service.saveBenANCDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenANCHistoryDetails should report a failure when no history section was captured")
		void saveHistory_shouldReportFailureWithoutCapturedSections() throws Exception {
			assertNull(service.saveBenANCHistoryDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenANCHistoryDetails should store every captured history section")
		void saveHistory_shouldStoreCapturedSections() throws Exception {
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

			JsonObject history = json("{\"pastHistory\":{},\"comorbidConditions\":{},"
					+ "\"medicationHistory\":{\"medicationHistoryList\":[{\"currentMedication\":\"Iron\"}]},"
					+ "\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},"
					+ "\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{}}");

			assertEquals(1L, service.saveBenANCHistoryDetails(history, VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).saveBenPastHistory(any());
		}

		@Test
		@DisplayName("saveBenANCVitalDetails should store the anthropometry and the physical vitals")
		void saveVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveBenANCVitalDetails(json("{\"height_cm\":170}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenANCVitalDetails should report a failure when the physical vitals were not stored")
		void saveVitals_shouldReportFailureWhenPhysicalVitalsNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(null);

			assertNull(service.saveBenANCVitalDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenANCExaminationDetails should report a failure when no examination section was captured")
		void saveExamination_shouldReportFailureWithoutCapturedSections() throws Exception {
			assertNull(service.saveBenANCExaminationDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenANCExaminationDetails should store every captured examination section")
		void saveExamination_shouldStoreCapturedSections() throws Exception {
			when(commonNurseServiceImpl.savePhyHeadToToeExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysCardiovascularExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysRespiratoryExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysCentralNervousExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysMusculoskeletalSystemExamination(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveSysGenitourinarySystemExamination(any())).thenReturn(1L);
			when(ancNurseServiceImpl.saveSysObstetricExamination(any())).thenReturn(1L);

			JsonObject examination = json("{\"generalExamination\":{},\"headToToeExamination\":{},"
					+ "\"cardioVascularExamination\":{},\"respiratorySystemExamination\":{},"
					+ "\"centralNervousSystemExamination\":{},\"musculoskeletalSystemExamination\":{},"
					+ "\"genitoUrinarySystemExamination\":{},\"obstetricExamination\":{}}");

			assertEquals(1L, service.saveBenANCExaminationDetails(examination, VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).savePhyHeadToToeExamination(any());
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseANC should assemble the visit, adherence, complaints and tests")
		void getVisitDetails_shouldAssembleVisitSections() {
			when(commonNurseServiceImpl.getBenAdherence(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");
			when(commonNurseServiceImpl.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE)).thenReturn("[]");
			when(commonNurseServiceImpl.getLabTestOrders(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			String result = service.getBenVisitDetailsFrmNurseANC(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("ANCNurseVisitDetail"));
			assertTrue(result.contains("BenAdherence"));
		}

		@Test
		@DisplayName("getBenANCDetailsFrmNurseANC should assemble the ANC care and vaccine details")
		void getAncDetails_shouldAssembleAncSections() {
			when(ancNurseServiceImpl.getANCCareDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");
			when(ancNurseServiceImpl.getANCWomenVaccineDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			String result = service.getBenANCDetailsFrmNurseANC(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("ANCCareDetail"));
			assertTrue(result.contains("ANCWomenVaccineDetails"));
		}

		@Test
		@DisplayName("getBenANCHistoryDetails should assemble every stored history section")
		void getHistoryDetails_shouldAssembleStoredSections() {
			when(commonNurseServiceImpl.getPastHistoryData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.BenMedHistory());

			assertTrue(service.getBenANCHistoryDetails(BEN_REG_ID, VISIT_CODE).contains("PastHistory"));
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
		@DisplayName("getANCExaminationDetailsData should assemble every stored examination section")
		void getExaminationDetails_shouldAssembleStoredSections() {
			when(commonNurseServiceImpl.getGeneralExaminationData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.PhyGeneralExamination());

			assertTrue(service.getANCExaminationDetailsData(BEN_REG_ID, VISIT_CODE).contains("generalExamination"));
		}

		@Test
		@DisplayName("getBenANCNurseData should assemble the nurse captured sections")
		void getNurseData_shouldAssembleNurseSections() {
			assertTrue(service.getBenANCNurseData(BEN_REG_ID, VISIT_CODE).contains("history"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorANC should assemble the whole doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			when(commonNurseServiceImpl.getGraphicalTrendData(BEN_REG_ID, "anc")).thenReturn(new HashMap<>());

			String result = service.getBenCaseRecordFromDoctorANC(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("findings"));
			assertTrue(result.contains("fetosenseData"));
			assertTrue(result.contains("GraphData"));
		}
	}

	@Nested
	@DisplayName("saveANCDoctorData")
	class SaveDoctorDataTests {

		private JsonObject doctorRequest() {
			return json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
					+ "\"createdBy\":\"doctor1\",\"doctorSignatureFlag\":true,\"findings\":{},"
					+ "\"diagnosis\":{\"specialistDiagnosis\":\"ANC follow up\"},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");
		}

		@BeforeEach
		void stubDoctorCollaborators() throws Exception {
			when(commonDoctorServiceImpl.saveDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.savePrescriptionDetailsAndGetPrescriptionID(any(), any(), any(), any(), any(),
					any(), any(), any(), any(), any())).thenReturn(4L);
			when(ancDoctorServiceImpl.saveBenANCDiagnosis(any(), any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.saveBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);
		}

		@Test
		@DisplayName("saveANCDoctorData should save the findings, diagnosis, tests, drugs and referral")
		void saveDoctorData_shouldSaveWholeCaseRecord() throws Exception {
			assertEquals(1L, service.saveANCDoctorData(doctorRequest(), AUTHORIZATION));

			verify(commonDoctorServiceImpl).saveDocFindings(any());
			verify(commonNurseServiceImpl).saveBenInvestigation(any());
		}

		@Test
		@DisplayName("saveANCDoctorData should succeed for a case record with only an investigation section")
		void saveDoctorData_shouldSucceedForMinimalCaseRecord() throws Exception {
			assertEquals(1L,
					service.saveANCDoctorData(json("{\"beneficiaryRegID\":11,\"investigation\":{}}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveANCDoctorData should fail when the beneficiary flow could not be advanced")
		void saveDoctorData_shouldFailWhenFlowNotAdvanced() throws Exception {
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> service.saveANCDoctorData(doctorRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveANCDoctorData should notify a scheduled teleconsultation by SMS")
		void saveDoctorData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveANCDoctorData(doctorRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(), any(),
					anyString(), any(), eq(AUTHORIZATION));
		}
	}

	@Nested
	@DisplayName("update endpoints")
	class UpdateTests {

		@Test
		@DisplayName("updateBenANCDetails should report no change when no ANC section was captured")
		void updateAncDetails_shouldReportNoChangeWithoutCapturedSections() throws Exception {
			assertEquals(0, service.updateBenANCDetails(json("{}")));
		}

		@Test
		@DisplayName("updateBenANCHistoryDetails should succeed when no history section was captured")
		void updateHistory_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1, service.updateBenANCHistoryDetails(json("{}")));
		}

		@Test
		@DisplayName("updateBenANCVitalDetails should confirm the update when both sections changed")
		void updateVitals_shouldConfirmUpdate() throws Exception {
			when(commonNurseServiceImpl.updateANCAnthropometryDetails(any())).thenReturn(1);
			when(commonNurseServiceImpl.updateANCPhysicalVitalDetails(any())).thenReturn(1);

			assertEquals(1, service.updateBenANCVitalDetails(json("{\"height_cm\":170}")));
		}

		@Test
		@DisplayName("updateBenANCExaminationDetails should report no change when no section was captured")
		void updateExamination_shouldReportNoChangeWithoutCapturedSections() throws Exception {
			assertEquals(0, service.updateBenANCExaminationDetails(json("{}")));
		}

		@Test
		@DisplayName("updateANCDoctorData should return nothing for a null request")
		void updateDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.updateANCDoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("updateANCDoctorData should update the whole case record")
		void updateDoctorData_shouldUpdateWholeCaseRecord() throws Exception {
			when(commonDoctorServiceImpl.updateDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.updatePrescription(any())).thenReturn(1);
			when(ancDoctorServiceImpl.updateBenANCDiagnosis(any())).thenReturn(1);
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

			assertEquals(1L, service.updateANCDoctorData(request, AUTHORIZATION));
		}
	}

	@Nested
	@DisplayName("getHRPStatus")
	class HighRiskPregnancyTests {

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary who is younger than twenty")
		void getHRPStatus_shouldFlagYoungBeneficiary() throws Exception {
			java.time.LocalDate dob = java.time.LocalDate.now().minusYears(18);
			when(beneficiaryFlowStatusRepo.getBenAgeVal(BEN_REG_ID))
					.thenReturn(Timestamp.valueOf(dob.atStartOfDay()));

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary shorter than 145 cm")
		void getHRPStatus_shouldFlagShortBeneficiary() throws Exception {
			when(benAnthropometryRepo.getBenLatestHeight(BEN_REG_ID)).thenReturn(140d);

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary with a risk marker in the ANC care screen")
		void getHRPStatus_shouldFlagAncCareRiskMarker() throws Exception {
			when(ancCareRepo.getANCCareDataForHRP(BEN_REG_ID))
					.thenReturn(new ArrayList<>(Collections.singletonList(new ANCCareDetails())));

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary with a relevant past illness")
		void getHRPStatus_shouldFlagPastIllness() throws Exception {
			when(benMedHistoryRepo.getHRPStatus(BEN_REG_ID))
					.thenReturn(new ArrayList<>(Collections.singletonList(1L)));

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary with a relevant comorbidity")
		void getHRPStatus_shouldFlagComorbidity() throws Exception {
			when(bencomrbidityCondRepo.getHRPStatus(BEN_REG_ID))
					.thenReturn(new ArrayList<>(Collections.singletonList(1L)));

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary with a relevant obstetric history")
		void getHRPStatus_shouldFlagObstetricHistory() throws Exception {
			when(femaleObstetricHistoryRepo.getPastObestetricDataForHRP(anyLong(), anyString(), anyString(),
					anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
					anyString(), anyString(), anyString(), anyString(), anyString(), any()))
					.thenReturn(new ArrayList<>(Collections.singletonList(new FemaleObstetricHistory())));

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should flag a beneficiary with a relevant recorded diagnosis")
		void getHRPStatus_shouldFlagRecordedDiagnosis() throws Exception {
			when(aNCDiagnosisRepo.getANCDiagnosisDataForHRP(anyLong(), anyString(), anyString(), anyString(),
					anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
					anyString(), anyString())).thenReturn(new ArrayList<>(Collections.singletonList(1L)));

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("true"));
		}

		@Test
		@DisplayName("getHRPStatus should not flag a beneficiary without any risk marker")
		void getHRPStatus_shouldNotFlagWithoutRiskMarker() throws Exception {
			java.time.LocalDate dob = java.time.LocalDate.now().minusYears(28);
			when(beneficiaryFlowStatusRepo.getBenAgeVal(BEN_REG_ID))
					.thenReturn(Timestamp.valueOf(dob.atStartOfDay()));
			when(benAnthropometryRepo.getBenLatestHeight(BEN_REG_ID)).thenReturn(160d);

			assertTrue(service.getHRPStatus(BEN_REG_ID, VISIT_CODE).contains("false"));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("ANC captured-section updates")
	class CapturedSectionUpdateTests {

		@Test
		@DisplayName("updateBenANCHistoryDetails should update every captured history section")
		void updateBenANCHistoryDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenANCHistoryDetails(
					com.google.gson.JsonParser.parseString("{\"pastHistory\":{},\"comorbidConditions\":{},\"medicationHistory\":{},\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{},\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{},\"allergyHistory\":{}}").getAsJsonObject()));
		}

		@Test
		@DisplayName("updateBenANCExaminationDetails should update every captured examination section")
		void updateBenANCExaminationDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenANCExaminationDetails(
					com.google.gson.JsonParser.parseString("{\"generalExamination\":{},\"headToToeExamination\":{},\"gastroIntestinalExamination\":{},\"cardioVascularExamination\":{},\"respiratorySystemExamination\":{},\"centralNervousSystemExamination\":{},\"musculoskeletalSystemExamination\":{},\"genitoUrinarySystemExamination\":{},\"obstetricExamination\":{}}").getAsJsonObject()));
		}

		@Test
		@DisplayName("updateBenANCDetails should update every captured ANC section")
		void updateBenANCDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenANCDetails(
					com.google.gson.JsonParser.parseString(
							"{\"ancObstetricDetails\":{},\"ancImmunization\":{}}").getAsJsonObject()));
		}
	}
}
