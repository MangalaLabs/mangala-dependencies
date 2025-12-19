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
import co.touchlab.stately.concurrency.Synchronizable
import co.touchlab.stately.concurrency.synchronize
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.bitcoinj.utils.testBit
import org.spongycastle.math.ec.ECAlgorithms.montgomeryTrick
import org.spongycastle.math.ec.endo.ECEndomorphism
import org.spongycastle.math.ec.endo.GLVEndomorphism
import org.spongycastle.math.field.FiniteField
import org.spongycastle.util.BigIntegers.fromUnsignedByteArray
import org.spongycastle.util.Integers.rotateLeft
import com.mangala.wallet.bitcoinj.utils.Synchronized

public abstract class ECCurve: Synchronizable {

    companion object{
        const val COORD_AFFINE = 0
        const val COORD_HOMOGENEOUS = 1
        const val COORD_JACOBIAN = 2
        const val COORD_JACOBIAN_CHUDNOVSKY = 3
        const val COORD_JACOBIAN_MODIFIED = 4
        const val COORD_LAMBDA_AFFINE = 5
        const val COORD_LAMBDA_PROJECTIVE = 6
        const val COORD_SKEWED = 7

    }


    inner class Config internal constructor(
        protected var mCoord: Int,
        protected var mEndomorphism: ECEndomorphism?,
        protected var mMultiplier: ECMultiplier?
    ) {
        fun setEndomorphism(mEndomorphism: ECEndomorphism): Config {
            this.mEndomorphism = mEndomorphism
            return this
        }

        fun create(): ECCurve {
            check(supportsCoordinateSystem(mCoord)) { "unsupported coordinate system" }
            val c: ECCurve = cloneCurve()
            check(c !== this@ECCurve) { "implementation returned current curve" }

            // NOTE: Synchronization added to keep FindBugs™ happy
            synchronize {
                c.mCoord = mCoord
                c.mEndomorphism = mEndomorphism
                c.mMultiplier = mMultiplier
            }
            return c
        }
    }

    private var mField: FiniteField? = null

    open fun getCField(): FiniteField {
        return mField!!
    }

    private var mA: ECFieldElement? = null
    private var mB: ECFieldElement? = null
    private var mOrder: BigInteger? = null
    private var mCofactor: BigInteger? = null

    open fun setCurveA(mA: ECFieldElement?) {
        this.mA = mA
    }

    open fun getCA(): ECFieldElement {
        return mA!!
    }

    open fun setCurveB(mB: ECFieldElement?) {
        this.mB = mB
    }

    open fun getCB(): ECFieldElement {
        return mB!!
    }

    open fun setCurveOrder(mOrder: BigInteger?) {
        this.mOrder = mOrder
    }

    open fun getCOrder(): BigInteger {
        return mOrder!!
    }

    open fun setCurveCofactor(mCofactor: BigInteger?) {
        this.mCofactor = mCofactor
    }

    open fun getCCofactor(): BigInteger? {
        return mCofactor
    }

    private var mCoord = COORD_AFFINE

    open fun setCurveCoord(mCoord: Int) {
        this.mCoord = mCoord
    }

    private var mEndomorphism: ECEndomorphism? = null


    open fun getCoordinateSystem(): Int {
        return mCoord
    }

    open fun getNewEndomorphism(): ECEndomorphism? {
        return mEndomorphism
    }

    private var mMultiplier: ECMultiplier? = null

    constructor(field: FiniteField?) {
        mField = field
    }

    abstract fun getNewInfinity(): ECPoint

    protected abstract fun decompressPoint(yTilde: Int, X1: BigInteger?): ECPoint?

    abstract fun getFieldSize(): Int

    abstract fun fromBigInteger(x: BigInteger?): ECFieldElement

    abstract fun isValidFieldElement(x: BigInteger?): Boolean

    protected abstract fun cloneCurve(): ECCurve

    public abstract fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        withCompression: Boolean
    ): ECPoint

    public abstract fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ): ECPoint

    @Synchronized
    open fun configure(): Config {
        return Config(mCoord,
            mEndomorphism,
            mMultiplier)
    }

    open fun validatePoint(x: BigInteger?, y: BigInteger?): ECPoint? {
        val p = createPoint(x, y)
        require(p.isValid) { "Invalid point coordinates" }
        return p
    }


    @Deprecated(
        """per-point compression property will be removed, use {@link #validatePoint(BigInteger, BigInteger)}
      and refer {@link ECPoint#getEncoded(boolean)}"""
    )
    open fun validatePoint(x: BigInteger?, y: BigInteger?, withCompression: Boolean): ECPoint {
        val p = createPoint(x, y, withCompression)
        require(p.isValid) { "Invalid point coordinates" }
        return p
    }

    open fun createPoint(x: BigInteger?, y: BigInteger?): ECPoint {
        return createPoint(x, y, false)
    }


    @Deprecated(
        """per-point compression property will be removed, use {@link #createPoint(BigInteger, BigInteger)}
      and refer {@link ECPoint#getEncoded(boolean)}"""
    )
    open fun createPoint(x: BigInteger?, y: BigInteger?, withCompression: Boolean): ECPoint {
        return createRawPoint(fromBigInteger(x), fromBigInteger(y), withCompression)!!
    }

    protected open fun createDefaultMultiplier(): ECMultiplier? {
        return if (mEndomorphism is GLVEndomorphism) {
            GLVMultiplier(this, (mEndomorphism as GLVEndomorphism?)!!)
        } else WNafL2RMultiplier()
    }

    open fun supportsCoordinateSystem(coord: Int): Boolean {
        return coord == COORD_AFFINE
    }

    open fun getPreCompInfo(point: ECPoint, name: String): PreCompInfo? {
        checkPoint(point)
        return synchronize {
            val table = point.preCompTable
            return@synchronize if (table == null) null else table[name] as PreCompInfo?
        }
    }

    /**
     * Adds `PreCompInfo` for a point on this curve, under a given name. Used by
     * `ECMultiplier`s to save the precomputation for this `ECPoint` for use
     * by subsequent multiplication.
     *
     * @param point       The `ECPoint` to store precomputations for.
     * @param name        A `String` used to index precomputations of different types.
     * @param preCompInfo The values precomputed by the `ECMultiplier`.
     */
    open fun setPreCompInfo(point: ECPoint, name: String?, preCompInfo: PreCompInfo?) {
        checkPoint(point)
        synchronize {
            var table: ConcurrentMutableMap<String, PreCompInfo>? = point.preCompTable
            if (table == null) {
                table = ConcurrentMutableMap() // Equivalent to Hashtable(4) but more idiomatic in Kotlin
                point.preCompTable = table
            }
            table[name ?: ""] = preCompInfo!!
        }
    }

    open fun importPoint(p: ECPoint): ECPoint? {
        var p = p
        if (this === p.getCurve()) {
            return p
        }
        if (p.isInfinity) {
            return getNewInfinity()
        }

        // TODO Default behaviour could be improved if the two curves have the same coordinate system by copying any Z coordinates.
        p = p.normalize()
        return validatePoint(
            p.xCoord!!.toBigInteger(),
            p.yCoord!!.toBigInteger(),
            p.getISCompressed()
        )
    }

    /**
     * Normalization ensures that any projective coordinate is 1, and therefore that the x, y
     * coordinates reflect those of the equivalent point in an affine coordinate system. Where more
     * than one point is to be normalized, this method will generally be more efficient than
     * normalizing each point separately.
     *
     * @param points An array of points that will be updated in place with their normalized versions,
     * where necessary
     */
    open fun normalizeAll(points: Array<ECPoint?>) {
        normalizeAll(points, 0, points.size, null)
    }

    /**
     * Normalization ensures that any projective coordinate is 1, and therefore that the x, y
     * coordinates reflect those of the equivalent point in an affine coordinate system. Where more
     * than one point is to be normalized, this method will generally be more efficient than
     * normalizing each point separately. An (optional) z-scaling factor can be applied; effectively
     * each z coordinate is scaled by this value prior to normalization (but only one
     * actual multiplication is needed).
     *
     * @param points An array of points that will be updated in place with their normalized versions,
     * where necessary
     * @param off    The start of the range of points to normalize
     * @param len    The length of the range of points to normalize
     * @param iso    The (optional) z-scaling factor - can be null
     */
    open fun normalizeAll(points: Array<ECPoint?>, off: Int, len: Int, iso: ECFieldElement?) {
        checkPoints(points, off, len)
        when (getCoordinateSystem()) {
            COORD_AFFINE, COORD_LAMBDA_AFFINE -> {
                require(iso == null) { "'iso' not valid for affine coordinates" }
                return
            }
        }

        /*
         * Figure out which of the points actually need to be normalized
         */
        val zs = arrayOfNulls<ECFieldElement>(len)
        val indices = IntArray(len)
        var count = 0
        for (i in 0 until len) {
            val p = points[off + i]
            if (null != p && (iso != null || !p.isNormalized)) {
                zs[count] = p.getZCoord(0)
                indices[count++] = off + i
            }
        }
        if (count == 0) {
            return
        }
        montgomeryTrick(zs, 0, count, iso)
        for (j in 0 until count) {
            val index = indices[j]
            points[index] = points[index]?.normalize(zs[j]!!)
        }
    }

    /**
     * Sets the default `ECMultiplier`, unless already set.
     */
    @Synchronized
    open fun getMultiplier(): ECMultiplier? {
        if (mMultiplier == null) {
            mMultiplier = createDefaultMultiplier()
        }
        return mMultiplier
    }

    /**
     * Decode a point on this curve from its ASN.1 encoding. The different
     * encodings are taken account of, including point compression for
     * `F<sub>p</sub>` (X9.62 s 4.2.1 pg 17).
     *
     * @return The decoded point.
     */
    open fun decodePoint(encoded: ByteArray): ECPoint {
        var p: ECPoint? = null
        val expectedLength = (getFieldSize() + 7) / 8
        val type = encoded[0]
        when (type) {
            0x00.toByte() -> {
                require(encoded.size == 1) { "Incorrect length for infinity encoding" }
                p = getNewInfinity()
            }

            0x02.toByte(), 0x03.toByte() -> {
                require(encoded.size == expectedLength + 1) { "Incorrect length for compressed encoding" }
                val yTilde = type.toInt() and 1
                val X = fromUnsignedByteArray(encoded, 1, expectedLength)
                p = decompressPoint(yTilde, X)
                require(p!!.satisfiesCofactor()) { "Invalid point" }
            }

            0x04.toByte() -> {
                require(encoded.size == 2 * expectedLength + 1) { "Incorrect length for uncompressed encoding" }
                val X = fromUnsignedByteArray(encoded, 1, expectedLength)
                val Y = fromUnsignedByteArray(encoded, 1 + expectedLength, expectedLength)
                p = validatePoint(X, Y)
            }

            0x06.toByte(), 0x07.toByte() -> {
                require(encoded.size == 2 * expectedLength + 1) { "Incorrect length for hybrid encoding" }
                val X = fromUnsignedByteArray(encoded, 1, expectedLength)
                val Y = fromUnsignedByteArray(encoded, 1 + expectedLength, expectedLength)
                require(Y.testBit(0) == (type.toInt() == 0x07)) { "Inconsistent Y coordinate in hybrid encoding" }
                p = validatePoint(X, Y)
            }

            else -> throw IllegalArgumentException(
                "Invalid point encoding 0x" + type.toInt().toString(16)
            )
        }
        require(!(type.toInt() != 0x00 && p!!.isInfinity)) { "Invalid infinity encoding" }
        return p!!
    }

    protected open fun checkPoint(point: ECPoint?) {
        require(!(null == point || this !== point.getCurve())) { "'point' must be non-null and on this curve" }
    }

    protected open fun checkPoints(points: Array<ECPoint?>?, off: Int, len: Int) {
        requireNotNull(points) { "'points' cannot be null" }
        require(!(off < 0 || len < 0 || off > points.size - len)) { "invalid range specified for 'points'" }
        for (i in 0 until len) {
            val point = points[off + i]
            require(!(null != point && this !== point.getCurve())) { "'points' entries must be null or on this curve" }
        }
    }

    open fun equalsCurve(other: ECCurve?): Boolean {
        return (this === other
                || ((null != other && getCField() == other.getCField() && getCA()!!.toBigInteger() == other.getCA()!!
            .toBigInteger() && getCB()!!.toBigInteger() == other.getCB()!!
            .toBigInteger())))
    }

    open fun equals2(obj: Any): Boolean {
        return this === obj || (obj is ECCurve && equalsCurve(obj))
    }

    override fun hashCode(): Int {
        return (getCField().hashCode()
                xor rotateLeft(getCA()!!.toBigInteger().hashCode(), 8)
                xor rotateLeft(getCB()!!.toBigInteger().hashCode(), 16))
    }
}
