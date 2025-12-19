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
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.add
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.negate
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.square
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.squareN
//import org.spongycastle.math.ec.custom.sec.SecP192K1Field.subtract
//import org.spongycastle.math.raw.Mod
//import org.spongycastle.math.raw.Nat192
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecP192K1FieldElement : ECFieldElement {
//    @JvmField
//    var x: IntArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP192K1FieldElement" }
//        this.x = fromBigInteger(x)
//    }
//
//    constructor() {
//        x = Nat192.create()
//    }
//
//    constructor(x: IntArray) {
//        this.x = x
//    }
//
//    override fun isZero(): Boolean {
//        return Nat192.isZero(x)
//    }
//
//    override fun isOne(): Boolean {
//        return Nat192.isOne(x)
//    }
//
//    override fun testBitZero(): Boolean {
//        return Nat192.getBit(x, 0) == 1
//    }
//
//    override fun toBigInteger(): BigInteger {
//        return Nat192.toBigInteger(x)
//    }
//
//    override fun getFieldName(): String {
//        return "SecP192K1Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return Q.bitLength()
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat192.create()
//        add(x, (b as SecP192K1FieldElement).x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat192.create()
//        addOne(x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        val z = Nat192.create()
//        subtract(x, (b as SecP192K1FieldElement).x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat192.create()
//        multiply(x, (b as SecP192K1FieldElement).x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
////        return multiply(b.invert());
//        val z = Nat192.create()
//        Mod.invert(SecP192K1Field.P, (b as SecP192K1FieldElement).x, z)
//        multiply(z, x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun negate(): ECFieldElement {
//        val z = Nat192.create()
//        negate(x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat192.create()
//        square(x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
////        return new SecP192K1FieldElement(toBigInteger().modInverse(Q));
//        val z = Nat192.create()
//        Mod.invert(SecP192K1Field.P, x, z)
//        return SecP192K1FieldElement(z)
//    }
//
//    /**
//     * return a sqrt root - the routine verifies that the calculation returns the right value - if
//     * none exists it returns null.
//     */
//    override fun sqrt(): ECFieldElement? {
//        /*
//         * Raise this element to the exponent 2^190 - 2^30 - 2^10 - 2^6 - 2^5 - 2^4 - 2^1
//         *
//         * Breaking up the exponent's binary representation into "repunits", we get:
//         * { 159 1s } { 1 0s } { 19 1s } { 1 0s } { 3 1s } { 3 0s} { 3 1s } { 1 0s }
//         *
//         * Therefore we need an addition chain containing 3, 19, 159 (the lengths of the repunits)
//         * We use: 1, 2, [3], 6, 8, 16, [19], 35, 70, 140, [159]
//         */
//        val x1 = x
//        if (Nat192.isZero(x1) || Nat192.isOne(x1)) {
//            return this
//        }
//        val x2 = Nat192.create()
//        square(x1, x2)
//        multiply(x2, x1, x2)
//        val x3 = Nat192.create()
//        square(x2, x3)
//        multiply(x3, x1, x3)
//        val x6 = Nat192.create()
//        squareN(x3, 3, x6)
//        multiply(x6, x3, x6)
//        squareN(x6, 2, x6)
//        multiply(x6, x2, x6)
//        squareN(x6, 8, x2)
//        multiply(x2, x6, x2)
//        squareN(x2, 3, x6)
//        multiply(x6, x3, x6)
//        val x35 = Nat192.create()
//        squareN(x6, 16, x35)
//        multiply(x35, x2, x35)
//        squareN(x35, 35, x2)
//        multiply(x2, x35, x2)
//        squareN(x2, 70, x35)
//        multiply(x35, x2, x35)
//        squareN(x35, 19, x2)
//        multiply(x2, x6, x2)
//        squareN(x2, 20, x2)
//        multiply(x2, x6, x2)
//        squareN(x2, 4, x2)
//        multiply(x2, x3, x2)
//        squareN(x2, 6, x2)
//        multiply(x2, x3, x2)
//        square(x2, x2)
//        square(x2, x3)
//        return if (Nat192.eq(x1, x3)) SecP192K1FieldElement(x2) else null
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecP192K1FieldElement) {
//            return false
//        }
//        return Nat192.eq(x, other.x)
//    }
//
//    override fun hashCode(): Int {
//        return Q.hashCode() xor Arrays.hashCode(x, 0, 6)
//    }
//
//    companion object {
//        val Q = SecP192K1Curve.q
//    }
//}
