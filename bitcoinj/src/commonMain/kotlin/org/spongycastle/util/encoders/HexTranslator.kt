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

package org.spongycastle.util.encoders//package org.spongycastle.util.encoders
//
///**
// * Converters for going from hex to binary and back. Note: this class assumes ASCII processing.
// */
//class HexTranslator : Translator {
//    /**
//     * size of the output block on encoding produced by getDecodedBlockSize()
//     * bytes.
//     */
//    override fun getEncodedBlockSize(): Int {
//        return 2
//    }
//
//    override fun encode(
//        `in`: ByteArray,
//        inOff: Int,
//        length: Int,
//        out: ByteArray,
//        outOff: Int
//    ): Int {
//        var inOff = inOff
//        var i = 0
//        var j = 0
//        while (i < length) {
//            out[outOff + j] = hexTable[`in`[inOff].toInt() shr 4 and 0x0f]
//            out[outOff + j + 1] = hexTable[`in`[inOff].toInt() and 0x0f]
//            inOff++
//            i++
//            j += 2
//        }
//        return length * 2
//    }
//
//    /**
//     * size of the output block on decoding produced by getEncodedBlockSize()
//     * bytes.
//     */
//    override fun getDecodedBlockSize(): Int {
//        return 1
//    }
//
//    override fun decode(
//        `in`: ByteArray,
//        inOff: Int,
//        length: Int,
//        out: ByteArray,
//        outOff: Int
//    ): Int {
//        var outOff = outOff
//        val halfLength = length / 2
//        var left: Byte
//        var right: Byte
//        for (i in 0 until halfLength) {
//            left = `in`[inOff + i * 2]
//            right = `in`[inOff + i * 2 + 1]
//            if (left < 'a'.code.toByte()) {
//                out[outOff] = (left - '0'.code.toByte() shl 4).toByte()
//            } else {
//                out[outOff] = (left - 'a'.code.toByte() + 10 shl 4).toByte()
//            }
//            if (right < 'a'.code.toByte()) {
//                out[outOff].plus(right - '0'.code.toByte())
//            } else {
//                (out[outOff].plus((right - 'a'.code.toByte() + 10).toByte()))
//            }
//            outOff++
//        }
//        return halfLength
//    }
//
//
//    companion object {
//        private val hexTable = byteArrayOf(
//            '0'.code.toByte(),
//            '1'.code.toByte(),
//            '2'.code.toByte(),
//            '3'.code.toByte(),
//            '4'.code.toByte(),
//            '5'.code.toByte(),
//            '6'.code.toByte(),
//            '7'.code.toByte(),
//            '8'.code.toByte(),
//            '9'.code.toByte(),
//            'a'.code.toByte(),
//            'b'.code.toByte(),
//            'c'.code.toByte(),
//            'd'.code.toByte(),
//            'e'.code.toByte(),
//            'f'.code.toByte()
//        )
//    }
//}
