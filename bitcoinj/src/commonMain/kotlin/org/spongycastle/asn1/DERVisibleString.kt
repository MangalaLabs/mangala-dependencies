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
 * DER VisibleString object encoding ISO 646 (ASCII) character code points 32 to 126.
 *
 *
 * Explicit character set escape sequences are not allowed.
 *
 */
class DERVisibleString : ASN1Primitive, ASN1String {
    private val string: ByteArray

    /*
     * Basic constructor - byte encoded string.
     */
    internal constructor(
        string: ByteArray
    ) {
        this.string = string
    }

    /**
     * Basic constructor
     *
     * @param string the string to be carried in the VisibleString object,
     */
    constructor(
        string: String
    ) {
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
        out.writeEncoded(BERTags.VISIBLE_STRING, string)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        return if (o !is DERVisibleString) {
            false
        } else areEqual(
            string,
            o.string
        )
    }

    override fun hashCode(): Int {
        return hashCode(string)
    }

    companion object {
        /**
         * Return a Visible String from the passed in object.
         *
         * @param obj a DERVisibleString or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return a DERVisibleString instance, or null
         */
        fun getInstance(
            obj: Any
        ): DERVisibleString {
            if (obj is DERVisibleString) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray(obj) as DERVisibleString
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return a Visible String from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return a DERVisibleString instance, or null
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERVisibleString {
            val o = obj.getObject()
            return if (explicit || o is DERVisibleString) {
                getInstance(o)
            } else {
                DERVisibleString(ASN1OctetString.getInstance(o).octets)
            }
        }
    }
}
