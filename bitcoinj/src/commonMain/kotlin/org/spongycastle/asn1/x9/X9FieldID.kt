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
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1Primitive
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.DERSequence
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * ASN.1 def for Elliptic-Curve Field ID structure. See
 * X9.62, for further details.
 */
class X9FieldID : ASN1Object, X9ObjectIdentifiers {
    var identifier: ASN1ObjectIdentifier?
        private set
    var parameters: ASN1Primitive
        private set

    /**
     * Constructor for elliptic curves over prime fields
     * `F<sub>2</sub>`.
     * @param primeP The prime `p` defining the prime field.
     */
    constructor(primeP: BigInteger) {
        identifier = X9ObjectIdentifiers.prime_field
        parameters = ASN1Integer(primeP)
    }
    /**
     * Constructor for elliptic curves over binary fields
     * `F<sub>2<sup>m</sup></sub>`.
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
     * represents the reduction polynomial `f(z)`..
     */
    /**
     * Constructor for elliptic curves over binary fields
     * `F<sub>2<sup>m</sup></sub>`.
     * @param m  The exponent `m` of
     * `F<sub>2<sup>m</sup></sub>`.
     * @param k1 The integer `k1` where `x<sup>m</sup> +
     * x<sup>k1</sup> + 1`
     * represents the reduction polynomial `f(z)`.
     */
    @JvmOverloads
    constructor(m: Int, k1: Int, k2: Int = 0, k3: Int = 0) {
        identifier = X9ObjectIdentifiers.characteristic_two_field
        val fieldIdParams = ASN1EncodableVector()
        fieldIdParams.add(ASN1Integer(m.toLong()))
        if (k2 == 0) {
            require(k3 == 0) { "inconsistent k values" }
            fieldIdParams.add(X9ObjectIdentifiers.tpBasis)
            fieldIdParams.add(ASN1Integer(k1.toLong()))
        } else {
            require(!(k2 <= k1 || k3 <= k2)) { "inconsistent k values" }
            fieldIdParams.add(X9ObjectIdentifiers.ppBasis)
            val pentanomialParams = ASN1EncodableVector()
            pentanomialParams.add(ASN1Integer(k1.toLong()))
            pentanomialParams.add(ASN1Integer(k2.toLong()))
            pentanomialParams.add(ASN1Integer(k3.toLong()))
            fieldIdParams.add(DERSequence(pentanomialParams))
        }
        parameters = DERSequence(fieldIdParams)
    }

    private constructor(
        seq: ASN1Sequence
    ) {
        identifier = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0))
        parameters = seq.getObjectAt(1).toASN1Primitive()
    }

    /**
     * Produce a DER encoding of the following structure.
     * <pre>
     * FieldID ::= SEQUENCE {
     * fieldType       FIELD-ID.&amp;id({IOSet}),
     * parameters      FIELD-ID.&amp;Type({IOSet}{&#64;fieldType})
     * }
    </pre> *
     */
    override fun toASN1Primitive(): ASN1Primitive {
        val v = ASN1EncodableVector()
        v.add(identifier!!)
        v.add(parameters)
        return DERSequence(v)
    }

    companion object {
        @JvmStatic
        fun getInstance(obj: Any): X9FieldID {
            if (obj is X9FieldID) {
                return obj
            }
            return X9FieldID(ASN1Sequence.getInstance(obj))
        }
    }
}
