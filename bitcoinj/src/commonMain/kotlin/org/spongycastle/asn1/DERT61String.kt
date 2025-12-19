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
 * DER T61String (also the teletex string), try not to use this if you don't need to. The standard support the encoding for
 * this has been withdrawn.
 */
class DERT61String : ASN1Primitive, ASN1String {
    private var string: ByteArray

    /**
     * Basic constructor - string encoded as a sequence of bytes.
     *
     * @param string the byte encoding of the string to be wrapped.
     */
    constructor(
        string: ByteArray
    ) {
        this.string = clone(string)
    }

    /**
     * Basic constructor - with string 8 bit assumed.
     *
     * @param string the string to be wrapped.
     */
    constructor(
        string: String
    ) {
        this.string = toByteArray(string)
    }

    /**
     * Decode the encoded string and return it, 8 bit encoding assumed.
     * @return the decoded String
     */
    override fun getString(): String {
        return Strings.fromByteArray(string)
    }

    override fun toString(): String {
        return getString()
    }

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
        out.writeEncoded(BERTags.T61_STRING, string)
    }

    val octets: ByteArray
        /**
         * Return the encoded string as a byte array.
         * @return the actual bytes making up the encoded body of the T61 string.
         */
        get() = clone(string)

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        return if (o !is DERT61String) {
            false
        } else areEqual(string, o.string)
    }

    override fun hashCode(): Int {
        return hashCode(string)
    }

    companion object {
        /**
         * Return a T61 string from the passed in object.
         *
         * @param obj a DERT61String or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return a DERT61String instance, or null
         */
        fun getInstance(
            obj: Any
        ): DERT61String {
            if (obj is DERT61String) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj)) as DERT61String
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an T61 String from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return a DERT61String instance, or null
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERT61String {
            val o = obj.getObject()
            return if (explicit || o is DERT61String) {
                getInstance(o)
            } else {
                DERT61String(ASN1OctetString.getInstance(o).octets)
            }
        }
    }
}
