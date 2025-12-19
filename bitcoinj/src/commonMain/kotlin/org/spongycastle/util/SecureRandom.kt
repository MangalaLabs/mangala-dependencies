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
import com.soywiz.krypto.SecureRandom

fun randomBigInteger(mBits: Int): BigInteger {
    val byteArray = ByteArray((mBits + 7) / 8) // +7 to round up to the nearest byte
    SecureRandom.nextBytes(byteArray)
    // Ensure the BigInteger is within the required bit length
    val bitMask = (1 shl (mBits % 8)) - 1
    if (mBits % 8 > 0) {
        byteArray[0] = (byteArray[0].toInt() and bitMask).toByte()
    }
    return BigInteger.fromUByteArray(byteArray.toUByteArray(), com.ionspin.kotlin.bignum.integer.Sign.POSITIVE)
}
