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

import com.mangala.MultiplatformInputStream
import okio.IOException

/**
 * Parser for DER encoded OCTET STRINGS
 */
class DEROctetStringParser internal constructor(
    private val stream: DefiniteLengthInputStream
) : ASN1OctetStringParser {
    /**
     * Return an InputStream representing the contents of the OCTET STRING.
     *
     * @return an InputStream with its source as the OCTET STRING content.
     */
    override fun getOctetStream(): MultiplatformInputStream {
        return stream
    }

    /**
     * Return an in-memory, encodable, representation of the OCTET STRING.
     *
     * @return a DEROctetString.
     * @throws IOException if there is an issue loading the data.
     */
    override fun getLoadedObject(): ASN1Primitive {
        return DEROctetString(stream.toByteArray())
    }

    /**
     * Return an DEROctetString representing this parser and its contents.
     *
     * @return an DEROctetString
     */
    override fun toASN1Primitive(): ASN1Primitive {
        return try {
            getLoadedObject()
        } catch (e: IOException) {
            throw ASN1ParsingException(
                "IOException converting stream to byte array: " + e.message,
                e
            )
        }
    }
}
