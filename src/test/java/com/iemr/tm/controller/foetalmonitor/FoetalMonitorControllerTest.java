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
package com.iemr.tm.controller.foetalmonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.iemr.tm.data.foetalmonitor.FoetalMonitor;
import com.iemr.tm.service.foetalmonitor.FoetalMonitorService;
import com.iemr.tm.utils.exception.IEMRException;

@ExtendWith(MockitoExtension.class)
@DisplayName("FoetalMonitorController Test Suite")
class FoetalMonitorControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String TEST_REQUEST = "{\"beneficiaryRegID\":11,\"motherName\":\"Asha\"}";

	@Mock
	private FoetalMonitorService foetalMonitorService;

	@InjectMocks
	private FoetalMonitorController controller;

	@Nested
	@DisplayName("sendANCMotherTestDetailsToFoetalMonitor")
	class SendTestDetailsTests {

		@Test
		@DisplayName("sendANCMotherTestDetailsToFoetalMonitor should return 200 with the device response")
		void sendTestDetails_shouldReturnDeviceResponse() throws Exception {
			when(foetalMonitorService.sendFoetalMonitorTestDetails(any(FoetalMonitor.class), eq(AUTHORIZATION)))
					.thenReturn("{\"fetosenseTestId\":9}");

			ResponseEntity<String> result = controller.sendANCMotherTestDetailsToFoetalMonitor(TEST_REQUEST,
					AUTHORIZATION);

			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody().contains("fetosenseTestId"));
		}

		@Test
		@DisplayName("sendANCMotherTestDetailsToFoetalMonitor should reject a null request")
		void sendTestDetails_shouldRejectNullRequest() throws Exception {
			ResponseEntity<String> result = controller.sendANCMotherTestDetailsToFoetalMonitor(null, AUTHORIZATION);

			assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
			assertTrue(result.getBody().contains("Invalid request"));
			verify(foetalMonitorService, never()).sendFoetalMonitorTestDetails(any(FoetalMonitor.class), anyString());
		}

		@Test
		@DisplayName("sendANCMotherTestDetailsToFoetalMonitor should surface a service failure")
		void sendTestDetails_shouldSurfaceServiceFailure() throws Exception {
			when(foetalMonitorService.sendFoetalMonitorTestDetails(any(FoetalMonitor.class), eq(AUTHORIZATION)))
					.thenThrow(new IllegalStateException("device unreachable"));

			ResponseEntity<String> result = controller.sendANCMotherTestDetailsToFoetalMonitor(TEST_REQUEST,
					AUTHORIZATION);

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
			assertTrue(result.getBody().contains("device unreachable"));
		}
	}

	@Test
	@DisplayName("saveMother should acknowledge that the device test is in progress")
	void saveMother_shouldAcknowledgeTestInProgress() {
		assertTrue(controller.saveMother(TEST_REQUEST, AUTHORIZATION).contains("Test in progress"));
	}

	@Nested
	@DisplayName("getFoetalMonitorDetails")
	class GetDetailsTests {

		@Test
		@DisplayName("getFoetalMonitorDetails should return the monitor details for the flow")
		void getFoetalMonitorDetails_shouldReturnDetails() throws Exception {
			when(foetalMonitorService.getFoetalMonitorDetails(11L)).thenReturn("{\"benFlowID\":11}");

			assertTrue(controller.getFoetalMonitorDetails(11L).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getFoetalMonitorDetails should report a failure when nothing comes back")
		void getFoetalMonitorDetails_shouldReportFailureWhenNothingFound() throws Exception {
			when(foetalMonitorService.getFoetalMonitorDetails(11L)).thenReturn(null);

			assertTrue(controller.getFoetalMonitorDetails(11L).contains("Error in fetching the details"));
		}

		@Test
		@DisplayName("getFoetalMonitorDetails should surface a service failure")
		void getFoetalMonitorDetails_shouldSurfaceServiceFailure() throws Exception {
			when(foetalMonitorService.getFoetalMonitorDetails(11L)).thenThrow(new IEMRException("db down"));

			assertTrue(controller.getFoetalMonitorDetails(11L).contains("db down"));
		}
	}

	@Nested
	@DisplayName("getFoetalMonitorDetails report graph")
	class ReportGraphTests {

		private FoetalMonitor request() {
			FoetalMonitor request = new FoetalMonitor();
			request.setaMRITFilePath("/reports/test.pdf");
			return request;
		}

		@Test
		@DisplayName("the report endpoint should return the base64 encoded report")
		void reportGraph_shouldReturnBase64Report() throws Exception {
			when(foetalMonitorService.readPDFANDGetBase64("/reports/test.pdf")).thenReturn("base64-report");

			ResponseEntity<String> result = controller.getFoetalMonitorDetails(request());

			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody().contains("base64-report"));
		}

		@Test
		@DisplayName("the report endpoint should report a failure when no report comes back")
		void reportGraph_shouldReportFailureWhenNoReport() throws Exception {
			when(foetalMonitorService.readPDFANDGetBase64("/reports/test.pdf")).thenReturn(null);

			assertTrue(controller.getFoetalMonitorDetails(request()).getBody()
					.contains("Error in fetching the details"));
		}

		@Test
		@DisplayName("the report endpoint should report a missing report file")
		void reportGraph_shouldReportMissingFile() throws Exception {
			when(foetalMonitorService.readPDFANDGetBase64("/reports/test.pdf"))
					.thenThrow(new FileNotFoundException("test.pdf"));

			assertTrue(controller.getFoetalMonitorDetails(request()).getBody().contains("File not found"));
		}

		@Test
		@DisplayName("the report endpoint should report a read failure")
		void reportGraph_shouldReportReadFailure() throws Exception {
			when(foetalMonitorService.readPDFANDGetBase64("/reports/test.pdf")).thenThrow(new IOException("disk error"));

			assertTrue(controller.getFoetalMonitorDetails(request()).getBody().contains("File not found"));
		}

		@Test
		@DisplayName("the report endpoint should surface a service failure")
		void reportGraph_shouldSurfaceServiceFailure() throws Exception {
			when(foetalMonitorService.readPDFANDGetBase64("/reports/test.pdf")).thenThrow(new IEMRException("db down"));

			assertTrue(controller.getFoetalMonitorDetails(request()).getBody().contains("db down"));
		}
	}

	@Nested
	@DisplayName("updateFoetalMonitorData")
	class UpdateTests {

		@Test
		@DisplayName("updateFoetalMonitorData should confirm the update when a row was changed")
		void updateFoetalMonitorData_shouldConfirmUpdate() throws Exception {
			when(foetalMonitorService.updateFoetalMonitorData(any(FoetalMonitor.class))).thenReturn(1);

			ResponseEntity<String> result = controller.updateFoetalMonitorData(TEST_REQUEST, AUTHORIZATION);

			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody().contains("Data updated successfully"));
		}

		@Test
		@DisplayName("updateFoetalMonitorData should leave the generic failure when no row was changed")
		void updateFoetalMonitorData_shouldLeaveGenericFailureWhenNothingChanged() throws Exception {
			when(foetalMonitorService.updateFoetalMonitorData(any(FoetalMonitor.class))).thenReturn(0);

			ResponseEntity<String> result = controller.updateFoetalMonitorData(TEST_REQUEST, AUTHORIZATION);

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}

		@Test
		@DisplayName("updateFoetalMonitorData should reject a null request")
		void updateFoetalMonitorData_shouldRejectNullRequest() throws Exception {
			ResponseEntity<String> result = controller.updateFoetalMonitorData(null, AUTHORIZATION);

			assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
			assertTrue(result.getBody().contains("Invalid request"));
		}

		@Test
		@DisplayName("updateFoetalMonitorData should surface a service failure")
		void updateFoetalMonitorData_shouldSurfaceServiceFailure() throws Exception {
			when(foetalMonitorService.updateFoetalMonitorData(any(FoetalMonitor.class)))
					.thenThrow(new IEMRException("update failed"));

			assertTrue(controller.updateFoetalMonitorData(TEST_REQUEST, AUTHORIZATION).getBody()
					.contains("update failed"));
		}
	}
}
