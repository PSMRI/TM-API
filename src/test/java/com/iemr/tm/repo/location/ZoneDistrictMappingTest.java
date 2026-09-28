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
*/package com.iemr.tm.repo.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.iemr.tm.common.PojoTestSupport;

@DisplayName("Location repository entity Test Suite")
class ZoneDistrictMappingTest {

	@TestFactory
	@DisplayName("The location entities should round trip every mapped property")
	List<DynamicTest> locationEntitiesShouldRoundTripProperties() {
		return PojoTestSupport.accessorTestsFor("com.iemr.tm.repo.location");
	}

	@Test
	@DisplayName("The mapping should hold the zone to district link it was built with")
	void mapping_shouldHoldZoneToDistrictLink() {
		Timestamp createdDate = new Timestamp(System.currentTimeMillis());

		ZoneDistrictMapping mapping = new ZoneDistrictMapping(1, 2, 3, 9, false, "N", "admin", createdDate,
				"admin", createdDate);

		assertEquals(1, mapping.getZoneDistrictMapID());
		assertEquals(2, mapping.getZoneID());
		assertEquals(3, mapping.getDistrictID());
		assertEquals(9, mapping.getProviderServiceMapID());
		assertEquals(Boolean.FALSE, mapping.getDeleted());
		assertEquals("N", mapping.getProcessed());
		assertEquals("admin", mapping.getCreatedBy());
		assertEquals(createdDate, mapping.getCreatedDate());
		assertEquals("admin", mapping.getModifiedBy());
		assertEquals(createdDate, mapping.getLastModDate());
	}

	@Test
	@DisplayName("The mapping should hold the districts it was built with")
	void mapping_shouldHoldDistricts() {
		Timestamp createdDate = new Timestamp(System.currentTimeMillis());
		com.iemr.tm.data.location.Districts district = new com.iemr.tm.data.location.Districts();

		ZoneDistrictMapping mapping = new ZoneDistrictMapping(1, 2, 3, 9, false, "N", "admin", createdDate,
				"admin", createdDate, Collections.singleton(district));

		assertEquals(1, mapping.getDistrictsSet().size());
		assertTrue(mapping.getDistrictsSet().contains(district));
	}

	@Test
	@DisplayName("An empty mapping should accept every mapped property")
	void emptyMapping_shouldAcceptEveryProperty() {
		ZoneDistrictMapping mapping = new ZoneDistrictMapping();
		mapping.setZoneDistrictMapID(1);
		mapping.setZoneID(2);
		mapping.setDistrictID(3);
		mapping.setProviderServiceMapID(9);
		mapping.setDeleted(true);
		mapping.setProcessed("U");
		mapping.setCreatedBy("admin");
		mapping.setModifiedBy("admin");
		mapping.setDistrictsSet(Collections.emptySet());

		assertEquals(1, mapping.getZoneDistrictMapID());
		assertEquals(Boolean.TRUE, mapping.getDeleted());
		assertEquals("U", mapping.getProcessed());
		assertTrue(mapping.getDistrictsSet().isEmpty());
	}
}
