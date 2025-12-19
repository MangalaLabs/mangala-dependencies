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

package org.spongycastle.asn1.x9

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.asn1.ASN1EncodableVector
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.ASN1Object
import org.spongycastle.asn1.ASN1OctetString
import org.spongycastle.asn1.ASN1Primitive
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.DERSequence
import org.spongycastle.math.ec.ECAlgorithms.isF2mCurve
import org.spongycastle.math.ec.ECAlgorithms.isFpCurve
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.math.field.PolynomialExtensionField
import kotlin.jvm.JvmOverloads

/**
 * ASN.1 def for Elliptic-Curve ECParameters structure. See
 * X9.62, for further details.
 */
class X9ECParameters : ASN1Object, X9ObjectIdentifiers {
    /**
     * Return the ASN.1 entry representing the FieldID.
     *
     * @return the X9FieldID for the FieldID in these parameters.
     */
    var fieldIDEntry: X9FieldID? = null
        private set
    var curve: ECCurve?
        private set

    /**
     * Return the ASN.1 entry representing the base point G.
     *
     * @return the X9ECPoint for the base point in these parameters.
     */
    var baseEntry: X9ECPoint? = null
        private set
    var n: BigInteger?
        private set
    var h: BigInteger? = null
        private set
    var seed: ByteArray?
        private set

    private constructor(
        seq: ASN1Sequence
    ) {
        require(
            !(seq.getObjectAt(0) !is ASN1Integer
                    || (seq.getObjectAt(0) as ASN1Integer).value != ONE)
        ) { "bad version in X9ECParameters" }
        val x9c = X9Curve(
            X9FieldID.getInstance(seq.getObjectAt(1)),
            ASN1Sequence.getInstance(seq.getObjectAt(2))
        )
        curve = x9c.curve
        val p: Any = seq.getObjectAt(3)
        if (p is X9ECPoint) {
            baseEntry = p
        } else {
            baseEntry = X9ECPoint(curve!!, (p as ASN1OctetString))
        }
        n = (seq.getObjectAt(4) as ASN1Integer).value
        seed = x9c.seed
        if (seq.size() == 6) {
            h = (seq.getObjectAt(5) as ASN1Integer).value
        }
    }

    @JvmOverloads
    constructor(
        curve: ECCurve,
        g: ECPoint?,
        n: BigInteger?,
        h: BigInteger? = null,
        seed: ByteArray? = null
    ) : this(curve, X9ECPoint(g!!), n, h, seed)

    @JvmOverloads
    constructor(
        curve: ECCurve,
        g: X9ECPoint?,
        n: BigInteger?,
        h: BigInteger?,
        seed: ByteArray? = null
    ) {
        this.curve = curve
        baseEntry = g
        this.n = n
        this.h = h
        this.seed = seed
        if (isFpCurve(curve)) {
            fieldIDEntry = X9FieldID(curve.getCField().getCharacteristic())
        } else if (isF2mCurve(curve)) {
            val field = curve.getCField() as PolynomialExtensionField
            val exponents = field.getMinimalPolynomial().getExponentsPresent()
            if (exponents.size == 3) {
                fieldIDEntry = X9FieldID(exponents[2], exponents[1])
            } else if (exponents.size == 5) {
                fieldIDEntry = X9FieldID(exponents[4], exponents[1], exponents[2], exponents[3])
            } else {
                throw IllegalArgumentException("Only trinomial and pentomial curves are supported")
            }
        } else {
            throw IllegalArgumentException("'curve' is of an unsupported type")
        }
    }

    fun getG(): ECPoint? {
        return baseEntry!!.point
    }

    val curveEntry: X9Curve
        /**
         * Return the ASN.1 entry representing the Curve.
         *
         * @return the X9Curve for the curve in these parameters.
         */
        get() = X9Curve(curve!!, seed!!)

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * ECParameters ::= SEQUENCE {
     * version         INTEGER { ecpVer1(1) } (ecpVer1),
     * fieldID         FieldID {{FieldTypes}},
     * curve           X9Curve,
     * base            X9ECPoint,
     * order           INTEGER,
     * cofactor        INTEGER OPTIONAL
     * }
    </pre> *
     */
    override fun toASN1Primitive(): ASN1Primitive {
        val v = ASN1EncodableVector()
        v.add(ASN1Integer(ONE))
        v.add(fieldIDEntry!!)
        v.add(X9Curve(curve!!, seed!!))
        v.add(baseEntry!!)
        v.add(ASN1Integer(n!!))
        if (h != null) {
            v.add(ASN1Integer(h!!))
        }
        return DERSequence(v)
    }

    companion object {
        private val ONE = BigInteger.fromInt(1)
        fun getInstance(obj: Any?): X9ECParameters? {
            if (obj is X9ECParameters) {
                return obj
            }
            return if (obj != null) {
                X9ECParameters(ASN1Sequence.getInstance(obj))
            } else null
        }
    }
}
