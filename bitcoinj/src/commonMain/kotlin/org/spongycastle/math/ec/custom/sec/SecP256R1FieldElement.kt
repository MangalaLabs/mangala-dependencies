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
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.add
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.addOne
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.fromBigInteger
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.multiply
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.negate
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.square
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.squareN
import org.spongycastle.math.ec.custom.sec.SecP256R1Field.subtract
import org.spongycastle.math.raw.Mod
import org.spongycastle.math.raw.Nat256
import org.spongycastle.util.Arrays
import kotlin.jvm.JvmField

class SecP256R1FieldElement : ECFieldElement {
    @JvmField
    var x: IntArray

    constructor(x: BigInteger) {
        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP256R1FieldElement" }
        this.x = fromBigInteger(x)
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
        return "SecP256R1Field"
    }

    override fun getFieldSize(): Int {
        return Q.bitLength()
    }

    override fun add(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        add(x, (b as SecP256R1FieldElement).x, z)
        return SecP256R1FieldElement(z)
    }

    override fun addOne(): ECFieldElement {
        val z = Nat256.create()
        addOne(x, z)
        return SecP256R1FieldElement(z)
    }

    override fun subtract(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        subtract(x, (b as SecP256R1FieldElement).x, z)
        return SecP256R1FieldElement(z)
    }

    override fun multiply(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        multiply(x, (b as SecP256R1FieldElement).x, z)
        return SecP256R1FieldElement(z)
    }

    override fun divide(b: ECFieldElement): ECFieldElement {
//        return multiply(b.invert());
        val z = Nat256.create()
        Mod.invert(SecP256R1Field.P, (b as SecP256R1FieldElement).x, z)
        multiply(z, x, z)
        return SecP256R1FieldElement(z)
    }

    override fun negate(): ECFieldElement {
        val z = Nat256.create()
        negate(x, z)
        return SecP256R1FieldElement(z)
    }

    override fun square(): ECFieldElement {
        val z = Nat256.create()
        square(x, z)
        return SecP256R1FieldElement(z)
    }

    override fun invert(): ECFieldElement {
//        return new SecP256R1FieldElement(toBigInteger().modInverse(Q));
        val z = Nat256.create()
        Mod.invert(SecP256R1Field.P, x, z)
        return SecP256R1FieldElement(z)
    }

    /**
     * return a sqrt root - the routine verifies that the calculation returns the right value - if
     * none exists it returns null.
     */
    override fun sqrt(): ECFieldElement? {
        // Raise this element to the exponent 2^254 - 2^222 + 2^190 + 2^94
        val x1 = x
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this
        }
        val t1 = Nat256.create()
        val t2 = Nat256.create()
        square(x1, t1)
        multiply(t1, x1, t1)
        squareN(t1, 2, t2)
        multiply(t2, t1, t2)
        squareN(t2, 4, t1)
        multiply(t1, t2, t1)
        squareN(t1, 8, t2)
        multiply(t2, t1, t2)
        squareN(t2, 16, t1)
        multiply(t1, t2, t1)
        squareN(t1, 32, t1)
        multiply(t1, x1, t1)
        squareN(t1, 96, t1)
        multiply(t1, x1, t1)
        squareN(t1, 94, t1)
        square(t1, t2)
        return if (Nat256.eq(x1, t2)) SecP256R1FieldElement(t1) else null
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is SecP256R1FieldElement) {
            return false
        }
        return Nat256.eq(x, other.x)
    }

    override fun hashCode(): Int {
        return Q.hashCode() xor Arrays.hashCode(x, 0, 8)
    }

    companion object {
        val Q = SecP256R1Curve.q
    }
}
