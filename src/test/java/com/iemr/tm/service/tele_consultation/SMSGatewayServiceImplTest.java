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
package com.iemr.tm.service.tele_consultation;

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

import com.iemr.tm.repo.tc_consultation.TCRequestModelRepo;
import com.iemr.tm.utils.CookieUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SMSGatewayServiceImpl Test Suite")
class SMSGatewayServiceImplTest {

	@Mock
	private TCRequestModelRepo tCRequestModelRepo;
	@Mock
	private CookieUtil cookieUtil;

	@InjectMocks
	private SMSGatewayServiceImpl service;

	@Test
	@DisplayName("smsSenderGateway should answer for a well formed request")
	void smsSenderGateway_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.smsSenderGateway("{}", 11L, 9, 11L, 11L, "{}", "{}", "{}", "{}"));
	}

	@Test
	@DisplayName("smsSenderGateway2 should answer for a well formed request")
	void smsSenderGateway2_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.smsSenderGateway2("{}", new java.util.ArrayList<>(), "{}", 11L, "{}", new java.util.ArrayList<>()));
	}

	@Test
	@DisplayName("createSMSRequest should answer for a well formed request")
	void createSMSRequest_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.createSMSRequest("{}", 11L, 9, 11L, 11L, "{}", "{}", "{}"));
	}

	@Test
	@DisplayName("sendSMS should reject a request it cannot act on")
	void sendSMS_shouldRejectRequestItCannotActOn() {
		assertThrows(NullPointerException.class, () -> service.sendSMS("{}", "{}"));
	}
}
