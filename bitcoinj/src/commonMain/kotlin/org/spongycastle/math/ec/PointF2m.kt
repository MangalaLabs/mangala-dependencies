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

import org.spongycastle.math.ec.EF2m.Companion.checkFieldElements

/**
 * Elliptic curve points over F2m
 */
class PointF2m : ECPointAbstractF2m {
    /**
     * @param curve base curve
     * @param x x point
     * @param y y point
     *
     */
    @Deprecated("Use ECCurve.createPoint to construct points")
    constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : this(curve, x, y, false)

    /**
     * @param curve base curve
     * @param x x point
     * @param y y point
     * @param withCompression true if encode with point compression.
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
        if (x != null) {
            // Check if x and y are elements of the same field
            checkFieldElements(this.rawXCoord, this.rawYCoord)

            // Check if x and a are elements of the same field
            if (curve != null) {
                checkFieldElements(this.rawXCoord, this.mCurve!!.getCA())
            }
        }
        this.isCompressed = withCompression

//            checkCurveEquation();
    }

    internal constructor(
        curve: ECCurve?,
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ) : super(curve, x, y, zs) {
        this.isCompressed = withCompression

//            checkCurveEquation();
    }

    override fun detach(): ECPoint {
        return PointF2m(null, this.affineXCoord, this.affineYCoord) // earlier JDK
    }

    fun getYCoord2(): ECFieldElement? {
        val coord = this.curveCoordinateSystem
        return when (coord) {
            ECCurve.COORD_LAMBDA_AFFINE, ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                val X = rawXCoord
                val L = rawYCoord
                if (this.isInfinity || X?.isZero() == true) {
                    return L
                }

                // Y is actually Lambda (X + Y/X) here; convert to affine value on the fly
                var Y = L?.add(X!!)?.multiply(X!!)
                if (ECCurve.COORD_LAMBDA_PROJECTIVE == coord) {
                    val Z = rawZCoords[0]
                    if (!Z.isOne()) {
                        Y = Y?.divide(Z)
                    }
                }
                Y
            }

            else -> {
                rawYCoord
            }
        }
    }

    override fun getCompressionYTilde(): Boolean {
        val X = this.rawXCoord
        if (X?.isZero() == true) {
            return false
        }
        val Y = this.rawYCoord
        return when (this.curveCoordinateSystem) {
            ECCurve.COORD_LAMBDA_AFFINE, ECCurve.COORD_LAMBDA_PROJECTIVE -> {

                // Y is actually Lambda (X + Y/X) here
                Y?.testBitZero() != X?.testBitZero()
            }

            else -> {
                Y?.divide(X!!)?.testBitZero() ?: false
            }
        }
    }

    override fun add(b: ECPoint?): ECPoint? {
        if (this.isInfinity) {
            return b
        }
        if (b?.isInfinity == true) {
            return this
        }
        val curve = getCurve()
        val coord = curve?.getCoordinateSystem()
        var X1 = rawXCoord
        val X2 = b?.rawXCoord
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val Y1 = rawYCoord
                val Y2 = b?.rawYCoord
                val dx = X1?.add(X2!!)
                val dy = Y1?.add(Y2!!)
                if (dx?.isZero() == true) {
                    return if (dy?.isZero() == true) {
                        twice()
                    } else curve.getNewInfinity()
                }
                val L = dy?.divide(dx!!)
                val X3 = L!!.square().add(L).add(dx!!).add(curve.getCA())
                val Y3 = L.multiply(X1!!.add(X3)).add(X3).add(Y1)
                PointF2m(curve, X3, Y3, isCompressed)
            }

            ECCurve.COORD_HOMOGENEOUS -> {
                val Y1 = rawYCoord
                val Z1 = rawZCoords[0]
                val Y2 = b?.rawYCoord
                val Z2 = b!!.rawZCoords[0]
                val Z2IsOne = Z2.isOne()
                val U1 = Z1.multiply(Y2!!)
                val U2 = if (Z2IsOne) Y1 else Y1!!.multiply(Z2)
                val U = U1.add(U2!!)
                val V1 = Z1.multiply(X2!!)
                val V2 = if (Z2IsOne) X1 else X1!!.multiply(Z2)
                val V = V1.add(V2!!)
                if (V.isZero()) {
                    return if (U.isZero()) {
                        twice()
                    } else curve.getNewInfinity()
                }
                val VSq = V.square()
                val VCu = VSq.multiply(V)
                val W = if (Z2IsOne) Z1 else Z1.multiply(Z2)
                val uv = U.add(V)
                val A = uv.multiplyPlusProduct(U, VSq, curve.getCA())!!.multiply(W).add(VCu)
                val X3 = V.multiply(A)
                val VSqZ2 = if (Z2IsOne) VSq else VSq.multiply(Z2)
                val Y3 = U.multiplyPlusProduct(X1!!, V, Y1!!)!!
                    .multiplyPlusProduct(VSqZ2, uv, A)
                val Z3 = VCu.multiply(W)
                PointF2m(curve, X3, Y3, arrayOf(Z3), isCompressed)
            }

            ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                if (X1?.isZero() == true) {
                    return if (X2?.isZero() == true) {
                        curve.getNewInfinity()
                    } else b?.add(this)
                }
                val L1 = rawYCoord
                val Z1 = rawZCoords[0]
                val L2 = b?.rawYCoord
                val Z2 = b!!.rawZCoords[0]
                val Z1IsOne = Z1.isOne()
                var U2 = X2
                var S2 = L2
                if (!Z1IsOne) {
                    U2 = U2!!.multiply(Z1)
                    S2 = S2!!.multiply(Z1)
                }
                val Z2IsOne = Z2.isOne()
                var U1 = X1
                var S1 = L1
                if (!Z2IsOne) {
                    U1 = U1!!.multiply(Z2)
                    S1 = S1!!.multiply(Z2)
                }
                val A = S1!!.add(S2!!)
                var B = U1!!.add(U2!!)
                if (B.isZero()) {
                    return if (A.isZero()) {
                        twice()
                    } else curve.getNewInfinity()
                }
                val X3: ECFieldElement
                val L3: ECFieldElement
                var Z3: ECFieldElement
                if (X2!!.isZero()) {
                    // TODO This can probably be optimized quite a bit
                    val p = this.normalize()
                    X1 = p.xCoord
                    val Y1 = p.yCoord
                    val L = Y1!!.add(L2!!).divide(X1!!)
                    X3 = L.square().add(L).add(X1!!).add(curve.getCA())
                    if (X3.isZero()) {
                        return PointF2m(curve, X3, curve.getCB().sqrt(), isCompressed)
                    }
                    val Y3 = L.multiply(X1.add(X3)).add(X3).add(Y1)
                    L3 = Y3.divide(X3).add(X3)
                    Z3 = curve.fromBigInteger(ECConstants.ONE)
                } else {
                    B = B.square()
                    val AU1 = A.multiply(U1)
                    val AU2 = A.multiply(U2)
                    X3 = AU1.multiply(AU2)
                    if (X3.isZero()) {
                        return PointF2m(curve, X3, curve.getCB().sqrt(), isCompressed)
                    }
                    var ABZ2 = A.multiply(B)
                    if (!Z2IsOne) {
                        ABZ2 = ABZ2.multiply(Z2)
                    }
                    L3 = AU2.add(B).squarePlusProduct(ABZ2, L1!!.add(Z1))
                    Z3 = ABZ2
                    if (!Z1IsOne) {
                        Z3 = Z3.multiply(Z1)
                    }
                }
                PointF2m(curve, X3, L3, arrayOf(Z3), isCompressed)
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }

    override fun twice(): ECPoint {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val X1 = rawXCoord
        if (X1!!.isZero()) {
            // A point with X == 0 is it's own additive inverse
            return curve!!.getNewInfinity()
        }
        val coord = curve!!.getCoordinateSystem()
        return when (coord) {
            ECCurve.COORD_AFFINE -> {
                val Y1 = rawYCoord
                val L1 = Y1!!.divide(X1).add(X1)
                val X3 = L1.square().add(L1).add(curve.getCA())
                val Y3 = X1.squarePlusProduct(X3, L1.addOne())
                PointF2m(curve, X3, Y3, isCompressed)
            }

            ECCurve.COORD_HOMOGENEOUS -> {
                val Y1 = rawYCoord
                val Z1 = rawZCoords[0]
                val Z1IsOne = Z1.isOne()
                val X1Z1 = if (Z1IsOne) X1 else X1.multiply(Z1)
                val Y1Z1 = if (Z1IsOne) Y1 else Y1!!.multiply(Z1)
                val X1Sq = X1.square()
                val S = X1Sq.add(Y1Z1!!)
                val vSquared = X1Z1.square()
                val sv = S.add(X1Z1)
                val h = sv.multiplyPlusProduct(S, vSquared, curve.getCA())
                val X3 = X1Z1.multiply(h!!)
                val Y3 = X1Sq.square().multiplyPlusProduct(X1Z1, h, sv)
                val Z3 = X1Z1.multiply(vSquared)
                PointF2m(curve, X3, Y3, arrayOf(Z3), isCompressed)
            }

            ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                val L1 = rawYCoord
                val Z1 = rawZCoords[0]
                val Z1IsOne = Z1.isOne()
                val L1Z1 = if (Z1IsOne) L1 else L1!!.multiply(Z1)
                val Z1Sq = if (Z1IsOne) Z1 else Z1.square()
                val a = curve.getCA()
                val aZ1Sq = if (Z1IsOne) a else a.multiply(Z1Sq)
                val T = L1!!.square().add(L1Z1!!).add(aZ1Sq)
                if (T.isZero()) {
                    return PointF2m(curve, T, curve.getCB().sqrt(), isCompressed)
                }
                val X3 = T.square()
                val Z3 = if (Z1IsOne) T else T.multiply(Z1Sq)
                val b = curve.getCB()
                var L3: ECFieldElement
                if (b.bitLength() < curve.getFieldSize() shr 1) {
                    val t1 = L1.add(X1).square()
                    val t2: ECFieldElement
                    t2 = if (b.isOne()) {
                        aZ1Sq.add(Z1Sq).square()
                    } else {
                        // TODO Can be calculated with one square if we pre-compute sqrt(b)
                        aZ1Sq.squarePlusProduct(b, Z1Sq.square())
                    }
                    L3 = t1.add(T).add(Z1Sq).multiply(t1).add(t2).add(X3)
                    if (a.isZero()) {
                        L3 = L3.add(Z3)
                    } else if (!a.isOne()) {
                        L3 = L3.add(a.addOne().multiply(Z3))
                    }
                } else {
                    val X1Z1 = if (Z1IsOne) X1 else X1.multiply(Z1)
                    L3 = X1Z1.squarePlusProduct(T, L1Z1).add(X3).add(Z3)
                }
                PointF2m(curve, X3, L3, arrayOf(Z3), isCompressed)
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }

    override fun twicePlus(b: ECPoint?): ECPoint? {
        if (this.isInfinity) {
            return b
        }
        if (b?.isInfinity == true) {
            return twice()
        }
        val curve = getCurve()
        val X1 = rawXCoord
        if (X1?.isZero() == true) {
            // A point with X == 0 is it's own additive inverse
            return b
        }
        val coord = curve?.getCoordinateSystem()
        return when (coord) {
            ECCurve.COORD_LAMBDA_PROJECTIVE -> {

                // NOTE: twicePlus() only optimized for lambda-affine argument
                val X2 = b!!.rawXCoord
                val Z2 = b.rawZCoords[0]
                if (X2!!.isZero() || !Z2.isOne()) {
                    return twice().add(b)
                }
                val L1 = rawYCoord
                val Z1 = rawZCoords[0]
                val L2 = b.rawYCoord
                val X1Sq = X1!!.square()
                val L1Sq = L1!!.square()
                val Z1Sq = Z1.square()
                val L1Z1 = L1.multiply(Z1)
                val T = curve.getCA().multiply(Z1Sq).add(L1Sq).add(L1Z1)
                val L2plus1 = L2!!.addOne()
                val A = curve.getCA().add(L2plus1).multiply(Z1Sq).add(L1Sq)
                    .multiplyPlusProduct(T, X1Sq, Z1Sq)
                val X2Z1Sq = X2.multiply(Z1Sq)
                val B = X2Z1Sq.add(T).square()
                if (B.isZero()) {
                    return if (A!!.isZero()) {
                        b.twice()
                    } else curve.getNewInfinity()
                }
                if (A!!.isZero()) {
                    return PointF2m(curve, A, curve.getCB().sqrt(), isCompressed)
                }
                val X3 = A.square().multiply(X2Z1Sq)
                val Z3 = A.multiply(B).multiply(Z1Sq)
                val L3 = A.add(B).square().multiplyPlusProduct(T, L2plus1, Z3)
                PointF2m(curve, X3, L3, arrayOf(Z3), isCompressed)
            }

            else -> {
                twice().add(b)
            }
        }
    }

    override fun negate(): ECPoint {
        if (this.isInfinity) {
            return this
        }
        val X = rawXCoord
        return if (X?.isZero() == true) {
            this
        } else when (this.curveCoordinateSystem) {
            ECCurve.COORD_AFFINE -> {
                val Y = rawYCoord
                PointF2m(mCurve, X, Y!!.add(X!!), isCompressed)
            }

            ECCurve.COORD_HOMOGENEOUS -> {
                val Y = rawYCoord
                val Z = rawZCoords[0]
                PointF2m(mCurve, X, Y!!.add(X!!), arrayOf(Z), isCompressed)
            }

            ECCurve.COORD_LAMBDA_AFFINE -> {
                val L = rawYCoord
                PointF2m(mCurve, X, L!!.addOne(), isCompressed)
            }

            ECCurve.COORD_LAMBDA_PROJECTIVE -> {

                // L is actually Lambda (X + Y/X) here
                val L = rawYCoord
                val Z = rawZCoords[0]
                PointF2m(mCurve, X, L!!.add(Z), arrayOf(Z), isCompressed)
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }
}
