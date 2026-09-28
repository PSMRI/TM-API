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
*/package com.iemr.tm.service.common.master;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.service.labtechnician.LabTechnicianServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CommonMasterServiceImpl Test Suite")
class CommonMasterServiceImplTest {

	private static final Integer PROVIDER_SERVICE_MAP_ID = 9;
	private static final Integer FACILITY_ID = 2;
	private static final Integer VAN_ID = 7;
	private static final String GENDER = "Female";

	@Mock
	private ANCMasterDataServiceImpl ancMasterDataServiceImpl;
	@Mock
	private NurseMasterDataServiceImpl nurseMasterDataServiceImpl;
	@Mock
	private DoctorMasterDataServiceImpl doctorMasterDataServiceImpl;
	@Mock
	private RegistrarServiceMasterDataImpl registrarServiceMasterDataImpl;
	@Mock
	private NCDScreeningMasterServiceImpl ncdScreeningServiceImpl;
	@Mock
	private QCMasterDataServiceImpl qCMasterDataServiceImpl;
	@Mock
	private NCDCareMasterDataServiceImpl ncdCareMasterDataServiceImpl;
	@Mock
	private LabTechnicianServiceImpl labTechnicianServiceImpl;

	private CommonMasterServiceImpl service;

	@BeforeEach
	@DisplayName("Wire the service with the mocked master services")
	void setUp() {
		service = new CommonMasterServiceImpl();
		service.setAncMasterDataServiceImpl(ancMasterDataServiceImpl);
		service.setNurseMasterDataServiceImpl(nurseMasterDataServiceImpl);
		service.setDoctorMasterDataServiceImpl(doctorMasterDataServiceImpl);
		service.setRegistrarServiceMasterDataImpl(registrarServiceMasterDataImpl);
		service.setNcdScreeningServiceImpl(ncdScreeningServiceImpl);
		service.setqCMasterDataServiceImpl(qCMasterDataServiceImpl);
		service.setNcdCareMasterDataServiceImpl(ncdCareMasterDataServiceImpl);
		service.setLabTechnicianServiceImpl(labTechnicianServiceImpl);
	}

	@Test
	@DisplayName("getVisitReasonAndCategories should return the visit master the nurse service assembled")
	void getVisitReasonAndCategories_shouldReturnNurseMaster() {
		when(nurseMasterDataServiceImpl.GetVisitReasonAndCategories()).thenReturn("{\"visitReasons\":[]}");

		assertEquals("{\"visitReasons\":[]}", service.getVisitReasonAndCategories());
	}

	@Test
	@DisplayName("getECGAbnormalFindings should return the findings master the lab service assembled")
	void getECGAbnormalFindings_shouldReturnLabMaster() {
		when(labTechnicianServiceImpl.getECGAbnormalFindings()).thenReturn("{\"findings\":[]}");

		assertEquals("{\"findings\":[]}", service.getECGAbnormalFindings());
	}

	@Nested
	@DisplayName("nurse master data")
	class NurseMasterTests {

		@Test
		@DisplayName("getMasterDataForNurse should route cancer screening to the nurse master service")
		void getMasterDataForNurse_shouldRouteCancerScreening() {
			when(nurseMasterDataServiceImpl.getCancerScreeningMasterDataForNurse()).thenReturn("cancer-master");

			assertEquals("cancer-master",
					service.getMasterDataForNurse(1, PROVIDER_SERVICE_MAP_ID, GENDER));
		}

		@Test
		@DisplayName("getMasterDataForNurse should route NCD screening to the screening master service")
		void getMasterDataForNurse_shouldRouteNcdScreening() {
			when(ncdScreeningServiceImpl.getNCDScreeningMasterData(anyInt(), anyInt(), anyString()))
					.thenReturn("ncd-screening-master");

			assertEquals("ncd-screening-master",
					service.getMasterDataForNurse(2, PROVIDER_SERVICE_MAP_ID, GENDER));
		}

		@ParameterizedTest
		@ValueSource(ints = { 3, 4, 5, 6, 7, 8, 10 })
		@DisplayName("getMasterDataForNurse should route the remaining visit categories to the shared master service")
		void getMasterDataForNurse_shouldRouteRemainingCategories(int visitCategoryID) {
			when(ancMasterDataServiceImpl.getCommonNurseMasterDataForGenopdAncNcdcarePnc(anyInt(), anyInt(),
					anyString())).thenReturn("shared-nurse-master");

			assertEquals("shared-nurse-master",
					service.getMasterDataForNurse(visitCategoryID, PROVIDER_SERVICE_MAP_ID, GENDER));
		}

		@Test
		@DisplayName("getMasterDataForNurse should reject a visit category it does not know")
		void getMasterDataForNurse_shouldRejectUnknownCategory() {
			assertEquals("Invalid VisitCategoryID",
					service.getMasterDataForNurse(99, PROVIDER_SERVICE_MAP_ID, GENDER));
		}

		@Test
		@DisplayName("getMasterDataForNurse should reject a request with no visit category")
		void getMasterDataForNurse_shouldRejectMissingCategory() {
			assertEquals("Invalid VisitCategoryID",
					service.getMasterDataForNurse(null, PROVIDER_SERVICE_MAP_ID, GENDER));
		}
	}

	@Nested
	@DisplayName("doctor master data")
	class DoctorMasterTests {

		@Test
		@DisplayName("getMasterDataForDoctor should route cancer screening to the doctor master service")
		void getMasterDataForDoctor_shouldRouteCancerScreening() {
			when(doctorMasterDataServiceImpl.getCancerScreeningMasterDataForDoctor(PROVIDER_SERVICE_MAP_ID))
					.thenReturn("cancer-doctor-master");

			assertEquals("cancer-doctor-master",
					service.getMasterDataForDoctor(1, PROVIDER_SERVICE_MAP_ID, GENDER, FACILITY_ID, VAN_ID));
		}

		@ParameterizedTest
		@ValueSource(ints = { 2, 3, 4, 5, 6, 7, 8, 10 })
		@DisplayName("getMasterDataForDoctor should route the remaining visit categories to the shared master service")
		void getMasterDataForDoctor_shouldRouteRemainingCategories(int visitCategoryID) {
			when(ancMasterDataServiceImpl.getCommonDoctorMasterDataForGenopdAncNcdcarePnc(anyInt(), anyInt(),
					anyString(), any(), any())).thenReturn("shared-doctor-master");

			assertEquals("shared-doctor-master", service.getMasterDataForDoctor(visitCategoryID,
					PROVIDER_SERVICE_MAP_ID, GENDER, FACILITY_ID, VAN_ID));
		}

		@Test
		@DisplayName("getMasterDataForDoctor should reject a visit category it does not know")
		void getMasterDataForDoctor_shouldRejectUnknownCategory() {
			assertEquals("Invalid VisitCategoryID",
					service.getMasterDataForDoctor(99, PROVIDER_SERVICE_MAP_ID, GENDER, FACILITY_ID, VAN_ID));
		}

		@Test
		@DisplayName("getMasterDataForDoctor should reject a request with no visit category")
		void getMasterDataForDoctor_shouldRejectMissingCategory() {
			assertEquals("Invalid VisitCategoryID",
					service.getMasterDataForDoctor(null, PROVIDER_SERVICE_MAP_ID, GENDER, FACILITY_ID, VAN_ID));
		}
	}
}
