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
import com.ionspin.kotlin.bignum.integer.Sign
import org.spongycastle.asn1.ASN1Object
import org.spongycastle.asn1.ASN1OctetString
import org.spongycastle.asn1.ASN1Primitive
import org.spongycastle.asn1.DEROctetString
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.EF2m
import org.spongycastle.math.ec.EFp


/**
 * class for processing an FieldElement as a DER object.
 */
class X9FieldElement(var value: ECFieldElement) : ASN1Object() {
    constructor(p: BigInteger, s: ASN1OctetString) : this(
        EFp(
            p,
            BigInteger.fromByteArray(s.octets, Sign.POSITIVE)
        )
    )

    constructor(m: Int, k1: Int, k2: Int, k3: Int, s: ASN1OctetString) : this(
        EF2m(
            m,
            k1,
            k2,
            k3,
            BigInteger.fromByteArray( s.octets, Sign.POSITIVE)
        )
    )

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * FieldElement ::= OCTET STRING
    </pre> *
     *
     *
     *
     *  1.  if *q* is an odd prime then the field element is
     * processed as an Integer and converted to an octet string
     * according to x 9.62 4.3.1.
     *  1.  if *q* is 2<sup>m</sup> then the bit string
     * contained in the field element is converted into an octet
     * string with the same ordering padded at the front if necessary.
     *
     *
     */
    override fun toASN1Primitive(): ASN1Primitive {
        val byteCount = converter.getByteLength(value)
        val paddedBigInteger = converter.integerToBytes(
            value.toBigInteger(), byteCount
        )
        return DEROctetString(paddedBigInteger)
    }

    companion object {
        private val converter = X9IntegerConverter()
    }
}
