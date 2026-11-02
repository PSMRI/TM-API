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
package com.iemr.tm.service.benFlowStatus;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CommonBenStatusFlowServiceImpl Test Suite")
class CommonBenStatusFlowServiceImplTest {

	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;

	@InjectMocks
	private CommonBenStatusFlowServiceImpl service;

	@Test
	@DisplayName("createBenFlowRecord should answer for a well formed request")
	void createBenFlowRecord_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createBenFlowRecord("{}", 11L, 11L));
	}

	@Test
	@DisplayName("updateBenFlowNurseAfterNurseActivity should answer for a well formed request")
	void updateBenFlowNurseAfterNurseActivity_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowNurseAfterNurseActivity(11L, 11L, 11L, "{}", "{}", (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, 11L, 9, (short) 1, new java.sql.Timestamp(1_700_000_000_000L), 9));
	}

	@Test
	@DisplayName("updateBenFlowNurseAfterNurseActivityANC should answer for a well formed request")
	void updateBenFlowNurseAfterNurseActivityANC_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowNurseAfterNurseActivityANC(11L, 11L, 11L, "{}", "{}", (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, 11L, 9, (short) 1, new java.sql.Timestamp(1_700_000_000_000L), 9, (short) 1));
	}

	@Test
	@DisplayName("updateBenFlowNurseAfterNurseUpdateNCD_Screening should answer for a well formed request")
	void updateBenFlowNurseAfterNurseUpdateNCD_Screening_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowNurseAfterNurseUpdateNCD_Screening(11L, 11L, (short) 1));
	}

	@Test
	@DisplayName("updateBenFlowAfterDocData should answer for a well formed request")
	void updateBenFlowAfterDocData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowAfterDocData(11L, 11L, 11L, 11L, (short) 1, (short) 1, (short) 1, (short) 1, 9, new java.sql.Timestamp(1_700_000_000_000L), (short) 1, Boolean.FALSE));
	}

	@Test
	@DisplayName("updateBenFlowAfterDocDataFromSpecialist should answer for a well formed request")
	void updateBenFlowAfterDocDataFromSpecialist_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowAfterDocDataFromSpecialist(11L, 11L, 11L, 11L, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, Boolean.FALSE));
	}

	@Test
	@DisplayName("updateBenFlowAfterDocDataFromSpecialistANC should answer for a well formed request")
	void updateBenFlowAfterDocDataFromSpecialistANC_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowAfterDocDataFromSpecialistANC(11L, 11L, 11L, 11L, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, Boolean.FALSE));
	}

	@Test
	@DisplayName("updateBenFlowAfterDocDataUpdate should answer for a well formed request")
	void updateBenFlowAfterDocDataUpdate_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowAfterDocDataUpdate(11L, 11L, 11L, 11L, (short) 1, (short) 1, (short) 1, (short) 1, 9, new java.sql.Timestamp(1_700_000_000_000L), (short) 1, Boolean.FALSE));
	}

	@Test
	@DisplayName("updateBenFlowAfterDocDataUpdateTCSpecialist should answer for a well formed request")
	void updateBenFlowAfterDocDataUpdateTCSpecialist_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenFlowAfterDocDataUpdateTCSpecialist(11L, 11L, 11L, 11L, (short) 1, (short) 1, (short) 1, (short) 1, 9, new java.sql.Timestamp(1_700_000_000_000L), (short) 1, Boolean.FALSE));
	}

	@Test
	@DisplayName("updateFlowAfterLabResultEntry should answer for a well formed request")
	void updateFlowAfterLabResultEntry_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateFlowAfterLabResultEntry(11L, 11L, 11L, (short) 1, (short) 1, (short) 1));
	}

	@Test
	@DisplayName("updateFlowAfterLabResultEntryForTCSpecialist should answer for a well formed request")
	void updateFlowAfterLabResultEntryForTCSpecialist_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateFlowAfterLabResultEntryForTCSpecialist(11L, 11L, (short) 1));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("beneficiary flow record creation")
	class FlowRecordCreationTests {

		private static final String REGISTRATION = "{\"beneficiaryRegID\":11,\"beneficiaryID\":9,"
				+ "\"providerServiceMapID\":3,\"vanID\":7,\"firstName\":\"Asha\",\"lastName\":\"Devi\","
				+ "\"createdBy\":\"registrar1\",\"genderID\":2,\"genderName\":\"Female\","
				+ "\"dOB\":\"1990-05-14T00:00:00.000Z\","
				+ "\"i_bendemographics\":{\"districtID\":21,\"districtName\":\"Nagpur\","
				+ "\"districtBranchID\":31,\"districtBranchName\":\"Kamptee\","
				+ "\"servicePointID\":41,\"servicePointName\":\"PHC Kamptee\"},"
				+ "\"benPhoneMaps\":[{\"phoneNo\":\"9999999999\"}],"
				+ "\"m_gender\":{\"genderID\":2,\"genderName\":\"Female\"}}";

		@org.junit.jupiter.api.BeforeEach
		void stubVisitCount() {
			org.mockito.Mockito.when(benVisitDetailRepo
					.getVisitCountForBeneficiary(org.mockito.ArgumentMatchers.anyLong())).thenReturn((short) 2);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
		}

		@Test
		@DisplayName("createBenFlowRecord should store the flow record for a registered beneficiary")
		void createFlowRecord_shouldStoreRecordForRegisteredBeneficiary() {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(REGISTRATION, 11L, 9L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus stored = captor.getValue();
			org.junit.jupiter.api.Assertions.assertEquals("Asha Devi", stored.getBenName());
			org.junit.jupiter.api.Assertions.assertEquals(21, stored.getDistrictID());
			org.junit.jupiter.api.Assertions.assertEquals(31, stored.getVillageID());
			org.junit.jupiter.api.Assertions.assertEquals("PHC Kamptee", stored.getServicePointName());
			org.junit.jupiter.api.Assertions.assertEquals("9999999999", stored.getPreferredPhoneNum());
			org.junit.jupiter.api.Assertions.assertEquals((short) 3, stored.getBenVisitNo());
			org.junit.jupiter.api.Assertions.assertEquals((short) 1, stored.getNurseFlag());
			org.junit.jupiter.api.Assertions.assertTrue(stored.getAge().contains("years"));
			org.junit.jupiter.api.Assertions.assertNotNull(stored.getRegistrationDate());
		}

		@Test
		@DisplayName("createBenFlowRecord should report a beneficiary already in the nurse worklist")
		void createFlowRecord_shouldReportBeneficiaryAlreadyInWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "nurseWL", 10);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.checkBenAlreadyInNurseWorkList(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(5L)));

			org.junit.jupiter.api.Assertions.assertEquals(3, service.createBenFlowRecord(REGISTRATION, null, null));
		}

		@Test
		@DisplayName("createBenFlowRecord should store a fresh record when the beneficiary is not in the worklist")
		void createFlowRecord_shouldStoreFreshRecordWhenNotInWorklist() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.checkBenAlreadyInNurseWorkList(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(REGISTRATION, null, null));
		}

		@Test
		@DisplayName("createBenFlowRecord should report a failure when the record could not be stored")
		void createFlowRecord_shouldReportStoreFailure() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(null);

			org.junit.jupiter.api.Assertions.assertEquals(0, service.createBenFlowRecord(REGISTRATION, 11L, 9L));
		}

		@Test
		@DisplayName("createBenFlowRecord should describe a beneficiary aged in months")
		void createFlowRecord_shouldDescribeAgeInMonths() {
			String dob = java.time.LocalDate.now().minusMonths(4).toString() + "T00:00:00.000Z";

			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(
					REGISTRATION.replace("1990-05-14T00:00:00.000Z", dob), 11L, 9L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertTrue(captor.getValue().getAge().contains("months"));
		}

		@Test
		@DisplayName("createBenFlowRecord should describe a beneficiary aged in days")
		void createFlowRecord_shouldDescribeAgeInDays() {
			String dob = java.time.LocalDate.now().minusDays(9).toString() + "T00:00:00.000Z";

			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(
					REGISTRATION.replace("1990-05-14T00:00:00.000Z", dob), 11L, 9L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertTrue(captor.getValue().getAge().contains("days"));
		}

		@Test
		@DisplayName("createBenFlowRecord should use the beneficiary first name alone when there is no surname")
		void createFlowRecord_shouldUseFirstNameAloneWithoutSurname() {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(
					REGISTRATION.replace(",\"lastName\":\"Devi\"", ""), 11L, 9L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertEquals("Asha", captor.getValue().getBenName());
		}

		@Test
		@DisplayName("createBenFlowRecord should take the gender from the master when the request omits it")
		void createFlowRecord_shouldTakeGenderFromMaster() {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(
					REGISTRATION.replace("\"genderID\":2,\"genderName\":\"Female\",", ""), 11L, 9L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertEquals("Female", captor.getValue().getGenderName());
		}

		@Test
		@DisplayName("createBenFlowRecord should count the first visit for a new beneficiary")
		void createFlowRecord_shouldCountFirstVisitForNewBeneficiary() {
			org.mockito.Mockito.when(benVisitDetailRepo
					.getVisitCountForBeneficiary(org.mockito.ArgumentMatchers.anyLong())).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenFlowRecord(REGISTRATION, 11L, 9L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertEquals((short) 1, captor.getValue().getBenVisitNo());
		}

		@Test
		@DisplayName("createBenFlowRecord should absorb a malformed registration request")
		void createFlowRecord_shouldAbsorbMalformedRequest() {
			org.junit.jupiter.api.Assertions.assertEquals(0, service.createBenFlowRecord("{}", 11L, 9L));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("pharmacist flag carry over")
	class PharmacistFlagTests {

		@Test
		@DisplayName("updateBenFlowAfterDocDataUpdate should keep a pharmacist flag already raised on the flow")
		void updateAfterDocUpdate_shouldKeepRaisedPharmacistFlag() throws Exception {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getPharmaFlag(5L)).thenReturn((short) 1);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBenFlowStatusAfterDoctorActivity(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenFlowAfterDocDataUpdate(5L, 11L, 9L, 3L,
					(short) 9, (short) 0, (short) 0, (short) 0, 0, null, (short) 0, Boolean.TRUE));

			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).updateBenFlowStatusAfterDoctorActivity(
					org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.eq(11L),
					org.mockito.ArgumentMatchers.eq(9L), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.eq((short) 1), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyBoolean());
		}

		@Test
		@DisplayName("updateBenFlowAfterDocDataUpdate should raise the pharmacist flag from the request")
		void updateAfterDocUpdate_shouldRaisePharmacistFlagFromRequest() throws Exception {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getPharmaFlag(5L)).thenReturn((short) 0);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBenFlowStatusAfterDoctorActivity(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenFlowAfterDocDataUpdate(5L, 11L, 9L, 3L,
					(short) 9, (short) 1, (short) 0, (short) 0, 0, null, (short) 0, Boolean.TRUE));
		}

		@Test
		@DisplayName("updateBenFlowAfterDocDataUpdate should surface a repository failure")
		void updateAfterDocUpdate_shouldSurfaceRepositoryFailure() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getPharmaFlag(5L))
					.thenThrow(new RuntimeException("flow table locked"));

			org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
					() -> service.updateBenFlowAfterDocDataUpdate(5L, 11L, 9L, 3L, (short) 9, (short) 0, (short) 0,
							(short) 0, 0, null, (short) 0, Boolean.TRUE));
		}

		@Test
		@DisplayName("updateBenFlowAfterDocDataUpdateTCSpecialist should keep a pharmacist flag already raised")
		void updateAfterSpecialistUpdate_shouldKeepRaisedPharmacistFlag() throws Exception {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getPharmaFlag(5L)).thenReturn((short) 1);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBenFlowStatusAfterDoctorActivityTCSpecialist(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyBoolean()))
					.thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBenFlowAfterDocDataUpdateTCSpecialist(5L,
					11L, 9L, 3L, (short) 0, (short) 0, (short) 0, (short) 9, 0, null, (short) 0, Boolean.TRUE));
		}

		@Test
		@DisplayName("updateBenFlowAfterDocDataUpdateTCSpecialist should surface a repository failure")
		void updateAfterSpecialistUpdate_shouldSurfaceRepositoryFailure() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getPharmaFlag(5L))
					.thenThrow(new RuntimeException("flow table locked"));

			org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
					() -> service.updateBenFlowAfterDocDataUpdateTCSpecialist(5L, 11L, 9L, 3L, (short) 0, (short) 0,
							(short) 0, (short) 9, 0, null, (short) 0, Boolean.TRUE));
		}
	}
}
