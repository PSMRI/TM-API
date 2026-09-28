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
package com.iemr.tm.service.pnc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.nurse.pnc.PNCDiagnosisRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PNCDoctorServiceImpl Test Suite")
class PNCDoctorServiceImplTest {

	@Mock
	private PNCDiagnosisRepo pncDiagnosisRepo;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;

	@InjectMocks
	private PNCDoctorServiceImpl service;

	@Test
	@DisplayName("saveBenPNCDiagnosis should answer for a well formed request")
	void saveBenPNCDiagnosis_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenPNCDiagnosis(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("getPNCDiagnosisDetails should answer for a well formed request")
	void getPNCDiagnosisDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getPNCDiagnosisDetails(11L, 11L));
	}

	@Test
	@DisplayName("updateBenPNCDiagnosis should answer for a well formed request")
	void updateBenPNCDiagnosis_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenPNCDiagnosis(new com.iemr.tm.data.pnc.PNCDiagnosis()));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("PNC diagnosis")
	class DiagnosisTests {

		@Test
		@DisplayName("saveBenPNCDiagnosis should store the diagnosis against the prescription")
		void saveDiagnosis_shouldStoreAgainstPrescription() throws Exception {
			com.iemr.tm.data.pnc.PNCDiagnosis stored = new com.iemr.tm.data.pnc.PNCDiagnosis();
			stored.setID(4L);
			org.mockito.Mockito.when(pncDiagnosisRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			com.google.gson.JsonObject request = com.google.gson.JsonParser.parseString(
					"{\"beneficiaryRegID\":11,\"visitCode\":22,\"benVisitID\":3,\"providerServiceMapID\":9,"
					+ "\"createdBy\":\"doctor1\",\"pncDiagnosis\":\"Anaemia\"}").getAsJsonObject();

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBenPNCDiagnosis(request, 5L));
		}

		@Test
		@DisplayName("saveBenPNCDiagnosis should report a failure when nothing was stored")
		void saveDiagnosis_shouldReportFailureWhenNothingStored() throws Exception {
			org.mockito.Mockito.when(pncDiagnosisRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertNull(service.saveBenPNCDiagnosis(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11}").getAsJsonObject(), 5L));
		}

		@Test
		@DisplayName("getPNCDiagnosisDetails should render the stored diagnosis for the visit")
		void getDiagnosisDetails_shouldRenderStoredDiagnosis() {
			com.iemr.tm.data.pnc.PNCDiagnosis stored = new com.iemr.tm.data.pnc.PNCDiagnosis();
			stored.setID(4L);
			org.mockito.Mockito.when(pncDiagnosisRepo.findByBeneficiaryRegIDAndVisitCode(11L, 22L))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(stored)));

			org.junit.jupiter.api.Assertions.assertNotNull(service.getPNCDiagnosisDetails(11L, 22L));
		}

		@Test
		@DisplayName("getPNCDiagnosisDetails should render an empty payload when the visit has no diagnosis")
		void getDiagnosisDetails_shouldRenderEmptyPayloadWithoutDiagnosis() {
			org.mockito.Mockito.when(pncDiagnosisRepo.findByBeneficiaryRegIDAndVisitCode(11L, 22L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getPNCDiagnosisDetails(11L, 22L));
		}

		@Test
		@DisplayName("updateBenPNCDiagnosis should mark an already processed diagnosis as updated")
		void updateDiagnosis_shouldMarkProcessedAsUpdated() throws Exception {
			com.iemr.tm.data.pnc.PNCDiagnosis diagnosis = new com.iemr.tm.data.pnc.PNCDiagnosis();
			diagnosis.setBeneficiaryRegID(11L);
			diagnosis.setVisitCode(22L);
			org.mockito.Mockito.when(pncDiagnosisRepo.getPNCDiagnosisStatus(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any())).thenReturn("P");

			org.junit.jupiter.api.Assertions.assertEquals(0, service.updateBenPNCDiagnosis(diagnosis));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("SNOMED diagnosis lists")
	class SnomedDiagnosisTests {

		private static final String TWO_DIAGNOSES = "{\"beneficiaryRegID\":11,\"visitCode\":22,\"benVisitID\":3,"
				+ "\"createdBy\":\"doctor1\","
				+ "\"provisionalDiagnosisList\":[{\"conceptID\":\"111\",\"term\":\"Anaemia\"},"
				+ "                             {\"term\":\"Hypertension\"}],"
				+ "\"confirmatoryDiagnosisList\":[{\"conceptID\":\"222\",\"term\":\"Sepsis\"},"
				+ "                              {\"term\":\"Fever\"}]}";

		@Test
		@DisplayName("saveBenPNCDiagnosis should flatten both diagnosis lists with their concept ids")
		void saveDiagnosis_shouldFlattenBothDiagnosisLists() throws Exception {
			com.iemr.tm.data.pnc.PNCDiagnosis stored = new com.iemr.tm.data.pnc.PNCDiagnosis();
			stored.setID(4L);
			org.mockito.Mockito.when(pncDiagnosisRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBenPNCDiagnosis(
					com.google.gson.JsonParser.parseString(TWO_DIAGNOSES).getAsJsonObject(), 31L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.pnc.PNCDiagnosis> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.pnc.PNCDiagnosis.class);
			org.mockito.Mockito.verify(pncDiagnosisRepo).save(captor.capture());
			com.iemr.tm.data.pnc.PNCDiagnosis saved = captor.getValue();
			org.junit.jupiter.api.Assertions.assertEquals(31L, saved.getPrescriptionID());
			org.junit.jupiter.api.Assertions.assertEquals("Anaemia  ||  Hypertension", saved.getProvisionalDiagnosis());
			org.junit.jupiter.api.Assertions.assertEquals("111  ||  N/A", saved.getProvisionalDiagnosisSCTCode());
			org.junit.jupiter.api.Assertions.assertEquals("Sepsis  ||  Fever", saved.getConfirmatoryDiagnosis());
			org.junit.jupiter.api.Assertions.assertEquals("222  ||  N/A", saved.getConfirmatoryDiagnosisSCTCode());
		}

		@Test
		@DisplayName("getPNCDiagnosisDetails should expand the stored diagnosis back into concept lists")
		void getDiagnosisDetails_shouldExpandStoredDiagnosisIntoLists() {
			com.iemr.tm.data.pnc.PNCDiagnosis stored = new com.iemr.tm.data.pnc.PNCDiagnosis();
			stored.setProvisionalDiagnosis("Anaemia  ||  Hypertension");
			stored.setProvisionalDiagnosisSCTCode("111  ||  N/A");
			stored.setConfirmatoryDiagnosis("Sepsis  ||  Fever");
			stored.setConfirmatoryDiagnosisSCTCode("222  ||  N/A");
			org.mockito.Mockito.when(pncDiagnosisRepo.findByBeneficiaryRegIDAndVisitCode(11L, 22L))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(stored)));
			java.util.ArrayList<Object[]> prescription = new java.util.ArrayList<>();
			prescription.add(new Object[] { "Ultrasound", "review in a week" });
			org.mockito.Mockito.when(prescriptionDetailRepo.getExternalinvestigationForVisitCode(11L, 22L))
					.thenReturn(prescription);

			String result = service.getPNCDiagnosisDetails(11L, 22L);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Anaemia"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Hypertension"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Sepsis"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Ultrasound"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("review in a week"));
		}

		@Test
		@DisplayName("updateBenPNCDiagnosis should flatten both diagnosis lists before updating")
		void updateDiagnosis_shouldFlattenBothDiagnosisLists() throws Exception {
			com.iemr.tm.data.pnc.PNCDiagnosis diagnosis = com.iemr.tm.utils.mapper.InputMapper.gson().fromJson(
					TWO_DIAGNOSES, com.iemr.tm.data.pnc.PNCDiagnosis.class);
			org.mockito.Mockito.when(pncDiagnosisRepo.getPNCDiagnosisStatus(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any())).thenReturn("P");
			org.mockito.Mockito.when(pncDiagnosisRepo.updatePNCDiagnosis(org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.eq("U"), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenPNCDiagnosis(diagnosis));
			org.junit.jupiter.api.Assertions.assertEquals("Anaemia  ||  Hypertension",
					diagnosis.getProvisionalDiagnosis());
		}

		@Test
		@DisplayName("updateBenPNCDiagnosis should store a fresh diagnosis when none was recorded before")
		void updateDiagnosis_shouldStoreFreshDiagnosis() throws Exception {
			com.iemr.tm.data.pnc.PNCDiagnosis diagnosis = com.iemr.tm.utils.mapper.InputMapper.gson().fromJson(
					TWO_DIAGNOSES, com.iemr.tm.data.pnc.PNCDiagnosis.class);
			com.iemr.tm.data.pnc.PNCDiagnosis stored = new com.iemr.tm.data.pnc.PNCDiagnosis();
			stored.setID(4L);
			org.mockito.Mockito.when(pncDiagnosisRepo.getPNCDiagnosisStatus(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any())).thenReturn(null);
			org.mockito.Mockito.when(pncDiagnosisRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenPNCDiagnosis(diagnosis));
		}

		@Test
		@DisplayName("saveBenPNCDiagnosis should store an empty diagnosis when no concept was picked")
		void saveDiagnosis_shouldStoreEmptyDiagnosisWithoutConcepts() throws Exception {
			com.iemr.tm.data.pnc.PNCDiagnosis stored = new com.iemr.tm.data.pnc.PNCDiagnosis();
			stored.setID(4L);
			org.mockito.Mockito.when(pncDiagnosisRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBenPNCDiagnosis(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11,\"visitCode\":22}")
							.getAsJsonObject(), 31L));
		}
	}
}
