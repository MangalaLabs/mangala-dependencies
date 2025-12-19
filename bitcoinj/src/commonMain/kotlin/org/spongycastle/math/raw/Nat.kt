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

package org.spongycastle.math.raw

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.toInt
import org.spongycastle.util.Pack.intToBigEndian
import kotlin.jvm.JvmStatic

object Nat {
    private const val M = 0xFFFFFFFFL
    fun add(len: Int, x: IntArray, y: IntArray, z: IntArray): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[i].toLong() and M) + (y[i].toLong() and M)
            z[i] = c.toInt()
            c = c ushr 32
        }
        return c.toInt()
    }

    fun add33At(len: Int, x: Int, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zPos + 0].toLong() and M) + (x.toLong() and M)
        z[zPos + 0] = c.toInt()
        c = c ushr 32
        c += (z[zPos + 1].toLong() and M) + 1L
        z[zPos + 1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zPos + 2)
    }

    fun add33At(len: Int, x: Int, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zOff + zPos].toLong() and M) + (x.toLong() and M)
        z[zOff + zPos] = c.toInt()
        c = c ushr 32
        c += (z[zOff + zPos + 1].toLong() and M) + 1L
        z[zOff + zPos + 1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zOff, zPos + 2)
    }

    fun add33To(len: Int, x: Int, z: IntArray): Int {
        var c = (z[0].toLong() and M) + (x.toLong() and M)
        z[0] = c.toInt()
        c = c ushr 32
        c += (z[1].toLong() and M) + 1L
        z[1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, 2)
    }

    fun add33To(len: Int, x: Int, z: IntArray, zOff: Int): Int {
        var c = (z[zOff + 0].toLong() and M) + (x.toLong() and M)
        z[zOff + 0] = c.toInt()
        c = c ushr 32
        c += (z[zOff + 1].toLong() and M) + 1L
        z[zOff + 1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zOff, 2)
    }

    fun addBothTo(len: Int, x: IntArray, y: IntArray, z: IntArray): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[i].toLong() and M) + (y[i].toLong() and M) + (z[i].toLong() and M)
            z[i] = c.toInt()
            c = c ushr 32
        }
        return c.toInt()
    }

    fun addBothTo(
        len: Int,
        x: IntArray,
        xOff: Int,
        y: IntArray,
        yOff: Int,
        z: IntArray,
        zOff: Int
    ): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[xOff + i].toLong() and M) + (y[yOff + i].toLong() and M) + (z[zOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c ushr 32
        }
        return c.toInt()
    }

    fun addDWordAt(len: Int, x: Long, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zPos + 0].toLong() and M) + (x and M)
        z[zPos + 0] = c.toInt()
        c = c ushr 32
        c += (z[zPos + 1].toLong() and M) + (x ushr 32)
        z[zPos + 1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zPos + 2)
    }

    fun addDWordAt(len: Int, x: Long, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zOff + zPos].toLong() and M) + (x and M)
        z[zOff + zPos] = c.toInt()
        c = c ushr 32
        c += (z[zOff + zPos + 1].toLong() and M) + (x ushr 32)
        z[zOff + zPos + 1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zOff, zPos + 2)
    }

    fun addDWordTo(len: Int, x: Long, z: IntArray): Int {
        var c = (z[0].toLong() and M) + (x and M)
        z[0] = c.toInt()
        c = c ushr 32
        c += (z[1].toLong() and M) + (x ushr 32)
        z[1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, 2)
    }

    fun addDWordTo(len: Int, x: Long, z: IntArray, zOff: Int): Int {
        var c = (z[zOff + 0].toLong() and M) + (x and M)
        z[zOff + 0] = c.toInt()
        c = c ushr 32
        c += (z[zOff + 1].toLong() and M) + (x ushr 32)
        z[zOff + 1] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zOff, 2)
    }

    fun addTo(len: Int, x: IntArray, z: IntArray): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[i].toLong() and M) + (z[i].toLong() and M)
            z[i] = c.toInt()
            c = c ushr 32
        }
        return c.toInt()
    }

    @JvmStatic
    fun addTo(len: Int, x: IntArray, xOff: Int, z: IntArray, zOff: Int): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[xOff + i].toLong() and M) + (z[zOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c ushr 32
        }
        return c.toInt()
    }

    @JvmStatic
    fun addWordAt(len: Int, x: Int, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 1);
        var c = (x.toLong() and M) + (z[zPos].toLong() and M)
        z[zPos] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zPos + 1)
    }

    fun addWordAt(len: Int, x: Int, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= (len - 1);
        var c = (x.toLong() and M) + (z[zOff + zPos].toLong() and M)
        z[zOff + zPos] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zOff, zPos + 1)
    }

    fun addWordTo(len: Int, x: Int, z: IntArray): Int {
        var c = (x.toLong() and M) + (z[0].toLong() and M)
        z[0] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, 1)
    }

    fun addWordTo(len: Int, x: Int, z: IntArray, zOff: Int): Int {
        var c = (x.toLong() and M) + (z[zOff].toLong() and M)
        z[zOff] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zOff, 1)
    }

    fun copy(len: Int, x: IntArray?): IntArray {
        val z = IntArray(len)
        x?.copyInto(destination = z, destinationOffset = 0, startIndex = 0, endIndex = len)
        return z
    }

    fun copy(len: Int, x: IntArray?, z: IntArray?) {
        z?.let {
            x?.copyInto(destination = z, destinationOffset = 0, startIndex = 0, endIndex = len)
        }
    }

    @JvmStatic
    fun create(len: Int): IntArray {
        return IntArray(len)
    }

    fun create64(len: Int): LongArray {
        return LongArray(len)
    }

    fun dec(len: Int, z: IntArray): Int {
        for (i in 0 until len) {
            if (--z[i] != -1) {
                return 0
            }
        }
        return -1
    }

    fun dec(len: Int, x: IntArray, z: IntArray): Int {
        var i = 0
        while (i < len) {
            val c = x[i] - 1
            z[i] = c
            ++i
            if (c != -1) {
                while (i < len) {
                    z[i] = x[i]
                    ++i
                }
                return 0
            }
        }
        return -1
    }

    fun decAt(len: Int, z: IntArray, zPos: Int): Int {
        // assert zPos <= len;
        for (i in zPos until len) {
            if (--z[i] != -1) {
                return 0
            }
        }
        return -1
    }

    fun decAt(len: Int, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= len;
        for (i in zPos until len) {
            if (--z[zOff + i] != -1) {
                return 0
            }
        }
        return -1
    }

    fun eq(len: Int, x: IntArray, y: IntArray): Boolean {
        for (i in len - 1 downTo 0) {
            if (x[i] != y[i]) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun fromBigInteger(bits: Int, x: BigInteger): IntArray {
        var x = x
        require(!(x.signum() < 0 || x.bitLength() > bits))
        val len = bits + 31 shr 5
        val z = create(len)
        var i = 0
        while (x.signum() != 0) {
            z[i++] = x.toInt()
            x = x.shiftRight(32)
        }
        return z
    }

    fun getBit(x: IntArray, bit: Int): Int {
        if (bit == 0) {
            return x[0] and 1
        }
        val w = bit shr 5
        if (w < 0 || w >= x.size) {
            return 0
        }
        val b = bit and 31
        return x[w] ushr b and 1
    }

    fun gte(len: Int, x: IntArray, y: IntArray): Boolean {
        for (i in len - 1 downTo 0) {
            val x_i = x[i] xor Int.MIN_VALUE
            val y_i = y[i] xor Int.MIN_VALUE
            if (x_i < y_i) return false
            if (x_i > y_i) return true
        }
        return true
    }

    fun inc(len: Int, z: IntArray): Int {
        for (i in 0 until len) {
            if (++z[i] != 0) {
                return 0
            }
        }
        return 1
    }

    fun inc(len: Int, x: IntArray, z: IntArray): Int {
        var i = 0
        while (i < len) {
            val c = x[i] + 1
            z[i] = c
            ++i
            if (c != 0) {
                while (i < len) {
                    z[i] = x[i]
                    ++i
                }
                return 0
            }
        }
        return 1
    }

    fun incAt(len: Int, z: IntArray, zPos: Int): Int {
        // assert zPos <= len;
        for (i in zPos until len) {
            if (++z[i] != 0) {
                return 0
            }
        }
        return 1
    }

    @JvmStatic
    fun incAt(len: Int, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= len;
        for (i in zPos until len) {
            if (++z[zOff + i] != 0) {
                return 0
            }
        }
        return 1
    }

    fun isOne(len: Int, x: IntArray): Boolean {
        if (x[0] != 1) {
            return false
        }
        for (i in 1 until len) {
            if (x[i] != 0) {
                return false
            }
        }
        return true
    }

    fun isZero(len: Int, x: IntArray): Boolean {
        for (i in 0 until len) {
            if (x[i] != 0) {
                return false
            }
        }
        return true
    }

    fun mul(len: Int, x: IntArray, y: IntArray, zz: IntArray) {
        zz[len] = mulWord(len, x[0], y, zz)
        for (i in 1 until len) {
            zz[i + len] = mulWordAddTo(len, x[i], y, 0, zz, i)
        }
    }

    fun mul(len: Int, x: IntArray, xOff: Int, y: IntArray, yOff: Int, zz: IntArray, zzOff: Int) {
        zz[zzOff + len] = mulWord(len, x[xOff], y, yOff, zz, zzOff)
        for (i in 1 until len) {
            zz[zzOff + i + len] = mulWordAddTo(len, x[xOff + i], y, yOff, zz, zzOff + i)
        }
    }

    fun mulAddTo(len: Int, x: IntArray, y: IntArray, zz: IntArray): Int {
        var zc: Long = 0
        for (i in 0 until len) {
            var c = mulWordAddTo(len, x[i], y, 0, zz, i).toLong() and M
            c += zc + (zz[i + len].toLong() and M)
            zz[i + len] = c.toInt()
            zc = c ushr 32
        }
        return zc.toInt()
    }

    fun mulAddTo(
        len: Int,
        x: IntArray,
        xOff: Int,
        y: IntArray,
        yOff: Int,
        zz: IntArray,
        zzOff: Int
    ): Int {
        var zzOff = zzOff
        var zc: Long = 0
        for (i in 0 until len) {
            var c = mulWordAddTo(len, x[xOff + i], y, yOff, zz, zzOff).toLong() and M
            c += zc + (zz[zzOff + len].toLong() and M)
            zz[zzOff + len] = c.toInt()
            zc = c ushr 32
            ++zzOff
        }
        return zc.toInt()
    }

    fun mul31BothAdd(
        len: Int,
        a: Int,
        x: IntArray,
        b: Int,
        y: IntArray,
        z: IntArray,
        zOff: Int
    ): Int {
        var c: Long = 0
        val aVal = a.toLong() and M
        val bVal = b.toLong() and M
        var i = 0
        do {
            c += aVal * (x[i].toLong() and M) + bVal * (y[i].toLong() and M) + (z[zOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c ushr 32
        } while (++i < len)
        return c.toInt()
    }

    fun mulWord(len: Int, x: Int, y: IntArray, z: IntArray): Int {
        var c: Long = 0
        val xVal = x.toLong() and M
        var i = 0
        do {
            c += xVal * (y[i].toLong() and M)
            z[i] = c.toInt()
            c = c ushr 32
        } while (++i < len)
        return c.toInt()
    }

    fun mulWord(len: Int, x: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
        var c: Long = 0
        val xVal = x.toLong() and M
        var i = 0
        do {
            c += xVal * (y[yOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c ushr 32
        } while (++i < len)
        return c.toInt()
    }

    fun mulWordAddTo(len: Int, x: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
        var c: Long = 0
        val xVal = x.toLong() and M
        var i = 0
        do {
            c += xVal * (y[yOff + i].toLong() and M) + (z[zOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c ushr 32
        } while (++i < len)
        return c.toInt()
    }

    fun mulWordDwordAddAt(len: Int, x: Int, y: Long, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 3);
        var c: Long = 0
        val xVal = x.toLong() and M
        c += xVal * (y and M) + (z[zPos + 0].toLong() and M)
        z[zPos + 0] = c.toInt()
        c = c ushr 32
        c += xVal * (y ushr 32) + (z[zPos + 1].toLong() and M)
        z[zPos + 1] = c.toInt()
        c = c ushr 32
        c += z[zPos + 2].toLong() and M
        z[zPos + 2] = c.toInt()
        c = c ushr 32
        return if (c == 0L) 0 else incAt(len, z, zPos + 3)
    }

    fun shiftDownBit(len: Int, z: IntArray, c: Int): Int {
        var c = c
        var i = len
        while (--i >= 0) {
            val next = z[i]
            z[i] = next ushr 1 or (c shl 31)
            c = next
        }
        return c shl 31
    }

    fun shiftDownBit(len: Int, z: IntArray, zOff: Int, c: Int): Int {
        var c = c
        var i = len
        while (--i >= 0) {
            val next = z[zOff + i]
            z[zOff + i] = next ushr 1 or (c shl 31)
            c = next
        }
        return c shl 31
    }

    fun shiftDownBit(len: Int, x: IntArray, c: Int, z: IntArray): Int {
        var c = c
        var i = len
        while (--i >= 0) {
            val next = x[i]
            z[i] = next ushr 1 or (c shl 31)
            c = next
        }
        return c shl 31
    }

    fun shiftDownBit(len: Int, x: IntArray, xOff: Int, c: Int, z: IntArray, zOff: Int): Int {
        var c = c
        var i = len
        while (--i >= 0) {
            val next = x[xOff + i]
            z[zOff + i] = next ushr 1 or (c shl 31)
            c = next
        }
        return c shl 31
    }

    fun shiftDownBits(len: Int, z: IntArray, bits: Int, c: Int): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        var i = len
        while (--i >= 0) {
            val next = z[i]
            z[i] = next ushr bits or (c shl -bits)
            c = next
        }
        return c shl -bits
    }

    fun shiftDownBits(len: Int, z: IntArray, zOff: Int, bits: Int, c: Int): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        var i = len
        while (--i >= 0) {
            val next = z[zOff + i]
            z[zOff + i] = next ushr bits or (c shl -bits)
            c = next
        }
        return c shl -bits
    }

    fun shiftDownBits(len: Int, x: IntArray, bits: Int, c: Int, z: IntArray): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        var i = len
        while (--i >= 0) {
            val next = x[i]
            z[i] = next ushr bits or (c shl -bits)
            c = next
        }
        return c shl -bits
    }

    fun shiftDownBits(
        len: Int,
        x: IntArray,
        xOff: Int,
        bits: Int,
        c: Int,
        z: IntArray,
        zOff: Int
    ): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        var i = len
        while (--i >= 0) {
            val next = x[xOff + i]
            z[zOff + i] = next ushr bits or (c shl -bits)
            c = next
        }
        return c shl -bits
    }

    fun shiftDownWord(len: Int, z: IntArray, c: Int): Int {
        var c = c
        var i = len
        while (--i >= 0) {
            val next = z[i]
            z[i] = c
            c = next
        }
        return c
    }

    fun shiftUpBit(len: Int, z: IntArray, c: Int): Int {
        var c = c
        for (i in 0 until len) {
            val next = z[i]
            z[i] = next shl 1 or (c ushr 31)
            c = next
        }
        return c ushr 31
    }

    fun shiftUpBit(len: Int, z: IntArray, zOff: Int, c: Int): Int {
        var c = c
        for (i in 0 until len) {
            val next = z[zOff + i]
            z[zOff + i] = next shl 1 or (c ushr 31)
            c = next
        }
        return c ushr 31
    }

    fun shiftUpBit(len: Int, x: IntArray, c: Int, z: IntArray): Int {
        var c = c
        for (i in 0 until len) {
            val next = x[i]
            z[i] = next shl 1 or (c ushr 31)
            c = next
        }
        return c ushr 31
    }

    fun shiftUpBit(len: Int, x: IntArray, xOff: Int, c: Int, z: IntArray, zOff: Int): Int {
        var c = c
        for (i in 0 until len) {
            val next = x[xOff + i]
            z[zOff + i] = next shl 1 or (c ushr 31)
            c = next
        }
        return c ushr 31
    }

    fun shiftUpBit64(len: Int, x: LongArray, xOff: Int, c: Long, z: LongArray, zOff: Int): Long {
        var c = c
        for (i in 0 until len) {
            val next = x[xOff + i]
            z[zOff + i] = next shl 1 or (c ushr 63)
            c = next
        }
        return c ushr 63
    }

    fun shiftUpBits(len: Int, z: IntArray, bits: Int, c: Int): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        for (i in 0 until len) {
            val next = z[i]
            z[i] = next shl bits or (c ushr -bits)
            c = next
        }
        return c ushr -bits
    }

    fun shiftUpBits(len: Int, z: IntArray, zOff: Int, bits: Int, c: Int): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        for (i in 0 until len) {
            val next = z[zOff + i]
            z[zOff + i] = next shl bits or (c ushr -bits)
            c = next
        }
        return c ushr -bits
    }

    fun shiftUpBits64(len: Int, z: LongArray, zOff: Int, bits: Int, c: Long): Long {
//        assert bits > 0 && bits < 64;
        var c = c
        for (i in 0 until len) {
            val next = z[zOff + i]
            z[zOff + i] = next shl bits or (c ushr -bits)
            c = next
        }
        return c ushr -bits
    }

    fun shiftUpBits(len: Int, x: IntArray, bits: Int, c: Int, z: IntArray): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        for (i in 0 until len) {
            val next = x[i]
            z[i] = next shl bits or (c ushr -bits)
            c = next
        }
        return c ushr -bits
    }

    fun shiftUpBits(
        len: Int,
        x: IntArray,
        xOff: Int,
        bits: Int,
        c: Int,
        z: IntArray,
        zOff: Int
    ): Int {
//        assert bits > 0 && bits < 32;
        var c = c
        for (i in 0 until len) {
            val next = x[xOff + i]
            z[zOff + i] = next shl bits or (c ushr -bits)
            c = next
        }
        return c ushr -bits
    }

    fun shiftUpBits64(
        len: Int,
        x: LongArray,
        xOff: Int,
        bits: Int,
        c: Long,
        z: LongArray,
        zOff: Int
    ): Long {
//        assert bits > 0 && bits < 64;
        var c = c
        for (i in 0 until len) {
            val next = x[xOff + i]
            z[zOff + i] = next shl bits or (c ushr -bits)
            c = next
        }
        return c ushr -bits
    }

    fun square(len: Int, x: IntArray, zz: IntArray) {
        val extLen = len shl 1
        var c = 0
        var j = len
        var k = extLen
        do {
            val xVal = x[--j].toLong() and M
            val p = xVal * xVal
            zz[--k] = c shl 31 or (p ushr 33).toInt()
            zz[--k] = (p ushr 1).toInt()
            c = p.toInt()
        } while (j > 0)
        for (i in 1 until len) {
            c = squareWordAdd(x, i, zz)
            addWordAt(extLen, c, zz, i shl 1)
        }
        shiftUpBit(extLen, zz, x[0] shl 31)
    }

    fun square(len: Int, x: IntArray, xOff: Int, zz: IntArray, zzOff: Int) {
        val extLen = len shl 1
        var c = 0
        var j = len
        var k = extLen
        do {
            val xVal = x[xOff + --j].toLong() and M
            val p = xVal * xVal
            zz[zzOff + --k] = c shl 31 or (p ushr 33).toInt()
            zz[zzOff + --k] = (p ushr 1).toInt()
            c = p.toInt()
        } while (j > 0)
        for (i in 1 until len) {
            c = squareWordAdd(x, xOff, i, zz, zzOff)
            addWordAt(extLen, c, zz, zzOff, i shl 1)
        }
        shiftUpBit(extLen, zz, zzOff, x[xOff] shl 31)
    }

    fun squareWordAdd(x: IntArray, xPos: Int, z: IntArray): Int {
        var c: Long = 0
        val xVal = x[xPos].toLong() and M
        var i = 0
        do {
            c += xVal * (x[i].toLong() and M) + (z[xPos + i].toLong() and M)
            z[xPos + i] = c.toInt()
            c = c ushr 32
        } while (++i < xPos)
        return c.toInt()
    }

    fun squareWordAdd(x: IntArray, xOff: Int, xPos: Int, z: IntArray, zOff: Int): Int {
        var zOff = zOff
        var c: Long = 0
        val xVal = x[xOff + xPos].toLong() and M
        var i = 0
        do {
            c += xVal * (x[xOff + i].toLong() and M) + (z[xPos + zOff].toLong() and M)
            z[xPos + zOff] = c.toInt()
            c = c ushr 32
            ++zOff
        } while (++i < xPos)
        return c.toInt()
    }

    fun sub(len: Int, x: IntArray, y: IntArray, z: IntArray): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[i].toLong() and M) - (y[i].toLong() and M)
            z[i] = c.toInt()
            c = c shr 32
        }
        return c.toInt()
    }

    fun sub(len: Int, x: IntArray, xOff: Int, y: IntArray, yOff: Int, z: IntArray, zOff: Int): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (x[xOff + i].toLong() and M) - (y[yOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c shr 32
        }
        return c.toInt()
    }

    fun sub33At(len: Int, x: Int, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zPos + 0].toLong() and M) - (x.toLong() and M)
        z[zPos + 0] = c.toInt()
        c = c shr 32
        c += (z[zPos + 1].toLong() and M) - 1
        z[zPos + 1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zPos + 2)
    }

    fun sub33At(len: Int, x: Int, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zOff + zPos].toLong() and M) - (x.toLong() and M)
        z[zOff + zPos] = c.toInt()
        c = c shr 32
        c += (z[zOff + zPos + 1].toLong() and M) - 1
        z[zOff + zPos + 1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zOff, zPos + 2)
    }

    fun sub33From(len: Int, x: Int, z: IntArray): Int {
        var c = (z[0].toLong() and M) - (x.toLong() and M)
        z[0] = c.toInt()
        c = c shr 32
        c += (z[1].toLong() and M) - 1
        z[1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, 2)
    }

    fun sub33From(len: Int, x: Int, z: IntArray, zOff: Int): Int {
        var c = (z[zOff + 0].toLong() and M) - (x.toLong() and M)
        z[zOff + 0] = c.toInt()
        c = c shr 32
        c += (z[zOff + 1].toLong() and M) - 1
        z[zOff + 1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zOff, 2)
    }

    fun subBothFrom(len: Int, x: IntArray, y: IntArray, z: IntArray): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (z[i].toLong() and M) - (x[i].toLong() and M) - (y[i].toLong() and M)
            z[i] = c.toInt()
            c = c shr 32
        }
        return c.toInt()
    }

    fun subBothFrom(
        len: Int,
        x: IntArray,
        xOff: Int,
        y: IntArray,
        yOff: Int,
        z: IntArray,
        zOff: Int
    ): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (z[zOff + i].toLong() and M) - (x[xOff + i].toLong() and M) - (y[yOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c shr 32
        }
        return c.toInt()
    }

    fun subDWordAt(len: Int, x: Long, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zPos + 0].toLong() and M) - (x and M)
        z[zPos + 0] = c.toInt()
        c = c shr 32
        c += (z[zPos + 1].toLong() and M) - (x ushr 32)
        z[zPos + 1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zPos + 2)
    }

    fun subDWordAt(len: Int, x: Long, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= (len - 2);
        var c = (z[zOff + zPos].toLong() and M) - (x and M)
        z[zOff + zPos] = c.toInt()
        c = c shr 32
        c += (z[zOff + zPos + 1].toLong() and M) - (x ushr 32)
        z[zOff + zPos + 1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zOff, zPos + 2)
    }

    fun subDWordFrom(len: Int, x: Long, z: IntArray): Int {
        var c = (z[0].toLong() and M) - (x and M)
        z[0] = c.toInt()
        c = c shr 32
        c += (z[1].toLong() and M) - (x ushr 32)
        z[1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, 2)
    }

    fun subDWordFrom(len: Int, x: Long, z: IntArray, zOff: Int): Int {
        var c = (z[zOff + 0].toLong() and M) - (x and M)
        z[zOff + 0] = c.toInt()
        c = c shr 32
        c += (z[zOff + 1].toLong() and M) - (x ushr 32)
        z[zOff + 1] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zOff, 2)
    }

    fun subFrom(len: Int, x: IntArray, z: IntArray): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (z[i].toLong() and M) - (x[i].toLong() and M)
            z[i] = c.toInt()
            c = c shr 32
        }
        return c.toInt()
    }

    @JvmStatic
    fun subFrom(len: Int, x: IntArray, xOff: Int, z: IntArray, zOff: Int): Int {
        var c: Long = 0
        for (i in 0 until len) {
            c += (z[zOff + i].toLong() and M) - (x[xOff + i].toLong() and M)
            z[zOff + i] = c.toInt()
            c = c shr 32
        }
        return c.toInt()
    }

    fun subWordAt(len: Int, x: Int, z: IntArray, zPos: Int): Int {
        // assert zPos <= (len - 1);
        var c = (z[zPos].toLong() and M) - (x.toLong() and M)
        z[zPos] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zPos + 1)
    }

    fun subWordAt(len: Int, x: Int, z: IntArray, zOff: Int, zPos: Int): Int {
        // assert zPos <= (len - 1);
        var c = (z[zOff + zPos].toLong() and M) - (x.toLong() and M)
        z[zOff + zPos] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zOff, zPos + 1)
    }

    fun subWordFrom(len: Int, x: Int, z: IntArray): Int {
        var c = (z[0].toLong() and M) - (x.toLong() and M)
        z[0] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, 1)
    }

    fun subWordFrom(len: Int, x: Int, z: IntArray, zOff: Int): Int {
        var c = (z[zOff + 0].toLong() and M) - (x.toLong() and M)
        z[zOff + 0] = c.toInt()
        c = c shr 32
        return if (c == 0L) 0 else decAt(len, z, zOff, 1)
    }

    @JvmStatic
    fun toBigInteger(len: Int, x: IntArray): BigInteger {
        val bs = ByteArray(len shl 2)
        for (i in 0 until len) {
            val x_i = x[i]
            if (x_i != 0) {
                intToBigEndian(x_i, bs, len - 1 - i shl 2)
            }
        }
//        return BigInteger(1, bs)
        return BigInteger.fromByteArray(bs, Sign.POSITIVE)
    }

    fun zero(len: Int, z: IntArray) {
        for (i in 0 until len) {
            z[i] = 0
        }
    }

    fun zero64(len: Int, z: LongArray) {
        for (i in 0 until len) {
            z[i] = 0L
        }
    }
}
