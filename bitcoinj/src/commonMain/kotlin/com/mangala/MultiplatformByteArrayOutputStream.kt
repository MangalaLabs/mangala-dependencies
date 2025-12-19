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

import okio.IOException
import com.mangala.wallet.bitcoinj.utils.Synchronized

class MultiplatformByteArrayOutputStream(
    val size: Int
): MultiplatformOutputStream() {
    protected var buf: ByteArray
    protected var count = 0

    constructor(): this(32)

    init {
        if (size < 0) {
            throw IllegalArgumentException("Negative initial size: " + size)
        }
        buf = ByteArray(size)
    }

    private fun ensureCapacity(minCapacity: Int) {
        // overflow-conscious code
        if (minCapacity - buf.size > 0) grow(minCapacity)
    }


    private fun grow(minCapacity: Int) {
        // overflow-conscious code
        val oldCapacity: Int = buf.size
        var newCapacity = oldCapacity shl 1
        if (newCapacity - minCapacity < 0) newCapacity = minCapacity
        if (newCapacity - MAX_ARRAY_SIZE > 0) newCapacity = hugeCapacity(minCapacity)
        buf = buf.copyOf(newCapacity)
    }

    @Synchronized
    override fun write(b: Int) {
        ensureCapacity(count + 1)
        buf[count] = b.toByte()
        count += 1
    }

    @Synchronized
    override fun write(b: ByteArray, off: Int, len: Int) {
        checkFromIndexSize(off, len, b.size)
        ensureCapacity(count + len)
        b.copyInto(destination = buf, destinationOffset = count, startIndex = off, endIndex = off + len)
        count += len
    }

    fun writeBytes(b: ByteArray) {
        write(b, 0, b.size)
    }

    @Synchronized
    @Throws(IOException::class)
    fun writeTo(out: MultiplatformOutputStream) {
        out.write(buf, 0, count)
    }

    @Synchronized
    fun reset() {
        count = 0
    }

    @Synchronized
    fun toByteArray(): ByteArray {
        return buf.copyOf(count)
    }

    @Synchronized
    fun size(): Int {
        return count
    }

    @Synchronized
    override fun toString(): String {
        return buf.joinToString(separator = "") { it.toString() }
    }

    override fun close() {
    }

    companion object {
        private const val MAX_ARRAY_SIZE = Int.MAX_VALUE - 8

        private fun hugeCapacity(minCapacity: Int): Int {
            if (minCapacity < 0) // overflow
                throw Exception("OutOfMemoryError")
            return if (minCapacity > MAX_ARRAY_SIZE) Int.MAX_VALUE else MAX_ARRAY_SIZE
        }
    }
}
