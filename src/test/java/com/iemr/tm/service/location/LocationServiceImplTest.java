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
package com.iemr.tm.service.location;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.location.CountryCityMasterRepo;
import com.iemr.tm.repo.location.CountryMasterRepo;
import com.iemr.tm.repo.location.DistrictBlockMasterRepo;
import com.iemr.tm.repo.location.DistrictBranchMasterRepo;
import com.iemr.tm.repo.location.DistrictMasterRepo;
import com.iemr.tm.repo.location.ParkingPlaceMasterRepo;
import com.iemr.tm.repo.location.ServicePointMasterRepo;
import com.iemr.tm.repo.location.StateMasterRepo;
import com.iemr.tm.repo.location.V_GetLocDetailsFromSPidAndPSMidRepo;
import com.iemr.tm.repo.location.V_getVanLocDetailsRepo;
import com.iemr.tm.repo.location.V_get_prkngplc_dist_zone_state_from_spidRepo;
import com.iemr.tm.repo.location.ZoneMasterRepo;
import com.iemr.tm.repo.login.ServicePointVillageMappingRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LocationServiceImpl Test Suite")
class LocationServiceImplTest {

	@Mock
	private CountryMasterRepo countryMasterRepo;
	@Mock
	private CountryCityMasterRepo countryCityMasterRepo;
	@Mock
	private StateMasterRepo stateMasterRepo;
	@Mock
	private ZoneMasterRepo zoneMasterRepo;
	@Mock
	private DistrictMasterRepo districtMasterRepo;
	@Mock
	private DistrictBlockMasterRepo districtBlockMasterRepo;
	@Mock
	private ParkingPlaceMasterRepo parkingPlaceMasterRepo;
	@Mock
	private ServicePointMasterRepo servicePointMasterRepo;
	@Mock
	private V_GetLocDetailsFromSPidAndPSMidRepo v_GetLocDetailsFromSPidAndPSMidRepo;
	@Mock
	private ServicePointVillageMappingRepo servicePointVillageMappingRepo;
	@Mock
	private DistrictBranchMasterRepo districtBranchMasterRepo;
	@Mock
	private V_get_prkngplc_dist_zone_state_from_spidRepo v_get_prkngplc_dist_zone_state_from_spidRepo;
	@Mock
	private V_getVanLocDetailsRepo v_getVanLocDetailsRepo;

	@InjectMocks
	private LocationServiceImpl service;

	@Test
	@DisplayName("getCountryList should answer for a well formed request")
	void getCountryList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCountryList());
	}

	@Test
	@DisplayName("getCountryCityList should answer for a well formed request")
	void getCountryCityList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getCountryCityList(9));
	}

	@Test
	@DisplayName("getStateList should answer for a well formed request")
	void getStateList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getStateList());
	}

	@Test
	@DisplayName("getZoneList should answer for a well formed request")
	void getZoneList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getZoneList(9));
	}

	@Test
	@DisplayName("getDistrictList should answer for a well formed request")
	void getDistrictList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getDistrictList(9));
	}

	@Test
	@DisplayName("getDistrictBlockList should answer for a well formed request")
	void getDistrictBlockList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getDistrictBlockList(9));
	}

	@Test
	@DisplayName("getParkingPlaceList should answer for a well formed request")
	void getParkingPlaceList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getParkingPlaceList(9));
	}

	@Test
	@DisplayName("getServicePointPlaceList should answer for a well formed request")
	void getServicePointPlaceList_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getServicePointPlaceList(9));
	}

	@Test
	@DisplayName("getVillageMasterFromBlockID should answer for a well formed request")
	void getVillageMasterFromBlockID_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getVillageMasterFromBlockID(9));
	}

	@Test
	@DisplayName("getLocDetailsNew should answer for a well formed request")
	void getLocDetailsNew_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getLocDetailsNew(9, 9));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("location master lookups")
	class LocationMasterTests {

		private java.util.ArrayList<Object[]> rows(Object[]... values) {
			return new java.util.ArrayList<>(java.util.Arrays.asList(values));
		}

		@Test
		@DisplayName("getStateList should name every configured state")
		void getStateList_shouldNameEveryState() {
			org.mockito.Mockito.when(stateMasterRepo.getStateMaster())
					.thenReturn(rows(new Object[] { 21, "Maharashtra" }));

			org.junit.jupiter.api.Assertions.assertTrue(service.getStateList().contains("Maharashtra"));
		}

		@Test
		@DisplayName("getStateList should answer with an empty list when no state is configured")
		void getStateList_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(stateMasterRepo.getStateMaster()).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getStateList());
		}

		@Test
		@DisplayName("getZoneList should name every zone of the provider")
		void getZoneList_shouldNameEveryZone() {
			org.mockito.Mockito.when(zoneMasterRepo.getZoneMaster(9))
					.thenReturn(rows(new Object[] { 5, "West Zone" }));

			org.junit.jupiter.api.Assertions.assertTrue(service.getZoneList(9).contains("West Zone"));
		}

		@Test
		@DisplayName("getZoneList should answer with an empty list when the provider has no zone")
		void getZoneList_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(zoneMasterRepo.getZoneMaster(9)).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getZoneList(9));
		}

		@Test
		@DisplayName("getDistrictList should name every district of the state")
		void getDistrictList_shouldNameEveryDistrict() {
			org.mockito.Mockito.when(districtMasterRepo.getDistrictMaster(21))
					.thenReturn(rows(new Object[] { 31, "Nagpur", 21, 5 }));

			org.junit.jupiter.api.Assertions.assertTrue(service.getDistrictList(21).contains("Nagpur"));
		}

		@Test
		@DisplayName("getDistrictList should answer with an empty list when the state has no district")
		void getDistrictList_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(districtMasterRepo.getDistrictMaster(21)).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getDistrictList(21));
		}

		@Test
		@DisplayName("getDistrictBlockList should name every block of the district")
		void getDistrictBlockList_shouldNameEveryBlock() {
			org.mockito.Mockito.when(districtBlockMasterRepo.getDistrictBlockMaster(31))
					.thenReturn(rows(new Object[] { 41, "Kamptee" }));

			org.junit.jupiter.api.Assertions.assertTrue(service.getDistrictBlockList(31).contains("Kamptee"));
		}

		@Test
		@DisplayName("getDistrictBlockList should answer with an empty list when the district has no block")
		void getDistrictBlockList_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(districtBlockMasterRepo.getDistrictBlockMaster(31))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getDistrictBlockList(31));
		}

		@Test
		@DisplayName("getParkingPlaceList should name every parking place of the provider")
		void getParkingPlaceList_shouldNameEveryParkingPlace() {
			org.mockito.Mockito.when(parkingPlaceMasterRepo.getParkingPlaceMaster(9))
					.thenReturn(rows(new Object[] { 2, "Kamptee Depot" }));

			org.junit.jupiter.api.Assertions.assertTrue(service.getParkingPlaceList(9).contains("Kamptee Depot"));
		}

		@Test
		@DisplayName("getParkingPlaceList should answer with an empty list when the provider has no parking place")
		void getParkingPlaceList_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(parkingPlaceMasterRepo.getParkingPlaceMaster(9))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getParkingPlaceList(9));
		}

		@Test
		@DisplayName("getServicePointPlaceList should name every service point of the parking place")
		void getServicePointPlaceList_shouldNameEveryServicePoint() {
			org.mockito.Mockito.when(servicePointMasterRepo.getServicePointMaster(2))
					.thenReturn(rows(new Object[] { 51, "PHC Kamptee" }));

			org.junit.jupiter.api.Assertions.assertTrue(service.getServicePointPlaceList(2).contains("PHC Kamptee"));
		}

		@Test
		@DisplayName("getServicePointPlaceList should answer with an empty list when the parking place has no service point")
		void getServicePointPlaceList_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(servicePointMasterRepo.getServicePointMaster(2))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getServicePointPlaceList(2));
		}

		@Test
		@DisplayName("getCountryCityList should list the cities of the country")
		void getCountryCityList_shouldListCities() {
			org.mockito.Mockito.when(countryCityMasterRepo.findByCountryIDAndDeleted(1, false))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getCountryCityList(1));
		}

		@Test
		@DisplayName("getVillageMasterFromBlockID should list the villages of the block")
		void getVillageMasterFromBlockID_shouldListVillages() {
			org.mockito.Mockito.when(districtBranchMasterRepo.findByBlockID(41))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertNotNull(service.getVillageMasterFromBlockID(41));
		}

		@Test
		@DisplayName("getLocDetailsNew should answer with the van location and the state master")
		void getLocDetailsNew_shouldAnswerWithVanLocationAndStateMaster() {
			org.mockito.Mockito.when(v_getVanLocDetailsRepo.getVanLocDetails(7))
					.thenReturn(rows(new Object[] { 21, 2, 31, "Nagpur" }, new Object[] { 21, 2, 32, "Wardha" }));
			org.mockito.Mockito.when(stateMasterRepo.getStateMaster())
					.thenReturn(rows(new Object[] { 21, "Maharashtra", 1 }));

			String result = service.getLocDetailsNew(7, 9);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Maharashtra"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Nagpur"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("parkingPlaceID"));
		}

		@Test
		@DisplayName("getLocDetailsNew should answer with an empty location when the van is not mapped")
		void getLocDetailsNew_shouldAnswerWithEmptyLocation() {
			org.mockito.Mockito.when(v_getVanLocDetailsRepo.getVanLocDetails(7))
					.thenReturn(new java.util.ArrayList<>());
			org.mockito.Mockito.when(stateMasterRepo.getStateMaster()).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertTrue(service.getLocDetailsNew(7, 9).contains("otherLoc"));
		}
	}
}
