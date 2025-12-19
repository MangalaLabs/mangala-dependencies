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

package org.spongycastle.util.encoders

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Utility class for converting hex data to bytes and back again.
 */
@OptIn(ExperimentalStdlibApi::class)
object Hex {
//    private val encoder: Encoder = HexEncoder()
    @JvmOverloads
    @JvmStatic
    fun toHexString(
        data: ByteArray,
//        off: Int = 0,
//        length: Int = data.size
    ): String {
        return data.toHexString()
    }
    /**
     * encode the input data producing a Hex encoded byte array.
     *
     * @return a byte array containing the Hex encoded data.
     */
    /**
     * encode the input data producing a Hex encoded byte array.
     *
     * @return a byte array containing the Hex encoded data.
     */
    @JvmOverloads
    @JvmStatic
    fun encode(
        data: ByteArray,
//        off: Int = 0,
//        length: Int = data.size
    ): ByteArray {
        return data.toHexString().encodeToByteArray()
    }

//    /**
//     * Hex encode the byte data writing it to the given output stream.
//     *
//     * @return the number of bytes produced.
//     */
//    @Throws(IOException::class)
//    fun encode(
//        data: ByteArray,
//        out: OutputStream
//    ): Int {
//        return encoder.encode(data, 0, data.size, out)
//    }

//    /**
//     * Hex encode the byte data writing it to the given output stream.
//     *
//     * @return the number of bytes produced.
//     */
//    @Throws(IOException::class)
//    fun encode(
//        data: ByteArray,
//        off: Int,
//        length: Int,
//        out: OutputStream
//    ): Int {
//        return encoder.encode(data, off, length, out)
//    }

    /**
     * decode the Hex encoded input data. It is assumed the input data is valid.
     *
     * @return a byte array representing the decoded data.
     */
//    fun decode(
//        data: ByteArray
//    ): ByteArray {
//        val bOut = ByteArrayOutputStream()
//        try {
//            encoder.decode(data, 0, data.size, bOut)
//        } catch (e: Exception) {
//            throw DecoderException("exception decoding Hex data: " + e.message, e)
//        }
//        return bOut.toByteArray()
//    }

    /**
     * decode the Hex encoded String data - whitespace will be ignored.
     *
     * @return a byte array representing the decoded data.
     */
    fun decode(
        data: String
    ): ByteArray {
        return data.hexToByteArray()
    }

//    /**
//     * decode the Hex encoded String data writing it to the given output stream,
//     * whitespace characters will be ignored.
//     *
//     * @return the number of bytes produced.
//     */
//    @Throws(IOException::class)
//    fun decode(
//        data: String,
//        out: OutputStream
//    ): Int {
//        return encoder.decode(data, out)
//    }
}
