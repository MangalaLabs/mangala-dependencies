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

package com.google.common.kotlin.base

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Static convenience methods that help a method or constructor check whether it was invoked
 * correctly (whether its *preconditions* have been met). These methods generally accept a
 * `boolean` expression which is expected to be `true` (or in the case of `checkNotNull`, an object reference which is expected to be non-null). When `false` (or
 * `null`) is passed instead, the `PreconditionsG` method throws an unchecked exception,
 * which helps the calling method communicate to *its* caller that *that* caller has made
 * a mistake. Example: <pre>   `/ **
 * * Returns the positive square root of the given value.
 * *
 * * @throws IllegalArgumentException if the value is negative
 * *``/
 * public static double sqrt(double value) {
 * PreconditionsG.checkArgument(value >= 0.0, "negative value: %s", value);
 * // calculate the square root
 * }
 *
 * void exampleBadCaller() {
 * double d = sqrt(-1.0);
 * }`</pre>
 *
 * In this example, `checkArgument` throws an `IllegalArgumentException` to indicate
 * that `exampleBadCaller` made an error in *its* call to `sqrt`.
 *
 * <h3>Warning about performance</h3>
 *
 *
 * The goal of this class is to improve readability of code, but in some circumstances this may
 * come at a significant performance cost. Remember that parameter values for message construction
 * must all be computed eagerly, and autoboxing and varargs array creation may happen as well, even
 * when the precondition check then succeeds (as it should almost always do in production). In some
 * circumstances these wasted CPU cycles and allocations can add up to a real problem.
 * Performance-sensitive precondition checks can always be converted to the customary form:
 * <pre>   `if (value < 0.0) {
 * throw new IllegalArgumentException("negative value: " + value);
 * }`</pre>
 *
 * <h3>Other types of preconditions</h3>
 *
 *
 * Not every type of precondition failure is supported by these methods. Continue to throw
 * standard JDK exceptions such as [java.util.NoSuchElementException] or [ ] in the situations they are intended for.
 *
 * <h3>Non-preconditions</h3>
 *
 *
 * It is of course possible to use the methods of this class to check for invalid conditions
 * which are *not the caller's fault*. Doing so is **not recommended** because it is
 * misleading to future readers of the code and of stack traces. See
 * [Conditional
 * failures explained](http://code.google.com/p/guava-libraries/wiki/ConditionalFailuresExplained) in the Guava User Guide for more advice.
 *
 * <h3>`java.util.ObjectsG.requireNonNull()`</h3>
 *
 *
 * Projects which use `com.google.common` should generally avoid the use of [ ][java.util.ObjectsG.requireNonNull]. Instead, use whichever of [ ][.checkNotNull] or [Verify.verifyNotNull] is appropriate to the situation.
 * (The same goes for the message-accepting overloads.)
 *
 * <h3>Only `%s` is supported</h3>
 *
 *
 * In `PreconditionsG` error message template strings, only the `"%s"` specifier is
 * supported, not the full range of [java.util.Formatter] specifiers.
 *
 * <h3>More information</h3>
 *
 *
 * See the Guava User Guide on
 * [using `PreconditionsG`](http://code.google.com/p/guava-libraries/wiki/PreconditionsExplained).
 *
 * @author Kevin Bourrillion
 * @since 2.0 (imported from Google Collections Library)
 */
object PreconditionsG {
    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if `expression` is false
     */
    @JvmStatic
    fun checkArgument(expression: Boolean) {
        require(expression)
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * string using [String.valueOf]
     * @throws IllegalArgumentException if `expression` is false
     */
    @JvmStatic
    fun checkArgument(expression: Boolean, errorMessage: Any?) {
        require(expression) { errorMessage.toString() }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     * message is formed by replacing each `%s` placeholder in the template with an
     * argument. These are matched by position - the first `%s` gets `errorMessageArgs[0]`, etc.  Unmatched arguments will be appended to the formatted message
     * in square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message template. Arguments
     * are converted to strings using [String.valueOf].
     * @throws IllegalArgumentException if `expression` is false
     * @throws NullPointerException if the check fails and either `errorMessageTemplate` or
     * `errorMessageArgs` is null (don't let this happen)
     */
    @JvmStatic
    fun checkArgument(
        expression: Boolean,
        errorMessageTemplate: String?,
        vararg errorMessageArgs: Any?
    ) {
        require(expression) {
            format(
                errorMessageTemplate,
                *errorMessageArgs
            )
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalStateException if `expression` is false
     */
    fun checkState(expression: Boolean) {
        check(expression)
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * string using [String.valueOf]
     * @throws IllegalStateException if `expression` is false
     */
    fun checkState(expression: Boolean, errorMessage: Any?) {
        check(expression) { errorMessage.toString() }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     * message is formed by replacing each `%s` placeholder in the template with an
     * argument. These are matched by position - the first `%s` gets `errorMessageArgs[0]`, etc.  Unmatched arguments will be appended to the formatted message
     * in square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message template. Arguments
     * are converted to strings using [String.valueOf].
     * @throws IllegalStateException if `expression` is false
     * @throws NullPointerException if the check fails and either `errorMessageTemplate` or
     * `errorMessageArgs` is null (don't let this happen)
     */
    fun checkState(
        expression: Boolean,
        errorMessageTemplate: String?,
        vararg errorMessageArgs: Any?
    ) {
        check(expression) {
            format(
                errorMessageTemplate,
                *errorMessageArgs
            )
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if `reference` is null
     */
    @JvmStatic
    fun <T> checkNotNull(reference: T?): T {
        if (reference == null) {
            throw NullPointerException()
        }
        return reference
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * string using [String.valueOf]
     * @return the non-null reference that was validated
     * @throws NullPointerException if `reference` is null
     */
    @JvmStatic
    fun <T> checkNotNull(reference: T?, errorMessage: Any?): T {
        if (reference == null) {
            throw NullPointerException(errorMessage.toString())
        }
        return reference
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     * message is formed by replacing each `%s` placeholder in the template with an
     * argument. These are matched by position - the first `%s` gets `errorMessageArgs[0]`, etc.  Unmatched arguments will be appended to the formatted message
     * in square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message template. Arguments
     * are converted to strings using [String.valueOf].
     * @return the non-null reference that was validated
     * @throws NullPointerException if `reference` is null
     */
    @JvmStatic
    fun <T> checkNotNull(
        reference: T?,
        errorMessageTemplate: String?,
        vararg errorMessageArgs: Any?
    ): T {
        if (reference == null) {
            // If either of these parameters is null, the right thing happens anyway
            throw NullPointerException(format(errorMessageTemplate, *errorMessageArgs))
        }
        return reference
    }
    /**
     * Ensures that `index` specifies a valid *element* in an array, list or string of size
     * `size`. An element index may range from zero, inclusive, to `size`, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size the size of that array, list or string
     * @param desc the text to use to describe this index in an error message
     * @return the value of `index`
     * @throws IndexOutOfBoundsException if `index` is negative or is not less than `size`
     * @throws IllegalArgumentException if `size` is negative
     */
    /*
     * All recent hotspots (as of 2009) *really* like to have the natural code
     *
     * if (guardExpression) {
     *    throw new BadException(messageExpression);
     * }
     *
     * refactored so that messageExpression is moved to a separate String-returning method.
     *
     * if (guardExpression) {
     *    throw new BadException(badMsg(...));
     * }
     *
     * The alternative natural refactorings into void or Exception-returning methods are much slower.
     * This is a big deal - we're talking factors of 2-8 in microbenchmarks, not just 10-20%.  (This
     * is a hotspot optimizer bug, which should be fixed, but that's a separate, big project).
     *
     * The coding pattern above is heavily used in java.util, e.g. in ArrayList.  There is a
     * RangeCheckMicroBenchmark in the JDK that was used to test this.
     *
     * But the methods in this class want to throw different exceptions, depending on the args, so it
     * appears that this pattern is not directly applicable.  But we can use the ridiculous, devious
     * trick of throwing an exception in the middle of the construction of another exception.  Hotspot
     * is fine with that.
     */
    /**
     * Ensures that `index` specifies a valid *element* in an array, list or string of size
     * `size`. An element index may range from zero, inclusive, to `size`, exclusive.
     *
     * @param index a user-supplied index identifying an element of an array, list or string
     * @param size the size of that array, list or string
     * @return the value of `index`
     * @throws IndexOutOfBoundsException if `index` is negative or is not less than `size`
     * @throws IllegalArgumentException if `size` is negative
     */
    @JvmStatic
    @JvmOverloads
    fun checkElementIndex(
        index: Int, size: Int, desc: String? = "index"
    ): Int {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException(badElementIndex(index, size, desc))
        }
        return index
    }

    private fun badElementIndex(index: Int, size: Int, desc: String?): String {
        return if (index < 0) {
            format("%s (%s) must not be negative", desc, index)
        } else if (size < 0) {
            throw IllegalArgumentException("negative size: $size")
        } else { // index >= size
            format(
                "%s (%s) must be less than size (%s)",
                desc,
                index,
                size
            )
        }
    }
    /**
     * Ensures that `index` specifies a valid *position* in an array, list or string of
     * size `size`. A position index may range from zero to `size`, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size the size of that array, list or string
     * @param desc the text to use to describe this index in an error message
     * @return the value of `index`
     * @throws IndexOutOfBoundsException if `index` is negative or is greater than `size`
     * @throws IllegalArgumentException if `size` is negative
     */
    /**
     * Ensures that `index` specifies a valid *position* in an array, list or string of
     * size `size`. A position index may range from zero to `size`, inclusive.
     *
     * @param index a user-supplied index identifying a position in an array, list or string
     * @param size the size of that array, list or string
     * @return the value of `index`
     * @throws IndexOutOfBoundsException if `index` is negative or is greater than `size`
     * @throws IllegalArgumentException if `size` is negative
     */
    @JvmOverloads
    fun checkPositionIndex(index: Int, size: Int, desc: String? = "index"): Int {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException(badPositionIndex(index, size, desc))
        }
        return index
    }

    private fun badPositionIndex(index: Int, size: Int, desc: String?): String {
        return if (index < 0) {
            format("%s (%s) must not be negative", desc, index)
        } else if (size < 0) {
            throw IllegalArgumentException("negative size: $size")
        } else { // index > size
            format(
                "%s (%s) must not be greater than size (%s)",
                desc,
                index,
                size
            )
        }
    }

    /**
     * Ensures that `start` and `end` specify a valid *positions* in an array, list
     * or string of size `size`, and are in order. A position index may range from zero to
     * `size`, inclusive.
     *
     * @param start a user-supplied index identifying a starting position in an array, list or string
     * @param end a user-supplied index identifying a ending position in an array, list or string
     * @param size the size of that array, list or string
     * @throws IndexOutOfBoundsException if either index is negative or is greater than `size`,
     * or if `end` is less than `start`
     * @throws IllegalArgumentException if `size` is negative
     */
    @JvmStatic
    fun checkPositionIndexes(start: Int, end: Int, size: Int) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (start < 0 || end < start || end > size) {
            throw IndexOutOfBoundsException(badPositionIndexes(start, end, size))
        }
    }

    private fun badPositionIndexes(start: Int, end: Int, size: Int): String {
        if (start < 0 || start > size) {
            return badPositionIndex(start, size, "start index")
        }
        return if (end < 0 || end > size) {
            badPositionIndex(end, size, "end index")
        } else format(
            "end index (%s) must not be less than start index (%s)",
            end,
            start
        )
        // end < start
    }

    /**
     * Substitutes each `%s` in `template` with an argument. These are matched by
     * position: the first `%s` gets `args[0]`, etc.  If there are more arguments than
     * placeholders, the unmatched arguments will be appended to the end of the formatted message in
     * square braces.
     *
     * @param template a non-null string containing 0 or more `%s` placeholders.
     * @param args the arguments to be substituted into the message template. Arguments are converted
     * to strings using [String.valueOf]. Arguments can be null.
     */
    // Note that this is somewhat-improperly used from Verify.java as well.
    fun format(template: String?, vararg args: Any?): String {
        var template = template
        template = template.toString() // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        val builder = StringBuilder(template.length + 16 * args.size)
        var templateStart = 0
        var i = 0
        while (i < args.size) {
            val placeholderStart = template.indexOf("%s", templateStart)
            if (placeholderStart == -1) {
                break
            }
            builder.append(template.substring(templateStart, placeholderStart))
            builder.append(args[i++])
            templateStart = placeholderStart + 2
        }
        builder.append(template.substring(templateStart))

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.size) {
            builder.append(" [")
            builder.append(args[i++])
            while (i < args.size) {
                builder.append(", ")
                builder.append(args[i++])
            }
            builder.append(']')
        }
        return builder.toString()
    }
}
