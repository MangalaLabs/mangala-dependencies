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
//import org.spongycastle.math.raw.Nat.incAt
//import org.spongycastle.util.Pack.intToBigEndian
//import java.math.BigInteger
//
//object Nat224 {
//    private const val M = 0xFFFFFFFFL
//    fun add(x: IntArray, y: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        c += (x[0].toLong() and M) + (y[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c ushr 32
//        c += (x[1].toLong() and M) + (y[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c ushr 32
//        c += (x[2].toLong() and M) + (y[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c ushr 32
//        c += (x[3].toLong() and M) + (y[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c ushr 32
//        c += (x[4].toLong() and M) + (y[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c ushr 32
//        c += (x[5].toLong() and M) + (y[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c ushr 32
//        c += (x[6].toLong() and M) + (y[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun add(x: IntArray, xOff: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
//        var c: Long = 0
//        c += (x[xOff + 0].toLong() and M) + (y[yOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 1].toLong() and M) + (y[yOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 2].toLong() and M) + (y[yOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 3].toLong() and M) + (y[yOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 4].toLong() and M) + (y[yOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 5].toLong() and M) + (y[yOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 6].toLong() and M) + (y[yOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun addBothTo(x: IntArray, y: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        c += (x[0].toLong() and M) + (y[0].toLong() and M) + (z[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c ushr 32
//        c += (x[1].toLong() and M) + (y[1].toLong() and M) + (z[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c ushr 32
//        c += (x[2].toLong() and M) + (y[2].toLong() and M) + (z[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c ushr 32
//        c += (x[3].toLong() and M) + (y[3].toLong() and M) + (z[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c ushr 32
//        c += (x[4].toLong() and M) + (y[4].toLong() and M) + (z[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c ushr 32
//        c += (x[5].toLong() and M) + (y[5].toLong() and M) + (z[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c ushr 32
//        c += (x[6].toLong() and M) + (y[6].toLong() and M) + (z[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun addBothTo(x: IntArray, xOff: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
//        var c: Long = 0
//        c += (x[xOff + 0].toLong() and M) + (y[yOff + 0].toLong() and M) + (z[zOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 1].toLong() and M) + (y[yOff + 1].toLong() and M) + (z[zOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 2].toLong() and M) + (y[yOff + 2].toLong() and M) + (z[zOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 3].toLong() and M) + (y[yOff + 3].toLong() and M) + (z[zOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 4].toLong() and M) + (y[yOff + 4].toLong() and M) + (z[zOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 5].toLong() and M) + (y[yOff + 5].toLong() and M) + (z[zOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 6].toLong() and M) + (y[yOff + 6].toLong() and M) + (z[zOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun addTo(x: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        c += (x[0].toLong() and M) + (z[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c ushr 32
//        c += (x[1].toLong() and M) + (z[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c ushr 32
//        c += (x[2].toLong() and M) + (z[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c ushr 32
//        c += (x[3].toLong() and M) + (z[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c ushr 32
//        c += (x[4].toLong() and M) + (z[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c ushr 32
//        c += (x[5].toLong() and M) + (z[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c ushr 32
//        c += (x[6].toLong() and M) + (z[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun addTo(x: IntArray, xOff: Int, z: IntArray, zOff: Int, cIn: Int): Int {
//        var c = cIn.toLong() and M
//        c += (x[xOff + 0].toLong() and M) + (z[zOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 1].toLong() and M) + (z[zOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 2].toLong() and M) + (z[zOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 3].toLong() and M) + (z[zOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 4].toLong() and M) + (z[zOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 5].toLong() and M) + (z[zOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c ushr 32
//        c += (x[xOff + 6].toLong() and M) + (z[zOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun addToEachOther(u: IntArray, uOff: Int, v: IntArray, vOff: Int): Int {
//        var c: Long = 0
//        c += (u[uOff + 0].toLong() and M) + (v[vOff + 0].toLong() and M)
//        u[uOff + 0] = c.toInt()
//        v[vOff + 0] = c.toInt()
//        c = c ushr 32
//        c += (u[uOff + 1].toLong() and M) + (v[vOff + 1].toLong() and M)
//        u[uOff + 1] = c.toInt()
//        v[vOff + 1] = c.toInt()
//        c = c ushr 32
//        c += (u[uOff + 2].toLong() and M) + (v[vOff + 2].toLong() and M)
//        u[uOff + 2] = c.toInt()
//        v[vOff + 2] = c.toInt()
//        c = c ushr 32
//        c += (u[uOff + 3].toLong() and M) + (v[vOff + 3].toLong() and M)
//        u[uOff + 3] = c.toInt()
//        v[vOff + 3] = c.toInt()
//        c = c ushr 32
//        c += (u[uOff + 4].toLong() and M) + (v[vOff + 4].toLong() and M)
//        u[uOff + 4] = c.toInt()
//        v[vOff + 4] = c.toInt()
//        c = c ushr 32
//        c += (u[uOff + 5].toLong() and M) + (v[vOff + 5].toLong() and M)
//        u[uOff + 5] = c.toInt()
//        v[vOff + 5] = c.toInt()
//        c = c ushr 32
//        c += (u[uOff + 6].toLong() and M) + (v[vOff + 6].toLong() and M)
//        u[uOff + 6] = c.toInt()
//        v[vOff + 6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun copy(x: IntArray, z: IntArray) {
//        z[0] = x[0]
//        z[1] = x[1]
//        z[2] = x[2]
//        z[3] = x[3]
//        z[4] = x[4]
//        z[5] = x[5]
//        z[6] = x[6]
//    }
//
//    fun create(): IntArray {
//        return IntArray(7)
//    }
//
//    fun createExt(): IntArray {
//        return IntArray(14)
//    }
//
//    fun diff(x: IntArray, xOff: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Boolean {
//        val pos = gte(x, xOff, y, yOff)
//        if (pos) {
//            sub(x, xOff, y, yOff, z, zOff)
//        } else {
//            sub(y, yOff, x, xOff, z, zOff)
//        }
//        return pos
//    }
//
//    fun eq(x: IntArray, y: IntArray): Boolean {
//        for (i in 6 downTo 0) {
//            if (x[i] != y[i]) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun fromBigInteger(x: BigInteger): IntArray {
//        var x = x
//        require(!(x.signum() < 0 || x.bitLength() > 224))
//        val z = create()
//        var i = 0
//        while (x.signum() != 0) {
//            z[i++] = x.toInt()
//            x = x.shiftRight(32)
//        }
//        return z
//    }
//
//    fun getBit(x: IntArray, bit: Int): Int {
//        if (bit == 0) {
//            return x[0] and 1
//        }
//        val w = bit shr 5
//        if (w < 0 || w >= 7) {
//            return 0
//        }
//        val b = bit and 31
//        return x[w] ushr b and 1
//    }
//
//    fun gte(x: IntArray, y: IntArray): Boolean {
//        for (i in 6 downTo 0) {
//            val x_i = x[i] xor Int.MIN_VALUE
//            val y_i = y[i] xor Int.MIN_VALUE
//            if (x_i < y_i) return false
//            if (x_i > y_i) return true
//        }
//        return true
//    }
//
//    fun gte(x: IntArray, xOff: Int, y: IntArray, yOff: Int): Boolean {
//        for (i in 6 downTo 0) {
//            val x_i = x[xOff + i] xor Int.MIN_VALUE
//            val y_i = y[yOff + i] xor Int.MIN_VALUE
//            if (x_i < y_i) return false
//            if (x_i > y_i) return true
//        }
//        return true
//    }
//
//    fun isOne(x: IntArray): Boolean {
//        if (x[0] != 1) {
//            return false
//        }
//        for (i in 1..6) {
//            if (x[i] != 0) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun isZero(x: IntArray): Boolean {
//        for (i in 0..6) {
//            if (x[i] != 0) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun mul(x: IntArray, y: IntArray, zz: IntArray) {
//        val y_0 = y[0].toLong() and M
//        val y_1 = y[1].toLong() and M
//        val y_2 = y[2].toLong() and M
//        val y_3 = y[3].toLong() and M
//        val y_4 = y[4].toLong() and M
//        val y_5 = y[5].toLong() and M
//        val y_6 = y[6].toLong() and M
//        run {
//            var c: Long = 0
//            val x_0 = x[0].toLong() and M
//            c += x_0 * y_0
//            zz[0] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_1
//            zz[1] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_2
//            zz[2] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_3
//            zz[3] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_4
//            zz[4] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_5
//            zz[5] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_6
//            zz[6] = c.toInt()
//            c = c ushr 32
//            zz[7] = c.toInt()
//        }
//        for (i in 1..6) {
//            var c: Long = 0
//            val x_i = x[i].toLong() and M
//            c += x_i * y_0 + (zz[i + 0].toLong() and M)
//            zz[i + 0] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_1 + (zz[i + 1].toLong() and M)
//            zz[i + 1] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_2 + (zz[i + 2].toLong() and M)
//            zz[i + 2] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_3 + (zz[i + 3].toLong() and M)
//            zz[i + 3] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_4 + (zz[i + 4].toLong() and M)
//            zz[i + 4] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_5 + (zz[i + 5].toLong() and M)
//            zz[i + 5] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_6 + (zz[i + 6].toLong() and M)
//            zz[i + 6] = c.toInt()
//            c = c ushr 32
//            zz[i + 7] = c.toInt()
//        }
//    }
//
//    fun mul(x: IntArray, xOff: Int, y: IntArray, yOff: Int, zz: IntArray, zzOff: Int) {
//        var zzOff = zzOff
//        val y_0 = y[yOff + 0].toLong() and M
//        val y_1 = y[yOff + 1].toLong() and M
//        val y_2 = y[yOff + 2].toLong() and M
//        val y_3 = y[yOff + 3].toLong() and M
//        val y_4 = y[yOff + 4].toLong() and M
//        val y_5 = y[yOff + 5].toLong() and M
//        val y_6 = y[yOff + 6].toLong() and M
//        run {
//            var c: Long = 0
//            val x_0 = x[xOff + 0].toLong() and M
//            c += x_0 * y_0
//            zz[zzOff + 0] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_1
//            zz[zzOff + 1] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_2
//            zz[zzOff + 2] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_3
//            zz[zzOff + 3] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_4
//            zz[zzOff + 4] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_5
//            zz[zzOff + 5] = c.toInt()
//            c = c ushr 32
//            c += x_0 * y_6
//            zz[zzOff + 6] = c.toInt()
//            c = c ushr 32
//            zz[zzOff + 7] = c.toInt()
//        }
//        for (i in 1..6) {
//            ++zzOff
//            var c: Long = 0
//            val x_i = x[xOff + i].toLong() and M
//            c += x_i * y_0 + (zz[zzOff + 0].toLong() and M)
//            zz[zzOff + 0] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_1 + (zz[zzOff + 1].toLong() and M)
//            zz[zzOff + 1] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_2 + (zz[zzOff + 2].toLong() and M)
//            zz[zzOff + 2] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_3 + (zz[zzOff + 3].toLong() and M)
//            zz[zzOff + 3] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_4 + (zz[zzOff + 4].toLong() and M)
//            zz[zzOff + 4] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_5 + (zz[zzOff + 5].toLong() and M)
//            zz[zzOff + 5] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_6 + (zz[zzOff + 6].toLong() and M)
//            zz[zzOff + 6] = c.toInt()
//            c = c ushr 32
//            zz[zzOff + 7] = c.toInt()
//        }
//    }
//
//    fun mulAddTo(x: IntArray, y: IntArray, zz: IntArray): Int {
//        val y_0 = y[0].toLong() and M
//        val y_1 = y[1].toLong() and M
//        val y_2 = y[2].toLong() and M
//        val y_3 = y[3].toLong() and M
//        val y_4 = y[4].toLong() and M
//        val y_5 = y[5].toLong() and M
//        val y_6 = y[6].toLong() and M
//        var zc: Long = 0
//        for (i in 0..6) {
//            var c: Long = 0
//            val x_i = x[i].toLong() and M
//            c += x_i * y_0 + (zz[i + 0].toLong() and M)
//            zz[i + 0] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_1 + (zz[i + 1].toLong() and M)
//            zz[i + 1] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_2 + (zz[i + 2].toLong() and M)
//            zz[i + 2] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_3 + (zz[i + 3].toLong() and M)
//            zz[i + 3] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_4 + (zz[i + 4].toLong() and M)
//            zz[i + 4] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_5 + (zz[i + 5].toLong() and M)
//            zz[i + 5] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_6 + (zz[i + 6].toLong() and M)
//            zz[i + 6] = c.toInt()
//            c = c ushr 32
//            c += zc + (zz[i + 7].toLong() and M)
//            zz[i + 7] = c.toInt()
//            zc = c ushr 32
//        }
//        return zc.toInt()
//    }
//
//    fun mulAddTo(x: IntArray, xOff: Int, y: IntArray, yOff: Int, zz: IntArray, zzOff: Int): Int {
//        var zzOff = zzOff
//        val y_0 = y[yOff + 0].toLong() and M
//        val y_1 = y[yOff + 1].toLong() and M
//        val y_2 = y[yOff + 2].toLong() and M
//        val y_3 = y[yOff + 3].toLong() and M
//        val y_4 = y[yOff + 4].toLong() and M
//        val y_5 = y[yOff + 5].toLong() and M
//        val y_6 = y[yOff + 6].toLong() and M
//        var zc: Long = 0
//        for (i in 0..6) {
//            var c: Long = 0
//            val x_i = x[xOff + i].toLong() and M
//            c += x_i * y_0 + (zz[zzOff + 0].toLong() and M)
//            zz[zzOff + 0] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_1 + (zz[zzOff + 1].toLong() and M)
//            zz[zzOff + 1] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_2 + (zz[zzOff + 2].toLong() and M)
//            zz[zzOff + 2] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_3 + (zz[zzOff + 3].toLong() and M)
//            zz[zzOff + 3] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_4 + (zz[zzOff + 4].toLong() and M)
//            zz[zzOff + 4] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_5 + (zz[zzOff + 5].toLong() and M)
//            zz[zzOff + 5] = c.toInt()
//            c = c ushr 32
//            c += x_i * y_6 + (zz[zzOff + 6].toLong() and M)
//            zz[zzOff + 6] = c.toInt()
//            c = c ushr 32
//            c += zc + (zz[zzOff + 7].toLong() and M)
//            zz[zzOff + 7] = c.toInt()
//            zc = c ushr 32
//            ++zzOff
//        }
//        return zc.toInt()
//    }
//
//    fun mul33Add(
//        w: Int,
//        x: IntArray,
//        xOff: Int,
//        y: IntArray,
//        yOff: Int,
//        z: IntArray,
//        zOff: Int
//    ): Long {
//        // assert w >>> 31 == 0;
//        var c: Long = 0
//        val wVal = w.toLong() and M
//        val x0 = x[xOff + 0].toLong() and M
//        c += wVal * x0 + (y[yOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        val x1 = x[xOff + 1].toLong() and M
//        c += wVal * x1 + x0 + (y[yOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        val x2 = x[xOff + 2].toLong() and M
//        c += wVal * x2 + x1 + (y[yOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        val x3 = x[xOff + 3].toLong() and M
//        c += wVal * x3 + x2 + (y[yOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c ushr 32
//        val x4 = x[xOff + 4].toLong() and M
//        c += wVal * x4 + x3 + (y[yOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c ushr 32
//        val x5 = x[xOff + 5].toLong() and M
//        c += wVal * x5 + x4 + (y[yOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c ushr 32
//        val x6 = x[xOff + 6].toLong() and M
//        c += wVal * x6 + x5 + (y[yOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c ushr 32
//        c += x6
//        return c
//    }
//
//    fun mulByWord(x: Int, z: IntArray): Int {
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        c += xVal * (z[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun mulByWordAddTo(x: Int, y: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        c += xVal * (z[0].toLong() and M) + (y[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[1].toLong() and M) + (y[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[2].toLong() and M) + (y[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[3].toLong() and M) + (y[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[4].toLong() and M) + (y[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[5].toLong() and M) + (y[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c ushr 32
//        c += xVal * (z[6].toLong() and M) + (y[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun mulWordAddTo(x: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        c += xVal * (y[yOff + 0].toLong() and M) + (z[zOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y[yOff + 1].toLong() and M) + (z[zOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y[yOff + 2].toLong() and M) + (z[zOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y[yOff + 3].toLong() and M) + (z[zOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y[yOff + 4].toLong() and M) + (z[zOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y[yOff + 5].toLong() and M) + (z[zOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y[yOff + 6].toLong() and M) + (z[zOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c ushr 32
//        return c.toInt()
//    }
//
//    fun mul33DWordAdd(x: Int, y: Long, z: IntArray, zOff: Int): Int {
//        // assert x >>> 31 == 0;
//        // assert zOff <= 3;
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        val y00 = y and M
//        c += xVal * y00 + (z[zOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        val y01 = y ushr 32
//        c += xVal * y01 + y00 + (z[zOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += y01 + (z[zOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        c += z[zOff + 3].toLong() and M
//        z[zOff + 3] = c.toInt()
//        c = c ushr 32
//        return if (c == 0L) 0 else incAt(7, z, zOff, 4)
//    }
//
//    fun mul33WordAdd(x: Int, y: Int, z: IntArray, zOff: Int): Int {
//        // assert x >>> 31 == 0;
//        // assert zOff <= 4;
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        val yVal = y.toLong() and M
//        c += yVal * xVal + (z[zOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        c += yVal + (z[zOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += z[zOff + 2].toLong() and M
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        return if (c == 0L) 0 else incAt(7, z, zOff, 3)
//    }
//
//    fun mulWordDwordAdd(x: Int, y: Long, z: IntArray, zOff: Int): Int {
//        // assert zOff <= 4;
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        c += xVal * (y and M) + (z[zOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c ushr 32
//        c += xVal * (y ushr 32) + (z[zOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c ushr 32
//        c += z[zOff + 2].toLong() and M
//        z[zOff + 2] = c.toInt()
//        c = c ushr 32
//        return if (c == 0L) 0 else incAt(7, z, zOff, 3)
//    }
//
//    fun mulWord(x: Int, y: IntArray, z: IntArray, zOff: Int): Int {
//        var c: Long = 0
//        val xVal = x.toLong() and M
//        var i = 0
//        do {
//            c += xVal * (y[i].toLong() and M)
//            z[zOff + i] = c.toInt()
//            c = c ushr 32
//        } while (++i < 7)
//        return c.toInt()
//    }
//
//    fun square(x: IntArray, zz: IntArray) {
//        val x_0 = x[0].toLong() and M
//        var zz_1: Long
//        var c = 0
//        var w: Int
//        run {
//            var i = 6
//            var j = 14
//            do {
//                val xVal = x[i--].toLong() and M
//                val p = xVal * xVal
//                zz[--j] = c shl 31 or (p ushr 33).toInt()
//                zz[--j] = (p ushr 1).toInt()
//                c = p.toInt()
//            } while (i > 0)
//            run {
//                val p = x_0 * x_0
//                zz_1 = (c shl 31).toLong() and M or (p ushr 33)
//                zz[0] = p.toInt()
//                c = (p ushr 32).toInt() and 1
//            }
//        }
//        val x_1 = x[1].toLong() and M
//        var zz_2 = zz[2].toLong() and M
//        run {
//            zz_1 += x_1 * x_0
//            w = zz_1.toInt()
//            zz[1] = w shl 1 or c
//            c = w ushr 31
//            zz_2 += zz_1 ushr 32
//        }
//        val x_2 = x[2].toLong() and M
//        var zz_3 = zz[3].toLong() and M
//        var zz_4 = zz[4].toLong() and M
//        run {
//            zz_2 += x_2 * x_0
//            w = zz_2.toInt()
//            zz[2] = w shl 1 or c
//            c = w ushr 31
//            zz_3 += (zz_2 ushr 32) + x_2 * x_1
//            zz_4 += zz_3 ushr 32
//            zz_3 = zz_3 and M
//        }
//        val x_3 = x[3].toLong() and M
//        var zz_5 = (zz[5].toLong() and M) + (zz_4 ushr 32)
//        zz_4 = zz_4 and M
//        var zz_6 = (zz[6].toLong() and M) + (zz_5 ushr 32)
//        zz_5 = zz_5 and M
//        run {
//            zz_3 += x_3 * x_0
//            w = zz_3.toInt()
//            zz[3] = w shl 1 or c
//            c = w ushr 31
//            zz_4 += (zz_3 ushr 32) + x_3 * x_1
//            zz_5 += (zz_4 ushr 32) + x_3 * x_2
//            zz_4 = zz_4 and M
//            zz_6 += zz_5 ushr 32
//            zz_5 = zz_5 and M
//        }
//        val x_4 = x[4].toLong() and M
//        var zz_7 = (zz[7].toLong() and M) + (zz_6 ushr 32)
//        zz_6 = zz_6 and M
//        var zz_8 = (zz[8].toLong() and M) + (zz_7 ushr 32)
//        zz_7 = zz_7 and M
//        run {
//            zz_4 += x_4 * x_0
//            w = zz_4.toInt()
//            zz[4] = w shl 1 or c
//            c = w ushr 31
//            zz_5 += (zz_4 ushr 32) + x_4 * x_1
//            zz_6 += (zz_5 ushr 32) + x_4 * x_2
//            zz_5 = zz_5 and M
//            zz_7 += (zz_6 ushr 32) + x_4 * x_3
//            zz_6 = zz_6 and M
//            zz_8 += zz_7 ushr 32
//            zz_7 = zz_7 and M
//        }
//        val x_5 = x[5].toLong() and M
//        var zz_9 = (zz[9].toLong() and M) + (zz_8 ushr 32)
//        zz_8 = zz_8 and M
//        var zz_10 = (zz[10].toLong() and M) + (zz_9 ushr 32)
//        zz_9 = zz_9 and M
//        run {
//            zz_5 += x_5 * x_0
//            w = zz_5.toInt()
//            zz[5] = w shl 1 or c
//            c = w ushr 31
//            zz_6 += (zz_5 ushr 32) + x_5 * x_1
//            zz_7 += (zz_6 ushr 32) + x_5 * x_2
//            zz_6 = zz_6 and M
//            zz_8 += (zz_7 ushr 32) + x_5 * x_3
//            zz_7 = zz_7 and M
//            zz_9 += (zz_8 ushr 32) + x_5 * x_4
//            zz_8 = zz_8 and M
//            zz_10 += zz_9 ushr 32
//            zz_9 = zz_9 and M
//        }
//        val x_6 = x[6].toLong() and M
//        var zz_11 = (zz[11].toLong() and M) + (zz_10 ushr 32)
//        zz_10 = zz_10 and M
//        var zz_12 = (zz[12].toLong() and M) + (zz_11 ushr 32)
//        zz_11 = zz_11 and M
//        run {
//            zz_6 += x_6 * x_0
//            w = zz_6.toInt()
//            zz[6] = w shl 1 or c
//            c = w ushr 31
//            zz_7 += (zz_6 ushr 32) + x_6 * x_1
//            zz_8 += (zz_7 ushr 32) + x_6 * x_2
//            zz_9 += (zz_8 ushr 32) + x_6 * x_3
//            zz_10 += (zz_9 ushr 32) + x_6 * x_4
//            zz_11 += (zz_10 ushr 32) + x_6 * x_5
//            zz_12 += zz_11 ushr 32
//        }
//        w = zz_7.toInt()
//        zz[7] = w shl 1 or c
//        c = w ushr 31
//        w = zz_8.toInt()
//        zz[8] = w shl 1 or c
//        c = w ushr 31
//        w = zz_9.toInt()
//        zz[9] = w shl 1 or c
//        c = w ushr 31
//        w = zz_10.toInt()
//        zz[10] = w shl 1 or c
//        c = w ushr 31
//        w = zz_11.toInt()
//        zz[11] = w shl 1 or c
//        c = w ushr 31
//        w = zz_12.toInt()
//        zz[12] = w shl 1 or c
//        c = w ushr 31
//        w = zz[13] + (zz_12 ushr 32).toInt()
//        zz[13] = w shl 1 or c
//    }
//
//    fun square(x: IntArray, xOff: Int, zz: IntArray, zzOff: Int) {
//        val x_0 = x[xOff + 0].toLong() and M
//        var zz_1: Long
//        var c = 0
//        var w: Int
//        run {
//            var i = 6
//            var j = 14
//            do {
//                val xVal = x[xOff + i--].toLong() and M
//                val p = xVal * xVal
//                zz[zzOff + --j] = c shl 31 or (p ushr 33).toInt()
//                zz[zzOff + --j] = (p ushr 1).toInt()
//                c = p.toInt()
//            } while (i > 0)
//            run {
//                val p = x_0 * x_0
//                zz_1 = (c shl 31).toLong() and M or (p ushr 33)
//                zz[zzOff + 0] = p.toInt()
//                c = (p ushr 32).toInt() and 1
//            }
//        }
//        val x_1 = x[xOff + 1].toLong() and M
//        var zz_2 = zz[zzOff + 2].toLong() and M
//        run {
//            zz_1 += x_1 * x_0
//            w = zz_1.toInt()
//            zz[zzOff + 1] = w shl 1 or c
//            c = w ushr 31
//            zz_2 += zz_1 ushr 32
//        }
//        val x_2 = x[xOff + 2].toLong() and M
//        var zz_3 = zz[zzOff + 3].toLong() and M
//        var zz_4 = zz[zzOff + 4].toLong() and M
//        run {
//            zz_2 += x_2 * x_0
//            w = zz_2.toInt()
//            zz[zzOff + 2] = w shl 1 or c
//            c = w ushr 31
//            zz_3 += (zz_2 ushr 32) + x_2 * x_1
//            zz_4 += zz_3 ushr 32
//            zz_3 = zz_3 and M
//        }
//        val x_3 = x[xOff + 3].toLong() and M
//        var zz_5 = (zz[zzOff + 5].toLong() and M) + (zz_4 ushr 32)
//        zz_4 = zz_4 and M
//        var zz_6 = (zz[zzOff + 6].toLong() and M) + (zz_5 ushr 32)
//        zz_5 = zz_5 and M
//        run {
//            zz_3 += x_3 * x_0
//            w = zz_3.toInt()
//            zz[zzOff + 3] = w shl 1 or c
//            c = w ushr 31
//            zz_4 += (zz_3 ushr 32) + x_3 * x_1
//            zz_5 += (zz_4 ushr 32) + x_3 * x_2
//            zz_4 = zz_4 and M
//            zz_6 += zz_5 ushr 32
//            zz_5 = zz_5 and M
//        }
//        val x_4 = x[xOff + 4].toLong() and M
//        var zz_7 = (zz[zzOff + 7].toLong() and M) + (zz_6 ushr 32)
//        zz_6 = zz_6 and M
//        var zz_8 = (zz[zzOff + 8].toLong() and M) + (zz_7 ushr 32)
//        zz_7 = zz_7 and M
//        run {
//            zz_4 += x_4 * x_0
//            w = zz_4.toInt()
//            zz[zzOff + 4] = w shl 1 or c
//            c = w ushr 31
//            zz_5 += (zz_4 ushr 32) + x_4 * x_1
//            zz_6 += (zz_5 ushr 32) + x_4 * x_2
//            zz_5 = zz_5 and M
//            zz_7 += (zz_6 ushr 32) + x_4 * x_3
//            zz_6 = zz_6 and M
//            zz_8 += zz_7 ushr 32
//            zz_7 = zz_7 and M
//        }
//        val x_5 = x[xOff + 5].toLong() and M
//        var zz_9 = (zz[zzOff + 9].toLong() and M) + (zz_8 ushr 32)
//        zz_8 = zz_8 and M
//        var zz_10 = (zz[zzOff + 10].toLong() and M) + (zz_9 ushr 32)
//        zz_9 = zz_9 and M
//        run {
//            zz_5 += x_5 * x_0
//            w = zz_5.toInt()
//            zz[zzOff + 5] = w shl 1 or c
//            c = w ushr 31
//            zz_6 += (zz_5 ushr 32) + x_5 * x_1
//            zz_7 += (zz_6 ushr 32) + x_5 * x_2
//            zz_6 = zz_6 and M
//            zz_8 += (zz_7 ushr 32) + x_5 * x_3
//            zz_7 = zz_7 and M
//            zz_9 += (zz_8 ushr 32) + x_5 * x_4
//            zz_8 = zz_8 and M
//            zz_10 += zz_9 ushr 32
//            zz_9 = zz_9 and M
//        }
//        val x_6 = x[xOff + 6].toLong() and M
//        var zz_11 = (zz[zzOff + 11].toLong() and M) + (zz_10 ushr 32)
//        zz_10 = zz_10 and M
//        var zz_12 = (zz[zzOff + 12].toLong() and M) + (zz_11 ushr 32)
//        zz_11 = zz_11 and M
//        run {
//            zz_6 += x_6 * x_0
//            w = zz_6.toInt()
//            zz[zzOff + 6] = w shl 1 or c
//            c = w ushr 31
//            zz_7 += (zz_6 ushr 32) + x_6 * x_1
//            zz_8 += (zz_7 ushr 32) + x_6 * x_2
//            zz_9 += (zz_8 ushr 32) + x_6 * x_3
//            zz_10 += (zz_9 ushr 32) + x_6 * x_4
//            zz_11 += (zz_10 ushr 32) + x_6 * x_5
//            zz_12 += zz_11 ushr 32
//        }
//        w = zz_7.toInt()
//        zz[zzOff + 7] = w shl 1 or c
//        c = w ushr 31
//        w = zz_8.toInt()
//        zz[zzOff + 8] = w shl 1 or c
//        c = w ushr 31
//        w = zz_9.toInt()
//        zz[zzOff + 9] = w shl 1 or c
//        c = w ushr 31
//        w = zz_10.toInt()
//        zz[zzOff + 10] = w shl 1 or c
//        c = w ushr 31
//        w = zz_11.toInt()
//        zz[zzOff + 11] = w shl 1 or c
//        c = w ushr 31
//        w = zz_12.toInt()
//        zz[zzOff + 12] = w shl 1 or c
//        c = w ushr 31
//        w = zz[zzOff + 13] + (zz_12 ushr 32).toInt()
//        zz[zzOff + 13] = w shl 1 or c
//    }
//
//    fun sub(x: IntArray, y: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        c += (x[0].toLong() and M) - (y[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c shr 32
//        c += (x[1].toLong() and M) - (y[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c shr 32
//        c += (x[2].toLong() and M) - (y[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c shr 32
//        c += (x[3].toLong() and M) - (y[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c shr 32
//        c += (x[4].toLong() and M) - (y[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c shr 32
//        c += (x[5].toLong() and M) - (y[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c shr 32
//        c += (x[6].toLong() and M) - (y[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c shr 32
//        return c.toInt()
//    }
//
//    fun sub(x: IntArray, xOff: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
//        var c: Long = 0
//        c += (x[xOff + 0].toLong() and M) - (y[yOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c shr 32
//        c += (x[xOff + 1].toLong() and M) - (y[yOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c shr 32
//        c += (x[xOff + 2].toLong() and M) - (y[yOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c shr 32
//        c += (x[xOff + 3].toLong() and M) - (y[yOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c shr 32
//        c += (x[xOff + 4].toLong() and M) - (y[yOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c shr 32
//        c += (x[xOff + 5].toLong() and M) - (y[yOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c shr 32
//        c += (x[xOff + 6].toLong() and M) - (y[yOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c shr 32
//        return c.toInt()
//    }
//
//    fun subBothFrom(x: IntArray, y: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        c += (z[0].toLong() and M) - (x[0].toLong() and M) - (y[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c shr 32
//        c += (z[1].toLong() and M) - (x[1].toLong() and M) - (y[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c shr 32
//        c += (z[2].toLong() and M) - (x[2].toLong() and M) - (y[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c shr 32
//        c += (z[3].toLong() and M) - (x[3].toLong() and M) - (y[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c shr 32
//        c += (z[4].toLong() and M) - (x[4].toLong() and M) - (y[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c shr 32
//        c += (z[5].toLong() and M) - (x[5].toLong() and M) - (y[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c shr 32
//        c += (z[6].toLong() and M) - (x[6].toLong() and M) - (y[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c shr 32
//        return c.toInt()
//    }
//
//    fun subFrom(x: IntArray, z: IntArray): Int {
//        var c: Long = 0
//        c += (z[0].toLong() and M) - (x[0].toLong() and M)
//        z[0] = c.toInt()
//        c = c shr 32
//        c += (z[1].toLong() and M) - (x[1].toLong() and M)
//        z[1] = c.toInt()
//        c = c shr 32
//        c += (z[2].toLong() and M) - (x[2].toLong() and M)
//        z[2] = c.toInt()
//        c = c shr 32
//        c += (z[3].toLong() and M) - (x[3].toLong() and M)
//        z[3] = c.toInt()
//        c = c shr 32
//        c += (z[4].toLong() and M) - (x[4].toLong() and M)
//        z[4] = c.toInt()
//        c = c shr 32
//        c += (z[5].toLong() and M) - (x[5].toLong() and M)
//        z[5] = c.toInt()
//        c = c shr 32
//        c += (z[6].toLong() and M) - (x[6].toLong() and M)
//        z[6] = c.toInt()
//        c = c shr 32
//        return c.toInt()
//    }
//
//    fun subFrom(x: IntArray, xOff: Int, z: IntArray, zOff: Int): Int {
//        var c: Long = 0
//        c += (z[zOff + 0].toLong() and M) - (x[xOff + 0].toLong() and M)
//        z[zOff + 0] = c.toInt()
//        c = c shr 32
//        c += (z[zOff + 1].toLong() and M) - (x[xOff + 1].toLong() and M)
//        z[zOff + 1] = c.toInt()
//        c = c shr 32
//        c += (z[zOff + 2].toLong() and M) - (x[xOff + 2].toLong() and M)
//        z[zOff + 2] = c.toInt()
//        c = c shr 32
//        c += (z[zOff + 3].toLong() and M) - (x[xOff + 3].toLong() and M)
//        z[zOff + 3] = c.toInt()
//        c = c shr 32
//        c += (z[zOff + 4].toLong() and M) - (x[xOff + 4].toLong() and M)
//        z[zOff + 4] = c.toInt()
//        c = c shr 32
//        c += (z[zOff + 5].toLong() and M) - (x[xOff + 5].toLong() and M)
//        z[zOff + 5] = c.toInt()
//        c = c shr 32
//        c += (z[zOff + 6].toLong() and M) - (x[xOff + 6].toLong() and M)
//        z[zOff + 6] = c.toInt()
//        c = c shr 32
//        return c.toInt()
//    }
//
//    fun toBigInteger(x: IntArray): BigInteger {
//        val bs = ByteArray(28)
//        for (i in 0..6) {
//            val x_i = x[i]
//            if (x_i != 0) {
//                intToBigEndian(x_i, bs, 6 - i shl 2)
//            }
//        }
//        return BigInteger(1, bs)
//    }
//
//    fun zero(z: IntArray) {
//        z[0] = 0
//        z[1] = 0
//        z[2] = 0
//        z[3] = 0
//        z[4] = 0
//        z[5] = 0
//        z[6] = 0
//    }
//}
