/*
 * Copyright (C) 2008 The Guava Authors
 * Copyright (C) 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/google/guava
 */

package com.google.common.kotlin.primitives


import com.google.common.kotlin.base.ConverterG
import com.google.common.kotlin.base.PreconditionsG
import com.google.common.kotlin.base.PreconditionsG.checkArgument
import com.google.common.kotlin.base.PreconditionsG.checkElementIndex
import com.google.common.kotlin.base.PreconditionsG.checkNotNull
import com.google.common.kotlin.base.PreconditionsG.checkPositionIndexes
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Static utility methods pertaining to `long` primitives, that are not
 * already found in either [Long] or [Arrays].
 *
 *
 * See the Guava User Guide article on [
 * primitive utilities](http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained).
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
object LongsG {

    // Bit Twiddling
    const val SIZE = 64

    const val BYTES_SIZE = 8
    /**
     * The number of bytes required to represent a primitive `long`
     * value.
     */
    const val BYTES = SIZE / BYTES_SIZE

    /**
     * The largest power of two that can be represented as a `long`.
     *
     * @since 10.0
     */
    const val MAX_POWER_OF_TWO = 1L shl SIZE - 2

    /**
     * Returns a hash code for `value`; equal to the result of invoking
     * `((Long) value).hashCode()`.
     *
     *
     * This method always return the value specified by [ ][Long.hashCode] in java, which might be different from
     * `((Long) value).hashCode()` in GWT because [Long.hashCode]
     * in GWT does not obey the JRE contract.
     *
     * @param value a primitive `long` value
     * @return a hash code for the value
     */
    @JvmStatic
    fun hashCode(value: Long): Int {
        return (value xor (value ushr 32)).toInt()
    }

    /**
     * Compares the two specified `long` values. The sign of the value
     * returned is the same as that of `((Long) a).compareTo(b)`.
     *
     *
     * **Note for Java 7 and later:** this method should be treated as
     * deprecated; use the equivalent [Long.compare] method instead.
     *
     * @param a the first `long` to compare
     * @param b the second `long` to compare
     * @return a negative value if `a` is less than `b`; a positive
     * value if `a` is greater than `b`; or zero if they are equal
     */
    @JvmStatic
    fun compare(a: Long, b: Long): Int {
        return if (a < b) -1 else if (a > b) 1 else 0
    }

    /**
     * Returns `true` if `target` is present as an element anywhere in
     * `array`.
     *
     * @param array an array of `long` values, possibly empty
     * @param target a primitive `long` value
     * @return `true` if `array[i] == target` for some value of `i`
     */
    fun contains(array: LongArray, target: Long): Boolean {
        for (value in array) {
            if (value == target) {
                return true
            }
        }
        return false
    }

    /**
     * Returns the index of the first appearance of the value `target` in
     * `array`.
     *
     * @param array an array of `long` values, possibly empty
     * @param target a primitive `long` value
     * @return the least index `i` for which `array[i] == target`, or
     * `-1` if no such index exists.
     */
    fun indexOf(array: LongArray, target: Long): Int {
        return indexOf(array, target, 0, array.size)
    }

    // TODO(kevinb): consider making this public
    private fun indexOf(
        array: LongArray, target: Long, start: Int, end: Int
    ): Int {
        for (i in start until end) {
            if (array[i] == target) {
                return i
            }
        }
        return -1
    }

    /**
     * Returns the start position of the first occurrence of the specified `target` within `array`, or `-1` if there is no such occurrence.
     *
     *
     * More formally, returns the lowest index `i` such that `java.util.Arrays.copyOfRange(array, i, i + target.length)` contains exactly
     * the same elements as `target`.
     *
     * @param array the array to search for the sequence `target`
     * @param target the array to search for as a sub-sequence of `array`
     */
    fun indexOf(array: LongArray, target: LongArray): Int {
        checkNotNull(array, "array")
        checkNotNull(target, "target")
        if (target.size == 0) {
            return 0
        }
        outer@ for (i in 0 until array.size - target.size + 1) {
            for (j in target.indices) {
                if (array[i + j] != target[j]) {
                    continue@outer
                }
            }
            return i
        }
        return -1
    }

    /**
     * Returns the index of the last appearance of the value `target` in
     * `array`.
     *
     * @param array an array of `long` values, possibly empty
     * @param target a primitive `long` value
     * @return the greatest index `i` for which `array[i] == target`,
     * or `-1` if no such index exists.
     */
    fun lastIndexOf(array: LongArray, target: Long): Int {
        return lastIndexOf(array, target, 0, array.size)
    }

    // TODO(kevinb): consider making this public
    private fun lastIndexOf(
        array: LongArray, target: Long, start: Int, end: Int
    ): Int {
        for (i in end - 1 downTo start) {
            if (array[i] == target) {
                return i
            }
        }
        return -1
    }

    /**
     * Returns the least value present in `array`.
     *
     * @param array a *nonempty* array of `long` values
     * @return the value present in `array` that is less than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if `array` is empty
     */
    fun min(vararg array: Long): Long {
        checkArgument(array.size > 0)
        var min = array[0]
        for (i in 1 until array.size) {
            if (array[i] < min) {
                min = array[i]
            }
        }
        return min
    }

    /**
     * Returns the greatest value present in `array`.
     *
     * @param array a *nonempty* array of `long` values
     * @return the value present in `array` that is greater than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if `array` is empty
     */
    fun max(vararg array: Long): Long {
        checkArgument(array.size > 0)
        var max = array[0]
        for (i in 1 until array.size) {
            if (array[i] > max) {
                max = array[i]
            }
        }
        return max
    }

    /**
     * Returns the values from each provided array combined into a single array.
     * For example, `concat(new long[] {a, b}, new long[] {}, new
     * long[] {c}` returns the array `{a, b, c}`.
     *
     * @param arrays zero or more `long` arrays
     * @return a single array containing all the values from the source arrays, in
     * order
     */
//    fun concat(vararg arrays: LongArray): LongArray {
//        var length = 0
//        for (array in arrays) {
//            length += array.size
//        }
//        val result = LongArray(length)
//        var pos = 0
//        for (array in arrays) {
//            System.arraycopy(array, 0, result, pos, array.size)
//            pos += array.size
//        }
//        return result
//    }
    fun concat(vararg arrays: LongArray): LongArray {
        var length = 0
        arrays.forEach { array ->
            length += array.size
        }
        val result = LongArray(length)
        var pos = 0
        arrays.forEach { array ->
            array.forEach { element ->
                result[pos] = element
                pos++
            }
        }
        return result
    }


    /**
     * Returns a big-endian representation of `value` in an 8-element byte
     * array; equivalent to `ByteBuffer.allocate(8).putLong(value).array()`.
     * For example, the input value `0x1213141516171819L` would yield the
     * byte array `{0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19}`.
     *
     *
     * If you need to convert and concatenate several values (possibly even of
     * different types), use a shared [java.nio.ByteBuffer] instance, or use
     * [com.google.common.io.ByteStreams.newDataOutput] to get a growable
     * buffer.
     */
    fun toByteArray(value: Long): ByteArray {
        // Note that this code needs to stay compatible with GWT, which has known
        // bugs when narrowing byte casts of long values occur.
        var value = value
        val result = ByteArray(8)
        for (i in 7 downTo 0) {
            result[i] = (value and 0xffL).toByte()
            value = value shr 8
        }
        return result
    }

    /**
     * Returns the `long` value whose big-endian representation is
     * stored in the first 8 bytes of `bytes`; equivalent to `ByteBuffer.wrap(bytes).getLong()`. For example, the input byte array
     * `{0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19}` would yield the
     * `long` value `0x1213141516171819L`.
     *
     *
     * Arguably, it's preferable to use [java.nio.ByteBuffer]; that
     * library exposes much more flexibility at little cost in readability.
     *
     * @throws IllegalArgumentException if `bytes` has fewer than 8
     * elements
     */
    fun fromByteArray(bytes: ByteArray): Long {
        checkArgument(
            bytes.size >= BYTES,
            "array too small: %s < %s", bytes.size, BYTES
        )
        return fromBytes(
            bytes[0], bytes[1], bytes[2], bytes[3],
            bytes[4], bytes[5], bytes[6], bytes[7]
        )
    }

    /**
     * Returns the `long` value whose byte representation is the given 8
     * bytes, in big-endian order; equivalent to `LongsG.fromByteArray(new
     * byte[] {b1, b2, b3, b4, b5, b6, b7, b8})`.
     *
     * @since 7.0
     */
    fun fromBytes(
        b1: Byte, b2: Byte, b3: Byte, b4: Byte,
        b5: Byte, b6: Byte, b7: Byte, b8: Byte
    ): Long {
        return b1.toLong() and 0xFFL shl 56 or (b2.toLong() and 0xFFL shl 48
                ) or (b3.toLong() and 0xFFL shl 40
                ) or (b4.toLong() and 0xFFL shl 32
                ) or (b5.toLong() and 0xFFL shl 24
                ) or (b6.toLong() and 0xFFL shl 16
                ) or (b7.toLong() and 0xFFL shl 8
                ) or (b8.toLong() and 0xFFL)
    }

    /**
     * Parses the specified string as a signed decimal long value. The ASCII
     * character `'-'` (`'&#92;u002D'`) is recognized as the
     * minus sign.
     *
     *
     * Unlike [Long.parseLong], this method returns
     * `null` instead of throwing an exception if parsing fails.
     * Additionally, this method only accepts ASCII digits, and returns
     * `null` if non-ASCII digits are present in the string.
     *
     *
     * Note that strings prefixed with ASCII `'+'` are rejected, even
     * under JDK 7, despite the change to [Long.parseLong] for
     * that version.
     *
     * @param string the string representation of a long value
     * @return the long value represented by `string`, or `null` if
     * `string` has a length of zero or cannot be parsed as a long
     * value
     * @since 14.0
     */
    fun tryParse(string: String): Long? {
        if (checkNotNull(string).isEmpty()) {
            return null
        }
        val negative = string[0] == '-'
        var index = if (negative) 1 else 0
        if (index == string.length) {
            return null
        }
        var digit = string[index++].code - '0'.code
        if (digit < 0 || digit > 9) {
            return null
        }
        var accum = -digit.toLong()
        while (index < string.length) {
            digit = string[index++].code - '0'.code
            if (digit < 0 || digit > 9 || accum < Long.MIN_VALUE / 10) {
                return null
            }
            accum *= 10
            if (accum < Long.MIN_VALUE + digit) {
                return null
            }
            accum -= digit.toLong()
        }
        return if (negative) {
            accum
        } else if (accum == Long.MIN_VALUE) {
            null
        } else {
            -accum
        }
    }

    /**
     * Returns a serializable converter object that converts between strings and
     * longs using [Long.decode] and [Long.toString].
     *
     * @since 16.0
     */
    fun stringConverter(): ConverterG<String, Long> {
        return LongConverter.INSTANCE
    }

    /**
     * Returns an array containing the same values as `array`, but
     * guaranteed to be of a specified minimum length. If `array` already
     * has a length of at least `minLength`, it is returned directly.
     * Otherwise, a new array of size `minLength + padding` is returned,
     * containing the values of `array`, and zeroes in the remaining places.
     *
     * @param array the source array
     * @param minLength the minimum length the returned array must guarantee
     * @param padding an extra amount to "grow" the array by if growth is
     * necessary
     * @throws IllegalArgumentException if `minLength` or `padding` is
     * negative
     * @return an array containing the values of `array`, with guaranteed
     * minimum length `minLength`
     */
    fun ensureCapacity(
        array: LongArray, minLength: Int, padding: Int
    ): LongArray {
        checkArgument(minLength >= 0, "Invalid minLength: %s", minLength)
        checkArgument(padding >= 0, "Invalid padding: %s", padding)
        return if (array.size < minLength) copyOf(array, minLength + padding) else array
    }

    // Arrays.copyOf() requires Java 6
//    private fun copyOf(original: LongArray, length: Int): LongArray {
//        val copy = LongArray(length)
//        System.arraycopy(original, 0, copy, 0, min(original.size, length))
//        return copy
//    }

    private fun copyOf(original: LongArray, length: Int): LongArray {
        val safeLength = original.size.coerceAtMost(length)
        val copy = LongArray(length)
        original.copyInto(copy, 0, 0, safeLength)
        return copy
    }


    /**
     * Returns a string containing the supplied `long` values separated
     * by `separator`. For example, `join("-", 1L, 2L, 3L)` returns
     * the string `"1-2-3"`.
     *
     * @param separator the text that should appear between consecutive values in
     * the resulting string (but not at the start or end)
     * @param array an array of `long` values, possibly empty
     */
    fun join(separator: String?, vararg array: Long): String {
        checkNotNull(separator)
        if (array.size == 0) {
            return ""
        }

        // For pre-sizing a builder, just get the right order of magnitude
        val builder = StringBuilder(array.size * 10)
        builder.append(array[0])
        for (i in 1 until array.size) {
            builder.append(separator).append(array[i])
        }
        return builder.toString()
    }

    /**
     * Returns a comparator that compares two `long` arrays
     * lexicographically. That is, it compares, using [ ][.compare]), the first pair of values that follow any
     * common prefix, or when one array is a prefix of the other, treats the
     * shorter array as the lesser. For example,
     * `[] < [1L] < [1L, 2L] < [2L]`.
     *
     *
     * The returned comparator is inconsistent with [ ][Object.equals] (since arrays support only identity equality), but
     * it is consistent with [Arrays.equals].
     *
     * @see [
     * Lexicographical order article at Wikipedia](http://en.wikipedia.org/wiki/Lexicographical_order)
     *
     * @since 2.0
     */
    fun lexicographicalComparator(): Comparator<LongArray> {
        return LexicographicalComparator.INSTANCE
    }

    /**
     * Returns an array containing each value of `collection`, converted to
     * a `long` value in the manner of [Number.longValue].
     *
     *
     * Elements are copied from the argument collection as if by `collection.toArray()`.  Calling this method is as thread-safe as calling
     * that method.
     *
     * @param collection a collection of `Number` instances
     * @return an array containing the same values as `collection`, in the
     * same order, converted to primitives
     * @throws NullPointerException if `collection` or any of its elements
     * is null
     * @since 1.0 (parameter was `Collection<Long>` before 12.0)
     */
    fun toArray(collection: Collection<Number>): LongArray {
        if (collection is LongArrayAsList) {
            return collection.toLongArray()
        }
        val boxedArray: Array<Any> = collection.toTypedArray()!!
        val len = boxedArray.size
        val array = LongArray(len)
        for (i in 0 until len) {
            // checkNotNull for GWT (do not optimize)
            array[i] = (checkNotNull(boxedArray[i]) as Number).toLong()
        }
        return array
    }

    /**
     * Returns a fixed-size list backed by the specified array, similar to [ ][Arrays.asList]. The list supports [List.set],
     * but any attempt to set a value to `null` will result in a [ ].
     *
     *
     * The returned list maintains the values, but not the identities, of
     * `Long` objects written to or read from it.  For example, whether
     * `list.get(0) == list.get(0)` is true for the returned list is
     * unspecified.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    fun asList(vararg backingArray: Long): List<Long> {
        return if (backingArray.size == 0) {
            emptyList()
        } else LongArrayAsList(backingArray)
    }

    private class LongConverter : ConverterG<String, Long>() {
//        protected override fun doForward(value: String): Long {
//            return java.lang.Long.decode(value)
//        }

        protected override fun doForward(value: String): Long {
            return when {
                value.startsWith("0x", ignoreCase = true) || value.startsWith("0X", ignoreCase = true) ->
                    value.drop(2).toLong(radix = 16)
                value.startsWith("#", ignoreCase = true) ->
                    value.drop(1).toLong(radix = 16)
                value.startsWith("0", ignoreCase = true) && value.length > 1 ->
                    value.drop(1).toLong(radix = 8)
                else ->
                    value.toLong()
            }
        }

        protected override fun doBackward(value: Long): String {
            return value.toString()
        }

        override fun toString(): String {
            return "LongsG.stringConverter()"
        }

        private fun readResolve(): Any {
            return INSTANCE
        }

        companion object {
            val INSTANCE = LongConverter()
            private const val serialVersionUID: Long = 1
        }
    }

    private enum class LexicographicalComparator : Comparator<LongArray> {
        INSTANCE;

//        override fun compare(left: LongArray, right: LongArray): Int {
//            val minLength = min(left.size, right.size)
//            for (i in 0 until minLength) {
//                val result = compare(left[i], right[i])
//                if (result != 0) {
//                    return result
//                }
//            }
//            return left.size - right.size
//        }

        override fun compare(left: LongArray, right: LongArray): Int {
            val minLength = minOf(left.size, right.size)
            for (i in 0 until minLength) {
                val result = left[i].compareTo(right[i])
                if (result != 0) {
                    return result
                }
            }
            return left.size - right.size
        }

    }

    private class LongArrayAsList @JvmOverloads internal constructor(
        val array: LongArray,
        val start: Int = 0,
        val end: Int = array.size, override val size: Int = end - start
    ) : AbstractList<Long>(), RandomAccess {
        override fun isEmpty(): Boolean {
            return false
        }

        override fun get(index: Int): Long {
            checkElementIndex(index, size)
            return array[start + index]
        }

        override operator fun contains(target: Long): Boolean {
            // Overridden to prevent a ton of boxing
            return (target is Long
                    && indexOf(array, target, start, end) != -1)
        }

        override fun indexOf(target: Long): Int {
            // Overridden to prevent a ton of boxing
            if (target is Long) {
                val i = indexOf(array, target, start, end)
                if (i >= 0) {
                    return i - start
                }
            }
            return -1
        }

        override fun lastIndexOf(target: Long): Int {
            // Overridden to prevent a ton of boxing
            if (target is Long) {
                val i = lastIndexOf(array, target, start, end)
                if (i >= 0) {
                    return i - start
                }
            }
            return -1
        }

        fun set(index: Int, element: Long): Long {
            checkElementIndex(index, size)
            val oldValue = array[start + index]
            // checkNotNull for GWT (do not optimize)
            array[start + index] = checkNotNull(element)
            return oldValue
        }

        override fun subList(fromIndex: Int, toIndex: Int): List<Long> {
            val size = size
            checkPositionIndexes(fromIndex, toIndex, size)
            return if (fromIndex == toIndex) {
                mutableListOf()
            } else LongArrayAsList(array, start + fromIndex, start + toIndex)
        }

        override fun equals(`object`: Any?): Boolean {
            if (`object` === this) {
                return true
            }
            if (`object` is LongArrayAsList) {
                val that = `object`
                val size = size
                if (that.size != size) {
                    return false
                }
                for (i in 0 until size) {
                    if (array[start + i] != that.array[that.start + i]) {
                        return false
                    }
                }
                return true
            }
            return super.equals(`object`)
        }

        override fun hashCode(): Int {
            var result = 1
            for (i in start until end) {
                result = 31 * result + hashCode(array[i])
            }
            return result
        }

        override fun toString(): String {
            val builder = StringBuilder(size * 10)
            builder.append('[').append(array[start])
            for (i in start + 1 until end) {
                builder.append(", ").append(array[i])
            }
            return builder.append(']').toString()
        }

//        fun toLongArray(): LongArray {
//            // Arrays.copyOfRange() is not available under GWT
//            val size = size
//            val result = LongArray(size)
//            System.arraycopy(array, start, result, 0, size)
//            return result
//        }

        fun toLongArray(): LongArray {
            val size = this.size // Assuming 'this.size' is correctly referring to some class property or calculated value.
            val result = LongArray(size)
            for (i in 0 until size) {
                result[i] = array[start + i] // Assuming 'array' and 'start' are accessible here.
            }
            return result
        }


        companion object {
            private const val serialVersionUID: Long = 0
        }
    }
}
