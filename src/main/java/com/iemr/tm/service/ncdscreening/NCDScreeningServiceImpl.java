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
package com.iemr.tm.service.ncdscreening;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iemr.tm.data.anc.BenAllergyHistory;
import com.iemr.tm.data.anc.BenChildDevelopmentHistory;
import com.iemr.tm.data.anc.BenFamilyHistory;
import com.iemr.tm.data.anc.BenMedHistory;
import com.iemr.tm.data.anc.BenMenstrualDetails;
import com.iemr.tm.data.anc.BenPersonalHabit;
import com.iemr.tm.data.anc.ChildFeedingDetails;
import com.iemr.tm.data.anc.PerinatalHistory;
import com.iemr.tm.data.anc.WrapperAncFindings;
import com.iemr.tm.data.anc.WrapperBenInvestigationANC;
import com.iemr.tm.data.anc.WrapperChildOptionalVaccineDetail;
import com.iemr.tm.data.anc.WrapperComorbidCondDetails;
import com.iemr.tm.data.anc.WrapperFemaleObstetricHistory;
import com.iemr.tm.data.anc.WrapperImmunizationHistory;
import com.iemr.tm.data.anc.WrapperMedicationHistory;
import com.iemr.tm.data.ncdScreening.IDRSData;
import com.iemr.tm.data.ncdScreening.NCDScreening;
import com.iemr.tm.data.ncdScreening.PhysicalActivityType;
import com.iemr.tm.data.nurse.BenAnthropometryDetail;
import com.iemr.tm.data.nurse.BenPhysicalVitalDetail;
import com.iemr.tm.data.nurse.BeneficiaryVisitDetail;
import com.iemr.tm.data.nurse.CommonUtilityClass;
import com.iemr.tm.data.quickConsultation.BenChiefComplaint;
import com.iemr.tm.data.quickConsultation.PrescribedDrugDetail;
import com.iemr.tm.data.quickConsultation.PrescriptionDetail;
import com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ;
import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.nurse.BenVisitDetailRepo;
import com.iemr.tm.repo.nurse.anc.BenAdherenceRepo;
import com.iemr.tm.repo.nurse.ncdscreening.IDRSDataRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;
import com.iemr.tm.service.tele_consultation.TeleConsultationServiceImpl;
import com.iemr.tm.utils.mapper.InputMapper;

@Service
public class NCDScreeningServiceImpl implements NCDScreeningService {

	private NCDScreeningNurseServiceImpl ncdScreeningNurseServiceImpl;
	private CommonNurseServiceImpl commonNurseServiceImpl;
	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	private LabTechnicianServiceImpl labTechnicianServiceImpl;

	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	@Autowired
	private CommonServiceImpl commonServiceImpl;
	@Autowired
	private TeleConsultationServiceImpl teleConsultationServiceImpl;

	@Autowired
	private BenVisitDetailRepo benVisitDetailRepo;

	@Autowired
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;

	@Autowired
	private PrescriptionDetailRepo prescriptionDetailRepo;

	@Autowired

	private NCDSCreeningDoctorServiceImpl ncdSCreeningDoctorServiceImpl;
	@Autowired
	private IDRSDataRepo iDrsDataRepo;
	
	@Autowired
	private BenChiefComplaintRepo benChiefComplaintRepo;
	@Autowired
	private BenAdherenceRepo benAdherenceRepo;

	@Autowired
	public void setLabTechnicianServiceImpl(LabTechnicianServiceImpl labTechnicianServiceImpl) {
		this.labTechnicianServiceImpl = labTechnicianServiceImpl;
	}

	@Autowired
	public void setBeneficiaryFlowStatusRepo(BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo) {
		this.beneficiaryFlowStatusRepo = beneficiaryFlowStatusRepo;
	}

	@Autowired
	public void setCommonBenStatusFlowServiceImpl(CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl) {
		this.commonBenStatusFlowServiceImpl = commonBenStatusFlowServiceImpl;
	}

	@Autowired
	public void setCommonNurseServiceImpl(CommonNurseServiceImpl commonNurseServiceImpl) {
		this.commonNurseServiceImpl = commonNurseServiceImpl;
	}

	@Autowired
	public void setNcdScreeningNurseServiceImpl(NCDScreeningNurseServiceImpl ncdScreeningNurseServiceImpl) {
		this.ncdScreeningNurseServiceImpl = ncdScreeningNurseServiceImpl;
	}

	@Autowired
	public void setCommonDoctorServiceImpl(CommonDoctorServiceImpl commonDoctorServiceImpl) {
		this.commonDoctorServiceImpl = commonDoctorServiceImpl;
	}

	@Override
	public String saveNCDScreeningNurseData(JsonObject requestOBJ, String Authorization) throws Exception {

		Long saveSuccessFlag = null;
		TeleconsultationRequestOBJ tcRequestOBJ = null;
		Long benVisitCode = null;
		// check if visit details data is not null
		if (requestOBJ != null && requestOBJ.has("visitDetails") && !requestOBJ.get("visitDetails").isJsonNull()) {
			CommonUtilityClass nurseUtilityClass = InputMapper.gson().fromJson(requestOBJ, CommonUtilityClass.class);
			// Call method to save visit details data
			Map<String, Long> visitIdAndCodeMap = saveBenVisitDetails(requestOBJ.getAsJsonObject("visitDetails"),
					nurseUtilityClass);

			// 07-06-2018 visit code
			Long benVisitID = null;

			if (visitIdAndCodeMap != null && visitIdAndCodeMap.size() > 0 && visitIdAndCodeMap.containsKey("visitID")
					&& visitIdAndCodeMap.containsKey("visitCode")) {
				benVisitID = visitIdAndCodeMap.get("visitID");
				benVisitCode = visitIdAndCodeMap.get("visitCode");

				nurseUtilityClass.setVisitCode(benVisitCode);
				nurseUtilityClass.setBenVisitID(benVisitID);
			} else {
				Map<String, String> responseMap = new HashMap<String, String>();
				responseMap.put("response", "Data already saved");
				return new Gson().toJson(responseMap);
			}

			// check if visit details data saved successfully
			Long historySaveSuccessFlag = null;
			Long vitalSaveSuccessFlag = null;
			Long idrsFlag = null;
			Long physicalActivityFlag = null;
			Integer i = null;

			JsonObject tmpOBJ = requestOBJ.getAsJsonObject("visitDetails").getAsJsonObject("visitDetails");
			// Getting benflowID for ben status update
			Long benFlowID = null;

			// Above if block code replaced by below line
			benFlowID = nurseUtilityClass.getBenFlowID();

			if (benVisitID != null && benVisitID > 0) {
				tcRequestOBJ = commonServiceImpl.createTcRequest(requestOBJ, nurseUtilityClass, Authorization);
				// call method to save History data
				if (requestOBJ.has("historyDetails") && !requestOBJ.get("historyDetails").isJsonNull())
					historySaveSuccessFlag = saveBenNCDCareHistoryDetails(requestOBJ.getAsJsonObject("historyDetails"),
							benVisitID, benVisitCode);
				// call method to save Vital data
				vitalSaveSuccessFlag = saveBenNCDCareVitalDetails(requestOBJ.getAsJsonObject("vitalDetails"),
						benVisitID, benVisitCode);
				// call method to save IDRS data
				idrsFlag = saveidrsDetails(requestOBJ.getAsJsonObject("idrsDetails"), benVisitID, benVisitCode);
				// call method to save physical activity
				physicalActivityFlag = savePhysicalActivityDetails(
						requestOBJ.getAsJsonObject("historyDetails").getAsJsonObject("physicalActivityHistory"),
						benVisitID, benVisitCode);
			} else {
				throw new RuntimeException("Error occurred while creating beneficiary visit");
			}
			if ((null != historySaveSuccessFlag && historySaveSuccessFlag > 0)
					&& (null != vitalSaveSuccessFlag && vitalSaveSuccessFlag > 0)) {

				/**
				 * We have to write new code to update ben status flow new logic
				 */
				int J = updateBenFlowNurseAfterNurseActivityANC(tmpOBJ, benVisitID, benFlowID, benVisitCode,
						nurseUtilityClass.getVanID(), tcRequestOBJ);

				if (J > 0)
					saveSuccessFlag = historySaveSuccessFlag;
				else
					throw new RuntimeException("Error occurred while saving data. Beneficiary status update failed");

				if (J > 0 && tcRequestOBJ != null && tcRequestOBJ.getWalkIn() == false) {
					int k = sMSGatewayServiceImpl.smsSenderGateway("schedule", nurseUtilityClass.getBeneficiaryRegID(),
							tcRequestOBJ.getSpecializationID(), tcRequestOBJ.getTmRequestID(), null,
							nurseUtilityClass.getCreatedBy(),
							tcRequestOBJ.getAllocationDate() != null ? String.valueOf(tcRequestOBJ.getAllocationDate())
									: "",
							null, Authorization);
				}

			} else {
				throw new RuntimeException("Error occurred while saving data");
			}
		} else {
			throw new Exception("Invalid input");
		}
		Map<String, String> responseMap = new HashMap<String, String>();
		if (benVisitCode != null) {
			responseMap.put("visitCode", benVisitCode.toString());
		}
		if (null != saveSuccessFlag && saveSuccessFlag > 0) {
			responseMap.put("response", "Data saved successfully");
		} else {
			responseMap.put("response", "Unable to save data");
		}
		return new Gson().toJson(responseMap);

	}
	
	@Override
	public void deleteVisitDetails(JsonObject requestOBJ) throws Exception {
		if (requestOBJ != null && requestOBJ.has("visitDetails") && !requestOBJ.get("visitDetails").isJsonNull()) {

			CommonUtilityClass nurseUtilityClass = InputMapper.gson().fromJson(requestOBJ, CommonUtilityClass.class);

			Long visitCode = benVisitDetailRepo.getVisitCode(nurseUtilityClass.getBeneficiaryRegID(),
					nurseUtilityClass.getProviderServiceMapID());

			if (visitCode != null) {
				benChiefComplaintRepo.deleteVisitDetails(visitCode);
				benAdherenceRepo.deleteVisitDetails(visitCode);
				benVisitDetailRepo.deleteVisitDetails(visitCode);
			}

		}

	}

	// method for updating ben flow status flag for nurse
	private int updateBenFlowNurseAfterNurseActivityANC(JsonObject tmpOBJ, Long benVisitID, Long benFlowID,
			Long benVisitCode, Integer vanID, TeleconsultationRequestOBJ tcRequestOBJ) {
		short nurseFlag = (short) 9;
		short docFlag = (short) 1;
		short labIteration = (short) 0;

		short specialistFlag = (short) 0;
		Timestamp tcDate = null;
		Integer tcSpecialistUserID = null;

		if (tcRequestOBJ != null && tcRequestOBJ.getUserID() != null && tcRequestOBJ.getAllocationDate() != null) {
			specialistFlag = (short) 1;
			tcDate = tcRequestOBJ.getAllocationDate();
			tcSpecialistUserID = tcRequestOBJ.getUserID();
		} else
			specialistFlag = (short) 0;

		int i = commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(benFlowID,
				tmpOBJ.get("beneficiaryRegID").getAsLong(), benVisitID, tmpOBJ.get("visitReason").getAsString(),
				tmpOBJ.get("visitCategory").getAsString(), nurseFlag, docFlag, labIteration, (short) 0, (short) 0,
				benVisitCode, vanID, specialistFlag, tcDate, tcSpecialistUserID);

		return i;

	}

	/**
	 * @param requestOBJ
	 * @return success or failure flag for visitDetails data saving
	 */
	public Map<String, Long> saveBenVisitDetails(JsonObject visitDetailsOBJ, CommonUtilityClass nurseUtilityClass)
			throws Exception {
		Map<String, Long> visitIdAndCodeMap = new HashMap<>();
		Long benVisitID = null;
		int adherenceSuccessFlag = 0;
		int investigationSuccessFlag = 0;
		if (visitDetailsOBJ != null && visitDetailsOBJ.has("visitDetails")
				&& !visitDetailsOBJ.get("visitDetails").isJsonNull()) {

			BeneficiaryVisitDetail benVisitDetailsOBJ = InputMapper.gson().fromJson(visitDetailsOBJ.get("visitDetails"),
					BeneficiaryVisitDetail.class);
			int i = commonNurseServiceImpl.getMaxCurrentdate(benVisitDetailsOBJ.getBeneficiaryRegID(),
					benVisitDetailsOBJ.getVisitReason(), benVisitDetailsOBJ.getVisitCategory());
			if (i < 1) {
				benVisitID = commonNurseServiceImpl.saveBeneficiaryVisitDetails(benVisitDetailsOBJ);

				// visit code
				Long benVisitCode = commonNurseServiceImpl.generateVisitCode(benVisitID, nurseUtilityClass.getVanID(),
						nurseUtilityClass.getSessionID());

				if (benVisitID != null && benVisitID > 0 && benVisitCode != null && benVisitCode > 0) {
					if (visitDetailsOBJ.has("chiefComplaints")
							&& !visitDetailsOBJ.get("chiefComplaints").isJsonNull()) {
						BenChiefComplaint[] benChiefComplaintArray = InputMapper.gson()
								.fromJson(visitDetailsOBJ.get("chiefComplaints"), BenChiefComplaint[].class);

						List<BenChiefComplaint> benChiefComplaintList = Arrays.asList(benChiefComplaintArray);
						if (null != benChiefComplaintList && benChiefComplaintList.size() > 0) {
							for (BenChiefComplaint benChiefComplaint : benChiefComplaintList) {
								benChiefComplaint.setBenVisitID(benVisitID);
								benChiefComplaint.setVisitCode(benVisitCode);
							}
						}
						// Save Beneficiary Chief Complaints
						commonNurseServiceImpl.saveBenChiefComplaints(benChiefComplaintList);
					}
				}
				visitIdAndCodeMap.put("visitID", benVisitID);
				visitIdAndCodeMap.put("visitCode", benVisitCode);
			}
		}
		return visitIdAndCodeMap;
	}

	private int updateBenFlowNurseAfterNurseActivityANC(Long benRegID, Long benVisitID, Long benFlowID,
			String visitReason, String visitCategory, Boolean isScreeningDone, Long benVisitCode, Integer vanID) {
		short nurseFlag;
		short docFlag = (short) 0;
		short labIteration = (short) 0;

		short specialistFlag = (short) 0;
		Timestamp tcDate = null;
		Integer tcSpecialistUserID = null;

		if (isScreeningDone != null && isScreeningDone == true)
			nurseFlag = (short) 9;
		else
			nurseFlag = (short) 100;

		int rs = commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseActivity(benFlowID, benRegID, benVisitID,
				visitReason, visitCategory, nurseFlag, docFlag, labIteration, (short) 0, (short) 0, benVisitCode, vanID,
				specialistFlag, tcDate, tcSpecialistUserID);

		return rs;
	}

	public Long saveNCDScreeningVitalDetails(JsonObject jsonObject, Long benVisitID, Long benVisitCode)
			throws Exception {

		Long vitalSuccessFlag = null;
		JsonElement ncdScreeningDetails = jsonObject.get("ncdScreeningDetails");

		BenAnthropometryDetail anthropometryDetail = null;
		BenPhysicalVitalDetail physicalVitalDetail = null;

		anthropometryDetail = InputMapper.gson().fromJson(ncdScreeningDetails, BenAnthropometryDetail.class);

		physicalVitalDetail = InputMapper.gson().fromJson(ncdScreeningDetails, BenPhysicalVitalDetail.class);

		Long saveAnthropometryDetail = null;
		if (null != anthropometryDetail) {
			anthropometryDetail.setBenVisitID(benVisitID);
			anthropometryDetail.setVisitCode(benVisitCode);
			saveAnthropometryDetail = commonNurseServiceImpl
					.saveBeneficiaryPhysicalAnthropometryDetails(anthropometryDetail);
		}
		Long savePhysicalVitalDetails = null;
		if (null != physicalVitalDetail) {
			physicalVitalDetail.setBenVisitID(benVisitID);
			physicalVitalDetail.setVisitCode(benVisitCode);
			savePhysicalVitalDetails = commonNurseServiceImpl.saveBeneficiaryPhysicalVitalDetails(physicalVitalDetail);
		}

		if ((null != saveAnthropometryDetail && saveAnthropometryDetail > 0)
				&& (null != savePhysicalVitalDetails && savePhysicalVitalDetails > 0)) {
			vitalSuccessFlag = saveAnthropometryDetail;
		}

		return vitalSuccessFlag;
	}

	/**
	 * @param requestOBJ
	 * @return success or failure flag for visitDetails data saving
	 */
	public Long saveBenNCDCareHistoryDetails(JsonObject ncdCareHistoryOBJ, Long benVisitID, Long benVisitCode)
			throws Exception {
		Long pastHistorySuccessFlag = null;
		Long comrbidSuccessFlag = null;
		Long medicationSuccessFlag = null;
		Long obstetricSuccessFlag = null;
		Integer menstrualHistorySuccessFlag = null;
		Long familyHistorySuccessFlag = null;
		Integer personalHistorySuccessFlag = null;
		Long allergyHistorySuccessFlag = null;
		Long childVaccineSuccessFlag = null;
		Long immunizationSuccessFlag = null;
		Long developmentHistorySuccessFlag = null;
		Long childFeedingSuccessFlag = null;
		Long perinatalHistorySuccessFlag = null;

		// Save past History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("pastHistory")
				&& !ncdCareHistoryOBJ.get("pastHistory").isJsonNull()) {
			BenMedHistory benMedHistory = InputMapper.gson().fromJson(ncdCareHistoryOBJ.get("pastHistory"),
					BenMedHistory.class);
			if (null != benMedHistory) {
				benMedHistory.setBenVisitID(benVisitID);
				benMedHistory.setVisitCode(benVisitCode);
				pastHistorySuccessFlag = commonNurseServiceImpl.saveBenPastHistory(benMedHistory);
			}

		} else {
			pastHistorySuccessFlag = new Long(1);
		}

		// Save Comorbidity/concurrent Conditions
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("comorbidConditions")
				&& !ncdCareHistoryOBJ.get("comorbidConditions").isJsonNull()) {
			WrapperComorbidCondDetails wrapperComorbidCondDetails = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("comorbidConditions"), WrapperComorbidCondDetails.class);
			if (null != wrapperComorbidCondDetails) {
				wrapperComorbidCondDetails.setBenVisitID(benVisitID);
				wrapperComorbidCondDetails.setVisitCode(benVisitCode);
				comrbidSuccessFlag = commonNurseServiceImpl.saveBenComorbidConditions(wrapperComorbidCondDetails);
			}
		} else {
			comrbidSuccessFlag = new Long(1);
		}

		// Save Medication History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("medicationHistory")
				&& !ncdCareHistoryOBJ.get("medicationHistory").isJsonNull()) {
			WrapperMedicationHistory wrapperMedicationHistory = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("medicationHistory"), WrapperMedicationHistory.class);
			if (null != wrapperMedicationHistory
					&& wrapperMedicationHistory.getBenMedicationHistoryDetails().size() > 0) {
				wrapperMedicationHistory.setBenVisitID(benVisitID);
				wrapperMedicationHistory.setVisitCode(benVisitCode);
				medicationSuccessFlag = commonNurseServiceImpl.saveBenMedicationHistory(wrapperMedicationHistory);
			} else {
				medicationSuccessFlag = new Long(1);
			}

		} else {
			medicationSuccessFlag = new Long(1);
		}

		// Save Past Obstetric History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("femaleObstetricHistory")
				&& !ncdCareHistoryOBJ.get("femaleObstetricHistory").isJsonNull()) {
			WrapperFemaleObstetricHistory wrapperFemaleObstetricHistory = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("femaleObstetricHistory"), WrapperFemaleObstetricHistory.class);

			if (wrapperFemaleObstetricHistory != null) {
				wrapperFemaleObstetricHistory.setBenVisitID(benVisitID);
				wrapperFemaleObstetricHistory.setVisitCode(benVisitCode);
				obstetricSuccessFlag = commonNurseServiceImpl.saveFemaleObstetricHistory(wrapperFemaleObstetricHistory);
			} else {
				// Female Obstetric Details not provided.
			}

		} else {
			obstetricSuccessFlag = new Long(1);
		}

		// Save Menstrual History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("menstrualHistory")
				&& !ncdCareHistoryOBJ.get("menstrualHistory").isJsonNull()) {
			BenMenstrualDetails menstrualDetails = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("menstrualHistory"), BenMenstrualDetails.class);
			if (null != menstrualDetails) {
				menstrualDetails.setBenVisitID(benVisitID);
				menstrualDetails.setVisitCode(benVisitCode);
				menstrualHistorySuccessFlag = commonNurseServiceImpl.saveBenMenstrualHistory(menstrualDetails);
			}

		} else {
			menstrualHistorySuccessFlag = 1;
		}

		// Save Family History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("familyHistory")
				&& !ncdCareHistoryOBJ.get("familyHistory").isJsonNull()) {
			BenFamilyHistory benFamilyHistory = InputMapper.gson().fromJson(ncdCareHistoryOBJ.get("familyHistory"),
					BenFamilyHistory.class);
			if (null != benFamilyHistory) {
				benFamilyHistory.setBenVisitID(benVisitID);
				benFamilyHistory.setVisitCode(benVisitCode);
				familyHistorySuccessFlag = commonNurseServiceImpl.saveBenFamilyHistoryNCDScreening(benFamilyHistory);
			}
		} else {
			familyHistorySuccessFlag = new Long(1);
		}

		// Save Personal History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("personalHistory")
				&& !ncdCareHistoryOBJ.get("personalHistory").isJsonNull()) {
			// Save Ben Personal Habits..
			BenPersonalHabit personalHabit = InputMapper.gson().fromJson(ncdCareHistoryOBJ.get("personalHistory"),
					BenPersonalHabit.class);
			if (null != personalHabit) {
				personalHabit.setBenVisitID(benVisitID);
				personalHabit.setVisitCode(benVisitCode);
				personalHistorySuccessFlag = commonNurseServiceImpl.savePersonalHistory(personalHabit);
			}

			BenAllergyHistory benAllergyHistory = InputMapper.gson().fromJson(ncdCareHistoryOBJ.get("personalHistory"),
					BenAllergyHistory.class);
			if (null != benAllergyHistory) {
				benAllergyHistory.setBenVisitID(benVisitID);
				benAllergyHistory.setVisitCode(benVisitCode);
				allergyHistorySuccessFlag = commonNurseServiceImpl.saveAllergyHistory(benAllergyHistory);
			}

		} else {
			personalHistorySuccessFlag = 1;
			allergyHistorySuccessFlag = new Long(1);
		}

		// Save Other/Optional Vaccines History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("childVaccineDetails")
				&& !ncdCareHistoryOBJ.get("childVaccineDetails").isJsonNull()) {
			WrapperChildOptionalVaccineDetail wrapperChildVaccineDetail = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("childVaccineDetails"), WrapperChildOptionalVaccineDetail.class);
			if (null != wrapperChildVaccineDetail) {
				wrapperChildVaccineDetail.setBenVisitID(benVisitID);
				wrapperChildVaccineDetail.setVisitCode(benVisitCode);
				childVaccineSuccessFlag = commonNurseServiceImpl
						.saveChildOptionalVaccineDetail(wrapperChildVaccineDetail);
			} else {
				// Child Optional Vaccine Detail not provided.
			}

		} else {
			childVaccineSuccessFlag = new Long(1);
		}

		// Save Immunization History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("immunizationHistory")
				&& !ncdCareHistoryOBJ.get("immunizationHistory").isJsonNull()) {
			WrapperImmunizationHistory wrapperImmunizationHistory = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("immunizationHistory"), WrapperImmunizationHistory.class);
			if (null != wrapperImmunizationHistory) {
				wrapperImmunizationHistory.setBenVisitID(benVisitID);
				wrapperImmunizationHistory.setVisitCode(benVisitCode);
				immunizationSuccessFlag = commonNurseServiceImpl.saveImmunizationHistory(wrapperImmunizationHistory);
			} else {

				// ImmunizationList Data not Available
			}

		} else {
			immunizationSuccessFlag = new Long(1);
		}

		// Save Development History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("developmentHistory")
				&& !ncdCareHistoryOBJ.get("developmentHistory").isJsonNull()) {
			BenChildDevelopmentHistory benChildDevelopmentHistory = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("developmentHistory"), BenChildDevelopmentHistory.class);

			if (null != benChildDevelopmentHistory) {
				benChildDevelopmentHistory.setBenVisitID(benVisitID);
				benChildDevelopmentHistory.setVisitCode(benVisitCode);
				developmentHistorySuccessFlag = commonNurseServiceImpl
						.saveChildDevelopmentHistory(benChildDevelopmentHistory);
			}

		} else {
			developmentHistorySuccessFlag = new Long(1);
		}

		// Save Feeding History
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("feedingHistory")
				&& !ncdCareHistoryOBJ.get("feedingHistory").isJsonNull()) {
			ChildFeedingDetails childFeedingDetails = InputMapper.gson()
					.fromJson(ncdCareHistoryOBJ.get("feedingHistory"), ChildFeedingDetails.class);

			if (null != childFeedingDetails) {
				childFeedingDetails.setBenVisitID(benVisitID);
				childFeedingDetails.setVisitCode(benVisitCode);
				childFeedingSuccessFlag = commonNurseServiceImpl.saveChildFeedingHistory(childFeedingDetails);
			}

		}
		{
			childFeedingSuccessFlag = new Long(1);
		}

		// Save Perinatal Histroy
		if (ncdCareHistoryOBJ != null && ncdCareHistoryOBJ.has("perinatalHistroy")
				&& !ncdCareHistoryOBJ.get("perinatalHistroy").isJsonNull()) {
			PerinatalHistory perinatalHistory = InputMapper.gson().fromJson(ncdCareHistoryOBJ.get("perinatalHistroy"),
					PerinatalHistory.class);

			if (null != perinatalHistory) {
				perinatalHistory.setBenVisitID(benVisitID);
				perinatalHistory.setVisitCode(benVisitCode);
				perinatalHistorySuccessFlag = commonNurseServiceImpl.savePerinatalHistory(perinatalHistory);
			}

		}
		{
			perinatalHistorySuccessFlag = new Long(1);
		}

		Long historySaveSucccessFlag = null;

		if ((null != pastHistorySuccessFlag && pastHistorySuccessFlag > 0)
				&& (null != comrbidSuccessFlag && comrbidSuccessFlag > 0)
				&& (null != medicationSuccessFlag && medicationSuccessFlag > 0)
				&& (null != obstetricSuccessFlag && obstetricSuccessFlag > 0)
				&& (null != menstrualHistorySuccessFlag && menstrualHistorySuccessFlag > 0)
				&& (null != familyHistorySuccessFlag && familyHistorySuccessFlag > 0)
				&& (null != personalHistorySuccessFlag && personalHistorySuccessFlag > 0)
				&& (null != allergyHistorySuccessFlag && allergyHistorySuccessFlag > 0)
				&& (null != childVaccineSuccessFlag && childVaccineSuccessFlag > 0)
				&& (null != immunizationSuccessFlag && immunizationSuccessFlag > 0)
				&& (null != developmentHistorySuccessFlag && developmentHistorySuccessFlag > 0)
				&& (null != childFeedingSuccessFlag && childFeedingSuccessFlag > 0)
				&& (null != perinatalHistorySuccessFlag && perinatalHistorySuccessFlag > 0)) {

			historySaveSucccessFlag = pastHistorySuccessFlag;
		}
		return historySaveSucccessFlag;
	}

	public Long saveidrsDetails(JsonObject idrsDetailsOBJ, Long benVisitID, Long benVisitCode) throws Exception {
		Long idrsFlag = null;
		// Save Physical Anthropometry && Physical Vital Details
		if (idrsDetailsOBJ != null) {
			IDRSData idrsDetail = InputMapper.gson().fromJson(idrsDetailsOBJ, IDRSData.class);
			String temp = "", temp1 = "";
			if (null != idrsDetail) {
				if (idrsDetail.getQuestionArray() != null && idrsDetail.getQuestionArray().length > 0) {
					IDRSData[] ar = idrsDetail.getQuestionArray();
					for (int i = 0; i < ar.length; i++) {
						idrsDetail = InputMapper.gson().fromJson(idrsDetailsOBJ, IDRSData.class);
						temp = "";
						temp1 = "";
						idrsDetail.setIdrsQuestionID(ar[i].getIdrsQuestionID());
						idrsDetail.setAnswer(ar[i].getAnswer());
						idrsDetail.setQuestion(ar[i].getQuestion());
						idrsDetail.setDiseaseQuestionType(ar[i].getDiseaseQuestionType());
						idrsDetail.setBenVisitID(benVisitID);
						idrsDetail.setVisitCode(benVisitCode);
						if (idrsDetail.getSuspectArray() != null && idrsDetail.getSuspectArray().length > 0) {
							for (int a = 0; a < idrsDetail.getSuspectArray().length; a++) {
								if (a == idrsDetail.getSuspectArray().length - 1)
									temp += idrsDetail.getSuspectArray()[a];
								else
									temp = temp + idrsDetail.getSuspectArray()[a] + ",";
							}
							if (temp.equalsIgnoreCase(""))
								temp = null;
							idrsDetail.setSuspectedDisease(temp);
						}

						if (idrsDetail.getConfirmArray() != null && idrsDetail.getConfirmArray().length > 0) {
							for (int a = 0; a < idrsDetail.getConfirmArray().length; a++) {
								if (a == idrsDetail.getConfirmArray().length - 1)
									temp1 += idrsDetail.getConfirmArray()[a];
								else
									temp1 = temp1 + idrsDetail.getConfirmArray()[a] + ",";
							}
							if (temp1.equalsIgnoreCase(""))
								temp1 = null;
							idrsDetail.setConfirmedDisease(temp1);
						}

						idrsFlag = commonNurseServiceImpl.saveIDRS(idrsDetail);
					}
				} else {
					idrsDetail.setBenVisitID(benVisitID);
					idrsDetail.setVisitCode(benVisitCode);
					if (idrsDetail.getSuspectArray() != null && idrsDetail.getSuspectArray().length > 0) {
						for (int a = 0; a < idrsDetail.getSuspectArray().length; a++) {
							if (a == idrsDetail.getSuspectArray().length - 1)
								temp1 += idrsDetail.getSuspectArray()[a];
							else
								temp1 = temp1 + idrsDetail.getSuspectArray()[a] + ",";
						}
						if (temp1.equalsIgnoreCase(""))
							temp1 = null;
						idrsDetail.setSuspectedDisease(temp1);
					}
					temp1 = "";
					if (idrsDetail.getConfirmArray() != null && idrsDetail.getConfirmArray().length > 0) {
						for (int a = 0; a < idrsDetail.getConfirmArray().length; a++) {
							if (a == idrsDetail.getConfirmArray().length - 1)
								temp1 += idrsDetail.getConfirmArray()[a];
							else
								temp1 = temp1 + idrsDetail.getConfirmArray()[a] + ",";
						}
						if (temp1.equalsIgnoreCase(""))
							temp1 = null;
						idrsDetail.setConfirmedDisease(temp1);
					}

					idrsFlag = commonNurseServiceImpl.saveIDRS(idrsDetail);
				}
			}

		}

		return idrsFlag;
	}

	public Long savePhysicalActivityDetails(JsonObject physicalActivityDetailsOBJ, Long benVisitID, Long benVisitCode)
			throws Exception {
		Long physicalActivityFlag = null;
		// Save Physical Anthropometry && Physical Vital Details
		if (physicalActivityDetailsOBJ != null) {
			PhysicalActivityType physicalActivityDetail = InputMapper.gson().fromJson(physicalActivityDetailsOBJ,
					PhysicalActivityType.class);

			if (null != physicalActivityDetail) {
				physicalActivityDetail.setBenVisitID(benVisitID);
				physicalActivityDetail.setVisitCode(benVisitCode);
				physicalActivityFlag = commonNurseServiceImpl.savePhysicalActivity(physicalActivityDetail);
			}

		}

		return physicalActivityFlag;
	}

	/**
	 * @param requestOBJ
	 * @return success or failure flag for visitDetails data saving
	 */
	public Long saveBenNCDCareVitalDetails(JsonObject vitalDetailsOBJ, Long benVisitID, Long benVisitCode)
			throws Exception {
		Long vitalSuccessFlag = null;
		Long anthropometrySuccessFlag = null;
		Long phyVitalSuccessFlag = null;
		// Save Physical Anthropometry && Physical Vital Details
		if (vitalDetailsOBJ != null) {
			BenAnthropometryDetail benAnthropometryDetail = InputMapper.gson().fromJson(vitalDetailsOBJ,
					BenAnthropometryDetail.class);
			BenPhysicalVitalDetail benPhysicalVitalDetail = InputMapper.gson().fromJson(vitalDetailsOBJ,
					BenPhysicalVitalDetail.class);

			if (null != benAnthropometryDetail) {
				benAnthropometryDetail.setBenVisitID(benVisitID);
				benAnthropometryDetail.setVisitCode(benVisitCode);
				anthropometrySuccessFlag = commonNurseServiceImpl
						.saveBeneficiaryPhysicalAnthropometryDetails(benAnthropometryDetail);
			}
			if (null != benPhysicalVitalDetail) {
				benPhysicalVitalDetail.setBenVisitID(benVisitID);
				benPhysicalVitalDetail.setVisitCode(benVisitCode);
				phyVitalSuccessFlag = commonNurseServiceImpl
						.saveBeneficiaryPhysicalVitalDetails(benPhysicalVitalDetail);
			}

			if (anthropometrySuccessFlag != null && anthropometrySuccessFlag > 0 && phyVitalSuccessFlag != null
					&& phyVitalSuccessFlag > 0) {
				vitalSuccessFlag = anthropometrySuccessFlag;
			}
		}

		return vitalSuccessFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updateNurseNCDScreeningDetails(JsonObject jsonObject) throws Exception {

		NCDScreening ncdScreening = InputMapper.gson().fromJson(jsonObject, NCDScreening.class);

		if (ncdScreening.getNextScreeningDate() != null)
			ncdScreening.setNextScreeningDateDB(
					Timestamp.valueOf(ncdScreening.getNextScreeningDate().replaceAll("T", " ").replaceAll("Z", " ")));

		BenAnthropometryDetail anthropometryDetail = InputMapper.gson().fromJson(jsonObject,
				BenAnthropometryDetail.class);
		BenPhysicalVitalDetail physicalVitalDetail = InputMapper.gson().fromJson(jsonObject,
				BenPhysicalVitalDetail.class);

		Integer result = null;

		Integer updateNCDScreeningDetails = ncdScreeningNurseServiceImpl.updateNCDScreeningDetails(ncdScreening);
		Integer updateANCAnthropometryDetails = commonNurseServiceImpl
				.updateANCAnthropometryDetails(anthropometryDetail);
		Integer updateANCPhysicalVitalDetails = commonNurseServiceImpl
				.updateANCPhysicalVitalDetails(physicalVitalDetail);

		// add file/doc id
		Integer[] docIdArr = ncdScreening.getFileIDs();
		StringBuilder sb = new StringBuilder();
		if (docIdArr != null && docIdArr.length > 0) {
			for (Integer i : docIdArr) {
				sb.append(i + ",");
			}
		}
		if (sb.length() > 0)
			benVisitDetailRepo.updateFileID(sb.toString(), ncdScreening.getBeneficiaryRegID(),
					ncdScreening.getVisitCode());

		if (null != updateANCAnthropometryDetails && null != updateANCPhysicalVitalDetails
				&& null != updateNCDScreeningDetails) {

			short nurseFlag = (short) 0;

			if (ncdScreening.getIsScreeningComplete() != null && ncdScreening.getIsScreeningComplete() == true)
				nurseFlag = (short) 9;
			else
				nurseFlag = (short) 100;

			int i = commonBenStatusFlowServiceImpl.updateBenFlowNurseAfterNurseUpdateNCD_Screening(
					ncdScreening.getBenFlowID(), ncdScreening.getBeneficiaryRegID(), nurseFlag);

			result = 1;
		} else
			throw new RuntimeException("Error occured while updating record in between...");

		return result;

	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBenVitalDetails(JsonObject vitalDetailsOBJ) throws Exception {
		int vitalSuccessFlag = 0;
		int anthropometrySuccessFlag = 0;
		int phyVitalSuccessFlag = 0;
		// Save Physical Anthropometry && Physical Vital Details
		if (vitalDetailsOBJ != null) {
			BenAnthropometryDetail benAnthropometryDetail = InputMapper.gson().fromJson(vitalDetailsOBJ,
					BenAnthropometryDetail.class);
			BenPhysicalVitalDetail benPhysicalVitalDetail = InputMapper.gson().fromJson(vitalDetailsOBJ,
					BenPhysicalVitalDetail.class);

			anthropometrySuccessFlag = commonNurseServiceImpl.updateANCAnthropometryDetails(benAnthropometryDetail);
			phyVitalSuccessFlag = commonNurseServiceImpl.updateANCPhysicalVitalDetails(benPhysicalVitalDetail);

			if (anthropometrySuccessFlag > 0 && phyVitalSuccessFlag > 0) {
				vitalSuccessFlag = anthropometrySuccessFlag;
			}
		} else {
			vitalSuccessFlag = 1;
		}

		return vitalSuccessFlag;
	}

	@Override
	public String getNCDScreeningDetails(Long beneficiaryRegID, Long visitCode) {
		String ncdScreeningDetails = ncdScreeningNurseServiceImpl.getNCDScreeningDetails(beneficiaryRegID, visitCode);
		String anthropometryDetails = commonNurseServiceImpl
				.getBeneficiaryPhysicalAnthropometryDetails(beneficiaryRegID, visitCode);
		String vitalDetails = commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(beneficiaryRegID, visitCode);

		Map<String, Object> res = new HashMap<String, Object>();

		if (ncdScreeningDetails != null && anthropometryDetails != null && vitalDetails != null) {
			res.put("ncdScreeningDetails", ncdScreeningDetails);
			res.put("anthropometryDetails", anthropometryDetails);
			res.put("vitalDetails", vitalDetails);
		} else {
			// Failed to Fetch Beneficiary NCD Screening Details
		}
		return res.toString();
	}

	public String getNcdScreeningVisitCnt(Long beneficiaryRegID) {
		Map<String, Long> returnMap = new HashMap<>();
		Long visitCount = beneficiaryFlowStatusRepo.getNcdScreeningVisitCount(beneficiaryRegID);
		returnMap.put("ncdScreeningVisitCount", visitCount + 1);
		return new Gson().toJson(returnMap);
	}

	@Override
	public Long UpdateIDRSScreen(JsonObject idrsOBJ) throws Exception {
		Long idrsFlag = null;
		if (idrsOBJ != null && idrsOBJ.has("idrsDetails") && !idrsOBJ.get("idrsDetails").isJsonNull()) {
			IDRSData idrsDetail1 = InputMapper.gson().fromJson(idrsOBJ.get("idrsDetails"), IDRSData.class);
			String temp = "", temp1 = "";
			if (null != idrsDetail1) {
				if (idrsDetail1.getQuestionArray() != null && idrsDetail1.getQuestionArray().length > 0) {
					IDRSData[] ar = idrsDetail1.getQuestionArray();
					for (int i = 0; i < ar.length; i++) {
						IDRSData idrsDetail = InputMapper.gson().fromJson(idrsOBJ.get("idrsDetails"), IDRSData.class);
						temp = "";
						temp1 = "";
						idrsDetail.setIdrsQuestionID(ar[i].getIdrsQuestionID());
						idrsDetail.setId(ar[i].getId());
						idrsDetail.setAnswer(ar[i].getAnswer());
						idrsDetail.setQuestion(ar[i].getQuestion());
						idrsDetail.setDiseaseQuestionType(ar[i].getDiseaseQuestionType());

						if (idrsDetail.getSuspectArray() != null && idrsDetail.getSuspectArray().length > 0) {
							for (int a = 0; a < idrsDetail.getSuspectArray().length; a++) {
								if (a == idrsDetail.getSuspectArray().length - 1)
									temp += idrsDetail.getSuspectArray()[a];
								else
									temp = temp + idrsDetail.getSuspectArray()[a] + ",";
							}
							if (temp.equalsIgnoreCase(""))
								temp = null;
							idrsDetail.setSuspectedDisease(temp);
						}
						if (idrsDetail.getConfirmArray() != null && idrsDetail.getConfirmArray().length > 0) {
							for (int a = 0; a < idrsDetail.getConfirmArray().length; a++) {
								if (a == idrsDetail.getConfirmArray().length - 1)
									temp1 += idrsDetail.getConfirmArray()[a];
								else
									temp1 = temp1 + idrsDetail.getConfirmArray()[a] + ",";
							}
							if (temp1.equalsIgnoreCase(""))
								temp1 = null;
							idrsDetail.setConfirmedDisease(temp1);
						}
						int updateSuspected = 0;
						if (temp != null)
							updateSuspected = iDrsDataRepo.updateSuspectedDiseases(idrsDetail1.getBeneficiaryRegID(),
									idrsDetail1.getVisitCode(), temp);
						idrsFlag = commonNurseServiceImpl.saveIDRS(idrsDetail);
					}
				} else {
					int success = 0;
					if (idrsDetail1.getConfirmArray() != null && idrsDetail1.getConfirmArray().length > 0) {
						for (int a = 0; a < idrsDetail1.getConfirmArray().length; a++) {
							if (a == idrsDetail1.getConfirmArray().length - 1)
								temp1 += idrsDetail1.getConfirmArray()[a];
							else
								temp1 = temp1 + idrsDetail1.getConfirmArray()[a] + ",";
						}
						if (temp1.equalsIgnoreCase(""))
							temp1 = null;
						if (temp1 != null) {
							success = iDrsDataRepo.updateConfirmedDiseases(idrsDetail1.getBeneficiaryRegID(),
									idrsDetail1.getVisitCode(), temp1);
						}

						if (success > 0) {
							idrsFlag = new Long(1);
						}
					}
					if (idrsDetail1.getIdrsScore() != null) {
						success = iDrsDataRepo.updateIdrsScore(idrsDetail1.getBeneficiaryRegID(),
								idrsDetail1.getVisitCode(), idrsDetail1.getIdrsScore());

						if (success > 0) {
							idrsFlag = new Long(1);
						}
					}
					if (idrsDetail1.getSuspectArray() != null && idrsDetail1.getSuspectArray().length > 0) {
						for (int a = 0; a < idrsDetail1.getSuspectArray().length; a++) {
							if (a == idrsDetail1.getSuspectArray().length - 1)
								temp1 += idrsDetail1.getSuspectArray()[a];
							else
								temp1 = temp1 + idrsDetail1.getSuspectArray()[a] + ",";
						}
						if (temp1.equalsIgnoreCase(""))
							temp1 = null;
						if (temp1 != null) {
							success = iDrsDataRepo.updateSuspectedDiseases(idrsDetail1.getBeneficiaryRegID(),
									idrsDetail1.getVisitCode(), temp1);
						}

						if (success > 0) {
							idrsFlag = new Long(1);
						}
					}
					if (idrsDetail1.getIdrsScore() != null) {
						success = iDrsDataRepo.updateIdrsScore(idrsDetail1.getBeneficiaryRegID(),
								idrsDetail1.getVisitCode(), idrsDetail1.getIdrsScore());

						if (success > 0) {
							idrsFlag = new Long(1);
						}
					}
				}

			}

		}

		return idrsFlag;

	}

	@Transactional(rollbackFor = Exception.class)
	public Long saveDoctorData(JsonObject requestOBJ, String Authorization) throws Exception {
		Long saveSuccessFlag = null;
		Long prescriptionID = null;
		Long investigationSuccessFlag = null;
		Integer findingSuccessFlag = null;
		Integer prescriptionSuccessFlag = null;
		Long referSaveSuccessFlag = null;
		Integer tcRequestStatusFlag = null;

		if (requestOBJ != null) {
			TeleconsultationRequestOBJ tcRequestOBJ = null;
			// TcSpecialistSlotBookingRequestOBJ tcSpecialistSlotBookingRequestOBJ = null;
			CommonUtilityClass commonUtilityClass = InputMapper.gson().fromJson(requestOBJ, CommonUtilityClass.class);

			tcRequestOBJ = commonServiceImpl.createTcRequest(requestOBJ, commonUtilityClass, Authorization);

			JsonArray testList = null;
			JsonArray drugList = null;

			Boolean isTestPrescribed = false;
			Boolean isMedicinePrescribed = false;

			Boolean doctorSignatureFlag = false;
			if (requestOBJ.has("doctorSignatureFlag") && !requestOBJ.get("doctorSignatureFlag").isJsonNull()) {
			doctorSignatureFlag = requestOBJ.get("doctorSignatureFlag").getAsBoolean();
			}

			// checking if test is prescribed
			if (requestOBJ.has("investigation") && !requestOBJ.get("investigation").isJsonNull()
					&& requestOBJ.get("investigation") != null) {
				testList = requestOBJ.getAsJsonObject("investigation").getAsJsonArray("laboratoryList");
				if (testList != null && !testList.isJsonNull() && testList.size() > 0)
					isTestPrescribed = true;
			}

			// checking if medicine is prescribed
			if (requestOBJ.has("prescription") && !requestOBJ.get("prescription").isJsonNull()
					&& requestOBJ.get("prescription") != null) {
				drugList = requestOBJ.getAsJsonArray("prescription");
				if (drugList != null && !drugList.isJsonNull() && drugList.size() > 0) {
					isMedicinePrescribed = true;
				}
			}

			// save findings
			if (requestOBJ.has("findings") && !requestOBJ.get("findings").isJsonNull()) {
				WrapperAncFindings wrapperAncFindings = InputMapper.gson().fromJson(requestOBJ.get("findings"),
						WrapperAncFindings.class);
				findingSuccessFlag = commonDoctorServiceImpl.saveDocFindings(wrapperAncFindings);

			} else {
				findingSuccessFlag = 1;
			}

			// creating prescription object
			PrescriptionDetail prescriptionDetail = new PrescriptionDetail();

			if (requestOBJ.has("diagnosis") && !requestOBJ.get("diagnosis").isJsonNull()) {
				JsonObject diagnosisObj = requestOBJ.getAsJsonObject("diagnosis");

				prescriptionDetail = InputMapper.gson().fromJson(diagnosisObj, PrescriptionDetail.class);
			} else {
			}

			// generate prescription
			WrapperBenInvestigationANC wrapperBenInvestigationANC = InputMapper.gson()
					.fromJson(requestOBJ.get("investigation"), WrapperBenInvestigationANC.class);
			// Save Prescription
			prescriptionDetail.setExternalInvestigation(wrapperBenInvestigationANC.getExternalInvestigations());
			prescriptionID = commonNurseServiceImpl.saveBenPrescription(prescriptionDetail);

			// save prescribed lab test
			if (isTestPrescribed) {
				wrapperBenInvestigationANC.setPrescriptionID(prescriptionID);
				investigationSuccessFlag = commonNurseServiceImpl.saveBenInvestigation(wrapperBenInvestigationANC);
			} else {
				investigationSuccessFlag = new Long(1);
			}

			// save prescribed medicine
			if (isMedicinePrescribed) {
				PrescribedDrugDetail[] prescribedDrugDetail = InputMapper.gson()
						.fromJson(requestOBJ.get("prescription"), PrescribedDrugDetail[].class);
				List<PrescribedDrugDetail> prescribedDrugDetailList = Arrays.asList(prescribedDrugDetail);

				for (PrescribedDrugDetail tmpObj : prescribedDrugDetailList) {
					tmpObj.setPrescriptionID(prescriptionID);
					tmpObj.setBeneficiaryRegID(commonUtilityClass.getBeneficiaryRegID());
					tmpObj.setBenVisitID(commonUtilityClass.getBenVisitID());
					tmpObj.setVisitCode(commonUtilityClass.getVisitCode());
					tmpObj.setProviderServiceMapID(commonUtilityClass.getProviderServiceMapID());
				}
				Integer r = commonNurseServiceImpl.saveBenPrescribedDrugsList(prescribedDrugDetailList);
				if (r > 0 && r != null) {
					prescriptionSuccessFlag = r;
				}

			} else {
				prescriptionSuccessFlag = 1;
			}

			// save referral details
			if (requestOBJ.has("refer") && !requestOBJ.get("refer").isJsonNull()) {
				referSaveSuccessFlag = commonDoctorServiceImpl
						.saveBenReferDetails(requestOBJ.get("refer").getAsJsonObject());
			} else {
				referSaveSuccessFlag = new Long(1);
			}

			// check if all requested data saved properly
			if ((findingSuccessFlag != null && findingSuccessFlag > 0)
					&& (investigationSuccessFlag != null && investigationSuccessFlag > 0)
					&& (prescriptionSuccessFlag != null && prescriptionSuccessFlag > 0)
					&& (referSaveSuccessFlag != null && referSaveSuccessFlag > 0)) {

				// call method to update beneficiary flow table
				if (prescriptionID != null) {
					commonUtilityClass.setPrescriptionID(prescriptionID);
					commonUtilityClass.setVisitCategoryID(2);
					commonUtilityClass.setAuthorization(Authorization);

				}
				int i = commonDoctorServiceImpl.updateBenFlowtableAfterDocDataSave(commonUtilityClass, isTestPrescribed,
						isMedicinePrescribed, tcRequestOBJ, doctorSignatureFlag);

				if (i > 0)
					saveSuccessFlag = investigationSuccessFlag;
				else
					throw new RuntimeException("Error occurred while saving data. Beneficiary status update failed");

				if (i > 0 && tcRequestOBJ != null && tcRequestOBJ.getWalkIn() == false) {
					int k = sMSGatewayServiceImpl.smsSenderGateway("schedule", commonUtilityClass.getBeneficiaryRegID(),
							tcRequestOBJ.getSpecializationID(), tcRequestOBJ.getTmRequestID(), null,
							commonUtilityClass.getCreatedBy(),
							tcRequestOBJ.getAllocationDate() != null ? String.valueOf(tcRequestOBJ.getAllocationDate())
									: "",
							null, Authorization);
				}

			} else {
				throw new RuntimeException();
			}
		} else {
			// request OBJ is null.
		}
		return saveSuccessFlag;
	}
	/// --------------- END of saving doctor data ------------------------

	public String getBenCaseRecordFromDoctorNCDScreening(Long benRegID, Long visitCode) throws JsonProcessingException, ParseException {
		Map<String, Object> resMap = new HashMap<>();

		resMap.put("findings", commonDoctorServiceImpl.getFindingsDetails(benRegID, visitCode));

		resMap.put("diagnosis", ncdSCreeningDoctorServiceImpl.getNCDDiagnosisData(benRegID, visitCode));

		resMap.put("investigation", commonDoctorServiceImpl.getInvestigationDetails(benRegID, visitCode));

		resMap.put("prescription", commonDoctorServiceImpl.getPrescribedDrugs(benRegID, visitCode));

		resMap.put("Refer", commonDoctorServiceImpl.getReferralDetails(benRegID, visitCode));

		resMap.put("LabReport",
				new Gson().toJson(labTechnicianServiceImpl.getLabResultDataForBen(benRegID, visitCode)));

		resMap.put("GraphData", new Gson().toJson(commonNurseServiceImpl.getGraphicalTrendData(benRegID, "ncdCare")));

		resMap.put("ArchivedVisitcodeForLabResult",
				labTechnicianServiceImpl.getLast_3_ArchivedTestVisitList(benRegID, visitCode));

		return resMap.toString();
	}

	/// --------------- Start of Fetching NCD Screening Nurse Data ----------------
	public String getBenVisitDetailsFrmNurseNCDScreening(Long benRegID, Long visitCode) {
		Map<String, Object> resMap = new HashMap<>();

		BeneficiaryVisitDetail visitDetail = commonNurseServiceImpl.getCSVisitDetails(benRegID, visitCode);

		resMap.put("NCDScreeningNurseVisitDetail", new Gson().toJson(visitDetail));

		resMap.put("BenChiefComplaints", commonNurseServiceImpl.getBenChiefComplaints(benRegID, visitCode));

		return resMap.toString();
	}

	public String getBenHistoryDetails(Long benRegID, Long visitCode) {

		Map<String, Object> HistoryDetailsMap = new HashMap<String, Object>();

		HistoryDetailsMap.put("FamilyHistory", commonNurseServiceImpl.getFamilyHistoryDetail(benRegID, visitCode));
		HistoryDetailsMap.put("PhysicalActivityHistory",
				commonNurseServiceImpl.getPhysicalActivityType(benRegID, visitCode));
		HistoryDetailsMap.put("PersonalHistory", commonNurseServiceImpl.getPersonalHistory(benRegID, visitCode));

		return new Gson().toJson(HistoryDetailsMap);
	}

	public String getBenIdrsDetailsFrmNurse(Long beneficiaryRegID, Long benVisitID) {
		Map<String, Object> resMap = new HashMap<>();

		resMap.put("IDRSDetail", commonNurseServiceImpl.getBeneficiaryIdrsDetails(beneficiaryRegID, benVisitID));

		return new Gson().toJson(resMap);
	}

	public String getBeneficiaryVitalDetails(Long beneficiaryRegID, Long benVisitID) {
		Map<String, Object> resMap = new HashMap<>();

		resMap.put("benAnthropometryDetail",
				commonNurseServiceImpl.getBeneficiaryPhysicalAnthropometryDetails(beneficiaryRegID, benVisitID));
		resMap.put("benPhysicalVitalDetail",
				commonNurseServiceImpl.getBeneficiaryPhysicalVitalDetails(beneficiaryRegID, benVisitID));

		return resMap.toString();
	}

	@Override
	public Integer UpdateNCDScreeningHistory(JsonObject historyOBJ) throws Exception {

		int familyHistorySuccessFlag = 0;
		int historyUpdatedSuccessfully = 0;
		int physicalActivitySuccessFlag = 0;

		// Update Family History
		if (historyOBJ != null && historyOBJ.has("familyHistory") && !historyOBJ.get("familyHistory").isJsonNull()) {
			BenFamilyHistory benFamilyHistory = InputMapper.gson().fromJson(historyOBJ.get("familyHistory"),
					BenFamilyHistory.class);

			familyHistorySuccessFlag = commonNurseServiceImpl.updateBenFamilyHistoryNCDScreening(benFamilyHistory);
		} else {
			familyHistorySuccessFlag = 0;
		}

		// Update Physical Activity
		if (historyOBJ != null && historyOBJ.has("physicalActivityHistory")
				&& !historyOBJ.get("physicalActivityHistory").isJsonNull()) {
			PhysicalActivityType physicalActivityType = InputMapper.gson()
					.fromJson(historyOBJ.get("physicalActivityHistory"), PhysicalActivityType.class);

			physicalActivitySuccessFlag = commonNurseServiceImpl
					.updateBenPhysicalActivityHistoryNCDScreening(physicalActivityType);
		} else {
			physicalActivitySuccessFlag = 0;
		}

		if (familyHistorySuccessFlag > 0 && physicalActivitySuccessFlag > 0) {
			historyUpdatedSuccessfully = 1;
		}
		// Update Personal History
		if (historyOBJ != null && historyOBJ.has("personalHistory")
				&& !historyOBJ.get("personalHistory").isJsonNull()) {
			// Update Ben Personal Habits..
			BenPersonalHabit personalHabit = InputMapper.gson().fromJson(historyOBJ.get("personalHistory"),
					BenPersonalHabit.class);

			commonNurseServiceImpl.updateBenPersonalHistory(personalHabit);

			// Update Ben Allergy History..
			BenAllergyHistory benAllergyHistory = InputMapper.gson().fromJson(historyOBJ.get("personalHistory"),
					BenAllergyHistory.class);
			commonNurseServiceImpl.updateBenAllergicHistory(benAllergyHistory);

		}
		return historyUpdatedSuccessfully;
	}

	public String getBenNCDScreeningNurseData(Long benRegID, Long visitCode) {
		Map<String, Object> resMap = new HashMap<>();

		resMap.put("vitals", getBeneficiaryVitalDetails(benRegID, visitCode));

		resMap.put("history", getBenHistoryDetails(benRegID, visitCode));

		resMap.put("idrs", getBenIdrsDetailsFrmNurse(benRegID, visitCode));

		return resMap.toString();
	}

}