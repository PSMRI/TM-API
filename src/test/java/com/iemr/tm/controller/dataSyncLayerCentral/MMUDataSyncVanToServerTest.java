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
*/package com.iemr.tm.controller.dataSyncLayerCentral;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.data.syncActivity_syncLayer.SyncDownloadMaster;
import com.iemr.tm.service.dataSyncLayerCentral.GetDataFromVanAndSyncToDBImpl;
import com.iemr.tm.service.dataSyncLayerCentral.GetMasterDataFromCentralForVanImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MMUDataSyncVanToServer Test Suite")
class MMUDataSyncVanToServerTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String REQUEST = "{\"schemaName\":\"db_iemr\",\"tableName\":\"m_gender\"}";

	@Mock
	private GetDataFromVanAndSyncToDBImpl getDataFromVanAndSyncToDBImpl;
	@Mock
	private GetMasterDataFromCentralForVanImpl getMasterDataFromCentralForVanImpl;

	@InjectMocks
	private MMUDataSyncVanToServer controller;

	private SyncDownloadMaster downloadRequest() {
		SyncDownloadMaster request = new SyncDownloadMaster();
		request.setSchemaName("db_iemr");
		request.setTableName("m_gender");
		return request;
	}

	@Test
	@DisplayName("dataSyncToServer should return the synced payload")
	void dataSyncToServer_shouldReturnSyncedPayload() throws Exception {
		when(getDataFromVanAndSyncToDBImpl.syncDataToServer(REQUEST, AUTHORIZATION)).thenReturn("{\"synced\":true}");

		String result = controller.dataSyncToServer(REQUEST, AUTHORIZATION);

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("synced"));
	}

	@Test
	@DisplayName("dataSyncToServer should report a sync the service could not complete")
	void dataSyncToServer_shouldReportIncompleteSync() throws Exception {
		when(getDataFromVanAndSyncToDBImpl.syncDataToServer(REQUEST, AUTHORIZATION)).thenReturn(null);

		assertTrue(controller.dataSyncToServer(REQUEST, AUTHORIZATION).contains("data dync failed"));
	}

	@Test
	@DisplayName("dataSyncToServer should report a request it cannot act on")
	void dataSyncToServer_shouldReportRequestItCannotActOn() throws Exception {
		when(getDataFromVanAndSyncToDBImpl.syncDataToServer(REQUEST, AUTHORIZATION))
				.thenThrow(new IllegalStateException("sync failed"));

		assertTrue(controller.dataSyncToServer(REQUEST, AUTHORIZATION).contains("\"statusCode\""));
	}

	@Test
	@DisplayName("dataDownloadFromServer should return the requested master table")
	void dataDownloadFromServer_shouldReturnRequestedTable() throws Exception {
		when(getMasterDataFromCentralForVanImpl.getMasterDataForVan(org.mockito.ArgumentMatchers.any()))
				.thenReturn("{\"rows\":[]}");

		String result = controller.dataDownloadFromServer(downloadRequest(), AUTHORIZATION);

		assertTrue(result.contains("\"statusCode\":200"));
		assertTrue(result.contains("rows"));
	}

	@Test
	@DisplayName("dataDownloadFromServer should name the table it could not download")
	void dataDownloadFromServer_shouldNameUndownloadableTable() throws Exception {
		when(getMasterDataFromCentralForVanImpl.getMasterDataForVan(org.mockito.ArgumentMatchers.any()))
				.thenReturn(null);

		assertTrue(controller.dataDownloadFromServer(downloadRequest(), AUTHORIZATION)
				.contains("db_iemr.m_gender"));
	}

	@Test
	@DisplayName("dataDownloadFromServer should reject a request with no table to download")
	void dataDownloadFromServer_shouldRejectEmptyRequest() {
		assertTrue(controller.dataDownloadFromServer(null, AUTHORIZATION).contains("Invalid request"));
	}

	@Test
	@DisplayName("dataDownloadFromServer should report a download it cannot complete")
	void dataDownloadFromServer_shouldReportFailedDownload() throws Exception {
		when(getMasterDataFromCentralForVanImpl.getMasterDataForVan(org.mockito.ArgumentMatchers.any()))
				.thenThrow(new IllegalStateException("central database unavailable"));

		assertTrue(controller.dataDownloadFromServer(downloadRequest(), AUTHORIZATION).contains("\"statusCode\""));
	}
}
