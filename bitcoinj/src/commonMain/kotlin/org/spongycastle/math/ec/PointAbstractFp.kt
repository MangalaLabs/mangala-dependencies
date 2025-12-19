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

abstract class PointAbstractFp : ECPoint {
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

    override fun getCompressionYTilde(): Boolean {
        return this.affineYCoord?.testBitZero() ?: false
    }

    override fun satisfiesCurveEquation(): Boolean {
        val X = rawXCoord
        val Y = rawYCoord
        var A = mCurve?.getCA()
        var B = mCurve?.getCB()
        var lhs = Y?.square()
        when (this.curveCoordinateSystem) {
            ECCurve.COORD_AFFINE -> {}
            ECCurve.COORD_HOMOGENEOUS -> {
                val Z = rawZCoords[0]
                if (!Z.isOne()) {
                    val Z2 = Z.square()
                    val Z3 = Z.multiply(Z2)
                    lhs = lhs?.multiply(Z)
                    A = A?.multiply(Z2)
                    B = B?.multiply(Z3)
                }
            }

            ECCurve.COORD_JACOBIAN, ECCurve.COORD_JACOBIAN_CHUDNOVSKY, ECCurve.COORD_JACOBIAN_MODIFIED -> {
                val Z = rawZCoords[0]
                if (!Z.isOne()) {
                    val Z2 = Z.square()
                    val Z4 = Z2.square()
                    val Z6 = Z2.multiply(Z4)
                    A = A?.multiply(Z4)
                    B = B?.multiply(Z6)
                }
            }

            else -> throw IllegalStateException("unsupported coordinate system")
        }
        val rhs = X?.square()?.add(A!!)?.multiply(X)?.add(B!!)
        return lhs == rhs
    }

    override fun subtract(b: ECPoint?): ECPoint? {
        return if (b?.isInfinity == true) {
            this
        } else add(b?.negate())

        // Add -b
    }
}
