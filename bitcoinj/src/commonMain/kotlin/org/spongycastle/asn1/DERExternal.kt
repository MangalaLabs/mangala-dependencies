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
import okio.IOException

/**
 * Class representing the DER-type External
 */
class DERExternal : ASN1Primitive {
    /**
     * Returns the direct reference of the external element
     * @return The reference
     */
    /**
     * Sets the direct reference of the external element
     * @param directReferemce The reference
     */
    var directReference: ASN1ObjectIdentifier? = null
        /**
         * Sets the direct reference of the external element
         * @param directReferemce The reference
         */
        private set
    /**
     * Returns the indirect reference of this element
     * @return The reference
     */
    /**
     * Sets the indirect reference of this element
     * @param indirectReference The reference
     */
    var indirectReference: ASN1Integer? = null
        /**
         * Sets the indirect reference of this element
         * @param indirectReference The reference
         */
        private set
    /**
     * Returns the data value descriptor
     * @return The descriptor
     */
    /**
     * Sets the data value descriptor
     * @param dataValueDescriptor The descriptor
     */
    var dataValueDescriptor: ASN1Primitive? = null
        /**
         * Sets the data value descriptor
         * @param dataValueDescriptor The descriptor
         */
        private set
    private var encoding = 0

    /**
     * Returns the content of this element
     * @return The content
     */
    var externalContent: ASN1Primitive? = null
        private set

    /**
     * Construct a DER EXTERNAL object, the input encoding vector must have exactly two elements on it.
     *
     *
     * Acceptable input formats are:
     *
     *  *  [ASN1ObjectIdentifier] + data [DERTaggedObject] (direct reference form)
     *  *  [ASN1Integer] + data [DERTaggedObject] (indirect reference form)
     *  *  Anything but [DERTaggedObject] + data [DERTaggedObject] (data value form)
     *
     *
     *
     * @throws IllegalArgumentException if input size is wrong, or
     */
    constructor(vector: ASN1EncodableVector) {
        var offset = 0
        var enc = getObjFromVector(vector, offset)
        if (enc is ASN1ObjectIdentifier) {
            directReference = enc
            offset++
            enc = getObjFromVector(vector, offset)
        }
        if (enc is ASN1Integer) {
            indirectReference = enc
            offset++
            enc = getObjFromVector(vector, offset)
        }
        if (enc !is ASN1TaggedObject) {
            dataValueDescriptor = enc
            offset++
            enc = getObjFromVector(vector, offset)
        }
        require(vector.size() == offset + 1) { "input vector too large" }
        require(enc is ASN1TaggedObject) { "No tagged object found in vector. Structure doesn't seem to be of type External" }
        val obj = enc
        setEncoding(obj.tagNo())
        externalContent = obj.getObject()
    }

    private fun getObjFromVector(v: ASN1EncodableVector, index: Int): ASN1Primitive {
        require(v.size() > index) { "too few objects in input vector" }
        return v[index].toASN1Primitive()
    }

    /**
     * Creates a new instance of DERExternal
     * See X.690 for more informations about the meaning of these parameters
     * @param directReference The direct reference or `null` if not set.
     * @param indirectReference The indirect reference or `null` if not set.
     * @param dataValueDescriptor The data value descriptor or `null` if not set.
     * @param externalData The external data in its encoded form.
     */
    constructor(
        directReference: ASN1ObjectIdentifier,
        indirectReference: ASN1Integer,
        dataValueDescriptor: ASN1Primitive,
        externalData: DERTaggedObject
    ) : this(
        directReference,
        indirectReference,
        dataValueDescriptor,
        externalData.tagNo(),
        externalData.toASN1Primitive()
    )

    /**
     * Creates a new instance of DERExternal.
     * See X.690 for more informations about the meaning of these parameters
     * @param directReference The direct reference or `null` if not set.
     * @param indirectReference The indirect reference or `null` if not set.
     * @param dataValueDescriptor The data value descriptor or `null` if not set.
     * @param encoding The encoding to be used for the external data
     * @param externalData The external data
     */
    constructor(
        directReference: ASN1ObjectIdentifier,
        indirectReference: ASN1Integer,
        dataValueDescriptor: ASN1Primitive,
        encoding: Int,
        externalData: ASN1Primitive
    ) {
        this.directReference = directReference
        this.indirectReference = indirectReference
        this.dataValueDescriptor = dataValueDescriptor
        setEncoding(encoding)
        setExternalContent(externalData.toASN1Primitive())
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    override fun hashCode(): Int {
        var ret = 0
        if (directReference != null) {
            ret = directReference.hashCode()
        }
        if (indirectReference != null) {
            ret = ret xor indirectReference.hashCode()
        }
        if (dataValueDescriptor != null) {
            ret = ret xor dataValueDescriptor.hashCode()
        }
        ret = ret xor externalContent.hashCode()
        return ret
    }

    override fun isConstructed(): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        return encoded.size
    }

    /* (non-Javadoc)
     * @see org.spongycastle.asn1.ASN1Primitive#encode(org.spongycastle.asn1.DEROutputStream)
     */
    @Throws(IOException::class)
    override fun encode(out: ASN1OutputStream) {
        val baos = MultiplatformByteArrayOutputStream()
        if (directReference != null) {
            baos.write(directReference!!.getEncoded(ASN1Encoding.DER))
        }
        if (indirectReference != null) {
            baos.write(indirectReference!!.getEncoded(ASN1Encoding.DER))
        }
        if (dataValueDescriptor != null) {
            baos.write(dataValueDescriptor!!.getEncoded(ASN1Encoding.DER))
        }
        val obj = DERTaggedObject(true, encoding, externalContent!!)
        baos.write(obj.getEncoded(ASN1Encoding.DER))
        out.writeEncoded(BERTags.CONSTRUCTED, BERTags.EXTERNAL, baos.toByteArray())
    }

    /* (non-Javadoc)
     * @see org.spongycastle.asn1.ASN1Primitive#asn1Equals(org.spongycastle.asn1.ASN1Primitive)
     */
    override fun asn1Equals(o: ASN1Primitive): Boolean {
        if (o !is DERExternal) {
            return false
        }
        if (this === o) {
            return true
        }
        val other = o
        if (directReference != null) {
            if (other.directReference == null || !other.directReference!!.equals(directReference)) {
                return false
            }
        }
        if (indirectReference != null) {
            if (other.indirectReference == null || !other.indirectReference!!.equals(
                    indirectReference
                )
            ) {
                return false
            }
        }
        if (dataValueDescriptor != null) {
            if (other.dataValueDescriptor == null || !other.dataValueDescriptor!!.equals(
                    dataValueDescriptor
                )
            ) {
                return false
            }
        }
        return externalContent!!.equals(other.externalContent)
    }

    /**
     * Returns the encoding of the content. Valid values are
     *
     *  * `0` single-ASN1-type
     *  * `1` OCTET STRING
     *  * `2` BIT STRING
     *
     * @return The encoding
     */
    fun getEncoding(): Int {
        return encoding
    }

    /**
     * Sets the encoding of the content. Valid values are
     *
     *  * `0` single-ASN1-type
     *  * `1` OCTET STRING
     *  * `2` BIT STRING
     *
     * @param encoding The encoding
     */
    private fun setEncoding(encoding: Int) {
        require(!(encoding < 0 || encoding > 2)) { "invalid encoding value: $encoding" }
        this.encoding = encoding
    }

    /**
     * Sets the content of this element
     * @param externalContent The content
     */
    private fun setExternalContent(externalContent: ASN1Primitive) {
        this.externalContent = externalContent
    }
}
