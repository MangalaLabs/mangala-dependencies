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

import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.math.ec.PointAbstractFp
import org.spongycastle.math.ec.custom.djb.Curve25519Field.multiply
import org.spongycastle.math.ec.custom.djb.Curve25519Field.multiplyAddToExt
import org.spongycastle.math.ec.custom.djb.Curve25519Field.negate
import org.spongycastle.math.ec.custom.djb.Curve25519Field.reduce
import org.spongycastle.math.ec.custom.djb.Curve25519Field.reduce27
import org.spongycastle.math.ec.custom.djb.Curve25519Field.square
import org.spongycastle.math.ec.custom.djb.Curve25519Field.subtract
import org.spongycastle.math.ec.custom.djb.Curve25519Field.twice
import org.spongycastle.math.raw.Nat256

class Curve25519Point : PointAbstractFp {
    /**
     * Create a point which encodes with point compression.
     *
     * @param curve the curve to use
     * @param x affine x co-ordinate
     * @param y affine y co-ordinate
     *
     */
    @Deprecated("Use ECCurve.createPoint to construct points")
    constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : this(curve, x, y, false)

    /**
     * Create a point that encodes with or without point compresion.
     *
     * @param curve the curve to use
     * @param x affine x co-ordinate
     * @param y affine y co-ordinate
     * @param withCompression if true encode with point compression
     *
     */
    @Deprecated("per-point compression property will be removed, refer {@link #getEncoded(boolean)}")
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
        curve: ECCurve?,
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ) : super(curve, x, y, zs) {
        this.isCompressed = withCompression
    }

    override fun detach(): ECPoint {
        return Curve25519Point(null, affineXCoord, affineYCoord)
    }

    override fun getZCoord(index: Int): ECFieldElement {
        return if (index == 1) {
            jacobianModifiedW
        } else super.getZCoord(index)!!
    }

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
        val X1 = rawXCoord as Curve25519FieldElement
        val Y1 = rawYCoord as Curve25519FieldElement
        val Z1 = rawZCoords[0] as Curve25519FieldElement
        val X2 = b?.xCoord as Curve25519FieldElement
        val Y2 = b.yCoord as Curve25519FieldElement
        val Z2 = b.getZCoord(0) as Curve25519FieldElement
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
        val HSquared = Nat256.create()
        square(H, HSquared)
        val G = Nat256.create()
        multiply(HSquared, H, G)
        multiply(HSquared, U1, t3)
        negate(G, G)
        Nat256.mul(S1, G, tt1)
        c = Nat256.addBothTo(t3, t3, G)
        reduce27(c, G)
        val X3 = Curve25519FieldElement(t4)
        square(t2, X3.x)
        subtract(X3.x, G, X3.x)
        val Y3 = Curve25519FieldElement(G)
        subtract(t3, X3.x, Y3.x)
        multiplyAddToExt(Y3.x, t2, tt1)
        reduce(tt1, Y3.x)
        val Z3 = Curve25519FieldElement(H)
        if (!Z1IsOne) {
            multiply(Z3.x, Z1.x, Z3.x)
        }
        if (!Z2IsOne) {
            multiply(Z3.x, Z2.x, Z3.x)
        }
        val Z3Squared = if (Z1IsOne && Z2IsOne) HSquared else null

        // TODO If the result will only be used in a subsequent addition, we don't need W3
        val W3 = calculateJacobianModifiedW(Z3, Z3Squared)
        val zs = arrayOf<ECFieldElement>(Z3, W3)
        return Curve25519Point(curve, X3, Y3, zs, isCompressed)
    }

    override fun twice(): ECPoint? {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val Y1 = rawYCoord
        return if (Y1?.isZero() == true) {
            curve?.getNewInfinity()
        } else twiceJacobianModified(true)
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
        } else twiceJacobianModified(false).add(b)
    }

    override fun threeTimes(): ECPoint? {
        if (this.isInfinity) {
            return this
        }
        val Y1 = rawYCoord
        return if (Y1?.isZero() == true) {
            this
        } else twiceJacobianModified(false).add(this)
    }

    override fun negate(): ECPoint {
        return if (this.isInfinity) {
            this
        } else Curve25519Point(
            getCurve(),
            rawXCoord,
            rawYCoord?.negate(),
            rawZCoords,
            isCompressed
        )
    }

    protected fun calculateJacobianModifiedW(
        Z: Curve25519FieldElement,
        ZSquared: IntArray?
    ): Curve25519FieldElement {
        var ZSquared = ZSquared
        val a4 = getCurve()?.getCA() as Curve25519FieldElement
        if (Z.isOne()) {
            return a4
        }
        val W = Curve25519FieldElement()
        if (ZSquared == null) {
            ZSquared = W.x
            square(Z.x, ZSquared)
        }
        square(ZSquared, W.x)
        multiply(W.x, a4.x, W.x)
        return W
    }

    protected val jacobianModifiedW: Curve25519FieldElement
        protected get() {
            var W = rawZCoords[1] as Curve25519FieldElement
            if (W == null) {
                // NOTE: Rarely, twicePlus will result in the need for a lazy W1 calculation here
                W = calculateJacobianModifiedW(rawZCoords[0] as Curve25519FieldElement, null)
                rawZCoords[1] = W
            }
            return W
        }

    protected fun twiceJacobianModified(calculateW: Boolean): Curve25519Point {
        val X1 = rawXCoord as Curve25519FieldElement
        val Y1 = rawYCoord as Curve25519FieldElement
        val Z1 = rawZCoords[0] as Curve25519FieldElement
        val W1 = jacobianModifiedW
        var c: Int
        val M = Nat256.create()
        square(X1.x, M)
        c = Nat256.addBothTo(M, M, M)
        c += Nat256.addTo(W1.x, M)
        reduce27(c, M)
        val _2Y1 = Nat256.create()
        twice(Y1.x, _2Y1)
        val _2Y1Squared = Nat256.create()
        multiply(_2Y1, Y1.x, _2Y1Squared)
        val S = Nat256.create()
        multiply(_2Y1Squared, X1.x, S)
        twice(S, S)
        val _8T = Nat256.create()
        square(_2Y1Squared, _8T)
        twice(_8T, _8T)
        val X3 = Curve25519FieldElement(_2Y1Squared)
        square(M, X3.x)
        subtract(X3.x, S, X3.x)
        subtract(X3.x, S, X3.x)
        val Y3 = Curve25519FieldElement(S)
        subtract(S, X3.x, Y3.x)
        multiply(Y3.x, M, Y3.x)
        subtract(Y3.x, _8T, Y3.x)
        val Z3 = Curve25519FieldElement(_2Y1)
        if (!Nat256.isOne(Z1.x)) {
            multiply(Z3.x, Z1.x, Z3.x)
        }
        var W3: Curve25519FieldElement? = null
        if (calculateW) {
            W3 = Curve25519FieldElement(_8T)
            multiply(W3.x, W1.x, W3.x)
            twice(W3.x, W3.x)
        }
        return Curve25519Point(getCurve(), X3, Y3, arrayOf(Z3, W3!!), isCompressed)
    }
}
