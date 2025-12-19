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

object SecP256K1Field {
    // 2^256 - 2^32 - 2^9 - 2^8 - 2^7 - 2^6 - 2^4 - 1
    @JvmField
    val P = intArrayOf(
        -0x3d1, -0x2, -0x1, -0x1, -0x1, -0x1,
        -0x1, -0x1
    )
    val PExt = intArrayOf(
        0x000E90A1, 0x000007A2, 0x00000001, 0x00000000, 0x00000000,
        0x00000000, 0x00000000, 0x00000000, -0x7a2, -0x3, -0x1, -0x1, -0x1, -0x1,
        -0x1, -0x1
    )
    private val PExtInv = intArrayOf(
        -0xe90a1, -0x7a3, -0x2, -0x1, -0x1,
        -0x1, -0x1, -0x1, 0x000007A1, 0x00000002
    )
    private const val P7 = -0x1
    private const val PExt15 = -0x1
    private const val PInv33 = 0x3D1
    @JvmStatic
    fun add(x: IntArray, y: IntArray, z: IntArray) {
        val c = Nat256.add(x, y, z)
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            Nat.add33To(8, PInv33, z)
        }
    }

    fun addExt(xx: IntArray, yy: IntArray, zz: IntArray) {
        val c = Nat.add(16, xx, yy, zz)
        if (c != 0 || zz[15] == PExt15 && Nat.gte(16, zz, PExt)) {
            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
                Nat.incAt(16, zz, PExtInv.size)
            }
        }
    }

    @JvmStatic
    fun addOne(x: IntArray, z: IntArray) {
        val c = Nat.inc(8, x, z)
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            Nat.add33To(8, PInv33, z)
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
        if (c != 0 || zz[15] == PExt15 && Nat.gte(16, zz, PExt)) {
            if (Nat.addTo(PExtInv.size, PExtInv, zz) != 0) {
                Nat.incAt(16, zz, PExtInv.size)
            }
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
        val cc = Nat256.mul33Add(PInv33, xx, 8, xx, 0, z, 0)
        val c = Nat256.mul33DWordAdd(PInv33, cc, z, 0)

        // assert c == 0L || c == 1L;
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            Nat.add33To(8, PInv33, z)
        }
    }

    @JvmStatic
    fun reduce32(x: Int, z: IntArray) {
        if (x != 0 && Nat256.mul33WordAdd(PInv33, x, z, 0) != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            Nat.add33To(8, PInv33, z)
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
            Nat.sub33From(8, PInv33, z)
        }
    }

    fun subtractExt(xx: IntArray, yy: IntArray, zz: IntArray) {
        val c = Nat.sub(16, xx, yy, zz)
        if (c != 0) {
            if (Nat.subFrom(PExtInv.size, PExtInv, zz) != 0) {
                Nat.decAt(16, zz, PExtInv.size)
            }
        }
    }

    @JvmStatic
    fun twice(x: IntArray, z: IntArray) {
        val c = Nat.shiftUpBit(8, x, 0, z)
        if (c != 0 || z[7] == P7 && Nat256.gte(z, P)) {
            Nat.add33To(8, PInv33, z)
        }
    }
}
