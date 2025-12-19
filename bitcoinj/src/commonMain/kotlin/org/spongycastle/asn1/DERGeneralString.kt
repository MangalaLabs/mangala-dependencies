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
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Arrays.hashCode
import org.spongycastle.util.Strings
import org.spongycastle.util.Strings.toByteArray
import okio.IOException

/**
 * ASN.1 GENERAL-STRING data type.
 *
 *
 * This is an 8-bit encoded ISO 646 (ASCII) character set
 * with optional escapes to other character sets.
 *
 */
class DERGeneralString : ASN1Primitive, ASN1String {
    private val string: ByteArray

    internal constructor(string: ByteArray) {
        this.string = string
    }

    /**
     * Construct a GeneralString from the passed in String.
     *
     * @param string the string to be contained in this object.
     */
    constructor(string: String?) {
        this.string = toByteArray(string!!)
    }

    /**
     * Return a Java String representation of our contained String.
     *
     * @return a Java String representing our contents.
     */
    override fun getString(): String {
        return Strings.fromByteArray(string)
    }

    override fun toString(): String {
        return getString()
    }

    val octets: ByteArray
        /**
         * Return a byte array representation of our contained String.
         *
         * @return a byte array representing our contents.
         */
        get() = clone(string)

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        return 1 + StreamUtil.calculateBodyLength(string.size) + string.size
    }

    @Throws(IOException::class)
    override fun encode(out: ASN1OutputStream) {
        out.writeEncoded(BERTags.GENERAL_STRING, string)
    }

    override fun hashCode(): Int {
        return hashCode(string)
    }

    override fun asn1Equals(o: ASN1Primitive): Boolean {
        if (o !is DERGeneralString) {
            return false
        }
        return areEqual(string, o.string)
    }

    companion object {
        /**
         * Return a GeneralString from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return a DERBMPString instance, or null.
         */
        fun getInstance(
            obj: Any?
        ): DERGeneralString? {
            if (obj == null || obj is DERGeneralString) {
                return obj as DERGeneralString?
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj as ByteArray?)!!) as DERGeneralString
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
         * Return a GeneralString from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return a DERGeneralString instance.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERGeneralString? {
            val o = obj.getObject()
            return if (explicit || o is DERGeneralString) {
                getInstance(o)
            } else {
                DERGeneralString((o as ASN1OctetString).octets)
            }
        }
    }
}
