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
import com.mangala.wallet.bitcoinj.utils.Synchronized

/**
 * Note: this class is for processing DER/DL encoded sequences only.
 */
internal class LazyEncodedSequence() : ASN1Sequence() {

    override lateinit var encoded: ByteArray

    constructor(
        encoded: ByteArray
    ) : this() {
        this.encoded = encoded
    }
    private fun parse() {
        val en: Iterator<ASN1Encodable> = LazyConstructionEnumeration(encoded!!)
        while (en.hasNext()) {
            seq.add(en.next())
        }
//        encoded = null
    }

    @Synchronized
    override fun getObjectAt(index: Int): ASN1Encodable {
        if (encoded != null) {
            parse()
        }
        return super.getObjectAt(index)
    }

    @get:Synchronized
    override val objects: Iterator<ASN1Encodable>
        get() = if (encoded == null) {
            super.objects
        } else LazyConstructionEnumeration(encoded)

    @Synchronized
    override fun size(): Int {
        if (encoded != null) {
            parse()
        }
        return super.size()
    }

    override fun toDERObject(): ASN1Primitive? {
        if (encoded != null) {
            parse()
        }
        return super.toDERObject()
    }

    override fun toDLObject(): ASN1Primitive? {
        if (encoded != null) {
            parse()
        }
        return super.toDLObject()
    }

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        return if (encoded != null) {
            1 + StreamUtil.calculateBodyLength(encoded!!.size) + encoded!!.size
        } else {
            super.toDLObject()!!.encodedLength()
        }
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        if (encoded != null) {
            out.writeEncoded(BERTags.SEQUENCE or BERTags.CONSTRUCTED, encoded!!)
        } else {
            super.toDLObject()!!.encode(out)
        }
    }
}
