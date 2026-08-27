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
package com.iemr.tm.service.dataSyncActivity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.syncActivity_syncLayer.DataSyncGroupsRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UploadDataToServerImpl Test Suite")
class UploadDataToServerImplTest {

	@Mock
	private DataSyncRepository dataSyncRepository;
	@Mock
	private DataSyncGroupsRepo dataSyncGroupsRepo;

	@InjectMocks
	private UploadDataToServerImpl service;

	@Test
	@DisplayName("getDataToSyncToServer should answer for a well formed request")
	void getDataToSyncToServer_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getDataToSyncToServer(9, "{}", "{}"));
	}

	@Test
	@DisplayName("syncIntercepter should answer for a well formed request")
	void syncIntercepter_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.syncIntercepter(9, "{}", "{}"));
	}

	@Test
	@DisplayName("syncDataToServer should reject a request it cannot act on")
	void syncDataToServer_shouldRejectRequestItCannotActOn() {
		assertThrows(IllegalArgumentException.class, () -> service.syncDataToServer("{}", "{}", "{}", "{}", new java.util.ArrayList<>(), "user", "{}"));
	}

	@Test
	@DisplayName("getVanSerialNoListForSyncedData should answer for a well formed request")
	void getVanSerialNoListForSyncedData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getVanSerialNoListForSyncedData("{}", new java.util.ArrayList<>()));
	}

	@Test
	@DisplayName("getDataSyncGroupDetails should answer for a well formed request")
	void getDataSyncGroupDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getDataSyncGroupDetails());
	}
}
