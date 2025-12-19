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
import com.mangala.wallet.bitcoinj.utils.clearBit
import com.mangala.wallet.bitcoinj.utils.shiftLeft
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.testBit
import com.mangala.wallet.bitcoinj.utils.toInt
import kotlin.jvm.JvmStatic

/**
 * Class holding methods for point multiplication based on the window
 * -adic nonadjacent form (WTNAF). The algorithms are based on the
 * paper "Improved Algorithms for Arithmetic on Anomalous Binary Curves"
 * by Jerome A. Solinas. The paper first appeared in the Proceedings of
 * Crypto 1997.
 */
internal object Tnaf {
    private val MINUS_ONE = ECConstants.ONE.negate()
    private val MINUS_TWO = ECConstants.TWO.negate()
    private val MINUS_THREE = ECConstants.THREE.negate()

    /**
     * The window width of WTNAF. The standard value of 4 is slightly less
     * than optimal for running time, but keeps space requirements for
     * precomputation low. For typical curves, a value of 5 or 6 results in
     * a better running time. When changing this value, the
     * `<sub>u</sub>`'s must be computed differently, see
     * e.g. "Guide to Elliptic Curve Cryptography", Darrel Hankerson,
     * Alfred Menezes, Scott Vanstone, Springer-Verlag New York Inc., 2004,
     * p. 121-122
     */
    const val WIDTH: Byte = 4

    /**
     * 2<sup>4</sup>
     */
    const val POW_2_WIDTH: Byte = 16

    /**
     * The `<sub>u</sub>`'s for `a=0` as an array
     * of `ZTauElement`s.
     */
    val alpha0 = arrayOf(
        null,
        ZTauElement(ECConstants.ONE, ECConstants.ZERO), null,
        ZTauElement(MINUS_THREE, MINUS_ONE), null,
        ZTauElement(MINUS_ONE, MINUS_ONE), null,
        ZTauElement(ECConstants.ONE, MINUS_ONE), null
    )

    /**
     * The `<sub>u</sub>`'s for `a=0` as an array
     * of TNAFs.
     */
    val alpha0Tnaf = arrayOf(
        null,
        byteArrayOf(1),
        null,
        byteArrayOf(-1, 0, 1),
        null,
        byteArrayOf(1, 0, 1),
        null,
        byteArrayOf(-1, 0, 0, 1)
    )

    /**
     * The `<sub>u</sub>`'s for `a=1` as an array
     * of `ZTauElement`s.
     */
    val alpha1 = arrayOf(
        null,
        ZTauElement(ECConstants.ONE, ECConstants.ZERO), null,
        ZTauElement(MINUS_THREE, ECConstants.ONE), null,
        ZTauElement(MINUS_ONE, ECConstants.ONE), null,
        ZTauElement(ECConstants.ONE, ECConstants.ONE), null
    )

    /**
     * The `<sub>u</sub>`'s for `a=1` as an array
     * of TNAFs.
     */
    val alpha1Tnaf = arrayOf(
        null,
        byteArrayOf(1),
        null,
        byteArrayOf(-1, 0, 1),
        null,
        byteArrayOf(1, 0, 1),
        null,
        byteArrayOf(-1, 0, 0, -1)
    )

    /**
     * Computes the norm of an element `` of
     * `**Z**[]`.
     * @param mu The parameter `` of the elliptic curve.
     * @param lambda The element `` of
     * `**Z**[]`.
     * @return The norm of ``.
     */
    fun norm(mu: Byte, lambda: ZTauElement): BigInteger {
        val norm: BigInteger

        // s1 = u^2
        val s1 = lambda.u.multiply(lambda.u)

        // s2 = u * v
        val s2 = lambda.u.multiply(lambda.v)

        // s3 = 2 * v^2
        val s3 = lambda.v.multiply(lambda.v).shiftLeft(1)
        norm = if (mu.toInt() == 1) {
            s1.add(s2).add(s3)
        } else if (mu.toInt() == -1) {
            s1.subtract(s2).add(s3)
        } else {
            throw IllegalArgumentException("mu must be 1 or -1")
        }
        return norm
    }

    /**
     * Computes the norm of an element `` of
     * `**R**[]`, where ` = u + v`
     * and `u` and `u` are real numbers (elements of
     * `**R**`).
     * @param mu The parameter `` of the elliptic curve.
     * @param u The real part of the element `` of
     * `**R**[]`.
     * @param v The ``-adic part of the element
     * `` of `**R**[]`.
     * @return The norm of ``.
     */
    fun norm(
        mu: Byte, u: SimpleBigDecimal,
        v: SimpleBigDecimal
    ): SimpleBigDecimal {
        val norm: SimpleBigDecimal

        // s1 = u^2
        val s1 = u.multiply(u)

        // s2 = u * v
        val s2 = u.multiply(v)

        // s3 = 2 * v^2
        val s3 = v.multiply(v).shiftLeft(1)
        norm = if (mu.toInt() == 1) {
            s1.add(s2).add(s3)
        } else if (mu.toInt() == -1) {
            s1.subtract(s2).add(s3)
        } else {
            throw IllegalArgumentException("mu must be 1 or -1")
        }
        return norm
    }

    /**
     * Rounds an element `` of `**R**[]`
     * to an element of `**Z**[]`, such that their difference
     * has minimal norm. `` is given as
     * ` = <sub>0</sub> + <sub>1</sub>`.
     * @param lambda0 The component `<sub>0</sub>`.
     * @param lambda1 The component `<sub>1</sub>`.
     * @param mu The parameter `` of the elliptic curve. Must
     * equal 1 or -1.
     * @return The rounded element of `**Z**[]`.
     * @throws IllegalArgumentException if `lambda0` and
     * `lambda1` do not have same scale.
     */
    fun round(
        lambda0: SimpleBigDecimal,
        lambda1: SimpleBigDecimal, mu: Byte
    ): ZTauElement {
        val scale = lambda0.scale
        require(lambda1.scale == scale) {
            "lambda0 and lambda1 do not " +
                    "have same scale"
        }
        require(mu.toInt() == 1 || mu.toInt() == -1) { "mu must be 1 or -1" }
        val f0 = lambda0.round()
        val f1 = lambda1.round()
        val eta0 = lambda0.subtract(f0)
        val eta1 = lambda1.subtract(f1)

        // eta = 2*eta0 + mu*eta1
        var eta = eta0.add(eta0)
        eta = if (mu.toInt() == 1) {
            eta.add(eta1)
        } else {
            // mu == -1
            eta.subtract(eta1)
        }

        // check1 = eta0 - 3*mu*eta1
        // check2 = eta0 + 4*mu*eta1
        val threeEta1 = eta1.add(eta1).add(eta1)
        val fourEta1 = threeEta1.add(eta1)
        val check1: SimpleBigDecimal
        val check2: SimpleBigDecimal
        if (mu.toInt() == 1) {
            check1 = eta0.subtract(threeEta1)
            check2 = eta0.add(fourEta1)
        } else {
            // mu == -1
            check1 = eta0.add(threeEta1)
            check2 = eta0.subtract(fourEta1)
        }
        var h0: Byte = 0
        var h1: Byte = 0

        // if eta >= 1
        if (eta.compareTo(ECConstants.ONE) >= 0) {
            if (check1.compareTo(MINUS_ONE) < 0) {
                h1 = mu
            } else {
                h0 = 1
            }
        } else {
            // eta < 1
            if (check2.compareTo(ECConstants.TWO) >= 0) {
                h1 = mu
            }
        }

        // if eta < -1
        if (eta.compareTo(MINUS_ONE) < 0) {
            if (check1.compareTo(ECConstants.ONE) >= 0) {
                h1 = (-mu).toByte()
            } else {
                h0 = -1
            }
        } else {
            // eta >= -1
            if (check2.compareTo(MINUS_TWO) < 0) {
                h1 = (-mu).toByte()
            }
        }
        val q0 = f0.add(BigInteger.fromLong(h0.toLong()))
        val q1 = f1.add(BigInteger.fromLong(h1.toLong()))
        return ZTauElement(q0, q1)
    }

    /**
     * Approximate division by `n`. For an integer
     * `k`, the value ` = s k / n` is
     * computed to `c` bits of accuracy.
     * @param k The parameter `k`.
     * @param s The curve parameter `s<sub>0</sub>` or
     * `s<sub>1</sub>`.
     * @param vm The Lucas Sequence element `V<sub>m</sub>`.
     * @param a The parameter `a` of the elliptic curve.
     * @param m The bit length of the finite field
     * `**F**<sub>m</sub>`.
     * @param c The number of bits of accuracy, i.e. the scale of the returned
     * `SimpleBigDecimal`.
     * @return The value ` = s k / n` computed to
     * `c` bits of accuracy.
     */
    fun approximateDivisionByN(
        k: BigInteger,
        s: BigInteger, vm: BigInteger, a: Byte, m: Int, c: Int
    ): SimpleBigDecimal {
        val _k = (m + 5) / 2 + c
        val ns = k.shiftRight(m - _k - 2 + a)
        val gs = s.multiply(ns)
        val hs = gs.shiftRight(m)
        val js = vm.multiply(hs)
        val gsPlusJs = gs.add(js)
        var ls = gsPlusJs.shiftRight(_k - c)
        if (gsPlusJs.testBit(_k - c - 1)) {
            // round up
            ls = ls.add(ECConstants.ONE)
        }
        return SimpleBigDecimal(ls, c)
    }

    /**
     * Computes the ``-adic NAF (non-adjacent form) of an
     * element `` of `**Z**[]`.
     * @param mu The parameter `` of the elliptic curve.
     * @param lambda The element `` of
     * `**Z**[]`.
     * @return The ``-adic NAF of ``.
     */
    fun tauAdicNaf(mu: Byte, lambda: ZTauElement): ByteArray {
        require(mu.toInt() == 1 || mu.toInt() == -1) { "mu must be 1 or -1" }
        val norm = norm(mu, lambda)

        // Ceiling of log2 of the norm 
        val log2Norm = norm.bitLength()

        // If length(TNAF) > 30, then length(TNAF) < log2Norm + 3.52
        val maxLength = if (log2Norm > 30) log2Norm + 4 else 34

        // The array holding the TNAF
        val u = ByteArray(maxLength)
        var i = 0

        // The actual length of the TNAF
        var length = 0
        var r0 = lambda.u
        var r1 = lambda.v
        while (!(r0 == ECConstants.ZERO && r1 == ECConstants.ZERO)) {
            // If r0 is odd
            if (r0.testBit(0)) {
                u[i] = ECConstants.TWO.subtract(r0.subtract(r1.shiftLeft(1)).mod(ECConstants.FOUR))
                    .toInt().toByte()

                // r0 = r0 - u[i]
                r0 = if (u[i].toInt() == 1) {
//                    java.math.BigInteger.ZERO.clearBit(0)
                    r0.clearBit(0)
                } else {
                    // u[i] == -1
                    r0.add(ECConstants.ONE)
                }
                length = i
            } else {
                u[i] = 0
            }
            val t = r0
            val s = r0.shiftRight(1)
            r0 = if (mu.toInt() == 1) {
                r1.add(s)
            } else {
                // mu == -1
                r1.subtract(s)
            }
            r1 = t.shiftRight(1).negate()
            i++
        }
        length++

        // Reduce the TNAF array to its actual length
        val tnaf = ByteArray(length)
        u.copyInto(destination = tnaf, destinationOffset = 0, startIndex = 0, endIndex = length)
        return tnaf
    }

    /**
     * Applies the operation `()` to an
     * `org.spongycastle.math.ec.ECPointAbstractF2m`.
     * @param p The org.spongycastle.math.ec.ECPointAbstractF2m to which `()` is applied.
     * @return `(p)`
     */
    fun tau(p: ECPointAbstractF2m): ECPointAbstractF2m {
        return p.tau()
    }

    /**
     * Returns the parameter `` of the elliptic curve.
     * @param curve The elliptic curve from which to obtain ``.
     * The curve must be a Koblitz curve, i.e. `a` equals
     * `0` or `1` and `b` equals
     * `1`.
     * @return `` of the elliptic curve.
     * @throws IllegalArgumentException if the given ECCurve is not a Koblitz
     * curve.
     */
    fun getMu(curve: CurveAbstractF2m): Byte {
        require(curve.isKoblitz()) { "No Koblitz curve (ABC), TNAF multiplication not possible" }
        return if (curve.getCA().isZero()) {
            -1
        } else 1
    }

    fun getMu(curveA: ECFieldElement): Byte {
        return (if (curveA.isZero()) -1 else 1).toByte()
    }

    fun getMu(curveA: Int): Byte {
        return (if (curveA == 0) -1 else 1).toByte()
    }

    /**
     * Calculates the Lucas Sequence elements `U<sub>k-1</sub>` and
     * `U<sub>k</sub>` or `V<sub>k-1</sub>` and
     * `V<sub>k</sub>`.
     * @param mu The parameter `` of the elliptic curve.
     * @param k The index of the second element of the Lucas Sequence to be
     * returned.
     * @param doV If set to true, computes `V<sub>k-1</sub>` and
     * `V<sub>k</sub>`, otherwise `U<sub>k-1</sub>` and
     * `U<sub>k</sub>`.
     * @return An array with 2 elements, containing `U<sub>k-1</sub>`
     * and `U<sub>k</sub>` or `V<sub>k-1</sub>`
     * and `V<sub>k</sub>`.
     */
    fun getLucas(
        mu: Byte,
        k: Int,
        doV: Boolean
    ): Array<BigInteger> {
        require(mu.toInt() == 1 || mu.toInt() == -1) { "mu must be 1 or -1" }
        var u0: BigInteger
        var u1: BigInteger
        var u2: BigInteger
        if (doV) {
            u0 = ECConstants.TWO
            u1 = BigInteger.fromLong(mu.toLong())
        } else {
            u0 = ECConstants.ZERO
            u1 = ECConstants.ONE
        }
        for (i in 1 until k) {
            // u2 = mu*u1 - 2*u0;
            var s: BigInteger? = null
            s = if (mu.toInt() == 1) {
                u1
            } else {
                // mu == -1
                u1.negate()
            }
            u2 = s!!.subtract(u0.shiftLeft(1))
            u0 = u1
            u1 = u2
            //            System.out.println(i + ": " + u2);
//            System.out.println();
        }
        return arrayOf(u0, u1)
    }

    /**
     * Computes the auxiliary value `t<sub>w</sub>`. If the width is
     * 4, then for `mu = 1`, `t<sub>w</sub> = 6` and for
     * `mu = -1`, `t<sub>w</sub> = 10`
     * @param mu The parameter `` of the elliptic curve.
     * @param w The window width of the WTNAF.
     * @return the auxiliary value `t<sub>w</sub>`
     */
    fun getTw(mu: Byte, w: Int): BigInteger {
        return if (w == 4) {
            if (mu.toInt() == 1) {
                BigInteger.fromInt(6)
            } else {
                // mu == -1
                BigInteger.fromInt(10)
            }
        } else {
            // For w <> 4, the values must be computed
            val us = getLucas(mu, w, false)
//            java.math.BigInteger.ZERO.setBit(2)
            val twoToW = ECConstants.ZERO.setBitAt(w.toLong(), true)
            val u1invert = us[1].modInverse(twoToW)
            val tw: BigInteger
            tw = ECConstants.TWO.multiply(us[0]).multiply(u1invert).mod(twoToW)
            //            System.out.println("mu = " + mu);
//            System.out.println("tw = " + tw);
            tw
        }
    }

    /**
     * Computes the auxiliary values `s<sub>0</sub>` and
     * `s<sub>1</sub>` used for partial modular reduction.
     * @param curve The elliptic curve for which to compute
     * `s<sub>0</sub>` and `s<sub>1</sub>`.
     * @throws IllegalArgumentException if `curve` is not a
     * Koblitz curve (Anomalous Binary Curve, ABC).
     */
    @JvmStatic
    fun getSi(curve: CurveAbstractF2m): Array<BigInteger> {
        require(curve.isKoblitz()) { "si is defined for Koblitz curves only" }
        val m = curve.getFieldSize()
        val a = curve.getCA().toBigInteger().toInt()
        val mu = getMu(a)
        val shifts = getShiftsForCofactor(curve.getCCofactor())
        val index = m + 3 - a
        val ui = getLucas(mu, index, false)
        if (mu.toInt() == 1) {
            ui[0] = ui[0].negate()
            ui[1] = ui[1].negate()
        }
        val dividend0 = ECConstants.ONE.add(ui[1]).shiftRight(shifts)
        val dividend1 = ECConstants.ONE.add(ui[0]).shiftRight(shifts).negate()
        return arrayOf(dividend0, dividend1)
    }

    fun getSi(fieldSize: Int, curveA: Int, cofactor: BigInteger?): Array<BigInteger> {
        val mu = getMu(curveA)
        val shifts = getShiftsForCofactor(cofactor)
        val index = fieldSize + 3 - curveA
        val ui = getLucas(mu, index, false)
        if (mu.toInt() == 1) {
            ui[0] = ui[0].negate()
            ui[1] = ui[1].negate()
        }
        val dividend0 = ECConstants.ONE.add(ui[1]).shiftRight(shifts)
        val dividend1 = ECConstants.ONE.add(ui[0]).shiftRight(shifts).negate()
        return arrayOf(dividend0, dividend1)
    }

    internal fun getShiftsForCofactor(h: BigInteger?): Int {
        if (h != null) {
            if (h == ECConstants.TWO) {
                return 1
            }
            if (h == ECConstants.FOUR) {
                return 2
            }
        }
        throw IllegalArgumentException("h (Cofactor) must be 2 or 4")
    }

    /**
     * Partial modular reduction modulo
     * `(<sup>m</sup> - 1)/( - 1)`.
     * @param k The integer to be reduced.
     * @param m The bitlength of the underlying finite field.
     * @param a The parameter `a` of the elliptic curve.
     * @param s The auxiliary values `s<sub>0</sub>` and
     * `s<sub>1</sub>`.
     * @param mu The parameter  of the elliptic curve.
     * @param c The precision (number of bits of accuracy) of the partial
     * modular reduction.
     * @return ` := k partmod (<sup>m</sup> - 1)/( - 1)`
     */
    fun partModReduction(
        k: BigInteger, m: Int, a: Byte,
        s: Array<BigInteger>, mu: Byte, c: Byte
    ): ZTauElement {
        // d0 = s[0] + mu*s[1]; mu is either 1 or -1
        val d0: BigInteger
        d0 = if (mu.toInt() == 1) {
            s[0].add(s[1])
        } else {
            s[0].subtract(s[1])
        }
        val v = getLucas(mu, m, true)
        val vm = v[1]
        val lambda0 = approximateDivisionByN(
            k, s[0], vm, a, m, c.toInt()
        )
        val lambda1 = approximateDivisionByN(
            k, s[1], vm, a, m, c.toInt()
        )
        val q = round(lambda0, lambda1, mu)

        // r0 = n - d0*q0 - 2*s1*q1
        val r0 = k.subtract(d0.multiply(q.u)).subtract(
            BigInteger.fromInt(2).multiply(s[1]).multiply(q.v)
        )

        // r1 = s1*q0 - s0*q1
        val r1 = s[1].multiply(q.u).subtract(s[0].multiply(q.v))
        return ZTauElement(r0, r1)
    }

    /**
     * Multiplies a [org.spongycastle.math.ec.ECPointAbstractF2m]
     * by a `BigInteger` using the reduced ``-adic
     * NAF (RTNAF) method.
     * @param p The org.spongycastle.math.ec.ECPointAbstractF2m to multiply.
     * @param k The `BigInteger` by which to multiply `p`.
     * @return `k * p`
     */
//    fun multiplyRTnaf(p: org.spongycastle.math.ec.ECPointAbstractF2m, k: BigInteger): org.spongycastle.math.ec.ECPointAbstractF2m {
//        val curve = p.getCurve() as CurveAbstractF2m
//        val m = curve.getFieldSize()
//        val a = curve.getCA().toBigInteger().toInt()
//        val mu = getMu(a)
//        val s = curve.si
//        val rho = partModReduction(k, m, a.toByte(), s!!, mu, 10.toByte())
//        return multiplyTnaf(p, rho)
//    }

    /**
     * Multiplies a [org.spongycastle.math.ec.ECPointAbstractF2m]
     * by an element `` of `**Z**[]`
     * using the ``-adic NAF (TNAF) method.
     * @param p The org.spongycastle.math.ec.ECPointAbstractF2m to multiply.
     * @param lambda The element `` of
     * `**Z**[]`.
     * @return ` * p`
     */
//    fun multiplyTnaf(
//        p: org.spongycastle.math.ec.ECPointAbstractF2m,
//        lambda: ZTauElement
//    ): org.spongycastle.math.ec.ECPointAbstractF2m {
//        val curve = p.getCurve() as CurveAbstractF2m
//        val mu = getMu(curve.getCA())
//        val u = tauAdicNaf(mu, lambda)
//        return multiplyFromTnaf(p, u)
//    }

    /**
     * Multiplies a [org.spongycastle.math.ec.ECPointAbstractF2m]
     * by an element `` of `**Z**[]`
     * using the ``-adic NAF (TNAF) method, given the TNAF
     * of ``.
     * @param p The org.spongycastle.math.ec.ECPointAbstractF2m to multiply.
     * @param u The the TNAF of ``..
     * @return ` * p`
     */
    fun multiplyFromTnaf(p: ECPointAbstractF2m, u: ByteArray?): ECPointAbstractF2m {
        val curve = p.getCurve()
        var q = curve?.getNewInfinity() as ECPointAbstractF2m
        val pNeg = p.negate() as ECPointAbstractF2m
        var tauCount = 0
        for (i in u!!.indices.reversed()) {
            ++tauCount
            val ui = u[i]
            if (ui.toInt() != 0) {
                q = q.tauPow(tauCount)
                tauCount = 0
                val x: ECPoint = if (ui > 0) p else pNeg
                q = q.add(x) as ECPointAbstractF2m
            }
        }
        if (tauCount > 0) {
            q = q.tauPow(tauCount)
        }
        return q
    }

    /**
     * Computes the `[]`-adic window NAF of an element
     * `` of `**Z**[]`.
     * @param mu The parameter  of the elliptic curve.
     * @param lambda The element `` of
     * `**Z**[]` of which to compute the
     * `[]`-adic NAF.
     * @param width The window width of the resulting WNAF.
     * @param pow2w 2<sup>width</sup>.
     * @param tw The auxiliary value `t<sub>w</sub>`.
     * @param alpha The `<sub>u</sub>`'s for the window width.
     * @return The `[]`-adic window NAF of
     * ``.
     */
    fun tauAdicWNaf(
        mu: Byte, lambda: ZTauElement,
        width: Byte, pow2w: BigInteger, tw: BigInteger?, alpha: Array<ZTauElement?>
    ): ByteArray {
        require(mu.toInt() == 1 || mu.toInt() == -1) { "mu must be 1 or -1" }
        val norm = norm(mu, lambda)

        // Ceiling of log2 of the norm 
        val log2Norm = norm.bitLength()

        // If length(TNAF) > 30, then length(TNAF) < log2Norm + 3.52
        val maxLength = if (log2Norm > 30) log2Norm + 4 + width else 34 + width

        // The array holding the TNAF
        val u = ByteArray(maxLength)

        // 2^(width - 1)
        val pow2wMin1 = pow2w.shiftRight(1)

        // Split lambda into two BigIntegers to simplify calculations
        var r0 = lambda.u
        var r1 = lambda.v
        var i = 0

        // while lambda <> (0, 0)
        while (!(r0 == ECConstants.ZERO && r1 == ECConstants.ZERO)) {
            // if r0 is odd
            if (r0.testBit(0)) {
                // uUnMod = r0 + r1*tw mod 2^width
                val uUnMod = r0.add(r1.multiply(tw!!)).mod(pow2w)
                var uLocal: Byte
                // if uUnMod >= 2^(width - 1)
                uLocal = if (uUnMod.compareTo(pow2wMin1) >= 0) {
                    uUnMod.subtract(pow2w).toInt().toByte()
                } else {
                    uUnMod.toInt().toByte()
                }
                // uLocal is now in [-2^(width-1), 2^(width-1)-1]
                u[i] = uLocal
                var s = true
                if (uLocal < 0) {
                    s = false
                    uLocal = (-uLocal).toByte()
                }
                // uLocal is now >= 0
                if (s) {
                    r0 = r0.subtract(alpha[uLocal.toInt()]?.u!!)
                    r1 = r1.subtract(alpha[uLocal.toInt()]?.v!!)
                } else {
                    r0 = r0.add(alpha[uLocal.toInt()]?.u!!)
                    r1 = r1.add(alpha[uLocal.toInt()]?.v!!)
                }
            } else {
                u[i] = 0
            }
            val t = r0
            r0 = if (mu.toInt() == 1) {
                r1.add(r0.shiftRight(1))
            } else {
                // mu == -1
                r1.subtract(r0.shiftRight(1))
            }
            r1 = t.shiftRight(1).negate()
            i++
        }
        return u
    }

    /**
     * Does the precomputation for WTNAF multiplication.
     * @param p The `ECPoint` for which to do the precomputation.
     * @param a The parameter `a` of the elliptic curve.
     * @return The precomputation array for `p`.
     */
//    fun getPreComp(p: org.spongycastle.math.ec.ECPointAbstractF2m, a: Byte): Array<org.spongycastle.math.ec.ECPointAbstractF2m> {
//        val alphaTnaf = if (a.toInt() == 0) alpha0Tnaf else alpha1Tnaf
//        val pu = arrayOfNulls<org.spongycastle.math.ec.ECPointAbstractF2m>(alphaTnaf.size + 1 ushr 1)
//        pu[0] = p
//        val precompLen = alphaTnaf.size
//        var i = 3
//        while (i < precompLen) {
//            pu[i ushr 1] = multiplyFromTnaf(p, alphaTnaf[i])
//            i += 2
//        }
//        p.getCurve().normalizeAll(pu)
//        return pu
//    }

    fun getPreComp(p: ECPointAbstractF2m, a: Byte): Array<ECPointAbstractF2m> {
        val alphaTnaf = if (a.toInt() == 0) alpha0Tnaf else alpha1Tnaf
        // Initialize the array with copies of `p`
        val pu = Array(alphaTnaf.size + 1 ushr 1) { p }
        pu[0] = p
        val precompLen = alphaTnaf.size
        var i = 3
        while (i < precompLen) {
            pu[i ushr 1] = multiplyFromTnaf(p, alphaTnaf[i])
            i += 2
        }
        p.getCurve()?.normalizeAll(pu as Array<ECPoint?>)
        return pu
    }
}
