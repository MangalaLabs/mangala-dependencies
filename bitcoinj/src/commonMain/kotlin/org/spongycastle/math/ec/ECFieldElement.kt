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
import org.spongycastle.util.BigIntegers.asUnsignedByteArray

abstract class ECFieldElement {
    abstract fun toBigInteger(): BigInteger
    abstract fun getFieldName(): String?
    abstract fun getFieldSize(): Int
    abstract fun add(b: ECFieldElement): ECFieldElement
    abstract fun addOne(): ECFieldElement
    abstract fun subtract(b: ECFieldElement): ECFieldElement
    abstract fun multiply(b: ECFieldElement): ECFieldElement
    abstract fun divide(b: ECFieldElement): ECFieldElement
    abstract fun negate(): ECFieldElement
    abstract fun square(): ECFieldElement
    abstract fun invert(): ECFieldElement
    abstract fun sqrt(): ECFieldElement?
    open fun bitLength(): Int {
        return toBigInteger().bitLength()
    }

    open fun isOne(): Boolean{
        return bitLength() == 1
    }
//        get() =
    open fun isZero(): Boolean{
        return 0 == toBigInteger().signum()
    }
//        get() =

    open fun multiplyMinusProduct(
        b: ECFieldElement,
        x: ECFieldElement,
        y: ECFieldElement
    ): ECFieldElement {
        return multiply(b).subtract(x.multiply(y))
    }

    open fun multiplyPlusProduct(
        b: ECFieldElement,
        x: ECFieldElement,
        y: ECFieldElement
    ): ECFieldElement? {
        return multiply(b).add(x.multiply(y))
    }

    open fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return square().subtract(x.multiply(y))
    }

    open fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return square().add(x.multiply(y))
    }

    open fun squarePow(pow: Int): ECFieldElement {
        var r = this
        for (i in 0 until pow) {
            r = r.square()
        }
        return r
    }

//    open fun testBitZero(): Boolean {
//        return toBigInteger().testBit(0)
//    }

    open fun testBitZero(): Boolean {
        val bigInt = toBigInteger()
        // In kotlin-multiplatform-bignum, you might need to directly compare the bit value
        // Assuming `bitAt` or similar method is available or implemented by you for getting the bit value
        return bigInt.bitAt(0L)
    }

    override fun toString(): String {
        return this.toBigInteger().toString(16)
    }

    val encoded: ByteArray
        get() = asUnsignedByteArray((getFieldSize() + 7) / 8, toBigInteger())
}
