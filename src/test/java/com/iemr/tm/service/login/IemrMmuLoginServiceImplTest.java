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
package com.iemr.tm.service.login;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.iemr.tm.repo.login.MasterVanRepo;
import com.iemr.tm.repo.login.ServicePointVillageMappingRepo;
import com.iemr.tm.repo.login.UserParkingplaceMappingRepo;
import com.iemr.tm.repo.login.UserVanSpDetails_View_Repo;
import com.iemr.tm.repo.login.VanServicepointMappingRepo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("IemrMmuLoginServiceImpl Test Suite")
class IemrMmuLoginServiceImplTest {

	@Mock
	private UserParkingplaceMappingRepo userParkingplaceMappingRepo;
	@Mock
	private MasterVanRepo masterVanRepo;
	@Mock
	private VanServicepointMappingRepo vanServicepointMappingRepo;
	@Mock
	private ServicePointVillageMappingRepo servicePointVillageMappingRepo;
	@Mock
	private UserVanSpDetails_View_Repo userVanSpDetails_View_Repo;

	@InjectMocks
	private IemrMmuLoginServiceImpl service;

	@Test
	@DisplayName("getUserServicePointVanDetails should answer for a well formed request")
	void getUserServicePointVanDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getUserServicePointVanDetails(9));
	}

	@Test
	@DisplayName("getServicepointVillages should answer for a well formed request")
	void getServicepointVillages_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getServicepointVillages(9));
	}

	@Test
	@DisplayName("getUserVanSpDetails should answer for a well formed request")
	void getUserVanSpDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getUserVanSpDetails(9, 9));
	}

	@Test
	@DisplayName("getUserSpokeDetails should answer for a well formed request")
	void getUserSpokeDetails_shouldAnswerForWellFormedRequest() throws Exception {
		assertDoesNotThrow(() -> service.getUserSpokeDetails(9));
	}

	@org.junit.jupiter.api.Nested
	@DisplayName("van and service point lookup")
	class VanAndServicePointTests {

		/** parkingPlaceID, stateID, stateName, districtID, districtName, blockID, blockName. */
		private java.util.List<Object[]> parkingPlaceRows() {
			return new java.util.ArrayList<>(java.util.Collections.singletonList(
					new Object[] { 2, 21, "Maharashtra", 31, "Nagpur", 41, "Kamptee" }));
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should assemble the vans and service points for the parking place")
		void getUserServicePointVanDetails_shouldAssembleVansAndServicePoints() {
			org.mockito.Mockito.when(userParkingplaceMappingRepo.getUserParkingPlce(41))
					.thenReturn(parkingPlaceRows());
			org.mockito.Mockito.when(masterVanRepo.getUserVanDatails(org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(
							new Object[] { 7, "MH-31-AB-1234" })));
			org.mockito.Mockito.when(vanServicepointMappingRepo
					.getuserSpSessionDetails(org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(
							new Object[] { 51, "PHC Kamptee", "Morning" })));

			String result = service.getUserServicePointVanDetails(41);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("MH-31-AB-1234"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("PHC Kamptee"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Maharashtra"));
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should answer with placeholders when no van or service point is mapped")
		void getUserServicePointVanDetails_shouldAnswerWithPlaceholders() {
			org.mockito.Mockito.when(userParkingplaceMappingRepo.getUserParkingPlce(41))
					.thenReturn(parkingPlaceRows());
			org.mockito.Mockito.when(masterVanRepo.getUserVanDatails(org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());
			org.mockito.Mockito.when(vanServicepointMappingRepo
					.getuserSpSessionDetails(org.mockito.ArgumentMatchers.any()))
					.thenReturn(new java.util.ArrayList<>());

			String result = service.getUserServicePointVanDetails(41);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("userVanDetails"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("userSpDetails"));
		}

		@Test
		@DisplayName("getUserServicePointVanDetails should answer with nothing when no parking place is mapped")
		void getUserServicePointVanDetails_shouldAnswerWithNothingWithoutParkingPlace() {
			org.mockito.Mockito.when(userParkingplaceMappingRepo.getUserParkingPlce(41))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("{}", service.getUserServicePointVanDetails(41));
		}

		@Test
		@DisplayName("getServicepointVillages should list the villages mapped to the service point")
		void getServicepointVillages_shouldListMappedVillages() {
			org.mockito.Mockito.when(servicePointVillageMappingRepo.getServicePointVillages(51))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(
							new Object[] { 61, "Kanhan" })));

			org.junit.jupiter.api.Assertions.assertTrue(service.getServicepointVillages(51).contains("Kanhan"));
		}

		@Test
		@DisplayName("getServicepointVillages should answer with an empty list when no village is mapped")
		void getServicepointVillages_shouldAnswerWithEmptyList() {
			org.mockito.Mockito.when(servicePointVillageMappingRepo.getServicePointVillages(51))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertEquals("[]", service.getServicepointVillages(51));
		}

		@Test
		@DisplayName("getUserVanSpDetails should assemble the van, service point and location for the user")
		void getUserVanSpDetails_shouldAssembleVanServicePointAndLocation() {
			org.mockito.Mockito.when(userVanSpDetails_View_Repo.getUserVanSpDetails_View(41, 9))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(
							new Object[] { 41, 7, "MH-31-AB-1234", (short) 1, 51, "PHC Kamptee", 2, 9 })));
			org.mockito.Mockito.when(userParkingplaceMappingRepo.getUserParkingPlce(41))
					.thenReturn(parkingPlaceRows());

			String result = service.getUserVanSpDetails(41, 9);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("UserVanSpDetails"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("Nagpur"));
		}

		@Test
		@DisplayName("getUserVanSpDetails should answer with empty sections when nothing is mapped to the user")
		void getUserVanSpDetails_shouldAnswerWithEmptySections() {
			org.mockito.Mockito.when(userVanSpDetails_View_Repo.getUserVanSpDetails_View(41, 9))
					.thenReturn(new java.util.ArrayList<>());
			org.mockito.Mockito.when(userParkingplaceMappingRepo.getUserParkingPlce(41))
					.thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertTrue(
					service.getUserVanSpDetails(41, 9).contains("UserLocDetails"));
		}

		@Test
		@DisplayName("getUserSpokeDetails should list every van after the all option")
		void getUserSpokeDetails_shouldListEveryVanAfterAllOption() {
			org.mockito.Mockito.when(masterVanRepo.getVanMaster(9))
					.thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList(
							new Object[] { 7, "MH-31-AB-1234" })));

			String result = service.getUserSpokeDetails(9);

			org.junit.jupiter.api.Assertions.assertTrue(result.contains("All"));
			org.junit.jupiter.api.Assertions.assertTrue(result.contains("MH-31-AB-1234"));
		}

		@Test
		@DisplayName("getUserSpokeDetails should offer only the all option when no van is configured")
		void getUserSpokeDetails_shouldOfferOnlyAllOption() {
			org.mockito.Mockito.when(masterVanRepo.getVanMaster(9)).thenReturn(new java.util.ArrayList<>());

			org.junit.jupiter.api.Assertions.assertTrue(service.getUserSpokeDetails(9).contains("All"));
		}
	}
}
