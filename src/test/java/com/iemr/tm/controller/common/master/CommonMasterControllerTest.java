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
package com.iemr.tm.controller.common.master;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iemr.tm.service.common.master.CommonMasterServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommonMasterController Test Suite")
class CommonMasterControllerTest {

	@Mock
	private CommonMasterServiceImpl commonMasterServiceImpl;

	private CommonMasterController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked master data service")
	void setUp() {
		controller = new CommonMasterController();
		controller.setCommonMasterServiceImpl(commonMasterServiceImpl);
	}

	@Test
	@DisplayName("getVisitReasonAndCategories should return the master data produced by the service")
	void getVisitReasonAndCategories_shouldReturnMasterData() {
		when(commonMasterServiceImpl.getVisitReasonAndCategories()).thenReturn("{\"visitCategories\":[]}");

		String result = controller.getVisitReasonAndCategories();

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("visitCategories"));
	}

	@Test
	@DisplayName("NurseMasterData should return the nurse master data for the requested category")
	void nurseMasterData_shouldReturnNurseMasterData() {
		when(commonMasterServiceImpl.getMasterDataForNurse(1, 2, "Female")).thenReturn("{\"nurseMaster\":[]}");

		String result = controller.NurseMasterData(1, 2, "Female");

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("nurseMaster"));
	}

	@Test
	@DisplayName("DoctorMasterData should return the doctor master data for the requested category")
	void doctorMasterData_shouldReturnDoctorMasterData() {
		when(commonMasterServiceImpl.getMasterDataForDoctor(1, 2, "Male", 3, 4)).thenReturn("{\"doctorMaster\":[]}");

		String result = controller.DoctorMasterData(1, 2, "Male", 3, 4);

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("doctorMaster"));
	}

	@Test
	@DisplayName("getECGAbnormalFindings should return the ECG findings master data")
	void getECGAbnormalFindings_shouldReturnEcgFindings() {
		when(commonMasterServiceImpl.getECGAbnormalFindings()).thenReturn("{\"ecgFindings\":[]}");

		String result = controller.getECGAbnormalFindings();

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("ecgFindings"));
	}
}
