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

import com.mangala.MultiplatformByteArrayInputStream
import com.mangala.MultiplatformFilterInputStream
import com.mangala.MultiplatformInputStream
import okio.EOFException
import org.spongycastle.asn1.ASN1BitString.Companion.fromInputStream
import org.spongycastle.util.Streams.readFully
import okio.IOException
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

/**
 * A general purpose ASN.1 decoder - note: this class differs from the
 * others in that it returns null after it has read the last object in
 * the stream. If an ASN.1 NULL is encountered a DER/BER Null object is
 * returned.
 */
class ASN1InputStream @JvmOverloads constructor(
    input: MultiplatformInputStream,
    @JvmField val limit: Int,
    private val lazyEvaluate: Boolean = false
) : MultiplatformFilterInputStream(input), BERTags {
    private val tmpBuffers: MutableList<ByteArray> = mutableListOf()

    constructor(
        inputStream: MultiplatformInputStream
    ) : this(inputStream, StreamUtil.findLimit(inputStream))

    /**
     * Create an ASN1InputStream based on the input byte array. The length of DER objects in
     * the stream is automatically limited to the length of the input array.
     *
     * @param input array containing ASN.1 encoded data.
     */
    constructor(
        input: ByteArray
    ) : this(MultiplatformByteArrayInputStream(input), input.size)

    /**
     * Create an ASN1InputStream based on the input byte array. The length of DER objects in
     * the stream is automatically limited to the length of the input array.
     *
     * @param input array containing ASN.1 encoded data.
     * @param lazyEvaluate true if parsing inside constructed objects can be delayed.
     */
    constructor(
        input: ByteArray,
        lazyEvaluate: Boolean
    ) : this(MultiplatformByteArrayInputStream(input), input.size, lazyEvaluate)

    /**
     * Create an ASN1InputStream where no DER object will be longer than limit, and constructed
     * objects such as sequences will be parsed lazily.
     *
     * @param input stream containing ASN.1 encoded data.
     * @param lazyEvaluate true if parsing inside constructed objects can be delayed.
     */
    constructor(
        input: MultiplatformInputStream,
        lazyEvaluate: Boolean
    ) : this(input, StreamUtil.findLimit(input), lazyEvaluate)
    /**
     * Create an ASN1InputStream where no DER object will be longer than limit, and constructed
     * objects such as sequences will be parsed lazily.
     *
     * @param input stream containing ASN.1 encoded data.
     * @param limit maximum size of a DER encoded object.
     * @param lazyEvaluate true if parsing inside constructed objects can be delayed.
     */
    /**
     * Create an ASN1InputStream where no DER object will be longer than limit.
     *
     * @param input stream containing ASN.1 encoded data.
     * @param limit maximum size of a DER encoded object.
     */
    @Throws(IOException::class)
    protected fun readLength(): Int {
        return readLength(this, limit)
    }

    @Throws(IOException::class)
    protected fun readFully(
        bytes: ByteArray
    ) {
        if (readFully(this, bytes) != bytes.size) {
            throw EOFException("EOF encountered in middle of object")
        }
    }

    /**
     * build an object given its tag and the number of bytes to construct it from.
     *
     * @param tag the full tag details.
     * @param tagNo the tagNo defined.
     * @param length the length of the object.
     * @return the resulting primitive.
     * @throws IOException on processing exception.
     */
    @Throws(IOException::class)
    protected fun buildObject(
        tag: Int,
        tagNo: Int,
        length: Int
    ): ASN1Primitive {
        val isConstructed = tag and BERTags.CONSTRUCTED != 0
        val defIn = DefiniteLengthInputStream(this, length)
        if (tag and BERTags.APPLICATION != 0) {
            return DERApplicationSpecific(isConstructed, tagNo, defIn.toByteArray())
        }
        if (tag and BERTags.TAGGED != 0) {
            return ASN1StreamParser(defIn).readTaggedObject(isConstructed, tagNo)
        }
        return if (isConstructed) {
            // TODO There are other tags that may be constructed (e.g. BIT_STRING)
            when (tagNo) {
                BERTags.OCTET_STRING -> {
                    //
                    // yes, people actually do this...
                    //
                    val v = buildDEREncodableVector(defIn)
                    val str = mutableListOf<ASN1OctetString>()
                    var i = 0
                    while (i != v.size()) {
                        str[i] = v[i] as ASN1OctetString
                        i++
                    }
                    BEROctetString(str.toTypedArray())
                }

                BERTags.SEQUENCE -> if (lazyEvaluate) {
                    LazyEncodedSequence(defIn.toByteArray())
                } else {
                    DERFactory.createSequence(buildDEREncodableVector(defIn))
                }

                BERTags.SET -> DERFactory.createSet(buildDEREncodableVector(defIn))
                BERTags.EXTERNAL -> DERExternal(buildDEREncodableVector(defIn))
                else -> throw IOException("unknown tag $tagNo encountered")
            }
        } else createPrimitiveDERObject(tagNo, defIn, tmpBuffers.toTypedArray())
    }

    @Throws(IOException::class)
    fun buildEncodableVector(): ASN1EncodableVector {
        val v = ASN1EncodableVector()
        var o: ASN1Primitive?
        while (readObject().also { o = it } != null) {
            v.add(o!!)
        }
        return v
    }

    @Throws(IOException::class)
    fun buildDEREncodableVector(
        dIn: DefiniteLengthInputStream
    ): ASN1EncodableVector {
        return ASN1InputStream(dIn).buildEncodableVector()
    }

    @Throws(IOException::class)
    fun readObject(): ASN1Primitive? {
        val tag = read()
        if (tag <= 0) {
            if (tag == 0) {
                throw IOException("unexpected end-of-contents marker")
            }
            return null
        }

        //
        // calculate tag number
        //
        val tagNo = readTagNumber(this, tag)
        val isConstructed = tag and BERTags.CONSTRUCTED != 0

        //
        // calculate length
        //
        val length = readLength()
        return if (length < 0) // indefinite-length method
        {
            if (!isConstructed) {
                throw IOException("indefinite-length primitive encoding encountered")
            }
            val indIn = IndefiniteLengthInputStream(this, limit)
            val sp = ASN1StreamParser(indIn, limit)
            if (tag and BERTags.APPLICATION != 0) {
                return BERApplicationSpecificParser(tagNo, sp).getLoadedObject()
            }
            if (tag and BERTags.TAGGED != 0) {
                return BERTaggedObjectParser(true, tagNo, sp).getLoadedObject()
            }
            when (tagNo) {
                BERTags.OCTET_STRING -> BEROctetStringParser(sp).getLoadedObject()
                BERTags.SEQUENCE -> BERSequenceParser(sp).getLoadedObject()
                BERTags.SET -> BERSetParser(sp).getLoadedObject()
                BERTags.EXTERNAL -> DERExternalParser(sp).getLoadedObject()
                else -> throw IOException("unknown BER object encountered")
            }
        } else {
            try {
                buildObject(tag, tagNo, length)
            } catch (e: IllegalArgumentException) {
                throw ASN1Exception("corrupted stream detected", e)
            }
        }
    }

    companion object {
        @Throws(IOException::class)
        fun readTagNumber(s: MultiplatformInputStream, tag: Int): Int {
            var tagNo = tag and 0x1f

            //
            // with tagged object tag number is bottom 5 bits, or stored at the start of the content
            //
            if (tagNo == 0x1f) {
                tagNo = 0
                var b = s.read()

                // X.690-0207 8.1.2.4.2
                // "c) bits 7 to 1 of the first subsequent octet shall not all be zero."
                if (b and 0x7f == 0) // Note: -1 will pass
                {
                    throw IOException("corrupted stream - invalid high tag number found")
                }
                while (b >= 0 && b and 0x80 != 0) {
                    tagNo = tagNo or (b and 0x7f)
                    tagNo = tagNo shl 7
                    b = s.read()
                }
                if (b < 0) {
                    throw EOFException("EOF found inside tag value.")
                }
                tagNo = tagNo or (b and 0x7f)
            }
            return tagNo
        }

        @Throws(IOException::class)
        fun readLength(s: MultiplatformInputStream, limit: Int): Int {
            var length = s.read()
            if (length < 0) {
                throw EOFException("EOF found when length expected")
            }
            if (length == 0x80) {
                return -1 // indefinite-length encoding
            }
            if (length > 127) {
                val size = length and 0x7f

                // Note: The invalid long form "0xff" (see X.690 8.1.3.5c) will be caught here
                if (size > 4) {
                    throw IOException("DER length more than 4 bytes: $size")
                }
                length = 0
                for (i in 0 until size) {
                    val next = s.read()
                    if (next < 0) {
                        throw EOFException("EOF found reading length")
                    }
                    length = (length shl 8) + next
                }
                if (length < 0) {
                    throw IOException("corrupted stream - negative length found")
                }
                if (length >= limit) // after all we must have read at least 1 byte
                {
                    throw IOException("corrupted stream - out of bounds length found")
                }
            }
            return length
        }

        @Throws(IOException::class)
        private fun getBuffer(
            defIn: DefiniteLengthInputStream,
            tmpBuffers: Array<ByteArray?>
        ): ByteArray? {
            val len = defIn.getRemaining()
            return if (defIn.getRemaining() < tmpBuffers.size) {
                var buf = tmpBuffers[len]
                if (buf == null) {
                    tmpBuffers[len] = ByteArray(len)
                    buf = tmpBuffers[len]
                }
                readFully(defIn, buf!!)
                buf
            } else {
                defIn.toByteArray()
            }
        }

        @Throws(IOException::class)
        private fun getBMPCharBuffer(defIn: DefiniteLengthInputStream): CharArray {
            val len = defIn.getRemaining() / 2
            val buf = CharArray(len)
            var totalRead = 0
            while (totalRead < len) {
                val ch1 = defIn.read()
                if (ch1 < 0) {
                    break
                }
                val ch2 = defIn.read()
                if (ch2 < 0) {
                    break
                }
                buf[totalRead++] = (ch1 shl 8 or (ch2 and 0xff)).toChar()
            }
            return buf
        }

        @Throws(IOException::class)
        fun createPrimitiveDERObject(
            tagNo: Int,
            defIn: DefiniteLengthInputStream,
            tmpBuffers: Array<ByteArray?>
        ): ASN1Primitive {
            return when (tagNo) {
                BERTags.BIT_STRING -> fromInputStream(defIn.getRemaining(), defIn)
                BERTags.BMP_STRING -> DERBMPString(getBMPCharBuffer(defIn))
                BERTags.BOOLEAN -> ASN1Boolean.fromOctetString(
                    getBuffer(
                        defIn,
                        tmpBuffers
                    )!!
                )

                BERTags.ENUMERATED -> ASN1Enumerated.fromOctetString(
                    getBuffer(
                        defIn,
                        tmpBuffers
                    )!!
                )

                BERTags.GENERALIZED_TIME -> ASN1GeneralizedTime(defIn.toByteArray())
                BERTags.GENERAL_STRING -> DERGeneralString(defIn.toByteArray())
                BERTags.IA5_STRING -> DERIA5String(defIn.toByteArray())
                BERTags.INTEGER -> ASN1Integer(defIn.toByteArray(), false)
                BERTags.NULL -> DERNull.INSTANCE // actual content is ignored (enforce 0 length?)
                BERTags.NUMERIC_STRING -> DERNumericString(defIn.toByteArray())
                BERTags.OBJECT_IDENTIFIER -> ASN1ObjectIdentifier.fromOctetString(
                    getBuffer(
                        defIn,
                        tmpBuffers
                    )!!
                )

                BERTags.OCTET_STRING -> DEROctetString(defIn.toByteArray())
                BERTags.PRINTABLE_STRING -> DERPrintableString(defIn.toByteArray())
                BERTags.T61_STRING -> DERT61String(defIn.toByteArray())
                BERTags.UNIVERSAL_STRING -> DERUniversalString(defIn.toByteArray())
                BERTags.UTC_TIME -> ASN1UTCTime(defIn.toByteArray())
                BERTags.UTF8_STRING -> DERUTF8String(defIn.toByteArray())
                BERTags.VISIBLE_STRING -> DERVisibleString(defIn.toByteArray())
                BERTags.GRAPHIC_STRING -> DERGraphicString(defIn.toByteArray())
                BERTags.VIDEOTEX_STRING -> DERVideotexString(defIn.toByteArray())
                else -> throw IOException("unknown tag $tagNo encountered")
            }
        }
    }
}
