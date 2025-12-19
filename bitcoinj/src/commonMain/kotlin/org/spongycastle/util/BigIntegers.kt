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

package org.spongycastle.util

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlin.jvm.JvmStatic


/**
 * BigInteger utilities.
 */
object BigIntegers {
    private const val MAX_ITERATIONS = 1000
    private val ZERO = BigInteger.ZERO

    /**
     * Return the passed in value as an unsigned byte array.
     *
     * @param value value to be converted.
     * @return a byte array without a leading zero byte if present in the signed encoding.
     */
//    fun asUnsignedByteArray(
//        value: BigInteger
//    ): ByteArray {
//        val bytes = value.toByteArray()
//        if (bytes[0].toInt() == 0) {
//            val tmp = ByteArray(bytes.size - 1)
//            System.arraycopy(bytes, 1, tmp, 0, tmp.size)
//            return tmp
//        }
//        return bytes
//    }
    fun asUnsignedByteArray(value: BigInteger): ByteArray {
        val bytes = value.toByteArray()
        if (bytes.isNotEmpty() && bytes[0] == 0.toByte()) {
            // Skip the first byte if it's 0, which is a sign byte added for positive numbers to ensure they're not interpreted as negative.
            return bytes.copyOfRange(1, bytes.size)
        }
        return bytes
    }

    /**
     * Return the passed in value as an unsigned byte array.
     *
     * @param value value to be converted.
     * @return a byte array without a leading zero byte if present in the signed encoding.
     */
//    @JvmStatic
//    fun asUnsignedByteArray(length: Int, value: BigInteger): ByteArray {
//        val bytes = value.toByteArray()
//        if (bytes.size == length) {
//            return bytes
//        }
//        val start = if (bytes[0].toInt() == 0) 1 else 0
//        val count = bytes.size - start
//        require(count <= length) { "standard length exceeded for value" }
//        val tmp = ByteArray(length)
//        System.arraycopy(bytes, start, tmp, tmp.size - count, count)
//        return tmp
//    }
    @JvmStatic
    fun asUnsignedByteArray(length: Int, value: BigInteger): ByteArray {
        val bytes = value.toByteArray()
        if (bytes.size == length) {
            return bytes
        }

        val start = if (bytes.isNotEmpty() && bytes[0] == 0.toByte()) 1 else 0
        val count = bytes.size - start
        require(count <= length) { "standard length exceeded for value" }

        val tmp = ByteArray(length)
        // Use copyInto for Kotlin Multiplatform instead of System.arraycopy
        bytes.copyInto(
            destination = tmp,
            destinationOffset = length - count,
            startIndex = start,
            endIndex = bytes.size
        )
        return tmp
    }

    /**
     * Return a random BigInteger not less than 'min' and not greater than 'max'
     *
     * @param min the least value that may be generated
     * @param max the greatest value that may be generated
     * @param random the source of randomness
     * @return a random BigInteger value in the range [min,max]
     */
//    fun createRandomInRange(
//        min: BigInteger,
//        max: BigInteger,
//        random: SecureRandom?
//    ): BigInteger {
//        val cmp = min.compareTo(max)
//        if (cmp >= 0) {
//            require(cmp <= 0) { "'min' may not be greater than 'max'" }
//            return min
//        }
//        if (min.bitLength() > max.bitLength() / 2) {
//            return createRandomInRange(ZERO, max.subtract(min), random).add(min)
//        }
//        for (i in 0 until MAX_ITERATIONS) {
//            val x = BigInteger(max.bitLength(), random)
//            if (x.compareTo(min) >= 0 && x.compareTo(max) <= 0) {
//                return x
//            }
//        }
//
//        // fall back to a faster (restricted) method
//        return BigInteger(max.subtract(min).bitLength() - 1, random).add(min)
//    }

//    fun fromUnsignedByteArray(buf: ByteArray?): BigInteger {
//        return BigInteger(1, buf)
//    }

//    @JvmStatic
//    fun fromUnsignedByteArray(buf: ByteArray, off: Int, length: Int): BigInteger {
//        var mag = buf
//        if (off != 0 || length != buf.size) {
//            mag = ByteArray(length)
//            System.arraycopy(buf, off, mag, 0, length)
//        }
//        return BigInteger(1, mag)
//    }

    @JvmStatic
    fun fromUnsignedByteArray(buf: ByteArray, off: Int, length: Int): BigInteger {
        val mag: ByteArray = if (off == 0 && length == buf.size) {
            buf
        } else {
            // Use copyOfRange for Kotlin Multiplatform instead of System.arraycopy
            buf.copyOfRange(off, off + length)
        }
        // Assuming BigInteger(1, mag) is to create a positive BigInteger
        // In kotlin-multiplatform-bignum, we use fromByteArray and specify the sign
        return BigInteger.fromUByteArray(mag.toUByteArray(), Sign.POSITIVE)
    }
}
