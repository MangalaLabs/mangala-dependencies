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

import co.touchlab.stately.collections.ConcurrentMutableList
import org.spongycastle.util.Arrays
import org.spongycastle.util.Iterable
import okio.IOException
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * ASN.1 `SEQUENCE` and `SEQUENCE OF` constructs.
 *
 *
 * DER form is always definite form length fields, while
 * BER support uses indefinite form.
 * <hr></hr>
 *
 * **X.690**
 *
 * **8: Basic encoding rules**
 *
 * **8.9 Encoding of a sequence value **
 * 8.9.1 The encoding of a sequence value shall be constructed.
 *
 *
 * **8.9.2** The contents octets shall consist of the complete
 * encoding of one data value from each of the types listed in
 * the ASN.1 definition of the sequence type, in the order of
 * their appearance in the definition, unless the type was referenced
 * with the keyword **OPTIONAL** or the keyword **DEFAULT**.
 *
 *
 * **8.9.3** The encoding of a data value may, but need not,
 * be present for a type which was referenced with the keyword
 * **OPTIONAL** or the keyword **DEFAULT**.
 * If present, it shall appear in the encoding at the point
 * corresponding to the appearance of the type in the ASN.1 definition.
 *
 *
 * **8.10 Encoding of a sequence-of value **
 *
 *
 * **8.10.1** The encoding of a sequence-of value shall be constructed.
 *
 *
 * **8.10.2** The contents octets shall consist of zero,
 * one or more complete encodings of data values from the type listed in
 * the ASN.1 definition.
 *
 *
 * **8.10.3** The order of the encodings of the data values shall be
 * the same as the order of the data values in the sequence-of value to
 * be encoded.
 *
 *
 * **9: Canonical encoding rules**
 *
 * **9.1 Length forms**
 * If the encoding is constructed, it shall employ the indefinite-length form.
 * If the encoding is primitive, it shall include the fewest length octets necessary.
 * [Contrast with 8.1.3.2 b).]
 *
 *
 * **11: Restrictions on BER employed by both CER and DER**
 *
 * **11.5 Set and sequence components with default value**
 *
 *
 * The encoding of a set value or sequence value shall not include
 * an encoding for any component value which is equal to
 * its default value.
 *
 */
abstract class ASN1Sequence : ASN1Primitive, Iterable<ASN1Encodable?> {
    @JvmField
    protected var seq: ConcurrentMutableList<ASN1Encodable> = ConcurrentMutableList<ASN1Encodable>()

    /**
     * Create an empty SEQUENCE
     */
    protected constructor()

    /**
     * Create a SEQUENCE containing one object.
     * @param obj the object to be put in the SEQUENCE.
     */
    protected constructor(
        obj: ASN1Encodable
    ) {
        seq.add(obj)
    }

    /**
     * Create a SEQUENCE containing a vector of objects.
     * @param v the vector of objects to be put in the SEQUENCE.
     */
    protected constructor(
        v: ASN1EncodableVector
    ) {
        for (i in 0 until v.size()) {
            seq.add(v[i])
        }
    }

    /**
     * Create a SEQUENCE containing an array of objects.
     * @param array the array of objects to be put in the SEQUENCE.
     */
    protected constructor(
        array: Array<ASN1Encodable>
    ) {
        for (i in array.indices) {
            seq.add(array[i])
        }
    }

    fun toArray(): Array<ASN1Encodable> {
        val values = mutableListOf<ASN1Encodable>()
//        val values = arrayOfNulls<ASN1Encodable>(size())
        for (i in 0 until size()) {
            values[i] = getObjectAt(i)
        }
        return values.toTypedArray()
    }

    open val objects: Iterator<ASN1Encodable>
        get() = seq.iterator()

    fun parser(): ASN1SequenceParser {
        val outer = this
        return object : ASN1SequenceParser {
            private val max = size()
            private var index = 0
            @Throws(IOException::class)
            override fun readObject(): ASN1Encodable? {
                if (index == max) {
                    return null
                }
                val obj = getObjectAt(index++)
                if (obj is ASN1Sequence) {
                    return obj.parser()
                }
                return if (obj is ASN1Set) {
                    obj.parser()
                } else obj
            }

            override fun getLoadedObject(): ASN1Primitive {
                return outer
            }

            override fun toASN1Primitive(): ASN1Primitive {
                return outer
            }
        }
    }

    /**
     * Return the object at the sequence position indicated by index.
     *
     * @param index the sequence number (starting at zero) of the object
     * @return the object at the sequence position indicated by index.
     */
    open fun getObjectAt(
        index: Int
    ): ASN1Encodable {
        return seq.elementAt(index) as ASN1Encodable
    }

    /**
     * Return the number of objects in this sequence.
     *
     * @return the number of objects in this sequence.
     */
    open fun size(): Int {
        return seq.size
    }

    override fun hashCode(): Int {
        val e = objects
        var hashCode = size()
        while (e.hasNext()) {
            val o: Any = getNext(e)
            hashCode *= 17
            hashCode = hashCode xor o.hashCode()
        }
        return hashCode
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1Sequence) {
            return false
        }
        val other = o
        if (size() != other.size()) {
            return false
        }
        val s1 = objects
        val s2 = other.objects
        while (s1.hasNext()) {
            val obj1 = getNext(s1)
            val obj2 = getNext(s2)
            val o1 = obj1.toASN1Primitive()
            val o2 = obj2.toASN1Primitive()
            if (o1 === o2 || o1.equals(o2)) {
                continue
            }
            return false
        }
        return true
    }

    private fun getNext(e: Iterator<*>): ASN1Encodable {
        return e.next() as ASN1Encodable
    }

    /**
     * Change current SEQUENCE object to be encoded as [DERSequence].
     * This is part of Distinguished Encoding Rules form serialization.
     */
    override fun toDERObject(): ASN1Primitive? {
        val derSeq: ASN1Sequence = DERSequence()
        derSeq.seq = seq
        return derSeq
    }

    /**
     * Change current SEQUENCE object to be encoded as [DLSequence].
     * This is part of Direct Length form serialization.
     */
    override fun toDLObject(): ASN1Primitive? {
        val dlSeq: ASN1Sequence = DLSequence()
        dlSeq.seq = seq
        return dlSeq
    }

    override fun isConstructed(): Boolean {
        return true
    }

    @Throws(IOException::class)
    abstract override fun encode(out: ASN1OutputStream)
    override fun toString(): String {
        return seq.toString()
    }

    override fun iterator(): MutableIterator<ASN1Encodable> {
        return Arrays.Iterator<ASN1Encodable>(toArray())
    }

    companion object {
        /**
         * Return an ASN1Sequence from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return an ASN1Sequence instance, or null.
         */
        @JvmStatic
        fun getInstance(
            obj: Any
        ): ASN1Sequence {
            if (obj is ASN1Sequence) {
                return obj
            } else if (obj is ASN1SequenceParser) {
                return getInstance(obj.toASN1Primitive())
            } else if (obj is ByteArray) {
                return try {
                    getInstance(
                        fromByteArray(
                            (obj as ByteArray?)!!
                        )
                    )
                } catch (e: IOException) {
                    throw IllegalArgumentException("failed to construct sequence from byte[]: " + e.message)
                }
            } else if (obj is ASN1Encodable) {
                val primitive = obj.toASN1Primitive()
                if (primitive is ASN1Sequence) {
                    return primitive
                }
            }
            throw IllegalArgumentException("unknown object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an ASN1 SEQUENCE from a tagged object. There is a special
         * case here, if an object appears to have been explicitly tagged on
         * reading but we were expecting it to be implicitly tagged in the
         * normal course of events it indicates that we lost the surrounding
         * sequence - so we need to add it back (this will happen if the tagged
         * object is a sequence that contains other sequences). If you are
         * dealing with implicitly tagged sequences you really **should**
         * be using this method.
         *
         * @param obj the tagged object.
         * @param explicit true if the object is meant to be explicitly tagged,
         * false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return an ASN1Sequence instance.
         */
        @JvmStatic
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1Sequence? {
            if (explicit) {
                require(obj.mIsExplicit) { "object implicit - explicit expected." }
                return getInstance(obj.getObject().toASN1Primitive())
            } else {
                //
                // constructed object which appears to be explicitly tagged
                // when it should be implicit means we have to add the
                // surrounding sequence.
                //
                if (obj.mIsExplicit) {
                    return if (obj is BERTaggedObject) {
                        BERSequence(obj.getObject())
                    } else {
                        DLSequence(obj.getObject())
                    }
                } else {
                    if (obj.getObject() is ASN1Sequence) {
                        return obj.getObject() as ASN1Sequence
                    }
                }
            }
            throw IllegalArgumentException("unknown object in getInstance: " + obj::class.simpleName)
        }
    }
}
