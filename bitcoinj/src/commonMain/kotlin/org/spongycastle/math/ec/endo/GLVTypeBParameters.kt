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

package org.spongycastle.math.ec.endo

import com.ionspin.kotlin.bignum.integer.BigInteger


class GLVTypeBParameters(
    beta: BigInteger,
    lambda: BigInteger,
    v1: Array<BigInteger?>,
    v2: Array<BigInteger?>,
    g1: BigInteger,
    g2: BigInteger,
    bits: Int
) {
    val beta: BigInteger
    val lambda: BigInteger
    val v1A: BigInteger?
    val v1B: BigInteger?
    val v2A: BigInteger?
    val v2B: BigInteger?
    val g1: BigInteger
    val g2: BigInteger
    val bits: Int

    init {
        checkVector(v1, "v1")
        checkVector(v2, "v2")
        this.beta = beta
        this.lambda = lambda
        v1A = v1[0]
        v1B = v1[1]
        v2A = v2[0]
        v2B = v2[1]
        this.g1 = g1
        this.g2 = g2
        this.bits = bits
    }

    @get:Deprecated("Use {@link #getV1A()} and {@link #getV1B()} instead.")
    val v1: Array<BigInteger?>
        get() = arrayOf(v1A, v1B)

    @get:Deprecated("Use {@link #getV2A()} and {@link #getV2B()} instead.")
    val v2: Array<BigInteger?>
        get() = arrayOf(v2A, v2B)

    companion object {
        private fun checkVector(v: Array<BigInteger?>?, name: String) {
            require(!(v == null || v.size != 2 || v[0] == null || v[1] == null)) { "'$name' must consist of exactly 2 (non-null) values" }
        }
    }
}
