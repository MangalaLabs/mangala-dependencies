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
 * Parser DER EXTERNAL tagged objects.
 */
class DERExternalParser
/**
 * Base constructor.
 *
 * @param parser the underlying parser to read the DER EXTERNAL from.
 */(private val _parser: ASN1StreamParser) : ASN1Encodable, InMemoryRepresentable {
    @Throws(IOException::class)
    fun readObject(): ASN1Encodable {
        return _parser.readObject()!!
    }

    /**
     * Return an in-memory, encodable, representation of the EXTERNAL object.
     *
     * @return a DERExternal.
     * @throws IOException if there is an issue loading the data.
     */
    override fun getLoadedObject(): ASN1Primitive {
        return try {
            DERExternal(_parser.readVector())
        } catch (e: IllegalArgumentException) {
            throw ASN1Exception(e.message!!, e)
        }
    }

    /**
     * Return an DERExternal representing this parser and its contents.
     *
     * @return an DERExternal
     */
    override fun toASN1Primitive(): ASN1Primitive {
        return try {
            getLoadedObject()
        } catch (ioe: IOException) {
            throw ASN1ParsingException("unable to get DER object", ioe)
        } catch (ioe: IllegalArgumentException) {
            throw ASN1ParsingException("unable to get DER object", ioe)
        }
    }
}
