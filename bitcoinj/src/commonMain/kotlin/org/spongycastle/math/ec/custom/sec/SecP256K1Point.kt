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

package org.spongycastle.math.ec.custom.sec

import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.math.ec.PointAbstractFp
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.multiply
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.multiplyAddToExt
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.negate
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.reduce
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.reduce32
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.square
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.subtract
import org.spongycastle.math.ec.custom.sec.SecP256K1Field.twice
import org.spongycastle.math.raw.Nat
import org.spongycastle.math.raw.Nat256

class SecP256K1Point : PointAbstractFp {
    /**
     * Create a point which encodes with point compression.
     *
     * @param curve
     * the curve to use
     * @param x
     * affine x co-ordinate
     * @param y
     * affine y co-ordinate
     *
     */
    @Deprecated("Use ECCurve.createPoint to construct points")
    constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : this(curve, x, y, false)

    /**
     * Create a point that encodes with or without point compresion.
     *
     * @param curve
     * the curve to use
     * @param x
     * affine x co-ordinate
     * @param y
     * affine y co-ordinate
     * @param withCompression
     * if true encode with point compression
     *
     */
    @Deprecated(
        """per-point compression property will be removed, refer
                  {@link #getEncoded(boolean)}"""
    )
    constructor(
        curve: ECCurve?,
        x: ECFieldElement?,
        y: ECFieldElement?,
        withCompression: Boolean
    ) : super(curve, x, y) {
        require(x == null == (y == null)) { "Exactly one of the field elements is null" }
        this.isCompressed = withCompression
    }

    internal constructor(
        curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?, zs: Array<ECFieldElement>,
        withCompression: Boolean
    ) : super(curve, x, y, zs) {
        this.isCompressed = withCompression
    }

    override fun detach(): ECPoint {
        return SecP256K1Point(null, affineXCoord, affineYCoord)
    }

    // B.3 pg 62
    override fun add(b: ECPoint?): ECPoint? {
        if (this.isInfinity) {
            return b
        }
        if (b?.isInfinity == true) {
            return this
        }
        if (this === b) {
            return twice()
        }
        val curve = getCurve()
        val X1 = rawXCoord as SecP256K1FieldElement
        val Y1 = rawYCoord as SecP256K1FieldElement
        val X2 = b?.xCoord as SecP256K1FieldElement
        val Y2 = b.yCoord as SecP256K1FieldElement
        val Z1 = rawZCoords[0] as SecP256K1FieldElement
        val Z2 = b.getZCoord(0) as SecP256K1FieldElement
        val c: Int
        val tt1 = Nat256.createExt()
        val t2 = Nat256.create()
        val t3 = Nat256.create()
        val t4 = Nat256.create()
        val Z1IsOne = Z1.isOne()
        val U2: IntArray
        val S2: IntArray
        if (Z1IsOne) {
            U2 = X2.x
            S2 = Y2.x
        } else {
            S2 = t3
            square(Z1.x, S2)
            U2 = t2
            multiply(S2, X2.x, U2)
            multiply(S2, Z1.x, S2)
            multiply(S2, Y2.x, S2)
        }
        val Z2IsOne = Z2.isOne()
        val U1: IntArray
        val S1: IntArray
        if (Z2IsOne) {
            U1 = X1.x
            S1 = Y1.x
        } else {
            S1 = t4
            square(Z2.x, S1)
            U1 = tt1
            multiply(S1, X1.x, U1)
            multiply(S1, Z2.x, S1)
            multiply(S1, Y1.x, S1)
        }
        val H = Nat256.create()
        subtract(U1, U2, H)
        subtract(S1, S2, t2)

        // Check if b == this or b == -this
        if (Nat256.isZero(H)) {
            return if (Nat256.isZero(t2)) {
                // this == b, i.e. this must be doubled
                this.twice()
            } else curve?.getNewInfinity()

            // this == -b, i.e. the result is the point at infinity
        }
        square(H, t3)
        val G = Nat256.create()
        multiply(t3, H, G)
        multiply(t3, U1, t3)
        negate(G, G)
        Nat256.mul(S1, G, tt1)
        c = Nat256.addBothTo(t3, t3, G)
        reduce32(c, G)
        val X3 = SecP256K1FieldElement(t4)
        square(t2, X3.x)
        subtract(X3.x, G, X3.x)
        val Y3 = SecP256K1FieldElement(G)
        subtract(t3, X3.x, Y3.x)
        multiplyAddToExt(Y3.x, t2, tt1)
        reduce(tt1, Y3.x)
        val Z3 = SecP256K1FieldElement(H)
        if (!Z1IsOne) {
            multiply(Z3.x, Z1.x, Z3.x)
        }
        if (!Z2IsOne) {
            multiply(Z3.x, Z2.x, Z3.x)
        }
        val zs = arrayOf<ECFieldElement>(Z3)
        return SecP256K1Point(curve, X3, Y3, zs, isCompressed)
    }

    // B.3 pg 62
    override fun twice(): ECPoint? {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val Y1 = rawYCoord as SecP256K1FieldElement
        if (Y1.isZero()) {
            return curve?.getNewInfinity()
        }
        val X1 = rawXCoord as SecP256K1FieldElement
        val Z1 = rawZCoords[0] as SecP256K1FieldElement
        var c: Int
        val Y1Squared = Nat256.create()
        square(Y1.x, Y1Squared)
        val T = Nat256.create()
        square(Y1Squared, T)
        val M = Nat256.create()
        square(X1.x, M)
        c = Nat256.addBothTo(M, M, M)
        reduce32(c, M)
        multiply(Y1Squared, X1.x, Y1Squared)
        c = Nat.shiftUpBits(8, Y1Squared, 2, 0)
        reduce32(c, Y1Squared)
        val t1 = Nat256.create()
        c = Nat.shiftUpBits(8, T, 3, 0, t1)
        reduce32(c, t1)
        val X3 = SecP256K1FieldElement(T)
        square(M, X3.x)
        subtract(X3.x, Y1Squared, X3.x)
        subtract(X3.x, Y1Squared, X3.x)
        val Y3 = SecP256K1FieldElement(Y1Squared)
        subtract(Y1Squared, X3.x, Y3.x)
        multiply(Y3.x, M, Y3.x)
        subtract(Y3.x, t1, Y3.x)
        val Z3 = SecP256K1FieldElement(M)
        twice(Y1.x, Z3.x)
        if (!Z1.isOne()) {
            multiply(Z3.x, Z1.x, Z3.x)
        }
        return SecP256K1Point(curve, X3, Y3, arrayOf(Z3), isCompressed)
    }

    override fun twicePlus(b: ECPoint?): ECPoint? {
        if (this === b) {
            return threeTimes()
        }
        if (this.isInfinity) {
            return b
        }
        if (b?.isInfinity == true) {
            return twice()
        }
        val Y1 = rawYCoord
        return if (Y1?.isZero() == true) {
            b
        } else twice()?.add(b)
    }

    override fun threeTimes(): ECPoint? {
        return if (this.isInfinity || rawYCoord?.isZero() == true) {
            this
        } else twice()?.add(this)

        // NOTE: Be careful about recursions between twicePlus and threeTimes
    }

    override fun negate(): ECPoint {
        return if (this.isInfinity) {
            this
        } else SecP256K1Point(mCurve, rawXCoord, rawYCoord?.negate(), rawZCoords, isCompressed)
    }
}
