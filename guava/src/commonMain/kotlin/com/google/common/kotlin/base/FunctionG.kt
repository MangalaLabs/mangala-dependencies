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

/**
 * Determines an output value based on an input value.
 *
 *
 * The [Functions] class provides common functions and related utilites.
 *
 *
 * See the Guava User Guide article on [the use of `FunctionG`](http://code.google.com/p/guava-libraries/wiki/FunctionalExplained).
 *
 * @author Kevin Bourrillion
 * @since 2.0 (imported from Google Collections Library)
 */
interface FunctionG<F, T> {
    /**
     * Returns the result of applying this function to `input`. This method is *generally
     * expected*, but not absolutely required, to have the following properties:
     *
     *
     *  * Its execution does not cause any observable side effects.
     *  * The computation is *consistent with equals*; that is, [     ObjectsG.equal][ObjectsG.equal]`(a, b)` implies that `ObjectsG.equal(function.apply(a),
     * function.apply(b))`.
     *
     *
     * @throws NullPointerException if `input` is null and this function does not accept null
     * arguments
     */
    fun apply(input: F): T

    /**
     * Indicates whether another object is equal to this function.
     *
     *
     * Most implementations will have no reason to override the behavior of [Object.equals].
     * However, an implementation may also choose to return `true` whenever `object` is a
     * [FunctionG] that it considers *interchangeable* with this one. "Interchangeable"
     * *typically* means that `ObjectsG.equal(this.apply(f), that.apply(f))` is true for all
     * `f` of type `F`. Note that a `false` result from this method does not imply
     * that the functions are known *not* to be interchangeable.
     */
    override fun equals(`object`: Any?): Boolean
}
