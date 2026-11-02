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
package com.iemr.tm.service.labtechnician;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.labModule.ECGAbnormalFindingMasterRepo;
import com.iemr.tm.repo.labModule.LabResultEntryRepo;
import com.iemr.tm.repo.labtechnician.V_benLabTestOrderedDetailsRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LabTechnicianServiceImpl Test Suite")
class LabTechnicianServiceImplTest {

	@Mock
	private V_benLabTestOrderedDetailsRepo v_benLabTestOrderedDetailsRepo;
	@Mock
	private LabResultEntryRepo labResultEntryRepo;
	@Mock
	private ECGAbnormalFindingMasterRepo ecgAbnormalFindingMasterRepo;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private com.iemr.tm.repo.nurse.BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private com.iemr.tm.repo.login.UserLoginRepo userLoginRepo;

	@InjectMocks
	private LabTechnicianServiceImpl service;

	@Test
	@DisplayName("getBenePrescribedProcedureDetails should answer for a well formed request")
	void getBenePrescribedProcedureDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenePrescribedProcedureDetails(11L, 11L));
	}

	@Test
	@DisplayName("getLabResultDataForBen should answer for a well formed request")
	void getLabResultDataForBen_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getLabResultDataForBen(11L, 11L));
	}

	@Test
	@DisplayName("saveLabTestResult should answer for a well formed request")
	void saveLabTestResult_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveLabTestResult(new com.google.gson.JsonObject()));
	}


	@Test
	@DisplayName("getLast_3_ArchivedTestVisitList should answer for a well formed request")
	void getLast_3_ArchivedTestVisitList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getLast_3_ArchivedTestVisitList(11L, 11L));
	}

	@Test
	@DisplayName("getLabResultForVisitcode should answer for a well formed request")
	void getLabResultForVisitcode_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getLabResultForVisitcode(11L, 11L));
	}

	@Test
	@DisplayName("getECGAbnormalFindings should answer for a well formed request")
	void getECGAbnormalFindings_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getECGAbnormalFindings());
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("lab result capture")
	class LabResultCaptureTests {

		private static final String LAB_REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22,\"benVisitID\":3,"
				+ "\"benFlowID\":5,\"createdBy\":\"lab1\",\"providerServiceMapID\":9,\"labCompleted\":true,"
				+ "\"nurseFlag\":2,\"doctorFlag\":2,\"specialist_flag\":1,"
				+ "\"labTestResults\":[{\"procedureID\":1,\"procedureName\":\"CBC\","
				+ "  \"compResult\":[{\"testComponentID\":\"2\",\"testResultValue\":\"12\","
				+ "                  \"testResultUnit\":\"g/dL\",\"remarks\":\"normal\"}]}],"
				+ "\"radiologyTestResults\":[]}";

		private com.google.gson.JsonObject request() {
			return com.google.gson.JsonParser.parseString(LAB_REQUEST).getAsJsonObject();
		}

		@Test
		@DisplayName("saveLabTestResult should store the entered results and stamp the lab technician")
		void saveLabTestResult_shouldStoreResultsAndStampTechnician() throws Exception {
			com.iemr.tm.data.login.Users user = new com.iemr.tm.data.login.Users();
			user.setUserID(42L);
			org.mockito.Mockito.when(userLoginRepo.getUserByUsername("lab1")).thenReturn(user);
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntryForTCSpecialist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntry(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(request()));
			org.mockito.Mockito.verify(benVisitDetailRepo).updateLabTechnicianID(42L, 22L);
		}

		@Test
		@DisplayName("saveLabTestResult should succeed when the request carries no result to store")
		void saveLabTestResult_shouldSucceedWithoutResults() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11}").getAsJsonObject()));
		}

		@Test
		@DisplayName("saveLabTestResult should store the entered results for a wrapper payload")
		void saveLabTestResult_shouldStoreWrapperResults() throws Exception {
			com.iemr.tm.data.labModule.WrapperLabResultEntry wrapper = com.iemr.tm.utils.mapper.InputMapper.gson()
					.fromJson(request(), com.iemr.tm.data.labModule.WrapperLabResultEntry.class);
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));

			org.junit.jupiter.api.Assertions.assertNotNull(service.saveLabTestResult(wrapper));
		}

		@Test
		@DisplayName("getLabResultDataForBen should expand the stored result components")
		void getLabResultDataForBen_shouldExpandStoredComponents() {
			com.iemr.tm.data.labModule.LabResultEntry entry = new com.iemr.tm.data.labModule.LabResultEntry();
			entry.setBeneficiaryRegID(11L);
			entry.setVisitCode(22L);
			com.iemr.tm.data.labModule.ProcedureData procedure = new com.iemr.tm.data.labModule.ProcedureData();
			procedure.setProcedureName("CBC");
			procedure.setProcedureType("Laboratory");
			entry.setProcedureData(procedure);
			com.iemr.tm.data.labModule.TestComponentMaster component =
					new com.iemr.tm.data.labModule.TestComponentMaster();
			component.setTestComponentName("Haemoglobin");
			entry.setTestComponentMaster(component);
			org.mockito.Mockito.when(labResultEntryRepo
					.findByBeneficiaryRegIDAndVisitCodeOrderByProcedureIDAsc(11L, 22L))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(entry)));

			org.junit.jupiter.api.Assertions.assertFalse(service.getLabResultDataForBen(11L, 22L).isEmpty());
		}

		@Test
		@DisplayName("getLabResultForVisitcode should render the stored results for the visit")
		void getLabResultForVisitcode_shouldRenderStoredResults() {
			org.mockito.Mockito.when(labResultEntryRepo
					.findByBeneficiaryRegIDAndVisitCodeOrderByProcedureIDAsc(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getLabResultForVisitcode(11L, 22L));
		}

		@Test
		@DisplayName("getLast_3_ArchivedTestVisitList should render the three most recent tested visits")
		void getArchivedVisitList_shouldRenderRecentVisits() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.getLast_3_visitForLabTestDone(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getLast_3_ArchivedTestVisitList(11L, 22L));
		}

		@Test
		@DisplayName("getECGAbnormalFindings should render the ECG findings master")
		void getECGAbnormalFindings_shouldRenderFindingsMaster() {
			org.mockito.Mockito.when(ecgAbnormalFindingMasterRepo.findByDeleted(false))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getECGAbnormalFindings());
		}

		@Test
		@DisplayName("getBenePrescribedProcedureDetails should render the prescribed procedures")
		void getPrescribedProcedures_shouldRenderProcedures() {
			org.mockito.Mockito.when(v_benLabTestOrderedDetailsRepo
					.findDistinctByBeneficiaryRegIDAndVisitCodeAndProcedureTypeAndProcedureIDNotInOrderByProcedureIDAscTestComponentIDAscResultValueAsc(
							org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
							org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenePrescribedProcedureDetails(11L, 22L));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("prescribed procedure assembly")
	class PrescribedProcedureTests {

		private com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails orderedTest(String procedureType,
				Integer procedureId, Integer componentId) {
			com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails ordered =
					new com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails();
			ordered.setBeneficiaryRegID(11L);
			ordered.setVisitCode(22L);
			ordered.setProcedureID(procedureId);
			ordered.setProcedureName("CBC");
			ordered.setProcedureType(procedureType);
			ordered.setPrescriptionID(4L);
			ordered.setIsMandatory(true);
			ordered.setTestComponentID(componentId);
			ordered.setTestComponentName("Haemoglobin");
			ordered.setInputType("Text");
			ordered.setMeasurementUnit("g/dL");
			return ordered;
		}

		private void orderedTestsAre(String procedureType,
				java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails> ordered) {
			org.mockito.Mockito.when(v_benLabTestOrderedDetailsRepo
					.findDistinctByBeneficiaryRegIDAndVisitCodeAndProcedureTypeAndProcedureIDNotInOrderByProcedureIDAscTestComponentIDAscResultValueAsc(
							org.mockito.ArgumentMatchers.eq(11L), org.mockito.ArgumentMatchers.eq(22L),
							org.mockito.ArgumentMatchers.eq(procedureType), org.mockito.ArgumentMatchers.any()))
					.thenReturn(ordered);
		}

		@Test
		@DisplayName("getBenePrescribedProcedureDetails should group the components of an ordered laboratory test")
		void getPrescribedProcedures_shouldGroupLaboratoryComponents() {
			java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails> ordered =
					new java.util.ArrayList<>();
			ordered.add(orderedTest("Laboratory", 1, 2));
			ordered.add(orderedTest("Laboratory", 1, 3));
			ordered.add(orderedTest("Laboratory", 5, 6));
			orderedTestsAre("Laboratory", ordered);
			orderedTestsAre("Radiology",
					new java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails>());
			org.mockito.Mockito.when(labResultEntryRepo
					.findByBeneficiaryRegIDAndVisitCodeOrderByProcedureIDAsc(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			String result = service.getBenePrescribedProcedureDetails(11L, 22L);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("CBC"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Haemoglobin"));
		}

		@Test
		@DisplayName("getBenePrescribedProcedureDetails should list an ordered radiology test")
		void getPrescribedProcedures_shouldListRadiologyTest() {
			java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails> ordered =
					new java.util.ArrayList<>();
			ordered.add(orderedTest("Radiology", 7, null));
			orderedTestsAre("Radiology", ordered);
			orderedTestsAre("Laboratory",
					new java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails>());
			org.mockito.Mockito.when(labResultEntryRepo
					.findByBeneficiaryRegIDAndVisitCodeOrderByProcedureIDAsc(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenePrescribedProcedureDetails(11L, 22L));
		}

		@Test
		@DisplayName("getBenePrescribedProcedureDetails should skip procedures whose result is already entered")
		void getPrescribedProcedures_shouldSkipAlreadyEnteredResults() {
			com.iemr.tm.data.labModule.LabResultEntry entered = new com.iemr.tm.data.labModule.LabResultEntry();
			entered.setProcedureID(1);
			com.iemr.tm.data.labModule.ProcedureData procedure = new com.iemr.tm.data.labModule.ProcedureData();
			procedure.setProcedureName("CBC");
			procedure.setProcedureType("Laboratory");
			entered.setProcedureData(procedure);
			com.iemr.tm.data.labModule.TestComponentMaster component =
					new com.iemr.tm.data.labModule.TestComponentMaster();
			component.setTestComponentName("Haemoglobin");
			entered.setTestComponentMaster(component);
			org.mockito.Mockito.when(labResultEntryRepo
					.findByBeneficiaryRegIDAndVisitCodeOrderByProcedureIDAsc(11L, 22L))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(entered)));
			orderedTestsAre("Laboratory",
					new java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails>());
			orderedTestsAre("Radiology",
					new java.util.ArrayList<com.iemr.tm.data.labtechnician.V_benLabTestOrderedDetails>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenePrescribedProcedureDetails(11L, 22L));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("lab result entry payload")
	class LabResultPayloadTests {

		private static final String WITH_COMPONENTS = "{\"beneficiaryRegID\":11,\"visitCode\":22,\"visitID\":3,"
				+ "\"benFlowID\":5,\"createdBy\":\"lab1\",\"providerServiceMapID\":9,\"vanID\":7,"
				+ "\"parkingPlaceID\":2,\"labCompleted\":true,\"nurseFlag\":2,\"doctorFlag\":2,\"specialist_flag\":1,"
				+ "\"labTestResults\":[{\"prescriptionID\":4,\"procedureID\":1,\"abnormalFindings\":[3],"
				+ "  \"compList\":["
				+ "    {\"testComponentID\":\"2\",\"testResultValue\":\"12\",\"testResultUnit\":\"g/dL\","
				+ "     \"remarks\":\"normal\"},"
				+ "    {\"testComponentID\":\"3\",\"stripsNotAvailable\":\"true\"},"
				+ "    {\"testComponentID\":\"4\",\"testResultValue\":\"\"},"
				+ "    {\"testComponentID\":\"\",\"testResultValue\":\"9\"}]}],"
				+ "\"radiologyTestResults\":[{\"prescriptionID\":4,\"procedureID\":7,"
				+ "  \"testResultValue\":\"No abnormality\",\"fileIDs\":[81,82]}]}";

		private com.iemr.tm.data.labModule.WrapperLabResultEntry wrapper(String raw) throws Exception {
			return com.iemr.tm.utils.mapper.InputMapper.gson().fromJson(
					com.google.gson.JsonParser.parseString(raw).getAsJsonObject(),
					com.iemr.tm.data.labModule.WrapperLabResultEntry.class);
		}

		@Test
		@DisplayName("saveLabTestResult should store one row per measured component and attach the report files")
		void saveLabTestResult_shouldStoreOneRowPerMeasuredComponent() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(wrapper(WITH_COMPONENTS)));

			@SuppressWarnings("unchecked")
			org.mockito.ArgumentCaptor<java.util.List<com.iemr.tm.data.labModule.LabResultEntry>> captor =
					org.mockito.ArgumentCaptor.forClass(java.util.List.class);
			org.mockito.Mockito.verify(labResultEntryRepo).saveAll(captor.capture());
			java.util.List<com.iemr.tm.data.labModule.LabResultEntry> stored = captor.getValue();
			org.junit.jupiter.api.Assertions.assertEquals(3, stored.size());
			org.junit.jupiter.api.Assertions.assertEquals("12", stored.get(0).getTestResultValue());
			org.junit.jupiter.api.Assertions.assertEquals("g/dL", stored.get(0).getTestResultUnit());
			org.junit.jupiter.api.Assertions.assertEquals("normal", stored.get(0).getRemarks());
			org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, stored.get(1).getStripsNotAvailable());
			org.junit.jupiter.api.Assertions.assertEquals("81,82,", stored.get(2).getTestReportFilePath());
			org.junit.jupiter.api.Assertions.assertEquals(7, stored.get(2).getProcedureID());
		}

		@Test
		@DisplayName("saveLabTestResult should report a failure when the stored rows do not match")
		void saveLabTestResult_shouldReportStoreMismatch() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNull(service.saveLabTestResult(wrapper(WITH_COMPONENTS)));
		}

		@Test
		@DisplayName("saveLabTestResult should succeed when the wrapper carries no result at all")
		void saveLabTestResult_shouldSucceedForEmptyWrapper() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(wrapper(
					"{\"beneficiaryRegID\":11,\"labTestResults\":[],\"radiologyTestResults\":[]}")));
		}

		@Test
		@DisplayName("saveLabTestResult should succeed when every component was left blank")
		void saveLabTestResult_shouldSucceedWhenEveryComponentBlank() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(wrapper(
					"{\"beneficiaryRegID\":11,\"labTestResults\":[{\"procedureID\":1,"
							+ "\"compList\":[{\"testComponentID\":\"2\",\"testResultValue\":\"\"}]}],"
							+ "\"radiologyTestResults\":[]}")));
		}

		@Test
		@DisplayName("saveLabTestResult should send a completed specialist visit back to the specialist")
		void saveLabTestResult_shouldReturnCompletedSpecialistVisit() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntryForTCSpecialist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(
					com.google.gson.JsonParser.parseString(
							WITH_COMPONENTS.replace("\"specialist_flag\":1", "\"specialist_flag\":2"))
							.getAsJsonObject()));

			org.mockito.Mockito.verify(commonBenStatusFlowServiceImpl)
					.updateFlowAfterLabResultEntryForTCSpecialist(5L, 11L, (short) 3);
		}

		@Test
		@DisplayName("saveLabTestResult should keep a partly tested specialist visit with the lab")
		void saveLabTestResult_shouldKeepPartlyTestedSpecialistVisitWithLab() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntryForTCSpecialist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(
					com.google.gson.JsonParser.parseString(WITH_COMPONENTS
							.replace("\"specialist_flag\":1", "\"specialist_flag\":2")
							.replace("\"labCompleted\":true", "\"labCompleted\":false")).getAsJsonObject()));

			org.mockito.Mockito.verify(commonBenStatusFlowServiceImpl)
					.updateFlowAfterLabResultEntryForTCSpecialist(5L, 11L, (short) 2);
		}

		@Test
		@DisplayName("saveLabTestResult should hand a completed nurse visit to the doctor")
		void saveLabTestResult_shouldHandCompletedNurseVisitToDoctor() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntry(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(
					com.google.gson.JsonParser.parseString(WITH_COMPONENTS).getAsJsonObject()));

			org.mockito.Mockito.verify(commonBenStatusFlowServiceImpl).updateFlowAfterLabResultEntry(5L, 11L, 3L,
					(short) 3, (short) 1, (short) 1);
		}

		@Test
		@DisplayName("saveLabTestResult should hand a completed doctor visit back to the doctor")
		void saveLabTestResult_shouldHandCompletedDoctorVisitBackToDoctor() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntry(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(
					com.google.gson.JsonParser.parseString(
							WITH_COMPONENTS.replace("\"nurseFlag\":2", "\"nurseFlag\":3")).getAsJsonObject()));

			org.mockito.Mockito.verify(commonBenStatusFlowServiceImpl).updateFlowAfterLabResultEntry(5L, 11L, 3L,
					(short) 3, (short) 3, (short) 1);
		}

		@Test
		@DisplayName("saveLabTestResult should keep a partly tested visit with the lab")
		void saveLabTestResult_shouldKeepPartlyTestedVisitWithLab() throws Exception {
			org.mockito.Mockito.when(labResultEntryRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateFlowAfterLabResultEntry(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.saveLabTestResult(
					com.google.gson.JsonParser.parseString(
							WITH_COMPONENTS.replace("\"labCompleted\":true", "\"labCompleted\":false"))
							.getAsJsonObject()));
		}
	}
}
