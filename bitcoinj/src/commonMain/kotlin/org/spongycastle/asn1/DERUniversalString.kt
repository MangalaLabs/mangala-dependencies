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
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Arrays.hashCode
import okio.IOException

/**
 * DER UniversalString object - encodes UNICODE (ISO 10646) characters using 32-bit format. In Java we
 * have no way of representing this directly so we rely on byte arrays to carry these.
 */
class DERUniversalString(
    string: ByteArray
) : ASN1Primitive(), ASN1String {
    private val string: ByteArray

    /**
     * Basic constructor - byte encoded string.
     *
     * @param string the byte encoding of the string to be carried in the UniversalString object,
     */
    init {
        this.string = clone(string!!)
    }

    override fun getString(): String {
        val buf = StringBuilder("#")
        val bOut = MultiplatformByteArrayOutputStream()
        val aOut = ASN1OutputStream(bOut)
        try {
            aOut.writeObject(this)
        } catch (e: IOException) {
            throw ASN1ParsingException("internal error encoding BitString")
        }
        val string = bOut.toByteArray()
        for (i in string.indices) {
            buf.append(table[string[i].toInt() ushr 4 and 0xf])
            buf.append(table[string[i].toInt() and 0xf])
        }
        return buf.toString()
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
        out.writeEncoded(BERTags.UNIVERSAL_STRING, octets)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        return if (o !is DERUniversalString) {
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
        private val table = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )

        /**
         * Return a Universal String from the passed in object.
         *
         * @param obj a DERUniversalString or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return a DERUniversalString instance, or null
         */
        fun getInstance(
            obj: Any
        ): DERUniversalString {
            if (obj is DERUniversalString) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj as ByteArray?)!!) as DERUniversalString
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return a Universal String from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return a DERUniversalString instance, or null
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): DERUniversalString {
            val o = obj.getObject()
            return if (explicit || o is DERUniversalString) {
                getInstance(o)
            } else {
                DERUniversalString((o as ASN1OctetString).octets)
            }
        }
    }
}
