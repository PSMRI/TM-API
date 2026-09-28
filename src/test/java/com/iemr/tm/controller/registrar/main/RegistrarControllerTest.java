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
package com.iemr.tm.controller.registrar.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

import com.iemr.tm.data.registrar.BeneficiaryData;
import com.iemr.tm.service.common.master.RegistrarServiceMasterDataImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.nurse.NurseServiceImpl;
import com.iemr.tm.service.registrar.RegistrarServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RegistrarController Test Suite")
class RegistrarControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final Long BEN_REG_ID = 11L;
	private static final String BEN_REQUEST = "{\"beneficiaryRegID\":11}";

	@Mock
	private RegistrarServiceImpl registrarServiceImpl;
	@Mock
	private RegistrarServiceMasterDataImpl registrarServiceMasterDataImpl;
	@Mock
	private NurseServiceImpl nurseServiceImpl;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;

	private RegistrarController controller;

	@BeforeEach
	@DisplayName("Wire the controller with mocked registrar services")
	void setUp() {
		controller = new RegistrarController();
		controller.setRegistrarServiceImpl(registrarServiceImpl);
		controller.setRegistrarServiceMasterDataImpl(registrarServiceMasterDataImpl);
		controller.setNurseServiceImpl(nurseServiceImpl);
		org.springframework.test.util.ReflectionTestUtils.setField(controller, "commonNurseServiceImpl",
				commonNurseServiceImpl);
	}

	@Nested
	@DisplayName("search endpoints")
	class SearchTests {

		@Test
		@DisplayName("getRegistrarWorkList should return the worklist for the service point")
		void getRegistrarWorkList_shouldReturnWorklist() throws Exception {
			when(registrarServiceImpl.getRegWorkList(9)).thenReturn("[]");

			assertTrue(controller.getRegistrarWorkList("{\"spID\":9}").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getRegistrarWorkList should surface a malformed request")
		void getRegistrarWorkList_shouldSurfaceMalformedRequest() throws Exception {
			assertTrue(controller.getRegistrarWorkList("{}").contains("\"statusCode\""));
		}

		@Test
		@DisplayName("quickSearchBeneficiary should return the matched beneficiaries")
		void quickSearchBeneficiary_shouldReturnMatches() throws Exception {
			when(registrarServiceImpl.getQuickSearchBenData("BEN1")).thenReturn("[]");

			assertTrue(controller.quickSearchBeneficiary("{\"benID\":\"BEN1\"}").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("advanceSearch should return the matched beneficiaries")
		void advanceSearch_shouldReturnMatches() throws Exception {
			when(registrarServiceImpl.getAdvanceSearchBenData(any())).thenReturn("[]");

			assertTrue(controller.advanceSearch("{\"firstName\":\"Asha\"}").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("quickSearchNew should return the matched beneficiaries")
		void quickSearchNew_shouldReturnMatches() throws Exception {
			when(registrarServiceImpl.beneficiaryQuickSearch(BEN_REQUEST, AUTHORIZATION)).thenReturn("[{\"id\":1}]");

			assertEquals("[{\"id\":1}]", controller.quickSearchNew(BEN_REQUEST, AUTHORIZATION));
		}

		@Test
		@DisplayName("quickSearchNew should reject a null request")
		void quickSearchNew_shouldRejectNullRequest() throws Exception {
			assertTrue(controller.quickSearchNew(null, AUTHORIZATION).contains("Invalid request"));
		}

		@Test
		@DisplayName("quickSearchNew should surface a service failure")
		void quickSearchNew_shouldSurfaceServiceFailure() throws Exception {
			when(registrarServiceImpl.beneficiaryQuickSearch(BEN_REQUEST, AUTHORIZATION))
					.thenThrow(new IllegalStateException("identity down"));

			assertTrue(controller.quickSearchNew(BEN_REQUEST, AUTHORIZATION)
					.contains("Error while searching beneficiary"));
		}

		@Test
		@DisplayName("advanceSearchNew should return the matched beneficiaries")
		void advanceSearchNew_shouldReturnMatches() throws Exception {
			when(registrarServiceImpl.beneficiaryAdvanceSearch(BEN_REQUEST, AUTHORIZATION)).thenReturn("[{\"id\":1}]");

			assertEquals("[{\"id\":1}]", controller.advanceSearchNew(BEN_REQUEST, AUTHORIZATION));
		}

		@Test
		@DisplayName("advanceSearchNew should reject a null request")
		void advanceSearchNew_shouldRejectNullRequest() throws Exception {
			assertTrue(controller.advanceSearchNew(null, AUTHORIZATION).contains("Invalid request"));
		}
	}

	@Nested
	@DisplayName("beneficiary detail reads")
	class DetailReadTests {

		@Test
		@DisplayName("getBenDetailsByRegID should return the stored beneficiary")
		void getBenDetailsByRegID_shouldReturnBeneficiary() throws Exception {
			when(registrarServiceMasterDataImpl.getBenDetailsByRegID(BEN_REG_ID)).thenReturn("{}");

			assertTrue(controller.getBenDetailsByRegID(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDetailsByRegID should reject a non positive registration id")
		void getBenDetailsByRegID_shouldRejectNonPositiveId() throws Exception {
			assertTrue(controller.getBenDetailsByRegID("{\"beneficiaryRegID\":0}")
					.contains("Please pass beneficiaryRegID"));
		}

		@Test
		@DisplayName("getBenDetailsByRegID should reject a request without the registration id")
		void getBenDetailsByRegID_shouldRejectRequestWithoutId() throws Exception {
			assertTrue(controller.getBenDetailsByRegID("{}").contains("Bad Request"));
		}

		@Test
		@DisplayName("getBeneficiaryDetails should return the stored beneficiary")
		void getBeneficiaryDetails_shouldReturnBeneficiary() throws Exception {
			when(registrarServiceImpl.getBeneficiaryDetails(BEN_REG_ID)).thenReturn("{}");

			assertTrue(controller.getBeneficiaryDetails(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBeneficiaryDetails should report no data when the beneficiary is unknown")
		void getBeneficiaryDetails_shouldReportNoDataForUnknownBeneficiary() throws Exception {
			when(registrarServiceImpl.getBeneficiaryDetails(BEN_REG_ID)).thenReturn(null);

			assertTrue(controller.getBeneficiaryDetails(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBeneficiaryDetails should reject a request without the registration id")
		void getBeneficiaryDetails_shouldRejectRequestWithoutId() throws Exception {
			assertTrue(controller.getBeneficiaryDetails("{}").contains("\"statusCode\""));
		}

		@Test
		@DisplayName("getBeneficiaryImage should return the stored image")
		void getBeneficiaryImage_shouldReturnImage() throws Exception {
			when(registrarServiceImpl.getBenImage(BEN_REG_ID)).thenReturn("base64-image");

			assertTrue(controller.getBeneficiaryImage(BEN_REQUEST).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBeneficiaryImage should reject a request without the registration id")
		void getBeneficiaryImage_shouldRejectRequestWithoutId() throws Exception {
			assertTrue(controller.getBeneficiaryImage("{}").contains("Bad Request"));
		}

		@Test
		@DisplayName("getBenDetailsForLeftSidePanelByRegID should return the panel details")
		void getLeftSidePanelDetails_shouldReturnPanelDetails() throws Exception {
			String request = "{\"beneficiaryRegID\":11,\"benFlowID\":5}";
			when(registrarServiceMasterDataImpl.getBenDetailsForLeftSideByRegIDNew(BEN_REG_ID, 5L, AUTHORIZATION,
					request)).thenReturn("{}");

			assertTrue(controller.getBenDetailsForLeftSidePanelByRegID(request, AUTHORIZATION)
					.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getBenDetailsForLeftSidePanelByRegID should reject a request without the registration id")
		void getLeftSidePanelDetails_shouldRejectRequestWithoutId() throws Exception {
			assertTrue(controller.getBenDetailsForLeftSidePanelByRegID("{}", AUTHORIZATION)
					.contains("Invalid request"));
		}

		@Test
		@DisplayName("getBenImage should return the identity image")
		void getBenImage_shouldReturnIdentityImage() throws Exception {
			when(registrarServiceMasterDataImpl.getBenImageFromIdentityAPI(AUTHORIZATION, BEN_REQUEST))
					.thenReturn("base64-image");

			assertEquals("base64-image", controller.getBenImage(BEN_REQUEST, AUTHORIZATION));
		}

		@Test
		@DisplayName("getBenImage should surface a service failure")
		void getBenImage_shouldSurfaceServiceFailure() throws Exception {
			when(registrarServiceMasterDataImpl.getBenImageFromIdentityAPI(AUTHORIZATION, BEN_REQUEST))
					.thenThrow(new IllegalStateException("identity down"));

			assertTrue(controller.getBenImage(BEN_REQUEST, AUTHORIZATION)
					.contains("Error while getting beneficiary image"));
		}

		@Test
		@DisplayName("masterDataForRegistration should return the registration master data")
		void masterDataForRegistration_shouldReturnMasterData() throws Exception {
			when(registrarServiceMasterDataImpl.getRegMasterData()).thenReturn("{}");

			assertTrue(controller.masterDataForRegistration("{\"spID\":9}").contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("masterDataForRegistration should reject an invalid service point")
		void masterDataForRegistration_shouldRejectInvalidServicePoint() throws Exception {
			assertTrue(controller.masterDataForRegistration("{\"spID\":0}").contains("Invalid service point"));
		}

		@Test
		@DisplayName("masterDataForRegistration should reject a request without the service point")
		void masterDataForRegistration_shouldRejectRequestWithoutServicePoint() throws Exception {
			assertTrue(controller.masterDataForRegistration("{}").contains("Invalid request"));
		}
	}

	@Nested
	@DisplayName("registration and update")
	class RegistrationTests {

		private static final String BEN_PAYLOAD = "{\"benD\":{\"firstName\":\"Asha\",\"beneficiaryRegID\":11,"
				+ "\"createdBy\":\"registrar1\"}}";

		@Test
		@DisplayName("createBeneficiary should register the beneficiary and submit them to the nurse worklist")
		void createBeneficiary_shouldRegisterAndSubmitToNurse() throws Exception {
			BeneficiaryData created = new BeneficiaryData();
			created.setBeneficiaryRegID(BEN_REG_ID);
			created.setBeneficiaryID("BEN1");
			when(registrarServiceImpl.createBeneficiary(any())).thenReturn(created);
			when(registrarServiceImpl.createBeneficiaryDemographic(any(), eq(BEN_REG_ID))).thenReturn(1L);
			when(registrarServiceImpl.createBeneficiaryPhoneMapping(any(), eq(BEN_REG_ID))).thenReturn(1L);
			when(registrarServiceImpl.createBenGovIdMapping(any(), eq(BEN_REG_ID))).thenReturn(1);
			when(registrarServiceImpl.createBeneficiaryDemographicAdditional(any(), eq(BEN_REG_ID))).thenReturn(1L);
			when(registrarServiceImpl.createBeneficiaryImage(any(), eq(BEN_REG_ID))).thenReturn(1L);
			when(commonNurseServiceImpl.updateBeneficiaryStatus('R', BEN_REG_ID)).thenReturn(1);

			assertTrue(controller.createBeneficiary(BEN_PAYLOAD, AUTHORIZATION).contains("BEN1"));
		}

		@Test
		@DisplayName("createBeneficiary should report a failure when the registration did not complete")
		void createBeneficiary_shouldReportFailureWhenRegistrationIncomplete() throws Exception {
			BeneficiaryData created = new BeneficiaryData();
			created.setBeneficiaryRegID(BEN_REG_ID);
			when(registrarServiceImpl.createBeneficiary(any())).thenReturn(created);

			assertTrue(controller.createBeneficiary(BEN_PAYLOAD, AUTHORIZATION).contains("Something Went-Wrong"));
		}

		@Test
		@DisplayName("createBeneficiary should reject a payload without beneficiary details")
		void createBeneficiary_shouldRejectPayloadWithoutDetails() throws Exception {
			assertTrue(controller.createBeneficiary("{}", AUTHORIZATION).contains("\"statusCode\""));
		}

		@Test
		@DisplayName("registrarBeneficaryRegistrationNew should return the registration payload")
		void registerBeneficiaryNew_shouldReturnRegistrationPayload() throws Exception {
			when(registrarServiceImpl.registerBeneficiary(BEN_REQUEST, AUTHORIZATION)).thenReturn("{\"benID\":1}");

			assertEquals("{\"benID\":1}", controller.registrarBeneficaryRegistrationNew(BEN_REQUEST, AUTHORIZATION));
		}

		@Test
		@DisplayName("registrarBeneficaryRegistrationNew should surface a registration failure")
		void registerBeneficiaryNew_shouldSurfaceRegistrationFailure() throws Exception {
			when(registrarServiceImpl.registerBeneficiary(BEN_REQUEST, AUTHORIZATION))
					.thenThrow(new IllegalStateException("identity down"));

			assertTrue(controller.registrarBeneficaryRegistrationNew(BEN_REQUEST, AUTHORIZATION)
					.contains("Error in registration"));
		}

		@Test
		@DisplayName("updateBeneficiary should update every beneficiary section")
		void updateBeneficiary_shouldUpdateEverySection() throws Exception {
			when(registrarServiceImpl.updateBeneficiary(any())).thenReturn(1);
			when(registrarServiceImpl.updateBeneficiaryDemographic(any(), eq(BEN_REG_ID))).thenReturn(1);
			when(registrarServiceImpl.updateBeneficiaryPhoneMapping(any(), eq(BEN_REG_ID))).thenReturn(1);
			when(registrarServiceImpl.updateBenGovIdMapping(any(), eq(BEN_REG_ID))).thenReturn(1);
			when(registrarServiceImpl.updateBeneficiaryDemographicAdditional(any(), eq(BEN_REG_ID))).thenReturn(1);
			when(registrarServiceImpl.updateBeneficiaryImage(any(), eq(BEN_REG_ID))).thenReturn(1);
			when(commonNurseServiceImpl.updateBeneficiaryStatus('R', BEN_REG_ID)).thenReturn(1);

			assertTrue(controller.updateBeneficiary(BEN_PAYLOAD).contains("Beneficiary Details updated successfully"));
		}

		@Test
		@DisplayName("updateBeneficiary should report a failure when a section did not change")
		void updateBeneficiary_shouldReportFailureWhenSectionUnchanged() throws Exception {
			when(registrarServiceImpl.updateBeneficiary(any())).thenReturn(0);

			assertTrue(controller.updateBeneficiary(BEN_PAYLOAD).contains("Something Went-Wrong"));
		}

		@Test
		@DisplayName("beneficiaryUpdate should confirm the update")
		void beneficiaryUpdate_shouldConfirmUpdate() throws Exception {
			when(registrarServiceImpl.updateBeneficiary(BEN_REQUEST, AUTHORIZATION)).thenReturn(1);

			assertTrue(controller.beneficiaryUpdate(BEN_REQUEST, AUTHORIZATION)
					.contains("Beneficiary details updated successfully"));
		}

		@Test
		@DisplayName("createReVisitForBenToNurse should move the beneficiary to the nurse worklist")
		void createReVisit_shouldMoveBeneficiaryToNurseWorklist() throws Exception {
			when(registrarServiceImpl.searchAndSubmitBeneficiaryToNurse(BEN_REQUEST)).thenReturn(1);

			assertTrue(controller.createReVisitForBenToNurse(BEN_REQUEST)
					.contains("Beneficiary moved to nurse worklist"));
		}

		@Test
		@DisplayName("createReVisitForBenToNurse should report a beneficiary already on the worklist")
		void createReVisit_shouldReportBeneficiaryAlreadyOnWorklist() throws Exception {
			when(registrarServiceImpl.searchAndSubmitBeneficiaryToNurse(BEN_REQUEST)).thenReturn(2);

			assertTrue(controller.createReVisitForBenToNurse(BEN_REQUEST)
					.contains("Beneficiary already present in nurse worklist"));
		}

		@Test
		@DisplayName("createReVisitForBenToNurse should report a failed move")
		void createReVisit_shouldReportFailedMove() throws Exception {
			when(registrarServiceImpl.searchAndSubmitBeneficiaryToNurse(BEN_REQUEST)).thenReturn(0);

			assertTrue(controller.createReVisitForBenToNurse(BEN_REQUEST)
					.contains("Error while moving beneficiary to nurse worklist"));
		}

		@Test
		@DisplayName("createReVisitForBenToNurse should surface a service failure")
		void createReVisit_shouldSurfaceServiceFailure() throws Exception {
			when(registrarServiceImpl.searchAndSubmitBeneficiaryToNurse(BEN_REQUEST))
					.thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.createReVisitForBenToNurse(BEN_REQUEST)
					.contains("Error while moving beneficiary to nurse worklist"));
		}
	}
}
