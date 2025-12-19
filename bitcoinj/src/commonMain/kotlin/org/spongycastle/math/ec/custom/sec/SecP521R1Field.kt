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
//import org.spongycastle.math.raw.Nat512
//import java.math.BigInteger
//
//object SecP521R1Field {
//    // 2^521 - 1
//    @JvmField
//    val P = intArrayOf(
//        -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1,
//        -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, 0x1FF
//    )
//    private const val P16 = 0x1FF
//    @JvmStatic
//    fun add(x: IntArray, y: IntArray, z: IntArray) {
//        var c = Nat.add(16, x, y, z) + x[16] + y[16]
//        if (c > P16 || c == P16 && Nat.eq(16, z, P)) {
//            c += Nat.inc(16, z)
//            c = c and P16
//        }
//        z[16] = c
//    }
//
//    @JvmStatic
//    fun addOne(x: IntArray, z: IntArray) {
//        var c = Nat.inc(16, x, z) + x[16]
//        if (c > P16 || c == P16 && Nat.eq(16, z, P)) {
//            c += Nat.inc(16, z)
//            c = c and P16
//        }
//        z[16] = c
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): IntArray {
//        val z = Nat.fromBigInteger(521, x)
//        if (Nat.eq(17, z, P)) {
//            Nat.zero(17, z)
//        }
//        return z
//    }
//
//    fun half(x: IntArray, z: IntArray) {
//        val x16 = x[16]
//        val c = Nat.shiftDownBit(16, x, x16, z)
//        z[16] = x16 ushr 1 or (c ushr 23)
//    }
//
//    @JvmStatic
//    fun multiply(x: IntArray, y: IntArray, z: IntArray) {
//        val tt = Nat.create(33)
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun negate(x: IntArray, z: IntArray) {
//        if (Nat.isZero(17, x)) {
//            Nat.zero(17, z)
//        } else {
//            Nat.sub(17, P, x, z)
//        }
//    }
//
//    fun reduce(xx: IntArray, z: IntArray) {
////        assert xx[32] >>> 18 == 0;
//        val xx32 = xx[32]
//        var c = Nat.shiftDownBits(16, xx, 16, 9, xx32, z, 0) ushr 23
//        c += xx32 ushr 9
//        c += Nat.addTo(16, xx, z)
//        if (c > P16 || c == P16 && Nat.eq(16, z, P)) {
//            c += Nat.inc(16, z)
//            c = c and P16
//        }
//        z[16] = c
//    }
//
//    @JvmStatic
//    fun reduce23(z: IntArray) {
//        val z16 = z[16]
//        var c = Nat.addWordTo(16, z16 ushr 9, z) + (z16 and P16)
//        if (c > P16 || c == P16 && Nat.eq(16, z, P)) {
//            c += Nat.inc(16, z)
//            c = c and P16
//        }
//        z[16] = c
//    }
//
//    @JvmStatic
//    fun square(x: IntArray, z: IntArray) {
//        val tt = Nat.create(33)
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareN(x: IntArray, n: Int, z: IntArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat.create(33)
//        implSquare(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            implSquare(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun subtract(x: IntArray, y: IntArray, z: IntArray) {
//        var c = Nat.sub(16, x, y, z) + x[16] - y[16]
//        if (c < 0) {
//            c += Nat.dec(16, z)
//            c = c and P16
//        }
//        z[16] = c
//    }
//
//    @JvmStatic
//    fun twice(x: IntArray, z: IntArray) {
//        val x16 = x[16]
//        val c = Nat.shiftUpBit(16, x, x16 shl 23, z) or (x16 shl 1)
//        z[16] = c and P16
//    }
//
//    internal fun implMultiply(x: IntArray, y: IntArray, zz: IntArray) {
//        Nat512.mul(x, y, zz)
//        val x16 = x[16]
//        val y16 = y[16]
//        zz[32] = Nat.mul31BothAdd(16, x16, y, y16, x, zz, 16) + x16 * y16
//    }
//
//    internal fun implSquare(x: IntArray, zz: IntArray) {
//        Nat512.square(x, zz)
//        val x16 = x[16]
//        zz[32] = Nat.mulWordAddTo(16, x16 shl 1, x, 0, zz, 16) + x16 * x16
//    }
//}
