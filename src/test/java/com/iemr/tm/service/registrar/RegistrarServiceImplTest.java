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
package com.iemr.tm.service.registrar;

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

import com.iemr.tm.repo.registrar.BeneficiaryDemographicAdditionalRepo;
import com.iemr.tm.repo.registrar.BeneficiaryImageRepo;
import com.iemr.tm.repo.registrar.RegistrarRepoBenData;
import com.iemr.tm.repo.registrar.RegistrarRepoBenDemoData;
import com.iemr.tm.repo.registrar.RegistrarRepoBenGovIdMapping;
import com.iemr.tm.repo.registrar.RegistrarRepoBenPhoneMapData;
import com.iemr.tm.repo.registrar.RegistrarRepoBeneficiaryDetails;
import com.iemr.tm.repo.registrar.ReistrarRepoBenSearch;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.utils.CookieUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RegistrarServiceImpl Test Suite")
class RegistrarServiceImplTest {

	@Mock
	private RegistrarRepoBenData registrarRepoBenData;
	@Mock
	private RegistrarRepoBenDemoData registrarRepoBenDemoData;
	@Mock
	private RegistrarRepoBenPhoneMapData registrarRepoBenPhoneMapData;
	@Mock
	private RegistrarRepoBenGovIdMapping registrarRepoBenGovIdMapping;
	@Mock
	private ReistrarRepoBenSearch reistrarRepoBenSearch;
	@Mock
	private BeneficiaryDemographicAdditionalRepo beneficiaryDemographicAdditionalRepo;
	@Mock
	private RegistrarRepoBeneficiaryDetails registrarRepoBeneficiaryDetails;
	@Mock
	private BeneficiaryImageRepo beneficiaryImageRepo;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private CookieUtil cookieUtil;

	@InjectMocks
	private RegistrarServiceImpl service;

	@Test
	@DisplayName("createBeneficiary should answer for a well formed request")
	void createBeneficiary_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createBeneficiary(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("createBeneficiaryDemographic should answer for a well formed request")
	void createBeneficiaryDemographic_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createBeneficiaryDemographic(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("createBeneficiaryDemographicAdditional should answer for a well formed request")
	void createBeneficiaryDemographicAdditional_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createBeneficiaryDemographicAdditional(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("createBeneficiaryImage should reject a request it cannot act on")
	void createBeneficiaryImage_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.createBeneficiaryImage(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("createBeneficiaryPhoneMapping should answer for a well formed request")
	void createBeneficiaryPhoneMapping_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createBeneficiaryPhoneMapping(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("createBenGovIdMapping should reject a request it cannot act on")
	void createBenGovIdMapping_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.createBenGovIdMapping(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("getRegWorkList should answer for a well formed request")
	void getRegWorkList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getRegWorkList(9));
	}

	@Test
	@DisplayName("getQuickSearchBenData should answer for a well formed request")
	void getQuickSearchBenData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getQuickSearchBenData("{}"));
	}

	@Test
	@DisplayName("getAdvanceSearchBenData should answer for a well formed request")
	void getAdvanceSearchBenData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getAdvanceSearchBenData(new com.iemr.tm.data.registrar.V_BenAdvanceSearch()));
	}

	@Test
	@DisplayName("getBenOBJ should answer for a well formed request")
	void getBenOBJ_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenOBJ(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("getBenDemoOBJ should answer for a well formed request")
	void getBenDemoOBJ_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDemoOBJ(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("getBenPhoneOBJ should answer for a well formed request")
	void getBenPhoneOBJ_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenPhoneOBJ(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("getBeneficiaryDetails should answer for a well formed request")
	void getBeneficiaryDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBeneficiaryDetails(11L));
	}

	@Test
	@DisplayName("getBenImage should answer for a well formed request")
	void getBenImage_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenImage(11L));
	}

	@Test
	@DisplayName("updateBeneficiary should answer for a well formed request")
	void updateBeneficiary_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBeneficiary(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("updateBeneficiaryDemographic should answer for a well formed request")
	void updateBeneficiaryDemographic_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBeneficiaryDemographic(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("updateBeneficiaryPhoneMapping should answer for a well formed request")
	void updateBeneficiaryPhoneMapping_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBeneficiaryPhoneMapping(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("updateBenGovIdMapping should reject a request it cannot act on")
	void updateBenGovIdMapping_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.updateBenGovIdMapping(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("updateBeneficiaryDemographicAdditional should reject a request it cannot act on")
	void updateBeneficiaryDemographicAdditional_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.updateBeneficiaryDemographicAdditional(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("updateBeneficiaryImage should answer for a well formed request")
	void updateBeneficiaryImage_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBeneficiaryImage(new com.google.gson.JsonObject(), 11L));
	}

	@Test
	@DisplayName("getBeneficiaryPersonalDetails should answer for a well formed request")
	void getBeneficiaryPersonalDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBeneficiaryPersonalDetails(11L));
	}

	@Test
	@DisplayName("registerBeneficiary should reject a request it cannot act on")
	void registerBeneficiary_shouldRejectRequestItCannotActOn() {
		assertThrows(IllegalArgumentException.class, () -> service.registerBeneficiary("{}", "{}"));
	}

	@Test
	@DisplayName("updateBeneficiary should reject a request it cannot act on (identity overload)")
	void updateBeneficiary_identityOverload_shouldRejectRequestItCannotActOn() {
		assertThrows(IllegalArgumentException.class, () -> service.updateBeneficiary("{}", "{}"));
	}

	@Test
	@DisplayName("beneficiaryQuickSearch should answer for a well formed request")
	void beneficiaryQuickSearch_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.beneficiaryQuickSearch("{}", "{}"));
	}

	@Test
	@DisplayName("beneficiaryAdvanceSearch should reject a request it cannot act on")
	void beneficiaryAdvanceSearch_shouldRejectRequestItCannotActOn() {
		assertThrows(IllegalArgumentException.class, () -> service.beneficiaryAdvanceSearch("{}", "{}"));
	}

	@Test
	@DisplayName("searchAndSubmitBeneficiaryToNurse should answer for a well formed request")
	void searchAndSubmitBeneficiaryToNurse_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.searchAndSubmitBeneficiaryToNurse("{}"));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("beneficiary payload mapping")
	class PayloadMappingTests {

		/** A registration payload with every optional field the registrar screen can send. */
		private com.google.gson.JsonObject fullPayload() {
			return com.google.gson.JsonParser.parseString("{"
					+ "\"firstName\":\"Asha\",\"lastName\":\"Devi\",\"gender\":2,\"dob\":\"1995-04-12T00:00:00.000\","
					+ "\"maritalStatus\":1,\"createdBy\":\"registrar1\",\"fatherName\":\"Ram\","
					+ "\"husbandName\":\"Shyam\",\"aadharNo\":\"1234\",\"beneficiaryRegID\":11,"
					+ "\"modifiedBy\":\"registrar1\",\"countryID\":1,\"stateID\":2,\"districtID\":3,\"blockID\":4,"
					+ "\"servicePointID\":5,\"villageID\":6,\"community\":7,\"religion\":8,\"income\":9,"
					+ "\"literacyStatus\":\"Literate\",\"educationQualification\":10,\"occupation\":11,"
					+ "\"phoneNo\":\"9999999999\",\"emailID\":\"asha@example.org\",\"bankName\":\"SBI\","
					+ "\"branchName\":\"Main\",\"IFSCCode\":\"SBIN0001\",\"accountNumber\":\"123456\","
					+ "\"habitation\":\"Colony\",\"ageAtMarriage\":21,\"image\":\"base64\","
					+ "\"govID\":[{\"type\":1,\"value\":\"1234\"}]}").getAsJsonObject();
		}

		@Test
		@DisplayName("getBenOBJ should map every captured personal field")
		void getBenOBJ_shouldMapEveryCapturedField() {
			com.iemr.tm.data.registrar.BeneficiaryData result = service.getBenOBJ(fullPayload());

			org.junit.jupiter.api.Assertions.assertEquals("Asha", result.getFirstName());
			org.junit.jupiter.api.Assertions.assertEquals("Devi", result.getLastName());
			org.junit.jupiter.api.Assertions.assertEquals("Ram", result.getFatherName());
			org.junit.jupiter.api.Assertions.assertEquals(11L, result.getBeneficiaryRegID());
		}

		@Test
		@DisplayName("getBenOBJ should map a payload that carries only the mandatory fields")
		void getBenOBJ_shouldMapMinimalPayload() {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenOBJ(
					com.google.gson.JsonParser.parseString("{\"firstName\":\"Asha\"}").getAsJsonObject()));
		}

		@Test
		@DisplayName("getBenDemoOBJ should map every captured demographic field")
		void getBenDemoOBJ_shouldMapEveryCapturedField() {
			com.iemr.tm.data.registrar.BeneficiaryDemographicData result = service.getBenDemoOBJ(fullPayload(), 11L);

			org.junit.jupiter.api.Assertions.assertEquals(11L, result.getBeneficiaryRegID());
			org.junit.jupiter.api.Assertions.assertEquals(2, result.getStateID());
		}

		@Test
		@DisplayName("getBenDemoOBJ should map a payload that carries no demographic field")
		void getBenDemoOBJ_shouldMapEmptyDemographics() {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenDemoOBJ(
					com.google.gson.JsonParser.parseString("{}").getAsJsonObject(), 11L));
		}

		@Test
		@DisplayName("getBenPhoneOBJ should map the captured phone number")
		void getBenPhoneOBJ_shouldMapCapturedPhoneNumber() {
			com.iemr.tm.data.registrar.BeneficiaryPhoneMapping result = service.getBenPhoneOBJ(fullPayload(), 11L);

			org.junit.jupiter.api.Assertions.assertEquals("9999999999", result.getPhoneNo());
		}

		@Test
		@DisplayName("getBenPhoneOBJ should map a payload that carries no phone number")
		void getBenPhoneOBJ_shouldMapEmptyPhoneMapping() {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenPhoneOBJ(
					com.google.gson.JsonParser.parseString("{}").getAsJsonObject(), 11L));
		}

		@Test
		@DisplayName("createBeneficiary should store the mapped beneficiary")
		void createBeneficiary_shouldStoreMappedBeneficiary() {
			com.iemr.tm.data.registrar.BeneficiaryData stored = new com.iemr.tm.data.registrar.BeneficiaryData();
			stored.setBeneficiaryRegID(11L);
			org.mockito.Mockito.when(registrarRepoBenData.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(11L,
					service.createBeneficiary(fullPayload()).getBeneficiaryRegID());
		}

		@Test
		@DisplayName("createBeneficiaryDemographic should store the mapped demographics")
		void createBeneficiaryDemographic_shouldStoreMappedDemographics() {
			com.iemr.tm.data.registrar.BeneficiaryDemographicData stored =
					new com.iemr.tm.data.registrar.BeneficiaryDemographicData();
			stored.setBenDemographicsID(4L);
			org.mockito.Mockito.when(registrarRepoBenDemoData.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L,
					service.createBeneficiaryDemographic(fullPayload(), 11L));
		}

		@Test
		@DisplayName("createBeneficiaryPhoneMapping should store the mapped phone number")
		void createBeneficiaryPhoneMapping_shouldStoreMappedPhoneNumber() {
			com.iemr.tm.data.registrar.BeneficiaryPhoneMapping stored =
					new com.iemr.tm.data.registrar.BeneficiaryPhoneMapping();
			stored.setBenPhMapID(4L);
			org.mockito.Mockito.when(registrarRepoBenPhoneMapData.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L,
					service.createBeneficiaryPhoneMapping(fullPayload(), 11L));
		}

		@Test
		@DisplayName("createBenGovIdMapping should store every captured government id")
		void createBenGovIdMapping_shouldStoreCapturedGovernmentIds() {
			org.mockito.Mockito.when(registrarRepoBenGovIdMapping.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));

			org.junit.jupiter.api.Assertions.assertEquals(1, service.createBenGovIdMapping(fullPayload(), 11L));
		}

		@Test
		@DisplayName("updateBeneficiary should update the mapped beneficiary")
		void updateBeneficiary_shouldUpdateMappedBeneficiary() {
			org.mockito.Mockito.when(registrarRepoBenData.updateBeneficiaryData(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBeneficiary(fullPayload()));
		}

		@Test
		@DisplayName("getRegWorkList should render the registrar worklist")
		void getRegWorkList_shouldRenderWorklist() {
			org.mockito.Mockito.when(registrarRepoBenData.getRegistrarWorkList(9))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getRegWorkList(9));
		}

		@Test
		@DisplayName("getQuickSearchBenData should render the matched beneficiaries")
		void getQuickSearchBenData_shouldRenderMatches() {
			org.mockito.Mockito.when(reistrarRepoBenSearch.getQuickSearch("BEN1"))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getQuickSearchBenData("BEN1"));
		}

		@Test
		@DisplayName("getBeneficiaryDetails should render the stored beneficiary")
		void getBeneficiaryDetails_shouldRenderStoredBeneficiary() {
			org.mockito.Mockito.when(registrarRepoBeneficiaryDetails.getBeneficiaryDetails(11L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNull(service.getBeneficiaryDetails(11L));
		}

		@Test
		@DisplayName("getBeneficiaryPersonalDetails should render the stored personal details")
		void getBeneficiaryPersonalDetails_shouldRenderStoredDetails() {
			org.mockito.Mockito.when(registrarRepoBenDemoData.getBeneficiaryDemographicData(11L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNull(service.getBeneficiaryPersonalDetails(11L));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("common API integration")
	class CommonApiIntegrationTests {

		private static final String REGISTERED = "{\"data\":{\"beneficiaryRegID\":11,\"beneficiaryID\":9}}";

		@org.junit.jupiter.api.BeforeEach
		void stubConfiguredUrls() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "registrationUrl",
					"http://common/registrar/registerBeneficiary");
			org.springframework.test.util.ReflectionTestUtils.setField(service, "beneficiaryEditUrl",
					"http://common/registrar/editBeneficiary");
			org.springframework.test.util.ReflectionTestUtils.setField(service, "registrarQuickSearchByIdUrl",
					"http://common/registrar/quickSearchById");
			org.springframework.test.util.ReflectionTestUtils.setField(service, "registrarQuickSearchByPhoneNoUrl",
					"http://common/registrar/quickSearchByPhoneNo");
			org.springframework.test.util.ReflectionTestUtils.setField(service, "registrarAdvanceSearchUrl",
					"http://common/registrar/advanceSearch");
		}

		/** Stands in for the Common-API call the service makes through its own RestTemplate. */
		private org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> respondWith(
				org.springframework.http.ResponseEntity<String> reply) {
			return org.mockito.Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
					(restTemplate, context) -> org.mockito.Mockito.when(restTemplate.exchange(
							org.mockito.ArgumentMatchers.anyString(),
							org.mockito.ArgumentMatchers.any(org.springframework.http.HttpMethod.class),
							org.mockito.ArgumentMatchers.any(),
							org.mockito.ArgumentMatchers.<Class<String>>any())).thenReturn(reply));
		}

		private org.springframework.http.ResponseEntity<String> ok(String body) {
			return new org.springframework.http.ResponseEntity<>(body, org.springframework.http.HttpStatus.OK);
		}

		@Test
		@DisplayName("registerBeneficiary should create the beneficiary flow record for a van registration")
		void registerBeneficiary_shouldCreateFlowRecord() throws Exception {
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.createBenFlowRecord(
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn(1);

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok(REGISTERED))) {
				String result = service.registerBeneficiary("{\"firstName\":\"Asha\"}", "Bearer session-token");

				org.junit.jupiter.api.Assertions.assertTrue(result.contains("Beneficiary successfully registered"));
				org.junit.jupiter.api.Assertions.assertTrue(result.contains("benGenId"));
			}
		}

		@Test
		@DisplayName("registerBeneficiary should skip the flow record for a mobile registration")
		void registerBeneficiary_shouldSkipFlowRecordForMobile() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok(REGISTERED))) {
				org.junit.jupiter.api.Assertions.assertTrue(service
						.registerBeneficiary("{\"isMobile\":true}", "Bearer session-token")
						.contains("Beneficiary successfully registered"));
			}

			org.mockito.Mockito.verify(commonBenStatusFlowServiceImpl, org.mockito.Mockito.never())
					.createBenFlowRecord(org.mockito.ArgumentMatchers.anyString(),
							org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
		}

		@Test
		@DisplayName("registerBeneficiary should report an error when the flow record could not be created")
		void registerBeneficiary_shouldReportFlowRecordFailure() throws Exception {
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.createBenFlowRecord(
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn(0);

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok(REGISTERED))) {
				org.junit.jupiter.api.Assertions.assertTrue(service
						.registerBeneficiary("{\"firstName\":\"Asha\"}", "Bearer session-token")
						.contains("please contact administrator"));
			}
		}

		@Test
		@DisplayName("registerBeneficiary should answer with an empty response when the common API rejects the request")
		void registerBeneficiary_shouldAnswerEmptyWhenCommonApiRejects() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored = respondWith(
					new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST))) {
				org.junit.jupiter.api.Assertions.assertNotNull(
						service.registerBeneficiary("{\"firstName\":\"Asha\"}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("updateBeneficiary should pass the beneficiary to the nurse when asked")
		void updateBeneficiary_shouldPassToNurse() throws Exception {
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.createBenFlowRecord(
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(3);

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{}"))) {
				org.junit.jupiter.api.Assertions.assertEquals(3, service
						.updateBeneficiary("{\"passToNurse\":true}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("updateBeneficiary should only confirm the edit when the beneficiary stays with the registrar")
		void updateBeneficiary_shouldOnlyConfirmEdit() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{}"))) {
				org.junit.jupiter.api.Assertions.assertEquals(1, service
						.updateBeneficiary("{\"passToNurse\":false}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("updateBeneficiary should answer with nothing when the common API rejects the edit")
		void updateBeneficiary_shouldAnswerNothingWhenRejected() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored = respondWith(
					new org.springframework.http.ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST))) {
				org.junit.jupiter.api.Assertions.assertNull(
						service.updateBeneficiary("{\"passToNurse\":false}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("beneficiaryQuickSearch should search by beneficiary id when one is supplied")
		void quickSearch_shouldSearchByBeneficiaryId() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{\"data\":[]}"))) {
				org.junit.jupiter.api.Assertions.assertEquals("{\"data\":[]}",
						service.beneficiaryQuickSearch("{\"beneficiaryID\":\"9\"}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("beneficiaryQuickSearch should search by ABHA number when one is supplied")
		void quickSearch_shouldSearchByHealthIdNumber() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{\"data\":[]}"))) {
				org.junit.jupiter.api.Assertions.assertNotNull(
						service.beneficiaryQuickSearch("{\"HealthIDNumber\":\"12-34\"}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("beneficiaryQuickSearch should search by phone number when no identifier is supplied")
		void quickSearch_shouldSearchByPhoneNumber() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{\"data\":[]}"))) {
				org.junit.jupiter.api.Assertions.assertNotNull(
						service.beneficiaryQuickSearch("{\"phoneNo\":\"9999999999\"}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("beneficiaryQuickSearch should answer with nothing when the request has no search key")
		void quickSearch_shouldAnswerNothingWithoutSearchKey() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{\"data\":[]}"))) {
				org.junit.jupiter.api.Assertions.assertNull(
						service.beneficiaryQuickSearch("{}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("beneficiaryAdvanceSearch should return the common API response")
		void advanceSearch_shouldReturnCommonApiResponse() throws Exception {
			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					respondWith(ok("{\"data\":[]}"))) {
				org.junit.jupiter.api.Assertions.assertEquals("{\"data\":[]}",
						service.beneficiaryAdvanceSearch("{\"firstName\":\"Asha\"}", "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("searchAndSubmitBeneficiaryToNurse should create the beneficiary flow record")
		void searchAndSubmit_shouldCreateFlowRecord() throws Exception {
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.createBenFlowRecord(
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.searchAndSubmitBeneficiaryToNurse("{\"beneficiaryRegID\":11}"));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("beneficiary record maintenance")
	class BeneficiaryRecordTests {

		private static final String DEMOGRAPHIC_ADDITIONAL = "{\"literacyStatus\":\"Literate\","
				+ "\"motherName\":\"Sita\",\"emailID\":\"asha@example.org\",\"bankName\":\"SBI\","
				+ "\"branchName\":\"Kamptee\",\"IFSCCode\":\"SBIN0001\",\"accountNumber\":\"12345\","
				+ "\"createdBy\":\"registrar1\",\"modifiedBy\":\"registrar1\",\"benDemoAdditionalID\":4,"
				+ "\"ageAtMarriage\":22,\"age\":31}";

		private com.google.gson.JsonObject json(String raw) {
			return com.google.gson.JsonParser.parseString(raw).getAsJsonObject();
		}

		@Test
		@DisplayName("createBeneficiaryDemographicAdditional should map every additional detail before storing")
		void createDemographicAdditional_shouldMapEveryDetail() {
			com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional stored =
					new com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional();
			stored.setBenDemoAdditionalID(4L);
			org.mockito.Mockito.when(beneficiaryDemographicAdditionalRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L,
					service.createBeneficiaryDemographicAdditional(json(DEMOGRAPHIC_ADDITIONAL), 11L));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional> captor =
					org.mockito.ArgumentCaptor.forClass(
							com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional.class);
			org.mockito.Mockito.verify(beneficiaryDemographicAdditionalRepo).save(captor.capture());
			com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional saved = captor.getValue();
			org.junit.jupiter.api.Assertions.assertEquals("Literate", saved.getLiteracyStatus());
			org.junit.jupiter.api.Assertions.assertEquals("Sita", saved.getMotherName());
			org.junit.jupiter.api.Assertions.assertEquals("asha@example.org", saved.getEmailID());
			org.junit.jupiter.api.Assertions.assertEquals("SBI", saved.getBankName());
			org.junit.jupiter.api.Assertions.assertEquals("Kamptee", saved.getBranchName());
			org.junit.jupiter.api.Assertions.assertEquals("SBIN0001", saved.getiFSCCode());
			org.junit.jupiter.api.Assertions.assertEquals("12345", saved.getAccountNo());
			org.junit.jupiter.api.Assertions.assertNotNull(saved.getMarrigeDate());
		}

		@Test
		@DisplayName("createBeneficiaryImage should store the captured photograph")
		void createBeneficiaryImage_shouldStoreCapturedPhotograph() {
			com.iemr.tm.data.registrar.BeneficiaryImage stored =
					new com.iemr.tm.data.registrar.BeneficiaryImage();
			stored.setBeneficiaryRegID(11L);
			org.mockito.Mockito.when(beneficiaryImageRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(11L, service.createBeneficiaryImage(
					json("{\"image\":\"base64-photo\",\"createdBy\":\"registrar1\"}"), 11L));
		}

		@Test
		@DisplayName("createBeneficiaryImage should report a photograph it could not store")
		void createBeneficiaryImage_shouldReportUnstoredPhotograph() {
			org.mockito.Mockito.when(beneficiaryImageRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(null);

			org.junit.jupiter.api.Assertions.assertNull(service.createBeneficiaryImage(
					json("{\"image\":\"base64-photo\",\"createdBy\":\"registrar1\"}"), 11L));
		}

		@Test
		@DisplayName("updateBeneficiaryDemographicAdditional should update the stored additional details")
		void updateDemographicAdditional_shouldUpdateStoredDetails() {
			org.mockito.Mockito.when(beneficiaryDemographicAdditionalRepo
					.getBeneficiaryDemographicAdditional(11L))
					.thenReturn(new com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional());
			org.mockito.Mockito.when(beneficiaryDemographicAdditionalRepo
					.updateBeneficiaryDemographicAdditional(org.mockito.ArgumentMatchers.any(),
							org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
							org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
							org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
							org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.updateBeneficiaryDemographicAdditional(json(DEMOGRAPHIC_ADDITIONAL), 11L));
		}

		@Test
		@DisplayName("updateBeneficiaryDemographicAdditional should store the details when none were recorded before")
		void updateDemographicAdditional_shouldStoreWhenNoneRecorded() {
			com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional stored =
					new com.iemr.tm.data.registrar.BeneficiaryDemographicAdditional();
			stored.setBenDemoAdditionalID(4L);
			org.mockito.Mockito.when(beneficiaryDemographicAdditionalRepo
					.getBeneficiaryDemographicAdditional(11L)).thenReturn(null);
			org.mockito.Mockito.when(beneficiaryDemographicAdditionalRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.updateBeneficiaryDemographicAdditional(json(DEMOGRAPHIC_ADDITIONAL), 11L));
		}

		@Test
		@DisplayName("updateBeneficiaryImage should update the stored photograph")
		void updateBeneficiaryImage_shouldUpdateStoredPhotograph() {
			org.mockito.Mockito.when(beneficiaryImageRepo.findBenImage(11L)).thenReturn(11L);
			org.mockito.Mockito.when(beneficiaryImageRepo.updateBeneficiaryImage(
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyLong())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBeneficiaryImage(
					json("{\"image\":\"base64-photo\",\"createdBy\":\"registrar1\",\"benImageID\":4,"
							+ "\"modifiedBy\":\"registrar1\"}"), 11L));
		}

		@Test
		@DisplayName("updateBeneficiaryImage should store the photograph when none was recorded before")
		void updateBeneficiaryImage_shouldStoreWhenNoneRecorded() {
			com.iemr.tm.data.registrar.BeneficiaryImage stored =
					new com.iemr.tm.data.registrar.BeneficiaryImage();
			stored.setBenImageID(4L);
			org.mockito.Mockito.when(beneficiaryImageRepo.findBenImage(11L)).thenReturn(null);
			org.mockito.Mockito.when(beneficiaryImageRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBeneficiaryImage(
					json("{\"image\":\"base64-photo\",\"createdBy\":\"registrar1\"}"), 11L));
		}

		@Test
		@DisplayName("updateBeneficiaryImage should succeed when the request carries no photograph")
		void updateBeneficiaryImage_shouldSucceedWithoutPhotograph() {
			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.updateBeneficiaryImage(json("{\"createdBy\":\"registrar1\"}"), 11L));
		}

		/** One beneficiary details row as the details view returns it. */
		private Object[] detailsRow(Boolean isGovType) {
			Object[] values = new Object[35];
			values[0] = 11L;
			for (int i = 1; i < 35; i++) {
				values[i] = "value";
			}
			values[4] = (short) 2;
			values[5] = java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(31));
			values[6] = (short) 1;
			values[8] = (short) 1;
			values[9] = (short) 1;
			values[10] = (short) 1;
			values[11] = 21;
			values[13] = 31;
			values[15] = 41;
			values[16] = (short) 1;
			values[19] = 51;
			values[21] = 61;
			values[24] = (short) 1;
			values[26] = isGovType;
			values[27] = java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(9));
			return values;
		}

		@Test
		@DisplayName("getBeneficiaryDetails should split the government and other identity documents")
		void getBeneficiaryDetails_shouldSplitIdentityDocuments() {
			org.mockito.Mockito.when(registrarRepoBeneficiaryDetails.getBeneficiaryDetails(11L))
					.thenReturn(new java.util.ArrayList<>(java.util.Arrays.asList(
							detailsRow(Boolean.TRUE), detailsRow(Boolean.FALSE), detailsRow(null))));
			org.mockito.Mockito.when(beneficiaryImageRepo.getBenImage(11L)).thenReturn("base64-photo");

			String result = service.getBeneficiaryDetails(11L);

			org.junit.jupiter.api.Assertions.assertNotNull(result);
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("base64-photo"));
		}

		@Test
		@DisplayName("getBeneficiaryDetails should answer with nothing for an unknown beneficiary")
		void getBeneficiaryDetails_shouldAnswerWithNothingForUnknownBeneficiary() {
			org.mockito.Mockito.when(registrarRepoBeneficiaryDetails.getBeneficiaryDetails(11L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNull(service.getBeneficiaryDetails(11L));
		}

		@org.junit.jupiter.params.ParameterizedTest
		@org.junit.jupiter.params.provider.CsvSource({ "1,Male", "2,Female", "3,Transgender" })
		@DisplayName("getBeneficiaryPersonalDetails should name the gender recorded against the beneficiary")
		void getBeneficiaryPersonalDetails_shouldNameGender(short genderID, String genderName) {
			java.util.List<Object[]> benRows = new java.util.ArrayList<>();
			benRows.add(new Object[] { 11L, "Asha", "Devi",
					java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(31)), genderID,
					new java.sql.Timestamp(System.currentTimeMillis()) });
			org.mockito.Mockito.when(registrarRepoBenData.getBenDetailsByRegID(11L)).thenReturn(benRows);
			java.util.List<Object[]> demoRows = new java.util.ArrayList<>();
			demoRows.add(new Object[] { 11L, 41, "PHC Kamptee" });
			org.mockito.Mockito.when(registrarRepoBenDemoData.getBeneficiaryDemographicData(11L))
					.thenReturn(demoRows);

			com.iemr.tm.data.registrar.BeneficiaryData details = service.getBeneficiaryPersonalDetails(11L);

			org.junit.jupiter.api.Assertions.assertEquals(genderName, details.getGenderName());
			org.junit.jupiter.api.Assertions.assertEquals("PHC Kamptee", details.getServicePointName());
		}

		@Test
		@DisplayName("getBeneficiaryPersonalDetails should answer with nothing for an unknown beneficiary")
		void getBeneficiaryPersonalDetails_shouldAnswerWithNothingForUnknownBeneficiary() {
			org.mockito.Mockito.when(registrarRepoBenData.getBenDetailsByRegID(11L))
					.thenReturn(new java.util.ArrayList<>());
			org.mockito.Mockito.when(registrarRepoBenDemoData.getBeneficiaryDemographicData(11L))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNull(service.getBeneficiaryPersonalDetails(11L));
		}

		@Test
		@DisplayName("getQuickSearchBenData should render the matched beneficiaries")
		void getQuickSearchBenData_shouldRenderMatchedBeneficiaries() {
			org.mockito.Mockito.when(reistrarRepoBenSearch.getQuickSearch("7"))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getQuickSearchBenData("7"));
		}

		@Test
		@DisplayName("getAdvanceSearchBenData should search with every supplied criterion")
		void getAdvanceSearchBenData_shouldSearchWithEveryCriterion() {
			com.iemr.tm.data.registrar.V_BenAdvanceSearch search =
					new com.iemr.tm.data.registrar.V_BenAdvanceSearch();
			search.setBeneficiaryID("7");
			search.setFirstName("Asha");
			search.setLastName("Devi");
			search.setFatherName("Ram");
			search.setPhoneNo("9999999999");
			search.setAadharNo("1234");
			search.setGovtIdentityNo("PAN-1");
			search.setStateID(21);
			search.setDistrictID(31);
			org.mockito.Mockito.when(reistrarRepoBenSearch.getAdvanceBenSearchList("7", "Asha", "Devi",
					"9999999999", "1234", "PAN-1", "21", "31")).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getAdvanceSearchBenData(search));
		}

		@Test
		@DisplayName("getAdvanceSearchBenData should search with wildcards when no criterion was supplied")
		void getAdvanceSearchBenData_shouldSearchWithWildcards() {
			org.mockito.Mockito.when(reistrarRepoBenSearch.getAdvanceBenSearchList(
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getAdvanceSearchBenData(
					new com.iemr.tm.data.registrar.V_BenAdvanceSearch()));
		}
	}
}
