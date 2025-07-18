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
package com.iemr.tm.controller.dataSyncActivity;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.iemr.tm.service.dataSyncActivity.DownloadDataFromServerImpl;
import com.iemr.tm.service.dataSyncActivity.UploadDataToServerImpl;
import com.iemr.tm.utils.response.OutputResponse;
import io.swagger.v3.oas.annotations.Operation;

/***
 * * @purpose Class used for data sync from van-to-server & server-to-van
 */
@RestController
@RequestMapping(value = "/dataSyncActivity", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class StartSyncActivity {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private UploadDataToServerImpl uploadDataToServerImpl;
	@Autowired
	private DownloadDataFromServerImpl downloadDataFromServerImpl;

	@Operation(summary = "Initiate data sync from van to server")
	@RequestMapping(value = { "/van-to-server" }, method = { RequestMethod.POST })
	public String dataSyncToServer(@RequestBody String requestOBJ,
			@RequestHeader(value = "Authorization") String Authorization,
			@RequestHeader(value = "ServerAuthorization") String ServerAuthorization) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject obj = new JSONObject(requestOBJ);
			if (obj != null && obj.has("groupID") && obj.get("groupID") != null && obj.has("user")
					&& obj.get("user") != null) {
				String s = uploadDataToServerImpl.getDataToSyncToServer(obj.getInt("groupID"), obj.getString("user"),
						ServerAuthorization);
				if (s != null)
					response.setResponse(s);
				else
					response.setError(5000, "Error in data sync");
			} else {
				response.setError(5000, "Invalid request, Either of groupID or user is invalid or null");
			}
		} catch (Exception e) {
			logger.error("Error in data sync : " + e);
			response.setError(e);
		}
		return response.toStringWithSerialization();
	}

	@Operation(summary = "Get data sync group details")
	@GetMapping(value = { "/getSyncGroupDetails" })
	public String getSyncGroupDetails() {
		OutputResponse response = new OutputResponse();
		try {
			String s = uploadDataToServerImpl.getDataSyncGroupDetails();
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error in getting data sync group details");
		} catch (Exception e) {
			logger.error("Error in getting data sync group details : " + e);
			response.setError(e);
		}
		return response.toString();
	}

	/**
	 * 
	 * @return Masters download in van from central server
	 */
	@Operation(summary = "Data synced master data")
	@PostMapping(value = { "/startMasterDownload" })
	public String startMasterDownload(@RequestBody String requestOBJ,
			@RequestHeader(value = "Authorization") String Authorization,
			@RequestHeader(value = "ServerAuthorization") String ServerAuthorization) {
		OutputResponse response = new OutputResponse();
		try {
			JSONObject obj = new JSONObject(requestOBJ);
			if (obj != null && obj.has("vanID") && obj.get("vanID") != null && obj.has("providerServiceMapID")
					&& obj.get("providerServiceMapID") != null) {
				String s = downloadDataFromServerImpl.downloadMasterDataFromServer(ServerAuthorization,
						obj.getInt("vanID"), obj.getInt("providerServiceMapID"));
				if (s != null) {
					if (s.equalsIgnoreCase("inProgress"))
						response.setError(5000,
								"Download is already in progress from different device, kindly wait to finish this");
					else
						response.setResponse(s);
				} else
					response.setError(5000, s);
			} else {
				response.setError(5000,
						"vanID / providerServiceMapID or both are missing," + " Kindly contact the administrator.");
			}

		} catch (Exception e) {
			logger.error("Error in Master data Download : " + e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Master data sync download progress check")
	@GetMapping(value = { "/checkMastersDownloadProgress" })
	public String checkMastersDownloadProgress() {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(new Gson().toJson(downloadDataFromServerImpl.getDownloadStatus()));
		} catch (Exception e) {
			logger.error("Error in Master data Download progress check : " + e);
			response.setError(e);
		}
		return response.toString();
	}

	@Operation(summary = "Get van details for master sync data download")
	@GetMapping(value = { "/getVanDetailsForMasterDownload" })
	public String getVanDetailsForMasterDownload() {
		OutputResponse response = new OutputResponse();
		try {
			String s = downloadDataFromServerImpl.getVanDetailsForMasterDownload();
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error while getting van details.");
		} catch (Exception e) {
			logger.error("Error while getting van details : " + e);
			response.setError(e);
		}
		return response.toString();
	}
}
