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
package com.iemr.tm.controller.generalOPD;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.tm.service.generalOPD.GeneralOPDService;
import com.iemr.tm.service.generalOPD.GeneralOPDServiceImpl;
import com.iemr.tm.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;

/***
 * @Objective Saving General OPD data for Nurse and Doctor.
 */

@RestController
@RequestMapping(value = "/generalOPD", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class GeneralOPDController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private GeneralOPDService generalOPDService;

	// @Autowired
	// public void setGeneralOPDServiceImpl(GeneralOPDServiceImpl
	// generalOPDServiceImpl) {
	// this.generalOPDService = generalOPDService;
	// }

	/**
	 * @Objective Save General OPD data for nurse.
	 * @param requestObj
	 * @return success or failure response
	 * @throws Exception
	 */
	@Operation(summary = "Save general OPD data collected by nurse")
	@PostMapping(value = { "/save/nurseData" })
	@PreAuthorize("hasRole('NURSE')")
	public String saveBenGenOPDNurseData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) throws Exception {
		OutputResponse response = new OutputResponse();

		if (null != requestObj) {
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();

			try {
				logger.info("Request object for GeneralOPD nurse data saving :" + requestObj);

				if (jsnOBJ != null) {
					String genOPDRes = generalOPDService.saveNurseData(jsnOBJ, Authorization);
					response.setResponse(genOPDRes);

				} else {
					response.setResponse("Invalid request");
				}
			} catch (Exception e) {
				logger.error("Error in nurse data saving :" + e.getMessage());
				generalOPDService.deleteVisitDetails(jsnOBJ);
				response.setError(5000, e.getMessage());
			}
		}
		return response.toString();
	}

	/**
	 * @Objective Save General OPD data for doctor.
	 * @param requestObj
	 * @return success or failure response
	 */
	@Operation(summary = "Save general OPD data collected by doctor")
	@PostMapping(value = { "/save/doctorData" })
	@PreAuthorize("hasRole('DOCTOR')")
	public String saveBenGenOPDDoctorData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for GeneralOPD doctor data saving :" + requestObj);

			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();

			if (jsnOBJ != null) {
				Long genOPDRes = generalOPDService.saveDoctorData(jsnOBJ, Authorization);
				if (null != genOPDRes && genOPDRes > 0) {
					response.setResponse("Data saved successfully");
				} else {
					response.setResponse("Unable to save data");
				}

			} else {
				response.setResponse("Invalid request");
			}
		} catch (Exception e) {
			logger.error("Error in doctor data saving :" + e.getMessage());
			response.setError(5000, e.getMessage());
		}
		return response.toString();
	}

	@Operation(summary = "Get general OPD beneficiary visit details")
	@PostMapping(value = { "/getBenVisitDetailsFrmNurseGOPD" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	@Transactional(rollbackFor = Exception.class)
	public String getBenVisitDetailsFrmNurseGOPD(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.length() > 1) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = generalOPDService.getBenVisitDetailsFrmNurseGOPD(benRegID, visitCode);
				response.setResponse(res);
			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenDataFrmNurseScrnToDocScrnVisitDetails response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary visit data");
			logger.error("Error in getBenDataFrmNurseScrnToDocScrnVisitDetails:" + e);
		}
		return response.toString();
	}

	/**
	 * @Objective Fetching beneficiary history details enterted by nurse.
	 * @param comingRequest
	 * @return history details in JSON format
	 */
	@Operation(summary = "Get general OPD beneficiary history")
	@PostMapping(value = { "/getBenHistoryDetails" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenHistoryDetails(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String s = generalOPDService.getBenHistoryDetails(benRegID, visitCode);
				response.setResponse(s);
			} else {
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenHistoryDetails response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary history data");
			logger.error("Error in getBenHistoryDetails:" + e);
		}
		return response.toString();
	}

	/**
	 * @Objective Fetching beneficiary vital details enterted by nurse.
	 * @param comingRequest
	 * @return vital details in JSON format
	 */
	@Operation(summary = "Get general OPD beneficiary vitals")
	@PostMapping(value = { "/getBenVitalDetailsFrmNurse" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenVitalDetailsFrmNurse(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = generalOPDService.getBeneficiaryVitalDetails(benRegID, visitCode);
				response.setResponse(res);
			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenVitalDetailsFrmNurse response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary vital data");
			logger.error("Error in getBenVitalDetailsFrmNurse:" + e);
		}
		return response.toString();
	}

	/**
	 * @Objective Fetching beneficiary examination details enterted by nurse.
	 * @param comingRequest
	 * @return examination details in JSON format
	 */
	@Operation(summary = "Get general OPD beneficiary examination details")
	@PostMapping(value = { "/getBenExaminationDetails" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenExaminationDetails(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String s = generalOPDService.getExaminationDetailsData(benRegID, visitCode);
				response.setResponse(s);
			} else {
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenExaminationDetails response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary examination data");
			logger.error("Error in getBenExaminationDetails:" + e);
		}
		return response.toString();
	}

	/**
	 * @Objective Fetching beneficiary doctor details.
	 * @param comingRequest
	 * @return doctor details in JSON format
	 */
	@Operation(summary = "Get general OPD beneficiary case record and referral")
	@PostMapping(value = { "/getBenCaseRecordFromDoctorGeneralOPD" })
	@Transactional(rollbackFor = Exception.class)
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenCaseRecordFromDoctorGeneralOPD(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (null != obj && obj.length() > 1 && obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = generalOPDService.getBenCaseRecordFromDoctorGeneralOPD(benRegID, visitCode);
				response.setResponse(res);
			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenCaseRecordFromDoctorGeneralOPD response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary doctor data");
			logger.error("Error in getBenCaseRecordFromDoctorGeneralOPD:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Update beneficiary's general OPD visit details")
	@PostMapping(value = { "/update/visitDetailsScreen" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String updateVisitNurse(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for visit data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			int result = generalOPDService.UpdateVisitDetails(jsnOBJ);
			if (result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("Visit data update response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating visit data :" + e);
		}

		return response.toString();
	}

	/**
	 * @param requestObj
	 * @return success or failure response
	 * @objective Replace General OPD History Data entered by Nurse with the details
	 *            entered by Doctor
	 */
	@Operation(summary = "Update beneficiary history")
	@PostMapping(value = { "/update/historyScreen" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String updateHistoryNurse(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for history data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			int result = generalOPDService.updateBenHistoryDetails(jsnOBJ);
			if (result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("History data update response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating history data :" + e);
		}

		return response.toString();
	}

	/**
	 * @param requestObj
	 * @return success or failure response
	 * @objective Replace General OPD Vital Data entered by Nurse with the details
	 *            entered by Doctor
	 */
	@Operation(summary = "Update general OPD beneficiary vitals")
	@PostMapping(value = { "/update/vitalScreen" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String updateVitalNurse(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for vital data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			int result = generalOPDService.updateBenVitalDetails(jsnOBJ);
			if (result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("Vital data update response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating vital data :" + e);
		}

		return response.toString();
	}

	/**
	 * @param requestObj
	 * @return success or failure response
	 * @objective Replace General OPD Examination Data entered by Nurse with the
	 *            details entered by Doctor
	 */
	@Operation(summary = "Update general OPD beneficiary examination data")
	@PostMapping(value = { "/update/examinationScreen" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String updateGeneralOPDExaminationNurse(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for examination data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			int result = generalOPDService.updateBenExaminationDetails(jsnOBJ);
			if (result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("Examination data update response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating examination data :" + e);
		}

		return response.toString();
	}

	/**
	 * @param requestObj
	 * @return success or failure response
	 * @objective Replace General OPD doctor data for the doctor next visit
	 */
	@Operation(summary = "Update general OPD beneficiary case record and referral")
	@PostMapping(value = { "/update/doctorData" })
	@PreAuthorize("hasRole('DOCTOR') ")
	public String updateGeneralOPDDoctorData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for doctor data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			Long result = generalOPDService.updateGeneralOPDDoctorData(jsnOBJ, Authorization);
			if (null != result && result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("Doctor data update response:" + response);
		} catch (Exception e) {
			logger.error("Unable to modify data. " + e.getMessage());
			response.setError(5000, e.getMessage());
		}

		return response.toString();
	}

}
