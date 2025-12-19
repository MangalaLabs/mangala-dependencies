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
import org.spongycastle.math.field.FiniteFields.getPrimeField

abstract class CurveAbstractFp protected constructor(q: BigInteger?) : ECCurve(
    getPrimeField(
        q!!
    )
) {
    override fun isValidFieldElement(x: BigInteger?): Boolean {
        return x != null && x.signum() >= 0 && x.compareTo(getCField().getCharacteristic()) < 0
    }

    override fun decompressPoint(yTilde: Int, X1: BigInteger?): ECPoint? {
        val x = fromBigInteger(X1)
        val rhs = x.square().add(getCA()).multiply(x).add(getCB())
        var y = rhs.sqrt() ?: throw IllegalArgumentException("Invalid point compression")

        /*
         * If y is not a square, then we haven't got a point on the curve
         */
        if (y.testBitZero() != (yTilde == 1)) {
            // Use the other root
            y = y.negate()
        }
        return this.createRawPoint(x, y, true)
    }
}
