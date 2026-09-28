/*
* AMRIT - Accessible Medical Records via Integrated Technologies
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
package com.iemr.tm.utils;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.iemr.tm.data.login.Users;
import com.iemr.tm.repo.login.UserLoginRepo;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationUtil Test Suite")
class JwtAuthenticationUtilTest {

    private static final String JWT_TOKEN = "a.jwt.token";
    private static final String USER_ID = "42";
    private static final String REDIS_KEY = "user_42";

    private final CookieUtil cookieUtil = new CookieUtil();

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private UserLoginRepo userLoginRepo;

    @Mock
    private Claims claims;

    private org.springframework.mock.web.MockHttpServletRequest request;

    private JwtAuthenticationUtil jwtAuthenticationUtil;

    @BeforeEach
    @DisplayName("Wire the util with mocked cookie, JWT, Redis and repository collaborators")
    void setUp() {
        jwtAuthenticationUtil = new JwtAuthenticationUtil(cookieUtil, jwtUtil);
        ReflectionTestUtils.setField(jwtAuthenticationUtil, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(jwtAuthenticationUtil, "userLoginRepo", userLoginRepo);
        request = new org.springframework.mock.web.MockHttpServletRequest();
    }

    private Users user(long id, String name) {
        Users user = new Users();
        user.setUserID(id);
        user.setUserName(name);
        return user;
    }

    @Nested
    @DisplayName("validateJwtToken from the request cookie")
    class ValidateJwtTokenTests {

        @Test
        @DisplayName("validateJwtToken should return 401 when the Jwttoken cookie is absent")
        void validateJwtToken_shouldReturnUnauthorizedWhenCookieMissing() {
            // no Jwttoken cookie set on the request

            ResponseEntity<String> result = jwtAuthenticationUtil.validateJwtToken(request);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
            assertEquals("Error 401: Unauthorized - JWT Token is not set!", result.getBody());
            verify(jwtUtil, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("validateJwtToken should return 401 when the token cannot be validated")
        void validateJwtToken_shouldReturnUnauthorizedWhenTokenInvalid() {
            request.setCookies(new Cookie("Jwttoken", JWT_TOKEN));
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(null);

            ResponseEntity<String> result = jwtAuthenticationUtil.validateJwtToken(request);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
            assertEquals("Error 401: Unauthorized - Invalid JWT Token!", result.getBody());
        }

        @Test
        @DisplayName("validateJwtToken should return 401 when the token carries no subject")
        void validateJwtToken_shouldReturnUnauthorizedWhenSubjectMissing() {
            request.setCookies(new Cookie("Jwttoken", JWT_TOKEN));
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.getSubject()).thenReturn(null);

            ResponseEntity<String> result = jwtAuthenticationUtil.validateJwtToken(request);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
            assertEquals("Error 401: Unauthorized - Username is missing!", result.getBody());
        }

        @Test
        @DisplayName("validateJwtToken should return 401 when the subject is blank")
        void validateJwtToken_shouldReturnUnauthorizedWhenSubjectIsBlank() {
            request.setCookies(new Cookie("Jwttoken", JWT_TOKEN));
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.getSubject()).thenReturn("");

            ResponseEntity<String> result = jwtAuthenticationUtil.validateJwtToken(request);

            assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        }

        @Test
        @DisplayName("validateJwtToken should return 200 with the username for a valid token")
        void validateJwtToken_shouldReturnUsernameForValidToken() {
            request.setCookies(new Cookie("Jwttoken", JWT_TOKEN));
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.getSubject()).thenReturn("amrit-user");

            ResponseEntity<String> result = jwtAuthenticationUtil.validateJwtToken(request);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("amrit-user", result.getBody());
        }
    }

    @Nested
    @DisplayName("validateUserIdAndJwtToken")
    class ValidateUserIdAndJwtTokenTests {

        @Test
        @DisplayName("validateUserIdAndJwtToken should accept a token whose user is already cached in Redis")
        void validateUserIdAndJwtToken_shouldAcceptUserFromRedisCache() throws Exception {
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.get("userId", String.class)).thenReturn(USER_ID);
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(REDIS_KEY)).thenReturn(user(42L, "amrit-user"));

            assertTrue(jwtAuthenticationUtil.validateUserIdAndJwtToken(JWT_TOKEN));
            verify(userLoginRepo, never()).getUserByUserID(anyLong());
        }

        @Test
        @DisplayName("validateUserIdAndJwtToken should fall back to the database and cache the user on a Redis miss")
        void validateUserIdAndJwtToken_shouldFallBackToDatabaseAndCacheUser() throws Exception {
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.get("userId", String.class)).thenReturn(USER_ID);
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(REDIS_KEY)).thenReturn(null);
            when(userLoginRepo.getUserByUserID(42L)).thenReturn(user(42L, "amrit-user"));

            assertTrue(jwtAuthenticationUtil.validateUserIdAndJwtToken(JWT_TOKEN));
            verify(valueOperations).set(eq(REDIS_KEY), any(Users.class), eq(30L), eq(TimeUnit.MINUTES));
        }

        @Test
        @DisplayName("validateUserIdAndJwtToken should reject a token that cannot be validated")
        void validateUserIdAndJwtToken_shouldRejectInvalidToken() {
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(null);

            Exception thrown = assertThrows(Exception.class,
                    () -> jwtAuthenticationUtil.validateUserIdAndJwtToken(JWT_TOKEN));

            assertTrue(thrown.getMessage().contains("Invalid JWT token."));
        }

        @Test
        @DisplayName("validateUserIdAndJwtToken should reject when the user exists in neither Redis nor the database")
        void validateUserIdAndJwtToken_shouldRejectUnknownUser() {
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.get("userId", String.class)).thenReturn(USER_ID);
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(REDIS_KEY)).thenReturn(null);
            when(userLoginRepo.getUserByUserID(42L)).thenReturn(null);

            Exception thrown = assertThrows(Exception.class,
                    () -> jwtAuthenticationUtil.validateUserIdAndJwtToken(JWT_TOKEN));

            assertTrue(thrown.getMessage().contains("Invalid User ID."));
        }

        @Test
        @DisplayName("validateUserIdAndJwtToken should reject a non-numeric userId claim")
        void validateUserIdAndJwtToken_shouldRejectNonNumericUserId() {
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.get("userId", String.class)).thenReturn("not-a-number");
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            lenient().when(valueOperations.get("user_not-a-number")).thenReturn(null);

            Exception thrown = assertThrows(Exception.class,
                    () -> jwtAuthenticationUtil.validateUserIdAndJwtToken(JWT_TOKEN));

            assertTrue(thrown.getMessage().startsWith("Validation error: "));
        }

        @Test
        @DisplayName("validateUserIdAndJwtToken should wrap a Redis outage as a validation error")
        void validateUserIdAndJwtToken_shouldWrapRedisOutage() {
            when(jwtUtil.validateToken(JWT_TOKEN)).thenReturn(claims);
            when(claims.get("userId", String.class)).thenReturn(USER_ID);
            when(redisTemplate.opsForValue()).thenThrow(new IllegalStateException("redis down"));

            Exception thrown = assertThrows(Exception.class,
                    () -> jwtAuthenticationUtil.validateUserIdAndJwtToken(JWT_TOKEN));

            assertTrue(thrown.getMessage().contains("redis down"));
        }
    }

    @Nested
    @DisplayName("getUserRoles")
    class GetUserRolesTests {

        @Test
        @DisplayName("getUserRoles should return the roles mapped to the user")
        void getUserRoles_shouldReturnRolesForUser() throws Exception {
            when(userLoginRepo.getRoleNamebyUserId(42L)).thenReturn(java.util.List.of("Nurse", "Doctor"));

            assertEquals(java.util.List.of("Nurse", "Doctor"), jwtAuthenticationUtil.getUserRoles(42L));
        }

        @Test
        @DisplayName("getUserRoles should reject a null userId")
        void getUserRoles_shouldRejectNullUserId() {
            Exception thrown = assertThrows(Exception.class, () -> jwtAuthenticationUtil.getUserRoles(null));

            assertTrue(thrown.getMessage().contains("Invalid User ID"));
        }

        @Test
        @DisplayName("getUserRoles should reject a non-positive userId")
        void getUserRoles_shouldRejectNonPositiveUserId() {
            Exception thrown = assertThrows(Exception.class, () -> jwtAuthenticationUtil.getUserRoles(0L));

            assertTrue(thrown.getMessage().contains("Invalid User ID"));
        }

        @Test
        @DisplayName("getUserRoles should fail when the user has no role mapped")
        void getUserRoles_shouldFailWhenNoRoleFound() {
            when(userLoginRepo.getRoleNamebyUserId(42L)).thenReturn(java.util.Collections.emptyList());

            Exception thrown = assertThrows(Exception.class, () -> jwtAuthenticationUtil.getUserRoles(42L));

            assertTrue(thrown.getMessage().contains("Failed to retrieverole"));
        }

        @Test
        @DisplayName("getUserRoles should wrap a repository failure")
        void getUserRoles_shouldWrapRepositoryFailure() {
            when(userLoginRepo.getRoleNamebyUserId(42L)).thenThrow(new IllegalStateException("db down"));

            Exception thrown = assertThrows(Exception.class, () -> jwtAuthenticationUtil.getUserRoles(42L));

            assertTrue(thrown.getMessage().contains("db down"));
        }
    }
}
