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

package org.spongycastle.util

//import java.io.ByteArrayOutputStream
//import okio.IOException
//import java.io.OutputStream
//import java.security.AccessController
//import java.security.PrivilegedAction
//import java.util.Vector
//import com.mangala.getSystemLineSeparator
import com.mangala.wallet.bitcoinj.utils.getByteArray
import kotlin.jvm.JvmStatic

/**
 * String utilities.
 */
object Strings {

//    private var LINE_SEPARATOR: String = getSystemLineSeparator()


    @JvmStatic
    fun fromUTF8ByteArray(bytes: ByteArray): String {
        var i = 0
        var length = 0
        while (i < bytes.size) {
            length++
            i += if (bytes[i].toInt() and 0xf0 == 0xf0) {
                // surrogate pair
                length++
                4
            } else if (bytes[i].toInt() and 0xe0 == 0xe0) {
                3
            } else if (bytes[i].toInt() and 0xc0 == 0xc0) {
                2
            } else {
                1
            }
        }
        val cs = CharArray(length)
        i = 0
        length = 0
        while (i < bytes.size) {
            var ch: Char
            if (bytes[i].toInt() and 0xf0 == 0xf0) {
                val codePoint =
                    bytes[i].toInt() and 0x03 shl 18 or (bytes[i + 1].toInt() and 0x3F shl 12) or (bytes[i + 2].toInt() and 0x3F shl 6) or (bytes[i + 3].toInt() and 0x3F)
                val U = codePoint - 0x10000
                val W1 = (0xD800 or (U shr 10)).toChar()
                val W2 = (0xDC00 or (U and 0x3FF)).toChar()
                cs[length++] = W1
                ch = W2
                i += 4
            } else if (bytes[i].toInt() and 0xe0 == 0xe0) {
                ch = (bytes[i].toInt() and 0x0f shl 12
                        or (bytes[i + 1].toInt() and 0x3f shl 6) or (bytes[i + 2].toInt() and 0x3f)).toChar()
                i += 3
            } else if (bytes[i].toInt() and 0xd0 == 0xd0) {
                ch = (bytes[i].toInt() and 0x1f shl 6 or (bytes[i + 1].toInt() and 0x3f)).toChar()
                i += 2
            } else if (bytes[i].toInt() and 0xc0 == 0xc0) {
                ch = (bytes[i].toInt() and 0x1f shl 6 or (bytes[i + 1].toInt() and 0x3f)).toChar()
                i += 2
            } else {
                ch = (bytes[i].toInt() and 0xff).toChar()
                i += 1
            }
            cs[length++] = ch
        }
        return cs.concatToString()
    }

    @JvmStatic
    fun toUTF8ByteArray(string: String): ByteArray {
        return toUTF8ByteArray(string.toCharArray())
    }

//    fun toUTF8ByteArray(string: CharArray): ByteArray {
//        val bOut = ByteArrayOutputStream()
//        try {
//            toUTF8ByteArray(string, bOut)
//        } catch (e: IOException) {
//            throw IllegalStateException("cannot encode string to byte array!")
//        }
//        return bOut.toByteArray()
//    }

    fun toUTF8ByteArray(string: CharArray): ByteArray {
        // Convert CharArray to String, then to ByteArray with UTF-8 encoding
        return string.concatToString().getByteArray()
    }


//    @Throws(IOException::class)
//    fun toUTF8ByteArray(string: CharArray, sOut: OutputStream) {
//        var i = 0
//        while (i < string.size) {
//            var ch = string[i]
//            if (ch.code < 0x0080) {
//                sOut.write(ch.code)
//            } else if (ch.code < 0x0800) {
//                sOut.write(0xc0 or (ch.code shr 6))
//                sOut.write(0x80 or (ch.code and 0x3f))
//            } else if (ch.code >= 0xD800 && ch.code <= 0xDFFF) {
//                // in error - can only happen, if the Java String class has a
//                // bug.
//                check(i + 1 < string.size) { "invalid UTF-16 codepoint" }
//                val W1 = ch
//                ch = string[++i]
//                val W2 = ch
//                // in error - can only happen, if the Java String class has a
//                // bug.
//                check(W1.code <= 0xDBFF) { "invalid UTF-16 codepoint" }
//                val codePoint = (W1.code and 0x03FF shl 10 or (W2.code and 0x03FF)) + 0x10000
//                sOut.write(0xf0 or (codePoint shr 18))
//                sOut.write(0x80 or (codePoint shr 12 and 0x3F))
//                sOut.write(0x80 or (codePoint shr 6 and 0x3F))
//                sOut.write(0x80 or (codePoint and 0x3F))
//            } else {
//                sOut.write(0xe0 or (ch.code shr 12))
//                sOut.write(0x80 or (ch.code shr 6 and 0x3F))
//                sOut.write(0x80 or (ch.code and 0x3F))
//            }
//            i++
//        }
//    }

    fun toUTF8ByteArray2(string: CharArray): ByteArray {
        val byteBuffer = mutableListOf<Byte>()

        var i = 0
        while (i < string.size) {
            val ch = string[i].code
            when {
                ch < 0x0080 -> {
                    byteBuffer.add(ch.toByte())
                }
                ch < 0x0800 -> {
                    byteBuffer.add((0xc0 or (ch shr 6)).toByte())
                    byteBuffer.add((0x80 or (ch and 0x3f)).toByte())
                }
                ch in 0xD800..0xDFFF -> {
                    check(i + 1 < string.size) { "Invalid UTF-16 codepoint" }
                    val w1 = ch
                    val w2 = string[++i].code
                    check(w1 <= 0xDBFF) { "Invalid UTF-16 codepoint" }

                    val codePoint = (w1 and 0x03FF shl 10 or (w2 and 0x03FF)) + 0x10000
                    byteBuffer.add((0xf0 or (codePoint shr 18)).toByte())
                    byteBuffer.add((0x80 or (codePoint shr 12 and 0x3F)).toByte())
                    byteBuffer.add((0x80 or (codePoint shr 6 and 0x3F)).toByte())
                    byteBuffer.add((0x80 or (codePoint and 0x3F)).toByte())
                }
                else -> {
                    byteBuffer.add((0xe0 or (ch shr 12)).toByte())
                    byteBuffer.add((0x80 or (ch shr 6 and 0x3F)).toByte())
                    byteBuffer.add((0x80 or (ch and 0x3F)).toByte())
                }
            }
            i++
        }

        return byteBuffer.toByteArray()
    }

    /**
     * A locale independent version of toUpperCase.
     *
     * @param string input to be converted
     * @return a US Ascii uppercase version
     */
    fun toUpperCase(string: String): String {
        var changed = false
        val chars = string.toCharArray()
        for (i in chars.indices) {
            val ch = chars[i]
            if ('a' <= ch && 'z' >= ch) {
                changed = true
                chars[i] = (ch.code - 'a'.code + 'A'.code).toChar()
            }
        }
        return if (changed) {
            chars.concatToString()
        } else string
    }

    /**
     * A locale independent version of toLowerCase.
     *
     * @param string input to be converted
     * @return a US ASCII lowercase version
     */
    fun toLowerCase(string: String): String {
        var changed = false
        val chars = string.toCharArray()
        for (i in chars.indices) {
            val ch = chars[i]
            if ('A' <= ch && 'Z' >= ch) {
                changed = true
                chars[i] = (ch.code - 'A'.code + 'a'.code).toChar()
            }
        }
        return if (changed) {
            chars.concatToString()
        } else string
    }

    fun toByteArray(chars: CharArray): ByteArray {
        val bytes = ByteArray(chars.size)
        for (i in bytes.indices) {
            bytes[i] = chars[i].code.toByte()
        }
        return bytes
    }

    @JvmStatic
    fun toByteArray(string: String): ByteArray {
        val bytes = ByteArray(string.length)
        for (i in bytes.indices) {
            val ch = string[i]
            bytes[i] = ch.code.toByte()
        }
        return bytes
    }

    fun toByteArray(s: String, buf: ByteArray, off: Int): Int {
        val count = s.length
        for (i in 0 until count) {
            val c = s[i]
            buf[off + i] = c.code.toByte()
        }
        return count
    }

    /**
     * Convert an array of 8 bit characters into a string.
     *
     * @param bytes 8 bit characters.
     * @return resulting String.
     */
    @JvmStatic
    fun fromByteArray(bytes: ByteArray): String {
        return asCharArray(bytes).concatToString()
    }

    /**
     * Do a simple conversion of an array of 8 bit characters into a string.
     *
     * @param bytes 8 bit characters.
     * @return resulting String.
     */
    fun asCharArray(bytes: ByteArray): CharArray {
        val chars = CharArray(bytes.size)
        for (i in chars.indices) {
            chars[i] = (bytes[i].toInt() and 0xff).toChar()
        }
        return chars
    }

    fun split(input: String, delimiter: Char): Array<String?> {
        var input = input
        val v: ArrayList<String> = ArrayList<String>()
        var moreTokens = true
        var subString: String
        while (moreTokens) {
            val tokenLocation = input.indexOf(delimiter)
            if (tokenLocation > 0) {
                subString = input.substring(0, tokenLocation)
                v.add(subString)
                input = input.substring(tokenLocation + 1)
            } else {
                moreTokens = false
                v.add(input)
            }
        }
        val res = arrayOfNulls<String>(v.size)
        for (i in res.indices) {
            res[i] = v.elementAt(i) as String
        }
        return res
    }

//    fun newList(): StringList {
//        return StringListImpl()
//    }

//    fun lineSeparator(): String? {
//        return LINE_SEPARATOR
//    }

//    private class StringListImpl : ArrayList<String>(), StringList {
//        override fun add(s: String): Boolean {
//            return super.add(s)
//        }
//
//        override fun sizeS(): Int {
//            return super.size
//        }
//
//        override fun set(index: Int, element: String): String {
//            return super.set(index, element)
//        }
//
//        override fun add(index: Int, element: String) {
//            super.add(index, element)
//        }
//
//        override fun toStringArray(): Array<String> {
//            val strs = Array(this.size) { "" }
//
//            for (i in strs.indices) {
//                strs[i] = this[i]
//            }
//
//            return strs
//        }
//
//
//        override fun toStringArray(from: Int, to: Int): Array<String> {
//            val strs = Array(to - from) { "" }
//
//            for (i in from until minOf(this.size, to)) {
//                strs[i - from] = this[i]
//            }
//
//            return strs
//        }
//    }

//    private class StringListImpl : StringList {
//        private val list = ArrayList<String>()
//
//        override fun add(s: String): Boolean = list.add(s)
//
//        override fun sizeS(): Int = list.size
//
//        override fun get(index: Int): String = list[index]
//
//        fun set(index: Int, element: String): String = list.set(index, element).also { list[index] = element }
//
//        fun add(index: Int, element: String) {
//            list.add(index, element)
//        }
//
//        override fun toStringArray(): Array<String> = list.toTypedArray()
//
//        override fun toStringArray(from: Int, to: Int): Array<String> {
//            val safeTo = minOf(list.size, to)
//            return if (from in 0 until safeTo) {
//                list.subList(from, safeTo).toTypedArray()
//            } else {
//                emptyArray()
//            }
//        }
//
//        override fun iterator(): Iterator<String> {
//            return list.iterator()
//        }
//    }


}
