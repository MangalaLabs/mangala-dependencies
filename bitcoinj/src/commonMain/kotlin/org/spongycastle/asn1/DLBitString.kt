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

import okio.IOException
import kotlin.jvm.JvmOverloads

/**
 * A Definite length BIT STRING
 */
class DLBitString : ASN1BitString {
    protected constructor(
        data: Byte,
        padBits: Int
    ) : this(toByteArray(data), padBits)

    /**
     * @param data the octets making up the bit string.
     * @param padBits the number of extra bits at the end of the string.
     */
    @JvmOverloads
    constructor(
        data: ByteArray,
        padBits: Int = 0
    ) : super(data, padBits)

    constructor(
        value: Int
    ) : super(getBytes(value), getPadBits(value))

    constructor(
        obj: ASN1Encodable
    ) : super(obj.toASN1Primitive().getEncoded(ASN1Encoding.DER), 0)

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        return 1 + StreamUtil.calculateBodyLength(data.size + 1) + data.size + 1
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        val string = data
        val bytes = ByteArray(string.size + 1)
        bytes[0] = padBits.toByte()
        string.copyInto(destination = bytes, destinationOffset = 1, startIndex = 0, endIndex = bytes.size - 1)
        out.writeEncoded(BERTags.BIT_STRING, bytes)
    }

    companion object {
        /**
         * return a Bit String that can be definite-length encoded from the passed in object.
         *
         * @param obj a DL or DER BitString or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return an ASN1BitString instance, or null.
         */
        fun getInstance(
            obj: Any
        ): ASN1BitString {
            if (obj is DLBitString) {
                return obj
            }
            if (obj is DERBitString) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray(obj) as ASN1BitString
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * return a Bit String from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return an ASN1BitString instance, or null.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1BitString {
            val o = obj.getObject()
            return if (explicit || o is DLBitString) {
                getInstance(o)
            } else {
                fromOctetString((o as ASN1OctetString).octets)
            }
        }

        private fun toByteArray(data: Byte): ByteArray {
            val rv = ByteArray(1)
            rv[0] = data
            return rv
        }

        fun fromOctetString(bytes: ByteArray): DLBitString {
            require(bytes.size >= 1) { "truncated BIT STRING detected" }
            val padBits = bytes[0].toInt()
            val data = ByteArray(bytes.size - 1)
            if (data.size != 0) {
                bytes.copyInto(destination = data, destinationOffset = 0, startIndex = 1, endIndex = bytes.size)
            }
            return DLBitString(data, padBits)
        }
    }
}
