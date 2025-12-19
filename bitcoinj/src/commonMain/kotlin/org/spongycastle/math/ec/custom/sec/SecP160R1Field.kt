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

package org.spongycastle.math.ec.custom.sec//package org.spongycastle.math.ec.custom.sec
//
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.math.raw.Nat160
//import java.math.BigInteger
//
//object SecP160R1Field {
//    private const val M = 0xFFFFFFFFL
//
//    // 2^160 - 2^31 - 1
//    @JvmField
//    val P = intArrayOf(0x7FFFFFFF, -0x1, -0x1, -0x1, -0x1)
//    val PExt = intArrayOf(
//        0x00000001, 0x40000001, 0x00000000, 0x00000000, 0x00000000,
//        -0x2, -0x2, -0x1, -0x1, -0x1
//    )
//    private val PExtInv = intArrayOf(
//        -0x1, -0x40000002, -0x1, -0x1,
//        -0x1, 0x00000001, 0x00000001
//    )
//    private const val P4 = -0x1
//    private const val PExt9 = -0x1
//    private const val PInv = -0x7fffffff
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat160.add(x, y, z)
//        if (c != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.addWordTo(5, PInv, z)
//        }
//    }
//
//    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.add(10, xx, yy, zz)
//        if (c != 0 || zz[9] == PExt9 && Nat.gte(10, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(10, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: IntArray, z: IntArray) {
//        val c = Nat.inc(5, x, z)
//        if (c != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.addWordTo(5, PInv, z)
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat160.fromBigInteger(x)
//        if (z[4] == P4 && Nat160.gte(z, P)) {
//            Nat160.subFrom(P, z)
//        }
//        return z
//    }
//
//    fun half(x: IntArray, z: IntArray) {
//        if (x[0] and 1 == 0) {
//            Nat.shiftDownBit(5, x, 0, z)
//        } else {
//            val c = Nat160.add(x, P, z)
//            Nat.shiftDownBit(5, z, c)
//        }
//    }
//
//    @JvmStatic
//    fun multiply(x: IntArray, y: IntArray, z: IntArray) {
//        val tt = Nat160.createExt()
//        Nat160.mul(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: IntArray, y: IntArray, zz: IntArray) {
//        val c = Nat160.mulAddTo(x, y, zz)
//        if (c != 0 || zz[9] == PExt9 && Nat.gte(10, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(10, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun negate(x: IntArray, z: IntArray) {
//        if (Nat160.isZero(x)) {
//            Nat160.zero(z)
//        } else {
//            Nat160.sub(P, x, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce(xx: IntArray, z: IntArray) {
//        val x5 = xx[5].toLong() and M
//        val x6 = xx[6].toLong() and M
//        val x7 = xx[7].toLong() and M
//        val x8 = xx[8].toLong() and M
//        val x9 = xx[9].toLong() and M
//        var c: Long = 0
//        c += (xx[0].toLong() and M) + x5 + (x5 shl 31)
//        z[0] = c.toInt()
//        c = c ushr 32
//        c += (xx[1].toLong() and M) + x6 + (x6 shl 31)
//        z[1] = c.toInt()
//        c = c ushr 32
//        c += (xx[2].toLong() and M) + x7 + (x7 shl 31)
//        z[2] = c.toInt()
//        c = c ushr 32
//        c += (xx[3].toLong() and M) + x8 + (x8 shl 31)
//        z[3] = c.toInt()
//        c = c ushr 32
//        c += (xx[4].toLong() and M) + x9 + (x9 shl 31)
//        z[4] = c.toInt()
//        c = c ushr 32
//
////        assert c >>> 32 == 0;
//        reduce32(c.toInt(), z)
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        if (x != 0 && Nat160.mulWordsAdd(PInv, x, z, 0) != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.addWordTo(5, PInv, z)
//        }
//    }
//
//    @JvmStatic
//    fun square(x: IntArray, z: IntArray) {
//        val tt = Nat160.createExt()
//        Nat160.square(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareN(x: IntArray, n: Int, z: IntArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat160.createExt()
//        Nat160.square(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            Nat160.square(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat160.sub(x, y, z)
//        if (c != 0) {
//            Nat.subWordFrom(5, PInv, z)
//        }
//    }
//
//    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.sub(10, xx, yy, zz)
//        if (c != 0) {
//            if (Nat.subFrom(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.decAt(10, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun twice(x: IntArray, z: IntArray) {
//        val c = Nat.shiftUpBit(5, x, 0, z)
//        if (c != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.addWordTo(5, PInv, z)
//        }
//    }
//}
