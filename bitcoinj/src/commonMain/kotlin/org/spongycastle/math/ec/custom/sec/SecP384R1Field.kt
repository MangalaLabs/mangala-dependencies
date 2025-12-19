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
//import org.spongycastle.math.raw.Nat384
//import java.math.BigInteger
//
//object SecP384R1Field {
//    private const val M = 0xFFFFFFFFL
//
//    // 2^384 - 2^128 - 2^96 + 2^32 - 1
//    @JvmField
//    val P = intArrayOf(
//        -0x1, 0x00000000, 0x00000000, -0x1, -0x2, -0x1,
//        -0x1, -0x1, -0x1, -0x1, -0x1, -0x1
//    )
//    val PExt = intArrayOf(
//        0x00000001,
//        -0x2,
//        0x00000000,
//        0x00000002,
//        0x00000000,
//        -0x2,
//        0x00000000,
//        0x00000002,
//        0x00000001,
//        0x00000000,
//        0x00000000,
//        0x00000000,
//        -0x2,
//        0x00000001,
//        0x00000000,
//        -0x2,
//        -0x3,
//        -0x1,
//        -0x1,
//        -0x1,
//        -0x1,
//        -0x1,
//        -0x1,
//        -0x1
//    )
//    private val PExtInv = intArrayOf(
//        -0x1, 0x00000001, -0x1, -0x3, -0x1, 0x00000001,
//        -0x1, -0x3, -0x2, -0x1, -0x1, -0x1, 0x00000001, -0x2, -0x1,
//        0x00000001, 0x00000002
//    )
//    private const val P11 = -0x1
//    private const val PExt23 = -0x1
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat.add(12, x, y, z)
//        if (c != 0 || z[11] == P11 && Nat.gte(12, z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.add(24, xx, yy, zz)
//        if (c != 0 || zz[23] == PExt23 && Nat.gte(24, zz, PExt)) {
//            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.incAt(24, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: IntArray, z: IntArray) {
//        val c = Nat.inc(12, x, z)
//        if (c != 0 || z[11] == P11 && Nat.gte(12, z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat.fromBigInteger(384, x)
//        if (z[11] == P11 && Nat.gte(12, z, P)) {
//            Nat.subFrom(12, P, z)
//        }
//        return z
//    }
//
//    fun half(x: IntArray, z: IntArray) {
//        if (x[0] and 1 == 0) {
//            Nat.shiftDownBit(12, x, 0, z)
//        } else {
//            val c = Nat.add(12, x, P, z)
//            Nat.shiftDownBit(12, z, c)
//        }
//    }
//
//    @JvmStatic
//    fun multiply(x: IntArray?, y: IntArray?, z: IntArray) {
//        val tt = Nat.create(24)
//        Nat384.mul(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun negate(x: IntArray, z: IntArray) {
//        if (Nat.isZero(12, x)) {
//            Nat.zero(12, z)
//        } else {
//            Nat.sub(12, P, x, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce(xx: IntArray, z: IntArray) {
//        val xx16 = xx[16].toLong() and M
//        val xx17 = xx[17].toLong() and M
//        val xx18 = xx[18].toLong() and M
//        val xx19 = xx[19].toLong() and M
//        val xx20 = xx[20].toLong() and M
//        val xx21 = xx[21].toLong() and M
//        val xx22 = xx[22].toLong() and M
//        val xx23 = xx[23].toLong() and M
//        val n: Long = 1
//        val t0 = (xx[12].toLong() and M) + xx20 - n
//        val t1 = (xx[13].toLong() and M) + xx22
//        val t2 = (xx[14].toLong() and M) + xx22 + xx23
//        val t3 = (xx[15].toLong() and M) + xx23
//        val t4 = xx17 + xx21
//        val t5 = xx21 - xx23
//        val t6 = xx22 - xx23
//        val t7 = t0 + t5
//        var cc: Long = 0
//        cc += (xx[0].toLong() and M) + t7
//        z[0] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[1].toLong() and M) + xx23 - t0 + t1
//        z[1] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[2].toLong() and M) - xx21 - t1 + t2
//        z[2] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[3].toLong() and M) - t2 + t3 + t7
//        z[3] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[4].toLong() and M) + xx16 + xx21 + t1 - t3 + t7
//        z[4] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[5].toLong() and M) - xx16 + t1 + t2 + t4
//        z[5] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[6].toLong() and M) + xx18 - xx17 + t2 + t3
//        z[6] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[7].toLong() and M) + xx16 + xx19 - xx18 + t3
//        z[7] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[8].toLong() and M) + xx16 + xx17 + xx20 - xx19
//        z[8] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[9].toLong() and M) + xx18 - xx20 + t4
//        z[9] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[10].toLong() and M) + xx18 + xx19 - t5 + t6
//        z[10] = cc.toInt()
//        cc = cc shr 32
//        cc += (xx[11].toLong() and M) + xx19 + xx20 - t6
//        z[11] = cc.toInt()
//        cc = cc shr 32
//        cc += n
//
////        assert cc >= 0;
//        reduce32(cc.toInt(), z)
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        var cc: Long = 0
//        if (x != 0) {
//            val xx12 = x.toLong() and M
//            cc += (z[0].toLong() and M) + xx12
//            z[0] = cc.toInt()
//            cc = cc shr 32
//            cc += (z[1].toLong() and M) - xx12
//            z[1] = cc.toInt()
//            cc = cc shr 32
//            if (cc != 0L) {
//                cc += z[2].toLong() and M
//                z[2] = cc.toInt()
//                cc = cc shr 32
//            }
//            cc += (z[3].toLong() and M) + xx12
//            z[3] = cc.toInt()
//            cc = cc shr 32
//            cc += (z[4].toLong() and M) + xx12
//            z[4] = cc.toInt()
//            cc = cc shr 32
//
////            assert cc == 0 || cc == 1;
//        }
//        if (cc != 0L && Nat.incAt(12, z, 5) != 0 || z[11] == P11 && Nat.gte(12, z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    @JvmStatic
//    fun square(x: IntArray?, z: IntArray) {
//        val tt = Nat.create(24)
//        Nat384.square(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareN(x: IntArray?, n: Int, z: IntArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat.create(24)
//        Nat384.square(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            Nat384.square(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat.sub(12, x, y, z)
//        if (c != 0) {
//            subPInvFrom(z)
//        }
//    }
//
//    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
//        val c = Nat.sub(24, xx, yy, zz)
//        if (c != 0) {
//            if (Nat.subFrom(PExtInv.size, PExtInv, zz) != 0) {
//                Nat.decAt(24, zz, PExtInv.size)
//            }
//        }
//    }
//
//    @JvmStatic
//    fun twice(x: IntArray, z: IntArray) {
//        val c = Nat.shiftUpBit(12, x, 0, z)
//        if (c != 0 || z[11] == P11 && Nat.gte(12, z, P)) {
//            addPInvTo(z)
//        }
//    }
//
//    private fun addPInvTo(z: IntArray) {
//        var c = (z[0].toLong() and M) + 1
//        z[0] = c.toInt()
//        c = c shr 32
//        c += (z[1].toLong() and M) - 1
//        z[1] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            c += z[2].toLong() and M
//            z[2] = c.toInt()
//            c = c shr 32
//        }
//        c += (z[3].toLong() and M) + 1
//        z[3] = c.toInt()
//        c = c shr 32
//        c += (z[4].toLong() and M) + 1
//        z[4] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            Nat.incAt(12, z, 5)
//        }
//    }
//
//    private fun subPInvFrom(z: IntArray) {
//        var c = (z[0].toLong() and M) - 1
//        z[0] = c.toInt()
//        c = c shr 32
//        c += (z[1].toLong() and M) + 1
//        z[1] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            c += z[2].toLong() and M
//            z[2] = c.toInt()
//            c = c shr 32
//        }
//        c += (z[3].toLong() and M) - 1
//        z[3] = c.toInt()
//        c = c shr 32
//        c += (z[4].toLong() and M) - 1
//        z[4] = c.toInt()
//        c = c shr 32
//        if (c != 0L) {
//            Nat.decAt(12, z, 5)
//        }
//    }
//}
