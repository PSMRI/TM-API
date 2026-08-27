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
package com.iemr.tm.controller.nurse.vitals;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iemr.tm.service.nurse.vitals.AnthropometryVitalsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnthropometryVitalsController Test Suite")
class AnthropometryVitalsControllerTest {

	@Mock
	private AnthropometryVitalsService anthropometryVitalsService;

	@InjectMocks
	private AnthropometryVitalsController controller;

	@Test
	@DisplayName("getBenHeightDetailsFrmNurse should return the height details for a valid request")
	void getBenHeightDetailsFrmNurse_shouldReturnHeightDetails() throws Exception {
		when(anthropometryVitalsService.getBeneficiaryHeightDetails(11L)).thenReturn("{\"height\":170}");

		String result = controller.getBenHeightDetailsFrmNurse("{\"benRegID\":11}");

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("height"));
	}

	@Test
	@DisplayName("getBenHeightDetailsFrmNurse should reject a request without benRegID")
	void getBenHeightDetailsFrmNurse_shouldRejectRequestWithoutBenRegId() throws Exception {
		String result = controller.getBenHeightDetailsFrmNurse("{\"visitCode\":22}");

		assertTrue(result.contains("Invalid request"));
		verify(anthropometryVitalsService, never()).getBeneficiaryHeightDetails(anyLong());
	}

	@Test
	@DisplayName("getBenHeightDetailsFrmNurse should surface a service failure")
	void getBenHeightDetailsFrmNurse_shouldSurfaceServiceFailure() throws Exception {
		when(anthropometryVitalsService.getBeneficiaryHeightDetails(11L)).thenThrow(new IllegalStateException("db down"));

		assertTrue(controller.getBenHeightDetailsFrmNurse("{\"benRegID\":11}")
				.contains("Error while getting beneficiary height data"));
	}

	@Test
	@DisplayName("getBenHeightDetailsFrmNurse should surface a malformed request")
	void getBenHeightDetailsFrmNurse_shouldSurfaceMalformedRequest() {
		assertTrue(controller.getBenHeightDetailsFrmNurse("not-json")
				.contains("Error while getting beneficiary height data"));
	}
}
