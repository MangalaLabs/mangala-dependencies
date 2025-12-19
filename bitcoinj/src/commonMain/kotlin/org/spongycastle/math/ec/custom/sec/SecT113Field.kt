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
//import org.spongycastle.math.raw.Interleave
//import org.spongycastle.math.raw.Nat128
//import java.math.BigInteger
//
//object SecT113Field {
//    private const val M49 = -1L ushr 15
//    private const val M57 = -1L ushr 7
//    @JvmStatic
//    fun add(x: LongArray, y: LongArray, z: LongArray) {
//        z[0] = x[0] xor y[0]
//        z[1] = x[1] xor y[1]
//    }
//
//    fun addExt(xx: LongArray, yy: LongArray, zz: LongArray) {
//        zz[0] = xx[0] xor yy[0]
//        zz[1] = xx[1] xor yy[1]
//        zz[2] = xx[2] xor yy[2]
//        zz[3] = xx[3] xor yy[3]
//    }
//
//    @JvmStatic
//    fun addOne(x: LongArray, z: LongArray) {
//        z[0] = x[0] xor 1L
//        z[1] = x[1]
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): LongArray {
//        val z = Nat128.fromBigInteger64(x)
//        reduce15(z, 0)
//        return z
//    }
//
//    @JvmStatic
//    fun invert(x: LongArray, z: LongArray) {
//        check(!Nat128.isZero64(x))
//
//        // Itoh-Tsujii inversion
//        val t0 = Nat128.create64()
//        val t1 = Nat128.create64()
//        square(x, t0)
//        multiply(t0, x, t0)
//        square(t0, t0)
//        multiply(t0, x, t0)
//        squareN(t0, 3, t1)
//        multiply(t1, t0, t1)
//        square(t1, t1)
//        multiply(t1, x, t1)
//        squareN(t1, 7, t0)
//        multiply(t0, t1, t0)
//        squareN(t0, 14, t1)
//        multiply(t1, t0, t1)
//        squareN(t1, 28, t0)
//        multiply(t0, t1, t0)
//        squareN(t0, 56, t1)
//        multiply(t1, t0, t1)
//        square(t1, z)
//    }
//
//    @JvmStatic
//    fun multiply(x: LongArray, y: LongArray, z: LongArray) {
//        val tt = Nat128.createExt64()
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: LongArray, y: LongArray, zz: LongArray) {
//        val tt = Nat128.createExt64()
//        implMultiply(x, y, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun reduce(xx: LongArray, z: LongArray) {
//        var x0 = xx[0]
//        var x1 = xx[1]
//        var x2 = xx[2]
//        val x3 = xx[3]
//        x1 = x1 xor (x3 shl 15 xor (x3 shl 24))
//        x2 = x2 xor (x3 ushr 49 xor (x3 ushr 40))
//        x0 = x0 xor (x2 shl 15 xor (x2 shl 24))
//        x1 = x1 xor (x2 ushr 49 xor (x2 ushr 40))
//        val t = x1 ushr 49
//        z[0] = x0 xor t xor (t shl 9)
//        z[1] = x1 and M49
//    }
//
//    fun reduce15(z: LongArray, zOff: Int) {
//        val z1 = z[zOff + 1]
//        val t = z1 ushr 49
//        z[zOff] = z[zOff] xor (t xor (t shl 9))
//        z[zOff + 1] = z1 and M49
//    }
//
//    @JvmStatic
//    fun sqrt(x: LongArray, z: LongArray) {
//        val u0 = Interleave.unshuffle(x[0])
//        val u1 = Interleave.unshuffle(x[1])
//        val e0 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        val c0 = u0 ushr 32 or (u1 and -0x100000000L)
//        z[0] = e0 xor (c0 shl 57) xor (c0 shl 5)
//        z[1] = c0 ushr 7 xor (c0 ushr 59)
//    }
//
//    @JvmStatic
//    fun square(x: LongArray, z: LongArray) {
//        val tt = Nat128.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareAddToExt(x: LongArray, zz: LongArray) {
//        val tt = Nat128.createExt64()
//        implSquare(x, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun squareN(x: LongArray, n: Int, z: LongArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat128.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            implSquare(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    fun trace(x: LongArray): Int {
//        // Non-zero-trace bits: 0
//        return x[0].toInt() and 1
//    }
//
//    internal fun implMultiply(x: LongArray, y: LongArray, zz: LongArray) {
//        /*
//         * "Three-way recursion" as described in "Batch binary Edwards", Daniel J. Bernstein.
//         */
//        var f0 = x[0]
//        var f1 = x[1]
//        f1 = f0 ushr 57 xor (f1 shl 7) and M57
//        f0 = f0 and M57
//        var g0 = y[0]
//        var g1 = y[1]
//        g1 = g0 ushr 57 xor (g1 shl 7) and M57
//        g0 = g0 and M57
//        val H = LongArray(6)
//        implMulw(f0, g0, H, 0) // H(0)       57/56 bits
//        implMulw(f1, g1, H, 2) // H(INF)     57/54 bits
//        implMulw(f0 xor f1, g0 xor g1, H, 4) // H(1)       57/56 bits
//        val r = H[1] xor H[2]
//        val z0 = H[0]
//        val z3 = H[3]
//        val z1 = H[4] xor z0 xor r
//        val z2 = H[5] xor z3 xor r
//        zz[0] = z0 xor (z1 shl 57)
//        zz[1] = z1 ushr 7 xor (z2 shl 50)
//        zz[2] = z2 ushr 14 xor (z3 shl 43)
//        zz[3] = z3 ushr 21
//    }
//
//    internal fun implMulw(x: Long, y: Long, z: LongArray, zOff: Int) {
////        assert x >>> 57 == 0;
////        assert y >>> 57 == 0;
//        val u = LongArray(8)
//        //      u[0] = 0;
//        u[1] = y
//        u[2] = u[1] shl 1
//        u[3] = u[2] xor y
//        u[4] = u[2] shl 1
//        u[5] = u[4] xor y
//        u[6] = u[3] shl 1
//        u[7] = u[6] xor y
//        var j = x.toInt()
//        var g: Long
//        var h: Long = 0
//        var l = u[j and 7]
//        var k = 48
//        do {
//            j = (x ushr k).toInt()
//            g = (u[j and 7]
//                    xor (u[j ushr 3 and 7] shl 3
//                    ) xor (u[j ushr 6 and 7] shl 6))
//            l = l xor (g shl k)
//            h = h xor (g ushr -k)
//        } while (9.let { k -= it; k } > 0)
//        h = h xor (x and 0x0100804020100800L and (y shl 7 shr 63) ushr 8)
//
////        assert h >>> 49 == 0;
//        z[zOff] = l and M57
//        z[zOff + 1] = l ushr 57 xor (h shl 7)
//    }
//
//    internal fun implSquare(x: LongArray, zz: LongArray) {
//        Interleave.expand64To128(x[0], zz, 0)
//        Interleave.expand64To128(x[1], zz, 2)
//    }
//}
