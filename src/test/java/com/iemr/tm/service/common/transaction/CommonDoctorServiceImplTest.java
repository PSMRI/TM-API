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
package com.iemr.tm.service.common.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.tm.data.anc.WrapperAncFindings;
import com.iemr.tm.data.quickConsultation.BenClinicalObservations;
import com.iemr.tm.data.login.Users;
import com.iemr.tm.data.quickConsultation.BenChiefComplaint;
import com.iemr.tm.data.snomedct.SCTDescription;
import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.quickConsultation.BenClinicalObservationsRepo;
import com.iemr.tm.repo.doctor.BenReferDetailsRepo;
import com.iemr.tm.repo.login.UserLoginRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.repo.quickConsultation.LabTestOrderDetailRepo;
import com.iemr.tm.repo.quickConsultation.PrescribedDrugDetailRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.snomedct.SnomedServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;
import com.iemr.tm.utils.CookieUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CommonDoctorServiceImpl Test Suite")
class CommonDoctorServiceImplTest {

	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;

	@Mock
	private BenClinicalObservationsRepo benClinicalObservationsRepo;
	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private com.iemr.tm.repo.doctor.DocWorkListRepo docWorkListRepo;
	@Mock
	private BenReferDetailsRepo benReferDetailsRepo;
	@Mock
	private LabTestOrderDetailRepo labTestOrderDetailRepo;
	@Mock
	private PrescribedDrugDetailRepo prescribedDrugDetailRepo;
	@Mock
	private SnomedServiceImpl snomedServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private com.iemr.tm.repo.tc_consultation.TCRequestModelRepo tCRequestModelRepo;
	@Mock
	private com.iemr.tm.repo.nurse.pnc.PNCDiagnosisRepo pNCDiagnosisRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private UserLoginRepo userLoginRepo;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;
	@Mock
	private com.iemr.tm.repo.nurse.ncdcare.NCDCareDiagnosisRepo NCDCareDiagnosisRepo;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Mock
	private com.iemr.tm.repo.foetalmonitor.FoetalMonitorRepo foetalMonitorRepo;
	@Mock
	private CookieUtil cookieUtil;
	@Mock
	private com.iemr.tm.repo.tc_consultation.TeleconsultationStatsRepo teleconsultationStatsRepo;

	@InjectMocks
	private CommonDoctorServiceImpl service;

	private static <T> org.mockito.stubbing.Answer<T> echoList() {
		return invocation -> invocation.getArgument(0);
	}

	private JsonObject json(String raw) {
		return JsonParser.parseString(raw).getAsJsonObject();
	}

	private WrapperAncFindings findings(String complaint) {
		ArrayList<BenChiefComplaint> complaints = new ArrayList<>();
		BenChiefComplaint chiefComplaint = new BenChiefComplaint();
		chiefComplaint.setChiefComplaint(complaint);
		complaints.add(chiefComplaint);
		WrapperAncFindings wrapper = new WrapperAncFindings(BEN_REG_ID, 3L, 9, "Clinical observation", "Fever",
				"Significant findings", complaints, Boolean.FALSE, VISIT_CODE);
		wrapper.setCreatedBy("doctor1");
		return wrapper;
	}

	private ArrayList<Object[]> oneEmptyRow() {
		ArrayList<Object[]> rows = new ArrayList<>();
		rows.add(new Object[60]);
		return rows;
	}

	@Nested
	@DisplayName("findings")
	class FindingsTests {

		@Test
		@DisplayName("saveFindings should confirm the stored clinical observation")
		void saveFindings_shouldConfirmStoredObservation() throws Exception {
			when(benClinicalObservationsRepo.save(any())).thenReturn(new BenClinicalObservations());

			assertEquals(1, service.saveFindings(json("{\"beneficiaryRegID\":11}")));
		}

		@Test
		@DisplayName("saveFindings should report no change when nothing was stored")
		void saveFindings_shouldReportNoChangeWhenNothingStored() throws Exception {
			when(benClinicalObservationsRepo.save(any())).thenReturn(null);

			assertEquals(0, service.saveFindings(json("{\"beneficiaryRegID\":11}")));
		}

		@Test
		@DisplayName("saveDocFindings should store the observation and the named chief complaints")
		void saveDocFindings_shouldStoreObservationAndComplaints() {
			when(benClinicalObservationsRepo.save(any())).thenReturn(new BenClinicalObservations());
			when(benChiefComplaintRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.saveDocFindings(findings("Fever")));
			verify(benChiefComplaintRepo).saveAll(any());
		}

		@Test
		@DisplayName("saveDocFindings should skip an unnamed chief complaint")
		void saveDocFindings_shouldSkipUnnamedComplaint() {
			when(benClinicalObservationsRepo.save(any())).thenReturn(new BenClinicalObservations());

			assertEquals(1, service.saveDocFindings(findings(null)));
			verify(benChiefComplaintRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveDocFindings should report no change when the observation was not stored")
		void saveDocFindings_shouldReportNoChangeWhenObservationNotStored() {
			when(benClinicalObservationsRepo.save(any())).thenReturn(null);

			assertEquals(0, service.saveDocFindings(findings(null)));
		}

		@Test
		@DisplayName("updateDocFindings should update the observation and replace the chief complaints")
		void updateDocFindings_shouldUpdateObservationAndReplaceComplaints() {
			when(benClinicalObservationsRepo.getBenClinicalObservationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");
			when(benClinicalObservationsRepo.updateBenClinicalObservations(any(), any(), any(), any(), any(), any(),
					any(), any(), any(), any())).thenReturn(1);
			when(benChiefComplaintRepo.saveAll(any())).thenAnswer(echoList());

			assertNotNull(service.updateDocFindings(findings("Fever")));
		}

		@Test
		@DisplayName("fetchBenPreviousSignificantFindings should render the earlier findings")
		void fetchPreviousFindings_shouldRenderEarlierFindings() {
			when(benClinicalObservationsRepo.getPreviousSignificantFindings(BEN_REG_ID))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.fetchBenPreviousSignificantFindings(BEN_REG_ID));
		}

		@Test
		@DisplayName("getFindingsDetails should assemble the observation and the chief complaints")
		void getFindingsDetails_shouldAssembleFindings() {
			when(benClinicalObservationsRepo.getFindingsData(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());
			when(benChiefComplaintRepo.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getFindingsDetails(BEN_REG_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("SNOMED lookups")
	class SnomedTests {

		@Test
		@DisplayName("getSnomedCTcode should map every term of a comma separated list")
		void getSnomedCTcode_shouldMapEveryTerm() {
			SCTDescription fever = new SCTDescription();
			fever.setConceptID("386661006");
			fever.setTerm("Fever");
			when(snomedServiceImpl.findSnomedCTRecordFromTerm(anyString())).thenReturn(fever);

			String[] result = service.getSnomedCTcode("Fever,Cough");

			assertEquals("386661006,386661006", result[0]);
			assertEquals("Fever,Fever", result[1]);
		}

		@Test
		@DisplayName("getSnomedCTcode should leave an unmatched term blank")
		void getSnomedCTcode_shouldLeaveUnmatchedTermBlank() {
			when(snomedServiceImpl.findSnomedCTRecordFromTerm(anyString())).thenReturn(null);

			assertNotNull(service.getSnomedCTcode("Fever"));
		}

		@Test
		@DisplayName("getSnomedCTcode should return empty codes for a blank request")
		void getSnomedCTcode_shouldReturnEmptyCodesForBlankRequest() {
			assertNotNull(service.getSnomedCTcode(""));
		}
	}

	@Nested
	@DisplayName("worklists")
	class WorklistTests {

		@Test
		@DisplayName("getDocWorkList should render the doctor worklist")
		void getDocWorkList_shouldRenderWorklist() {
			when(docWorkListRepo.getDocWorkList()).thenReturn(new ArrayList<>());

			assertNotNull(service.getDocWorkList());
		}

		@Test
		@DisplayName("getDocWorkListNew should render the general OPD doctor worklist")
		void getDocWorkListNew_shouldRenderGeneralWorklist() {
			when(beneficiaryFlowStatusRepo.getDocWorkListNew(9)).thenReturn(new ArrayList<>());

			assertEquals("[]", service.getDocWorkListNew(9, 2, 7));
		}

		@Test
		@DisplayName("getDocWorkListNew should render the teleconsultation doctor worklist")
		void getDocWorkListNew_shouldRenderTeleconsultationWorklist() {
			ReflectionTestUtils.setField(service, "docWL", 7);
			when(beneficiaryFlowStatusRepo.getDocWorkListNewTC(any(), any(), any())).thenReturn(new ArrayList<>());

			assertEquals("[]", service.getDocWorkListNew(9, 4, 7));
		}

		@Test
		@DisplayName("getDocWorkListNew should render an empty worklist for an unknown service")
		void getDocWorkListNew_shouldRenderEmptyWorklistForUnknownService() {
			assertEquals("[]", service.getDocWorkListNew(9, 99, 7));
		}

		@Test
		@DisplayName("getDocWorkListNewFutureScheduledForTM should render the future scheduled worklist")
		void getDocWorkListFutureScheduled_shouldRenderWorklist() {
			when(beneficiaryFlowStatusRepo.getDocWorkListNewFutureScheduledTC(any(), any()))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getDocWorkListNewFutureScheduledForTM(9, 4, 7));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNewForTMPatientApp should render the patient app worklist")
		void getTCSpecialistWorkListPatientApp_shouldRenderWorklist() {
			ReflectionTestUtils.setField(service, "tcSpeclistWL", 7);
			when(beneficiaryFlowStatusRepo.getTCSpecialistWorkListNewPatientApp(any(), any(), any(), any()))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getTCSpecialistWorkListNewForTMPatientApp(9, 42, 4, 7));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNewForTM should render the specialist worklist")
		void getTCSpecialistWorkList_shouldRenderWorklist() {
			when(beneficiaryFlowStatusRepo.getTCSpecialistWorkListNew(any(), any(), any()))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getTCSpecialistWorkListNewForTM(9, 42, 4));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNewFutureScheduledForTM should render the future specialist worklist")
		void getTCSpecialistWorkListFutureScheduled_shouldRenderWorklist() {
			when(beneficiaryFlowStatusRepo.getTCSpecialistWorkListNewFutureScheduled(any(), any()))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getTCSpecialistWorkListNewFutureScheduledForTM(9, 42, 4));
		}
	}

	@Nested
	@DisplayName("referrals and case record reads")
	class ReferralAndReadTests {

		@Test
		@DisplayName("saveBenReferDetails should store the referral for every additional service")
		void saveReferDetails_shouldStoreReferralPerService() throws Exception {
			when(benReferDetailsRepo.saveAll(any())).thenAnswer(echoList());

			Long result = service.saveBenReferDetails(json("{\"beneficiaryRegID\":11,\"visitCode\":22,"
					+ "\"refrredToAdditionalServiceList\":[{\"serviceName\":\"Radiology\"}],"
					+ "\"referredToInstituteName\":\"District Hospital\"}"));

			assertNotNull(result);
		}

		@Test
		@DisplayName("saveBenReferDetails should store a referral without any additional service")
		void saveReferDetails_shouldStoreReferralWithoutAdditionalService() throws Exception {
			when(benReferDetailsRepo.saveAll(any())).thenAnswer(echoList());

			assertNotNull(service.saveBenReferDetails(json("{\"beneficiaryRegID\":11,\"visitCode\":22,"
					+ "\"referredToInstituteName\":\"District Hospital\"}")));
		}

		@Test
		@DisplayName("getInvestigationDetails should render the ordered lab tests")
		void getInvestigationDetails_shouldRenderOrderedTests() {
			when(labTestOrderDetailRepo.getLabTestOrderDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			assertNotNull(service.getInvestigationDetails(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getPrescribedDrugs should render the prescribed drugs")
		void getPrescribedDrugs_shouldRenderPrescribedDrugs() {
			when(prescribedDrugDetailRepo.getBenPrescribedDrugDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getPrescribedDrugs(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getReferralDetails should render the referral")
		void getReferralDetails_shouldRenderReferral() {
			when(benReferDetailsRepo.getBenReferDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			assertNotNull(service.getReferralDetails(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getFoetalMonitorData should render the foetal monitor readings")
		void getFoetalMonitorData_shouldRenderReadings() {
			when(foetalMonitorRepo.getFoetalMonitorDetailsForCaseRecord(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			assertNotNull(service.getFoetalMonitorData(BEN_REG_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("updates")
	class UpdateTests {

		@Test
		@DisplayName("updateDoctorBenChiefComplaints should replace the stored complaints")
		void updateChiefComplaints_shouldReplaceStoredComplaints() {
			BenChiefComplaint complaint = new BenChiefComplaint();
			complaint.setBeneficiaryRegID(BEN_REG_ID);
			complaint.setVisitCode(VISIT_CODE);
			when(benChiefComplaintRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateDoctorBenChiefComplaints(Collections.singletonList(complaint)));
		}

		@Test
		@DisplayName("updateDoctorBenChiefComplaints should succeed for an empty list")
		void updateChiefComplaints_shouldSucceedForEmptyList() {
			assertEquals(1, service.updateDoctorBenChiefComplaints(Collections.emptyList()));
		}

		@Test
		@DisplayName("updateBenClinicalObservations should mark an already processed observation as updated")
		void updateClinicalObservations_shouldMarkProcessedAsUpdated() {
			BenClinicalObservations observations = new BenClinicalObservations();
			observations.setBeneficiaryRegID(BEN_REG_ID);
			observations.setVisitCode(VISIT_CODE);
			when(benClinicalObservationsRepo.getBenClinicalObservationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateBenClinicalObservations(observations));
		}

		@Test
		@DisplayName("updateBenClinicalObservations should report no change for a null payload")
		void updateClinicalObservations_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateBenClinicalObservations(null));
		}

		@Test
		@DisplayName("updateBenReferDetails should update the referral for the visit")
		void updateReferDetails_shouldUpdateReferral() throws Exception {
			when(benReferDetailsRepo.saveAll(any())).thenAnswer(echoList());

			assertNotNull(service.updateBenReferDetails(json("{\"beneficiaryRegID\":11,\"visitCode\":22,"
					+ "\"referredToInstituteName\":\"District Hospital\"}")));
		}

		@Test
		@DisplayName("deletePrescribedMedicine should confirm the deletion")
		void deletePrescribedMedicine_shouldConfirmDeletion() throws Exception {
			when(prescribedDrugDetailRepo.deletePrescribedmedicine(4L)).thenReturn(1);

			assertEquals("record deleted successfully",
					service.deletePrescribedMedicine(new org.json.JSONObject().put("id", 4L)));
		}

		@Test
		@DisplayName("deletePrescribedMedicine should return nothing when no row was deleted")
		void deletePrescribedMedicine_shouldReturnNothingWhenNoRowDeleted() throws Exception {
			when(prescribedDrugDetailRepo.deletePrescribedmedicine(4L)).thenReturn(0);

			assertNull(service.deletePrescribedMedicine(new org.json.JSONObject().put("id", 4L)));
		}

		@Test
		@DisplayName("deletePrescribedMedicine should return nothing for a request without an id")
		void deletePrescribedMedicine_shouldReturnNothingWithoutId() {
			assertNull(service.deletePrescribedMedicine(new org.json.JSONObject()));
		}
	}

	@Nested
	@DisplayName("beneficiary flow after doctor data")
	class BeneficiaryFlowTests {

		private com.iemr.tm.data.nurse.CommonUtilityClass utility(boolean isSpecialist) {
			com.iemr.tm.data.nurse.CommonUtilityClass utility = new com.iemr.tm.data.nurse.CommonUtilityClass();
			utility.setBenFlowID(5L);
			utility.setBeneficiaryID(7L);
			utility.setBenVisitID(3L);
			utility.setBeneficiaryRegID(BEN_REG_ID);
			utility.setVisitCode(VISIT_CODE);
			utility.setVisitCategoryID(1);
			utility.setIsSpecialist(isSpecialist);
			utility.setCreatedBy("doctor1");
			utility.setAuthorization("Bearer session-token");
			return utility;
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataSave should route a doctor visit that ordered a test to the lab")
		void updateFlowAfterSave_shouldRouteTestToLab() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataSave(utility(false), true, true, null, false));
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataSave should close the visit when nothing was prescribed")
		void updateFlowAfterSave_shouldCloseVisitWithoutPrescriptions() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataSave(utility(false), false, false, null, false));
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataSave should route a specialist visit through the specialist flow")
		void updateFlowAfterSave_shouldRouteSpecialistVisit() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataFromSpecialist(any(), any(), any(), any(),
					anyShort(), anyShort(), anyShort(), anyShort(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataSave(utility(true), false, false, null, false));
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataSave should read the foetal monitor state for an ANC visit")
		void updateFlowAfterSave_shouldReadFoetalMonitorStateForAncVisit() throws Exception {
			com.iemr.tm.data.nurse.CommonUtilityClass utility = utility(false);
			utility.setVisitCategoryID(4);
			com.iemr.tm.data.foetalmonitor.FoetalMonitor monitor = new com.iemr.tm.data.foetalmonitor.FoetalMonitor();
			monitor.setResultState(false);
			when(foetalMonitorRepo.getFoetalMonitorDetailsByFlowId(5L))
					.thenReturn(new ArrayList<>(Collections.singletonList(monitor)));
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataSave(utility, false, false, null, false));
			verify(foetalMonitorRepo).updateVisitCode(VISIT_CODE, 5L);
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataUpdate should advance the flow for a doctor visit")
		void updateFlowAfterUpdate_shouldAdvanceFlow() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdate(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataUpdate(utility(false), true, true, null, false));
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataUpdate should advance the flow for a specialist visit")
		void updateFlowAfterUpdate_shouldAdvanceSpecialistFlow() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdateTCSpecialist(any(), any(), any(), any(),
					anyShort(), anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataUpdate(utility(true), false, false, null, false));
		}

		@Test
		@DisplayName("createTMPrescriptionSms should do nothing when the prescription has no drug")
		void createPrescriptionSms_shouldDoNothingWithoutDrugs() throws Exception {
			when(prescribedDrugDetailRepo.getPrescriptionDetails(any())).thenReturn(new ArrayList<>());

			service.createTMPrescriptionSms(utility(false));

			verify(sMSGatewayServiceImpl, never()).smsSenderGateway2(anyString(), any(), any(), any(), any(), any());
		}
	}

	@Nested
	@DisplayName("teleconsultation flow and prescription SMS")
	class TeleconsultationFlowTests {

		private com.iemr.tm.data.nurse.CommonUtilityClass utility(boolean isSpecialist, Integer visitCategoryID) {
			com.iemr.tm.data.nurse.CommonUtilityClass utility = new com.iemr.tm.data.nurse.CommonUtilityClass();
			utility.setBenFlowID(5L);
			utility.setBeneficiaryID(7L);
			utility.setBenVisitID(3L);
			utility.setBeneficiaryRegID(BEN_REG_ID);
			utility.setVisitCode(VISIT_CODE);
			utility.setVisitCategoryID(visitCategoryID);
			utility.setIsSpecialist(isSpecialist);
			utility.setCreatedBy("doctor1");
			utility.setPrescriptionID(31L);
			utility.setAuthorization("Bearer session-token");
			return utility;
		}

		private com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest() {
			com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest =
					new com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ();
			tcRequest.setUserID(41);
			tcRequest.setSpecializationID(3);
			tcRequest.setAllocationDate(new java.sql.Timestamp(System.currentTimeMillis()));
			return tcRequest;
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataSave should hand a scheduled visit to the specialist")
		void updateFlowAfterSave_shouldHandScheduledVisitToSpecialist() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataSave(utility(false, 1), false, true, tcRequest(),
					true));
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataUpdate should hand a scheduled visit to the specialist")
		void updateFlowAfterUpdate_shouldHandScheduledVisitToSpecialist() throws Exception {
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdate(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataUpdate(utility(false, 1), false, true, tcRequest(),
					true));
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataUpdate should close the specialist consultation and stamp its end time")
		void updateFlowAfterUpdate_shouldCloseSpecialistConsultation() throws Exception {
			com.iemr.tm.data.tele_consultation.TeleconsultationStats stats =
					new com.iemr.tm.data.tele_consultation.TeleconsultationStats();
			when(teleconsultationStatsRepo.getLatestStartTime(BEN_REG_ID, VISIT_CODE)).thenReturn(stats);
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdateTCSpecialist(any(), any(), any(), any(),
					anyShort(), anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);
			when(prescribedDrugDetailRepo.getPrescriptionDetails(any())).thenReturn(new ArrayList<>());

			assertEquals(1, service.updateBenFlowtableAfterDocDataUpdate(utility(true, 1), false, true, null, true));

			verify(teleconsultationStatsRepo).save(stats);
			verify(tCRequestModelRepo).updateStatusIfConsultationCompleted(BEN_REG_ID, VISIT_CODE, "D");
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataUpdate should start a fresh stats entry when the last one is closed")
		void updateFlowAfterUpdate_shouldStartFreshStatsEntry() throws Exception {
			com.iemr.tm.data.tele_consultation.TeleconsultationStats stats =
					new com.iemr.tm.data.tele_consultation.TeleconsultationStats();
			stats.settMStatsID(8L);
			stats.setEndTime(new java.sql.Timestamp(System.currentTimeMillis()));
			when(teleconsultationStatsRepo.getLatestStartTime(BEN_REG_ID, VISIT_CODE)).thenReturn(stats);
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdateTCSpecialist(any(), any(), any(), any(),
					anyShort(), anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);

			assertEquals(1, service.updateBenFlowtableAfterDocDataUpdate(utility(true, 1), true, false, null, true));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.tele_consultation.TeleconsultationStats> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.tele_consultation.TeleconsultationStats.class);
			verify(teleconsultationStatsRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertNull(captor.getValue().gettMStatsID());
		}

		@Test
		@DisplayName("updateBenFlowtableAfterDocDataUpdate should read the foetal monitor state for an ANC visit")
		void updateFlowAfterUpdate_shouldReadFoetalMonitorStateForAncVisit() throws Exception {
			com.iemr.tm.data.foetalmonitor.FoetalMonitor monitor = new com.iemr.tm.data.foetalmonitor.FoetalMonitor();
			monitor.setResultState(true);
			when(foetalMonitorRepo.getFoetalMonitorDetailsByFlowId(5L))
					.thenReturn(new ArrayList<>(Collections.singletonList(monitor)));
			when(commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdate(any(), any(), any(), any(), anyShort(),
					anyShort(), anyShort(), anyShort(), anyInt(), any(), anyShort(), any())).thenReturn(1);
			when(prescribedDrugDetailRepo.getPrescriptionDetails(any())).thenReturn(new ArrayList<>());

			assertEquals(1, service.updateBenFlowtableAfterDocDataUpdate(utility(false, 4), false, false, null, true));
			verify(foetalMonitorRepo).updateVisitCode(VISIT_CODE, 5L);
		}

		private java.util.List<com.iemr.tm.data.quickConsultation.PrescribedDrugDetail> onePrescribedDrug() {
			com.iemr.tm.data.quickConsultation.PrescribedDrugDetail drug = new com.iemr.tm.data.quickConsultation.PrescribedDrugDetail();
			drug.setDrugName("Paracetamol");
			return new ArrayList<>(Collections.singletonList(drug));
		}

		@Test
		@DisplayName("createTMPrescriptionSms should read the provisional diagnosis for a general OPD prescription")
		void createPrescriptionSms_shouldReadProvisionalDiagnosis() throws Exception {
			when(prescribedDrugDetailRepo.getPrescriptionDetails(31L)).thenReturn(onePrescribedDrug());
			when(prescriptionDetailRepo.getProvisionalDiagnosis(VISIT_CODE, 31L)).thenReturn(new ArrayList<>());
			when(sMSGatewayServiceImpl.smsSenderGateway2(anyString(), any(), any(), any(), any(), any()))
					.thenReturn(1);

			service.createTMPrescriptionSms(utility(false, 6));

			verify(sMSGatewayServiceImpl).smsSenderGateway2(eq("prescription"), any(), anyString(), eq(BEN_REG_ID),
					anyString(), any());
		}

		@Test
		@DisplayName("createTMPrescriptionSms should read the NCD condition for an NCD care prescription")
		void createPrescriptionSms_shouldReadNcdCondition() throws Exception {
			when(prescribedDrugDetailRepo.getPrescriptionDetails(31L)).thenReturn(onePrescribedDrug());
			when(NCDCareDiagnosisRepo.getNCDcondition(VISIT_CODE, 31L)).thenReturn(new ArrayList<>());

			service.createTMPrescriptionSms(utility(false, 3));

			verify(NCDCareDiagnosisRepo).getNCDcondition(VISIT_CODE, 31L);
		}

		@Test
		@DisplayName("createTMPrescriptionSms should read the PNC diagnosis for a PNC prescription")
		void createPrescriptionSms_shouldReadPncDiagnosis() throws Exception {
			when(prescribedDrugDetailRepo.getPrescriptionDetails(31L)).thenReturn(onePrescribedDrug());
			when(pNCDiagnosisRepo.getProvisionalDiagnosis(VISIT_CODE, 31L)).thenReturn(new ArrayList<>());

			service.createTMPrescriptionSms(utility(false, 5));

			verify(pNCDiagnosisRepo).getProvisionalDiagnosis(VISIT_CODE, 31L);
		}

		@Test
		@DisplayName("createTMPrescriptionSms should still send when the diagnosis lookup fails")
		void createPrescriptionSms_shouldStillSendWhenDiagnosisLookupFails() throws Exception {
			when(prescribedDrugDetailRepo.getPrescriptionDetails(31L)).thenReturn(onePrescribedDrug());
			when(prescriptionDetailRepo.getProvisionalDiagnosis(VISIT_CODE, 31L))
					.thenThrow(new RuntimeException("diagnosis unavailable"));

			service.createTMPrescriptionSms(utility(false, 7));

			verify(sMSGatewayServiceImpl).smsSenderGateway2(eq("prescription"), any(), anyString(), eq(BEN_REG_ID),
					anyString(), any());
		}

		@Test
		@DisplayName("createTMPrescriptionSms should absorb a failure from the SMS gateway")
		void createPrescriptionSms_shouldAbsorbGatewayFailure() throws Exception {
			when(prescribedDrugDetailRepo.getPrescriptionDetails(31L)).thenReturn(onePrescribedDrug());
			when(sMSGatewayServiceImpl.smsSenderGateway2(anyString(), any(), any(), any(), any(), any()))
					.thenThrow(new RuntimeException("gateway down"));

			org.junit.jupiter.api.Assertions.assertDoesNotThrow(
					() -> service.createTMPrescriptionSms(utility(false, 8)));
		}

		@Test
		@DisplayName("callTmForSpecialistSlotBook should confirm a booked slot")
		void callTmForSlotBook_shouldConfirmBookedSlot() {
			new com.iemr.tm.utils.mapper.OutputMapper();
			org.springframework.test.util.ReflectionTestUtils.setField(service, "tcSpecialistSlotBook",
					"http://common/tc/specialistSlotBook");

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					org.mockito.Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
							(restTemplate, context) -> when(restTemplate.exchange(anyString(),
									any(org.springframework.http.HttpMethod.class), any(),
									org.mockito.ArgumentMatchers.<Class<String>>any()))
									.thenReturn(new org.springframework.http.ResponseEntity<>(
											"{\"statusCode\":200}", org.springframework.http.HttpStatus.OK)))) {
				assertEquals(1, service.callTmForSpecialistSlotBook(
						new com.iemr.tm.data.tele_consultation.TcSpecialistSlotBookingRequestOBJ(),
						"Bearer session-token"));
			}
		}

		@Test
		@DisplayName("callTmForSpecialistSlotBook should report a slot the scheduler refused")
		void callTmForSlotBook_shouldReportRefusedSlot() {
			new com.iemr.tm.utils.mapper.OutputMapper();
			org.springframework.test.util.ReflectionTestUtils.setField(service, "tcSpecialistSlotBook",
					"http://common/tc/specialistSlotBook");

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					org.mockito.Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
							(restTemplate, context) -> when(restTemplate.exchange(anyString(),
									any(org.springframework.http.HttpMethod.class), any(),
									org.mockito.ArgumentMatchers.<Class<String>>any()))
									.thenReturn(new org.springframework.http.ResponseEntity<>(
											"{\"statusCode\":5000}", org.springframework.http.HttpStatus.OK)))) {
				assertEquals(0, service.callTmForSpecialistSlotBook(
						new com.iemr.tm.data.tele_consultation.TcSpecialistSlotBookingRequestOBJ(),
						"Bearer session-token"));
			}
		}
	}
}
