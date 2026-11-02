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
package com.iemr.tm.service.cancerScreening;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.doctor.CancerDiagnosisRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CSDoctorServiceImpl Test Suite")
class CSDoctorServiceImplTest {

	@Mock
	private CancerDiagnosisRepo cancerDiagnosisRepo;

	@InjectMocks
	private CSDoctorServiceImpl service;

	@Test
	@DisplayName("saveCancerDiagnosisData should answer for a well formed request")
	void saveCancerDiagnosisData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveCancerDiagnosisData(new com.iemr.tm.data.doctor.CancerDiagnosis()));
	}

	@Test
	@DisplayName("getCancerDiagnosisObj should answer for a well formed request")
	void getCancerDiagnosisObj_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCancerDiagnosisObj(new com.iemr.tm.data.doctor.CancerDiagnosis()));
	}

	@Test
	@DisplayName("getBenDoctorEnteredDataForCaseSheet should answer for a well formed request")
	void getBenDoctorEnteredDataForCaseSheet_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDoctorEnteredDataForCaseSheet(11L, 11L));
	}

	@Test
	@DisplayName("getBenCancerDiagnosisData should answer for a well formed request")
	void getBenCancerDiagnosisData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenCancerDiagnosisData(11L, 11L));
	}

	@Test
	@DisplayName("updateCancerDiagnosisDetailsByDoctor should answer for a well formed request")
	void updateCancerDiagnosisDetailsByDoctor_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateCancerDiagnosisDetailsByDoctor(new com.iemr.tm.data.doctor.CancerDiagnosis()));
	}
}
