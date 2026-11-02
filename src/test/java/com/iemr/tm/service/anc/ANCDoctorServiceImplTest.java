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
package com.iemr.tm.service.anc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.nurse.anc.ANCDiagnosisRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ANCDoctorServiceImpl Test Suite")
class ANCDoctorServiceImplTest {

	@Mock
	private ANCDiagnosisRepo ancDiagnosisRepo;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;

	@InjectMocks
	private ANCDoctorServiceImpl service;

	@Test
	@DisplayName("saveBenANCDiagnosis should answer for a well formed request")
	void saveBenANCDiagnosis_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenANCDiagnosis(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("getANCDiagnosisDetails should answer for a well formed request")
	void getANCDiagnosisDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getANCDiagnosisDetails(11L, 11L));
	}

	@Test
	@DisplayName("updateBenANCDiagnosis should answer for a well formed request")
	void updateBenANCDiagnosis_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenANCDiagnosis(new com.iemr.tm.data.anc.ANCDiagnosis()));
	}
}
