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
//import org.spongycastle.math.ec.ECCurve
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.ECPoint
//import org.spongycastle.math.ec.PointAbstractFp
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.math.raw.Nat160
//
//class SecP160K1Point : PointAbstractFp {
//    /**
//     * Create a point which encodes with point compression.
//     *
//     * @param curve
//     * the curve to use
//     * @param x
//     * affine x co-ordinate
//     * @param y
//     * affine y co-ordinate
//     *
//     */
//    @Deprecated("Use ECCurve.createPoint to construct points")
//    constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : this(curve, x, y, false)
//
//    /**
//     * Create a point that encodes with or without point compresion.
//     *
//     * @param curve
//     * the curve to use
//     * @param x
//     * affine x co-ordinate
//     * @param y
//     * affine y co-ordinate
//     * @param withCompression
//     * if true encode with point compression
//     *
//     */
//    @Deprecated(
//        """per-point compression property will be removed, refer
//                  {@link #getEncoded(boolean)}"""
//    )
//    constructor(
//        curve: ECCurve?,
//        x: ECFieldElement?,
//        y: ECFieldElement?,
//        withCompression: Boolean
//    ) : super(curve, x, y) {
//        require(x == null == (y == null)) { "Exactly one of the field elements is null" }
//        this.withCompression = withCompression
//    }
//
//    internal constructor(
//        curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?, zs: Array<ECFieldElement>?,
//        withCompression: Boolean
//    ) : super(curve, x, y, zs) {
//        this.withCompression = withCompression
//    }
//
//    override fun detach(): ECPoint {
//        return SecP160K1Point(null, affineXCoord, affineYCoord)
//    }
//
//    // B.3 pg 62
//    override fun add(b: ECPoint): ECPoint {
//        if (this.isInfinity) {
//            return b
//        }
//        if (b.isInfinity) {
//            return this
//        }
//        if (this === b) {
//            return twice()
//        }
//        val curve = getCurve()
//        val X1 = x as SecP160R2FieldElement
//        val Y1 = y as SecP160R2FieldElement
//        val X2 = b.xCoord as SecP160R2FieldElement
//        val Y2 = b.yCoord as SecP160R2FieldElement
//        val Z1 = zs[0] as SecP160R2FieldElement
//        val Z2 = b.getZCoord(0) as SecP160R2FieldElement
//        val c: Int
//        val tt1 = Nat160.createExt()
//        val t2 = Nat160.create()
//        val t3 = Nat160.create()
//        val t4 = Nat160.create()
//        val Z1IsOne = Z1.isOne()
//        val U2: IntArray
//        val S2: IntArray
//        if (Z1IsOne) {
//            U2 = X2.x
//            S2 = Y2.x
//        } else {
//            S2 = t3
//            SecP160R2Field.square(Z1.x, S2)
//            U2 = t2
//            SecP160R2Field.multiply(S2, X2.x, U2)
//            SecP160R2Field.multiply(S2, Z1.x, S2)
//            SecP160R2Field.multiply(S2, Y2.x, S2)
//        }
//        val Z2IsOne = Z2.isOne()
//        val U1: IntArray
//        val S1: IntArray
//        if (Z2IsOne) {
//            U1 = X1.x
//            S1 = Y1.x
//        } else {
//            S1 = t4
//            SecP160R2Field.square(Z2.x, S1)
//            U1 = tt1
//            SecP160R2Field.multiply(S1, X1.x, U1)
//            SecP160R2Field.multiply(S1, Z2.x, S1)
//            SecP160R2Field.multiply(S1, Y1.x, S1)
//        }
//        val H = Nat160.create()
//        SecP160R2Field.subtract(U1, U2, H)
//        SecP160R2Field.subtract(S1, S2, t2)
//
//        // Check if b == this or b == -this
//        if (Nat160.isZero(H)) {
//            return if (Nat160.isZero(t2)) {
//                // this == b, i.e. this must be doubled
//                twice()
//            } else curve.getNewInfinity()
//
//            // this == -b, i.e. the result is the point at infinity
//        }
//        SecP160R2Field.square(H, t3)
//        val G = Nat160.create()
//        SecP160R2Field.multiply(t3, H, G)
//        SecP160R2Field.multiply(t3, U1, t3)
//        SecP160R2Field.negate(G, G)
//        Nat160.mul(S1, G, tt1)
//        c = Nat160.addBothTo(t3, t3, G)
//        SecP160R2Field.reduce32(c, G)
//        val X3 = SecP160R2FieldElement(t4)
//        SecP160R2Field.square(t2, X3.x)
//        SecP160R2Field.subtract(X3.x, G, X3.x)
//        val Y3 = SecP160R2FieldElement(G)
//        SecP160R2Field.subtract(t3, X3.x, Y3.x)
//        SecP160R2Field.multiplyAddToExt(Y3.x, t2, tt1)
//        SecP160R2Field.reduce(tt1, Y3.x)
//        val Z3 = SecP160R2FieldElement(H)
//        if (!Z1IsOne) {
//            SecP160R2Field.multiply(Z3.x, Z1.x, Z3.x)
//        }
//        if (!Z2IsOne) {
//            SecP160R2Field.multiply(Z3.x, Z2.x, Z3.x)
//        }
//        val zs = arrayOf<ECFieldElement>(Z3)
//        return SecP160K1Point(curve, X3, Y3, zs, withCompression)
//    }
//
//    // B.3 pg 62
//    override fun twice(): ECPoint {
//        if (this.isInfinity) {
//            return this
//        }
//        val curve = getCurve()
//        val Y1 = y as SecP160R2FieldElement
//        if (Y1.isZero()) {
//            return curve.getNewInfinity()
//        }
//        val X1 = x as SecP160R2FieldElement
//        val Z1 = zs[0] as SecP160R2FieldElement
//        var c: Int
//        val Y1Squared = Nat160.create()
//        SecP160R2Field.square(Y1.x, Y1Squared)
//        val T = Nat160.create()
//        SecP160R2Field.square(Y1Squared, T)
//        val M = Nat160.create()
//        SecP160R2Field.square(X1.x, M)
//        c = Nat160.addBothTo(M, M, M)
//        SecP160R2Field.reduce32(c, M)
//        SecP160R2Field.multiply(Y1Squared, X1.x, Y1Squared)
//        c = Nat.shiftUpBits(5, Y1Squared, 2, 0)
//        SecP160R2Field.reduce32(c, Y1Squared)
//        val t1 = Nat160.create()
//        c = Nat.shiftUpBits(5, T, 3, 0, t1)
//        SecP160R2Field.reduce32(c, t1)
//        val X3 = SecP160R2FieldElement(T)
//        SecP160R2Field.square(M, X3.x)
//        SecP160R2Field.subtract(X3.x, Y1Squared, X3.x)
//        SecP160R2Field.subtract(X3.x, Y1Squared, X3.x)
//        val Y3 = SecP160R2FieldElement(Y1Squared)
//        SecP160R2Field.subtract(Y1Squared, X3.x, Y3.x)
//        SecP160R2Field.multiply(Y3.x, M, Y3.x)
//        SecP160R2Field.subtract(Y3.x, t1, Y3.x)
//        val Z3 = SecP160R2FieldElement(M)
//        SecP160R2Field.twice(Y1.x, Z3.x)
//        if (!Z1.isOne()) {
//            SecP160R2Field.multiply(Z3.x, Z1.x, Z3.x)
//        }
//        return SecP160K1Point(curve, X3, Y3, arrayOf(Z3), withCompression)
//    }
//
//    override fun twicePlus(b: ECPoint): ECPoint {
//        if (this === b) {
//            return threeTimes()
//        }
//        if (this.isInfinity) {
//            return b
//        }
//        if (b.isInfinity) {
//            return twice()
//        }
//        val Y1 = y
//        return if (Y1.isZero()) {
//            b
//        } else twice().add(b)
//    }
//
//    override fun threeTimes(): ECPoint {
//        return if (this.isInfinity || y.isZero()) {
//            this
//        } else twice().add(this)
//
//        // NOTE: Be careful about recursions between twicePlus and threeTimes
//    }
//
//    override fun negate(): ECPoint {
//        return if (this.isInfinity) {
//            this
//        } else SecP160K1Point(
//            curve,
//            x,
//            y.negate(),
//            zs,
//            withCompression
//        )
//    }
//}
