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
import com.mangala.MultiplatformInputStream
import okio.IOException
import kotlin.jvm.JvmOverloads

/**
 * A parser for ASN.1 streams which also returns, where possible, parsers for the objects it encounters.
 */
class ASN1StreamParser @JvmOverloads constructor(
    private val _in: MultiplatformInputStream,
    private val _limit: Int = StreamUtil.findLimit(_in)
) {
    private val tmpBuffers: Array<ByteArray?>

    init {
        tmpBuffers = arrayOfNulls(11)
    }

    constructor(
        encoding: ByteArray
    ) : this(MultiplatformByteArrayInputStream(encoding), encoding.size)

    @Throws(IOException::class)
    fun readIndef(tagValue: Int): ASN1Encodable {
        // Note: INDEF => CONSTRUCTED

        // TODO There are other tags that may be constructed (e.g. BIT_STRING)
        return when (tagValue) {
            BERTags.EXTERNAL -> DERExternalParser(this)
            BERTags.OCTET_STRING -> BEROctetStringParser(this)
            BERTags.SEQUENCE -> BERSequenceParser(this)
            BERTags.SET -> BERSetParser(this)
            else -> throw ASN1Exception(
                "unknown BER object encountered: 0x" + tagValue.toString(16)
            )
        }
    }

    @Throws(IOException::class)
    fun readImplicit(constructed: Boolean, tag: Int): ASN1Encodable {
        if (_in is IndefiniteLengthInputStream) {
            if (!constructed) {
                throw IOException("indefinite-length primitive encoding encountered")
            }
            return readIndef(tag)
        }
        if (constructed) {
            when (tag) {
                BERTags.SET -> return DERSetParser(this)
                BERTags.SEQUENCE -> return DERSequenceParser(this)
                BERTags.OCTET_STRING -> return BEROctetStringParser(this)
            }
        } else {
            when (tag) {
                BERTags.SET -> throw ASN1Exception("sequences must use constructed encoding (see X.690 8.9.1/8.10.1)")
                BERTags.SEQUENCE -> throw ASN1Exception("sets must use constructed encoding (see X.690 8.11.1/8.12.1)")
                BERTags.OCTET_STRING -> return DEROctetStringParser(_in as DefiniteLengthInputStream)
            }
        }
        throw ASN1Exception("implicit tagging not implemented")
    }

    @Throws(IOException::class)
    fun readTaggedObject(constructed: Boolean, tag: Int): ASN1Primitive {
        if (!constructed) {
            // Note: !CONSTRUCTED => IMPLICIT
            val defIn = _in as DefiniteLengthInputStream
            return DERTaggedObject(false, tag, DEROctetString(defIn.toByteArray()))
        }
        val v = readVector()
        if (_in is IndefiniteLengthInputStream) {
            return if (v.size() == 1) BERTaggedObject(true, tag, v[0]) else BERTaggedObject(
                false,
                tag,
                BERFactory.createSequence(v)
            )
        }
        return if (v.size() == 1) DERTaggedObject(true, tag, v[0]) else DERTaggedObject(
            false,
            tag,
            DERFactory.createSequence(v)
        )
    }

    @Throws(IOException::class)
    fun readObject(): ASN1Encodable? {
        val tag = _in.read()
        if (tag == -1) {
            return null
        }

        //
        // turn of looking for "00" while we resolve the tag
        //
        set00Check(false)

        //
        // calculate tag number
        //
        val tagNo = ASN1InputStream.readTagNumber(_in, tag)
        val isConstructed = tag and BERTags.CONSTRUCTED != 0

        //
        // calculate length
        //
        val length = ASN1InputStream.readLength(_in, _limit)
        return if (length < 0) // indefinite-length method
        {
            if (!isConstructed) {
                throw IOException("indefinite-length primitive encoding encountered")
            }
            val indIn = IndefiniteLengthInputStream(_in, _limit)
            val sp = ASN1StreamParser(indIn, _limit)
            if (tag and BERTags.APPLICATION != 0) {
                return BERApplicationSpecificParser(tagNo, sp)
            }
            if (tag and BERTags.TAGGED != 0) {
                BERTaggedObjectParser(true, tagNo, sp)
            } else sp.readIndef(tagNo)
        } else {
            val defIn = DefiniteLengthInputStream(_in, length)
            if (tag and BERTags.APPLICATION != 0) {
                return DERApplicationSpecific(isConstructed, tagNo, defIn.toByteArray())
            }
            if (tag and BERTags.TAGGED != 0) {
                return BERTaggedObjectParser(isConstructed, tagNo, ASN1StreamParser(defIn))
            }
            if (isConstructed) {
                // TODO There are other tags that may be constructed (e.g. BIT_STRING)
                return when (tagNo) {
                    BERTags.OCTET_STRING ->                         //
                        // yes, people actually do this...
                        //
                        BEROctetStringParser(ASN1StreamParser(defIn))

                    BERTags.SEQUENCE -> DERSequenceParser(ASN1StreamParser(defIn))
                    BERTags.SET -> DERSetParser(ASN1StreamParser(defIn))
                    BERTags.EXTERNAL -> DERExternalParser(ASN1StreamParser(defIn))
                    else -> throw IOException("unknown tag $tagNo encountered")
                }
            }
            when (tagNo) {
                BERTags.OCTET_STRING -> return DEROctetStringParser(defIn)
            }
            try {
                ASN1InputStream.createPrimitiveDERObject(tagNo, defIn, tmpBuffers)
            } catch (e: IllegalArgumentException) {
                throw ASN1Exception("corrupted stream detected", e)
            }
        }
    }

    private fun set00Check(enabled: Boolean) {
        if (_in is IndefiniteLengthInputStream) {
            _in.setEofOn00(enabled)
        }
    }

    @Throws(IOException::class)
    fun readVector(): ASN1EncodableVector {
        val v = ASN1EncodableVector()
        var obj: ASN1Encodable
        while (readObject().also { obj = it!! } != null) {
            if (obj is InMemoryRepresentable) {
                v.add((obj as InMemoryRepresentable).getLoadedObject())
            } else {
                v.add(obj.toASN1Primitive())
            }
        }
        return v
    }
}
