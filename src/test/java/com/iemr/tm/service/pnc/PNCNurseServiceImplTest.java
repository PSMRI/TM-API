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
package com.iemr.tm.service.pnc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.nurse.pnc.PNCCareRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PNCNurseServiceImpl Test Suite")
class PNCNurseServiceImplTest {

	@Mock
	private PNCCareRepo pncCareRepo;

	@InjectMocks
	private PNCNurseServiceImpl service;

	@Test
	@DisplayName("saveBenPncCareDetails should answer for a well formed request")
	void saveBenPncCareDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.saveBenPncCareDetails(org.mockito.Mockito.mock(com.iemr.tm.data.pnc.PNCCare.class)));
	}

	@Test
	@DisplayName("getPNCCareDetails should answer for a well formed request")
	void getPNCCareDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getPNCCareDetails(11L, 11L));
	}

	@Test
	@DisplayName("updateBenPNCCareDetails should answer for a well formed request")
	void updateBenPNCCareDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenPNCCareDetails(org.mockito.Mockito.mock(com.iemr.tm.data.pnc.PNCCare.class)));
	}

	@Test
	@DisplayName("updateBenPNCCare should answer for a well formed request")
	void updateBenPNCCare_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.updateBenPNCCare(org.mockito.Mockito.mock(com.iemr.tm.data.pnc.PNCCare.class)));
	}
}
