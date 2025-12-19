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

package org.spongycastle.asn1

import okio.IOException

/**
 * Indefinite length SEQUENCE of objects.
 *
 *
 * Length field has value 0x80, and the sequence ends with two bytes of: 0x00, 0x00.
 *
 *
 * For X.690 syntax rules, see [ASN1Sequence].
 *
 */
class BERSequence : ASN1Sequence {
    /**
     * Create an empty sequence
     */
    constructor()

    /**
     * Create a sequence containing one object
     */
    constructor(
        obj: ASN1Encodable
    ) : super(obj)

    /**
     * Create a sequence containing a vector of objects.
     */
    constructor(
        v: ASN1EncodableVector
    ) : super(v)

    /**
     * Create a sequence containing an array of objects.
     */
    constructor(
        array: Array<ASN1Encodable>
    ) : super(array)

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        var length = 0
        val e: Iterator<ASN1Encodable> = objects
        while (e.hasNext()) {
            length += (e.next() as ASN1Encodable).toASN1Primitive().encodedLength()
        }
        return 2 + length + 2
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.write(BERTags.SEQUENCE or BERTags.CONSTRUCTED)
        out.write(0x80)
        val e: Iterator<ASN1Encodable> = objects
        while (e.hasNext()) {
            out.writeObject(e.next() as ASN1Encodable)
        }
        out.write(0x00)
        out.write(0x00)
    }
}
