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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonMasterServiceImpl implements CommonMaterService {

	private ANCMasterDataServiceImpl ancMasterDataServiceImpl;
	private NurseMasterDataServiceImpl nurseMasterDataServiceImpl;
	private DoctorMasterDataServiceImpl doctorMasterDataServiceImpl;
	private RegistrarServiceMasterDataImpl registrarServiceMasterDataImpl;
	private NCDScreeningMasterServiceImpl ncdScreeningServiceImpl;
	private QCMasterDataServiceImpl qCMasterDataServiceImpl;
	private NCDCareMasterDataServiceImpl ncdCareMasterDataServiceImpl;

	@Autowired
	public void setNcdCareMasterDataServiceImpl(NCDCareMasterDataServiceImpl ncdCareMasterDataServiceImpl) {
		this.ncdCareMasterDataServiceImpl = ncdCareMasterDataServiceImpl;
	}

	@Autowired
	public void setqCMasterDataServiceImpl(QCMasterDataServiceImpl qCMasterDataServiceImpl) {
		this.qCMasterDataServiceImpl = qCMasterDataServiceImpl;
	}

	@Autowired
	public void setRegistrarServiceMasterDataImpl(RegistrarServiceMasterDataImpl registrarServiceMasterDataImpl) {
		this.registrarServiceMasterDataImpl = registrarServiceMasterDataImpl;
	}

	@Autowired
	public void setAncMasterDataServiceImpl(ANCMasterDataServiceImpl ancMasterDataServiceImpl) {
		this.ancMasterDataServiceImpl = ancMasterDataServiceImpl;
	}

	@Autowired
	public void setNurseMasterDataServiceImpl(NurseMasterDataServiceImpl nurseMasterDataServiceImpl) {
		this.nurseMasterDataServiceImpl = nurseMasterDataServiceImpl;
	}

	@Autowired
	public void setDoctorMasterDataServiceImpl(DoctorMasterDataServiceImpl doctorMasterDataServiceImpl) {
		this.doctorMasterDataServiceImpl = doctorMasterDataServiceImpl;
	}

	@Autowired
	public void setNcdScreeningServiceImpl(NCDScreeningMasterServiceImpl ncdScreeningServiceImpl) {
		this.ncdScreeningServiceImpl = ncdScreeningServiceImpl;
	}

	@Override
	public String getVisitReasonAndCategories() {
		String visitReasonAndCategories = nurseMasterDataServiceImpl.GetVisitReasonAndCategories();
		return visitReasonAndCategories;
	}

	@Override
	public String getMasterDataForNurse(Integer visitCategoryID, Integer providerServiceMapID, String gender) {
		String nurseMasterData = null;
		if (null != visitCategoryID) {
			switch (visitCategoryID) {
			case 1: {
				// 1 : Cancer Screening
				nurseMasterData = nurseMasterDataServiceImpl.getCancerScreeningMasterDataForNurse();
			}
				break;
			case 2: {
				// 2 : NCD screening
				nurseMasterData = ncdScreeningServiceImpl.getNCDScreeningMasterData(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			case 3: {
				// 3 : NCD care
				// TODO: NCD Care Master Data call - tmprlly calling ANC master Data
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			case 4: {
				// 4 : ANC
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			case 5: {
				// 5 : PNC
				// TODO: PNC Master Data call - tmprlly calling ANC master Data
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			case 6: {
				// 6 : General OPD
				// TODO: General OPD Master Data call - tmprlly calling ANC master Data
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			case 7: {
				// 7 : General OPD (QC)
				// nurseMasterData = "No Master Data found for QuickConsultation";
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			case (8): {
				// 8 : Covid 19 - pandemic
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
			case (10): {
				// 10 : Covid 19 - pandemic
				nurseMasterData = ancMasterDataServiceImpl
						.getCommonNurseMasterDataForGenopdAncNcdcarePnc(visitCategoryID, providerServiceMapID, gender);
			}
				break;
			default: {
				nurseMasterData = "Invalid VisitCategoryID";
			}
			}
		} else {
			nurseMasterData = "Invalid VisitCategoryID";
		}
		return nurseMasterData;
	}

	@Override
	public String getMasterDataForDoctor(Integer visitCategoryID, Integer providerServiceMapID, String gender,
			Integer facilityID, Integer vanID) {
		String doctorMasterData = null;
		if (null != visitCategoryID) {
			switch (visitCategoryID) {
			case 1: {
				// 1 : Cancer Screening
				doctorMasterData = doctorMasterDataServiceImpl
						.getCancerScreeningMasterDataForDoctor(providerServiceMapID);
			}
				break;
			case 2: {
				// 2 : NCD screening
				// TODO: NCD SCreening Master Data call
//				doctorMasterData = "No Master Data found for NCD SCreening";
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 3: {
				// 3 : NCD care
				// TODO: NCD Care Master Data call
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 4: {
				// 4 : ANC
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 5: {
				// 5 : PNC
				// TODO: PNC Master Data call - tmprlly calling ANC master Data
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 6: {
				// 6 : General OPD
				// TODO: General OPD Master Data call - tmprlly calling ANC master Data
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 7: {
				// 7 : General OPD (QC)
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 8: {
				// 8, covid
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			case 10: {
				// 10, covid, only applicable for dev env, later will be removed
				doctorMasterData = ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(
						visitCategoryID, providerServiceMapID, gender, facilityID, vanID);
			}
				break;
			default: {
				doctorMasterData = "Invalid VisitCategoryID";
			}
			}
		} else {
			doctorMasterData = "Invalid VisitCategoryID";
		}
		return doctorMasterData;
	}

}
