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
import com.google.common.kotlin.primitives.LongsG.hashCode
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmOverloads

/**
 * A wrapper class for unsigned `long` values, supporting arithmetic operations.
 *
 *
 * In some cases, when speed is more important than code readability, it may be faster simply to
 * treat primitive `long` values as unsigned, using the methods from [UnsignedLongsG].
 *
 *
 * See the Guava User Guide article on [
 * unsigned primitive utilities](http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained#Unsigned_support).
 *
 * @author Louis Wasserman
 * @author Colin Evans
 * @since 11.0
 */
class UnsignedLongG private constructor(private val value: Long) : Number(),
    Comparable<UnsignedLongG> {

    /**
     * Returns the result of adding this and `val`. If the result would have more than 64 bits,
     * returns the low 64 bits of the result.
     *
     * @since 14.0
     */
    operator fun plus(`val`: UnsignedLongG?): UnsignedLongG {
        return fromLongBits(value + checkNotNull(`val`).value)
    }

    /**
     * The minimum radix available for conversion to and from strings.
     * The constant value of this field is the smallest value permitted
     * for the radix argument in radix-conversion methods such as the
     * `digit` method, the `forDigit` method, and the
     * `toString` method of class `Integer`.
     *
     * @see Character.digit
     * @see Character.forDigit
     * @see Integer.toString
     * @see Integer.valueOf
     */
    val MIN_RADIX = 2

    /**
     * The maximum radix available for conversion to and from strings.
     * The constant value of this field is the largest value permitted
     * for the radix argument in radix-conversion methods such as the
     * `digit` method, the `forDigit` method, and the
     * `toString` method of class `Integer`.
     *
     * @see Character.digit
     * @see Character.forDigit
     * @see Integer.toString
     * @see Integer.valueOf
     */
    val MAX_RADIX = 36
    /**
     * Returns the result of subtracting this and `val`. If the result would have more than 64
     * bits, returns the low 64 bits of the result.
     *
     * @since 14.0
     */
    operator fun minus(`val`: UnsignedLongG?): UnsignedLongG {
        return fromLongBits(value - checkNotNull(`val`).value)
    }

    /**
     * Returns the result of multiplying this and `val`. If the result would have more than 64
     * bits, returns the low 64 bits of the result.
     *
     * @since 14.0
     */
    operator fun times(`val`: UnsignedLongG?): UnsignedLongG {
        return fromLongBits(value * checkNotNull(`val`).value)
    }

    /**
     * Returns the result of dividing this by `val`.
     *
     * @since 14.0
     */
    fun dividedBy(`val`: UnsignedLongG?): UnsignedLongG {
        return fromLongBits(UnsignedLongsG.divide(value, checkNotNull(`val`).value))
    }

    /**
     * Returns this modulo `val`.
     *
     * @since 14.0
     */
    fun mod(`val`: UnsignedLongG): UnsignedLongG {
        return fromLongBits(UnsignedLongsG.remainder(value, checkNotNull(`val`).value))
    }

    /**
     * Returns the value of this `UnsignedLongG` as an `int`.
     */
    override fun toInt(): Int {
        return value.toInt()
    }

    /**
     * Returns the value of this `UnsignedLongG` as a `long`. This is an inverse operation
     * to [.fromLongBits].
     *
     *
     * Note that if this `UnsignedLongG` holds a value `>= 2^63`, the returned value
     * will be equal to `this - 2^64`.
     */
    override fun toLong(): Long {
        return value
    }

    override fun toShort(): Short {
        return value.toShort()
    }

    /**
     * Returns the value of this `UnsignedLongG` as a `float`, analogous to a widening
     * primitive conversion from `long` to `float`, and correctly rounded.
     */
    override fun toFloat(): Float {
        var fValue = (value and UNSIGNED_MASK).toFloat()
        if (value < 0) {
            fValue += 9.223372036854776e+18f
        }
        return fValue
    }

    /**
     * Returns the value of this `UnsignedLongG` as a `double`, analogous to a widening
     * primitive conversion from `long` to `double`, and correctly rounded.
     */
    override fun toDouble(): Double {
        var dValue = (value and UNSIGNED_MASK).toDouble()
        if (value < 0) {
            dValue += 9.223372036854776e+18
        }
        return dValue
    }

    // Bit Twiddling
    val LONG_SIZE = 64

    /**
     * Returns the value of this `UnsignedLongG` as a [BigInteger].
     */
    fun bigIntegerValue(): BigInteger {
        var bigInt = BigInteger(value and UNSIGNED_MASK)
        if (value < 0) {
            bigInt = bigInt.setBitAt((LONG_SIZE - 1).toLong(), true)
        }
        return bigInt
    }


    override fun compareTo(o: UnsignedLongG): Int {
        checkNotNull(o)
        return UnsignedLongsG.compare(value, o.value)
    }

    override fun hashCode(): Int {
        return hashCode(value)
    }

    override fun toByte(): Byte {
        return value.toByte()
    }

    override fun toChar(): Char {
        return value.toChar()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is UnsignedLongG) {
            return value == obj.value
        }
        return false
    }

    /**
     * Returns a string representation of the `UnsignedLongG` value, in base 10.
     */
    override fun toString(): String {
        return UnsignedLongsG.toString(value)
    }

    /**
     * Returns a string representation of the `UnsignedLongG` value, in base `radix`. If
     * `radix < Character.MIN_RADIX` or `radix > Character.MAX_RADIX`, the radix
     * `10` is used.
     */
    fun toString(radix: Int): String {
        return UnsignedLongsG.toString(value, radix)
    }

    companion object {
        private const val UNSIGNED_MASK = 0x7fffffffffffffffL
        val ZERO = UnsignedLongG(0)
        val ONE = UnsignedLongG(1)
        val MAX_VALUE = UnsignedLongG(-1L)

        /**
         * Returns an `UnsignedLongG` corresponding to a given bit representation.
         * The argument is interpreted as an unsigned 64-bit value. Specifically, the sign bit
         * of `bits` is interpreted as a normal bit, and all other bits are treated as usual.
         *
         *
         * If the argument is nonnegative, the returned result will be equal to `bits`,
         * otherwise, the result will be equal to `2^64 + bits`.
         *
         *
         * To represent decimal constants less than `2^63`, consider [.valueOf]
         * instead.
         *
         * @since 14.0
         */
        fun fromLongBits(bits: Long): UnsignedLongG {
            // TODO(user): consider caching small values, like Long.valueOf
            return UnsignedLongG(bits)
        }

        /**
         * Returns an `UnsignedLongG` representing the same value as the specified `long`.
         *
         * @throws IllegalArgumentException if `value` is negative
         * @since 14.0
         */
        fun valueOf(value: Long): UnsignedLongG {
            checkArgument(
                value >= 0,
                "value (%s) is outside the range for an unsigned long value", value
            )
            return fromLongBits(value)
        }

        /**
         * Returns a `UnsignedLongG` representing the same value as the specified
         * `BigInteger`. This is the inverse operation of [.bigIntegerValue].
         *
         * @throws IllegalArgumentException if `value` is negative or `value >= 2^64`
         */
//        fun valueOf(value: BigInteger): UnsignedLongG {
//            checkNotNull(value)
//            checkArgument(
//                value.signum() >= 0 && value.bitLength() <= LONG_SIZE,
//                "value (%s) is outside the range for an unsigned long value", value
//            )
//            return fromLongBits(value.toLong())
//        }

        fun valueOf(value: BigInteger): UnsignedLongG {
            require(value >= BigInteger.ZERO && value.bitLength() <= ULong.SIZE_BITS) {
                "value ($value) is outside the range for an unsigned long value"
            }
//            return value.toLong().toULong()
            return fromLongBits(value.longValue())
        }
        /**
         * Returns an `UnsignedLongG` holding the value of the specified `String`, parsed as
         * an unsigned `long` value in the specified radix.
         *
         * @throws NumberFormatException if the string does not contain a parsable unsigned `long`
         * value, or `radix` is not between [Character.MIN_RADIX] and
         * [Character.MAX_RADIX]
         */
        /**
         * Returns an `UnsignedLongG` holding the value of the specified `String`, parsed as
         * an unsigned `long` value.
         *
         * @throws NumberFormatException if the string does not contain a parsable unsigned `long`
         * value
         */
        @JvmOverloads
        fun valueOf(string: String, radix: Int = 10): UnsignedLongG {
            return fromLongBits(UnsignedLongsG.parseUnsignedLong(string, radix))
        }
    }
}
