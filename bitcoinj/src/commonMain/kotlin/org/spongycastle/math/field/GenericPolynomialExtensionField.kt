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
import org.spongycastle.util.Integers.rotateLeft


internal class GenericPolynomialExtensionField(
    protected val mSubfield: FiniteField,
    protected val mMinimalPolynomial: Polynomial
) : PolynomialExtensionField {
    override fun getCharacteristic(): BigInteger {
        return mSubfield.getCharacteristic()
    }

    override fun getDimension(): Int {
        return mSubfield.getDimension() * mMinimalPolynomial.getDegree()
    }

    override fun getSubfield(): FiniteField {
        return mSubfield
    }

    override fun getDegree(): Int {
        return mMinimalPolynomial.getDegree()
    }

    override fun getMinimalPolynomial(): Polynomial {
        return mMinimalPolynomial
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj !is GenericPolynomialExtensionField) {
            return false
        }
        val other = obj
        return mSubfield == other.mSubfield && mMinimalPolynomial == other.mMinimalPolynomial
    }

    override fun hashCode(): Int {
        return (mSubfield.hashCode()
                xor rotateLeft(mMinimalPolynomial.hashCode(), 16))
    }
}
