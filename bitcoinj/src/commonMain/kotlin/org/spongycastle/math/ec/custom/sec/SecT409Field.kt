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
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.math.raw.Nat448
//import java.math.BigInteger
//
//object SecT409Field {
//    private const val M25 = -1L ushr 39
//    private const val M59 = -1L ushr 5
//    @JvmStatic
//    fun add(x: LongArray, y: LongArray, z: LongArray) {
//        z[0] = x[0] xor y[0]
//        z[1] = x[1] xor y[1]
//        z[2] = x[2] xor y[2]
//        z[3] = x[3] xor y[3]
//        z[4] = x[4] xor y[4]
//        z[5] = x[5] xor y[5]
//        z[6] = x[6] xor y[6]
//    }
//
//    fun addExt(xx: LongArray, yy: LongArray, zz: LongArray) {
//        for (i in 0..12) {
//            zz[i] = xx[i] xor yy[i]
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: LongArray, z: LongArray) {
//        z[0] = x[0] xor 1L
//        z[1] = x[1]
//        z[2] = x[2]
//        z[3] = x[3]
//        z[4] = x[4]
//        z[5] = x[5]
//        z[6] = x[6]
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): LongArray {
//        val z = Nat448.fromBigInteger64(x)
//        reduce39(z, 0)
//        return z
//    }
//
//    @JvmStatic
//    fun invert(x: LongArray, z: LongArray) {
//        check(!Nat448.isZero64(x))
//
//        // Itoh-Tsujii inversion with bases { 2, 3 }
//        val t0 = Nat448.create64()
//        val t1 = Nat448.create64()
//        val t2 = Nat448.create64()
//        square(x, t0)
//
//        // 3 | 408
//        squareN(t0, 1, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 1, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 136
//        squareN(t0, 3, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 68
//        squareN(t0, 6, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 34
//        squareN(t0, 12, t1)
//        multiply(t0, t1, t2)
//
//        // ! {2,3} | 17
//        squareN(t2, 24, t0)
//        squareN(t0, 24, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 8
//        squareN(t0, 48, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 4
//        squareN(t0, 96, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 2
//        squareN(t0, 192, t1)
//        multiply(t0, t1, t0)
//        multiply(t0, t2, z)
//    }
//
//    @JvmStatic
//    fun multiply(x: LongArray, y: LongArray, z: LongArray) {
//        val tt = Nat448.createExt64()
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: LongArray, y: LongArray, zz: LongArray) {
//        val tt = Nat448.createExt64()
//        implMultiply(x, y, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun reduce(xx: LongArray, z: LongArray) {
//        var x00 = xx[0]
//        var x01 = xx[1]
//        var x02 = xx[2]
//        var x03 = xx[3]
//        var x04 = xx[4]
//        var x05 = xx[5]
//        var x06 = xx[6]
//        var x07 = xx[7]
//        var u = xx[12]
//        x05 = x05 xor (u shl 39)
//        x06 = x06 xor (u ushr 25 xor (u shl 62))
//        x07 = x07 xor (u ushr 2)
//        u = xx[11]
//        x04 = x04 xor (u shl 39)
//        x05 = x05 xor (u ushr 25 xor (u shl 62))
//        x06 = x06 xor (u ushr 2)
//        u = xx[10]
//        x03 = x03 xor (u shl 39)
//        x04 = x04 xor (u ushr 25 xor (u shl 62))
//        x05 = x05 xor (u ushr 2)
//        u = xx[9]
//        x02 = x02 xor (u shl 39)
//        x03 = x03 xor (u ushr 25 xor (u shl 62))
//        x04 = x04 xor (u ushr 2)
//        u = xx[8]
//        x01 = x01 xor (u shl 39)
//        x02 = x02 xor (u ushr 25 xor (u shl 62))
//        x03 = x03 xor (u ushr 2)
//        u = x07
//        x00 = x00 xor (u shl 39)
//        x01 = x01 xor (u ushr 25 xor (u shl 62))
//        x02 = x02 xor (u ushr 2)
//        val t = x06 ushr 25
//        z[0] = x00 xor t
//        z[1] = x01 xor (t shl 23)
//        z[2] = x02
//        z[3] = x03
//        z[4] = x04
//        z[5] = x05
//        z[6] = x06 and M25
//    }
//
//    fun reduce39(z: LongArray, zOff: Int) {
//        val z6 = z[zOff + 6]
//        val t = z6 ushr 25
//        z[zOff] = z[zOff] xor t
//        z[zOff + 1] = z[zOff + 1] xor (t shl 23)
//        z[zOff + 6] = z6 and M25
//    }
//
//    @JvmStatic
//    fun sqrt(x: LongArray, z: LongArray) {
//        var u0: Long
//        var u1: Long
//        u0 = Interleave.unshuffle(x[0])
//        u1 = Interleave.unshuffle(x[1])
//        val e0 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        val c0 = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[2])
//        u1 = Interleave.unshuffle(x[3])
//        val e1 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        val c1 = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[4])
//        u1 = Interleave.unshuffle(x[5])
//        val e2 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        val c2 = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[6])
//        val e3 = u0 and 0x00000000FFFFFFFFL
//        val c3 = u0 ushr 32
//        z[0] = e0 xor (c0 shl 44)
//        z[1] = e1 xor (c1 shl 44) xor (c0 ushr 20)
//        z[2] = e2 xor (c2 shl 44) xor (c1 ushr 20)
//        z[3] = e3 xor (c3 shl 44) xor (c2 ushr 20) xor (c0 shl 13)
//        z[4] = c3 ushr 20 xor (c1 shl 13) xor (c0 ushr 51)
//        z[5] = c2 shl 13 xor (c1 ushr 51)
//        z[6] = c3 shl 13 xor (c2 ushr 51)
//
////        assert (c3 >>> 51) == 0;
//    }
//
//    @JvmStatic
//    fun square(x: LongArray, z: LongArray) {
//        val tt = Nat.create64(13)
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareAddToExt(x: LongArray, zz: LongArray) {
//        val tt = Nat.create64(13)
//        implSquare(x, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun squareN(x: LongArray, n: Int, z: LongArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat.create64(13)
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
//    internal fun implCompactExt(zz: LongArray) {
//        val z00 = zz[0]
//        val z01 = zz[1]
//        val z02 = zz[2]
//        val z03 = zz[3]
//        val z04 = zz[4]
//        val z05 = zz[5]
//        val z06 = zz[6]
//        val z07 = zz[7]
//        val z08 = zz[8]
//        val z09 = zz[9]
//        val z10 = zz[10]
//        val z11 = zz[11]
//        val z12 = zz[12]
//        val z13 = zz[13]
//        zz[0] = z00 xor (z01 shl 59)
//        zz[1] = z01 ushr 5 xor (z02 shl 54)
//        zz[2] = z02 ushr 10 xor (z03 shl 49)
//        zz[3] = z03 ushr 15 xor (z04 shl 44)
//        zz[4] = z04 ushr 20 xor (z05 shl 39)
//        zz[5] = z05 ushr 25 xor (z06 shl 34)
//        zz[6] = z06 ushr 30 xor (z07 shl 29)
//        zz[7] = z07 ushr 35 xor (z08 shl 24)
//        zz[8] = z08 ushr 40 xor (z09 shl 19)
//        zz[9] = z09 ushr 45 xor (z10 shl 14)
//        zz[10] = z10 ushr 50 xor (z11 shl 9)
//        zz[11] = (z11 ushr 55 xor (z12 shl 4)
//                xor (z13 shl 63))
//        zz[12] = (z12 ushr 60
//                xor (z13 ushr 1))
//        zz[13] = 0
//    }
//
//    internal fun implExpand(x: LongArray, z: LongArray) {
//        val x0 = x[0]
//        val x1 = x[1]
//        val x2 = x[2]
//        val x3 = x[3]
//        val x4 = x[4]
//        val x5 = x[5]
//        val x6 = x[6]
//        z[0] = x0 and M59
//        z[1] = x0 ushr 59 xor (x1 shl 5) and M59
//        z[2] = x1 ushr 54 xor (x2 shl 10) and M59
//        z[3] = x2 ushr 49 xor (x3 shl 15) and M59
//        z[4] = x3 ushr 44 xor (x4 shl 20) and M59
//        z[5] = x4 ushr 39 xor (x5 shl 25) and M59
//        z[6] = x5 ushr 34 xor (x6 shl 30)
//    }
//
//    internal fun implMultiply(x: LongArray, y: LongArray, zz: LongArray) {
//        val a = LongArray(7)
//        val b = LongArray(7)
//        implExpand(x, a)
//        implExpand(y, b)
//        for (i in 0..6) {
//            implMulwAcc(a, b[i], zz, i)
//        }
//        implCompactExt(zz)
//    }
//
//    internal fun implMulwAcc(xs: LongArray, y: Long, z: LongArray, zOff: Int) {
////        assert y >>> 59 == 0;
//        val u = LongArray(8)
//        //      u[0] = 0;
//        u[1] = y
//        u[2] = u[1] shl 1
//        u[3] = u[2] xor y
//        u[4] = u[2] shl 1
//        u[5] = u[4] xor y
//        u[6] = u[3] shl 1
//        u[7] = u[6] xor y
//        for (i in 0..6) {
//            val x = xs[i]
//
////            assert x >>> 59 == 0;
//            var j = x.toInt()
//            var g: Long
//            var h: Long = 0
//            var l = (u[j and 7]
//                    xor (u[j ushr 3 and 7] shl 3))
//            var k = 54
//            do {
//                j = (x ushr k).toInt()
//                g = (u[j and 7]
//                        xor u[j ushr 3 and 7] shl 3)
//                l = l xor (g shl k)
//                h = h xor (g ushr -k)
//            } while (6.let { k -= it; k } > 0)
//
////            assert h >>> 53 == 0;
//            z[zOff + i] = z[zOff + i] xor (l and M59)
//            z[zOff + i + 1] = z[zOff + i + 1] xor (l ushr 59 xor (h shl 5))
//        }
//    }
//
//    internal fun implSquare(x: LongArray, zz: LongArray) {
//        for (i in 0..5) {
//            Interleave.expand64To128(x[i], zz, i shl 1)
//        }
//        zz[12] = Interleave.expand32to64(x[6].toInt())
//    }
//}
