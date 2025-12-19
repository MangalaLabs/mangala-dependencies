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
import com.mangala.wallet.bitcoinj.utils.testBit
import org.spongycastle.math.ec.endo.GLVEndomorphism
import org.spongycastle.math.field.FiniteField
import org.spongycastle.math.field.PolynomialExtensionField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object ECAlgorithms {
    @JvmStatic
    fun isF2mCurve(c: ECCurve): Boolean {
        return isF2mField(c.getCField())
    }

    fun isF2mField(field: FiniteField): Boolean {
        return field.getDimension() > 1 && field.getCharacteristic() == ECConstants.TWO && field is PolynomialExtensionField
    }

    @JvmStatic
    fun isFpCurve(c: ECCurve): Boolean {
        return isFpField(c.getCField())
    }

    fun isFpField(field: FiniteField): Boolean {
        return field.getDimension() == 1
    }

//    fun sumOfMultiplies(ps: Array<ECPoint>, ks: Array<BigInteger>): ECPoint {
//        require(!(ps == null || ks == null || ps.size != ks.size || ps.size < 1)) { "point and scalar arrays should be non-null, and of equal, non-zero, length" }
//        val count = ps.size
//        when (count) {
//            1 -> return ps[0].multiply(ks[0])
//            2 -> return sumOfTwoMultiplies(ps[0], ks[0], ps[1], ks[1])
//            else -> {}
//        }
//        val p = ps[0]
//        val c = p.getCurve()
//        val imported = emptyArray<ECPoint>()
//        imported[0] = p
//        for (i in 1 until count) {
//            imported[i] = importPoint(c, ps[i])!!
//        }
//        val endomorphism = c.getNewEndomorphism()
//        return if (endomorphism is GLVEndomorphism) {
//            validatePoint(
//                implSumOfMultipliesGLV(
//                    imported,
//                    ks,
//                    endomorphism
//                )
//            )
//        } else validatePoint(implSumOfMultiplies(imported, ks))
//    }

    fun sumOfTwoMultiplies(
        P: ECPoint, a: BigInteger,
        Q: ECPoint, b: BigInteger
    ): ECPoint {
        var Q = Q
        val cp = P.getCurve()
        Q = importPoint(cp, Q)!!

        // Point multiplication for Koblitz curves (using WTNAF) beats Shamir's trick
        if (cp is CurveAbstractF2m) {
            if (cp.isKoblitz()) {
                return validatePoint(P.multiply(a).add(Q.multiply(b))!!)
            }
        }
        val endomorphism = cp.getNewEndomorphism()
        return if (endomorphism is GLVEndomorphism) {
            validatePoint(
                implSumOfMultipliesGLV(
                    arrayOf(
                        P,
                        Q
                    ), arrayOf(a, b), endomorphism
                )
            )
        } else validatePoint(implShamirsTrickWNaf(P, a, Q, b))
    }

    /*
     * "Shamir's Trick", originally due to E. G. Straus
     * (Addition chains of vectors. American Mathematical Monthly,
     * 71(7):806-808, Aug./Sept. 1964)
     * <pre>
     * Input: The points P, Q, scalar k = (km?, ... , k1, k0)
     * and scalar l = (lm?, ... , l1, l0).
     * Output: R = k * P + l * Q.
     * 1: Z <- P + Q
     * 2: R <- O
     * 3: for i from m-1 down to 0 do
     * 4:        R <- R + R        {point doubling}
     * 5:        if (ki = 1) and (li = 0) then R <- R + P end if
     * 6:        if (ki = 0) and (li = 1) then R <- R + Q end if
     * 7:        if (ki = 1) and (li = 1) then R <- R + Z end if
     * 8: end for
     * 9: return R
     * </pre>
     */
//    fun shamirsTrick(
//        P: ECPoint, k: BigInteger,
//        Q: ECPoint, l: BigInteger
//    ): ECPoint {
//        var Q = Q
//        val cp = P.getCurve()
//        Q = importPoint(cp, Q)!!
//        return validatePoint(implShamirsTrickJsf(P, k, Q, l))
//    }

    fun importPoint(c: ECCurve, p: ECPoint): ECPoint? {
        val cp = p.getCurve()
        require(c.equalsCurve(cp)) { "Point must be on the same curve" }
        return c.importPoint(p)
    }

    @JvmStatic
    @JvmOverloads
    fun montgomeryTrick(
        zs: Array<ECFieldElement?>,
        off: Int,
        len: Int,
        scale: ECFieldElement? = null
    ) {
        /*
         * Uses the "Montgomery Trick" to invert many field elements, with only a single actual
         * field inversion. See e.g. the paper:
         * "Fast Multi-scalar Multiplication Methods on Elliptic Curves with Precomputation Strategy Using Montgomery Trick"
         * by Katsuyuki Okeya, Kouichi Sakurai.
         */
        val c = arrayOfNulls<ECFieldElement>(len)
        c[0] = zs[off]
        var i = 0
        while (++i < len) {
            c[i] = c[i - 1]!!.multiply(zs[off + i]!!)
        }
        --i
        if (scale != null) {
            c[i] = c[i]!!.multiply(scale)
        }
        var u = c[i]!!.invert()
        while (i > 0) {
            val j = off + i--
            val tmp = zs[j]
            zs[j] = c[i]!!.multiply(u)
            u = u.multiply(tmp!!)
        }
        zs[off] = u
    }

    /**
     * Simple shift-and-add multiplication. Serves as reference implementation
     * to verify (possibly faster) implementations, and for very small scalars.
     *
     * @param p
     * The point to multiply.
     * @param k
     * The multiplier.
     * @return The result of the point multiplication `kP`.
     */
    @JvmStatic
    fun referenceMultiply(p: ECPoint, k: BigInteger): ECPoint {
        var p = p
        val x = k.abs()
        var q = p.getCurve().getNewInfinity()
        val t = x.bitLength()
        if (t > 0) {
            if (x.testBit(0)) {
                q = p
            }
            for (i in 1 until t) {
                p = p.twice()!!
                if (x.testBit(i)) {
                    q = q.add(p)!!
                }
            }
        }
        return if (k.signum() < 0) q.negate()!! else q
    }

    fun validatePoint(p: ECPoint): ECPoint {
        require(p.isValid) { "Invalid point" }
        return p
    }

    fun implShamirsTrickJsf(
        P: ECPoint, k: BigInteger,
        Q: ECPoint, l: BigInteger
    ): ECPoint {
        val curve = P.getCurve()
        val infinity = curve.getNewInfinity()

        // TODO conjugate co-Z addition (ZADDC) can return both of these
        val PaddQ = P.add(Q)
        val PsubQ = P.subtract(Q)
        val points = arrayOf(Q, PsubQ, P, PaddQ)
        curve.normalizeAll(points)
        val table = arrayOf(
            points[3]!!.negate(), points[2]!!.negate(), points[1]!!.negate(),
            points[0]!!.negate(), infinity, points[0],
            points[1], points[2], points[3]
        )
        val jsf = WNafUtil.generateJSF(k, l)
        var R = infinity
        var i = jsf.size
        while (--i >= 0) {
            val jsfi = jsf[i].toInt()

            // NOTE: The shifting ensures the sign is extended correctly
            val kDigit = jsfi shl 24 shr 28
            val lDigit = jsfi shl 28 shr 28
            val index = 4 + kDigit * 3 + lDigit
            R = R.twicePlus(table[index])!!
        }
        return R
    }

    @JvmStatic
    fun implShamirsTrickWNaf(
        P: ECPoint, k: BigInteger,
        Q: ECPoint, l: BigInteger
    ): ECPoint {
        var k = k
        var l = l
        val negK = k!!.signum() < 0
        val negL = l!!.signum() < 0
        k = k.abs()
        l = l.abs()
        val widthP = max(2, min(16, WNafUtil.getWindowSize(k.bitLength())))
        val widthQ = max(2, min(16, WNafUtil.getWindowSize(l.bitLength())))
        val infoP = WNafUtil.precompute(P, widthP, true)
        val infoQ = WNafUtil.precompute(Q, widthQ, true)
        val preCompP = if (negK) infoP.getPreCompNeg() else infoP.getPreComp()
        val preCompQ = if (negL) infoQ.getPreCompNeg() else infoQ.getPreComp()
        val preCompNegP = if (negK) infoP.getPreComp() else infoP.getPreCompNeg()
        val preCompNegQ = if (negL) infoQ.getPreComp() else infoQ.getPreCompNeg()
        val wnafP = WNafUtil.generateWindowNaf(widthP, k)
        val wnafQ = WNafUtil.generateWindowNaf(widthQ, l)
        return implShamirsTrickWNaf(preCompP, preCompNegP, wnafP, preCompQ, preCompNegQ, wnafQ)
    }

    @JvmStatic
    fun implShamirsTrickWNaf(
        P: ECPoint,
        k: BigInteger,
        pointMapQ: ECPointMap,
        l: BigInteger
    ): ECPoint {
        var k = k
        var l = l
        val negK = k.signum() < 0
        val negL = l.signum() < 0
        k = k.abs()
        l = l.abs()
        val width = max(
            2,
            min(16, WNafUtil.getWindowSize(max(k.bitLength(), l.bitLength())))
        )
        val Q = WNafUtil.mapPointWithPrecomp(P, width, true, pointMapQ)
        val infoP = WNafUtil.getWNafPreCompInfo(P)
        val infoQ = WNafUtil.getWNafPreCompInfo(Q)
        val preCompP = if (negK) infoP.getPreCompNeg() else infoP.getPreComp()
        val preCompQ = if (negL) infoQ.getPreCompNeg() else infoQ.getPreComp()
        val preCompNegP = if (negK) infoP.getPreComp() else infoP.getPreCompNeg()
        val preCompNegQ = if (negL) infoQ.getPreComp() else infoQ.getPreCompNeg()
        val wnafP = WNafUtil.generateWindowNaf(width, k)
        val wnafQ = WNafUtil.generateWindowNaf(width, l)
        return implShamirsTrickWNaf(preCompP, preCompNegP, wnafP, preCompQ, preCompNegQ, wnafQ)
    }

    private fun implShamirsTrickWNaf(
        preCompP: Array<ECPoint?>?, preCompNegP: Array<ECPoint?>?, wnafP: ByteArray,
        preCompQ: Array<ECPoint?>?, preCompNegQ: Array<ECPoint?>?, wnafQ: ByteArray
    ): ECPoint {
        val len = max(wnafP.size, wnafQ.size)
        val curve = preCompP?.get(0)?.getCurve()
        val infinity = curve?.getNewInfinity()
        var R = infinity
        var zeroes = 0
        for (i in len - 1 downTo 0) {
            val wiP = (if (i < wnafP.size) wnafP[i] else 0).toInt()
            val wiQ = (if (i < wnafQ.size) wnafQ[i] else 0).toInt()
            if (wiP or wiQ == 0) {
                ++zeroes
                continue
            }
            var r = infinity
            if (wiP != 0) {
                val nP = abs(wiP)
                val tableP = if (wiP < 0) preCompNegP else preCompP
                r = r?.add(tableP?.get(nP ushr 1) ?: infinity)
            }
            if (wiQ != 0) {
                val nQ = abs(wiQ)
                val tableQ = if (wiQ < 0) preCompNegQ else preCompQ
                r = r?.add(tableQ?.get(nQ ushr 1) ?: infinity)
            }
            if (zeroes > 0) {
                R = R?.timesPow2(zeroes)
                zeroes = 0
            }
            R = R?.twicePlus(r)
        }
        if (zeroes > 0) {
            R = R?.timesPow2(zeroes)
        }
        return R!!
    }

    fun implSumOfMultiplies(ps: Array<ECPoint>, ks: Array<BigInteger>): ECPoint {
        val count = ps.size
        val negs = BooleanArray(count)
        val infos = emptyArray<WNafPreCompInfo>()
        val wnafs = emptyArray<ByteArray>()
        for (i in 0 until count) {
            var ki = ks[i]
            negs[i] = ki!!.signum() < 0
            ki = ki.abs()
            val width = max(2, min(16, WNafUtil.getWindowSize(ki.bitLength())))
            infos[i] = WNafUtil.precompute(ps[i], width, true)
            wnafs[i] = WNafUtil.generateWindowNaf(width, ki)
        }
        return implSumOfMultiplies(negs, infos, wnafs)
    }

    fun implSumOfMultipliesGLV(ps: Array<ECPoint>, ks: Array<BigInteger>, glvEndomorphism: GLVEndomorphism): ECPoint {
        val n = ps[0].getCurve().getCOrder()

        val len = ps.size

        val abs = Array(len shl 1) { BigInteger.ZERO }
        for (i in 0 until len) {
            val ab = glvEndomorphism.decomposeScalar(ks[i].mod(n))
            abs[i shl 1] = ab[0]
            abs[(i shl 1) + 1] = ab[1]
        }

        val pointMap = glvEndomorphism.getPointMap()
        if (glvEndomorphism.hasEfficientPointMap()) {
            return implSumOfMultiplies(ps, pointMap, abs)
        }

        val pqs = Array(len shl 1) { ps[0] } // Initialized with the first point, will be overwritten
        for (i in 0 until len) {
            val p = ps[i]
            val q = pointMap.map(p)
            pqs[i shl 1] = p
            pqs[(i shl 1) + 1] = q
        }

        return implSumOfMultiplies(pqs, abs)
    }

    fun implSumOfMultiplies(ps: Array<ECPoint>, pointMap: ECPointMap, ks: Array<BigInteger>): ECPoint {
        val halfCount = ps.size
        val fullCount = halfCount shl 1

        val negs = BooleanArray(fullCount)
        val infos = Array(fullCount) { WNafPreCompInfo() } // Temporary initialization
        val wnafs = Array(fullCount) { ByteArray(0) } // Temporary initialization

        for (i in 0 until halfCount) {
            val j0 = i shl 1
            val j1 = j0 + 1

            var kj0 = ks[j0]
            negs[j0] = kj0.signum() < 0
            kj0 = kj0.abs()

            var kj1 = ks[j1]
            negs[j1] = kj1.signum() < 0
            kj1 = kj1.abs()

            val width = maxOf(2, minOf(16,
                WNafUtil.getWindowSize(maxOf(kj0.bitLength(), kj1.bitLength()))
            ))

            val P = ps[i]
            val Q = WNafUtil.mapPointWithPrecomp(P, width, true, pointMap)
            infos[j0] = WNafUtil.getWNafPreCompInfo(P)
            infos[j1] = WNafUtil.getWNafPreCompInfo(Q)
            wnafs[j0] = WNafUtil.generateWindowNaf(width, kj0)
            wnafs[j1] = WNafUtil.generateWindowNaf(width, kj1)
        }

        return implSumOfMultiplies(negs, infos, wnafs)
    }

    private fun implSumOfMultiplies(
        negs: BooleanArray,
        infos: Array<WNafPreCompInfo>,
        wnafs: Array<ByteArray>
    ): ECPoint {
        var len = 0
        val count = wnafs.size
        for (i in 0 until count) {
            len = max(len, wnafs[i]!!.size)
        }
        val curve = infos[0]!!.getPreComp()?.get(0)?.getCurve()
        val infinity = curve?.getNewInfinity()
        var R = infinity
        var zeroes = 0
        for (i in len - 1 downTo 0) {
            var r = infinity
            for (j in 0 until count) {
                val wnaf = wnafs[j]
                val wi = (if (i < wnaf!!.size) wnaf[i] else 0).toInt()
                if (wi != 0) {
                    val n = abs(wi)
                    val info = infos[j]
                    val table =
                        if (wi < 0 == negs[j]) info!!.getPreComp() else info!!.getPreCompNeg()
                    r = r?.add(table?.get(n ushr 1) ?: infinity)
                }
            }
            if (r === infinity) {
                ++zeroes
                continue
            }
            if (zeroes > 0) {
                R = R?.timesPow2(zeroes)
                zeroes = 0
            }
            R = R?.twicePlus(r)
        }
        if (zeroes > 0) {
            R = R?.timesPow2(zeroes)
        }
        return R!!
    }
}
