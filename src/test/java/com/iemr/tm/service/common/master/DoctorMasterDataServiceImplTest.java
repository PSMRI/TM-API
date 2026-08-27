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

import com.iemr.tm.repo.masterrepo.anc.ServiceMasterRepo;
import com.iemr.tm.repo.masterrepo.doctor.InstituteRepo;
import com.iemr.tm.repo.masterrepo.doctor.PreMalignantLesionMasterRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DoctorMasterDataServiceImpl Test Suite")
class DoctorMasterDataServiceImplTest {

	@Mock
	private PreMalignantLesionMasterRepo preMalignantLesionMasterRepo;
	@Mock
	private InstituteRepo instituteRepo;
	@Mock
	private ServiceMasterRepo serviceMasterRepo;

	@InjectMocks
	private DoctorMasterDataServiceImpl service;

	@Test
	@DisplayName("getCancerScreeningMasterDataForDoctor should answer for a well formed request")
	void getCancerScreeningMasterDataForDoctor_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCancerScreeningMasterDataForDoctor(9));
	}
}
