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
//import org.spongycastle.math.raw.Nat192
//import java.math.BigInteger
//
//object SecT163Field {
//    private const val M35 = -1L ushr 29
//    private const val M55 = -1L ushr 9
//    private val ROOT_Z = longArrayOf(-0x4924924924924950L, 0x492492492492DB6DL, 0x492492492L)
//    @JvmStatic
//    fun add(x: LongArray, y: LongArray, z: LongArray) {
//        z[0] = x[0] xor y[0]
//        z[1] = x[1] xor y[1]
//        z[2] = x[2] xor y[2]
//    }
//
//    fun addExt(xx: LongArray, yy: LongArray, zz: LongArray) {
//        zz[0] = xx[0] xor yy[0]
//        zz[1] = xx[1] xor yy[1]
//        zz[2] = xx[2] xor yy[2]
//        zz[3] = xx[3] xor yy[3]
//        zz[4] = xx[4] xor yy[4]
//        zz[5] = xx[5] xor yy[5]
//    }
//
//    @JvmStatic
//    fun addOne(x: LongArray, z: LongArray) {
//        z[0] = x[0] xor 1L
//        z[1] = x[1]
//        z[2] = x[2]
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): LongArray {
//        val z = Nat192.fromBigInteger64(x)
//        reduce29(z, 0)
//        return z
//    }
//
//    @JvmStatic
//    fun invert(x: LongArray, z: LongArray) {
//        check(!Nat192.isZero64(x))
//
//        // Itoh-Tsujii inversion with bases { 2, 3 }
//        val t0 = Nat192.create64()
//        val t1 = Nat192.create64()
//        square(x, t0)
//
//        // 3 | 162
//        squareN(t0, 1, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 1, t1)
//        multiply(t0, t1, t0)
//
//        // 3 | 54
//        squareN(t0, 3, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 3, t1)
//        multiply(t0, t1, t0)
//
//        // 3 | 18
//        squareN(t0, 9, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 9, t1)
//        multiply(t0, t1, t0)
//
//        // 3 | 6
//        squareN(t0, 27, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 27, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 2
//        squareN(t0, 81, t1)
//        multiply(t0, t1, z)
//    }
//
//    @JvmStatic
//    fun multiply(x: LongArray, y: LongArray, z: LongArray) {
//        val tt = Nat192.createExt64()
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: LongArray, y: LongArray, zz: LongArray) {
//        val tt = Nat192.createExt64()
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
//        val x4 = xx[4]
//        val x5 = xx[5]
//        x2 = x2 xor (x5 shl 29 xor (x5 shl 32) xor (x5 shl 35) xor (x5 shl 36))
//        x3 = x3 xor (x5 ushr 35 xor (x5 ushr 32) xor (x5 ushr 29) xor (x5 ushr 28))
//        x1 = x1 xor (x4 shl 29 xor (x4 shl 32) xor (x4 shl 35) xor (x4 shl 36))
//        x2 = x2 xor (x4 ushr 35 xor (x4 ushr 32) xor (x4 ushr 29) xor (x4 ushr 28))
//        x0 = x0 xor (x3 shl 29 xor (x3 shl 32) xor (x3 shl 35) xor (x3 shl 36))
//        x1 = x1 xor (x3 ushr 35 xor (x3 ushr 32) xor (x3 ushr 29) xor (x3 ushr 28))
//        val t = x2 ushr 35
//        z[0] = x0 xor t xor (t shl 3) xor (t shl 6) xor (t shl 7)
//        z[1] = x1
//        z[2] = x2 and M35
//    }
//
//    fun reduce29(z: LongArray, zOff: Int) {
//        val z2 = z[zOff + 2]
//        val t = z2 ushr 35
//        z[zOff] = z[zOff] xor (t xor (t shl 3) xor (t shl 6) xor (t shl 7))
//        z[zOff + 2] = z2 and M35
//    }
//
//    @JvmStatic
//    fun sqrt(x: LongArray, z: LongArray) {
//        val odd = Nat192.create64()
//        var u0: Long
//        val u1: Long
//        u0 = Interleave.unshuffle(x[0])
//        u1 = Interleave.unshuffle(x[1])
//        val e0 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        odd[0] = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[2])
//        val e1 = u0 and 0x00000000FFFFFFFFL
//        odd[1] = u0 ushr 32
//        multiply(odd, ROOT_Z, z)
//        z[0] = z[0] xor e0
//        z[1] = z[1] xor e1
//    }
//
//    @JvmStatic
//    fun square(x: LongArray, z: LongArray) {
//        val tt = Nat192.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareAddToExt(x: LongArray, zz: LongArray) {
//        val tt = Nat192.createExt64()
//        implSquare(x, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun squareN(x: LongArray, n: Int, z: LongArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat192.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            implSquare(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    fun trace(x: LongArray): Int {
//        // Non-zero-trace bits: 0, 157
//        return (x[0] xor (x[2] ushr 29)).toInt() and 1
//    }
//
//    internal fun implCompactExt(zz: LongArray) {
//        val z0 = zz[0]
//        val z1 = zz[1]
//        val z2 = zz[2]
//        val z3 = zz[3]
//        val z4 = zz[4]
//        val z5 = zz[5]
//        zz[0] = z0 xor (z1 shl 55)
//        zz[1] = z1 ushr 9 xor (z2 shl 46)
//        zz[2] = z2 ushr 18 xor (z3 shl 37)
//        zz[3] = z3 ushr 27 xor (z4 shl 28)
//        zz[4] = z4 ushr 36 xor (z5 shl 19)
//        zz[5] = z5 ushr 45
//    }
//
//    internal fun implMultiply(x: LongArray, y: LongArray, zz: LongArray) {
//        /*
//         * "Five-way recursion" as described in "Batch binary Edwards", Daniel J. Bernstein.
//         */
//        var f0 = x[0]
//        var f1 = x[1]
//        var f2 = x[2]
//        f2 = f1 ushr 46 xor (f2 shl 18)
//        f1 = f0 ushr 55 xor (f1 shl 9) and M55
//        f0 = f0 and M55
//        var g0 = y[0]
//        var g1 = y[1]
//        var g2 = y[2]
//        g2 = g1 ushr 46 xor (g2 shl 18)
//        g1 = g0 ushr 55 xor (g1 shl 9) and M55
//        g0 = g0 and M55
//        val H = LongArray(10)
//        implMulw(f0, g0, H, 0) // H(0)       55/54 bits
//        implMulw(f2, g2, H, 2) // H(INF)     55/50 bits
//        val t0 = f0 xor f1 xor f2
//        val t1 = g0 xor g1 xor g2
//        implMulw(t0, t1, H, 4) // H(1)       55/54 bits
//        val t2 = f1 shl 1 xor (f2 shl 2)
//        val t3 = g1 shl 1 xor (g2 shl 2)
//        implMulw(f0 xor t2, g0 xor t3, H, 6) // H(t)       55/56 bits
//        implMulw(t0 xor t2, t1 xor t3, H, 8) // H(t + 1)   55/56 bits
//        val t4 = H[6] xor H[8]
//        val t5 = H[7] xor H[9]
//
////        assert t5 >>> 55 == 0;
//
//        // Calculate V
//        val v0 = t4 shl 1 xor H[6]
//        val v1 = t4 xor (t5 shl 1) xor H[7]
//
//        // Calculate U
//        val u0 = H[0]
//        val u1 = H[1] xor H[0] xor H[4]
//        val u2 = H[1] xor H[5]
//
//        // Calculate W
//        var w0 = u0 xor v0 xor (H[2] shl 4) xor (H[2] shl 1)
//        var w1 = u1 xor v1 xor (H[3] shl 4) xor (H[3] shl 1)
//        var w2 = u2 xor t5
//
//        // Propagate carries
//        w1 = w1 xor (w0 ushr 55)
//        w0 = w0 and M55
//        w2 = w2 xor (w1 ushr 55)
//        w1 = w1 and M55
//
////        assert (w0 & 1L) == 0;
//
//        // Divide W by t
//        w0 = w0 ushr 1 xor (w1 and 1L shl 54)
//        w1 = w1 ushr 1 xor (w2 and 1L shl 54)
//        w2 = w2 ushr 1
//
//        // Divide W by (t + 1)
//        w0 = w0 xor (w0 shl 1)
//        w0 = w0 xor (w0 shl 2)
//        w0 = w0 xor (w0 shl 4)
//        w0 = w0 xor (w0 shl 8)
//        w0 = w0 xor (w0 shl 16)
//        w0 = w0 xor (w0 shl 32)
//        w0 = w0 and M55
//        w1 = w1 xor (w0 ushr 54)
//        w1 = w1 xor (w1 shl 1)
//        w1 = w1 xor (w1 shl 2)
//        w1 = w1 xor (w1 shl 4)
//        w1 = w1 xor (w1 shl 8)
//        w1 = w1 xor (w1 shl 16)
//        w1 = w1 xor (w1 shl 32)
//        w1 = w1 and M55
//        w2 = w2 xor (w1 ushr 54)
//        w2 = w2 xor (w2 shl 1)
//        w2 = w2 xor (w2 shl 2)
//        w2 = w2 xor (w2 shl 4)
//        w2 = w2 xor (w2 shl 8)
//        w2 = w2 xor (w2 shl 16)
//        w2 = w2 xor (w2 shl 32)
//
////        assert w2 >>> 52 == 0;
//        zz[0] = u0
//        zz[1] = u1 xor w0 xor H[2]
//        zz[2] = u2 xor w1 xor w0 xor H[3]
//        zz[3] = w2 xor w1
//        zz[4] = w2 xor H[2]
//        zz[5] = H[3]
//        implCompactExt(zz)
//    }
//
//    internal fun implMulw(x: Long, y: Long, z: LongArray, zOff: Int) {
////        assert x >>> 56 == 0;
////        assert y >>> 56 == 0;
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
//        var l = u[j and 3]
//        var k = 47
//        do {
//            j = (x ushr k).toInt()
//            g = (u[j and 7]
//                    xor (u[j ushr 3 and 7] shl 3
//                    ) xor (u[j ushr 6 and 7] shl 6))
//            l = l xor (g shl k)
//            h = h xor (g ushr -k)
//        } while (9.let { k -= it; k } > 0)
//
////        assert h >>> 47 == 0;
//        z[zOff] = l and M55
//        z[zOff + 1] = l ushr 55 xor (h shl 9)
//    }
//
//    internal fun implSquare(x: LongArray, zz: LongArray) {
//        Interleave.expand64To128(x[0], zz, 0)
//        Interleave.expand64To128(x[1], zz, 2)
//        val x2 = x[2]
//        zz[4] = Interleave.expand32to64(x2.toInt())
//        zz[5] = Interleave.expand8to16((x2 ushr 32).toInt()).toLong() and 0xFFFFFFFFL
//    }
//}
