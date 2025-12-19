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
 * BER TaggedObject - in ASN.1 notation this is any object preceded by
 * a [n] where n is some number - these are assumed to follow the construction
 * rules (as with sequences).
 */
class BERTaggedObject : ASN1TaggedObject {
    /**
     * @param tagNo the tag number for this object.
     * @param obj the tagged object.
     */
    constructor(
        tagNo: Int,
        obj: ASN1Encodable
    ) : super(true, tagNo, obj)

    /**
     * @param explicit true if an explicitly tagged object.
     * @param tagNo the tag number for this object.
     * @param obj the tagged object.
     */
    constructor(
        explicit: Boolean,
        tagNo: Int,
        obj: ASN1Encodable
    ) : super(explicit, tagNo, obj)

    /**
     * create an implicitly tagged object that contains a zero
     * length sequence.
     */
    constructor(
        tagNo: Int
    ) : super(false, tagNo, BERSequence())

    override fun isConstructed(): Boolean {
        return if (!isEmpty()) {
            if (isExplicit()) {
                true
            } else {
                val primitive = obj!!.toASN1Primitive().toDERObject()
                primitive!!.isConstructed()
            }
        } else {
            true
        }
    }

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        return if (!isEmpty()) {
            val primitive = obj!!.toASN1Primitive()
            var length = primitive.encodedLength()
            if (isExplicit()) {
                StreamUtil.calculateTagLength(tagNo) + StreamUtil.calculateBodyLength(
                    length
                ) + length
            } else {
                // header length already in calculation
                length = length - 1
                StreamUtil.calculateTagLength(tagNo) + length
            }
        } else {
            StreamUtil.calculateTagLength(tagNo) + 1
        }
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.writeTag(BERTags.CONSTRUCTED or BERTags.TAGGED, tagNo)
        out.write(0x80)
        if (!isEmpty()) {
            if (!isExplicit()) {
                val e: Iterator<*>
                e = if (obj is ASN1OctetString) {
                    if (obj is BEROctetString) {
                        (obj as BEROctetString).objects
                    } else {
                        val octs = obj as ASN1OctetString
                        val berO = BEROctetString(octs.octets)
                        berO.objects
                    }
                } else if (obj is ASN1Sequence) {
                    (obj as ASN1Sequence).objects
                } else if (obj is ASN1Set) {
                    (obj as ASN1Set).objects
                } else {
                    throw ASN1Exception("not implemented: " + obj!!::class.simpleName)
                }
                while (e.hasNext()) {
                    out.writeObject(e.next() as ASN1Encodable)
                }
            } else {
                out.writeObject(obj)
            }
        }
        out.write(0x00)
        out.write(0x00)
    }
}
