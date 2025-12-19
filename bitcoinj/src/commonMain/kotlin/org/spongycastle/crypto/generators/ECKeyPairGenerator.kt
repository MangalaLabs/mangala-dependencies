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

package org.spongycastle.crypto.generators

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
import org.spongycastle.crypto.KeyGenerationParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECKeyGenerationParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.math.ec.ECConstants
import org.spongycastle.math.ec.ECMultiplier
import org.spongycastle.math.ec.FixedPointCombMultiplier
import org.spongycastle.math.ec.WNafUtil
import org.spongycastle.util.randomBigInteger

class ECKeyPairGenerator : AsymmetricCipherKeyPairGenerator {
    var params: ECDomainParameters? = null
//    var random: SecureRandom? = null
    override fun init(
        param: KeyGenerationParameters
    ) {
        val ecP = param as ECKeyGenerationParameters
//        random = ecP.random
        params = ecP.domainParameters
//        if (random == null) {
//            random = SecureRandom()
//        }
    }

    /**
     * Given the domain parameters this routine generates an EC key
     * pair in accordance with X9.62 section 5.2.1 pages 26, 27.
     */
    override fun generateKeyPair(): AsymmetricCipherKeyPair {
        val n = params!!.n
        val nBitLength = n.bitLength()
        val minWeight = nBitLength ushr 2
        var d: BigInteger
        while (true) {
            d = randomBigInteger(nBitLength)
            if (d.compareTo(ECConstants.TWO) < 0 || d.compareTo(n) >= 0) {
                continue
            }
            if (WNafUtil.getNafWeight(d) < minWeight) {
                continue
            }
            break
        }
        val Q = createBasePointMultiplier().multiply(params!!.g, d)
        return AsymmetricCipherKeyPair(
            ECPublicKeyParameters(Q, params),
            ECPrivateKeyParameters(d, params)
        )
    }

    protected fun createBasePointMultiplier(): ECMultiplier {
        return FixedPointCombMultiplier()
    }
}
