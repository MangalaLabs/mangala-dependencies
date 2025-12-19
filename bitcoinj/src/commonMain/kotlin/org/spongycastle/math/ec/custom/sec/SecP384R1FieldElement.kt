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
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.add
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.negate
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.square
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.squareN
//import org.spongycastle.math.ec.custom.sec.SecP384R1Field.subtract
//import org.spongycastle.math.raw.Mod
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecP384R1FieldElement : ECFieldElement {
//    @JvmField
//    var x: IntArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP384R1FieldElement" }
//        this.x = fromBigInteger(x)
//    }
//
//    constructor() {
//        x = Nat.create(12)
//    }
//
//    constructor(x: IntArray) {
//        this.x = x
//    }
//
//    override fun isZero(): Boolean {
//        return Nat.isZero(12, x)
//    }
//
//    override fun isOne(): Boolean {
//        return Nat.isOne(12, x)
//    }
//
//    override fun testBitZero(): Boolean {
//        return Nat.getBit(x, 0) == 1
//    }
//
//    override fun toBigInteger(): BigInteger {
//        return Nat.toBigInteger(12, x)
//    }
//
//    override fun getFieldName(): String {
//        return "SecP384R1Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return Q.bitLength()
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat.create(12)
//        add(x, (b as SecP384R1FieldElement).x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat.create(12)
//        addOne(x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        val z = Nat.create(12)
//        subtract(x, (b as SecP384R1FieldElement).x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat.create(12)
//        multiply(x, (b as SecP384R1FieldElement).x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
////        return multiply(b.invert());
//        val z = Nat.create(12)
//        Mod.invert(SecP384R1Field.P, (b as SecP384R1FieldElement).x, z)
//        multiply(z, x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun negate(): ECFieldElement {
//        val z = Nat.create(12)
//        negate(x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat.create(12)
//        square(x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
////        return new SecP384R1FieldElement(toBigInteger().modInverse(Q));
//        val z = Nat.create(12)
//        Mod.invert(SecP384R1Field.P, x, z)
//        return SecP384R1FieldElement(z)
//    }
//
//    /**
//     * return a sqrt root - the routine verifies that the calculation returns the right value - if
//     * none exists it returns null.
//     */
//    override fun sqrt(): ECFieldElement? {
//        // Raise this element to the exponent 2^382 - 2^126 - 2^94 + 2^30
//        val x1 = x
//        if (Nat.isZero(12, x1) || Nat.isOne(12, x1)) {
//            return this
//        }
//        val t1 = Nat.create(12)
//        val t2 = Nat.create(12)
//        val t3 = Nat.create(12)
//        val t4 = Nat.create(12)
//        square(x1, t1)
//        multiply(t1, x1, t1)
//        squareN(t1, 2, t2)
//        multiply(t2, t1, t2)
//        square(t2, t2)
//        multiply(t2, x1, t2)
//        squareN(t2, 5, t3)
//        multiply(t3, t2, t3)
//        squareN(t3, 5, t4)
//        multiply(t4, t2, t4)
//        squareN(t4, 15, t2)
//        multiply(t2, t4, t2)
//        squareN(t2, 2, t3)
//        multiply(t1, t3, t1)
//        squareN(t3, 28, t3)
//        multiply(t2, t3, t2)
//        squareN(t2, 60, t3)
//        multiply(t3, t2, t3)
//        squareN(t3, 120, t2)
//        multiply(t2, t3, t2)
//        squareN(t2, 15, t2)
//        multiply(t2, t4, t2)
//        squareN(t2, 33, t2)
//        multiply(t2, t1, t2)
//        squareN(t2, 64, t2)
//        multiply(t2, x1, t2)
//        squareN(t2, 30, t1)
//        square(t1, t2)
//        return if (Nat.eq(12, x1, t2)) SecP384R1FieldElement(t1) else null
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecP384R1FieldElement) {
//            return false
//        }
//        return Nat.eq(12, x, other.x)
//    }
//
//    override fun hashCode(): Int {
//        return Q.hashCode() xor Arrays.hashCode(x, 0, 12)
//    }
//
//    companion object {
//        val Q = SecP384R1Curve.q
//    }
//}
