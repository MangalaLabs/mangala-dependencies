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

package org.spongycastle.math.ec.custom.sec//package org.spongycastle.math.ec.custom.sec
//
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.add
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.negate
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.square
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.squareN
//import org.spongycastle.math.ec.custom.sec.SecP160R1Field.subtract
//import org.spongycastle.math.raw.Mod
//import org.spongycastle.math.raw.Nat160
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecP160R1FieldElement : ECFieldElement {
//    @JvmField
//    var x: IntArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP160R1FieldElement" }
//        this.x = fromBigInteger(x)
//    }
//
//    constructor() {
//        x = Nat160.create()
//    }
//
//    constructor(x: IntArray) {
//        this.x = x
//    }
//
//    override fun isZero(): Boolean {
//        return Nat160.isZero(x)
//    }
//
//    override fun isOne(): Boolean {
//        return Nat160.isOne(x)
//    }
//
//    override fun testBitZero(): Boolean {
//        return Nat160.getBit(x, 0) == 1
//    }
//
//    override fun toBigInteger(): BigInteger {
//        return Nat160.toBigInteger(x)
//    }
//
//    override fun getFieldName(): String {
//        return "SecP160R1Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return Q.bitLength()
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat160.create()
//        add(x, (b as SecP160R1FieldElement).x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat160.create()
//        addOne(x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        val z = Nat160.create()
//        subtract(x, (b as SecP160R1FieldElement).x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat160.create()
//        multiply(x, (b as SecP160R1FieldElement).x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
////        return multiply(b.invert());
//        val z = Nat160.create()
//        Mod.invert(SecP160R1Field.P, (b as SecP160R1FieldElement).x, z)
//        multiply(z, x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun negate(): ECFieldElement {
//        val z = Nat160.create()
//        negate(x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat160.create()
//        square(x, z)
//        return SecP160R1FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
////        return new SecP160R1FieldElement(toBigInteger().modInverse(Q));
//        val z = Nat160.create()
//        Mod.invert(SecP160R1Field.P, x, z)
//        return SecP160R1FieldElement(z)
//    }
//    // D.1.4 91
//    /**
//     * return a sqrt root - the routine verifies that the calculation returns the right value - if
//     * none exists it returns null.
//     */
//    override fun sqrt(): ECFieldElement? {
//        /*
//         * Raise this element to the exponent 2^158 - 2^29
//         *
//         * Breaking up the exponent's binary representation into "repunits", we get:
//         *     { 129 1s } { 29 0s }
//         *
//         * Therefore we need an addition chain containing 129 (the length of the repunit) We use:
//         *     1, 2, 4, 8, 16, 32, 64, 128, [129]
//         */
//        val x1 = x
//        if (Nat160.isZero(x1) || Nat160.isOne(x1)) {
//            return this
//        }
//        val x2 = Nat160.create()
//        square(x1, x2)
//        multiply(x2, x1, x2)
//        val x4 = Nat160.create()
//        squareN(x2, 2, x4)
//        multiply(x4, x2, x4)
//        squareN(x4, 4, x2)
//        multiply(x2, x4, x2)
//        squareN(x2, 8, x4)
//        multiply(x4, x2, x4)
//        squareN(x4, 16, x2)
//        multiply(x2, x4, x2)
//        squareN(x2, 32, x4)
//        multiply(x4, x2, x4)
//        squareN(x4, 64, x2)
//        multiply(x2, x4, x2)
//        square(x2, x4)
//        multiply(x4, x1, x4)
//        squareN(x4, 29, x4)
//        square(x4, x2)
//        return if (Nat160.eq(x1, x2)) SecP160R1FieldElement(x4) else null
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecP160R1FieldElement) {
//            return false
//        }
//        return Nat160.eq(x, other.x)
//    }
//
//    override fun hashCode(): Int {
//        return Q.hashCode() xor Arrays.hashCode(x, 0, 5)
//    }
//
//    companion object {
//        val Q = SecP160R1Curve.q
//    }
//}
