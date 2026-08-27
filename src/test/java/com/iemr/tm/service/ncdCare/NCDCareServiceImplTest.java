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
package com.iemr.tm.service.ncdCare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
@DisplayName("NCDCareServiceImpl Test Suite")
class NCDCareServiceImplTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private NCDCareDoctorServiceImpl ncdCareDoctorServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;

	@InjectMocks
	private NCDCareServiceImpl service;

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	/** A nurse request carrying a visit, an ordered lab test, history and vitals. */
	private JsonObject nurseRequest() {
		return json("{" + "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
				+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
				+ "\"visitDetails\":{"
				+ "  \"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
				+ "                    \"visitCategory\":\"NCD care\"},"
				+ "  \"adherence\":{\"toDrugs\":true},"
				+ "  \"investigation\":{\"laboratoryList\":[{\"testID\":1}]}"
				+ "},"
				+ "\"historyDetails\":{},\"vitalDetails\":{\"height_cm\":170}" + "}");
	}

	private CommonUtilityClass utility() {
		CommonUtilityClass utility = new CommonUtilityClass();
		utility.setBeneficiaryRegID(BEN_REG_ID);
		utility.setVisitCode(VISIT_CODE);
		utility.setBenVisitID(VISIT_ID);
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
		when(commonNurseServiceImpl.saveBenAdherenceDetails(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveBenInvestigationDetails(any())).thenReturn(1);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalAnthropometryDetails(any())).thenReturn(1L);
		when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(1L);
		when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
				anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(), any(),
				anyShort(), any(), any())).thenReturn(1);
	}

	@Nested
	@DisplayName("saveNCDCareNurseData")
	class SaveNurseDataTests {

		@Test
		@DisplayName("saveNCDCareNurseData should save the visit, history and vitals and return the visit code")
		void saveNurseData_shouldSaveVisitHistoryAndVitals() throws Exception {
			String result = service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data saved successfully"));
			assertTrue(result.contains("\"visitCode\":\"22\""));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should report an already saved visit without touching the history")
		void saveNurseData_shouldReportAlreadySavedVisit() throws Exception {
			when(commonNurseServiceImpl.getMaxCurrentdate(anyLong(), anyString(), anyString())).thenReturn(1);

			String result = service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION);

			assertTrue(result.contains("Data already saved"));
			verify(commonNurseServiceImpl, never()).saveBeneficiaryVisitDetails(any());
		}

		@Test
		@DisplayName("saveNCDCareNurseData should reject a request without visit details")
		void saveNurseData_shouldRejectRequestWithoutVisitDetails() {
			assertThrows(Exception.class, () -> service.saveNCDCareNurseData(json("{}"), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should reject a null request")
		void saveNurseData_shouldRejectNullRequest() {
			assertThrows(Exception.class, () -> service.saveNCDCareNurseData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should fail when the visit could not be created")
		void saveNurseData_shouldFailWhenVisitNotCreated() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryVisitDetails(any())).thenReturn(0L);

			assertThrows(RuntimeException.class, () -> service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should fail when the vitals could not be stored")
		void saveNurseData_shouldFailWhenVitalsNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(any())).thenReturn(null);

			assertThrows(RuntimeException.class, () -> service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should fail when the beneficiary flow could not be advanced")
		void saveNurseData_shouldFailWhenFlowNotAdvanced() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
					anyString(), anyString(), anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), anyLong(),
					any(), anyShort(), any(), any())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should notify a scheduled teleconsultation by SMS")
		void saveNurseData_shouldNotifyScheduledTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(false));

			service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), eq(AUTHORIZATION));
		}

		@Test
		@DisplayName("saveNCDCareNurseData should not notify a walk-in teleconsultation")
		void saveNurseData_shouldNotNotifyWalkInTeleconsultation() throws Exception {
			when(commonServiceImpl.createTcRequest(any(), any(), anyString()))
					.thenReturn(teleconsultationRequest(true));

			service.saveNCDCareNurseData(nurseRequest(), AUTHORIZATION);

			verify(sMSGatewayServiceImpl, never()).smsSenderGateway(anyString(), anyLong(), anyInt(), anyLong(), any(),
					anyString(), anyString(), any(), anyString());
		}

		@Test
		@DisplayName("saveNCDCareNurseData should route the beneficiary to the doctor only when no test was ordered")
		void saveNurseData_shouldRouteToDoctorOnlyWithoutOrderedTest() throws Exception {
			JsonObject request = nurseRequest();
			request.getAsJsonObject("visitDetails").getAsJsonObject("investigation").add("laboratoryList",
					new com.google.gson.JsonArray());

			service.saveNCDCareNurseData(request, AUTHORIZATION);

			verify(commonBenStatusFlowServiceImpl).updateBenFlowNurseAfterNurseActivity(any(), anyLong(), anyLong(),
					anyString(), anyString(), eq((short) 9), eq((short) 1), eq((short) 0), anyShort(), anyShort(),
					anyLong(), any(), anyShort(), any(), any());
		}
	}

	@Nested
	@DisplayName("deleteVisitDetails")
	class DeleteVisitDetailsTests {

		@Test
		@DisplayName("deleteVisitDetails should remove the complaint, adherence and visit rows")
		void deleteVisitDetails_shouldRemoveVisitRows() throws Exception {
			when(benVisitDetailRepo.getVisitCode(BEN_REG_ID, 9)).thenReturn(VISIT_CODE);

			service.deleteVisitDetails(nurseRequest());

			verify(benChiefComplaintRepo).deleteVisitDetails(VISIT_CODE);
			verify(benAdherenceRepo).deleteVisitDetails(VISIT_CODE);
			verify(benVisitDetailRepo).deleteVisitDetails(VISIT_CODE);
		}

		@Test
		@DisplayName("deleteVisitDetails should do nothing when no visit was created")
		void deleteVisitDetails_shouldDoNothingWithoutVisit() throws Exception {
			when(benVisitDetailRepo.getVisitCode(BEN_REG_ID, 9)).thenReturn(null);

			service.deleteVisitDetails(nurseRequest());

			verify(benVisitDetailRepo, never()).deleteVisitDetails(anyLong());
		}

		@Test
		@DisplayName("deleteVisitDetails should do nothing for a request without visit details")
		void deleteVisitDetails_shouldDoNothingWithoutVisitDetails() throws Exception {
			service.deleteVisitDetails(json("{}"));

			verify(benVisitDetailRepo, never()).deleteVisitDetails(anyLong());
		}
	}

	@Nested
	@DisplayName("saveBenVisitDetails")
	class SaveVisitDetailsTests {

		@Test
		@DisplayName("saveBenVisitDetails should return the new visit id and code")
		void saveBenVisitDetails_shouldReturnVisitIdAndCode() throws Exception {
			Map<String, Long> result = service.saveBenVisitDetails(
					nurseRequest().getAsJsonObject("visitDetails"), utility());

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
		@DisplayName("saveBenVisitDetails should skip the adherence and investigation when they are absent")
		void saveBenVisitDetails_shouldSkipAbsentAdherenceAndInvestigation() throws Exception {
			JsonObject payload = json("{\"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New\","
					+ "\"visitCategory\":\"NCD care\"}}");

			assertEquals(VISIT_ID, service.saveBenVisitDetails(payload, utility()).get("visitID"));
			verify(commonNurseServiceImpl, never()).saveBenAdherenceDetails(any());
		}
	}

	@Nested
	@DisplayName("saveBenNCDCareHistoryDetails")
	class SaveHistoryDetailsTests {

		@Test
		@DisplayName("saveBenNCDCareHistoryDetails should succeed when no history section was captured")
		void saveHistory_shouldSucceedWithoutCapturedSections() throws Exception {
			assertEquals(1L, service.saveBenNCDCareHistoryDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenNCDCareHistoryDetails should store every captured history section")
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
					+ "\"personalHistory\":{},\"allergyHistory\":{},\"childVaccineDetails\":{},"
					+ "\"immunizationHistory\":{},\"developmentHistory\":{},\"childFeedingDetails\":{},"
					+ "\"perinatalHistroy\":{}}");

			assertEquals(1L, service.saveBenNCDCareHistoryDetails(history, VISIT_ID, VISIT_CODE));
			verify(commonNurseServiceImpl).saveBenPastHistory(any());
			verify(commonNurseServiceImpl).saveBenComorbidConditions(any());
		}

		@Test
		@DisplayName("saveBenNCDCareHistoryDetails should report a failure when a section could not be stored")
		void saveHistory_shouldReportFailureWhenSectionNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBenPastHistory(any())).thenReturn(null);

			assertNull(service.saveBenNCDCareHistoryDetails(json("{\"pastHistory\":{}}"), VISIT_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("saveBenNCDCareVitalDetails")
	class SaveVitalDetailsTests {

		@Test
		@DisplayName("saveBenNCDCareVitalDetails should store the anthropometry and the physical vitals")
		void saveVitals_shouldStoreAnthropometryAndPhysicalVitals() throws Exception {
			assertEquals(1L, service.saveBenNCDCareVitalDetails(json("{\"height_cm\":170}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenNCDCareVitalDetails should report a failure when the anthropometry was not stored")
		void saveVitals_shouldReportFailureWhenAnthropometryNotStored() throws Exception {
			when(commonNurseServiceImpl.saveBeneficiaryPhysicalAnthropometryDetails(any())).thenReturn(null);

			assertNull(service.saveBenNCDCareVitalDetails(json("{}"), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("saveBenNCDCareVitalDetails should report nothing for a null payload")
		void saveVitals_shouldReportNothingForNullPayload() throws Exception {
			assertNull(service.saveBenNCDCareVitalDetails(null, VISIT_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("read endpoints")
	class ReadTests {

		@Test
		@DisplayName("getBenVisitDetailsFrmNurseNCDCare should assemble the visit, adherence and investigations")
		void getVisitDetails_shouldAssembleVisitSections() {
			when(commonNurseServiceImpl.getBenAdherence(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");
			when(commonNurseServiceImpl.getLabTestOrders(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			String result = service.getBenVisitDetailsFrmNurseNCDCare(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("NCDCareNurseVisitDetail"));
			assertTrue(result.contains("BenAdherence"));
			assertTrue(result.contains("Investigation"));
		}

		@Test
		@DisplayName("getBenNCDCareHistoryDetails should assemble every stored history section")
		void getHistoryDetails_shouldAssembleStoredHistorySections() {
			when(commonNurseServiceImpl.getPastHistoryData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.BenMedHistory());
			when(commonNurseServiceImpl.getFeedingHistory(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new com.iemr.tm.data.anc.ChildFeedingDetails());

			String result = service.getBenNCDCareHistoryDetails(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("PastHistory"));
			assertTrue(result.contains("FeedingHistory"));
		}

		@Test
		@DisplayName("getBeneficiaryVitalDetails should assemble the anthropometry and the physical vitals")
		void getVitalDetails_shouldAssembleVitalSections() {
			when(commonNurseServiceImpl.getBeneficiaryPhysicalAnthropometryDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn("{}");
			when(commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn("{}");

			String result = service.getBeneficiaryVitalDetails(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("benAnthropometryDetail"));
			assertTrue(result.contains("benPhysicalVitalDetail"));
		}

		@Test
		@DisplayName("getBenNCDCareNurseData should assemble the vitals and the history")
		void getNurseData_shouldAssembleVitalsAndHistory() {
			String result = service.getBenNCDCareNurseData(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("vitals"));
			assertTrue(result.contains("history"));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorNCDCare should assemble the whole doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			when(commonNurseServiceImpl.getGraphicalTrendData(BEN_REG_ID, "ncdCare")).thenReturn(new HashMap<>());

			String result = service.getBenCaseRecordFromDoctorNCDCare(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.contains("findings"));
			assertTrue(result.contains("diagnosis"));
			assertTrue(result.contains("prescription"));
			assertTrue(result.contains("LabReport"));
			assertTrue(result.contains("GraphData"));
		}
	}

	@Nested
	@DisplayName("saveDoctorData")
	class SaveDoctorDataTests

	{
		private JsonObject doctorRequest() {
			return json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
					+ "\"createdBy\":\"doctor1\",\"doctorSignatureFlag\":true,"
					+ "\"findings\":{},"
					+ "\"diagnosis\":{\"specialistDiagnosis\":\"NCD follow up\","
					+ "  \"provisionalDiagnosisList\":[{\"term\":\"Diabetes\",\"conceptID\":\"73211009\"}]},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");
		}

		@BeforeEach
		void stubDoctorCollaborators() throws Exception {
			when(commonDoctorServiceImpl.saveDocFindings(any())).thenReturn(1);
			when(commonNurseServiceImpl.savePrescriptionDetailsAndGetPrescriptionID(any(), any(), any(), any(), any(),
					any(), any(), any(), any(), any())).thenReturn(4L);
			when(ncdCareDoctorServiceImpl.saveNCDDiagnosisData(any())).thenReturn(1L);
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
			verify(ncdCareDoctorServiceImpl).saveNCDDiagnosisData(any());
			verify(commonNurseServiceImpl).saveBenInvestigation(any());
			verify(commonNurseServiceImpl).saveBenPrescribedDrugsList(any());
			verify(commonDoctorServiceImpl).saveBenReferDetails(any());
		}

		@Test
		@DisplayName("saveDoctorData should succeed for a case record with only an investigation section")
		void saveDoctorData_shouldSucceedForMinimalCaseRecord() throws Exception {
			assertEquals(1L, service.saveDoctorData(json("{\"beneficiaryRegID\":11,\"investigation\":{}}"),
					AUTHORIZATION));

			verify(commonDoctorServiceImpl, never()).saveDocFindings(any());
			verify(commonNurseServiceImpl, never()).saveBenPrescribedDrugsList(any());
		}

		@Test
		@DisplayName("saveDoctorData should return nothing for a null request")
		void saveDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.saveDoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("saveDoctorData should fail when a section could not be stored")
		void saveDoctorData_shouldFailWhenSectionNotStored() throws Exception {
			when(ncdCareDoctorServiceImpl.saveNCDDiagnosisData(any())).thenReturn(0L);

			assertThrows(RuntimeException.class, () -> service.saveDoctorData(doctorRequest(), AUTHORIZATION));
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

			verify(sMSGatewayServiceImpl).smsSenderGateway(eq("schedule"), anyLong(), anyInt(), anyLong(), any(),
					any(), anyString(), any(), eq(AUTHORIZATION));
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
			when(commonNurseServiceImpl.updateANCAnthropometryDetails(any())).thenReturn(1);
			when(commonNurseServiceImpl.updateANCPhysicalVitalDetails(any())).thenReturn(0);

			assertEquals(0, service.updateBenVitalDetails(json("{}")));
		}

		@Test
		@DisplayName("updateBenVitalDetails should succeed for a null payload")
		void updateVitals_shouldSucceedForNullPayload() throws Exception {
			assertEquals(1, service.updateBenVitalDetails(null));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should return nothing for a null request")
		void updateDoctorData_shouldReturnNothingForNullRequest() throws Exception {
			assertNull(service.updateNCDCareDoctorData(null, AUTHORIZATION));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should update the whole case record")
		void updateDoctorData_shouldUpdateWholeCaseRecord() throws Exception {
			when(commonDoctorServiceImpl.updateDocFindings(any())).thenReturn(1);
			when(ncdCareDoctorServiceImpl.updateBenNCDCareDiagnosis(any())).thenReturn(1);
			when(commonNurseServiceImpl.saveBenInvestigation(any())).thenReturn(1L);
			when(commonNurseServiceImpl.saveBenPrescribedDrugsList(any())).thenReturn(1);
			when(commonDoctorServiceImpl.updateBenReferDetails(any())).thenReturn(1L);
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(1);

			JsonObject request = json("{" + "\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,"
					+ "\"findings\":{},\"diagnosis\":{\"prescriptionID\":4,\"specialistDiagnosis\":\"NCD\"},"
					+ "\"investigation\":{\"laboratoryList\":[{\"testID\":1}]},"
					+ "\"prescription\":[{\"drugID\":1,\"formName\":\"Tablet\"}],"
					+ "\"refer\":{\"referredToInstituteID\":1}" + "}");

			assertEquals(1L, service.updateNCDCareDoctorData(request, AUTHORIZATION));
		}

		@Test
		@DisplayName("updateNCDCareDoctorData should fail when the beneficiary flow could not be advanced")
		void updateDoctorData_shouldFailWhenFlowNotAdvanced() throws Exception {
			when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(any(), anyBoolean(), anyBoolean(), any(),
					anyBoolean())).thenReturn(0);

			assertThrows(RuntimeException.class, () -> service.updateNCDCareDoctorData(
					json("{\"beneficiaryRegID\":11,\"investigation\":{}}"), AUTHORIZATION));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("NCD care captured-section updates")
	class CapturedSectionUpdateTests {

		@Test
		@DisplayName("updateBenHistoryDetails should update every captured history section")
		void updateBenHistoryDetails_shouldUpdateEveryCapturedSection() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.updateBenHistoryDetails(
					com.google.gson.JsonParser.parseString("{\"pastHistory\":{},\"comorbidConditions\":{},\"medicationHistory\":{},\"personalHistory\":{},\"familyHistory\":{},\"menstrualHistory\":{},\"femaleObstetricHistory\":{},\"immunizationHistory\":{},\"childVaccineDetails\":{},\"developmentHistory\":{},\"feedingHistory\":{},\"perinatalHistroy\":{},\"allergyHistory\":{}}").getAsJsonObject()));
		}
	}
}
