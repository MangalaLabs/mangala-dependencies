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
import com.google.common.kotlin.primitives.UnsignedBytesG.LexicographicalComparatorHolder.PureJavaComparator
import kotlin.jvm.JvmOverloads

//import sun.misc.Unsafe;
/**
 * Static utility methods pertaining to `byte` primitives that interpret
 * values as *unsigned* (that is, any negative value `b` is treated
 * as the positive value `256 + b`). The corresponding methods that treat
 * the values as signed are found in [SignedBytes], and the methods for
 * which signedness is not an issue are in [Bytes].
 *
 *
 * See the Guava User Guide article on [
 * primitive utilities](http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained).
 *
 * @author Kevin Bourrillion
 * @author Martin Buchholz
 * @author Hiroshi Yamauchi
 * @author Louis Wasserman
 * @since 1.0
 */
object UnsignedBytesG {
    /**
     * The largest power of two that can be represented as an unsigned `byte`.
     *
     * @since 10.0
     */
    const val MAX_POWER_OF_TWO = 0x80.toByte()

    /**
     * The largest value that fits into an unsigned byte.
     *
     * @since 13.0
     */
    const val MAX_VALUE = 0xFF.toByte()
    private const val UNSIGNED_MASK = 0xFF

    /**
     * Returns the value of the given byte as an integer, when treated as
     * unsigned. That is, returns `value + 256` if `value` is
     * negative; `value` itself otherwise.
     *
     * @since 6.0
     */
    fun toInt(value: kotlin.Byte): Int {
        return value.toInt() and UNSIGNED_MASK
    }

    const val Byte_SIZE = 8

    /**
     * Returns the `byte` value that, when treated as unsigned, is equal to
     * `value`, if possible.
     *
     * @param value a value between 0 and 255 inclusive
     * @return the `byte` value that, when treated as unsigned, equals
     * `value`
     * @throws IllegalArgumentException if `value` is negative or greater
     * than 255
     */
    fun checkedCast(value: Long): kotlin.Byte {
        require(value shr Byte_SIZE == 0L) {
            // don't use checkArgument here, to avoid boxing
            "Out of range: $value"
        }
        return value.toByte()
    }

    /**
     * Returns the `byte` value that, when treated as unsigned, is nearest
     * in value to `value`.
     *
     * @param value any `long` value
     * @return `(byte) 255` if `value >= 255`, `(byte) 0` if
     * `value <= 0`, and `value` cast to `byte` otherwise
     */
    fun saturatedCast(value: Long): kotlin.Byte {
        if (value > toInt(MAX_VALUE)) {
            return MAX_VALUE // -1
        }
        return if (value < 0) {
            0.toByte()
        } else value.toByte()
    }

    /**
     * Compares the two specified `byte` values, treating them as unsigned
     * values between 0 and 255 inclusive. For example, `(byte) -127` is
     * considered greater than `(byte) 127` because it is seen as having
     * the value of positive `129`.
     *
     * @param a the first `byte` to compare
     * @param b the second `byte` to compare
     * @return a negative value if `a` is less than `b`; a positive
     * value if `a` is greater than `b`; or zero if they are equal
     */
    fun compare(a: kotlin.Byte, b: kotlin.Byte): Int {
        return toInt(a) - toInt(b)
    }

    /**
     * Returns the least value present in `array`.
     *
     * @param array a *nonempty* array of `byte` values
     * @return the value present in `array` that is less than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if `array` is empty
     */
    fun min(vararg array: kotlin.Byte): kotlin.Byte {
        checkArgument(array.size > 0)
        var min = toInt(array[0])
        for (i in 1 until array.size) {
            val next = toInt(array[i])
            if (next < min) {
                min = next
            }
        }
        return min.toByte()
    }

    /**
     * Returns the greatest value present in `array`.
     *
     * @param array a *nonempty* array of `byte` values
     * @return the value present in `array` that is greater than or equal
     * to every other value in the array
     * @throws IllegalArgumentException if `array` is empty
     */
    fun max(vararg array: kotlin.Byte): kotlin.Byte {
        checkArgument(array.size > 0)
        var max = toInt(array[0])
        for (i in 1 until array.size) {
            val next = toInt(array[i])
            if (next > max) {
                max = next
            }
        }
        return max.toByte()
    }

    // Android-removed: no Constable support.
    // , Constable
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
    const val MIN_RADIX = 2

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
    const val MAX_RADIX = 36
    /**
     * Returns a string representation of `x` for the given radix, where `x` is treated
     * as unsigned.
     *
     * @param x the value to convert to a string.
     * @param radix the radix to use while working with `x`
     * @throws IllegalArgumentException if `radix` is not between [Character.MIN_RADIX]
     * and [Character.MAX_RADIX].
     * @since 13.0
     */
    /**
     * Returns a string representation of x, where x is treated as unsigned.
     *
     * @since 13.0
     */
    @JvmOverloads
    fun toString(x: kotlin.Byte, radix: Int = 10): String {
        checkArgument(
            radix >= MIN_RADIX && radix <= MAX_RADIX,
            "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", radix
        )
        // Benchmarks indicate this is probably not worth optimizing.
//        return Integer.toString(toInt(x), radix)
        return x.toInt().toString(radix)
    }
    /**
     * Returns the unsigned `byte` value represented by a string with the given radix.
     *
     * @param string the string containing the unsigned `byte` representation to be parsed.
     * @param radix the radix to use while parsing `string`
     * @throws NumberFormatException if the string does not contain a valid unsigned `byte`
     * with the given radix, or if `radix` is not between [Character.MIN_RADIX]
     * and [Character.MAX_RADIX].
     * @throws NullPointerException if `s` is null
     * (in contrast to [Byte.parseByte])
     * @since 13.0
     */
    /**
     * Returns the unsigned `byte` value represented by the given decimal string.
     *
     * @throws NumberFormatException if the string does not contain a valid unsigned `byte`
     * value
     * @throws NullPointerException if `s` is null
     * (in contrast to [Byte.parseByte])
     * @since 13.0
     */
    @JvmOverloads
    fun parseUnsignedByte(string: String?, radix: Int = 10): kotlin.Byte {
        val parse = checkNotNull(string).toInt(radix)
        // We need to throw a NumberFormatException, so we have to duplicate checkedCast. =(
        return if (parse shr Byte_SIZE == 0) {
            parse.toByte()
        } else {
            throw NumberFormatException("out of range: $parse")
        }
    }

    /**
     * Returns a string containing the supplied `byte` values separated by
     * `separator`. For example, `join(":", (byte) 1, (byte) 2,
     * (byte) 255)` returns the string `"1:2:255"`.
     *
     * @param separator the text that should appear between consecutive values in
     * the resulting string (but not at the start or end)
     * @param array an array of `byte` values, possibly empty
     */
    fun join(separator: String, vararg array: kotlin.Byte): String {
        checkNotNull(separator)
        if (array.size == 0) {
            return ""
        }

        // For pre-sizing a builder, just get the right order of magnitude
        val builder = StringBuilder(array.size * (3 + separator.length))
        builder.append(toInt(array[0]))
        for (i in 1 until array.size) {
            builder.append(separator).append(toString(array[i]))
        }
        return builder.toString()
    }

    /**
     * Returns a comparator that compares two `byte` arrays
     * lexicographically. That is, it compares, using [ ][.compare]), the first pair of values that follow any common
     * prefix, or when one array is a prefix of the other, treats the shorter
     * array as the lesser. For example, `[] < [0x01] < [0x01, 0x7F] <
     * [0x01, 0x80] < [0x02]`. Values are treated as unsigned.
     *
     *
     * The returned comparator is inconsistent with [ ][Object.equals] (since arrays support only identity equality), but
     * it is consistent with [java.util.Arrays.equals].
     *
     * @see [
     * Lexicographical order article at Wikipedia](http://en.wikipedia.org/wiki/Lexicographical_order)
     *
     * @since 2.0
     */
//    fun lexicographicalComparator(): Comparator<ByteArray> {
//        return LexicographicalComparatorHolder.BEST_COMPARATOR
//    }

//    fun lexicographicalComparatorJavaImpl(): Comparator<ByteArray> {
//        return PureJavaComparator.INSTANCE
//    }

    /**
     * Provides a lexicographical comparator implementation; either a Java
     * implementation or a faster implementation based on [Unsafe].
     *
     *
     * Uses reflection to gracefully fall back to the Java implementation if
     * `Unsafe` isn't available.
     */
    internal object LexicographicalComparatorHolder {
//        val UNSAFE_COMPARATOR_NAME =
//            LexicographicalComparatorHolder::class.java.name + "\$UnsafeComparator"

        val UNSAFE_COMPARATOR_NAME = "${LexicographicalComparatorHolder::class.qualifiedName}\$UnsafeComparator"

//        val BEST_COMPARATOR = bestComparator
//        val bestComparator: Comparator<ByteArray>
//            /**
//             * Returns the Unsafe-using Comparator, or falls back to the pure-Java
//             * implementation if unable to do so.
//             */
//            get() = try {
//                val theClass =
//                    Class.forName(UNSAFE_COMPARATOR_NAME)
//
//                // yes, UnsafeComparator does implement Comparator<byte[]>
//                theClass.enumConstants[0] as Comparator<ByteArray>
//            } catch (t: Throwable) { // ensure we really catch *everything*
//                lexicographicalComparatorJavaImpl()
//            }
        //        enum UnsafeComparator implements Comparator<byte[]> {
        //            INSTANCE;
        //
        //            static final boolean BIG_ENDIAN =
        //                    ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
        /*
             * The following static final fields exist for performance reasons.
             *
             * In UnsignedBytesBenchmark, accessing the following objects via static
             * final fields is the fastest (more than twice as fast as the Java
             * implementation, vs ~1.5x with non-final static fields, on x86_32)
             * under the Hotspot server compiler. The reason is obviously that the
             * non-final fields need to be reloaded inside the loop.
             *
             * And, no, defining (final or not) local variables out of the loop still
             * isn't as good because the null check on the theUnsafe object remains
             * inside the loop and BYTE_ARRAY_BASE_OFFSET doesn't get
             * constant-folded.
             *
             * The compiler can treat static final fields as compile-time constants
             * and can constant-fold them while (final or not) local variables are
             * run time values.
             */
        //            static final Unsafe theUnsafe;
        //
        //            /** The offset to the first element in a byte array. */
        //            static final int BYTE_ARRAY_BASE_OFFSET;
        //
        //            static {
        //                theUnsafe = getUnsafe();
        //
        //                BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);
        //
        //                // sanity check - this should never fail
        //                if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
        //                    throw new AssertionError();
        //                }
        //            }
        /**
         * Returns a sun.misc.Unsafe.  Suitable for use in a 3rd party package.
         * Replace with a simple call to Unsafe.getUnsafe when integrating
         * into a jdk.
         *
         * @return a sun.misc.Unsafe
         */
        //            private static sun.misc.Unsafe getUnsafe() {
        //                try {
        //                    return sun.misc.Unsafe.getUnsafe();
        //                } catch (SecurityException tryReflectionInstead) {}
        //                try {
        //                    return java.security.AccessController.doPrivileged
        //                            (new java.security.PrivilegedExceptionAction<sun.misc.Unsafe>() {
        //                                public sun.misc.Unsafe run() throws Exception {
        //                                    Class<sun.misc.Unsafe> k = sun.misc.Unsafe.class;
        //                                    for (java.lang.reflect.Field f : k.getDeclaredFields()) {
        //                                        f.setAccessible(true);
        //                                        Object x = f.get(null);
        //                                        if (k.isInstance(x))
        //                                            return k.cast(x);
        //                                    }
        //                                    throw new NoSuchFieldError("the Unsafe");
        //                                }});
        //                } catch (java.security.PrivilegedActionException e) {
        //                    throw new RuntimeException("Could not initialize intrinsics",
        //                            e.getCause());
        //                }
        //            }
        //            @Override public int compare(byte[] left, byte[] right) {
        //                int minLength = min(left.length, right.length);
        //                int minWords = minLength / LongsG.BYTES;
        //
        //                /*
        //                 * Compare 8 bytes at a time. Benchmarking shows comparing 8 bytes at a
        //                 * time is no slower than comparing 4 bytes at a time even on 32-bit.
        //                 * On the other hand, it is substantially faster on 64-bit.
        //                 */
        //                for (int i = 0; i < minWords * LongsG.BYTES; i += LongsG.BYTES) {
        //                    long lw = theUnsafe.getLong(left, BYTE_ARRAY_BASE_OFFSET + (long) i);
        //                    long rw = theUnsafe.getLong(right, BYTE_ARRAY_BASE_OFFSET + (long) i);
        //                    if (lw != rw) {
        //                        if (BIG_ENDIAN) {
        //                            return UnsignedLongsG.compare(lw, rw);
        //                        }
        //
        //                        /*
        //                         * We want to compare only the first index where left[index] != right[index].
        //                         * This corresponds to the least significant nonzero byte in lw ^ rw, since lw
        //                         * and rw are little-endian.  Long.numberOfTrailingZeros(diff) tells us the least
        //                         * significant nonzero bit, and zeroing out the first three bits of L.nTZ gives us the
        //                         * shift to get that least significant nonzero byte.
        //                         */
        //                        int n = Long.numberOfTrailingZeros(lw ^ rw) & ~0x7;
        //                        return (int) (((lw >>> n) & UNSIGNED_MASK) - ((rw >>> n) & UNSIGNED_MASK));
        //                    }
        //                }
        //
        //                // The epilogue to cover the last (minLength % 8) elements.
        //                for (int i = minWords * LongsG.BYTES; i < minLength; i++) {
        //                    int result = UnsignedBytesG.compare(left[i], right[i]);
        //                    if (result != 0) {
        //                        return result;
        //                    }
        //                }
        //                return left.length - right.length;
        //            }
        //        }
        internal enum class PureJavaComparator : Comparator<ByteArray> {
            INSTANCE;

            override fun compare(left: ByteArray, right: ByteArray): Int {
                val minLength = minOf(left.size, right.size)
                for (i in 0 until minLength) {
                    val result = compare(left[i], right[i])
                    if (result != 0) {
                        return result
                    }
                }
                return left.size - right.size
            }
        }
    }
}
