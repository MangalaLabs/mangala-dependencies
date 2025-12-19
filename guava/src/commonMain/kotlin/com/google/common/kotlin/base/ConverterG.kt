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

import com.google.common.kotlin.base.PreconditionsG.checkNotNull
import kotlin.jvm.Transient

//import java.io.Serializable

abstract class ConverterG<A, B>
/**
 * Constructor used only by `LegacyConverter` to suspend automatic null-handling.
 */ internal constructor(private val handleNullAutomatically: Boolean) : FunctionG<A, B> {
    // We lazily cache the reverse view to avoid allocating on every call to reverse().
    @Transient
    private var reverse: ConverterG<B, A>? = null

    /** Constructor for use by subclasses.  */
    protected constructor() : this(true)
    // SPI methods (what subclasses must implement)
    /**
     * Returns a representation of `a` as an instance of type `B`. If `a` cannot be
     * converted, an unchecked exception (such as [IllegalArgumentException]) should be thrown.
     *
     * @param a the instance to convert; will never be null
     * @return the converted instance; **must not** be null
     */
    protected abstract fun doForward(a: A): B

    /**
     * Returns a representation of `b` as an instance of type `A`. If `b` cannot be
     * converted, an unchecked exception (such as [IllegalArgumentException]) should be thrown.
     *
     * @param b the instance to convert; will never be null
     * @return the converted instance; **must not** be null
     * @throws UnsupportedOperationException if backward conversion is not implemented; this should be
     * very rare. Note that if backward conversion is not only unimplemented but
     * unimplement*able* (for example, consider a `ConverterG<Chicken, ChickenNugget>`),
     * then this is not logically a `ConverterG` at all, and should just implement [     ].
     */
    protected abstract fun doBackward(b: B): A
    // API (consumer-side) methods
    /**
     * Returns a representation of `a` as an instance of type `B`.
     *
     * @return the converted value; is null *if and only if* `a` is null
     */
    fun convert(a: A): B? {
        return correctedDoForward(a)
    }

    open fun correctedDoForward(a: A?): B? {
        return if (handleNullAutomatically) {
            // TODO(kevinb): we shouldn't be checking for a null result at runtime. Assert?
            if (a == null) null else checkNotNull<B>(doForward(a))
        } else {
            doForward(a!!)
        }
    }

    open fun correctedDoBackward(b: B?): A? {
        return if (handleNullAutomatically) {
            // TODO(kevinb): we shouldn't be checking for a null result at runtime. Assert?
            if (b == null) null else checkNotNull<A>(doBackward(b))
        } else {
            doBackward(b!!)
        }
    }

    /**
     * Returns an iterable that applies `convert` to each element of `fromIterable`. The
     * conversion is done lazily.
     *
     *
     * The returned iterable's iterator supports `remove()` if the input iterator does. After
     * a successful `remove()` call, `fromIterable` no longer contains the corresponding
     * element.
     */
//    fun convertAll(fromIterable: Iterable<A>): Iterable<B> {
//        checkNotNull(fromIterable, "fromIterable")
//        return object : Iterable<B> {
//            override fun iterator(): Iterator<B> {
//                return object : MutableIterator<B> {
//                    private val fromIterator = fromIterable.iterator()
//                    override fun hasNext(): Boolean {
//                        return fromIterator.hasNext()
//                    }
//
//                    override fun next(): B {
//                        return convert(fromIterator.next())
//                    }
//
//                    override fun remove() {
//                        fromIterator.remove()
//                    }
//                }
//            }
//        }
//    }

    /**
     * Returns the reversed view of this converter, which converts `this.convert(a)` back to a
     * value roughly equivalent to `a`.
     *
     *
     * The returned converter is serializable if `this` converter is.
     */
    // TODO(user): Make this method final
    open fun reverse(): ConverterG<B, A> {
        val result = reverse
        return result ?: ReverseConverter(this).also { reverse = it }
    }

    private class ReverseConverter<A, B> internal constructor(original: ConverterG<A, B>) :
        ConverterG<B, A>() {
        val original: ConverterG<A, B>

        /*
         * These gymnastics are a little confusing. Basically this class has neither legacy nor
         * non-legacy behavior; it just needs to let the behavior of the backing converter shine
         * through. So, we override the correctedDo* methods, after which the do* methods should never
         * be reached.
         */
        override fun doForward(b: B): A {
            throw AssertionError()
        }

        override fun doBackward(a: A): B {
            throw AssertionError()
        }

        override fun correctedDoForward(b: B?): A? {
            return original.correctedDoBackward(b)
        }

        override fun correctedDoBackward(a: A?): B? {
            return original.correctedDoForward(a)
        }

        override fun reverse(): ConverterG<A, B> {
            return original
        }

        override fun equals(`object`: Any?): Boolean {
            if (`object` is ReverseConverter<*, *>) {
                return original == `object`.original
            }
            return false
        }

        override fun hashCode(): Int {
            return original.hashCode().inv()
        }

        override fun toString(): String {
            return "$original.reverse()"
        }

        init {
            this.original = original
        }

        companion object {
            private const val serialVersionUID = 0L
        }
    }

    /**
     * Returns a converter whose `convert` method applies `secondConverter` to the result
     * of this converter. Its `reverse` method applies the converters in reverse order.
     *
     *
     * The returned converter is serializable if `this` converter and `secondConverter`
     * are.
     */
    fun <C> andThen(secondConverter: ConverterG<B, C>): ConverterG<A, C> {
        return doAndThen(secondConverter)
    }

    /**
     * Package-private non-final implementation of andThen() so only we can override it.
     */
    open fun <C> doAndThen(secondConverter: ConverterG<B, C>): ConverterG<A, C> {
        return ConverterComposition(this, checkNotNull(secondConverter))
    }

    private class ConverterComposition<A, B, C> internal constructor(
        first: ConverterG<A, B>,
        second: ConverterG<B, C>
    ) : ConverterG<A, C>() {
        val first: ConverterG<A, B>
        val second: ConverterG<B, C>

        /*
         * These gymnastics are a little confusing. Basically this class has neither legacy nor
         * non-legacy behavior; it just needs to let the behaviors of the backing converters shine
         * through (which might even differ from each other!). So, we override the correctedDo* methods,
         * after which the do* methods should never be reached.
         */
        override fun doForward(a: A): C {
            throw AssertionError()
        }

        override fun doBackward(c: C): A {
            throw AssertionError()
        }

        override fun correctedDoForward(a: A?): C? {
            return second.correctedDoForward(first.correctedDoForward(a))
        }

        override fun correctedDoBackward(c: C?): A? {
            return first.correctedDoBackward(second.correctedDoBackward(c))
        }

        override fun equals(`object`: Any?): Boolean {
            if (`object` is ConverterComposition<*, *, *>) {
                val that = `object`
                return first == that.first && second == that.second
            }
            return false
        }

        override fun hashCode(): Int {
            return 31 * first.hashCode() + second.hashCode()
        }

        override fun toString(): String {
            return "$first.andThen($second)"
        }

        init {
            this.first = first
            this.second = second
        }

        companion object {
            private const val serialVersionUID = 0L
        }
    }

    @Deprecated("Provided to satisfy the {@code FunctionG} interface; use {@link #convert} instead.")
    override fun apply(a: A): B {
        return convert(a)!!
    }

    /**
     * Indicates whether another object is equal to this converter.
     *
     *
     * Most implementations will have no reason to override the behavior of [Object.equals].
     * However, an implementation may also choose to return `true` whenever `object` is a
     * [ConverterG] that it considers *interchangeable* with this one. "Interchangeable"
     * *typically* means that `ObjectsG.equal(this.convert(a), that.convert(a))` is true for
     * all `a` of type `A` (and similarly for `reverse`). Note that a `false`
     * result from this method does not imply that the converters are known *not* to be
     * interchangeable.
     */
    override fun equals(`object`: Any?): Boolean {
        return super.equals(`object`)
    }

    private class FunctionGBasedConverter<A, B>(
        forwardFunctionG: FunctionG<in A, out B>,
        backwardFunctionG: FunctionG<in B, out A>
    ) : ConverterG<A, B>() {
        private val forwardFunctionG: FunctionG<in A, out B>
        private val backwardFunctionG: FunctionG<in B, out A>

        init {
            this.forwardFunctionG = checkNotNull(forwardFunctionG)
            this.backwardFunctionG = checkNotNull(backwardFunctionG)
        }

        protected override fun doForward(a: A): B {
            return forwardFunctionG.apply(a)
        }

        protected override fun doBackward(b: B): A {
            return backwardFunctionG.apply(b)
        }

        override fun equals(`object`: Any?): Boolean {
            if (`object` is FunctionGBasedConverter<*, *>) {
                val that = `object`
                return (forwardFunctionG.equals(that.forwardFunctionG)
                        && backwardFunctionG.equals(that.backwardFunctionG))
            }
            return false
        }

        override fun hashCode(): Int {
            return forwardFunctionG.hashCode() * 31 + backwardFunctionG.hashCode()
        }

        override fun toString(): String {
            return "ConverterG.from($forwardFunctionG, $backwardFunctionG)"
        }
    }

    /**
     * A converter that always converts or reverses an object to itself. Note that T is now a
     * "pass-through type".
     */
    private class IdentityConverter<T> : ConverterG<T, T>() {
        protected override fun doForward(t: T): T {
            return t
        }

        protected override fun doBackward(t: T): T {
            return t
        }

        override fun reverse(): IdentityConverter<T> {
            return this
        }

        override fun <S> doAndThen(otherConverter: ConverterG<T, S>): ConverterG<T, S> {
            return checkNotNull(otherConverter, "otherConverter")
        }

        /*
         * We *could* override convertAll() to return its input, but it's a rather pointless
         * optimization and opened up a weird type-safety problem.
         */
        override fun toString(): String {
            return "ConverterG.identity()"
        }

        private fun readResolve(): Any {
            return INSTANCE
        }

        companion object {
            val INSTANCE: IdentityConverter<*> = IdentityConverter<Any?>()
            private const val serialVersionUID = 0L
        }
    }

    companion object {
        // Static converters
        /**
         * Returns a converter based on *existing* forward and backward functions. Note that it is
         * unnecessary to create *new* classes implementing `FunctionG` just to pass them in
         * here. Instead, simply subclass `ConverterG` and implement its [.doForward] and
         * [.doBackward] methods directly.
         *
         *
         * These functions will never be passed `null` and must not under any circumstances
         * return `null`. If a value cannot be converted, the function should throw an unchecked
         * exception (typically, but not necessarily, [IllegalArgumentException]).
         *
         *
         * The returned converter is serializable if both provided functions are.
         *
         * @since 17.0
         */
        fun <A, B> from(
            forwardFunctionG: FunctionG<in A, out B>,
            backwardFunctionG: FunctionG<in B, out A>
        ): ConverterG<A, B> {
            return FunctionGBasedConverter(forwardFunctionG, backwardFunctionG)
        }

        /**
         * Returns a serializable converter that always converts or reverses an object to itself.
         */
        // implementation is "fully variant"
        fun <T> identity(): ConverterG<T, T> {
            return IdentityConverter.INSTANCE as IdentityConverter<T>
        }
    }
}
