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
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Properties.isOverrideSet
import okio.IOException
import kotlin.jvm.JvmStatic

/**
 * Class representing the ASN.1 INTEGER type.
 */
class ASN1Integer : ASN1Primitive {
    private val bytes: ByteArray

    /**
     * Construct an INTEGER from the passed in long value.
     *
     * @param value the long representing the value desired.
     */
    constructor(
        value: Long
    ) {
        bytes = BigInteger.fromLong(value).toByteArray()
    }

    /**
     * Construct an INTEGER from the passed in BigInteger value.
     *
     * @param value the BigInteger representing the value desired.
     */
    constructor(
        value: BigInteger
    ) {
        bytes = value.toByteArray()
    }

    /**
     * Construct an INTEGER from the passed in byte array.
     *
     *
     *
     * **NB: Strict Validation applied by default.**
     *
     *
     *
     * It has turned out that there are still a few applications that struggle with
     * the ASN.1 BER encoding rules for an INTEGER as described in:
     *
     * @link https://www.itu.int/ITU-T/studygroups/com17/languages/X.690-0207.pdf
     * Section 8.3.2.
     *
     *
     *
     * Users can set the 'org.spongycastle.asn1.allow_unsafe_integer' to 'true'
     * and a looser validation will be applied. Users must recognise that this is
     * not ideal and may pave the way for an exploit based around a faulty encoding
     * in the future.
     *
     *
     * @param bytes the byte array representing a 2's complement encoding of a BigInteger.
     */
    constructor(
        bytes: ByteArray
    ) : this(bytes, true)

    internal constructor(bytes: ByteArray, clone: Boolean) {
        // Apply loose validation, see note in public constructor ANS1Integer(byte[])
        if (!isOverrideSet("org.spongycastle.asn1.allow_unsafe_integer")) {
            require(!isMalformed(bytes)) { "malformed integer" }
        }
        this.bytes = if (clone) clone(bytes) else bytes
    }

    val value: BigInteger
        get() = BigInteger.fromByteArray(bytes, Sign.POSITIVE)
    val positiveValue: BigInteger
        /**
         * in some cases positive values get crammed into a space,
         * that's not quite big enough...
         *
         * @return the BigInteger that results from treating this ASN.1 INTEGER as unsigned.
         */
        get() = BigInteger.fromByteArray(bytes, Sign.POSITIVE)

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
        out.writeEncoded(BERTags.INTEGER, bytes)
    }

    override fun hashCode(): Int {
        var value = 0
        for (i in bytes.indices) {
            value = value xor (bytes[i].toInt() and 0xff shl i % 4)
        }
        return value
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1Integer) {
            return false
        }
        return areEqual(bytes, o.bytes)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        /**
         * Return an integer from the passed in object.
         *
         * @param obj an ASN1Integer or an object that can be converted into one.
         * @return an ASN1Integer instance.
         * @throws IllegalArgumentException if the object cannot be converted.
         */
        @JvmStatic
        fun getInstance(
            obj: Any
        ): ASN1Integer {
            if (obj is ASN1Integer) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj)) as ASN1Integer
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an Integer from a tagged object.
         *
         * @param obj      the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @return an ASN1Integer instance.
         * @throws IllegalArgumentException if the tagged object cannot
         * be converted.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1Integer {
            val o = obj.getObject()
            return if (explicit || o is ASN1Integer) {
                getInstance(o)
            } else {
                ASN1Integer(ASN1OctetString.getInstance(obj.getObject()).octets)
            }
        }

        /**
         * Apply the correct validation for an INTEGER primitive following the BER rules.
         *
         * @param bytes The raw encoding of the integer.
         * @return true if the (in)put fails this validation.
         */
        fun isMalformed(bytes: ByteArray): Boolean {
            if (bytes.size > 1) {
                if (bytes[0].toInt() == 0 && bytes[1].toInt() and 0x80 == 0) {
                    return true
                }
                if (bytes[0] == 0xff.toByte() && bytes[1].toInt() and 0x80 != 0) {
                    return true
                }
            }
            return false
        }
    }
}
