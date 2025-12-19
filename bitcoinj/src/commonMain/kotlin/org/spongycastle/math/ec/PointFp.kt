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

package org.spongycastle.math.ec

import com.ionspin.kotlin.bignum.integer.BigInteger


/**
 * Elliptic curve points over Fp
 */
class PointFp : PointAbstractFp {
    /**
     * Create a point which encodes without point compression.
     *
     * @param curve the curve to use
     * @param x affine x co-ordinate
     * @param y affine y co-ordinate
     *
     */
    @Deprecated("Use ECCurve.createPoint to construct points")
    constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : this(curve, x, y, false)

    /**
     * Create a point that encodes with or without point compression.
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
        return PointFp(null, this.affineXCoord, this.affineYCoord)
    }

    override fun getZCoord(index: Int): ECFieldElement {
        return if (index == 1 && ECCurve.COORD_JACOBIAN_MODIFIED == this.curveCoordinateSystem) {
            jacobianModifiedW
        } else super.getZCoord(index)!!
    }

    // B.3 pg 62
    override fun add(b: ECPoint?): ECPoint?{
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
        val coord = curve?.getCoordinateSystem()
        val X1 = rawXCoord
        val Y1 = rawYCoord
        val X2 = b!!.rawXCoord
        val Y2 = b.rawYCoord
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val dx = X2!!.subtract(X1!!)
                val dy = Y2!!.subtract(Y1!!)
                if (dx.isZero()) {
                    return if (dy.isZero()) {
                        // this == b, i.e. this must be doubled
                        twice()
                    } else curve.getNewInfinity()

                    // this == -b, i.e. the result is the point at infinity
                }
                val gamma = dy.divide(dx)
                val X3 = gamma.square().subtract(X1).subtract(X2)
                val Y3 = gamma.multiply(X1.subtract(X3)).subtract(Y1)
                PointFp(curve, X3, Y3, isCompressed)
            }

            ECCurve.COORD_HOMOGENEOUS -> {
                val Z1 = rawZCoords[0]
                val Z2 = b.rawZCoords[0]
                val Z1IsOne = Z1.isOne()
                val Z2IsOne = Z2.isOne()
                val u1 = if (Z1IsOne) Y2 else Y2!!.multiply(Z1)
                val u2 = if (Z2IsOne) Y1 else Y1!!.multiply(Z2)
                val u = u1!!.subtract(u2!!)
                val v1 = if (Z1IsOne) X2 else X2!!.multiply(Z1)
                val v2 = if (Z2IsOne) X1 else X1!!.multiply(Z2)
                val v = v1!!.subtract(v2!!)

                // Check if b == this or b == -this
                if (v.isZero()) {
                    return if (u.isZero()) {
                        // this == b, i.e. this must be doubled
                        twice()
                    } else curve.getNewInfinity()

                    // this == -b, i.e. the result is the point at infinity
                }

                // TODO Optimize for when w == 1
                val w = if (Z1IsOne) Z2 else if (Z2IsOne) Z1 else Z1.multiply(Z2)
                val vSquared = v.square()
                val vCubed = vSquared.multiply(v)
                val vSquaredV2 = vSquared.multiply(v2)
                val A = u.square().multiply(w).subtract(vCubed).subtract(two(vSquaredV2))
                val X3 = v.multiply(A)
                val Y3 = vSquaredV2.subtract(A).multiplyMinusProduct(u, u2, vCubed)
                val Z3 = vCubed.multiply(w)
                PointFp(curve, X3, Y3, arrayOf(Z3), isCompressed)
            }

            ECCurve.COORD_JACOBIAN, ECCurve.COORD_JACOBIAN_MODIFIED -> {
                val Z1 = rawZCoords[0]
                val Z2 = b.rawZCoords[0]
                val Z1IsOne = Z1.isOne()
                val X3: ECFieldElement
                val Y3: ECFieldElement
                var Z3: ECFieldElement
                var Z3Squared: ECFieldElement? = null
                if (!Z1IsOne && Z1 == Z2) {
                    // TODO Make this available as public method coZAdd?
                    val dx = X1!!.subtract(X2!!)
                    val dy = Y1!!.subtract(Y2!!)
                    if (dx.isZero()) {
                        return if (dy.isZero()) {
                            twice()
                        } else curve.getNewInfinity()
                    }
                    val C = dx.square()
                    val W1 = X1.multiply(C)
                    val W2 = X2.multiply(C)
                    val A1 = W1.subtract(W2).multiply(Y1)
                    X3 = dy.square().subtract(W1).subtract(W2)
                    Y3 = W1.subtract(X3).multiply(dy).subtract(A1)
                    Z3 = dx
                    Z3 = Z3.multiply(Z1)
                } else {
                    val Z1Squared: ECFieldElement
                    val U2: ECFieldElement
                    val S2: ECFieldElement
                    if (Z1IsOne) {
                        Z1Squared = Z1
                        U2 = X2!!
                        S2 = Y2!!
                    } else {
                        Z1Squared = Z1.square()
                        U2 = Z1Squared.multiply(X2!!)
                        val Z1Cubed = Z1Squared.multiply(Z1)
                        S2 = Z1Cubed.multiply(Y2!!)
                    }
                    val Z2IsOne = Z2.isOne()
                    val Z2Squared: ECFieldElement
                    val U1: ECFieldElement
                    val S1: ECFieldElement
                    if (Z2IsOne) {
                        Z2Squared = Z2
                        U1 = X1!!
                        S1 = Y1!!
                    } else {
                        Z2Squared = Z2.square()
                        U1 = Z2Squared.multiply(X1!!)
                        val Z2Cubed = Z2Squared.multiply(Z2)
                        S1 = Z2Cubed.multiply(Y1!!)
                    }
                    val H = U1.subtract(U2)
                    val R = S1.subtract(S2)

                    // Check if b == this or b == -this
                    if (H.isZero()) {
                        return if (R.isZero()) {
                            // this == b, i.e. this must be doubled
                            twice()
                        } else curve.getNewInfinity()

                        // this == -b, i.e. the result is the point at infinity
                    }
                    val HSquared = H.square()
                    val G = HSquared.multiply(H)
                    val V = HSquared.multiply(U1)
                    X3 = R.square().add(G).subtract(two(V))
                    Y3 = V.subtract(X3).multiplyMinusProduct(R, G, S1)
                    Z3 = H
                    if (!Z1IsOne) {
                        Z3 = Z3.multiply(Z1)
                    }
                    if (!Z2IsOne) {
                        Z3 = Z3.multiply(Z2)
                    }

                    // Alternative calculation of Z3 using fast square
                    //                X3 = four(X3);
                    //                Y3 = eight(Y3);
                    //                Z3 = doubleProductFromSquares(Z1, Z2, Z1Squared, Z2Squared).multiply(H);
                    if (Z3 === H) {
                        Z3Squared = HSquared
                    }
                }
                val zs: Array<ECFieldElement>
                zs = if (coord == ECCurve.COORD_JACOBIAN_MODIFIED) {
                    // TODO If the result will only be used in a subsequent addition, we don't need W3
                    val W3 = calculateJacobianModifiedW(Z3, Z3Squared)
                    arrayOf(Z3, W3)
                } else {
                    arrayOf(Z3)
                }
                PointFp(curve, X3, Y3, zs, isCompressed)
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }

    // B.3 pg 62
    override fun twice(): ECPoint {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val Y1 = rawYCoord
        if (Y1!!.isZero()) {
            return curve!!.getNewInfinity()
        }
        val coord = curve!!.getCoordinateSystem()
        val X1 = rawXCoord
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val X1Squared = X1!!.square()
                val gamma = three(X1Squared).add(getCurve()!!.getCA()).divide(two(Y1))
                val X3 = gamma.square().subtract(two(X1))
                val Y3 = gamma.multiply(X1.subtract(X3)).subtract(Y1)
                PointFp(curve, X3, Y3, isCompressed)
            }

            ECCurve.COORD_HOMOGENEOUS -> {
                val Z1 = rawZCoords[0]
                val Z1IsOne = Z1.isOne()

                // TODO Optimize for small negative a4 and -3
                var w = curve.getCA()
                if (!w.isZero() && !Z1IsOne) {
                    w = w.multiply(Z1.square())
                }
                w = w.add(three(X1!!.square()))
                val s = if (Z1IsOne) Y1 else Y1.multiply(Z1)
                val t = if (Z1IsOne) Y1.square() else s.multiply(Y1)
                val B = X1.multiply(t)
                val _4B = four(B)
                val h = w.square().subtract(two(_4B))
                val _2s = two(s)
                val X3 = h.multiply(_2s)
                val _2t = two(t)
                val Y3 = _4B.subtract(h).multiply(w).subtract(two(_2t.square()))
                val _4sSquared = if (Z1IsOne) two(_2t) else _2s.square()
                val Z3 = two(_4sSquared).multiply(s)
                PointFp(curve, X3, Y3, arrayOf(Z3), isCompressed)
            }

            ECCurve.COORD_JACOBIAN -> {
                val Z1 = rawZCoords[0]
                val Z1IsOne = Z1.isOne()
                val Y1Squared = Y1.square()
                val T = Y1Squared.square()
                val a4 = curve.getCA()
                val a4Neg = a4.negate()
                var M: ECFieldElement
                val S: ECFieldElement
                if (a4Neg.toBigInteger() == BigInteger.fromInt(3)) {
                    val Z1Squared = if (Z1IsOne) Z1 else Z1.square()
                    M = three(X1!!.add(Z1Squared).multiply(X1!!.subtract(Z1Squared)))
                    S = four(Y1Squared.multiply(X1))
                } else {
                    val X1Squared = X1!!.square()
                    M = three(X1Squared)
                    if (Z1IsOne) {
                        M = M.add(a4)
                    } else if (!a4.isZero()) {
                        val Z1Squared = Z1.square()
                        val Z1Pow4 = Z1Squared.square()
                        M = if (a4Neg.bitLength() < a4.bitLength()) {
                            M.subtract(Z1Pow4.multiply(a4Neg))
                        } else {
                            M.add(Z1Pow4.multiply(a4))
                        }
                    }
                    //                  S = two(doubleProductFromSquares(X1, Y1Squared, X1Squared, T));
                    S = four(X1.multiply(Y1Squared))
                }
                val X3 = M.square().subtract(two(S))
                val Y3 = S.subtract(X3).multiply(M).subtract(eight(T))
                var Z3 = two(Y1)
                if (!Z1IsOne) {
                    Z3 = Z3.multiply(Z1)
                }

                // Alternative calculation of Z3 using fast square
//                ECFieldElement Z3 = doubleProductFromSquares(Y1, Z1, Y1Squared, Z1Squared);
                PointFp(curve, X3, Y3, arrayOf(Z3), isCompressed)
            }

            ECCurve.COORD_JACOBIAN_MODIFIED -> {
                twiceJacobianModified(true)
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }

    override fun twicePlus(b: ECPoint?): ECPoint? {
        if (this === b) {
            return threeTimes()
        }
        if (this.isInfinity) {
            return b
        }
        if (b!!.isInfinity) {
            return twice()
        }
        val Y1 = rawYCoord
        if (Y1!!.isZero()) {
            return b
        }
        val curve = getCurve()
        val coord = curve!!.getCoordinateSystem()
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val X1 = rawXCoord
                val X2 = b.rawXCoord
                val Y2 = b.rawYCoord
                val dx = X2!!.subtract(X1!!)
                val dy = Y2!!.subtract(Y1)
                if (dx.isZero()) {
                    return if (dy.isZero()) {
                        // this == b i.e. the result is 3P
                        threeTimes()
                    } else this

                    // this == -b, i.e. the result is P
                }

                /*
                 * Optimized calculation of 2P + Q, as described in "Trading Inversions for
                 * Multiplications in Elliptic Curve Cryptography", by Ciet, Joye, Lauter, Montgomery.
                 */
                val X = dx.square()
                val Y = dy.square()
                val d = X.multiply(two(X1).add(X2)).subtract(Y)
                if (d.isZero()) {
                    return curve.getNewInfinity()
                }
                val D = d.multiply(dx)
                val I = D.invert()
                val L1 = d.multiply(I).multiply(dy)
                val L2 = two(Y1).multiply(X).multiply(dx).multiply(I).subtract(L1)
                val X4 = L2.subtract(L1).multiply(L1.add(L2)).add(X2)
                val Y4 = X1.subtract(X4).multiply(L2).subtract(Y1)
                PointFp(curve, X4, Y4, isCompressed)
            }

            ECCurve.COORD_JACOBIAN_MODIFIED -> {
                twiceJacobianModified(false).add(b)
            }

            else -> {
                twice().add(b)
            }
        }
    }

    override fun threeTimes(): ECPoint {
        if (this.isInfinity) {
            return this
        }
        val Y1 = rawYCoord
        if (Y1!!.isZero()) {
            return this
        }
        val curve = getCurve()
        val coord = curve!!.getCoordinateSystem()
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val X1 = rawXCoord
                val _2Y1 = two(Y1)
                val X = _2Y1.square()
                val Z = three(X1!!.square()).add(getCurve()!!.getCA())
                val Y = Z.square()
                val d = three(X1).multiply(X).subtract(Y)
                if (d.isZero()) {
                    return getCurve()!!.getNewInfinity()
                }
                val D = d.multiply(_2Y1)
                val I = D.invert()
                val L1 = d.multiply(I).multiply(Z)
                val L2 = X.square().multiply(I).subtract(L1)
                val X4 = L2.subtract(L1).multiply(L1.add(L2)).add(X1)
                val Y4 = X1.subtract(X4).multiply(L2).subtract(Y1)
                PointFp(curve, X4, Y4, isCompressed)
            }

            ECCurve.COORD_JACOBIAN_MODIFIED -> {
                twiceJacobianModified(false).add(this)!!
            }

            else -> {

                // NOTE: Be careful about recursions between twicePlus and threeTimes
                twice().add(this)!!
            }
        }
    }

    override fun timesPow2(e: Int): ECPoint {
        require(e >= 0) { "'e' cannot be negative" }
        if (e == 0 || this.isInfinity) {
            return this
        }
        if (e == 1) {
            return twice()
        }
        val curve = getCurve()
        var Y1 = rawYCoord
        if (Y1!!.isZero()) {
            return curve!!.getNewInfinity()
        }
        val coord = curve!!.getCoordinateSystem()
        var W1 = curve!!.getCA()
        var X1 = rawXCoord
        var Z1 = if (rawZCoords.size < 1) curve.fromBigInteger(ECConstants.ONE) else rawZCoords[0]
        if (!Z1.isOne()) {
            when (coord) {
                ECCurve.COORD_AFFINE -> {}
                ECCurve.COORD_HOMOGENEOUS -> {
                    val Z1Sq = Z1.square()
                    X1 = X1!!.multiply(Z1)
                    Y1 = Y1.multiply(Z1Sq)
                    W1 = calculateJacobianModifiedW(Z1, Z1Sq)
                }

                ECCurve.COORD_JACOBIAN -> W1 = calculateJacobianModifiedW(Z1, null)
                ECCurve.COORD_JACOBIAN_MODIFIED -> W1 = jacobianModifiedW
                else -> throw IllegalStateException("unsupported coordinate system")
            }
        }
        for (i in 0 until e) {
            if (Y1!!.isZero()) {
                return curve.getNewInfinity()
            }
            val X1Squared = X1!!.square()
            var M = three(X1Squared)
            val _2Y1 = two(Y1)
            val _2Y1Squared = _2Y1.multiply(Y1)
            val S = two(X1.multiply(_2Y1Squared))
            val _4T = _2Y1Squared.square()
            val _8T = two(_4T)
            if (!W1.isZero()) {
                M = M.add(W1)
                W1 = two(_8T.multiply(W1))
            }
            X1 = M.square().subtract(two(S))
            Y1 = M.multiply(S.subtract(X1)).subtract(_8T)
            Z1 = if (Z1.isOne()) _2Y1 else _2Y1.multiply(Z1)
        }
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val zInv = Z1.invert()
                val zInv2 = zInv.square()
                val zInv3 = zInv2.multiply(zInv)
                PointFp(curve, X1!!.multiply(zInv2), Y1!!.multiply(zInv3), isCompressed)
            }

            ECCurve.COORD_HOMOGENEOUS -> {
                X1 = X1!!.multiply(Z1)
                Z1 = Z1.multiply(Z1.square())
                PointFp(curve, X1, Y1, arrayOf(Z1), isCompressed)
            }

            ECCurve.COORD_JACOBIAN -> PointFp(
                curve,
                X1,
                Y1,
                arrayOf(Z1),
                isCompressed
            )

            ECCurve.COORD_JACOBIAN_MODIFIED -> PointFp(
                curve,
                X1,
                Y1,
                arrayOf(Z1, W1),
                isCompressed
            )

            else -> throw IllegalStateException("unsupported coordinate system")
        }
    }

    protected fun two(x: ECFieldElement): ECFieldElement {
        return x.add(x)
    }

    protected fun three(x: ECFieldElement): ECFieldElement {
        return two(x).add(x)
    }

    protected fun four(x: ECFieldElement): ECFieldElement {
        return two(two(x))
    }

    protected fun eight(x: ECFieldElement): ECFieldElement {
        return four(two(x))
    }

    protected fun doubleProductFromSquares(
        a: ECFieldElement, b: ECFieldElement?,
        aSquared: ECFieldElement?, bSquared: ECFieldElement?
    ): ECFieldElement {
        /*
         * NOTE: If squaring in the field is faster than multiplication, then this is a quicker
         * way to calculate 2.A.B, if A^2 and B^2 are already known.
         */
        return a.add(b!!).square().subtract(aSquared!!).subtract(bSquared!!)
    }

    override fun negate(): ECPoint {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val coord = curve!!.getCoordinateSystem()
        return if (ECCurve.COORD_AFFINE != coord) {
            PointFp(curve, rawXCoord, rawYCoord!!.negate(), rawZCoords, isCompressed)
        } else PointFp(curve, rawXCoord, rawYCoord!!.negate(), isCompressed)
    }

    protected fun calculateJacobianModifiedW(
        Z: ECFieldElement,
        ZSquared: ECFieldElement?
    ): ECFieldElement {
        var ZSquared = ZSquared
        val a4 = getCurve()!!.getCA()
        if (a4.isZero() || Z.isOne()) {
            return a4
        }
        if (ZSquared == null) {
            ZSquared = Z.square()
        }
        var W = ZSquared.square()
        val a4Neg = a4.negate()
        W = if (a4Neg.bitLength() < a4.bitLength()) {
            W.multiply(a4Neg).negate()
        } else {
            W.multiply(a4)
        }
        return W
    }

    protected val jacobianModifiedW: ECFieldElement
        protected get() {
            var W = rawZCoords[1]
            if (W == null) {
                // NOTE: Rarely, twicePlus will result in the need for a lazy W1 calculation here
                W = calculateJacobianModifiedW(rawZCoords[0], null)
                rawZCoords[1] = W
            }
            return W
        }

    protected fun twiceJacobianModified(calculateW: Boolean): PointFp {
        val X1 = rawXCoord
        val Y1 = rawYCoord
        val Z1 = rawZCoords[0]
        val W1 = jacobianModifiedW
        val X1Squared = X1!!.square()
        val M = three(X1Squared).add(W1)
        val _2Y1 = two(Y1!!)
        val _2Y1Squared = _2Y1.multiply(Y1)
        val S = two(X1.multiply(_2Y1Squared))
        val X3 = M.square().subtract(two(S))
        val _4T = _2Y1Squared.square()
        val _8T = two(_4T)
        val Y3 = M.multiply(S.subtract(X3)).subtract(_8T)
        val W3 = if (calculateW) two(_8T.multiply(W1)) else null
        val Z3 = if (Z1.isOne()) _2Y1 else _2Y1.multiply(Z1)
        return PointFp(getCurve(), X3, Y3, arrayOf(Z3, W3!!), isCompressed)
    }
}
