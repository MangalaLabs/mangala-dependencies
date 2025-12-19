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
import org.spongycastle.util.Strings.fromUTF8ByteArray
import org.spongycastle.util.Strings.toUTF8ByteArray
import okio.IOException

/**
 * DER UTF8String object.
 */
class DERUTF8String : ASN1Primitive, ASN1String {
    private val string: ByteArray

    /*
     * Basic constructor - byte encoded string.
     */
    internal constructor(string: ByteArray) {
        this.string = string
    }

    /**
     * Basic constructor
     *
     * @param string the string to be carried in the UTF8String object,
     */
    constructor(string: String) {
        this.string = toUTF8ByteArray(string)
    }

    override fun getString(): String {
        return fromUTF8ByteArray(string)
    }

    override fun toString(): String {
        return getString()
    }

    override fun hashCode(): Int {
        return hashCode(string)
    }

    override fun asn1Equals(o: ASN1Primitive): Boolean {
        if (o !is DERUTF8String) {
            return false
        }
        return areEqual(string, o.string)
    }

    override fun isConstructed(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        return 1 + StreamUtil.calculateBodyLength(string.size) + string.size
    }

    @Throws(IOException::class)
    override fun encode(out: ASN1OutputStream) {
        out.writeEncoded(BERTags.UTF8_STRING, string)
    }

    companion object {
        /**
         * Return an UTF8 string from the passed in object.
         *
         * @param obj a DERUTF8String or an object that can be converted into one.
         * @exception IllegalArgumentException
         * if the object cannot be converted.
         * @return a DERUTF8String instance, or null
         */
        fun getInstance(obj: Any): DERUTF8String {
            if (obj is DERUTF8String) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray(obj) as DERUTF8String
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException(
                "illegal object in getInstance: "
                        + obj::class.simpleName
            )
        }

        /**
         * Return an UTF8 String from a tagged object.
         *
         * @param obj
         * the tagged object holding the object we want
         * @param explicit
         * true if the object is meant to be explicitly tagged false
         * otherwise.
         * @exception IllegalArgumentException
         * if the tagged object cannot be converted.
         * @return a DERUTF8String instance, or null
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERUTF8String {
            val o = obj.getObject()
            return if (explicit || o is DERUTF8String) {
                getInstance(o)
            } else {
                DERUTF8String(ASN1OctetString.getInstance(o).octets)
            }
        }
    }
}
