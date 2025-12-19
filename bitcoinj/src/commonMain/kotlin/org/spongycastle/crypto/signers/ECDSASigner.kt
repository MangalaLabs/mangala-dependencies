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

package org.spongycastle.crypto.signers//package org.spongycastle.crypto.signers
//
//import com.ionspin.kotlin.bignum.integer.BigInteger
//import com.ionspin.kotlin.bignum.integer.Sign
//import com.mangala.wallet.utils.shiftRight
//import org.spongycastle.crypto.CipherParameters
//import org.spongycastle.crypto.DSA
//import org.spongycastle.crypto.params.ECKeyParameters
//import org.spongycastle.crypto.params.ECPrivateKeyParameters
//import org.spongycastle.crypto.params.ECPublicKeyParameters
//import org.spongycastle.crypto.params.ParametersWithRandom
//import org.spongycastle.math.ec.ECAlgorithms
//import org.spongycastle.math.ec.ECConstants
//import org.spongycastle.math.ec.ECCurve
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.ECMultiplier
//import org.spongycastle.math.ec.ECPoint
//import org.spongycastle.math.ec.FixedPointCombMultiplier
//import java.security.SecureRandom
//
///**
// * EC-DSA as described in X9.62
// * EC-DSA as described in X9.62
// */
//class ECDSASigner: DSA {
//    private val kCalculator: DSAKCalculator
//    private var key: ECKeyParameters? = null
//    private var random: SecureRandom? = null
//
//    /**
//     * Default configuration, random K values.
//     */
//    constructor() {
//        kCalculator = RandomDSAKCalculator()
//    }
//
//    /**
//     * Configuration with an alternate, possibly deterministic calculator of K.
//     *
//     * @param kCalculator a K value calculator.
//     */
//    constructor(kCalculator: DSAKCalculator) {
//        this.kCalculator = kCalculator
//    }
//
//    override fun init(
//        forSigning: Boolean,
//        param: CipherParameters
//    ) {
//        var providedRandom: SecureRandom? = null
//        if (forSigning) {
//            if (param is ParametersWithRandom) {
//                val rParam = param
//                key = rParam.parameters as ECPrivateKeyParameters
//                providedRandom = rParam.random
//            } else {
//                key = param as ECPrivateKeyParameters
//            }
//        } else {
//            key = param as ECPublicKeyParameters
//        }
//        random = initSecureRandom(forSigning && !kCalculator.isDeterministic(), providedRandom)
//    }
//    // 5.3 pg 28
//    /**
//     * generate a signature for the given message using the key we were
//     * initialised with. For conventional DSA the message should be a SHA-1
//     * hash of the message of interest.
//     *
//     * @param message the message that will be verified later.
//     */
//    override fun generateSignature(
//        message: ByteArray
//    ): Array<BigInteger> {
//        val ec = key!!.parameters
//        val n = ec.n
//        val e = calculateE(n, message)
//        val d = (key as ECPrivateKeyParameters?)!!.d
//        if (kCalculator.isDeterministic()) {
//            kCalculator.init(n, d, message)
//        } else {
//            random?.let {
//                kCalculator.init(n, it)
//            }
//        }
//        var r: BigInteger
//        var s: BigInteger
//        val basePointMultiplier = createBasePointMultiplier()
//
//        // 5.3.2
//        do  // generate s
//        {
//            var k: BigInteger
//            do  // generate r
//            {
//                k = kCalculator.nextK()
//                val p = basePointMultiplier.multiply(ec.g, k).normalize()
//
//                // 5.3.3
//                r = p.affineXCoord!!.toBigInteger().mod(n)
//            } while (r == ECConstants.ZERO)
//            s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n)
//        } while (s == ECConstants.ZERO)
//        return arrayOf(r, s)
//    }
//    // 5.4 pg 29
//    /**
//     * return true if the value r and s represent a DSA signature for
//     * the passed in message (for standard DSA the message should be
//     * a SHA-1 hash of the real message to be verified).
//     */
//    override fun verifySignature(
//        message: ByteArray,
//        r: BigInteger,
//        s: BigInteger
//    ): Boolean {
//        var r = r
//        val ec = key!!.parameters
//        val n = ec.n
//        val e = calculateE(n, message)
//
//        // r in the range [1,n-1]
//        if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0) {
//            return false
//        }
//
//        // s in the range [1,n-1]
//        if (s.compareTo(ECConstants.ONE) < 0 || s.compareTo(n) >= 0) {
//            return false
//        }
//        val c = s.modInverse(n)
//        val u1 = e.multiply(c).mod(n)
//        val u2 = r.multiply(c).mod(n)
//        val G = ec.g
//        val Q = (key as ECPublicKeyParameters?)!!.q!!
//        val point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q, u2)
//
//        // components must be bogus.
//        if (point.isInfinity) {
//            return false
//        }
//
//        /*
//         * If possible, avoid normalizing the point (to save a modular inversion in the curve field).
//         *
//         * There are ~cofactor elements of the curve field that reduce (modulo the group order) to 'r'.
//         * If the cofactor is known and small, we generate those possible field values and project each
//         * of them to the same "denominator" (depending on the particular projective coordinates in use)
//         * as the calculated point.X. If any of the projected values matches point.X, then we have:
//         *     (point.X / Denominator mod p) mod n == r
//         * as required, and verification succeeds.
//         *
//         * Based on an original idea by Gregory Maxwell (https://github.com/gmaxwell), as implemented in
//         * the libsecp256k1 project (https://github.com/bitcoin/secp256k1).
//         */
//        val curve = point.getCurve()
//        if (curve != null) {
//            val cofactor = curve.getCCofactor()
//            if (cofactor != null && cofactor.compareTo(ECConstants.EIGHT) <= 0) {
//                val D = getDenominator(curve.getCoordinateSystem(), point)
//                if (D != null && !D.isZero()) {
//                    val X = point.xCoord
//                    while (curve.isValidFieldElement(r)) {
//                        val R = curve.fromBigInteger(r).multiply(D)
//                        if (R == X) {
//                            return true
//                        }
//                        r = r.add(n)
//                    }
//                    return false
//                }
//            }
//        }
//        val v = point.normalize().affineXCoord!!.toBigInteger().mod(n)
//        return v == r
//    }
//
//    protected fun calculateE(n: BigInteger, message: ByteArray): BigInteger {
//        val log2n = n.bitLength()
//        val messageBitLength = message.size * 8
//        var e = BigInteger.fromByteArray(message, Sign.POSITIVE)
//        if (log2n < messageBitLength) {
//            e = e.shiftRight(messageBitLength - log2n)
//        }
//        return e
//    }
//
//    protected fun createBasePointMultiplier(): ECMultiplier {
//        return FixedPointCombMultiplier()
//    }
//
//    protected fun getDenominator(coordinateSystem: Int, p: ECPoint): ECFieldElement? {
//        return when (coordinateSystem) {
//            ECCurve.COORD_HOMOGENEOUS, ECCurve.COORD_LAMBDA_PROJECTIVE, ECCurve.COORD_SKEWED -> p.getZCoord(
//                0
//            )
//
//            ECCurve.COORD_JACOBIAN, ECCurve.COORD_JACOBIAN_CHUDNOVSKY, ECCurve.COORD_JACOBIAN_MODIFIED -> p.getZCoord(
//                0
//            )?.square()
//
//            else -> null
//        }
//    }
//
//    protected fun initSecureRandom(needed: Boolean, provided: SecureRandom?): SecureRandom? {
//        return if (!needed) null else provided ?: SecureRandom()
//    }
//}
