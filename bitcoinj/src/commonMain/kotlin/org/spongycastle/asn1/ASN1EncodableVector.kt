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

/**
 * Mutable class for building ASN.1 constructed objects such as SETs or SEQUENCEs.
 */
class ASN1EncodableVector
/**
 * Base constructor.
 */
{
    private val v: ConcurrentMutableList<ASN1Encodable> = ConcurrentMutableList<ASN1Encodable>()

    /**
     * Add an encodable to the vector.
     *
     * @param obj the encodable to add.
     */
    fun add(obj: ASN1Encodable) {
        v.add(obj)
    }

    /**
     * Add the contents of another vector.
     *
     * @param other the vector to add.
     */
    fun addAll(other: ASN1EncodableVector) {
        val en = other.v.iterator()
        while (en.hasNext()) {
            v.add(en.next())
        }
    }

    /**
     * Return the object at position i in this vector.
     *
     * @param i the index of the object of interest.
     * @return the object at position i.
     */
    operator fun get(i: Int): ASN1Encodable {
        return v.elementAt(i) as ASN1Encodable
    }

    /**
     * Return the size of the vector.
     *
     * @return the object count in the vector.
     */
    fun size(): Int {
        return v.size
    }
}
