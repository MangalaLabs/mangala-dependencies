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
import org.spongycastle.math.ec.Tnaf.getSi
import org.spongycastle.math.field.FiniteField
import org.spongycastle.math.field.FiniteFields.getBinaryExtensionField
import org.spongycastle.util.randomBigInteger
import com.mangala.wallet.bitcoinj.utils.Synchronized

abstract class CurveAbstractF2m protected constructor(m: Int, k1: Int, k2: Int, k3: Int) : ECCurve(
    buildField(m, k1, k2, k3)
) {
    /**
     * The auxiliary values `s<sub>0</sub>` and
     * `s<sub>1</sub>` used for partial modular reduction for
     * Koblitz curves.
     */
    @get:Synchronized
    var si: Array<BigInteger>? = null
        /**
         * @return the auxiliary values `s<sub>0</sub>` and
         * `s<sub>1</sub>` used for partial modular reduction for
         * Koblitz curves.
         */
        get() {
            if (field == null) {
                field = getSi(this)
            }
            return field
        }
        private set

    override fun isValidFieldElement(x: BigInteger?): Boolean {
        return x != null && x.signum() >= 0 && x.bitLength() <= this.getFieldSize()
    }

    override fun createPoint(x: BigInteger?, y: BigInteger?, withCompression: Boolean): ECPoint {
        val X = fromBigInteger(x)
        var Y = fromBigInteger(y)
        val coord = this.getCoordinateSystem()
        when (coord) {
            COORD_LAMBDA_AFFINE, COORD_LAMBDA_PROJECTIVE -> {
                if (X.isZero()) {
                    require(Y.square() == getCB())
                } else {
                    // Y becomes Lambda (X + Y/X) here
                    Y = Y.divide(X).add(X)
                }
            }

            else -> {}
        }
        return this.createRawPoint(X, Y, withCompression)
    }

    /**
     * Decompresses a compressed point P = (xp, yp) (X9.62 s 4.2.2).
     *
     * @param yTilde
     * ~yp, an indication bit for the decompression of yp.
     * @param X1
     * The field element xp.
     * @return the decompressed point.
     */
    override fun decompressPoint(yTilde: Int, X1: BigInteger?): ECPoint? {
        val x = fromBigInteger(X1)
        var y: ECFieldElement? = null
        if (x.isZero()) {
            y = getCB().sqrt()
        } else {
            val beta = x.square().invert().multiply(getCB()).add(getCA()).add(x)
            var z = solveQuadraticEquation(beta)
            if (z != null) {
                if (z.testBitZero() != (yTilde == 1)) {
                    z = z.addOne()
                }
                y = when (this.getCoordinateSystem()) {
                    COORD_LAMBDA_AFFINE, COORD_LAMBDA_PROJECTIVE -> {
                        z.add(x)
                    }

                    else -> {
                        z.multiply(x)
                    }
                }
            }
        }
        requireNotNull(y) { "Invalid point compression" }
        return this.createRawPoint(x, y, true)
    }

    /**
     * Solves a quadratic equation `z<sup>2</sup> + z = beta`(X9.62
     * D.1.6) The other solution is `z + 1`.
     *
     * @param beta
     * The value to solve the quadratic equation for.
     * @return the solution for `z<sup>2</sup> + z = beta` or
     * `null` if no solution exists.
     */
    private fun solveQuadraticEquation(beta: ECFieldElement): ECFieldElement? {
        if (beta.isZero()) {
            return beta
        }
        var gamma: ECFieldElement
        var z: ECFieldElement
        val zeroElement = fromBigInteger(ECConstants.ZERO)
        val m = this.getFieldSize()
//        val rand = Random()
        do {
            val t = fromBigInteger(randomBigInteger(m))
            z = zeroElement
            var w = beta
            for (i in 1 until m) {
                val w2 = w.square()
                z = z.square().add(w2.multiply(t))
                w = w2.add(beta)
            }
            if (!w.isZero()) {
                return null
            }
            gamma = z.square().add(z)
        } while (gamma.isZero())
        return z
    }

    open fun isKoblitz(): Boolean{
        return getCOrder() != null && getCCofactor() != null && getCB().isOne() && (getCA().isZero() || getCA().isOne())
    }
        /**
         * Returns true if this is a Koblitz curve (ABC curve).
         * @return true if this is a Koblitz curve (ABC curve), false otherwise
         */
//        get() =

    companion object {
        fun inverse(m: Int, ks: IntArray, x: BigInteger?): BigInteger {
            return NewLongArray(x).modInverse(m, ks).toBigInteger()
        }

        private fun buildField(m: Int, k1: Int, k2: Int, k3: Int): FiniteField {
            require(k1 != 0) { "k1 must be > 0" }
            if (k2 == 0) {
                require(k3 == 0) { "k3 must be 0 if k2 == 0" }
                return getBinaryExtensionField(intArrayOf(0, k1, m))
            }
            require(k2 > k1) { "k2 must be > k1" }
            require(k3 > k2) { "k3 must be > k2" }
            return getBinaryExtensionField(intArrayOf(0, k1, k2, k3, m))
        }
    }
}
