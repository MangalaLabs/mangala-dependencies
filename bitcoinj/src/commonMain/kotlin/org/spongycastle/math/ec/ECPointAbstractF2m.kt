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

abstract class ECPointAbstractF2m : ECPoint {
    protected constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : super(
        curve,
        x,
        y
    )

    protected constructor(
        curve: ECCurve?,
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>
    ) : super(curve, x, y, zs)

    override fun satisfiesCurveEquation(): Boolean {
        val curve = getCurve()
        val X = rawXCoord
        var A = curve?.getCA()
        var B = curve?.getCB()
        val coord = curve?.getCoordinateSystem()
        if (coord == ECCurve.COORD_LAMBDA_PROJECTIVE) {
            val Z = rawZCoords[0]
            val ZIsOne = Z.isOne()
            if (X?.isZero() == true) {
                // NOTE: For x == 0, we expect the affine-y instead of the lambda-y
                val Y = rawYCoord
                val lhs = Y?.square()
                var rhs = B
                if (!ZIsOne) {
                    rhs = rhs?.multiply(Z.square())
                }
                return lhs == rhs
            }
            val L = rawYCoord
            val X2 = X?.square()
            var lhs: ECFieldElement?
            val rhs: ECFieldElement
            if (ZIsOne) {
                lhs = L?.square()?.add(L)?.add(A!!)
                rhs = X2?.square()?.add(B!!)!!
            } else {
                val Z2 = Z.square()
                val Z4 = Z2.square()
                lhs = L?.add(Z)?.multiplyPlusProduct(L, A!!, Z2)
                // TODO If sqrt(b) is precomputed this can be simplified to a single square
                rhs = X2?.squarePlusProduct(B!!, Z4)!!
            }
            lhs = lhs!!.multiply(X2)
            return lhs == rhs
        }
        val Y = rawYCoord
        var lhs = Y?.add(X!!)?.multiply(Y)
        when (coord) {
            ECCurve.COORD_AFFINE -> {}
            ECCurve.COORD_HOMOGENEOUS -> {
                val Z = rawZCoords[0]
                if (!Z.isOne()) {
                    val Z2 = Z.square()
                    val Z3 = Z.multiply(Z2)
                    lhs = lhs?.multiply(Z)
                    A = A?.multiply(Z)
                    B = B?.multiply(Z3)
                }
            }

            else -> throw IllegalStateException("unsupported coordinate system")
        }
        val rhs = X?.add(A!!)?.multiply(X.square())?.add(B!!)
        return lhs == rhs
    }

    override fun scaleX(scale: ECFieldElement?): ECPoint? {
        if (this.isInfinity) {
            return this
        }
        val coord = this.curveCoordinateSystem
        return when (coord) {
            ECCurve.COORD_LAMBDA_AFFINE -> {

                // Y is actually Lambda (X + Y/X) here
                val X = this.rawXCoord
                val L = this.rawYCoord // earlier JDK
                val X2 = X?.multiply(scale!!)
                val L2 = L?.add(X!!)?.divide(scale!!)?.add(X2!!)
                getCurve()
                    ?.createRawPoint(X, L2, this.rawZCoords, isCompressed) // earlier JDK
            }

            ECCurve.COORD_LAMBDA_PROJECTIVE -> {

                // Y is actually Lambda (X + Y/X) here
                val X = this.rawXCoord
                val L = this.rawYCoord
                val Z = this.rawZCoords[0] // earlier JDK

                // We scale the Z coordinate also, to avoid an inversion
                val X2 = X?.multiply(scale?.square()!!)
                val L2 = L?.add(X!!)?.add(X2!!)
                val Z2 = Z.multiply(scale!!)
                getCurve()?.createRawPoint(
                    X2,
                    L2,
                    arrayOf(Z2),
                    isCompressed
                ) // earlier JDK
            }

            else -> {
                super.scaleX(scale)
            }
        }
    }

    override fun scaleY(scale: ECFieldElement?): ECPoint? {
        if (this.isInfinity) {
            return this
        }
        val coord = this.curveCoordinateSystem
        return when (coord) {
            ECCurve.COORD_LAMBDA_AFFINE, ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                val X = this.rawXCoord
                val L = this.rawYCoord // earlier JDK

                // Y is actually Lambda (X + Y/X) here
                val L2 = L?.add(X!!)?.multiply(scale!!)?.add(X)
                getCurve()
                    ?.createRawPoint(X, L2, this.rawZCoords, isCompressed) // earlier JDK
            }

            else -> {
                super.scaleY(scale)
            }
        }
    }

    override fun subtract(b: ECPoint?): ECPoint? {
        return if (b?.isInfinity == true) {
            this
        } else add(b?.negate())

        // Add -b
    }

    fun tau(): ECPointAbstractF2m {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val coord = curve?.getCoordinateSystem()
        val X1 = rawXCoord
        return when (coord) {
            ECCurve.COORD_AFFINE, ECCurve.COORD_LAMBDA_AFFINE -> {
                val Y1 = rawYCoord
                curve?.createRawPoint(
                    X1?.square(),
                    Y1?.square(),
                    isCompressed
                ) as ECPointAbstractF2m
            }

            ECCurve.COORD_HOMOGENEOUS, ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                val Y1 = rawYCoord
                val Z1 = rawZCoords[0]
                curve?.createRawPoint(
                    X1?.square(),
                    Y1?.square(),
                    arrayOf(Z1.square()),
                    isCompressed
                ) as ECPointAbstractF2m
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }

    fun tauPow(pow: Int): ECPointAbstractF2m {
        if (this.isInfinity) {
            return this
        }
        val curve = getCurve()
        val coord = curve?.getCoordinateSystem()
        val X1 = rawXCoord
        return when (coord) {
            ECCurve.COORD_AFFINE, ECCurve.COORD_LAMBDA_AFFINE -> {
                val Y1 = rawYCoord
                curve?.createRawPoint(
                    X1?.squarePow(pow),
                    Y1?.squarePow(pow),
                    isCompressed
                ) as ECPointAbstractF2m
            }

            ECCurve.COORD_HOMOGENEOUS, ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                val Y1 = rawYCoord
                val Z1 = rawZCoords[0]
                curve.createRawPoint(
                    X1?.squarePow(pow),
                    Y1?.squarePow(pow),
                    arrayOf(Z1.squarePow(pow)),
                    isCompressed
                ) as ECPointAbstractF2m
            }

            else -> {
                throw IllegalStateException("unsupported coordinate system")
            }
        }
    }
}
