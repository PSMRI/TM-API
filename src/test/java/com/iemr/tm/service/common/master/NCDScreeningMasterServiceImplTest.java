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

import com.iemr.tm.repo.doctor.ChiefComplaintMasterRepo;
import com.iemr.tm.repo.doctor.LabTestMasterRepo;
import com.iemr.tm.repo.labModule.ProcedureRepo;
import com.iemr.tm.repo.masterrepo.anc.AllergicReactionTypesRepo;
import com.iemr.tm.repo.masterrepo.anc.DiseaseTypeRepo;
import com.iemr.tm.repo.masterrepo.anc.PersonalHabitTypeRepo;
import com.iemr.tm.repo.masterrepo.ncdScreening.BPAndDiabeticStatusRepo;
import com.iemr.tm.repo.masterrepo.ncdScreening.IDRS_ScreenQuestionsRepo;
import com.iemr.tm.repo.masterrepo.ncdScreening.NCDScreeningConditionRepo;
import com.iemr.tm.repo.masterrepo.ncdScreening.NCDScreeningReasonRepo;
import com.iemr.tm.repo.masterrepo.ncdScreening.PhysicalActivityRepo;
import com.iemr.tm.repo.masterrepo.nurse.FamilyMemberMasterRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NCDScreeningMasterServiceImpl Test Suite")
class NCDScreeningMasterServiceImplTest {

	@Mock
	private NCDScreeningConditionRepo ncdScreeningConditionRepo;
	@Mock
	private NCDScreeningReasonRepo ncdScreeningReasonRepo;
	@Mock
	private BPAndDiabeticStatusRepo bpAndDiabeticStatusRepo;
	@Mock
	private LabTestMasterRepo labTestMasterRepo;
	@Mock
	private ChiefComplaintMasterRepo chiefComplaintMasterRepo;
	@Mock
	private ProcedureRepo procedureRepo;
	@Mock
	private IDRS_ScreenQuestionsRepo iDRS_ScreenQuestionsRepo;
	@Mock
	private PhysicalActivityRepo physicalActivityRepo;
	@Mock
	private DiseaseTypeRepo diseaseTypeRepo;
	@Mock
	private FamilyMemberMasterRepo familyMemberMasterRepo;
	@Mock
	private PersonalHabitTypeRepo personalHabitTypeRepo;
	@Mock
	private AllergicReactionTypesRepo allergicReactionTypesRepo;

	@InjectMocks
	private NCDScreeningMasterServiceImpl service;

	@Test
	@DisplayName("getNCDScreeningConditions should answer for a well formed request")
	void getNCDScreeningConditions_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getNCDScreeningConditions());
	}

	@Test
	@DisplayName("getNCDScreeningReasons should answer for a well formed request")
	void getNCDScreeningReasons_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getNCDScreeningReasons());
	}

	@Test
	@DisplayName("getBPAndDiabeticStatus should answer for a well formed request")
	void getBPAndDiabeticStatus_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getBPAndDiabeticStatus(Boolean.FALSE));
	}

	@Test
	@DisplayName("getNCDTest should answer for a well formed request")
	void getNCDTest_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getNCDTest());
	}

	@Test
	@DisplayName("getChiefComplaintMaster should answer for a well formed request")
	void getChiefComplaintMaster_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getChiefComplaintMaster());
	}

	@Test
	@DisplayName("getNCDScreeningMasterData should answer for a well formed request")
	void getNCDScreeningMasterData_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getNCDScreeningMasterData(9, 9, "{}"));
	}
}
