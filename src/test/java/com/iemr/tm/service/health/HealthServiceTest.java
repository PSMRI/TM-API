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
package com.iemr.tm.service.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("HealthService Test Suite")
class HealthServiceTest {

	@Mock
	private DataSource dataSource;
	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement statement;
	@Mock
	private ResultSet resultSet;

	private HealthService service;

	@BeforeEach
	@DisplayName("Wire a service over a mocked datasource and Redis template")
	void setUp() throws Exception {
		when(dataSource.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(any(String.class))).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(0);
		service = new HealthService(dataSource, redisTemplate);
	}

	@AfterEach
	@DisplayName("Shut the health check executor down after each test")
	void tearDown() {
		service.shutdown();
	}

	@SuppressWarnings("unchecked")
	private void redisReplies(String reply) {
		when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(reply);
	}

	@Nested
	@DisplayName("checkHealth")
	class CheckHealthTests {

		@Test
		@DisplayName("checkHealth should report both components up when MySQL and Redis answer")
		void checkHealth_shouldReportBothComponentsUp() {
			redisReplies("PONG");

			Map<String, Object> result = service.checkHealth();

			assertEquals("UP", result.get("status"));
			assertNotNull(result.get("timestamp"));
			assertNotNull(result.get("components"));
		}

		@Test
		@DisplayName("checkHealth should report Redis down when the ping is not answered")
		void checkHealth_shouldReportRedisDownWhenPingUnanswered() {
			redisReplies("NOPE");

			assertEquals("DOWN", service.checkHealth().get("status"));
		}

		@Test
		@DisplayName("checkHealth should report Redis down when the ping fails")
		void checkHealth_shouldReportRedisDownWhenPingFails() {
			when(redisTemplate.execute(any(RedisCallback.class))).thenThrow(new IllegalStateException("redis down"));

			assertEquals("DOWN", service.checkHealth().get("status"));
		}

		@Test
		@DisplayName("checkHealth should report MySQL down when the connection fails")
		void checkHealth_shouldReportMysqlDownWhenConnectionFails() throws Exception {
			when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));
			redisReplies("PONG");

			Map<String, Object> result = service.checkHealth();

			assertEquals("DOWN", result.get("status"));
		}

		@Test
		@DisplayName("checkHealth should report MySQL down when the probe query returns no row")
		void checkHealth_shouldReportMysqlDownWithoutProbeRow() throws Exception {
			when(resultSet.next()).thenReturn(false);
			redisReplies("PONG");

			assertEquals("DOWN", service.checkHealth().get("status"));
		}

		@Test
		@DisplayName("checkHealth should skip Redis when no Redis template is configured")
		void checkHealth_shouldSkipRedisWhenNotConfigured() {
			HealthService withoutRedis = new HealthService(dataSource, null);
			try {
				Map<String, Object> result = withoutRedis.checkHealth();

				assertEquals("UP", result.get("status"));
			} finally {
				withoutRedis.shutdown();
			}
		}

		@Test
		@DisplayName("checkHealth should degrade MySQL when the pool reports lock waits")
		void checkHealth_shouldDegradeMysqlOnLockWaits() throws Exception {
			when(resultSet.getInt(1)).thenReturn(3);
			redisReplies("PONG");

			assertEquals("DEGRADED", service.checkHealth().get("status"));
		}

		@Test
		@DisplayName("checkHealth should degrade MySQL when the pool reports slow queries")
		void checkHealth_shouldDegradeMysqlOnSlowQueries() throws Exception {
			when(resultSet.getInt(1)).thenReturn(0, 5);
			redisReplies("PONG");

			assertNotNull(service.checkHealth().get("status"));
		}

		@Test
		@DisplayName("checkHealth should still answer when the advanced diagnostics fail")
		void checkHealth_shouldStillAnswerWhenDiagnosticsFail() throws Exception {
			when(statement.executeQuery()).thenReturn(resultSet).thenThrow(new SQLException("diagnostics failed"));
			redisReplies("PONG");

			assertNotNull(service.checkHealth().get("status"));
		}

		@Test
		@DisplayName("checkHealth should reuse the throttled diagnostics result on a second call")
		void checkHealth_shouldReuseThrottledDiagnostics() {
			redisReplies("PONG");

			service.checkHealth();

			assertEquals("UP", service.checkHealth().get("status"));
		}
	}

	@Nested
	@DisplayName("shutdown")
	class ShutdownTests {

		@Test
		@DisplayName("shutdown should stop the executor so later checks report the components down")
		void shutdown_shouldStopExecutor() {
			service.shutdown();

			Map<String, Object> result = service.checkHealth();

			assertEquals("DOWN", result.get("status"));
		}

		@Test
		@DisplayName("shutdown should be safe to call twice")
		void shutdown_shouldBeSafeToCallTwice() {
			service.shutdown();
			service.shutdown();

			assertTrue(true);
		}
	}
}
