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

/**
 * Parser for indefinite-length tagged objects.
 */
class BERTaggedObjectParser internal constructor(
    /**
     * Return true if this tagged object is marked as constructed.
     *
     * @return true if constructed, false otherwise.
     */
    val isConstructed: Boolean,
    private val _tagNumber: Int,
    private val _parser: ASN1StreamParser
) : ASN1TaggedObjectParser {
    /**
     * Return the tag number associated with this object.
     *
     * @return the tag number.
     */
    override fun tagNo(): Int {
        return _tagNumber
    }

    /**
     * Return an object parser for the contents of this tagged object.
     *
     * @param tag the actual tag number of the object (needed if implicit).
     * @param isExplicit true if the contained object was explicitly tagged, false if implicit.
     * @return an ASN.1 encodable object parser.
     * @throws IOException if there is an issue building the object parser from the stream.
     */
    @Throws(IOException::class)
    override fun getObjectParser(
        tag: Int,
        isExplicit: Boolean
    ): ASN1Encodable {
        if (isExplicit) {
            if (!isConstructed) {
                throw IOException("Explicit tags must be constructed (see X.690 8.14.2)")
            }
            return _parser.readObject()!!
        }
        return _parser.readImplicit(isConstructed, tag)
    }

    /**
     * Return an in-memory, encodable, representation of the tagged object.
     *
     * @return an ASN1TaggedObject.
     * @throws IOException if there is an issue loading the data.
     */
    override fun getLoadedObject(): ASN1Primitive {
        return _parser.readTaggedObject(isConstructed, _tagNumber)
    }

    /**
     * Return an ASN1TaggedObject representing this parser and its contents.
     *
     * @return an ASN1TaggedObject
     */
    override fun toASN1Primitive(): ASN1Primitive {
        return try {
            this.getLoadedObject()
        } catch (e: IOException) {
            throw ASN1ParsingException(e.message!!)
        }
    }
}
