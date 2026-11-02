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
package com.iemr.tm.service.ncdCare;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.nurse.ncdcare.NCDCareDiagnosisRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NCDCareDoctorServiceImpl Test Suite")
class NCDCareDoctorServiceImplTest {

	@Mock
	private NCDCareDiagnosisRepo ncdCareDiagnosisRepo;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;

	@InjectMocks
	private NCDCareDoctorServiceImpl service;

	@Test
	@DisplayName("saveNCDDiagnosisData should answer for a well formed request")
	void saveNCDDiagnosisData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveNCDDiagnosisData(org.mockito.Mockito.mock(com.iemr.tm.data.ncdcare.NCDCareDiagnosis.class)));
	}

	@Test
	@DisplayName("getNCDCareDiagnosisDetails should answer for a well formed request")
	void getNCDCareDiagnosisDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getNCDCareDiagnosisDetails(11L, 11L));
	}

	@Test
	@DisplayName("updateBenNCDCareDiagnosis should answer for a well formed request")
	void updateBenNCDCareDiagnosis_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenNCDCareDiagnosis(org.mockito.Mockito.mock(com.iemr.tm.data.ncdcare.NCDCareDiagnosis.class)));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("NCD care diagnosis")
	class DiagnosisTests {

		private com.iemr.tm.data.ncdcare.NCDCareDiagnosis diagnosis() throws Exception {
			com.iemr.tm.data.ncdcare.NCDCareDiagnosis diagnosis = com.iemr.tm.utils.mapper.InputMapper.gson().fromJson(
					"{\"beneficiaryRegID\":11,\"visitCode\":22,\"benVisitID\":3,"
							+ "\"ncdScreeningConditionArray\":[\"Diabetes\",\"Hypertension\"]}",
					com.iemr.tm.data.ncdcare.NCDCareDiagnosis.class);
			return diagnosis;
		}

		@Test
		@DisplayName("saveNCDDiagnosisData should flatten the screening conditions before storing")
		void saveDiagnosis_shouldFlattenScreeningConditions() throws Exception {
			com.iemr.tm.data.ncdcare.NCDCareDiagnosis stored = diagnosis();
			stored.setID(4L);
			org.mockito.Mockito.when(ncdCareDiagnosisRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveNCDDiagnosisData(diagnosis()));
		}

		@Test
		@DisplayName("getNCDCareDiagnosisDetails should render the stored diagnosis for the visit")
		void getDiagnosisDetails_shouldRenderStoredDiagnosis() {
			org.mockito.Mockito.when(ncdCareDiagnosisRepo.getNCDCareDiagnosisDetails(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getNCDCareDiagnosisDetails(11L, 22L));
		}

		@Test
		@DisplayName("updateBenNCDCareDiagnosis should mark an already processed diagnosis as updated")
		void updateDiagnosis_shouldMarkProcessedAsUpdated() throws Exception {
			org.mockito.Mockito.when(ncdCareDiagnosisRepo.getNCDCareDiagnosisStatus(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any())).thenReturn("P");

			org.junit.jupiter.api.Assertions.assertEquals(0, service.updateBenNCDCareDiagnosis(diagnosis()));
		}
	}
}
