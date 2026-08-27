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
package com.iemr.tm.service.foetalmonitor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.foetalmonitor.FoetalMonitorDeviceIDRepo;
import com.iemr.tm.repo.foetalmonitor.FoetalMonitorRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FoetalMonitorServiceImpl Test Suite")
class FoetalMonitorServiceImplTest {

	@Mock
	private FoetalMonitorRepo foetalMonitorRepo;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private FoetalMonitorDeviceIDRepo foetalMonitorDeviceIDRepo;

	@InjectMocks
	private FoetalMonitorServiceImpl service;

	@Test
	@DisplayName("updateFoetalMonitorData should reject a request it cannot act on")
	void updateFoetalMonitorData_shouldRejectRequestItCannotActOn() {
		assertThrows(com.iemr.tm.utils.exception.IEMRException.class, () -> service.updateFoetalMonitorData(new com.iemr.tm.data.foetalmonitor.FoetalMonitor()));
	}

	@Test
	@DisplayName("readPDFANDGetBase64 should reject a request it cannot act on")
	void readPDFANDGetBase64_shouldRejectRequestItCannotActOn() {
		assertThrows(IllegalArgumentException.class, () -> service.readPDFANDGetBase64("{}"));
	}

	@Test
	@DisplayName("sendFoetalMonitorTestDetails should reject a request it cannot act on")
	void sendFoetalMonitorTestDetails_shouldRejectRequestItCannotActOn() {
		assertThrows(Exception.class, () -> service.sendFoetalMonitorTestDetails(new com.iemr.tm.data.foetalmonitor.FoetalMonitor(), "{}"));
	}

	@Test
	@DisplayName("getFoetalMonitorDetails should answer for a well formed request")
	void getFoetalMonitorDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getFoetalMonitorDetails(11L));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("foetal monitor test results")
	class TestResultTests {

		private com.iemr.tm.data.foetalmonitor.FoetalMonitor deviceResult() {
			com.iemr.tm.data.foetalmonitor.FoetalMonitor result = new com.iemr.tm.data.foetalmonitor.FoetalMonitor();
			result.setFoetalMonitorID(4L);
			result.setAccelerationsList(new java.util.ArrayList<>());
			result.setDecelerationsList(new java.util.ArrayList<>());
			result.setMovementEntries(new java.util.ArrayList<>());
			result.setAutoFetalMovement(new java.util.ArrayList<>());
			java.util.Map<String, String> mother = new java.util.HashMap<>();
			mother.put("cmMotherId", "M1");
			mother.put("partnerId", "P1");
			mother.put("partnerName", "Partner");
			result.setMother(mother);
			return result;
		}

		@Test
		@DisplayName("updateFoetalMonitorData should reject a result for an unknown device test")
		void updateResult_shouldRejectUnknownDeviceTest() {
			org.mockito.Mockito.when(foetalMonitorRepo.getFoetalMonitorDetails(4L)).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(com.iemr.tm.utils.exception.IEMRException.class,
					() -> service.updateFoetalMonitorData(deviceResult()));
		}

		@Test
		@DisplayName("updateFoetalMonitorData should fail when the report file cannot be written")
		void updateResult_shouldFailWhenReportCannotBeWritten() throws Exception {
			com.iemr.tm.data.foetalmonitor.FoetalMonitor stored = new com.iemr.tm.data.foetalmonitor.FoetalMonitor();
			stored.setFoetalMonitorID(4L);
			stored.setBeneficiaryID(7L);
			stored.setBeneficiaryRegID(11L);
			stored.setVisitCode(22L);
			stored.setBenFlowID(5L);
			org.mockito.Mockito.when(foetalMonitorRepo.getFoetalMonitorDetails(4L)).thenReturn(stored);
			org.mockito.Mockito.when(foetalMonitorRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertThrows(com.iemr.tm.utils.exception.IEMRException.class,
					() -> service.updateFoetalMonitorData(deviceResult()));
		}

		@Test
		@DisplayName("getFoetalMonitorDetails should render the tests recorded against the flow")
		void getDetails_shouldRenderTestsForFlow() throws Exception {
			com.iemr.tm.data.foetalmonitor.FoetalMonitor stored = new com.iemr.tm.data.foetalmonitor.FoetalMonitor();
			stored.setFoetalMonitorID(4L);
			stored.setBeneficiaryRegID(11L);
			stored.setBenFlowID(5L);
			stored.setVisitCode(22L);
			stored.setResultState(true);
			org.mockito.Mockito.when(foetalMonitorRepo.getFoetalMonitorDetailsByFlowId(5L))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(stored)));

			org.junit.jupiter.api.Assertions.assertTrue(
					service.getFoetalMonitorDetails(5L).contains("benFetosenseData"));
		}

		@Test
		@DisplayName("getFoetalMonitorDetails should render an empty list when the flow has no test")
		void getDetails_shouldRenderEmptyListWithoutTests() throws Exception {
			org.mockito.Mockito.when(foetalMonitorRepo.getFoetalMonitorDetailsByFlowId(5L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertTrue(
					service.getFoetalMonitorDetails(5L).contains("benFetosenseData"));
		}

		@Test
		@DisplayName("readPDFANDGetBase64 should reject a path that does not exist")
		void readReport_shouldRejectMissingPath() {
			org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
					() -> service.readPDFANDGetBase64("/no/such/report.pdf"));
		}
	}
}
