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
//import org.spongycastle.math.raw.Nat128
//import org.spongycastle.math.raw.Nat256
//import java.math.BigInteger
//
//object SecP128R1Field {
//    private const val M = 0xFFFFFFFFL
//
//    // 2^128 - 2^97 - 1
//    @JvmField
//    val P = intArrayOf(-0x1, -0x1, -0x1, -0x3)
//    val PExt = intArrayOf(
//        0x00000001, 0x00000000, 0x00000000, 0x00000004, -0x2,
//        -0x1, 0x00000003, -0x4
//    )
//    private val PExtInv = intArrayOf(
//        -0x1, -0x1, -0x1, -0x5,
//        0x00000001, 0x00000000, -0x4, 0x00000003
//    )
//    private const val P3s1 = -0x3 ushr 1
//    private const val PExt7s1 = -0x4 ushr 1
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat128.add(x, y, z)
//        if (c != 0 || z[3] ushr 1 >= P3s1 && Nat128.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat256.add(xx, yy, zz)
//        if (c != 0 || zz[7] ushr 1 >= PExt7s1 && Nat256.gte(zz, PExt)) {
//            Nat.addTo(PExtInv.size, PExtInv, zz)
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: IntArray, z: IntArray) {
//        val c = Nat.inc(4, x, z)
//        if (c != 0 || z[3] ushr 1 >= P3s1 && Nat128.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat128.fromBigInteger(x)
//        if (z[3] ushr 1 >= P3s1 && Nat128.gte(z, P)) {
//            Nat128.subFrom(P, z)
//        }
//        return z
//    }
//
//    fun half(x: IntArray, z: IntArray) {
//        if (x[0] and 1 == 0) {
//            Nat.shiftDownBit(4, x, 0, z)
//        } else {
//            val c = Nat128.add(x, P, z)
//            Nat.shiftDownBit(4, z, c)
//        }
//    }
//
//    @JvmStatic
//    fun multiply(x: IntArray, y: IntArray, z: IntArray) {
//        val tt = Nat128.createExt()
//        Nat128.mul(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: IntArray, y: IntArray, zz: IntArray) {
//        val c = Nat128.mulAddTo(x, y, zz)
//        if (c != 0 || zz[7] ushr 1 >= PExt7s1 && Nat256.gte(zz, PExt)) {
//            Nat.addTo(PExtInv.size, PExtInv, zz)
//        }
//    }
//
//    @JvmStatic
//    fun negate(x: IntArray, z: IntArray) {
//        if (Nat128.isZero(x)) {
//            Nat128.zero(z)
//        } else {
//            Nat128.sub(P, x, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce(xx: IntArray, z: IntArray) {
//        var x0 = xx[0].toLong() and M
//        var x1 = xx[1].toLong() and M
//        var x2 = xx[2].toLong() and M
//        var x3 = xx[3].toLong() and M
//        var x4 = xx[4].toLong() and M
//        var x5 = xx[5].toLong() and M
//        var x6 = xx[6].toLong() and M
//        val x7 = xx[7].toLong() and M
//        x3 += x7
//        x6 += x7 shl 1
//        x2 += x6
//        x5 += x6 shl 1
//        x1 += x5
//        x4 += x5 shl 1
//        x0 += x4
//        x3 += x4 shl 1
//        z[0] = x0.toInt()
//        x1 += x0 ushr 32
//        z[1] = x1.toInt()
//        x2 += x1 ushr 32
//        z[2] = x2.toInt()
//        x3 += x2 ushr 32
//        z[3] = x3.toInt()
//        reduce32((x3 ushr 32).toInt(), z)
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        var x = x
//        while (x != 0) {
//            var c: Long
//            val x4 = x.toLong() and M
//            c = (z[0].toLong() and M) + x4
//            z[0] = c.toInt()
//            c = c shr 32
//            if (c != 0L) {
//                c += z[1].toLong() and M
//                z[1] = c.toInt()
//                c = c shr 32
//                c += z[2].toLong() and M
//                z[2] = c.toInt()
//                c = c shr 32
//            }
//            c += (z[3].toLong() and M) + (x4 shl 1)
//            z[3] = c.toInt()
//            c = c shr 32
//
////            assert c >= 0 && c <= 2;
//            x = c.toInt()
//        }
//    }
//
//    @JvmStatic
//    fun square(x: IntArray, z: IntArray) {
//        val tt = Nat128.createExt()
//        Nat128.square(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareN(x: IntArray, n: Int, z: IntArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat128.createExt()
//        Nat128.square(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            Nat128.square(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat128.sub(x, y, z)
//        if (c != 0) {
//            subPInvFrom(z)
//        }
//    }
//
//    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.sub(10, xx, yy, zz)
//        if (c != 0) {
//            Nat.subFrom(PExtInv.size, PExtInv, zz)
//        }
//    }
//
//    @JvmStatic
//    fun twice(x: IntArray, z: IntArray) {
//        val c = Nat.shiftUpBit(4, x, 0, z)
//        if (c != 0 || z[3] ushr 1 >= P3s1 && Nat128.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    private fun addPInvTo(z: IntArray) {
//        var c = (z[0].toLong() and M) + 1
//        z[0] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            c += z[1].toLong() and M
//            z[1] = c.toInt()
//            c = c shr 32
//            c += z[2].toLong() and M
//            z[2] = c.toInt()
//            c = c shr 32
//        }
//        c += (z[3].toLong() and M) + 2
//        z[3] = c.toInt()
//    }
//
//    private fun subPInvFrom(z: IntArray) {
//        var c = (z[0].toLong() and M) - 1
//        z[0] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            c += z[1].toLong() and M
//            z[1] = c.toInt()
//            c = c shr 32
//            c += z[2].toLong() and M
//            z[2] = c.toInt()
//            c = c shr 32
//        }
//        c += (z[3].toLong() and M) - 2
//        z[3] = c.toInt()
//    }
//}
