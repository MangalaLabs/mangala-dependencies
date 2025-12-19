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

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Arrays.hashCode
import org.spongycastle.util.Properties.isOverrideSet
import okio.IOException
import kotlin.jvm.JvmStatic

/**
 * Class representing the ASN.1 ENUMERATED type.
 */
class ASN1Enumerated : ASN1Primitive {
    private val bytes: ByteArray

    /**
     * Constructor from int.
     *
     * @param value the value of this enumerated.
     */
    constructor(
        value: Int
    ) {
        bytes = BigInteger.fromLong(value.toLong()).toByteArray()
    }

    /**
     * Constructor from BigInteger
     *
     * @param value the value of this enumerated.
     */
    constructor(
        value: BigInteger
    ) {
        bytes = value.toByteArray()
    }

    /**
     * Constructor from encoded BigInteger.
     *
     * @param bytes the value of this enumerated as an encoded BigInteger (signed).
     */
    constructor(
        bytes: ByteArray
    ) {
        if (!isOverrideSet("org.spongycastle.asn1.allow_unsafe_integer")) {
            require(!ASN1Integer.isMalformed(bytes)) { "malformed enumerated" }
        }
        this.bytes = clone(bytes)
    }

    val value: BigInteger
        get() = BigInteger.fromTwosComplementByteArray(bytes)

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        return 1 + StreamUtil.calculateBodyLength(bytes.size) + bytes.size
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.writeEncoded(BERTags.ENUMERATED, bytes)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1Enumerated) {
            return false
        }
        return areEqual(bytes, o.bytes)
    }

    override fun hashCode(): Int {
        return hashCode(bytes)
    }

    companion object {
        /**
         * return an enumerated from the passed in object
         *
         * @param obj an ASN1Enumerated or an object that can be converted into one.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return an ASN1Enumerated instance, or null.
         */
        fun getInstance(
            obj: Any
        ): ASN1Enumerated {
            if (obj is ASN1Enumerated) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj as ByteArray?)!!) as ASN1Enumerated
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * return an Enumerated from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return an ASN1Enumerated instance, or null.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1Enumerated {
            val o = obj.getObject()
            return if (explicit || o is ASN1Enumerated) {
                getInstance(o)
            } else {
                fromOctetString((o as ASN1OctetString).octets)
            }
        }

        private val cache = mutableListOf<ASN1Enumerated>()
        @JvmStatic
        fun fromOctetString(enc: ByteArray): ASN1Enumerated {
            if (enc.size > 1) {
                return ASN1Enumerated(enc)
            }
            require(enc.size != 0) { "ENUMERATED has zero length" }
            val value = enc[0].toInt() and 0xff
            if (value >= cache.size) {
                return ASN1Enumerated(clone(enc))
            }
            var possibleMatch = cache[value]
//            if (possibleMatch == null) {
                cache[value] = ASN1Enumerated(clone(enc))
                possibleMatch = cache[value]
//            }
            return possibleMatch
        }
    }
}
