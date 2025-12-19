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

internal class ConstructedOctetStream(
    private val _parser: ASN1StreamParser
) : MultiplatformInputStream() {
    private var _first = true
    private var _currentStream: MultiplatformInputStream? = null
    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (_currentStream == null) {
            if (!_first) {
                return -1
            }
            val s = _parser.readObject() as ASN1OctetStringParser? ?: return -1
            _first = false
            _currentStream = s.getOctetStream()
        }
        var totalRead = 0
        while (true) {
            val numRead = _currentStream!!.read(b, off + totalRead, len - totalRead)
            if (numRead >= 0) {
                totalRead += numRead
                if (totalRead == len) {
                    return totalRead
                }
            } else {
                val aos = _parser.readObject() as ASN1OctetStringParser?
                if (aos == null) {
                    _currentStream = null
                    return if (totalRead < 1) -1 else totalRead
                }
                _currentStream = aos.getOctetStream()
            }
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        if (_currentStream == null) {
            if (!_first) {
                return -1
            }
            val s = _parser.readObject() as ASN1OctetStringParser? ?: return -1
            _first = false
            _currentStream = s.getOctetStream()
        }
        while (true) {
            val b = _currentStream!!.read()
            if (b >= 0) {
                return b
            }
            val s = _parser.readObject() as ASN1OctetStringParser?
            if (s == null) {
                _currentStream = null
                return -1
            }
            _currentStream = s.getOctetStream()
        }
    }
}
