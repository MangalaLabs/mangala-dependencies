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
//object Mont256 {
//    private const val M = 0xFFFFFFFFL
//    fun inverse32(x: Int): Int {
//        // assert (x & 1) == 1;
//        var z = x // x.z == 1 mod 2**3
//        z *= 2 - x * z // x.z == 1 mod 2**6
//        z *= 2 - x * z // x.z == 1 mod 2**12
//        z *= 2 - x * z // x.z == 1 mod 2**24
//        z *= 2 - x * z // x.z == 1 mod 2**48
//        // assert x * z == 1;
//        return z
//    }
//
//    fun multAdd(x: IntArray, y: IntArray, z: IntArray, m: IntArray, mInv32: Int) {
//        var z_8 = 0
//        val y_0 = y[0].toLong() and M
//        for (i in 0..7) {
//            val z_0 = z[0].toLong() and M
//            val x_i = x[i].toLong() and M
//            var prod1 = x_i * y_0
//            var carry = (prod1 and M) + z_0
//            val t = (carry.toInt() * mInv32).toLong() and M
//            var prod2 = t * (m[0].toLong() and M)
//            carry += prod2 and M
//            // assert (int)carry == 0;
//            carry = (carry ushr 32) + (prod1 ushr 32) + (prod2 ushr 32)
//            for (j in 1..7) {
//                prod1 = x_i * (y[j].toLong() and M)
//                prod2 = t * (m[j].toLong() and M)
//                carry += (prod1 and M) + (prod2 and M) + (z[j].toLong() and M)
//                z[j - 1] = carry.toInt()
//                carry = (carry ushr 32) + (prod1 ushr 32) + (prod2 ushr 32)
//            }
//            carry += z_8.toLong() and M
//            z[7] = carry.toInt()
//            z_8 = (carry ushr 32).toInt()
//        }
//        if (z_8 != 0 || Nat256.gte(z, m)) {
//            Nat256.sub(z, m, z)
//        }
//    }
//
//    fun multAddXF(x: IntArray, y: IntArray, z: IntArray, m: IntArray) {
//        // assert m[0] == M;
//        var z_8 = 0
//        val y_0 = y[0].toLong() and M
//        for (i in 0..7) {
//            val x_i = x[i].toLong() and M
//            var carry = x_i * y_0 + (z[0].toLong() and M)
//            val t = carry and M
//            carry = (carry ushr 32) + t
//            for (j in 1..7) {
//                val prod1 = x_i * (y[j].toLong() and M)
//                val prod2 = t * (m[j].toLong() and M)
//                carry += (prod1 and M) + (prod2 and M) + (z[j].toLong() and M)
//                z[j - 1] = carry.toInt()
//                carry = (carry ushr 32) + (prod1 ushr 32) + (prod2 ushr 32)
//            }
//            carry += z_8.toLong() and M
//            z[7] = carry.toInt()
//            z_8 = (carry ushr 32).toInt()
//        }
//        if (z_8 != 0 || Nat256.gte(z, m)) {
//            Nat256.sub(z, m, z)
//        }
//    }
//
//    fun reduce(z: IntArray, m: IntArray, mInv32: Int) {
//        for (i in 0..7) {
//            val z_0 = z[0]
//            val t = (z_0 * mInv32).toLong() and M
//            var carry = t * (m[0].toLong() and M) + (z_0.toLong() and M)
//            // assert (int)carry == 0;
//            carry = carry ushr 32
//            for (j in 1..7) {
//                carry += t * (m[j].toLong() and M) + (z[j].toLong() and M)
//                z[j - 1] = carry.toInt()
//                carry = carry ushr 32
//            }
//            z[7] = carry.toInt()
//            // assert carry >>> 32 == 0;
//        }
//        if (Nat256.gte(z, m)) {
//            Nat256.sub(z, m, z)
//        }
//    }
//
//    fun reduceXF(z: IntArray, m: IntArray) {
//        // assert m[0] == M;
//        for (i in 0..7) {
//            val z_0 = z[0]
//            val t = z_0.toLong() and M
//            var carry = t
//            for (j in 1..7) {
//                carry += t * (m[j].toLong() and M) + (z[j].toLong() and M)
//                z[j - 1] = carry.toInt()
//                carry = carry ushr 32
//            }
//            z[7] = carry.toInt()
//            // assert carry >>> 32 == 0;
//        }
//        if (Nat256.gte(z, m)) {
//            Nat256.sub(z, m, z)
//        }
//    }
//}
