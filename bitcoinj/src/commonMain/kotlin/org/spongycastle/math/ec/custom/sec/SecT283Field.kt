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
//import org.spongycastle.math.raw.Nat320
//import java.math.BigInteger
//
//object SecT283Field {
//    private const val M27 = -1L ushr 37
//    private const val M57 = -1L ushr 7
//    private val ROOT_Z = longArrayOf(
//        0x0C30C30C30C30808L,
//        0x30C30C30C30C30C3L,
//        -0x7df7df7df7df7cf4L,
//        0x0820820820820820L,
//        0x2082082L
//    )
//
//    @JvmStatic
//    fun add(x: LongArray, y: LongArray, z: LongArray) {
//        z[0] = x[0] xor y[0]
//        z[1] = x[1] xor y[1]
//        z[2] = x[2] xor y[2]
//        z[3] = x[3] xor y[3]
//        z[4] = x[4] xor y[4]
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
//        zz[8] = xx[8] xor yy[8]
//    }
//
//    @JvmStatic
//    fun addOne(x: LongArray, z: LongArray) {
//        z[0] = x[0] xor 1L
//        z[1] = x[1]
//        z[2] = x[2]
//        z[3] = x[3]
//        z[4] = x[4]
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): LongArray {
//        val z = Nat320.fromBigInteger64(x)
//        reduce37(z, 0)
//        return z
//    }
//
//    @JvmStatic
//    fun invert(x: LongArray, z: LongArray) {
//        check(!Nat320.isZero64(x))
//
//        // Itoh-Tsujii inversion
//        val t0 = Nat320.create64()
//        val t1 = Nat320.create64()
//        square(x, t0)
//        multiply(t0, x, t0)
//        squareN(t0, 2, t1)
//        multiply(t1, t0, t1)
//        squareN(t1, 4, t0)
//        multiply(t0, t1, t0)
//        squareN(t0, 8, t1)
//        multiply(t1, t0, t1)
//        square(t1, t1)
//        multiply(t1, x, t1)
//        squareN(t1, 17, t0)
//        multiply(t0, t1, t0)
//        square(t0, t0)
//        multiply(t0, x, t0)
//        squareN(t0, 35, t1)
//        multiply(t1, t0, t1)
//        squareN(t1, 70, t0)
//        multiply(t0, t1, t0)
//        square(t0, t0)
//        multiply(t0, x, t0)
//        squareN(t0, 141, t1)
//        multiply(t1, t0, t1)
//        square(t1, z)
//    }
//
//    @JvmStatic
//    fun multiply(x: LongArray, y: LongArray, z: LongArray) {
//        val tt = Nat320.createExt64()
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: LongArray, y: LongArray, zz: LongArray) {
//        val tt = Nat320.createExt64()
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
//        val x5 = xx[5]
//        val x6 = xx[6]
//        val x7 = xx[7]
//        val x8 = xx[8]
//        x3 = x3 xor (x8 shl 37 xor (x8 shl 42) xor (x8 shl 44) xor (x8 shl 49))
//        x4 = x4 xor (x8 ushr 27 xor (x8 ushr 22) xor (x8 ushr 20) xor (x8 ushr 15))
//        x2 = x2 xor (x7 shl 37 xor (x7 shl 42) xor (x7 shl 44) xor (x7 shl 49))
//        x3 = x3 xor (x7 ushr 27 xor (x7 ushr 22) xor (x7 ushr 20) xor (x7 ushr 15))
//        x1 = x1 xor (x6 shl 37 xor (x6 shl 42) xor (x6 shl 44) xor (x6 shl 49))
//        x2 = x2 xor (x6 ushr 27 xor (x6 ushr 22) xor (x6 ushr 20) xor (x6 ushr 15))
//        x0 = x0 xor (x5 shl 37 xor (x5 shl 42) xor (x5 shl 44) xor (x5 shl 49))
//        x1 = x1 xor (x5 ushr 27 xor (x5 ushr 22) xor (x5 ushr 20) xor (x5 ushr 15))
//        val t = x4 ushr 27
//        z[0] = x0 xor t xor (t shl 5) xor (t shl 7) xor (t shl 12)
//        z[1] = x1
//        z[2] = x2
//        z[3] = x3
//        z[4] = x4 and M27
//    }
//
//    fun reduce37(z: LongArray, zOff: Int) {
//        val z4 = z[zOff + 4]
//        val t = z4 ushr 27
//        z[zOff] = z[zOff] xor (t xor (t shl 5) xor (t shl 7) xor (t shl 12))
//        z[zOff + 4] = z4 and M27
//    }
//
//    @JvmStatic
//    fun sqrt(x: LongArray, z: LongArray) {
//        val odd = Nat320.create64()
//        var u0: Long
//        var u1: Long
//        u0 = Interleave.unshuffle(x[0])
//        u1 = Interleave.unshuffle(x[1])
//        val e0 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        odd[0] = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[2])
//        u1 = Interleave.unshuffle(x[3])
//        val e1 = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//        odd[1] = u0 ushr 32 or (u1 and -0x100000000L)
//        u0 = Interleave.unshuffle(x[4])
//        val e2 = u0 and 0x00000000FFFFFFFFL
//        odd[2] = u0 ushr 32
//        multiply(odd, ROOT_Z, z)
//        z[0] = z[0] xor e0
//        z[1] = z[1] xor e1
//        z[2] = z[2] xor e2
//    }
//
//    @JvmStatic
//    fun square(x: LongArray, z: LongArray) {
//        val tt = Nat.create64(9)
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareAddToExt(x: LongArray, zz: LongArray) {
//        val tt = Nat.create64(9)
//        implSquare(x, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun squareN(x: LongArray, n: Int, z: LongArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat.create64(9)
//        implSquare(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            implSquare(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    fun trace(x: LongArray): Int {
//        // Non-zero-trace bits: 0, 271
//        return (x[0] xor (x[4] ushr 15)).toInt() and 1
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
//        val z8 = zz[8]
//        val z9 = zz[9]
//        zz[0] = z0 xor (z1 shl 57)
//        zz[1] = z1 ushr 7 xor (z2 shl 50)
//        zz[2] = z2 ushr 14 xor (z3 shl 43)
//        zz[3] = z3 ushr 21 xor (z4 shl 36)
//        zz[4] = z4 ushr 28 xor (z5 shl 29)
//        zz[5] = z5 ushr 35 xor (z6 shl 22)
//        zz[6] = z6 ushr 42 xor (z7 shl 15)
//        zz[7] = z7 ushr 49 xor (z8 shl 8)
//        zz[8] = z8 ushr 56 xor (z9 shl 1)
//        zz[9] = z9 ushr 63 // Zero!
//    }
//
//    internal fun implExpand(x: LongArray, z: LongArray) {
//        val x0 = x[0]
//        val x1 = x[1]
//        val x2 = x[2]
//        val x3 = x[3]
//        val x4 = x[4]
//        z[0] = x0 and M57
//        z[1] = x0 ushr 57 xor (x1 shl 7) and M57
//        z[2] = x1 ushr 50 xor (x2 shl 14) and M57
//        z[3] = x2 ushr 43 xor (x3 shl 21) and M57
//        z[4] = x3 ushr 36 xor (x4 shl 28)
//    }
//
//    //    protected static void addMs(long[] zz, int zOff, long[] p, int... ms)
//    //    {
//    //        long t0 = 0, t1 = 0;
//    //        for (int m : ms)
//    //        {
//    //            int i = (m - 1) << 1;
//    //            t0 ^= p[i    ];
//    //            t1 ^= p[i + 1];
//    //        }
//    //        zz[zOff    ] ^= t0;
//    //        zz[zOff + 1] ^= t1;
//    //    }
//    internal fun implMultiply(x: LongArray, y: LongArray, zz: LongArray) {
//        /*
//         * Formula (17) from "Some New Results on Binary Polynomial Multiplication",
//         * Murat Cenk and M. Anwar Hasan.
//         *
//         * The formula as given contained an error in the term t25, as noted below
//         */
//        val a = LongArray(5)
//        val b = LongArray(5)
//        implExpand(x, a)
//        implExpand(y, b)
//        val p = LongArray(26)
//        implMulw(a[0], b[0], p, 0) // m1
//        implMulw(a[1], b[1], p, 2) // m2
//        implMulw(a[2], b[2], p, 4) // m3
//        implMulw(a[3], b[3], p, 6) // m4
//        implMulw(a[4], b[4], p, 8) // m5
//        val u0 = a[0] xor a[1]
//        val v0 = b[0] xor b[1]
//        val u1 = a[0] xor a[2]
//        val v1 = b[0] xor b[2]
//        val u2 = a[2] xor a[4]
//        val v2 = b[2] xor b[4]
//        val u3 = a[3] xor a[4]
//        val v3 = b[3] xor b[4]
//        implMulw(u1 xor a[3], v1 xor b[3], p, 18) // m10
//        implMulw(u2 xor a[1], v2 xor b[1], p, 20) // m11
//        val A4 = u0 xor u3
//        val B4 = v0 xor v3
//        val A5 = A4 xor a[2]
//        val B5 = B4 xor b[2]
//        implMulw(A4, B4, p, 22) // m12
//        implMulw(A5, B5, p, 24) // m13
//        implMulw(u0, v0, p, 10) // m6
//        implMulw(u1, v1, p, 12) // m7
//        implMulw(u2, v2, p, 14) // m8
//        implMulw(u3, v3, p, 16) // m9
//
//
//        // Original method, corresponding to formula (16)
////        addMs(zz, 0, p, 1);
////        addMs(zz, 1, p, 1, 2, 6);
////        addMs(zz, 2, p, 1, 2, 3, 7);
////        addMs(zz, 3, p, 1, 3, 4, 5, 8, 10, 12, 13);
////        addMs(zz, 4, p, 1, 2, 4, 5, 6, 9, 10, 11, 13);
////        addMs(zz, 5, p, 1, 2, 3, 5, 7, 11, 12, 13);
////        addMs(zz, 6, p, 3, 4, 5, 8);
////        addMs(zz, 7, p, 4, 5, 9);
////        addMs(zz, 8, p, 5);
//
//        // Improved method factors out common single-word terms
//        // NOTE: p1,...,p26 in the paper maps to p[0],...,p[25] here
//        zz[0] = p[0]
//        zz[9] = p[9]
//        val t1 = p[0] xor p[1]
//        val t2 = t1 xor p[2]
//        val t3 = t2 xor p[10]
//        zz[1] = t3
//        val t4 = p[3] xor p[4]
//        val t5 = p[11] xor p[12]
//        val t6 = t4 xor t5
//        val t7 = t2 xor t6
//        zz[2] = t7
//        val t8 = t1 xor t4
//        val t9 = p[5] xor p[6]
//        val t10 = t8 xor t9
//        val t11 = t10 xor p[8]
//        val t12 = p[13] xor p[14]
//        val t13 = t11 xor t12
//        val t14 = p[18] xor p[22]
//        val t15 = t14 xor p[24]
//        val t16 = t13 xor t15
//        zz[3] = t16
//        val t17 = p[7] xor p[8]
//        val t18 = t17 xor p[9]
//        val t19 = t18 xor p[17]
//        zz[8] = t19
//        val t20 = t18 xor t9
//        val t21 = p[15] xor p[16]
//        val t22 = t20 xor t21
//        zz[7] = t22
//        val t23 = t22 xor t3
//        val t24 = p[19] xor p[20]
//        //      long t25 = p[23] ^ p[24];
//        val t25 = p[25] xor p[24] // Fixes an error in the paper: p[23] -> p{25]
//        val t26 = p[18] xor p[23]
//        val t27 = t24 xor t25
//        val t28 = t27 xor t26
//        val t29 = t28 xor t23
//        zz[4] = t29
//        val t30 = t7 xor t19
//        val t31 = t27 xor t30
//        val t32 = p[21] xor p[22]
//        val t33 = t31 xor t32
//        zz[5] = t33
//        val t34 = t11 xor p[0]
//        val t35 = t34 xor p[9]
//        val t36 = t35 xor t12
//        val t37 = t36 xor p[21]
//        val t38 = t37 xor p[23]
//        val t39 = t38 xor p[25]
//        zz[6] = t39
//        implCompactExt(zz)
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
//        for (i in 0..3) {
//            Interleave.expand64To128(x[i], zz, i shl 1)
//        }
//        zz[8] = Interleave.expand32to64(x[4].toInt())
//    }
//}
