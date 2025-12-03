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
package com.iemr.tm.controller.common.main;

import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.tm.data.benFlowStatus.BeneficiaryFlowStatus;
import com.iemr.tm.service.common.transaction.CommonDoctorServiceImpl;
import com.iemr.tm.service.common.transaction.CommonNurseServiceImpl;
import com.iemr.tm.service.common.transaction.CommonServiceImpl;
import com.iemr.tm.utils.CookieUtil;
import com.iemr.tm.utils.JwtUtil;
import com.iemr.tm.utils.mapper.InputMapper;
import com.iemr.tm.utils.response.OutputResponse;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/common", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class WorklistController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	private CommonDoctorServiceImpl commonDoctorServiceImpl;
	private CommonNurseServiceImpl commonNurseServiceImpl;
	private CommonServiceImpl commonServiceImpl;
	private InputMapper inputMapper = new InputMapper();

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	public void setCommonServiceImpl(CommonServiceImpl commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	@Autowired
	public void setCommonDoctorServiceImpl(CommonDoctorServiceImpl commonDoctorServiceImpl) {
		this.commonDoctorServiceImpl = commonDoctorServiceImpl;
	}

	@Autowired
	public void setCommonNurseServiceImpl(CommonNurseServiceImpl commonNurseServiceImpl) {
		this.commonNurseServiceImpl = commonNurseServiceImpl;
	}

	// doc worklist new
	@Operation(summary = "Get doctor worklist")
	@GetMapping(value = { "/getDocWorklistNew/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('DOCTOR') ")
	public String getDocWorkListNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("serviceID") Integer serviceID, @PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			if (providerServiceMapID != null && serviceID != null) {
				String s = commonDoctorServiceImpl.getDocWorkListNew(providerServiceMapID, serviceID, vanID);
				if (s != null)
					response.setResponse(s);
			} else {
				logger.error("Invalid request, either ProviderServiceMapID or ServiceID is invalid. PSMID = "
						+ providerServiceMapID + " SID = " + serviceID);
				response.setError(5000, "Invalid request, either ProviderServiceMapID or ServiceID is invalid");
			}

		} catch (Exception e) {
			logger.error("Error in getDocWorkList:" + e);
			response.setError(5000, "Error while getting doctor worklist");
		}
		return response.toString();
	}

	// doc worklist new (TM future scheduled beneficiary)
	@Operation(summary = "Get doctor future worklist scheduled for telemedicine")
	@GetMapping(value = { "/getDocWorkListNewFutureScheduledForTM/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('TC_SPECIALIST') || hasRole('TCSPECIALIST') ")
	public String getDocWorkListNewFutureScheduledForTM(
			@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("serviceID") Integer serviceID, @PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			if (providerServiceMapID != null && serviceID != null) {
				String s = commonDoctorServiceImpl.getDocWorkListNewFutureScheduledForTM(providerServiceMapID,
						serviceID, vanID);
				if (s != null)
					response.setResponse(s);
			} else {
				logger.error("Invalid request, either ProviderServiceMapID or ServiceID is invalid. PSMID = "
						+ providerServiceMapID + " SID = " + serviceID);
				response.setError(5000, "Invalid request, either ProviderServiceMapID or ServiceID is invalid");
			}

		} catch (Exception e) {
			logger.error("Error in getDocWorkListFutureScheduledbeneficiary:" + e);
			response.setError(5000, "Error while getting doctor worklist for future scheduled beneficiay");
		}
		return response.toString();
	}

	// nurse worklist new
	@Operation(summary = "Get nurse worklist")
	@GetMapping(value = { "/getNurseWorklistNew/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('NURSE') ")
	public String getNurseWorkListNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getNurseWorkListNew(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting nurse worklist");
		} catch (Exception e) {
			logger.error("Error in getNurseWorklist:" + e);
			response.setError(5000, "Error while getting nurse worklist");
		}
		return response.toString();
	}

	// nurse worklist TC schedule (current-date) new
	@Operation(summary = "Get worklist for teleconsultation for the current date")
	@GetMapping(value = { "/getNurseWorkListTcCurrentDate/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('NURSE') || hasRole('TC_SPECIALIST') || hasRole('TCSPECIALIST') ")
	public String getNurseWorkListTcCurrentDateNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getNurseWorkListTcCurrentDate(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting nurse worklist");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in getNurseWorklist:" + e);
			response.setError(5000, "Error while getting nurse worklist");
		}
		return response.toString();
	}

	// nurse worklist TC schedule (future-date) new
	@Operation(summary = "Get worklist for teleconsultation for the future date")
	@GetMapping(value = { "/getNurseWorkListTcFutureDate/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('NURSE') || hasRole('TC_SPECIALIST') || hasRole('TCSPECIALIST') ")
	public String getNurseWorkListTcFutureDateNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getNurseWorkListTcFutureDate(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting nurse worklist");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in getNurseWorklist:" + e);
			response.setError(5000, "Error while getting nurse worklist");
		}
		return response.toString();
	}

	@Operation(summary = "Get previous significant findings")
	@PostMapping(value = { "/getDoctorPreviousSignificantFindings" })
	@PreAuthorize("hasRole('DOCTOR') ")
	public String getDoctorPreviousSignificantFindings(
			@Param(value = "{\"beneficiaryRegID\": \"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj != null && obj.has("beneficiaryRegID") && obj.get("beneficiaryRegID") != null) {
				Long beneficiaryRegID = obj.getLong("beneficiaryRegID");
				String s = commonDoctorServiceImpl.fetchBenPreviousSignificantFindings(beneficiaryRegID);
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5000, "Error while getting previous significant findings");
			} else {
				response.setError(5000, "Invalid data!");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error while fetching previous significant findings" + e);
			response.setError(5000, "Error while getting previous significant findings");
		}
		return response.toString();
	}

	// Get Lab technician worklist new
	@Operation(summary = "Get lab technician worklist")
	@GetMapping(value = { "/getLabWorklistNew/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('LAB_TECHNICIAN') || hasRole('LABTECHNICIAN') ")
	public String getLabWorkListNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getLabWorkListNew(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting lab technician worklist");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in getLabWorklist:" + e);
			response.setError(5000, "Error while getting lab technician worklist");
		}
		return response.toString();
	}

	// Get radiologist worklist new
	@Operation(summary = "Get radiologist worklist")
	@GetMapping(value = { "/getRadiologist-worklist-New/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('RADIOLOGIST') ")
	public String getRadiologistWorklistNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getRadiologistWorkListNew(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting radiologist worklist");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in getLabWorklist:" + e);
			response.setError(5000, "Error while getting radiologist worklist");
		}
		return response.toString();
	}

	// Get oncologist worklist new
	@Operation(summary = "Get oncologist worklist")
	@GetMapping(value = { "/getOncologist-worklist-New/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('ONCOLOGIST') ")
	public String getOncologistWorklistNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getOncologistWorkListNew(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting oncologist worklist");
		} catch (Exception e) {
			logger.error("Error in getLabWorklist:" + e);
			response.setError(5000, "Error while getting oncologist worklist");
		}
		return response.toString();
	}

	// Get pharma worklist new
	@Operation(summary = "Get pharmacist worklist")
	@GetMapping(value = { "/getPharma-worklist-New/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('PHARMACIST') ")
	public String getPharmaWorklistNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getPharmaWorkListNew(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting pharma worklist");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in getLabWorklist:" + e);
			response.setError(5000, "Error while getting pharma worklist");
		}
		return response.toString();
	}

	@Operation(summary = "Print case sheet of beneficiary")
	@PostMapping(value = { "/get/Case-sheet/printData" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
		public String getCasesheetPrintData(@RequestBody String comingReq,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			if (comingReq != null) {
				BeneficiaryFlowStatus obj = InputMapper.gson().fromJson(comingReq, BeneficiaryFlowStatus.class);
				String casesheetData = commonServiceImpl.getCaseSheetPrintDataForBeneficiary(obj, Authorization);
				response.setResponse(casesheetData);
			} else
				response.setError(5000, "Invalid request");
		} catch (Exception e) {
			logger.error("" + e);
		}

		return response.toString();
	}

	// Start of Fetch Previous Medical History...
	@Operation(summary = "Get beneficiary history")
	@PostMapping(value = { "/getBenPastHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenPastHistory(@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenPastHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenPastHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting illness and surgery history");
			logger.error("Error in getBenPastHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary tobacco consumption history")
	@PostMapping(value = { "/getBenTobaccoHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenTobaccoHistory(@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getPersonalTobaccoHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenTobaccoHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting tobacco history");
			logger.error("Error in getBenTobaccoHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary alcohol consumption history")
	@PostMapping(value = { "/getBenAlcoholHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenAlcoholHistory(@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getPersonalAlcoholHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenAlcoholHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting alcohol history");
			logger.error("Error in getBenAlcoholHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary allergy history")
	@PostMapping(value = { "/getBenAllergyHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenANCAllergyHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getPersonalAllergyHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenAllergyHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting allergy history");
			logger.error("Error in getBenAllergyHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary medication history")
	@PostMapping(value = { "/getBenMedicationHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenMedicationHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getMedicationHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenMedicationHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting medication history");
			logger.error("Error in getBenMedicationHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary family history")
	@PostMapping(value = { "/getBenFamilyHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenFamilyHistory(@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getFamilyHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenFamilyHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting family history");
			logger.error("Error in getBenFamilyHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary menstrual history")
	@PostMapping(value = { "/getBenMenstrualHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenMenstrualHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getMenstrualHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenMenstrualHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting menstrual history");
			logger.error("Error in getBenMenstrualHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary obstetric history")
	@PostMapping(value = { "/getBenPastObstetricHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenPastObstetricHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getObstetricHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenPastObstetricHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting obstetric history");
			logger.error("Error in getBenPastObstetricHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary comorbidity condition details")
	@PostMapping(value = { "/getBenComorbidityConditionHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenANCComorbidityConditionHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getComorbidHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenComorbidityConditionHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting comodbidity history");
			logger.error("Error in getBenComorbidityConditionHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary optional vaccine details")
	@PostMapping(value = { "/getBenOptionalVaccineHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenOptionalVaccineHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getChildVaccineHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenOptionalVaccineHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting optional vaccination history");
			logger.error("Error in getBenOptionalVaccineHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get child beneficiary vaccine details")
	@PostMapping(value = { "/getBenChildVaccineHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenImmunizationHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getImmunizationHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenImmunizationHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting child vaccine(immunization) history");
			logger.error("Error in getBenImmunizationHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary perinatal history")
	@PostMapping(value = { "/getBenPerinatalHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenPerinatalHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenPerinatalHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenPerinatalHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting perinatal history");
			logger.error("Error in getBenPerinatalHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get child beneficiary feeding history")
	@PostMapping(value = { "/getBenFeedingHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenFeedingHistory(@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenFeedingHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenFeedingHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting child feeding history");
			logger.error("Error in getBenFeedingHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get child beneficiary development history")
	@PostMapping(value = { "/getBenDevelopmentHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBenDevelopmentHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenDevelopmentHistoryData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenDevelopmentHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting child development history");
			logger.error("Error in getBenDevelopmentHistory:" + e);
		}
		return response.toString();
	}

	// End of Fetch Previous History...

	/***
	 * fetch ben previous visit details for history case-record(Platform).
	 * 08-08-2018
	 */
	@Operation(summary = "Get beneficiary casesheet history")
	@PostMapping(value = { "/getBeneficiaryCaseSheetHistory" })
	@PreAuthorize("hasRole('DOCTOR') || hasRole('NURSE') ")
	public String getBeneficiaryCaseSheetHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();
		try {
			String responseData = commonServiceImpl.getBenPreviousVisitDataForCaseRecord(comingRequest);
			if (responseData != null)
				response.setResponse(responseData);
			else
				response.setError(5000, "Error while fetching beneficiary previous visit history details");
		} catch (Exception e) {
			response.setError(5000, "Error while fetching beneficiary previous visit history details");
			logger.error("Error while fetching beneficiary previous visit history :" + e);
		}
		return response.toString();
	}

	// TC specialist worklist new
	@Operation(summary = "Get teleconsultation specialist worklist")
	@GetMapping(value = { "/getTCSpecialistWorklist/{providerServiceMapID}/{serviceID}" })
	public String getTCSpecialistWorkListNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			 @PathVariable("serviceID") Integer serviceID, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
		String jwtToken = CookieUtil.getJwtTokenFromCookie(request);
		String userId = jwtUtil.getUserIdFromToken(jwtToken);
			Integer userID=Integer.parseInt(userId);
			if (providerServiceMapID != null && userId != null ) {
				String s = commonDoctorServiceImpl.getTCSpecialistWorkListNewForTM(providerServiceMapID, userID,
						serviceID);
				if (s != null)
					response.setResponse(s);
			} else if(userId  == null || jwtToken == null) {
				response.setError(403, "Unauthorized access!");
			} else {
				logger.error("Invalid request");
				response.setError(5000, "Invalid request");
			}

		} catch (Exception e) {
			logger.error("Error in getTC_SpecialistWorkList:" + e);
			response.setError(5000, "Error while getting TC specialist worklist");
		}
		return response.toString();
	}

	// TC specialist worklist new, patient App, 14-08-2020
	@Operation(summary = "Get teleconsultation specialist worklist for patient app")
	@GetMapping(value = {
			"/getTCSpecialistWorklistPatientApp/{providerServiceMapID}/{serviceID}/{vanID}" })
	public String getTCSpecialistWorkListNewPatientApp(
			@PathVariable("providerServiceMapID") Integer providerServiceMapID, 
			@PathVariable("serviceID") Integer serviceID, @PathVariable("vanID") Integer vanID, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
		String jwtToken = CookieUtil.getJwtTokenFromCookie(request);
		String userId = jwtUtil.getUserIdFromToken(jwtToken);
		Integer userID=Integer.parseInt(userId);
			if (providerServiceMapID != null && userID != null) {
				String s = commonDoctorServiceImpl.getTCSpecialistWorkListNewForTMPatientApp(providerServiceMapID,
						userID, serviceID, vanID);
				if (s != null)
					response.setResponse(s);
			} else if(userId  == null || jwtToken == null) {
				response.setError(403, "Unauthorized access!");
			} else {
				logger.error("Invalid request");
				response.setError(5000, "Invalid request");
			}

		} catch (Exception e) {
			logger.error("Error in getTC_SpecialistWorkList:" + e);
			response.setError(5000, "Error while getting TC specialist worklist");
		}
		return response.toString();
	}

	// TC specialist worklist new future scheduled
	@Operation(summary = "Get teleconsultation specialist future scheduled")
	@GetMapping(value = {
			"/getTCSpecialistWorklistFutureScheduled/{providerServiceMapID}/{serviceID}" })
	public String getTCSpecialistWorklistFutureScheduled(
			@PathVariable("providerServiceMapID") Integer providerServiceMapID, 
			@PathVariable("serviceID") Integer serviceID, HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {

		String jwtToken = CookieUtil.getJwtTokenFromCookie(request);
		String userId = jwtUtil.getUserIdFromToken(jwtToken);
		Integer userID=Integer.parseInt(userId);
			if (providerServiceMapID != null && userID != null ) {
				String s = commonDoctorServiceImpl.getTCSpecialistWorkListNewFutureScheduledForTM(providerServiceMapID,
						userID, serviceID);
				if (s != null)
					response.setResponse(s);
			} else if(userId  == null || jwtToken == null) {
				response.setError(403, "Unauthorized access!");
			} else {
				logger.error("Invalid request");
				response.setError(5000, "Invalid request");
			}

		} catch (Exception e) {
			logger.error("Error in getTC_SpecialistWorkList future scheduled:" + e);
			response.setError(5000, "Error while getting TC specialist future scheduled worklist");
		}
		return response.toString();
	}

	// openkm file download
	@Operation(summary = "Add file as string to openKM")
	@PostMapping(value = "/getKMFile", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON, headers = "Authorization")
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getKMFile(@Param(value = "{}") @RequestBody String request,
			@RequestHeader(value = "Authorization") String Authorization) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonServiceImpl.getOpenKMDocURL(request, Authorization);
			if (s != null)
				response.setResponse(s);
			else {
				response.setError(5000, "file not available, please contact administrator");
			}
		} catch (Exception e) {
			logger.error("Error while getting file download url : " + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary physical history")
	@PostMapping(value = { "/getBenPhysicalHistory" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenPhysicalHistory(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenPhysicalHistory(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("getBenPhysicalHistory response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting Physical history");
			logger.error("Error in getBenPhysicalHistory:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary symptomatic questionnaire answer details")
	@PostMapping(value = { "/getBenSymptomaticQuestionnaireDetails" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenSymptomaticQuestionnaireDetails(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenSymptomaticQuestionnaireDetailsData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("Get Beneficiary Symptomatic questionnaire answer details response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting details");
			logger.error("Error in Get Beneficiary Symptomatic questionnaire answer details:" + e);
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary previous diabetes history")
	@PostMapping(value = { "/getBenPreviousDiabetesHistoryDetails" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenPreviousDiabetesHistoryDetails(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenPreviousDiabetesData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("Get Beneficiary previous Diabetes history response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting details");
			logger.error("Error in Get Beneficiary previous Diabetes history:" + e);
		}
		return response.toString();
	}

	// nurse worklist coming from MMU application
	@Operation(summary = "Get mmu nurse worklist")
	@GetMapping(value = { "/getMmuNurseWorklistNew/{providerServiceMapID}/{serviceID}/{vanID}" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getMmuNurseWorklistNew(@PathVariable("providerServiceMapID") Integer providerServiceMapID,
			@PathVariable("vanID") Integer vanID) {
		OutputResponse response = new OutputResponse();
		try {
			String s = commonNurseServiceImpl.getMmuNurseWorkListNew(providerServiceMapID, vanID);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting MMU Nurse Worklist");
		} catch (Exception e) {
			logger.error("Error in getNurseWorklist:" + e);
			response.setError(5000, "Error while getting MMU Nurse Worklist");
		}
		return response.toString();
	}

	@Operation(summary = "Get beneficiary previous referral history")
	@PostMapping(value = { "/getBenPreviousReferralHistoryDetails" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getBenPreviousReferralHistoryDetails(
			@Param(value = "{\"benRegID\":\"Long\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("benRegID")) {
				Long benRegID = obj.getLong("benRegID");
				String s = commonServiceImpl.getBenPreviousReferralData(benRegID);
				response.setResponse(s);

			} else {
				logger.info("Invalid Request Data.");
				response.setError(5000, "Invalid request");
			}
			logger.info("Get Beneficiary previous Referral history response:" + response);
		} catch (Exception e) {
			response.setError(5000, "Error while getting details");
			logger.error("Error in Get Beneficiary previous Referral history:" + e);
		}
		return response.toString();
	}

	/**
	 * @param comingRequest
	 * @return ProviderSpecificMasterData
	 */
	@Operation(summary = "Get provider specific data")
	@PostMapping(value = { "/getProviderSpecificData" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String getProviderSpecificData(
			@Param(value = "{\"benvisitID\":\"Long\",\"benvisitCode\":\"Long\",\"fetchMMUDataFor\":\"String\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			String s = commonServiceImpl.getProviderSpecificData(comingRequest);
			response.setResponse(s);
			logger.info("getProviderSpecificData response:" + response);
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
			logger.error("Error in getProviderSpecificData:" + e);
		}
		return response.toString();
	}

	/**
	 * @param comingRequest
	 * @return ProviderSpecificMasterData
	 */
	@Operation(summary = "Calculate beneficiary BMI status")
	@PostMapping(value = { "/calculateBMIStatus" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String calculateBMIStatus(
			@Param(value = "{\"bmi\":\"double\",\"yearMonth\":\"String\",\"gender\":\"String\"}") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();

		try {
			String s = commonNurseServiceImpl.calculateBMIStatus(comingRequest);
			response.setResponse(s);
			logger.info("calculateBMIStatus response:" + response);
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
			logger.error("Error in calculateBMIStatus:" + e.getMessage());
		}
		return response.toString();
	}

	@Operation(summary = "Update beneficiary status flag")
	@PostMapping(value = { "/update/benDetailsAndSubmitToNurse" })
	@PreAuthorize("hasRole('NURSE') || hasRole('DOCTOR') ")
	public String saveBeneficiaryVisitDetail(
			@Param(value = "{\"beneficiaryRegID\": \"Long\"}") @RequestBody String comingRequest) {

		OutputResponse response = new OutputResponse();
		inputMapper = new InputMapper();
		try {
			JSONObject obj = new JSONObject(comingRequest);
			if (obj.has("beneficiaryRegID")) {
				if (obj.getLong("beneficiaryRegID") > 0) {

					Integer i = commonNurseServiceImpl.updateBeneficiaryStatus('R', obj.getLong("beneficiaryRegID"));
					if (i != null && i > 0) {
						response.setResponse("Beneficiary Successfully Submitted to Nurse Work-List.");
					} else {
						response.setError(500, "Something went Wrong please try after Some Time !!!");
					}

				} else {
					response.setError(500, "Beneficiary Registration ID is Not valid !!!");
				}
			} else {
				response.setError(500, "Beneficiary Registration ID is Not valid !!!");
			}
			logger.info("benDetailsAndSubmitToNurse response:" + response);
		} catch (Exception e) {
			response.setError(e);
			logger.error("Error in benDetailsAndSubmitToNurse:" + e);
		}

		return response.toString();
	}

	@Operation(summary = "Extend redis session for 30 mins")
	@PostMapping(value = { "/extend/redisSession" })
	@PreAuthorize("hasRole('NURSE') || hasRole('PHARMACIST') || hasRole('LABTECHNICIAN') || hasRole('REGISTRAR') || hasRole('DATASYNC') || hasRole('DATA_SYNC') || hasRole('DOCTOR') || hasRole('LAB_TECHNICIAN') || hasRole('TC_SPECIALIST') || hasRole('ONCOLOGIST') || hasRole('RADIOLOGIST')")
	public String extendRedisSession() {
		OutputResponse response = new OutputResponse();
		try {
			// temp code later have to validate the user also.
			response.setResponse("Session extended for 30 mins");
		} catch (Exception e) {
			logger.error("Error while extending running session");
		}
		return response.toString();
	}

	@Operation(summary = "Soft delete prescribed medicine")
	@PostMapping(value = { "/doctor/delete/prescribedMedicine" })
	@PreAuthorize("hasRole('DOCTOR') ")
	public String deletePrescribedMedicine(@RequestBody String requestOBJ) {
		OutputResponse response = new OutputResponse();
		try {
			if (requestOBJ != null) {
				JSONObject obj = new JSONObject(requestOBJ);
				String s = commonDoctorServiceImpl.deletePrescribedMedicine(obj);
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5000, "error while deleting record");
			} else {

			}
		} catch (Exception e) {
			logger.error("Error while deleting prescribed medicine");
			response.setError(e);
		}
		return response.toString();
	}

}
