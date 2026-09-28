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
*/package com.iemr.tm.annotation.sqlInjectionSafe;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SQLInjectionSafeConstraintValidator Test Suite")
class SQLInjectionSafeConstraintValidatorTest {

	private SQLInjectionSafeConstraintValidator validator;

	@BeforeEach
	@DisplayName("Create the validator before each test")
	void setUp() {
		validator = new SQLInjectionSafeConstraintValidator();
		validator.initialize(null);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("isValid should accept a value that was left out")
	void isValid_shouldAcceptOmittedValue(String value) {
		assertTrue(validator.isValid(value, null));
	}

	@ParameterizedTest
	@ValueSource(strings = { "Asha Devi", "9999999999", "Kamptee, Nagpur", "fever since 2 days", "B+", "Bearer token" })
	@DisplayName("isValid should accept the values the API is given in practice")
	void isValid_shouldAcceptOrdinaryValues(String value) {
		assertTrue(validator.isValid(value, null));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"SELECT * FROM m_beneficiary",
			"INSERT INTO m_beneficiary VALUES (1)",
			"UPDATE m_beneficiary SET deleted = true",
			"DELETE FROM m_beneficiary",
			"UPSERT m_beneficiary",
			"SAVEPOINT before_drop",
			"CALL sp_drop_all",
			"ROLLBACK to_savepoint",
			"KILL 1",
			"DROP m_beneficiary",
			"CREATE TABLE evil",
			"ALTER TABLE m_beneficiary",
			"TRUNCATE TABLE m_beneficiary",
			"LOCK TABLE m_beneficiary",
			"UNLOCK TABLE m_beneficiary",
			"RELEASE SAVEPOINT before_drop",
			"DESC m_beneficiary",
			"DESCRIBE m_beneficiary",
			"Asha; DROP",
			"Asha /* comment",
			"Asha -- comment" })
	@DisplayName("isValid should reject a value carrying SQL")
	void isValid_shouldRejectValueCarryingSql(String value) {
		assertFalse(validator.isValid(value, null));
	}

	@Test
	@DisplayName("SQL_TYPES should name the object types the validator guards")
	void sqlTypes_shouldNameGuardedObjectTypes() {
		assertTrue(SQLInjectionSafeConstraintValidator.SQL_TYPES.contains("TABLE"));
		assertTrue(SQLInjectionSafeConstraintValidator.SQL_TYPES.contains("PROCEDURE"));
	}
}
