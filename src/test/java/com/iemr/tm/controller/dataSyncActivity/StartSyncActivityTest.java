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
*/package com.iemr.tm.controller.dataSyncActivity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.service.dataSyncActivity.DownloadDataFromServerImpl;
import com.iemr.tm.service.dataSyncActivity.UploadDataToServerImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StartSyncActivity Test Suite")
class StartSyncActivityTest {

	private static final String AUTHORIZATION = "Bearer session-token";
	private static final String SERVER_AUTHORIZATION = "Bearer server-token";

	@Mock
	private UploadDataToServerImpl uploadDataToServerImpl;
	@Mock
	private DownloadDataFromServerImpl downloadDataFromServerImpl;

	@InjectMocks
	private StartSyncActivity controller;

	@Nested
	@DisplayName("van to server sync")
	class VanToServerTests {

		private static final String REQUEST = "{\"groupID\":3,\"user\":\"syncuser\"}";

		@Test
		@DisplayName("dataSyncToServer should return the uploaded payload")
		void dataSyncToServer_shouldReturnUploadedPayload() throws Exception {
			when(uploadDataToServerImpl.getDataToSyncToServer(3, "syncuser", SERVER_AUTHORIZATION))
					.thenReturn("{\"synced\":true}");

			String result = controller.dataSyncToServer(REQUEST, AUTHORIZATION, SERVER_AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("synced"));
		}

		@Test
		@DisplayName("dataSyncToServer should report a sync the service could not complete")
		void dataSyncToServer_shouldReportIncompleteSync() throws Exception {
			when(uploadDataToServerImpl.getDataToSyncToServer(3, "syncuser", SERVER_AUTHORIZATION)).thenReturn(null);

			assertTrue(controller.dataSyncToServer(REQUEST, AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("Error in data sync"));
		}

		@Test
		@DisplayName("dataSyncToServer should reject a request without a sync group or user")
		void dataSyncToServer_shouldRejectRequestWithoutGroupOrUser() {
			assertTrue(controller.dataSyncToServer("{}", AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("Either of groupID or user is invalid or null"));
		}

		@Test
		@DisplayName("dataSyncToServer should report a request it cannot act on")
		void dataSyncToServer_shouldReportRequestItCannotActOn() {
			assertTrue(controller.dataSyncToServer("not json", AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("\"statusCode\""));
		}

		@Test
		@DisplayName("getSyncGroupDetails should return the configured sync groups")
		void getSyncGroupDetails_shouldReturnConfiguredGroups() throws Exception {
			when(uploadDataToServerImpl.getDataSyncGroupDetails()).thenReturn("{\"groups\":[]}");

			assertTrue(controller.getSyncGroupDetails().contains("groups"));
		}

		@Test
		@DisplayName("getSyncGroupDetails should report groups the service could not read")
		void getSyncGroupDetails_shouldReportUnreadableGroups() throws Exception {
			when(uploadDataToServerImpl.getDataSyncGroupDetails()).thenReturn(null);

			assertTrue(controller.getSyncGroupDetails().contains("Error in getting data sync group details"));
		}

		@Test
		@DisplayName("getSyncGroupDetails should report a lookup it cannot complete")
		void getSyncGroupDetails_shouldReportFailedLookup() throws Exception {
			when(uploadDataToServerImpl.getDataSyncGroupDetails())
					.thenThrow(new IllegalStateException("sync group table unavailable"));

			assertTrue(controller.getSyncGroupDetails().contains("\"statusCode\""));
		}
	}

	@Nested
	@DisplayName("master download")
	class MasterDownloadTests {

		private static final String REQUEST = "{\"vanID\":7,\"providerServiceMapID\":9}";

		@Test
		@DisplayName("startMasterDownload should return the downloaded master payload")
		void startMasterDownload_shouldReturnDownloadedPayload() throws Exception {
			when(downloadDataFromServerImpl.downloadMasterDataFromServer(SERVER_AUTHORIZATION, 7, 9))
					.thenReturn("done");

			String result = controller.startMasterDownload(REQUEST, AUTHORIZATION, SERVER_AUTHORIZATION);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("done"));
		}

		@Test
		@DisplayName("startMasterDownload should report a download already running on another device")
		void startMasterDownload_shouldReportDownloadAlreadyRunning() throws Exception {
			when(downloadDataFromServerImpl.downloadMasterDataFromServer(SERVER_AUTHORIZATION, 7, 9))
					.thenReturn("inProgress");

			assertTrue(controller.startMasterDownload(REQUEST, AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("Download is already in progress"));
		}

		@Test
		@DisplayName("startMasterDownload should report a download the service could not start")
		void startMasterDownload_shouldReportUnstartedDownload() throws Exception {
			when(downloadDataFromServerImpl.downloadMasterDataFromServer(SERVER_AUTHORIZATION, 7, 9))
					.thenReturn(null);

			assertTrue(controller.startMasterDownload(REQUEST, AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("\"statusCode\""));
		}

		@Test
		@DisplayName("startMasterDownload should reject a request without a van or provider")
		void startMasterDownload_shouldRejectRequestWithoutVanOrProvider() {
			assertTrue(controller.startMasterDownload("{}", AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("Kindly contact the administrator"));
		}

		@Test
		@DisplayName("startMasterDownload should report a request it cannot act on")
		void startMasterDownload_shouldReportRequestItCannotActOn() {
			assertTrue(controller.startMasterDownload("not json", AUTHORIZATION, SERVER_AUTHORIZATION)
					.contains("\"statusCode\""));
		}

		@Test
		@DisplayName("checkMastersDownloadProgress should return the download status")
		void checkMastersDownloadProgress_shouldReturnStatus() {
			when(downloadDataFromServerImpl.getDownloadStatus()).thenReturn(new java.util.HashMap<>());

			assertTrue(controller.checkMastersDownloadProgress().contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("checkMastersDownloadProgress should report a status it cannot read")
		void checkMastersDownloadProgress_shouldReportUnreadableStatus() {
			when(downloadDataFromServerImpl.getDownloadStatus())
					.thenThrow(new IllegalStateException("status unavailable"));

			assertTrue(controller.checkMastersDownloadProgress().contains("\"statusCode\""));
		}

		@Test
		@DisplayName("getVanDetailsForMasterDownload should return the vans available to sync")
		void getVanDetails_shouldReturnAvailableVans() throws Exception {
			when(downloadDataFromServerImpl.getVanDetailsForMasterDownload()).thenReturn("{\"vans\":[]}");

			assertTrue(controller.getVanDetailsForMasterDownload().contains("vans"));
		}

		@Test
		@DisplayName("getVanDetailsForMasterDownload should report vans the service could not read")
		void getVanDetails_shouldReportUnreadableVans() throws Exception {
			when(downloadDataFromServerImpl.getVanDetailsForMasterDownload()).thenReturn(null);

			assertTrue(controller.getVanDetailsForMasterDownload().contains("Error while getting van details"));
		}

		@Test
		@DisplayName("getVanDetailsForMasterDownload should report a lookup it cannot complete")
		void getVanDetails_shouldReportFailedLookup() throws Exception {
			when(downloadDataFromServerImpl.getVanDetailsForMasterDownload())
					.thenThrow(new IllegalStateException("van table unavailable"));

			assertTrue(controller.getVanDetailsForMasterDownload().contains("\"statusCode\""));
		}
	}
}
