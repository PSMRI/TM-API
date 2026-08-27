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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.data.anc.BenAdherence;
import com.iemr.tm.data.anc.BenAllergyHistory;
import com.iemr.tm.data.anc.BenChildDevelopmentHistory;
import com.iemr.tm.data.anc.ChildFeedingDetails;
import com.iemr.tm.data.anc.PerinatalHistory;
import com.iemr.tm.data.anc.WrapperBenInvestigationANC;
import com.iemr.tm.data.anc.BenFamilyHistory;
import com.iemr.tm.data.anc.BenMedHistory;
import com.iemr.tm.data.anc.BenMedicationHistory;
import com.iemr.tm.data.anc.BenMenstrualDetails;
import com.iemr.tm.data.anc.BenPersonalHabit;
import com.iemr.tm.data.anc.BencomrbidityCondDetails;
import com.iemr.tm.data.anc.ChildOptionalVaccineDetail;
import com.iemr.tm.data.anc.ChildVaccineDetail1;
import com.iemr.tm.data.anc.FemaleObstetricHistory;
import com.iemr.tm.data.anc.PhyGeneralExamination;
import com.iemr.tm.data.anc.PhyHeadToToeExamination;
import com.iemr.tm.data.anc.SysCardiovascularExamination;
import com.iemr.tm.data.anc.SysCentralNervousExamination;
import com.iemr.tm.data.anc.SysGastrointestinalExamination;
import com.iemr.tm.data.anc.SysGenitourinarySystemExamination;
import com.iemr.tm.data.anc.SysMusculoskeletalSystemExamination;
import com.iemr.tm.data.anc.SysRespiratoryExamination;
import com.iemr.tm.data.anc.WrapperChildOptionalVaccineDetail;
import com.iemr.tm.data.anc.WrapperComorbidCondDetails;
import com.iemr.tm.data.anc.WrapperFemaleObstetricHistory;
import com.iemr.tm.data.anc.WrapperImmunizationHistory;
import com.iemr.tm.data.anc.WrapperMedicationHistory;
import com.iemr.tm.data.login.Users;
import com.iemr.tm.data.ncdScreening.IDRSData;
import com.iemr.tm.data.nurse.BenAnthropometryDetail;
import com.iemr.tm.data.nurse.BenPhysicalVitalDetail;
import com.iemr.tm.data.nurse.BeneficiaryVisitDetail;
import com.iemr.tm.data.ncdScreening.PhysicalActivityType;
import com.iemr.tm.data.quickConsultation.BenChiefComplaint;
import com.iemr.tm.data.quickConsultation.PrescribedDrugDetail;
import com.iemr.tm.data.quickConsultation.PrescriptionDetail;
import com.iemr.tm.data.snomedct.SCTDescription;
import com.iemr.tm.repo.bmiCalculation.BMICalculationRepo;
import com.iemr.tm.repo.login.UserLoginRepo;
import com.iemr.tm.repo.nurse.BenAnthropometryRepo;
import com.iemr.tm.repo.nurse.BenPhysicalVitalRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.nurse.anc.BenAllergyHistoryRepo;
import com.iemr.tm.repo.nurse.anc.BenChildDevelopmentHistoryRepo;
import com.iemr.tm.repo.nurse.anc.BenFamilyHistoryRepo;
import com.iemr.tm.repo.nurse.anc.BenMedHistoryRepo;
import com.iemr.tm.repo.nurse.anc.BenMedicationHistoryRepo;
import com.iemr.tm.repo.nurse.anc.BenMenstrualDetailsRepo;
import com.iemr.tm.repo.nurse.anc.BenPersonalHabitRepo;
import com.iemr.tm.repo.nurse.anc.BencomrbidityCondRepo;
import com.iemr.tm.repo.nurse.anc.ChildFeedingDetailsRepo;
import com.iemr.tm.repo.nurse.anc.ChildOptionalVaccineDetailRepo;
import com.iemr.tm.repo.nurse.anc.ChildVaccineDetail1Repo;
import com.iemr.tm.repo.nurse.anc.FemaleObstetricHistoryRepo;
import com.iemr.tm.repo.nurse.anc.PerinatalHistoryRepo;
import com.iemr.tm.repo.nurse.anc.PhyGeneralExaminationRepo;
import com.iemr.tm.repo.nurse.anc.PhyHeadToToeExaminationRepo;
import com.iemr.tm.repo.nurse.anc.SysCardiovascularExaminationRepo;
import com.iemr.tm.repo.nurse.anc.SysCentralNervousExaminationRepo;
import com.iemr.tm.repo.nurse.anc.SysGastrointestinalExaminationRepo;
import com.iemr.tm.repo.nurse.anc.SysGenitourinarySystemExaminationRepo;
import com.iemr.tm.repo.nurse.anc.SysMusculoskeletalSystemExaminationRepo;
import com.iemr.tm.repo.nurse.anc.SysRespiratoryExaminationRepo;
import com.iemr.tm.utils.exception.IEMRException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CommonNurseServiceImpl Test Suite")
class CommonNurseServiceImplTest {

	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_CODE = 22L;

	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private UserLoginRepo userLoginRepo;
	@Mock
	private com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo benChiefComplaintRepo;
	@Mock
	private BenMedHistoryRepo benMedHistoryRepo;
	@Mock
	private BencomrbidityCondRepo bencomrbidityCondRepo;
	@Mock
	private BenMedicationHistoryRepo benMedicationHistoryRepo;
	@Mock
	private FemaleObstetricHistoryRepo femaleObstetricHistoryRepo;
	@Mock
	private BenMenstrualDetailsRepo benMenstrualDetailsRepo;
	@Mock
	private BenFamilyHistoryRepo benFamilyHistoryRepo;
	@Mock
	private BenPersonalHabitRepo benPersonalHabitRepo;
	@Mock
	private BenAllergyHistoryRepo benAllergyHistoryRepo;
	@Mock
	private ChildOptionalVaccineDetailRepo childOptionalVaccineDetailRepo;
	@Mock
	private ChildVaccineDetail1Repo childVaccineDetail1Repo;
	@Mock
	private BenAnthropometryRepo benAnthropometryRepo;
	@Mock
	private BenPhysicalVitalRepo benPhysicalVitalRepo;
	@Mock
	private PhyGeneralExaminationRepo phyGeneralExaminationRepo;
	@Mock
	private PhyHeadToToeExaminationRepo phyHeadToToeExaminationRepo;
	@Mock
	private SysGastrointestinalExaminationRepo sysGastrointestinalExaminationRepo;
	@Mock
	private SysCardiovascularExaminationRepo sysCardiovascularExaminationRepo;
	@Mock
	private SysRespiratoryExaminationRepo sysRespiratoryExaminationRepo;
	@Mock
	private SysCentralNervousExaminationRepo sysCentralNervousExaminationRepo;
	@Mock
	private SysMusculoskeletalSystemExaminationRepo sysMusculoskeletalSystemExaminationRepo;
	@Mock
	private SysGenitourinarySystemExaminationRepo sysGenitourinarySystemExaminationRepo;
	@Mock
	private com.iemr.tm.repo.registrar.RegistrarRepoBenData registrarRepoBenData;
	@Mock
	private com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo prescriptionDetailRepo;
	@Mock
	private com.iemr.tm.repo.quickConsultation.LabTestOrderDetailRepo labTestOrderDetailRepo;
	@Mock
	private com.iemr.tm.repo.quickConsultation.PrescribedDrugDetailRepo prescribedDrugDetailRepo;
	@Mock
	private com.iemr.tm.repo.registrar.ReistrarRepoBenSearch reistrarRepoBenSearch;
	@Mock
	private BenAdherenceRepo benAdherenceRepo;
	@Mock
	private BenChildDevelopmentHistoryRepo benChildDevelopmentHistoryRepo;
	@Mock
	private ChildFeedingDetailsRepo childFeedingDetailsRepo;
	@Mock
	private PerinatalHistoryRepo perinatalHistoryRepo;
	@Mock
	private com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Mock
	private com.iemr.tm.repo.nurse.ncdscreening.PhysicalActivityTypeRepo physicalActivityTypeRepo;
	@Mock
	private com.iemr.tm.repo.nurse.ncdscreening.PhysicalActivityTypeRepo physicalActivityaRepo;
	@Mock
	private com.iemr.tm.repo.nurse.ncdscreening.IDRSDataRepo iDRSDataRepo;
	@Mock
	private com.iemr.tm.repo.nurse.ncdscreening.IDRSDataRepo iDrsDataRepo;
	@Mock
	private com.iemr.tm.repo.nurse.BenCancerVitalDetailRepo benCancerVitalDetailRepo;
	@Mock
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Mock
	private com.iemr.tm.repo.doctor.BenReferDetailsRepo benReferDetailsRepo;
	@Mock
	private BMICalculationRepo bmiCalculationRepo;

	@InjectMocks
	private CommonNurseServiceImpl service;

	/** Echoes the entities handed to a {@code saveAll} call back to the caller. */
	private static <T> org.mockito.stubbing.Answer<T> echoList() {
		return invocation -> invocation.getArgument(0);
	}

	private BeneficiaryVisitDetail visitDetail() {
		BeneficiaryVisitDetail detail = new BeneficiaryVisitDetail();
		detail.setBeneficiaryRegID(BEN_REG_ID);
		detail.setVisitCode(VISIT_CODE);
		detail.setCreatedBy("nurse1");
		return detail;
	}

	@Nested
	@DisplayName("beneficiary visit details")
	class VisitDetailTests {

		@Test
		@DisplayName("updateBeneficiaryStatus should delegate the flow status change to the registrar repository")
		void updateBeneficiaryStatus_shouldDelegateToRegistrarRepo() {
			when(registrarRepoBenData.updateBenFlowStatus('N', BEN_REG_ID)).thenReturn(1);

			assertEquals(1, service.updateBeneficiaryStatus('N', BEN_REG_ID));
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetails should increment the visit number and return the new visit id")
		void saveBeneficiaryVisitDetails_shouldIncrementVisitNumber() {
			BeneficiaryVisitDetail detail = visitDetail();
			detail.setFileIDs(new Integer[] { 7, 8 });
			when(benVisitDetailRepo.getVisitCountForBeneficiary(BEN_REG_ID)).thenReturn((short) 2);
			BeneficiaryVisitDetail saved = visitDetail();
			saved.setBenVisitID(99L);
			when(benVisitDetailRepo.save(any(BeneficiaryVisitDetail.class))).thenReturn(saved);
			Users user = new Users();
			user.setUserID(42L);
			when(userLoginRepo.getUserByUsername("nurse1")).thenReturn(user);

			assertEquals(99L, service.saveBeneficiaryVisitDetails(detail));

			ArgumentCaptor<BeneficiaryVisitDetail> captor = ArgumentCaptor.forClass(BeneficiaryVisitDetail.class);
			verify(benVisitDetailRepo).save(captor.capture());
			assertEquals((short) 3, captor.getValue().getVisitNo());
			assertEquals("7,8,", captor.getValue().getReportFilePath());
			assertEquals(42L, captor.getValue().getNurseID());
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetails should start the visit numbering at one for a first visit")
		void saveBeneficiaryVisitDetails_shouldStartVisitNumberingAtOne() {
			when(benVisitDetailRepo.getVisitCountForBeneficiary(BEN_REG_ID)).thenReturn(null);
			BeneficiaryVisitDetail saved = visitDetail();
			saved.setBenVisitID(99L);
			when(benVisitDetailRepo.save(any(BeneficiaryVisitDetail.class))).thenReturn(saved);

			assertEquals(99L, service.saveBeneficiaryVisitDetails(visitDetail()));

			ArgumentCaptor<BeneficiaryVisitDetail> captor = ArgumentCaptor.forClass(BeneficiaryVisitDetail.class);
			verify(benVisitDetailRepo).save(captor.capture());
			assertEquals((short) 1, captor.getValue().getVisitNo());
			assertEquals("", captor.getValue().getReportFilePath());
			assertNull(captor.getValue().getNurseID());
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetails should return null when the save does not come back")
		void saveBeneficiaryVisitDetails_shouldReturnNullWhenSaveFails() {
			when(benVisitDetailRepo.getVisitCountForBeneficiary(BEN_REG_ID)).thenReturn((short) 0);
			when(benVisitDetailRepo.save(any(BeneficiaryVisitDetail.class))).thenReturn(null);

			assertNull(service.saveBeneficiaryVisitDetails(visitDetail()));
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetails should leave the nurse unresolved for a blank createdBy")
		void saveBeneficiaryVisitDetails_shouldLeaveNurseUnresolvedForBlankCreatedBy() {
			BeneficiaryVisitDetail detail = visitDetail();
			detail.setCreatedBy("   ");
			BeneficiaryVisitDetail saved = visitDetail();
			saved.setBenVisitID(99L);
			when(benVisitDetailRepo.save(any(BeneficiaryVisitDetail.class))).thenReturn(saved);

			service.saveBeneficiaryVisitDetails(detail);

			verify(userLoginRepo, never()).getUserByUsername(anyString());
		}

		@Test
		@DisplayName("saveBeneficiaryVisitDetails should leave the nurse unresolved for an unknown user")
		void saveBeneficiaryVisitDetails_shouldLeaveNurseUnresolvedForUnknownUser() {
			when(userLoginRepo.getUserByUsername("nurse1")).thenReturn(null);
			BeneficiaryVisitDetail saved = visitDetail();
			saved.setBenVisitID(99L);
			when(benVisitDetailRepo.save(any(BeneficiaryVisitDetail.class))).thenReturn(saved);

			service.saveBeneficiaryVisitDetails(visitDetail());

			ArgumentCaptor<BeneficiaryVisitDetail> captor = ArgumentCaptor.forClass(BeneficiaryVisitDetail.class);
			verify(benVisitDetailRepo).save(captor.capture());
			assertNull(captor.getValue().getNurseID());
		}

		@Test
		@DisplayName("getMaxCurrentdate should report a recent visit as still within the ten minute window")
		void getMaxCurrentdate_shouldReportRecentVisitWithinWindow() throws Exception {
			String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new java.util.Date(System.currentTimeMillis()));
			when(benVisitDetailRepo.getMaxCreatedDate(BEN_REG_ID, "New", "ANC")).thenReturn(now + ".0");

			assertEquals(1, service.getMaxCurrentdate(BEN_REG_ID, "New", "ANC"));
		}

		@Test
		@DisplayName("getMaxCurrentdate should report an old visit as outside the ten minute window")
		void getMaxCurrentdate_shouldReportOldVisitOutsideWindow() throws Exception {
			when(benVisitDetailRepo.getMaxCreatedDate(BEN_REG_ID, "New", "ANC"))
					.thenReturn("2020-01-01 10:00:00.0");

			assertEquals(-1, service.getMaxCurrentdate(BEN_REG_ID, "New", "ANC"));
		}

		@Test
		@DisplayName("getMaxCurrentdate should report zero when the beneficiary has no earlier visit")
		void getMaxCurrentdate_shouldReportZeroWithoutEarlierVisit() throws Exception {
			when(benVisitDetailRepo.getMaxCreatedDate(BEN_REG_ID, "New", "ANC")).thenReturn(null);

			assertEquals(0, service.getMaxCurrentdate(BEN_REG_ID, "New", "ANC"));
		}

		@Test
		@DisplayName("getMaxCurrentdate should fail for an unparseable created date")
		void getMaxCurrentdate_shouldFailForUnparseableDate() {
			when(benVisitDetailRepo.getMaxCreatedDate(BEN_REG_ID, "New", "ANC")).thenReturn("not-a-date.0");

			IEMRException thrown = assertThrows(IEMRException.class,
					() -> service.getMaxCurrentdate(BEN_REG_ID, "New", "ANC"));

			assertTrue(thrown.getMessage().contains("Error while parseing created date"));
		}

		@Test
		@DisplayName("generateVisitCode should build a zero padded visit code and return it once stored")
		void generateVisitCode_shouldBuildZeroPaddedVisitCode() {
			when(benVisitDetailRepo.updateVisitCode(10000100123456L, 123456L)).thenReturn(1);

			assertEquals(10000100123456L, service.generateVisitCode(123456L, 1, 1));
		}

		@Test
		@DisplayName("generateVisitCode should return zero when the visit code could not be stored")
		void generateVisitCode_shouldReturnZeroWhenNotStored() {
			when(benVisitDetailRepo.updateVisitCode(anyLong(), anyLong())).thenReturn(0);

			assertEquals(0L, service.generateVisitCode(123456L, 1, 1));
		}

		@Test
		@DisplayName("updateVisitCodeInVisitDetailsTable should delegate to the visit detail repository")
		void updateVisitCodeInVisitDetailsTable_shouldDelegateToRepo() {
			when(benVisitDetailRepo.updateVisitCode(55L, 66L)).thenReturn(1);

			assertEquals(1, service.updateVisitCodeInVisitDetailsTable(55L, 66L));
		}

		@Test
		@DisplayName("getBenVisitCount should return the next visit number for a returning beneficiary")
		void getBenVisitCount_shouldReturnNextVisitNumber() {
			when(benVisitDetailRepo.getVisitCountForBeneficiary(BEN_REG_ID)).thenReturn((short) 4);

			assertEquals((short) 5, service.getBenVisitCount(BEN_REG_ID));
		}

		@Test
		@DisplayName("getBenVisitCount should return one for a first time beneficiary")
		void getBenVisitCount_shouldReturnOneForFirstVisit() {
			when(benVisitDetailRepo.getVisitCountForBeneficiary(BEN_REG_ID)).thenReturn(null);

			assertEquals((short) 1, service.getBenVisitCount(BEN_REG_ID));
		}

		@Test
		@DisplayName("updateBeneficiaryVisitDetails should return the number of rows changed")
		void updateBeneficiaryVisitDetails_shouldReturnRowsChanged() {
			when(benVisitDetailRepo.updateBeneficiaryVisitDetail(any(), any(), any(), any(), any(), any(), any(), any(),
					any(), any())).thenReturn(1);

			assertEquals(1, service.updateBeneficiaryVisitDetails(visitDetail()));
		}

		@Test
		@DisplayName("updateBeneficiaryVisitDetails should swallow a repository failure and report no change")
		void updateBeneficiaryVisitDetails_shouldSwallowRepositoryFailure() {
			when(benVisitDetailRepo.updateBeneficiaryVisitDetail(any(), any(), any(), any(), any(), any(), any(), any(),
					any(), any())).thenThrow(new IllegalStateException("db down"));

			assertEquals(0, service.updateBeneficiaryVisitDetails(visitDetail()));
		}

		@Test
		@DisplayName("getCSVisitDetails should copy the stored visit and expand the report file ids")
		void getCSVisitDetails_shouldCopyVisitAndExpandFileIds() {
			BeneficiaryVisitDetail stored = visitDetail();
			stored.setBenVisitID(99L);
			stored.setReportFilePath("7, 8,");
			when(benVisitDetailRepo.getVisitDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			BeneficiaryVisitDetail result = service.getCSVisitDetails(BEN_REG_ID, VISIT_CODE);

			assertNotNull(result);
			assertEquals(99L, result.getBenVisitID());
			assertEquals(2, result.getFileIDs().length);
			assertEquals(7, result.getFileIDs()[0]);
			assertEquals(8, result.getFileIDs()[1]);
		}

		@Test
		@DisplayName("getCSVisitDetails should leave the file ids empty when no report path is stored")
		void getCSVisitDetails_shouldLeaveFileIdsEmptyWithoutReportPath() {
			BeneficiaryVisitDetail stored = visitDetail();
			stored.setBenVisitID(99L);
			when(benVisitDetailRepo.getVisitDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			BeneficiaryVisitDetail result = service.getCSVisitDetails(BEN_REG_ID, VISIT_CODE);

			assertEquals(0, result.getFileIDs().length);
		}

		@Test
		@DisplayName("getCSVisitDetails should return null when the visit is unknown")
		void getCSVisitDetails_shouldReturnNullForUnknownVisit() {
			when(benVisitDetailRepo.getVisitDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(null);

			assertNull(service.getCSVisitDetails(BEN_REG_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("history saves")
	class HistorySaveTests {

		@Test
		@DisplayName("saveBenChiefComplaints should store only the complaints that carry an id")
		void saveBenChiefComplaints_shouldStoreOnlyIdentifiedComplaints() {
			BenChiefComplaint identified = new BenChiefComplaint();
			identified.setChiefComplaintID(3);
			BenChiefComplaint unidentified = new BenChiefComplaint();
			when(benChiefComplaintRepo.saveAll(any())).thenReturn(Collections.singletonList(identified));

			assertEquals(1, service.saveBenChiefComplaints(Arrays.asList(identified, unidentified)));
		}

		@Test
		@DisplayName("saveBenChiefComplaints should succeed without touching the repository when nothing is identified")
		void saveBenChiefComplaints_shouldSucceedWithoutIdentifiedComplaints() {
			assertEquals(1, service.saveBenChiefComplaints(Collections.singletonList(new BenChiefComplaint())));

			verify(benChiefComplaintRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveBenChiefComplaints should report a failure when not every complaint was stored")
		void saveBenChiefComplaints_shouldReportPartialSaveAsFailure() {
			BenChiefComplaint identified = new BenChiefComplaint();
			identified.setChiefComplaintID(3);
			when(benChiefComplaintRepo.saveAll(any())).thenReturn(Collections.emptyList());

			assertEquals(0, service.saveBenChiefComplaints(Collections.singletonList(identified)));
		}

		@Test
		@DisplayName("saveBenPastHistory should store the derived past illness and surgery entries")
		void saveBenPastHistory_shouldStoreDerivedEntries() {
			BenMedHistory history = new BenMedHistory();
			ArrayList<Map<String, Object>> illnesses = new ArrayList<>();
			illnesses.add(illness());
			history.setPastIllness(illnesses);
			history.setPastSurgery(new ArrayList<>());
			when(benMedHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveBenPastHistory(history));
		}

		@Test
		@DisplayName("saveBenPastHistory should succeed for an empty past history")
		void saveBenPastHistory_shouldSucceedForEmptyHistory() {
			BenMedHistory history = new BenMedHistory();
			history.setPastIllness(new ArrayList<>());
			history.setPastSurgery(new ArrayList<>());

			assertEquals(1L, service.saveBenPastHistory(history));
			verify(benMedHistoryRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveBenPastHistory should report a failure when not every entry was stored")
		void saveBenPastHistory_shouldReportPartialSaveAsFailure() {
			BenMedHistory history = new BenMedHistory();
			ArrayList<Map<String, Object>> illnesses = new ArrayList<>();
			illnesses.add(illness());
			history.setPastIllness(illnesses);
			history.setPastSurgery(new ArrayList<>());
			when(benMedHistoryRepo.saveAll(any())).thenReturn(new ArrayList<BenMedHistory>());

			assertNull(service.saveBenPastHistory(history));
		}

		@Test
		@DisplayName("saveBenComorbidConditions should return the id of the first stored condition")
		void saveBenComorbidConditions_shouldReturnFirstStoredId() {
			WrapperComorbidCondDetails wrapper = new WrapperComorbidCondDetails();
			BencomrbidityCondDetails condition = new BencomrbidityCondDetails();
			condition.setComorbidCondition("Diabetes");
			condition.setID(5L);
			wrapper.setComorbidityConcurrentConditionsList(
					new ArrayList<>(Collections.singletonList(condition)));
			when(bencomrbidityCondRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(5L, service.saveBenComorbidConditions(wrapper));
		}

		@Test
		@DisplayName("saveBenComorbidConditions should succeed when no condition carries a name")
		void saveBenComorbidConditions_shouldSucceedWithoutNamedConditions() {
			WrapperComorbidCondDetails wrapper = new WrapperComorbidCondDetails();
			wrapper.setComorbidityConcurrentConditionsList(
					new ArrayList<>(Collections.singletonList(new BencomrbidityCondDetails())));

			assertEquals(1L, service.saveBenComorbidConditions(wrapper));
			verify(bencomrbidityCondRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveBenComorbidConditions should report a failure when not every condition was stored")
		void saveBenComorbidConditions_shouldReportPartialSaveAsFailure() {
			WrapperComorbidCondDetails wrapper = new WrapperComorbidCondDetails();
			BencomrbidityCondDetails condition = new BencomrbidityCondDetails();
			condition.setComorbidCondition("Diabetes");
			wrapper.setComorbidityConcurrentConditionsList(
					new ArrayList<>(Collections.singletonList(condition)));
			when(bencomrbidityCondRepo.saveAll(any())).thenReturn(new ArrayList<BencomrbidityCondDetails>());

			assertNull(service.saveBenComorbidConditions(wrapper));
		}

		@Test
		@DisplayName("saveBenMedicationHistory should return the id of the first stored entry")
		void saveBenMedicationHistory_shouldReturnFirstStoredId() {
			WrapperMedicationHistory wrapper = new WrapperMedicationHistory();
			BenMedicationHistory entry = new BenMedicationHistory();
			entry.setCurrentMedication("Metformin");
			entry.setID(6L);
			wrapper.setMedicationHistoryList(new ArrayList<>(Collections.singletonList(entry)));
			when(benMedicationHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(6L, service.saveBenMedicationHistory(wrapper));
		}

		@Test
		@DisplayName("saveBenMedicationHistory should succeed when no entry names a medication")
		void saveBenMedicationHistory_shouldSucceedWithoutNamedMedication() {
			WrapperMedicationHistory wrapper = new WrapperMedicationHistory();
			wrapper.setMedicationHistoryList(new ArrayList<>(Collections.singletonList(new BenMedicationHistory())));

			assertEquals(1L, service.saveBenMedicationHistory(wrapper));
			verify(benMedicationHistoryRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveFemaleObstetricHistory should flatten the complication lists before storing")
		void saveFemaleObstetricHistory_shouldFlattenComplicationLists() {
			FemaleObstetricHistory history = new FemaleObstetricHistory();
			history.setPregComplicationList(complications("pregComplicationID", "pregComplicationType"));
			history.setDeliveryComplicationList(
					complications("deliveryComplicationID", "deliveryComplicationType"));
			history.setPostpartumComplicationList(
					complications("postpartumComplicationID", "postpartumComplicationType"));
			ArrayList<Map<String, Object>> postAbortion = new ArrayList<>();
			postAbortion.add(complication("complicationID", 1d, "complicationValue", "Sepsis"));
			postAbortion.add(complication("complicationID", 2d, "complicationValue", "Bleeding"));
			history.setPostAbortionComplication(postAbortion);
			history.setAbortionType(complication("complicationID", 3d, "complicationValue", "Induced"));
			history.setTypeofFacility(complication("serviceFacilityID", 4d, "facilityName", "PHC"));

			WrapperFemaleObstetricHistory wrapper = new WrapperFemaleObstetricHistory();
			wrapper.setFemaleObstetricHistoryList(new ArrayList<>(Collections.singletonList(history)));
			when(femaleObstetricHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveFemaleObstetricHistory(wrapper));
			assertEquals("1,2", history.getPregComplicationID());
			assertEquals("A,B", history.getPregComplicationType());
			assertEquals("1,2", history.getDeliveryComplicationID());
			assertEquals("1,2", history.getPostpartumComplicationID());
			assertEquals("1,2", history.getPostAbortionComplication_db());
			assertEquals("Sepsis,Bleeding", history.getPostAbortionComplicationsValues());
			assertEquals(3, history.getAbortionTypeID());
			assertEquals("Induced", history.getTypeOfAbortionValue());
			assertEquals(4, history.getTypeofFacilityID());
			assertEquals("PHC", history.getServiceFacilityValue());
		}

		@Test
		@DisplayName("saveFemaleObstetricHistory should succeed for an entry without any complications")
		void saveFemaleObstetricHistory_shouldSucceedWithoutComplications() {
			WrapperFemaleObstetricHistory wrapper = new WrapperFemaleObstetricHistory();
			wrapper.setFemaleObstetricHistoryList(
					new ArrayList<>(Collections.singletonList(new FemaleObstetricHistory())));
			when(femaleObstetricHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveFemaleObstetricHistory(wrapper));
		}

		@Test
		@DisplayName("saveFemaleObstetricHistory should succeed for an empty obstetric history")
		void saveFemaleObstetricHistory_shouldSucceedForEmptyHistory() {
			WrapperFemaleObstetricHistory wrapper = new WrapperFemaleObstetricHistory();
			wrapper.setFemaleObstetricHistoryList(new ArrayList<>());

			assertEquals(1L, service.saveFemaleObstetricHistory(wrapper));
			verify(femaleObstetricHistoryRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveBenMenstrualHistory should flatten the menstrual problem list before storing")
		void saveBenMenstrualHistory_shouldFlattenProblemList() {
			BenMenstrualDetails details = new BenMenstrualDetails();
			ArrayList<Map<String, Object>> problems = new ArrayList<>();
			problems.add(complication("menstrualProblemID", 1, "problemName", "Cramps"));
			problems.add(complication("menstrualProblemID", 2, "problemName", "Irregular"));
			details.setMenstrualProblemList(problems);
			BenMenstrualDetails saved = new BenMenstrualDetails();
			saved.setBenMenstrualID(9);
			when(benMenstrualDetailsRepo.save(details)).thenReturn(saved);

			assertEquals(9, service.saveBenMenstrualHistory(details));
			assertEquals("1,2", details.getMenstrualProblemID());
			assertEquals("Cramps,Irregular", details.getProblemName());
		}

		@Test
		@DisplayName("saveBenMenstrualHistory should return null when the stored row carries no id")
		void saveBenMenstrualHistory_shouldReturnNullWithoutStoredId() {
			BenMenstrualDetails details = new BenMenstrualDetails();
			BenMenstrualDetails saved = new BenMenstrualDetails();
			saved.setBenMenstrualID(0);
			when(benMenstrualDetailsRepo.save(details)).thenReturn(saved);

			assertNull(service.saveBenMenstrualHistory(details));
		}

		@Test
		@DisplayName("saveBenFamilyHistory should store the derived family history entries")
		void saveBenFamilyHistory_shouldStoreDerivedEntries() {
			BenFamilyHistory history = new BenFamilyHistory();
			history.setFamilyDiseaseList(familyDiseaseList());
			when(benFamilyHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveBenFamilyHistory(history));
		}

		@Test
		@DisplayName("saveBenFamilyHistory should succeed for an empty family history")
		void saveBenFamilyHistory_shouldSucceedForEmptyHistory() {
			BenFamilyHistory history = new BenFamilyHistory();
			history.setFamilyDiseaseList(new ArrayList<>());

			assertEquals(1L, service.saveBenFamilyHistory(history));
			verify(benFamilyHistoryRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("savePersonalHistory should store the derived personal habit entries")
		void savePersonalHistory_shouldStoreDerivedEntries() {
			BenPersonalHabit habit = new BenPersonalHabit();
			List<Map<String, String>> tobacco = new ArrayList<>();
			Map<String, String> entry = new HashMap<>();
			entry.put("habit", "Smoking");
			tobacco.add(entry);
			habit.setTobaccoList(tobacco);
			habit.setAlcoholList(new ArrayList<>());
			habit.setAllergicList(new ArrayList<>());
			when(benPersonalHabitRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.savePersonalHistory(habit));
		}

		@Test
		@DisplayName("savePersonalHistory should succeed for an empty personal history")
		void savePersonalHistory_shouldSucceedForEmptyHistory() {
			BenPersonalHabit habit = new BenPersonalHabit();
			habit.setTobaccoList(new ArrayList<>());
			habit.setAlcoholList(new ArrayList<>());
			habit.setAllergicList(new ArrayList<>());

			assertEquals(1, service.savePersonalHistory(habit));
		}

		@Test
		@DisplayName("saveAllergyHistory should store the derived allergy entries")
		void saveAllergyHistory_shouldStoreDerivedEntries() {
			BenAllergyHistory allergy = new BenAllergyHistory();
			List<Map<String, Object>> allergies = new ArrayList<>();
			Map<String, Object> entry = new HashMap<>();
			entry.put("allergyType", "Food");
			allergies.add(entry);
			allergy.setAllergicList(allergies);
			when(benAllergyHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveAllergyHistory(allergy));
		}

		@Test
		@DisplayName("saveAllergyHistory should succeed for an empty allergy history")
		void saveAllergyHistory_shouldSucceedForEmptyHistory() {
			BenAllergyHistory allergy = new BenAllergyHistory();
			allergy.setAllergicList(new ArrayList<>());

			assertEquals(1L, service.saveAllergyHistory(allergy));
			verify(benAllergyHistoryRepo, never()).saveAll(any());
		}

		@Test
		@DisplayName("saveChildOptionalVaccineDetail should store the optional vaccine entries")
		void saveChildOptionalVaccineDetail_shouldStoreEntries() {
			WrapperChildOptionalVaccineDetail wrapper = new WrapperChildOptionalVaccineDetail();
			wrapper.setChildOptionalVaccineList(
					new ArrayList<>(Collections.singletonList(new ChildOptionalVaccineDetail())));
			when(childOptionalVaccineDetailRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveChildOptionalVaccineDetail(wrapper));
		}

		@Test
		@DisplayName("saveChildOptionalVaccineDetail should succeed for an empty vaccine list")
		void saveChildOptionalVaccineDetail_shouldSucceedForEmptyList() {
			WrapperChildOptionalVaccineDetail wrapper = new WrapperChildOptionalVaccineDetail();
			wrapper.setChildOptionalVaccineList(new ArrayList<>());

			assertEquals(1L, service.saveChildOptionalVaccineDetail(wrapper));
		}

		@Test
		@DisplayName("saveImmunizationHistory should expand each dose of the immunization list")
		void saveImmunizationHistory_shouldExpandEachDose() {
			WrapperImmunizationHistory wrapper = new WrapperImmunizationHistory();
			ChildVaccineDetail1 vaccine = new ChildVaccineDetail1();
			List<Map<String, Object>> vaccines = new ArrayList<>();
			Map<String, Object> dose = new HashMap<>();
			dose.put("vaccine", "BCG");
			dose.put("status", Boolean.TRUE);
			vaccines.add(dose);
			vaccine.setVaccines(vaccines);
			wrapper.setImmunizationList(new ArrayList<>(Collections.singletonList(vaccine)));
			when(childVaccineDetail1Repo.saveAll(any())).thenAnswer(echoList());

			assertNull(service.saveImmunizationHistory(wrapper));
		}

		@Test
		@DisplayName("saveImmunizationHistory should store a placeholder row for an empty immunization list")
		void saveImmunizationHistory_shouldStorePlaceholderForEmptyList() {
			WrapperImmunizationHistory wrapper = new WrapperImmunizationHistory();
			wrapper.setImmunizationList(new ArrayList<>());
			when(childVaccineDetail1Repo.saveAll(any())).thenAnswer(echoList());

			assertNull(service.saveImmunizationHistory(wrapper));
		}

		@Test
		@DisplayName("saveBenFamilyHistoryNCDScreening should store the derived screening family history")
		void saveBenFamilyHistoryNCDScreening_shouldStoreDerivedEntries() {
			BenFamilyHistory history = new BenFamilyHistory();
			history.setFamilyDiseaseList(familyDiseaseList());
			when(benFamilyHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveBenFamilyHistoryNCDScreening(history));
		}

		@Test
		@DisplayName("saveBenFamilyHistoryNCDScreening should succeed for an empty screening history")
		void saveBenFamilyHistoryNCDScreening_shouldSucceedForEmptyHistory() {
			BenFamilyHistory history = new BenFamilyHistory();
			history.setFamilyDiseaseList(new ArrayList<>());

			assertEquals(1L, service.saveBenFamilyHistoryNCDScreening(history));
			verify(benFamilyHistoryRepo, never()).saveAll(any());
		}

		private Map<String, Object> illness() {
			Map<String, Object> map = new HashMap<>();
			map.put("illnessType", "Asthma");
			map.put("illnessTypeID", 1);
			return map;
		}

		private List<Map<String, Object>> familyDiseaseList() {
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String, Object> disease = new HashMap<>();
			disease.put("diseaseType", "Diabetes");
			disease.put("diseaseTypeID", 1);
			disease.put("deleted", "false");
			disease.put("familyMembers", Arrays.asList("Mother", "Father"));
			list.add(disease);
			return list;
		}

		private ArrayList<Map<String, Object>> complications(String idKey, String nameKey) {
			ArrayList<Map<String, Object>> list = new ArrayList<>();
			list.add(complication(idKey, 1, nameKey, "A"));
			list.add(complication(idKey, 2, nameKey, "B"));
			return list;
		}

		private Map<String, Object> complication(String idKey, Object id, String nameKey, Object name) {
			Map<String, Object> map = new HashMap<>();
			map.put(idKey, id);
			map.put(nameKey, name);
			return map;
		}
	}

	@Nested
	@DisplayName("vitals and examinations")
	class VitalsAndExaminationTests {

		@Test
		@DisplayName("saveBeneficiaryPhysicalAnthropometryDetails should return the stored row id")
		void saveAnthropometry_shouldReturnStoredId() {
			BenAnthropometryDetail detail = new BenAnthropometryDetail();
			BenAnthropometryDetail saved = new BenAnthropometryDetail();
			saved.setID(4L);
			when(benAnthropometryRepo.save(detail)).thenReturn(saved);

			assertEquals(4L, service.saveBeneficiaryPhysicalAnthropometryDetails(detail));
		}

		@Test
		@DisplayName("saveBeneficiaryPhysicalAnthropometryDetails should return null when nothing was stored")
		void saveAnthropometry_shouldReturnNullWhenNothingStored() {
			BenAnthropometryDetail detail = new BenAnthropometryDetail();
			when(benAnthropometryRepo.save(detail)).thenReturn(null);

			assertNull(service.saveBeneficiaryPhysicalAnthropometryDetails(detail));
		}

		@Test
		@DisplayName("saveIDRS should return the stored row id")
		void saveIDRS_shouldReturnStoredId() {
			IDRSData detail = new IDRSData();
			IDRSData saved = new IDRSData();
			saved.setId(4L);
			when(iDrsDataRepo.save(detail)).thenReturn(saved);

			assertEquals(4L, service.saveIDRS(detail));
		}

		@Test
		@DisplayName("saveIDRS should return null when nothing was stored")
		void saveIDRS_shouldReturnNullWhenNothingStored() {
			IDRSData detail = new IDRSData();
			when(iDrsDataRepo.save(detail)).thenReturn(null);

			assertNull(service.saveIDRS(detail));
		}

		@Test
		@DisplayName("savePhysicalActivity should return the stored row id")
		void savePhysicalActivity_shouldReturnStoredId() {
			PhysicalActivityType detail = new PhysicalActivityType();
			PhysicalActivityType saved = new PhysicalActivityType();
			saved.setpAID(4L);
			when(physicalActivityaRepo.save(detail)).thenReturn(saved);

			assertEquals(4L, service.savePhysicalActivity(detail));
		}

		@Test
		@DisplayName("savePhysicalActivity should return null when nothing was stored")
		void savePhysicalActivity_shouldReturnNullWhenNothingStored() {
			PhysicalActivityType detail = new PhysicalActivityType();
			when(physicalActivityaRepo.save(detail)).thenReturn(null);

			assertNull(service.savePhysicalActivity(detail));
		}

		@Test
		@DisplayName("saveBeneficiaryPhysicalVitalDetails should average the three blood pressure readings")
		void savePhysicalVitals_shouldAverageThreeReadings() {
			BenPhysicalVitalDetail detail = new BenPhysicalVitalDetail();
			detail.setSystolicBP_1stReading((short) 120);
			detail.setDiastolicBP_1stReading((short) 80);
			detail.setSystolicBP_2ndReading((short) 130);
			detail.setDiastolicBP_2ndReading((short) 90);
			detail.setSystolicBP_3rdReading((short) 110);
			detail.setDiastolicBP_3rdReading((short) 70);
			BenPhysicalVitalDetail saved = new BenPhysicalVitalDetail();
			saved.setID(4L);
			when(benPhysicalVitalRepo.save(detail)).thenReturn(saved);

			assertEquals(4L, service.saveBeneficiaryPhysicalVitalDetails(detail));
			assertEquals((short) 120, detail.getAverageSystolicBP());
			assertEquals((short) 80, detail.getAverageDiastolicBP());
		}

		@Test
		@DisplayName("saveBeneficiaryPhysicalVitalDetails should leave the average unset without any reading")
		void savePhysicalVitals_shouldLeaveAverageUnsetWithoutReadings() {
			BenPhysicalVitalDetail detail = new BenPhysicalVitalDetail();
			BenPhysicalVitalDetail saved = new BenPhysicalVitalDetail();
			saved.setID(4L);
			when(benPhysicalVitalRepo.save(detail)).thenReturn(saved);

			assertEquals(4L, service.saveBeneficiaryPhysicalVitalDetails(detail));
			assertNull(detail.getAverageSystolicBP());
		}

		@Test
		@DisplayName("saveBeneficiaryPhysicalVitalDetails should return null when nothing was stored")
		void savePhysicalVitals_shouldReturnNullWhenNothingStored() {
			BenPhysicalVitalDetail detail = new BenPhysicalVitalDetail();
			when(benPhysicalVitalRepo.save(detail)).thenReturn(null);

			assertNull(service.saveBeneficiaryPhysicalVitalDetails(detail));
		}

		@Test
		@DisplayName("getBeneficiaryPhysicalAnthropometryDetails should return the stored row as JSON")
		void getAnthropometry_shouldReturnJson() {
			BenAnthropometryDetail detail = new BenAnthropometryDetail();
			detail.setID(4L);
			when(benAnthropometryRepo.getBenAnthropometryDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(detail);

			assertTrue(service.getBeneficiaryPhysicalAnthropometryDetails(BEN_REG_ID, VISIT_CODE).contains("\"ID\""));
		}

		@Test
		@DisplayName("getBeneficiaryPhysicalVitalDetails should return the stored row as JSON")
		void getPhysicalVitals_shouldReturnJson() {
			BenPhysicalVitalDetail detail = new BenPhysicalVitalDetail();
			detail.setID(4L);
			when(benPhysicalVitalRepo.getBenPhysicalVitalDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(detail);

			assertTrue(service.getBeneficiaryPhysicalVitalDetails(BEN_REG_ID, VISIT_CODE).contains("\"ID\""));
		}

		@Test
		@DisplayName("updateANCAnthropometryDetails should mark an already processed row as updated")
		void updateAnthropometry_shouldMarkProcessedRowAsUpdated() {
			BenAnthropometryDetail detail = new BenAnthropometryDetail();
			detail.setBeneficiaryRegID(BEN_REG_ID);
			detail.setVisitCode(VISIT_CODE);
			when(benAnthropometryRepo.getBenAnthropometryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");
			when(benAnthropometryRepo.updateANCCareDetails(any(), any(), any(), any(), any(), any(), any(), any(),
					any(), eq("U"), eq(BEN_REG_ID), eq(VISIT_CODE))).thenReturn(1);

			assertEquals(1, service.updateANCAnthropometryDetails(detail));
		}

		@Test
		@DisplayName("updateANCAnthropometryDetails should keep a fresh row marked as new")
		void updateAnthropometry_shouldKeepFreshRowAsNew() {
			BenAnthropometryDetail detail = new BenAnthropometryDetail();
			detail.setBeneficiaryRegID(BEN_REG_ID);
			detail.setVisitCode(VISIT_CODE);
			when(benAnthropometryRepo.getBenAnthropometryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("N");
			when(benAnthropometryRepo.updateANCCareDetails(any(), any(), any(), any(), any(), any(), any(), any(),
					any(), eq("N"), eq(BEN_REG_ID), eq(VISIT_CODE))).thenReturn(1);

			assertEquals(1, service.updateANCAnthropometryDetails(detail));
		}

		@Test
		@DisplayName("updateANCAnthropometryDetails should report no change for a null payload")
		void updateAnthropometry_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateANCAnthropometryDetails(null));
		}

		@Test
		@DisplayName("updateANCPhysicalVitalDetails should copy the first reading into the averages")
		void updatePhysicalVitals_shouldCopyFirstReadingIntoAverages() {
			BenPhysicalVitalDetail detail = new BenPhysicalVitalDetail();
			detail.setBeneficiaryRegID(BEN_REG_ID);
			detail.setVisitCode(VISIT_CODE);
			detail.setSystolicBP_1stReading((short) 118);
			detail.setDiastolicBP_1stReading((short) 78);
			when(benPhysicalVitalRepo.getBenPhysicalVitalStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");
			when(benPhysicalVitalRepo.updatePhysicalVitalDetails(any(), any(), any(), any(), any(), any(), any(), any(),
					any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
					eq("U"), any(), any(), any(), eq(BEN_REG_ID), eq(VISIT_CODE))).thenReturn(1);

			assertEquals(1, service.updateANCPhysicalVitalDetails(detail));
			assertEquals((short) 118, detail.getAverageSystolicBP());
			assertEquals((short) 78, detail.getAverageDiastolicBP());
		}

		@Test
		@DisplayName("updateANCPhysicalVitalDetails should report no change for a null payload")
		void updatePhysicalVitals_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateANCPhysicalVitalDetails(null));
		}

		@Test
		@DisplayName("savePhyGeneralExamination should flatten the danger sign list before storing")
		void saveGeneralExamination_shouldFlattenDangerSigns() {
			PhyGeneralExamination examination = new PhyGeneralExamination();
			examination.setTypeOfDangerSigns(new ArrayList<>(Arrays.asList("Fever", "Bleeding")));
			PhyGeneralExamination saved = new PhyGeneralExamination();
			saved.setID(4L);
			when(phyGeneralExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.savePhyGeneralExamination(examination));
			assertEquals("Fever,Bleeding,", examination.getTypeOfDangerSign());
		}

		@Test
		@DisplayName("savePhyGeneralExamination should return null when nothing was stored")
		void saveGeneralExamination_shouldReturnNullWhenNothingStored() {
			PhyGeneralExamination examination = new PhyGeneralExamination();
			when(phyGeneralExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.savePhyGeneralExamination(examination));
		}

		@Test
		@DisplayName("savePhyHeadToToeExamination should return the stored row id")
		void saveHeadToToeExamination_shouldReturnStoredId() {
			PhyHeadToToeExamination examination = new PhyHeadToToeExamination();
			PhyHeadToToeExamination saved = new PhyHeadToToeExamination();
			saved.setID(4L);
			when(phyHeadToToeExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.savePhyHeadToToeExamination(examination));
		}

		@Test
		@DisplayName("savePhyHeadToToeExamination should return null when nothing was stored")
		void saveHeadToToeExamination_shouldReturnNullWhenNothingStored() {
			PhyHeadToToeExamination examination = new PhyHeadToToeExamination();
			when(phyHeadToToeExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.savePhyHeadToToeExamination(examination));
		}

		@Test
		@DisplayName("saveSysGastrointestinalExamination should return the stored row id")
		void saveGastrointestinalExamination_shouldReturnStoredId() {
			SysGastrointestinalExamination examination = new SysGastrointestinalExamination();
			SysGastrointestinalExamination saved = new SysGastrointestinalExamination();
			saved.setID(4L);
			when(sysGastrointestinalExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveSysGastrointestinalExamination(examination));
		}

		@Test
		@DisplayName("saveSysGastrointestinalExamination should return null when nothing was stored")
		void saveGastrointestinalExamination_shouldReturnNullWhenNothingStored() {
			SysGastrointestinalExamination examination = new SysGastrointestinalExamination();
			when(sysGastrointestinalExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveSysGastrointestinalExamination(examination));
		}

		@Test
		@DisplayName("saveSysCardiovascularExamination should return the stored row id")
		void saveCardiovascularExamination_shouldReturnStoredId() {
			SysCardiovascularExamination examination = new SysCardiovascularExamination();
			SysCardiovascularExamination saved = new SysCardiovascularExamination();
			saved.setID(4L);
			when(sysCardiovascularExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveSysCardiovascularExamination(examination));
		}

		@Test
		@DisplayName("saveSysCardiovascularExamination should return null when nothing was stored")
		void saveCardiovascularExamination_shouldReturnNullWhenNothingStored() {
			SysCardiovascularExamination examination = new SysCardiovascularExamination();
			when(sysCardiovascularExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveSysCardiovascularExamination(examination));
		}

		@Test
		@DisplayName("saveSysRespiratoryExamination should return the stored row id")
		void saveRespiratoryExamination_shouldReturnStoredId() {
			SysRespiratoryExamination examination = new SysRespiratoryExamination();
			SysRespiratoryExamination saved = new SysRespiratoryExamination();
			saved.setID(4L);
			when(sysRespiratoryExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveSysRespiratoryExamination(examination));
		}

		@Test
		@DisplayName("saveSysRespiratoryExamination should return null when nothing was stored")
		void saveRespiratoryExamination_shouldReturnNullWhenNothingStored() {
			SysRespiratoryExamination examination = new SysRespiratoryExamination();
			when(sysRespiratoryExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveSysRespiratoryExamination(examination));
		}

		@Test
		@DisplayName("saveSysCentralNervousExamination should return the stored row id")
		void saveCentralNervousExamination_shouldReturnStoredId() {
			SysCentralNervousExamination examination = new SysCentralNervousExamination();
			SysCentralNervousExamination saved = new SysCentralNervousExamination();
			saved.setID(4L);
			when(sysCentralNervousExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveSysCentralNervousExamination(examination));
		}

		@Test
		@DisplayName("saveSysCentralNervousExamination should return null when nothing was stored")
		void saveCentralNervousExamination_shouldReturnNullWhenNothingStored() {
			SysCentralNervousExamination examination = new SysCentralNervousExamination();
			when(sysCentralNervousExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveSysCentralNervousExamination(examination));
		}

		@Test
		@DisplayName("saveSysMusculoskeletalSystemExamination should return the stored row id")
		void saveMusculoskeletalExamination_shouldReturnStoredId() {
			SysMusculoskeletalSystemExamination examination = new SysMusculoskeletalSystemExamination();
			SysMusculoskeletalSystemExamination saved = new SysMusculoskeletalSystemExamination();
			saved.setID(4L);
			when(sysMusculoskeletalSystemExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveSysMusculoskeletalSystemExamination(examination));
		}

		@Test
		@DisplayName("saveSysMusculoskeletalSystemExamination should return null when nothing was stored")
		void saveMusculoskeletalExamination_shouldReturnNullWhenNothingStored() {
			SysMusculoskeletalSystemExamination examination = new SysMusculoskeletalSystemExamination();
			when(sysMusculoskeletalSystemExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveSysMusculoskeletalSystemExamination(examination));
		}

		@Test
		@DisplayName("saveSysGenitourinarySystemExamination should return the stored row id")
		void saveGenitourinaryExamination_shouldReturnStoredId() {
			SysGenitourinarySystemExamination examination = new SysGenitourinarySystemExamination();
			SysGenitourinarySystemExamination saved = new SysGenitourinarySystemExamination();
			saved.setID(4L);
			when(sysGenitourinarySystemExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveSysGenitourinarySystemExamination(examination));
		}

		@Test
		@DisplayName("saveSysGenitourinarySystemExamination should return null when nothing was stored")
		void saveGenitourinaryExamination_shouldReturnNullWhenNothingStored() {
			SysGenitourinarySystemExamination examination = new SysGenitourinarySystemExamination();
			when(sysGenitourinarySystemExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveSysGenitourinarySystemExamination(examination));
		}
	}

	@Nested
	@DisplayName("history report fetches")
	class HistoryFetchTests {

		/** A single result row whose every column is empty, as an unfilled visit returns. */
		private ArrayList<Object[]> oneEmptyRow() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[40]);
			return rows;
		}

		@Test
		@DisplayName("fetchBenPastMedicalHistory should render the stored rows with the report columns")
		void fetchBenPastMedicalHistory_shouldRenderRowsWithColumns() {
			when(benMedHistoryRepo.getBenPastHistory(BEN_REG_ID)).thenReturn(oneEmptyRow());

			String result = service.fetchBenPastMedicalHistory(BEN_REG_ID);

			assertTrue(result.contains("Illness Type"));
			assertTrue(result.contains("\"data\""));
		}

		@Test
		@DisplayName("fetchBenPastMedicalHistory should render only the columns when nothing is stored")
		void fetchBenPastMedicalHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benMedHistoryRepo.getBenPastHistory(BEN_REG_ID)).thenReturn(new ArrayList<>());

			assertTrue(service.fetchBenPastMedicalHistory(BEN_REG_ID).contains("Year of Surgery"));
		}

		@Test
		@DisplayName("fetchBenPersonalTobaccoHistory should render the stored rows with the report columns")
		void fetchBenPersonalTobaccoHistory_shouldRenderRowsWithColumns() {
			when(benPersonalHabitRepo.getBenPersonalTobaccoHabitDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPersonalTobaccoHistory(BEN_REG_ID).contains("Tobacco Use Type"));
		}

		@Test
		@DisplayName("fetchBenPersonalTobaccoHistory should render only the columns when nothing is stored")
		void fetchBenPersonalTobaccoHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benPersonalHabitRepo.getBenPersonalTobaccoHabitDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPersonalTobaccoHistory(BEN_REG_ID).contains("Tobacco Use Status"));
		}

		@Test
		@DisplayName("fetchBenPersonalAlcoholHistory should render the stored rows with the report columns")
		void fetchBenPersonalAlcoholHistory_shouldRenderRowsWithColumns() {
			when(benPersonalHabitRepo.getBenPersonalAlcoholHabitDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPersonalAlcoholHistory(BEN_REG_ID).contains("Alcohol Type"));
		}

		@Test
		@DisplayName("fetchBenPersonalAlcoholHistory should render only the columns when nothing is stored")
		void fetchBenPersonalAlcoholHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benPersonalHabitRepo.getBenPersonalAlcoholHabitDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPersonalAlcoholHistory(BEN_REG_ID).contains("Alcohol Intake Status"));
		}

		@Test
		@DisplayName("fetchBenPersonalAllergyHistory should render the stored rows with the report columns")
		void fetchBenPersonalAllergyHistory_shouldRenderRowsWithColumns() {
			when(benAllergyHistoryRepo.getBenPersonalAllergyDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPersonalAllergyHistory(BEN_REG_ID).contains("Allergy Name"));
		}

		@Test
		@DisplayName("fetchBenPersonalAllergyHistory should render only the columns when nothing is stored")
		void fetchBenPersonalAllergyHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benAllergyHistoryRepo.getBenPersonalAllergyDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPersonalAllergyHistory(BEN_REG_ID).contains("Allergy Status"));
		}

		@Test
		@DisplayName("fetchBenPersonalMedicationHistory should render the stored rows with the report columns")
		void fetchBenPersonalMedicationHistory_shouldRenderRowsWithColumns() {
			when(benMedicationHistoryRepo.getBenMedicationHistoryDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPersonalMedicationHistory(BEN_REG_ID).contains("Current Medication"));
		}

		@Test
		@DisplayName("fetchBenPersonalMedicationHistory should render only the columns when nothing is stored")
		void fetchBenPersonalMedicationHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benMedicationHistoryRepo.getBenMedicationHistoryDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPersonalMedicationHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenPersonalFamilyHistory should render the stored rows with the report columns")
		void fetchBenPersonalFamilyHistory_shouldRenderRowsWithColumns() {
			when(benFamilyHistoryRepo.getBenFamilyHistoryDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPersonalFamilyHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenPersonalFamilyHistory should render only the columns when nothing is stored")
		void fetchBenPersonalFamilyHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benFamilyHistoryRepo.getBenFamilyHistoryDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPersonalFamilyHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenPhysicalHistory should render the stored rows with the report columns")
		void fetchBenPhysicalHistory_shouldRenderRowsWithColumns() {
			when(physicalActivityTypeRepo.getBenPhysicalHistoryDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPhysicalHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenPhysicalHistory should render only the columns when nothing is stored")
		void fetchBenPhysicalHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(physicalActivityTypeRepo.getBenPhysicalHistoryDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPhysicalHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenMenstrualHistory should render the stored rows with the report columns")
		void fetchBenMenstrualHistory_shouldRenderRowsWithColumns() {
			when(benMenstrualDetailsRepo.getBenMenstrualDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenMenstrualHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenMenstrualHistory should render only the columns when nothing is stored")
		void fetchBenMenstrualHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benMenstrualDetailsRepo.getBenMenstrualDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenMenstrualHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenPastObstetricHistory should render the stored rows with the report columns")
		void fetchBenPastObstetricHistory_shouldRenderRowsWithColumns() {
			when(femaleObstetricHistoryRepo.getBenFemaleObstetricHistoryDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPastObstetricHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenPastObstetricHistory should render only the columns when nothing is stored")
		void fetchBenPastObstetricHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(femaleObstetricHistoryRepo.getBenFemaleObstetricHistoryDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPastObstetricHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenComorbidityHistory should render the stored rows with the report columns")
		void fetchBenComorbidityHistory_shouldRenderRowsWithColumns() {
			when(bencomrbidityCondRepo.getBencomrbidityCondDetails(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenComorbidityHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenComorbidityHistory should render only the columns when nothing is stored")
		void fetchBenComorbidityHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(bencomrbidityCondRepo.getBencomrbidityCondDetails(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenComorbidityHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenImmunizationHistory should render the stored rows with the report columns")
		void fetchBenImmunizationHistory_shouldRenderRowsWithColumns() {
			when(childVaccineDetail1Repo.getBenChildVaccineDetails(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenImmunizationHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenImmunizationHistory should render only the columns when nothing is stored")
		void fetchBenImmunizationHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(childVaccineDetail1Repo.getBenChildVaccineDetails(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenImmunizationHistory(BEN_REG_ID).contains("Date of Capture"));
		}

		@Test
		@DisplayName("fetchBenOptionalVaccineHistory should render the stored rows with the report columns")
		void fetchBenOptionalVaccineHistory_shouldRenderRowsWithColumns() {
			when(childOptionalVaccineDetailRepo.getBenOptionalVaccineDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenOptionalVaccineHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenOptionalVaccineHistory should render only the columns when nothing is stored")
		void fetchBenOptionalVaccineHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(childOptionalVaccineDetailRepo.getBenOptionalVaccineDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenOptionalVaccineHistory(BEN_REG_ID).contains("Date of Capture"));
		}
	}

	@Nested
	@DisplayName("per visit history lookups")
	class PerVisitLookupTests {

		private ArrayList<Object[]> oneEmptyRow() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[60]);
			return rows;
		}

		@Test
		@DisplayName("getBenChiefComplaints should render the stored complaints as JSON")
		void getBenChiefComplaints_shouldRenderJson() {
			when(benChiefComplaintRepo.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getBenChiefComplaints(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getFamilyHistoryDetail should return the stored family history for the visit")
		void getFamilyHistoryDetail_shouldReturnStoredHistory() {
			when(benFamilyHistoryRepo.getBenFamilyHisDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getFamilyHistoryDetail(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getPhysicalActivityType should delegate to the physical activity repository")
		void getPhysicalActivityType_shouldDelegateToRepo() {
			PhysicalActivityType stored = new PhysicalActivityType();
			when(physicalActivityTypeRepo.getBenPhysicalHistoryDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			assertEquals(stored, service.getPhysicalActivityType(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBeneficiaryIdrsDetails should return the stored IDRS row for the visit")
		void getBeneficiaryIdrsDetails_shouldReturnStoredRow() {
			when(iDRSDataRepo.getBenIdrsDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getBeneficiaryIdrsDetails(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getPastHistoryData should return the stored past history for the visit")
		void getPastHistoryData_shouldReturnStoredHistory() {
			when(benMedHistoryRepo.getBenPastHistory(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getPastHistoryData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getComorbidityConditionsHistory should return the stored comorbid conditions for the visit")
		void getComorbidityConditionsHistory_shouldReturnStoredConditions() {
			when(bencomrbidityCondRepo.getBencomrbidityCondDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getComorbidityConditionsHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getMedicationHistory should return the stored medication history for the visit")
		void getMedicationHistory_shouldReturnStoredHistory() {
			when(benMedicationHistoryRepo.getBenMedicationHistoryDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(oneEmptyRow());

			assertNotNull(service.getMedicationHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getPersonalHistory should merge the stored habits with the allergy history")
		void getPersonalHistory_shouldMergeHabitsAndAllergies() {
			when(benPersonalHabitRepo.getBenPersonalHabitDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());
			when(benAllergyHistoryRepo.getBenPersonalAllergyDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getPersonalHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getPersonalHistory should build an empty habit record when nothing is stored")
		void getPersonalHistory_shouldBuildEmptyRecordWhenNothingStored() {
			when(benPersonalHabitRepo.getBenPersonalHabitDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());
			when(benAllergyHistoryRepo.getBenPersonalAllergyDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getPersonalHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getFamilyHistory should return the stored family history for the visit")
		void getFamilyHistory_shouldReturnStoredHistory() {
			when(benFamilyHistoryRepo.getBenFamilyHistoryDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getFamilyHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getMenstrualHistory should return the stored menstrual history for the visit")
		void getMenstrualHistory_shouldReturnStoredHistory() {
			when(benMenstrualDetailsRepo.getBenMenstrualDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getMenstrualHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getFemaleObstetricHistory should return the stored obstetric history for the visit")
		void getFemaleObstetricHistory_shouldReturnStoredHistory() {
			when(femaleObstetricHistoryRepo.getBenFemaleObstetricHistoryDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(oneEmptyRow());

			assertNotNull(service.getFemaleObstetricHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getChildOptionalVaccineHistory should return the stored optional vaccines for the visit")
		void getChildOptionalVaccineHistory_shouldReturnStoredVaccines() {
			when(childOptionalVaccineDetailRepo.getBenOptionalVaccineDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(oneEmptyRow());

			assertNotNull(service.getChildOptionalVaccineHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getImmunizationHistory should return the stored immunisations for the visit")
		void getImmunizationHistory_shouldReturnStoredImmunisations() {
			when(childVaccineDetail1Repo.getBenChildVaccineDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getImmunizationHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getGeneralExaminationData should expand the comma separated danger signs")
		void getGeneralExaminationData_shouldExpandDangerSigns() {
			PhyGeneralExamination stored = new PhyGeneralExamination();
			stored.setTypeOfDangerSign("Fever,Bleeding");
			when(phyGeneralExaminationRepo.getPhyGeneralExaminationData(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			PhyGeneralExamination result = service.getGeneralExaminationData(BEN_REG_ID, VISIT_CODE);

			assertNotNull(result.getTypeOfDangerSigns());
			assertEquals(2, result.getTypeOfDangerSigns().size());
		}

		@Test
		@DisplayName("getGeneralExaminationData should leave the danger signs alone when none are stored")
		void getGeneralExaminationData_shouldLeaveDangerSignsAloneWhenNoneStored() {
			PhyGeneralExamination stored = new PhyGeneralExamination();
			when(phyGeneralExaminationRepo.getPhyGeneralExaminationData(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			assertNotNull(service.getGeneralExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getGeneralExaminationData should return null when the visit has no general examination")
		void getGeneralExaminationData_shouldReturnNullWithoutExamination() {
			when(phyGeneralExaminationRepo.getPhyGeneralExaminationData(BEN_REG_ID, VISIT_CODE)).thenReturn(null);

			assertNull(service.getGeneralExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getHeadToToeExaminationData should delegate to the head to toe repository")
		void getHeadToToeExaminationData_shouldDelegateToRepo() {
			PhyHeadToToeExamination stored = new PhyHeadToToeExamination();
			when(phyHeadToToeExaminationRepo.getPhyHeadToToeExaminationData(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			assertEquals(stored, service.getHeadToToeExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getSysGastrointestinalExamination should delegate to the gastrointestinal repository")
		void getSysGastrointestinalExamination_shouldDelegateToRepo() {
			SysGastrointestinalExamination stored = new SysGastrointestinalExamination();
			when(sysGastrointestinalExaminationRepo.getSSysGastrointestinalExamination(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getSysGastrointestinalExamination(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getCardiovascularExamination should delegate to the cardiovascular repository")
		void getCardiovascularExamination_shouldDelegateToRepo() {
			SysCardiovascularExamination stored = new SysCardiovascularExamination();
			when(sysCardiovascularExaminationRepo.getSysCardiovascularExaminationData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getCardiovascularExamination(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getRespiratoryExamination should delegate to the respiratory repository")
		void getRespiratoryExamination_shouldDelegateToRepo() {
			SysRespiratoryExamination stored = new SysRespiratoryExamination();
			when(sysRespiratoryExaminationRepo.getSysRespiratoryExaminationData(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			assertEquals(stored, service.getRespiratoryExamination(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getSysCentralNervousExamination should delegate to the central nervous repository")
		void getSysCentralNervousExamination_shouldDelegateToRepo() {
			SysCentralNervousExamination stored = new SysCentralNervousExamination();
			when(sysCentralNervousExaminationRepo.getSysCentralNervousExaminationData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getSysCentralNervousExamination(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getMusculoskeletalExamination should delegate to the musculoskeletal repository")
		void getMusculoskeletalExamination_shouldDelegateToRepo() {
			SysMusculoskeletalSystemExamination stored = new SysMusculoskeletalSystemExamination();
			when(sysMusculoskeletalSystemExaminationRepo.getSysMusculoskeletalSystemExamination(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getMusculoskeletalExamination(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getGenitourinaryExamination should delegate to the genitourinary repository")
		void getGenitourinaryExamination_shouldDelegateToRepo() {
			SysGenitourinarySystemExamination stored = new SysGenitourinarySystemExamination();
			when(sysGenitourinarySystemExaminationRepo.getSysGenitourinarySystemExaminationData(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getGenitourinaryExamination(BEN_REG_ID, VISIT_CODE));
		}
	}

	@Nested
	@DisplayName("history updates")
	class HistoryUpdateTests {

		/** One already-processed row and one fresh row, as a re-edited visit returns. */
		private ArrayList<Object[]> statuses() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[] { 1L, "P" });
			rows.add(new Object[] { 2L, "N" });
			return rows;
		}

		private ArrayList<Object[]> integerKeyedStatuses() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[] { 1, "P" });
			rows.add(new Object[] { 2, "N" });
			return rows;
		}

		@Test
		@DisplayName("updateBenChiefComplaints should replace the stored complaints and report the row count")
		void updateBenChiefComplaints_shouldReplaceStoredComplaints() {
			BenChiefComplaint complaint = new BenChiefComplaint();
			complaint.setBeneficiaryRegID(BEN_REG_ID);
			complaint.setVisitCode(VISIT_CODE);
			List<BenChiefComplaint> complaints = Collections.singletonList(complaint);
			when(benChiefComplaintRepo.saveAll(complaints)).thenReturn(complaints);

			assertEquals(1, service.updateBenChiefComplaints(complaints));
			verify(benChiefComplaintRepo).deleteExistingBenChiefComplaints(BEN_REG_ID, VISIT_CODE);
		}

		@Test
		@DisplayName("updateBenChiefComplaints should report no change for an empty complaint list")
		void updateBenChiefComplaints_shouldReportNoChangeForEmptyList() {
			assertEquals(0, service.updateBenChiefComplaints(Collections.emptyList()));
		}

		@Test
		@DisplayName("updateBenChiefComplaints should report no change for a null complaint list")
		void updateBenChiefComplaints_shouldReportNoChangeForNullList() {
			assertEquals(0, service.updateBenChiefComplaints(null));
		}

		@Test
		@DisplayName("updateBenPastHistoryDetails should soft delete the stored rows before writing the new ones")
		void updateBenPastHistoryDetails_shouldSoftDeleteBeforeWriting() throws Exception {
			BenMedHistory history = new BenMedHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			ArrayList<Map<String, Object>> illnesses = new ArrayList<>();
			Map<String, Object> illness = new HashMap<>();
			illness.put("illnessType", "Asthma");
			illnesses.add(illness);
			history.setPastIllness(illnesses);
			history.setPastSurgery(new ArrayList<>());
			when(benMedHistoryRepo.getBenMedHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(statuses());
			when(benMedHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateBenPastHistoryDetails(history));
			verify(benMedHistoryRepo).deleteExistingBenMedHistory(1L, "U");
			verify(benMedHistoryRepo).deleteExistingBenMedHistory(2L, "N");
		}

		@Test
		@DisplayName("updateBenPastHistoryDetails should report no change for a null payload")
		void updateBenPastHistoryDetails_shouldReportNoChangeForNullPayload() throws Exception {
			assertEquals(0, service.updateBenPastHistoryDetails(null));
		}

		@Test
		@DisplayName("updateBenComorbidConditions should soft delete the stored rows before writing the new ones")
		void updateBenComorbidConditions_shouldSoftDeleteBeforeWriting() {
			WrapperComorbidCondDetails wrapper = new WrapperComorbidCondDetails();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitCode(VISIT_CODE);
			BencomrbidityCondDetails condition = new BencomrbidityCondDetails();
			condition.setComorbidCondition("Diabetes");
			wrapper.setComorbidityConcurrentConditionsList(
					new ArrayList<>(Collections.singletonList(condition)));
			when(bencomrbidityCondRepo.getBenComrbidityCondHistoryStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());
			when(bencomrbidityCondRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateBenComorbidConditions(wrapper));
			verify(bencomrbidityCondRepo).deleteExistingBenComrbidityCondDetails(1L, "U");
			verify(bencomrbidityCondRepo).deleteExistingBenComrbidityCondDetails(2L, "N");
		}

		@Test
		@DisplayName("updateBenComorbidConditions should report no change for a null payload")
		void updateBenComorbidConditions_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateBenComorbidConditions(null));
		}

		@Test
		@DisplayName("updateBenMedicationHistory should soft delete the stored rows before writing the new ones")
		void updateBenMedicationHistory_shouldSoftDeleteBeforeWriting() {
			WrapperMedicationHistory wrapper = new WrapperMedicationHistory();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitCode(VISIT_CODE);
			BenMedicationHistory entry = new BenMedicationHistory();
			entry.setCurrentMedication("Metformin");
			wrapper.setMedicationHistoryList(new ArrayList<>(Collections.singletonList(entry)));
			when(benMedicationHistoryRepo.getBenMedicationHistoryStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());
			when(benMedicationHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateBenMedicationHistory(wrapper));
		}

		@Test
		@DisplayName("updateBenMedicationHistory should report no change for a null payload")
		void updateBenMedicationHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateBenMedicationHistory(null));
		}

		@Test
		@DisplayName("updateBenPersonalHistory should soft delete the stored rows before writing the new ones")
		void updateBenPersonalHistory_shouldSoftDeleteBeforeWriting() {
			BenPersonalHabit habit = new BenPersonalHabit();
			habit.setBeneficiaryRegID(BEN_REG_ID);
			habit.setVisitCode(VISIT_CODE);
			habit.setTobaccoList(new ArrayList<>());
			habit.setAlcoholList(new ArrayList<>());
			habit.setAllergicList(new ArrayList<>());
			when(benPersonalHabitRepo.getBenPersonalHistoryStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(integerKeyedStatuses());

			assertEquals(1, service.updateBenPersonalHistory(habit));
			verify(benPersonalHabitRepo).deleteExistingBenPersonalHistory(1, "U");
			verify(benPersonalHabitRepo).deleteExistingBenPersonalHistory(2, "N");
		}

		@Test
		@DisplayName("updateBenPersonalHistory should report no change for a null payload")
		void updateBenPersonalHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateBenPersonalHistory(null));
		}

		@Test
		@DisplayName("updateBenAllergicHistory should soft delete the stored rows before writing the new ones")
		void updateBenAllergicHistory_shouldSoftDeleteBeforeWriting() {
			BenAllergyHistory allergy = new BenAllergyHistory();
			allergy.setBeneficiaryRegID(BEN_REG_ID);
			allergy.setVisitCode(VISIT_CODE);
			allergy.setAllergicList(new ArrayList<>());
			when(benAllergyHistoryRepo.getBenAllergyHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(statuses());

			assertEquals(1, service.updateBenAllergicHistory(allergy));
			verify(benAllergyHistoryRepo).deleteExistingBenAllergyHistory(1L, "U");
		}

		@Test
		@DisplayName("updateBenAllergicHistory should report no change for a null payload")
		void updateBenAllergicHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateBenAllergicHistory(null));
		}

		@Test
		@DisplayName("updateBenFamilyHistory should soft delete the stored rows before writing the new ones")
		void updateBenFamilyHistory_shouldSoftDeleteBeforeWriting() {
			BenFamilyHistory history = new BenFamilyHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			history.setFamilyDiseaseList(new ArrayList<>());
			when(benFamilyHistoryRepo.getBenFamilyHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(statuses());

			assertEquals(1, service.updateBenFamilyHistory(history));
			verify(benFamilyHistoryRepo).deleteExistingBenFamilyHistory(1L, "U");
		}

		@Test
		@DisplayName("updateBenFamilyHistory should report no change for a null payload")
		void updateBenFamilyHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateBenFamilyHistory(null));
		}

		@Test
		@DisplayName("updateMenstrualHistory should update the stored row when one already exists")
		void updateMenstrualHistory_shouldUpdateExistingRow() {
			BenMenstrualDetails details = new BenMenstrualDetails();
			details.setBeneficiaryRegID(BEN_REG_ID);
			details.setVisitCode(VISIT_CODE);
			ArrayList<Map<String, Object>> problems = new ArrayList<>();
			Map<String, Object> problem = new HashMap<>();
			problem.put("menstrualProblemID", 1);
			problem.put("problemName", "Cramps");
			problems.add(problem);
			details.setMenstrualProblemList(problems);
			when(benMenstrualDetailsRepo.getBenMenstrualDetailStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateMenstrualHistory(details));
			assertEquals("1", details.getMenstrualProblemID());
		}

		@Test
		@DisplayName("updateMenstrualHistory should insert a new row when the visit has none")
		void updateMenstrualHistory_shouldInsertNewRow() {
			BenMenstrualDetails details = new BenMenstrualDetails();
			details.setBeneficiaryRegID(BEN_REG_ID);
			details.setVisitCode(VISIT_CODE);
			details.setModifiedBy("nurse1");
			when(benMenstrualDetailsRepo.getBenMenstrualDetailStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(null);
			BenMenstrualDetails saved = new BenMenstrualDetails();
			saved.setBenMenstrualID(9);
			when(benMenstrualDetailsRepo.save(details)).thenReturn(saved);

			assertEquals(1, service.updateMenstrualHistory(details));
			assertEquals("nurse1", details.getCreatedBy());
		}

		@Test
		@DisplayName("updateMenstrualHistory should report no change for a null payload")
		void updateMenstrualHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateMenstrualHistory(null));
		}

		@Test
		@DisplayName("updatePastObstetricHistory should soft delete the stored rows before writing the new ones")
		void updatePastObstetricHistory_shouldSoftDeleteBeforeWriting() {
			WrapperFemaleObstetricHistory wrapper = new WrapperFemaleObstetricHistory();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitCode(VISIT_CODE);
			wrapper.setFemaleObstetricHistoryList(new ArrayList<>());
			when(femaleObstetricHistoryRepo.getBenObstetricHistoryStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());

			assertEquals(1, service.updatePastObstetricHistory(wrapper));
			verify(femaleObstetricHistoryRepo).deleteExistingObstetricHistory(1L, "U");
		}

		@Test
		@DisplayName("updatePastObstetricHistory should report no change for a null payload")
		void updatePastObstetricHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updatePastObstetricHistory(null));
		}

		@Test
		@DisplayName("updateChildOptionalVaccineDetail should soft delete the stored rows before writing the new ones")
		void updateChildOptionalVaccineDetail_shouldSoftDeleteBeforeWriting() {
			WrapperChildOptionalVaccineDetail wrapper = new WrapperChildOptionalVaccineDetail();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitCode(VISIT_CODE);
			wrapper.setChildOptionalVaccineList(new ArrayList<>());
			when(childOptionalVaccineDetailRepo.getBenChildOptionalVaccineHistoryStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());

			assertEquals(1, service.updateChildOptionalVaccineDetail(wrapper));
		}

		@Test
		@DisplayName("updateChildOptionalVaccineDetail should report no change for a null payload")
		void updateChildOptionalVaccineDetail_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateChildOptionalVaccineDetail(null));
		}

		@Test
		@DisplayName("updateChildImmunizationDetail should report no change for an empty immunisation list")
		void updateChildImmunizationDetail_shouldReportNoChangeForEmptyList() {
			WrapperImmunizationHistory wrapper = new WrapperImmunizationHistory();
			wrapper.setImmunizationList(new ArrayList<>());

			assertEquals(0, service.updateChildImmunizationDetail(wrapper));
		}

		@Test
		@DisplayName("updatePhyGeneralExamination should mark an already processed row as updated")
		void updatePhyGeneralExamination_shouldMarkProcessedRowAsUpdated() {
			PhyGeneralExamination examination = new PhyGeneralExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			examination.setTypeOfDangerSigns(new ArrayList<>(Arrays.asList("Fever")));
			when(phyGeneralExaminationRepo.getBenGeneralExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updatePhyGeneralExamination(examination));
			assertEquals("Fever,", examination.getTypeOfDangerSign());
		}

		@Test
		@DisplayName("updatePhyGeneralExamination should report no change for a null payload")
		void updatePhyGeneralExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updatePhyGeneralExamination(null));
		}

		@Test
		@DisplayName("updatePhyHeadToToeExamination should read the processed flag before updating")
		void updatePhyHeadToToeExamination_shouldReadProcessedFlag() {
			PhyHeadToToeExamination examination = new PhyHeadToToeExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(phyHeadToToeExaminationRepo.getBenHeadToToeExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("N");

			assertEquals(0, service.updatePhyHeadToToeExamination(examination));
		}

		@Test
		@DisplayName("updatePhyHeadToToeExamination should report no change for a null payload")
		void updatePhyHeadToToeExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updatePhyHeadToToeExamination(null));
		}

		@Test
		@DisplayName("updateSysCardiovascularExamination should read the processed flag before updating")
		void updateSysCardiovascularExamination_shouldReadProcessedFlag() {
			SysCardiovascularExamination examination = new SysCardiovascularExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(sysCardiovascularExaminationRepo.getBenCardiovascularExaminationStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn("P");

			assertEquals(0, service.updateSysCardiovascularExamination(examination));
		}

		@Test
		@DisplayName("updateSysCardiovascularExamination should report no change for a null payload")
		void updateSysCardiovascularExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateSysCardiovascularExamination(null));
		}

		@Test
		@DisplayName("updateSysRespiratoryExamination should report no change for a null payload")
		void updateSysRespiratoryExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateSysRespiratoryExamination(null));
		}

		@Test
		@DisplayName("updateSysCentralNervousExamination should read the processed flag before updating")
		void updateSysCentralNervousExamination_shouldReadProcessedFlag() {
			SysCentralNervousExamination examination = new SysCentralNervousExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(sysCentralNervousExaminationRepo.getBenCentralNervousExaminationStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn("P");

			assertEquals(0, service.updateSysCentralNervousExamination(examination));
		}

		@Test
		@DisplayName("updateSysCentralNervousExamination should report no change for a null payload")
		void updateSysCentralNervousExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateSysCentralNervousExamination(null));
		}

		@Test
		@DisplayName("updateSysMusculoskeletalSystemExamination should read the processed flag before updating")
		void updateSysMusculoskeletalSystemExamination_shouldReadProcessedFlag() {
			SysMusculoskeletalSystemExamination examination = new SysMusculoskeletalSystemExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(sysMusculoskeletalSystemExaminationRepo
					.getBenMusculoskeletalSystemExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateSysMusculoskeletalSystemExamination(examination));
		}

		@Test
		@DisplayName("updateSysMusculoskeletalSystemExamination should report no change for a null payload")
		void updateSysMusculoskeletalSystemExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateSysMusculoskeletalSystemExamination(null));
		}

		@Test
		@DisplayName("updateSysGenitourinarySystemExamination should read the processed flag before updating")
		void updateSysGenitourinarySystemExamination_shouldReadProcessedFlag() {
			SysGenitourinarySystemExamination examination = new SysGenitourinarySystemExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(sysGenitourinarySystemExaminationRepo
					.getBenGenitourinarySystemExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateSysGenitourinarySystemExamination(examination));
		}

		@Test
		@DisplayName("updateSysGenitourinarySystemExamination should report no change for a null payload")
		void updateSysGenitourinarySystemExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateSysGenitourinarySystemExamination(null));
		}

		@Test
		@DisplayName("updateSysGastrointestinalExamination should report no change for a null payload")
		void updateSysGastrointestinalExamination_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateSysGastrointestinalExamination(null));
		}
	}

	@Nested
	@DisplayName("prescriptions and prescribed drugs")
	class PrescriptionTests {

		private SCTDescription diagnosis(String term, String conceptId) {
			SCTDescription description = new SCTDescription();
			description.setTerm(term);
			description.setConceptID(conceptId);
			return description;
		}

		private PrescriptionDetail stored(Long prescriptionId) {
			PrescriptionDetail stored = new PrescriptionDetail();
			stored.setPrescriptionID(prescriptionId);
			return stored;
		}

		@Test
		@DisplayName("savePrescriptionDetailsAndGetPrescriptionID should build the prescription and return its id")
		void savePrescriptionDetailsAndGetPrescriptionID_shouldBuildAndReturnId() {
			when(prescriptionDetailRepo.save(any(PrescriptionDetail.class))).thenReturn(stored(5L));

			Long result = service.savePrescriptionDetailsAndGetPrescriptionID(BEN_REG_ID, 3L, 9, "nurse1",
					"X-Ray", VISIT_CODE, 7, 8, "after food",
					new ArrayList<>(Collections.singletonList(diagnosis("Fever", "386661006"))));

			assertEquals(5L, result);

			ArgumentCaptor<PrescriptionDetail> captor = ArgumentCaptor.forClass(PrescriptionDetail.class);
			verify(prescriptionDetailRepo).save(captor.capture());
			assertEquals(BEN_REG_ID, captor.getValue().getBeneficiaryRegID());
			assertEquals("after food", captor.getValue().getInstruction());
			assertEquals("Fever", captor.getValue().getDiagnosisProvided());
			assertEquals("386661006", captor.getValue().getDiagnosisProvided_SCTCode());
		}

		@Test
		@DisplayName("savePrescriptionDetailsAndGetPrescriptionID should omit an absent instruction and diagnosis list")
		void savePrescriptionDetailsAndGetPrescriptionID_shouldOmitAbsentOptionalFields() {
			when(prescriptionDetailRepo.save(any(PrescriptionDetail.class))).thenReturn(stored(5L));

			assertEquals(5L, service.savePrescriptionDetailsAndGetPrescriptionID(BEN_REG_ID, 3L, 9, "nurse1",
					"X-Ray", VISIT_CODE, 7, 8, null, null));
		}

		@Test
		@DisplayName("savePrescriptionCovid should carry the doctor diagnosis onto the prescription")
		void savePrescriptionCovid_shouldCarryDoctorDiagnosis() {
			when(prescriptionDetailRepo.save(any(PrescriptionDetail.class))).thenReturn(stored(5L));

			assertEquals(5L, service.savePrescriptionCovid(BEN_REG_ID, 3L, 9, "nurse1", "X-Ray", VISIT_CODE, 7, 8,
					"after food", "Covid positive"));

			ArgumentCaptor<PrescriptionDetail> captor = ArgumentCaptor.forClass(PrescriptionDetail.class);
			verify(prescriptionDetailRepo).save(captor.capture());
			assertEquals("Covid positive", captor.getValue().getDiagnosisProvided());
		}

		@Test
		@DisplayName("savePrescriptionCovid should omit an absent instruction and diagnosis")
		void savePrescriptionCovid_shouldOmitAbsentOptionalFields() {
			when(prescriptionDetailRepo.save(any(PrescriptionDetail.class))).thenReturn(stored(5L));

			assertEquals(5L, service.savePrescriptionCovid(BEN_REG_ID, 3L, 9, "nurse1", "X-Ray", VISIT_CODE, 7, 8,
					null, null));
		}

		@Test
		@DisplayName("saveBeneficiaryPrescription should map the case sheet onto a prescription and store it")
		void saveBeneficiaryPrescription_shouldMapCaseSheetAndStore() throws Exception {
			when(prescriptionDetailRepo.save(any(PrescriptionDetail.class))).thenReturn(stored(5L));

			com.google.gson.JsonObject caseSheet = new com.google.gson.JsonObject();
			caseSheet.addProperty("beneficiaryRegID", BEN_REG_ID);

			assertEquals(5L, service.saveBeneficiaryPrescription(caseSheet));
		}

		@Test
		@DisplayName("saveBenPrescription should join several provisional diagnoses into one field")
		void saveBenPrescription_shouldJoinSeveralDiagnoses() {
			PrescriptionDetail prescription = new PrescriptionDetail();
			prescription.setProvisionalDiagnosisList(new ArrayList<>(
					Arrays.asList(diagnosis("Fever", "1"), diagnosis("Cough", null), diagnosis(null, "3"))));
			when(prescriptionDetailRepo.save(prescription)).thenReturn(stored(5L));

			assertEquals(5L, service.saveBenPrescription(prescription));
			assertEquals("Fever  ||  Cough", prescription.getDiagnosisProvided());
			assertEquals("1  ||  N/A", prescription.getDiagnosisProvided_SCTCode());
		}

		@Test
		@DisplayName("saveBenPrescription should record N/A when the first diagnosis carries no concept id")
		void saveBenPrescription_shouldRecordNotAvailableForMissingConceptId() {
			PrescriptionDetail prescription = new PrescriptionDetail();
			prescription.setProvisionalDiagnosisList(
					new ArrayList<>(Collections.singletonList(diagnosis("Fever", null))));
			when(prescriptionDetailRepo.save(prescription)).thenReturn(stored(5L));

			assertEquals(5L, service.saveBenPrescription(prescription));
			assertEquals("N/A", prescription.getDiagnosisProvided_SCTCode());
		}

		@Test
		@DisplayName("saveBenPrescription should return null when the stored prescription carries no id")
		void saveBenPrescription_shouldReturnNullWithoutStoredId() {
			PrescriptionDetail prescription = new PrescriptionDetail();
			when(prescriptionDetailRepo.save(prescription)).thenReturn(stored(0L));

			assertNull(service.saveBenPrescription(prescription));
		}

		@Test
		@DisplayName("updatePrescription should mark an already processed prescription as updated")
		void updatePrescription_shouldMarkProcessedPrescriptionAsUpdated() {
			PrescriptionDetail prescription = new PrescriptionDetail();
			prescription.setBeneficiaryRegID(BEN_REG_ID);
			prescription.setVisitCode(VISIT_CODE);
			prescription.setPrescriptionID(5L);
			prescription.setProvisionalDiagnosisList(new ArrayList<>(
					Arrays.asList(diagnosis("Fever", "1"), diagnosis("Cough", null))));
			PrescriptionDetail existing = stored(5L);
			existing.setProcessed("P");
			existing.setInstruction("after food");
			when(prescriptionDetailRepo.getGeneralOPDDiagnosisStatus(BEN_REG_ID, VISIT_CODE, 5L)).thenReturn(existing);
			when(prescriptionDetailRepo.save(prescription)).thenReturn(stored(5L));

			assertEquals(1, service.updatePrescription(prescription));
			assertEquals("U", prescription.getProcessed());
			assertEquals("after food", prescription.getInstruction());
			assertEquals("Fever  ||  Cough", prescription.getDiagnosisProvided());
		}

		@Test
		@DisplayName("updatePrescription should keep a fresh prescription marked as new")
		void updatePrescription_shouldKeepFreshPrescriptionAsNew() {
			PrescriptionDetail prescription = new PrescriptionDetail();
			prescription.setBeneficiaryRegID(BEN_REG_ID);
			prescription.setVisitCode(VISIT_CODE);
			prescription.setPrescriptionID(5L);
			PrescriptionDetail existing = stored(5L);
			existing.setProcessed("N");
			existing.setDiagnosisProvided("Fever");
			when(prescriptionDetailRepo.getGeneralOPDDiagnosisStatus(BEN_REG_ID, VISIT_CODE, 5L)).thenReturn(existing);
			when(prescriptionDetailRepo.save(prescription)).thenReturn(stored(5L));

			assertEquals(1, service.updatePrescription(prescription));
			assertEquals("N", prescription.getProcessed());
			assertEquals("Fever", prescription.getDiagnosisProvided());
		}

		@Test
		@DisplayName("updatePrescription should treat an unknown prescription as new")
		void updatePrescription_shouldTreatUnknownPrescriptionAsNew() {
			PrescriptionDetail prescription = new PrescriptionDetail();
			prescription.setBeneficiaryRegID(BEN_REG_ID);
			prescription.setVisitCode(VISIT_CODE);
			prescription.setPrescriptionID(5L);
			when(prescriptionDetailRepo.getGeneralOPDDiagnosisStatus(BEN_REG_ID, VISIT_CODE, 5L)).thenReturn(null);
			when(prescriptionDetailRepo.save(prescription)).thenReturn(stored(0L));

			assertEquals(0, service.updatePrescription(prescription));
			assertEquals("N", prescription.getProcessed());
		}

		@Test
		@DisplayName("saveBeneficiaryLabTestOrderDetails should succeed when the case sheet orders no test")
		void saveBeneficiaryLabTestOrderDetails_shouldSucceedWithoutOrders() {
			assertEquals(1L, service.saveBeneficiaryLabTestOrderDetails(new com.google.gson.JsonObject(), 5L));
		}

		@Test
		@DisplayName("saveBenPrescribedDrugsList should succeed for an empty drug list")
		void saveBenPrescribedDrugsList_shouldSucceedForEmptyList() {
			assertEquals(1, service.saveBenPrescribedDrugsList(new ArrayList<>()));
		}

		@Test
		@DisplayName("saveBenPrescribedDrugsList should store a drug that needs no quantity calculation")
		void saveBenPrescribedDrugsList_shouldStoreDrugWithoutQuantityCalculation() {
			PrescribedDrugDetail drug = new PrescribedDrugDetail();
			drug.setFormName("Syrup");
			List<PrescribedDrugDetail> drugs = new ArrayList<>(Collections.singletonList(drug));
			when(prescribedDrugDetailRepo.saveAll(drugs)).thenReturn(drugs);

			assertEquals(1, service.saveBenPrescribedDrugsList(drugs));
			assertNull(drug.getQtyPrescribed());
		}

		@Test
		@DisplayName("saveBenPrescribedDrugsList should report a failure when not every drug was stored")
		void saveBenPrescribedDrugsList_shouldReportPartialSaveAsFailure() {
			PrescribedDrugDetail drug = new PrescribedDrugDetail();
			drug.setFormName("Syrup");
			List<PrescribedDrugDetail> drugs = new ArrayList<>(Collections.singletonList(drug));
			when(prescribedDrugDetailRepo.saveAll(drugs)).thenReturn(new ArrayList<>());

			assertEquals(0, service.saveBenPrescribedDrugsList(drugs));
		}

		@org.junit.jupiter.params.ParameterizedTest(name = "{0} {1} {2} for {3} {4} should be {5} units")
		@org.junit.jupiter.params.provider.CsvSource({
				"Tablet, Half Tab, Once Daily(OD), 10, Day(s), 5",
				"Tablet, One Tab, Once Daily(OD) Before Food, 10, Day(s), 10",
				"Tablet, One & Half Tab, Once Daily(OD) After Food, 2, Week(s), 21",
				"Tablet, Two Tabs, Once Daily(OD) At Bedtime, 1, Month(s), 60",
				"Capsule, One Tab, Once Daily(OD), 10, Day(s), 10",
				"Tablet, Half Tab, Twice Daily(BD), 10, Day(s), 10",
				"Tablet, One Tab, Twice Daily(BD) Before Food, 10, Day(s), 20",
				"Tablet, One & Half Tab, Twice Daily(BD) After Food, 10, Day(s), 30",
				"Tablet, Two Tabs, Twice Daily(BD), 10, Day(s), 40",
				"Capsule, One Tab, Twice Daily(BD), 10, Day(s), 20",
				"Tablet, Half Tab, Thrice Daily (TID), 10, Day(s), 15",
				"Tablet, One Tab, Thrice Daily (TID) After Food, 10, Day(s), 30",
				"Tablet, One & Half Tab, Thrice Daily (TID) Before Food, 10, Day(s), 45",
				"Tablet, Two Tabs, Thrice Daily (TID), 10, Day(s), 60",
				"Tablet, Half Tab, Four Times in a Day (QID), 10, Day(s), 20",
				"Tablet, One Tab, Four Times in a Day AF, 10, Day(s), 40",
				"Tablet, One & Half Tab, Four Times in a Day BF, 10, Day(s), 60",
				"Tablet, Two Tabs, Four Times in a Day (QID), 10, Day(s), 80",
				"Tablet, Half Tab, Single Dose, 10, Day(s), 1",
				"Tablet, One Tab, Stat Dose, 10, Day(s), 1",
				"Tablet, Two Tabs, Single Dose After  Food, 10, Day(s), 2",
				"Tablet, One Tab, Once in a Week, 4, Week(s), 4",
				"Tablet, Half Tab, SOS, 10, Day(s), 5",
				"Tablet, One Tab, SOS, 10, Day(s), 10" })
		@DisplayName("saveBenPrescribedDrugsList should derive the dispensed quantity from the dosage")
		void saveBenPrescribedDrugsList_shouldDeriveDispensedQuantity(String form, String dose, String frequency,
				String duration, String unit, int expectedQuantity) {
			PrescribedDrugDetail drug = new PrescribedDrugDetail();
			drug.setFormName(form);
			drug.setDose(dose);
			drug.setFrequency(frequency);
			drug.setDuration(duration);
			drug.setUnit(unit);
			List<PrescribedDrugDetail> drugs = new ArrayList<>(Collections.singletonList(drug));
			when(prescribedDrugDetailRepo.saveAll(drugs)).thenReturn(drugs);

			service.saveBenPrescribedDrugsList(drugs);

			assertEquals(expectedQuantity, drug.getQtyPrescribed());
		}

		@Test
		@DisplayName("saveBenPrescribedDrugsList should leave the quantity at zero when the dosage is incomplete")
		void saveBenPrescribedDrugsList_shouldLeaveQuantityAtZeroForIncompleteDosage() {
			PrescribedDrugDetail drug = new PrescribedDrugDetail();
			drug.setFormName("Tablet");
			List<PrescribedDrugDetail> drugs = new ArrayList<>(Collections.singletonList(drug));
			when(prescribedDrugDetailRepo.saveAll(drugs)).thenReturn(drugs);

			service.saveBenPrescribedDrugsList(drugs);

			assertEquals(0, drug.getQtyPrescribed());
		}
	}

	@Nested
	@DisplayName("investigations, worklists and child histories")
	class WorklistAndChildHistoryTests {

		private ArrayList<Object[]> oneEmptyRow() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[60]);
			return rows;
		}

		@Test
		@DisplayName("saveBenInvestigationDetails should create the prescription then store the ordered tests")
		void saveBenInvestigationDetails_shouldCreatePrescriptionThenStoreTests() {
			WrapperBenInvestigationANC wrapper = new WrapperBenInvestigationANC();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitCode(VISIT_CODE);
			PrescriptionDetail stored = new PrescriptionDetail();
			stored.setPrescriptionID(5L);
			when(prescriptionDetailRepo.save(any(PrescriptionDetail.class))).thenReturn(stored);

			assertEquals(1, service.saveBenInvestigationDetails(wrapper));
			assertEquals(5L, wrapper.getPrescriptionID());
		}

		@Test
		@DisplayName("saveBenInvestigationDetails should report no change for a null payload")
		void saveBenInvestigationDetails_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.saveBenInvestigationDetails(null));
		}

		@Test
		@DisplayName("saveBenInvestigation should stamp the visit details onto every ordered test")
		void saveBenInvestigation_shouldStampVisitDetailsOntoTests() {
			WrapperBenInvestigationANC wrapper = new WrapperBenInvestigationANC();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitCode(VISIT_CODE);
			wrapper.setPrescriptionID(5L);
			com.iemr.tm.data.quickConsultation.LabTestOrderDetail test =
					new com.iemr.tm.data.quickConsultation.LabTestOrderDetail();
			wrapper.setLaboratoryList(new ArrayList<>(Collections.singletonList(test)));
			when(labTestOrderDetailRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1L, service.saveBenInvestigation(wrapper));
			assertEquals(5L, test.getPrescriptionID());
			assertEquals(BEN_REG_ID, test.getBeneficiaryRegID());
		}

		@Test
		@DisplayName("saveBenInvestigation should succeed when no test was ordered")
		void saveBenInvestigation_shouldSucceedWithoutOrderedTests() {
			WrapperBenInvestigationANC wrapper = new WrapperBenInvestigationANC();

			assertEquals(1L, service.saveBenInvestigation(wrapper));
		}

		@Test
		@DisplayName("updateBenVisitStatusFlag should confirm the flag change")
		void updateBenVisitStatusFlag_shouldConfirmFlagChange() {
			when(benVisitDetailRepo.updateBenFlowStatus("C", 3L)).thenReturn(1);

			assertTrue(service.updateBenVisitStatusFlag(3L, "C").contains("Updated Successfully"));
		}

		@Test
		@DisplayName("updateBenStatus should leave the response empty when no row was changed")
		void updateBenStatus_shouldLeaveResponseEmptyWhenNothingChanged() {
			when(benVisitDetailRepo.updateBenFlowStatus("C", 3L)).thenReturn(0);

			assertEquals("{}", service.updateBenStatus(3L, "C"));
		}

		@Test
		@DisplayName("getNurseWorkList should render the registrar worklist")
		void getNurseWorkList_shouldRenderRegistrarWorklist() {
			when(reistrarRepoBenSearch.getNurseWorkList()).thenReturn(new ArrayList<>());

			assertNotNull(service.getNurseWorkList());
		}

		@Test
		@DisplayName("getNurseWorkListNew should render the worklist for the configured lookback window")
		void getNurseWorkListNew_shouldRenderWorklistForConfiguredWindow() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "nurseWL", 10);
			when(beneficiaryFlowStatusRepo.getNurseWorklistNew(eq(9), eq(7), any(java.sql.Timestamp.class)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getNurseWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getNurseWorkListNew should fall back to a seven day window when the setting is out of range")
		void getNurseWorkListNew_shouldFallBackToSevenDayWindow() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "nurseWL", 90);
			when(beneficiaryFlowStatusRepo.getNurseWorklistNew(eq(9), eq(7), any(java.sql.Timestamp.class)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getNurseWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getNurseWorkListTcCurrentDate should render the same day teleconsultation worklist")
		void getNurseWorkListTcCurrentDate_shouldRenderWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "nurseTCWL", 5);
			when(beneficiaryFlowStatusRepo.getNurseWorklistCurrentDate(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getNurseWorkListTcCurrentDate(9, 7));
		}

		@Test
		@DisplayName("getNurseWorkListTcCurrentDate should fall back to a seven day window when unset")
		void getNurseWorkListTcCurrentDate_shouldFallBackToSevenDayWindow() {
			when(beneficiaryFlowStatusRepo.getNurseWorklistCurrentDate(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getNurseWorkListTcCurrentDate(9, 7));
		}

		@Test
		@DisplayName("getNurseWorkListTcFutureDate should render the future teleconsultation worklist")
		void getNurseWorkListTcFutureDate_shouldRenderWorklist() {
			when(beneficiaryFlowStatusRepo.getNurseWorklistFutureDate(9, 7)).thenReturn(new ArrayList<>());

			assertEquals("[]", service.getNurseWorkListTcFutureDate(9, 7));
		}

		@Test
		@DisplayName("getLabWorkListNew should render the laboratory worklist")
		void getLabWorkListNew_shouldRenderWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "labWL", 5);
			when(beneficiaryFlowStatusRepo.getLabWorklistNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getLabWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getLabWorkListNew should fall back to a seven day window when unset")
		void getLabWorkListNew_shouldFallBackToSevenDayWindow() {
			when(beneficiaryFlowStatusRepo.getLabWorklistNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getLabWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getRadiologistWorkListNew should render the radiology worklist")
		void getRadiologistWorkListNew_shouldRenderWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "radioWL", 5);
			when(beneficiaryFlowStatusRepo.getRadiologistWorkListNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getRadiologistWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getRadiologistWorkListNew should fall back to a seven day window when unset")
		void getRadiologistWorkListNew_shouldFallBackToSevenDayWindow() {
			when(beneficiaryFlowStatusRepo.getRadiologistWorkListNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getRadiologistWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getOncologistWorkListNew should render the oncology worklist")
		void getOncologistWorkListNew_shouldRenderWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "oncoWL", 5);
			when(beneficiaryFlowStatusRepo.getOncologistWorkListNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getOncologistWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getOncologistWorkListNew should fall back to a seven day window when unset")
		void getOncologistWorkListNew_shouldFallBackToSevenDayWindow() {
			when(beneficiaryFlowStatusRepo.getOncologistWorkListNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getOncologistWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getPharmaWorkListNew should render the pharmacy worklist")
		void getPharmaWorkListNew_shouldRenderWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "pharmaWL", 5);
			when(beneficiaryFlowStatusRepo.getPharmaWorkListNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getPharmaWorkListNew(9, 7));
		}

		@Test
		@DisplayName("getPharmaWorkListNew should fall back to a seven day window when unset")
		void getPharmaWorkListNew_shouldFallBackToSevenDayWindow() {
			when(beneficiaryFlowStatusRepo.getPharmaWorkListNew(eq(9), any(java.sql.Timestamp.class), eq(7)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getPharmaWorkListNew(9, 7));
		}

		@Test
		@DisplayName("saveBenAdherenceDetails should confirm the stored adherence row")
		void saveBenAdherenceDetails_shouldConfirmStoredRow() {
			BenAdherence adherence = new BenAdherence();
			when(benAdherenceRepo.save(adherence)).thenReturn(adherence);

			assertEquals(1, service.saveBenAdherenceDetails(adherence));
		}

		@Test
		@DisplayName("saveBenAdherenceDetails should report no change when nothing was stored")
		void saveBenAdherenceDetails_shouldReportNoChangeWhenNothingStored() {
			BenAdherence adherence = new BenAdherence();
			when(benAdherenceRepo.save(adherence)).thenReturn(null);

			assertEquals(0, service.saveBenAdherenceDetails(adherence));
		}

		@Test
		@DisplayName("saveChildFeedingHistory should return the stored row id")
		void saveChildFeedingHistory_shouldReturnStoredId() {
			ChildFeedingDetails details = new ChildFeedingDetails();
			ChildFeedingDetails saved = new ChildFeedingDetails();
			saved.setID(4L);
			when(childFeedingDetailsRepo.save(details)).thenReturn(saved);

			assertEquals(4L, service.saveChildFeedingHistory(details));
		}

		@Test
		@DisplayName("saveChildFeedingHistory should return null when nothing was stored")
		void saveChildFeedingHistory_shouldReturnNullWhenNothingStored() {
			ChildFeedingDetails details = new ChildFeedingDetails();
			when(childFeedingDetailsRepo.save(details)).thenReturn(null);

			assertNull(service.saveChildFeedingHistory(details));
		}

		@Test
		@DisplayName("savePerinatalHistory should return the stored row id")
		void savePerinatalHistory_shouldReturnStoredId() {
			PerinatalHistory history = new PerinatalHistory();
			PerinatalHistory saved = new PerinatalHistory();
			saved.setID(4L);
			when(perinatalHistoryRepo.save(history)).thenReturn(saved);

			assertEquals(4L, service.savePerinatalHistory(history));
		}

		@Test
		@DisplayName("savePerinatalHistory should return null when nothing was stored")
		void savePerinatalHistory_shouldReturnNullWhenNothingStored() {
			PerinatalHistory history = new PerinatalHistory();
			when(perinatalHistoryRepo.save(history)).thenReturn(null);

			assertNull(service.savePerinatalHistory(history));
		}

		@Test
		@DisplayName("getBenAdherence should render the stored adherence for the visit")
		void getBenAdherence_shouldRenderStoredAdherence() {
			when(benAdherenceRepo.getBenAdherence(BEN_REG_ID, VISIT_CODE)).thenReturn(oneEmptyRow());

			assertNotNull(service.getBenAdherence(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getLabTestOrders should render the stored lab test orders for the visit")
		void getLabTestOrders_shouldRenderStoredOrders() {
			when(labTestOrderDetailRepo.getLabTestOrderDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			assertNotNull(service.getLabTestOrders(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getPerinatalHistory should return the stored perinatal history for the visit")
		void getPerinatalHistory_shouldReturnStoredHistory() {
			when(perinatalHistoryRepo.getBenPerinatalDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			assertNull(service.getPerinatalHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getFeedingHistory should return the stored feeding history for the visit")
		void getFeedingHistory_shouldReturnStoredHistory() {
			when(childFeedingDetailsRepo.getBenFeedingDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			assertNull(service.getFeedingHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("fetchBenPerinatalHistory should render the stored rows with the report columns")
		void fetchBenPerinatalHistory_shouldRenderRowsWithColumns() {
			when(perinatalHistoryRepo.getBenPerinatalDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenPerinatalHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenPerinatalHistory should render only the columns when nothing is stored")
		void fetchBenPerinatalHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(perinatalHistoryRepo.getBenPerinatalDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenPerinatalHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenFeedingHistory should render the stored rows with the report columns")
		void fetchBenFeedingHistory_shouldRenderRowsWithColumns() {
			when(childFeedingDetailsRepo.getBenFeedingHistoryDetail(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenFeedingHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenFeedingHistory should render only the columns when nothing is stored")
		void fetchBenFeedingHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(childFeedingDetailsRepo.getBenFeedingHistoryDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenFeedingHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("updateChildFeedingHistory should report no change for a null payload")
		void updateChildFeedingHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateChildFeedingHistory(null));
		}

		@Test
		@DisplayName("updatePerinatalHistory should report no change for a null payload")
		void updatePerinatalHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updatePerinatalHistory(null));
		}

		@Test
		@DisplayName("updateChildDevelopmentHistory should report no change for a null payload")
		void updateChildDevelopmentHistory_shouldReportNoChangeForNullPayload() {
			assertEquals(0, service.updateChildDevelopmentHistory(null));
		}
	}

	@Nested
	@DisplayName("trends, screening summaries and BMI status")
	class TrendAndSummaryTests {

		private ArrayList<Object[]> visitRows(String visitCategory) {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[] { 1L, visitCategory, 22L });
			return rows;
		}

		@Test
		@DisplayName("getGraphicalTrendData should return the weight, blood pressure and blood glucose series")
		void getGraphicalTrendData_shouldReturnAllSeries() {
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID))
					.thenReturn(visitRows("General OPD"));
			when(benAnthropometryRepo.getBenAnthropometryDetailForGraphtrends(any())).thenReturn(new ArrayList<>());
			when(benPhysicalVitalRepo.getBenPhysicalVitalDetailForGraphTrends(any())).thenReturn(new ArrayList<>());

			Map<String, Object> result = service.getGraphicalTrendData(BEN_REG_ID, "General OPD");

			assertTrue(result.containsKey("weightList"));
			assertTrue(result.containsKey("bpList"));
			assertTrue(result.containsKey("bgList"));
		}

		@Test
		@DisplayName("getGraphicalTrendData should read the cancer vitals for a cancer screening visit")
		void getGraphicalTrendData_shouldReadCancerVitals() {
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID))
					.thenReturn(visitRows("Cancer Screening"));
			when(benCancerVitalDetailRepo.getBenCancerVitalDetailForGraph(any())).thenReturn(new ArrayList<>());

			assertNotNull(service.getGraphicalTrendData(BEN_REG_ID, "Cancer Screening"));
			verify(benCancerVitalDetailRepo).getBenCancerVitalDetailForGraph(any());
		}

		@Test
		@DisplayName("getGraphicalTrendData should return empty series when the beneficiary has no earlier visit")
		void getGraphicalTrendData_shouldReturnEmptySeriesWithoutVisits() {
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID)).thenReturn(new ArrayList<>());

			assertNotNull(service.getGraphicalTrendData(BEN_REG_ID, "General OPD"));
		}

		@Test
		@DisplayName("updateBenFamilyHistoryNCDScreening should confirm the update when entries were stored")
		void updateBenFamilyHistoryNCDScreening_shouldConfirmUpdate() {
			BenFamilyHistory history = new BenFamilyHistory();
			List<Map<String, Object>> diseases = new ArrayList<>();
			Map<String, Object> disease = new HashMap<>();
			disease.put("diseaseType", "Diabetes");
			disease.put("deleted", "false");
			diseases.add(disease);
			history.setFamilyDiseaseList(diseases);
			when(benFamilyHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateBenFamilyHistoryNCDScreening(history));
		}

		@Test
		@DisplayName("updateBenFamilyHistoryNCDScreening should report no change for an empty history")
		void updateBenFamilyHistoryNCDScreening_shouldReportNoChangeForEmptyHistory() {
			BenFamilyHistory history = new BenFamilyHistory();
			history.setFamilyDiseaseList(new ArrayList<>());

			assertEquals(0, service.updateBenFamilyHistoryNCDScreening(history));
		}

		@Test
		@DisplayName("updateBenPhysicalActivityHistoryNCDScreening should mark an existing row as updated")
		void updatePhysicalActivityNCDScreening_shouldMarkExistingRowAsUpdated() {
			PhysicalActivityType activity = new PhysicalActivityType();
			activity.setID(3L);
			when(physicalActivityTypeRepo.save(activity)).thenReturn(activity);

			assertEquals(1, service.updateBenPhysicalActivityHistoryNCDScreening(activity));
			assertEquals("U", activity.getProcessed());
			assertEquals(Boolean.FALSE, activity.getDeleted());
		}

		@Test
		@DisplayName("updateBenPhysicalActivityHistoryNCDScreening should mark a new row as new")
		void updatePhysicalActivityNCDScreening_shouldMarkNewRowAsNew() {
			PhysicalActivityType activity = new PhysicalActivityType();
			when(physicalActivityTypeRepo.save(activity)).thenReturn(null);

			assertEquals(0, service.updateBenPhysicalActivityHistoryNCDScreening(activity));
			assertEquals("N", activity.getProcessed());
		}

		@Test
		@DisplayName("getBenSymptomaticData should summarise the confirmed and suspected diseases")
		void getBenSymptomaticData_shouldSummariseDiseases() throws Exception {
			when(iDRSDataRepo.getBenIdrsDetailsLast_3_Month(eq(BEN_REG_ID), any(java.sql.Timestamp.class)))
					.thenReturn(new ArrayList<>());
			when(iDRSDataRepo.isDiabeticCheck(BEN_REG_ID)).thenReturn(1);
			when(iDRSDataRepo.isEpilepsyCheck(BEN_REG_ID)).thenReturn(1);
			when(iDRSDataRepo.isDefectiveVisionCheck(BEN_REG_ID)).thenReturn(1);
			when(iDRSDataRepo.isHypertensionCheck(BEN_REG_ID)).thenReturn(1);

			assertNotNull(service.getBenSymptomaticData(BEN_REG_ID));
		}

		@Test
		@DisplayName("getBenSymptomaticData should summarise nothing when no screening was recorded")
		void getBenSymptomaticData_shouldSummariseNothingWithoutScreening() throws Exception {
			when(iDRSDataRepo.getBenIdrsDetailsLast_3_Month(eq(BEN_REG_ID), any(java.sql.Timestamp.class)))
					.thenReturn(new ArrayList<>());

			assertNotNull(service.getBenSymptomaticData(BEN_REG_ID));
		}

		@Test
		@DisplayName("getBenPreviousDiabetesData should render the earlier diabetes screening")
		void getBenPreviousDiabetesData_shouldRenderEarlierScreening() throws Exception {
			when(iDRSDataRepo.getBenPreviousDiabetesDetails(BEN_REG_ID)).thenReturn(new ArrayList<>());

			assertNotNull(service.getBenPreviousDiabetesData(BEN_REG_ID));
		}

		@Test
		@DisplayName("getBenPreviousReferralData should render the earlier referrals")
		void getBenPreviousReferralData_shouldRenderEarlierReferrals() throws Exception {
			assertNotNull(service.getBenPreviousReferralData(BEN_REG_ID));
		}

		@Test
		@DisplayName("getMmuNurseWorkListNew should render the MMU referred worklist")
		void getMmuNurseWorkListNew_shouldRenderWorklist() {
			org.springframework.test.util.ReflectionTestUtils.setField(service, "TMReferredWL", 7);
			when(beneficiaryFlowStatusRepo.getMmuNurseWorklistNew(eq(9), eq(7), any(java.sql.Timestamp.class)))
					.thenReturn(new ArrayList<>());

			assertEquals("[]", service.getMmuNurseWorkListNew(9, 7));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should render the referral data for a referral request")
		void fetchProviderSpecificdata_shouldRenderReferralData() throws Exception {
			when(benReferDetailsRepo.getBenReferDetails(anyLong(), any())).thenReturn(new ArrayList<>());

			String result = service.fetchProviderSpecificdata(
					"{\"benRegID\":11,\"visitCode\":22,\"fetchMMUDataFor\":\"referral\"}");

			assertNotNull(result);
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should render the prescription data for a prescription request")
		void fetchProviderSpecificdata_shouldRenderPrescriptionData() throws Exception {
			when(prescribedDrugDetailRepo.getBenPrescribedDrugDetails(anyLong(), any())).thenReturn(new ArrayList<>());

			assertNotNull(service.fetchProviderSpecificdata(
					"{\"benRegID\":11,\"visitCode\":22,\"fetchMMUDataFor\":\"prescription\"}"));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should reject an unknown data category")
		void fetchProviderSpecificdata_shouldRejectUnknownCategory() throws Exception {
			assertEquals("Invalid master param to fetch data", service.fetchProviderSpecificdata(
					"{\"benRegID\":11,\"visitCode\":22,\"fetchMMUDataFor\":\"unknown\"}"));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should fail for a request without a data category")
		void fetchProviderSpecificdata_shouldFailWithoutCategory() {
			assertThrows(IEMRException.class,
					() -> service.fetchProviderSpecificdata("{\"benRegID\":11,\"visitCode\":22}"));
		}

		@org.junit.jupiter.params.ParameterizedTest(name = "a BMI of {0} should read as {1}")
		@org.junit.jupiter.params.provider.CsvSource({ "16.0, Normal", "14.5, Mild malnourished",
				"12.5, Moderately Malnourished", "11.0, Severely Malnourished", "18.5, Overweight", "20.5, Obese",
				"23.0, Severely Obese" })
		@DisplayName("calculateBMIStatus should classify the BMI against the age and gender reference")
		void calculateBMIStatus_shouldClassifyAgainstReference(double bmi, String expectedStatus) throws Exception {
			com.iemr.tm.data.bmi.BmiCalculation reference = new com.iemr.tm.data.bmi.BmiCalculation();
			reference.setN3SD(12d);
			reference.setN2SD(13d);
			reference.setN1SD(15d);
			reference.setP1SD(18d);
			reference.setP2SD(20d);
			reference.setP3SD(22d);
			when(bmiCalculationRepo.getBMIDetails(30, "Male")).thenReturn(reference);

			String result = service.calculateBMIStatus(
					"{\"yearMonth\":\"2 years and 6 months\",\"gender\":\"Male\",\"bmi\":" + bmi + "}");

			assertTrue(result.contains(expectedStatus));
		}

		@Test
		@DisplayName("calculateBMIStatus should fail when no reference exists for the age and gender")
		void calculateBMIStatus_shouldFailWithoutReference() {
			when(bmiCalculationRepo.getBMIDetails(30, "Male")).thenReturn(null);

			IEMRException thrown = assertThrows(IEMRException.class, () -> service.calculateBMIStatus(
					"{\"yearMonth\":\"2 years and 6 months\",\"gender\":\"Male\",\"bmi\":16.0}"));

			assertTrue(thrown.getMessage().contains("No data found for this category"));
		}

		@Test
		@DisplayName("calculateBMIStatus should report an empty status for a request without a BMI")
		void calculateBMIStatus_shouldReportEmptyStatusWithoutBmi() throws Exception {
			assertTrue(service.calculateBMIStatus("{\"yearMonth\":\"2 years and 6 months\",\"gender\":\"Male\"}")
					.contains("\"bmiStatus\":\"\""));
		}
	}

	@Nested
	@DisplayName("child history reports and graph series")
	class ChildHistoryAndGraphTests {

		private ArrayList<Object[]> oneEmptyRow() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[40]);
			return rows;
		}

		@Test
		@DisplayName("fetchBenDevelopmentHistory should render the stored rows with the report columns")
		void fetchDevelopmentHistory_shouldRenderRowsWithColumns() {
			when(benChildDevelopmentHistoryRepo.getBenDevelopmentHistoryDetail(BEN_REG_ID))
					.thenReturn(oneEmptyRow());

			assertTrue(service.fetchBenDevelopmentHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("fetchBenDevelopmentHistory should render only the columns when nothing is stored")
		void fetchDevelopmentHistory_shouldRenderColumnsOnlyWhenEmpty() {
			when(benChildDevelopmentHistoryRepo.getBenDevelopmentHistoryDetail(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.fetchBenDevelopmentHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("updateChildDevelopmentHistory should update the stored development history")
		void updateDevelopmentHistory_shouldUpdateStoredHistory() {
			BenChildDevelopmentHistory history = new BenChildDevelopmentHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			when(benChildDevelopmentHistoryRepo.getDevelopmentHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateChildDevelopmentHistory(history));
		}

		@Test
		@DisplayName("updateChildDevelopmentHistory should insert a new row when the visit has none")
		void updateDevelopmentHistory_shouldInsertNewRow() {
			BenChildDevelopmentHistory history = new BenChildDevelopmentHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			history.setModifiedBy("nurse1");
			when(benChildDevelopmentHistoryRepo.getDevelopmentHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(null);
			BenChildDevelopmentHistory saved = new BenChildDevelopmentHistory();
			saved.setID(4L);
			when(benChildDevelopmentHistoryRepo.save(any())).thenReturn(saved);

			assertEquals(1, service.updateChildDevelopmentHistory(history));
		}

		@Test
		@DisplayName("updateChildFeedingHistory should update the stored feeding history")
		void updateFeedingHistory_shouldUpdateStoredHistory() {
			ChildFeedingDetails details = new ChildFeedingDetails();
			details.setBeneficiaryRegID(BEN_REG_ID);
			details.setVisitCode(VISIT_CODE);
			when(childFeedingDetailsRepo.getBenChildFeedingDetailStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateChildFeedingHistory(details));
		}

		@Test
		@DisplayName("updateChildFeedingHistory should insert a new row when the visit has none")
		void updateFeedingHistory_shouldInsertNewRow() {
			ChildFeedingDetails details = new ChildFeedingDetails();
			details.setBeneficiaryRegID(BEN_REG_ID);
			details.setVisitCode(VISIT_CODE);
			details.setModifiedBy("nurse1");
			when(childFeedingDetailsRepo.getBenChildFeedingDetailStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(null);
			ChildFeedingDetails saved = new ChildFeedingDetails();
			saved.setID(4L);
			when(childFeedingDetailsRepo.save(any())).thenReturn(saved);

			assertEquals(1, service.updateChildFeedingHistory(details));
		}

		@Test
		@DisplayName("updatePerinatalHistory should update the stored perinatal history")
		void updatePerinatalHistory_shouldUpdateStoredHistory() {
			PerinatalHistory history = new PerinatalHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			when(perinatalHistoryRepo.getPerinatalHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updatePerinatalHistory(history));
		}

		@Test
		@DisplayName("updatePerinatalHistory should insert a new row when the visit has none")
		void updatePerinatalHistory_shouldInsertNewRow() {
			PerinatalHistory history = new PerinatalHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			history.setModifiedBy("nurse1");
			when(perinatalHistoryRepo.getPerinatalHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn(null);
			PerinatalHistory saved = new PerinatalHistory();
			saved.setID(4L);
			when(perinatalHistoryRepo.save(any())).thenReturn(saved);

			assertEquals(1, service.updatePerinatalHistory(history));
		}

		@Test
		@DisplayName("getGraphicalTrendData should build the weight, blood pressure and blood glucose series")
		void getGraphicalTrendData_shouldBuildEverySeries() {
			ArrayList<Object[]> visits = new ArrayList<>();
			visits.add(new Object[] { 1L, "General OPD", 22L });
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID)).thenReturn(visits);

			ArrayList<Object[]> anthro = new ArrayList<>();
			anthro.add(new Object[] { 60d, new java.sql.Timestamp(1_700_000_000_000L) });
			when(benAnthropometryRepo.getBenAnthropometryDetailForGraphtrends(any())).thenReturn(anthro);

			ArrayList<Object[]> vitals = new ArrayList<>();
			vitals.add(new Object[] { (short) 120, (short) 80, 90d, 140d, 120d, new java.sql.Timestamp(
					1_700_000_000_000L) });
			when(benPhysicalVitalRepo.getBenPhysicalVitalDetailForGraphTrends(any())).thenReturn(vitals);

			Map<String, Object> result = service.getGraphicalTrendData(BEN_REG_ID, "General OPD");

			assertNotNull(result.get("weightList"));
			assertNotNull(result.get("bpList"));
			assertNotNull(result.get("bgList"));
		}

		@Test
		@DisplayName("getGraphicalTrendData should build the series from the cancer screening vitals")
		void getGraphicalTrendData_shouldBuildSeriesFromCancerVitals() {
			ArrayList<Object[]> visits = new ArrayList<>();
			visits.add(new Object[] { 1L, "Cancer Screening", 22L });
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID)).thenReturn(visits);
			com.iemr.tm.data.nurse.BenCancerVitalDetail cancerVital =
					new com.iemr.tm.data.nurse.BenCancerVitalDetail();
			cancerVital.setSystolicBP_1stReading((short) 120);
			cancerVital.setDiastolicBP_1stReading((short) 80);
			cancerVital.setWeight_Kg(60d);
			cancerVital.setCreatedDate(new java.sql.Timestamp(1_700_000_000_000L));
			when(benCancerVitalDetailRepo.getBenCancerVitalDetailForGraph(any()))
					.thenReturn(new ArrayList<>(Collections.singletonList(cancerVital)));

			assertNotNull(service.getGraphicalTrendData(BEN_REG_ID, "Cancer Screening"));
		}
	}

	@Nested
	@DisplayName("obstetric, menstrual and trend assembly")
	class HistoryAssemblyTests {

		/**
		 * One obstetric history row as the native query returns it: identifiers,
		 * the pregnancy count and the comma separated complication columns.
		 */
		private Object[] obstetricRow() {
			Object[] values = new Object[38];
			values[0] = 11L;
			values[1] = 3L;
			values[2] = 9;
			values[3] = (short) 1;
			values[4] = (short) 2;
			for (int i = 5; i < 38; i++) {
				values[i] = "1,2";
			}
			values[8] = (short) 1;
			values[10] = (short) 1;
			values[12] = (short) 1;
			values[18] = (short) 1;
			values[23] = (short) 1;
			values[27] = (short) 1;
			values[30] = 5L;
			values[31] = 3;
			values[33] = 3;
			values[34] = 3;
			return values;
		}

		@Test
		@DisplayName("getFemaleObstetricHistory should expand the complications of every pregnancy")
		void getFemaleObstetricHistory_shouldExpandComplications() {
			java.util.ArrayList<Object[]> rows = new java.util.ArrayList<>();
			rows.add(obstetricRow());
			when(femaleObstetricHistoryRepo.getBenFemaleObstetricHistoryDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(rows);

			com.iemr.tm.data.anc.WrapperFemaleObstetricHistory history =
					service.getFemaleObstetricHistory(BEN_REG_ID, VISIT_CODE);

			assertEquals(1, history.getFemaleObstetricHistoryList().size());
			com.iemr.tm.data.anc.FemaleObstetricHistory pregnancy = history.getFemaleObstetricHistoryList().get(0);
			assertNotNull(pregnancy.getPregComplicationList());
			assertNotNull(pregnancy.getDeliveryComplicationList());
			assertNotNull(pregnancy.getPostpartumComplicationList());
			assertNotNull(pregnancy.getAbortionType());
			assertNotNull(pregnancy.getTypeofFacility());
			assertNotNull(pregnancy.getPostAbortionComplication());
		}

		@Test
		@DisplayName("getFemaleObstetricHistory should answer for a beneficiary with no recorded pregnancy")
		void getFemaleObstetricHistory_shouldAnswerWithoutRecordedPregnancy() {
			when(femaleObstetricHistoryRepo.getBenFemaleObstetricHistoryDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new java.util.ArrayList<>());

			assertNotNull(service.getFemaleObstetricHistory(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getMenstrualHistory should expand the recorded menstrual problems")
		void getMenstrualHistory_shouldExpandRecordedProblems() {
			java.util.ArrayList<Object[]> rows = new java.util.ArrayList<>();
			rows.add(new Object[] { 11L, 3L, 9, (short) 1, "1,2", "1,2", (short) 5, "1,2", (short) 28,
					"1,2", "1,2", "1,2", new java.sql.Timestamp(System.currentTimeMillis()), 22L });
			when(benMenstrualDetailsRepo.getBenMenstrualDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(rows);

			com.iemr.tm.data.anc.BenMenstrualDetails details =
					service.getMenstrualHistory(BEN_REG_ID, VISIT_CODE);

			assertNotNull(details);
		}

		@Test
		@DisplayName("getMenstrualHistory should answer for a beneficiary with no recorded menstrual history")
		void getMenstrualHistory_shouldAnswerWithoutRecordedHistory() {
			when(benMenstrualDetailsRepo.getBenMenstrualDetail(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertDoesNotThrow(
					() -> service.getMenstrualHistory(BEN_REG_ID, VISIT_CODE));
		}

		private com.iemr.tm.data.nurse.BenCancerVitalDetail cancerVitals() {
			com.iemr.tm.data.nurse.BenCancerVitalDetail vitals =
					new com.iemr.tm.data.nurse.BenCancerVitalDetail();
			vitals.setWeight_Kg(62.0);
			vitals.setSystolicBP_1stReading((short) 120);
			vitals.setSystolicBP_2ndReading((short) 122);
			vitals.setSystolicBP_3rdReading((short) 118);
			vitals.setDiastolicBP_1stReading((short) 80);
			vitals.setDiastolicBP_2ndReading((short) 82);
			vitals.setDiastolicBP_3rdReading((short) 78);
			vitals.setBloodGlucose_Fasting((short) 90);
			vitals.setBloodGlucose_Random((short) 120);
			vitals.setBloodGlucose_2HrPostPrandial((short) 140);
			vitals.setCreatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
			return vitals;
		}

		@Test
		@DisplayName("getGraphicalTrendData should chart the weight, blood pressure and blood glucose readings")
		void getGraphicalTrendData_shouldChartWeightBpAndGlucose() {
			java.util.ArrayList<Object[]> visits = new java.util.ArrayList<>();
			visits.add(new Object[] { 3L, "Cancer Screening", "22" });
			visits.add(new Object[] { 4L, "ANC", "23" });
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID)).thenReturn(visits);
			when(benCancerVitalDetailRepo.getBenCancerVitalDetailForGraph(org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(
							java.util.Collections.singletonList(cancerVitals())));
			java.util.ArrayList<Object[]> anthro = new java.util.ArrayList<>();
			anthro.add(new Object[] { "60", java.sql.Date.valueOf("2026-08-01") });
			when(benAnthropometryRepo.getBenAnthropometryDetailForGraphtrends(org.mockito.ArgumentMatchers.any()))
					.thenReturn(anthro);
			java.util.ArrayList<Object[]> vital = new java.util.ArrayList<>();
			vital.add(new Object[] { (short) 120, (short) 80, "90", "120", "140",
					java.sql.Date.valueOf("2026-08-01") });
			when(benPhysicalVitalRepo.getBenPhysicalVitalDetailForGraphTrends(org.mockito.ArgumentMatchers.any()))
					.thenReturn(vital);

			java.util.Map<String, Object> trend = service.getGraphicalTrendData(BEN_REG_ID, "ANC");

			assertNotNull(trend);
			assertTrue(trend.containsKey("bpList") || !trend.isEmpty());
		}

		@Test
		@DisplayName("getGraphicalTrendData should answer for a beneficiary with no earlier visit")
		void getGraphicalTrendData_shouldAnswerWithoutEarlierVisit() {
			when(benVisitDetailRepo.getLastSixVisitDetailsForBeneficiary(BEN_REG_ID))
					.thenReturn(new java.util.ArrayList<>());

			assertNotNull(service.getGraphicalTrendData(BEN_REG_ID, "ANC"));
		}

		private com.iemr.tm.data.ncdScreening.IDRSData idrsAnswer(Long visitCode, Integer questionID) {
			com.iemr.tm.data.ncdScreening.IDRSData answer = new com.iemr.tm.data.ncdScreening.IDRSData();
			answer.setVisitCode(visitCode);
			answer.setIdrsQuestionID(questionID);
			answer.setAnswer("Yes");
			answer.setSuspectedDisease("Diabetes");
			answer.setConfirmedDisease("Diabetes");
			return answer;
		}

		@Test
		@DisplayName("getBenSymptomaticData should collect the answers of the most recent screening")
		void getBenSymptomaticData_shouldCollectRecentAnswers() throws Exception {
			when(iDRSDataRepo.getBenIdrsDetailsLast_3_Month(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Arrays.asList(
							idrsAnswer(22L, 1), idrsAnswer(22L, 2), idrsAnswer(23L, 3))));

			String result = service.getBenSymptomaticData(BEN_REG_ID);

			assertNotNull(result);
			assertTrue(result.contains("Diabetes"));
		}

		@Test
		@DisplayName("getBenSymptomaticData should answer for a beneficiary with no recent screening")
		void getBenSymptomaticData_shouldAnswerWithoutRecentScreening() throws Exception {
			when(iDRSDataRepo.getBenIdrsDetailsLast_3_Month(org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.any())).thenReturn(new java.util.ArrayList<>());

			assertNotNull(service.getBenSymptomaticData(BEN_REG_ID));
		}

		@Test
		@DisplayName("getBenPreviousReferralData should render the earlier referrals with the report columns")
		void getBenPreviousReferralData_shouldRenderEarlierReferrals() throws Exception {
			java.util.ArrayList<Object[]> rows = new java.util.ArrayList<>();
			rows.add(new Object[] { java.math.BigInteger.valueOf(22L),
					new java.sql.Timestamp(System.currentTimeMillis()), "Diabetes" });
			when(iDRSDataRepo.getBenPreviousReferredDetails(BEN_REG_ID)).thenReturn(rows);

			String result = service.getBenPreviousReferralData(BEN_REG_ID);

			assertTrue(result.contains("columns"));
			assertTrue(result.contains("Diabetes"));
		}

		@Test
		@DisplayName("getBenPreviousReferralData should render only the columns when there is no earlier referral")
		void getBenPreviousReferralData_shouldRenderColumnsOnly() throws Exception {
			when(iDRSDataRepo.getBenPreviousReferredDetails(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.getBenPreviousReferralData(BEN_REG_ID).contains("columns"));
		}
	}

	@Nested
	@DisplayName("provider specific data")
	class ProviderSpecificDataTests {

		private String request(String fetchFor) {
			return "{\"benRegID\":11,\"visitCode\":22,\"fetchMMUDataFor\":\"" + fetchFor + "\"}";
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should render the prescribed drugs")
		void fetchProviderSpecificdata_shouldRenderPrescribedDrugs() throws Exception {
			when(prescribedDrugDetailRepo.getBenPrescribedDrugDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new java.util.ArrayList<>());

			assertTrue(service.fetchProviderSpecificdata(request("prescription")).contains("columns"));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should render the ordered tests and the RBS test from the vitals")
		void fetchProviderSpecificdata_shouldRenderOrderedTestsAndRbsFromVitals() throws Exception {
			java.util.ArrayList<Object[]> orders = new java.util.ArrayList<>();
			orders.add(new Object[] { 11L, 3L, 9, 4, "CBC", "1,2", 22L });
			when(labTestOrderDetailRepo.getLabTestOrderDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(orders);
			com.iemr.tm.data.nurse.BenPhysicalVitalDetail vitals =
					new com.iemr.tm.data.nurse.BenPhysicalVitalDetail();
			vitals.setRbsTestResult("110");
			when(benPhysicalVitalRepo.getBenPhysicalVitalDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(vitals);

			String result = service.fetchProviderSpecificdata(request("investigation"));

			assertTrue(result.contains("CBC"));
			assertTrue(result.contains("RBS Test"));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should render the RBS test alone when no test was ordered")
		void fetchProviderSpecificdata_shouldRenderRbsTestAlone() throws Exception {
			when(labTestOrderDetailRepo.getLabTestOrderDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new java.util.ArrayList<>());
			com.iemr.tm.data.nurse.BenPhysicalVitalDetail vitals =
					new com.iemr.tm.data.nurse.BenPhysicalVitalDetail();
			vitals.setRbsTestResult("110");
			when(benPhysicalVitalRepo.getBenPhysicalVitalDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(vitals);

			assertTrue(service.fetchProviderSpecificdata(request("investigation")).contains("RBS Test"));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should skip the RBS test when it was already ordered")
		void fetchProviderSpecificdata_shouldSkipAlreadyOrderedRbsTest() throws Exception {
			java.util.ArrayList<Object[]> orders = new java.util.ArrayList<>();
			orders.add(new Object[] { 11L, 3L, 9, 4, "RBS Test", "1,2", 22L });
			when(labTestOrderDetailRepo.getLabTestOrderDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(orders);

			assertTrue(service.fetchProviderSpecificdata(request("investigation")).contains("RBS Test"));
			verify(benPhysicalVitalRepo, never()).getBenPhysicalVitalDetail(BEN_REG_ID, VISIT_CODE);
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should render the referrals")
		void fetchProviderSpecificdata_shouldRenderReferrals() throws Exception {
			when(benReferDetailsRepo.getBenReferDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new java.util.ArrayList<>());

			assertTrue(service.fetchProviderSpecificdata(request("referral")).contains("columns"));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should reject a master it does not know")
		void fetchProviderSpecificdata_shouldRejectUnknownMaster() throws Exception {
			assertEquals("Invalid master param to fetch data", service.fetchProviderSpecificdata(request("unknown")));
		}

		@Test
		@DisplayName("fetchProviderSpecificdata should report a request it cannot act on")
		void fetchProviderSpecificdata_shouldReportRequestItCannotActOn() {
			org.junit.jupiter.api.Assertions.assertThrows(
					com.iemr.tm.utils.exception.IEMRException.class,
					() -> service.fetchProviderSpecificdata("{\"benRegID\":11}"));
		}
	}
}
