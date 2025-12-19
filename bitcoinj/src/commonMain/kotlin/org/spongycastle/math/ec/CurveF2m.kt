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
import kotlin.jvm.JvmOverloads


/**
 * Elliptic curves over F2m. The Weierstrass equation is given by
 * `y<sup>2</sup> + xy = x<sup>3</sup> + ax<sup>2</sup> + b`.
 */
class CurveF2m : CurveAbstractF2m {
    /**
     * The exponent `m` of `F<sub>2<sup>m</sup></sub>`.
     */
    var m // can't be final - JDK 1.1
            : Int
        private set

    /**
     * TPB: The integer `k` where `x<sup>m</sup> +
     * x<sup>k</sup> + 1` represents the reduction polynomial
     * `f(z)`.<br></br>
     * PPB: The integer `k1` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.<br></br>
     */
    var k1 // can't be final - JDK 1.1
            : Int
        private set

    /**
     * TPB: Always set to `0`<br></br>
     * PPB: The integer `k2` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.<br></br>
     */
    var k2 // can't be final - JDK 1.1
            : Int
        private set

    /**
     * TPB: Always set to `0`<br></br>
     * PPB: The integer `k3` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.<br></br>
     */
    var k3 // can't be final - JDK 1.1
            : Int
        private set

    /**
     * The point at infinity on this curve.
     */
    private var infinity // can't be final - JDK 1.1
            : PointF2m

    /**
     * Constructor for Trinomial Polynomial Basis (TPB).
     * @param m  The exponent `m` of
     * `F<sub>2<sup>m</sup></sub>`.
     * @param k The integer `k` where `x<sup>m</sup> +
     * x<sup>k</sup> + 1` represents the reduction
     * polynomial `f(z)`.
     * @param a The coefficient `a` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     * @param b The coefficient `b` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     */
    constructor(
        m: Int,
        k: Int,
        a: BigInteger,
        b: BigInteger
    ) : this(m, k, 0, 0, a, b, null, null)

    /**
     * Constructor for Trinomial Polynomial Basis (TPB).
     * @param m  The exponent `m` of
     * `F<sub>2<sup>m</sup></sub>`.
     * @param k The integer `k` where `x<sup>m</sup> +
     * x<sup>k</sup> + 1` represents the reduction
     * polynomial `f(z)`.
     * @param a The coefficient `a` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     * @param b The coefficient `b` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     * @param order The order of the main subgroup of the elliptic curve.
     * @param cofactor The cofactor of the elliptic curve, i.e.
     * `#E<sub>a</sub>(F<sub>2<sup>m</sup></sub>) = h * n`.
     */
    constructor(
        m: Int,
        k: Int,
        a: BigInteger,
        b: BigInteger,
        order: BigInteger?,
        cofactor: BigInteger?
    ) : this(m, k, 0, 0, a, b, order, cofactor)
    /**
     * Constructor for Pentanomial Polynomial Basis (PPB).
     * @param m  The exponent `m` of
     * `F<sub>2<sup>m</sup></sub>`.
     * @param k1 The integer `k1` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     * @param k2 The integer `k2` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     * @param k3 The integer `k3` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     * @param a The coefficient `a` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     * @param b The coefficient `b` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     * @param order The order of the main subgroup of the elliptic curve.
     * @param cofactor The cofactor of the elliptic curve, i.e.
     * `#E<sub>a</sub>(F<sub>2<sup>m</sup></sub>) = h * n`.
     */
    /**
     * Constructor for Pentanomial Polynomial Basis (PPB).
     * @param m  The exponent `m` of
     * `F<sub>2<sup>m</sup></sub>`.
     * @param k1 The integer `k1` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     * @param k2 The integer `k2` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     * @param k3 The integer `k3` where `x<sup>m</sup> +
     * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     * @param a The coefficient `a` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     * @param b The coefficient `b` in the Weierstrass equation
     * for non-supersingular elliptic curves over
     * `F<sub>2<sup>m</sup></sub>`.
     */
    @JvmOverloads
    constructor(
        m: Int,
        k1: Int,
        k2: Int,
        k3: Int,
        a: BigInteger,
        b: BigInteger,
        order: BigInteger? = null,
        cofactor: BigInteger? = null
    ) : super(m, k1, k2, k3) {
        this.m = m
        this.k1 = k1
        this.k2 = k2
        this.k3 = k3
        this.setCurveOrder(order)
        this.setCurveCofactor(cofactor)
        infinity = PointF2m(this, null, null)
        this.setCurveA(fromBigInteger(a))
        this.setCurveB(fromBigInteger(b))
        setCurveCoord(F2M_DEFAULT_COORDS)
    }

    protected constructor(
        m: Int,
        k1: Int,
        k2: Int,
        k3: Int,
        a: ECFieldElement?,
        b: ECFieldElement?,
        order: BigInteger?,
        cofactor: BigInteger?
    ) : super(m, k1, k2, k3) {
        this.m = m
        this.k1 = k1
        this.k2 = k2
        this.k3 = k3
        this.setCurveOrder(order)
        this.setCurveCofactor(cofactor)
        infinity = PointF2m(this, null, null)
        this.setCurveA(a)
        this.setCurveB(b)
        setCurveCoord(F2M_DEFAULT_COORDS)
    }

    override fun cloneCurve(): ECCurve {
        return CurveF2m(m, k1, k2, k3, getCA(), getCB(), getCOrder(), getCCofactor())
    }

    override fun supportsCoordinateSystem(coord: Int): Boolean {
        return when (coord) {
            COORD_AFFINE, COORD_HOMOGENEOUS, COORD_LAMBDA_PROJECTIVE -> true
            else -> false
        }
    }

    override fun createDefaultMultiplier(): ECMultiplier? {
        return if (isKoblitz()) {
            WTauNafMultiplier()
        } else super.createDefaultMultiplier()
    }

    override fun getFieldSize(): Int {
        return m
    }

    override fun fromBigInteger(x: BigInteger?): ECFieldElement {
        return EF2m(m, k1, k2, k3, x)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        withCompression: Boolean
    ): ECPoint {
        return PointF2m(this, x, y, withCompression)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ): ECPoint {
        return PointF2m(this, x, y, zs, withCompression)
    }

    override fun getNewInfinity(): ECPoint {
        return infinity
    }

    val isTrinomial: Boolean
        /**
         * Return true if curve uses a Trinomial basis.
         *
         * @return true if curve Trinomial, false otherwise.
         */
        get() = k2 == 0 && k3 == 0

    @get:Deprecated("use {@link #getOrder()} instead")
    val n: BigInteger
        get() = getCOrder()

    @get:Deprecated("use {@link #getCofactor()} instead")
    val h: BigInteger?
        get() = getCCofactor()

    companion object {
        private const val F2M_DEFAULT_COORDS = COORD_LAMBDA_PROJECTIVE
    }
}
