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
import okio.EOFException
import okio.IOException

internal class IndefiniteLengthInputStream(
    inputStream: MultiplatformInputStream,
    limit: Int
) : LimitedInputStream(inputStream, limit) {
    private var _b1: Int
    private var _b2: Int
    private var _eofReached = false
    private var _eofOn00 = true

    init {
        _b1 = inputStream.read()
        _b2 = inputStream.read()
        if (_b2 < 0) {
            // Corrupted stream
            throw EOFException()
        }
        checkForEof()
    }

    fun setEofOn00(
        eofOn00: Boolean
    ) {
        _eofOn00 = eofOn00
        checkForEof()
    }

    private fun checkForEof(): Boolean {
        if (!_eofReached && _eofOn00 && _b1 == 0x00 && _b2 == 0x00) {
            _eofReached = true
            setParentEofDetect(true)
        }
        return _eofReached
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        // Only use this optimisation if we aren't checking for 00
        if (_eofOn00 || len < 3) {
            return super.read(b, off, len)
        }
        if (_eofReached) {
            return -1
        }
        val numRead = _in.read(b, off + 2, len - 2)
        if (numRead < 0) {
            // Corrupted stream
            throw EOFException()
        }
        b[off] = _b1.toByte()
        b[off + 1] = _b2.toByte()
        _b1 = _in.read()
        _b2 = _in.read()
        if (_b2 < 0) {
            // Corrupted stream
            throw EOFException()
        }
        return numRead + 2
    }

    @Throws(IOException::class)
    override fun read(): Int {
        if (checkForEof()) {
            return -1
        }
        val b = _in.read()
        if (b < 0) {
            // Corrupted stream
            throw EOFException()
        }
        val v = _b1
        _b1 = _b2
        _b2 = b
        return v
    }

    override fun getRemaining(): Int {
        return mRemaining
    }
}
