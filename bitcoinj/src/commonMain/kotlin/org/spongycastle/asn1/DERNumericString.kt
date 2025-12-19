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
import kotlin.jvm.JvmOverloads

/**
 * DER NumericString object - this is an ascii string of characters {0,1,2,3,4,5,6,7,8,9, }.
 * ASN.1 NUMERIC-STRING object.
 *
 *
 * This is an ASCII string of characters {0,1,2,3,4,5,6,7,8,9} + space.
 *
 *
 * See X.680 section 37.2.
 *
 *
 * Explicit character set escape sequences are not allowed.
 */
class DERNumericString : ASN1Primitive, ASN1String {
    private val string: ByteArray

    /**
     * Basic constructor - with bytes.
     */
    internal constructor(
        string: ByteArray
    ) {
        this.string = string
    }
    /**
     * Constructor with optional validation.
     *
     * @param string the base string to wrap.
     * @param validate whether or not to check the string.
     * @throws IllegalArgumentException if validate is true and the string
     * contains characters that should not be in a NumericString.
     */
    /**
     * Basic constructor -  without validation..
     */
    @JvmOverloads
    constructor(
        string: String,
        validate: Boolean = false
    ) {
        require(!(validate && !isNumericString(string))) { "string contains illegal characters" }
        this.string = toByteArray(string)
    }

    override fun getString(): String {
        return Strings.fromByteArray(string)
    }

    override fun toString(): String {
        return getString()
    }

    val octets: ByteArray
        get() = clone(string)

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        return 1 + StreamUtil.calculateBodyLength(string.size) + string.size
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.writeEncoded(BERTags.NUMERIC_STRING, string)
    }

    override fun hashCode(): Int {
        return hashCode(string)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is DERNumericString) {
            return false
        }
        return areEqual(string, o.string)
    }

    companion object {
        /**
         * Return a Numeric string from the passed in object
         *
         * @param obj a DERNumericString or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return a DERNumericString instance, or null
         */
        fun getInstance(
            obj: Any
        ): DERNumericString {
            if (obj is DERNumericString) {
                return obj as DERNumericString
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj)) as DERNumericString
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an Numeric String from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return a DERNumericString instance, or null.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERNumericString? {
            val o = obj.getObject()
            return if (explicit || o is DERNumericString) {
                getInstance(o)
            } else {
                DERNumericString(ASN1OctetString.getInstance(o).octets)
            }
        }

        /**
         * Return true if the string can be represented as a NumericString ('0'..'9', ' ')
         *
         * @param str string to validate.
         * @return true if numeric, fale otherwise.
         */
        fun isNumericString(
            str: String
        ): Boolean {
            for (i in str.length - 1 downTo 0) {
                val ch = str[i]
                if (ch.code > 0x007f) {
                    return false
                }
                if ('0' <= ch && ch <= '9' || ch == ' ') {
                    continue
                }
                return false
            }
            return true
        }
    }
}
