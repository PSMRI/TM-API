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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.tm.data.anc.WrapperAncFindings;
import com.iemr.tm.data.anc.WrapperBenInvestigationANC;
import com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus;
import com.iemr.tm.data.doctor.BenReferDetails;
import com.iemr.tm.data.foetalmonitor.FoetalMonitor;
import com.iemr.tm.data.masterdata.anc.ServiceMaster;
import com.iemr.tm.data.nurse.CommonUtilityClass;
import com.iemr.tm.data.quickConsultation.BenChiefComplaint;
import com.iemr.tm.data.quickConsultation.BenClinicalObservations;
import com.iemr.tm.data.quickConsultation.LabTestOrderDetail;
import com.iemr.tm.data.quickConsultation.PrescribedDrugDetail;
import com.iemr.tm.data.registrar.WrapperRegWorklist;
import com.iemr.tm.data.snomedct.SCTDescription;
import com.iemr.tm.data.tele_consultation.TcSpecialistSlotBookingRequestOBJ;
import com.iemr.tm.data.tele_consultation.TeleconsultationRequestOBJ;
import com.iemr.tm.data.tele_consultation.TeleconsultationStats;
import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.doctor.BenReferDetailsRepo;
import com.iemr.tm.repo.doctor.DocWorkListRepo;
import com.iemr.tm.repo.foetalmonitor.FoetalMonitorRepo;
import com.iemr.tm.repo.nurse.ncdcare.NCDCareDiagnosisRepo;
import com.iemr.tm.repo.nurse.pnc.PNCDiagnosisRepo;
import com.iemr.tm.repo.quickConsultation.BenChiefComplaintRepo;
import com.iemr.tm.repo.quickConsultation.BenClinicalObservationsRepo;
import com.iemr.tm.repo.quickConsultation.LabTestOrderDetailRepo;
import com.iemr.tm.repo.quickConsultation.PrescribedDrugDetailRepo;
import com.iemr.tm.repo.quickConsultation.PrescriptionDetailRepo;
import com.iemr.tm.repo.tc_consultation.TCRequestModelRepo;
import com.iemr.tm.repo.tc_consultation.TeleconsultationStatsRepo;
import com.iemr.tm.service.benFlowStatus.CommonBenStatusFlowServiceImpl;
import com.iemr.tm.service.snomedct.SnomedServiceImpl;
import com.iemr.tm.service.tele_consultation.SMSGatewayServiceImpl;
import com.iemr.tm.utils.CookieUtil;
import com.iemr.tm.utils.RestTemplateUtil;
import com.iemr.tm.utils.exception.IEMRException;
import com.iemr.tm.utils.mapper.InputMapper;
import com.iemr.tm.utils.mapper.OutputMapper;

import jakarta.servlet.http.HttpServletRequest;

@Service
@PropertySource("classpath:application.properties")
public class CommonDoctorServiceImpl {

	@Value("${tcSpecialistSlotBook}")
	private String tcSpecialistSlotBook;

	@Value("${docWL}")
	private Integer docWL;
	@Value("${tcSpeclistWL}")
	private Integer tcSpeclistWL;

	private BenClinicalObservationsRepo benClinicalObservationsRepo;
	private BenChiefComplaintRepo benChiefComplaintRepo;
	private DocWorkListRepo docWorkListRepo;
	private BenReferDetailsRepo benReferDetailsRepo;
	private LabTestOrderDetailRepo labTestOrderDetailRepo;
	private PrescribedDrugDetailRepo prescribedDrugDetailRepo;

	private SnomedServiceImpl snomedServiceImpl;

	private CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl;

	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	@Autowired
	private TCRequestModelRepo tCRequestModelRepo;
	@Autowired
	private PNCDiagnosisRepo pNCDiagnosisRepo;
	@Autowired
	private PrescriptionDetailRepo prescriptionDetailRepo;
	@Autowired
	private NCDCareDiagnosisRepo NCDCareDiagnosisRepo;
	@Autowired
	private SMSGatewayServiceImpl sMSGatewayServiceImpl;
	@Autowired
	private FoetalMonitorRepo foetalMonitorRepo;
	@Autowired
	private CookieUtil cookieUtil;

	@Autowired
	public void setSnomedServiceImpl(SnomedServiceImpl snomedServiceImpl) {
		this.snomedServiceImpl = snomedServiceImpl;
	}

	@Autowired
	public void setCommonBenStatusFlowServiceImpl(CommonBenStatusFlowServiceImpl commonBenStatusFlowServiceImpl) {
		this.commonBenStatusFlowServiceImpl = commonBenStatusFlowServiceImpl;
	}

	@Autowired
	public void setBeneficiaryFlowStatusRepo(BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo) {
		this.beneficiaryFlowStatusRepo = beneficiaryFlowStatusRepo;
	}

	@Autowired
	public void setPrescribedDrugDetailRepo(PrescribedDrugDetailRepo prescribedDrugDetailRepo) {
		this.prescribedDrugDetailRepo = prescribedDrugDetailRepo;
	}

	@Autowired
	public void setLabTestOrderDetailRepo(LabTestOrderDetailRepo labTestOrderDetailRepo) {
		this.labTestOrderDetailRepo = labTestOrderDetailRepo;
	}

	@Autowired
	public void setBenReferDetailsRepo(BenReferDetailsRepo benReferDetailsRepo) {
		this.benReferDetailsRepo = benReferDetailsRepo;
	}

	@Autowired
	public void setDocWorkListRepo(DocWorkListRepo docWorkListRepo) {
		this.docWorkListRepo = docWorkListRepo;
	}

	@Autowired
	public void setBenChiefComplaintRepo(BenChiefComplaintRepo benChiefComplaintRepo) {
		this.benChiefComplaintRepo = benChiefComplaintRepo;
	}

	@Autowired
	public void setBenClinicalObservationsRepo(BenClinicalObservationsRepo benClinicalObservationsRepo) {
		this.benClinicalObservationsRepo = benClinicalObservationsRepo;
	}

	@Autowired
	private TeleconsultationStatsRepo teleconsultationStatsRepo;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	public Integer saveFindings(JsonObject obj) throws Exception {
		int i = 0;
		BenClinicalObservations clinicalObservations = InputMapper.gson().fromJson(obj, BenClinicalObservations.class);
		BenClinicalObservations benClinicalObservationsRS = benClinicalObservationsRepo.save(clinicalObservations);
		if (benClinicalObservationsRS != null) {
			i = 1;
		}

		return i;

	}

	public Integer saveDocFindings(WrapperAncFindings wrapperAncFindings) {
		int i = 0;
		int clinicalObservationFlag = 0;
		int chiefComFlag = 0;

		// save clinical observation
		BenClinicalObservations benClinicalObservationsRS = benClinicalObservationsRepo
				.save(getBenClinicalObservations(wrapperAncFindings));
		if (benClinicalObservationsRS != null) {
			clinicalObservationFlag = 1;
		}

		// get chief complaints Object to save
		ArrayList<BenChiefComplaint> tmpBenCHiefComplaints = wrapperAncFindings.getComplaints();
		ArrayList<BenChiefComplaint> tmpBenCHiefComplaintsTMP = new ArrayList<>();
		// filter out valid chief complaints
		if (tmpBenCHiefComplaints.size() > 0) {
			for (BenChiefComplaint benChiefComplaint : tmpBenCHiefComplaints) {
				if (benChiefComplaint.getChiefComplaint() != null) {
					benChiefComplaint.setBeneficiaryRegID(wrapperAncFindings.getBeneficiaryRegID());
					benChiefComplaint.setBenVisitID(wrapperAncFindings.getBenVisitID());
					benChiefComplaint.setVisitCode(wrapperAncFindings.getVisitCode());
					benChiefComplaint.setProviderServiceMapID(wrapperAncFindings.getProviderServiceMapID());
					benChiefComplaint.setCreatedBy(wrapperAncFindings.getCreatedBy());
					benChiefComplaint.setVanID(wrapperAncFindings.getVanID());
					benChiefComplaint.setParkingPlaceID(wrapperAncFindings.getParkingPlaceID());

					tmpBenCHiefComplaintsTMP.add(benChiefComplaint);
				}
			}

		}
		// if valid chief complaints is present than save to DB
		if (tmpBenCHiefComplaintsTMP.size() > 0) {
			ArrayList<BenChiefComplaint> benChiefComplaintListRS = (ArrayList<BenChiefComplaint>) benChiefComplaintRepo
					.saveAll(tmpBenCHiefComplaintsTMP);
			if (tmpBenCHiefComplaintsTMP.size() == benChiefComplaintListRS.size()) {
				chiefComFlag = 1;
			}
		} else {
			chiefComFlag = 1;
		}

		// check if both clinical observation & chief complaints both saved successfully
		if (clinicalObservationFlag > 0 && chiefComFlag > 0)
			i = 1;

		return i;
	}

	// get comma separated snomedCT code for give string comma seperated
	public String[] getSnomedCTcode(String requestString) {
		String[] returnARR = new String[2];
		String snomedCTidVal = "";
		String snomedCTtermVal = "";

		if (requestString != null && requestString.length() > 0) {
			String[] symptomArr = requestString.split(",");

			int pointer = 0;
			for (String s : symptomArr) {
				SCTDescription obj = snomedServiceImpl.findSnomedCTRecordFromTerm(s.trim());
				if (obj != null) {
					if (pointer == symptomArr.length - 1) {
						snomedCTidVal += obj.getConceptID();
						snomedCTtermVal += obj.getTerm();
					} else {
						snomedCTidVal += obj.getConceptID() + ",";
						snomedCTtermVal += obj.getTerm() + ",";
					}
				} else {
					if (pointer == symptomArr.length - 1) {
						snomedCTidVal += "N/A";
						snomedCTtermVal += "N/A";
					} else {
						snomedCTidVal += "N/A" + ",";
						snomedCTtermVal += "N/A" + ",";
					}

				}
				pointer++;
			}

		}
		returnARR[0] = snomedCTidVal;
		returnARR[1] = snomedCTtermVal;

		return returnARR;
	}

	private BenClinicalObservations getBenClinicalObservations(WrapperAncFindings wrapperAncFindings) {
		// snomedCT integration started
		String symptoms = wrapperAncFindings.getOtherSymptoms();
		String[] responseString = getSnomedCTcode(symptoms);
		// end of snomedCT integration
		BenClinicalObservations benClinicalObservations = new BenClinicalObservations();
		benClinicalObservations.setBeneficiaryRegID(wrapperAncFindings.getBeneficiaryRegID());
		benClinicalObservations.setBenVisitID(wrapperAncFindings.getBenVisitID());
		benClinicalObservations.setVisitCode(wrapperAncFindings.getVisitCode());
		benClinicalObservations.setProviderServiceMapID(wrapperAncFindings.getProviderServiceMapID());
		benClinicalObservations.setVanID(wrapperAncFindings.getVanID());
		benClinicalObservations.setParkingPlaceID(wrapperAncFindings.getParkingPlaceID());
		benClinicalObservations.setCreatedBy(wrapperAncFindings.getCreatedBy());
		benClinicalObservations.setClinicalObservation(wrapperAncFindings.getClinicalObservation());
		benClinicalObservations.setOtherSymptoms(wrapperAncFindings.getOtherSymptoms());
		benClinicalObservations.setSignificantFindings(wrapperAncFindings.getSignificantFindings());
		benClinicalObservations.setIsForHistory(wrapperAncFindings.getIsForHistory());
		benClinicalObservations.setModifiedBy(wrapperAncFindings.getModifiedBy());
		if (responseString != null && responseString.length > 1) {
			benClinicalObservations.setOtherSymptomsSCTCode(responseString[0]);
			benClinicalObservations.setOtherSymptomsSCTTerm(responseString[1]);
		}
		return benClinicalObservations;
	}

	private ArrayList<BenChiefComplaint> getBenChiefComplaint(WrapperAncFindings wrapperAncFindings) {
		ArrayList<BenChiefComplaint> benChiefComplaintList = new ArrayList<>();
		BenChiefComplaint benChiefComplaint;
		if (wrapperAncFindings != null && wrapperAncFindings.getComplaints() != null
				&& wrapperAncFindings.getComplaints().size() > 0) {
			for (BenChiefComplaint complaintsDetails : wrapperAncFindings.getComplaints()) {
				benChiefComplaint = new BenChiefComplaint();
				benChiefComplaint.setBeneficiaryRegID(wrapperAncFindings.getBeneficiaryRegID());
				benChiefComplaint.setBenVisitID(wrapperAncFindings.getBenVisitID());
				benChiefComplaint.setProviderServiceMapID(wrapperAncFindings.getProviderServiceMapID());
				benChiefComplaint.setCreatedBy(wrapperAncFindings.getCreatedBy());

				if (null != complaintsDetails.getChiefComplaintID()) {
					/*
					 * Double d = (Double) complaintsDetails.getChiefComplaintID(); if (d == null)
					 * continue;
					 */
					benChiefComplaint.setChiefComplaintID(complaintsDetails.getChiefComplaintID());
				}
				if (null != complaintsDetails.getChiefComplaint())
					benChiefComplaint.setChiefComplaint(complaintsDetails.getChiefComplaint());
				if (null != complaintsDetails.getDuration())
					benChiefComplaint.setDuration(complaintsDetails.getDuration());
				if (null != complaintsDetails.getUnitOfDuration())
					benChiefComplaint.setUnitOfDuration(complaintsDetails.getUnitOfDuration());
				if (null != complaintsDetails.getDescription())
					benChiefComplaint.setDescription(complaintsDetails.getDescription());

				benChiefComplaintList.add(benChiefComplaint);
			}
		}
		return benChiefComplaintList;
	}

	public String getDocWorkList() {
		List<Object[]> docWorkListData = docWorkListRepo.getDocWorkList();
		return WrapperRegWorklist.getDocWorkListData(docWorkListData);
	}

	// New doc work-list service
	public String getDocWorkListNew(Integer providerServiceMapId, Integer serviceID, Integer vanID) {

		Calendar cal = Calendar.getInstance();
		if (docWL != null && docWL > 0 && docWL <= 30)
			cal.add(Calendar.DAY_OF_YEAR, -docWL);
		else
			cal.add(Calendar.DAY_OF_YEAR, -7);
		long sevenDaysAgo = cal.getTimeInMillis();

		// new Timestamp(fiveDaysAgo);

		ArrayList<BeneficiaryFlowStatus> docWorkList = new ArrayList<>();
		// MMU doc work-list
		if (serviceID != null && serviceID == 2) {
			docWorkList = beneficiaryFlowStatusRepo.getDocWorkListNew(providerServiceMapId);
		}
		// TC doc work-list
		else if (serviceID != null && serviceID == 4) {
			docWorkList = beneficiaryFlowStatusRepo.getDocWorkListNewTC(providerServiceMapId,
					new Timestamp(sevenDaysAgo), vanID);
		}

		return new Gson().toJson(docWorkList);
	}

	// New doc work-list service (Future scheduled beneficiary for TM)
	public String getDocWorkListNewFutureScheduledForTM(Integer providerServiceMapId, Integer serviceID,
			Integer vanID) {

		ArrayList<BeneficiaryFlowStatus> docWorkListFutureScheduled = new ArrayList<>();
		if (serviceID != null && serviceID == 4) {
			docWorkListFutureScheduled = beneficiaryFlowStatusRepo
					.getDocWorkListNewFutureScheduledTC(providerServiceMapId, vanID);
		}
		return new Gson().toJson(docWorkListFutureScheduled);
	}

	// New TC specialist work-list service
	public String getTCSpecialistWorkListNewForTMPatientApp(Integer providerServiceMapId, Integer userID,
			Integer serviceID, Integer vanID) {
		Calendar cal = Calendar.getInstance();
		if (tcSpeclistWL != null && tcSpeclistWL > 0 && tcSpeclistWL <= 30)
			cal.add(Calendar.DAY_OF_YEAR, -tcSpeclistWL);
		else
			cal.add(Calendar.DAY_OF_YEAR, -7);
		long sevenDaysAgo = cal.getTimeInMillis();

		ArrayList<BeneficiaryFlowStatus> tcSpecialistWorkList = new ArrayList<>();
		if (serviceID != null && serviceID == 4) {
			tcSpecialistWorkList = beneficiaryFlowStatusRepo.getTCSpecialistWorkListNewPatientApp(providerServiceMapId,
					userID, new Timestamp(sevenDaysAgo), vanID);
		}
		return new Gson().toJson(tcSpecialistWorkList);
	}

	// New TC specialist work-list service, patient App, 14-08-2020
	public String getTCSpecialistWorkListNewForTM(Integer providerServiceMapId, Integer userID, Integer serviceID) {
		Calendar cal = Calendar.getInstance();
		if (tcSpeclistWL != null && tcSpeclistWL > 0 && tcSpeclistWL <= 30)
			cal.add(Calendar.DAY_OF_YEAR, -tcSpeclistWL);
		else
			cal.add(Calendar.DAY_OF_YEAR, -7);
		long sevenDaysAgo = cal.getTimeInMillis();

		ArrayList<BeneficiaryFlowStatus> tcSpecialistWorkList = new ArrayList<>();
		if (serviceID != null && serviceID == 4) {
			tcSpecialistWorkList = beneficiaryFlowStatusRepo.getTCSpecialistWorkListNew(providerServiceMapId, userID,
					new Timestamp(sevenDaysAgo));
		}
		return new Gson().toJson(tcSpecialistWorkList);
	}

	// New TC specialist work-list service (Future scheduled beneficiary for TM)
	public String getTCSpecialistWorkListNewFutureScheduledForTM(Integer providerServiceMapId, Integer userID,
			Integer serviceID) {

		ArrayList<BeneficiaryFlowStatus> tcSpecialistWorkListFutureScheduled = new ArrayList<>();
		if (serviceID != null && serviceID == 4) {
			tcSpecialistWorkListFutureScheduled = beneficiaryFlowStatusRepo
					.getTCSpecialistWorkListNewFutureScheduled(providerServiceMapId, userID);
		}
		return new Gson().toJson(tcSpecialistWorkListFutureScheduled);
	}

	public String fetchBenPreviousSignificantFindings(Long beneficiaryRegID) {
		ArrayList<Object[]> previousSignificantFindings = (ArrayList<Object[]>) benClinicalObservationsRepo
				.getPreviousSignificantFindings(beneficiaryRegID);

		Map<String, Object> response = new HashMap<String, Object>();

		ArrayList<BenClinicalObservations> clinicalObservations = new ArrayList<BenClinicalObservations>();
		if (null != clinicalObservations) {
			for (Object[] obj : previousSignificantFindings) {
				BenClinicalObservations clinicalObservation = new BenClinicalObservations((String) obj[1],
						(Date) obj[0]);
				clinicalObservations.add(clinicalObservation);
			}
		}

		response.put("findings", clinicalObservations);
		return new Gson().toJson(response);

	}

	public Long saveBenReferDetails(JsonObject obj) throws IEMRException {
		Long ID = null;
		BenReferDetails referDetails = InputMapper.gson().fromJson(obj, BenReferDetails.class);
		List<BenReferDetails> referDetailsList = new ArrayList<BenReferDetails>();

		BenReferDetails referDetailsTemp = null;

		if (referDetails.getRefrredToAdditionalServiceList() != null
				&& referDetails.getRefrredToAdditionalServiceList().size() > 0) {
			for (ServiceMaster sm : referDetails.getRefrredToAdditionalServiceList()) {
				if (sm.getServiceName() != null) {
					referDetailsTemp = new BenReferDetails();
					referDetailsTemp.setBeneficiaryRegID(referDetails.getBeneficiaryRegID());
					referDetailsTemp.setBenVisitID(referDetails.getBenVisitID());
					referDetailsTemp.setVisitCode(referDetails.getVisitCode());
					referDetailsTemp.setProviderServiceMapID(referDetails.getProviderServiceMapID());
					referDetailsTemp.setVisitCode(referDetails.getVisitCode());
					referDetailsTemp.setCreatedBy(referDetails.getCreatedBy());
					referDetailsTemp.setVanID(referDetails.getVanID());
					referDetailsTemp.setParkingPlaceID(referDetails.getParkingPlaceID());

					referDetailsTemp.setServiceID(sm.getServiceID());
					referDetailsTemp.setServiceName(sm.getServiceName());

					if (referDetails.getReferredToInstituteID() != null
							&& referDetails.getReferredToInstituteName() != null) {
						referDetailsTemp.setReferredToInstituteID(referDetails.getReferredToInstituteID());
						referDetailsTemp.setReferredToInstituteName(referDetails.getReferredToInstituteName());
					}

					if (referDetails.getRevisitDate() != null)
						referDetailsTemp.setRevisitDate(referDetails.getRevisitDate());

					if (referDetails.getReferralReason() != null)
						referDetailsTemp.setReferralReason(referDetails.getReferralReason());

					referDetailsList.add(referDetailsTemp);
				}
			}
		} else {
			if (referDetails.getReferredToInstituteName() != null || referDetails.getRevisitDate() != null
					|| referDetails.getReferralReason() != null)
				referDetailsList.add(referDetails);

		}

		ArrayList<BenReferDetails> res = (ArrayList<BenReferDetails>) benReferDetailsRepo.saveAll(referDetailsList);
		if (referDetailsList.size() == res.size()) {
			ID = new Long(1);
		}
		return ID;
	}

	public String getFindingsDetails(Long beneficiaryRegID, Long visitCode) {
		ArrayList<Object[]> clinicalObservationsList = benClinicalObservationsRepo.getFindingsData(beneficiaryRegID,
				visitCode);
		ArrayList<Object[]> chiefComplaintsList = benChiefComplaintRepo.getBenChiefComplaints(beneficiaryRegID,
				visitCode);

		WrapperAncFindings findings = WrapperAncFindings.getFindingsData(clinicalObservationsList, chiefComplaintsList);
		return new Gson().toJson(findings);
	}

	public String getInvestigationDetails(Long beneficiaryRegID, Long visitCode) {
		ArrayList<Object[]> labTestOrders = labTestOrderDetailRepo.getLabTestOrderDetails(beneficiaryRegID, visitCode);
		WrapperBenInvestigationANC labTestOrdersList = LabTestOrderDetail.getLabTestOrderDetails(labTestOrders);

		return new Gson().toJson(labTestOrdersList);
	}

	public String getPrescribedDrugs(Long beneficiaryRegID, Long visitCode) {
		ArrayList<Object[]> resList = prescribedDrugDetailRepo.getBenPrescribedDrugDetails(beneficiaryRegID, visitCode);

		ArrayList<PrescribedDrugDetail> prescribedDrugs = PrescribedDrugDetail.getprescribedDrugs(resList);

		return new Gson().toJson(prescribedDrugs);
	}

	public String getReferralDetails(Long beneficiaryRegID, Long visitCode) {
		ArrayList<Object[]> resList = benReferDetailsRepo.getBenReferDetails(beneficiaryRegID, visitCode);

		BenReferDetails referDetails = BenReferDetails.getBenReferDetails(resList);

		return new Gson().toJson(referDetails);
	}

	public Integer updateDocFindings(WrapperAncFindings wrapperAncFindings) {
		int clinObsrvtnsRes = 0;
		int chiefCmpltsRes = 0;
		int updateFindingsRes = 0;

		BenClinicalObservations benClinicalObservations = getBenClinicalObservations(wrapperAncFindings);
		clinObsrvtnsRes = updateBenClinicalObservations(benClinicalObservations);

		ArrayList<BenChiefComplaint> tmpBenCHiefComplaints = wrapperAncFindings.getComplaints();
		ArrayList<BenChiefComplaint> tmpBenCHiefComplaintsTMP = new ArrayList<>();
		if (tmpBenCHiefComplaints.size() > 0) {
			for (BenChiefComplaint benChiefComplaint : tmpBenCHiefComplaints) {
				if (benChiefComplaint.getChiefComplaint() != null) {
					benChiefComplaint.setBeneficiaryRegID(wrapperAncFindings.getBeneficiaryRegID());
					benChiefComplaint.setBenVisitID(wrapperAncFindings.getBenVisitID());
					benChiefComplaint.setVisitCode(wrapperAncFindings.getVisitCode());
					benChiefComplaint.setProviderServiceMapID(wrapperAncFindings.getProviderServiceMapID());
					benChiefComplaint.setCreatedBy(wrapperAncFindings.getCreatedBy());

					tmpBenCHiefComplaintsTMP.add(benChiefComplaint);
				}
			}
			chiefCmpltsRes = updateDoctorBenChiefComplaints(tmpBenCHiefComplaintsTMP);

		} else {
			chiefCmpltsRes = 1;
		}
		if (clinObsrvtnsRes > 0 && chiefCmpltsRes > 0) {
			updateFindingsRes = 1;

		}
		return updateFindingsRes;
	}

	public int updateDoctorBenChiefComplaints(List<BenChiefComplaint> benChiefComplaintList) {
		int r = 0;
		if (null != benChiefComplaintList && benChiefComplaintList.size() > 0) {

			List<BenChiefComplaint> benChiefComplaintResultList = (List<BenChiefComplaint>) benChiefComplaintRepo
					.saveAll(benChiefComplaintList);

			if (benChiefComplaintResultList != null && benChiefComplaintResultList.size() > 0) {
				r = benChiefComplaintResultList.size();
			}
		} else {
			r = 1;
		}
		return r;
	}

	public int updateBenClinicalObservations(BenClinicalObservations benClinicalObservations) {
		Integer r = 0;
		int recordsAvailable = 0;
		if (null != benClinicalObservations) {
			String processed = benClinicalObservationsRepo.getBenClinicalObservationStatus(
					benClinicalObservations.getBeneficiaryRegID(), benClinicalObservations.getVisitCode());

			if (null != processed) {
				recordsAvailable = 1;
			}

			if (null != processed && !processed.equals("N")) {
				processed = "U";
			} else {
				processed = "N";
			}

			if (recordsAvailable > 0) {
				r = benClinicalObservationsRepo.updateBenClinicalObservations(
						benClinicalObservations.getClinicalObservation(), benClinicalObservations.getOtherSymptoms(),
						benClinicalObservations.getOtherSymptomsSCTCode(),
						benClinicalObservations.getOtherSymptomsSCTTerm(),
						benClinicalObservations.getSignificantFindings(), benClinicalObservations.getIsForHistory(),
						benClinicalObservations.getCreatedBy(), processed,
						benClinicalObservations.getBeneficiaryRegID(), benClinicalObservations.getVisitCode());
			} else {
				BenClinicalObservations observationsRes = benClinicalObservationsRepo.save(benClinicalObservations);
				if (null != observationsRes && observationsRes.getClinicalObservationID() > 0) {
					r = 1;
				}
			}
		}
		return r;
	}

	public Long updateBenReferDetails(JsonObject referObj) throws IEMRException {
		Long ID = null;
		int delRes = 0;
		BenReferDetails referDetails = InputMapper.gson().fromJson(referObj, BenReferDetails.class);
		List<BenReferDetails> referDetailsList = new ArrayList<BenReferDetails>();

		BenReferDetails referDetailsTemp = null;

		ArrayList<Object[]> benReferDetailsStatuses = benReferDetailsRepo
				.getBenReferDetailsStatus(referDetails.getBeneficiaryRegID(), referDetails.getVisitCode());

		for (Object[] obj : benReferDetailsStatuses) {
			String processed = (String) obj[1];
			if (null != processed && !"N".equals(processed)) {
				processed = "U";
			} else {
				processed = "N";
			}
			if (referDetails.getReferredToInstituteID() != null || referDetails.getReferredToInstituteName() != null
					|| referDetails.getRevisitDate() != null || referDetails.getReferralReason() != null) {
				benReferDetailsRepo.updateReferredInstituteName(referDetails.getReferredToInstituteID(),
						referDetails.getReferredToInstituteName(), referDetails.getRevisitDate(),
						referDetails.getReferralReason(), (Long) obj[0], processed);
			}
		}

		if (referDetails.getRefrredToAdditionalServiceList() != null
				&& referDetails.getRefrredToAdditionalServiceList().size() > 0) {
			for (ServiceMaster sm : referDetails.getRefrredToAdditionalServiceList()) {
				if (sm.getServiceName() != null) {
					referDetailsTemp = new BenReferDetails();
					referDetailsTemp.setBeneficiaryRegID(referDetails.getBeneficiaryRegID());
					referDetailsTemp.setBenVisitID(referDetails.getBenVisitID());
					referDetailsTemp.setProviderServiceMapID(referDetails.getProviderServiceMapID());
					referDetailsTemp.setVisitCode(referDetails.getVisitCode());
					referDetailsTemp.setCreatedBy(referDetails.getCreatedBy());
					referDetailsTemp.setVanID(referDetails.getVanID());
					referDetailsTemp.setParkingPlaceID(referDetails.getParkingPlaceID());
					if (referDetails.getReferredToInstituteID() != null
							&& referDetails.getReferredToInstituteName() != null) {
						referDetailsTemp.setReferredToInstituteID(referDetails.getReferredToInstituteID());
						referDetailsTemp.setReferredToInstituteName(referDetails.getReferredToInstituteName());
					}

					referDetailsTemp.setServiceID(sm.getServiceID());
					referDetailsTemp.setServiceName(sm.getServiceName());

					if (referDetails.getRevisitDate() != null)
						referDetailsTemp.setRevisitDate(referDetails.getRevisitDate());

					if (referDetails.getReferralReason() != null)
						referDetailsTemp.setReferralReason(referDetails.getReferralReason());

					referDetailsList.add(referDetailsTemp);
				}
			}
		} else {
			if (referDetails.getReferredToInstituteName() != null ||
					referDetails.getRevisitDate() != null || referDetails.getReferralReason() != null) {
				referDetailsList.add(referDetails);
			}
		}

		ArrayList<BenReferDetails> res = (ArrayList<BenReferDetails>) benReferDetailsRepo.saveAll(referDetailsList);
		if (referDetailsList.size() == res.size()) {
			ID = new Long(1);
		}
		return ID;
	}

	/**
	 * 
	 * 
	 * @param commonUtilityClass
	 * @param testList
	 * @param drugList
	 * @return
	 * @throws IEMRException
	 */
	/// ------Start of beneficiary flow table after doctor data save-------------

	public int updateBenFlowtableAfterDocDataSave(CommonUtilityClass commonUtilityClass, Boolean isTestPrescribed,
			Boolean isMedicinePrescribed, TeleconsultationRequestOBJ tcRequestOBJ, Boolean signatureFlag) throws IEMRException {
		short pharmaFalg;
		short docFlag = (short) 1;
		short tcSpecialistFlag = (short) 0;

		// for feto sense
		short labTechnicianFlag = (short) 0;
		int tcUserID = 0;
		Timestamp tcDate = null;

		Long tmpBenFlowID = commonUtilityClass.getBenFlowID();
		Long tmpBeneficiaryID = commonUtilityClass.getBeneficiaryID();
		Long tmpBenVisitID = commonUtilityClass.getBenVisitID();
		Long tmpbeneficiaryRegID = commonUtilityClass.getBeneficiaryRegID();

		if (commonUtilityClass != null && commonUtilityClass.getVisitCategoryID() != null
				&& commonUtilityClass.getVisitCategoryID() == 4) {
			ArrayList<FoetalMonitor> foetalMonitorData = foetalMonitorRepo
					.getFoetalMonitorDetailsByFlowId(tmpBenFlowID);
			if (foetalMonitorData.size() > 0) {
				labTechnicianFlag = 3;
				for (FoetalMonitor data : foetalMonitorData) {
					if (data != null && !data.getResultState()) {
						labTechnicianFlag = 2;
					}
					if (data != null && data.getVisitCode() == null) {
						foetalMonitorRepo.updateVisitCode(commonUtilityClass.getVisitCode(), tmpBenFlowID);
					}
				}
			}
		}
		// get lab technician flag,SH20094090,19-7-2021(Foetal monitor flag changes)
		// Short labFlag= beneficiaryFlowStatusRepo.getLabTechnicianFlag(tmpBenFlowID);

		// check if TC specialist or doctor
		if (commonUtilityClass != null && commonUtilityClass.getIsSpecialist() != null
				&& commonUtilityClass.getIsSpecialist() == true) {
			// checking if test is prescribed
			if (isTestPrescribed) {
				tcSpecialistFlag = (short) 2;
			} else {
				// update lab technician flag,SH20094090,19-7-2021(Foetal monitor flag changes)
				if (labTechnicianFlag == 3)
					labTechnicianFlag = 9;
				tcSpecialistFlag = (short) 9;
			}

		} else {
			// checking if test is prescribed
			if (isTestPrescribed) {
				docFlag = (short) 2;
			} else {
				docFlag = (short) 9;
				// update lab technician flag,SH20094090,19-7-2021(Foetal monitor flag changes)
				if (labTechnicianFlag == 3)
					labTechnicianFlag = 9;
			}
		}

		// checking if medicine is prescribed
		if (isMedicinePrescribed) {
			pharmaFalg = (short) 1;
		} else {
			pharmaFalg = (short) 0;
		}

		if (tcRequestOBJ != null && tcRequestOBJ.getUserID() != null && tcRequestOBJ.getUserID() > 0
				&& tcRequestOBJ.getAllocationDate() != null) {
			tcSpecialistFlag = (short) 1;
			tcUserID = tcRequestOBJ.getUserID();
			tcDate = tcRequestOBJ.getAllocationDate();

		}

		int i = 0;

		if (commonUtilityClass != null && commonUtilityClass.getIsSpecialist() != null
				&& commonUtilityClass.getIsSpecialist() == true) {
			// updating lab technician flag as well after feto sense
			i = commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataFromSpecialist(tmpBenFlowID,
					tmpbeneficiaryRegID, tmpBeneficiaryID, tmpBenVisitID, docFlag, pharmaFalg, (short) 0,
					tcSpecialistFlag, labTechnicianFlag,signatureFlag);
			if (tcSpecialistFlag == 9) {
				int l = tCRequestModelRepo.updateStatusIfConsultationCompleted(commonUtilityClass.getBeneficiaryRegID(),
						commonUtilityClass.getVisitCode(), "D");
			}

			// check if consultation start time is there and update the end time
			TeleconsultationStats teleconsultationStats = teleconsultationStatsRepo
					.getLatestStartTime(commonUtilityClass.getBeneficiaryRegID(), commonUtilityClass.getVisitCode());

			// if consultation end time is not available, update it else create a new entry
			if (teleconsultationStats != null) {
				if (teleconsultationStats.getEndTime() == null) {
					teleconsultationStats.setEndTime(new Timestamp(System.currentTimeMillis()));
				} else {
					teleconsultationStats.settMStatsID(null);
					teleconsultationStats.setEndTime(new Timestamp(System.currentTimeMillis()));
				}

				teleconsultationStatsRepo.save(teleconsultationStats);
			}

		} else
			i = commonBenStatusFlowServiceImpl.updateBenFlowAfterDocData(tmpBenFlowID, tmpbeneficiaryRegID,
					tmpBeneficiaryID, tmpBenVisitID, docFlag, pharmaFalg, (short) 0, tcSpecialistFlag, tcUserID, tcDate,
					labTechnicianFlag, signatureFlag);
		// TM Prescription SMS
		if (commonUtilityClass.getIsSpecialist() == true) {
			if (tcSpecialistFlag == 9) {
				if (commonUtilityClass.getPrescriptionID() != null) {
					try {
						createTMPrescriptionSms(commonUtilityClass);
					} catch (Exception e) {
						logger.info("Error while sending TM prescription SMS :" + e.getMessage());
					}
				}

			}

		} else if (commonUtilityClass.getIsSpecialist() == false) {
			if (docFlag == 9 && tcRequestOBJ == null) {
				if (commonUtilityClass.getPrescriptionID() != null) {
					try {
						createTMPrescriptionSms(commonUtilityClass);
					} catch (Exception e) {
						logger.info("Error while sending TM prescription SMS :" + e.getMessage());
					}
				}
			}
		}
		return i;
	}

	/// ------End of beneficiary flow table after doctor data save-------------

	/// ------Start of beneficiary flow table after doctor data update-------------
	/**
	 * @param commonUtilityClass
	 * @param isTestPrescribed
	 * @param isMedicinePrescribed
	 * @return
	 */
	public int updateBenFlowtableAfterDocDataUpdate(CommonUtilityClass commonUtilityClass, Boolean isTestPrescribed,
			Boolean isMedicinePrescribed, TeleconsultationRequestOBJ tcRequestOBJ, Boolean doctorSignatureFlag) throws Exception {
		int i = 0;
		short pharmaFalg;
		short docFlag = (short) 0;
		short tcSpecialistFlag = (short) 0;
		int tcUserID = 0;
		Timestamp tcDate = null;

		// for feto sense
		short labTechnicianFlag = (short) 0;

		Long tmpBenFlowID = commonUtilityClass.getBenFlowID();
		Long tmpBeneficiaryID = commonUtilityClass.getBeneficiaryID();
		Long tmpBenVisitID = commonUtilityClass.getBenVisitID();
		Long tmpbeneficiaryRegID = commonUtilityClass.getBeneficiaryRegID();

		// Foetal monitor related update in visitcode and lab flag
		if (commonUtilityClass != null && commonUtilityClass.getVisitCategoryID() != null
				&& commonUtilityClass.getVisitCategoryID() == 4) {
			ArrayList<FoetalMonitor> foetalMonitorData = foetalMonitorRepo
					.getFoetalMonitorDetailsByFlowId(tmpBenFlowID);
			if (foetalMonitorData.size() > 0) {
				labTechnicianFlag = 3;
				for (FoetalMonitor data : foetalMonitorData) {
					if (data != null && !data.getResultState()) {
						labTechnicianFlag = 2;
					}
					if (data != null && data.getVisitCode() == null) {
						foetalMonitorRepo.updateVisitCode(commonUtilityClass.getVisitCode(), tmpBenFlowID);
					}
				}
			}
		}

		if (commonUtilityClass.getIsSpecialist() != null && commonUtilityClass.getIsSpecialist() == true) {
			if (isTestPrescribed)
				tcSpecialistFlag = (short) 2;
			else {
				tcSpecialistFlag = (short) 9;
				// update lab technician flag,SH20094090,19-7-2021(foetalMonitor flag changes)
				if (labTechnicianFlag == 3)
					labTechnicianFlag = 9;
			}

			if (isMedicinePrescribed)
				pharmaFalg = (short) 1;
			else
				pharmaFalg = (short) 0;

			i = commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdateTCSpecialist(tmpBenFlowID,
					tmpbeneficiaryRegID, tmpBeneficiaryID, tmpBenVisitID, docFlag, pharmaFalg, (short) 0,
					tcSpecialistFlag, tcUserID, tcDate, labTechnicianFlag, doctorSignatureFlag);

			if (tcSpecialistFlag == 9) {
				int l = tCRequestModelRepo.updateStatusIfConsultationCompleted(commonUtilityClass.getBeneficiaryRegID(),
						commonUtilityClass.getVisitCode(), "D");
			}

			// check if consultation start time is there and update the end time
			TeleconsultationStats teleconsultationStats = teleconsultationStatsRepo
					.getLatestStartTime(commonUtilityClass.getBeneficiaryRegID(), commonUtilityClass.getVisitCode());

			// if consultation end time is not available, update it else create a new entry
			if (teleconsultationStats != null) {
				if (teleconsultationStats.getEndTime() == null) {
					teleconsultationStats.setEndTime(new Timestamp(System.currentTimeMillis()));
				} else {
					teleconsultationStats.settMStatsID(null);
					teleconsultationStats.setEndTime(new Timestamp(System.currentTimeMillis()));
				}

				teleconsultationStatsRepo.save(teleconsultationStats);
			}

		} else {

			if (isTestPrescribed)
				docFlag = (short) 2;
			else {
				docFlag = (short) 9;
				// update lab technician flag,SH20094090,19-7-2021(foetalMonitor flag changes)
				if (labTechnicianFlag == 3)
					labTechnicianFlag = 9;
			}

			if (isMedicinePrescribed)
				pharmaFalg = (short) 1;
			else
				pharmaFalg = (short) 0;

			if (tcRequestOBJ != null && tcRequestOBJ.getUserID() != null && tcRequestOBJ.getUserID() > 0
					&& tcRequestOBJ.getAllocationDate() != null) {
				tcSpecialistFlag = (short) 1;
				tcUserID = tcRequestOBJ.getUserID();
				tcDate = tcRequestOBJ.getAllocationDate();
			}

			i = commonBenStatusFlowServiceImpl.updateBenFlowAfterDocDataUpdate(tmpBenFlowID, tmpbeneficiaryRegID,
					tmpBeneficiaryID, tmpBenVisitID, docFlag, pharmaFalg, (short) 0, tcSpecialistFlag, tcUserID, tcDate,
					labTechnicianFlag, doctorSignatureFlag);

		}

		// TM Prescription SMS
		if (commonUtilityClass.getIsSpecialist() == true) {
			if (tcSpecialistFlag == 9) {
				if (commonUtilityClass.getPrescriptionID() != null)
					createTMPrescriptionSms(commonUtilityClass);
			}
		} else if (commonUtilityClass.getIsSpecialist() == false) {
			if (docFlag == 9 && tcRequestOBJ == null) {
				if (commonUtilityClass.getPrescriptionID() != null)
					createTMPrescriptionSms(commonUtilityClass);
			}
		}
		return i;
	}

	/// ------End of beneficiary flow table after doctor data update-------------

	public String deletePrescribedMedicine(JSONObject obj) {
		int i = 0;
		if (obj != null && obj.has("id")) {
			try {
				i = prescribedDrugDetailRepo.deletePrescribedmedicine(obj.getLong("id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

		}
		if (i > 0)
			return "record deleted successfully";
		else
			return null;
	}

	public int callTmForSpecialistSlotBook(TcSpecialistSlotBookingRequestOBJ tcSpecialistSlotBookingRequestOBJ,
			String Authorization) {
		int successFlag = 0;
		// OutputMapper outputMapper = new OutputMapper();
		String requestOBJ = OutputMapper.gson().toJson(tcSpecialistSlotBookingRequestOBJ);

		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Object> request = RestTemplateUtil.createRequestEntity(requestOBJ, Authorization);
		ResponseEntity<String> response = restTemplate.exchange(tcSpecialistSlotBook, HttpMethod.POST, request,
				String.class);
		// System.out.println(response.getBody());

		if (response.getStatusCodeValue() == 200 && response.hasBody()) {
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(response.getBody());
			jsnOBJ = jsnElmnt.getAsJsonObject();
			if (jsnOBJ.has("statusCode") && jsnOBJ.get("statusCode").getAsInt() == 200)
				successFlag = 1;
		}
		return successFlag;
	}

	// Shubham Shekhar,15-10-2020,TM Prescription SMS
	public void createTMPrescriptionSms(CommonUtilityClass commonUtilityClass) throws IEMRException {
		List<Object> diagnosis = null;
		List<PrescribedDrugDetail> prescriptionDetails = null;
		int k = 0;
		prescriptionDetails = prescribedDrugDetailRepo.getPrescriptionDetails(commonUtilityClass.getPrescriptionID());
		if (prescriptionDetails != null && prescriptionDetails.size() > 0) {
			try {

				if (commonUtilityClass.getVisitCategoryID() == 6 || commonUtilityClass.getVisitCategoryID() == 7
						|| commonUtilityClass.getVisitCategoryID() == 8) {
					diagnosis = prescriptionDetailRepo.getProvisionalDiagnosis(commonUtilityClass.getVisitCode(),
							commonUtilityClass.getPrescriptionID());// add visit code too
				} else if (commonUtilityClass.getVisitCategoryID() == 3) {
					diagnosis = NCDCareDiagnosisRepo.getNCDcondition(commonUtilityClass.getVisitCode(),
							commonUtilityClass.getPrescriptionID());// add visit code too
				} else if (commonUtilityClass.getVisitCategoryID() == 5) {
					diagnosis = pNCDiagnosisRepo.getProvisionalDiagnosis(commonUtilityClass.getVisitCode(),
							commonUtilityClass.getPrescriptionID());
				}
			} catch (Exception e) {
				logger.info("Exception during fetching diagnosis and precription detail " + e.getMessage());
			}

			try {
				if (prescriptionDetails != null)
					k = sMSGatewayServiceImpl.smsSenderGateway2("prescription", prescriptionDetails,
							commonUtilityClass.getAuthorization(), commonUtilityClass.getBeneficiaryRegID(),
							commonUtilityClass.getCreatedBy(), diagnosis);
			} catch (Exception e) {
				logger.info("Exception during sending TM prescription SMS " + e.getMessage());
			}
		}
		if (k != 0)
			logger.info("SMS sent for TM Prescription");
		else
			logger.info("SMS not sent for TM Prescription");
	}

	public String getFoetalMonitorData(Long beneFiciaryRegID, Long visitCode) {

		ArrayList<FoetalMonitor> foetalMonitorData = foetalMonitorRepo
				.getFoetalMonitorDetailsForCaseRecord(beneFiciaryRegID, visitCode);

		return new Gson().toJson(foetalMonitorData);

	}
}
