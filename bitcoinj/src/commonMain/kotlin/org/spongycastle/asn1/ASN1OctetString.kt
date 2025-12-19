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

import com.mangala.MultiplatformByteArrayInputStream
import com.mangala.MultiplatformInputStream
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.hashCode
import org.spongycastle.util.Strings
import org.spongycastle.util.encoders.Hex.encode
import okio.IOException
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Abstract base for the ASN.1 OCTET STRING data type
 *
 *
 * This supports BER, and DER forms of the data.
 *
 *
 * DER form is always primitive single OCTET STRING, while
 * BER support includes the constructed forms.
 *
 *
 * **X.690**
 *
 * **8: Basic encoding rules**
 *
 * **8.7 Encoding of an octetstring value**
 *
 *
 * **8.7.1** The encoding of an octetstring value shall be
 * either primitive or constructed at the option of the sender.
 * <blockquote>
 * NOTE  Where it is necessary to transfer part of an octet string
 * before the entire OCTET STRING is available, the constructed encoding
 * is used.
</blockquote> *
 *
 *
 * **8.7.2** The primitive encoding contains zero,
 * one or more contents octets equal in value to the octets
 * in the data value, in the order they appear in the data value,
 * and with the most significant bit of an octet of the data value
 * aligned with the most significant bit of an octet of the contents octets.
 *
 *
 *
 * **8.7.3** The contents octets for the constructed encoding shall consist
 * of zero, one, or more encodings.
 * <blockquote>
 * NOTE  Each such encoding includes identifier, length, and contents octets,
 * and may include end-of-contents octets if it is constructed.
</blockquote> *
 *
 *
 *
 * **8.7.3.1** To encode an octetstring value in this way,
 * it is segmented. Each segment shall consist of a series of
 * consecutive octets of the value. There shall be no significance
 * placed on the segment boundaries.
 * <blockquote>
 * NOTE  A segment may be of size zero, i.e. contain no octets.
</blockquote> *
 *
 *
 *
 * **8.7.3.2** Each encoding in the contents octets shall represent
 * a segment of the overall octetstring, the encoding arising from
 * a recursive application of this subclause.
 * In this recursive application, each segment is treated as if it were
 * a octetstring value. The encodings of the segments shall appear in the contents
 * octets in the order in which their octets appear in the overall value.
 * <blockquote>
 * NOTE 1  As a consequence of this recursion,
 * each encoding in the contents octets may itself
 * be primitive or constructed.
 * However, such encodings will usually be primitive.
 * <br></br>
 * NOTE 2  In particular, the tags in the contents octets are always universal class, number 4.
</blockquote> *
 *
 *
 * **9: Canonical encoding rules**
 *
 * **9.1 Length forms**
 *
 *
 * If the encoding is constructed, it shall employ the indefinite-length form.
 * If the encoding is primitive, it shall include the fewest length octets necessary.
 * [Contrast with 8.1.3.2 b).]
 *
 *
 * **9.2 String encoding forms**
 *
 *
 * BIT STRING, OCTET STRING,and restricted character string
 * values shall be encoded with a primitive encoding if they would
 * require no more than 1000 contents octets, and as a constructed
 * encoding otherwise. The string fragments contained in
 * the constructed encoding shall be encoded with a primitive encoding.
 * The encoding of each fragment, except possibly
 * the last, shall have 1000 contents octets. (Contrast with 8.21.6.)
 *
 *
 * **10: Distinguished encoding rules**
 *
 *
 * **10.1 Length forms**
 * The definite form of length encoding shall be used,
 * encoded in the minimum number of octets.
 * [Contrast with 8.1.3.2 b).]
 *
 *
 * **10.2 String encoding forms**
 * For BIT STRING, OCTET STRING and restricted character string types,
 * the constructed form of encoding shall not be used.
 * (Contrast with 8.21.6.)
 *
 */
abstract class ASN1OctetString(
    string: ByteArray
) : ASN1Primitive(), ASN1OctetStringParser {
    @JvmField
    var string: ByteArray

    /**
     * Base constructor.
     *
     * @param string the octets making up the octet string.
     */
    init {
        if (string == null) {
            throw NullPointerException("string cannot be null")
        }
        this.string = string
    }

    /**
     * Return the content of the OCTET STRING as an MultiplatformInputStream.
     *
     * @return an InputStream representing the OCTET STRING's content.
     */
    override fun getOctetStream(): MultiplatformInputStream {
        return MultiplatformByteArrayInputStream(string)
    }

    /**
     * Return the parser associated with this object.
     *
     * @return a parser based on this OCTET STRING
     */
    fun parser(): ASN1OctetStringParser {
        return this
    }

    open val octets: ByteArray
        /**
         * Return the content of the OCTET STRING as a byte array.
         *
         * @return the byte[] representing the OCTET STRING's content.
         */
        get() = string

    override fun hashCode(): Int {
        return hashCode(octets)
    }

    public override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1OctetString) {
            return false
        }
        return areEqual(string, o.string)
    }

    override fun getLoadedObject(): ASN1Primitive {
        return toASN1Primitive()
    }

    public override fun toDERObject(): ASN1Primitive {
        return DEROctetString(string)
    }

    public override fun toDLObject(): ASN1Primitive {
        return DEROctetString(string)
    }

    @Throws(IOException::class)
    abstract override fun encode(out: ASN1OutputStream)
    override fun toString(): String {
        return "#" + Strings.fromByteArray(encode(string))
    }

    companion object {
        /**
         * return an Octet String from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         */
        @JvmStatic
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1OctetString {
            val o = obj.getObject()
            return if (explicit || o is ASN1OctetString) {
                getInstance(o)
            } else {
                BEROctetString.fromSequence(ASN1Sequence.getInstance(o))
            }
        }

        /**
         * return an Octet String from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        @JvmStatic
        fun getInstance(
            obj: Any
        ): ASN1OctetString {
            if (obj is ASN1OctetString) {
                return obj
            } else if (obj is ByteArray) {
                return try {
                    getInstance(fromByteArray(obj))
                } catch (e: IOException) {
                    throw IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + e.message)
                }
            } else if (obj is ASN1Encodable) {
                val primitive = obj.toASN1Primitive()
                if (primitive is ASN1OctetString) {
                    return primitive
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }
    }
}
