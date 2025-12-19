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

import com.mangala.wallet.bitcoinj.utils.toInt
import org.spongycastle.asn1.ASN1EncodableVector
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.ASN1Object
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1OctetString
import org.spongycastle.asn1.ASN1Primitive
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.DERBitString
import org.spongycastle.asn1.DERSequence
import org.spongycastle.math.ec.CurveF2m
import org.spongycastle.math.ec.CurveFp
import org.spongycastle.math.ec.ECAlgorithms.isF2mCurve
import org.spongycastle.math.ec.ECAlgorithms.isFpCurve
import org.spongycastle.math.ec.ECCurve

/**
 * ASN.1 def for Elliptic-Curve Curve structure. See
 * X9.62, for further details.
 */
class X9Curve : ASN1Object, X9ObjectIdentifiers {
    var curve: ECCurve? = null
        private set
    var seed: ByteArray? = null
        private set
    private var fieldIdentifier: ASN1ObjectIdentifier? = null

    constructor(
        curve: ECCurve
    ) {
        this.curve = curve
        seed = null
        setFieldIdentifier()
    }

    constructor(
        curve: ECCurve,
        seed: ByteArray
    ) {
        this.curve = curve
        this.seed = seed
        setFieldIdentifier()
    }

    constructor(
        fieldID: X9FieldID,
        seq: ASN1Sequence
    ) {
        // TODO Is it possible to get the order(n) and cofactor(h) too?
        fieldIdentifier = fieldID.identifier
        if (fieldIdentifier!!.equals(X9ObjectIdentifiers.prime_field)) {
            val p = (fieldID.parameters as ASN1Integer).value
            val x9A = X9FieldElement(p, (seq.getObjectAt(0) as ASN1OctetString))
            val x9B = X9FieldElement(p, (seq.getObjectAt(1) as ASN1OctetString))
            curve = CurveFp(
                p,
                x9A.value.toBigInteger(),
                x9B.value.toBigInteger()
            )
        } else if (fieldIdentifier!!.equals(X9ObjectIdentifiers.characteristic_two_field)) {
            // Characteristic two field
            val parameters = ASN1Sequence.getInstance(fieldID.parameters)
            val m = (parameters.getObjectAt(0) as ASN1Integer).value.toInt()
            val representation = parameters.getObjectAt(1) as ASN1ObjectIdentifier
            var k1 = 0
            var k2 = 0
            var k3 = 0
            if (representation.equals(X9ObjectIdentifiers.tpBasis)) {
                // Trinomial basis representation
                k1 = ASN1Integer.getInstance(parameters.getObjectAt(2)).value.toInt()
            } else if (representation.equals(X9ObjectIdentifiers.ppBasis)) {
                // Pentanomial basis representation
                val pentanomial = ASN1Sequence.getInstance(parameters.getObjectAt(2))
                k1 = ASN1Integer.getInstance(pentanomial.getObjectAt(0)).value.toInt()
                k2 = ASN1Integer.getInstance(pentanomial.getObjectAt(1)).value.toInt()
                k3 = ASN1Integer.getInstance(pentanomial.getObjectAt(2)).value.toInt()
            } else {
                throw IllegalArgumentException("This type of EC basis is not implemented")
            }
            val x9A = X9FieldElement(m, k1, k2, k3, (seq.getObjectAt(0) as ASN1OctetString))
            val x9B = X9FieldElement(m, k1, k2, k3, (seq.getObjectAt(1) as ASN1OctetString))
            curve = CurveF2m(
                m,
                k1,
                k2,
                k3,
                x9A.value.toBigInteger(),
                x9B.value.toBigInteger()
            )
        } else {
            throw IllegalArgumentException("This type of ECCurve is not implemented")
        }
        if (seq.size() == 3) {
            seed = (seq.getObjectAt(2) as DERBitString).bytes
        }
    }

    private fun setFieldIdentifier() {
        fieldIdentifier = if (isFpCurve(curve!!)) {
            X9ObjectIdentifiers.prime_field
        } else if (isF2mCurve(curve!!)) {
            X9ObjectIdentifiers.characteristic_two_field
        } else {
            throw IllegalArgumentException("This type of ECCurve is not implemented")
        }
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * Curve ::= SEQUENCE {
     * a               FieldElement,
     * b               FieldElement,
     * seed            BIT STRING      OPTIONAL
     * }
    </pre> *
     */
    override fun toASN1Primitive(): ASN1Primitive {
        val v = ASN1EncodableVector()
        if (fieldIdentifier!!.equals(X9ObjectIdentifiers.prime_field)) {
            v.add(X9FieldElement(curve!!.getCA()).toASN1Primitive())
            v.add(X9FieldElement(curve!!.getCB()).toASN1Primitive())
        } else if (fieldIdentifier!!.equals(X9ObjectIdentifiers.characteristic_two_field)) {
            v.add(X9FieldElement(curve!!.getCA()).toASN1Primitive())
            v.add(X9FieldElement(curve!!.getCB()).toASN1Primitive())
        }
        if (seed != null) {
            v.add(DERBitString(seed!!))
        }
        return DERSequence(v)
    }
}
