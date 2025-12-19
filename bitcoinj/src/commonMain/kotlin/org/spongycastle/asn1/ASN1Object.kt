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
import org.spongycastle.util.Encodable
import okio.IOException

/**
 * Base class for defining an ASN.1 object.
 */
abstract class ASN1Object : ASN1Encodable, Encodable {
    override val encoded: ByteArray
        /**
         * Return the default BER or DER encoding for this object.
         *
         * @return BER/DER byte encoded object.
         * @throws IOException on encoding error.
         */
        get() {
            val bOut = MultiplatformByteArrayOutputStream()
            val aOut = ASN1OutputStream(bOut)
            aOut.writeObject(this)
            return bOut.toByteArray()
        }

    /**
     * Return either the default for "BER" or a DER encoding if "DER" is specified.
     *
     * @param encoding name of encoding to use.
     * @return byte encoded object.
     * @throws IOException on encoding error.
     */
    @Throws(IOException::class)
    fun getEncoded(
        encoding: String
    ): ByteArray {
        if (encoding == ASN1Encoding.DER) {
            val bOut = MultiplatformByteArrayOutputStream()
            val dOut = DEROutputStream(bOut)
            dOut.writeObject(this)
            return bOut.toByteArray()
        } else if (encoding == ASN1Encoding.DL) {
            val bOut = MultiplatformByteArrayOutputStream()
            val dOut = DLOutputStream(bOut)
            dOut.writeObject(this)
            return bOut.toByteArray()
        }
        return encoded
    }

    override fun hashCode(): Int {
        return toASN1Primitive().hashCode()
    }

    override fun equals(
        o: Any?
    ): Boolean {
        if (this === o) {
            return true
        }
        if (o !is ASN1Encodable) {
            return false
        }
        return toASN1Primitive() == o.toASN1Primitive()
    }

    /**
     * @return the underlying primitive type.
     */
    @Deprecated(
        """use toASN1Primitive()
      """
    )
    fun toASN1Object(): ASN1Primitive {
        return toASN1Primitive()
    }

    /**
     * Method providing a primitive representation of this object suitable for encoding.
     * @return a primitive representation of this object.
     */
    abstract override fun toASN1Primitive(): ASN1Primitive

    companion object {
        /**
         * Return true if obj is a byte array and represents an object with the given tag value.
         *
         * @param obj object of interest.
         * @param tagValue tag value to check for.
         * @return  true if obj is a byte encoding starting with the given tag value, false otherwise.
         */
        protected fun hasEncodedTagValue(obj: Any?, tagValue: Int): Boolean {
            return obj is ByteArray && obj[0].toInt() == tagValue
        }
    }
}
