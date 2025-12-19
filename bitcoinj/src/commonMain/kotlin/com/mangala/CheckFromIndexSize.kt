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

fun checkFromIndexSize(fromIndex: Int, size: Int, length: Int): Int {
    if (fromIndex < 0 || size < 0 || fromIndex > length - size) {
        throw IndexOutOfBoundsException("Range [$fromIndex, ${fromIndex + size}) out of bounds for length $length")
    }
    return fromIndex
}
