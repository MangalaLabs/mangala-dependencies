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


/**
 * Class implementing the WTNAF (Window
 * ``-adic Non-Adjacent Form) algorithm.
 */
class WTauNafMultiplier : AbstractECMultiplier() {
    /**
     * Multiplies a [org.spongycastle.math.ec.ECPointAbstractF2m]
     * by `k` using the reduced ``-adic NAF (RTNAF)
     * method.
     * @param point The org.spongycastle.math.ec.ECPointAbstractF2m to multiply.
     * @param k The integer by which to multiply `k`.
     * @return `p` multiplied by `k`.
     */
    override fun multiplyPositive(point: ECPoint, k: BigInteger): ECPoint {
        require(point is ECPointAbstractF2m) {
            "Only org.spongycastle.math.ec.ECPointAbstractF2m can be " +
                    "used in WTauNafMultiplier"
        }
        val p = point
        val curve = p.getCurve() as CurveAbstractF2m
        val m = curve.getFieldSize()
        val a = curve.getCA().toBigInteger().byteValue()
        val mu = Tnaf.getMu(a.toInt())
        val s = curve.si
        val rho = Tnaf.partModReduction(k, m, a, s!!, mu, 10.toByte())
        return multiplyWTnaf(p, rho, curve.getPreCompInfo(p, PRECOMP_NAME)!!, a, mu)
    }

    /**
     * Multiplies a [org.spongycastle.math.ec.ECPointAbstractF2m]
     * by an element `` of `**Z**[]` using
     * the ``-adic NAF (TNAF) method.
     * @param p The org.spongycastle.math.ec.ECPointAbstractF2m to multiply.
     * @param lambda The element `` of
     * `**Z**[]` of which to compute the
     * `[]`-adic NAF.
     * @return `p` multiplied by ``.
     */
    private fun multiplyWTnaf(
        p: ECPointAbstractF2m, lambda: ZTauElement,
        preCompInfo: PreCompInfo, a: Byte, mu: Byte
    ): ECPointAbstractF2m {
        val alpha = if (a.toInt() == 0) Tnaf.alpha0 else Tnaf.alpha1
        val tw = Tnaf.getTw(mu, Tnaf.WIDTH.toInt())
        val u = Tnaf.tauAdicWNaf(
            mu, lambda, Tnaf.WIDTH,
            BigInteger.fromLong(Tnaf.POW_2_WIDTH.toLong()), tw, alpha
        )
        return multiplyFromWTnaf(p, u, preCompInfo)
    }

    companion object {
        // TODO Create WTauNafUtil class and move various functionality into it
        const val PRECOMP_NAME = "bc_wtnaf"

        /**
         * Multiplies a [org.spongycastle.math.ec.ECPointAbstractF2m]
         * by an element `` of `**Z**[]`
         * using the window ``-adic NAF (TNAF) method, given the
         * WTNAF of ``.
         * @param p The org.spongycastle.math.ec.ECPointAbstractF2m to multiply.
         * @param u The the WTNAF of ``..
         * @return ` * p`
         */
        private fun multiplyFromWTnaf(
            p: ECPointAbstractF2m,
            u: ByteArray,
            preCompInfo: PreCompInfo
        ): ECPointAbstractF2m {
            val curve = p.getCurve() as CurveAbstractF2m
            val a = curve.getCA().toBigInteger().byteValue()
            val pu: Array<ECPointAbstractF2m>?
            if (preCompInfo == null || preCompInfo !is WTauNafPreCompInfo) {
                pu = Tnaf.getPreComp(p, a)
                val pre = WTauNafPreCompInfo()
                pre.preComp = pu
                curve.setPreCompInfo(p, PRECOMP_NAME, pre)
            } else {
                pu = preCompInfo.preComp
            }

            // TODO Include negations in precomp (optionally) and use from here
            val puNeg = arrayOfNulls<ECPointAbstractF2m>(
                pu!!.size
            )
            for (i in pu.indices) {
                puNeg[i] = pu[i].negate() as ECPointAbstractF2m
            }


            // q = infinity
            var q = p.getCurve().getNewInfinity() as ECPointAbstractF2m
            var tauCount = 0
            for (i in u.indices.reversed()) {
                ++tauCount
                val ui = u[i].toInt()
                if (ui != 0) {
                    q = q.tauPow(tauCount)
                    tauCount = 0
                    val x: ECPoint = if (ui > 0) pu[ui ushr 1] else puNeg[-ui ushr 1]!!
                    q = q.add(x) as ECPointAbstractF2m
                }
            }
            if (tauCount > 0) {
                q = q.tauPow(tauCount)
            }
            return q
        }
    }
}
