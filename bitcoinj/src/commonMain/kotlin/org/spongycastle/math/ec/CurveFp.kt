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
import org.spongycastle.math.ec.EFp.Companion.calculateResidue
import kotlin.jvm.JvmOverloads

/**
 * Elliptic curve over Fp
 */
class CurveFp : CurveAbstractFp {
    var q: BigInteger
    var r: BigInteger?
    var infinity: PointFp

    @JvmOverloads
    constructor(
        q: BigInteger,
        a: BigInteger,
        b: BigInteger,
        order: BigInteger? = null,
        cofactor: BigInteger? = null
    ) : super(q) {
        this.q = q
        r = calculateResidue(q)
        infinity = PointFp(this, null, null)
        this.setCurveA(fromBigInteger(a))
        this.setCurveB(fromBigInteger(b))
        this.setCurveOrder(order)
        this.setCurveCofactor(cofactor)
        setCurveCoord(FP_DEFAULT_COORDS)
    }

    protected constructor(
        q: BigInteger,
        r: BigInteger?,
        a: ECFieldElement?,
        b: ECFieldElement?,
        order: BigInteger? = null,
        cofactor: BigInteger? = null
    ) : super(q) {
        this.q = q
        this.r = r
        infinity = PointFp(this, null, null)
        this.setCurveA(a)
        this.setCurveB(b)
        this.setCurveOrder(order)
        this.setCurveCofactor(cofactor)
        setCurveCoord(FP_DEFAULT_COORDS)
    }

    override fun cloneCurve(): ECCurve {
        return CurveFp(q, r, getCA(), getCB(), getCOrder(), getCCofactor())
    }

    override fun supportsCoordinateSystem(coord: Int): Boolean {
        return when (coord) {
            COORD_AFFINE, COORD_HOMOGENEOUS, COORD_JACOBIAN, COORD_JACOBIAN_MODIFIED -> true
            else -> false
        }
    }

    override fun getFieldSize(): Int {
        return q.bitLength()
    }

    override fun fromBigInteger(x: BigInteger?): ECFieldElement {
        return EFp(q, r, x)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        withCompression: Boolean
    ): ECPoint {
        return PointFp(this, x, y, withCompression)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ): ECPoint {
        return PointFp(this, x, y, zs, withCompression)
    }

    override fun importPoint(p: ECPoint): ECPoint {
        if (this !== p.getCurve() && this.getCoordinateSystem() == COORD_JACOBIAN && !p.isInfinity) {
            when (p.getCurve()?.getCoordinateSystem()) {
                COORD_JACOBIAN, COORD_JACOBIAN_CHUDNOVSKY, COORD_JACOBIAN_MODIFIED -> return PointFp(
                    this,
                    p.rawXCoord?.toBigInteger()?.let { fromBigInteger(it) },
                    p.rawYCoord?.let { fromBigInteger(it.toBigInteger()) },
                    arrayOf(fromBigInteger(p.rawZCoords[0]?.toBigInteger()!!)),
                    p.isCompressed
                )

                else -> {}
            }
        }
        return super.importPoint(p)!!
    }

    override fun getNewInfinity(): ECPoint {
        return infinity
    }

    companion object {
        private const val FP_DEFAULT_COORDS = COORD_JACOBIAN_MODIFIED
    }
}
