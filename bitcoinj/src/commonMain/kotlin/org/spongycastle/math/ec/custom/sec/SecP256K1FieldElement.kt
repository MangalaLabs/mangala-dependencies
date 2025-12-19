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

package org.spongycastle.math.ec.custom.sec

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.add
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.addOne
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.fromBigInteger
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.multiply
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.negate
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.square
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.squareN
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.subtract
import org.spongycastle.math.raw.Mod
import org.spongycastle.math.raw.Nat256
import org.spongycastle.util.Arrays
import kotlin.jvm.JvmField

class SecP256K1FieldElement : ECFieldElement {
    @JvmField
    var x: IntArray

    constructor(x: BigInteger?) {
        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP256K1FieldElement" }
        this.x = fromBigInteger(x!!)
    }

    constructor() {
        x = Nat256.create()
    }

    constructor(x: IntArray) {
        this.x = x
    }

    override fun isZero(): Boolean {
        return Nat256.isZero(x)
    }

    override fun isOne(): Boolean {
        return Nat256.isOne(x)
    }

    override fun testBitZero(): Boolean {
        return Nat256.getBit(x, 0) == 1
    }

    override fun toBigInteger(): BigInteger {
        return Nat256.toBigInteger(x)
    }

    override fun getFieldName(): String {
        return "SecP256K1Field"
    }

    override fun getFieldSize(): Int {
        return Q.bitLength()
    }

    override fun add(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        add(x, (b as SecP256K1FieldElement).x, z)
        return SecP256K1FieldElement(z)
    }

    override fun addOne(): ECFieldElement {
        val z = Nat256.create()
        addOne(x, z)
        return SecP256K1FieldElement(z)
    }

    override fun subtract(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        subtract(x, (b as SecP256K1FieldElement).x, z)
        return SecP256K1FieldElement(z)
    }

    override fun multiply(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        multiply(x, (b as SecP256K1FieldElement).x, z)
        return SecP256K1FieldElement(z)
    }

    override fun divide(b: ECFieldElement): ECFieldElement {
//        return multiply(b.invert());
        val z = Nat256.create()
        Mod.invert(SecP256K1Field.P, (b as SecP256K1FieldElement).x, z)
        multiply(z, x, z)
        return SecP256K1FieldElement(z)
    }

    override fun negate(): ECFieldElement {
        val z = Nat256.create()
        negate(x, z)
        return SecP256K1FieldElement(z)
    }

    override fun square(): ECFieldElement {
        val z = Nat256.create()
        square(x, z)
        return SecP256K1FieldElement(z)
    }

    override fun invert(): ECFieldElement {
//        return new SecP256K1FieldElement(toBigInteger().modInverse(Q));
        val z = Nat256.create()
        Mod.invert(SecP256K1Field.P, x, z)
        return SecP256K1FieldElement(z)
    }
    // D.1.4 91
    /**
     * return a sqrt root - the routine verifies that the calculation returns the right value - if
     * none exists it returns null.
     */
    override fun sqrt(): ECFieldElement? {
        /*
         * Raise this element to the exponent 2^254 - 2^30 - 2^7 - 2^6 - 2^5 - 2^4 - 2^2
         *
         * Breaking up the exponent's binary representation into "repunits", we get:
         * { 223 1s } { 1 0s } { 22 1s } { 4 0s } { 2 1s } { 2 0s}
         *
         * Therefore we need an addition chain containing 2, 22, 223 (the lengths of the repunits)
         * We use: 1, [2], 3, 6, 9, 11, [22], 44, 88, 176, 220, [223]
         */
        val x1 = x
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this
        }
        val x2 = Nat256.create()
        square(x1, x2)
        multiply(x2, x1, x2)
        val x3 = Nat256.create()
        square(x2, x3)
        multiply(x3, x1, x3)
        val x6 = Nat256.create()
        squareN(x3, 3, x6)
        multiply(x6, x3, x6)
        squareN(x6, 3, x6)
        multiply(x6, x3, x6)
        squareN(x6, 2, x6)
        multiply(x6, x2, x6)
        val x22 = Nat256.create()
        squareN(x6, 11, x22)
        multiply(x22, x6, x22)
        squareN(x22, 22, x6)
        multiply(x6, x22, x6)
        val x88 = Nat256.create()
        squareN(x6, 44, x88)
        multiply(x88, x6, x88)
        val x176 = Nat256.create()
        squareN(x88, 88, x176)
        multiply(x176, x88, x176)
        squareN(x176, 44, x88)
        multiply(x88, x6, x88)
        squareN(x88, 3, x6)
        multiply(x6, x3, x6)
        squareN(x6, 23, x6)
        multiply(x6, x22, x6)
        squareN(x6, 6, x6)
        multiply(x6, x2, x6)
        squareN(x6, 2, x6)
        square(x6, x2)
        return if (Nat256.eq(x1, x2)) SecP256K1FieldElement(x6) else null
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is SecP256K1FieldElement) {
            return false
        }
        return Nat256.eq(x, other.x)
    }

    override fun hashCode(): Int {
        return Q.hashCode() xor Arrays.hashCode(x, 0, 8)
    }

    companion object {
        val Q = SecP256K1Curve.q
    }
}
