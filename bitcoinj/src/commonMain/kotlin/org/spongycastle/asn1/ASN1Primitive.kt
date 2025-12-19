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
import kotlin.jvm.JvmStatic

/**
 * Base class for ASN.1 primitive objects. These are the actual objects used to generate byte encodings.
 */
abstract class ASN1Primitive internal constructor() : ASN1Object() {
    override fun equals(o: Any?): Boolean {
        return if (this === o) {
            true
        } else o is ASN1Encodable && asn1Equals(o.toASN1Primitive())
    }

    override fun toASN1Primitive(): ASN1Primitive {
        return this
    }

    /**
     * Return the current object as one which encodes using Distinguished Encoding Rules.
     *
     * @return a DER version of this.
     */
    open fun toDERObject(): ASN1Primitive? {
        return this
    }

    /**
     * Return the current object as one which encodes using Definite Length encoding.
     *
     * @return a DL version of this.
     */
    open fun toDLObject(): ASN1Primitive? {
        return this
    }

    abstract override fun hashCode(): Int

    /**
     * Return true if this objected is a CONSTRUCTED one, false otherwise.
     * @return true if CONSTRUCTED bit set on object's tag, false otherwise.
     */
//    @JvmField
    abstract fun isConstructed(): Boolean

    /**
     * Return the length of the encoding this object will produce.
     * @return the length of the object's encoding.
     * @throws IOException if the encoding length cannot be calculated.
     */
    @Throws(IOException::class)
    abstract fun encodedLength(): Int
    @Throws(IOException::class)
    abstract fun encode(out: ASN1OutputStream)

    /**
     * Equality (similarity) comparison for two ASN1Primitive objects.
     */
    abstract fun asn1Equals(o: ASN1Primitive): Boolean

    companion object {
        /**
         * Create a base ASN.1 object from a byte stream.
         *
         * @param data the byte stream to parse.
         * @return the base ASN.1 object represented by the byte stream.
         * @exception IOException if there is a problem parsing the data, or parsing the stream did not exhaust the available data.
         */
        @JvmStatic
        @Throws(IOException::class)
        fun fromByteArray(data: ByteArray): ASN1Primitive {
            val aIn = ASN1InputStream(data)
            return try {
                val o = aIn.readObject()
                if (aIn.available() != 0) {
                    throw IOException("Extra data detected in stream")
                }
                o!!
            } catch (e: ClassCastException) {
                throw IOException("cannot recognise object in stream")
            }
        }
    }
}
