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
package com.iemr.tm.controller.quickBlox;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iemr.tm.service.quickBlox.QuickbloxService;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuickbloxController Test Suite")
class QuickbloxControllerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String REQUEST = "{\"userName\":\"nurse\"}";

	@Mock
	private QuickbloxService quickbloxService;

	@InjectMocks
	private QuickbloxController controller;

	@Test
	@DisplayName("getquickbloxIds should return the ids produced by the service")
	void getquickbloxIds_shouldReturnServiceIds() throws Exception {
		when(quickbloxService.getQuickbloxIds(REQUEST)).thenReturn("{\"quickbloxId\":\"99\"}");

		String result = controller.getquickbloxIds(REQUEST, AUTHORIZATION);

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("quickbloxId"));
	}

	@Test
	@DisplayName("getquickbloxIds should surface a service failure")
	void getquickbloxIds_shouldSurfaceServiceFailure() throws Exception {
		when(quickbloxService.getQuickbloxIds(REQUEST)).thenThrow(new IllegalStateException("quickblox down"));

		assertTrue(controller.getquickbloxIds(REQUEST, AUTHORIZATION).contains("Error while getting quickblox Ids"));
	}
}
