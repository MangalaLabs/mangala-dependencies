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
 * Definite Length TaggedObject - in ASN.1 notation this is any object preceded by
 * a [n] where n is some number - these are assumed to follow the construction
 * rules (as with sequences).
 */
class DLTaggedObject
/**
 * @param explicit true if an explicitly tagged object.
 * @param tagNo the tag number for this object.
 * @param obj the tagged object.
 */
    (
    explicit: Boolean,
    tagNo: Int,
    obj: ASN1Encodable
) : ASN1TaggedObject(explicit, tagNo, obj) {
    override fun isConstructed(): Boolean {
        return if (!isEmpty()) {
            if (isExplicit()) {
                true
            } else {
                val primitive = obj!!.toASN1Primitive().toDLObject()
                primitive!!.isConstructed()
            }
        } else {
            true
        }
    }

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        return if (!isEmpty()) {
            var length = obj!!.toASN1Primitive().toDLObject()!!.encodedLength()
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
        if (!isEmpty()) {
            val primitive = obj!!.toASN1Primitive().toDLObject()
            if (isExplicit()) {
                out.writeTag(BERTags.CONSTRUCTED or BERTags.TAGGED, tagNo)
                out.writeLength(primitive!!.encodedLength())
                out.writeObject(primitive)
            } else {
                //
                // need to mark constructed types...
                //
                val flags: Int
                flags = if (primitive!!.isConstructed()) {
                    BERTags.CONSTRUCTED or BERTags.TAGGED
                } else {
                    BERTags.TAGGED
                }
                out.writeTag(flags, tagNo)
                out.writeImplicitObject(primitive)
            }
        } else {
            out.writeEncoded(BERTags.CONSTRUCTED or BERTags.TAGGED, tagNo, ZERO_BYTES)
        }
    }

    companion object {
        private val ZERO_BYTES = ByteArray(0)
    }
}
