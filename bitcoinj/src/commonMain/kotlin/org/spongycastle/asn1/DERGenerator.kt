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
//
//import org.spongycastle.util.Streams.readAll
//import java.io.ByteArrayOutputStream
//import okio.IOException
//import java.io.InputStream
//import java.io.OutputStream
//
//abstract class DERGenerator : ASN1Generator {
//    private var _tagged = false
//    private var _isExplicit = false
//    private var _tagNo = 0
//
//    protected constructor(
//        out: OutputStream
//    ) : super(out!!)
//
//    constructor(
//        out: OutputStream,
//        tagNo: Int,
//        isExplicit: Boolean
//    ) : super(out!!) {
//        _tagged = true
//        _isExplicit = isExplicit
//        _tagNo = tagNo
//    }
//
//    @Throws(IOException::class)
//    private fun writeLength(
//        out: OutputStream,
//        length: Int
//    ) {
//        if (length > 127) {
//            var size = 1
//            var `val` = length
//            while (8.let { `val` = `val` ushr it; `val` } != 0) {
//                size++
//            }
//            out.write((size or 0x80).toByte().toInt())
//            var i = (size - 1) * 8
//            while (i >= 0) {
//                out.write((length shr i).toByte().toInt())
//                i -= 8
//            }
//        } else {
//            out.write(length.toByte().toInt())
//        }
//    }
//
//    @Throws(IOException::class)
//    fun writeDEREncoded(
//        out: OutputStream,
//        tag: Int,
//        bytes: ByteArray
//    ) {
//        out.write(tag)
//        writeLength(out, bytes.size)
//        out.write(bytes)
//    }
//
//    @Throws(IOException::class)
//    fun writeDEREncoded(
//        tag: Int,
//        bytes: ByteArray
//    ) {
//        if (_tagged) {
//            val tagNum = _tagNo or BERTags.TAGGED
//            if (_isExplicit) {
//                val newTag = _tagNo or BERTags.CONSTRUCTED or BERTags.TAGGED
//                val bOut = ByteArrayOutputStream()
//                writeDEREncoded(bOut, tag, bytes)
//                writeDEREncoded(_out, newTag, bOut.toByteArray())
//            } else {
//                if (tag and BERTags.CONSTRUCTED != 0) {
//                    writeDEREncoded(_out, tagNum or BERTags.CONSTRUCTED, bytes)
//                } else {
//                    writeDEREncoded(_out, tagNum, bytes)
//                }
//            }
//        } else {
//            writeDEREncoded(_out, tag, bytes)
//        }
//    }
//
//    @Throws(IOException::class)
//    fun writeDEREncoded(
//        out: OutputStream,
//        tag: Int,
//        input: InputStream
//    ) {
//        writeDEREncoded(out, tag, readAll(input))
//    }
//}
