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

/**
 * A NULL object - use DERNull.INSTANCE for populating structures.
 */
abstract class ASN1Null : ASN1Primitive() {
    override fun hashCode(): Int {
        return -1
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        return if (o !is ASN1Null) {
            false
        } else true
    }

    @Throws(IOException::class)
    abstract override fun encode(out: ASN1OutputStream)
    override fun toString(): String {
        return "NULL"
    }

    companion object {
        /**
         * Return an instance of ASN.1 NULL from the passed in object.
         *
         *
         * Accepted inputs:
         *
         *  *  null  null
         *  *  [ASN1Null] object
         *  *  a byte[] containing ASN.1 NULL object
         *
         *
         *
         * @param o object to be converted.
         * @return an instance of ASN1Null, or null.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        fun getInstance(o: Any): ASN1Null {
            if (o is ASN1Null) {
                return o
            }
            return try {
                    getInstance(fromByteArray((o as ByteArray?)!!))
                } catch (e: IOException) {
                    throw IllegalArgumentException("failed to construct NULL from byte[]: " + e.message)
                } catch (e: ClassCastException) {
                    throw IllegalArgumentException("unknown object in getInstance(): " + o::class.simpleName)
                }

        }
    }
}
