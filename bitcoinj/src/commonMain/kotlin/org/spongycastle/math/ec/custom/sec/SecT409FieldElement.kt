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
//import org.spongycastle.math.ec.EF2m
//import org.spongycastle.math.ec.custom.sec.SecT409Field.add
//import org.spongycastle.math.ec.custom.sec.SecT409Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecT409Field.fromBigInteger
//import org.spongycastle.math.ec.custom.sec.SecT409Field.invert
//import org.spongycastle.math.ec.custom.sec.SecT409Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecT409Field.multiplyAddToExt
//import org.spongycastle.math.ec.custom.sec.SecT409Field.reduce
//import org.spongycastle.math.ec.custom.sec.SecT409Field.sqrt
//import org.spongycastle.math.ec.custom.sec.SecT409Field.square
//import org.spongycastle.math.ec.custom.sec.SecT409Field.squareAddToExt
//import org.spongycastle.math.ec.custom.sec.SecT409Field.squareN
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.math.raw.Nat448
//import org.spongycastle.util.Arrays
//import java.math.BigInteger
//
//class SecT409FieldElement : ECFieldElement {
//    protected var x: LongArray
//
//    constructor(x: BigInteger?) {
//        require(!(x == null || x.signum() < 0 || x.bitLength() > 409)) { "x value invalid for SecT409FieldElement" }
//        this.x = fromBigInteger(x)
//    }
//
//    constructor() {
//        x = Nat448.create64()
//    }
//
//    protected constructor(x: LongArray) {
//        this.x = x
//    }
//
//    //    public int bitLength()
//    //    {
//    //        return x.degree();
//    //    }
//    override fun isOne(): Boolean {
//        return Nat448.isOne64(x)
//    }
//
//    override fun isZero(): Boolean {
//        return Nat448.isZero64(x)
//    }
//
//    override fun testBitZero(): Boolean {
//        return x[0] and 1L != 0L
//    }
//
//    override fun toBigInteger(): BigInteger {
//        return Nat448.toBigInteger64(x)
//    }
//
//    override fun getFieldName(): String {
//        return "SecT409Field"
//    }
//
//    override fun getFieldSize(): Int {
//        return 409
//    }
//
//    override fun add(b: ECFieldElement): ECFieldElement {
//        val z = Nat448.create64()
//        add(x, (b as SecT409FieldElement).x, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun addOne(): ECFieldElement {
//        val z = Nat448.create64()
//        addOne(x, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun subtract(b: ECFieldElement): ECFieldElement {
//        // Addition and subtraction are the same in F2m
//        return add(b)
//    }
//
//    override fun multiply(b: ECFieldElement): ECFieldElement {
//        val z = Nat448.create64()
//        multiply(x, (b as SecT409FieldElement).x, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun multiplyMinusProduct(
//        b: ECFieldElement,
//        x: ECFieldElement,
//        y: ECFieldElement
//    ): ECFieldElement {
//        return multiplyPlusProduct(b, x, y)
//    }
//
//    override fun multiplyPlusProduct(
//        b: ECFieldElement,
//        x: ECFieldElement,
//        y: ECFieldElement
//    ): ECFieldElement {
//        val ax = this.x
//        val bx = (b as SecT409FieldElement).x
//        val xx = (x as SecT409FieldElement).x
//        val yx = (y as SecT409FieldElement).x
//        val tt = Nat.create64(13)
//        multiplyAddToExt(ax, bx, tt)
//        multiplyAddToExt(xx, yx, tt)
//        val z = Nat448.create64()
//        reduce(tt, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun divide(b: ECFieldElement): ECFieldElement {
//        return multiply(b.invert())
//    }
//
//    override fun negate(): ECFieldElement {
//        return this
//    }
//
//    override fun square(): ECFieldElement {
//        val z = Nat448.create64()
//        square(x, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
//        return squarePlusProduct(x, y)
//    }
//
//    override fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
//        val ax = this.x
//        val xx = (x as SecT409FieldElement).x
//        val yx = (y as SecT409FieldElement).x
//        val tt = Nat.create64(13)
//        squareAddToExt(ax, tt)
//        multiplyAddToExt(xx, yx, tt)
//        val z = Nat448.create64()
//        reduce(tt, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun squarePow(pow: Int): ECFieldElement {
//        if (pow < 1) {
//            return this
//        }
//        val z = Nat448.create64()
//        squareN(x, pow, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun invert(): ECFieldElement {
//        val z = Nat448.create64()
//        invert(x, z)
//        return SecT409FieldElement(z)
//    }
//
//    override fun sqrt(): ECFieldElement {
//        val z = Nat448.create64()
//        sqrt(x, z)
//        return SecT409FieldElement(z)
//    }
//
//    val representation: Int
//        get() = EF2m.TPB
//    val m: Int
//        get() = 409
//    val k1: Int
//        get() = 87
//    val k2: Int
//        get() = 0
//    val k3: Int
//        get() = 0
//
//    override fun equals(other: Any?): Boolean {
//        if (other === this) {
//            return true
//        }
//        if (other !is SecT409FieldElement) {
//            return false
//        }
//        return Nat448.eq64(x, other.x)
//    }
//
//    override fun hashCode(): Int {
//        return 4090087 xor Arrays.hashCode(x, 0, 7)
//    }
//}
