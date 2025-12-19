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

import com.mangala.MultiplatformByteArrayOutputStream
import org.spongycastle.util.encoders.Hex.toHexString
import okio.IOException

/**
 * A DER encoding version of an application specific object.
 */
class DERApplicationSpecific : ASN1ApplicationSpecific {
    internal constructor(
        isConstructed: Boolean,
        tag: Int,
        octets: ByteArray
    ) : super(isConstructed, tag, octets)

    /**
     * Create an application specific object from the passed in data. This will assume
     * the data does not represent a constructed object.
     *
     * @param tag the tag number for this object.
     * @param octets the encoding of the object's body.
     */
    constructor(
        tag: Int,
        octets: ByteArray
    ) : this(false, tag, octets)

    /**
     * Create an application specific object with a tagging of explicit/constructed.
     *
     * @param tag the tag number for this object.
     * @param object the object to be contained.
     */
    constructor(
        tag: Int,
        `object`: ASN1Encodable
    ) : this(true, tag, `object`)

    /**
     * Create an application specific object with the tagging style given by the value of constructed.
     *
     * @param constructed true if the object is constructed.
     * @param tag the tag number for this object.
     * @param `object` the object to be contained.
     */
    constructor(
        constructed: Boolean,
        tag: Int,
        obj: ASN1Encodable
    ) : super(
        constructed || obj.toASN1Primitive().isConstructed(),
        tag,
        getEncoding(constructed, obj)
    )

    /**
     * Create an application specific object which is marked as constructed
     *
     * @param tagNo the tag number for this object.
     * @param vec the objects making up the application specific object.
     */
    constructor(tagNo: Int, vec: ASN1EncodableVector) : super(true, tagNo, getEncodedVector(vec))

    /* (non-Javadoc)
     * @see org.spongycastle.asn1.ASN1Primitive#encode(org.spongycastle.asn1.DEROutputStream)
     */
    @Throws(IOException::class)
    override fun encode(out: ASN1OutputStream) {
        var classBits = BERTags.APPLICATION
        if (isConstructed) {
            classBits = classBits or BERTags.CONSTRUCTED
        }
        out.writeEncoded(classBits, getApplicationTag(), octets!!)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        if (isConstructed()) {
            sb.append("CONSTRUCTED ")
        }
        sb.append("APPLICATION ")
        sb.append(getApplicationTag())
        sb.append("]")
        // @todo content encoding somehow?
        if (octets != null) {
            sb.append(" #")
            sb.append(toHexString(octets))
        } else {
            sb.append(" #null")
        }
        sb.append(" ")
        return sb.toString()
    }

    companion object {
        @Throws(IOException::class)
        private fun getEncoding(explicit: Boolean, obj: ASN1Encodable): ByteArray {
            val data = obj.toASN1Primitive().getEncoded(ASN1Encoding.DER)
            return if (explicit) {
                data
            } else {
                val lenBytes = getLengthOfHeader(data)
                val tmp = ByteArray(data.size - lenBytes)
                data.copyInto(destination = tmp, destinationOffset = 0, startIndex = lenBytes, endIndex = lenBytes + tmp.size)
                tmp
            }
        }

        private fun getEncodedVector(vec: ASN1EncodableVector): ByteArray {
            val bOut = MultiplatformByteArrayOutputStream()
            for (i in 0 until vec.size()) {
                try {
                    bOut.write((vec[i] as ASN1Object).getEncoded(ASN1Encoding.DER))
                } catch (e: IOException) {
                    throw ASN1ParsingException("malformed object: $e", e)
                }
            }
            return bOut.toByteArray()
        }
    }
}
