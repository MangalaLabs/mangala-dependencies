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
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

//import okio.IOException

/**
 * Base class for an ASN.1 ApplicationSpecific object
 */
abstract class ASN1ApplicationSpecific internal constructor(
    @JvmField protected val isConstructed: Boolean,
    /**
     * Return the tag number associated with this object,
     *
     * @return the application tag number.
     */
    public val mApplicationTag: Int,
    octets: ByteArray
) : ASN1Primitive() {

    @JvmField
    protected val octets: ByteArray?

    init {
        this.octets = clone(octets)
    }

    public fun getApplicationTag(): Int {
        return mApplicationTag
    }

    /**
     * Return true if the object is marked as constructed, false otherwise.
     *
     * @return true if constructed, otherwise false.
     */
    override fun isConstructed(): Boolean {
        return isConstructed
    }

    val contents: ByteArray
        /**
         * Return the contents of this object as a byte[]
         *
         * @return the encoded contents of the object.
         */
        get() = clone(octets!!)

//    @get:Throws(Exception::class)
//    val `object`: ASN1Primitive
//        /**
//         * Return the enclosed object assuming explicit tagging.
//         *
//         * @return  the resulting object
//         * @throws Exception if reconstruction fails.
//         */
//        get() = fromByteArray(contents!!)

    /**
     * Return the enclosed object assuming implicit tagging.
     *
     * @param derTagNo the type tag that should be applied to the object's contents.
     * @return  the resulting object
     * @throws Exception if reconstruction fails.
     */
    @Throws(Exception::class)
    fun getObject(derTagNo: Int): ASN1Primitive {
        if (derTagNo >= 0x1f) {
            throw Exception("unsupported tag number")
        }
        val orig = encoded
        val tmp = replaceTagNumber(derTagNo, orig)
        if (orig[0].toInt() and BERTags.CONSTRUCTED != 0) {
            tmp[0] = (tmp[0].toInt() or BERTags.CONSTRUCTED).toByte()
        }
        return fromByteArray(tmp)
    }

    override fun encodedLength(): Int {
        return StreamUtil.calculateTagLength(mApplicationTag) + StreamUtil.calculateBodyLength(
            octets!!.size
        ) + octets.size
    }

    /* (non-Javadoc)
     * @see org.spongycastle.asn1.ASN1Primitive#encode(org.spongycastle.asn1.DEROutputStream)
     */
    override fun encode(out: ASN1OutputStream) {
        var classBits = BERTags.APPLICATION
        if (isConstructed) {
            classBits = classBits or BERTags.CONSTRUCTED
        }
        out.writeEncoded(classBits, mApplicationTag, octets!!)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1ApplicationSpecific) {
            return false
        }
        val other = o
        return isConstructed == other.isConstructed && mApplicationTag == other.mApplicationTag && areEqual(
            octets,
            other.octets
        )
    }

    override fun hashCode(): Int {
        return (if (isConstructed) 1 else 0) xor mApplicationTag xor hashCode(octets!!)
    }

    @Throws(Exception::class)
    private fun replaceTagNumber(newTag: Int, input: ByteArray): ByteArray {
        var tagNo = input[0].toInt() and 0x1f
        var index = 1
        //
        // with tagged object tag number is bottom 5 bits, or stored at the start of the content
        //
        if (tagNo == 0x1f) {
            tagNo = 0
            var b = input[index++].toInt() and 0xff

            // X.690-0207 8.1.2.4.2
            // "c) bits 7 to 1 of the first subsequent octet shall not all be zero."
            if (b and 0x7f == 0) // Note: -1 will pass
            {
                throw ASN1ParsingException("corrupted stream - invalid high tag number found")
            }
            while (b >= 0 && b and 0x80 != 0) {
                tagNo = tagNo or (b and 0x7f)
                tagNo = tagNo shl 7
                b = input[index++].toInt() and 0xff
            }

//            tagNo |= (b & 0x7f);
        }
        val tmp = ByteArray(input.size - index + 1)
        input.copyInto(destination = tmp, destinationOffset = 1, startIndex = index, endIndex = index + tmp.size - 1)
        tmp[0] = newTag.toByte()
        return tmp
    }

    companion object {
        /**
         * Return an ASN1ApplicationSpecific from the passed in object, which may be a byte array, or null.
         *
         * @param obj the object to be converted.
         * @return obj's representation as an ASN1ApplicationSpecific object.
         */
        fun getInstance(obj: Any): ASN1ApplicationSpecific {
            if (obj is ASN1ApplicationSpecific) {
                return obj
            } else if (obj is ByteArray) {
                return try {
                    getInstance(fromByteArray(obj))
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to construct object from byte[]: " + e.message)
                }
            }
            throw IllegalArgumentException("unknown object in getInstance: " + obj::class.simpleName)
        }

        @JvmStatic
        protected fun getLengthOfHeader(data: ByteArray): Int {
            val length = data[1].toInt() and 0xff // TODO: assumes 1 byte tag
            if (length == 0x80) {
                return 2 // indefinite-length encoding
            }
            if (length > 127) {
                val size = length and 0x7f

                // Note: The invalid long form "0xff" (see X.690 8.1.3.5c) will be caught here
                check(size <= 4) { "DER length more than 4 bytes: $size" }
                return size + 2
            }
            return 2
        }
    }
}
