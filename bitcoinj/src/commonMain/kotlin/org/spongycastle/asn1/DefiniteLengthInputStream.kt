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
import org.spongycastle.util.Streams.readFully
import okio.IOException
import kotlin.math.min

/**
 * Parse data stream of expected ASN.1 data expecting definite-length encoding..
 */
class DefiniteLengthInputStream internal constructor(
    input: MultiplatformInputStream,
    length: Int
) : LimitedInputStream(input, length) {
    private val _originalLength: Int
    private var _remaining: Int

    init {
        require(length >= 0) { "negative lengths not allowed" }
        _originalLength = length
        _remaining = length
        if (length == 0) {
            setParentEofDetect(true)
        }
    }

    public override fun getRemaining(): Int {
        return _remaining
    }

    @Throws(IOException::class)
    override fun read(): Int {
        if (_remaining == 0) {
            return -1
        }
        val b = _in.read()
        if (b < 0) {
            throw EOFException("DEF length $_originalLength object truncated by $_remaining")
        }
        if (--_remaining == 0) {
            setParentEofDetect(true)
        }
        return b
    }

    @Throws(IOException::class)
    override fun read(buf: ByteArray, off: Int, len: Int): Int {
        if (_remaining == 0) {
            return -1
        }
        val toRead = min(len, _remaining)
        val numRead = _in.read(buf, off, toRead)
        if (numRead < 0) {
            throw EOFException("DEF length $_originalLength object truncated by $_remaining")
        }
        if (numRead.let { _remaining -= it; _remaining } == 0) {
            setParentEofDetect(true)
        }
        return numRead
    }

    @Throws(IOException::class)
    fun toByteArray(): ByteArray {
        if (_remaining == 0) {
            return EMPTY_BYTES
        }
        val bytes = ByteArray(_remaining)
        if (readFully(_in, bytes).let { _remaining -= it; _remaining } != 0) {
            throw EOFException("DEF length $_originalLength object truncated by $_remaining")
        }
        setParentEofDetect(true)
        return bytes
    }

    companion object {
        private val EMPTY_BYTES = ByteArray(0)
    }
}
