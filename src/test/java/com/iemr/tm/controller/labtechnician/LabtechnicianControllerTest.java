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
package com.iemr.tm.controller.labtechnician;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.google.gson.JsonObject;
import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("LabtechnicianController Test Suite")
class LabtechnicianControllerTest {

	private static final String VISIT_REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";

	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;

	private LabtechnicianController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked lab technician service")
	void setUp() {
		controller = new LabtechnicianController();
		controller.setLabTechnicianServiceImpl(labTechnicianServiceImpl);
	}

	@Nested
	@DisplayName("saveLabTestResult")
	class SaveLabTestResultTests {

		@Test
		@DisplayName("saveLabTestResult should confirm the save when rows were written")
		void saveLabTestResult_shouldConfirmSave() throws Exception {
			when(labTechnicianServiceImpl.saveLabTestResult(any(JsonObject.class))).thenReturn(1);

			assertTrue(controller.saveLabTestResult("{\"visitCode\":22}").contains("Data saved successfully"));
		}

		@Test
		@DisplayName("saveLabTestResult should report an unsuccessful save")
		void saveLabTestResult_shouldReportUnsuccessfulSave() throws Exception {
			when(labTechnicianServiceImpl.saveLabTestResult(any(JsonObject.class))).thenReturn(0);

			assertTrue(controller.saveLabTestResult("{\"visitCode\":22}").contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveLabTestResult should report an unsuccessful save when no count comes back")
		void saveLabTestResult_shouldReportUnsuccessfulSaveForNullCount() throws Exception {
			when(labTechnicianServiceImpl.saveLabTestResult(any(JsonObject.class))).thenReturn(null);

			assertTrue(controller.saveLabTestResult("{\"visitCode\":22}").contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveLabTestResult should surface a service failure")
		void saveLabTestResult_shouldSurfaceServiceFailure() throws Exception {
			when(labTechnicianServiceImpl.saveLabTestResult(any(JsonObject.class)))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.saveLabTestResult("{\"visitCode\":22}").contains("Unable to save data"));
		}

		@Test
		@DisplayName("saveLabTestResult should surface a malformed request")
		void saveLabTestResult_shouldSurfaceMalformedRequest() {
			assertTrue(controller.saveLabTestResult("not-json").contains("Unable to save data"));
		}
	}

	@Nested
	@DisplayName("getBeneficiaryPrescribedProcedure")
	class PrescribedProcedureTests {

		@Test
		@DisplayName("getBeneficiaryPrescribedProcedure should return the prescribed procedures")
		void getBeneficiaryPrescribedProcedure_shouldReturnProcedures() throws Exception {
			when(labTechnicianServiceImpl.getBenePrescribedProcedureDetails(11L, 22L)).thenReturn("[{\"procedure\":1}]");

			assertTrue(controller.getBeneficiaryPrescribedProcedure(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBeneficiaryPrescribedProcedure should report a failure when nothing comes back")
		void getBeneficiaryPrescribedProcedure_shouldReportFailureWhenNothingFound() throws Exception {
			when(labTechnicianServiceImpl.getBenePrescribedProcedureDetails(11L, 22L)).thenReturn(null);

			assertTrue(controller.getBeneficiaryPrescribedProcedure(VISIT_REQUEST)
					.contains("Error in prescribed procedure details"));
		}

		@Test
		@DisplayName("getBeneficiaryPrescribedProcedure should reject a request without the visit code")
		void getBeneficiaryPrescribedProcedure_shouldRejectIncompleteRequest() throws Exception {
			String result = controller.getBeneficiaryPrescribedProcedure("{\"beneficiaryRegID\":11}");

			assertTrue(result.contains("Invalid request"));
			verify(labTechnicianServiceImpl, never()).getBenePrescribedProcedureDetails(anyLong(), anyLong());
		}

		@Test
		@DisplayName("getBeneficiaryPrescribedProcedure should surface a service failure")
		void getBeneficiaryPrescribedProcedure_shouldSurfaceServiceFailure() throws Exception {
			when(labTechnicianServiceImpl.getBenePrescribedProcedureDetails(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBeneficiaryPrescribedProcedure(VISIT_REQUEST)
					.contains("Error while getting prescribed procedure data"));
		}

		@Test
		@DisplayName("getBeneficiaryPrescribedProcedure should surface a malformed request")
		void getBeneficiaryPrescribedProcedure_shouldSurfaceMalformedRequest() {
			assertTrue(controller.getBeneficiaryPrescribedProcedure("not-json")
					.contains("Error while getting prescribed procedure data"));
		}
	}

	@Nested
	@DisplayName("getLabResultForVisitCode")
	class LabResultTests {

		@Test
		@DisplayName("getLabResultForVisitCode should return the lab report for the visit")
		void getLabResultForVisitCode_shouldReturnLabReport() throws Exception {
			when(labTechnicianServiceImpl.getLabResultForVisitcode(11L, 22L)).thenReturn("[{\"result\":\"normal\"}]");

			assertTrue(controller.getLabResultForVisitCode(VISIT_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getLabResultForVisitCode should report a failure when no report comes back")
		void getLabResultForVisitCode_shouldReportFailureWhenNoReport() throws Exception {
			when(labTechnicianServiceImpl.getLabResultForVisitcode(11L, 22L)).thenReturn(null);

			assertTrue(controller.getLabResultForVisitCode(VISIT_REQUEST).contains("Error while getting lab report"));
		}

		@Test
		@DisplayName("getLabResultForVisitCode should reject a request without the visit code")
		void getLabResultForVisitCode_shouldRejectIncompleteRequest() throws Exception {
			String result = controller.getLabResultForVisitCode("{\"beneficiaryRegID\":11}");

			assertTrue(result.contains("Invalid request"));
			verify(labTechnicianServiceImpl, never()).getLabResultForVisitcode(anyLong(), anyLong());
		}

		@Test
		@DisplayName("getLabResultForVisitCode should surface a service failure")
		void getLabResultForVisitCode_shouldSurfaceServiceFailure() throws Exception {
			when(labTechnicianServiceImpl.getLabResultForVisitcode(11L, 22L))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getLabResultForVisitCode(VISIT_REQUEST).contains("Error while getting lab report"));
		}

		@Test
		@DisplayName("getLabResultForVisitCode should surface a malformed request")
		void getLabResultForVisitCode_shouldSurfaceMalformedRequest() {
			assertTrue(controller.getLabResultForVisitCode("not-json").contains("Error while getting lab report"));
		}
	}
}
