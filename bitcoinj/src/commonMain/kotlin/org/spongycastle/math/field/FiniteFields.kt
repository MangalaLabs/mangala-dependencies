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

package org.spongycastle.math.field

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.bitcoinj.utils.toInt


object FiniteFields {
    val GF_2: FiniteField = PrimeField(BigInteger.TWO)
    val GF_3: FiniteField = PrimeField(BigInteger.TWO + BigInteger.ONE)

    fun getBinaryExtensionField(exponents: IntArray): PolynomialExtensionField {
        require(exponents[0] == 0) { "Irreducible polynomials in GF(2) must have constant term" }
        for (i in 1 until exponents.size) {
            require(exponents[i] > exponents[i - 1]) { "Polynomial exponents must be montonically increasing" }
        }
        return GenericPolynomialExtensionField(GF_2, GF2Polynomial(exponents))
    }

    //    public static PolynomialExtensionField getTernaryExtensionField(Term[] terms)
    //    {
    //        return new GenericPolynomialExtensionField(GF_3, new GF3Polynomial(terms));
    //    }

    fun getPrimeField(characteristic: BigInteger): FiniteField {
        val bitLength = characteristic.bitLength()
        require(!(characteristic.signum() <= 0 || bitLength < 2)) { "'characteristic' must be >= 2" }
        if (bitLength < 3) {
            when (characteristic.toInt()) {
                2 -> return GF_2
                3 -> return GF_3
            }
        }
        return PrimeField(characteristic)
    }
}
