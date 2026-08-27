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
package com.iemr.tm.controller.snomedct;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iemr.tm.data.snomedct.SCTDescription;
import com.iemr.tm.service.snomedct.SnomedService;

@ExtendWith(MockitoExtension.class)
@DisplayName("SnomedController Test Suite")
class SnomedControllerTest {

	private static final String TERM_REQUEST = "{\"term\":\"fever\"}";

	@Mock
	private SnomedService snomedService;

	private SnomedController controller;

	@BeforeEach
	@DisplayName("Wire the controller with a mocked SNOMED service")
	void setUp() {
		controller = new SnomedController();
		controller.setSnomedService(snomedService);
	}

	private SCTDescription record(String conceptId, String term) {
		SCTDescription description = new SCTDescription();
		description.setConceptID(conceptId);
		description.setTerm(term);
		return description;
	}

	@Nested
	@DisplayName("getSnomedCTRecord")
	class GetSnomedCtRecordTests {

		@Test
		@DisplayName("getSnomedCTRecord should return the matched clinical term")
		void getSnomedCTRecord_shouldReturnMatchedTerm() {
			when(snomedService.findSnomedCTRecordFromTerm("fever")).thenReturn(record("386661006", "Fever"));

			String result = controller.getSnomedCTRecord(TERM_REQUEST);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("386661006"));
		}

		@Test
		@DisplayName("getSnomedCTRecord should report no records when the service finds nothing")
		void getSnomedCTRecord_shouldReportNoRecordsWhenNothingFound() {
			when(snomedService.findSnomedCTRecordFromTerm("fever")).thenReturn(null);

			assertTrue(controller.getSnomedCTRecord(TERM_REQUEST).contains("No Records Found"));
		}

		@Test
		@DisplayName("getSnomedCTRecord should report no records when the match carries no concept id")
		void getSnomedCTRecord_shouldReportNoRecordsWithoutConceptId() {
			when(snomedService.findSnomedCTRecordFromTerm("fever")).thenReturn(record(null, "Fever"));

			assertTrue(controller.getSnomedCTRecord(TERM_REQUEST).contains("No Records Found"));
		}

		@Test
		@DisplayName("getSnomedCTRecord should surface a service failure")
		void getSnomedCTRecord_shouldSurfaceServiceFailure() {
			when(snomedService.findSnomedCTRecordFromTerm("fever")).thenThrow(new IllegalStateException("snomed down"));

			assertTrue(controller.getSnomedCTRecord(TERM_REQUEST).contains("snomed down"));
		}

		@Test
		@DisplayName("getSnomedCTRecord should surface a malformed request")
		void getSnomedCTRecord_shouldSurfaceMalformedRequest() {
			assertTrue(controller.getSnomedCTRecord("not-json").contains("\"statusCode\""));
		}
	}

	@Nested
	@DisplayName("getSnomedCTRecordList")
	class GetSnomedCtRecordListTests {

		@Test
		@DisplayName("getSnomedCTRecordList should return the matched clinical term list")
		void getSnomedCTRecordList_shouldReturnMatchedList() throws Exception {
			when(snomedService.findSnomedCTRecordList(any(SCTDescription.class)))
					.thenReturn("[{\"conceptID\":\"386661006\"}]");

			String result = controller.getSnomedCTRecordList(TERM_REQUEST);

			assertTrue(result.contains("\"statusCode\":200"));
			assertTrue(result.contains("386661006"));
		}

		@Test
		@DisplayName("getSnomedCTRecordList should report no records when the service returns nothing")
		void getSnomedCTRecordList_shouldReportNoRecords() throws Exception {
			when(snomedService.findSnomedCTRecordList(any(SCTDescription.class))).thenReturn(null);

			assertTrue(controller.getSnomedCTRecordList(TERM_REQUEST).contains("No Records Found"));
		}

		@Test
		@DisplayName("getSnomedCTRecordList should surface a service failure")
		void getSnomedCTRecordList_shouldSurfaceServiceFailure() throws Exception {
			when(snomedService.findSnomedCTRecordList(any(SCTDescription.class)))
					.thenThrow(new IllegalStateException("snomed down"));

			assertTrue(controller.getSnomedCTRecordList(TERM_REQUEST).contains("snomed down"));
		}

		@Test
		@DisplayName("getSnomedCTRecordList should surface a malformed request")
		void getSnomedCTRecordList_shouldSurfaceMalformedRequest() {
			assertTrue(controller.getSnomedCTRecordList("not-json").contains("\"statusCode\""));
		}
	}
}
