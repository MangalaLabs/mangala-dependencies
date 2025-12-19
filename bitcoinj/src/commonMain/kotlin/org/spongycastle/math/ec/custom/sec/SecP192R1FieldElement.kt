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
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.add
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.negate
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.square
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.squareN
//import org.spongycastle.math.ec.custom.sec.SecP192R1Field.subtract
//import org.spongycastle.math.raw.Mod
//import org.spongycastle.math.raw.Nat192
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecP192R1FieldElement : ECFieldElement {
//    @JvmField
//    var x: IntArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP192R1FieldElement" }
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
//        return "SecP192R1Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return Q.bitLength()
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat192.create()
//        add(x, (b as SecP192R1FieldElement).x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat192.create()
//        addOne(x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        val z = Nat192.create()
//        subtract(x, (b as SecP192R1FieldElement).x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat192.create()
//        multiply(x, (b as SecP192R1FieldElement).x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
////        return multiply(b.invert());
//        val z = Nat192.create()
//        Mod.invert(SecP192R1Field.P, (b as SecP192R1FieldElement).x, z)
//        multiply(z, x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun negate(): ECFieldElement {
//        val z = Nat192.create()
//        negate(x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat192.create()
//        square(x, z)
//        return SecP192R1FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
////        return new SecP192R1FieldElement(toBigInteger().modInverse(Q));
//        val z = Nat192.create()
//        Mod.invert(SecP192R1Field.P, x, z)
//        return SecP192R1FieldElement(z)
//    }
//    // D.1.4 91
//    /**
//     * return a sqrt root - the routine verifies that the calculation returns the right value - if
//     * none exists it returns null.
//     */
//    override fun sqrt(): ECFieldElement? {
//        // Raise this element to the exponent 2^190 - 2^62
//        val x1 = x
//        if (Nat192.isZero(x1) || Nat192.isOne(x1)) {
//            return this
//        }
//        val t1 = Nat192.create()
//        val t2 = Nat192.create()
//        square(x1, t1)
//        multiply(t1, x1, t1)
//        squareN(t1, 2, t2)
//        multiply(t2, t1, t2)
//        squareN(t2, 4, t1)
//        multiply(t1, t2, t1)
//        squareN(t1, 8, t2)
//        multiply(t2, t1, t2)
//        squareN(t2, 16, t1)
//        multiply(t1, t2, t1)
//        squareN(t1, 32, t2)
//        multiply(t2, t1, t2)
//        squareN(t2, 64, t1)
//        multiply(t1, t2, t1)
//        squareN(t1, 62, t1)
//        square(t1, t2)
//        return if (Nat192.eq(x1, t2)) SecP192R1FieldElement(t1) else null
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecP192R1FieldElement) {
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
//        val Q = SecP192R1Curve.q
//    }
//}
