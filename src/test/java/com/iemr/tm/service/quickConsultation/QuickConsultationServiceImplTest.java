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
package com.iemr.tm.service.quickConsultation;

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

import com.iemr.tm.repo.nurse.BenPhysicalVitalRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.repo.quickConsultation.BenClinicalObservationsRepo;
import com.iemr.tm.repo.quickConsultation.ExternalTestOrderRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.generalOPD.GeneralOPDDoctorServiceImpl;
import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("QuickConsultationServiceImpl Test Suite")
class QuickConsultationServiceImplTest {

	@Mock
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenClinicalObservationsRepo benClinicalObservationsRepo;
	@Mock
	private PrescriptionDetailRepo prescriptionDetailRepo;
	@Mock
	private ExternalTestOrderRepo externalTestOrderRepo;
	@Mock
	private CommonNurseServiceImpl commonNurseServiceImpl;
	@Mock
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private GeneralOPDDoctorServiceImpl generalOPDDoctorServiceImpl;
	@Mock
	private CommonServiceImpl commonServiceImpl;
	@Mock
	private TeleConsultationServiceImpl teleConsultationServiceImpl;
	@Mock
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Mock
	private BenPhysicalVitalRepo benPhysicalVitalRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;

	@InjectMocks
	private QuickConsultationServiceImpl service;

	@Test
	@DisplayName("saveBeneficiaryChiefComplaint should answer for a well formed request")
	void saveBeneficiaryChiefComplaint_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBeneficiaryChiefComplaint(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("saveBeneficiaryClinicalObservations should answer for a well formed request")
	void saveBeneficiaryClinicalObservations_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBeneficiaryClinicalObservations(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("saveBenPrescriptionForANC should answer for a well formed request")
	void saveBenPrescriptionForANC_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenPrescriptionForANC(new com.iemr.tm.data.quickConsultation.PrescriptionDetail()));
	}

	@Test
	@DisplayName("saveBeneficiaryExternalLabTestOrderDetails should answer for a well formed request")
	void saveBeneficiaryExternalLabTestOrderDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBeneficiaryExternalLabTestOrderDetails(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("quickConsultNurseDataInsert should reject a request it cannot act on")
	void quickConsultNurseDataInsert_shouldRejectRequestItCannotActOn() {
		assertThrows(Exception.class, () -> service.quickConsultNurseDataInsert(new com.google.gson.JsonObject(), "{}"));
	}

	@Test
	@DisplayName("deleteVisitDetails should answer for a well formed request")
	void deleteVisitDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.deleteVisitDetails(new com.google.gson.JsonObject()));
	}

	@Test
	@DisplayName("quickConsultDoctorDataInsert should reject a request it cannot act on")
	void quickConsultDoctorDataInsert_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.quickConsultDoctorDataInsert(new com.google.gson.JsonObject(), "{}"));
	}

	@Test
	@DisplayName("getBenDataFrmNurseToDocVisitDetailsScreen should answer for a well formed request")
	void getBenDataFrmNurseToDocVisitDetailsScreen_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenDataFrmNurseToDocVisitDetailsScreen(11L, 11L));
	}

	@Test
	@DisplayName("getBeneficiaryVitalDetails should answer for a well formed request")
	void getBeneficiaryVitalDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBeneficiaryVitalDetails(11L, 11L));
	}

	@Test
	@DisplayName("getBenQuickConsultNurseData should answer for a well formed request")
	void getBenQuickConsultNurseData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenQuickConsultNurseData(11L, 11L));
	}

	@Test
	@DisplayName("getBenCaseRecordFromDoctorQuickConsult should answer for a well formed request")
	void getBenCaseRecordFromDoctorQuickConsult_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBenCaseRecordFromDoctorQuickConsult(11L, 11L));
	}

	@Test
	@DisplayName("updateGeneralOPDQCDoctorData should reject a request it cannot act on")
	void updateGeneralOPDQCDoctorData_shouldRejectRequestItCannotActOn() {
		assertThrows(RuntimeException.class, () -> service.updateGeneralOPDQCDoctorData(new com.google.gson.JsonObject(), "{}"));
	}

	@Test
	@DisplayName("updateBeneficiaryClinicalObservations should answer for a well formed request")
	void updateBeneficiaryClinicalObservations_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBeneficiaryClinicalObservations(new com.google.gson.JsonObject()));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("quick consultation capture")
	class QuickConsultationCaptureTests {

		private com.google.gson.JsonObject nurseRequest() {
			return com.google.gson.JsonParser.parseString("{"
					+ "\"beneficiaryRegID\":11,\"providerServiceMapID\":9,\"vanID\":7,\"sessionID\":1,"
					+ "\"benFlowID\":5,\"createdBy\":\"nurse1\","
					+ "\"visitDetails\":{\"beneficiaryRegID\":11,\"visitReason\":\"New Chief Complaint\","
					+ "                  \"visitCategory\":\"Quick Consultation\"},"
					+ "\"vitalsDetails\":{\"height_cm\":170}}").getAsJsonObject();
		}

		@org.junit.jupiter.api.BeforeEach
		void stubNurseCollaborators() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl.getMaxCurrentdate(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(0);
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryVisitDetails(org.mockito.ArgumentMatchers.any())).thenReturn(3L);
			org.mockito.Mockito.when(commonNurseServiceImpl.generateVisitCode(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(22L);
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryPhysicalAnthropometryDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryPhysicalVitalDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.anyShort(),
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyShort(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);
		}

		@Test
		@DisplayName("quickConsultNurseDataInsert should save the visit and the vitals")
		void nurseDataInsert_shouldSaveVisitAndVitals() throws Exception {
			String result = service.quickConsultNurseDataInsert(nurseRequest(), "Bearer session-token");

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Data saved successfully"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("\"visitCode\":\"22\""));
		}

		@Test
		@DisplayName("quickConsultNurseDataInsert should report an already saved visit")
		void nurseDataInsert_shouldReportAlreadySavedVisit() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl.getMaxCurrentdate(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertTrue(service
					.quickConsultNurseDataInsert(nurseRequest(), "Bearer session-token")
					.contains("Data already saved"));
		}

		@Test
		@DisplayName("quickConsultNurseDataInsert should fail when the vitals could not be stored")
		void nurseDataInsert_shouldFailWhenVitalsNotStored() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryPhysicalVitalDetails(org.mockito.ArgumentMatchers.any())).thenReturn(null);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.quickConsultNurseDataInsert(nurseRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("deleteVisitDetails should remove the visit rows for a created visit")
		void deleteVisitDetails_shouldRemoveVisitRows() throws Exception {
			org.mockito.Mockito.when(benVisitDetailRepo.getVisitCode(11L, 9)).thenReturn(22L);

			service.deleteVisitDetails(nurseRequest());

			org.mockito.Mockito.verify(benVisitDetailRepo).deleteVisitDetails(22L);
		}

		@Test
		@DisplayName("saveBeneficiaryClinicalObservations should return the stored observation id")
		void saveClinicalObservations_shouldReturnStoredId() throws Exception {
			com.iemr.tm.data.quickConsultation.BenClinicalObservations stored =
					new com.iemr.tm.data.quickConsultation.BenClinicalObservations();
			stored.setClinicalObservationID(4L);
			org.mockito.Mockito.when(benClinicalObservationsRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBeneficiaryClinicalObservations(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11}").getAsJsonObject()));
		}

		@Test
		@DisplayName("saveBenPrescriptionForANC should return the stored prescription id")
		void savePrescription_shouldReturnStoredId() {
			com.iemr.tm.data.quickConsultation.PrescriptionDetail stored =
					new com.iemr.tm.data.quickConsultation.PrescriptionDetail();
			stored.setPrescriptionID(4L);
			org.mockito.Mockito.when(prescriptionDetailRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(stored);

			org.junit.jupiter.api.Assertions.assertEquals(4L, service.saveBenPrescriptionForANC(
					new com.iemr.tm.data.quickConsultation.PrescriptionDetail()));
		}

		@Test
		@DisplayName("getBenDataFrmNurseToDocVisitDetailsScreen should assemble the visit and the complaints")
		void getVisitDetailsScreen_shouldAssembleVisitSections() {
			org.mockito.Mockito.when(commonNurseServiceImpl.getBenChiefComplaints(11L, 22L)).thenReturn("[]");

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.getBenDataFrmNurseToDocVisitDetailsScreen(11L, 22L));
		}

		@Test
		@DisplayName("getBeneficiaryVitalDetails should assemble the anthropometry and the physical vitals")
		void getVitalDetails_shouldAssembleVitalSections() {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.getBeneficiaryPhysicalAnthropometryDetails(11L, 22L)).thenReturn("{}");
			org.mockito.Mockito.when(commonNurseServiceImpl
					.getBeneficiaryPhysicalVitalDetails(11L, 22L)).thenReturn("{}");

			org.junit.jupiter.api.Assertions.assertNotNull(service.getBeneficiaryVitalDetails(11L, 22L));
		}

		@Test
		@DisplayName("getBenQuickConsultNurseData should assemble the nurse captured sections")
		void getNurseData_shouldAssembleNurseSections() {
			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenQuickConsultNurseData(11L, 22L));
		}

		@Test
		@DisplayName("getBenCaseRecordFromDoctorQuickConsult should assemble the doctor case record")
		void getCaseRecord_shouldAssembleDoctorCaseRecord() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl.getGraphicalTrendData(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(new java.util.HashMap<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getBenCaseRecordFromDoctorQuickConsult(11L, 22L));
		}
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("quick consultation doctor data")
	class DoctorDataTests {

		private static final String DOCTOR_REQUEST = "{\"beneficiaryRegID\":11,\"beneficiaryID\":9,\"benVisitID\":3,"
				+ "\"visitCode\":22,\"benFlowID\":5,\"providerServiceMapID\":9,\"createdBy\":\"doctor1\","
				+ "\"prescriptionID\":31,\"doctorSignatureFlag\":true,\"isSpecialist\":false,"
				+ "\"clinicalObservations\":{\"otherSymptoms\":\"fever\"},"
				+ "\"chiefComplaints\":[{\"chiefComplaint\":\"fever\",\"duration\":2,"
				+ "                     \"unitOfDuration\":\"days\"}],"
				+ "\"prescription\":[{\"drugID\":4,\"drugName\":\"Paracetamol\",\"dose\":\"1\","
				+ "                  \"frequency\":\"TDS\",\"duration\":\"3\",\"unitOfDuration\":\"days\"}],"
				+ "\"labTestOrders\":[{\"testID\":7,\"testName\":\"CBC\"}],"
				+ "\"rbsTestResult\":\"110\",\"rbsTestRemarks\":\"normal\","
				+ "\"refer\":{\"beneficiaryRegID\":11,\"visitCode\":22,\"referralReason\":\"follow up\"}}";

		private com.google.gson.JsonObject doctorRequest() {
			return com.google.gson.JsonParser.parseString(DOCTOR_REQUEST).getAsJsonObject();
		}

		private com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest(boolean walkIn) {
			com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ tcRequest =
					new com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ();
			tcRequest.setUserID(41);
			tcRequest.setSpecializationID(3);
			tcRequest.setTmRequestID(77L);
			tcRequest.setWalkIn(walkIn);
			tcRequest.setAllocationDate(new java.sql.Timestamp(System.currentTimeMillis()));
			return tcRequest;
		}

		@org.junit.jupiter.api.BeforeEach
		void stubDoctorCollaborators() throws Exception {
			com.iemr.tm.data.quickConsultation.BenClinicalObservations observations =
					new com.iemr.tm.data.quickConsultation.BenClinicalObservations();
			observations.setClinicalObservationID(4L);
			org.mockito.Mockito.when(benClinicalObservationsRepo.save(org.mockito.ArgumentMatchers.any()))
					.thenReturn(observations);
			org.mockito.Mockito.when(benChiefComplaintRepo.saveAll(org.mockito.ArgumentMatchers.any()))
					.thenAnswer(invocation -> invocation.getArgument(0));
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryPrescription(org.mockito.ArgumentMatchers.any())).thenReturn(31L);
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBenPrescribedDrugsList(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(commonNurseServiceImpl.saveBeneficiaryLabTestOrderDetails(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(commonNurseServiceImpl
					.updatePrescription(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(benPhysicalVitalRepo.updatePhysicalVitalDetailsQCDoctor(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(commonDoctorServiceImpl
					.saveBenReferDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(commonDoctorServiceImpl
					.updateBenReferDetails(org.mockito.ArgumentMatchers.any())).thenReturn(1L);
			org.mockito.Mockito.when(commonDoctorServiceImpl
					.updateBenClinicalObservations(org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);
			org.mockito.Mockito.when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(1);
		}

		@Test
		@DisplayName("quickConsultDoctorDataInsert should save every doctor section for a walk in visit")
		void doctorDataInsert_shouldSaveEverySection() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.quickConsultDoctorDataInsert(doctorRequest(), "Bearer session-token"));

			org.mockito.Mockito.verify(commonNurseServiceImpl)
					.saveBenPrescribedDrugsList(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(commonNurseServiceImpl).saveBeneficiaryLabTestOrderDetails(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(31L));
			org.mockito.Mockito.verify(benPhysicalVitalRepo).updatePhysicalVitalDetailsQCDoctor("110", "normal", 11L,
					22L);
		}

		@Test
		@DisplayName("quickConsultDoctorDataInsert should text the beneficiary about a scheduled teleconsultation")
		void doctorDataInsert_shouldTextBeneficiaryAboutSchedule() throws Exception {
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(false));

			org.junit.jupiter.api.Assertions.assertEquals(1,
					service.quickConsultDoctorDataInsert(doctorRequest(), "Bearer session-token"));

			org.mockito.Mockito.verify(sMSGatewayServiceImpl).smsSenderGateway(
					org.mockito.ArgumentMatchers.eq("schedule"), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyString());
		}

		@Test
		@DisplayName("quickConsultDoctorDataInsert should save a visit without a prescription or a test")
		void doctorDataInsert_shouldSaveVisitWithoutPrescriptionOrTest() throws Exception {
			org.junit.jupiter.api.Assertions.assertEquals(1, service.quickConsultDoctorDataInsert(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11,\"visitCode\":22,"
							+ "\"clinicalObservations\":{},\"chiefComplaints\":[{\"chiefComplaint\":\"fever\"}]}")
							.getAsJsonObject(),
					"Bearer session-token"));
		}

		@Test
		@DisplayName("quickConsultDoctorDataInsert should fail when the beneficiary flow could not be advanced")
		void doctorDataInsert_shouldFailWhenFlowNotAdvanced() throws Exception {
			org.mockito.Mockito.when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.quickConsultDoctorDataInsert(doctorRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("quickConsultDoctorDataInsert should fail when the prescription could not be created")
		void doctorDataInsert_shouldFailWhenPrescriptionNotCreated() throws Exception {
			org.mockito.Mockito.when(commonNurseServiceImpl
					.saveBeneficiaryPrescription(org.mockito.ArgumentMatchers.any())).thenReturn(0L);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.quickConsultDoctorDataInsert(doctorRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("updateGeneralOPDQCDoctorData should update every doctor section")
		void doctorDataUpdate_shouldUpdateEverySection() throws Exception {
			org.junit.jupiter.api.Assertions.assertNotNull(
					service.updateGeneralOPDQCDoctorData(doctorRequest(), "Bearer session-token"));

			org.mockito.Mockito.verify(commonNurseServiceImpl)
					.updatePrescription(org.mockito.ArgumentMatchers.any());
			org.mockito.Mockito.verify(commonDoctorServiceImpl)
					.updateBenReferDetails(org.mockito.ArgumentMatchers.any());
		}

		@Test
		@DisplayName("updateGeneralOPDQCDoctorData should text the beneficiary about a scheduled teleconsultation")
		void doctorDataUpdate_shouldTextBeneficiaryAboutSchedule() throws Exception {
			org.mockito.Mockito.when(commonServiceImpl.createTcRequest(org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
					.thenReturn(tcRequest(false));

			org.junit.jupiter.api.Assertions.assertNotNull(
					service.updateGeneralOPDQCDoctorData(doctorRequest(), "Bearer session-token"));

			org.mockito.Mockito.verify(sMSGatewayServiceImpl).smsSenderGateway(
					org.mockito.ArgumentMatchers.eq("schedule"), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString(),
					org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.anyString());
		}

		@Test
		@DisplayName("updateGeneralOPDQCDoctorData should update a visit without a prescription or a test")
		void doctorDataUpdate_shouldUpdateVisitWithoutPrescriptionOrTest() throws Exception {
			org.junit.jupiter.api.Assertions.assertNotNull(service.updateGeneralOPDQCDoctorData(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11,\"visitCode\":22,"
							+ "\"prescriptionID\":31,\"clinicalObservations\":{},"
							+ "\"chiefComplaints\":[{\"chiefComplaint\":\"fever\"}]}").getAsJsonObject(),
					"Bearer session-token"));
		}

		@Test
		@DisplayName("updateGeneralOPDQCDoctorData should fail when the beneficiary flow could not be advanced")
		void doctorDataUpdate_shouldFailWhenFlowNotAdvanced() throws Exception {
			org.mockito.Mockito.when(commonDoctorServiceImpl.updateBenFlowtableAfterDocDataUpdate(
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateGeneralOPDQCDoctorData(doctorRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("updateGeneralOPDQCDoctorData should fail when the observations could not be updated")
		void doctorDataUpdate_shouldFailWhenObservationsNotUpdated() throws Exception {
			org.mockito.Mockito.when(commonDoctorServiceImpl
					.updateBenClinicalObservations(org.mockito.ArgumentMatchers.any())).thenReturn(0);

			org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
					() -> service.updateGeneralOPDQCDoctorData(doctorRequest(), "Bearer session-token"));
		}

		@Test
		@DisplayName("updateBeneficiaryClinicalObservations should attach the SNOMED code for the entered symptoms")
		void updateObservations_shouldAttachSnomedCode() throws Exception {
			org.mockito.Mockito.when(commonDoctorServiceImpl.getSnomedCTcode("fever"))
					.thenReturn(new String[] { "386661006", "Fever" });
			org.mockito.Mockito.when(commonDoctorServiceImpl
					.updateBenClinicalObservations(org.mockito.ArgumentMatchers.any())).thenReturn(1);

			org.junit.jupiter.api.Assertions.assertEquals(1, service.updateBeneficiaryClinicalObservations(
					com.google.gson.JsonParser.parseString("{\"otherSymptoms\":\"fever\"}").getAsJsonObject()));

			org.mockito.ArgumentCaptor<com.iemr.tm.data.quickConsultation.BenClinicalObservations> captor =
					org.mockito.ArgumentCaptor.forClass(
							com.iemr.tm.data.quickConsultation.BenClinicalObservations.class);
			org.mockito.Mockito.verify(commonDoctorServiceImpl).updateBenClinicalObservations(captor.capture());
			org.junit.jupiter.api.Assertions.assertEquals("386661006", captor.getValue().getOtherSymptomsSCTCode());
		}
	}
}
