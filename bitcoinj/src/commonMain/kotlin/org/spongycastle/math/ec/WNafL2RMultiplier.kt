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

package org.spongycastle.math.ec

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.WNafUtil.generateCompactWindowNaf
import org.spongycastle.math.ec.WNafUtil.precompute
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Class implementing the WNAF (Window Non-Adjacent Form) multiplication
 * algorithm.
 */
class WNafL2RMultiplier : AbstractECMultiplier() {
    /**
     * Multiplies `this` by an integer `k` using the
     * Window NAF method.
     * @param k The integer by which `this` is multiplied.
     * @return A new `ECPoint` which equals `this`
     * multiplied by `k`.
     */
    override fun multiplyPositive(p: ECPoint, k: BigInteger): ECPoint {
        // Clamp the window width in the range [2, 16]
        val width = max(2, min(16, getWindowSize(k.bitLength())))
        val wnafPreCompInfo = precompute(p, width, true)
        val preComp = wnafPreCompInfo.getPreComp()
        val preCompNeg = wnafPreCompInfo.getPreCompNeg()
        val wnaf = generateCompactWindowNaf(width, k)
        var R = p.getCurve().getNewInfinity()
        var i = wnaf.size

        /*
         * NOTE: We try to optimize the first window using the precomputed points to substitute an
         * addition for 2 or more doublings.
         */if (i > 1) {
            val wi = wnaf[--i]
            val digit = wi shr 16
            var zeroes = wi and 0xFFFF
            val n = abs(digit)
            val table = if (digit < 0) preCompNeg else preComp

            // Optimization can only be used for values in the lower half of the table
            if (n shl 2 < 1 shl width) {
                val highest = NewLongArray.bitLengths[n].toInt()

                // TODO Get addition/doubling cost ratio from curve and compare to 'scale' to see if worth substituting?
                val scale = width - highest
                val lowBits = n xor (1 shl highest - 1)
                val i1 = (1 shl width - 1) - 1
                val i2 = (lowBits shl scale) + 1
                R = table?.get(i1 ushr 1)?.add(table[i2 ushr 1]) ?: throw IllegalStateException("Precomputed table for " + p::class + " must contain value for 2^(width - 1)")
                zeroes -= scale

//              System.out.println("Optimized: 2^" + scale + " * " + n + " = " + i1 + " + " + i2);
            } else {
                R = table?.get(n ushr 1) ?: throw IllegalStateException("Precomputed table for " +  p::class + " must contain value for 2^(width - 1)")
            }
            R = R.timesPow2(zeroes)
        }
        while (i > 0) {
            val wi = wnaf[--i]
            val digit = wi shr 16
            val zeroes = wi and 0xFFFF
            val n = abs(digit)
            val table = if (digit < 0) preCompNeg else preComp
            val r = table?.get(n ushr 1)
            R = R.twicePlus(r)!!
            R = R.timesPow2(zeroes)
        }
        return R
    }

    /**
     * Determine window width to use for a scalar multiplication of the given size.
     *
     * @param bits the bit-length of the scalar to multiply by
     * @return the window size to use
     */
    protected fun getWindowSize(bits: Int): Int {
        return WNafUtil.getWindowSize(bits)
    }
}
