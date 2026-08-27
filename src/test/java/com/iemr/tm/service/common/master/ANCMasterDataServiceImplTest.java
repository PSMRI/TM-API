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
package com.iemr.tm.service.common.master;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.doctor.ChiefComplaintMasterRepo;
import com.iemr.tm.repo.doctor.DrugDoseMasterRepo;
import com.iemr.tm.repo.doctor.DrugDurationUnitMasterRepo;
import com.iemr.tm.repo.doctor.DrugFrequencyMasterRepo;
import com.iemr.tm.repo.foetalmonitor.FoetalMonitorTestsRepo;
import com.iemr.tm.repo.labModule.ProcedureRepo;
import com.iemr.tm.repo.login.MasterVanRepo;
import com.iemr.tm.repo.masterrepo.anc.AllergicReactionTypesRepo;
import com.iemr.tm.repo.masterrepo.anc.BloodGroupsRepo;
import com.iemr.tm.repo.masterrepo.anc.ChildVaccinationsRepo;
import com.iemr.tm.repo.masterrepo.anc.ComorbidConditionRepo;
import com.iemr.tm.repo.masterrepo.anc.CompFeedsRepo;
import com.iemr.tm.repo.masterrepo.anc.ComplicationTypesRepo;
import com.iemr.tm.repo.masterrepo.anc.CounsellingTypeRepo;
import com.iemr.tm.repo.masterrepo.anc.DeliveryPlaceRepo;
import com.iemr.tm.repo.masterrepo.anc.DeliveryTypeRepo;
import com.iemr.tm.repo.masterrepo.anc.DevelopmentProblemsRepo;
import com.iemr.tm.repo.masterrepo.anc.DiseaseTypeRepo;
import com.iemr.tm.repo.masterrepo.anc.FundalHeightRepo;
import com.iemr.tm.repo.masterrepo.anc.GestationRepo;
import com.iemr.tm.repo.masterrepo.anc.GrossMotorMilestoneRepo;
import com.iemr.tm.repo.masterrepo.anc.IllnessTypesRepo;
import com.iemr.tm.repo.masterrepo.anc.JointTypesRepo;
import com.iemr.tm.repo.masterrepo.anc.MenstrualCycleRangeRepo;
import com.iemr.tm.repo.masterrepo.anc.MenstrualCycleStatusRepo;
import com.iemr.tm.repo.masterrepo.anc.MenstrualProblemRepo;
import com.iemr.tm.repo.masterrepo.anc.MusculoskeletalRepo;
import com.iemr.tm.repo.masterrepo.anc.OptionalVaccinationsRepo;
import com.iemr.tm.repo.masterrepo.anc.PersonalHabitTypeRepo;
import com.iemr.tm.repo.masterrepo.anc.PregDurationRepo;
import com.iemr.tm.repo.masterrepo.anc.PregOutcomeRepo;
import com.iemr.tm.repo.masterrepo.anc.ServiceFacilityMasterRepo;
import com.iemr.tm.repo.masterrepo.anc.ServiceMasterRepo;
import com.iemr.tm.repo.masterrepo.anc.SurgeryTypesRepo;
import com.iemr.tm.repo.masterrepo.covid19.CovidContactHistoryMasterRepo;
import com.iemr.tm.repo.masterrepo.covid19.CovidRecommnedationMasterRepo;
import com.iemr.tm.repo.masterrepo.covid19.CovidSymptomsMasterRepo;
import com.iemr.tm.repo.masterrepo.doctor.InstituteRepo;
import com.iemr.tm.repo.masterrepo.doctor.ItemFormMasterRepo;
import com.iemr.tm.repo.masterrepo.doctor.ItemMasterRepo;
import com.iemr.tm.repo.masterrepo.doctor.RouteOfAdminRepo;
import com.iemr.tm.repo.masterrepo.doctor.V_DrugPrescriptionRepo;
import com.iemr.tm.repo.masterrepo.ncdCare.NCDCareTypeRepo;
import com.iemr.tm.repo.masterrepo.nurse.FamilyMemberMasterRepo;
import com.iemr.tm.repo.masterrepo.pnc.NewbornHealthStatusRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ANCMasterDataServiceImpl Test Suite")
class ANCMasterDataServiceImplTest {

	@Mock
	private AllergicReactionTypesRepo allergicReactionTypesRepo;
	@Mock
	private BloodGroupsRepo bloodGroupsRepo;
	@Mock
	private ChildVaccinationsRepo childVaccinationsRepo;
	@Mock
	private DeliveryPlaceRepo deliveryPlaceRepo;
	@Mock
	private DeliveryTypeRepo deliveryTypeRepo;
	@Mock
	private DevelopmentProblemsRepo developmentProblemsRepo;
	@Mock
	private GestationRepo gestationRepo;
	@Mock
	private IllnessTypesRepo illnessTypesRepo;
	@Mock
	private JointTypesRepo jointTypesRepo;
	@Mock
	private MenstrualCycleRangeRepo menstrualCycleRangeRepo;
	@Mock
	private MenstrualCycleStatusRepo menstrualCycleStatusRepo;
	@Mock
	private MenstrualProblemRepo menstrualProblemRepo;
	@Mock
	private MusculoskeletalRepo musculoskeletalRepo;
	@Mock
	private PregDurationRepo pregDurationRepo;
	@Mock
	private SurgeryTypesRepo surgeryTypesRepo;
	@Mock
	private ComorbidConditionRepo comorbidConditionRepo;
	@Mock
	private CompFeedsRepo compFeedsRepo;
	@Mock
	private FundalHeightRepo fundalHeightRepo;
	@Mock
	private GrossMotorMilestoneRepo grossMotorMilestoneRepo;
	@Mock
	private ServiceMasterRepo serviceMasterRepo;
	@Mock
	private CounsellingTypeRepo counsellingTypeRepo;
	@Mock
	private InstituteRepo instituteRepo;
	@Mock
	private PersonalHabitTypeRepo personalHabitTypeRepo;
	@Mock
	private PregOutcomeRepo pregOutcomeRepo;
	@Mock
	private DiseaseTypeRepo diseaseTypeRepo;
	@Mock
	private ComplicationTypesRepo complicationTypesRepo;
	@Mock
	private ChiefComplaintMasterRepo chiefComplaintMasterRepo;
	@Mock
	private FamilyMemberMasterRepo familyMemberMasterRepo;
	@Mock
	private DrugDoseMasterRepo drugDoseMasterRepo;
	@Mock
	private DrugDurationUnitMasterRepo drugDurationUnitMasterRepo;
	@Mock
	private DrugFrequencyMasterRepo drugFrequencyMasterRepo;
	@Mock
	private NewbornHealthStatusRepo newbornHealthStatusRepo;
	@Mock
	private NCDCareTypeRepo ncdCareTypeRepo;
	@Mock
	private ProcedureRepo procedureRepo;
	@Mock
	private OptionalVaccinationsRepo optionalVaccinationsRepo;
	@Mock
	private ItemMasterRepo itemMasterRepo;
	@Mock
	private ItemFormMasterRepo itemFormMasterRepo;
	@Mock
	private RouteOfAdminRepo routeOfAdminRepo;
	@Mock
	private V_DrugPrescriptionRepo v_DrugPrescriptionRepo;
	@Mock
	private CovidSymptomsMasterRepo covidSymptomsMasterRepo;
	@Mock
	private CovidContactHistoryMasterRepo covidContactHistoryMasterRepo;
	@Mock
	private CovidRecommnedationMasterRepo covidRecommnedationMasterRepo;
	@Mock
	private MasterVanRepo masterVanRepo;
	@Mock
	private FoetalMonitorTestsRepo foetakMonitorTestRepo;
	@Mock
	private ServiceFacilityMasterRepo serviceFacilityMasterRepo;

	@InjectMocks
	private ANCMasterDataServiceImpl service;

	@Test
	@DisplayName("getCommonNurseMasterDataForGenopdAncNcdcarePnc should assemble the nurse master data")
	void getNurseMasterData_shouldAssembleMasterData() {
		String result = service.getCommonNurseMasterDataForGenopdAncNcdcarePnc(1, 9, "Female");

		assertNotNull(result);
		assertTrue(result.contains("bloodGroups"));
	}

	@Test
	@DisplayName("getCommonNurseMasterDataForGenopdAncNcdcarePnc should assemble the master data for a male")
	void getNurseMasterData_shouldAssembleMasterDataForMale() {
		assertNotNull(service.getCommonNurseMasterDataForGenopdAncNcdcarePnc(1, 9, "Male"));
	}

	@Test
	@DisplayName("getCommonDoctorMasterDataForGenopdAncNcdcarePnc should assemble the doctor master data")
	void getDoctorMasterData_shouldAssembleMasterData() {
		String result = service.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(1, 9, "Female", 3, 7);

		assertNotNull(result);
		assertTrue(result.contains("additionalServices"));
	}

	@Test
	@DisplayName("getCommonDoctorMasterDataForGenopdAncNcdcarePnc should assemble the cancer screening master data")
	void getDoctorMasterData_shouldAssembleCancerScreeningMasterData() {
		assertNotNull(service.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(7, 9, "Female", 3, 7));
	}
}
