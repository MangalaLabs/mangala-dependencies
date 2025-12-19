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
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.add
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.negate
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.reduce32
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.square
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.squareN
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.subtract
//import org.spongycastle.math.ec.custom.sec.SecP224R1Field.twice
//import org.spongycastle.math.raw.Mod
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.math.raw.Nat224
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecP224R1FieldElement : ECFieldElement {
//    @JvmField
//    var x: IntArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.compareTo(Q) >= 0)) { "x value invalid for SecP224R1FieldElement" }
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
//        return "SecP224R1Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return Q.bitLength()
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat224.create()
//        add(x, (b as SecP224R1FieldElement).x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat224.create()
//        addOne(x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        val z = Nat224.create()
//        subtract(x, (b as SecP224R1FieldElement).x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat224.create()
//        multiply(x, (b as SecP224R1FieldElement).x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
////        return multiply(b.invert());
//        val z = Nat224.create()
//        Mod.invert(SecP224R1Field.P, (b as SecP224R1FieldElement).x, z)
//        multiply(z, x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun negate(): ECFieldElement {
//        val z = Nat224.create()
//        negate(x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat224.create()
//        square(x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
////        return new SecP224R1FieldElement(toBigInteger().modInverse(Q));
//        val z = Nat224.create()
//        Mod.invert(SecP224R1Field.P, x, z)
//        return SecP224R1FieldElement(z)
//    }
//
//    /**
//     * return a sqrt root - the routine verifies that the calculation returns the right value - if
//     * none exists it returns null.
//     */
//    override fun sqrt(): ECFieldElement? {
//        val c = x
//        if (Nat224.isZero(c) || Nat224.isOne(c)) {
//            return this
//        }
//        val nc = Nat224.create()
//        negate(c, nc)
//        val r = Mod.random(SecP224R1Field.P)
//        val t = Nat224.create()
//        if (!isSquare(c)) {
//            return null
//        }
//        while (!trySqrt(nc, r, t)) {
//            addOne(r, r)
//        }
//        square(t, r)
//        return if (Nat224.eq(c, r)) SecP224R1FieldElement(t) else null
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecP224R1FieldElement) {
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
//        val Q = SecP224R1Curve.q
//        private fun isSquare(x: IntArray): Boolean {
//            val t1 = Nat224.create()
//            val t2 = Nat224.create()
//            Nat224.copy(x, t1)
//            for (i in 0..6) {
//                Nat224.copy(t1, t2)
//                squareN(t1, 1 shl i, t1)
//                multiply(t1, t2, t1)
//            }
//            squareN(t1, 95, t1)
//            return Nat224.isOne(t1)
//        }
//
//        private fun RM(
//            nc: IntArray,
//            d0: IntArray,
//            e0: IntArray,
//            d1: IntArray,
//            e1: IntArray,
//            f1: IntArray,
//            t: IntArray
//        ) {
//            multiply(e1, e0, t)
//            multiply(t, nc, t)
//            multiply(d1, d0, f1)
//            add(f1, t, f1)
//            multiply(d1, e0, t)
//            Nat224.copy(f1, d1)
//            multiply(e1, d0, e1)
//            add(e1, t, e1)
//            square(e1, f1)
//            multiply(f1, nc, f1)
//        }
//
//        private fun RP(nc: IntArray, d1: IntArray, e1: IntArray, f1: IntArray, t: IntArray) {
//            Nat224.copy(nc, f1)
//            val d0 = Nat224.create()
//            val e0 = Nat224.create()
//            for (i in 0..6) {
//                Nat224.copy(d1, d0)
//                Nat224.copy(e1, e0)
//                var j = 1 shl i
//                while (--j >= 0) {
//                    RS(d1, e1, f1, t)
//                }
//                RM(nc, d0, e0, d1, e1, f1, t)
//            }
//        }
//
//        private fun RS(d: IntArray, e: IntArray, f: IntArray, t: IntArray) {
//            multiply(e, d, e)
//            twice(e, e)
//            square(d, t)
//            add(f, t, d)
//            multiply(f, t, f)
//            val c = Nat.shiftUpBits(7, f, 2, 0)
//            reduce32(c, f)
//        }
//
//        private fun trySqrt(nc: IntArray, r: IntArray, t: IntArray): Boolean {
//            val d1 = Nat224.create()
//            Nat224.copy(r, d1)
//            val e1 = Nat224.create()
//            e1[0] = 1
//            val f1 = Nat224.create()
//            RP(nc, d1, e1, f1, t)
//            val d0 = Nat224.create()
//            val e0 = Nat224.create()
//            for (k in 1..95) {
//                Nat224.copy(d1, d0)
//                Nat224.copy(e1, e0)
//                RS(d1, e1, f1, t)
//                if (Nat224.isZero(d1)) {
//                    Mod.invert(SecP224R1Field.P, e0, t)
//                    multiply(t, d0, t)
//                    return true
//                }
//            }
//            return false
//        }
//    }
//}
