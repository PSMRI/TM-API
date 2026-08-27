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
package com.iemr.tm.utils.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("AMRIT exception types Test Suite")
class ExceptionsTest {

    private static final String MESSAGE = "Invalid session key";

    private RuntimeException causeWithStackTrace() {
        RuntimeException cause = new RuntimeException("root cause");
        cause.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("com.iemr.Origin", "failingMethod", "Origin.java", 42) });
        return cause;
    }

    @Nested
    @DisplayName("IEMRException")
    class IEMRExceptionTests {

        @Test
        @DisplayName("the message constructor should expose the message through both accessors")
        void messageConstructor_shouldExposeMessage() {
            IEMRException exception = new IEMRException(MESSAGE);

            assertEquals(MESSAGE, exception.getMessage());
            assertEquals(MESSAGE, exception.toString());
        }

        @Test
        @DisplayName("the cause constructor should adopt the stack trace of the cause")
        void causeConstructor_shouldAdoptCauseStackTrace() {
            RuntimeException cause = causeWithStackTrace();

            IEMRException exception = new IEMRException(MESSAGE, cause);

            assertEquals(MESSAGE, exception.getMessage());
            assertArrayEquals(cause.getStackTrace(), exception.getStackTrace());
        }

        @Test
        @DisplayName("the cause constructor should not chain the cause itself")
        void causeConstructor_shouldNotChainCause() {
            IEMRException exception = new IEMRException(MESSAGE, causeWithStackTrace());

            assertNull(exception.getCause(),
                    "only the stack trace is adopted; the cause is deliberately not chained");
        }

        @Test
        @DisplayName("toString should return null when constructed with a null message")
        void toString_shouldReturnNullForNullMessage() {
            assertNull(new IEMRException(null).toString());
        }
    }

    @Nested
    @DisplayName("TMException")
    class TMExceptionTests {

        @Test
        @DisplayName("the message constructor should expose the message through both accessors")
        void messageConstructor_shouldExposeMessage() {
            TMException exception = new TMException(MESSAGE);

            assertEquals(MESSAGE, exception.getMessage());
            assertEquals(MESSAGE, exception.toString());
        }

        @Test
        @DisplayName("the cause constructor should adopt the stack trace of the cause")
        void causeConstructor_shouldAdoptCauseStackTrace() {
            RuntimeException cause = causeWithStackTrace();

            TMException exception = new TMException(MESSAGE, cause);

            assertEquals(MESSAGE, exception.getMessage());
            assertArrayEquals(cause.getStackTrace(), exception.getStackTrace());
        }

        @Test
        @DisplayName("the cause constructor should not chain the cause itself")
        void causeConstructor_shouldNotChainCause() {
            assertNull(new TMException(MESSAGE, causeWithStackTrace()).getCause());
        }
    }

    @Nested
    @DisplayName("VideoConsultationException")
    class VideoConsultationExceptionTests {

        @Test
        @DisplayName("the message constructor should expose the message through both accessors")
        void messageConstructor_shouldExposeMessage() {
            VideoConsultationException exception = new VideoConsultationException(MESSAGE);

            assertEquals(MESSAGE, exception.getMessage());
            assertEquals(MESSAGE, exception.toString());
        }

        @Test
        @DisplayName("the cause constructor should adopt the stack trace of the cause")
        void causeConstructor_shouldAdoptCauseStackTrace() {
            RuntimeException cause = causeWithStackTrace();

            VideoConsultationException exception = new VideoConsultationException(MESSAGE, cause);

            assertEquals(MESSAGE, exception.getMessage());
            assertArrayEquals(cause.getStackTrace(), exception.getStackTrace());
        }

        @Test
        @DisplayName("the cause constructor should not chain the cause itself")
        void causeConstructor_shouldNotChainCause() {
            assertNull(new VideoConsultationException(MESSAGE, causeWithStackTrace()).getCause());
        }
    }
}
