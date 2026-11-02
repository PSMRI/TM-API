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
package com.iemr.tm.service.cancerScreening;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.data.nurse.BenCancerVitalDetail;
import com.iemr.tm.data.nurse.BeneficiaryVisitDetail;
import com.iemr.tm.data.doctor.CancerAbdominalExamination;
import com.iemr.tm.data.doctor.CancerBreastExamination;
import com.iemr.tm.data.doctor.CancerExaminationImageAnnotation;
import com.iemr.tm.data.doctor.CancerGynecologicalExamination;
import com.iemr.tm.data.doctor.CancerLymphNodeDetails;
import com.iemr.tm.data.doctor.CancerOralExamination;
import com.iemr.tm.data.doctor.CancerSignAndSymptoms;
import com.iemr.tm.data.doctor.WrapperCancerExamImgAnotasn;
import com.iemr.tm.data.nurse.BenFamilyCancerHistory;
import com.iemr.tm.data.nurse.BenObstetricCancerHistory;
import com.iemr.tm.data.nurse.BenPersonalCancerDietHistory;
import com.iemr.tm.data.nurse.BenPersonalCancerHistory;
import com.iemr.tm.repo.nurse.BenCancerVitalDetailRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.doctor.CancerAbdominalExaminationRepo;
import com.iemr.tm.repo.doctor.CancerBreastExaminationRepo;
import com.iemr.tm.repo.doctor.CancerExaminationImageAnnotationRepo;
import com.iemr.tm.repo.doctor.CancerGynecologicalExaminationRepo;
import com.iemr.tm.repo.doctor.CancerLymphNodeExaminationRepo;
import com.iemr.tm.repo.doctor.CancerOralExaminationRepo;
import com.iemr.tm.repo.doctor.CancerSignAndSymptomsRepo;
import com.iemr.tm.repo.nurse.BenFamilyCancerHistoryRepo;
import com.iemr.tm.repo.nurse.BenObstetricCancerHistoryRepo;
import com.iemr.tm.repo.nurse.BenPersonalCancerDietHistoryRepo;
import com.iemr.tm.repo.nurse.BenPersonalCancerHistoryRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CSNurseServiceImpl Test Suite")
class CSNurseServiceImplTest {

	private static final Long BEN_REG_ID = 11L;
	private static final Long VISIT_ID = 3L;
	private static final Long VISIT_CODE = 22L;

	@Mock
	private BenFamilyCancerHistoryRepo benFamilyCancerHistoryRepo;
	@Mock
	private BenPersonalCancerHistoryRepo benPersonalCancerHistoryRepo;
	@Mock
	private BenPersonalCancerDietHistoryRepo benPersonalCancerDietHistoryRepo;
	@Mock
	private BenObstetricCancerHistoryRepo benObstetricCancerHistoryRepo;
	@Mock
	private BenCancerVitalDetailRepo benCancerVitalDetailRepo;
	@Mock
	private BenVisitDetailRepo benVisitDetailRepo;
	@Mock
	private CancerAbdominalExaminationRepo cancerAbdominalExaminationRepo;
	@Mock
	private CancerBreastExaminationRepo cancerBreastExaminationRepo;
	@Mock
	private CancerGynecologicalExaminationRepo cancerGynecologicalExaminationRepo;
	@Mock
	private CancerSignAndSymptomsRepo cancerSignAndSymptomsRepo;
	@Mock
	private CancerLymphNodeExaminationRepo cancerLymphNodeExaminationRepo;
	@Mock
	private CancerOralExaminationRepo cancerOralExaminationRepo;
	@Mock
	private CancerExaminationImageAnnotationRepo cancerExaminationImageAnnotationRepo;

	@InjectMocks
	private CSNurseServiceImpl service;

	/** One already-processed row and one fresh row, as a re-edited visit returns. */
	private ArrayList<Object[]> statuses() {
		ArrayList<Object[]> rows = new ArrayList<>();
		rows.add(new Object[] { 1L, "P" });
		rows.add(new Object[] { 2L, "N" });
		return rows;
	}

	private static <T> org.mockito.stubbing.Answer<T> echoList() {
		return invocation -> invocation.getArgument(0);
	}

	private BenFamilyCancerHistory familyHistory(List<String> familyMembers) {
		BenFamilyCancerHistory history = new BenFamilyCancerHistory();
		history.setBeneficiaryRegID(BEN_REG_ID);
		history.setVisitCode(VISIT_CODE);
		history.setFamilyMemberList(familyMembers);
		return history;
	}

	@Nested
	@DisplayName("history saves")
	class HistorySaveTests {

		@Test
		@DisplayName("saveBenFamilyCancerHistory should flatten the family member list before storing")
		void saveFamilyHistory_shouldFlattenFamilyMembers() {
			BenFamilyCancerHistory history = familyHistory(Arrays.asList("Mother", "Father"));
			when(benFamilyCancerHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.saveBenFamilyCancerHistory(Collections.singletonList(history)));
			assertEquals("Mother,Father", history.getFamilyMember());
		}

		@Test
		@DisplayName("saveBenFamilyCancerHistory should skip an entry without any family member")
		void saveFamilyHistory_shouldSkipEntryWithoutFamilyMember() {
			when(benFamilyCancerHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.saveBenFamilyCancerHistory(
					Collections.singletonList(familyHistory(Collections.emptyList()))));
		}

		@Test
		@DisplayName("saveBenPersonalCancerHistory should flatten the tobacco product list before storing")
		void savePersonalHistory_shouldFlattenTobaccoProducts() {
			BenPersonalCancerHistory history = new BenPersonalCancerHistory();
			history.setTypeOfTobaccoProductList(Arrays.asList("Cigarette", "Beedi"));
			BenPersonalCancerHistory saved = new BenPersonalCancerHistory();
			saved.setID(4L);
			when(benPersonalCancerHistoryRepo.save(history)).thenReturn(saved);

			assertEquals(4L, service.saveBenPersonalCancerHistory(history));
			assertEquals("Cigarette,Beedi,", history.getTypeOfTobaccoProduct());
		}

		@Test
		@DisplayName("saveBenPersonalCancerHistory should return null when nothing was stored")
		void savePersonalHistory_shouldReturnNullWhenNothingStored() {
			BenPersonalCancerHistory history = new BenPersonalCancerHistory();
			when(benPersonalCancerHistoryRepo.save(history)).thenReturn(null);

			assertNull(service.saveBenPersonalCancerHistory(history));
		}

		@Test
		@DisplayName("saveBenPersonalCancerDietHistory should flatten the oil list before storing")
		void savePersonalDietHistory_shouldFlattenOilList() {
			BenPersonalCancerDietHistory history = new BenPersonalCancerDietHistory();
			history.setTypeOfOilConsumedList(Arrays.asList("Mustard", "Sunflower"));
			BenPersonalCancerDietHistory saved = new BenPersonalCancerDietHistory();
			saved.setID(4L);
			when(benPersonalCancerDietHistoryRepo.save(history)).thenReturn(saved);

			assertEquals(4L, service.saveBenPersonalCancerDietHistory(history));
			assertEquals("Mustard,Sunflower,", history.getTypeOfOilConsumed());
		}

		@Test
		@DisplayName("saveBenPersonalCancerDietHistory should return null when nothing was stored")
		void savePersonalDietHistory_shouldReturnNullWhenNothingStored() {
			BenPersonalCancerDietHistory history = new BenPersonalCancerDietHistory();
			when(benPersonalCancerDietHistoryRepo.save(history)).thenReturn(null);

			assertNull(service.saveBenPersonalCancerDietHistory(history));
		}

		@Test
		@DisplayName("saveBenObstetricCancerHistory should return the stored row id")
		void saveObstetricHistory_shouldReturnStoredId() {
			BenObstetricCancerHistory history = new BenObstetricCancerHistory();
			BenObstetricCancerHistory saved = new BenObstetricCancerHistory();
			saved.setID(4L);
			when(benObstetricCancerHistoryRepo.save(history)).thenReturn(saved);

			assertEquals(4L, service.saveBenObstetricCancerHistory(history));
		}

		@Test
		@DisplayName("saveBenObstetricCancerHistory should return null when nothing was stored")
		void saveObstetricHistory_shouldReturnNullWhenNothingStored() {
			BenObstetricCancerHistory history = new BenObstetricCancerHistory();
			when(benObstetricCancerHistoryRepo.save(history)).thenReturn(null);

			assertNull(service.saveBenObstetricCancerHistory(history));
		}

		@Test
		@DisplayName("saveBenVitalDetail should return the stored row id")
		void saveVitalDetail_shouldReturnStoredId() {
			BenCancerVitalDetail vital = new BenCancerVitalDetail();
			BenCancerVitalDetail saved = new BenCancerVitalDetail();
			saved.setID(4L);
			when(benCancerVitalDetailRepo.save(vital)).thenReturn(saved);

			assertEquals(4L, service.saveBenVitalDetail(vital));
		}

		@Test
		@DisplayName("saveBenVitalDetail should return null when nothing was stored")
		void saveVitalDetail_shouldReturnNullWhenNothingStored() {
			BenCancerVitalDetail vital = new BenCancerVitalDetail();
			when(benCancerVitalDetailRepo.save(vital)).thenReturn(null);

			assertNull(service.saveBenVitalDetail(vital));
		}
	}

	@Nested
	@DisplayName("examination saves")
	class ExaminationSaveTests {

		@Test
		@DisplayName("saveLymphNodeDetails should stamp the visit onto every lymph node row")
		void saveLymphNodeDetails_shouldStampVisitOntoRows() {
			CancerLymphNodeDetails node = new CancerLymphNodeDetails();
			node.setID(4L);
			when(cancerLymphNodeExaminationRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(4L, service.saveLymphNodeDetails(Collections.singletonList(node), VISIT_ID, VISIT_CODE));
			assertEquals(VISIT_ID, node.getBenVisitID());
			assertEquals(VISIT_CODE, node.getVisitCode());
		}

		@Test
		@DisplayName("saveLymphNodeDetails should return null when nothing was stored")
		void saveLymphNodeDetails_shouldReturnNullWhenNothingStored() {
			when(cancerLymphNodeExaminationRepo.saveAll(any())).thenReturn(new ArrayList<>());

			assertNull(service.saveLymphNodeDetails(Collections.singletonList(new CancerLymphNodeDetails()), VISIT_ID,
					VISIT_CODE));
		}

		@Test
		@DisplayName("saveCancerSignAndSymptomsData should stamp the visit before storing")
		void saveSignAndSymptoms_shouldStampVisitBeforeStoring() {
			CancerSignAndSymptoms symptoms = new CancerSignAndSymptoms();
			CancerSignAndSymptoms saved = new CancerSignAndSymptoms();
			saved.setID(4L);
			when(cancerSignAndSymptomsRepo.save(symptoms)).thenReturn(saved);

			assertEquals(4L, service.saveCancerSignAndSymptomsData(symptoms, VISIT_ID, VISIT_CODE));
			assertEquals(VISIT_ID, symptoms.getBenVisitID());
		}

		@Test
		@DisplayName("saveCancerSignAndSymptomsData should return the stored row id")
		void saveSignAndSymptoms_shouldReturnStoredId() {
			CancerSignAndSymptoms symptoms = new CancerSignAndSymptoms();
			CancerSignAndSymptoms saved = new CancerSignAndSymptoms();
			saved.setID(4L);
			when(cancerSignAndSymptomsRepo.save(symptoms)).thenReturn(saved);

			assertEquals(4L, service.saveCancerSignAndSymptomsData(symptoms));
		}

		@Test
		@DisplayName("saveCancerSignAndSymptomsData should return null when nothing was stored")
		void saveSignAndSymptoms_shouldReturnNullWhenNothingStored() {
			CancerSignAndSymptoms symptoms = new CancerSignAndSymptoms();
			when(cancerSignAndSymptomsRepo.save(symptoms)).thenReturn(null);

			assertNull(service.saveCancerSignAndSymptomsData(symptoms));
		}

		@Test
		@DisplayName("saveCancerOralExaminationData should return the stored row id")
		void saveOralExamination_shouldReturnStoredId() {
			CancerOralExamination examination = new CancerOralExamination();
			CancerOralExamination saved = new CancerOralExamination();
			saved.setID(4L);
			when(cancerOralExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveCancerOralExaminationData(examination));
		}

		@Test
		@DisplayName("saveCancerOralExaminationData should return null when nothing was stored")
		void saveOralExamination_shouldReturnNullWhenNothingStored() {
			CancerOralExamination examination = new CancerOralExamination();
			when(cancerOralExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveCancerOralExaminationData(examination));
		}

		@Test
		@DisplayName("saveCancerBreastExaminationData should return the stored row id")
		void saveBreastExamination_shouldReturnStoredId() {
			CancerBreastExamination examination = new CancerBreastExamination();
			CancerBreastExamination saved = new CancerBreastExamination();
			saved.setID(4L);
			when(cancerBreastExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveCancerBreastExaminationData(examination));
		}

		@Test
		@DisplayName("saveCancerBreastExaminationData should return null when nothing was stored")
		void saveBreastExamination_shouldReturnNullWhenNothingStored() {
			CancerBreastExamination examination = new CancerBreastExamination();
			when(cancerBreastExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveCancerBreastExaminationData(examination));
		}

		@Test
		@DisplayName("saveCancerAbdominalExaminationData should return the stored row id")
		void saveAbdominalExamination_shouldReturnStoredId() {
			CancerAbdominalExamination examination = new CancerAbdominalExamination();
			CancerAbdominalExamination saved = new CancerAbdominalExamination();
			saved.setID(4L);
			when(cancerAbdominalExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveCancerAbdominalExaminationData(examination));
		}

		@Test
		@DisplayName("saveCancerAbdominalExaminationData should return null when nothing was stored")
		void saveAbdominalExamination_shouldReturnNullWhenNothingStored() {
			CancerAbdominalExamination examination = new CancerAbdominalExamination();
			when(cancerAbdominalExaminationRepo.save(examination)).thenReturn(null);

			assertNull(service.saveCancerAbdominalExaminationData(examination));
		}

		@Test
		@DisplayName("saveCancerGynecologicalExaminationData should return the stored row id")
		void saveGynecologicalExamination_shouldReturnStoredId() {
			CancerGynecologicalExamination examination = new CancerGynecologicalExamination();
			CancerGynecologicalExamination saved = new CancerGynecologicalExamination();
			saved.setID(4L);
			when(cancerGynecologicalExaminationRepo.save(examination)).thenReturn(saved);

			assertEquals(4L, service.saveCancerGynecologicalExaminationData(examination));
		}

		@Test
		@DisplayName("saveDocExaminationImageAnnotation should expand every marker of the annotation")
		void saveImageAnnotation_shouldExpandMarkers() {
			WrapperCancerExamImgAnotasn wrapper = new WrapperCancerExamImgAnotasn();
			wrapper.setBeneficiaryRegID(BEN_REG_ID);
			wrapper.setVisitID(VISIT_ID);
			ArrayList<Map<String, Object>> markers = new ArrayList<>();
			Map<String, Object> marker = new HashMap<>();
			marker.put("xCord", 10d);
			marker.put("yCord", 20d);
			marker.put("point", 1d);
			marker.put("description", "Lesion");
			markers.add(marker);
			wrapper.setMarkers(markers);
			CancerExaminationImageAnnotation saved = new CancerExaminationImageAnnotation();
			saved.setID(4L);
			when(cancerExaminationImageAnnotationRepo.saveAll(any()))
					.thenReturn(Collections.singletonList(saved));

			assertNotNull(service.saveDocExaminationImageAnnotation(Collections.singletonList(wrapper), VISIT_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getCancerExaminationImageAnnotationList should return nothing for an annotation without markers")
		void getImageAnnotationList_shouldReturnNothingWithoutMarkers() {
			WrapperCancerExamImgAnotasn wrapper = new WrapperCancerExamImgAnotasn();

			assertTrue(service.getCancerExaminationImageAnnotationList(Collections.singletonList(wrapper), VISIT_CODE)
					.isEmpty());
		}
	}

	@Nested
	@DisplayName("per visit lookups")
	class PerVisitLookupTests {

		@Test
		@DisplayName("getBenFamilyHisData should expand the comma separated family members")
		void getFamilyHistory_shouldExpandFamilyMembers() {
			BenFamilyCancerHistory stored = new BenFamilyCancerHistory();
			stored.setFamilyMember("Mother,Father");
			when(benFamilyCancerHistoryRepo.getBenFamilyHistory(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>(Collections.singletonList(stored)));

			List<BenFamilyCancerHistory> result = service.getBenFamilyHisData(BEN_REG_ID, VISIT_CODE);

			assertEquals(2, result.get(0).getFamilyMemberList().size());
		}

		@Test
		@DisplayName("getBenFamilyHisData should leave the family member list empty when none is stored")
		void getFamilyHistory_shouldLeaveListEmptyWhenNoneStored() {
			when(benFamilyCancerHistoryRepo.getBenFamilyHistory(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>(Collections.singletonList(new BenFamilyCancerHistory())));

			assertTrue(service.getBenFamilyHisData(BEN_REG_ID, VISIT_CODE).get(0).getFamilyMemberList().isEmpty());
		}

		@Test
		@DisplayName("getBenObstetricDetailsData should delegate to the obstetric history repository")
		void getObstetricDetails_shouldDelegateToRepo() {
			BenObstetricCancerHistory stored = new BenObstetricCancerHistory();
			when(benObstetricCancerHistoryRepo.getBenObstetricCancerHistory(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getBenObstetricDetailsData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenPersonalCancerHistoryData should expand the comma separated tobacco products")
		void getPersonalHistory_shouldExpandTobaccoProducts() {
			BenPersonalCancerHistory stored = new BenPersonalCancerHistory();
			stored.setTypeOfTobaccoProduct("Cigarette,Beedi");
			when(benPersonalCancerHistoryRepo.getBenPersonalHistory(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			BenPersonalCancerHistory result = service.getBenPersonalCancerHistoryData(BEN_REG_ID, VISIT_CODE);

			assertEquals(2, result.getTypeOfTobaccoProductList().size());
		}

		@Test
		@DisplayName("getBenPersonalCancerHistoryData should return null when the visit has no personal history")
		void getPersonalHistory_shouldReturnNullWithoutStoredHistory() {
			when(benPersonalCancerHistoryRepo.getBenPersonalHistory(BEN_REG_ID, VISIT_CODE)).thenReturn(null);

			assertNull(service.getBenPersonalCancerHistoryData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenPersonalCancerDietHistoryData should expand the comma separated oil list")
		void getPersonalDietHistory_shouldExpandOilList() {
			BenPersonalCancerDietHistory stored = new BenPersonalCancerDietHistory();
			stored.setTypeOfOilConsumed("Mustard,Sunflower");
			when(benPersonalCancerDietHistoryRepo.getBenPersonaDietHistory(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			BenPersonalCancerDietHistory result = service.getBenPersonalCancerDietHistoryData(BEN_REG_ID, VISIT_CODE);

			assertEquals(2, result.getTypeOfOilConsumedList().size());
		}

		@Test
		@DisplayName("getBenPersonalCancerDietHistoryData should return null when the visit has no diet history")
		void getPersonalDietHistory_shouldReturnNullWithoutStoredHistory() {
			when(benPersonalCancerDietHistoryRepo.getBenPersonaDietHistory(BEN_REG_ID, VISIT_CODE)).thenReturn(null);

			assertNull(service.getBenPersonalCancerDietHistoryData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerVitalDetailData should delegate to the cancer vitals repository")
		void getVitalDetail_shouldDelegateToRepo() {
			BenCancerVitalDetail stored = new BenCancerVitalDetail();
			when(benCancerVitalDetailRepo.getBenCancerVitalDetail(BEN_REG_ID, VISIT_CODE)).thenReturn(stored);

			assertEquals(stored, service.getBenCancerVitalDetailData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerAbdominalExaminationData should delegate to the abdominal repository")
		void getAbdominalExamination_shouldDelegateToRepo() {
			CancerAbdominalExamination stored = new CancerAbdominalExamination();
			when(cancerAbdominalExaminationRepo.getBenCancerAbdominalExaminationDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getBenCancerAbdominalExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerBreastExaminationData should delegate to the breast repository")
		void getBreastExamination_shouldDelegateToRepo() {
			CancerBreastExamination stored = new CancerBreastExamination();
			when(cancerBreastExaminationRepo.getBenCancerBreastExaminationDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getBenCancerBreastExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerGynecologicalExaminationData should delegate to the gynecological repository")
		void getGynecologicalExamination_shouldDelegateToRepo() {
			CancerGynecologicalExamination stored = new CancerGynecologicalExamination();
			when(cancerGynecologicalExaminationRepo.getBenCancerGynecologicalExaminationDetails(BEN_REG_ID,
					VISIT_CODE)).thenReturn(stored);

			assertEquals(stored, service.getBenCancerGynecologicalExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerSignAndSymptomsData should delegate to the sign and symptoms repository")
		void getSignAndSymptoms_shouldDelegateToRepo() {
			CancerSignAndSymptoms stored = new CancerSignAndSymptoms();
			when(cancerSignAndSymptomsRepo.getBenCancerSignAndSymptomsDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getBenCancerSignAndSymptomsData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerLymphNodeDetailsData should delegate to the lymph node repository")
		void getLymphNodeDetails_shouldDelegateToRepo() {
			List<CancerLymphNodeDetails> stored = Collections.singletonList(new CancerLymphNodeDetails());
			when(cancerLymphNodeExaminationRepo.getBenCancerLymphNodeDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getBenCancerLymphNodeDetailsData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenCancerOralExaminationData should delegate to the oral repository")
		void getOralExamination_shouldDelegateToRepo() {
			CancerOralExamination stored = new CancerOralExamination();
			when(cancerOralExaminationRepo.getBenCancerOralExaminationDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(stored);

			assertEquals(stored, service.getBenCancerOralExaminationData(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getCancerExaminationImageAnnotationCasesheet should group the stored markers by image")
		void getImageAnnotationCasesheet_shouldGroupMarkersByImage() {
			CancerExaminationImageAnnotation stored = new CancerExaminationImageAnnotation();
			stored.setBeneficiaryRegID(BEN_REG_ID);
			stored.setVisitCode(VISIT_CODE);
			stored.setCancerImageID(1);
			stored.setxCoordinate(10);
			stored.setyCoordinate(20);
			stored.setPoint(1);
			when(cancerExaminationImageAnnotationRepo.getCancerExaminationImageAnnotationList(BEN_REG_ID, VISIT_CODE))
					.thenReturn(Collections.singletonList(stored));

			assertNotNull(service.getCancerExaminationImageAnnotationCasesheet(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getCancerExaminationImageAnnotationCasesheet should return nothing when no annotation is stored")
		void getImageAnnotationCasesheet_shouldReturnNothingWhenNoneStored() {
			when(cancerExaminationImageAnnotationRepo.getCancerExaminationImageAnnotationList(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>());

			assertTrue(service.getCancerExaminationImageAnnotationCasesheet(BEN_REG_ID, VISIT_CODE).isEmpty());
		}

		@Test
		@DisplayName("getBeneficiaryVisitDetails should build the visit from the stored row")
		void getBeneficiaryVisitDetails_shouldBuildVisitFromRow() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[40]);
			when(benVisitDetailRepo.getBeneficiaryVisitDetails(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>(rows));

			assertNotNull(service.getBeneficiaryVisitDetails(BEN_REG_ID, VISIT_CODE));
		}

		@Test
		@DisplayName("getBenNurseDataForCaseSheet should assemble every nurse captured section")
		void getNurseDataForCaseSheet_shouldAssembleEverySection() {
			when(benFamilyCancerHistoryRepo.getBenFamilyHistory(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());
			when(benVisitDetailRepo.getBeneficiaryVisitDetails(BEN_REG_ID, VISIT_CODE)).thenReturn(new ArrayList<>());

			Map<String, Object> result = service.getBenNurseDataForCaseSheet(BEN_REG_ID, VISIT_CODE);

			assertTrue(result.containsKey("benVisitDetail"));
			assertTrue(result.containsKey("oralExamination"));
		}
	}

	@Nested
	@DisplayName("cross visit history reports")
	class HistoryReportTests {

		private ArrayList<Object[]> oneEmptyRow() {
			ArrayList<Object[]> rows = new ArrayList<>();
			rows.add(new Object[40]);
			return rows;
		}

		@Test
		@DisplayName("getBenCancerFamilyHistory should render the stored rows with the report columns")
		void getFamilyHistoryReport_shouldRenderRowsWithColumns() {
			when(benFamilyCancerHistoryRepo.getBenCancerFamilyHistory(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.getBenCancerFamilyHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("getBenCancerFamilyHistory should render only the columns when nothing is stored")
		void getFamilyHistoryReport_shouldRenderColumnsOnlyWhenEmpty() {
			when(benFamilyCancerHistoryRepo.getBenCancerFamilyHistory(BEN_REG_ID)).thenReturn(null);

			assertTrue(service.getBenCancerFamilyHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("getBenCancerPersonalHistory should render the stored rows with the report columns")
		void getPersonalHistoryReport_shouldRenderRowsWithColumns() {
			when(benPersonalCancerHistoryRepo.getBenPersonalHistory(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.getBenCancerPersonalHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("getBenCancerPersonalDietHistory should render the stored rows with the report columns")
		void getPersonalDietHistoryReport_shouldRenderRowsWithColumns() {
			when(benPersonalCancerDietHistoryRepo.getBenPersonaDietHistory(BEN_REG_ID)).thenReturn(oneEmptyRow());

			assertTrue(service.getBenCancerPersonalDietHistory(BEN_REG_ID).contains("\"columns\""));
		}

		@Test
		@DisplayName("getBenCancerObstetricHistory should render the stored rows with the report columns")
		void getObstetricHistoryReport_shouldRenderRowsWithColumns() {
			when(benObstetricCancerHistoryRepo.getBenObstetricCancerHistoryData(BEN_REG_ID))
					.thenReturn(oneEmptyRow());

			assertTrue(service.getBenCancerObstetricHistory(BEN_REG_ID).contains("\"columns\""));
		}
	}

	@Nested
	@DisplayName("history and examination updates")
	class UpdateTests {

		@Test
		@DisplayName("updateBeneficiaryFamilyCancerHistory should soft delete the stored rows before writing")
		void updateFamilyHistory_shouldSoftDeleteBeforeWriting() {
			BenFamilyCancerHistory history = familyHistory(Arrays.asList("Mother"));
			when(benFamilyCancerHistoryRepo.getFamilyCancerHistoryStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());
			when(benFamilyCancerHistoryRepo.deleteExistingFamilyRecord(anyLong(), anyString())).thenReturn(1);
			when(benFamilyCancerHistoryRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateBeneficiaryFamilyCancerHistory(new ArrayList<>(
					Collections.singletonList(history))));
			verify(benFamilyCancerHistoryRepo).deleteExistingFamilyRecord(1L, "U");
			verify(benFamilyCancerHistoryRepo).deleteExistingFamilyRecord(2L, "N");
		}

		@Test
		@DisplayName("updateBenObstetricCancerHistory should read the processed flag before updating")
		void updateObstetricHistory_shouldReadProcessedFlag() {
			BenObstetricCancerHistory history = new BenObstetricCancerHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			when(benObstetricCancerHistoryRepo.getObstetricCancerHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateBenObstetricCancerHistory(history));
		}

		@Test
		@DisplayName("updateBenPersonalCancerHistory should read the processed flag before updating")
		void updatePersonalHistory_shouldReadProcessedFlag() {
			BenPersonalCancerHistory history = new BenPersonalCancerHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			when(benPersonalCancerHistoryRepo.getPersonalCancerHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateBenPersonalCancerHistory(history));
		}

		@Test
		@DisplayName("updateBenPersonalCancerDietHistory should read the processed flag before updating")
		void updatePersonalDietHistory_shouldReadProcessedFlag() {
			BenPersonalCancerDietHistory history = new BenPersonalCancerDietHistory();
			history.setBeneficiaryRegID(BEN_REG_ID);
			history.setVisitCode(VISIT_CODE);
			when(benPersonalCancerDietHistoryRepo.getPersonalCancerDietHistoryStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateBenPersonalCancerDietHistory(history));
		}

		@Test
		@DisplayName("updateBenVitalDetail should read the processed flag before updating")
		void updateVitalDetail_shouldReadProcessedFlag() {
			BenCancerVitalDetail vital = new BenCancerVitalDetail();
			vital.setBeneficiaryRegID(BEN_REG_ID);
			vital.setVisitCode(VISIT_CODE);
			when(benCancerVitalDetailRepo.getCancerVitalStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateBenVitalDetail(vital));
		}

		@Test
		@DisplayName("updateSignAndSymptomsExaminationDetails should read the processed flag before updating")
		void updateSignAndSymptoms_shouldReadProcessedFlag() {
			CancerSignAndSymptoms symptoms = new CancerSignAndSymptoms();
			symptoms.setBeneficiaryRegID(BEN_REG_ID);
			symptoms.setVisitCode(VISIT_CODE);
			when(cancerSignAndSymptomsRepo.getCancerSignAndSymptomsStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateSignAndSymptomsExaminationDetails(symptoms));
		}

		@Test
		@DisplayName("updateCancerOralDetails should read the processed flag before updating")
		void updateOralExamination_shouldReadProcessedFlag() {
			CancerOralExamination examination = new CancerOralExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(cancerOralExaminationRepo.getCancerOralExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateCancerOralDetails(examination));
		}

		@Test
		@DisplayName("updateCancerBreastDetails should read the processed flag before updating")
		void updateBreastExamination_shouldReadProcessedFlag() {
			CancerBreastExamination examination = new CancerBreastExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(cancerBreastExaminationRepo.getCancerBreastExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateCancerBreastDetails(examination));
		}

		@Test
		@DisplayName("updateCancerAbdominalExaminationDetails should read the processed flag before updating")
		void updateAbdominalExamination_shouldReadProcessedFlag() {
			CancerAbdominalExamination examination = new CancerAbdominalExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(cancerAbdominalExaminationRepo.getCancerAbdominalExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateCancerAbdominalExaminationDetails(examination));
		}

		@Test
		@DisplayName("updateCancerGynecologicalExaminationDetails should read the processed flag before updating")
		void updateGynecologicalExamination_shouldReadProcessedFlag() {
			CancerGynecologicalExamination examination = new CancerGynecologicalExamination();
			examination.setBeneficiaryRegID(BEN_REG_ID);
			examination.setVisitCode(VISIT_CODE);
			when(cancerGynecologicalExaminationRepo.getCancerGynecologicalExaminationStatus(BEN_REG_ID, VISIT_CODE)).thenReturn("P");

			assertEquals(0, service.updateCancerGynecologicalExaminationDetails(examination));
		}
	}

	@Nested
	@DisplayName("lymph node and image annotation updates")
	class LymphNodeAndAnnotationUpdateTests {

		private com.iemr.tm.data.doctor.WrapperCancerSymptoms symptoms(Boolean enlarged, boolean withMeasurements) {
			com.iemr.tm.data.doctor.WrapperCancerSymptoms wrapper =
					new com.iemr.tm.data.doctor.WrapperCancerSymptoms();
			com.iemr.tm.data.doctor.CancerSignAndSymptoms signs = new com.iemr.tm.data.doctor.CancerSignAndSymptoms();
			signs.setBeneficiaryRegID(BEN_REG_ID);
			signs.setVisitCode(VISIT_CODE);
			signs.setLymphNode_Enlarged(enlarged);
			wrapper.setCancerSignAndSymptoms(signs);

			com.iemr.tm.data.doctor.CancerLymphNodeDetails cervical =
					new com.iemr.tm.data.doctor.CancerLymphNodeDetails();
			cervical.setBeneficiaryRegID(BEN_REG_ID);
			cervical.setVisitCode(VISIT_CODE);
			cervical.setLymphNodeName("Cervical");
			com.iemr.tm.data.doctor.CancerLymphNodeDetails axillary =
					new com.iemr.tm.data.doctor.CancerLymphNodeDetails();
			axillary.setBeneficiaryRegID(BEN_REG_ID);
			axillary.setVisitCode(VISIT_CODE);
			axillary.setLymphNodeName("Cervical");
			if (withMeasurements) {
				cervical.setMobility_Left(Boolean.TRUE);
				cervical.setSize_Right("2 cm");
			}
			wrapper.setCancerLymphNodeDetails(Arrays.asList(cervical, axillary));
			return wrapper;
		}

		@Test
		@DisplayName("updateLymphNodeExaminationDetails should replace the measured nodes for the visit")
		void updateLymphNode_shouldReplaceMeasuredNodes() {
			when(cancerLymphNodeExaminationRepo.getCancerLymphNodeDetailsStatusForLymphnodeNameList(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyList())).thenReturn(statuses());
			when(cancerLymphNodeExaminationRepo.deleteExistingLymphNodeDetails(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString())).thenReturn(1);
			when(cancerLymphNodeExaminationRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateLymphNodeExaminationDetails(symptoms(Boolean.TRUE, true)));
		}

		@Test
		@DisplayName("updateLymphNodeExaminationDetails should succeed when no node was measured")
		void updateLymphNode_shouldSucceedWithoutMeasurements() {
			when(cancerLymphNodeExaminationRepo.getCancerLymphNodeDetailsStatusForLymphnodeNameList(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());

			assertEquals(1, service.updateLymphNodeExaminationDetails(symptoms(Boolean.TRUE, false)));
		}

		@Test
		@DisplayName("updateLymphNodeExaminationDetails should clear the stored nodes when none are enlarged")
		void updateLymphNode_shouldClearStoredNodesWhenNoneEnlarged() {
			when(cancerLymphNodeExaminationRepo.getCancerLymphNodeDetailsStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());
			when(cancerLymphNodeExaminationRepo.deleteExistingLymphNodeDetails(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString())).thenReturn(1);

			assertEquals(1, service.updateLymphNodeExaminationDetails(symptoms(Boolean.FALSE, false)));
		}

		@Test
		@DisplayName("updateLymphNodeExaminationDetails should succeed when nothing was stored to clear")
		void updateLymphNode_shouldSucceedWhenNothingStoredToClear() {
			when(cancerLymphNodeExaminationRepo.getCancerLymphNodeDetailsStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(new ArrayList<>());

			assertEquals(1, service.updateLymphNodeExaminationDetails(symptoms(Boolean.FALSE, false)));
		}

		@Test
		@DisplayName("updateLymphNodeExaminationDetails should report a failure when the stored nodes cannot be cleared")
		void updateLymphNode_shouldReportFailureWhenClearFails() {
			when(cancerLymphNodeExaminationRepo.getCancerLymphNodeDetailsStatus(BEN_REG_ID, VISIT_CODE))
					.thenReturn(statuses());
			when(cancerLymphNodeExaminationRepo.deleteExistingLymphNodeDetails(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString())).thenReturn(0);

			assertEquals(0, service.updateLymphNodeExaminationDetails(symptoms(Boolean.FALSE, false)));
		}

		private com.iemr.tm.data.doctor.CancerExaminationImageAnnotation annotation(boolean complete) {
			com.iemr.tm.data.doctor.CancerExaminationImageAnnotation annotation =
					new com.iemr.tm.data.doctor.CancerExaminationImageAnnotation();
			annotation.setBeneficiaryRegID(BEN_REG_ID);
			annotation.setVisitCode(VISIT_CODE);
			annotation.setCancerImageID(2);
			if (complete) {
				annotation.setxCoordinate(120);
				annotation.setyCoordinate(240);
				annotation.setCreatedBy("tester");
			}
			return annotation;
		}

		@Test
		@DisplayName("updateCancerExamImgAnotasnDetails should replace the stored markers for the visit")
		void updateImageAnnotation_shouldReplaceStoredMarkers() {
			when(cancerExaminationImageAnnotationRepo.getCancerExaminationImageAnnotationDetailsStatus(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyList())).thenReturn(statuses());
			when(cancerExaminationImageAnnotationRepo.deleteExistingImageAnnotationDetails(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString())).thenReturn(1);
			when(cancerExaminationImageAnnotationRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateCancerExamImgAnotasnDetails(
					new ArrayList<>(Collections.singletonList(annotation(true)))));
		}

		@Test
		@DisplayName("updateCancerExamImgAnotasnDetails should succeed when no marker was stored before")
		void updateImageAnnotation_shouldSucceedWhenNothingStoredBefore() {
			when(cancerExaminationImageAnnotationRepo.getCancerExaminationImageAnnotationDetailsStatus(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
			when(cancerExaminationImageAnnotationRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateCancerExamImgAnotasnDetails(
					new ArrayList<>(Collections.singletonList(annotation(true)))));
		}

		@Test
		@DisplayName("updateCancerExamImgAnotasnDetails should skip markers without coordinates")
		void updateImageAnnotation_shouldSkipMarkersWithoutCoordinates() {
			when(cancerExaminationImageAnnotationRepo.getCancerExaminationImageAnnotationDetailsStatus(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
			when(cancerExaminationImageAnnotationRepo.saveAll(any())).thenAnswer(echoList());

			assertEquals(1, service.updateCancerExamImgAnotasnDetails(
					new ArrayList<>(Collections.singletonList(annotation(false)))));
		}

		@Test
		@DisplayName("updateCancerExamImgAnotasnDetails should succeed for an empty marker list")
		void updateImageAnnotation_shouldSucceedForEmptyList() {
			assertEquals(1, service.updateCancerExamImgAnotasnDetails(new ArrayList<>()));
		}

		@Test
		@DisplayName("updateCancerExamImgAnotasnDetails should report a failure when the stored markers cannot be cleared")
		void updateImageAnnotation_shouldReportFailureWhenClearFails() {
			when(cancerExaminationImageAnnotationRepo.getCancerExaminationImageAnnotationDetailsStatus(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(),
					org.mockito.ArgumentMatchers.anyList())).thenReturn(statuses());
			when(cancerExaminationImageAnnotationRepo.deleteExistingImageAnnotationDetails(
					org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString())).thenReturn(0);

			assertEquals(0, service.updateCancerExamImgAnotasnDetails(
					new ArrayList<>(Collections.singletonList(annotation(true)))));
		}
	}
}
