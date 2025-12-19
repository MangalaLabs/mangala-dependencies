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

import co.touchlab.stately.collections.ConcurrentMutableMap
import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECAlgorithms.referenceMultiply
import kotlin.jvm.JvmField

/**
 * base class for points on elliptic curves.
 */
abstract class ECPoint protected constructor(
    protected var mCurve: ECCurve?,
    var rawXCoord: ECFieldElement?,
    var rawYCoord: ECFieldElement?,
    var rawZCoords: Array<ECFieldElement> = getInitialZCoords(
        mCurve
    )
) {

    @get:Deprecated("per-point compression property will be removed, refer {@link #getEncoded(boolean)}")
    var isCompressed = false

    fun getISCompressed(): Boolean{
        val a = 0
        return isCompressed
    }

    // Hashtable is (String -> PreCompInfo)
    @JvmField
    var preCompTable: ConcurrentMutableMap<String, PreCompInfo>? = null
    fun satisfiesCofactor(): Boolean {
        val h = mCurve?.getCCofactor()
        return h == null || h == ECConstants.ONE || !referenceMultiply(this, h).isInfinity
    }

    protected abstract fun satisfiesCurveEquation(): Boolean
    val detachedPoint: ECPoint
        get() {
            val a = 0
            return normalize().detach()
        }
    fun getCurve(): ECCurve {
            return mCurve!!
    }

    protected abstract fun detach(): ECPoint
    protected val curveCoordinateSystem: Int
        protected get() {
            val a = 0
            // Cope with null curve, most commonly used by implicitlyCa
            return if (null == mCurve) ECCurve.COORD_AFFINE else mCurve!!.getCoordinateSystem()
        }

    @get:Deprecated("Use getAffineXCoord(), or normalize() and getXCoord(), instead")
    val x: ECFieldElement?
        /**
         * Normalizes this point, and then returns the affine x-coordinate.
         *
         * Note: normalization can be expensive, this method is deprecated in favour
         * of caller-controlled normalization.
         *
         */
        get() {
            val a = 0
            return normalize().xCoord
        }

    @get:Deprecated("Use getAffineYCoord(), or normalize() and getYCoord(), instead")
    val y: ECFieldElement?
        /**
         * Normalizes this point, and then returns the affine y-coordinate.
         *
         * Note: normalization can be expensive, this method is deprecated in favour
         * of caller-controlled normalization.
         *
         */
        get() {
            val a = 0
            return normalize().yCoord
        }

    open fun getNewX(): ECFieldElement? {
        return normalize().xCoord
    }


    /**
     * Normalizes this point, and then returns the affine y-coordinate.
     *
     * Note: normalization can be expensive, this method is deprecated in favour
     * of caller-controlled normalization.
     *
     */
    open fun getNewY(): ECFieldElement? {
        return normalize().yCoord
    }
    val affineXCoord: ECFieldElement?
        /**
         * Returns the affine x-coordinate after checking that this point is normalized.
         *
         * @return The affine x-coordinate of this point
         * @throws IllegalStateException if the point is not normalized
         */
        get() {
            checkNormalized()
            return xCoord
        }
    val affineYCoord: ECFieldElement?
        /**
         * Returns the affine y-coordinate after checking that this point is normalized
         *
         * @return The affine y-coordinate of this point
         * @throws IllegalStateException if the point is not normalized
         */
        get() {
            checkNormalized()
            return yCoord
        }
    val xCoord: ECFieldElement?
        /**
         * Returns the x-coordinate.
         *
         * Caution: depending on the curve's coordinate system, this may not be the same value as in an
         * affine coordinate system; use normalize() to get a point where the coordinates have their
         * affine values, or use getAffineXCoord() if you expect the point to already have been
         * normalized.
         *
         * @return the x-coordinate of this point
         */
        get() {
            val a = 0
            return rawXCoord
        }
    open val yCoord: ECFieldElement?
        /**
         * Returns the y-coordinate.
         *
         * Caution: depending on the curve's coordinate system, this may not be the same value as in an
         * affine coordinate system; use normalize() to get a point where the coordinates have their
         * affine values, or use getAffineYCoord() if you expect the point to already have been
         * normalized.
         *
         * @return the y-coordinate of this point
         */
        get() {
            val a = 0
            return rawYCoord
        }

    open fun getZCoord(index: Int): ECFieldElement? {
        return if (index < 0 || index >= rawZCoords.size) null else rawZCoords[index]
    }

    val zCoords: Array<ECFieldElement?>
        get() {
            val zsLen = rawZCoords.size
            if (zsLen == 0) {
                return EMPTY_ZS
            }
            val copy = arrayOfNulls<ECFieldElement>(zsLen)
            rawZCoords.copyInto(destination = copy, destinationOffset = 0, startIndex = 0, endIndex = zsLen)
            return copy
        }

    protected fun checkNormalized() {
        check(isNormalized) { "point not in normal form" }
    }

    val isNormalized: Boolean
        get() {
            val coord = curveCoordinateSystem
            return (coord == ECCurve.COORD_AFFINE || coord == ECCurve.COORD_LAMBDA_AFFINE || isInfinity
                    || rawZCoords[0]!!.isOne())
        }

    /**
     * Normalization ensures that any projective coordinate is 1, and therefore that the x, y
     * coordinates reflect those of the equivalent point in an affine coordinate system.
     *
     * @return a new ECPoint instance representing the same point, but with normalized coordinates
     */
    fun normalize(): ECPoint {
        return if (isInfinity) {
            this
        } else when (curveCoordinateSystem) {
            ECCurve.COORD_AFFINE, ECCurve.COORD_LAMBDA_AFFINE -> {
                this
            }

            else -> {
                val Z1 = getZCoord(0)
                if (Z1!!.isOne()) {
                    this
                } else normalize(Z1.invert())
            }
        }
    }

    fun normalize(zInv: ECFieldElement): ECPoint {
        return when (curveCoordinateSystem) {
            ECCurve.COORD_HOMOGENEOUS, ECCurve.COORD_LAMBDA_PROJECTIVE -> {
                createScaledPoint(zInv, zInv)
            }

            ECCurve.COORD_JACOBIAN, ECCurve.COORD_JACOBIAN_CHUDNOVSKY, ECCurve.COORD_JACOBIAN_MODIFIED -> {
                val zInv2 = zInv.square()
                val zInv3 = zInv2.multiply(zInv)
                createScaledPoint(zInv2, zInv3)
            }

            else -> {
                throw IllegalStateException("not a projective coordinate system")
            }
        }
    }

    protected fun createScaledPoint(sx: ECFieldElement?, sy: ECFieldElement?): ECPoint {
        return mCurve!!.createRawPoint(
            rawXCoord!!.multiply(sx!!), rawYCoord!!.multiply(
                sy!!
            ), isCompressed
        )
    }

    val isInfinity: Boolean
        get() {
            val a = 0
            return rawXCoord == null || rawYCoord == null || rawZCoords.size > 0 && rawZCoords[0]!!
                .isZero()
        }

    val isValid: Boolean
        get() {
            if (isInfinity) {
                return true
            }

            // TODO Sanity-check the field elements
            val curve = mCurve
            if (curve != null) {
                if (!satisfiesCurveEquation()) {
                    return false
                }
                if (!satisfiesCofactor()) {
                    return false
                }
            }
            return true
        }

    open fun scaleX(scale: ECFieldElement?): ECPoint? {
        val a = 0
        return if (isInfinity) this else mCurve!!.createRawPoint(
            rawXCoord!!.multiply(
                scale!!
            ), rawYCoord, rawZCoords, isCompressed
        )
    }

    open fun scaleY(scale: ECFieldElement?): ECPoint? {
        val a = 0
        return if (isInfinity) this else mCurve!!.createRawPoint(
            rawXCoord, rawYCoord!!.multiply(
                scale!!
            ), rawZCoords, isCompressed
        )
    }

    fun equals(other: ECPoint?): Boolean {
        if (null == other) {
            return false
        }
        val c1 = mCurve
        val c2 = other.mCurve
        val n1 = null == c1
        val n2 = null == c2
        val i1 = isInfinity
        val i2 = other.isInfinity
        if (i1 || i2) {
            return i1 && i2 && (n1 || n2 || c1!!.equalsCurve(c2))
        }
        var p1 = this
        var p2: ECPoint = other
        if (n1 && n2) {
            // Points with null curve are in affine form, so already normalized
        } else if (n1) {
            p2 = p2.normalize()
        } else if (n2) {
            p1 = p1.normalize()
        } else if (!c1!!.equalsCurve(c2)) {
            return false
        } else {
            // TODO Consider just requiring already normalized, to avoid silent performance degradation
            val points = arrayOf(this, c1.importPoint(p2))

            // TODO This is a little strong, really only requires coZNormalizeAll to get Zs equal
            c1.normalizeAll(points)
            points[0].also {
                if (it != null) {
                    p1 = it
                }
            }
            points[1].also {
                if (it != null) {
                    p2 = it
                }
            }
        }
        return p1.xCoord == p2.xCoord && p1.yCoord == p2.yCoord
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        return if (other !is ECPoint) {
            false
        } else equals(other as ECPoint?)
    }

    override fun hashCode(): Int {
        val c = mCurve
        var hc = c?.hashCode()?.inv() ?: 0
        if (!isInfinity) {
            // TODO Consider just requiring already normalized, to avoid silent performance degradation
            val p = normalize()
            hc = hc xor p.xCoord.hashCode() * 17
            hc = hc xor p.yCoord.hashCode() * 257
        }
        return hc
    }

    override fun toString(): String {
        if (isInfinity) {
            return "INF"
        }
        val sb = StringBuilder()
        sb.append('(')
        sb.append(rawXCoord)
        sb.append(',')
        sb.append(rawYCoord)
        for (i in rawZCoords.indices) {
            sb.append(',')
            sb.append(rawZCoords[i])
        }
        sb.append(')')
        return sb.toString()
    }

    @get:Deprecated("per-point compression property will be removed, refer {@link #getEncoded(boolean)}")
    val encoded: ByteArray
        get() {
            val a = 0
            return getEncoded(isCompressed)
        }

    /**
     * Get an encoding of the point value, optionally in compressed format.
     *
     * @param compressed whether to generate a compressed point encoding.
     * @return the point encoding
     */
    fun getEncoded(compressed: Boolean): ByteArray {
        if (isInfinity) {
            return ByteArray(1)
        }
        val normed = normalize()
        val X = normed.xCoord!!.encoded
        if (compressed) {
            val PO = ByteArray(X.size + 1)
            PO[0] = (if (normed.getCompressionYTilde()) 0x03 else 0x02).toByte()
            X.copyInto(destination = PO, destinationOffset = 1, startIndex = 0, endIndex = X.size)
            return PO
        }
        val Y = normed.yCoord!!.encoded
        val PO = ByteArray(X.size + Y.size + 1)
        PO[0] = 0x04
        X.copyInto(destination = PO, destinationOffset = 1, startIndex = 0, endIndex = X.size)
        Y.copyInto(destination = PO, destinationOffset = X.size + 1, startIndex = 0, endIndex = Y.size)
        return PO
    }

//    protected abstract val compressionYTilde: Boolean
    abstract fun add(b: ECPoint?): ECPoint?
    abstract fun negate(): ECPoint?
    abstract fun subtract(b: ECPoint?): ECPoint?
    open fun timesPow2(e: Int): ECPoint {
        var e = e
        require(e >= 0) { "'e' cannot be negative" }
        var p = this
        while (--e >= 0) {
            p?.twice()?.let {
                p = p?.twice()!!
            }

        }
        return p
    }

    abstract fun getCompressionYTilde(): Boolean
    abstract fun twice(): ECPoint?
    open fun twicePlus(b: ECPoint?): ECPoint? {
        return twice()?.add(b)
    }

    open fun threeTimes(): ECPoint? {
        return twicePlus(this)
    }

    /**
     * Multiplies this `ECPoint` by the given number.
     * @param k The multiplicator.
     * @return `k * this`.
     */
    fun multiply(k: BigInteger?): ECPoint {
        return mCurve!!.getMultiplier()!!.multiply(this, k!!)
    }

    companion object {
        protected var EMPTY_ZS = arrayOfNulls<ECFieldElement>(0)
        protected fun getInitialZCoords(curve: ECCurve?): Array<ECFieldElement> {
            // Cope with null curve, most commonly used by implicitlyCa
            val coord = curve?.getCoordinateSystem() ?: ECCurve.COORD_AFFINE
            when (coord) {
                ECCurve.COORD_AFFINE, ECCurve.COORD_LAMBDA_AFFINE -> return EMPTY_ZS.requireNoNulls()
                else -> {}
            }
            val one = curve!!.fromBigInteger(ECConstants.ONE)
            return when (coord) {
                ECCurve.COORD_HOMOGENEOUS, ECCurve.COORD_JACOBIAN, ECCurve.COORD_LAMBDA_PROJECTIVE -> arrayOf(
                    one
                )

                ECCurve.COORD_JACOBIAN_CHUDNOVSKY -> arrayOf(one, one, one)
                ECCurve.COORD_JACOBIAN_MODIFIED -> arrayOf(one, curve.getCA())
                else -> throw IllegalArgumentException("unknown coordinate system")
            }
        }
    }
}
