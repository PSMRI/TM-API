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
package com.iemr.tm.service.common.master;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.masterrepo.nurse.CancerDiseaseMasterRepo;
import com.iemr.tm.repo.masterrepo.nurse.CancerPersonalHabitMasterRepo;
import com.iemr.tm.repo.masterrepo.nurse.FamilyMemberMasterRepo;
import com.iemr.tm.repo.masterrepo.nurse.VisitCategoryMasterRepo;
import com.iemr.tm.repo.masterrepo.nurse.VisitReasonMasterRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NurseMasterDataServiceImpl Test Suite")
class NurseMasterDataServiceImplTest {

	@Mock
	private CancerDiseaseMasterRepo cancerDiseaseMasterRepo;
	@Mock
	private CancerPersonalHabitMasterRepo cancerPersonalHabitMasterRepo;
	@Mock
	private FamilyMemberMasterRepo familyMemberMasterRepo;
	@Mock
	private VisitCategoryMasterRepo visitCategoryMasterRepo;
	@Mock
	private VisitReasonMasterRepo visitReasonMasterRepo;

	@InjectMocks
	private NurseMasterDataServiceImpl service;

	@Test
	@DisplayName("GetVisitReasonAndCategories should answer for a well formed request")
	void GetVisitReasonAndCategories_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.GetVisitReasonAndCategories());
	}

	@Test
	@DisplayName("getCancerScreeningMasterDataForNurse should answer for a well formed request")
	void getCancerScreeningMasterDataForNurse_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCancerScreeningMasterDataForNurse());
	}
}
