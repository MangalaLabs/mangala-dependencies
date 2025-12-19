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
//object SecP224K1Field {
//    // 2^224 - 2^32 - 2^12 - 2^11 - 2^9 - 2^7 - 2^4 - 2 - 1
//    @JvmField
//    val P = intArrayOf(
//        -0x1a93, -0x2, -0x1, -0x1, -0x1, -0x1,
//        -0x1
//    )
//    val PExt = intArrayOf(
//        0x02C23069, 0x00003526, 0x00000001, 0x00000000, 0x00000000,
//        0x00000000, 0x00000000, -0x3526, -0x3, -0x1, -0x1, -0x1, -0x1, -0x1
//    )
//    private val PExtInv = intArrayOf(
//        -0x2c23069, -0x3527, -0x2, -0x1, -0x1,
//        -0x1, -0x1, 0x00003525, 0x00000002
//    )
//    private const val P6 = -0x1
//    private const val PExt13 = -0x1
//    private const val PInv33 = 0x1A93
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat224.add(x, y, z)
//        if (c != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            Nat.add33To(7, PInv33, z)
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
//            Nat.add33To(7, PInv33, z)
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat224.fromBigInteger(x)
//        if (z[6] == P6 && Nat224.gte(z, P)) {
//            Nat.add33To(7, PInv33, z)
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
//        val cc = Nat224.mul33Add(PInv33, xx, 7, xx, 0, z, 0)
//        val c = Nat224.mul33DWordAdd(PInv33, cc, z, 0)
//
//        // assert c == 0L || c == 1L;
//        if (c != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            Nat.add33To(7, PInv33, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        if (x != 0 && Nat224.mul33WordAdd(PInv33, x, z, 0) != 0 || z[6] == P6 && Nat224.gte(z, P)) {
//            Nat.add33To(7, PInv33, z)
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
//            Nat.sub33From(7, PInv33, z)
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
//            Nat.add33To(7, PInv33, z)
//        }
//    }
//}
