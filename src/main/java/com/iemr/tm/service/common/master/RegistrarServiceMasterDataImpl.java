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
import java.util.List;
import java.util.Map;

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

import com.google.gson.Gson;
import com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus;
import com.iemr.tm.data.masterdata.registrar.CommunityMaster;
import com.iemr.tm.data.masterdata.registrar.GenderMaster;
import com.iemr.tm.data.masterdata.registrar.GovIdEntityType;
import com.iemr.tm.data.masterdata.registrar.IncomeStatusMaster;
import com.iemr.tm.data.masterdata.registrar.MaritalStatusMaster;
import com.iemr.tm.data.masterdata.registrar.OccupationMaster;
import com.iemr.tm.data.masterdata.registrar.QualificationMaster;
import com.iemr.tm.data.masterdata.registrar.ReligionMaster;
import com.iemr.tm.data.registrar.BeneficiaryData;
import com.iemr.tm.repo.benFlowStatus.BeneficiaryFlowStatusRepo;
import com.iemr.tm.repo.masterrepo.CommunityMasterRepo;
import com.iemr.tm.repo.masterrepo.GenderMasterRepo;
import com.iemr.tm.repo.masterrepo.GovIdEntityTypeRepo;
import com.iemr.tm.repo.masterrepo.IncomeStatusMasterRepo;
import com.iemr.tm.repo.masterrepo.MaritalStatusMasterRepo;
import com.iemr.tm.repo.masterrepo.OccupationMasterRepo;
import com.iemr.tm.repo.masterrepo.QualificationMasterRepo;
import com.iemr.tm.repo.masterrepo.ReligionMasterRepo;
import com.iemr.tm.repo.nurse.anc.ANCCareRepo;
import com.iemr.tm.repo.registrar.BeneficiaryImageRepo;
import com.iemr.tm.repo.registrar.ReistrarRepoBenSearch;
import com.iemr.tm.utils.RestTemplateUtil;

@Service
@PropertySource("classpath:application.properties")
public class RegistrarServiceMasterDataImpl implements RegistrarServiceMasterData {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Value("${getBenImageFromIdentity}")
	private String getBenImageFromIdentity;

	private CommunityMasterRepo communityMasterRepo;
	private GenderMasterRepo genderMasterRepo;
	private GovIdEntityTypeRepo govIdEntityTypeRepo;
	private IncomeStatusMasterRepo incomeStatusMasterRepo;
	private MaritalStatusMasterRepo maritalStatusMasterRepo;
	private OccupationMasterRepo occupationMasterRepo;
	private QualificationMasterRepo qualificationMasterRepo;
	private ReligionMasterRepo religionMasterRepo;
	private BeneficiaryImageRepo beneficiaryImageRepo;
	private BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo;
	private ANCCareRepo aNCCareRepo;

	@Autowired
	public void setaNCCareRepo(ANCCareRepo aNCCareRepo) {
		this.aNCCareRepo = aNCCareRepo;
	}

	@Autowired
	public void setBeneficiaryFlowStatusRepo(BeneficiaryFlowStatusRepo beneficiaryFlowStatusRepo) {
		this.beneficiaryFlowStatusRepo = beneficiaryFlowStatusRepo;
	}

	@Autowired
	public void setBeneficiaryImageRepo(BeneficiaryImageRepo beneficiaryImageRepo) {
		this.beneficiaryImageRepo = beneficiaryImageRepo;
	}

	@Autowired
	public void setCommunityMasterRepo(CommunityMasterRepo communityMasterRepo) {
		this.communityMasterRepo = communityMasterRepo;
	}

	@Autowired
	public void setGenderMasterRepo(GenderMasterRepo genderMasterRepo) {
		this.genderMasterRepo = genderMasterRepo;
	}

	@Autowired
	public void setGovIdEntityTypeRepo(GovIdEntityTypeRepo govIdEntityTypeRepo) {
		this.govIdEntityTypeRepo = govIdEntityTypeRepo;
	}

	@Autowired
	public void setIncomeStatusMasterRepo(IncomeStatusMasterRepo incomeStatusMasterRepo) {
		this.incomeStatusMasterRepo = incomeStatusMasterRepo;
	}

	@Autowired
	public void setMaritalStatusMasterRepo(MaritalStatusMasterRepo maritalStatusMasterRepo) {
		this.maritalStatusMasterRepo = maritalStatusMasterRepo;
	}

	@Autowired
	public void setOccupationMasterRepo(OccupationMasterRepo occupationMasterRepo) {
		this.occupationMasterRepo = occupationMasterRepo;
	}

	@Autowired
	public void setQualificationMasterRepo(QualificationMasterRepo qualificationMasterRepo) {
		this.qualificationMasterRepo = qualificationMasterRepo;
	}

	@Autowired
	public void setReligionMasterRepo(ReligionMasterRepo religionMasterRepo) {
		this.religionMasterRepo = religionMasterRepo;
	}

	public String getRegMasterData() {
		Map<String, Object> resMap = new HashMap<String, Object>();
		ArrayList<Object[]> cm = communityMasterRepo.getCommunityMaster();
		ArrayList<Object[]> gm = genderMasterRepo.getGenderMaster();
		ArrayList<Object[]> gietm = govIdEntityTypeRepo.getGovIdEntityMaster();
		ArrayList<Object[]> ogietm = govIdEntityTypeRepo.getOtherGovIdEntityMaster();
		ArrayList<Object[]> ism = incomeStatusMasterRepo.getIncomeStatusMaster();
		ArrayList<Object[]> msm = maritalStatusMasterRepo.getMaritalStatusMaster();
		ArrayList<Object[]> om = occupationMasterRepo.getOccupationMaster();
		ArrayList<Object[]> qm = qualificationMasterRepo.getQualificationMaster();
		ArrayList<Object[]> rm = religionMasterRepo.getReligionMaster();

		// System.out.println("hello....");
		try {
			resMap.put("communityMaster", CommunityMaster.getCommunityMasterData(cm));
			resMap.put("genderMaster", GenderMaster.getGenderMasterData(gm));
			resMap.put("govIdEntityMaster", GovIdEntityType.getGovIdEntityTypeData(gietm));
			resMap.put("otherGovIdEntityMaster", GovIdEntityType.getGovIdEntityTypeData(ogietm));
			resMap.put("incomeMaster", IncomeStatusMaster.getIncomeStatusMasterData(ism));
			resMap.put("maritalStatusMaster", MaritalStatusMaster.getMaritalStatusMasterData(msm));
			resMap.put("occupationMaster", OccupationMaster.getOccupationMasterData(om));
			resMap.put("qualificationMaster", QualificationMaster.getQualificationMasterData(qm));
			resMap.put("religionMaster", ReligionMaster.getReligionMasterData(rm));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return new Gson().toJson(resMap);

	}

	private ReistrarRepoBenSearch reistrarRepoBenSearch;

	@Autowired
	public void setReistrarRepoAdvanceBenSearch(ReistrarRepoBenSearch reistrarRepoBenSearch) {
		this.reistrarRepoBenSearch = reistrarRepoBenSearch;
	}

	@Override
	public String getBenDetailsByRegID(Long beneficiaryRegID) {
		List<Object[]> benDetailsList = reistrarRepoBenSearch.getBenDetails(beneficiaryRegID);
		BeneficiaryData benDetails = BeneficiaryData.getBeneficiaryData(benDetailsList).get(0);
		benDetails.setImage(beneficiaryImageRepo.getBenImage(beneficiaryRegID));
		if (benDetails != null) {
			if (benDetails.getGenderID() != null) {
				if (benDetails.getGenderID() == 1) {
					benDetails.setGenderName("Male");
				} else if (benDetails.getGenderID() == 2) {
					benDetails.setGenderName("Female");
				} else {
					if (benDetails.getGenderID() == 3) {
						benDetails.setGenderName("Transgender");
					}
				}
			}
		}
		return new Gson().toJson(benDetails);
	}

	// Beneficiary details for left side of the screen new
	public String getBenDetailsForLeftSideByRegIDNew(Long beneficiaryRegID, Long benFlowID, String Authorization,
			String comingRequest) {

		ArrayList<Object[]> benFlowOBJ = beneficiaryFlowStatusRepo.getBenDetailsForLeftSidePanel(beneficiaryRegID,
				benFlowID);

		BeneficiaryFlowStatus returnBenFlowOBJ = BeneficiaryFlowStatus.getBeneficiaryFlowStatusForLeftPanel(benFlowOBJ);
		// have to add image also from common and identity.

		String bloodGroup = aNCCareRepo.getBenANCCareDetailsStatus(beneficiaryRegID);
		if (bloodGroup != null)
			returnBenFlowOBJ.setBloodGroup(bloodGroup);

		return new Gson().toJson(returnBenFlowOBJ);
	}

	public String getBenImageFromIdentityAPI(String Authorization, String comingRequest) throws Exception {
		String returnOBJ = null;
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Object> request = RestTemplateUtil.createRequestEntity(comingRequest, Authorization);
		ResponseEntity<String> response = restTemplate.exchange(getBenImageFromIdentity, HttpMethod.POST, request,
				String.class);
		returnOBJ = response.getBody();
		return returnOBJ;
	}
}
