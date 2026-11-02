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
package com.iemr.tm.service.patientApp.master;

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
import com.iemr.tm.repo.doctor.ChiefComplaintMasterRepo;
import com.iemr.tm.repo.masterrepo.covid19.CovidContactHistoryMasterRepo;
import com.iemr.tm.repo.masterrepo.covid19.CovidRecommnedationMasterRepo;
import com.iemr.tm.repo.masterrepo.covid19.CovidSymptomsMasterRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.quickBlox.QuickBloxRepo;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.covid19.Covid19ServiceImpl;
import com.iemr.tm.service.generalOPD.GeneralOPDDoctorServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CommonPatientAppMasterServiceImpl Test Suite")
class CommonPatientAppMasterServiceImplTest {

	@Mock
	private CovidSymptomsMasterRepo covidSymptomsMasterRepo;
	@Mock
	private CovidContactHistoryMasterRepo covidContactHistoryMasterRepo;
	@Mock
	private CovidRecommnedationMasterRepo covidRecommnedationMasterRepo;
	@Mock
	private ChiefComplaintMasterRepo chiefComplaintMasterRepo;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private Covid19ServiceImpl covid19ServiceImpl;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private GeneralOPDDoctorServiceImpl generalOPDDoctorServiceImpl;
	@Mock
	private QuickBloxRepo quickBloxRepo;

	@InjectMocks
	private CommonPatientAppMasterServiceImpl service;

	@Test
	@DisplayName("getChiefComplaintsMaster should answer for a well formed request")
	void getChiefComplaintsMaster_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getChiefComplaintsMaster(9, 9, "{}"));
	}

	@Test
	@DisplayName("getCovidMaster should answer for a well formed request")
	void getCovidMaster_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCovidMaster(9, 9, "{}"));
	}

	@Test
	@DisplayName("saveCovidScreeningData should reject a request that carries no beneficiary details")
	void saveCovidScreeningData_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.saveCovidScreeningData("{}"));
	}

	@Test
	@DisplayName("savechiefComplaintsData should reject a request that carries no beneficiary details")
	void savechiefComplaintsData_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.savechiefComplaintsData("{}"));
	}

	@Test
	@DisplayName("bookTCSlotData should reject a request that carries no beneficiary details")
	void bookTCSlotData_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.bookTCSlotData("{}", "{}"));
	}

	@Test
	@DisplayName("getPatientEpisodeData should reject a request that carries no beneficiary details")
	void getPatientEpisodeData_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.getPatientEpisodeData("{}"));
	}

	@Test
	@DisplayName("getPatientBookedSlots should reject a request that carries no beneficiary details")
	void getPatientBookedSlots_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.getPatientBookedSlots("{}"));
	}

	@Test
	@DisplayName("saveSpecialistDiagnosisData should reject a request that carries no beneficiary details")
	void saveSpecialistDiagnosisData_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.saveSpecialistDiagnosisData("{}"));
	}

	@Test
	@DisplayName("getSpecialistDiagnosisData should reject a request that carries no beneficiary details")
	void getSpecialistDiagnosisData_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.getSpecialistDiagnosisData("{}"));
	}

	@Test
	@DisplayName("getPatientsLast_3_Episode should reject a request that carries no beneficiary details")
	void getPatientsLast_3_Episode_shouldRejectRequestWithoutBeneficiaryDetails() {
		assertThrows(RuntimeException.class, () -> service.getPatientsLast_3_Episode("{}"));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("patient app episodes and diagnosis")
	class PatientAppEpisodeTests {

		private static final String BEN_REQUEST = "{\"beneficiaryRegID\":11,\"beneficiaryID\":7,\"vanID\":7,"
				+ "\"visitCode\":22,\"benVisitID\":3,\"providerServiceMapID\":9,\"createdBy\":\"patient1\","
				+ "\"parkingPlaceID\":8,\"specialistDiagnosis\":\"Covid suspect\",\"benFlowID\":5,"
				+ "\"visitReason\":\"New Chief Complaint\",\"visitCategory\":\"COVID-19 Screening\","
				+ "\"isCovidFlowDone\":true,"
				+ "\"chiefComplaints\":{\"pastIllness\":[{\"illnessTypeID\":3,\"illnessType\":\"Fever\"}]},"
				+ "\"covidDetails\":{\"suspectedStatusUI\":\"Suspected\"}}";

		@org.junit.jupiter.api.BeforeEach
		void stubVisitCreation() {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryVisitDetails(org.mockito.ArgumentMatchers.any())).thenReturn(3L);
			org.mockito.Mockito.when(commonNurseServiceImpl.generateVisitCode(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(22L);
		}

		@Test
		@DisplayName("getChiefComplaintsMaster should render the chief complaint master")
		void getChiefComplaintsMaster_shouldRenderMaster() {
			org.mockito.Mockito.when(chiefComplaintMasterRepo.getChiefComplaintMaster())
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getChiefComplaintsMaster(1, 9, "Female"));
		}

		@Test
		@DisplayName("getCovidMaster should render the covid screening master")
		void getCovidMaster_shouldRenderMaster() {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getCovidMaster(1, 9, "Female"));
		}

		@Test
		@DisplayName("saveCovidScreeningData should create the episode and store the screening feedback")
		void saveCovidScreening_shouldCreateEpisodeAndStoreFeedback() throws Exception {
			org.mockito.Mockito.when(covid19ServiceImpl.saveCovidDetails(org.mockito.ArgumentMatchers.any()))
					.thenReturn(1);

			org.junit.jupiter.api.Assertions.assertNotNull(service.saveCovidScreeningData(BEN_REQUEST));
		}

		@Test
		@DisplayName("saveCovidScreeningData should still answer when the screening feedback was not stored")
		void saveCovidScreening_shouldStillAnswerWhenFeedbackNotStored() throws Exception {
			org.mockito.Mockito.when(covid19ServiceImpl.saveCovidDetails(org.mockito.ArgumentMatchers.any()))
					.thenReturn(null);

			org.junit.jupiter.api.Assertions.assertNotNull(service.saveCovidScreeningData(BEN_REQUEST));
		}

		@Test
		@DisplayName("saveCovidScreeningData should fail when the episode could not be created")
		void saveCovidScreening_shouldFailWhenEpisodeNotCreated() {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryVisitDetails(org.mockito.ArgumentMatchers.any())).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.saveCovidScreeningData(BEN_REQUEST));
		}

		@Test
		@DisplayName("savechiefComplaintsData should create the episode and store the complaints")
		void saveChiefComplaints_shouldCreateEpisodeAndStoreComplaints() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBenChiefComplaints(org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertNotNull(service.savechiefComplaintsData(BEN_REQUEST));
		}


		@Test
		@DisplayName("getPatientEpisodeData should assemble the covid details and the complaints")
		void getPatientEpisodeData_shouldAssembleEpisode() throws Exception {
			org.mockito.Mockito.when(covid19ServiceImpl.getBenVisitDetailsFrmNurseCovid19(11L, 22L))
					.thenReturn("{\"covidDetails\":\"{}\"}");
			org.mockito.Mockito.when(commonNurseServiceImpl.getBenChiefComplaints(11L, 22L)).thenReturn("[]");

			org.junit.jupiter.api.Assertions.assertNotNull(service.getPatientEpisodeData(BEN_REQUEST));
		}

		@Test
		@DisplayName("saveSpecialistDiagnosisData should store the diagnosis and advance the flow")
		void saveSpecialistDiagnosis_shouldStoreAndAdvanceFlow() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl.saveBenPrescription(org.mockito.ArgumentMatchers.any()))
					.thenReturn(4L);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBenFlowStatusAfterSpecialistMobileAPP(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveSpecialistDiagnosisData(BEN_REQUEST));
		}

		@Test
		@DisplayName("saveSpecialistDiagnosisData should fail when the flow could not be advanced")
		void saveSpecialistDiagnosis_shouldFailWhenFlowNotAdvanced() {
			org.mockito.Mockito.when(commonNurseServiceImpl.saveBenPrescription(org.mockito.ArgumentMatchers.any()))
					.thenReturn(4L);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.updateBenFlowStatusAfterSpecialistMobileAPP(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.saveSpecialistDiagnosisData(BEN_REQUEST));
		}

		@Test
		@DisplayName("getSpecialistDiagnosisData should return the recorded diagnosis")
		void getSpecialistDiagnosis_shouldReturnRecordedDiagnosis() throws Exception {
			org.mockito.Mockito.when(generalOPDDoctorServiceImpl.getGeneralOPDDiagnosisDetails(11L, 22L))
					.thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertEquals("{}", service.getSpecialistDiagnosisData(BEN_REQUEST));
		}

		@Test
		@DisplayName("getSpecialistDiagnosisData should fail when no diagnosis was recorded")
		void getSpecialistDiagnosis_shouldFailWithoutRecordedDiagnosis() {
			org.mockito.Mockito.when(generalOPDDoctorServiceImpl.getGeneralOPDDiagnosisDetails(11L, 22L))
					.thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.getSpecialistDiagnosisData(BEN_REQUEST));
		}

		@Test
		@DisplayName("getPatientsLast_3_Episode should render the three most recent episodes")
		void getLastThreeEpisodes_shouldRenderRecentEpisodes() throws Exception {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getPatientsLast_3_Episode(BEN_REQUEST));
		}

		@Test
		@DisplayName("getPatientBookedSlots should render the slots booked for the beneficiary")
		void getBookedSlots_shouldRenderBookedSlots() throws Exception {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getPatientBookedSlots(BEN_REQUEST));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("teleconsultation slot booking and booked slots")
	class SlotBookingTests {

		private static final String BOOK_REQUEST = "{\"beneficiaryRegID\":11,\"beneficiaryID\":7,\"visitCode\":22,"
				+ "\"createdBy\":\"9999999999\",\"firstName\":\"Asha\",\"lastName\":\"Devi\",\"age\":31,"
				+ "\"ageUnits\":\"years\",\"genderID\":2,\"genderName\":\"Female\",\"districtID\":31,"
				+ "\"districtName\":\"Nagpur\",\"villageId\":41,\"villageName\":\"Kamptee\","
				+ "\"providerServiceMapID\":9,\"vanID\":7,\"parkingPlaceID\":8}";

		private com.iemr.tm.data.nurse.BeneficiaryVisitDetail visit() {
			com.iemr.tm.data.nurse.BeneficiaryVisitDetail visit = new com.iemr.tm.data.nurse.BeneficiaryVisitDetail();
			visit.setBeneficiaryRegID(11L);
			visit.setBenVisitID(3L);
			visit.setVisitCode(22L);
			visit.setVisitReason("New Chief Complaint");
			visit.setVisitCategory("COVID-19 Screening");
			visit.setVisitNo((short) 1);
			return visit;
		}

		private com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest(Long tmRequestID) {
			com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest =
					new com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ();
			tcRequest.setUserID(41);
			tcRequest.setSpecializationID(3);
			tcRequest.setTmRequestID(tmRequestID);
			tcRequest.setAllocationDate(new java.sql.Timestamp(System.currentTimeMillis()));
			return tcRequest;
		}

		@Test
		@DisplayName("bookTCSlotData should create the beneficiary flow record for the booked slot")
		void bookTCSlot_shouldCreateFlowRecord() throws Exception {
			org.mockito.Mockito.when(benVisitDetailRepo.getVisitDetails(11L, 22L)).thenReturn(visit());
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(77L));
			com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus saved =
					new com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus();
			saved.setBenFlowID(5L);
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(saved);

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.bookTCSlotData(BOOK_REQUEST, "Bearer session-token"));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus> captor =
					org.mockito.ArgumentCaptor.forClass(com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus.class);
			org.mockito.Mockito.verify(beneficiaryFlowStatusRepo).save(captor.capture());
			org.junit.jupiter.api.Assertions.assertEquals("Asha Devi", captor.getValue().getBenName());
			org.junit.jupiter.api.Assertions.assertEquals((short) 1, captor.getValue().getSpecialist_flag());
		}

		@Test
		@DisplayName("bookTCSlotData should fail when the flow record could not be created")
		void bookTCSlot_shouldFailWhenFlowRecordNotCreated() throws Exception {
			org.mockito.Mockito.when(benVisitDetailRepo.getVisitDetails(11L, 22L)).thenReturn(visit());
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(77L));
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.bookTCSlotData(BOOK_REQUEST, "Bearer session-token"));
		}

		@Test
		@DisplayName("bookTCSlotData should fail when the slot could not be booked")
		void bookTCSlot_shouldFailWhenSlotNotBooked() throws Exception {
			org.mockito.Mockito.when(benVisitDetailRepo.getVisitDetails(11L, 22L)).thenReturn(visit());
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(null));

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.bookTCSlotData(BOOK_REQUEST, "Bearer session-token"));
		}

		@Test
		@DisplayName("bookTCSlotData should fail when the beneficiary has no visit")
		void bookTCSlot_shouldFailWithoutVisit() {
			org.mockito.Mockito.when(benVisitDetailRepo.getVisitDetails(11L, 22L)).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.bookTCSlotData(BOOK_REQUEST, "Bearer session-token"));
		}

		private com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus bookedSlot(Long beneficiaryRegID) {
			com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus slot =
					new com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus();
			slot.setBeneficiaryRegID(beneficiaryRegID);
			slot.setBeneficiaryID(7L);
			slot.setBenName("Asha Devi");
			slot.setAge("31 years");
			slot.setGenderName("Female");
			slot.settCSpecialistUserID(41);
			slot.settCRequestDate(new java.sql.Timestamp(System.currentTimeMillis()));
			return slot;
		}

		private void stubQuickblox() {
			com.iemr.tm.data.quickBlox.Quickblox quickblox =
					new com.iemr.tm.data.quickBlox.Quickblox();
			quickblox.setSpecialistBenQuickbloxID(1L);
			quickblox.setSpecialistBenQuickBloxPass("qb-pass");
			org.mockito.Mockito.when(quickBloxRepo.getQuickbloxIds(org.mockito.ArgumentMatchers.anyInt()))
					.thenReturn(quickblox);
		}

		@Test
		@DisplayName("getPatientBookedSlots should return the slot booked for this beneficiary")
		void getBookedSlots_shouldReturnSlotForThisBeneficiary() throws Exception {
			stubQuickblox();
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getBenSlotDetails("9999999999"))
					.thenReturn(new java.util.ArrayList<>(java.util.Arrays.asList(bookedSlot(11L))));

			String result = service.getPatientBookedSlots(BOOK_REQUEST);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("\"isAnyActiveSlotForSameBen\":true"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("qb-pass"));
		}

		@Test
		@DisplayName("getPatientBookedSlots should return the last slot when none belongs to this beneficiary")
		void getBookedSlots_shouldReturnLastSlotForAnotherBeneficiary() throws Exception {
			stubQuickblox();
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getBenSlotDetails("9999999999"))
					.thenReturn(new java.util.ArrayList<>(
							java.util.Arrays.asList(bookedSlot(99L), bookedSlot(98L))));

			String result = service.getPatientBookedSlots(BOOK_REQUEST);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("\"isAnyActiveSlotForSameBen\":false"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("patientDetails"));
		}

		@Test
		@DisplayName("getPatientBookedSlots should report no active slot")
		void getBookedSlots_shouldReportNoActiveSlot() throws Exception {
			org.mockito.Mockito.when(beneficiaryFlowStatusRepo.getBenSlotDetails("9999999999"))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertTrue(
					service.getPatientBookedSlots(BOOK_REQUEST).contains("\"isAnyActiveSlot\":false"));
		}
	}
}
