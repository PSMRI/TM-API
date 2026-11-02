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
package com.iemr.tm.controller.location;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iemr.tm.service.location.LocationServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationController Test Suite")
class LocationControllerTest {

	@Mock
	private LocationServiceImpl locationServiceImpl;

	private LocationController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked location service")
	void setUp() {
		controller = new LocationController();
		controller.setLocationServiceImpl(locationServiceImpl);
	}

	@Nested
	@DisplayName("location master lookups")
	class MasterLookupTests {

		@Test
		@DisplayName("getCountryMaster should return the country list")
		void getCountryMaster_shouldReturnCountryList() {
			when(locationServiceImpl.getCountryList()).thenReturn("[{\"countryID\":1}]");

			assertTrue(controller.getCountryMaster().contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getCountryMaster should report a failure when no country list comes back")
		void getCountryMaster_shouldReportFailureWhenNoList() {
			when(locationServiceImpl.getCountryList()).thenReturn(null);

			assertTrue(controller.getCountryMaster().contains("Error while getting country"));
		}

		@Test
		@DisplayName("getCountryCityMaster should return the city list for the country")
		void getCountryCityMaster_shouldReturnCityList() {
			when(locationServiceImpl.getCountryCityList(1)).thenReturn("[{\"cityID\":9}]");

			assertTrue(controller.getCountryCityMaster(1).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getCountryCityMaster should report a failure when no city list comes back")
		void getCountryCityMaster_shouldReportFailureWhenNoList() {
			when(locationServiceImpl.getCountryCityList(1)).thenReturn(null);

			assertTrue(controller.getCountryCityMaster(1).contains("Error while getting country city"));
		}

		@Test
		@DisplayName("getStateMaster should return the state list")
		void getStateMaster_shouldReturnStateList() {
			when(locationServiceImpl.getStateList()).thenReturn("[{\"stateID\":2}]");

			assertTrue(controller.getStateMaster().contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getStateMaster should report a failure when no state list comes back")
		void getStateMaster_shouldReportFailureWhenNoList() {
			when(locationServiceImpl.getStateList()).thenReturn(null);

			assertTrue(controller.getStateMaster().contains("Error while getting states"));
		}

		@Test
		@DisplayName("getDistrictMaster should return the district list for the state")
		void getDistrictMaster_shouldReturnDistrictList() {
			when(locationServiceImpl.getDistrictList(2)).thenReturn("[{\"districtID\":3}]");

			assertTrue(controller.getDistrictMaster(2).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getDistrictMaster should report a failure when no district list comes back")
		void getDistrictMaster_shouldReportFailureWhenNoList() {
			when(locationServiceImpl.getDistrictList(2)).thenReturn(null);

			assertTrue(controller.getDistrictMaster(2).contains("Error while getting districts"));
		}

		@Test
		@DisplayName("getDistrictBlockMaster should return the block list for the district")
		void getDistrictBlockMaster_shouldReturnBlockList() {
			when(locationServiceImpl.getDistrictBlockList(3)).thenReturn("[{\"blockID\":4}]");

			assertTrue(controller.getDistrictBlockMaster(3).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getDistrictBlockMaster should report a failure when no block list comes back")
		void getDistrictBlockMaster_shouldReportFailureWhenNoList() {
			when(locationServiceImpl.getDistrictBlockList(3)).thenReturn(null);

			assertTrue(controller.getDistrictBlockMaster(3).contains("Error while getting district blocks"));
		}

		@Test
		@DisplayName("getVillageMaster should return the village list for the block")
		void getVillageMaster_shouldReturnVillageList() {
			when(locationServiceImpl.getVillageMasterFromBlockID(4)).thenReturn("[{\"villageID\":5}]");

			assertTrue(controller.getVillageMaster(4).contains("\"statusCode\":200"));
		}

		@Test
		@DisplayName("getVillageMaster should report a failure when no village list comes back")
		void getVillageMaster_shouldReportFailureWhenNoList() {
			when(locationServiceImpl.getVillageMasterFromBlockID(4)).thenReturn(null);

			assertTrue(controller.getVillageMaster(4).contains("Error while getting villages"));
		}
	}

	@Nested
	@DisplayName("getLocDetailsBasedOnSpIDAndPsmIDNew")
	class LocationDetailsTests {

		@Test
		@DisplayName("getLocDetailsBasedOnSpIDAndPsmIDNew should return the location details for a complete request")
		void getLocDetails_shouldReturnLocationDetails() {
			when(locationServiceImpl.getLocDetailsNew(7, 8)).thenReturn("{\"districtID\":3}");

			String result = controller.getLocDetailsBasedOnSpIDAndPsmIDNew("{\"vanID\":7,\"spPSMID\":8}");

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("districtID"));
		}

		@Test
		@DisplayName("getLocDetailsBasedOnSpIDAndPsmIDNew should reject a request without spPSMID")
		void getLocDetails_shouldRejectRequestWithoutSpPsmId() {
			String result = controller.getLocDetailsBasedOnSpIDAndPsmIDNew("{\"vanID\":7}");

			assertTrue(result.contains("Invalid request"));
			verify(locationServiceImpl, never()).getLocDetailsNew(anyInt(), anyInt());
		}

		@Test
		@DisplayName("getLocDetailsBasedOnSpIDAndPsmIDNew should surface a service failure")
		void getLocDetails_shouldSurfaceServiceFailure() {
			when(locationServiceImpl.getLocDetailsNew(7, 8)).thenThrow(new IllegalStateException("db down"));

			assertTrue(controller.getLocDetailsBasedOnSpIDAndPsmIDNew("{\"vanID\":7,\"spPSMID\":8}")
					.contains("Error while getting location data"));
		}

		@Test
		@DisplayName("getLocDetailsBasedOnSpIDAndPsmIDNew should surface a malformed request")
		void getLocDetails_shouldSurfaceMalformedRequest() {
			assertTrue(controller.getLocDetailsBasedOnSpIDAndPsmIDNew("not-json")
					.contains("Error while getting location data"));
		}
	}
}
