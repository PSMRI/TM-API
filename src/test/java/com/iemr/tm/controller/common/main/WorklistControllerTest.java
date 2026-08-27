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
package com.iemr.tm.controller.common.main;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;

import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WorklistController Test Suite")
class WorklistControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final String BEN_REQUEST = "{\"benRegID\":11,\"beneficiaryRegID\":11}";

	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private Authentication authentication;

	private WorklistController controller;

	@BeforeEach
	@DisplayName("Wire the controller with mocked services")
	void setUp() {
		controller = new WorklistController();
		controller.setCommonServiceImpl(commonServiceImpl);
		controller.setCommonDoctorServiceImpl(commonDoctorServiceImpl);
		controller.setCommonNurseServiceImpl(commonNurseServiceImpl);
	}

	@Nested
	@DisplayName("getNurseWorkListNew")
	class GetNurseWorkListNewTests {

		@Test
		@DisplayName("getNurseWorkListNew should return the worklist for the provider and van")
		void getNurseWorkListNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getNurseWorkListNew(9, 7)).thenReturn("[]");

			assertTrue(controller.getNurseWorkListNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getNurseWorkListNew should surface a service failure")
		void getNurseWorkListNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getNurseWorkListNew(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getNurseWorkListNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getNurseWorkListTcCurrentDateNew")
	class GetNurseWorkListTcCurrentDateNewTests {

		@Test
		@DisplayName("getNurseWorkListTcCurrentDateNew should return the worklist for the provider and van")
		void getNurseWorkListTcCurrentDateNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getNurseWorkListTcCurrentDate(9, 7)).thenReturn("[]");

			assertTrue(controller.getNurseWorkListTcCurrentDateNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getNurseWorkListTcCurrentDateNew should surface a service failure")
		void getNurseWorkListTcCurrentDateNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getNurseWorkListTcCurrentDate(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getNurseWorkListTcCurrentDateNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getNurseWorkListTcFutureDateNew")
	class GetNurseWorkListTcFutureDateNewTests {

		@Test
		@DisplayName("getNurseWorkListTcFutureDateNew should return the worklist for the provider and van")
		void getNurseWorkListTcFutureDateNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getNurseWorkListTcFutureDate(9, 7)).thenReturn("[]");

			assertTrue(controller.getNurseWorkListTcFutureDateNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getNurseWorkListTcFutureDateNew should surface a service failure")
		void getNurseWorkListTcFutureDateNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getNurseWorkListTcFutureDate(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getNurseWorkListTcFutureDateNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getLabWorkListNew")
	class GetLabWorkListNewTests {

		@Test
		@DisplayName("getLabWorkListNew should return the worklist for the provider and van")
		void getLabWorkListNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getLabWorkListNew(9, 7)).thenReturn("[]");

			assertTrue(controller.getLabWorkListNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getLabWorkListNew should surface a service failure")
		void getLabWorkListNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getLabWorkListNew(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getLabWorkListNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getRadiologistWorklistNew")
	class GetRadiologistWorklistNewTests {

		@Test
		@DisplayName("getRadiologistWorklistNew should return the worklist for the provider and van")
		void getRadiologistWorklistNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getRadiologistWorkListNew(9, 7)).thenReturn("[]");

			assertTrue(controller.getRadiologistWorklistNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getRadiologistWorklistNew should surface a service failure")
		void getRadiologistWorklistNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getRadiologistWorkListNew(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getRadiologistWorklistNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getOncologistWorklistNew")
	class GetOncologistWorklistNewTests {

		@Test
		@DisplayName("getOncologistWorklistNew should return the worklist for the provider and van")
		void getOncologistWorklistNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getOncologistWorkListNew(9, 7)).thenReturn("[]");

			assertTrue(controller.getOncologistWorklistNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getOncologistWorklistNew should surface a service failure")
		void getOncologistWorklistNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getOncologistWorkListNew(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getOncologistWorklistNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getPharmaWorklistNew")
	class GetPharmaWorklistNewTests {

		@Test
		@DisplayName("getPharmaWorklistNew should return the worklist for the provider and van")
		void getPharmaWorklistNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getPharmaWorkListNew(9, 7)).thenReturn("[]");

			assertTrue(controller.getPharmaWorklistNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getPharmaWorklistNew should surface a service failure")
		void getPharmaWorklistNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getPharmaWorkListNew(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getPharmaWorklistNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getMmuNurseWorklistNew")
	class GetMmuNurseWorklistNewTests {

		@Test
		@DisplayName("getMmuNurseWorklistNew should return the worklist for the provider and van")
		void getMmuNurseWorklistNew_shouldReturnWorklist() {
			when(commonNurseServiceImpl.getMmuNurseWorkListNew(9, 7)).thenReturn("[]");

			assertTrue(controller.getMmuNurseWorklistNew(9, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getMmuNurseWorklistNew should surface a service failure")
		void getMmuNurseWorklistNew_shouldSurfaceServiceFailure() {
			when(commonNurseServiceImpl.getMmuNurseWorkListNew(9, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getMmuNurseWorklistNew(9, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getDocWorkListNew")
	class GetDocWorkListNewTests {

		@Test
		@DisplayName("getDocWorkListNew should return the worklist for the provider and service")
		void getDocWorkListNew_shouldReturnWorklist() {
			when(commonDoctorServiceImpl.getDocWorkListNew(9, 2, 7)).thenReturn("[]");

			assertTrue(controller.getDocWorkListNew(9, 2, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getDocWorkListNew should reject a request without a service id")
		void getDocWorkListNew_shouldRejectRequestWithoutServiceId() {
			assertTrue(controller.getDocWorkListNew(9, null, 7).contains("Invalid request"));
		}

		@Test
		@DisplayName("getDocWorkListNew should surface a service failure")
		void getDocWorkListNew_shouldSurfaceServiceFailure() {
			when(commonDoctorServiceImpl.getDocWorkListNew(9, 2, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getDocWorkListNew(9, 2, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getDocWorkListNewFutureScheduledForTM")
	class GetDocWorkListNewFutureScheduledForTMTests {

		@Test
		@DisplayName("getDocWorkListNewFutureScheduledForTM should return the worklist for the provider and service")
		void getDocWorkListNewFutureScheduledForTM_shouldReturnWorklist() {
			when(commonDoctorServiceImpl.getDocWorkListNewFutureScheduledForTM(9, 2, 7)).thenReturn("[]");

			assertTrue(controller.getDocWorkListNewFutureScheduledForTM(9, 2, 7).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getDocWorkListNewFutureScheduledForTM should reject a request without a service id")
		void getDocWorkListNewFutureScheduledForTM_shouldRejectRequestWithoutServiceId() {
			assertTrue(controller.getDocWorkListNewFutureScheduledForTM(9, null, 7).contains("Invalid request"));
		}

		@Test
		@DisplayName("getDocWorkListNewFutureScheduledForTM should surface a service failure")
		void getDocWorkListNewFutureScheduledForTM_shouldSurfaceServiceFailure() {
			when(commonDoctorServiceImpl.getDocWorkListNewFutureScheduledForTM(9, 2, 7)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getDocWorkListNewFutureScheduledForTM(9, 2, 7).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenPastHistory")
	class GetBenPastHistoryTests {

		@Test
		@DisplayName("getBenPastHistory should return the stored history for the beneficiary")
		void getBenPastHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getBenPastHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenPastHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPastHistory should reject a request without the beneficiary id")
		void getBenPastHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenPastHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenPastHistory should surface a service failure")
		void getBenPastHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getBenPastHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenPastHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenTobaccoHistory")
	class GetBenTobaccoHistoryTests {

		@Test
		@DisplayName("getBenTobaccoHistory should return the stored history for the beneficiary")
		void getBenTobaccoHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getPersonalTobaccoHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenTobaccoHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenTobaccoHistory should reject a request without the beneficiary id")
		void getBenTobaccoHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenTobaccoHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenTobaccoHistory should surface a service failure")
		void getBenTobaccoHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getPersonalTobaccoHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenTobaccoHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenAlcoholHistory")
	class GetBenAlcoholHistoryTests {

		@Test
		@DisplayName("getBenAlcoholHistory should return the stored history for the beneficiary")
		void getBenAlcoholHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getPersonalAlcoholHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenAlcoholHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenAlcoholHistory should reject a request without the beneficiary id")
		void getBenAlcoholHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenAlcoholHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenAlcoholHistory should surface a service failure")
		void getBenAlcoholHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getPersonalAlcoholHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenAlcoholHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenANCAllergyHistory")
	class GetBenANCAllergyHistoryTests {

		@Test
		@DisplayName("getBenANCAllergyHistory should return the stored history for the beneficiary")
		void getBenANCAllergyHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getPersonalAllergyHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenANCAllergyHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenANCAllergyHistory should reject a request without the beneficiary id")
		void getBenANCAllergyHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenANCAllergyHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenANCAllergyHistory should surface a service failure")
		void getBenANCAllergyHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getPersonalAllergyHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenANCAllergyHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenMedicationHistory")
	class GetBenMedicationHistoryTests {

		@Test
		@DisplayName("getBenMedicationHistory should return the stored history for the beneficiary")
		void getBenMedicationHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getMedicationHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenMedicationHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenMedicationHistory should reject a request without the beneficiary id")
		void getBenMedicationHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenMedicationHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenMedicationHistory should surface a service failure")
		void getBenMedicationHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getMedicationHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenMedicationHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenFamilyHistory")
	class GetBenFamilyHistoryTests {

		@Test
		@DisplayName("getBenFamilyHistory should return the stored history for the beneficiary")
		void getBenFamilyHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getFamilyHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenFamilyHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenFamilyHistory should reject a request without the beneficiary id")
		void getBenFamilyHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenFamilyHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenFamilyHistory should surface a service failure")
		void getBenFamilyHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getFamilyHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenFamilyHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenMenstrualHistory")
	class GetBenMenstrualHistoryTests {

		@Test
		@DisplayName("getBenMenstrualHistory should return the stored history for the beneficiary")
		void getBenMenstrualHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getMenstrualHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenMenstrualHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenMenstrualHistory should reject a request without the beneficiary id")
		void getBenMenstrualHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenMenstrualHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenMenstrualHistory should surface a service failure")
		void getBenMenstrualHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getMenstrualHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenMenstrualHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenPastObstetricHistory")
	class GetBenPastObstetricHistoryTests {

		@Test
		@DisplayName("getBenPastObstetricHistory should return the stored history for the beneficiary")
		void getBenPastObstetricHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getObstetricHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenPastObstetricHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPastObstetricHistory should reject a request without the beneficiary id")
		void getBenPastObstetricHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenPastObstetricHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenPastObstetricHistory should surface a service failure")
		void getBenPastObstetricHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getObstetricHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenPastObstetricHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenANCComorbidityConditionHistory")
	class GetBenANCComorbidityConditionHistoryTests {

		@Test
		@DisplayName("getBenANCComorbidityConditionHistory should return the stored history for the beneficiary")
		void getBenANCComorbidityConditionHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getComorbidHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenANCComorbidityConditionHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenANCComorbidityConditionHistory should reject a request without the beneficiary id")
		void getBenANCComorbidityConditionHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenANCComorbidityConditionHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenANCComorbidityConditionHistory should surface a service failure")
		void getBenANCComorbidityConditionHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getComorbidHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenANCComorbidityConditionHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenOptionalVaccineHistory")
	class GetBenOptionalVaccineHistoryTests {

		@Test
		@DisplayName("getBenOptionalVaccineHistory should return the stored history for the beneficiary")
		void getBenOptionalVaccineHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getChildVaccineHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenOptionalVaccineHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenOptionalVaccineHistory should reject a request without the beneficiary id")
		void getBenOptionalVaccineHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenOptionalVaccineHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenOptionalVaccineHistory should surface a service failure")
		void getBenOptionalVaccineHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getChildVaccineHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenOptionalVaccineHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenImmunizationHistory")
	class GetBenImmunizationHistoryTests {

		@Test
		@DisplayName("getBenImmunizationHistory should return the stored history for the beneficiary")
		void getBenImmunizationHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getImmunizationHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenImmunizationHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenImmunizationHistory should reject a request without the beneficiary id")
		void getBenImmunizationHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenImmunizationHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenImmunizationHistory should surface a service failure")
		void getBenImmunizationHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getImmunizationHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenImmunizationHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenPerinatalHistory")
	class GetBenPerinatalHistoryTests {

		@Test
		@DisplayName("getBenPerinatalHistory should return the stored history for the beneficiary")
		void getBenPerinatalHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getBenPerinatalHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenPerinatalHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPerinatalHistory should reject a request without the beneficiary id")
		void getBenPerinatalHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenPerinatalHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenPerinatalHistory should surface a service failure")
		void getBenPerinatalHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getBenPerinatalHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenPerinatalHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenFeedingHistory")
	class GetBenFeedingHistoryTests {

		@Test
		@DisplayName("getBenFeedingHistory should return the stored history for the beneficiary")
		void getBenFeedingHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getBenFeedingHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenFeedingHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenFeedingHistory should reject a request without the beneficiary id")
		void getBenFeedingHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenFeedingHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenFeedingHistory should surface a service failure")
		void getBenFeedingHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getBenFeedingHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenFeedingHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenDevelopmentHistory")
	class GetBenDevelopmentHistoryTests {

		@Test
		@DisplayName("getBenDevelopmentHistory should return the stored history for the beneficiary")
		void getBenDevelopmentHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getBenDevelopmentHistoryData(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenDevelopmentHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDevelopmentHistory should reject a request without the beneficiary id")
		void getBenDevelopmentHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenDevelopmentHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenDevelopmentHistory should surface a service failure")
		void getBenDevelopmentHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getBenDevelopmentHistoryData(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenDevelopmentHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBenPhysicalHistory")
	class GetBenPhysicalHistoryTests {

		@Test
		@DisplayName("getBenPhysicalHistory should return the stored history for the beneficiary")
		void getBenPhysicalHistory_shouldReturnStoredHistory() throws Exception {
			when(commonServiceImpl.getBenPhysicalHistory(BEN_REG_ID)).thenReturn("{\"columns\":[]}");

			assertTrue(controller.getBenPhysicalHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPhysicalHistory should reject a request without the beneficiary id")
		void getBenPhysicalHistory_shouldRejectRequestWithoutBeneficiaryId() throws Exception {
			assertTrue(controller.getBenPhysicalHistory("{}").contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenPhysicalHistory should surface a service failure")
		void getBenPhysicalHistory_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getBenPhysicalHistory(BEN_REG_ID)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getBenPhysicalHistory(BEN_REQUEST).contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getDoctorPreviousSignificantFindings")
	class PreviousFindingsTests {

		@Test
		@DisplayName("getDoctorPreviousSignificantFindings should return the earlier findings")
		void getPreviousFindings_shouldReturnEarlierFindings() {
			when(commonDoctorServiceImpl.fetchBenPreviousSignificantFindings(BEN_REG_ID)).thenReturn("{}");

			assertTrue(controller.getDoctorPreviousSignificantFindings("{\"beneficiaryRegID\":11}")
					.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getDoctorPreviousSignificantFindings should reject a request without the beneficiary id")
		void getPreviousFindings_shouldRejectRequestWithoutBeneficiaryId() {
			assertTrue(controller.getDoctorPreviousSignificantFindings("{}").contains("\"statusCode\":5000"));
		}
	}

	@Nested
	@DisplayName("getBeneficiaryCaseSheetHistory")
	class CaseSheetHistoryTests {

		@Test
		@DisplayName("getBeneficiaryCaseSheetHistory should return the earlier visit history")
		void getCaseSheetHistory_shouldReturnEarlierVisitHistory() throws Exception {
			when(commonServiceImpl.getBenPreviousVisitDataForCaseRecord(BEN_REQUEST)).thenReturn("{}");

			assertTrue(controller.getBeneficiaryCaseSheetHistory(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBeneficiaryCaseSheetHistory should report a failure when nothing comes back")
		void getCaseSheetHistory_shouldReportFailureWhenNothingReturned() throws Exception {
			when(commonServiceImpl.getBenPreviousVisitDataForCaseRecord(BEN_REQUEST)).thenReturn(null);

			assertTrue(controller.getBeneficiaryCaseSheetHistory(BEN_REQUEST)
					.contains("Error while fetching beneficiary previous visit history details"));
		}
	}

	@Nested
	@DisplayName("teleconsultation specialist worklists")
	class SpecialistWorklistTests {

		@Test
		@DisplayName("getTCSpecialistWorkListNew should return the specialist worklist for an authenticated user")
		void getSpecialistWorklist_shouldReturnWorklist() {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(commonDoctorServiceImpl.getTCSpecialistWorkListNewForTM(9, 42, 4)).thenReturn("[]");

			assertTrue(controller.getTCSpecialistWorkListNew(9, 4, authentication).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNew should reject a missing authentication")
		void getSpecialistWorklist_shouldRejectMissingAuthentication() {
			assertTrue(controller.getTCSpecialistWorkListNew(9, 4, null).contains("Unauthorized access"));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNewPatientApp should return the patient app worklist")
		void getSpecialistWorklistPatientApp_shouldReturnWorklist() {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(commonDoctorServiceImpl.getTCSpecialistWorkListNewForTMPatientApp(9, 42, 4, 7)).thenReturn("[]");

			assertTrue(controller.getTCSpecialistWorkListNewPatientApp(9, 4, 7, authentication)
					.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getTCSpecialistWorkListNewPatientApp should reject a missing authentication")
		void getSpecialistWorklistPatientApp_shouldRejectMissingAuthentication() {
			assertTrue(controller.getTCSpecialistWorkListNewPatientApp(9, 4, 7, null)
					.contains("Unauthorized access"));
		}

		@Test
		@DisplayName("getTCSpecialistWorklistFutureScheduled should return the future scheduled worklist")
		void getSpecialistWorklistFutureScheduled_shouldReturnWorklist() {
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn("42");
			when(commonDoctorServiceImpl.getTCSpecialistWorkListNewFutureScheduledForTM(9, 42, 4)).thenReturn("[]");

			assertTrue(controller.getTCSpecialistWorklistFutureScheduled(9, 4, authentication)
					.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getTCSpecialistWorklistFutureScheduled should reject a missing authentication")
		void getSpecialistWorklistFutureScheduled_shouldRejectMissingAuthentication() {
			assertTrue(controller.getTCSpecialistWorklistFutureScheduled(9, 4, null).contains("Unauthorized access"));
		}
	}

	@Nested
	@DisplayName("miscellaneous endpoints")
	class MiscellaneousTests {

		@Test
		@DisplayName("getKMFile should return the document url")
		void getKMFile_shouldReturnDocumentUrl() throws Exception {
			when(commonServiceImpl.getOpenKMDocURL(BEN_REQUEST, AUTHORIZATION)).thenReturn("https://km/doc");

			assertTrue(controller.getKMFile(BEN_REQUEST, AUTHORIZATION).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getKMFile should report a failure when no url comes back")
		void getKMFile_shouldReportFailureWithoutUrl() throws Exception {
			when(commonServiceImpl.getOpenKMDocURL(BEN_REQUEST, AUTHORIZATION)).thenReturn(null);

			assertTrue(controller.getKMFile(BEN_REQUEST, AUTHORIZATION).contains("\"statusCode\":5000"));
		}

		@Test
		@DisplayName("getBenSymptomaticQuestionnaireDetails should return the screening questionnaire")
		void getSymptomaticQuestionnaire_shouldReturnQuestionnaire() throws Exception {
			when(commonServiceImpl.getBenSymptomaticQuestionnaireDetailsData(BEN_REG_ID)).thenReturn("{}");

			assertTrue(controller.getBenSymptomaticQuestionnaireDetails(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPreviousDiabetesHistoryDetails should return the earlier diabetes screening")
		void getPreviousDiabetes_shouldReturnEarlierScreening() throws Exception {
			when(commonServiceImpl.getBenPreviousDiabetesData(BEN_REG_ID)).thenReturn("{}");

			assertTrue(controller.getBenPreviousDiabetesHistoryDetails(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenPreviousReferralHistoryDetails should return the earlier referrals")
		void getPreviousReferral_shouldReturnEarlierReferrals() throws Exception {
			when(commonServiceImpl.getBenPreviousReferralData(BEN_REG_ID)).thenReturn("{}");

			assertTrue(controller.getBenPreviousReferralHistoryDetails(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getProviderSpecificData should return the MMU data for the request")
		void getProviderSpecificData_shouldReturnMmuData() throws Exception {
			when(commonServiceImpl.getProviderSpecificData(BEN_REQUEST)).thenReturn("{}");

			assertTrue(controller.getProviderSpecificData(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getProviderSpecificData should surface a service failure")
		void getProviderSpecificData_shouldSurfaceServiceFailure() throws Exception {
			when(commonServiceImpl.getProviderSpecificData(BEN_REQUEST))
					.thenThrow(new IllegalStateException("mmu down"));

			assertTrue(controller.getProviderSpecificData(BEN_REQUEST).contains("mmu down"));
		}

		@Test
		@DisplayName("calculateBMIStatus should return the BMI classification")
		void calculateBMIStatus_shouldReturnClassification() throws Exception {
			when(commonNurseServiceImpl.calculateBMIStatus(BEN_REQUEST)).thenReturn("{\"bmiStatus\":\"Normal\"}");

			assertTrue(controller.calculateBMIStatus(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("calculateBMIStatus should surface a service failure")
		void calculateBMIStatus_shouldSurfaceServiceFailure() throws Exception {
			when(commonNurseServiceImpl.calculateBMIStatus(BEN_REQUEST))
					.thenThrow(new IllegalStateException("bmi failed"));

			assertTrue(controller.calculateBMIStatus(BEN_REQUEST).contains("bmi failed"));
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetail should confirm the submission to the nurse worklist")
		void saveVisitDetail_shouldConfirmSubmission() {
			when(commonNurseServiceImpl.updateBeneficiaryStatus('R', BEN_REG_ID)).thenReturn(1);

			assertTrue(controller.saveBeneficiaryVisitDetail("{\"beneficiaryRegID\":11}")
					.contains("Beneficiary Successfully Submitted to Nurse Work-List."));
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetail should report a failure when the status was not changed")
		void saveVisitDetail_shouldReportFailureWhenStatusUnchanged() {
			when(commonNurseServiceImpl.updateBeneficiaryStatus('R', BEN_REG_ID)).thenReturn(0);

			assertTrue(controller.saveBeneficiaryVisitDetail("{\"beneficiaryRegID\":11}")
					.contains("Something went Wrong"));
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetail should reject a request without the beneficiary id")
		void saveVisitDetail_shouldRejectRequestWithoutBeneficiaryId() {
			assertTrue(controller.saveBeneficiaryVisitDetail("{}")
					.contains("Beneficiary Registration ID is Not valid !!!"));
		}

		@Test
		@DisplayName("extendRedisSession should confirm the extended session")
		void extendRedisSession_shouldConfirmExtendedSession() {
			assertTrue(controller.extendRedisSession().contains("Session extended for 30 mins"));
		}

		@Test
		@DisplayName("deletePrescribedMedicine should confirm the deletion")
		void deletePrescribedMedicine_shouldConfirmDeletion() {
			when(commonDoctorServiceImpl.deletePrescribedMedicine(any(org.json.JSONObject.class)))
					.thenReturn("record deleted successfully");

			assertTrue(controller.deletePrescribedMedicine("{\"id\":4}").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("deletePrescribedMedicine should report a failure when nothing was deleted")
		void deletePrescribedMedicine_shouldReportFailureWhenNothingDeleted() {
			when(commonDoctorServiceImpl.deletePrescribedMedicine(any(org.json.JSONObject.class))).thenReturn(null);

			assertTrue(controller.deletePrescribedMedicine("{\"id\":4}").contains("error while deleting record"));
		}
	}
}
