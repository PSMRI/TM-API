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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iemr.tm.data.doctor.ChiefComplaintMaster;
import com.iemr.tm.data.doctor.DrugDoseMaster;
import com.iemr.tm.data.doctor.DrugDurationUnitMaster;
import com.iemr.tm.data.doctor.DrugFrequencyMaster;
import com.iemr.tm.data.foetalmonitor.FoetalMonitorTestMaster;
import com.iemr.tm.data.institution.Institute;
import com.iemr.tm.data.labModule.ProcedureData;
import com.iemr.tm.data.masterdata.anc.AllergicReactionTypes;
import com.iemr.tm.data.masterdata.anc.BloodGroups;
import com.iemr.tm.data.masterdata.anc.ChildVaccinations;
import com.iemr.tm.data.masterdata.anc.ComorbidCondition;
import com.iemr.tm.data.masterdata.anc.CompFeeds;
import com.iemr.tm.data.masterdata.anc.ComplicationTypes;
import com.iemr.tm.data.masterdata.anc.CounsellingType;
import com.iemr.tm.data.masterdata.anc.DeliveryPlace;
import com.iemr.tm.data.masterdata.anc.DeliveryType;
import com.iemr.tm.data.masterdata.anc.DevelopmentProblems;
import com.iemr.tm.data.masterdata.anc.DiseaseType;
import com.iemr.tm.data.masterdata.anc.FundalHeight;
import com.iemr.tm.data.masterdata.anc.Gestation;
import com.iemr.tm.data.masterdata.anc.GrossMotorMilestone;
import com.iemr.tm.data.masterdata.anc.IllnessTypes;
import com.iemr.tm.data.masterdata.anc.JointTypes;
import com.iemr.tm.data.masterdata.anc.MenstrualCycleRange;
import com.iemr.tm.data.masterdata.anc.MenstrualCycleStatus;
import com.iemr.tm.data.masterdata.anc.MenstrualProblem;
import com.iemr.tm.data.masterdata.anc.Musculoskeletal;
import com.iemr.tm.data.masterdata.anc.OptionalVaccinations;
import com.iemr.tm.data.masterdata.anc.PersonalHabitType;
import com.iemr.tm.data.masterdata.anc.PregDuration;
import com.iemr.tm.data.masterdata.anc.PregOutcome;
import com.iemr.tm.data.masterdata.anc.ServiceFacilityMaster;
import com.iemr.tm.data.masterdata.anc.ServiceMaster;
import com.iemr.tm.data.masterdata.anc.SurgeryTypes;
import com.iemr.tm.data.masterdata.doctor.ItemFormMaster;
import com.iemr.tm.data.masterdata.doctor.ItemMaster;
import com.iemr.tm.data.masterdata.doctor.RouteOfAdmin;
import com.iemr.tm.data.masterdata.doctor.V_DrugPrescription;
import com.iemr.tm.data.masterdata.ncdcare.NCDCareType;
import com.iemr.tm.data.masterdata.ncdscreening.NCDScreeningCondition;
import com.iemr.tm.data.masterdata.nurse.FamilyMemberType;
import com.iemr.tm.data.masterdata.pnc.NewbornHealthStatus;
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

@Service
public class ANCMasterDataServiceImpl {

	private AllergicReactionTypesRepo allergicReactionTypesRepo;
	private BloodGroupsRepo bloodGroupsRepo;
	private ChildVaccinationsRepo childVaccinationsRepo;
	private DeliveryPlaceRepo deliveryPlaceRepo;
	private DeliveryTypeRepo deliveryTypeRepo;
	private DevelopmentProblemsRepo developmentProblemsRepo;
	private GestationRepo gestationRepo;
	private IllnessTypesRepo illnessTypesRepo;
	private JointTypesRepo jointTypesRepo;
	private MenstrualCycleRangeRepo menstrualCycleRangeRepo;
	private MenstrualCycleStatusRepo menstrualCycleStatusRepo;
	private MenstrualProblemRepo menstrualProblemRepo;
	private MusculoskeletalRepo musculoskeletalRepo;
	private PregDurationRepo pregDurationRepo;
	private SurgeryTypesRepo surgeryTypesRepo;
	private ComorbidConditionRepo comorbidConditionRepo;
	private CompFeedsRepo compFeedsRepo;
	private FundalHeightRepo fundalHeightRepo;
	private GrossMotorMilestoneRepo grossMotorMilestoneRepo;
	private ServiceMasterRepo serviceMasterRepo;
	private CounsellingTypeRepo counsellingTypeRepo;
	private InstituteRepo instituteRepo;
	private PersonalHabitTypeRepo personalHabitTypeRepo;
	private PregOutcomeRepo pregOutcomeRepo;
	private DiseaseTypeRepo diseaseTypeRepo;
	private ComplicationTypesRepo complicationTypesRepo;
	private ChiefComplaintMasterRepo chiefComplaintMasterRepo;
	private FamilyMemberMasterRepo familyMemberMasterRepo;
	private DrugDoseMasterRepo drugDoseMasterRepo;
	private DrugDurationUnitMasterRepo drugDurationUnitMasterRepo;
	private DrugFrequencyMasterRepo drugFrequencyMasterRepo;
	private NewbornHealthStatusRepo newbornHealthStatusRepo;
	private NCDScreeningMasterServiceImpl ncdScreeningMasterServiceImpl;
	private NCDCareTypeRepo ncdCareTypeRepo;
	private ProcedureRepo procedureRepo;
	private OptionalVaccinationsRepo optionalVaccinationsRepo;
	@Autowired
	private ItemMasterRepo itemMasterRepo;

	private ItemFormMasterRepo itemFormMasterRepo;
	private RouteOfAdminRepo routeOfAdminRepo;
	private V_DrugPrescriptionRepo v_DrugPrescriptionRepo;

	@Autowired
	private CovidSymptomsMasterRepo covidSymptomsMasterRepo;
	@Autowired
	private CovidContactHistoryMasterRepo covidContactHistoryMasterRepo;
	@Autowired
	private CovidRecommnedationMasterRepo covidRecommnedationMasterRepo;

	@Autowired
	private MasterVanRepo masterVanRepo;
	
	@Autowired
	private FoetalMonitorTestsRepo foetakMonitorTestRepo;

	@Autowired
	public void setV_DrugPrescriptionRepo(V_DrugPrescriptionRepo v_DrugPrescriptionRepo) {
		this.v_DrugPrescriptionRepo = v_DrugPrescriptionRepo;
	}

	@Autowired
	public void setRouteOfAdminRepo(RouteOfAdminRepo routeOfAdminRepo) {
		this.routeOfAdminRepo = routeOfAdminRepo;
	}

	@Autowired
	public void setItemFormMasterRepo(ItemFormMasterRepo itemFormMasterRepo) {
		this.itemFormMasterRepo = itemFormMasterRepo;
	}

	@Autowired
	public void setOptionalVaccinationsRepo(OptionalVaccinationsRepo optionalVaccinationsRepo) {
		this.optionalVaccinationsRepo = optionalVaccinationsRepo;
	}

	@Autowired
	public void setProcedureRepo(ProcedureRepo procedureRepo) {
		this.procedureRepo = procedureRepo;
	}

	@Autowired
	public void setNcdScreeningMasterServiceImpl(NCDScreeningMasterServiceImpl ncdScreeningMasterServiceImpl) {
		this.ncdScreeningMasterServiceImpl = ncdScreeningMasterServiceImpl;
	}

	@Autowired
	public void setNcdCareTypeRepo(NCDCareTypeRepo ncdCareTypeRepo) {
		this.ncdCareTypeRepo = ncdCareTypeRepo;
	}

	@Autowired
	public void setNewbornHealthStatusRepo(NewbornHealthStatusRepo newbornHealthStatusRepo) {
		this.newbornHealthStatusRepo = newbornHealthStatusRepo;
	}

	@Autowired
	public void setAllergicReactionTypesRepo(AllergicReactionTypesRepo allergicReactionTypesRepo) {
		this.allergicReactionTypesRepo = allergicReactionTypesRepo;
	}

	@Autowired
	public void setBloodGroupsRepo(BloodGroupsRepo bloodGroupsRepo) {
		this.bloodGroupsRepo = bloodGroupsRepo;
	}

	@Autowired
	public void setChildVaccinationsRepo(ChildVaccinationsRepo childVaccinationsRepo) {
		this.childVaccinationsRepo = childVaccinationsRepo;
	}

	@Autowired
	public void setDeliveryPlaceRepo(DeliveryPlaceRepo deliveryPlaceRepo) {
		this.deliveryPlaceRepo = deliveryPlaceRepo;
	}

	@Autowired
	public void setDeliveryTypeRepo(DeliveryTypeRepo deliveryTypeRepo) {
		this.deliveryTypeRepo = deliveryTypeRepo;
	}

	@Autowired
	public void setDevelopmentProblemsRepo(DevelopmentProblemsRepo developmentProblemsRepo) {
		this.developmentProblemsRepo = developmentProblemsRepo;
	}

	@Autowired
	public void setGestationRepo(GestationRepo gestationRepo) {
		this.gestationRepo = gestationRepo;
	}

	@Autowired
	public void setIllnessTypesRepo(IllnessTypesRepo illnessTypesRepo) {
		this.illnessTypesRepo = illnessTypesRepo;
	}

	@Autowired
	public void setJointTypesRepo(JointTypesRepo jointTypesRepo) {
		this.jointTypesRepo = jointTypesRepo;
	}

	@Autowired
	public void setMenstrualCycleRangeRepo(MenstrualCycleRangeRepo menstrualCycleRangeRepo) {
		this.menstrualCycleRangeRepo = menstrualCycleRangeRepo;
	}

	@Autowired
	public void setMenstrualCycleStatusRepo(MenstrualCycleStatusRepo menstrualCycleStatusRepo) {
		this.menstrualCycleStatusRepo = menstrualCycleStatusRepo;
	}

	@Autowired
	public void setMenstrualProblemRepo(MenstrualProblemRepo menstrualProblemRepo) {
		this.menstrualProblemRepo = menstrualProblemRepo;
	}

	@Autowired
	public void setMusculoskeletalRepo(MusculoskeletalRepo musculoskeletalRepo) {
		this.musculoskeletalRepo = musculoskeletalRepo;
	}

	@Autowired
	public void setPregDurationRepo(PregDurationRepo pregDurationRepo) {
		this.pregDurationRepo = pregDurationRepo;
	}

	@Autowired
	public void setSurgeryTypesRepo(SurgeryTypesRepo surgeryTypesRepo) {
		this.surgeryTypesRepo = surgeryTypesRepo;
	}

	@Autowired
	public void setChiefComplaintMasterRepo(ChiefComplaintMasterRepo chiefComplaintMasterRepo) {
		this.chiefComplaintMasterRepo = chiefComplaintMasterRepo;
	}

	@Autowired
	public void setFamilyMemberMasterRepo(FamilyMemberMasterRepo familyMemberMasterRepo) {
		this.familyMemberMasterRepo = familyMemberMasterRepo;
	}

	@Autowired
	public void setDrugDoseMasterRepo(DrugDoseMasterRepo drugDoseMasterRepo) {
		this.drugDoseMasterRepo = drugDoseMasterRepo;
	}

	@Autowired
	public void setDrugDurationUnitMasterRepo(DrugDurationUnitMasterRepo drugDurationUnitMasterRepo) {
		this.drugDurationUnitMasterRepo = drugDurationUnitMasterRepo;
	}

	@Autowired
	public void setDrugFrequencyMasterRepo(DrugFrequencyMasterRepo drugFrequencyMasterRepo) {
		this.drugFrequencyMasterRepo = drugFrequencyMasterRepo;
	}

	@Autowired
	public void setComorbidConditionRepo(ComorbidConditionRepo comorbidConditionRepo) {
		this.comorbidConditionRepo = comorbidConditionRepo;
	}

	@Autowired
	public void setCompFeedsRepo(CompFeedsRepo compFeedsRepo) {
		this.compFeedsRepo = compFeedsRepo;
	}

	@Autowired
	public void setFundalHeightRepo(FundalHeightRepo fundalHeightRepo) {
		this.fundalHeightRepo = fundalHeightRepo;
	}

	@Autowired
	public void setGrossMotorMilestoneRepo(GrossMotorMilestoneRepo grossMotorMilestoneRepo) {
		this.grossMotorMilestoneRepo = grossMotorMilestoneRepo;
	}

	@Autowired
	public void setServiceMasterRepo(ServiceMasterRepo serviceMasterRepo) {
		this.serviceMasterRepo = serviceMasterRepo;
	}

	@Autowired
	public void setCounsellingTypeRepo(CounsellingTypeRepo counsellingTypeRepo) {
		this.counsellingTypeRepo = counsellingTypeRepo;
	}

	@Autowired
	public void setInstituteRepo(InstituteRepo instituteRepo) {
		this.instituteRepo = instituteRepo;
	}

	@Autowired
	public void setPersonalHabitTypeRepo(PersonalHabitTypeRepo personalHabitTypeRepo) {
		this.personalHabitTypeRepo = personalHabitTypeRepo;
	}


	@Autowired
	public void setPregOutcomeRepo(PregOutcomeRepo pregOutcomeRepo) {
		this.pregOutcomeRepo = pregOutcomeRepo;
	}

	@Autowired
	public void setDiseaseTypeRepo(DiseaseTypeRepo diseaseTypeRepo) {
		this.diseaseTypeRepo = diseaseTypeRepo;
	}

	@Autowired
	public void setComplicationTypesRepo(ComplicationTypesRepo complicationTypesRepo) {
		this.complicationTypesRepo = complicationTypesRepo;
	}

	@Autowired
	private ServiceFacilityMasterRepo serviceFacilityMasterRepo;

	public String getCommonNurseMasterDataForGenopdAncNcdcarePnc(Integer visitCategoryID, Integer providerServiceMapID,
			String gender) {
		Map<String, Object> resMap = new HashMap<String, Object>();

		ArrayList<Object[]> allergicReactionTypes = allergicReactionTypesRepo.getAllergicReactionTypes();
		ArrayList<Object[]> bloodGroups = bloodGroupsRepo.getBloodGroups();
		ArrayList<Object[]> childVaccinations = childVaccinationsRepo.getChildVaccinations();
		ArrayList<Object[]> deliveryPlace = deliveryPlaceRepo.getDeliveryPlaces();
		ArrayList<Object[]> deliveryType = deliveryTypeRepo.getDeliveryTypes();
		ArrayList<Object[]> developmentProblems = developmentProblemsRepo.getDevelopmentProblems();
		ArrayList<Object[]> gestation = gestationRepo.getGestationTypes();
		// # illness history
		ArrayList<Object[]> illnessTypes = illnessTypesRepo.getIllnessTypes(visitCategoryID);
		ArrayList<Object[]> jointTypes = jointTypesRepo.getJointTypes();
		ArrayList<Object[]> menstrualCycleLengths = menstrualCycleRangeRepo.getMenstrualCycleRanges("Cycle Length");
		ArrayList<Object[]> menstrualCycleBloodFlowDuration = menstrualCycleRangeRepo
				.getMenstrualCycleRanges(" Blood Flow Duration");
		ArrayList<Object[]> menstrualCycleStatus = menstrualCycleStatusRepo.getMenstrualCycleStatuses(visitCategoryID);
		ArrayList<Object[]> menstrualProblem = menstrualProblemRepo.getMenstrualProblems();
		ArrayList<Object[]> musculoskeletalLateralityTypes = musculoskeletalRepo.getMusculoskeletalvalues("Laterality");
		ArrayList<Object[]> musculoskeletalAbnormalityTypes = musculoskeletalRepo
				.getMusculoskeletalvalues("Abnormality");
		ArrayList<Object[]> pregDuration = pregDurationRepo.getPregDurationTypes();
		// # surgery history
		ArrayList<Object[]> surgeryTypes = surgeryTypesRepo.getSurgeryTypes(visitCategoryID, gender);
		// # comorbidity history
		ArrayList<Object[]> comorbidConditions = comorbidConditionRepo.getComorbidConditions(visitCategoryID);
		ArrayList<Object[]> grossMotorMilestones = grossMotorMilestoneRepo.getGrossMotorMilestones();
		ArrayList<Object[]> fundalHeights = fundalHeightRepo.getFundalHeights();
		ArrayList<Object[]> feedTypes = compFeedsRepo.getCompFeeds("Feed Type");
		ArrayList<Object[]> compFeedAges = compFeedsRepo.getCompFeeds("Comp Feed Age");
		ArrayList<Object[]> compFeedServings = compFeedsRepo.getCompFeeds("Comp Feed Serving ");
		ArrayList<Object[]> pregOutcomes = pregOutcomeRepo.getPregOutcomes();
		ArrayList<Object[]> birthComplications = complicationTypesRepo.getComplicationTypes("Birth Complication");
		ArrayList<Object[]> deliveryComplicationTypes = complicationTypesRepo
				.getComplicationTypes("Delivery Complication");
		ArrayList<Object[]> postpartumComplicationTypes = complicationTypesRepo
				.getComplicationTypes("Postpartum Complication");
		ArrayList<Object[]> pregComplicationTypes = complicationTypesRepo
				.getComplicationTypes("Pregnancy Complication");
		ArrayList<Object[]> postNatalComplications = complicationTypesRepo
				.getComplicationTypes("Postnatal Complication");

		// newely added masters, 10-07-2020
		ArrayList<Object[]> typeOfAbbortion = complicationTypesRepo.getComplicationTypes("typeOfAbortion");
		ArrayList<Object[]> postAbortionComplications = complicationTypesRepo
				.getComplicationTypes("PostAbortionComplications");

		ArrayList<ServiceFacilityMaster> serviceFacility = serviceFacilityMasterRepo.findByDeleted(false);

		resMap.put("typeOfAbortion", ComplicationTypes.getComplicationTypes(typeOfAbbortion, 0));
		resMap.put("postAbortionComplications", ComplicationTypes.getComplicationTypes(postAbortionComplications, 0));
		resMap.put("serviceFacilities", serviceFacility);

		// newborn and birth complications are same

		// existing
		ArrayList<Object[]> ccList = chiefComplaintMasterRepo.getChiefComplaintMaster();

		ArrayList<Object[]> DiseaseTypes = diseaseTypeRepo.getDiseaseTypes();

		ArrayList<Object[]> tobaccoUseStatus = personalHabitTypeRepo.getPersonalHabitTypeMaster("Tobacco Use Status");
		ArrayList<Object[]> typeOfTobaccoProducts = personalHabitTypeRepo
				.getPersonalHabitTypeMaster("Type of Tobacco Use");
		ArrayList<Object[]> alcoholUseStatus = personalHabitTypeRepo
				.getPersonalHabitTypeMaster("Alcohol Intake Status");
		ArrayList<Object[]> typeOfAlcoholProducts = personalHabitTypeRepo.getPersonalHabitTypeMaster("Type of Alcohol");
		ArrayList<Object[]> frequencyOfAlcoholIntake = personalHabitTypeRepo
				.getPersonalHabitTypeMaster("Frequency of Alcohol Intake");
		ArrayList<Object[]> quantityOfAlcoholIntake = personalHabitTypeRepo
				.getPersonalHabitTypeMaster("Average Quantity of Alcohol consumption");
		ArrayList<Object[]> familyMemberTypes = familyMemberMasterRepo.getFamilyMemberTypeMaster();
		// ArrayList<Object[]> labTests = labTestMasterRepo.getLabTestMaster();
		ArrayList<Object[]> procedures = procedureRepo.getProcedureMasterData(providerServiceMapID, gender);

		// PNC specific master data
		if (visitCategoryID == 5) {
			ArrayList<Object[]> healthStatuses = newbornHealthStatusRepo.getnewBornHealthStatuses();
			resMap.put("newbornHealthStatuses", NewbornHealthStatus.getNewbornHealthStatuses(healthStatuses));
		}

		ArrayList<Object[]> optionalVaccinations = optionalVaccinationsRepo.getOptionalVaccinations();

		resMap.put("AllergicReactionTypes", AllergicReactionTypes.getAllergicReactionTypes(allergicReactionTypes));
		resMap.put("bloodGroups", BloodGroups.getBloodGroups(bloodGroups));
		resMap.put("childVaccinations", ChildVaccinations.getChildVaccinations(childVaccinations));
		resMap.put("deliveryPlaces", DeliveryPlace.getDeliveryPlace(deliveryPlace));
		resMap.put("deliveryTypes", DeliveryType.getDeliveryType(deliveryType));
		resMap.put("developmentProblems", DevelopmentProblems.getDevelopmentProblems(developmentProblems));
		resMap.put("gestation", Gestation.getGestations(gestation));
		resMap.put("illnessTypes", IllnessTypes.getIllnessTypes(illnessTypes));
		resMap.put("jointTypes", JointTypes.getJointTypes(jointTypes));
		resMap.put("menstrualCycleLengths", MenstrualCycleRange.getMenstrualCycleRanges(menstrualCycleLengths));
		resMap.put("menstrualCycleBloodFlowDuration",
				MenstrualCycleRange.getMenstrualCycleRanges(menstrualCycleBloodFlowDuration));
		resMap.put("menstrualCycleStatus", MenstrualCycleStatus.getMenstrualCycleStatuses(menstrualCycleStatus));
		resMap.put("menstrualProblem", MenstrualProblem.getMenstrualProblems(menstrualProblem));
		resMap.put("musculoskeletalLateralityTypes",
				Musculoskeletal.getMusculoskeletals(musculoskeletalLateralityTypes));
		resMap.put("musculoskeletalAbnormalityTypes",
				Musculoskeletal.getMusculoskeletals(musculoskeletalAbnormalityTypes));
		resMap.put("pregDuration", PregDuration.getPregDurationValues(pregDuration));
		resMap.put("surgeryTypes", SurgeryTypes.getSurgeryTypes(surgeryTypes));
		resMap.put("comorbidConditions", ComorbidCondition.getComorbidConditions(comorbidConditions));
		resMap.put("grossMotorMilestones", GrossMotorMilestone.getGrossMotorMilestone(grossMotorMilestones));
		resMap.put("fundalHeights", FundalHeight.getFundalHeights(fundalHeights));
		resMap.put("feedTypes", CompFeeds.getCompFeeds(feedTypes));
		resMap.put("compFeedAges", CompFeeds.getCompFeeds(compFeedAges));
		resMap.put("compFeedServings", CompFeeds.getCompFeeds(compFeedServings));
		resMap.put("pregOutcomes", PregOutcome.getPregOutcomes(pregOutcomes));

		resMap.put("birthComplications", ComplicationTypes.getComplicationTypes(birthComplications, 0));
		resMap.put("deliveryComplicationTypes", ComplicationTypes.getComplicationTypes(deliveryComplicationTypes, 2));
		resMap.put("postpartumComplicationTypes",
				ComplicationTypes.getComplicationTypes(postpartumComplicationTypes, 3));
		resMap.put("pregComplicationTypes", ComplicationTypes.getComplicationTypes(pregComplicationTypes, 1));
		resMap.put("postNatalComplications", ComplicationTypes.getComplicationTypes(postNatalComplications, 0));
		resMap.put("newBornComplications", ComplicationTypes.getComplicationTypes(birthComplications, 0));

		// existing
		resMap.put("chiefComplaintMaster", ChiefComplaintMaster.getChiefComplaintMasters(ccList));
		resMap.put("DiseaseTypes", DiseaseType.getDiseaseTypes(DiseaseTypes));
		resMap.put("tobaccoUseStatus", PersonalHabitType.getPersonalHabitTypeMasterData(tobaccoUseStatus));
		resMap.put("typeOfTobaccoProducts", PersonalHabitType.getPersonalHabitTypeMasterData(typeOfTobaccoProducts));
		resMap.put("alcoholUseStatus", PersonalHabitType.getPersonalHabitTypeMasterData(alcoholUseStatus));
		resMap.put("typeOfAlcoholProducts", PersonalHabitType.getPersonalHabitTypeMasterData(typeOfAlcoholProducts));
		resMap.put("frequencyOfAlcoholIntake",
				PersonalHabitType.getPersonalHabitTypeMasterData(frequencyOfAlcoholIntake));
		resMap.put("quantityOfAlcoholIntake",
				PersonalHabitType.getPersonalHabitTypeMasterData(quantityOfAlcoholIntake));
		resMap.put("familyMemberTypes", FamilyMemberType.getFamilyMemberTypeMasterData(familyMemberTypes));

		// resMap.put("labTests", LabTestMaster.getLabTestMasters(labTests));

		resMap.put("procedures", ProcedureData.getProcedures(procedures));
		resMap.put("vaccineMasterData", OptionalVaccinations.getOptionalVaccinations(optionalVaccinations));

		if (visitCategoryID == 8 || visitCategoryID == 10) {
			resMap.put("covidSymptomsMaster", covidSymptomsMasterRepo.findByDeleted(false));
			resMap.put("covidContactHistoryMaster", covidContactHistoryMasterRepo.findByDeleted(false));
			resMap.put("covidRecommendationMaster", covidRecommnedationMasterRepo.findByDeleted(false));
		}
		
		//To Fetch foetal monitor Test Master Details
		if (visitCategoryID == 4) {
			ArrayList<Object[]> fetoTestList = foetakMonitorTestRepo.getFoetalMonitorTestsDetails(providerServiceMapID);
			resMap.put("fetosenseTestMaster", FoetalMonitorTestMaster.getFoetalMonitorMasters(fetoTestList));
		}

		return new Gson().toJson(resMap);
	}

	public String getCommonDoctorMasterDataForGenopdAncNcdcarePnc(Integer visitCategoryID, int psmID, String gender,
			Integer facilityID, Integer vanID) {
		Map<String, Object> resMap = new HashMap<>();
		ArrayList<Object[]> additionalServices = serviceMasterRepo.getAdditionalServices();
		// Institute institute = new Institute();
		ArrayList<Object[]> instituteDetails = instituteRepo.getInstituteDetails(psmID);
		resMap.put("higherHealthCare", Institute.getinstituteDetails(instituteDetails));
		resMap.put("additionalServices", ServiceMaster.getServiceMaster(additionalServices));
		if (visitCategoryID != 7) {
			ArrayList<Object[]> counsellingTypes = counsellingTypeRepo.getCounsellingTypes();
			resMap.put("counsellingTypes", CounsellingType.getCounsellingType(counsellingTypes));
		} else {
			ArrayList<Object[]> procedures = procedureRepo.getProcedureMasterData(psmID, gender);
			ArrayList<Object[]> ccList = chiefComplaintMasterRepo.getChiefComplaintMaster();

			resMap.put("procedures", ProcedureData.getProcedures(procedures));
			resMap.put("chiefComplaintMaster", ChiefComplaintMaster.getChiefComplaintMasters(ccList));
		}

		ArrayList<Object[]> ifmList = itemFormMasterRepo.getItemFormMaster();
		ArrayList<Object[]> ddmList = drugDoseMasterRepo.getDrugDoseMaster();
		ArrayList<Object[]> ddumList = drugDurationUnitMasterRepo.getDrugDurationUnitMaster();
		ArrayList<Object[]> dfrmList = drugFrequencyMasterRepo.getDrugFrequencyMaster();
		ArrayList<Object[]> roaList = routeOfAdminRepo.getRouteOfAdminList();
		// ArrayList<Object[]> edlList=itemMasterRepo.searchEdl(psmID);
		ArrayList<ItemMaster> NonedlList = itemMasterRepo.searchEdl(psmID);
		// edlist.get()
		// foreach()
		for (int i = 0; i < NonedlList.size(); i++) {
			if(NonedlList.get(i).getUom() != null)
			      NonedlList.get(i).setUnitOfMeasurement(NonedlList.get(i).getUom().getuOMName());
		}
		ArrayList<V_DrugPrescription> itemList = new ArrayList<>();
		if (facilityID == null || facilityID <= 0) {
			Integer fID = masterVanRepo.getFacilityID(vanID);
			if (fID != null && fID > 0)
				facilityID = fID;
		}

		itemList = v_DrugPrescriptionRepo.getItemListForFacility(facilityID);
		resMap.put("drugFormMaster", ItemFormMaster.getItemFormList(ifmList));
		resMap.put("drugDoseMaster", DrugDoseMaster.getDrugDoseMasters(ddmList));
		resMap.put("drugDurationUnitMaster", DrugDurationUnitMaster.getDrugDurationUnitMaster(ddumList));
		resMap.put("drugFrequencyMaster", DrugFrequencyMaster.getDrugFrequencyMaster(dfrmList));
		resMap.put("routeOfAdmin", RouteOfAdmin.getRouteOfAdminList(roaList));
		resMap.put("itemMaster", itemList);
		resMap.put("NonEdlMaster", NonedlList);
		// NCD Care specific master data
		if (visitCategoryID == 3) {
			resMap.put("ncdCareConditions", NCDScreeningCondition.getNCDScreeningCondition(
					(ArrayList<Object[]>) ncdScreeningMasterServiceImpl.getNCDScreeningConditions()));
			resMap.put("ncdCareTypes",
					NCDCareType.getNCDCareTypes((ArrayList<Object[]>) ncdCareTypeRepo.getNCDCareTypes()));
		}

		return new Gson().toJson(resMap);
	}

}
