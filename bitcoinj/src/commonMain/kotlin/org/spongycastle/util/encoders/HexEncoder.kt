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
//import okio.IOException
//import java.io.OutputStream
//
///**
// * A streaming Hex encoder.
// */
//class HexEncoder : Encoder {
//    protected val encodingTable = byteArrayOf(
//        '0'.code.toByte(),
//        '1'.code.toByte(),
//        '2'.code.toByte(),
//        '3'.code.toByte(),
//        '4'.code.toByte(),
//        '5'.code.toByte(),
//        '6'.code.toByte(),
//        '7'.code.toByte(),
//        '8'.code.toByte(),
//        '9'.code.toByte(),
//        'a'.code.toByte(),
//        'b'.code.toByte(),
//        'c'.code.toByte(),
//        'd'.code.toByte(),
//        'e'.code.toByte(),
//        'f'.code.toByte()
//    )
//
//    /*
//     * set up the decoding table.
//     */
//    protected val decodingTable = ByteArray(128)
//    protected fun initialiseDecodingTable() {
//        for (i in decodingTable.indices) {
//            decodingTable[i] = 0xff.toByte()
//        }
//        for (i in encodingTable.indices) {
//            decodingTable[encodingTable[i].toInt()] = i.toByte()
//        }
//        decodingTable['A'.code] = decodingTable['a'.code]
//        decodingTable['B'.code] = decodingTable['b'.code]
//        decodingTable['C'.code] = decodingTable['c'.code]
//        decodingTable['D'.code] = decodingTable['d'.code]
//        decodingTable['E'.code] = decodingTable['e'.code]
//        decodingTable['F'.code] = decodingTable['f'.code]
//    }
//
//    init {
//        initialiseDecodingTable()
//    }
//
//    /**
//     * encode the input data producing a Hex output stream.
//     *
//     * @return the number of bytes produced.
//     */
//    @Throws(IOException::class)
//    override fun encode(
//        data: ByteArray,
//        off: Int,
//        length: Int,
//        out: OutputStream
//    ): Int {
//        for (i in off until off + length) {
//            val v = data[i].toInt() and 0xff
//            out.write(encodingTable[v ushr 4].toInt())
//            out.write(encodingTable[v and 0xf].toInt())
//        }
//        return length * 2
//    }
//
//    /**
//     * decode the Hex encoded byte data writing it to the given output stream,
//     * whitespace characters will be ignored.
//     *
//     * @return the number of bytes produced.
//     */
//    @Throws(IOException::class)
//    override fun decode(
//        data: ByteArray,
//        off: Int,
//        length: Int,
//        out: OutputStream
//    ): Int {
//        var b1: Byte
//        var b2: Byte
//        var outLen = 0
//        var end = off + length
//        while (end > off) {
//            if (!ignore(Char(data[end - 1].toUShort()))) {
//                break
//            }
//            end--
//        }
//        var i = off
//        while (i < end) {
//            while (i < end && ignore(Char(data[i].toUShort()))) {
//                i++
//            }
//            b1 = decodingTable[data[i++].toInt()]
//            while (i < end && ignore(Char(data[i].toUShort()))) {
//                i++
//            }
//            b2 = decodingTable[data[i++].toInt()]
//            if (b1.toInt() or b2.toInt() < 0) {
//                throw IOException("invalid characters encountered in Hex data")
//            }
//            out.write(b1.toInt() shl 4 or b2.toInt())
//            outLen++
//        }
//        return outLen
//    }
//
//    /**
//     * decode the Hex encoded String data writing it to the given output stream,
//     * whitespace characters will be ignored.
//     *
//     * @return the number of bytes produced.
//     */
//    @Throws(IOException::class)
//    override fun decode(
//        data: String,
//        out: OutputStream
//    ): Int {
//        var b1: Byte
//        var b2: Byte
//        var length = 0
//        var end = data.length
//        while (end > 0) {
//            if (!ignore(data[end - 1])) {
//                break
//            }
//            end--
//        }
//        var i = 0
//        while (i < end) {
//            while (i < end && ignore(data[i])) {
//                i++
//            }
//            b1 = decodingTable[data[i++].code]
//            while (i < end && ignore(data[i])) {
//                i++
//            }
//            b2 = decodingTable[data[i++].code]
//            if (b1.toInt() or b2.toInt() < 0) {
//                throw IOException("invalid characters encountered in Hex string")
//            }
//            out.write(b1.toInt() shl 4 or b2.toInt())
//            length++
//        }
//        return length
//    }
//
//    companion object {
//        private fun ignore(
//            c: Char
//        ): Boolean {
//            return c == '\n' || c == '\r' || c == '\t' || c == ' '
//        }
//    }
//}
