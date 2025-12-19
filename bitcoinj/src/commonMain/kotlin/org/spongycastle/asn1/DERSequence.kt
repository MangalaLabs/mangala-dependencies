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
 * Definite length SEQUENCE, encoding tells explicit number of bytes
 * that the content of this sequence occupies.
 *
 *
 * For X.690 syntax rules, see [ASN1Sequence].
 */
class DERSequence : ASN1Sequence {
//    @get:Throws(IOException::class)
    private var bodyLength = -1
        private get() {
            if (field < 0) {
                var length = 0
                val e: Iterator<ASN1Encodable> = objects
                while (e.hasNext()) {
                    val obj = e.next()
                    length += (obj as ASN1Encodable).toASN1Primitive().toDERObject()!!
                        .encodedLength()
                }
                field = length
            }
            return field
        }

    /**
     * Create an empty sequence
     */
    constructor()

    /**
     * Create a sequence containing one object
     * @param obj the object to go in the sequence.
     */
    constructor(
        obj: ASN1Encodable
    ) : super(obj)

    /**
     * Create a sequence containing a vector of objects.
     * @param v the vector of objects to make up the sequence.
     */
    constructor(
        v: ASN1EncodableVector
    ) : super(v)

    /**
     * Create a sequence containing an array of objects.
     * @param array the array of objects to make up the sequence.
     */
    constructor(
        array: Array<ASN1Encodable>
    ) : super(array)

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        val length = bodyLength
        return 1 + StreamUtil.calculateBodyLength(length) + length
    }

    /*
     * A note on the implementation:
     * <p>
     * As DER requires the constructed, definite-length model to
     * be used for structured types, this varies slightly from the
     * ASN.1 descriptions given. Rather than just outputting SEQUENCE,
     * we also have to specify CONSTRUCTED, and the objects length.
     */
    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        val dOut = out.dERSubStream
        val length = bodyLength
        out.write(BERTags.SEQUENCE or BERTags.CONSTRUCTED)
        out.writeLength(length)
        val e: Iterator<ASN1Encodable> = objects
        while (e.hasNext()) {
            val obj = e.next()
            dOut!!.writeObject(obj as ASN1Encodable)
        }
    }
}
