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

package org.spongycastle.math.ec.custom.djb

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.raw.Nat
import org.spongycastle.math.raw.Nat256
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic


object Curve25519Field {
    private const val M = 0xFFFFFFFFL

    // 2^255 - 2^4 - 2^1 - 1
    @JvmField
    val P = intArrayOf(
        -0x13, -0x1, -0x1, -0x1, -0x1, -0x1,
        -0x1, 0x7FFFFFFF
    )
    private const val P7 = 0x7FFFFFFF
    private val PExt = intArrayOf(
        0x00000169, 0x00000000, 0x00000000, 0x00000000, 0x00000000,
        0x00000000, 0x00000000, 0x00000000, -0x13, -0x1, -0x1, -0x1, -0x1, -0x1,
        -0x1, 0x3FFFFFFF
    )
    private const val PInv = 0x13
    @JvmStatic
    fun add(x: IntArray, y: IntArray, z: IntArray) {
        Nat256.add(x, y, z)
        if (Nat256.gte(z, P)) {
            subPFrom(z)
        }
    }

    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
        Nat.add(16, xx, yy, zz)
        if (Nat.gte(16, zz, PExt)) {
            subPExtFrom(zz)
        }
    }

    @JvmStatic
    fun addOne(x: IntArray, z: IntArray) {
        Nat.inc(8, x, z)
        if (Nat256.gte(z, P)) {
            subPFrom(z)
        }
    }

    @JvmStatic
    fun fromBigInteger(x: BigInteger): IntArray {
        val z = Nat256.fromBigInteger(x)
        while (Nat256.gte(z, P)) {
            Nat256.subFrom(P, z)
        }
        return z
    }

    fun half(x: IntArray, z: IntArray) {
        if (x[0] and 1 == 0) {
            Nat.shiftDownBit(8, x, 0, z)
        } else {
            Nat256.add(x, P, z)
            Nat.shiftDownBit(8, z, 0)
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
        Nat256.mulAddTo(x, y, zz)
        if (Nat.gte(16, zz, PExt)) {
            subPExtFrom(zz)
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
//        assert xx[15] >>> 30 == 0;
        val xx07 = xx[7]
        Nat.shiftUpBit(8, xx, 8, xx07, z, 0)
        var c = Nat256.mulByWordAddTo(PInv, xx, z) shl 1
        var z7 = z[7]
        c += (z7 ushr 31) - (xx07 ushr 31)
        z7 = z7 and P7
        z7 += Nat.addWordTo(7, c * PInv, z)
        z[7] = z7
        if (Nat256.gte(z, P)) {
            subPFrom(z)
        }
    }

    @JvmStatic
    fun reduce27(x: Int, z: IntArray) {
//        assert x >>> 26 == 0;
        var z7 = z[7]
        val c = x shl 1 or (z7 ushr 31)
        z7 = z7 and P7
        z7 += Nat.addWordTo(7, c * PInv, z)
        z[7] = z7
        if (Nat256.gte(z, P)) {
            subPFrom(z)
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
            addPTo(z)
        }
    }

    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
        val c = Nat.sub(16, xx, yy, zz)
        if (c != 0) {
            addPExtTo(zz)
        }
    }

    @JvmStatic
    fun twice(x: IntArray, z: IntArray) {
        Nat.shiftUpBit(8, x, 0, z)
        if (Nat256.gte(z, P)) {
            subPFrom(z)
        }
    }

    private fun addPTo(z: IntArray): Int {
        var c = (z[0].toLong() and M) - PInv
        z[0] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c = Nat.decAt(7, z, 1).toLong()
        }
        c += (z[7].toLong() and M) + ((P7 + 1).toLong() and M)
        z[7] = c.toInt()
        c = c shr 32
        return c.toInt()
    }

    private fun addPExtTo(zz: IntArray): Int {
        var c = (zz[0].toLong() and M) + (PExt[0].toLong() and M)
        zz[0] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c = Nat.incAt(8, zz, 1).toLong()
        }
        c += (zz[8].toLong() and M) - PInv
        zz[8] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c = Nat.decAt(15, zz, 9).toLong()
        }
        c += (zz[15].toLong() and M) + ((PExt[15] + 1).toLong() and M)
        zz[15] = c.toInt()
        c = c shr 32
        return c.toInt()
    }

    private fun subPFrom(z: IntArray): Int {
        var c = (z[0].toLong() and M) + PInv
        z[0] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c = Nat.incAt(7, z, 1).toLong()
        }
        c += (z[7].toLong() and M) - ((P7 + 1).toLong() and M)
        z[7] = c.toInt()
        c = c shr 32
        return c.toInt()
    }

    private fun subPExtFrom(zz: IntArray): Int {
        var c = (zz[0].toLong() and M) - (PExt[0].toLong() and M)
        zz[0] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c = Nat.decAt(8, zz, 1).toLong()
        }
        c += (zz[8].toLong() and M) + PInv
        zz[8] = c.toInt()
        c = c shr 32
        if (c != 0L) {
            c = Nat.incAt(15, zz, 9).toLong()
        }
        c += (zz[15].toLong() and M) - ((PExt[15] + 1).toLong() and M)
        zz[15] = c.toInt()
        c = c shr 32
        return c.toInt()
    }
}
