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

package org.spongycastle.asn1.x9

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECFieldElement

/**
 * A class which converts integers to byte arrays, allowing padding and calculations
 * to be done according the the filed size of the curve or field element involved.
 */
class X9IntegerConverter {
    /**
     * Return the curve's field size in bytes.
     *
     * @param c the curve of interest.
     * @return the field size in bytes (rounded up).
     */
    fun getByteLength(
        c: ECCurve
    ): Int {
        return (c.getFieldSize() + 7) / 8
    }

    /**
     * Return the field element's field size in bytes.
     *
     * @param fe the field element of interest.
     * @return the field size in bytes (rounded up).
     */
    fun getByteLength(
        fe: ECFieldElement
    ): Int {
        return (fe.getFieldSize() + 7) / 8
    }

    /**
     * Convert an integer to a byte array, ensuring it is exactly qLength long.
     *
     * @param s the integer to be converted.
     * @param qLength the length
     * @return the resulting byte array.
     */
    fun integerToBytes(
        s: BigInteger,
        qLength: Int
    ): ByteArray {
        val bytes = s.toByteArray()
        if (qLength < bytes.size) {
            val tmp = ByteArray(qLength)
            bytes.copyInto(destination = tmp, destinationOffset = 0, startIndex = bytes.size - tmp.size, endIndex = bytes.size)
            return tmp
        } else if (qLength > bytes.size) {
            val tmp = ByteArray(qLength)
            bytes.copyInto(destination = tmp, destinationOffset = tmp.size - bytes.size, startIndex = 0, endIndex = bytes.size)
            return tmp
        }
        return bytes
    }
}
