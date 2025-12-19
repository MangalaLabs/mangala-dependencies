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
//import org.spongycastle.math.raw.Nat256
//import java.math.BigInteger
//
//object SecT233Field {
//    private const val M41 = -1L ushr 23
//    private const val M59 = -1L ushr 5
//    @JvmStatic
//    fun add(x: LongArray, y: LongArray, z: LongArray) {
//        z[0] = x[0] xor y[0]
//        z[1] = x[1] xor y[1]
//        z[2] = x[2] xor y[2]
//        z[3] = x[3] xor y[3]
//    }
//
//    fun addExt(xx: LongArray, yy: LongArray, zz: LongArray) {
//        zz[0] = xx[0] xor yy[0]
//        zz[1] = xx[1] xor yy[1]
//        zz[2] = xx[2] xor yy[2]
//        zz[3] = xx[3] xor yy[3]
//        zz[4] = xx[4] xor yy[4]
//        zz[5] = xx[5] xor yy[5]
//        zz[6] = xx[6] xor yy[6]
//        zz[7] = xx[7] xor yy[7]
//    }
//
//    @JvmStatic
//    fun addOne(x: LongArray, z: LongArray) {
//        z[0] = x[0] xor 1L
//        z[1] = x[1]
//        z[2] = x[2]
//        z[3] = x[3]
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): LongArray {
//        val z = Nat256.fromBigInteger64(x)
//        reduce23(z, 0)
//        return z
//    }
//
//    @JvmStatic
//    fun invert(x: LongArray, z: LongArray) {
//        check(!Nat256.isZero64(x))
//
//        // Itoh-Tsujii inversion
//        val t0 = Nat256.create64()
//        val t1 = Nat256.create64()
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
//        square(t1, t1)
//        multiply(t1, x, t1)
//        squareN(t1, 29, t0)
//        multiply(t0, t1, t0)
//        squareN(t0, 58, t1)
//        multiply(t1, t0, t1)
//        squareN(t1, 116, t0)
//        multiply(t0, t1, t0)
//        square(t0, z)
//    }
//
//    @JvmStatic
//    fun multiply(x: LongArray, y: LongArray, z: LongArray) {
//        val tt = Nat256.createExt64()
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: LongArray, y: LongArray, zz: LongArray) {
//        val tt = Nat256.createExt64()
//        implMultiply(x, y, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun reduce(xx: LongArray, z: LongArray) {
//        var x0 = xx[0]
//        var x1 = xx[1]
//        var x2 = xx[2]
//        var x3 = xx[3]
//        var x4 = xx[4]
//        var x5 = xx[5]
//        val x6 = xx[6]
//        val x7 = xx[7]
//        x3 = x3 xor (x7 shl 23)
//        x4 = x4 xor (x7 ushr 41 xor (x7 shl 33))
//        x5 = x5 xor (x7 ushr 31)
//        x2 = x2 xor (x6 shl 23)
//        x3 = x3 xor (x6 ushr 41 xor (x6 shl 33))
//        x4 = x4 xor (x6 ushr 31)
//        x1 = x1 xor (x5 shl 23)
//        x2 = x2 xor (x5 ushr 41 xor (x5 shl 33))
//        x3 = x3 xor (x5 ushr 31)
//        x0 = x0 xor (x4 shl 23)
//        x1 = x1 xor (x4 ushr 41 xor (x4 shl 33))
//        x2 = x2 xor (x4 ushr 31)
//        val t = x3 ushr 41
//        z[0] = x0 xor t
//        z[1] = x1 xor (t shl 10)
//        z[2] = x2
//        z[3] = x3 and M41
//    }
//
//    fun reduce23(z: LongArray, zOff: Int) {
//        val z3 = z[zOff + 3]
//        val t = z3 ushr 41
//        z[zOff] = z[zOff] xor t
//        z[zOff + 1] = z[zOff + 1] xor (t shl 10)
//        z[zOff + 3] = z3 and M41
//    }
//
//    @JvmStatic
//    fun square(x: LongArray, z: LongArray) {
//        val tt = Nat256.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareAddToExt(x: LongArray, zz: LongArray) {
//        val tt = Nat256.createExt64()
//        implSquare(x, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun squareN(x: LongArray, n: Int, z: LongArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat256.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            implSquare(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    @JvmStatic
//    fun sqrt(x: LongArray, z: LongArray) {
//        var u0: Long
//        var u1: Long
//        u0 = Interleave.unshuffle(x[0])
//        u1 = Interleave.unshuffle(x[1])
//        val e0 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        var c0 = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[2])
//        u1 = Interleave.unshuffle(x[3])
//        val e1 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        var c1 = u0 ushr 32 or (u1 and -0x100000000L)
//        val c2: Long
//        c2 = c1 ushr 27
//        c1 = c1 xor (c0 ushr 27 or (c1 shl 37))
//        c0 = c0 xor (c0 shl 37)
//        val tt = Nat256.createExt64()
//        val shifts = intArrayOf(32, 117, 191)
//        for (i in shifts.indices) {
//            val w = shifts[i] ushr 6
//            val s = shifts[i] and 63
//            //            assert s != 0;
//            tt[w] = tt[w] xor (c0 shl s)
//            tt[w + 1] = tt[w + 1] xor (c1 shl s or (c0 ushr -s))
//            tt[w + 2] = tt[w + 2] xor (c2 shl s or (c1 ushr -s))
//            tt[w + 3] = tt[w + 3] xor (c2 ushr -s)
//        }
//        reduce(tt, z)
//        z[0] = z[0] xor e0
//        z[1] = z[1] xor e1
//    }
//
//    fun trace(x: LongArray): Int {
//        // Non-zero-trace bits: 0, 159
//        return (x[0] xor (x[2] ushr 31)).toInt() and 1
//    }
//
//    internal fun implCompactExt(zz: LongArray) {
//        val z0 = zz[0]
//        val z1 = zz[1]
//        val z2 = zz[2]
//        val z3 = zz[3]
//        val z4 = zz[4]
//        val z5 = zz[5]
//        val z6 = zz[6]
//        val z7 = zz[7]
//        zz[0] = z0 xor (z1 shl 59)
//        zz[1] = z1 ushr 5 xor (z2 shl 54)
//        zz[2] = z2 ushr 10 xor (z3 shl 49)
//        zz[3] = z3 ushr 15 xor (z4 shl 44)
//        zz[4] = z4 ushr 20 xor (z5 shl 39)
//        zz[5] = z5 ushr 25 xor (z6 shl 34)
//        zz[6] = z6 ushr 30 xor (z7 shl 29)
//        zz[7] = z7 ushr 35
//    }
//
//    internal fun implExpand(x: LongArray, z: LongArray) {
//        val x0 = x[0]
//        val x1 = x[1]
//        val x2 = x[2]
//        val x3 = x[3]
//        z[0] = x0 and M59
//        z[1] = x0 ushr 59 xor (x1 shl 5) and M59
//        z[2] = x1 ushr 54 xor (x2 shl 10) and M59
//        z[3] = x2 ushr 49 xor (x3 shl 15)
//    }
//
//    internal fun implMultiply(x: LongArray, y: LongArray, zz: LongArray) {
//        /*
//         * "Two-level seven-way recursion" as described in "Batch binary Edwards", Daniel J. Bernstein.
//         */
//        val f = LongArray(4)
//        val g = LongArray(4)
//        implExpand(x, f)
//        implExpand(y, g)
//        implMulwAcc(f[0], g[0], zz, 0)
//        implMulwAcc(f[1], g[1], zz, 1)
//        implMulwAcc(f[2], g[2], zz, 2)
//        implMulwAcc(f[3], g[3], zz, 3)
//
//        // U *= (1 - t^n)
//        for (i in 5 downTo 1) {
//            zz[i] = zz[i] xor zz[i - 1]
//        }
//        implMulwAcc(f[0] xor f[1], g[0] xor g[1], zz, 1)
//        implMulwAcc(f[2] xor f[3], g[2] xor g[3], zz, 3)
//
//        // V *= (1 - t^2n)
//        for (i in 7 downTo 2) {
//            zz[i] = zz[i] xor zz[i - 2]
//        }
//
//        // Double-length recursion
//        run {
//            val c0 = f[0] xor f[2]
//            val c1 = f[1] xor f[3]
//            val d0 = g[0] xor g[2]
//            val d1 = g[1] xor g[3]
//            implMulwAcc(c0 xor c1, d0 xor d1, zz, 3)
//            val t = LongArray(3)
//            implMulwAcc(c0, d0, t, 0)
//            implMulwAcc(c1, d1, t, 1)
//            val t0 = t[0]
//            val t1 = t[1]
//            val t2 = t[2]
//            zz[2] = zz[2] xor t0
//            zz[3] = zz[3] xor (t0 xor t1)
//            zz[4] = zz[4] xor (t2 xor t1)
//            zz[5] = zz[5] xor t2
//        }
//        implCompactExt(zz)
//    }
//
//    internal fun implMulwAcc(x: Long, y: Long, z: LongArray, zOff: Int) {
////        assert x >>> 59 == 0;
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
//        var j = x.toInt()
//        var g: Long
//        var h: Long = 0
//        var l = (u[j and 7]
//                xor (u[j ushr 3 and 7] shl 3))
//        var k = 54
//        do {
//            j = (x ushr k).toInt()
//            g = (u[j and 7]
//                    xor u[j ushr 3 and 7] shl 3)
//            l = l xor (g shl k)
//            h = h xor (g ushr -k)
//        } while (6.let { k -= it; k } > 0)
//
////        assert h >>> 53 == 0;
//        z[zOff] = z[zOff] xor (l and M59)
//        z[zOff + 1] = z[zOff + 1] xor (l ushr 59 xor (h shl 5))
//    }
//
//    internal fun implSquare(x: LongArray, zz: LongArray) {
//        Interleave.expand64To128(x[0], zz, 0)
//        Interleave.expand64To128(x[1], zz, 2)
//        Interleave.expand64To128(x[2], zz, 4)
//        val x3 = x[3]
//        zz[6] = Interleave.expand32to64(x3.toInt())
//        zz[7] = Interleave.expand16to32((x3 ushr 32).toInt()).toLong() and 0xFFFFFFFFL
//    }
//}
