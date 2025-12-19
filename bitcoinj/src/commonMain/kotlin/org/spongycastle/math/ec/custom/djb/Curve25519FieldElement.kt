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

package org.spongycastle.math.ec.custom.djb

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.custom.djb.Curve25519.Companion.q
import org.spongycastle.math.ec.custom.djb.Curve25519Field.add
import org.spongycastle.math.ec.custom.djb.Curve25519Field.addOne
import org.spongycastle.math.ec.custom.djb.Curve25519Field.fromBigInteger
import org.spongycastle.math.ec.custom.djb.Curve25519Field.multiply
import org.spongycastle.math.ec.custom.djb.Curve25519Field.negate
import org.spongycastle.math.ec.custom.djb.Curve25519Field.square
import org.spongycastle.math.ec.custom.djb.Curve25519Field.squareN
import org.spongycastle.math.ec.custom.djb.Curve25519Field.subtract
import org.spongycastle.math.raw.Mod
import org.spongycastle.math.raw.Nat256
import org.spongycastle.util.Arrays
import kotlin.jvm.JvmField

class Curve25519FieldElement : ECFieldElement {
    @JvmField
    var x: IntArray

    constructor(x: BigInteger?) {
        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for Curve25519FieldElement" }
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
        return "Curve25519Field"
    }

    override fun getFieldSize(): Int {
        return Q.bitLength()
    }

    override fun add(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        add(x, (b as Curve25519FieldElement).x, z)
        return Curve25519FieldElement(z)
    }

    override fun addOne(): ECFieldElement {
        val z = Nat256.create()
        addOne(x, z)
        return Curve25519FieldElement(z)
    }

    override fun subtract(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        subtract(x, (b as Curve25519FieldElement).x, z)
        return Curve25519FieldElement(z)
    }

    override fun multiply(b: ECFieldElement): ECFieldElement {
        val z = Nat256.create()
        multiply(x, (b as Curve25519FieldElement).x, z)
        return Curve25519FieldElement(z)
    }

    override fun divide(b: ECFieldElement): ECFieldElement {
//        return multiply(b.invert());
        val z = Nat256.create()
        Mod.invert(Curve25519Field.P, (b as Curve25519FieldElement).x, z)
        multiply(z, x, z)
        return Curve25519FieldElement(z)
    }

    override fun negate(): ECFieldElement {
        val z = Nat256.create()
        negate(x, z)
        return Curve25519FieldElement(z)
    }

    override fun square(): ECFieldElement {
        val z = Nat256.create()
        square(x, z)
        return Curve25519FieldElement(z)
    }

    override fun invert(): ECFieldElement {
//        return new Curve25519FieldElement(toBigInteger().modInverse(Q));
        val z = Nat256.create()
        Mod.invert(Curve25519Field.P, x, z)
        return Curve25519FieldElement(z)
    }

    /**
     * return a sqrt root - the routine verifies that the calculation returns the right value - if
     * none exists it returns null.
     */
    override fun sqrt(): ECFieldElement {
        /*
         * Q == 8m + 5, so we use Pocklington's method for this case.
         *
         * First, raise this element to the exponent 2^252 - 2^1 (i.e. m + 1)
         * 
         * Breaking up the exponent's binary representation into "repunits", we get:
         * { 251 1s } { 1 0s }
         * 
         * Therefore we need an addition chain containing 251 (the lengths of the repunits)
         * We use: 1, 2, 3, 4, 7, 11, 15, 30, 60, 120, 131, [251]
         */
        val x1 = x
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this
        }
        val x2 = Nat256.create()
        square(x1, x2)
        multiply(x2, x1, x2)
        square(x2, x2)
        multiply(x2, x1, x2)
        val x4 = Nat256.create()
        square(x2, x4)
        multiply(x4, x1, x4)
        val x7 = Nat256.create()
        squareN(x4, 3, x7)
        multiply(x7, x2, x7)
        squareN(x7, 4, x2)
        multiply(x2, x4, x2)
        squareN(x2, 4, x7)
        multiply(x7, x4, x7)
        squareN(x7, 15, x4)
        multiply(x4, x7, x4)
        squareN(x4, 30, x7)
        multiply(x7, x4, x7)
        squareN(x7, 60, x4)
        multiply(x4, x7, x4)
        squareN(x4, 11, x7)
        multiply(x7, x2, x7)
        squareN(x7, 120, x2)
        multiply(x2, x4, x2)
        square(x2, x2)
        square(x2, x4)
        if (Nat256.eq(x1, x4)) {
            return Curve25519FieldElement(x2)
        }

        /*
         * If the first guess is incorrect, we multiply by a precomputed power of 2 to get the second guess,
         * which is ((4x)^(m + 1))/2 mod Q
         */multiply(x2, PRECOMP_POW2, x2)
        square(x2, x4)
        return if (Nat256.eq(x1, x4)) {
            Curve25519FieldElement(x2)
        } else Curve25519FieldElement(x2)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is Curve25519FieldElement) {
            return false
        }
        return Nat256.eq(x, other.x)
    }

    override fun hashCode(): Int {
        return Q.hashCode() xor Arrays.hashCode(x, 0, 8)
    }

    companion object {
        val Q = q

        // Calculated as ECConstants.TWO.modPow(Q.shiftRight(2), Q)
        private val PRECOMP_POW2 = intArrayOf(
            0x4a0ea0b0, -0x3b11e4d9, -0x52d01b88, 0x2f431806,
            0x3dfbd7a7, 0x2b4d0099, 0x4fc1df0b, 0x2b832480
        )
    }
}
