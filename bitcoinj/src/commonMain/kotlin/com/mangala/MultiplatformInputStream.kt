/*
 * Copyright (c) 1994, 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 * Modified from original source: https://github.com/openjdk/jdk
 */

package com.mangala

import okio.Closeable
import okio.EOFException
import okio.IOException
import com.mangala.wallet.bitcoinj.utils.Synchronized
import kotlin.math.min

abstract class MultiplatformInputStream: Closeable {

    private val MAX_SKIP_BUFFER_SIZE = 2048
    private val DEFAULT_BUFFER_SIZE = 8192

    @Throws(IOException::class)
    abstract fun read(): Int

    @Throws(IOException::class)
    open fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    @Throws(IOException::class)
    open fun read(b: ByteArray, off: Int, len: Int): Int {
        checkFromIndexSize(off, len, b.size)
        if (len == 0) {
            return 0
        }
        var c = read()
        if (c == -1) {
            return -1
        }
        b[off] = c.toByte()
        var i = 1
        try {
            while (i < len) {
                c = read()
                if (c == -1) {
                    break
                }
                b[off + i] = c.toByte()
                i++
            }
        } catch (ee: IOException) {
        }
        return i
    }

    @Throws(IOException::class)
    open fun readAllBytes(): ByteArray? {
        return readNBytes(Int.MAX_VALUE)
    }

    @Throws(IOException::class)
    open fun readNBytes(len: Int): ByteArray? {
        if (len < 0) {
            throw IllegalArgumentException("len < 0")
        }
        var bufs: MutableList<ByteArray>? = null
        var result: ByteArray? = null
        var total = 0
        var remaining = len
        var n: Int
        do {
            var buf = ByteArray(
                min(remaining.toDouble(), DEFAULT_BUFFER_SIZE.toDouble())
                    .toInt()
            )
            var nread = 0

            // read to EOF which may read more or less than buffer size
            while (read(
                    buf, nread,
                    min((buf.size - nread).toDouble(), remaining.toDouble()).toInt()
                ).also { n = it } > 0
            ) {
                nread += n
                remaining -= n
            }
            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw Exception("Required array size too large")
                }
                if (nread < buf.size) {
                    buf = buf.copyOfRange(0, nread)
                }
                total += nread
                if (result == null) {
                    result = buf
                } else {
                    if (bufs == null) {
                        bufs = ArrayList()
                        bufs.add(result)
                    }
                    bufs.add(buf)
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0)
        if (bufs == null) {
            if (result == null) {
                return ByteArray(0)
            }
            return if (result.size == total) result else result.copyOf(total)
        }
        result = ByteArray(total)
        var offset = 0
        remaining = total
        for (b in bufs) {
            val count = min(b.size.toDouble(), remaining.toDouble()).toInt()
            b.copyInto(result, destinationOffset = offset, startIndex = 0, endIndex = count)
            offset += count
            remaining -= count
        }
        return result
    }

    @Throws(IOException::class)
    open fun readNBytes(b: ByteArray, off: Int, len: Int): Int {
        checkFromIndexSize(off, len, b.size)
        var n = 0
        while (n < len) {
            val count = read(b, off + n, len - n)
            if (count < 0) break
            n += count
        }
        return n
    }

    @Throws(IOException::class)
    open fun skip(n: Long): Long {
        var remaining = n
        var nr: Int
        if (n <= 0) {
            return 0
        }
        val size = min(MAX_SKIP_BUFFER_SIZE.toDouble(), remaining.toDouble()).toInt()
        val skipBuffer = ByteArray(size)
        while (remaining > 0) {
            nr = read(skipBuffer, 0, min(size.toDouble(), remaining.toDouble()).toInt())
            if (nr < 0) {
                break
            }
            remaining -= nr.toLong()
        }
        return n - remaining
    }

    @Throws(IOException::class)
    fun skipNBytes(n: Long) {
        var n = n
        while (n > 0) {
            val ns = skip(n)
            if (ns > 0 && ns <= n) {
                // adjust number to skip
                n -= ns
            } else if (ns == 0L) { // no bytes skipped
                // read one byte to check for EOS
                if (read() == -1) {
                    throw EOFException()
                }
                // one byte read so decrement number to skip
                n--
            } else { // skipped negative or too many bytes
                throw IOException("Unable to skip exactly")
            }
        }
    }

    @Throws(IOException::class)
    open fun available(): Int {
        return 0
    }

    override fun close() {
    }

    @Synchronized
    open fun mark(readlimit: Int) {
    }

    @Synchronized
    @Throws(IOException::class)
    open fun reset() {
        throw IOException("mark/reset not supported")
    }

    open fun markSupported(): Boolean {
        return false
    }

    @Throws(IOException::class)
    open fun transferTo(out: MultiplatformOutputStream): Long {
        var transferred: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var read: Int
        while (this.read(buffer, 0, DEFAULT_BUFFER_SIZE)
                .also { read = it } >= 0
        ) {
            out.write(buffer, 0, read)
            transferred += read.toLong()
        }
        return transferred
    }
    
    companion object {
        private const val MAX_BUFFER_SIZE = Int.MAX_VALUE - 8
    }
}
