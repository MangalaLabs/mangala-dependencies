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
//object SecP160R2Field {
//    // 2^160 - 2^32 - 2^14 - 2^12 - 2^9 - 2^8 - 2^7 - 2^3 - 2^2 - 1
//    @JvmField
//    val P = intArrayOf(-0x538d, -0x2, -0x1, -0x1, -0x1)
//    val PExt = intArrayOf(
//        0x1B44BBA9, 0x0000A71A, 0x00000001, 0x00000000, 0x00000000,
//        -0xa71a, -0x3, -0x1, -0x1, -0x1
//    )
//    private val PExtInv = intArrayOf(
//        -0x1b44bba9, -0xa71b, -0x2, -0x1, -0x1,
//        0x0000A719, 0x00000002
//    )
//    private const val P4 = -0x1
//    private const val PExt9 = -0x1
//    private const val PInv33 = 0x538D
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        val c = Nat160.add(x, y, z)
//        if (c != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.add33To(5, PInv33, z)
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
//            Nat.add33To(5, PInv33, z)
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
//        val cc = Nat160.mul33Add(PInv33, xx, 5, xx, 0, z, 0)
//        val c = Nat160.mul33DWordAdd(PInv33, cc, z, 0)
//
//        // assert c == 0 || c == 1;
//        if (c != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.add33To(5, PInv33, z)
//        }
//    }
//
//    @JvmStatic
//    fun reduce32(x: Int, z: IntArray) {
//        if (x != 0 && Nat160.mul33WordAdd(PInv33, x, z, 0) != 0 || z[4] == P4 && Nat160.gte(z, P)) {
//            Nat.add33To(5, PInv33, z)
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
//            Nat.sub33From(5, PInv33, z)
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
//            Nat.add33To(5, PInv33, z)
//        }
//    }
//}
