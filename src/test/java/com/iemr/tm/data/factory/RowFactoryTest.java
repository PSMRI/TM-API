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
package com.iemr.tm.data.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * The data classes expose static factories that turn a native query's
 * {@code Object[]} rows into the payloads the API answers with. These tests
 * feed them rows shaped the way the queries return them.
 */
@DisplayName("Row factory Test Suite")
class RowFactoryTest {

	/**
	 * Builds one query row from a compact type spec: {@code L} long, {@code I}
	 * integer, {@code S} short, {@code T} comma separated text, {@code D}
	 * timestamp, {@code B} boolean, {@code C} character, {@code A} sql date and
	 * {@code _} null.
	 */
	private static Object[] row(String spec) {
		Object[] values = new Object[spec.length()];
		for (int i = 0; i < spec.length(); i++) {
			switch (spec.charAt(i)) {
			case 'L':
				values[i] = Long.valueOf(i + 1);
				break;
			case 'I':
				values[i] = Integer.valueOf(i + 1);
				break;
			case 'S':
				values[i] = Short.valueOf((short) (i + 1));
				break;
			case 'T':
				values[i] = "1,2";
				break;
			case 'D':
				values[i] = new Timestamp(System.currentTimeMillis());
				break;
			case 'B':
				values[i] = Boolean.TRUE;
				break;
			case 'C':
				values[i] = Character.valueOf('Y');
				break;
			case 'A':
				values[i] = java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(31));
				break;
			default:
				values[i] = null;
			}
		}
		return values;
	}

	private static ArrayList<Object[]> rows(Object[]... values) {
		return new ArrayList<>(Arrays.asList(values));
	}

	@Nested
	@DisplayName("registrar worklist rows")
	class RegistrarWorklistTests {

		/** benRegID, benID, name, dob, genderID, gender, visitID, visitNo, flag, category, … */
		private Object[] worklistRow(String flowStatus, java.sql.Date dob) {
			Object[] values = row("LTTASTLSTTTTTTT");
			values[3] = dob;
			values[8] = flowStatus;
			return values;
		}

		@Test
		@DisplayName("getDocWorkListData should read a beneficiary pending consultation")
		void docWorkList_shouldReadPendingBeneficiary() {
			String result = com.iemr.tm.data.registrar.WrapperRegWorklist.getDocWorkListData(
					Collections.singletonList(worklistRow("N",
							java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(31)))));

			assertTrue(result.contains("Pending For Consultation"));
			assertTrue(result.contains("years"));
		}

		@Test
		@DisplayName("getDocWorkListData should read a beneficiary whose consultation is done")
		void docWorkList_shouldReadConsultationDone() {
			assertTrue(com.iemr.tm.data.registrar.WrapperRegWorklist.getDocWorkListData(
					Collections.singletonList(worklistRow("D",
							java.sql.Date.valueOf(java.time.LocalDate.now().minusMonths(5)))))
					.contains("Consultation Done"));
		}

		@Test
		@DisplayName("getDocWorkListData should read a visit the nurse closed")
		void docWorkList_shouldReadVisitClosedByNurse() {
			assertTrue(com.iemr.tm.data.registrar.WrapperRegWorklist.getDocWorkListData(
					Collections.singletonList(worklistRow("C",
							java.sql.Date.valueOf(java.time.LocalDate.now().minusDays(11)))))
					.contains("Visit closed by Nurse"));
		}

		@Test
		@DisplayName("getDocWorkListData should read a beneficiary with no date of birth")
		void docWorkList_shouldReadBeneficiaryWithoutDob() {
			assertNotNull(com.iemr.tm.data.registrar.WrapperRegWorklist
					.getDocWorkListData(Collections.singletonList(worklistRow("N", null))));
		}

		@Test
		@DisplayName("getDocWorkListData should render an empty list for no rows")
		void docWorkList_shouldRenderEmptyListForNoRows() {
			assertEquals("[]", com.iemr.tm.data.registrar.WrapperRegWorklist
					.getDocWorkListData(new ArrayList<Object[]>()));
		}

		@Test
		@DisplayName("getRegistrarWorkList should read the registered beneficiary")
		void registrarWorkList_shouldReadRegisteredBeneficiary() {
			String result = com.iemr.tm.data.registrar.WrapperRegWorklist
					.getRegistrarWorkList(Collections.singletonList(row("LTTASTTTTTTT")));

			assertTrue(result.contains("years"));
			assertTrue(result.contains("genderName"));
		}

		@Test
		@DisplayName("getRegistrarWorkList should read a beneficiary aged in months")
		void registrarWorkList_shouldReadBeneficiaryAgedInMonths() {
			Object[] values = row("LTTASTTTTTTT");
			values[3] = java.sql.Date.valueOf(java.time.LocalDate.now().minusMonths(5));

			assertTrue(com.iemr.tm.data.registrar.WrapperRegWorklist
					.getRegistrarWorkList(Collections.singletonList(values)).contains("months"));
		}

		@Test
		@DisplayName("getRegistrarWorkList should read a beneficiary aged in days")
		void registrarWorkList_shouldReadBeneficiaryAgedInDays() {
			Object[] values = row("LTTASTTTTTTT");
			values[3] = java.sql.Date.valueOf(java.time.LocalDate.now().minusDays(11));

			assertTrue(com.iemr.tm.data.registrar.WrapperRegWorklist
					.getRegistrarWorkList(Collections.singletonList(values)).contains("days"));
		}

		@Test
		@DisplayName("getRegistrarWorkList should render an empty list for no rows")
		void registrarWorkList_shouldRenderEmptyListForNoRows() {
			assertEquals("[]", com.iemr.tm.data.registrar.WrapperRegWorklist
					.getRegistrarWorkList(new ArrayList<Object[]>()));
		}
	}

	@Nested
	@DisplayName("prescription and investigation rows")
	class PrescriptionRowTests {

		@Test
		@DisplayName("getprescribedDrugs should read the prescribed drug rows")
		void getPrescribedDrugs_shouldReadRows() {
			ArrayList<com.iemr.tm.data.quickConsultation.PrescribedDrugDetail> drugs =
					com.iemr.tm.data.quickConsultation.PrescribedDrugDetail
							.getprescribedDrugs(rows(row("LLTTITTTTTTTTTIBTTD")));

			assertEquals(1, drugs.size());
			assertNotNull(drugs.get(0));
		}

		@Test
		@DisplayName("getprescribedDrugs should read an empty result set")
		void getPrescribedDrugs_shouldReadEmptyResultSet() {
			assertTrue(com.iemr.tm.data.quickConsultation.PrescribedDrugDetail
					.getprescribedDrugs(new ArrayList<Object[]>()).isEmpty());
		}

		@Test
		@DisplayName("getBenChiefComplaints should read the recorded complaint rows")
		void getChiefComplaints_shouldReadRows() {
			ArrayList<com.iemr.tm.data.quickConsultation.BenChiefComplaint> complaints =
					com.iemr.tm.data.quickConsultation.BenChiefComplaint
							.getBenChiefComplaints(rows(row("LLLIITITTLT")));

			assertEquals(1, complaints.size());
			assertNotNull(complaints.get(0).getChiefComplaint());
		}

		@Test
		@DisplayName("getBenChiefComplaintList should read the complaints out of a case sheet")
		void getChiefComplaintList_shouldReadFromCaseSheet() {
			com.google.gson.JsonObject caseSheet = com.google.gson.JsonParser.parseString(
					"{\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
							+ "\"vanID\":7,\"parkingPlaceID\":2,\"createdBy\":\"doctor1\","
							+ "\"chiefComplaintList\":[{\"chiefComplaintID\":4,\"chiefComplaint\":\"fever\","
							+ "\"duration\":2,\"unitOfDuration\":\"days\",\"description\":\"since monday\"},"
							+ "{\"chiefComplaint\":\"cough\"}]}").getAsJsonObject();

			ArrayList<com.iemr.tm.data.quickConsultation.BenChiefComplaint> complaints =
					com.iemr.tm.data.quickConsultation.BenChiefComplaint.getBenChiefComplaintList(caseSheet);

			assertEquals(2, complaints.size());
			assertEquals("fever", complaints.get(0).getChiefComplaint());
			assertEquals(22L, complaints.get(0).getVisitCode());
		}

		@Test
		@DisplayName("getBenChiefComplaintList should read a case sheet with no complaint")
		void getChiefComplaintList_shouldReadCaseSheetWithoutComplaint() {
			assertTrue(com.iemr.tm.data.quickConsultation.BenChiefComplaint.getBenChiefComplaintList(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11}").getAsJsonObject())
					.isEmpty());
		}

		@Test
		@DisplayName("getLabTestOrderDetails should group the ordered laboratory tests")
		void getLabTestOrderDetails_shouldGroupOrderedTests() {
			com.iemr.tm.data.anc.WrapperBenInvestigationANC orders =
					com.iemr.tm.data.quickConsultation.LabTestOrderDetail
							.getLabTestOrderDetails(rows(row("LLIIT_L"), row("LLIIT_L")));

			assertEquals(2, orders.getLaboratoryList().size());
			assertNotNull(orders.getBeneficiaryRegID());
		}

		@Test
		@DisplayName("getLabTestOrderDetails should read an empty result set")
		void getLabTestOrderDetails_shouldReadEmptyResultSet() {
			assertNotNull(com.iemr.tm.data.quickConsultation.LabTestOrderDetail
					.getLabTestOrderDetails(new ArrayList<Object[]>()));
		}

		@Test
		@DisplayName("getRBSTestOrderDetailsFromVitals should add the RBS test taken with the vitals")
		void getRbsTestOrderFromVitals_shouldAddRbsTest() {
			com.iemr.tm.data.nurse.BenPhysicalVitalDetail vitals =
					new com.iemr.tm.data.nurse.BenPhysicalVitalDetail();
			vitals.setBeneficiaryRegID(11L);
			vitals.setVisitCode(22L);
			vitals.setRbsTestResult("110");

			com.iemr.tm.data.anc.WrapperBenInvestigationANC orders =
					com.iemr.tm.data.quickConsultation.LabTestOrderDetail
							.getRBSTestOrderDetailsFromVitals(vitals);

			assertEquals(1, orders.getLaboratoryList().size());
			assertEquals("RBS Test", orders.getLaboratoryList().get(0).getProcedureName());
		}

		@Test
		@DisplayName("getRBSTestOrderDetailsFromVitals should skip the RBS test when it was not taken")
		void getRbsTestOrderFromVitals_shouldSkipUntakenRbsTest() {
			assertTrue(com.iemr.tm.data.quickConsultation.LabTestOrderDetail
					.getRBSTestOrderDetailsFromVitals(new com.iemr.tm.data.nurse.BenPhysicalVitalDetail())
					.getLaboratoryList().isEmpty());
		}

		@Test
		@DisplayName("getLabTestOrderDetailList should read the ordered tests out of a case sheet")
		void getLabTestOrderDetailList_shouldReadFromCaseSheet() {
			com.google.gson.JsonObject caseSheet = com.google.gson.JsonParser.parseString(
					"{\"beneficiaryRegID\":11,\"benVisitID\":3,\"visitCode\":22,\"providerServiceMapID\":9,"
							+ "\"vanID\":7,\"parkingPlaceID\":2,\"createdBy\":\"doctor1\","
							+ "\"labTestOrders\":[{\"procedureID\":4,\"procedureName\":\"CBC\","
							+ "\"testingRequirements\":\"fasting\"},{\"procedureName\":\"ECG\"}]}")
					.getAsJsonObject();

			ArrayList<com.iemr.tm.data.quickConsultation.LabTestOrderDetail> orders =
					com.iemr.tm.data.quickConsultation.LabTestOrderDetail
							.getLabTestOrderDetailList(caseSheet, 31L);

			assertEquals(2, orders.size());
			assertEquals("CBC", orders.get(0).getProcedureName());
			assertEquals(31L, orders.get(0).getPrescriptionID());
		}

		@Test
		@DisplayName("getLabTestOrderDetailList should read a case sheet with no ordered test")
		void getLabTestOrderDetailList_shouldReadCaseSheetWithoutOrder() {
			assertTrue(com.iemr.tm.data.quickConsultation.LabTestOrderDetail.getLabTestOrderDetailList(
					com.google.gson.JsonParser.parseString("{\"beneficiaryRegID\":11}").getAsJsonObject(), 31L)
					.isEmpty());
		}
	}

	@Nested
	@DisplayName("lab result rows")
	class LabResultRowTests {

		@Test
		@DisplayName("getVisitCodeAndDate should read the tested visits")
		void getVisitCodeAndDate_shouldReadTestedVisits() {
			ArrayList<Object[]> resultSet = rows(new Object[] { 22L, "2026-08-26" });

			ArrayList<com.iemr.tm.data.labModule.LabResultEntry> visits =
					com.iemr.tm.data.labModule.LabResultEntry.getVisitCodeAndDate(resultSet);

			assertEquals(1, visits.size());
			assertEquals(22L, visits.get(0).getVisitCode());
		}

		@Test
		@DisplayName("getVisitCodeAndDate should read an empty result set")
		void getVisitCodeAndDate_shouldReadEmptyResultSet() {
			assertTrue(com.iemr.tm.data.labModule.LabResultEntry
					.getVisitCodeAndDate(new ArrayList<Object[]>()).isEmpty());
		}

		private com.iemr.tm.data.labModule.LabResultEntry stored(Integer procedureID, String reportPath) {
			com.iemr.tm.data.labModule.LabResultEntry entry = new com.iemr.tm.data.labModule.LabResultEntry();
			entry.setPrescriptionID(31L);
			entry.setProcedureID(procedureID);
			entry.setTestComponentID(2);
			entry.setTestResultValue("12");
			entry.setTestResultUnit("g/dL");
			entry.setTestReportFilePath(reportPath);
			entry.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			com.iemr.tm.data.labModule.ProcedureData procedure = new com.iemr.tm.data.labModule.ProcedureData();
			procedure.setProcedureName("CBC");
			procedure.setProcedureType("Laboratory");
			entry.setProcedureData(procedure);
			com.iemr.tm.data.labModule.TestComponentMaster component =
					new com.iemr.tm.data.labModule.TestComponentMaster();
			component.setTestComponentName("Haemoglobin");
			entry.setTestComponentMaster(component);
			return entry;
		}

		@Test
		@DisplayName("getLabResultEntry should group the components of one procedure and read its report files")
		void getLabResultEntry_shouldGroupComponentsAndReadReportFiles() {
			ArrayList<com.iemr.tm.data.labModule.LabResultEntry> comingList = new ArrayList<>();
			comingList.add(stored(1, "81,82,"));
			comingList.add(stored(1, null));
			comingList.add(stored(5, ""));

			ArrayList<com.iemr.tm.data.labModule.LabResultEntry> grouped =
					com.iemr.tm.data.labModule.LabResultEntry.getLabResultEntry(comingList);

			assertEquals(2, grouped.size());
			assertEquals(2, grouped.get(0).getComponentList().size());
			assertEquals("CBC", grouped.get(0).getProcedureName());
		}

		@Test
		@DisplayName("getLabResultEntry should read an empty result list")
		void getLabResultEntry_shouldReadEmptyList() {
			assertTrue(com.iemr.tm.data.labModule.LabResultEntry
					.getLabResultEntry(new ArrayList<>()).isEmpty());
		}
	}

	@Nested
	@DisplayName("obstetric and personal history rows")
	class HistoryRowTests {

		@Test
		@DisplayName("getFemaleObstetricHistory should read every recorded pregnancy")
		void getFemaleObstetricHistory_shouldReadRecordedPregnancies() {
			com.iemr.tm.data.anc.WrapperFemaleObstetricHistory history =
					com.iemr.tm.data.anc.WrapperFemaleObstetricHistory.getFemaleObstetricHistory(
							rows(row("LLISSTTTSTSTSTTTTTSTTTTSTTTSTTLITIITTT")));

			assertEquals(1, history.getFemaleObstetricHistoryList().size());
			assertNotNull(history.getBeneficiaryRegID());
		}

		@Test
		@DisplayName("getFemaleObstetricHistory should read a beneficiary with no recorded pregnancy")
		void getFemaleObstetricHistory_shouldReadNoRecordedPregnancy() {
			Object[] values = row("LLISSTTTSTSTSTTTTTSTTTTSTTTSTTLITIITTT");
			values[4] = Short.valueOf((short) 0);

			assertTrue(com.iemr.tm.data.anc.WrapperFemaleObstetricHistory
					.getFemaleObstetricHistory(rows(values)).getFemaleObstetricHistoryList().isEmpty());
		}

		@Test
		@DisplayName("getFemaleObstetricHistory should read an empty result set")
		void getFemaleObstetricHistory_shouldReadEmptyResultSet() {
			assertNotNull(com.iemr.tm.data.anc.WrapperFemaleObstetricHistory
					.getFemaleObstetricHistory(new ArrayList<Object[]>()));
		}

		@Test
		@DisplayName("getPersonalDetails should read the recorded tobacco and alcohol habits")
		void getPersonalDetails_shouldReadTobaccoAndAlcoholHabits() {
			com.iemr.tm.data.anc.BenPersonalHabit habits = com.iemr.tm.data.anc.BenPersonalHabit
					.getPersonalDetails(rows(row("LLITTTTTTSD_TTTTTDCDL"), row("LLITTTTTTSD_TTTTTDCDL")));

			assertNotNull(habits);
			assertFalse(habits.getTobaccoList().isEmpty());
			assertFalse(habits.getAlcoholList().isEmpty());
		}

		@Test
		@DisplayName("getPersonalDetails should read a beneficiary with no recorded habit")
		void getPersonalDetails_shouldReadNoRecordedHabit() {
			org.junit.jupiter.api.Assertions.assertNull(
					com.iemr.tm.data.anc.BenPersonalHabit.getPersonalDetails(new ArrayList<Object[]>()));
		}
	}

	@Nested
	@DisplayName("row builder")
	class RowBuilderTests {

		@Test
		@DisplayName("row should build one value of each supported column type")
		void row_shouldBuildOneValueOfEachType() {
			Object[] values = row("LISTDBA_");

			assertEquals(Long.valueOf(1L), values[0]);
			assertEquals(Integer.valueOf(2), values[1]);
			assertEquals(Short.valueOf((short) 3), values[2]);
			assertEquals("1,2", values[3]);
			assertNotNull(values[4]);
			assertEquals(Boolean.TRUE, values[5]);
			assertNotNull(values[6]);
			org.junit.jupiter.api.Assertions.assertNull(values[7]);
		}

		@Test
		@DisplayName("rows should collect the built rows in order")
		void rows_shouldCollectRowsInOrder() {
			List<Object[]> collected = rows(row("L"), row("I"));

			assertEquals(2, collected.size());
			assertEquals(Long.valueOf(1L), collected.get(0)[0]);
		}
	}

	@Nested
	@DisplayName("beneficiary search and detail rows")
	class BeneficiarySearchRowTests {

		@Test
		@DisplayName("getSearchData should read the matched beneficiaries with their age")
		void getSearchData_shouldReadMatchedBeneficiaries() {
			String result = com.iemr.tm.data.registrar.V_BenAdvanceSearch
					.getSearchData(Collections.singletonList(row("LTTSTATTITITTT")));

			assertTrue(result.contains("years"));
			assertTrue(result.contains("villageName"));
		}

		@Test
		@DisplayName("getSearchData should read a beneficiary aged in months")
		void getSearchData_shouldReadBeneficiaryAgedInMonths() {
			Object[] values = row("LTTSTATTITITTT");
			values[5] = java.sql.Date.valueOf(java.time.LocalDate.now().minusMonths(5));

			assertTrue(com.iemr.tm.data.registrar.V_BenAdvanceSearch
					.getSearchData(Collections.singletonList(values)).contains("months"));
		}

		@Test
		@DisplayName("getSearchData should read a beneficiary aged in days")
		void getSearchData_shouldReadBeneficiaryAgedInDays() {
			Object[] values = row("LTTSTATTITITTT");
			values[5] = java.sql.Date.valueOf(java.time.LocalDate.now().minusDays(11));

			assertTrue(com.iemr.tm.data.registrar.V_BenAdvanceSearch
					.getSearchData(Collections.singletonList(values)).contains("days"));
		}

		@Test
		@DisplayName("getSearchData should read a beneficiary with no date of birth")
		void getSearchData_shouldReadBeneficiaryWithoutDob() {
			Object[] values = row("LTTSTATTITITTT");
			values[5] = null;

			assertNotNull(com.iemr.tm.data.registrar.V_BenAdvanceSearch
					.getSearchData(Collections.singletonList(values)));
		}

		@Test
		@DisplayName("getSearchData should render an empty list for no match")
		void getSearchData_shouldRenderEmptyListForNoMatch() {
			assertEquals("[]", com.iemr.tm.data.registrar.V_BenAdvanceSearch
					.getSearchData(new ArrayList<Object[]>()));
		}

		/** The registration row the beneficiary details screen is built from. */
		private Object[] detailRow() {
			Object[] values = row("LTTTSASTSSSITITISTTITITTSTBATTTTTTT");
			values[5] = java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(31));
			values[27] = java.sql.Date.valueOf(java.time.LocalDate.now().minusYears(9));
			return values;
		}

		@Test
		@DisplayName("getBeneficiaryDetails should read the registration row and work out the age at marriage")
		void getBeneficiaryDetails_shouldReadRowAndAgeAtMarriage() {
			com.iemr.tm.data.registrar.FetchBeneficiaryDetails details =
					com.iemr.tm.data.registrar.FetchBeneficiaryDetails.getBeneficiaryDetails(detailRow(),
							new ArrayList<>(), "Yes", new ArrayList<>());

			assertNotNull(details);
			assertNotNull(details.getBeneficiaryRegID());
		}

		@Test
		@DisplayName("getBeneficiaryDetails should leave the age at marriage out when no marriage date is stored")
		void getBeneficiaryDetails_shouldLeaveAgeAtMarriageOut() {
			Object[] values = detailRow();
			values[27] = null;

			assertNotNull(com.iemr.tm.data.registrar.FetchBeneficiaryDetails.getBeneficiaryDetails(values,
					new ArrayList<>(), "No", new ArrayList<>()));
		}

		@Test
		@DisplayName("getFetchBeneficiaryDetailsObj should answer with nothing")
		void getFetchBeneficiaryDetailsObj_shouldAnswerWithNothing() {
			org.junit.jupiter.api.Assertions.assertNull(com.iemr.tm.data.registrar.FetchBeneficiaryDetails
					.getFetchBeneficiaryDetailsObj(row("L"), new ArrayList<>()));
		}

		@Test
		@DisplayName("getBeneficiaryData should read the beneficiary rows")
		void getBeneficiaryData_shouldReadRows() {
			ArrayList<com.iemr.tm.data.registrar.BeneficiaryData> beneficiaries =
					com.iemr.tm.data.registrar.BeneficiaryData
							.getBeneficiaryData(Collections.singletonList(row("LTTASDTT")));

			assertEquals(1, beneficiaries.size());
			assertNotNull(beneficiaries.get(0));
		}

		@Test
		@DisplayName("getBeneficiaryPersonalData should read the beneficiary rows without the extra columns")
		void getBeneficiaryPersonalData_shouldReadRows() {
			assertEquals(1, com.iemr.tm.data.registrar.BeneficiaryData
					.getBeneficiaryPersonalData(Collections.singletonList(row("LTTASD"))).size());
		}
	}

	@Nested
	@DisplayName("history and master rows")
	class HistoryAndMasterRowTests {

		@Test
		@DisplayName("getBenFamilyHistory should group the recorded family diseases")
		void getBenFamilyHistory_shouldGroupRecordedDiseases() {
			com.iemr.tm.data.anc.BenFamilyHistory history = com.iemr.tm.data.anc.BenFamilyHistory
					.getBenFamilyHistory(rows(row("LLITSTTBTBLTT"), row("LLITSTTBTBLTT")));

			assertNotNull(history);
			assertEquals(2, history.getFamilyDiseaseList().size());
			assertTrue(history.getFamilyDiseaseList().get(0).containsKey("familyMembers"));
		}

		@Test
		@DisplayName("getBenFamilyHistory should read no recorded family disease")
		void getBenFamilyHistory_shouldReadNoRecordedDisease() {
			org.junit.jupiter.api.Assertions.assertNull(com.iemr.tm.data.anc.BenFamilyHistory
					.getBenFamilyHistory(new ArrayList<Object[]>()));
		}

		@Test
		@DisplayName("getBenFamilyHist should group the recorded family diseases with their row ids")
		void getBenFamilyHist_shouldGroupRecordedDiseasesWithIds() {
			com.iemr.tm.data.anc.BenFamilyHistory history = com.iemr.tm.data.anc.BenFamilyHistory
					.getBenFamilyHist(rows(row("LLLITSTTBTBLTT"), row("LLLITSTTBTBLTT")));

			assertNotNull(history);
			assertEquals(2, history.getFamilyDiseaseList().size());
			assertEquals(Boolean.FALSE, history.getFamilyDiseaseList().get(0).get("deleted"));
		}

		@Test
		@DisplayName("getBenFamilyHist should read no recorded family disease")
		void getBenFamilyHist_shouldReadNoRecordedDisease() {
			org.junit.jupiter.api.Assertions.assertNull(com.iemr.tm.data.anc.BenFamilyHistory
					.getBenFamilyHist(new ArrayList<Object[]>()));
		}

		@Test
		@DisplayName("getBenAllergicHistory should expand the recorded reaction types")
		void getBenAllergicHistory_shouldExpandReactionTypes() {
			ArrayList<com.iemr.tm.data.anc.BenAllergyHistory> allergies =
					com.iemr.tm.data.anc.BenAllergyHistory
							.getBenAllergicHistory(rows(row("LLITTTTTTTLTT")));

			assertEquals(1, allergies.size());
			assertFalse(allergies.get(0).getTypeOfAllergicReactions().isEmpty());
		}

		@Test
		@DisplayName("getBenAllergicHistory should read no recorded allergy")
		void getBenAllergicHistory_shouldReadNoRecordedAllergy() {
			assertTrue(com.iemr.tm.data.anc.BenAllergyHistory
					.getBenAllergicHistory(new ArrayList<Object[]>()).isEmpty());
		}

		@Test
		@DisplayName("getBenChildDevelopmentDetails should split the recorded milestones")
		void getChildDevelopmentDetails_shouldSplitRecordedMilestones() {
			com.iemr.tm.data.anc.BenChildDevelopmentHistory history =
					com.iemr.tm.data.anc.BenChildDevelopmentHistory
							.getBenChildDevelopmentDetails(rows(row("LLITBTBTBTBTL")));

			assertNotNull(history);
			assertEquals(2, history.getGrossMotorMilestones().size());
			assertEquals(2, history.getFineMotorMilestones().size());
			assertEquals(2, history.getSocialMilestones().size());
			assertEquals(2, history.getLanguageMilestones().size());
			assertEquals(2, history.getDevelopmentProblems().size());
		}

		@Test
		@DisplayName("getBenChildDevelopmentDetails should read no recorded development history")
		void getChildDevelopmentDetails_shouldReadNoRecordedHistory() {
			org.junit.jupiter.api.Assertions.assertNull(com.iemr.tm.data.anc.BenChildDevelopmentHistory
					.getBenChildDevelopmentDetails(new ArrayList<Object[]>()));
		}

		@Test
		@DisplayName("getDevelopmentHistory should flatten the picked milestones for storage")
		void getDevelopmentHistory_shouldFlattenPickedMilestones() {
			com.iemr.tm.data.anc.BenChildDevelopmentHistory history =
					new com.iemr.tm.data.anc.BenChildDevelopmentHistory();
			history.setGrossMotorMilestones(Arrays.asList("Sits", "Stands"));
			history.setFineMotorMilestones(Arrays.asList("Grasps"));
			history.setSocialMilestones(Arrays.asList("Smiles"));
			history.setLanguageMilestones(Arrays.asList("Babbles"));
			history.setDevelopmentProblems(Arrays.asList("None"));

			com.iemr.tm.data.anc.BenChildDevelopmentHistory flattened =
					com.iemr.tm.data.anc.BenChildDevelopmentHistory.getDevelopmentHistory(history);

			assertEquals("Sits,Stands,", flattened.getGrossMotorMilestone());
			assertEquals("Grasps,", flattened.getFineMotorMilestone());
			assertEquals("Smiles,", flattened.getSocialMilestone());
			assertEquals("Babbles,", flattened.getLanguageMilestone());
		}

		@Test
		@DisplayName("getDevelopmentHistory should flatten an empty pick into empty text")
		void getDevelopmentHistory_shouldFlattenEmptyPick() {
			assertEquals("", com.iemr.tm.data.anc.BenChildDevelopmentHistory
					.getDevelopmentHistory(new com.iemr.tm.data.anc.BenChildDevelopmentHistory())
					.getGrossMotorMilestone());
		}

		@org.junit.jupiter.params.ParameterizedTest
		@org.junit.jupiter.params.provider.CsvSource({ "1", "2", "3", "4" })
		@DisplayName("getComplicationTypes should read the master rows for each complication master")
		void getComplicationTypes_shouldReadEachMaster(int masterType) {
			assertEquals(1, com.iemr.tm.data.masterdata.anc.ComplicationTypes
					.getComplicationTypes(rows(row("ST")), masterType).size());
		}

		@Test
		@DisplayName("getANCWomenVaccineDetails should read all three tetanus doses")
		void getAncWomenVaccineDetails_shouldReadAllThreeDoses() {
			com.iemr.tm.data.anc.WrapperAncImmunization immunization =
					com.iemr.tm.data.anc.ANCWomenVaccineDetail.getANCWomenVaccineDetails(
							rows(vaccineRow(1L), vaccineRow(2L), vaccineRow(3L)));

			assertNotNull(immunization.getDateReceivedForTT_1());
			assertNotNull(immunization.getDateReceivedForTT_2());
			assertNotNull(immunization.getDateReceivedForTT_3());
			assertNotNull(immunization.getFacilityNameOfTT_1());
		}

		private Object[] vaccineRow(Long id) {
			Object[] values = row("LLLI_TATL");
			values[0] = id;
			values[6] = java.sql.Date.valueOf(java.time.LocalDate.now());
			return values;
		}

		@Test
		@DisplayName("getANCWomenVaccineDetails should read a dose with no received date")
		void getAncWomenVaccineDetails_shouldReadDoseWithoutDate() {
			Object[] values = vaccineRow(1L);
			values[6] = null;

			assertNotNull(com.iemr.tm.data.anc.ANCWomenVaccineDetail
					.getANCWomenVaccineDetails(rows(values)));
		}

		@Test
		@DisplayName("getANCWomenVaccineDetails should read no recorded dose")
		void getAncWomenVaccineDetails_shouldReadNoRecordedDose() {
			assertNotNull(com.iemr.tm.data.anc.ANCWomenVaccineDetail
					.getANCWomenVaccineDetails(new ArrayList<Object[]>()));
		}
	}
}
