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
package com.iemr.tm.service.dataSyncLayerCentral;

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



@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GetDataFromVanAndSyncToDBImpl Test Suite")
class GetDataFromVanAndSyncToDBImplTest {

	@Mock
	private DataSyncRepositoryCentral dataSyncRepositoryCentral;

	@InjectMocks
	private GetDataFromVanAndSyncToDBImpl service;

	@Test
	@DisplayName("syncDataToServer should reject a request it cannot act on")
	void syncDataToServer_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.syncDataToServer("{}", "{}"));
	}

	@Test
	@DisplayName("update_M_BeneficiaryRegIdMapping_for_provisioned_benID should answer for a well formed request")
	void update_M_BeneficiaryRegIdMapping_for_provisioned_benID_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.update_M_BeneficiaryRegIdMapping_for_provisioned_benID(org.mockito.Mockito.mock(com.iemr.tm.data.syncActivity_syncLayer.SyncUploadDataDigester.class)));
	}

	@Test
	@DisplayName("getQueryToInsertDataToServerDB should answer for a well formed request")
	void getQueryToInsertDataToServerDB_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getQueryToInsertDataToServerDB("{}", "{}", "{}"));
	}

	@Test
	@DisplayName("getQueryToUpdateDataToServerDB should answer for a well formed request")
	void getQueryToUpdateDataToServerDB_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getQueryToUpdateDataToServerDB("{}", "{}", "{}"));
	}
}
