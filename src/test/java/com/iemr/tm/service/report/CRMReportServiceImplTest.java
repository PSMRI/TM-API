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
package com.iemr.tm.service.report;

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

import com.iemr.tm.repo.login.UserParkingplaceMappingRepo;
import com.iemr.tm.repo.report.BenChiefComplaintReportRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CRMReportServiceImpl Test Suite")
class CRMReportServiceImplTest {

	@Mock
	private BenChiefComplaintReportRepo benChiefComplaintReportRepo;
	@Mock
	private UserParkingplaceMappingRepo userParkingplaceMappingRepo;

	@InjectMocks
	private CRMReportServiceImpl service;

	@Test
	@DisplayName("getParkingplaceID should reject a request that carries no beneficiary details")
	void getParkingplaceID_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(Exception.class, () -> service.getParkingplaceID(9, 9));
	}

	@Test
	@DisplayName("calculateTime should answer for a well formed request")
	void calculateTime_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.calculateTime(new java.sql.Timestamp(1_700_000_000_000L), new java.sql.Timestamp(1_700_000_000_000L)));
	}

	@Test
	@DisplayName("getChiefcomplaintreport should reject a request that carries no beneficiary details")
	void getChiefcomplaintreport_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(Exception.class, () -> service.getChiefcomplaintreport(org.mockito.Mockito.mock(com.iemr.tm.data.report.ReportInput.class)));
	}

	@Test
	@DisplayName("getConsultationReport should reject a request that carries no beneficiary details")
	void getConsultationReport_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(Exception.class, () -> service.getConsultationReport(org.mockito.Mockito.mock(com.iemr.tm.data.report.ReportInput.class)));
	}

	@Test
	@DisplayName("getTotalConsultationReport should reject a request that carries no beneficiary details")
	void getTotalConsultationReport_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(Exception.class, () -> service.getTotalConsultationReport(org.mockito.Mockito.mock(com.iemr.tm.data.report.ReportInput.class)));
	}

	@Test
	@DisplayName("getMonthlyReport should reject a request that carries no beneficiary details")
	void getMonthlyReport_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(Exception.class, () -> service.getMonthlyReport(org.mockito.Mockito.mock(com.iemr.tm.data.report.ReportInput.class)));
	}

	@Test
	@DisplayName("getDailyReport should reject a request that carries no beneficiary details")
	void getDailyReport_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(Exception.class, () -> service.getDailyReport(org.mockito.Mockito.mock(com.iemr.tm.data.report.ReportInput.class)));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("reports for a mapped user")
	class MappedUserReportTests {

		private com.iemr.tm.data.report.ReportInput input() {
			com.iemr.tm.data.report.ReportInput input = new com.iemr.tm.data.report.ReportInput();
			input.setUserID(42);
			input.setProviderServiceMapID(9);
			input.setVanID(7);
			input.setFromDate(new java.sql.Date(1_700_000_000_000L));
			input.setToDate(new java.sql.Date(1_700_600_000_000L));
			return input;
		}

		@org.junit.jupiter.api.BeforeEach
		void mapUserToParkingPlace() {
			com.iemr.tm.data.login.UserParkingplaceMapping mapping =
					new com.iemr.tm.data.login.UserParkingplaceMapping();
			mapping.setParkingPlaceID(3);
			org.mockito.Mockito.when(userParkingplaceMappingRepo
					.findOneByUserIDAndProviderServiceMapIdAndDeleted(42, 9, 0)).thenReturn(mapping);
		}

		@Test
		@DisplayName("getParkingplaceID should return the parking place the user is mapped to")
		void getParkingplaceID_shouldReturnMappedParkingPlace() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(3, service.getParkingplaceID(42, 9));
		}

		@Test
		@DisplayName("getChiefcomplaintreport should group the chief complaints by spoke")
		void getChiefcomplaintreport_shouldGroupBySpoke() throws Exception {
			java.util.List<Object[]> rows = new java.util.ArrayList<>();
			rows.add(new Object[] { 1, "Fever", 7, "Van 7", 0, 0, 5L, 2L, 3L, 0L });
			org.mockito.Mockito.when(benChiefComplaintReportRepo.getcmreport(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(3))).thenReturn(rows);

			org.junit.jupiter.api.Assertions.assertFalse(service.getChiefcomplaintreport(input()).isEmpty());
		}

		@Test
		@DisplayName("getChiefcomplaintreport should return nothing when no complaint was recorded")
		void getChiefcomplaintreport_shouldReturnNothingWithoutComplaints() throws Exception {
			org.mockito.Mockito.when(benChiefComplaintReportRepo.getcmreport(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(3)))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertTrue(service.getChiefcomplaintreport(input()).isEmpty());
		}

		@Test
		@DisplayName("getConsultationReport should return the consultations for the window")
		void getConsultationReport_shouldReturnConsultations() throws Exception {
			org.mockito.Mockito.when(benChiefComplaintReportRepo.getConsultationReport(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.eq(3))).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getConsultationReport(input()));
		}

		@Test
		@DisplayName("getTotalConsultationReport should return the totals for the window")
		void getTotalConsultationReport_shouldReturnTotals() throws Exception {
			org.mockito.Mockito.when(benChiefComplaintReportRepo.getTotalConsultationReport(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.eq(3))).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getTotalConsultationReport(input()));
		}

		@Test
		@DisplayName("getMonthlyReport should return a column per month in the window")
		void getMonthlyReport_shouldReturnColumnPerMonth() throws Exception {
			java.util.List<Object[]> rows = new java.util.ArrayList<>();
			rows.add(new Object[] { "Total Consultations", "Nov-23", 4 });
			org.mockito.Mockito.when(benChiefComplaintReportRepo.getMonthlyReport(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(3),
					org.mockito.ArgumentMatchers.any())).thenReturn(rows);

			org.junit.jupiter.api.Assertions.assertNotNull(service.getMonthlyReport(input()));
		}

		@Test
		@DisplayName("getDailyReport should return the visits for the day")
		void getDailyReport_shouldReturnVisitsForTheDay() throws Exception {
			org.mockito.Mockito.when(benChiefComplaintReportRepo.getDailyReport(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.eq(3))).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getDailyReport(input()));
		}
	}
}
