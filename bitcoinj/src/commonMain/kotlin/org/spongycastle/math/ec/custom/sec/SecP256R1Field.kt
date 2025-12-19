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

package org.spongycastle.math.ec.custom.sec

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.raw.Nat
import org.spongycastle.math.raw.Nat256
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object SecP256R1Field {
    private const val M = 0xFFFFFFFFL

    // 2^256 - 2^224 + 2^192 + 2^96 - 1
    @JvmField
    val P = intArrayOf(
        -0x1, -0x1, -0x1, 0x00000000, 0x00000000, 0x00000000,
        0x00000001, -0x1
    )
    val PExt = intArrayOf(
        0x00000001, 0x00000000, 0x00000000, -0x2, -0x1,
        -0x1, -0x2, 0x00000001, -0x2, 0x00000001, -0x2, 0x00000001, 0x00000001, -0x2,
        0x00000002, -0x2
    )
    private const val P7 = -0x1
    private const val PExt15s1 = -0x2 ushr 1
    @JvmStatic
    fun add(x: IntArray, y: IntArray, z: IntArray) {
        val c = Nat256.add(x, y, z)
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            addPInvTo(z)
        }
    }

    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
        val c = Nat.add(16, xx, yy, zz)
        if (c != 0 || zz[15] ushr 1 >= PExt15s1 && Nat.gte(16, zz, PExt)) {
            Nat.subFrom(16, PExt, zz)
        }
    }

    @JvmStatic
    fun addOne(x: IntArray, z: IntArray) {
        val c = Nat.inc(8, x, z)
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            addPInvTo(z)
        }
    }

    @JvmStatic
    fun fromBigInteger(x: BigInteger): IntArray {
        val z = Nat256.fromBigInteger(x)
        if (z[7] == P7 && Nat256.gte(z, P)) {
            Nat256.subFrom(P, z)
        }
        return z
    }

    fun half(x: IntArray, z: IntArray) {
        if (x[0] and 1 == 0) {
            Nat.shiftDownBit(8, x, 0, z)
        } else {
            val c = Nat256.add(x, P, z)
            Nat.shiftDownBit(8, z, c)
        }
    }

    @JvmStatic
    fun multiply(x: IntArray, y: IntArray, z: IntArray) {
        val tt = Nat256.createExt()
        Nat256.mul(x, y, tt)
        reduce(tt, z)
    }

    @JvmStatic
    fun multiplyAddToExt(x: IntArray, y: IntArray, zz: IntArray) {
        val c = Nat256.mulAddTo(x, y, zz)
        if (c != 0 || zz[15] ushr 1 >= PExt15s1 && Nat.gte(16, zz, PExt)) {
            Nat.subFrom(16, PExt, zz)
        }
    }

    @JvmStatic
    fun negate(x: IntArray, z: IntArray) {
        if (Nat256.isZero(x)) {
            Nat256.zero(z)
        } else {
            Nat256.sub(P, x, z)
        }
    }

    @JvmStatic
    fun reduce(xx: IntArray, z: IntArray) {
        var xx08 = xx[8].toLong() and M
        val xx09 = xx[9].toLong() and M
        val xx10 = xx[10].toLong() and M
        val xx11 = xx[11].toLong() and M
        val xx12 = xx[12].toLong() and M
        val xx13 = xx[13].toLong() and M
        val xx14 = xx[14].toLong() and M
        val xx15 = xx[15].toLong() and M
        val n: Long = 6
        xx08 -= n
        val t0 = xx08 + xx09
        val t1 = xx09 + xx10
        val t2 = xx10 + xx11 - xx15
        val t3 = xx11 + xx12
        val t4 = xx12 + xx13
        val t5 = xx13 + xx14
        val t6 = xx14 + xx15
        val t7 = t5 - t0
        var cc: Long = 0
        cc += (xx[0].toLong() and M) - t3 - t7
        z[0] = cc.toInt()
        cc = cc shr 32
        cc += (xx[1].toLong() and M) + t1 - t4 - t6
        z[1] = cc.toInt()
        cc = cc shr 32
        cc += (xx[2].toLong() and M) + t2 - t5
        z[2] = cc.toInt()
        cc = cc shr 32
        cc += (xx[3].toLong() and M) + (t3 shl 1) + t7 - t6
        z[3] = cc.toInt()
        cc = cc shr 32
        cc += (xx[4].toLong() and M) + (t4 shl 1) + xx14 - t1
        z[4] = cc.toInt()
        cc = cc shr 32
        cc += (xx[5].toLong() and M) + (t5 shl 1) - t2
        z[5] = cc.toInt()
        cc = cc shr 32
        cc += (xx[6].toLong() and M) + (t6 shl 1) + t7
        z[6] = cc.toInt()
        cc = cc shr 32
        cc += (xx[7].toLong() and M) + (xx15 shl 1) + xx08 - t2 - t4
        z[7] = cc.toInt()
        cc = cc shr 32
        cc += n

//        assert cc >= 0;
        reduce32(cc.toInt(), z)
    }

    @JvmStatic
    fun reduce32(x: Int, z: IntArray) {
        var cc: Long = 0
        if (x != 0) {
            val xx08 = x.toLong() and M
            cc += (z[0].toLong() and M) + xx08
            z[0] = cc.toInt()
            cc = cc shr 32
            if (cc != 0L) {
                cc += z[1].toLong() and M
                z[1] = cc.toInt()
                cc = cc shr 32
                cc += z[2].toLong() and M
                z[2] = cc.toInt()
                cc = cc shr 32
            }
            cc += (z[3].toLong() and M) - xx08
            z[3] = cc.toInt()
            cc = cc shr 32
            if (cc != 0L) {
                cc += z[4].toLong() and M
                z[4] = cc.toInt()
                cc = cc shr 32
                cc += z[5].toLong() and M
                z[5] = cc.toInt()
                cc = cc shr 32
            }
            cc += (z[6].toLong() and M) - xx08
            z[6] = cc.toInt()
            cc = cc shr 32
            cc += (z[7].toLong() and M) + xx08
            z[7] = cc.toInt()
            cc = cc shr 32

//          assert cc == 0 || cc == 1;
        }
        if (cc != 0L || z[7] == P7 && Nat256.gte(z, P)) {
            addPInvTo(z)
        }
    }

    @JvmStatic
    fun square(x: IntArray, z: IntArray) {
        val tt = Nat256.createExt()
        Nat256.square(x, tt)
        reduce(tt, z)
    }

    @JvmStatic
    fun squareN(x: IntArray, n: Int, z: IntArray) {
//        assert n > 0;
        var n = n
        val tt = Nat256.createExt()
        Nat256.square(x, tt)
        reduce(tt, z)
        while (--n > 0) {
            Nat256.square(z, tt)
            reduce(tt, z)
        }
    }

    @JvmStatic
    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
        val c = Nat256.sub(x, y, z)
        if (c != 0) {
            subPInvFrom(z)
        }
    }

    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
        val c = Nat.sub(16, xx, yy, zz)
        if (c != 0) {
            Nat.addTo(16, PExt, zz)
        }
    }

    @JvmStatic
    fun twice(x: IntArray, z: IntArray) {
        val c = Nat.shiftUpBit(8, x, 0, z)
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            addPInvTo(z)
        }
    }

    private fun addPInvTo(z: IntArray) {
        var c = (z[0].toLong() and M) + 1
        z[0] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c += z[1].toLong() and M
            z[1] = c.toInt()
            c = c shr 32
            c += z[2].toLong() and M
            z[2] = c.toInt()
            c = c shr 32
        }
        c += (z[3].toLong() and M) - 1
        z[3] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c += z[4].toLong() and M
            z[4] = c.toInt()
            c = c shr 32
            c += z[5].toLong() and M
            z[5] = c.toInt()
            c = c shr 32
        }
        c += (z[6].toLong() and M) - 1
        z[6] = c.toInt()
        c = c shr 32
        c += (z[7].toLong() and M) + 1
        z[7] = c.toInt()
        //        c >>= 32;
    }

    private fun subPInvFrom(z: IntArray) {
        var c = (z[0].toLong() and M) - 1
        z[0] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c += z[1].toLong() and M
            z[1] = c.toInt()
            c = c shr 32
            c += z[2].toLong() and M
            z[2] = c.toInt()
            c = c shr 32
        }
        c += (z[3].toLong() and M) + 1
        z[3] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c += z[4].toLong() and M
            z[4] = c.toInt()
            c = c shr 32
            c += z[5].toLong() and M
            z[5] = c.toInt()
            c = c shr 32
        }
        c += (z[6].toLong() and M) + 1
        z[6] = c.toInt()
        c = c shr 32
        c += (z[7].toLong() and M) - 1
        z[7] = c.toInt()
        //        c >>= 32;
    }
}
