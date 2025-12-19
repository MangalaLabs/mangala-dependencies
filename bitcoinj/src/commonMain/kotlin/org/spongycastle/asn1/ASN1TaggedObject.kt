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
import kotlin.jvm.JvmField

/**
 * ASN.1 TaggedObject - in ASN.1 notation this is any object preceded by
 * a [n] where n is some number - these are assumed to follow the construction
 * rules (as with sequences).
 */
abstract class ASN1TaggedObject(
    explicit: Boolean,
    tagNo: Int,
    obj: ASN1Encodable
) : ASN1Primitive(), ASN1TaggedObjectParser {
    @JvmField
    var tagNo: Int
    var mIsEmpty = false

    fun isEmpty() = mIsEmpty

    /**
     * return whether or not the object may be explicitly tagged.
     *
     *
     * Note: if the object has been read from an input stream, the only
     * time you can be sure if isExplicit is returning the true state of
     * affairs is if it returns false. An implicitly tagged object may appear
     * to be explicitly tagged, so you need to understand the context under
     * which the reading was done as well, see getObject below.
     */
    var mIsExplicit = true
    fun isExplicit() = mIsExplicit

    @JvmField
    var obj: ASN1Encodable? = null

    /**
     * Create a tagged object with the style given by the value of explicit.
     *
     *
     * If the object implements ASN1Choice the tag style will always be changed
     * to explicit in accordance with the ASN.1 encoding rules.
     *
     * @param explicit true if the object is explicitly tagged.
     * @param tagNo the tag number for this object.
     * @param obj the tagged object.
     */
    init {
        if (obj is ASN1Choice) {
            mIsExplicit = true
        } else {
            mIsExplicit = explicit
        }
        this.tagNo = tagNo
        if (mIsExplicit) {
            this.obj = obj
        } else {
            val prim = obj.toASN1Primitive()
            if (prim is ASN1Set) {
                val s: ASN1Set? = null
            }
            this.obj = obj
        }
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1TaggedObject) {
            return false
        }
        val other = o
        if (tagNo != other.tagNo || mIsEmpty != other.mIsEmpty || mIsExplicit != other.mIsExplicit) {
            return false
        }
        if (obj == null) {
            if (other.obj != null) {
                return false
            }
        } else {
            if (!obj!!.toASN1Primitive().equals(other.obj!!.toASN1Primitive())) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var code = tagNo

        // TODO: actually this is wrong - the problem is that a re-encoded
        // object may end up with a different hashCode due to implicit
        // tagging. As implicit tagging is ambiguous if a sequence is involved
        // it seems the only correct method for both equals and hashCode is to
        // compare the encodings...
        if (obj != null) {
            code = code xor obj.hashCode()
        }
        return code
    }

    /**
     * Return the tag number associated with this object.
     *
     * @return the tag number.
     */
    override fun tagNo(): Int {
        return tagNo
    }

    fun getObject(): ASN1Primitive {
        return obj!!.toASN1Primitive()
    }

//    val mObject: ASN1Primitive?
//        /**
//         * Return whatever was following the tag.
//         *
//         *
//         * Note: tagged objects are generally context dependent if you're
//         * trying to extract a tagged object you should be going via the
//         * appropriate getInstance method.
//         */
//        get() = if (obj != null) {
//            obj!!.toASN1Primitive()
//        } else null

    /**
     * Return the object held in this tagged object as a parser assuming it has
     * the type of the passed in tag. If the object doesn't have a parser
     * associated with it, the base object is returned.
     */
    @Throws(IOException::class)
    override fun getObjectParser(
        tag: Int,
        isExplicit: Boolean
    ): ASN1Encodable {
        when (tag) {
            BERTags.SET -> return ASN1Set.getInstance(this, isExplicit).parser()
            BERTags.SEQUENCE -> return ASN1Sequence.getInstance(this, isExplicit)!!.parser()
            BERTags.OCTET_STRING -> return ASN1OctetString.getInstance(this, isExplicit)!!.parser()
        }
        if (isExplicit) {
            return getObject()
        }
        throw ASN1Exception("implicit tagging not implemented for tag: $tag")
    }

    override fun getLoadedObject(): ASN1Primitive {
        return toASN1Primitive()
    }

    override fun toDERObject(): ASN1Primitive {
        return DERTaggedObject(mIsExplicit, tagNo, obj!!)
    }

    override fun toDLObject(): ASN1Primitive {
        return DLTaggedObject(mIsExplicit, tagNo, obj!!)
    }

    @Throws(IOException::class)
    abstract override fun encode(out: ASN1OutputStream)
    override fun toString(): String {
        return "[$tagNo]$obj"
    }

    companion object {
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1TaggedObject {
            if (explicit) {
                return obj.getObject() as ASN1TaggedObject
            }
            throw IllegalArgumentException("implicitly tagged tagged object")
        }

        fun getInstance(
            obj: Any
        ): ASN1TaggedObject {
            if (obj is ASN1TaggedObject) {
                return obj
            } else if (obj is ByteArray) {
                return try {
                    getInstance(
                        fromByteArray(
                            (obj as ByteArray?)!!
                        )
                    )
                } catch (e: IOException) {
                    throw IllegalArgumentException("failed to construct tagged object from byte[]: " + e.message)
                }
            }
            throw IllegalArgumentException("unknown object in getInstance: " + obj::class.simpleName)
        }
    }
}
