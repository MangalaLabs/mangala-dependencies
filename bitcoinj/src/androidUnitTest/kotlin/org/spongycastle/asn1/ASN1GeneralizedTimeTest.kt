/*
 * Copyright (c) 2000-2021 The Legion of the Bouncy Castle Inc. (https://www.bouncycastle.org)
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Modified from original source: https://github.com/bcgit/bc-java
 */

package org.spongycastle.asn1

import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.junit.Test

class ASN1GeneralizedTimeTest {

    @Test
    fun getDateZuluTimeWithoutFractionalSecond() {
        val actual = ASN1GeneralizedTime("20241002100000Z".toByteArray()).date.toEpochMilliseconds()
        val expected = 1727863200000

        assertEquals(expected, actual)
    }

    @Test
    fun getDateZuluTimeWithFractionalSecond() {
        val actual = ASN1GeneralizedTime("20241002100000.123Z".toByteArray()).date.toEpochMilliseconds()
        val expected = 1727863200123

        assertEquals(expected, actual)
    }

    @Test
    fun getDateWithTimeZoneFractionalSecond() {
        val actual = ASN1GeneralizedTime("20241002100000+0100".toByteArray()).date.toEpochMilliseconds()
        val expected = 1727859600000

        assertEquals(expected, actual)
    }

    @Test
    fun getDateWithTimeZoneAndFractionalSecond() {
        val actual = ASN1GeneralizedTime("20241002100000.123+0100".toByteArray()).date.toEpochMilliseconds()
        val expected = 1727859600123

        assertEquals(expected, actual)
    }

    @Test
    fun getDateWithoutTimeZoneFractionalSecond() {
        val actual = ASN1GeneralizedTime("20241002100000".toByteArray()).date.toEpochMilliseconds()
        val expected = 1727863200000

        assertEquals(expected, actual)
    }

    @Test
    fun getDateWithoutTimeZoneAndFractionalSecond() {
        val actual = ASN1GeneralizedTime("20241002100000.123".toByteArray()).date.toEpochMilliseconds()
        val expected = 1727863200123

        assertEquals(expected, actual)
    }

    @Test
    fun calculateGMTOffset() {
        val actual = ASN1GeneralizedTime("20241002100000Z".toByteArray()).calculateGMTOffset()
        val expected = "GMT+07:00"

        assertEquals(expected, actual)
    }

    @Test
    fun calculateGMTOffsetWithDaylightSavingsPlus() {
        // true, false
        // offset 0 hours 0 minutes 0
        val expectedDateTimestamp = 1730109600000
        val time = ASN1GeneralizedTime("20241028100000Z".toByteArray())
        val actualDateTimestamp = time.date.toEpochMilliseconds()
        val actual = time.calculateGMTOffset(
            TimeZone.of("Europe/London")
        )
        val expected = "GMT+00:00"

        assertEquals(expectedDateTimestamp, actualDateTimestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun calculateGMTOffsetWithDaylightSavingsPlusEnded() {
        // true, true
        // offset 0 hours 1 minutes 0
        val expectedDateTimestamp = 1727776800000
        val time = ASN1GeneralizedTime("20241001100000Z".toByteArray())
        val actualDateTimestamp = time.date.toEpochMilliseconds()
        val actual = time.calculateGMTOffset(TimeZone.of("Europe/London"))
        val expected = "GMT+01:00"

        assertEquals(expectedDateTimestamp, actualDateTimestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun calculateGMTOffsetWithDaylightSavingsMinus() {
        // true, true
        // offset 18000000 hours 4 minutes 0
        val expectedDateTimestamp = 1710151200000
        val time = ASN1GeneralizedTime("20240311100000Z".toByteArray())
        val actualDateTimestamp = time.date.toEpochMilliseconds()
        val actual = time.calculateGMTOffset(TimeZone.of("Canada/Eastern"))
        val expected = "GMT-04:00"

        assertEquals(expectedDateTimestamp, actualDateTimestamp)
        assertEquals(expected, actual)
    }

    @Test
    fun calculateGMTOffsetWithDaylightSavingsMinusEnded() {
        // true, false
        // offset 18000000 hours 5 minutes 0
        val expectedDateTimestamp = 1709978400000
        val time = ASN1GeneralizedTime("20240309100000Z".toByteArray())
        val actualDateTimestamp = time.date.toEpochMilliseconds()
        val actual = time.calculateGMTOffset(TimeZone.of("Canada/Eastern"))
        val expected = "GMT-05:00"

        assertEquals(expectedDateTimestamp, actualDateTimestamp)
        assertEquals(expected, actual)
    }
}
