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

import org.spongycastle.util.Arrays.clone
import kotlin.jvm.JvmStatic

//import okio.IOException

/**
 * Public facade of ASN.1 Boolean data.
 *
 *
 * Use following to place a new instance of ASN.1 Boolean in your dataset:
 *
 *  *  ASN1Boolean.TRUE literal
 *  *  ASN1Boolean.FALSE literal
 *  *  [ASN1Boolean.getInstance(boolean)][ASN1Boolean.getInstance]
 *  *  [ASN1Boolean.getInstance(int)][ASN1Boolean.getInstance]
 *
 *
 */
class ASN1Boolean : ASN1Primitive {
    private val value: ByteArray

    internal constructor(
        value: ByteArray
    ) {
        require(value.size == 1) { "byte value should have 1 byte in it" }
        if (value[0].toInt() == 0) {
            this.value = FALSE_VALUE
        } else if (value[0].toInt() and 0xff == 0xff) {
            this.value = TRUE_VALUE
        } else {
            this.value = clone(value)
        }
    }

    /**
     * @param value true or false.
     */
    @Deprecated(
        """use getInstance(boolean) method.
      """
    )
    constructor(
        value: Boolean
    ) {
        this.value = if (value) TRUE_VALUE else FALSE_VALUE
    }

    val isTrue: Boolean
        get() = value[0].toInt() != 0

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        return 3
    }

    override fun encode(
        out: ASN1OutputStream
    ) {
        out.writeEncoded(BERTags.BOOLEAN, value)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        return if (o is ASN1Boolean) {
            value[0] == o.value[0]
        } else false
    }

    override fun hashCode(): Int {
        return value[0].toInt()
    }

    override fun toString(): String {
        return if (value[0].toInt() != 0) "TRUE" else "FALSE"
    }

    companion object {
        private val TRUE_VALUE = byteArrayOf(0xff.toByte())
        private val FALSE_VALUE = byteArrayOf(0)
        val FALSE = ASN1Boolean(false)
        val TRUE = ASN1Boolean(true)

        /**
         * Return a boolean from the passed in object.
         *
         * @param obj an ASN1Boolean or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return an ASN1Boolean instance.
         */
        fun getInstance(
            obj: Any?
        ): ASN1Boolean? {
            if (obj == null || obj is ASN1Boolean) {
                return obj as ASN1Boolean?
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray(obj) as ASN1Boolean
                } catch (e: Exception) {
                    throw IllegalArgumentException("failed to construct boolean from byte[]: " + e.message)
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an ASN1Boolean from the passed in boolean.
         * @param value true or false depending on the ASN1Boolean wanted.
         * @return an ASN1Boolean instance.
         */
        fun getInstance(
            value: Boolean
        ): ASN1Boolean {
            return if (value) TRUE else FALSE
        }

        /**
         * Return an ASN1Boolean from the passed in value.
         * @param value non-zero (true) or zero (false) depending on the ASN1Boolean wanted.
         * @return an ASN1Boolean instance.
         */
        fun getInstance(
            value: Int
        ): ASN1Boolean {
            return if (value != 0) TRUE else FALSE
        }

        /**
         * Return a Boolean from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return an ASN1Boolean instance.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1Boolean? {
            val o = obj.getObject()
            return if (explicit || o is ASN1Boolean) {
                getInstance(o)
            } else {
                fromOctetString((o as ASN1OctetString).octets)
            }
        }

        @JvmStatic
        fun fromOctetString(value: ByteArray): ASN1Boolean {
            require(value.size == 1) { "BOOLEAN value should have 1 byte in it" }
            return if (value[0].toInt() == 0) {
                FALSE
            } else if (value[0].toInt() and 0xff == 0xff) {
                TRUE
            } else {
                ASN1Boolean(value)
            }
        }
    }
}
