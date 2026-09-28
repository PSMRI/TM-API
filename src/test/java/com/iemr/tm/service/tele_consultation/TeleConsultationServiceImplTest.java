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
package com.iemr.tm.service.tele_consultation;

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
import com.iemr.tm.repo.tc_consultation.TCRequestModelRepo;
import com.iemr.tm.repo.tc_consultation.TeleconsultationStatsRepo;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.utils.CookieUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TeleConsultationServiceImpl Test Suite")
class TeleConsultationServiceImplTest {

	@Mock
	private TCRequestModelRepo tCRequestModelRepo;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Mock
	private TeleconsultationStatsRepo teleconsultationStatsRepo;
	@Mock
	private CookieUtil cookieUtil;

	@InjectMocks
	private TeleConsultationServiceImpl service;

	@Test
	@DisplayName("createTCRequest should answer for a well formed request")
	void createTCRequest_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createTCRequest(org.mockito.Mockito.mock(com.iemr.tm.data.tele_consultation.TCRequestModel.class)));
	}

	@Test
	@DisplayName("updateBeneficiaryArrivalStatus should reject a request it cannot act on")
	void updateBeneficiaryArrivalStatus_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.updateBeneficiaryArrivalStatus("{}"));
	}

	@Test
	@DisplayName("updateBeneficiaryStatusToCancelTCRequest should reject a request it cannot act on")
	void updateBeneficiaryStatusToCancelTCRequest_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.updateBeneficiaryStatusToCancelTCRequest("{}", "{}"));
	}

	@Test
	@DisplayName("cancelSlotForTCCancel should answer for a well formed request")
	void cancelSlotForTCCancel_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.cancelSlotForTCCancel(9, 11L, 11L, "{}"));
	}

	@Test
	@DisplayName("checkBeneficiaryStatusForSpecialistTransaction should reject a request it cannot act on")
	void checkBeneficiaryStatusForSpecialistTransaction_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.checkBeneficiaryStatusForSpecialistTransaction("{}"));
	}

	@Test
	@DisplayName("createTCRequestFromWorkList should reject a request it cannot act on")
	void createTCRequestFromWorkList_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.createTCRequestFromWorkList(new com.google.gson.JsonObject(), "{}"));
	}

	@Test
	@DisplayName("getTCRequestListBySpecialistIdAndDate should reject a request it cannot act on")
	void getTCRequestListBySpecialistIdAndDate_shouldRejectRequestItCannotActOn() {
		assertThrows(java.time.format.DateTimeParseException.class, () -> service.getTCRequestListBySpecialistIdAndDate(9, 9, "{}"));
	}

	@Test
	@DisplayName("startconsultation should answer for a well formed request")
	void startconsultation_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.startconsultation(11L, 11L));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("teleconsultation session handling")
	class SessionHandlingTests {

		private static final String REQUEST = "{\"benflowID\":5,\"benRegID\":11,\"visitCode\":22,\"userID\":42,"
				+ "\"benArrivedFlag\":true,\"tmRequestID\":8,\"status\":true,\"modifiedBy\":\"nurse1\"}";

		@Test
		@DisplayName("createTCRequest should return the stored request id")
		void createTCRequest_shouldReturnStoredRequestId() {
			com.iemr.tm.data.tele_consultation.TCRequestModel stored =
					new com.iemr.tm.data.tele_consultation.TCRequestModel();
			stored.settMRequestID(8L);
			org.mockito.Mockito.when(tCRequestModelRepo.save(org.mockito.ArgumentMatchers.any())).thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(8L, service.createTCRequest(
					new com.iemr.tm.data.tele_consultation.TCRequestModel()));
		}

		@Test
		@DisplayName("updateBeneficiaryArrivalStatus should confirm the arrival for a complete request")
		void updateArrivalStatus_shouldConfirmArrival() throws Exception {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBeneficiaryArrivalStatus(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(tCRequestModelRepo.updateBeneficiaryStatus(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBeneficiaryArrivalStatus(REQUEST));
		}

		@Test
		@DisplayName("updateBeneficiaryArrivalStatus should fail when the arrival could not be recorded")
		void updateArrivalStatus_shouldFailWhenArrivalNotRecorded() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBeneficiaryArrivalStatus(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateBeneficiaryArrivalStatus(REQUEST));
		}

		@Test
		@DisplayName("checkBeneficiaryStatusForSpecialistTransaction should allow an arrived beneficiary with a session")
		void checkStatus_shouldAllowArrivedBeneficiaryWithSession() throws Exception {
			com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus flow =
					new com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus();
			flow.setBenArrivedFlag(true);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.checkBeneficiaryArrivalStatus(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(flow)));
			com.iemr.tm.data.tele_consultation.TCRequestModel request =
					new com.iemr.tm.data.tele_consultation.TCRequestModel();
			org.mockito.Mockito.when(tCRequestModelRepo.checkBenTcStatus(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(request)));

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.checkBeneficiaryStatusForSpecialistTransaction(REQUEST));
		}

		@Test
		@DisplayName("checkBeneficiaryStatusForSpecialistTransaction should reject a beneficiary who has not arrived")
		void checkStatus_shouldRejectBeneficiaryWhoHasNotArrived() {
			com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus flow =
					new com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus();
			flow.setBenArrivedFlag(false);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.checkBeneficiaryArrivalStatus(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(flow)));

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.checkBeneficiaryStatusForSpecialistTransaction(REQUEST));
		}

		@Test
		@DisplayName("checkBeneficiaryStatusForSpecialistTransaction should reject an unknown visit")
		void checkStatus_shouldRejectUnknownVisit() {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.checkBeneficiaryArrivalStatus(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.checkBeneficiaryStatusForSpecialistTransaction(REQUEST));
		}

		@Test
		@DisplayName("getTCRequestListBySpecialistIdAndDate should render the specialist request list")
		void getRequestList_shouldRenderRequestList() throws Exception {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getTCRequestList(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getTCRequestListBySpecialistIdAndDate(9, 42, "2024-01-15"));
		}

		@Test
		@DisplayName("startconsultation should record the start time and return the updated row count")
		void startConsultation_shouldRecordStartTime() {
			org.mockito.Mockito.when(tCRequestModelRepo.updateStartConsultationTime(11L, 22L)).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.startconsultation(11L, 22L));
			org.mockito.Mockito.verify(teleconsultationStatsRepo).save(org.mockito.ArgumentMatchers.any());
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("teleconsultation cancellation and worklist requests")
	class CancellationTests {

		private static final String CANCEL_REQUEST = "{\"benflowID\":5,\"benRegID\":11,\"visitCode\":22,"
				+ "\"modifiedBy\":\"doctor1\",\"userID\":41}";

		private com.iemr.tm.data.tele_consultation.TCRequestModel storedRequest() {
			com.iemr.tm.data.tele_consultation.TCRequestModel request =
					new com.iemr.tm.data.tele_consultation.TCRequestModel();
			request.setUserID(41);
			request.setSpecializationID(3);
			request.setRequestDate(java.sql.Timestamp.valueOf("2026-08-26 10:00:00"));
			request.setDuration_minute(30L);
			return request;
		}

		private void stubCancelChain(int flowUpdate, int requestUpdate) {
			org.mockito.Mockito.when(tCRequestModelRepo.getSpecializationID(11L, 22L, 41))
					.thenReturn(storedRequest());
			org.mockito.Mockito.when(tCRequestModelRepo.getTcDetailsList(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any())).thenReturn(new java.util.ArrayList<>());
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBeneficiaryStatusToCancelRequest(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyInt())).thenReturn(flowUpdate);
			org.mockito.Mockito.when(tCRequestModelRepo.updateBeneficiaryStatusCancel(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyBoolean()))
					.thenReturn(requestUpdate);
		}

		@Test
		@DisplayName("updateBeneficiaryStatusToCancelTCRequest should cancel the session and text the beneficiary")
		void cancelRequest_shouldCancelSessionAndTextBeneficiary() throws Exception {
			stubCancelChain(1, 1);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.updateBeneficiaryStatusToCancelTCRequest(CANCEL_REQUEST, "Bearer session-token"));

			org.mockito.Mockito.verify(sMSGatewayServiceImpl).smsSenderGateway(
					org.mockito.ArgumentMatchers.eq("cancel"), org.mockito.ArgumentMatchers.eq(11L),
					org.mockito.ArgumentMatchers.eq(3), org.mockito.ArgumentMatchers.isNull(),
					org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.eq("doctor1"),
					org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString());
		}

		@Test
		@DisplayName("updateBeneficiaryStatusToCancelTCRequest should fail when there is no active session")
		void cancelRequest_shouldFailWithoutActiveSession() {
			stubCancelChain(0, 0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateBeneficiaryStatusToCancelTCRequest(CANCEL_REQUEST, "Bearer session-token"));
		}

		@Test
		@DisplayName("cancelSlotForTCCancel should release the booked slot with the scheduler")
		void cancelSlot_shouldReleaseBookedSlot() throws Exception {
			new com.iemr.tm.utils.mapper.OutputMapper();
			org.springframework.test.util.ReflectionTestUtils.setField(service, "tcSpecialistSlotCancel",
					"http://common/tc/specialistSlotCancel");
			org.mockito.Mockito.when(tCRequestModelRepo.getTcDetailsList(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(storedRequest())));

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					org.mockito.Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
							(restTemplate, context) -> org.mockito.Mockito.when(restTemplate.exchange(
									org.mockito.ArgumentMatchers.anyString(),
									org.mockito.ArgumentMatchers.any(org.springframework.http.HttpMethod.class),
									org.mockito.ArgumentMatchers.any(),
									org.mockito.ArgumentMatchers.<Class<String>>any()))
									.thenReturn(new org.springframework.http.ResponseEntity<>(
											"{\"statusCode\":200}", org.springframework.http.HttpStatus.OK)))) {
				org.junit.jupiter.api.Assertions.assertEquals(1,
						service.cancelSlotForTCCancel(41, 11L, 22L, "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("cancelSlotForTCCancel should report a slot the scheduler would not release")
		void cancelSlot_shouldReportUnreleasedSlot() throws Exception {
			new com.iemr.tm.utils.mapper.OutputMapper();
			org.springframework.test.util.ReflectionTestUtils.setField(service, "tcSpecialistSlotCancel",
					"http://common/tc/specialistSlotCancel");
			org.mockito.Mockito.when(tCRequestModelRepo.getTcDetailsList(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(storedRequest())));

			try (org.mockito.MockedConstruction<org.springframework.web.client.RestTemplate> ignored =
					org.mockito.Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
							(restTemplate, context) -> org.mockito.Mockito.when(restTemplate.exchange(
									org.mockito.ArgumentMatchers.anyString(),
									org.mockito.ArgumentMatchers.any(org.springframework.http.HttpMethod.class),
									org.mockito.ArgumentMatchers.any(),
									org.mockito.ArgumentMatchers.<Class<String>>any()))
									.thenReturn(new org.springframework.http.ResponseEntity<>(
											"{\"statusCode\":5000}", org.springframework.http.HttpStatus.OK)))) {
				org.junit.jupiter.api.Assertions.assertEquals(0,
						service.cancelSlotForTCCancel(41, 11L, 22L, "Bearer session-token"));
			}
		}

		@Test
		@DisplayName("cancelSlotForTCCancel should succeed when the beneficiary holds no slot")
		void cancelSlot_shouldSucceedWithoutHeldSlot() throws Exception {
			org.mockito.Mockito.when(tCRequestModelRepo.getTcDetailsList(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt(),
					org.mockito.ArgumentMatchers.any())).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.cancelSlotForTCCancel(41, 11L, 22L, "Bearer session-token"));
		}

		private com.google.gson.JsonObject worklistRequest() {
			return com.google.gson.JsonParser.parseString("{\"benFlowID\":5,\"beneficiaryRegID\":11,"
					+ "\"visitCode\":22,\"createdBy\":\"doctor1\",\"providerServiceMapID\":9}").getAsJsonObject();
		}

		private com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest(boolean walkIn) {
			com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest =
					new com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ();
			tcRequest.setUserID(41);
			tcRequest.setSpecializationID(3);
			tcRequest.setWalkIn(walkIn);
			tcRequest.setAllocationDate(java.sql.Timestamp.valueOf("2026-08-26 10:00:00"));
			return tcRequest;
		}

		@Test
		@DisplayName("createTCRequestFromWorkList should raise the request and text the beneficiary")
		void createRequestFromWorklist_shouldRaiseRequestAndTextBeneficiary() throws Exception {
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(false));
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateFlagAfterTcRequestCreatedFromWorklist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.createTCRequestFromWorkList(worklistRequest(), "Bearer session-token"));

			org.mockito.Mockito.verify(sMSGatewayServiceImpl).smsSenderGateway(
					org.mockito.ArgumentMatchers.eq("schedule"), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.isNull(),
					org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(),
					org.mockito.ArgumentMatchers.anyString());
		}

		@Test
		@DisplayName("createTCRequestFromWorkList should skip the text for a walk in request")
		void createRequestFromWorklist_shouldSkipTextForWalkIn() throws Exception {
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(true));
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateFlagAfterTcRequestCreatedFromWorklist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.createTCRequestFromWorkList(worklistRequest(), "Bearer session-token"));

			org.mockito.Mockito.verifyNoInteractions(sMSGatewayServiceImpl);
		}

		@Test
		@DisplayName("createTCRequestFromWorkList should fail when the flow status could not be updated")
		void createRequestFromWorklist_shouldFailWhenFlowStatusNotUpdated() throws Exception {
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(false));
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateFlagAfterTcRequestCreatedFromWorklist(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.createTCRequestFromWorkList(worklistRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("createTCRequestFromWorkList should fail when no slot could be allocated")
		void createRequestFromWorklist_shouldFailWithoutAllocatedSlot() throws Exception {
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString())).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.createTCRequestFromWorkList(worklistRequest(), "Bearer session-token"));
		}
	}
}
