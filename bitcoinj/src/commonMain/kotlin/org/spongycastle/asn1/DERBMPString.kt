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

import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.hashCode
import okio.IOException

/**
 * DER BMPString object encodes BMP (*Basic Multilingual Plane*) subset
 * (aka UCS-2) of UNICODE (ISO 10646) characters in codepoints 0 to 65535.
 *
 *
 * At ISO-10646:2011 the term "BMP" has been withdrawn, and replaced by
 * term "UCS-2".
 *
 */
class DERBMPString : ASN1Primitive, ASN1String {
    private val string: CharArray

    /**
     * Basic constructor - byte encoded string.
     * @param string the encoded BMP STRING to wrap.
     */
    internal constructor(
        string: ByteArray
    ) {
        val cs = CharArray(string.size / 2)
        for (i in cs.indices) {
            cs[i] = (string[2 * i].toInt() shl 8 or (string[2 * i + 1].toInt() and 0xff)).toChar()
        }
        this.string = cs
    }

    internal constructor(string: CharArray) {
        this.string = string
    }

    /**
     * Basic constructor
     * @param string a String to wrap as a BMP STRING.
     */
    constructor(
        string: String
    ) {
        this.string = string.toCharArray()
    }

    override fun getString(): String {
        return string.concatToString()
    }

    override fun toString(): String {
        return getString()
    }

    override fun hashCode(): Int {
        return hashCode(string)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is DERBMPString) {
            return false
        }
        return areEqual(string, o.string)
    }

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        return 1 + StreamUtil.calculateBodyLength(string.size * 2) + string.size * 2
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.write(BERTags.BMP_STRING)
        out.writeLength(string.size * 2)
        for (i in string.indices) {
            val c = string[i]
            out.write((c.code shr 8).toByte().toInt())
            out.write(c.code.toByte().toInt())
        }
    }

    companion object {
        /**
         * Return a BMP String from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return a DERBMPString instance, or null.
         */
        fun getInstance(
            obj: Any?
        ): DERBMPString? {
            if (obj == null || obj is DERBMPString) {
                return obj as DERBMPString?
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj as ByteArray?)!!) as DERBMPString
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return a BMP String from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return a DERBMPString instance.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERBMPString? {
            val o = obj.getObject()
            return if (explicit || o is DERBMPString) {
                getInstance(o)
            } else {
                DERBMPString(ASN1OctetString.getInstance(o).octets)
            }
        }
    }
}
