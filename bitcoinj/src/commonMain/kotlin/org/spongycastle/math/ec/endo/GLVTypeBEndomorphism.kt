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

package org.spongycastle.math.ec.endo

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.testBit
import org.spongycastle.math.ec.ECConstants
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECPointMap
import org.spongycastle.math.ec.ScaleXPointMap

class GLVTypeBEndomorphism(
    protected val curve: ECCurve,
    protected val parameters: GLVTypeBParameters
) : GLVEndomorphism {
    protected val mPointMap: ECPointMap

    init {
        mPointMap = ScaleXPointMap(curve.fromBigInteger(parameters.beta))
    }

    override fun decomposeScalar(k: BigInteger): Array<BigInteger> {
        val bits = parameters.bits
        val b1 = calculateB(k, parameters.g1, bits)
        val b2 = calculateB(k, parameters.g2, bits)
        val p = parameters
        val a = k.subtract(b1.multiply(p.v1A!!).add(b2.multiply(p.v2A!!)))
        val b = b1.multiply(p.v1B!!).add(b2.multiply(p.v2B!!)).negate()
        return arrayOf(a, b)
    }

    override fun getPointMap(): ECPointMap {
        return mPointMap
    }

    override fun hasEfficientPointMap(): Boolean {
        return true
    }

    protected fun calculateB(k: BigInteger, g: BigInteger, t: Int): BigInteger {
        val negative = g.signum() < 0
        var b = k.multiply(g.abs())
        val extra = b.testBit(t - 1)
        b = b.shiftRight(t)
        if (extra) {
            b = b.add(ECConstants.ONE)
        }
        return if (negative) b.negate() else b
    }
}
