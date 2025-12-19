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
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.hashCode
import kotlin.jvm.JvmStatic

/**
 * Class representing the Elements of the finite field
 * `F<sub>2<sup>m</sup></sub>` in polynomial basis (PB)
 * representation. Both trinomial (TPB) and pentanomial (PPB) polynomial
 * basis representations are supported. Gaussian normal basis (GNB)
 * representation is not supported.
 */
class EF2m : ECFieldElement {
    /**
     * @return the representation of the field
     * `F<sub>2<sup>m</sup></sub>`, either of
     * TPB (trinomial
     * basis representation) or
     * PPB (pentanomial
     * basis representation).
     */
    /**
     * TPB or PPB.
     */
    var representation = 0
        private set
    /**
     * @return the degree `m` of the reduction polynomial
     * `f(z)`.
     */
    /**
     * The exponent `m` of `F<sub>2<sup>m</sup></sub>`.
     */
    var m: Int
        private set
    private var ks: IntArray

    /**
     * The `LongArray` holding the bits.
     */
    private var x: NewLongArray

    /**
     * Constructor for PPB.
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
     * @param x The BigInteger representing the value of the field element.
     */
    @Deprecated("Use ECCurve.fromBigInteger to construct field elements")
    constructor(
        m: Int,
        k1: Int,
        k2: Int,
        k3: Int,
        x: BigInteger?
    ) {
        require(!(x == null || x.signum() < 0 || x.bitLength() > m)) { "x value invalid in F2m field element" }
        if (k2 == 0 && k3 == 0) {
            representation = TPB
            ks = intArrayOf(k1)
        } else {
            require(k2 < k3) { "k2 must be smaller than k3" }
            require(k2 > 0) { "k2 must be larger than 0" }
            representation = PPB
            ks = intArrayOf(k1, k2, k3)
        }
        this.m = m
        this.x = NewLongArray(x)
    }

    /**
     * Constructor for TPB.
     * @param m  The exponent `m` of
     * `F<sub>2<sup>m</sup></sub>`.
     * @param k The integer `k` where `x<sup>m</sup> +
     * x<sup>k</sup> + 1` represents the reduction
     * polynomial `f(z)`.
     * @param x The BigInteger representing the value of the field element.
     */
    @Deprecated("Use ECCurve.fromBigInteger to construct field elements")
    constructor(m: Int, k: Int, x: BigInteger?) : this(m, k, 0, 0, x)
    private constructor(m: Int, ks: IntArray, x: NewLongArray) {
        this.m = m
        representation = if (ks.size == 1) TPB else PPB
        this.ks = ks
        this.x = x
    }

    override fun bitLength(): Int {
        return x.degree()
    }

    override fun isOne(): Boolean {
        return x.isOne
    }

    override fun isZero(): Boolean {
        return x.isZero
    }

    override fun testBitZero(): Boolean {
        return x.testBitZero()
    }

    override fun toBigInteger(): BigInteger {
        return x.toBigInteger()
    }

    override fun getFieldName(): String {
        return "F2m"
    }

    override fun getFieldSize(): Int {
        return m
    }

    override fun add(b: ECFieldElement): ECFieldElement {
        // No check performed here for performance reasons. Instead the
        // elements involved are checked in ECPoint.F2m
        // checkFieldElements(this, b);
        val iarrClone = x.clone() as NewLongArray
        val bF2m = b as EF2m
        iarrClone.addShiftedByWords(bF2m.x, 0)
        return EF2m(m, ks, iarrClone)
    }

    override fun addOne(): ECFieldElement {
        return EF2m(m, ks, x.addOne())
    }

    override fun subtract(b: ECFieldElement): ECFieldElement {
        // Addition and subtraction are the same in F2m
        return add(b)
    }

    override fun multiply(b: ECFieldElement): ECFieldElement {
        // Right-to-left comb multiplication in the LongArray
        // Input: Binary polynomials a(z) and b(z) of degree at most m-1
        // Output: c(z) = a(z) * b(z) mod f(z)

        // No check performed here for performance reasons. Instead the
        // elements involved are checked in ECPoint.F2m
        // checkFieldElements(this, b);
        return EF2m(m, ks, x.modMultiply((b as EF2m).x, m, ks))
    }

    override fun multiplyMinusProduct(
        b: ECFieldElement,
        x: ECFieldElement,
        y: ECFieldElement
    ): ECFieldElement {
        return multiplyPlusProduct(b, x, y)
    }

    override fun multiplyPlusProduct(
        b: ECFieldElement,
        x: ECFieldElement,
        y: ECFieldElement
    ): ECFieldElement {
        val ax = this.x
        val bx = (b as EF2m).x
        val xx = (x as EF2m).x
        val yx = (y as EF2m).x
        var ab = ax.multiply(bx, m, ks)
        val xy = xx.multiply(yx, m, ks)
        if (ab === ax || ab === bx) {
            ab = ab.clone() as NewLongArray
        }
        ab.addShiftedByWords(xy, 0)
        ab.reduce(m, ks)
        return EF2m(m, ks, ab)
    }

    override fun divide(b: ECFieldElement): ECFieldElement {
        // There may be more efficient implementations
        val bInv = b.invert()
        return multiply(bInv)
    }

    override fun negate(): ECFieldElement {
        // -x == x holds for all x in F2m
        return this
    }

    override fun square(): ECFieldElement {
        return EF2m(m, ks, x.modSquare(m, ks))
    }

    override fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return squarePlusProduct(x, y)
    }

    override fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        val ax = this.x
        val xx = (x as EF2m).x
        val yx = (y as EF2m).x
        var aa = ax.square(m, ks)
        val xy = xx.multiply(yx, m, ks)
        if (aa === ax) {
            aa = aa.clone() as NewLongArray
        }
        aa.addShiftedByWords(xy, 0)
        aa.reduce(m, ks)
        return EF2m(m, ks, aa)
    }

    override fun squarePow(pow: Int): ECFieldElement {
        return if (pow < 1) this else EF2m(m, ks, x.modSquareN(pow, m, ks))
    }

    override fun invert(): ECFieldElement {
        return EF2m(m, ks, x.modInverse(m, ks))
    }

    override fun sqrt(): ECFieldElement {
        return if (x.isZero || x.isOne) this else squarePow(m - 1)
    }

    val k1: Int
        /**
         * @return TPB: The integer `k` where `x<sup>m</sup> +
         * x<sup>k</sup> + 1` represents the reduction polynomial
         * `f(z)`.<br></br>
         * PPB: The integer `k1` where `x<sup>m</sup> +
         * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
         * represents the reduction polynomial `f(z)`.<br></br>
         */
        get() = ks.get(0)
    val k2: Int
        /**
         * @return TPB: Always returns `0`<br></br>
         * PPB: The integer `k2` where `x<sup>m</sup> +
         * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
         * represents the reduction polynomial `f(z)`.<br></br>
         */
        get() = if (ks.size >= 2) ks.get(1) else 0
    val k3: Int
        /**
         * @return TPB: Always set to `0`<br></br>
         * PPB: The integer `k3` where `x<sup>m</sup> +
         * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
         * represents the reduction polynomial `f(z)`.<br></br>
         */
        get() = if (ks.size >= 3) ks.get(2) else 0

    override fun equals(anObject: Any?): Boolean {
        if (anObject === this) {
            return true
        }
        if (anObject !is EF2m) {
            return false
        }
        val b = anObject
        return (m == b.m && representation == b.representation
                && areEqual(ks, b.ks) && x == b.x)
    }

    override fun hashCode(): Int {
        return x.hashCode() xor m xor hashCode(ks)
    }

    companion object {
        /**
         * Indicates gaussian normal basis representation (GNB). Number chosen
         * according to X9.62. GNB is not implemented at present.
         */
        const val GNB = 1

        /**
         * Indicates trinomial basis representation (TPB). Number chosen
         * according to X9.62.
         */
        const val TPB = 2

        /**
         * Indicates pentanomial basis representation (PPB). Number chosen
         * according to X9.62.
         */
        const val PPB = 3

        /**
         * Checks, if the ECFieldElements `a` and `b`
         * are elements of the same field `F<sub>2<sup>m</sup></sub>`
         * (having the same representation).
         * @param a field element.
         * @param b field element to be compared.
         * @throws IllegalArgumentException if `a` and `b`
         * are not elements of the same field
         * `F<sub>2<sup>m</sup></sub>` (having the same
         * representation).
         */
        @JvmStatic
        fun checkFieldElements(
            a: ECFieldElement?,
            b: ECFieldElement?
        ) {
            require(!(a !is EF2m || b !is EF2m)) {
                ("Field elements are not "
                        + "both instances of ECFieldElement.F2m")
            }
            val aF2m = a
            val bF2m = b
            require(aF2m.representation == bF2m.representation) {
                // Should never occur
                "One of the F2m field elements has incorrect representation"
            }
            if (aF2m.m != bF2m.m || !areEqual(aF2m.ks, bF2m.ks)) {
                throw IllegalArgumentException("Field elements are not elements of the same field F2m")
            }
        }
    }
}
