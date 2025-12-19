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
//import org.spongycastle.math.raw.Nat576
//import java.math.BigInteger
//
//object SecT571Field {
//    private const val M59 = -1L ushr 5
//    private const val RM = -0x1084210842108422L
//    private val ROOT_Z = longArrayOf(
//        0x2BE1195F08CAFB99L,
//        -0x6a0f73507b9a83ddL,
//        -0x3507b9a83dcd41efL,
//        0x657C232BE1195F08L,
//        -0x7b9a83dcf73507cL,
//        0x7C232BE1195F08CAL,
//        -0x41ee6a0f73507b9bL,
//        0x5F08CAF84657C232L,
//        0x784657C232BE119L
//    )
//
//    @JvmStatic
//    fun add(x: LongArray, y: LongArray, z: LongArray) {
//        for (i in 0..8) {
//            z[i] = x[i] xor y[i]
//        }
//    }
//
//    private fun add(x: LongArray, xOff: Int, y: LongArray, yOff: Int, z: LongArray, zOff: Int) {
//        for (i in 0..8) {
//            z[zOff + i] = x[xOff + i] xor y[yOff + i]
//        }
//    }
//
//    @JvmStatic
//    fun addBothTo(x: LongArray, y: LongArray, z: LongArray) {
//        for (i in 0..8) {
//            z[i] = z[i] xor (x[i] xor y[i])
//        }
//    }
//
//    private fun addBothTo(
//        x: LongArray,
//        xOff: Int,
//        y: LongArray,
//        yOff: Int,
//        z: LongArray,
//        zOff: Int
//    ) {
//        for (i in 0..8) {
//            z[zOff + i] = z[zOff + i] xor (x[xOff + i] xor y[yOff + i])
//        }
//    }
//
//    fun addExt(xx: LongArray, yy: LongArray, zz: LongArray) {
//        for (i in 0..17) {
//            zz[i] = xx[i] xor yy[i]
//        }
//    }
//
//    @JvmStatic
//    fun addOne(x: LongArray, z: LongArray) {
//        z[0] = x[0] xor 1L
//        for (i in 1..8) {
//            z[i] = x[i]
//        }
//    }
//
//    @JvmStatic
//    fun fromBigInteger(x: BigInteger): LongArray {
//        val z = Nat576.fromBigInteger64(x)
//        reduce5(z, 0)
//        return z
//    }
//
//    @JvmStatic
//    fun invert(x: LongArray, z: LongArray) {
//        check(!Nat576.isZero64(x))
//
//        // Itoh-Tsujii inversion with bases { 2, 3, 5 }
//        val t0 = Nat576.create64()
//        val t1 = Nat576.create64()
//        val t2 = Nat576.create64()
//        square(x, t2)
//
//        // 5 | 570
//        square(t2, t0)
//        square(t0, t1)
//        multiply(t0, t1, t0)
//        squareN(t0, 2, t1)
//        multiply(t0, t1, t0)
//        multiply(t0, t2, t0)
//
//        // 3 | 114
//        squareN(t0, 5, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 5, t1)
//        multiply(t0, t1, t0)
//
//        // 2 | 38
//        squareN(t0, 15, t1)
//        multiply(t0, t1, t2)
//
//        // ! {2,3,5} | 19
//        squareN(t2, 30, t0)
//        squareN(t0, 30, t1)
//        multiply(t0, t1, t0)
//
//        // 3 | 9
//        squareN(t0, 60, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 60, t1)
//        multiply(t0, t1, t0)
//
//        // 3 | 3
//        squareN(t0, 180, t1)
//        multiply(t0, t1, t0)
//        squareN(t1, 180, t1)
//        multiply(t0, t1, t0)
//        multiply(t0, t2, z)
//    }
//
//    @JvmStatic
//    fun multiply(x: LongArray, y: LongArray?, z: LongArray) {
//        val tt = Nat576.createExt64()
//        implMultiply(x, y, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyAddToExt(x: LongArray, y: LongArray?, zz: LongArray) {
//        val tt = Nat576.createExt64()
//        implMultiply(x, y, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun multiplyPrecomp(x: LongArray, precomp: LongArray, z: LongArray) {
//        val tt = Nat576.createExt64()
//        implMultiplyPrecomp(x, precomp, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun multiplyPrecompAddToExt(x: LongArray, precomp: LongArray, zz: LongArray) {
//        val tt = Nat576.createExt64()
//        implMultiplyPrecomp(x, precomp, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun precompMultiplicand(x: LongArray?): LongArray {
//        /*
//         * Precompute table of all 4-bit products of x (first section)
//         */
//        val len = 9 shl 4
//        val t = LongArray(len shl 1)
//        System.arraycopy(x, 0, t, 9, 9)
//        //        reduce5(T0, 9);
//        var tOff = 0
//        for (i in 7 downTo 1) {
//            tOff += 18
//            Nat.shiftUpBit64(9, t, tOff ushr 1, 0L, t, tOff)
//            reduce5(t, tOff)
//            add(t, 9, t, tOff, t, tOff + 9)
//        }
//
//        /*
//         * Second section with all 4-bit products of B shifted 4 bits
//         */Nat.shiftUpBits64(len, t, 0, 4, 0L, t, len)
//        return t
//    }
//
//    @JvmStatic
//    fun reduce(xx: LongArray, z: LongArray) {
//        var xx09 = xx[9]
//        var u = xx[17]
//        var v = xx09
//        xx09 = v xor (u ushr 59) xor (u ushr 57) xor (u ushr 54) xor (u ushr 49)
//        v = xx[8] xor (u shl 5) xor (u shl 7) xor (u shl 10) xor (u shl 15)
//        for (i in 16 downTo 10) {
//            u = xx[i]
//            z[i - 8] = v xor (u ushr 59) xor (u ushr 57) xor (u ushr 54) xor (u ushr 49)
//            v = xx[i - 9] xor (u shl 5) xor (u shl 7) xor (u shl 10) xor (u shl 15)
//        }
//        u = xx09
//        z[1] = v xor (u ushr 59) xor (u ushr 57) xor (u ushr 54) xor (u ushr 49)
//        v = xx[0] xor (u shl 5) xor (u shl 7) xor (u shl 10) xor (u shl 15)
//        val x08 = z[8]
//        val t = x08 ushr 59
//        z[0] = v xor t xor (t shl 2) xor (t shl 5) xor (t shl 10)
//        z[8] = x08 and M59
//    }
//
//    fun reduce5(z: LongArray, zOff: Int) {
//        val z8 = z[zOff + 8]
//        val t = z8 ushr 59
//        z[zOff] = z[zOff] xor (t xor (t shl 2) xor (t shl 5) xor (t shl 10))
//        z[zOff + 8] = z8 and M59
//    }
//
//    @JvmStatic
//    fun sqrt(x: LongArray, z: LongArray) {
//        val evn = Nat576.create64()
//        val odd = Nat576.create64()
//        var pos = 0
//        for (i in 0..3) {
//            val u0 = Interleave.unshuffle(x[pos++])
//            val u1 = Interleave.unshuffle(x[pos++])
//            evn[i] = u0 and 0x00000000FFFFFFFFL or (u1 shl 32)
//            odd[i] = u0 ushr 32 or (u1 and -0x100000000L)
//        }
//        run {
//            val u0 = Interleave.unshuffle(x[pos])
//            evn[4] = u0 and 0x00000000FFFFFFFFL
//            odd[4] = u0 ushr 32
//        }
//        multiply(odd, ROOT_Z, z)
//        add(z, evn, z)
//    }
//
//    @JvmStatic
//    fun square(x: LongArray, z: LongArray) {
//        val tt = Nat576.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//    }
//
//    @JvmStatic
//    fun squareAddToExt(x: LongArray, zz: LongArray) {
//        val tt = Nat576.createExt64()
//        implSquare(x, tt)
//        addExt(zz, tt, zz)
//    }
//
//    @JvmStatic
//    fun squareN(x: LongArray, n: Int, z: LongArray) {
////        assert n > 0;
//        var n = n
//        val tt = Nat576.createExt64()
//        implSquare(x, tt)
//        reduce(tt, z)
//        while (--n > 0) {
//            implSquare(z, tt)
//            reduce(tt, z)
//        }
//    }
//
//    fun trace(x: LongArray): Int {
//        // Non-zero-trace bits: 0, 561, 569
//        return (x[0] xor (x[8] ushr 49) xor (x[8] ushr 57)).toInt() and 1
//    }
//
//    internal fun implMultiply(x: LongArray, y: LongArray?, zz: LongArray) {
////        for (int i = 0; i < 9; ++i)
////        {
////            implMulwAcc(x, y[i], zz, i);
////        }
//        val precomp = precompMultiplicand(y)
//        implMultiplyPrecomp(x, precomp, zz)
//    }
//
//    internal fun implMultiplyPrecomp(x: LongArray, precomp: LongArray, zz: LongArray) {
//        val MASK = 0xF
//
//        /*
//         * Lopez-Dahab algorithm
//         */run {
//            var k = 56
//            while (k >= 0) {
//                var j = 1
//                while (j < 9) {
//                    val aVal = (x[j] ushr k).toInt()
//                    val u = aVal and MASK
//                    val v = aVal ushr 4 and MASK
//                    addBothTo(precomp, 9 * u, precomp, 9 * (v + 16), zz, j - 1)
//                    j += 2
//                }
//                Nat.shiftUpBits64(16, zz, 0, 8, 0L)
//                k -= 8
//            }
//        }
//        var k = 56
//        while (k >= 0) {
//            var j = 0
//            while (j < 9) {
//                val aVal = (x[j] ushr k).toInt()
//                val u = aVal and MASK
//                val v = aVal ushr 4 and MASK
//                addBothTo(precomp, 9 * u, precomp, 9 * (v + 16), zz, j)
//                j += 2
//            }
//            if (k > 0) {
//                Nat.shiftUpBits64(18, zz, 0, 8, 0L)
//            }
//            k -= 8
//        }
//    }
//
//    internal fun implMulwAcc(xs: LongArray, y: Long, z: LongArray, zOff: Int) {
//        val u = LongArray(32)
//        //      u[0] = 0;
//        u[1] = y
//        run {
//            var i = 2
//            while (i < 32) {
//                u[i] = u[i ushr 1] shl 1
//                u[i + 1] = u[i] xor y
//                i += 2
//            }
//        }
//        var l: Long = 0
//        for (i in 0..8) {
//            var x = xs[i]
//            var j = x.toInt()
//            l = l xor u[j and 31]
//            var g: Long
//            var h: Long = 0
//            var k = 60
//            do {
//                j = (x ushr k).toInt()
//                g = u[j and 31]
//                l = l xor (g shl k)
//                h = h xor (g ushr -k)
//            } while (5.let { k -= it; k } > 0)
//            for (p in 0..3) {
//                x = x and RM ushr 1
//                h = h xor (x and (y shl p shr 63))
//            }
//            z[zOff + i] = z[zOff + i] xor l
//            l = h
//        }
//        z[zOff + 9] = z[zOff + 9] xor l
//    }
//
//    internal fun implSquare(x: LongArray, zz: LongArray) {
//        for (i in 0..8) {
//            Interleave.expand64To128(x[i], zz, i shl 1)
//        }
//    }
//}
