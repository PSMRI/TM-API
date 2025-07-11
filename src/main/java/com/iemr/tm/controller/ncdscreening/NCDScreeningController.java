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
package com.iemr.tm.controller.ncdscreening;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.tm.service.ncdscreening.NCDSCreeningDoctorService;
import com.iemr.tm.service.ncdscreening.NCDScreeningService;
import com.iemr.tm.service.ncdscreening.NCDScreeningServiceImpl;
import com.iemr.tm.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Objective Saving NCD Screening nurse data.
 */
@RestController
@RequestMapping(value = "/NCD", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class NCDScreeningController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	private NCDScreeningServiceImpl ncdScreeningServiceImpl;
	@Autowired
	private NCDScreeningService ncdScreeningService;
	@Autowired
	private NCDSCreeningDoctorService ncdSCreeningDoctorService;

	@Autowired
	public void setNcdScreeningServiceImpl(NCDScreeningServiceImpl ncdScreeningServiceImpl) {
		this.ncdScreeningServiceImpl = ncdScreeningServiceImpl;
	}

	/**
	 * @Objective Save NCD Screening data for nurse.
	 * @param JSON requestObj
	 * @return success or failure response
	 * @throws Exception
	 */
	@Operation(summary = "Save NCD screening beneficiary data collected by nurse")

	@PostMapping(value = { "/save/nurseData" })
	public String saveBeneficiaryNCDScreeningDetails(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) throws Exception {

		logger.info("Request object for NCD Screening nurse data saving :" + requestObj);
		OutputResponse response = new OutputResponse();

		if (null != requestObj) {
			JsonObject jsonObject = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsonObject = jsnElmnt.getAsJsonObject();

			try {

				if (jsonObject != null) {
					String r = ncdScreeningServiceImpl.saveNCDScreeningNurseData(jsonObject, Authorization);
					response.setResponse(r);
				} else {
					response.setError(5000, "Invalid request");
				}
			} catch (Exception e) {
				logger.error("Error in nurse data saving :" + e.getMessage());
				ncdScreeningServiceImpl.deleteVisitDetails(jsonObject);
				response.setError(5000, e.getMessage());
			}
		}
		return response.toString();
	}

	@Operation(summary = "Save NCD screening beneficiary data collected by doctor")
	@PostMapping(value = { "/save/doctorData" })
	public String saveBenNCDScreeningDoctorData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			logger.info("Request object for NCD Screening doctor data saving :" + requestObj);

			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();

			if (jsnOBJ != null) {
				Long ncdCareRes = ncdScreeningServiceImpl.saveDoctorData(jsnOBJ, Authorization);
				if (null != ncdCareRes && ncdCareRes > 0) {
					response.setResponse("Data saved successfully");
				} else {
					response.setResponse("Unable to save data");
				}

			} else {
				response.setResponse("Invalid request");
			}
		} catch (Exception e) {
			logger.error("Error while saving NCD Screening doctor data :" + e.getMessage());
			response.setError(5000, e.getMessage());
		}
		return response.toString();
	}

	@Operation(summary = "Get NCD screening beneficiary visit details")
	@PostMapping(value = { "/get/nurseData" })
	public String getNCDScreenigDetails(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {

		OutputResponse response = new OutputResponse();
		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.length() > 1) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String screeningDetails = ncdScreeningServiceImpl.getNCDScreeningDetails(benRegID, visitCode);
				response.setResponse(screeningDetails);
			} else {
				response.setError(5000, "Invalid request");
			}
			logger.info("NCD Screening nurse data fetch response :" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting NCD Screening data");
			logger.error("Error while getting NCD Screening data :" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get NCD screening visit count for beneficiary register id")
	@GetMapping(value = { "/getNcdScreeningVisitCount/{beneficiaryRegID}" })
	public String getNcdScreeningVisitCount(@PathVariable("beneficiaryRegID") Long beneficiaryRegID) {
		OutputResponse response = new OutputResponse();
		try {
			if (beneficiaryRegID != null) {
				String s = ncdScreeningServiceImpl.getNcdScreeningVisitCnt(beneficiaryRegID);
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5000, "Error while getting NCD screening Visit Count");

			} else {
				response.setError(5000, "Invalid request");
			}
		} catch (Exception e) {
			response.setError(5000, "Error while getting NCD screening Visit Count");
			logger.error("Error while getting NCD screening Visit Count" + e);
		}
		return response.toString();
	}

	/**
	 * @Objective Fetching beneficiary doctor details.
	 * @param comingRequest
	 * @return visit details in JSON format
	 */
	@Operation(summary = "Get NCD screening beneficiary case record and referral")
	@PostMapping(value = { "/getBenCaseRecordFromDoctorNCDScreening" })
	@Transactional(rollbackFor = Exception.class)
	public String getBenCaseRecordFromDoctorNCDCare(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (null != obj && obj.length() > 1 && obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = ncdScreeningServiceImpl.getBenCaseRecordFromDoctorNCDScreening(benRegID, visitCode);
				response.setResponse(res);
			} else {
				logger.info("Invalid request");
				response.setError(5000, "Invalid request");
			}
			logger.info("NCD Screening doctor data fetching Response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary doctor data");
			logger.error("Error while getting beneficiary doctor data :" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get NCD screening beneficiary visit details")
	@PostMapping(value = { "/getBenVisitDetailsFrmNurseNCDScreening" })
	@Transactional(rollbackFor = Exception.class)
	public String getBenVisitDetailsFrmNurseGOPD(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.length() > 1) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = ncdScreeningServiceImpl.getBenVisitDetailsFrmNurseNCDScreening(benRegID, visitCode);
				response.setResponse(res);
			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenVisitDetailsFrmNurseNCDScreening response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary visit data");
			logger.error("Error in getBenVisitDetailsFrmNurseNCDScreening:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get NCD screening beneficiary general OPD history")
	@PostMapping(value = { "/getBenHistoryDetails" })

	public String getBenHistoryDetails(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String s = ncdScreeningServiceImpl.getBenHistoryDetails(benRegID, visitCode);
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

	@Operation(summary = "Get NCD screening beneficiary vitals from general OPD nurse")
	@PostMapping(value = { "/getBenVitalDetailsFrmNurse" })
	public String getBenVitalDetailsFrmNurse(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = ncdScreeningServiceImpl.getBeneficiaryVitalDetails(benRegID, visitCode);
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

	@Operation(summary = "Get NCD screening IDRS details from general OPD nurse")
	@PostMapping(value = { "/getBenIdrsDetailsFrmNurse" })
	public String getBenIdrsDetailsFrmNurse(
			@Param(value = "{\"benRegID\":\"Long\",\"visitCode\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID") && obj.has("visitCode")) {
				Long benRegID = obj.getLong("benRegID");
				Long visitCode = obj.getLong("visitCode");

				String res = ncdScreeningServiceImpl.getBenIdrsDetailsFrmNurse(benRegID, visitCode);
				response.setResponse(res);
			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenIdrsDetailsFrmNurse response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting beneficiary Idrs data");
			logger.error("Error in getBenIdrsDetailsFrmNurse:" + e);

		}
		return response.toString();
	}

	@Operation(summary = "Get NCD screening beneficiary case record and referral")
	@PostMapping(value = { "/update/nurseData" })
	public String updateBeneficiaryNCDScreeningDetails(@RequestBody String requestObj) {

		logger.info("Update NCDScreening Details request:" + requestObj);
		OutputResponse response = new OutputResponse();
		JsonObject jsonObject = new JsonObject();
		JsonParser jsonParser = new JsonParser();

		try {
			JsonElement jsonElement = jsonParser.parse(requestObj);
			jsonObject = jsonElement.getAsJsonObject();

			if (jsonObject != null) {
				Integer r = ncdScreeningServiceImpl.updateNurseNCDScreeningDetails(jsonObject);
				if (r != null && r == 1) {
					response.setResponse("Data updated successfully");
				} else {
					response.setError(5000, "Unable to modify data");
				}
			} else {
				response.setError(5000, "Invalid request");
			}
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error in updating beneficiary NCD screening data: " + e);
		}
		logger.info("Update NCDScreening Details response:" + response);
		return response.toString();
	}

	/*
	 * Updating the history WDF requirement 9-12-2020
	 */
	@Operation(summary = "Update NCD screening beneficiary history")
	@PostMapping(value = { "/update/historyScreen" })
	public String updateHistoryNurse(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for history data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			int result = ncdScreeningService.UpdateNCDScreeningHistory(jsnOBJ);
			if (result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("History data update Response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating history data :" + e);
		}

		return response.toString();
	}

	@Operation(summary = "Update NCD screening beneficiary vitals")
	@PostMapping(value = { "/update/vitalScreen" })
	public String updateVitalNurse(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for vital data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			int result = ncdScreeningServiceImpl.updateBenVitalDetails(jsnOBJ);
			if (result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("Vital data update Response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating vital data :" + e);
		}

		return response.toString();
	}

	@Operation(summary = "Update NCD screening beneficiary history")
	@PostMapping(value = { "/update/idrsScreen" })
	public String updateIDRSScreen(@RequestBody String requestObj) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for history data updating :" + requestObj);

		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(requestObj);
		jsnOBJ = jsnElmnt.getAsJsonObject();

		try {
			Long result = ncdScreeningService.UpdateIDRSScreen(jsnOBJ);
			if (result != null && result > 0) {
				response.setResponse("Data updated successfully");
			} else {
				response.setError(500, "Unable to modify data");
			}
			logger.info("IDRS data update Response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Unable to modify data");
			logger.error("Error while updating history data :" + e);
		}

		return response.toString();
	}

	@Operation(summary = "Update NCD screening beneficiary case record and referral")
	@PostMapping(value = { "/update/doctorData" })
	public String updateDoctorData(@RequestBody String requestObj,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		logger.info("Request object for doctor data update :" + requestObj);

		try {
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(requestObj);
			jsnOBJ = jsnElmnt.getAsJsonObject();

			int i = ncdSCreeningDoctorService.updateDoctorData(jsnOBJ, Authorization);
			if (i > 0)
				response.setResponse("Data updated successfully");
			else
				response.setError(5000, "Error in data update");
		} catch (Exception e) {
			logger.error("Unable to modify data" + e.getMessage());
			response.setError(5000, e.getMessage());
		}

		return response.toString();
	}

}
