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

package org.spongycastle.util.encoders

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class HexTest {
    @Test
    fun testDecodeString() {
        val expected = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -4)

        val actual = Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testToHexString() {
        val input = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -4)
        val expected = "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC"

        val actual = Hex.toHexString(input)

        assertTrue(expected.equals(actual, ignoreCase = true))
    }

    @Test
    fun testDecodeString2() {
        val expected = byteArrayOf(-60, -99, 54, 8, -122, -25, 4, -109, 106, 102, 120, -31, 19, -99, 38, -73, -127, -97, 126, -112)

        val actual = Hex.decode("C49D360886E704936A6678E1139D26B7819F7E90")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testToHexString2() {
        val input = byteArrayOf(-60, -99, 54, 8, -122, -25, 4, -109, 106, 102, 120, -31, 19, -99, 38, -73, -127, -97, 126, -112)
        val expected = "C49D360886E704936A6678E1139D26B7819F7E90"

        val actual = Hex.toHexString(input)

        assertTrue(expected.equals(actual, ignoreCase = true))
    }

    @Test
    fun testDecodeString3() {
        val expected = byteArrayOf(42, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -104, 73, 20, -95, 68)

        val actual = Hex.decode("2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA984914A144")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testDecodeString4() {
        val expected = byteArrayOf(123, 66, 94, -48, -105, -76, 37, -19, 9, 123, 66, 94, -48, -105, -76, 37, -19, 9, 123, 66, 94, -48, -105, -76, 38, 11, 94, -100, 119, 16, -56, 100)

        val actual = Hex.decode("7B425ED097B425ED097B425ED097B425ED097B425ED097B4260B5E9C7710C864")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testDecodeString5() {
        val expected = byteArrayOf(16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, -34, -7, -34, -94, -9, -100, -42, 88, 18, 99, 26, 92, -11, -45, -19)

        val actual = Hex.decode("1000000000000000000000000000000014DEF9DEA2F79CD65812631A5CF5D3ED")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testDecodeString6() {
        val expected = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -4)

        val actual = Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testDecodeString7() {
        val expected = byteArrayOf(40, -23, -6, -98, -99, -97, 94, 52, 77, 90, -98, 75, -49, 101, 9, -89, -13, -105, -119, -11, 21, -85, -113, -110, -35, -68, -67, 65, 77, -108, 14, -109)

        val actual = Hex.decode("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testDecodeString8() {
        val expected = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 114, 3, -33, 107, 33, -58, 5, 43, 83, -69, -12, 9, 57, -43, 65, 35)

        val actual = Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123")

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode() {
        val input = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -4)
        val expected = byteArrayOf(102, 102, 102, 102, 102, 102, 102, 101, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 48, 48, 48, 48, 48, 48, 48, 48, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 99)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode2() {
        val input = byteArrayOf(-60, -99, 54, 8, -122, -25, 4, -109, 106, 102, 120, -31, 19, -99, 38, -73, -127, -97, 126, -112)
        val expected = byteArrayOf(99, 52, 57, 100, 51, 54, 48, 56, 56, 54, 101, 55, 48, 52, 57, 51, 54, 97, 54, 54, 55, 56, 101, 49, 49, 51, 57, 100, 50, 54, 98, 55, 56, 49, 57, 102, 55, 101, 57, 48)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode3() {
        val input = byteArrayOf(42, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -104, 73, 20, -95, 68)
        val expected = byteArrayOf(50, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 57, 56, 52, 57, 49, 52, 97, 49, 52, 52)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode4() {
        val input = byteArrayOf(123, 66, 94, -48, -105, -76, 37, -19, 9, 123, 66, 94, -48, -105, -76, 37, -19, 9, 123, 66, 94, -48, -105, -76, 38, 11, 94, -100, 119, 16, -56, 100)
        val expected = byteArrayOf(55, 98, 52, 50, 53, 101, 100, 48, 57, 55, 98, 52, 50, 53, 101, 100, 48, 57, 55, 98, 52, 50, 53, 101, 100, 48, 57, 55, 98, 52, 50, 53, 101, 100, 48, 57, 55, 98, 52, 50, 53, 101, 100, 48, 57, 55, 98, 52, 50, 54, 48, 98, 53, 101, 57, 99, 55, 55, 49, 48, 99, 56, 54, 52)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode5() {
        val input = byteArrayOf(16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, -34, -7, -34, -94, -9, -100, -42, 88, 18, 99, 26, 92, -11, -45, -19)
        val expected = byteArrayOf(49, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 49, 52, 100, 101, 102, 57, 100, 101, 97, 50, 102, 55, 57, 99, 100, 54, 53, 56, 49, 50, 54, 51, 49, 97, 53, 99, 102, 53, 100, 51, 101, 100)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode6() {
        val input = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -4)
        val expected = byteArrayOf(102, 102, 102, 102, 102, 102, 102, 101, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 48, 48, 48, 48, 48, 48, 48, 48, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 99)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }


    @Test
    fun testEncode7() {
        val input = byteArrayOf(40, -23, -6, -98, -99, -97, 94, 52, 77, 90, -98, 75, -49, 101, 9, -89, -13, -105, -119, -11, 21, -85, -113, -110, -35, -68, -67, 65, 77, -108, 14, -109)
        val expected = byteArrayOf(50, 56, 101, 57, 102, 97, 57, 101, 57, 100, 57, 102, 53, 101, 51, 52, 52, 100, 53, 97, 57, 101, 52, 98, 99, 102, 54, 53, 48, 57, 97, 55, 102, 51, 57, 55, 56, 57, 102, 53, 49, 53, 97, 98, 56, 102, 57, 50, 100, 100, 98, 99, 98, 100, 52, 49, 52, 100, 57, 52, 48, 101, 57, 51)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }

    @Test
    fun testEncode8() {
        val input = byteArrayOf(-1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 114, 3, -33, 107, 33, -58, 5, 43, 83, -69, -12, 9, 57, -43, 65, 35)
        val expected = byteArrayOf(102, 102, 102, 102, 102, 102, 102, 101, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 55, 50, 48, 51, 100, 102, 54, 98, 50, 49, 99, 54, 48, 53, 50, 98, 53, 51, 98, 98, 102, 52, 48, 57, 51, 57, 100, 53, 52, 49, 50, 51)

        val actual = Hex.encode(input)

        assertEquals(expected.toHexString(), actual.toHexString())
    }
}
