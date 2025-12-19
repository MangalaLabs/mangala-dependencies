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
import kotlin.jvm.JvmStatic
import kotlin.math.min

/**
 * ASN.1 `SET` and `SET OF` constructs.
 *
 *
 * Note: This does not know which syntax the set is!
 * (The difference: ordering of SET elements or not ordering.)
 *
 *
 * DER form is always definite form length fields, while
 * BER support uses indefinite form.
 *
 *
 * The CER form support does not exist.
 *
 *
 * <h2>X.690</h2>
 * <h3>8: Basic encoding rules</h3>
 * <h4>8.11 Encoding of a set value </h4>
 * **8.11.1** The encoding of a set value shall be constructed
 *
 *
 * **8.11.2** The contents octets shall consist of the complete
 * encoding of a data value from each of the types listed in the
 * ASN.1 definition of the set type, in an order chosen by the sender,
 * unless the type was referenced with the keyword
 * **OPTIONAL** or the keyword **DEFAULT**.
 *
 *
 * **8.11.3** The encoding of a data value may, but need not,
 * be present for a type which was referenced with the keyword
 * **OPTIONAL** or the keyword **DEFAULT**.
 * <blockquote>
 * NOTE  The order of data values in a set value is not significant,
 * and places no constraints on the order during transfer
</blockquote> *
 * <h4>8.12 Encoding of a set-of value</h4>
 * **8.12.1** The encoding of a set-of value shall be constructed.
 *
 *
 * **8.12.2** The text of 8.10.2 applies:
 * *The contents octets shall consist of zero,
 * one or more complete encodings of data values from the type listed in
 * the ASN.1 definition.*
 *
 *
 * **8.12.3** The order of data values need not be preserved by
 * the encoding and subsequent decoding.
 *
 * <h3>9: Canonical encoding rules</h3>
 * <h4>9.1 Length forms</h4>
 * If the encoding is constructed, it shall employ the indefinite-length form.
 * If the encoding is primitive, it shall include the fewest length octets necessary.
 * [Contrast with 8.1.3.2 b).]
 * <h4>9.3 Set components</h4>
 * The encodings of the component values of a set value shall
 * appear in an order determined by their tags as specified
 * in 8.6 of ITU-T Rec. X.680 | ISO/IEC 8824-1.
 * Additionally, for the purposes of determining the order in which
 * components are encoded when one or more component is an untagged
 * choice type, each untagged choice type is ordered as though it
 * has a tag equal to that of the smallest tag in that choice type
 * or any untagged choice types nested within.
 *
 * <h3>10: Distinguished encoding rules</h3>
 * <h4>10.1 Length forms</h4>
 * The definite form of length encoding shall be used,
 * encoded in the minimum number of octets.
 * [Contrast with 8.1.3.2 b).]
 * <h4>10.3 Set components</h4>
 * The encodings of the component values of a set value shall appear
 * in an order determined by their tags as specified
 * in 8.6 of ITU-T Rec. X.680 | ISO/IEC 8824-1.
 * <blockquote>
 * NOTE  Where a component of the set is an untagged choice type,
 * the location of that component in the ordering will depend on
 * the tag of the choice component being encoded.
</blockquote> *
 *
 * <h3>11: Restrictions on BER employed by both CER and DER</h3>
 * <h4>11.5 Set and sequence components with default value </h4>
 * The encoding of a set value or sequence value shall not include
 * an encoding for any component value which is equal to
 * its default value.
 * <h4>11.6 Set-of components </h4>
 *
 *
 * The encodings of the component values of a set-of value
 * shall appear in ascending order, the encodings being compared
 * as octet strings with the shorter components being padded at
 * their trailing end with 0-octets.
 * <blockquote>
 * NOTE  The padding octets are for comparison purposes only
 * and do not appear in the encodings.
</blockquote> *
 *
 */
abstract class ASN1Set : ASN1Primitive, Iterable<ASN1Encodable> {
    private var set: ConcurrentMutableList<ASN1Encodable> = ConcurrentMutableList()
    private var isSorted = false

    protected constructor()

    /**
     * Create a SET containing one object
     * @param obj object to be added to the SET.
     */
    protected constructor(
        obj: ASN1Encodable
    ) {
        set.add(obj)
    }

    /**
     * Create a SET containing a vector of objects.
     * @param v a vector of objects to make up the SET.
     * @param doSort true if should be sorted DER style, false otherwise.
     */
    protected constructor(
        v: ASN1EncodableVector,
        doSort: Boolean
    ) {
        for (i in 0 until v.size()) {
            set.add(v[i])
        }
        if (doSort) {
            this.sort()
        }
    }

    /**
     * Create a SET containing an array of objects.
     * @param array an array of objects to make up the SET.
     * @param doSort true if should be sorted DER style, false otherwise.
     */
    protected constructor(
        array: Array<ASN1Encodable>,
        doSort: Boolean
    ) {
        for (i in array.indices) {
            set.add(array[i])
        }
        if (doSort) {
            this.sort()
        }
    }

    val objects: Iterator<ASN1Encodable>
        get() = set.iterator()

    /**
     * return the object at the set position indicated by index.
     *
     * @param index the set number (starting at zero) of the object
     * @return the object at the set position indicated by index.
     */
    fun getObjectAt(
        index: Int
    ): ASN1Encodable {
        return set.elementAt(index) as ASN1Encodable
    }

    /**
     * return the number of objects in this set.
     *
     * @return the number of objects in this set.
     */
    fun size(): Int {
        return set.size
    }

    fun toArray(): Array<ASN1Encodable> {
        val values = mutableListOf<ASN1Encodable>()
//            arrayOfNulls<ASN1Encodable>(size())
        for (i in 0 until size()) {
            values[i] = getObjectAt(i)
        }
        return values.toTypedArray()
    }

    fun parser(): ASN1SetParser {
        val outer = this
        return object : ASN1SetParser {
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

    /**
     * Change current SET object to be encoded as [DERSet].
     * This is part of Distinguished Encoding Rules form serialization.
     */
    override fun toDERObject(): ASN1Primitive {
        return if (isSorted) {
            val derSet: ASN1Set = DERSet()
            derSet.set = set
            derSet
        } else {
            val v: ConcurrentMutableList<ASN1Encodable> = ConcurrentMutableList<ASN1Encodable>()
            for (i in set.indices) {
                v.add(set.elementAt(i))
            }
            val derSet: ASN1Set = DERSet()
            derSet.set = v
            derSet.sort()
            derSet
        }
    }

    /**
     * Change current SET object to be encoded as [DLSet].
     * This is part of Direct Length form serialization.
     */
    override fun toDLObject(): ASN1Primitive {
        val derSet: ASN1Set = DLSet()
        derSet.set = set
        return derSet
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1Set) {
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

    private fun getNext(e: Iterator<ASN1Encodable>): ASN1Encodable {

        // unfortunately null was allowed as a substitute for DER null
        return e.next() as ASN1Encodable ?: return DERNull.INSTANCE
    }

    /**
     * return true if a <= b (arrays are assumed padded with zeros).
     */
    private fun lessThanOrEqual(
        a: ByteArray,
        b: ByteArray
    ): Boolean {
        val len = min(a.size, b.size)
        for (i in 0 until len) {
            if (a[i] != b[i]) {
                return a[i].toInt() and 0xff < b[i].toInt() and 0xff
            }
        }
        return len == a.size
    }

    private fun getDEREncoded(
        obj: ASN1Encodable
    ): ByteArray {
        return try {
            obj.toASN1Primitive().getEncoded(ASN1Encoding.DER)
        } catch (e: IOException) {
            throw IllegalArgumentException("cannot encode object added to SET")
        }
    }

    protected fun sort() {
        if (!isSorted) {
            isSorted = true
            if (set.size > 1) {
                var swapped = true
                var lastSwap = set.size - 1
                while (swapped) {
                    var index = 0
                    var swapIndex = 0
                    var a = getDEREncoded(set.elementAt(0) as ASN1Encodable)
                    swapped = false
                    while (index != lastSwap) {
                        val b = getDEREncoded(set.elementAt(index + 1) as ASN1Encodable)
                        if (lessThanOrEqual(a, b)) {
                            a = b
                        } else {
                            val o = set.elementAt(index)
                            set.set(index, set.elementAt(index + 1))
                            set.set(index + 1, o)
                            swapped = true
                            swapIndex = index
                        }
                        index++
                    }
                    lastSwap = swapIndex
                }
            }
        }
    }

    override fun isConstructed(): Boolean {
        return true
    }

    @Throws(IOException::class)
    abstract override fun encode(out: ASN1OutputStream)
    override fun toString(): String {
        return set.toString()
    }

    override fun iterator(): MutableIterator<ASN1Encodable> {
        return Arrays.Iterator<ASN1Encodable>(toArray())
    }

    companion object {
        /**
         * return an ASN1Set from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         * @return an ASN1Set instance, or null.
         */
        fun getInstance(
            obj: Any
        ): ASN1Set? {
            if (obj is ASN1Set) {
                return obj
            } else if (obj is ASN1SetParser) {
                return getInstance(obj.toASN1Primitive())
            } else if (obj is ByteArray) {
                return try {
                    getInstance(fromByteArray((obj as ByteArray?)!!))
                } catch (e: IOException) {
                    throw IllegalArgumentException("failed to construct set from byte[]: " + e.message)
                }
            } else if (obj is ASN1Encodable) {
                val primitive = obj.toASN1Primitive()
                if (primitive is ASN1Set) {
                    return primitive
                }
            }
            throw IllegalArgumentException("unknown object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an ASN1 set from a tagged object. There is a special
         * case here, if an object appears to have been explicitly tagged on
         * reading but we were expecting it to be implicitly tagged in the
         * normal course of events it indicates that we lost the surrounding
         * set - so we need to add it back (this will happen if the tagged
         * object is a sequence that contains other sequences). If you are
         * dealing with implicitly tagged sets you really **should**
         * be using this method.
         *
         * @param obj the tagged object.
         * @param explicit true if the object is meant to be explicitly tagged
         * false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         * be converted.
         * @return an ASN1Set instance.
         */
        @JvmStatic
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1Set {
            if (explicit) {
                require(obj.mIsExplicit) { "object implicit - explicit expected." }
                return obj.getObject() as ASN1Set
            } else {
                //
                // constructed object which appears to be explicitly tagged
                // and it's really implicit means we have to add the
                // surrounding set.
                //
                if (obj.mIsExplicit) {
                    return if (obj is BERTaggedObject) {
                        BERSet(obj.getObject())
                    } else {
                        DLSet(obj.getObject())
                    }
                } else {
                    if (obj.getObject() is ASN1Set) {
                        return obj.getObject() as ASN1Set
                    }

                    //
                    // in this case the parser returns a sequence, convert it
                    // into a set.
                    //
                    if (obj.getObject() is ASN1Sequence) {
                        val s = obj.getObject() as ASN1Sequence
                        return if (obj is BERTaggedObject) {
                            BERSet(s.toArray())
                        } else {
                            DLSet(s.toArray())
                        }
                    }
                }
            }
            throw IllegalArgumentException("unknown object in getInstance: " + obj::class.simpleName)
        }
    }
}
