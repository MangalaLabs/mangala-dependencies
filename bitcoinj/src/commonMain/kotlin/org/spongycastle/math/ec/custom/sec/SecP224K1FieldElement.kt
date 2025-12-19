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
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.add
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.negate
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.square
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.squareN
//import org.spongycastle.math.ec.custom.sec.SecP224K1Field.subtract
//import org.spongycastle.math.raw.Mod
//import org.spongycastle.math.raw.Nat224
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecP224K1FieldElement : ECFieldElement {
//    @JvmField
//    var x: IntArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP224K1FieldElement" }
//        this.x = fromBigInteger(x)
//    }
//
//    constructor() {
//        x = Nat224.create()
//    }
//
//    constructor(x: IntArray) {
//        this.x = x
//    }
//
//    override fun isZero(): Boolean {
//        return Nat224.isZero(x)
//    }
//
//    override fun isOne(): Boolean {
//        return Nat224.isOne(x)
//    }
//
//    override fun testBitZero(): Boolean {
//        return Nat224.getBit(x, 0) == 1
//    }
//
//    override fun toBigInteger(): BigInteger {
//        return Nat224.toBigInteger(x)
//    }
//
//    override fun getFieldName(): String {
//        return "SecP224K1Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return Q.bitLength()
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat224.create()
//        add(x, (b as SecP224K1FieldElement).x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat224.create()
//        addOne(x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        val z = Nat224.create()
//        subtract(x, (b as SecP224K1FieldElement).x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat224.create()
//        multiply(x, (b as SecP224K1FieldElement).x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
////        return multiply(b.invert());
//        val z = Nat224.create()
//        Mod.invert(SecP224K1Field.P, (b as SecP224K1FieldElement).x, z)
//        multiply(z, x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun negate(): ECFieldElement {
//        val z = Nat224.create()
//        negate(x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat224.create()
//        square(x, z)
//        return SecP224K1FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
////        return new SecP224K1FieldElement(toBigInteger().modInverse(Q));
//        val z = Nat224.create()
//        Mod.invert(SecP224K1Field.P, x, z)
//        return SecP224K1FieldElement(z)
//    }
//    // D.1.4 91
//    /**
//     * return a sqrt root - the routine verifies that the calculation returns the right value - if
//     * none exists it returns null.
//     */
//    override fun sqrt(): ECFieldElement? {
//        /*
//         * Q == 8m + 5, so we use Pocklington's method for this case.
//         *
//         * First, raise this element to the exponent 2^221 - 2^29 - 2^9 - 2^8 - 2^6 - 2^4 - 2^1 (i.e. m + 1)
//         *
//         * Breaking up the exponent's binary representation into "repunits", we get:
//         * { 191 1s } { 1 0s } { 19 1s } { 2 0s } { 1 1s } { 1 0s} { 1 1s } { 1 0s} { 3 1s } { 1 0s}
//         *
//         * Therefore we need an addition chain containing 1, 3, 19, 191 (the lengths of the repunits)
//         * We use: [1], 2, [3], 4, 8, 11, [19], 23, 42, 84, 107, [191]
//         */
//        val x1 = x
//        if (Nat224.isZero(x1) || Nat224.isOne(x1)) {
//            return this
//        }
//        val x2 = Nat224.create()
//        square(x1, x2)
//        multiply(x2, x1, x2)
//        square(x2, x2)
//        multiply(x2, x1, x2)
//        val x4 = Nat224.create()
//        square(x2, x4)
//        multiply(x4, x1, x4)
//        val x8 = Nat224.create()
//        squareN(x4, 4, x8)
//        multiply(x8, x4, x8)
//        val x11 = Nat224.create()
//        squareN(x8, 3, x11)
//        multiply(x11, x2, x11)
//        squareN(x11, 8, x11)
//        multiply(x11, x8, x11)
//        squareN(x11, 4, x8)
//        multiply(x8, x4, x8)
//        squareN(x8, 19, x4)
//        multiply(x4, x11, x4)
//        val x84 = Nat224.create()
//        squareN(x4, 42, x84)
//        multiply(x84, x4, x84)
//        squareN(x84, 23, x4)
//        multiply(x4, x8, x4)
//        squareN(x4, 84, x8)
//        multiply(x8, x84, x8)
//        squareN(x8, 20, x8)
//        multiply(x8, x11, x8)
//        squareN(x8, 3, x8)
//        multiply(x8, x1, x8)
//        squareN(x8, 2, x8)
//        multiply(x8, x1, x8)
//        squareN(x8, 4, x8)
//        multiply(x8, x2, x8)
//        square(x8, x8)
//        square(x8, x84)
//        if (Nat224.eq(x1, x84)) {
//            return SecP224K1FieldElement(x8)
//        }
//
//        /*
//         * If the first guess is incorrect, we multiply by a precomputed power of 2 to get the second guess,
//         * which is ((4x)^(m + 1))/2 mod Q
//         */multiply(x8, PRECOMP_POW2, x8)
//        square(x8, x84)
//        return if (Nat224.eq(x1, x84)) {
//            SecP224K1FieldElement(x8)
//        } else null
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecP224K1FieldElement) {
//            return false
//        }
//        return Nat224.eq(x, other.x)
//    }
//
//    override fun hashCode(): Int {
//        return Q.hashCode() xor Arrays.hashCode(x, 0, 7)
//    }
//
//    companion object {
//        val Q = SecP224K1Curve.q
//
//        // Calculated as ECConstants.TWO.modPow(Q.shiftRight(2), Q)
//        private val PRECOMP_POW2 = intArrayOf(
//            0x33bfd202, -0x23052ecd, 0x2287624a, -0x3c7ee458,
//            -0x57aaa704, 0x1eaef5d7, -0x7120eab4
//        )
//    }
//}
