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
//import org.spongycastle.math.raw.Nat224
//import java.math.BigInteger
//
//object SecP224R1Field {
//    private const val M = 0xFFFFFFFFL
//
//    // 2^224 - 2^96 + 1
//    @JvmField
//    val P = intArrayOf(0x00000001, 0x00000000, 0x00000000, -0x1, -0x1, -0x1, -0x1)
//    val PExt = intArrayOf(
//        0x00000001, 0x00000000, 0x00000000, -0x2, -0x1,
//        -0x1, 0x00000000, 0x00000002, 0x00000000, 0x00000000, -0x2, -0x1, -0x1, -0x1
//    )
//    private val PExtInv = intArrayOf(
//        -0x1, -0x1, -0x1, 0x00000001, 0x00000000,
//        0x00000000, -0x1, -0x3, -0x1, -0x1, 0x00000001
//    )
//    private const val P6 = -0x1
//    private const val PExt13 = -0x1
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat224.add(x, y, z)
//        if (c != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.add(14, xx, yy, zz)
//        if (c != 0 || zz[13] == PExt13 && Nat.gte(14, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(14, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: IntArray, z: IntArray) {
//        val c = Nat.inc(7, x, z)
//        if (c != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat224.fromBigInteger(x)
//        if (z[6] == P6 && Nat224.gte(z, P)) {
//            Nat224.subFrom(P, z)
//        }
//        return z
//    }
//
//    fun half(x: IntArray, z: IntArray) {
//        if (x[0] and 1 == 0) {
//            Nat.shiftDownBit(7, x, 0, z)
//        } else {
//            val c = Nat224.add(x, P, z)
//            Nat.shiftDownBit(7, z, c)
//        }
//    }
//
//    @JvmStatic
//    fun multiply(x: IntArray, y: IntArray, z: IntArray) {
//        val tt = Nat224.createExt()
//        Nat224.mul(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: IntArray, y: IntArray, zz: IntArray) {
//        val c = Nat224.mulAddTo(x, y, zz)
//        if (c != 0 || zz[13] == PExt13 && Nat.gte(14, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(14, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun negate(x: IntArray, z: IntArray) {
//        if (Nat224.isZero(x)) {
//            Nat224.zero(z)
//        } else {
//            Nat224.sub(P, x, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce(xx: IntArray, z: IntArray) {
//        val xx10 = xx[10].toLong() and M
//        val xx11 = xx[11].toLong() and M
//        val xx12 = xx[12].toLong() and M
//        val xx13 = xx[13].toLong() and M
//        val n: Long = 1
//        val t0 = (xx[7].toLong() and M) + xx11 - n
//        val t1 = (xx[8].toLong() and M) + xx12
//        val t2 = (xx[9].toLong() and M) + xx13
//        var cc: Long = 0
//        cc += (xx[0].toLong() and M) - t0
//        var z0 = cc and M
//        cc = cc shr 32
//        cc += (xx[1].toLong() and M) - t1
//        z[1] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[2].toLong() and M) - t2
//        z[2] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[3].toLong() and M) + t0 - xx10
//        var z3 = cc and M
//        cc = cc shr 32
//        cc += (xx[4].toLong() and M) + t1 - xx11
//        z[4] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[5].toLong() and M) + t2 - xx12
//        z[5] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[6].toLong() and M) + xx10 - xx13
//        z[6] = cc.toInt()
//        cc = cc shr 32
//        cc += n
//
////        assert cc >= 0;
//        z3 += cc
//        z0 -= cc
//        z[0] = z0.toInt()
//        cc = z0 shr 32
//        if (cc != 0L) {
//            cc += z[1].toLong() and M
//            z[1] = cc.toInt()
//            cc = cc shr 32
//            cc += z[2].toLong() and M
//            z[2] = cc.toInt()
//            z3 += cc shr 32
//        }
//        z[3] = z3.toInt()
//        cc = z3 shr 32
//
////        assert cc == 0 || cc == 1;
//        if (cc != 0L && Nat.incAt(7, z, 4) != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        var cc: Long = 0
//        if (x != 0) {
//            val xx07 = x.toLong() and M
//            cc += (z[0].toLong() and M) - xx07
//            z[0] = cc.toInt()
//            cc = cc shr 32
//            if (cc != 0L) {
//                cc += z[1].toLong() and M
//                z[1] = cc.toInt()
//                cc = cc shr 32
//                cc += z[2].toLong() and M
//                z[2] = cc.toInt()
//                cc = cc shr 32
//            }
//            cc += (z[3].toLong() and M) + xx07
//            z[3] = cc.toInt()
//            cc = cc shr 32
//
////            assert cc == 0 || cc == 1;
//        }
//        if (cc != 0L && Nat.incAt(7, z, 4) != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun square(x: IntArray, z: IntArray) {
//        val tt = Nat224.createExt()
//        Nat224.square(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareN(x: IntArray, n: Int, z: IntArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat224.createExt()
//        Nat224.square(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            Nat224.square(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat224.sub(x, y, z)
//        if (c != 0) {
//            subPInvFrom(z)
//        }
//    }
//
//    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.sub(14, xx, yy, zz)
//        if (c != 0) {
//            if (Nat.subFrom(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.decAt(14, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun twice(x: IntArray, z: IntArray) {
//        val c = Nat.shiftUpBit(7, x, 0, z)
//        if (c != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    private fun addPInvTo(z: IntArray) {
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
//        c += (z[3].toLong() and M) + 1
//        z[3] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            Nat.incAt(7, z, 4)
//        }
//    }
//
//    private fun subPInvFrom(z: IntArray) {
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
//        c += (z[3].toLong() and M) - 1
//        z[3] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            Nat.decAt(7, z, 4)
//        }
//    }
//}
