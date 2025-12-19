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

import com.mangala.wallet.bitcoinj.utils.Synchronized
import kotlin.math.min

class MultiplatformByteArrayInputStream(
    protected val buf: ByteArray,
    protected var pos: Int = 0,
    protected var mark: Int = 0,
    protected val count: Int = buf.size
): MultiplatformInputStream() {

    constructor(buf: ByteArray) : this(buf, 0, 0, buf.size)
    constructor(buf: ByteArray, offset: Int, length: Int) : this(buf, offset, mark = offset, count = min((offset + length), buf.size))

    @Synchronized
    override fun read(): Int {
        return if (pos < count) buf[pos++].toInt() and 0xff else -1
    }

    @Synchronized
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var len = len
        checkFromIndexSize(off, len, b.size)
        if (pos >= count) {
            return -1
        }
        val avail = count - pos
        if (len > avail) {
            len = avail
        }
        if (len <= 0) {
            return 0
        }
        buf.copyInto(b, destinationOffset = off, startIndex = pos, endIndex = pos + len)
        pos += len
        return len
    }

    @Synchronized
    override fun readAllBytes(): ByteArray {
        val result = buf.copyOfRange(fromIndex = pos, toIndex = count)
        pos = count
        return result
    }

    override fun readNBytes(b: ByteArray, off: Int, len: Int): Int {
        val n = read(b, off, len)
        return if (n == -1) 0 else n
    }

    @Synchronized
    override fun transferTo(out: MultiplatformOutputStream): Long {
        val len = count - pos
        out.write(buf, pos, len)
        pos = count
        return len.toLong()
    }

    @Synchronized
    override fun skip(n: Long): Long {
        var k = (count - pos).toLong()
        if (n < k) {
            k = if (n < 0) 0 else n
        }
        pos += k.toInt()
        return k
    }

    @Synchronized
    override fun available(): Int {
        return count - pos
    }

    override fun markSupported(): Boolean {
        return true
    }

    override fun mark(readlimit: Int) {
        mark = pos
    }

    @Synchronized
    override fun reset() {
        pos = mark
    }

    override fun close() {
    }
}
