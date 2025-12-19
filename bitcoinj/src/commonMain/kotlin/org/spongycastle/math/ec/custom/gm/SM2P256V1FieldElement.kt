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

package org.spongycastle.math.ec.custom.gm

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.add
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.addOne
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.fromBigInteger
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.multiply
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.negate
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.square
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.squareN
import org.spongycastle.math.ec.custom.gm.SM2P256V1Field.subtract
import org.spongycastle.math.raw.Mod
import org.spongycastle.math.raw.Nat256
import org.spongycastle.util.Arrays
import kotlin.jvm.JvmField

class SM2P256V1FieldElement : ECFieldElement {
    @JvmField
    var x: IntArray

    constructor(x: BigInteger?) {
        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SM2P256V1FieldElement" }
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
        return "SM2P256V1Field"
    }

    override fun getFieldSize(): Int {
        return Q.bitLength()
    }

    override fun add(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        add(x, (b as SM2P256V1FieldElement).x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun addOne(): ECFieldElement {
        val z = Nat256.create()
        addOne(x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun subtract(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        subtract(x, (b as SM2P256V1FieldElement).x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun multiply(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        multiply(x, (b as SM2P256V1FieldElement).x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun divide(b: ECFieldElement): ECFieldElement {
//        return multiply(b.invert());
        val z = Nat256.create()
        Mod.invert(SM2P256V1Field.P, (b as SM2P256V1FieldElement).x, z)
        multiply(z, x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun negate(): ECFieldElement {
        val z = Nat256.create()
        negate(x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun square(): ECFieldElement {
        val z = Nat256.create()
        square(x, z)
        return SM2P256V1FieldElement(z)
    }

    override fun invert(): ECFieldElement {
//        return new SM2P256V1FieldElement(toBigInteger().modInverse(Q));
        val z = Nat256.create()
        Mod.invert(SM2P256V1Field.P, x, z)
        return SM2P256V1FieldElement(z)
    }

    /**
     * return a sqrt root - the routine verifies that the calculation returns the right value - if
     * none exists it returns null.
     */
    override fun sqrt(): ECFieldElement? {
        /*
         * Raise this element to the exponent 2^254 - 2^222 - 2^94 + 2^62
         *
         * Breaking up the exponent's binary representation into "repunits", we get:
         * { 31 1s } { 1 0s } { 128 1s } { 31 0s } { 1 1s } { 62 0s}
         *
         * We use an addition chain for the beginning: [1], 2, 3, 6, 12, [24], 30, [31] 
         */
        val x1 = x
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this
        }
        val x2 = Nat256.create()
        square(x1, x2)
        multiply(x2, x1, x2)
        val x4 = Nat256.create()
        squareN(x2, 2, x4)
        multiply(x4, x2, x4)
        val x6 = Nat256.create()
        squareN(x4, 2, x6)
        multiply(x6, x2, x6)
        squareN(x6, 6, x2)
        multiply(x2, x6, x2)
        val x24 = Nat256.create()
        squareN(x2, 12, x24)
        multiply(x24, x2, x24)
        squareN(x24, 6, x2)
        multiply(x2, x6, x2)
        square(x2, x6)
        multiply(x6, x1, x6)
        squareN(x6, 31, x24)
        multiply(x24, x6, x2)
        squareN(x24, 32, x24)
        multiply(x24, x2, x24)
        squareN(x24, 62, x24)
        multiply(x24, x2, x24)
        squareN(x24, 4, x24)
        multiply(x24, x4, x24)
        squareN(x24, 32, x24)
        multiply(x24, x1, x24)
        squareN(x24, 62, x24)
        square(x24, x4)
        return if (Nat256.eq(x1, x4)) SM2P256V1FieldElement(x24) else null
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is SM2P256V1FieldElement) {
            return false
        }
        return Nat256.eq(x, other.x)
    }

    override fun hashCode(): Int {
        return Q.hashCode() xor Arrays.hashCode(x, 0, 8)
    }

    companion object {
        val Q = SM2P256V1Curve.q
    }
}
