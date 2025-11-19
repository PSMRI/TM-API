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
package com.iemr.tm.controller.videoconsultationcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.tm.service.videoconsultation.VideoConsultationService;
import com.iemr.tm.utils.response.OutputResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.iemr.tm.utils.CookieUtil;
import com.iemr.tm.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/videoConsultation", headers = "Authorization", consumes = "application/json", produces = "application/json")
public class VideoConsultationController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private VideoConsultationService videoConsultationService;

	@Autowired
	private JwtUtil jwtUtil;

	@Operation(summary = "Login to video consultation service")
	@GetMapping(value = "/login/{userID}", headers = "Authorization", produces = {
			"application/json" })
	public String login(@PathVariable("userID") Long userID, HttpServletRequest request) {

		OutputResponse response = new OutputResponse();
		try {
		String jwtToken = CookieUtil.getJwtTokenFromCookie(request);
		String userId = jwtUtil.getUserIdFromToken(jwtToken);

			if(userID.toString().equals(userId)) {
				String createdData = videoConsultationService.login(userID);

				response.setResponse(createdData.toString());
			} else {
				response.setError(403, "Unauthorized access!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@Operation(summary = "Call user for video consultation service")
	@GetMapping(value = "/call/{fromuserID}/{touserID}", headers = "Authorization", produces = { "application/json" })
	public String call(@PathVariable("fromuserID") Long fromuserID, @PathVariable("touserID") Long touserID) {

		OutputResponse response = new OutputResponse();

		try {

			String createdData = videoConsultationService.callUser(fromuserID, touserID);

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@Operation(summary = "Video consultation service for users by passing type")
	@GetMapping(value = "/call/{fromuserID}/{touserID}/{type}", headers = "Authorization", produces = {
			"application/json" })
	public String callSwymedAndJitsi(@PathVariable("fromuserID") Long fromuserID,
			@PathVariable("touserID") Long touserID, @PathVariable("type") String Type) {

		OutputResponse response = new OutputResponse();

		try {
			String createdData = null;
			if (Type.equalsIgnoreCase("VideoConsultation")) {
				createdData = videoConsultationService.callUser(fromuserID, touserID);
			} else {
				createdData = videoConsultationService.callUserjitsi(fromuserID, touserID);
			}

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@Operation(summary = "Call van through video consultation")
	@GetMapping(value = "/callvan/{fromuserID}/{vanID}", headers = "Authorization", produces = { "application/json" })
	public String callvan(@PathVariable("fromuserID") Long fromuserID, @PathVariable("vanID") Integer vanID) {

		OutputResponse response = new OutputResponse();

		try {

			String createdData = videoConsultationService.callVan(fromuserID, vanID);

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@Operation(summary = "Call van through video consultation by passing type")
	@GetMapping(value = "/callvan/{fromuserID}/{vanID}/{type}", headers = "Authorization", produces = {
			"application/json" })
	public String callVanSwymedAndJitsi(@PathVariable("fromuserID") Long fromuserID,
			@PathVariable("vanID") Integer vanID, @PathVariable("type") String Type) {

		OutputResponse response = new OutputResponse();

		try {

			String createdData = null;
			if (Type.equalsIgnoreCase("Swymed")) {
				createdData = videoConsultationService.callVan(fromuserID, vanID);
			} else {
				createdData = videoConsultationService.callVanJitsi(fromuserID, vanID);
			}

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

	@Operation(summary = "Logout of video consultation service")
	@GetMapping(value = "/logout", headers = "Authorization", produces = {
			"application/json" })
	public String logout() {

		OutputResponse response = new OutputResponse();

		try {

			String createdData = videoConsultationService.logout();

			response.setResponse(createdData.toString());

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setError(e);

		}
		/**
		 * sending the response...
		 */
		return response.toString();

	}

}
