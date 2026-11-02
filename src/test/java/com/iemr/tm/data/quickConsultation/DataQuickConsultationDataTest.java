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
package com.iemr.tm.data.quickConsultation;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.iemr.tm.common.PojoTestSupport;

/**
 * Accessor, constructor and row-mapper coverage for every data class in
 * {@code com.iemr.tm.data.quickConsultation}.
 */
@DisplayName("com.iemr.tm.data.quickConsultation data classes")
class DataQuickConsultationDataTest {

	@TestFactory
	@DisplayName("every data class should round-trip its properties and map query rows")
	List<DynamicTest> dataClassesShouldRoundTripProperties() {
		return PojoTestSupport.accessorTestsFor("com.iemr.tm.data.quickConsultation");
	}
}
