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

package org.spongycastle.math.raw//package org.spongycastle.math.raw
//
//import org.spongycastle.util.Pack.longToBigEndian
//import java.math.BigInteger
//
//object Nat448 {
//    fun copy64(x: LongArray, z: LongArray) {
//        z[0] = x[0]
//        z[1] = x[1]
//        z[2] = x[2]
//        z[3] = x[3]
//        z[4] = x[4]
//        z[5] = x[5]
//        z[6] = x[6]
//    }
//
//    fun create64(): LongArray {
//        return LongArray(7)
//    }
//
//    fun createExt64(): LongArray {
//        return LongArray(14)
//    }
//
//    fun eq64(x: LongArray, y: LongArray): Boolean {
//        for (i in 6 downTo 0) {
//            if (x[i] != y[i]) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun fromBigInteger64(x: BigInteger): LongArray {
//        var x = x
//        require(!(x.signum() < 0 || x.bitLength() > 448))
//        val z = create64()
//        var i = 0
//        while (x.signum() != 0) {
//            z[i++] = x.toLong()
//            x = x.shiftRight(64)
//        }
//        return z
//    }
//
//    fun isOne64(x: LongArray): Boolean {
//        if (x[0] != 1L) {
//            return false
//        }
//        for (i in 1..6) {
//            if (x[i] != 0L) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun isZero64(x: LongArray): Boolean {
//        for (i in 0..6) {
//            if (x[i] != 0L) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun toBigInteger64(x: LongArray): BigInteger {
//        val bs = ByteArray(56)
//        for (i in 0..6) {
//            val x_i = x[i]
//            if (x_i != 0L) {
//                longToBigEndian(x_i, bs, 6 - i shl 3)
//            }
//        }
//        return BigInteger(1, bs)
//    }
//}
