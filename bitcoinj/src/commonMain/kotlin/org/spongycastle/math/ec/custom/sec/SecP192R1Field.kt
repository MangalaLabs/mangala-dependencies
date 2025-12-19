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
//import org.spongycastle.math.raw.Nat192
//import java.math.BigInteger
//
//object SecP192R1Field {
//    private const val M = 0xFFFFFFFFL
//
//    // 2^192 - 2^64 - 1
//    @JvmField
//    val P = intArrayOf(-0x1, -0x1, -0x2, -0x1, -0x1, -0x1)
//    val PExt = intArrayOf(
//        0x00000001, 0x00000000, 0x00000002, 0x00000000, 0x00000001,
//        0x00000000, -0x2, -0x1, -0x3, -0x1, -0x1, -0x1
//    )
//    private val PExtInv = intArrayOf(
//        -0x1, -0x1, -0x3, -0x1, -0x2,
//        -0x1, 0x00000001, 0x00000000, 0x00000002
//    )
//    private const val P5 = -0x1
//    private const val PExt11 = -0x1
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat192.add(x, y, z)
//        if (c != 0 || z[5] == P5 && Nat192.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.add(12, xx, yy, zz)
//        if (c != 0 || zz[11] == PExt11 && Nat.gte(12, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(12, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: IntArray, z: IntArray) {
//        val c = Nat.inc(6, x, z)
//        if (c != 0 || z[5] == P5 && Nat192.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat192.fromBigInteger(x)
//        if (z[5] == P5 && Nat192.gte(z, P)) {
//            Nat192.subFrom(P, z)
//        }
//        return z
//    }
//
//    fun half(x: IntArray, z: IntArray) {
//        if (x[0] and 1 == 0) {
//            Nat.shiftDownBit(6, x, 0, z)
//        } else {
//            val c = Nat192.add(x, P, z)
//            Nat.shiftDownBit(6, z, c)
//        }
//    }
//
//    @JvmStatic
//    fun multiply(x: IntArray, y: IntArray, z: IntArray) {
//        val tt = Nat192.createExt()
//        Nat192.mul(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: IntArray, y: IntArray, zz: IntArray) {
//        val c = Nat192.mulAddTo(x, y, zz)
//        if (c != 0 || zz[11] == PExt11 && Nat.gte(12, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(12, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun negate(x: IntArray, z: IntArray) {
//        if (Nat192.isZero(x)) {
//            Nat192.zero(z)
//        } else {
//            Nat192.sub(P, x, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce(xx: IntArray, z: IntArray) {
//        val xx06 = xx[6].toLong() and M
//        val xx07 = xx[7].toLong() and M
//        val xx08 = xx[8].toLong() and M
//        val xx09 = xx[9].toLong() and M
//        val xx10 = xx[10].toLong() and M
//        val xx11 = xx[11].toLong() and M
//        var t0 = xx06 + xx10
//        var t1 = xx07 + xx11
//        var cc: Long = 0
//        cc += (xx[0].toLong() and M) + t0
//        val z0 = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[1].toLong() and M) + t1
//        z[1] = cc.toInt()
//        cc = cc shr 32
//        t0 += xx08
//        t1 += xx09
//        cc += (xx[2].toLong() and M) + t0
//        var z2 = cc and M
//        cc = cc shr 32
//        cc += (xx[3].toLong() and M) + t1
//        z[3] = cc.toInt()
//        cc = cc shr 32
//        t0 -= xx06
//        t1 -= xx07
//        cc += (xx[4].toLong() and M) + t0
//        z[4] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[5].toLong() and M) + t1
//        z[5] = cc.toInt()
//        cc = cc shr 32
//        z2 += cc
//        cc += z0.toLong() and M
//        z[0] = cc.toInt()
//        cc = cc shr 32
//        if (cc != 0L) {
//            cc += z[1].toLong() and M
//            z[1] = cc.toInt()
//            z2 += cc shr 32
//        }
//        z[2] = z2.toInt()
//        cc = z2 shr 32
//
////      assert cc == 0 || cc == 1;
//        if (cc != 0L && Nat.incAt(6, z, 3) != 0 || z[5] == P5 && Nat192.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        var cc: Long = 0
//        if (x != 0) {
//            val xx06 = x.toLong() and M
//            cc += (z[0].toLong() and M) + xx06
//            z[0] = cc.toInt()
//            cc = cc shr 32
//            if (cc != 0L) {
//                cc += z[1].toLong() and M
//                z[1] = cc.toInt()
//                cc = cc shr 32
//            }
//            cc += (z[2].toLong() and M) + xx06
//            z[2] = cc.toInt()
//            cc = cc shr 32
//
////            assert cc == 0 || cc == 1;
//        }
//        if (cc != 0L && Nat.incAt(6, z, 3) != 0 || z[5] == P5 && Nat192.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun square(x: IntArray, z: IntArray) {
//        val tt = Nat192.createExt()
//        Nat192.square(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareN(x: IntArray, n: Int, z: IntArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat192.createExt()
//        Nat192.square(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            Nat192.square(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat192.sub(x, y, z)
//        if (c != 0) {
//            subPInvFrom(z)
//        }
//    }
//
//    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.sub(12, xx, yy, zz)
//        if (c != 0) {
//            if (Nat.subFrom(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.decAt(12, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun twice(x: IntArray, z: IntArray) {
//        val c = Nat.shiftUpBit(6, x, 0, z)
//        if (c != 0 || z[5] == P5 && Nat192.gte(z, P)) {
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
//        }
//        c += (z[2].toLong() and M) + 1
//        z[2] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            Nat.incAt(6, z, 3)
//        }
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
//        }
//        c += (z[2].toLong() and M) - 1
//        z[2] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            Nat.decAt(6, z, 3)
//        }
//    }
//}
