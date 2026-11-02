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

import com.iemr.tm.repo.syncActivity_syncLayer.SyncDownloadMasterRepo;
import com.iemr.tm.repo.syncActivity_syncLayer.TempVanRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DownloadDataFromServerImpl Test Suite")
class DownloadDataFromServerImplTest {

	@Mock
	private SyncDownloadMasterRepo syncDownloadMasterRepo;
	@Mock
	private DataSyncRepository dataSyncRepository;
	@Mock
	private TempVanRepo tempVanRepo;

	@InjectMocks
	private DownloadDataFromServerImpl service;

	@Test
	@DisplayName("downloadMasterDataFromServer should answer for a well formed request")
	void downloadMasterDataFromServer_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.downloadMasterDataFromServer("{}", 9, 9));
	}

	@Test
	@DisplayName("getVanDetailsForMasterDownload should reject a request it cannot act on")
	void getVanDetailsForMasterDownload_shouldRejectRequestItCannotActOn() {
		assertThrows(Exception.class, () -> service.getVanDetailsForMasterDownload());
	}

	@Test
	@DisplayName("getDownloadStatus should reject a request it cannot act on")
	void getDownloadStatus_shouldRejectRequestItCannotActOn() {
		assertThrows(ArithmeticException.class, () -> service.getDownloadStatus());
	}
}
