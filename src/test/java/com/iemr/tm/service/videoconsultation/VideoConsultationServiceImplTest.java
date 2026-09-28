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
package com.iemr.tm.service.videoconsultation;

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

import com.iemr.tm.repo.login.MasterVanRepo;
import com.iemr.tm.repo.videoconsultation.UserJitsiRepo;
import com.iemr.tm.repo.videoconsultation.VideoConsultationUserRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VideoConsultationServiceImpl Test Suite")
class VideoConsultationServiceImplTest {

	@Mock
	private VideoConsultationUserRepo userRepo;
	@Mock
	private UserJitsiRepo userJitsiRepo;
	@Mock
	private MasterVanRepo masterVanRepo;

	@InjectMocks
	private VideoConsultationServiceImpl service;

	@Test
	@DisplayName("login should reject a request it cannot act on")
	void login_shouldRejectRequestItCannotActOn() {
		assertThrows(com.iemr.tm.utils.exception.VideoConsultationException.class, () -> service.login(11L));
	}

	@Test
	@DisplayName("callUser should reject a request it cannot act on")
	void callUser_shouldRejectRequestItCannotActOn() {
		assertThrows(com.iemr.tm.utils.exception.VideoConsultationException.class, () -> service.callUser(11L, 11L));
	}

	@Test
	@DisplayName("callUserjitsi should reject a request it cannot act on")
	void callUserjitsi_shouldRejectRequestItCannotActOn() {
		assertThrows(com.iemr.tm.utils.exception.VideoConsultationException.class, () -> service.callUserjitsi(11L, 11L));
	}

	@Test
	@DisplayName("callVan should reject a request it cannot act on")
	void callVan_shouldRejectRequestItCannotActOn() {
		assertThrows(com.iemr.tm.utils.exception.VideoConsultationException.class, () -> service.callVan(11L, 9));
	}

	@Test
	@DisplayName("callVanJitsi should reject a request it cannot act on")
	void callVanJitsi_shouldRejectRequestItCannotActOn() {
		assertThrows(com.iemr.tm.utils.exception.VideoConsultationException.class, () -> service.callVanJitsi(11L, 9));
	}

	@Test
	@DisplayName("logout should answer for a well formed request")
	void logout_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.logout());
	}
}
