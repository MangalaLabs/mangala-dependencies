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

import com.mangala.MultiplatformByteArrayInputStream
import com.mangala.MultiplatformInputStream
import com.mangala.getMaxMemory
import okio.IOException

internal object StreamUtil {
    private val MAX_MEMORY = getMaxMemory()

    /**
     * Find out possible longest length...
     *
     * @param `in` input stream of interest
     * @return length calculation or MAX_VALUE.
     */
    fun findLimit(inputStream: MultiplatformInputStream): Int {
        if (inputStream is LimitedInputStream) {
            return inputStream.getRemaining()
        } else if (inputStream is ASN1InputStream) {
            return inputStream.limit
        } else if (inputStream is MultiplatformByteArrayInputStream) {
            return inputStream.available()
        }
//        else if (inputStream is FileInputStream) {
//            try {
//                val channel = inputStream.channel
//                val size = channel?.size() ?: Int.MAX_VALUE.toLong()
//                if (size < Int.MAX_VALUE) {
//                    return size.toInt()
//                }
//            } catch (e: IOException) {
//                 ignore - they'll find out soon enough!
//            }
//        }
        return if (MAX_MEMORY > Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else MAX_MEMORY.toInt()
    }

    fun calculateBodyLength(
        length: Int
    ): Int {
        var count = 1
        if (length > 127) {
            var size = 1
            var `val` = length
            while (8.let { `val` = `val` ushr it; `val` } != 0) {
                size++
            }
            var i = (size - 1) * 8
            while (i >= 0) {
                count++
                i -= 8
            }
        }
        return count
    }

    @Throws(IOException::class)
    fun calculateTagLength(tagNo: Int): Int {
        var tagNo = tagNo
        var length = 1
        if (tagNo >= 31) {
            if (tagNo < 128) {
                length++
            } else {
                val stack = ByteArray(5)
                var pos = stack.size
                stack[--pos] = (tagNo and 0x7F).toByte()
                do {
                    tagNo = tagNo shr 7
                    stack[--pos] = (tagNo and 0x7F or 0x80).toByte()
                } while (tagNo > 127)
                length += stack.size - pos
            }
        }
        return length
    }
}
