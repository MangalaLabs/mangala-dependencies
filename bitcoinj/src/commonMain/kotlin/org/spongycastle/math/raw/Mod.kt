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

import kotlin.jvm.JvmStatic
import kotlin.random.Random

object Mod {
    fun inverse32(d: Int): Int {
//        int x = d + (((d + 1) & 4) << 1);   // d.x == 1 mod 2**4
        var x = d // d.x == 1 mod 2**3
        x *= 2 - d * x // d.x == 1 mod 2**6
        x *= 2 - d * x // d.x == 1 mod 2**12
        x *= 2 - d * x // d.x == 1 mod 2**24
        x *= 2 - d * x // d.x == 1 mod 2**48
        //        assert d * x == 1;
        return x
    }

    @JvmStatic
    fun invert(p: IntArray, x: IntArray, z: IntArray) {
        val len = p.size
        require(!Nat.isZero(len, x)) { "'x' cannot be 0" }
        if (Nat.isOne(len, x)) {
            x.copyInto(destination = z, destinationOffset = 0, startIndex = 0, endIndex = len)
            return
        }
        val u = Nat.copy(len, x)
        val a = Nat.create(len)
        a[0] = 1
        var ac = 0
        if (u[0] and 1 == 0) {
            ac = inversionStep(p, u, len, a, ac)
        }
        if (Nat.isOne(len, u)) {
            inversionResult(p, ac, a, z)
            return
        }
        val v = Nat.copy(len, p)
        val b = Nat.create(len)
        var bc = 0
        var uvLen = len
        while (true) {
            while (u[uvLen - 1] == 0 && v[uvLen - 1] == 0) {
                --uvLen
            }
            if (Nat.gte(uvLen, u, v)) {
                Nat.subFrom(uvLen, v, u)
                //              assert (u[0] & 1) == 0;
                ac += Nat.subFrom(len, b, a) - bc
                ac = inversionStep(p, u, uvLen, a, ac)
                if (Nat.isOne(uvLen, u)) {
                    inversionResult(p, ac, a, z)
                    return
                }
            } else {
                Nat.subFrom(uvLen, u, v)
                //              assert (v[0] & 1) == 0;
                bc += Nat.subFrom(len, a, b) - ac
                bc = inversionStep(p, v, uvLen, b, bc)
                if (Nat.isOne(uvLen, v)) {
                    inversionResult(p, bc, b, z)
                    return
                }
            }
        }
    }

    fun random(p: IntArray): IntArray {
        val len = p.size
        val s = Nat.create(len)
        var m = p[len - 1]
        m = m or (m ushr 1)
        m = m or (m ushr 2)
        m = m or (m ushr 4)
        m = m or (m ushr 8)
        m = m or (m ushr 16)
        do {
            for (i in 0 until len) {
                s[i] = Random.nextInt()
            }
            s[len - 1] = s[len - 1] and m
        } while (Nat.gte(len, s, p))
        return s
    }

    fun add(p: IntArray, x: IntArray, y: IntArray, z: IntArray) {
        val len = p.size
        val c = Nat.add(len, x, y, z)
        if (c != 0) {
            Nat.subFrom(len, p, z)
        }
    }

    fun subtract(p: IntArray, x: IntArray, y: IntArray, z: IntArray) {
        val len = p.size
        val c = Nat.sub(len, x, y, z)
        if (c != 0) {
            Nat.addTo(len, p, z)
        }
    }

    private fun inversionResult(p: IntArray, ac: Int, a: IntArray, z: IntArray) {
        if (ac < 0) {
            Nat.add(p.size, a, p, z)
        } else {
            a.copyInto(destination = z, destinationOffset = 0, startIndex = 0, endIndex = p.size)
        }
    }

    private fun inversionStep(p: IntArray, u: IntArray, uLen: Int, x: IntArray, xc: Int): Int {
        var xc = xc
        val len = p.size
        var count = 0
        while (u[0] == 0) {
            Nat.shiftDownWord(uLen, u, 0)
            count += 32
        }
        run {
            val zeroes = getTrailingZeroes(u[0])
            if (zeroes > 0) {
                Nat.shiftDownBits(uLen, u, zeroes, 0)
                count += zeroes
            }
        }
        for (i in 0 until count) {
            if (x[0] and 1 != 0) {
                xc += if (xc < 0) {
                    Nat.addTo(len, p, x)
                } else {
                    Nat.subFrom(len, p, x)
                }
            }

//            assert xc == 0 || xc == 1;
            Nat.shiftDownBit(len, x, xc)
        }
        return xc
    }

    private fun getTrailingZeroes(x: Int): Int {
//        assert x != 0;
        var x = x
        var count = 0
        while (x and 1 == 0) {
            x = x ushr 1
            ++count
        }
        return count
    }
}
