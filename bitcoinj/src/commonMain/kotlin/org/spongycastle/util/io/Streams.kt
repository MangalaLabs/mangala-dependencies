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

//package org.spongycastle.util.io
//
//import kotlin.jvm.JvmStatic
//
////import java.io.ByteArrayOutputStream
////import okio.IOException
////import java.io.InputStream
////import java.io.OutputStream
//
///**
// * Utility methods to assist with stream processing.
// */
//object Streams {
//    private const val BUFFER_SIZE = 4096
//
//    /**
//     * Read stream till EOF is encountered.
//     *
//     * @param inStr stream to be emptied.
//     * @throws IOException in case of underlying IOException.
//     */
////    @Throws(IOException::class)
////    fun drain(inStr: InputStream) {
////        val bs = ByteArray(BUFFER_SIZE)
////        while (inStr.read(bs, 0, bs.size) >= 0) {
////        }
////    }
//
//    /**
//     * Read stream fully, returning contents in a byte array.
//     *
//     * @param inStr stream to be read.
//     * @return a byte array representing the contents of inStr.
//     * @throws IOException in case of underlying IOException.
//     */
//    @JvmStatic
//    @Throws(IOException::class)
//    fun readAll(inStr: InputStream): ByteArray {
//        val buf = ByteArrayOutputStream()
//        pipeAll(inStr, buf)
//        return buf.toByteArray()
//    }
//
//    /**
//     * Read from inStr up to a maximum number of bytes, throwing an exception if more the maximum amount
//     * of requested data is available.
//     *
//     * @param inStr stream to be read.
//     * @param limit maximum number of bytes that can be read.
//     * @return a byte array representing the contents of inStr.
//     * @throws IOException in case of underlying IOException, or if limit is reached on inStr still has data in it.
//     */
////    @Throws(IOException::class)
////    fun readAllLimited(inStr: InputStream, limit: Int): ByteArray {
////        val buf = ByteArrayOutputStream()
////        pipeAllLimited(inStr, limit.toLong(), buf)
////        return buf.toByteArray()
////    }
//    /**
//     * Fully read in len's bytes of data into buf, or up to EOF, whichever occurs first,
//     *
//     * @param inStr the stream to be read.
//     * @param buf the buffer to be read into.
//     * @param off offset into buf to start putting bytes into.
//     * @param len  the number of bytes to be read.
//     * @return the number of bytes read into the buffer.
//     * @throws IOException in case of underlying IOException.
//     */
//    /**
//     * Fully read in buf's length in data, or up to EOF, whichever occurs first,
//     *
//     * @param inStr the stream to be read.
//     * @param buf the buffer to be read into.
//     * @return the number of bytes read into the buffer.
//     * @throws IOException in case of underlying IOException.
//     */
//    @JvmOverloads
//    @JvmStatic
//    @Throws(IOException::class)
//    fun readFully(inStr: InputStream, buf: ByteArray, off: Int = 0, len: Int = buf.size): Int {
//        var totalRead = 0
//        while (totalRead < len) {
//            val numRead = inStr.read(buf, off + totalRead, len - totalRead)
//            if (numRead < 0) {
//                break
//            }
//            totalRead += numRead
//        }
//        return totalRead
//    }
//
//    /**
//     * Write the full contents of inStr to the destination stream outStr.
//     *
//     * @param inStr source input stream.
//     * @param outStr destination output stream.
//     * @throws IOException in case of underlying IOException.
//     */
//    @Throws(IOException::class)
//    fun pipeAll(inStr: InputStream, outStr: OutputStream) {
//        val bs = ByteArray(BUFFER_SIZE)
//        var numRead: Int
//        while (inStr.read(bs, 0, bs.size).also { numRead = it } >= 0) {
//            outStr.write(bs, 0, numRead)
//        }
//    }
//
//    /**
//     * Write up to limit bytes of data from inStr to the destination stream outStr.
//     *
//     * @param inStr source input stream.
//     * @param limit the maximum number of bytes allowed to be read.
//     * @param outStr destination output stream.
//     * @throws IOException in case of underlying IOException, or if limit is reached on inStr still has data in it.
//     */
//    @Throws(IOException::class)
//    fun pipeAllLimited(inStr: InputStream, limit: Long, outStr: OutputStream): Long {
//        var total: Long = 0
//        val bs = ByteArray(BUFFER_SIZE)
//        var numRead: Int
//        while (inStr.read(bs, 0, bs.size).also { numRead = it } >= 0) {
//            if (limit - total < numRead) {
//                throw StreamOverflowException("Data Overflow")
//            }
//            total += numRead.toLong()
//            outStr.write(bs, 0, numRead)
//        }
//        return total
//    }
//
////    @Throws(IOException::class)
////    fun writeBufTo(buf: ByteArrayOutputStream, output: OutputStream?) {
////        buf.writeTo(output)
////    }
//}
