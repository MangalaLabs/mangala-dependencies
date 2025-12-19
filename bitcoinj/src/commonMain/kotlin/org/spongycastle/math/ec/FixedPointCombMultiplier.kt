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
import com.mangala.wallet.bitcoinj.utils.testBit
import org.spongycastle.math.ec.FixedPointUtil.getCombSize
import org.spongycastle.math.ec.FixedPointUtil.precompute

class FixedPointCombMultiplier : AbstractECMultiplier() {
    override fun multiplyPositive(p: ECPoint, k: BigInteger): ECPoint {
        val c = p.getCurve()
        val size = getCombSize(c)
        check(k.bitLength() <= size) {
            /*
             * TODO The comb works best when the scalars are less than the (possibly unknown) order.
             * Still, if we want to handle larger scalars, we could allow customization of the comb
             * size, or alternatively we could deal with the 'extra' bits either by running the comb
             * multiple times as necessary, or by using an alternative multiplier as prelude.
             */
            "fixed-point comb doesn't support scalars larger than the curve order"
        }
        val minWidth = getWidthForCombSize(size)
        val info = precompute(p, minWidth)
        val lookupTable = info.preComp
        val width = info.width
        val d = (size + width - 1) / width
        var R = c.getNewInfinity()
        val top = d * width - 1
        for (i in 0 until d) {
            var index = 0
            var j = top - i
            while (j >= 0) {
                index = index shl 1
                if (k.testBit(j)) {
                    index = index or 1
                }
                j -= d
            }
            R = R.twicePlus(lookupTable!![index])!!
        }
        return R.add(info.offset)!!
    }

    protected fun getWidthForCombSize(combSize: Int): Int {
        return if (combSize > 257) 6 else 5
    }
}
