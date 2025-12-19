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

import com.mangala.MultiplatformByteArrayOutputStream
import com.mangala.MultiplatformInputStream
import okio.EOFException
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Arrays.hashCode
import org.spongycastle.util.Streams.readFully
import okio.IOException
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Base class for BIT STRING objects
 */
abstract class ASN1BitString(
    data: ByteArray,
    padBits: Int
) : ASN1Primitive(), ASN1String {
    @JvmField
    val data: ByteArray
    @JvmField
    val padBits: Int

    /**
     * Base constructor.
     *
     * @param data the octets making up the bit string.
     * @param padBits the number of extra bits at the end of the string.
     */
    init {
        if (data == null) {
            throw NullPointerException("data cannot be null")
        }
        require(!(data.size == 0 && padBits != 0)) { "zero length data with non-zero pad bits" }
        require(!(padBits > 7 || padBits < 0)) { "pad bits cannot be greater than 7 or less than 0" }
        this.data = clone(data)
        this.padBits = padBits
    }

    /**
     * Return a String representation of this BIT STRING
     *
     * @return a String representation.
     */
    override fun getString(): String {
        val buf = StringBuilder("#")
        val bOut = MultiplatformByteArrayOutputStream()
        val aOut = ASN1OutputStream(bOut)
        try {
            aOut.writeObject(this)
        } catch (e: IOException) {
            throw ASN1ParsingException("Internal error encoding BitString: " + e.message, e)
        }
        val string = bOut.toByteArray()
        for (i in string.indices) {
            buf.append(table[string[i].toInt() ushr 4 and 0xf])
            buf.append(table[string[i].toInt() and 0xf])
        }
        return buf.toString()
    }

    /**
     * @return the value of the bit string as an int (truncating if necessary)
     */
    fun intValue(): Int {
        var value = 0
        var string = data
        if (padBits > 0 && data!!.size <= 4) {
            string = derForm(data!!, padBits!!)
        }
        var i = 0
        while (i != string!!.size && i != 4) {
            value = value or (string[i].toInt() and 0xff shl 8 * i)
            i++
        }
        return value
    }

    val octets: ByteArray?
        /**
         * Return the octets contained in this BIT STRING, checking that this BIT STRING really
         * does represent an octet aligned string. Only use this method when the standard you are
         * following dictates that the BIT STRING will be octet aligned.
         *
         * @return a copy of the octet aligned data.
         */
        get() {
            check(padBits == 0) { "attempt to get non-octet aligned data from BIT STRING" }
            return clone(data)
        }
    val bytes: ByteArray?
        get() = derForm(data, padBits)

    override fun toString(): String {
        return getString()
    }

    override fun hashCode(): Int {
        return padBits xor hashCode(bytes!!)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o !is ASN1BitString) {
            return false
        }
        val other = o
        return (padBits == other.padBits
                && areEqual(bytes, other.bytes))
    }

    val loadedObject: ASN1Primitive
        get() = toASN1Primitive()

    override fun toDERObject(): ASN1Primitive? {
        return DERBitString(data, padBits)
    }

    override fun toDLObject(): ASN1Primitive? {
        return DLBitString(data, padBits)
    }

    @Throws(IOException::class)
    abstract override fun encode(out: ASN1OutputStream)

    companion object {
        private val table = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )

        /**
         * @param bitString an int containing the BIT STRING
         * @return the correct number of pad bits for a bit string defined in
         * a 32 bit constant
         */
        @JvmStatic
        protected fun getPadBits(
            bitString: Int
        ): Int {
            var `val` = 0
            for (i in 3 downTo 0) {
                //
                // this may look a little odd, but if it isn't done like this pre jdk1.2
                // JVM's break!
                //
                if (i != 0) {
                    if (bitString shr i * 8 != 0) {
                        `val` = bitString shr i * 8 and 0xFF
                        break
                    }
                } else {
                    if (bitString != 0) {
                        `val` = bitString and 0xFF
                        break
                    }
                }
            }
            if (`val` == 0) {
                return 0
            }
            var bits = 1
            while (1.let { `val` = `val` shl it; `val` } and 0xFF != 0) {
                bits++
            }
            return 8 - bits
        }

        /**
         * @param bitString an int containing the BIT STRING
         * @return the correct number of bytes for a bit string defined in
         * a 32 bit constant
         */
        @JvmStatic
        protected fun getBytes(bitString: Int): ByteArray {
            if (bitString == 0) {
                return ByteArray(0)
            }
            var bytes = 4
            for (i in 3 downTo 1) {
                if (bitString and (0xFF shl i * 8) != 0) {
                    break
                }
                bytes--
            }
            val result = ByteArray(bytes)
            for (i in 0 until bytes) {
                result[i] = (bitString shr i * 8 and 0xFF).toByte()
            }
            return result
        }

        @JvmStatic
        protected fun derForm(data: ByteArray, padBits: Int): ByteArray {
            val rv = clone(data)
            // DER requires pad bits be zero
            if (padBits > 0) {
                rv!![data!!.size - 1] = (rv[data.size - 1].toInt() and (0xff shl padBits)).toByte()
            }
            return rv
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromInputStream(length: Int, stream: MultiplatformInputStream): ASN1BitString {
            require(length >= 1) { "truncated BIT STRING detected" }
            val padBits = stream.read()
            val data = ByteArray(length - 1)
            if (data.size != 0) {
                if (readFully(stream, data) != data.size) {
                    throw EOFException("EOF encountered in middle of BIT STRING")
                }
                if (padBits > 0 && padBits < 8) {
                    if (data[data.size - 1] != (data[data.size - 1].toInt() and (0xff shl padBits)).toByte()) {
                        return DLBitString(data, padBits)
                    }
                }
            }
            return DERBitString(data, padBits)
        }
    }
}
