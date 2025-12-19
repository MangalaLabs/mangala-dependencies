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

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmStatic


/**
 * General array utilities.
 */
object Arrays {
    fun areEqual(
        a: BooleanArray?,
        b: BooleanArray?
    ): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun areEqual(
        a: CharArray?,
        b: CharArray?
    ): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun areEqual(
        a: ByteArray?,
        b: ByteArray?
    ): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }

    fun areEqual(
        a: ShortArray?,
        b: ShortArray?
    ): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }

    /**
     * A constant time equals comparison - does not terminate early if
     * test will fail. For best results always pass the expected value
     * as the first parameter.
     *
     * @param expected first array
     * @param supplied second array
     * @return true if arrays equal, false otherwise.
     */
    fun constantTimeAreEqual(
        expected: ByteArray?,
        supplied: ByteArray?
    ): Boolean {
        if (expected == supplied) {
            return true
        }
        if (expected == null || supplied == null) {
            return false
        }
        if (expected.size != supplied.size) {
            return !constantTimeAreEqual(expected, expected)
        }
        var nonEqual = 0
        for (i in expected.indices) {
            nonEqual = nonEqual or (expected[i].toInt() xor supplied[i].toInt())
        }
        return nonEqual == 0
    }

    @JvmStatic
    fun areEqual(
        a: IntArray?,
        b: IntArray?
    ): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }

    fun areEqual(
        a: LongArray?,
        b: LongArray?
    ): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }

    fun areEqual(a: Array<Any?>?, b: Array<Any?>?): Boolean {
        if (a == b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        if (a.size != b.size) {
            return false
        }
        for (i in a.indices) {
            val objA = a[i]
            val objB = b[i]
            if (objA == null) {
                if (objB != null) {
                    return false
                }
            } else if (objA != objB) {
                return false
            }
        }
        return true
    }

    fun compareUnsigned(a: ByteArray?, b: ByteArray?): Int {
        if (a == b) {
            return 0
        }
        if (a == null) {
            return -1
        }
        if (b == null) {
            return 1
        }
        val minLen = minOf(a.size, b.size)
        for (i in 0 until minLen) {
            val aVal = a[i].toInt() and 0xFF
            val bVal = b[i].toInt() and 0xFF
            if (aVal < bVal) {
                return -1
            }
            if (aVal > bVal) {
                return 1
            }
        }
        if (a.size < b.size) {
            return -1
        }
        return if (a.size > b.size) {
            1
        } else 0
    }

    fun contains(a: ShortArray, n: Short): Boolean {
        for (i in a.indices) {
            if (a[i] == n) {
                return true
            }
        }
        return false
    }

    fun contains(a: IntArray, n: Int): Boolean {
        for (i in a.indices) {
            if (a[i] == n) {
                return true
            }
        }
        return false
    }

    fun fill(
        array: ByteArray,
        value: Byte
    ) {
        for (i in array.indices) {
            array[i] = value
        }
    }

    fun fill(
        array: CharArray,
        value: Char
    ) {
        for (i in array.indices) {
            array[i] = value
        }
    }

    fun fill(
        array: LongArray,
        value: Long
    ) {
        for (i in array.indices) {
            array[i] = value
        }
    }

    fun fill(
        array: ShortArray,
        value: Short
    ) {
        for (i in array.indices) {
            array[i] = value
        }
    }

    fun fill(
        array: IntArray,
        value: Int
    ) {
        for (i in array.indices) {
            array[i] = value
        }
    }

    @JvmStatic
    fun hashCode(data: ByteArray): Int {
        var i = data.size
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor data[i].toInt()
        }
        return hc
    }

    fun hashCode(data: ByteArray, off: Int, len: Int): Int {
        var i = len
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor data[off + i].toInt()
        }
        return hc
    }

    @JvmStatic
    fun hashCode(data: CharArray): Int {
        var i = data.size
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor data[i].code
        }
        return hc
    }

    fun hashCode(ints: Array<IntArray>): Int {
        var hc = 0
        for (i in ints.indices) {
            hc = hc * 257 + hashCode(ints[i])
        }
        return hc
    }

    @JvmStatic
    fun hashCode(data: IntArray): Int {
        var i = data.size
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor data[i]
        }
        return hc
    }

    fun hashCode(data: IntArray, off: Int, len: Int): Int {
        var i = len
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor data[off + i]
        }
        return hc
    }

    fun hashCode(data: LongArray): Int {
        var i = data.size
        var hc = i + 1
        while (--i >= 0) {
            val di = data[i]
            hc *= 257
            hc = hc xor di.toInt()
            hc *= 257
            hc = hc xor (di ushr 32).toInt()
        }
        return hc
    }

    fun hashCode(data: LongArray, off: Int, len: Int): Int {
        var i = len
        var hc = i + 1
        while (--i >= 0) {
            val di = data[off + i]
            hc *= 257
            hc = hc xor di.toInt()
            hc *= 257
            hc = hc xor (di ushr 32).toInt()
        }
        return hc
    }

    fun hashCode(shorts: Array<Array<ShortArray>>): Int {
        var hc = 0
        for (i in shorts.indices) {
            hc = hc * 257 + hashCode(shorts[i])
        }
        return hc
    }

    fun hashCode(shorts: Array<ShortArray>): Int {
        var hc = 0
        for (i in shorts.indices) {
            hc = hc * 257 + hashCode(shorts[i])
        }
        return hc
    }

    fun hashCode(data: ShortArray): Int {
        var i = data.size
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor (data[i].toInt() and 0xff)
        }
        return hc
    }

    fun hashCode(data: Array<Any>?): Int {
        if (data == null) {
            return 0
        }
        var i = data.size
        var hc = i + 1
        while (--i >= 0) {
            hc *= 257
            hc = hc xor data[i].hashCode()
        }
        return hc
    }

//    @JvmStatic
//    fun clone(data: ByteArray): ByteArray {
//        val copy = ByteArray(data.size)
//        System.arraycopy(data, 0, copy, 0, data.size)
//        return copy
//    }
//
//    fun clone(data: CharArray?): CharArray? {
//        if (data == null) {
//            return null
//        }
//        val copy = CharArray(data.size)
//        System.arraycopy(data, 0, copy, 0, data.size)
//        return copy
//    }
//
//    fun clone(data: ByteArray, existing: ByteArray): ByteArray {
//        if (existing.size != data.size) {
//            return clone(data)
//        }
//        System.arraycopy(data, 0, existing, 0, existing.size)
//        return existing
//    }

    @JvmStatic
    fun clone(data: ByteArray): ByteArray {
        return data.copyOf()
    }

    fun clone(data: CharArray?): CharArray? {
        return data?.copyOf()
    }

    fun clone(data: ByteArray, existing: ByteArray): ByteArray {
        return if (existing.size != data.size) {
            data.copyOf()
        } else {
            data.copyInto(existing)
        }
    }

    fun clone(data: Array<ByteArray>): Array<ByteArray> {
//        val copy = arrayOfNulls<ByteArray>(data.size)
        val copyA = mutableListOf<ByteArray>()
        val copy = copyA.toTypedArray()

        for (i in data.indices) {
            copy[i] = clone(data[i])
        }
        return copy
    }

    fun clone(data: Array<Array<ByteArray>>): Array<Array<ByteArray>> {
//        val copy = arrayOfNulls<Array<ByteArray?>?>(data.size)
        val copyA = mutableListOf<Array<ByteArray>>()
        val copy = copyA.toTypedArray()
        for (i in data.indices) {
            copy[i] = clone(data[i])
        }
        return copy
    }

//    @JvmStatic
//    fun clone(data: IntArray): IntArray {
//        val copy = IntArray(data.size)
//        System.arraycopy(data, 0, copy, 0, data.size)
//        return copy
//    }
//
//    @JvmStatic
//    fun clone(data: LongArray): LongArray {
//        val copy = LongArray(data.size)
//        System.arraycopy(data, 0, copy, 0, data.size)
//        return copy
//    }
//
//    fun clone(data: LongArray, existing: LongArray): LongArray {
//        if (existing.size != data.size) {
//            return clone(data)
//        }
//        System.arraycopy(data, 0, existing, 0, existing.size)
//        return existing
//    }
//
//    fun clone(data: ShortArray): ShortArray {
//        val copy = ShortArray(data.size)
//        System.arraycopy(data, 0, copy, 0, data.size)
//        return copy
//    }
//
//    fun clone(data: Array<BigInteger>): Array<BigInteger> {
////        val copy = arrayOfNulls<BigInteger>(data.size)
//        val copyA = mutableListOf<BigInteger>()
//        val copy = copyA.toTypedArray()
//        System.arraycopy(data, 0, copy, 0, data.size)
//        return copy
//    }

    @JvmStatic
    fun clone(data: IntArray): IntArray {
        return data.copyOf()
    }

    @JvmStatic
    fun clone(data: LongArray): LongArray {
        return data.copyOf()
    }

    fun clone(data: LongArray, existing: LongArray): LongArray {
        return if (existing.size != data.size) {
            data.copyOf()
        } else {
            data.copyInto(existing)
        }
    }

    fun clone(data: ShortArray): ShortArray {
        return data.copyOf()
    }

    fun clone(data: Array<BigInteger>): Array<BigInteger> {
        return data.copyOf()
    }

//    fun copyOf(data: ByteArray, newLength: Int): ByteArray {
//        val tmp = ByteArray(newLength)
//        if (newLength < data.size) {
//            System.arraycopy(data, 0, tmp, 0, newLength)
//        } else {
//            System.arraycopy(data, 0, tmp, 0, data.size)
//        }
//        return tmp
//    }
//
//    fun copyOf(data: CharArray, newLength: Int): CharArray {
//        val tmp = CharArray(newLength)
//        if (newLength < data.size) {
//            System.arraycopy(data, 0, tmp, 0, newLength)
//        } else {
//            System.arraycopy(data, 0, tmp, 0, data.size)
//        }
//        return tmp
//    }
//
//    fun copyOf(data: IntArray, newLength: Int): IntArray {
//        val tmp = IntArray(newLength)
//        if (newLength < data.size) {
//            System.arraycopy(data, 0, tmp, 0, newLength)
//        } else {
//            System.arraycopy(data, 0, tmp, 0, data.size)
//        }
//        return tmp
//    }

    fun copyOf(data: ByteArray, newLength: Int): ByteArray {
        return data.copyOf(newLength)
    }

    fun copyOf(data: CharArray, newLength: Int): CharArray {
        return data.copyOf(newLength)
    }

    fun copyOf(data: IntArray, newLength: Int): IntArray {
        return data.copyOf(newLength)
    }


//    fun copyOf(data: LongArray, newLength: Int): LongArray {
//        val tmp = LongArray(newLength)
//        if (newLength < data.size) {
//            System.arraycopy(data, 0, tmp, 0, newLength)
//        } else {
//            System.arraycopy(data, 0, tmp, 0, data.size)
//        }
//        return tmp
//    }

    fun copyOf(data: LongArray, newLength: Int): LongArray {
        return data.copyOf(newLength)
    }


//    fun copyOf(data: Array<BigInteger>, newLength: Int): Array<BigInteger> {
////        val tmp = arrayOfNulls<BigInteger>(newLength)
//        val tmpA = mutableListOf<BigInteger>()
//        val tmp = tmpA.toTypedArray()
//        if (newLength < data.size) {
//            System.arraycopy(data, 0, tmp, 0, newLength)
//        } else {
//            System.arraycopy(data, 0, tmp, 0, data.size)
//        }
//        return tmp
//    }

    fun copyOf(data: Array<BigInteger>, newLength: Int): Array<BigInteger> {
        // Ensure the newLength is not negative
        if (newLength < 0) {
            throw IllegalArgumentException("newLength must be >= 0")
        }

        val tmp = arrayOfNulls<BigInteger?>(newLength)
        for (i in tmp.indices) {
            if (i < data.size) {
                tmp[i] = data[i]
            } else {
                break // Once we reach the original data size, no need to continue
            }
        }
        // Note: arrayOfNulls creates an Array<BigInteger?>, so we need to cast nulls to BigInteger
        // If nulls are acceptable, this is fine. Otherwise, consider initializing to BigInteger.ZERO or another default value.
        @Suppress("UNCHECKED_CAST")
        return tmp as Array<BigInteger>
    }


    /**
     * Make a copy of a range of bytes from the passed in data array. The range can
     * extend beyond the end of the input array, in which case the return array will
     * be padded with zeroes.
     *
     * @param data the array from which the data is to be copied.
     * @param from the start index at which the copying should take place.
     * @param to the final index of the range (exclusive).
     *
     * @return a new byte array containing the range given.
     */
//    fun copyOfRange(data: ByteArray, from: Int, to: Int): ByteArray {
//        val newLength = getLength(from, to)
//        val tmp = ByteArray(newLength)
//        if (data.size - from < newLength) {
//            System.arraycopy(data, from, tmp, 0, data.size - from)
//        } else {
//            System.arraycopy(data, from, tmp, 0, newLength)
//        }
//        return tmp
//    }

    fun copyOfRange(data: ByteArray, from: Int, to: Int): ByteArray {
        // First, check if 'from' and 'to' indices are valid
        if (from < 0 || to > data.size || from > to) {
            throw IllegalArgumentException("Invalid range: from $from, to $to, array length ${data.size}")
        }
        // Use Kotlin's built-in function to copy a range of the array
        return data.copyOfRange(from, to)
    }


//    fun copyOfRange(data: IntArray, from: Int, to: Int): IntArray {
//        val newLength = getLength(from, to)
//        val tmp = IntArray(newLength)
//        if (data.size - from < newLength) {
//            System.arraycopy(data, from, tmp, 0, data.size - from)
//        } else {
//            System.arraycopy(data, from, tmp, 0, newLength)
//        }
//        return tmp
//    }

    fun copyOfRange(data: IntArray, from: Int, to: Int): IntArray {
        val newLength = getLength(from, to)
        // Validate the indices
        if (from < 0 || to > data.size || newLength < 0) {
            throw IllegalArgumentException("copyOfRange: out of bounds: from $from, to $to, array length ${data.size}")
        }
        // Kotlin's way to copy ranges of arrays
        return data.copyOfRange(from, to)
    }


//    fun copyOfRange(data: LongArray, from: Int, to: Int): LongArray {
//        val newLength = getLength(from, to)
//        val tmp = LongArray(newLength)
//        if (data.size - from < newLength) {
//            System.arraycopy(data, from, tmp, 0, data.size - from)
//        } else {
//            System.arraycopy(data, from, tmp, 0, newLength)
//        }
//        return tmp
//    }

    fun copyOfRange(data: LongArray, from: Int, to: Int): LongArray {
        val newLength = getLength(from, to)
        // Ensure the from and to indices are within the bounds of the data array
        if (from < 0 || to > data.size || newLength < 0) {
            throw IllegalArgumentException("copyOfRange: out of bounds: from $from, to $to, array length ${data.size}")
        }
        // Use Kotlin's copyInto function for arrays to achieve the copy operation
        return LongArray(newLength).apply {
            data.copyInto(this, 0, from, from + newLength.coerceAtMost(data.size - from))
        }
    }


//    fun copyOfRange(data: Array<BigInteger>, from: Int, to: Int): Array<BigInteger> {
//        val newLength = getLength(from, to)
//        val tmpA = mutableListOf<BigInteger>()
//        val tmp = tmpA.toTypedArray()
////        val tmp = arrayOfNulls<BigInteger>(newLength)
//        if (data.size - from < newLength) {
//            System.arraycopy(data, from, tmp, 0, data.size - from)
//        } else {
//            System.arraycopy(data, from, tmp, 0, newLength)
//        }
//        return tmp
//    }

    fun copyOfRange(data: Array<BigInteger>, from: Int, to: Int): Array<BigInteger> {
        // Use previously defined getLength to determine the new length and validate the range
        val newLength = getLength(from, to)
        if (newLength <= 0 || from < 0 || to > data.size) {
            throw IllegalArgumentException("Invalid range: $from to $to")
        }

        // Utilize sliceArray to copy the range directly, handling any ArrayIndexOutOfBoundsException internally
        return data.sliceArray(from until to)
    }


//    private fun getLength(from: Int, to: Int): Int {
//        val newLength = to - from
//        if (newLength < 0) {
//            val sb = StringBuffer(from)
//            sb.append(" > ").append(to)
//            throw IllegalArgumentException(sb.toString())
//        }
//        return newLength
//    }

    private fun getLength(from: Int, to: Int): Int {
        val newLength = to - from
        if (newLength < 0) {
            throw IllegalArgumentException("$from > $to")
        }
        return newLength
    }


//    fun append(a: ByteArray?, b: Byte): ByteArray {
//        if (a == null) {
//            return byteArrayOf(b)
//        }
//        val length = a.size
//        val result = ByteArray(length + 1)
//        System.arraycopy(a, 0, result, 0, length)
//        result[length] = b
//        return result
//    }

    fun append(a: ByteArray?, b: Byte): ByteArray {
        // If 'a' is null, return a new ByteArray containing only 'b'
        if (a == null) {
            return byteArrayOf(b)
        }
        // Otherwise, create a new ByteArray with an additional slot for 'b'
        val result = ByteArray(a.size + 1)
        // Copy 'a' into 'result' and set the last element to 'b'
        a.copyInto(result)
        result[a.size] = b
        return result
    }


//    fun append(a: ShortArray?, b: Short): ShortArray {
//        if (a == null) {
//            return shortArrayOf(b)
//        }
//        val length = a.size
//        val result = ShortArray(length + 1)
//        System.arraycopy(a, 0, result, 0, length)
//        result[length] = b
//        return result
//    }

    fun append(a: ShortArray?, b: Short): ShortArray {
        // If 'a' is null, return a new ShortArray containing only 'b'
        if (a == null) {
            return shortArrayOf(b)
        }
        // Otherwise, create a new ShortArray with an additional slot for 'b'
        val result = ShortArray(a.size + 1)
        // Copy 'a' into 'result' and set the last element to 'b'
        a.copyInto(result)
        result[a.size] = b
        return result
    }


//    fun append(a: IntArray?, b: Int): IntArray {
//        if (a == null) {
//            return intArrayOf(b)
//        }
//        val length = a.size
//        val result = IntArray(length + 1)
//        System.arraycopy(a, 0, result, 0, length)
//        result[length] = b
//        return result
//    }

    fun append(a: IntArray?, b: Int): IntArray {
        // If 'a' is null, return a new IntArray containing only 'b'
        if (a == null) {
            return intArrayOf(b)
        }
        // Otherwise, create a MutableList from 'a', append 'b', and convert back to IntArray
        return (a.toMutableList() + b).toIntArray()
    }


//    fun append(a: Array<String>, b: String): Array<String> {
//        if (a == null) {
//            return arrayOf(b)
//        }
//        val length = a.size
//        val resultA = mutableListOf<String>()
//        val result = resultA.toTypedArray()
////        val result = arrayOfNulls<String>(length + 1)
//        System.arraycopy(a, 0, result, 0, length)
//        result[length] = b
//        return result
//    }

    fun append(a: Array<String>, b: String): Array<String> {
        // Convert the original array to a MutableList for easier manipulation
        val resultList = a.toMutableList()
        // Append 'b' to the MutableList
        resultList.add(b)
        // Return the MutableList converted back to an Array
        return resultList.toTypedArray()
    }


//    fun concatenate(a: ByteArray, b: ByteArray?): ByteArray {
//        return if (a != null && b != null) {
//            val rv = ByteArray(a.size + b.size)
//            System.arraycopy(a, 0, rv, 0, a.size)
//            System.arraycopy(b, 0, rv, a.size, b.size)
//            rv
//        } else if (b != null) {
//            clone(b)
//        } else {
//            clone(a)
//        }
//    }

//    fun concatenate(a: ByteArray?, b: ByteArray, c: ByteArray?): ByteArray? {
//        return if (a != null && b != null && c != null) {
//            val rv = ByteArray(a.size + b.size + c.size)
//            System.arraycopy(a, 0, rv, 0, a.size)
//            System.arraycopy(b, 0, rv, a.size, b.size)
//            System.arraycopy(c, 0, rv, a.size + b.size, c.size)
//            rv
//        } else if (a == null) {
//            concatenate(b, c)
//        } else if (b == null) {
//            concatenate(a, c)
//        } else {
//            concatenate(a, b)
//        }
//    }

//    fun concatenate(a: ByteArray?, b: ByteArray, c: ByteArray?, d: ByteArray?): ByteArray? {
//        return if (a != null && b != null && c != null && d != null) {
//            val rv = ByteArray(a.size + b.size + c.size + d.size)
//            System.arraycopy(a, 0, rv, 0, a.size)
//            System.arraycopy(b, 0, rv, a.size, b.size)
//            System.arraycopy(c, 0, rv, a.size + b.size, c.size)
//            System.arraycopy(d, 0, rv, a.size + b.size + c.size, d.size)
//            rv
//        } else if (d == null) {
//            concatenate(a, b, c)
//        } else if (c == null) {
//            concatenate(a, b, d)
//        } else if (b == null) {
//            concatenate(a, c, d)
//        } else {
//            concatenate(b, c, d)
//        }
//    }

    fun concatenate(a: ByteArray?, b: ByteArray?, c: ByteArray?, d: ByteArray?): ByteArray? {
        // Since your original logic specifically checks for all non-null to concatenate,
        // and otherwise tries to concatenate different combinations based on which arrays are non-null,
        // we directly pass all arrays to the helper which handles nulls gracefully.
        return concatenateNonNull(a, b, c, d)
    }


    fun concatenateNonNull(vararg arrays: ByteArray?): ByteArray {
        // Filter out null arrays and flatten the remaining ones
        return arrays.filterNotNull().flatMap { it.asIterable() }.toByteArray()
    }


//    fun concatenate(arrays: Array<ByteArray>): ByteArray {
//        var size = 0
//        for (i in arrays.indices) {
//            size += arrays[i].size
//        }
//        val rv = ByteArray(size)
//        var offSet = 0
//        for (i in arrays.indices) {
//            System.arraycopy(arrays[i], 0, rv, offSet, arrays[i].size)
//            offSet += arrays[i].size
//        }
//        return rv
//    }

    fun concatenate(arrays: Array<ByteArray>): ByteArray {
        var size = arrays.sumOf { it.size }
        val rv = ByteArray(size)
        var offset = 0

        arrays.forEach { array ->
            array.copyInto(rv, offset)
            offset += array.size
        }

        return rv
    }


//    fun concatenate(a: IntArray, b: IntArray): IntArray {
//        val c = IntArray(a.size + b.size)
//        System.arraycopy(a, 0, c, 0, a.size)
//        System.arraycopy(b, 0, c, a.size, b.size)
//        return c
//    }

    fun concatenate(a: IntArray, b: IntArray): IntArray {
        return a + b
    }


//    fun prepend(a: ByteArray?, b: Byte): ByteArray {
//        if (a == null) {
//            return byteArrayOf(b)
//        }
//        val length = a.size
//        val result = ByteArray(length + 1)
//        System.arraycopy(a, 0, result, 1, length)
//        result[0] = b
//        return result
//    }

    fun prepend(a: ByteArray?, b: Byte): ByteArray {
        if (a == null) {
            return byteArrayOf(b)
        }
        val result = ByteArray(a.size + 1)
        result[0] = b
        a.copyInto(result, 1)
        return result
    }


//    fun prepend(a: ShortArray?, b: Short): ShortArray {
//        if (a == null) {
//            return shortArrayOf(b)
//        }
//        val length = a.size
//        val result = ShortArray(length + 1)
//        System.arraycopy(a, 0, result, 1, length)
//        result[0] = b
//        return result
//    }

    fun prepend(a: ShortArray?, b: Short): ShortArray {
        if (a == null) {
            return shortArrayOf(b)
        }
        val result = ShortArray(a.size + 1)
        result[0] = b
        a.copyInto(result, 1)
        return result
    }


//    fun prepend(a: IntArray?, b: Int): IntArray {
//        if (a == null) {
//            return intArrayOf(b)
//        }
//        val length = a.size
//        val result = IntArray(length + 1)
//        System.arraycopy(a, 0, result, 1, length)
//        result[0] = b
//        return result
//    }

    fun prepend(a: IntArray?, b: Int): IntArray {
        if (a == null) {
            return intArrayOf(b)
        }
        val result = IntArray(a.size + 1)
        result[0] = b
        a.copyInto(result, 1)
        return result
    }

    fun reverse(a: ByteArray): ByteArray {
        var p1 = 0
        var p2 = a.size
        val result = ByteArray(p2)
        while (--p2 >= 0) {
            result[p2] = a[p1++]
        }
        return result
    }

    fun reverse(a: IntArray?): IntArray? {
        if (a == null) {
            return null
        }
        var p1 = 0
        var p2 = a.size
        val result = IntArray(p2)
        while (--p2 >= 0) {
            result[p2] = a[p1++]
        }
        return result
    }

    /**
     * Iterator backed by a specific array.
     */
    class Iterator<T>
    /**
     * Base constructor.
     *
     *
     * Note: the array is not cloned, changes to it will affect the values returned by next().
     *
     *
     * @param dataArray array backing the iterator.
     */(private val dataArray: Array<T>) : MutableIterator<T> {
        private var position = 0
        override fun hasNext(): Boolean {
            return position < dataArray.size
        }

        override fun next(): T {
            if (position == dataArray.size) {
                throw NoSuchElementException("Out of elements: $position")
            }
            return dataArray[position++]
        }

        override fun remove() {
            throw UnsupportedOperationException("Cannot remove element from an Array.")
        }
    }
}
