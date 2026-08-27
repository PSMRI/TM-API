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

import com.iemr.tm.repo.nurse.anc.ANCCareRepo;
import com.iemr.tm.repo.nurse.anc.ANCWomenVaccineRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.nurse.anc.SysObstetricExaminationRepo;
import com.iemr.tm.repo.quickConsultation.LabTestOrderDetailRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ANCNurseServiceImpl Test Suite")
class ANCNurseServiceImplTest {

	@Mock
	private ANCCareRepo ancCareRepo;
	@Mock
	private ANCWomenVaccineRepo ancWomenVaccineRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;
	@Mock
	private SysObstetricExaminationRepo sysObstetricExaminationRepo;
	@Mock
	private LabTestOrderDetailRepo labTestOrderDetailRepo;

	@InjectMocks
	private ANCNurseServiceImpl service;

	@Test
	@DisplayName("saveBeneficiaryANCDetails should answer for a well formed request")
	void saveBeneficiaryANCDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBeneficiaryANCDetails(new com.iemr.tm.data.anc.ANCCareDetails()));
	}

	@Test
	@DisplayName("saveANCWomenVaccineDetails should answer for a well formed request")
	void saveANCWomenVaccineDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveANCWomenVaccineDetails(new java.util.ArrayList<>()));
	}

	@Test
	@DisplayName("saveBenInvestigationFromDoc should answer for a well formed request")
	void saveBenInvestigationFromDoc_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenInvestigationFromDoc(new com.iemr.tm.data.anc.WrapperBenInvestigationANC()));
	}

	@Test
	@DisplayName("saveBenAncCareDetails should answer for a well formed request")
	void saveBenAncCareDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenAncCareDetails(new com.iemr.tm.data.anc.ANCCareDetails()));
	}

	@Test
	@DisplayName("saveAncImmunizationDetails should answer for a well formed request")
	void saveAncImmunizationDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveAncImmunizationDetails(new com.iemr.tm.data.anc.WrapperAncImmunization()));
	}

	@Test
	@DisplayName("saveSysObstetricExamination should answer for a well formed request")
	void saveSysObstetricExamination_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveSysObstetricExamination(org.mockito.Mockito.mock(com.iemr.tm.data.anc.SysObstetricExamination.class)));
	}

	@Test
	@DisplayName("getSysObstetricExamination should answer for a well formed request")
	void getSysObstetricExamination_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getSysObstetricExamination(11L, 11L));
	}

	@Test
	@DisplayName("getANCCareDetails should answer for a well formed request")
	void getANCCareDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getANCCareDetails(11L, 11L));
	}

	@Test
	@DisplayName("getANCWomenVaccineDetails should answer for a well formed request")
	void getANCWomenVaccineDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getANCWomenVaccineDetails(11L, 11L));
	}

	@Test
	@DisplayName("updateBenAdherenceDetails should answer for a well formed request")
	void updateBenAdherenceDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenAdherenceDetails(new com.iemr.tm.data.anc.BenAdherence()));
	}

	@Test
	@DisplayName("updateBenAncCareDetails should answer for a well formed request")
	void updateBenAncCareDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenAncCareDetails(new com.iemr.tm.data.anc.ANCCareDetails()));
	}

	@Test
	@DisplayName("updateBenAncImmunizationDetails should answer for a well formed request")
	void updateBenAncImmunizationDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenAncImmunizationDetails(new com.iemr.tm.data.anc.WrapperAncImmunization()));
	}

	@Test
	@DisplayName("updateSysObstetricExamination should answer for a well formed request")
	void updateSysObstetricExamination_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateSysObstetricExamination(org.mockito.Mockito.mock(com.iemr.tm.data.anc.SysObstetricExamination.class)));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("ANC care capture")
	class AncCareTests {

		private com.iemr.tm.data.anc.ANCCareDetails careDetails() {
			com.iemr.tm.data.anc.ANCCareDetails details = new com.iemr.tm.data.anc.ANCCareDetails();
			details.setBeneficiaryRegID(11L);
			details.setVisitCode(22L);
			return details;
		}

		@Test
		@DisplayName("saveBeneficiaryANCDetails should return the stored row id")
		void saveAncDetails_shouldReturnStoredId() {
			com.iemr.tm.data.anc.ANCCareDetails stored = careDetails();
			stored.setID(4L);
			org.mockito.Mockito.when(ancCareRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBeneficiaryANCDetails(careDetails()));
		}

		@Test
		@DisplayName("saveBeneficiaryANCDetails should return null when nothing was stored")
		void saveAncDetails_shouldReturnNullWhenNothingStored() {
			org.mockito.Mockito.when(ancCareRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertNull(service.saveBeneficiaryANCDetails(careDetails()));
		}

		@Test
		@DisplayName("saveBenAncCareDetails should store the ANC care details for the visit")
		void saveAncCareDetails_shouldStoreCareDetails() throws Exception {
			com.iemr.tm.data.anc.ANCCareDetails stored = careDetails();
			stored.setID(4L);
			org.mockito.Mockito.when(ancCareRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBenAncCareDetails(careDetails()));
		}

		@Test
		@DisplayName("getANCCareDetails should render the stored ANC care for the visit")
		void getAncCareDetails_shouldRenderStoredCare() {
			org.mockito.Mockito.when(ancCareRepo.getANCCareDetails(11L, 22L)).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getANCCareDetails(11L, 22L));
		}

		@Test
		@DisplayName("getANCWomenVaccineDetails should render the stored vaccine details for the visit")
		void getWomenVaccineDetails_shouldRenderStoredVaccines() {
			org.mockito.Mockito.when(ancWomenVaccineRepo.getANCWomenVaccineDetails(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getANCWomenVaccineDetails(11L, 22L));
		}

		@Test
		@DisplayName("getSysObstetricExamination should delegate to the obstetric examination repository")
		void getObstetricExamination_shouldDelegateToRepo() {
			com.iemr.tm.data.anc.SysObstetricExamination stored = new com.iemr.tm.data.anc.SysObstetricExamination();
			org.mockito.Mockito.when(sysObstetricExaminationRepo.getSysObstetricExaminationData(11L, 22L))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(stored, service.getSysObstetricExamination(11L, 22L));
		}

		@Test
		@DisplayName("saveSysObstetricExamination should return the stored row id")
		void saveObstetricExamination_shouldReturnStoredId() {
			com.iemr.tm.data.anc.SysObstetricExamination stored = new com.iemr.tm.data.anc.SysObstetricExamination();
			stored.setID(4L);
			org.mockito.Mockito.when(sysObstetricExaminationRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveSysObstetricExamination(
					new com.iemr.tm.data.anc.SysObstetricExamination()));
		}

		@Test
		@DisplayName("updateBenAncCareDetails should mark an already processed row as updated")
		void updateAncCareDetails_shouldMarkProcessedAsUpdated() throws Exception {
			org.mockito.Mockito.when(ancCareRepo.getBenANCCareDetailsStatus(11L, 22L)).thenReturn("P");

			org.junit.jupiter.api.Assertions.assertEquals(0, service.updateBenAncCareDetails(careDetails()));
		}

		@Test
		@DisplayName("updateSysObstetricExamination should mark an already processed row as updated")
		void updateObstetricExamination_shouldMarkProcessedAsUpdated() {
			com.iemr.tm.data.anc.SysObstetricExamination examination =
					new com.iemr.tm.data.anc.SysObstetricExamination();
			examination.setBeneficiaryRegID(11L);
			examination.setVisitCode(22L);
			org.mockito.Mockito.when(sysObstetricExaminationRepo.getBenObstetricExaminationStatus(11L, 22L))
					.thenReturn("P");

			org.junit.jupiter.api.Assertions.assertEquals(0, service.updateSysObstetricExamination(examination));
		}

		@Test
		@DisplayName("updateBenAdherenceDetails should mark an already processed row as updated")
		void updateAdherenceDetails_shouldMarkProcessedAsUpdated() {
			com.iemr.tm.data.anc.BenAdherence adherence = new com.iemr.tm.data.anc.BenAdherence();
			adherence.setBeneficiaryRegID(11L);
			adherence.setVisitCode(22L);
			org.mockito.Mockito.when(benAdherenceRepo.getBenAdherenceDetailsStatus(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any())).thenReturn("P");

			org.junit.jupiter.api.Assertions.assertEquals(0, service.updateBenAdherenceDetails(adherence));
		}
	}
}
