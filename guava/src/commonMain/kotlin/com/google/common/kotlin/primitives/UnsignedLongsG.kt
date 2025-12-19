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

import com.google.common.kotlin.base.PreconditionsG.checkArgument
import com.google.common.kotlin.base.PreconditionsG.checkNotNull
import com.google.common.kotlin.primitives.ParseRequestG.Companion.fromString
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmOverloads

//import java.math.BigInteger

/**
 * Static utility methods pertaining to `long` primitives that interpret values as
 * *unsigned* (that is, any negative value `x` is treated as the positive value
 * `2^64 + x`). The methods for which signedness is not an issue are in [LongsG], as
 * well as signed versions of methods for which signedness is an issue.
 *
 *
 * In addition, this class provides several static methods for converting a `long` to a
 * `String` and a `String` to a `long` that treat the `long` as an unsigned
 * number.
 *
 *
 * Users of these utilities must be *extremely careful* not to mix up signed and unsigned
 * `long` values. When possible, it is recommended that the [UnsignedLongG] wrapper
 * class be used, at a small efficiency penalty, to enforce the distinction in the type system.
 *
 *
 * See the Guava User Guide article on [
 * unsigned primitive utilities](http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained#Unsigned_support).
 *
 * @author Louis Wasserman
 * @author Brian Milch
 * @author Colin Evans
 * @since 10.0
 */
object UnsignedLongsG {
    const val MAX_VALUE = -1L // Equivalent to 2^64 - 1

    const val MIN_RADIX = 2

    const val MAX_RADIX = 36
    /**
     * A (self-inverse) bijection which converts the ordering on unsigned longs to the ordering on
     * longs, that is, `a <= b` as unsigned longs if and only if `flip(a) <= flip(b)`
     * as signed longs.
     */
    private fun flip(a: Long): Long {
        return a xor Long.MIN_VALUE
    }

    /**
     * Compares the two specified `long` values, treating them as unsigned values between
     * `0` and `2^64 - 1` inclusive.
     *
     * @param a the first unsigned `long` to compare
     * @param b the second unsigned `long` to compare
     * @return a negative value if `a` is less than `b`; a positive value if `a` is
     * greater than `b`; or zero if they are equal
     */
    fun compare(a: Long, b: Long): Int {
        return LongsG.compare(flip(a), flip(b))
    }

    /**
     * Returns the least value present in `array`, treating values as unsigned.
     *
     * @param array a *nonempty* array of unsigned `long` values
     * @return the value present in `array` that is less than or equal to every other value in
     * the array according to [.compare]
     * @throws IllegalArgumentException if `array` is empty
     */
    fun min(vararg array: Long): Long {
        checkArgument(array.size > 0)
        var min = flip(array[0])
        for (i in 1 until array.size) {
            val next = flip(array[i])
            if (next < min) {
                min = next
            }
        }
        return flip(min)
    }

    /**
     * Returns the greatest value present in `array`, treating values as unsigned.
     *
     * @param array a *nonempty* array of unsigned `long` values
     * @return the value present in `array` that is greater than or equal to every other value
     * in the array according to [.compare]
     * @throws IllegalArgumentException if `array` is empty
     */
    fun max(vararg array: Long): Long {
        checkArgument(array.size > 0)
        var max = flip(array[0])
        for (i in 1 until array.size) {
            val next = flip(array[i])
            if (next > max) {
                max = next
            }
        }
        return flip(max)
    }

    /**
     * Returns a string containing the supplied unsigned `long` values separated by
     * `separator`. For example, `join("-", 1, 2, 3)` returns the string `"1-2-3"`.
     *
     * @param separator the text that should appear between consecutive values in the resulting
     * string (but not at the start or end)
     * @param array an array of unsigned `long` values, possibly empty
     */
    fun join(separator: String?, vararg array: Long): String {
        checkNotNull(separator)
        if (array.size == 0) {
            return ""
        }

        // For pre-sizing a builder, just get the right order of magnitude
        val builder = StringBuilder(array.size * 5)
        builder.append(toString(array[0]))
        for (i in 1 until array.size) {
            builder.append(separator).append(toString(array[i]))
        }
        return builder.toString()
    }

    /**
     * Returns a comparator that compares two arrays of unsigned `long` values
     * lexicographically. That is, it compares, using [.compare]), the first pair of
     * values that follow any common prefix, or when one array is a prefix of the other, treats the
     * shorter array as the lesser. For example, `[] < [1L] < [1L, 2L] < [2L] < [1L << 63]`.
     *
     *
     * The returned comparator is inconsistent with [Object.equals] (since arrays
     * support only identity equality), but it is consistent with
     * [Arrays.equals].
     *
     * @see [Lexicographical order
     * article at Wikipedia](http://en.wikipedia.org/wiki/Lexicographical_order)
     */
    fun lexicographicalComparator(): Comparator<LongArray> {
        return LexicographicalComparator.INSTANCE
    }

    /**
     * Returns dividend / divisor, where the dividend and divisor are treated as unsigned 64-bit
     * quantities.
     *
     * @param dividend the dividend (numerator)
     * @param divisor the divisor (denominator)
     * @throws ArithmeticException if divisor is 0
     */
    fun divide(dividend: Long, divisor: Long): Long {
        if (divisor < 0) { // i.e., divisor >= 2^63:
            return if (compare(dividend, divisor) < 0) {
                0 // dividend < divisor
            } else {
                1 // dividend >= divisor
            }
        }

        // Optimization - use signed division if dividend < 2^63
        if (dividend >= 0) {
            return dividend / divisor
        }

        /*
         * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
         * guaranteed to be either exact or one less than the correct value. This follows from fact
         * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
         * quite trivial.
         */
        val quotient = (dividend ushr 1) / divisor shl 1
        val rem = dividend - quotient * divisor
        return quotient + if (compare(rem, divisor) >= 0) 1 else 0
    }

    /**
     * Returns dividend % divisor, where the dividend and divisor are treated as unsigned 64-bit
     * quantities.
     *
     * @param dividend the dividend (numerator)
     * @param divisor the divisor (denominator)
     * @throws ArithmeticException if divisor is 0
     * @since 11.0
     */
    fun remainder(dividend: Long, divisor: Long): Long {
        if (divisor < 0) { // i.e., divisor >= 2^63:
            return if (compare(dividend, divisor) < 0) {
                dividend // dividend < divisor
            } else {
                dividend - divisor // dividend >= divisor
            }
        }

        // Optimization - use signed modulus if dividend < 2^63
        if (dividend >= 0) {
            return dividend % divisor
        }

        /*
         * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
         * guaranteed to be either exact or one less than the correct value. This follows from fact
         * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
         * quite trivial.
         */
        val quotient = (dividend ushr 1) / divisor shl 1
        val rem = dividend - quotient * divisor
        return rem - if (compare(rem, divisor) >= 0) divisor else 0
    }

    /**
     * Returns the unsigned `long` value represented by the given string.
     *
     * Accepts a decimal, hexadecimal, or octal number given by specifying the following prefix:
     *
     *
     *  * `0x`*HexDigits*
     *  * `0X`*HexDigits*
     *  * `#`*HexDigits*
     *  * `0`*OctalDigits*
     *
     *
     * @throws NumberFormatException if the string does not contain a valid unsigned `long`
     * value
     * @since 13.0
     */
    fun decode(stringValue: String): Long {
        val request = fromString(stringValue)
        return try {
            parseUnsignedLong(request.rawValue, request.radix)
        } catch (e: NumberFormatException) {
//            val decodeException = NumberFormatException("Error parsing value: $stringValue")
//            decodeException.initCause(e)

            val decodeException = NumberFormatException("Error parsing value: $stringValue").apply {
//                initCause(e)
            }
            throw decodeException
        }
    }
    /**
     * Returns the unsigned `long` value represented by a string with the given radix.
     *
     * @param s the string containing the unsigned `long` representation to be parsed.
     * @param radix the radix to use while parsing `s`
     * @throws NumberFormatException if the string does not contain a valid unsigned `long`
     * with the given radix, or if `radix` is not between [Character.MIN_RADIX]
     * and [Character.MAX_RADIX].
     * @throws NullPointerException if `s` is null
     * (in contrast to [Long.parseLong])
     */
    /**
     * Returns the unsigned `long` value represented by the given decimal string.
     *
     * @throws NumberFormatException if the string does not contain a valid unsigned `long`
     * value
     * @throws NullPointerException if `s` is null
     * (in contrast to [Long.parseLong])
     */
    @JvmOverloads
    fun parseUnsignedLong(s: String, radix: Int = 10): Long {
        checkNotNull(s)
        if (s.length == 0) {
            throw NumberFormatException("empty string")
        }
        if (radix < MIN_RADIX || radix > MAX_RADIX) {
            throw NumberFormatException("illegal radix: $radix")
        }
        val max_safe_pos = maxSafeDigits[radix] - 1
        var value: Long = 0
        for (pos in 0 until s.length) {
            val digit = s[pos].digitToIntOrNull(radix) ?: -1
            if (digit == -1) {
                throw NumberFormatException(s)
            }
            if (pos > max_safe_pos && overflowInParse(value, digit, radix)) {
                throw NumberFormatException("Too large for unsigned long: $s")
            }
            value = value * radix + digit
        }
        return value
    }

    /**
     * Returns true if (current * radix) + digit is a number too large to be represented by an
     * unsigned long. This is useful for detecting overflow while parsing a string representation of
     * a number. Does not verify whether supplied radix is valid, passing an invalid radix will give
     * undefined results or an ArrayIndexOutOfBoundsException.
     */
    private fun overflowInParse(current: Long, digit: Int, radix: Int): Boolean {
        if (current >= 0) {
            if (current < maxValueDivs[radix]) {
                return false
            }
            return if (current > maxValueDivs[radix]) {
                true
            } else digit > maxValueMods[radix]
            // current == maxValueDivs[radix]
        }

        // current < 0: high bit is set
        return true
    }
    /**
     * Returns a string representation of `x` for the given radix, where `x` is treated
     * as unsigned.
     *
     * @param x the value to convert to a string.
     * @param radix the radix to use while working with `x`
     * @throws IllegalArgumentException if `radix` is not between [Character.MIN_RADIX]
     * and [Character.MAX_RADIX].
     */
    /**
     * Returns a string representation of x, where x is treated as unsigned.
     */
//    @JvmOverloads
//    fun toString(x: Long, radix: Int = 10): String {
//        var x = x
//        checkArgument(
//            radix >= MIN_RADIX && radix <= MAX_RADIX,
//            "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", radix
//        )
//        return if (x == 0L) {
//            // Simply return "0"
//            "0"
//        } else {
//            val buf = CharArray(64)
//            var i = buf.size
//            if (x < 0) {
//                // Separate off the last digit using unsigned division. That will leave
//                // a number that is nonnegative as a signed integer.
//                val quotient = divide(x, radix.toLong())
//                val rem = x - quotient * radix
//                buf[--i] = Character.forDigit(rem.toInt(), radix)
//                x = quotient
//            }
//            // Simple modulo/division approach
//            while (x > 0) {
//                buf[--i] = Character.forDigit((x % radix).toInt(), radix)
//                x /= radix.toLong()
//            }
//            // Generate string
//            String(buf, i, buf.size - i)
//        }
//    }

    fun toString(x: Long, radix: Int = 10): String {
        require(radix in MIN_RADIX..MAX_RADIX) {
            "radix ($radix) must be between $MIN_RADIX and $MAX_RADIX"
        }

        if (x == 0L) return "0"

        val buf = CharArray(64)
        var i = 64
        var value = x

        // Handle negative values separately
        if (value < 0) {
            // For negative numbers, handle as positive and then adjust manually if needed
            while (value <= -radix) {
                buf[--i] = ((-(value % radix)).toInt()).toString(radix).first()
                value /= radix
            }
            buf[--i] = (-(value % radix)).toInt().toString(radix).first()
            // Prefix with negative sign if necessary
            return "-${buf.concatToString(i, i + (64 - i))}"
        } else {
            // Handle positive numbers
            while (value >= radix) {
                buf[--i] = (value % radix).toInt().toString(radix).first()
                value /= radix
            }
            buf[--i] = value.toInt().toString(radix).first()
            return buf.concatToString(i, i + (64 - i))
        }
    }


    // calculated as 0xffffffffffffffff / radix
    private val maxValueDivs = LongArray(MAX_RADIX + 1)
    private val maxValueMods = IntArray(MAX_RADIX + 1)
    private val maxSafeDigits = IntArray(MAX_RADIX + 1)

    init {
//        val overflow = BigInteger("10000000000000000", 16)
        val overflow = BigInteger.parseString("10000000000000000", 16)
        val start = MIN_RADIX
        for (i in 2..MAX_RADIX) {
            maxValueDivs[i] = divide(MAX_VALUE, i.toLong())
            maxValueMods[i] = remainder(MAX_VALUE, i.toLong()).toInt()
            maxSafeDigits[i] = overflow.toString(i).length - 1
        }
    }

    internal enum class LexicographicalComparator : Comparator<LongArray> {
        INSTANCE;

        override fun compare(left: LongArray, right: LongArray): Int {
            val minLength = minOf(left.size, right.size)
            for (i in 0 until minLength) {
                if (left[i] != right[i]) {
                    return compare(left[i], right[i])
                }
            }
            return left.size - right.size
        }
    }
}
