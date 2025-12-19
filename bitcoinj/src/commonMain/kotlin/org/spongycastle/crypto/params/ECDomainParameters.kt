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

package org.spongycastle.crypto.params

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECConstants
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.util.Arrays
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

class ECDomainParameters @JvmOverloads constructor(
    val curve: ECCurve,
    G: ECPoint,
    @JvmField val n: BigInteger,
    val h: BigInteger = ECConstants.ONE,
    private val seed: ByteArray? = null
) {
    val g: ECPoint

    init {
        g = G.normalize()
    }

    fun getSeed(): ByteArray {
        return Arrays.clone(seed!!)
    }

    override fun equals(
        obj: Any?
    ): Boolean {
        if (this === obj) {
            return true
        }
        if (obj is ECDomainParameters) {
            val other = obj
            return curve.equalsCurve(other.curve) && g.equals(other.g) && n == other.n && h == other.h
        }
        return false
    }

    override fun hashCode(): Int {
        var hc = curve.hashCode()
        hc *= 37
        hc = hc xor g.hashCode()
        hc *= 37
        hc = hc xor n.hashCode()
        hc *= 37
        hc = hc xor h.hashCode()
        return hc
    }
}
