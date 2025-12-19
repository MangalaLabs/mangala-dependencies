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

/**
 * Class for breaking up an OID into it's component tokens, ala
 * java.util.StringTokenizer. We need this class as some of the
 * lightweight Java environment don't support classes like
 * StringTokenizer.
 */
class OIDTokenizer
/**
 * Base constructor.
 *
 * @param oid the string representation of the OID.
 */(
    private val oid: String
) {
    private var index = 0

    /**
     * Return whether or not there are more tokens in this tokenizer.
     *
     * @return true if there are more tokens, false otherwise.
     */
    fun hasMoreTokens(): Boolean {
        return index != -1
    }

    /**
     * Return the next token in the underlying String.
     *
     * @return the next token.
     */
    fun nextToken(): String {
        if (index == -1) {
            return ""
        }
        val token: String
        val end = oid.indexOf('.', index)
        if (end == -1) {
            token = oid.substring(index)
            index = -1
            return token
        }
        token = oid.substring(index, end)
        index = end + 1
        return token
    }
}
