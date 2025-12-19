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

import kotlin.jvm.JvmStatic

object FixedPointUtil {
    const val PRECOMP_NAME = "bc_fixed_point"
    @JvmStatic
    fun getCombSize(c: ECCurve?): Int {
        val order = c?.getCOrder()
        return order?.bitLength() ?: (c?.getFieldSize() ?: 0 + 1)
    }

    fun getFixedPointPreCompInfo(preCompInfo: PreCompInfo?): FixedPointPreCompInfo {
        return if (preCompInfo != null && preCompInfo is FixedPointPreCompInfo) {
            preCompInfo
        } else FixedPointPreCompInfo()
    }

    @JvmStatic
    fun precompute(p: ECPoint?, minWidth: Int): FixedPointPreCompInfo {
        val c = p?.getCurve()
        val n = 1 shl minWidth
        val info = getFixedPointPreCompInfo(c?.getPreCompInfo(p, PRECOMP_NAME))
        var lookupTable = info.preComp
        if (lookupTable == null || lookupTable.size < n) {
            val bits = getCombSize(c)
            val d = (bits + minWidth - 1) / minWidth
            val pow2Table = arrayOfNulls<ECPoint>(minWidth + 1)
            pow2Table[0] = p
            for (i in 1 until minWidth) {
                pow2Table[i] = pow2Table[i - 1]!!.timesPow2(d)
            }

            // This will be the 'offset' value 
            pow2Table[minWidth] = pow2Table[0]!!.subtract(pow2Table[1])
            c?.normalizeAll(pow2Table)
            lookupTable = arrayOfNulls(n)
            lookupTable[0] = pow2Table[0]
            for (bit in minWidth - 1 downTo 0) {
                val pow2 = pow2Table[bit]
                val step = 1 shl bit
                var i = step
                while (i < n) {
                    lookupTable[i] = lookupTable[i - step]!!.add(pow2)
                    i += step shl 1
                }
            }
            c?.normalizeAll(lookupTable)
            info.offset = pow2Table[minWidth]
            info.preComp = lookupTable
            info.width = minWidth
            c?.setPreCompInfo(p, PRECOMP_NAME, info)
        }
        return info
    }
}
