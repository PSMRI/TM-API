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

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.tm.controller.common.master.CommonMasterController;
import com.iemr.tm.service.patientApp.master.CommonPatientAppMasterService;
import com.iemr.tm.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping(value = "/master", headers = "Authorization", consumes = "application/json", produces = "application/json")

public class PatientAppCommonMasterController {

	private Logger logger = LoggerFactory.getLogger(PatientAppCommonMasterController.class);
	private CommonPatientAppMasterService commonPatientAppMasterService;

	@Autowired
	public void setCommonPatientAppMasterService(CommonPatientAppMasterService commonPatientAppMasterService) {
		this.commonPatientAppMasterService = commonPatientAppMasterService;
	}
	/**
	 * @param visitCategoryID
	 * @return nurse master data for the provided visitCategoryID
	 */
	@Operation(summary= "Chief complaints master data API for patient app")
	@PostMapping(value = "/patientApp/chiefComplaintsMaster/{visitCategoryID}/{providerServiceMapID}/{gender}")
	public String patientAppChiefComplaintsMasterData(@PathVariable("visitCategoryID") Integer visitCategoryID,
			@PathVariable("providerServiceMapID") Integer providerServiceMapID, @PathVariable("gender") String gender) {
		logger.info("Nurse master Data for categoryID:" + visitCategoryID + " and providerServiceMapID:"
				+ providerServiceMapID);
		;
		OutputResponse response = new OutputResponse();
		response.setResponse(
				commonPatientAppMasterService.getChiefComplaintsMaster(visitCategoryID, providerServiceMapID, gender));
		logger.info("Nurse master Data for categoryID:" + response.toString());
		return response.toString();
	}

	@Operation(summary= "COVID master data API for patient app")
	@PostMapping(value = "/patientApp/covidMaster/{visitCategoryID}/{providerServiceMapID}/{gender}")
	public String patientAppCovidMasterData(@PathVariable("visitCategoryID") Integer visitCategoryID,
			@PathVariable("providerServiceMapID") Integer providerServiceMapID, @PathVariable("gender") String gender) {
		logger.info("Nurse master Data for categoryID:" + visitCategoryID + " and providerServiceMapID:"
				+ providerServiceMapID);
		;
		OutputResponse response = new OutputResponse();
		response.setResponse(
				commonPatientAppMasterService.getCovidMaster(visitCategoryID, providerServiceMapID, gender));
		logger.info("Nurse master Data for categoryID:" + response.toString());
		return response.toString();
	}

	
	@Operation(summary= "Save COVID data in patient app")
	@PostMapping(value = { "/save/covidScreeningDataPatientApp" })
	public String saveBenCovidDoctorDataPatientApp(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for Covid data saving - patient APP :" + requestObj);

			String s = commonPatientAppMasterService.saveCovidScreeningData(requestObj);

			response.setResponse(s);

		} catch (Exception e) {
			logger.error("Error while saving Covid data - patient APP :" + e);
			response.setError(5000, "Unable to save data. " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Save chief-complaints data in patient app")
	@PostMapping(value = { "/save/chiefComplaintsDataPatientApp" })
	public String saveBenChiefComplaintsDataPatientApp(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for chief-Complaints data saving - patient APP :" + requestObj);

			String s = commonPatientAppMasterService.savechiefComplaintsData(requestObj);

			response.setResponse(s);

		} catch (Exception e) {
			logger.error("Error while saving chief-Complaints data - patient APP :" + e);
			response.setError(5000, "Unable to save data. " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Save tele-consultation slot in data patient app")
	@PostMapping(value = { "/save/tcSlotDetailsDataPatientApp" })
	public String saveTCSlotDataPatientApp(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for TC slot data saving - patient APP :" + requestObj);

			Integer i = commonPatientAppMasterService.bookTCSlotData(requestObj, Authorization);
			if (i > 0)
				response.setResponse("Teleconsultation slot booked successfully");
			else
				response.setError(5000, "error in slot booking");

		} catch (Exception e) {
			logger.error("Error while booking TC slot - patient APP :" + e);
			response.setError(5000, "error in slot booking : " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Get patient episode data for specialist in patient app")
	@PostMapping(value = { "/get/getPatientEpisodeData" })
	public String getPatientEpisodeDataMobileApp(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for patient episode data for diagnosis - patient APP :" + requestObj);

			String s = commonPatientAppMasterService.getPatientEpisodeData(requestObj);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "error in getting beneficiary episode data");

		} catch (Exception e) {
			logger.error("error in getting beneficiary episode data - patient APP :" + e);
			response.setError(5000, "error in getting beneficiary episode data : " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Get patient booked slot data in patient app")
	@PostMapping(value = { "/get/getPatientBookedSlotDetails" })
	public String getPatientBookedSlotDetails(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for patient booked slot data - patient APP :" + requestObj);

			String s = commonPatientAppMasterService.getPatientBookedSlots(requestObj);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "error in getting beneficiary booked slot data");

		} catch (Exception e) {
			logger.error("error in getting beneficiary booked slot data - patient APP :" + e);
			response.setError(5000, "error in getting beneficiary booked slot data : " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Save specialist diagnosis data in patient app")
	@PostMapping(value = { "/save/saveSpecialistDiagnosisData" })
	public String saveSpecialistDiagnosisData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for specialist diagnosis data - patient APP :" + requestObj);

			Long pID = commonPatientAppMasterService.saveSpecialistDiagnosisData(requestObj);
			if (pID != null)
				response.setResponse("success");
			else
				response.setError(5000, "error in saving diagnosis data");

		} catch (Exception e) {
			logger.error("error in saving specialist diagnosis data - patient APP :" + e);
			response.setError(5000, "error in saving specialist diagnosis data : " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Get specialist diagnosis data in patient app")
	@PostMapping(value = { "/save/getSpecialistDiagnosisData" })
	public String getSpecialistDiagnosisData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for getting specialist diagnosis data - patient APP :" + requestObj);

			String s = commonPatientAppMasterService.getSpecialistDiagnosisData(requestObj);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "error in getting diagnosis data");

		} catch (Exception e) {
			logger.error("error in getting specialist diagnosis data - patient APP :" + e);
			response.setError(5000, "error in getting specialist diagnosis data : " + e.getMessage());
		}
		return response.toString();
	}

	
	@Operation(summary= "Get last 3 episode data of the patient in patient app")
	@PostMapping(value = { "/get/getPatientsEpisodes" })
	public String getPatientsLast_3_Episode(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for getPatientsLast_3_Episode data - patient APP :" + requestObj);

			String s = commonPatientAppMasterService.getPatientsLast_3_Episode(requestObj);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "error in getPatientsLast_3_Episode data");

		} catch (Exception e) {
			logger.error("error in getPatientsLast_3_Episode data - patient APP :" + e);
			response.setError(5000, "error in getPatientsLast_3_Episode data : " + e.getMessage());
		}
		return response.toString();
	}

}
