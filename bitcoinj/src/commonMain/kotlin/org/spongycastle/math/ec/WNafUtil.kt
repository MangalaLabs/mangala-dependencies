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
import com.mangala.wallet.bitcoinj.utils.bitCount
import com.mangala.wallet.bitcoinj.utils.shiftLeft
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.testBit
import com.mangala.wallet.bitcoinj.utils.toInt
import org.spongycastle.math.ec.ECAlgorithms.isFpCurve
import kotlin.jvm.JvmStatic
import kotlin.math.max

object WNafUtil {
    const val PRECOMP_NAME = "bc_wnaf"
    private val DEFAULT_WINDOW_SIZE_CUTOFFS = intArrayOf(13, 41, 121, 337, 897, 2305)
    private val EMPTY_BYTES = ByteArray(0)
    private val EMPTY_INTS = IntArray(0)
    private val EMPTY_POINTS = arrayOfNulls<ECPoint>(0)
    @JvmStatic
    fun generateCompactNaf(k: BigInteger): IntArray {
        require(k.bitLength() ushr 16 == 0) { "'k' must have bitlength < 2^16" }
        if (k.signum() == 0) {
            return EMPTY_INTS
        }
        val _3k = k.shiftLeft(1).add(k)
        val bits = _3k.bitLength()
        var naf = IntArray(bits shr 1)
        val diff = _3k.xor(k)
        val highBit = bits - 1
        var length = 0
        var zeroes = 0
        var i = 1
        while (i < highBit) {
            if (!diff.testBit(i)) {
                ++zeroes
                ++i
                continue
            }
            val digit = if (k.testBit(i)) -1 else 1
            naf[length++] = digit shl 16 or zeroes
            zeroes = 1
            ++i
            ++i
        }
        naf[length++] = 1 shl 16 or zeroes
        if (naf.size > length) {
            naf = trim(naf, length)
        }
        return naf
    }

    @JvmStatic
    fun generateCompactWindowNaf(width: Int, k: BigInteger): IntArray {
        var k = k
        if (width == 2) {
            return generateCompactNaf(k)
        }
        require(!(width < 2 || width > 16)) { "'width' must be in the range [2, 16]" }
        require(k.bitLength() ushr 16 == 0) { "'k' must have bitlength < 2^16" }
        if (k.signum() == 0) {
            return EMPTY_INTS
        }
        var wnaf = IntArray(k.bitLength() / width + 1)

        // 2^width and a mask and sign bit set accordingly
        val pow2 = 1 shl width
        val mask = pow2 - 1
        val sign = pow2 ushr 1
        var carry = false
        var length = 0
        var pos = 0
        while (pos <= k.bitLength()) {
            if (k.testBit(pos) == carry) {
                ++pos
                continue
            }
            k = k.shiftRight(pos)
            var digit = k.toInt() and mask
            if (carry) {
                ++digit
            }
            carry = digit and sign != 0
            if (carry) {
                digit -= pow2
            }
            val zeroes = if (length > 0) pos - 1 else pos
            wnaf[length++] = digit shl 16 or zeroes
            pos = width
        }

        // Reduce the WNAF array to its actual length
        if (wnaf.size > length) {
            wnaf = trim(wnaf, length)
        }
        return wnaf
    }

    fun generateJSF(g: BigInteger, h: BigInteger): ByteArray {
        val digits = max(g.bitLength(), h.bitLength()) + 1
        var jsf = ByteArray(digits)
        var k0 = g
        var k1 = h
        var j = 0
        var d0 = 0
        var d1 = 0
        var offset = 0
        while (d0 or d1 != 0 || k0.bitLength() > offset || k1.bitLength() > offset) {
            val n0 = (k0.toInt() ushr offset) + d0 and 7
            val n1 = (k1.toInt() ushr offset) + d1 and 7
            var u0 = n0 and 1
            if (u0 != 0) {
                u0 -= n0 and 2
                if (n0 + u0 == 4 && n1 and 3 == 2) {
                    u0 = -u0
                }
            }
            var u1 = n1 and 1
            if (u1 != 0) {
                u1 -= n1 and 2
                if (n1 + u1 == 4 && n0 and 3 == 2) {
                    u1 = -u1
                }
            }
            if (d0 shl 1 == 1 + u0) {
                d0 = d0 xor 1
            }
            if (d1 shl 1 == 1 + u1) {
                d1 = d1 xor 1
            }
            if (++offset == 30) {
                offset = 0
                k0 = k0.shiftRight(30)
                k1 = k1.shiftRight(30)
            }
            jsf[j++] = (u0 shl 4 or (u1 and 0xF)).toByte()
        }

        // Reduce the JSF array to its actual length
        if (jsf.size > j) {
            jsf = trim(jsf, j)
        }
        return jsf
    }

    fun generateNaf(k: BigInteger): ByteArray {
        if (k.signum() == 0) {
            return EMPTY_BYTES
        }
        val _3k = k.shiftLeft(1).add(k)
        val digits = _3k.bitLength() - 1
        val naf = ByteArray(digits)
        val diff = _3k.xor(k)
        var i = 1
        while (i < digits) {
            if (diff.testBit(i)) {
                naf[i - 1] = (if (k.testBit(i)) -1 else 1).toByte()
                ++i
            }
            ++i
        }
        naf[digits - 1] = 1
        return naf
    }

    /**
     * Computes the Window NAF (non-adjacent Form) of an integer.
     * @param width The width `w` of the Window NAF. The width is
     * defined as the minimal number `w`, such that for any
     * `w` consecutive digits in the resulting representation, at
     * most one is non-zero.
     * @param k The integer of which the Window NAF is computed.
     * @return The Window NAF of the given width, such that the following holds:
     * `k = <sub>i=0</sub><sup>l-1</sup> k<sub>i</sub>2<sup>i</sup>
    ` * , where the `k<sub>i</sub>` denote the elements of the
     * returned `byte[]`.
     */
    fun generateWindowNaf(width: Int, k: BigInteger): ByteArray {
        var k = k
        if (width == 2) {
            return generateNaf(k)
        }
        require(!(width < 2 || width > 8)) { "'width' must be in the range [2, 8]" }
        if (k.signum() == 0) {
            return EMPTY_BYTES
        }
        var wnaf = ByteArray(k.bitLength() + 1)

        // 2^width and a mask and sign bit set accordingly
        val pow2 = 1 shl width
        val mask = pow2 - 1
        val sign = pow2 ushr 1
        var carry = false
        var length = 0
        var pos = 0
        while (pos <= k.bitLength()) {
            if (k.testBit(pos) == carry) {
                ++pos
                continue
            }
            k = k.shiftRight(pos)
            var digit = k.toInt() and mask
            if (carry) {
                ++digit
            }
            carry = digit and sign != 0
            if (carry) {
                digit -= pow2
            }
            length += if (length > 0) pos - 1 else pos
            wnaf[length++] = digit.toByte()
            pos = width
        }

        // Reduce the WNAF array to its actual length
        if (wnaf.size > length) {
            wnaf = trim(wnaf, length)
        }
        return wnaf
    }

    fun getNafWeight(k: BigInteger): Int {
        if (k.signum() == 0) {
            return 0
        }
        val _3k = k.shiftLeft(1).add(k)
        val diff = _3k.xor(k)
        return diff.bitCount()
    }

    fun getWNafPreCompInfo(p: ECPoint): WNafPreCompInfo {
        return getWNafPreCompInfo(p.getCurve().getPreCompInfo(p, PRECOMP_NAME))
    }

    fun getWNafPreCompInfo(preCompInfo: PreCompInfo?): WNafPreCompInfo {
        return if (preCompInfo != null && preCompInfo is WNafPreCompInfo) {
            preCompInfo
        } else WNafPreCompInfo()
    }

    /**
     * Determine window width to use for a scalar multiplication of the given size.
     *
     * @param bits the bit-length of the scalar to multiply by
     * @return the window size to use
     */
    @JvmStatic
    fun getWindowSize(bits: Int): Int {
        return getWindowSize(bits, DEFAULT_WINDOW_SIZE_CUTOFFS)
    }

    /**
     * Determine window width to use for a scalar multiplication of the given size.
     *
     * @param bits the bit-length of the scalar to multiply by
     * @param windowSizeCutoffs a monotonically increasing list of bit sizes at which to increment the window width
     * @return the window size to use
     */
    fun getWindowSize(bits: Int, windowSizeCutoffs: IntArray): Int {
        var w = 0
        while (w < windowSizeCutoffs.size) {
            if (bits < windowSizeCutoffs[w]) {
                break
            }
            ++w
        }
        return w + 2
    }

    fun mapPointWithPrecomp(
        p: ECPoint, width: Int, includeNegated: Boolean,
        pointMap: ECPointMap
    ): ECPoint {
        val c = p.getCurve()
        val wnafPreCompP = precompute(p, width, includeNegated)
        val q = pointMap.map(p)
        val wnafPreCompQ = getWNafPreCompInfo(c.getPreCompInfo(q, PRECOMP_NAME))
        val twiceP = wnafPreCompP.getTwice()
        if (twiceP != null) {
            val twiceQ = pointMap.map(twiceP)
            wnafPreCompQ.setTwice(twiceQ)
        }
        val preCompP = wnafPreCompP.getPreComp()
        val preCompQ = arrayOfNulls<ECPoint>(preCompP!!.size)
        for (i in preCompP!!.indices) {
            preCompQ[i] = preCompP[i]?.let { pointMap.map(it) }
        }
        wnafPreCompQ.setPreComp(preCompQ)
        if (includeNegated) {
            val preCompNegQ = arrayOfNulls<ECPoint>(preCompQ.size)
            for (i in preCompNegQ.indices) {
                preCompNegQ[i] = preCompQ[i]!!.negate()
            }
            wnafPreCompQ.setPreCompNeg(preCompNegQ)
        }
        c.setPreCompInfo(q, PRECOMP_NAME, wnafPreCompQ)
        return q
    }

    @JvmStatic
    fun precompute(p: ECPoint, width: Int, includeNegated: Boolean): WNafPreCompInfo {
        val c = p.getCurve()
        val wnafPreCompInfo = getWNafPreCompInfo(c.getPreCompInfo(p, PRECOMP_NAME))
        var iniPreCompLen = 0
        val reqPreCompLen = 1 shl max(0, width - 2)
        var preComp = wnafPreCompInfo.getPreComp()
        if (preComp == null) {
            preComp = EMPTY_POINTS
        } else {
            iniPreCompLen = preComp.size
        }
        if (iniPreCompLen < reqPreCompLen) {
            preComp = resizeTable(preComp, reqPreCompLen)
            if (reqPreCompLen == 1) {
                preComp[0] = p.normalize()
            } else {
                var curPreCompLen = iniPreCompLen
                if (curPreCompLen == 0) {
                    preComp[0] = p
                    curPreCompLen = 1
                }
                var iso: ECFieldElement? = null
                if (reqPreCompLen == 2) {
                    preComp[1] = p.threeTimes()
                } else {
                    var twiceP = wnafPreCompInfo.getTwice()
                    var last = preComp[curPreCompLen - 1]
                    if (twiceP == null) {
                        twiceP = preComp[0]!!.twice()
                        wnafPreCompInfo.setTwice(twiceP)

                        /*
                         * For Fp curves with Jacobian projective coordinates, use a (quasi-)isomorphism
                         * where 'twiceP' is "affine", so that the subsequent additions are cheaper. This
                         * also requires scaling the initial point's X, Y coordinates, and reversing the
                         * isomorphism as part of the subsequent normalization.
                         * 
                         *  NOTE: The correctness of this optimization depends on:
                         *      1) additions do not use the curve's A, B coefficients.
                         *      2) no special cases (i.e. Q +/- Q) when calculating 1P, 3P, 5P, ...
                         */if (!twiceP!!.isInfinity && isFpCurve(c) && c.getFieldSize() >= 64) {
                            when (c.getCoordinateSystem()) {
                                ECCurve.COORD_JACOBIAN, ECCurve.COORD_JACOBIAN_CHUDNOVSKY, ECCurve.COORD_JACOBIAN_MODIFIED -> {
                                    iso = twiceP.getZCoord(0)
                                    twiceP = c.createPoint(
                                        twiceP.xCoord!!.toBigInteger(), twiceP.yCoord
                                        !!.toBigInteger()
                                    )
                                    val iso2 = iso!!.square()
                                    val iso3 = iso2.multiply(iso)
                                    last = last!!.scaleX(iso2)!!.scaleY(iso3)
                                    if (iniPreCompLen == 0) {
                                        preComp[0] = last
                                    }
                                }
                            }
                        }
                    }
                    while (curPreCompLen < reqPreCompLen) {
                        /*
                         * Compute the new ECPoints for the precomputation array. The values 1, 3,
                         * 5, ..., 2^(width-1)-1 times p are computed
                         */
                        last = last!!.add(twiceP)
                        preComp[curPreCompLen++] = last
                    }
                }

                /*
                 * Having oft-used operands in affine form makes operations faster.
                 */c.normalizeAll(preComp, iniPreCompLen, reqPreCompLen - iniPreCompLen, iso)
            }
        }
        wnafPreCompInfo.setPreComp(preComp)
        if (includeNegated) {
            var preCompNeg = wnafPreCompInfo.getPreCompNeg()
            var pos: Int
            if (preCompNeg == null) {
                pos = 0
                preCompNeg = arrayOfNulls(reqPreCompLen)
//                preCompNeg = emptyArray()
            } else {
                pos = preCompNeg.size
                if (pos < reqPreCompLen) {
                    preCompNeg = resizeTable(preCompNeg, reqPreCompLen)
                }
            }
            while (pos < reqPreCompLen) {
                preCompNeg[pos] = preComp[pos]!!.negate()
                ++pos
            }
            wnafPreCompInfo.setPreCompNeg(preCompNeg)
        }
        c.setPreCompInfo(p, PRECOMP_NAME, wnafPreCompInfo)
        return wnafPreCompInfo
    }

    private fun trim(a: ByteArray, length: Int): ByteArray {
        val result = ByteArray(length)
        a.copyInto(destination = result, destinationOffset = 0, startIndex = 0, endIndex = result.size)
        return result
    }

    private fun trim(a: IntArray, length: Int): IntArray {
        val result = IntArray(length)
        a.copyInto(destination = result, destinationOffset = 0, startIndex = 0, endIndex = result.size)
        return result
    }

    private fun resizeTable(a: Array<ECPoint?>?, length: Int): Array<ECPoint?> {
        val result = arrayOfNulls<ECPoint>(length)
        a?.copyInto(destination = result, destinationOffset = 0, startIndex = 0, endIndex = a.size)
        return result
    }
}
