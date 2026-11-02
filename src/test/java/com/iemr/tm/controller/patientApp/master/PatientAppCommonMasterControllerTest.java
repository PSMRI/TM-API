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
package com.iemr.tm.controller.patientApp.master;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.iemr.tm.service.patientApp.master.CommonPatientAppMasterService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PatientAppCommonMasterController Test Suite")
class PatientAppCommonMasterControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String REQUEST = "{\"beneficiaryRegID\":11,\"visitCode\":22}";

	@Mock
	private CommonPatientAppMasterService commonPatientAppMasterService;

	private PatientAppCommonMasterController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked service")
	void setUp() {
		controller = new PatientAppCommonMasterController();
		controller.setCommonPatientAppMasterService(commonPatientAppMasterService);
	}

	@Nested
	@DisplayName("patientAppChiefComplaintsMasterData")
	class PatientAppChiefComplaintsMasterDataTests {

		@Test
		@DisplayName("patientAppChiefComplaintsMasterData should return the master the service assembled")
		void patientAppChiefComplaintsMasterData_shouldReturnServiceMaster() {
			when(commonPatientAppMasterService.getChiefComplaintsMaster(1, 9, "Female")).thenReturn("{\"master\":[]}");

			String result = controller.patientAppChiefComplaintsMasterData(1, 9, "Female");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("master"));
		}
	}

	@Nested
	@DisplayName("patientAppCovidMasterData")
	class PatientAppCovidMasterDataTests {

		@Test
		@DisplayName("patientAppCovidMasterData should return the master the service assembled")
		void patientAppCovidMasterData_shouldReturnServiceMaster() {
			when(commonPatientAppMasterService.getCovidMaster(1, 9, "Female")).thenReturn("{\"master\":[]}");

			String result = controller.patientAppCovidMasterData(1, 9, "Female");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("master"));
		}
	}

	@Nested
	@DisplayName("saveBenCovidDoctorDataPatientApp")
	class SaveBenCovidDoctorDataPatientAppTests {

		@Test
		@DisplayName("saveBenCovidDoctorDataPatientApp should return the payload the service produced")
		void saveBenCovidDoctorDataPatientApp_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.saveCovidScreeningData(REQUEST)).thenReturn("saved");

			String result = controller.saveBenCovidDoctorDataPatientApp(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("saved"));
		}

		@Test
		@DisplayName("saveBenCovidDoctorDataPatientApp should report a request the service could not act on")
		void saveBenCovidDoctorDataPatientApp_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.saveCovidScreeningData(REQUEST))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenCovidDoctorDataPatientApp(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}
	}

	@Nested
	@DisplayName("saveBenChiefComplaintsDataPatientApp")
	class SaveBenChiefComplaintsDataPatientAppTests {

		@Test
		@DisplayName("saveBenChiefComplaintsDataPatientApp should return the payload the service produced")
		void saveBenChiefComplaintsDataPatientApp_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.savechiefComplaintsData(REQUEST)).thenReturn("saved");

			String result = controller.saveBenChiefComplaintsDataPatientApp(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("saved"));
		}

		@Test
		@DisplayName("saveBenChiefComplaintsDataPatientApp should report a request the service could not act on")
		void saveBenChiefComplaintsDataPatientApp_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.savechiefComplaintsData(REQUEST))
					.thenThrow(new IllegalStateException("save failed"));

			assertTrue(controller.saveBenChiefComplaintsDataPatientApp(REQUEST, AUTHORIZATION).contains("Unable to save data"));
		}
	}

	@Nested
	@DisplayName("saveTCSlotDataPatientApp")
	class SaveTCSlotDataPatientAppTests {

		@Test
		@DisplayName("saveTCSlotDataPatientApp should confirm the booked slot")
		void saveTCSlotDataPatientApp_shouldConfirmBookedSlot() throws Exception {
			when(commonPatientAppMasterService.bookTCSlotData(REQUEST, AUTHORIZATION)).thenReturn(1);

			String result = controller.saveTCSlotDataPatientApp(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("saveTCSlotDataPatientApp should report a slot the service could not book")
		void saveTCSlotDataPatientApp_shouldReportUnbookedSlot() throws Exception {
			when(commonPatientAppMasterService.bookTCSlotData(REQUEST, AUTHORIZATION)).thenReturn(null);

			assertTrue(controller.saveTCSlotDataPatientApp(REQUEST, AUTHORIZATION).contains("error in slot booking"));
		}

		@Test
		@DisplayName("saveTCSlotDataPatientApp should report a request the service could not act on")
		void saveTCSlotDataPatientApp_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.bookTCSlotData(REQUEST, AUTHORIZATION))
					.thenThrow(new IllegalStateException("booking failed"));

			assertTrue(controller.saveTCSlotDataPatientApp(REQUEST, AUTHORIZATION).contains("error in slot booking"));
		}
	}

	@Nested
	@DisplayName("getPatientEpisodeDataMobileApp")
	class GetPatientEpisodeDataMobileAppTests {

		@Test
		@DisplayName("getPatientEpisodeDataMobileApp should return the payload the service produced")
		void getPatientEpisodeDataMobileApp_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.getPatientEpisodeData(REQUEST)).thenReturn("{\"data\":[]}");

			String result = controller.getPatientEpisodeDataMobileApp(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getPatientEpisodeDataMobileApp should report an answer the service could not produce")
		void getPatientEpisodeDataMobileApp_shouldReportMissingAnswer() throws Exception {
			when(commonPatientAppMasterService.getPatientEpisodeData(REQUEST)).thenReturn(null);

			assertTrue(controller.getPatientEpisodeDataMobileApp(REQUEST, AUTHORIZATION).contains("error in getting beneficiary episode data"));
		}

		@Test
		@DisplayName("getPatientEpisodeDataMobileApp should report a request the service could not act on")
		void getPatientEpisodeDataMobileApp_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.getPatientEpisodeData(REQUEST))
					.thenThrow(new IllegalStateException("lookup failed"));

			assertTrue(controller.getPatientEpisodeDataMobileApp(REQUEST, AUTHORIZATION).contains("error in getting beneficiary episode data"));
		}
	}

	@Nested
	@DisplayName("getPatientBookedSlotDetails")
	class GetPatientBookedSlotDetailsTests {

		@Test
		@DisplayName("getPatientBookedSlotDetails should return the payload the service produced")
		void getPatientBookedSlotDetails_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.getPatientBookedSlots(REQUEST)).thenReturn("{\"data\":[]}");

			String result = controller.getPatientBookedSlotDetails(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getPatientBookedSlotDetails should report an answer the service could not produce")
		void getPatientBookedSlotDetails_shouldReportMissingAnswer() throws Exception {
			when(commonPatientAppMasterService.getPatientBookedSlots(REQUEST)).thenReturn(null);

			assertTrue(controller.getPatientBookedSlotDetails(REQUEST, AUTHORIZATION).contains("error in getting beneficiary booked slot data"));
		}

		@Test
		@DisplayName("getPatientBookedSlotDetails should report a request the service could not act on")
		void getPatientBookedSlotDetails_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.getPatientBookedSlots(REQUEST))
					.thenThrow(new IllegalStateException("lookup failed"));

			assertTrue(controller.getPatientBookedSlotDetails(REQUEST, AUTHORIZATION).contains("error in getting beneficiary booked slot data"));
		}
	}

	@Nested
	@DisplayName("saveSpecialistDiagnosisData")
	class SaveSpecialistDiagnosisDataTests {

		@Test
		@DisplayName("saveSpecialistDiagnosisData should return the payload the service produced")
		void saveSpecialistDiagnosisData_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.saveSpecialistDiagnosisData(REQUEST)).thenReturn(4L);

			String result = controller.saveSpecialistDiagnosisData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("saveSpecialistDiagnosisData should report an answer the service could not produce")
		void saveSpecialistDiagnosisData_shouldReportMissingAnswer() throws Exception {
			when(commonPatientAppMasterService.saveSpecialistDiagnosisData(REQUEST)).thenReturn(null);

			assertTrue(controller.saveSpecialistDiagnosisData(REQUEST, AUTHORIZATION).contains("error in saving diagnosis data"));
		}

		@Test
		@DisplayName("saveSpecialistDiagnosisData should report a request the service could not act on")
		void saveSpecialistDiagnosisData_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.saveSpecialistDiagnosisData(REQUEST))
					.thenThrow(new IllegalStateException("lookup failed"));

			assertTrue(controller.saveSpecialistDiagnosisData(REQUEST, AUTHORIZATION)
					.contains("error in saving specialist diagnosis data"));
		}
	}

	@Nested
	@DisplayName("getSpecialistDiagnosisData")
	class GetSpecialistDiagnosisDataTests {

		@Test
		@DisplayName("getSpecialistDiagnosisData should return the payload the service produced")
		void getSpecialistDiagnosisData_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.getSpecialistDiagnosisData(REQUEST)).thenReturn("{\"data\":[]}");

			String result = controller.getSpecialistDiagnosisData(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getSpecialistDiagnosisData should report an answer the service could not produce")
		void getSpecialistDiagnosisData_shouldReportMissingAnswer() throws Exception {
			when(commonPatientAppMasterService.getSpecialistDiagnosisData(REQUEST)).thenReturn(null);

			assertTrue(controller.getSpecialistDiagnosisData(REQUEST, AUTHORIZATION).contains("error in getting diagnosis data"));
		}

		@Test
		@DisplayName("getSpecialistDiagnosisData should report a request the service could not act on")
		void getSpecialistDiagnosisData_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.getSpecialistDiagnosisData(REQUEST))
					.thenThrow(new IllegalStateException("lookup failed"));

			assertTrue(controller.getSpecialistDiagnosisData(REQUEST, AUTHORIZATION)
					.contains("error in getting specialist diagnosis data"));
		}
	}

	@Nested
	@DisplayName("getPatientsLast_3_Episode")
	class GetPatientsLast_3_EpisodeTests {

		@Test
		@DisplayName("getPatientsLast_3_Episode should return the payload the service produced")
		void getPatientsLast_3_Episode_shouldReturnServicePayload() throws Exception {
			when(commonPatientAppMasterService.getPatientsLast_3_Episode(REQUEST)).thenReturn("{\"data\":[]}");

			String result = controller.getPatientsLast_3_Episode(REQUEST, AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getPatientsLast_3_Episode should report an answer the service could not produce")
		void getPatientsLast_3_Episode_shouldReportMissingAnswer() throws Exception {
			when(commonPatientAppMasterService.getPatientsLast_3_Episode(REQUEST)).thenReturn(null);

			assertTrue(controller.getPatientsLast_3_Episode(REQUEST, AUTHORIZATION).contains("error in getPatientsLast_3_Episode data"));
		}

		@Test
		@DisplayName("getPatientsLast_3_Episode should report a request the service could not act on")
		void getPatientsLast_3_Episode_shouldReportRequestItCannotActOn() throws Exception {
			when(commonPatientAppMasterService.getPatientsLast_3_Episode(REQUEST))
					.thenThrow(new IllegalStateException("lookup failed"));

			assertTrue(controller.getPatientsLast_3_Episode(REQUEST, AUTHORIZATION).contains("error in getPatientsLast_3_Episode data"));
		}
	}
}
