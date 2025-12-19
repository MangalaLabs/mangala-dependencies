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
//import org.spongycastle.util.Strings
//import java.io.ByteArrayOutputStream
//import okio.IOException
//import java.io.OutputStream
//
///**
// * Utility class for converting Base64 data to bytes and back again.
// */
//object Base64 {
//    private val encoder: Encoder = Base64Encoder()
//    @JvmOverloads
//    fun toBase64String(
//        data: ByteArray,
//        off: Int = 0,
//        length: Int = data.size
//    ): String {
//        val encoded = encode(data, off, length)
//        return Strings.fromByteArray(encoded)
//    }
//    /**
//     * encode the input data producing a base 64 encoded byte array.
//     *
//     * @return a byte array containing the base 64 encoded data.
//     */
//    /**
//     * encode the input data producing a base 64 encoded byte array.
//     *
//     * @return a byte array containing the base 64 encoded data.
//     */
//    @JvmOverloads
//    fun encode(
//        data: ByteArray,
//        off: Int = 0,
//        length: Int = data.size
//    ): ByteArray {
//        val len = (length + 2) / 3 * 4
//        val bOut = ByteArrayOutputStream(len)
//        try {
//            encoder.encode(data, off, length, bOut)
//        } catch (e: Exception) {
//            throw EncoderException("exception encoding base64 string: " + e.message, e)
//        }
//        return bOut.toByteArray()
//    }
//
//    /**
//     * Encode the byte data to base 64 writing it to the given output stream.
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
//
//    /**
//     * Encode the byte data to base 64 writing it to the given output stream.
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
//
//    /**
//     * decode the base 64 encoded input data. It is assumed the input data is valid.
//     *
//     * @return a byte array representing the decoded data.
//     */
//    fun decode(
//        data: ByteArray
//    ): ByteArray {
//        val len = data.size / 4 * 3
//        val bOut = ByteArrayOutputStream(len)
//        try {
//            encoder.decode(data, 0, data.size, bOut)
//        } catch (e: Exception) {
//            throw DecoderException("unable to decode base64 data: " + e.message, e)
//        }
//        return bOut.toByteArray()
//    }
//
//    /**
//     * decode the base 64 encoded String data - whitespace will be ignored.
//     *
//     * @return a byte array representing the decoded data.
//     */
//    fun decode(
//        data: String
//    ): ByteArray {
//        val len = data.length / 4 * 3
//        val bOut = ByteArrayOutputStream(len)
//        try {
//            encoder.decode(data, bOut)
//        } catch (e: Exception) {
//            throw DecoderException("unable to decode base64 string: " + e.message, e)
//        }
//        return bOut.toByteArray()
//    }
//
//    /**
//     * decode the base 64 encoded String data writing it to the given output stream,
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
//
//    /**
//     * Decode to an output stream;
//     *
//     * @param base64Data       The source data.
//     * @param start            Start position.
//     * @param length           the length.
//     * @param out The output stream to write to.
//     */
//    fun decode(base64Data: ByteArray, start: Int, length: Int, out: OutputStream): Int {
//        return try {
//            encoder.decode(base64Data, start, length, out)
//        } catch (e: Exception) {
//            throw DecoderException("unable to decode base64 data: " + e.message, e)
//        }
//    }
//}
