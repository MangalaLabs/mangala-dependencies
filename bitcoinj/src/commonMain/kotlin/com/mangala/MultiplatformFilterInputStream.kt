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

open class MultiplatformFilterInputStream(val _in: MultiplatformInputStream): MultiplatformInputStream() {

    override fun read(): Int {
        return _in.read()
    }

    override fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return _in.read(b, off, len)
    }

    override fun skip(n: Long): Long {
        return _in.skip(n)
    }

    override fun available(): Int {
        return _in.available()
    }

    override fun close() {
        _in.close()
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        _in.mark(readlimit)
    }

    @Synchronized
    override fun reset() {
        _in.reset()
    }

    override fun markSupported(): Boolean {
        return _in.markSupported()
    }
}
