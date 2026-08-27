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
package com.iemr.tm.controller.report;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iemr.tm.data.report.ConsultationReport;
import com.iemr.tm.data.report.ReportInput;
import com.iemr.tm.data.report.SpokeReport;
import com.iemr.tm.data.report.TMDailyReport;
import com.iemr.tm.service.report.CRMReportService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CRMReportController Test Suite")
class CRMReportControllerTest {

	@Mock
	private CRMReportService cRMReportService;

	@InjectMocks
	private CRMReportController controller;

	private final ReportInput input = new ReportInput();

	@Test
	@DisplayName("chiefcomplaintreport should return the chief complaint report")
	void chiefcomplaintreport_shouldReturnReport() throws Exception {
		Set<SpokeReport> report = Collections.singleton(new SpokeReport());
		when(cRMReportService.getChiefcomplaintreport(any(ReportInput.class))).thenReturn(report);

		assertTrue(controller.chiefcomplaintreport(input).contains("\"statusCode\":200"));
	}

	@Test
	@DisplayName("chiefcomplaintreport should surface a service failure")
	void chiefcomplaintreport_shouldSurfaceServiceFailure() throws Exception {
		when(cRMReportService.getChiefcomplaintreport(any(ReportInput.class)))
				.thenThrow(new IllegalStateException("report failed"));

		assertTrue(controller.chiefcomplaintreport(input).contains("report failed"));
	}

	@Test
	@DisplayName("getConsultationReport should return the consultation report with nulls serialised")
	void getConsultationReport_shouldReturnReport() throws Exception {
		List<ConsultationReport> report = Collections.singletonList(new ConsultationReport());
		when(cRMReportService.getConsultationReport(any(ReportInput.class))).thenReturn(report);

		assertTrue(controller.getConsultationReport(input).contains("\"statusCode\":200"));
	}

	@Test
	@DisplayName("getConsultationReport should surface a service failure")
	void getConsultationReport_shouldSurfaceServiceFailure() throws Exception {
		when(cRMReportService.getConsultationReport(any(ReportInput.class)))
				.thenThrow(new IllegalStateException("report failed"));

		assertTrue(controller.getConsultationReport(input).contains("report failed"));
	}

	@Test
	@DisplayName("getTotalConsultationReport should return the total consultation report")
	void getTotalConsultationReport_shouldReturnReport() throws Exception {
		when(cRMReportService.getTotalConsultationReport(any(ReportInput.class))).thenReturn("{\"total\":9}");

		assertTrue(controller.getTotalConsultationReport(input).contains("\"statusCode\":200"));
	}

	@Test
	@DisplayName("getTotalConsultationReport should surface a service failure")
	void getTotalConsultationReport_shouldSurfaceServiceFailure() throws Exception {
		when(cRMReportService.getTotalConsultationReport(any(ReportInput.class)))
				.thenThrow(new IllegalStateException("report failed"));

		assertTrue(controller.getTotalConsultationReport(input).contains("report failed"));
	}

	@Test
	@DisplayName("getMonthlyReport should return the monthly report")
	void getMonthlyReport_shouldReturnReport() throws Exception {
		when(cRMReportService.getMonthlyReport(any(ReportInput.class))).thenReturn("{\"month\":\"Jan\"}");

		assertTrue(controller.getMonthlyReport(input).contains("\"statusCode\":200"));
	}

	@Test
	@DisplayName("getMonthlyReport should surface a service failure")
	void getMonthlyReport_shouldSurfaceServiceFailure() throws Exception {
		when(cRMReportService.getMonthlyReport(any(ReportInput.class)))
				.thenThrow(new IllegalStateException("report failed"));

		assertTrue(controller.getMonthlyReport(input).contains("report failed"));
	}

	@Test
	@DisplayName("getDailyReport should return the daily report with nulls serialised")
	void getDailyReport_shouldReturnReport() throws Exception {
		List<TMDailyReport> report = Collections.singletonList(new TMDailyReport());
		when(cRMReportService.getDailyReport(any(ReportInput.class))).thenReturn(report);

		assertTrue(controller.getDailyReport(input).contains("\"statusCode\":200"));
	}

	@Test
	@DisplayName("getDailyReport should surface a service failure")
	void getDailyReport_shouldSurfaceServiceFailure() throws Exception {
		when(cRMReportService.getDailyReport(any(ReportInput.class)))
				.thenThrow(new IllegalStateException("report failed"));

		assertTrue(controller.getDailyReport(input).contains("report failed"));
	}
}
