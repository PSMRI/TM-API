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
package com.iemr.tm.service.common.transaction;

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

import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.provider.ProviderServiceMappingRepo;
import com.iemr.tm.service.anc.ANCServiceImpl;
import com.iemr.tm.service.cancerScreening.CSNurseServiceImpl;
import com.iemr.tm.service.cancerScreening.CSServiceImpl;
import com.iemr.tm.service.covid19.Covid19ServiceImpl;
import com.iemr.tm.service.generalOPD.GeneralOPDServiceImpl;
import com.iemr.tm.service.ncdCare.NCDCareServiceImpl;
import com.iemr.tm.service.ncdscreening.NCDScreeningServiceImpl;
import com.iemr.tm.service.pnc.PNCServiceImpl;
import com.iemr.tm.service.quickConsultation.QuickConsultationServiceImpl;
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;
import com.iemr.tm.utils.CookieUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CommonServiceImpl Test Suite")
class CommonServiceImplTest {

	@Mock
	private Covid19ServiceImpl covid19ServiceImpl;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private ANCServiceImpl ancServiceImpl;
	@Mock
	private PNCServiceImpl pncServiceImpl;
	@Mock
	private GeneralOPDServiceImpl generalOPDServiceImpl;
	@Mock
	private NCDCareServiceImpl ncdCareServiceImpl;
	@Mock
	private QuickConsultationServiceImpl quickConsultationServiceImpl;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CSNurseServiceImpl cSNurseServiceImpl;
	@Mock
	private CSServiceImpl csServiceImpl;
	@Mock
	private NCDScreeningServiceImpl ncdScreeningServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private ProviderServiceMappingRepo providerServiceMappingRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private CookieUtil cookieUtil;

	@InjectMocks
	private CommonServiceImpl service;

	@Test
	@DisplayName("getCaseSheetPrintDataForBeneficiary should answer for a well formed request")
	void getCaseSheetPrintDataForBeneficiary_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCaseSheetPrintDataForBeneficiary(new com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus(), "{}"));
	}

	@Test
	@DisplayName("getBenPastHistoryData should answer for a well formed request")
	void getBenPastHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPastHistoryData(11L));
	}

	@Test
	@DisplayName("getComorbidHistoryData should answer for a well formed request")
	void getComorbidHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getComorbidHistoryData(11L));
	}

	@Test
	@DisplayName("getMedicationHistoryData should answer for a well formed request")
	void getMedicationHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getMedicationHistoryData(11L));
	}

	@Test
	@DisplayName("getPersonalTobaccoHistoryData should answer for a well formed request")
	void getPersonalTobaccoHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getPersonalTobaccoHistoryData(11L));
	}

	@Test
	@DisplayName("getPersonalAlcoholHistoryData should answer for a well formed request")
	void getPersonalAlcoholHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getPersonalAlcoholHistoryData(11L));
	}

	@Test
	@DisplayName("getPersonalAllergyHistoryData should answer for a well formed request")
	void getPersonalAllergyHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getPersonalAllergyHistoryData(11L));
	}

	@Test
	@DisplayName("getFamilyHistoryData should answer for a well formed request")
	void getFamilyHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getFamilyHistoryData(11L));
	}

	@Test
	@DisplayName("getProviderSpecificData should answer for a well formed request")
	void getProviderSpecificData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getProviderSpecificData("{}"));
	}

	@Test
	@DisplayName("getBenPhysicalHistory should answer for a well formed request")
	void getBenPhysicalHistory_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPhysicalHistory(11L));
	}

	@Test
	@DisplayName("getMenstrualHistoryData should answer for a well formed request")
	void getMenstrualHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getMenstrualHistoryData(11L));
	}

	@Test
	@DisplayName("getObstetricHistoryData should answer for a well formed request")
	void getObstetricHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getObstetricHistoryData(11L));
	}

	@Test
	@DisplayName("getImmunizationHistoryData should answer for a well formed request")
	void getImmunizationHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getImmunizationHistoryData(11L));
	}

	@Test
	@DisplayName("getChildVaccineHistoryData should answer for a well formed request")
	void getChildVaccineHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getChildVaccineHistoryData(11L));
	}

	@Test
	@DisplayName("getBenPerinatalHistoryData should answer for a well formed request")
	void getBenPerinatalHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPerinatalHistoryData(11L));
	}

	@Test
	@DisplayName("getBenFeedingHistoryData should answer for a well formed request")
	void getBenFeedingHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenFeedingHistoryData(11L));
	}

	@Test
	@DisplayName("getBenDevelopmentHistoryData should answer for a well formed request")
	void getBenDevelopmentHistoryData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDevelopmentHistoryData(11L));
	}

	@Test
	@DisplayName("getBenPreviousVisitDataForCaseRecord should answer for a well formed request")
	void getBenPreviousVisitDataForCaseRecord_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPreviousVisitDataForCaseRecord("{}"));
	}

	@Test
	@DisplayName("createTcRequest should answer for a well formed request")
	void createTcRequest_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createTcRequest(new com.google.gson.JsonObject(), org.mockito.Mockito.mock(com.iemr.tm.data.nurse.CommonUtilityClass.class), "{}"));
	}

	@Test
	@DisplayName("getOpenKMDocURL should reject a request it cannot act on")
	void getOpenKMDocURL_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.getOpenKMDocURL("{}", "{}"));
	}

	@Test
	@DisplayName("getBenSymptomaticQuestionnaireDetailsData should answer for a well formed request")
	void getBenSymptomaticQuestionnaireDetailsData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenSymptomaticQuestionnaireDetailsData(11L));
	}

	@Test
	@DisplayName("getBenPreviousDiabetesData should answer for a well formed request")
	void getBenPreviousDiabetesData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPreviousDiabetesData(11L));
	}

	@Test
	@DisplayName("getBenPreviousReferralData should answer for a well formed request")
	void getBenPreviousReferralData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPreviousReferralData(11L));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("case sheet assembly")
	class CaseSheetTests {

		private com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus flow(String visitCategory) {
			com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus flow =
					new com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus();
			flow.setBeneficiaryRegID(11L);
			flow.setBenVisitCode(22L);
			flow.setBenFlowID(5L);
			flow.setVisitCategory(visitCategory);
			return flow;
		}

		@org.junit.jupiter.api.BeforeEach
		void stubBeneficiaryPanel() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getBenDetailsForLeftSidePanel(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong()))
					.thenReturn(new java.util.ArrayList<>());
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the ANC case sheet")
		void getCaseSheet_shouldAssembleAncCaseSheet() throws Exception {
			org.mockito.Mockito.when(ancServiceImpl.getBenANCNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(ancServiceImpl.getBenCaseRecordFromDoctorANC(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("ANC"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the PNC case sheet")
		void getCaseSheet_shouldAssemblePncCaseSheet() throws Exception {
			org.mockito.Mockito.when(pncServiceImpl.getBenPNCNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(pncServiceImpl.getBenCaseRecordFromDoctorPNC(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("PNC"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the General OPD case sheet")
		void getCaseSheet_shouldAssembleGeneralOpdCaseSheet() throws Exception {
			org.mockito.Mockito.when(generalOPDServiceImpl.getBenGeneralOPDNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(generalOPDServiceImpl.getBenCaseRecordFromDoctorGeneralOPD(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("General OPD"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the NCD care case sheet")
		void getCaseSheet_shouldAssembleNcdCareCaseSheet() throws Exception {
			org.mockito.Mockito.when(ncdCareServiceImpl.getBenNCDCareNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(ncdCareServiceImpl.getBenCaseRecordFromDoctorNCDCare(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("NCD care"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the General OPD (QC) case sheet")
		void getCaseSheet_shouldAssembleGeneralOpdQcCaseSheet() throws Exception {
			org.mockito.Mockito.when(quickConsultationServiceImpl.getBenQuickConsultNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(quickConsultationServiceImpl.getBenCaseRecordFromDoctorQuickConsult(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("General OPD (QC)"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the COVID-19 Screening case sheet")
		void getCaseSheet_shouldAssembleCovid19ScreeningCaseSheet() throws Exception {
			org.mockito.Mockito.when(covid19ServiceImpl.getBenCovidNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(covid19ServiceImpl.getBenCaseRecordFromDoctorCovid19(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("COVID-19 Screening"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should assemble the NCD screening case sheet")
		void getCaseSheet_shouldAssembleNcdScreeningCaseSheet() throws Exception {
			org.mockito.Mockito.when(ncdScreeningServiceImpl.getBenNCDScreeningNurseData(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");
			org.mockito.Mockito.when(ncdScreeningServiceImpl.getBenCaseRecordFromDoctorNCDScreening(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("NCD screening"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should answer for an unknown visit category")
		void getCaseSheet_shouldAnswerForUnknownVisitCategory() throws Exception {
			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getCaseSheetPrintDataForBeneficiary(flow("Unknown"), "Bearer session-token"));
		}

		@Test
		@DisplayName("getCaseSheetPrintDataForBeneficiary should answer for a visit without a category")
		void getCaseSheet_shouldAnswerWithoutVisitCategory() throws Exception {
			org.junit.jupiter.api.Assertions.assertDoesNotThrow(
					() -> service.getCaseSheetPrintDataForBeneficiary(flow(null), "Bearer session-token"));
		}
	}
}
