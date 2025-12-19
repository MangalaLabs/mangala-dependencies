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

package org.spongycastle.crypto.signers//package org.spongycastle.crypto.signers
//
//import com.ionspin.kotlin.bignum.integer.BigInteger
//import java.security.SecureRandom
//
///**
// * Interface define calculators of K values for DSA/ECDSA.
// */
//interface DSAKCalculator {
//    /**
//     * Return true if this calculator is deterministic, false otherwise.
//     *
//     * @return true if deterministic, otherwise false.
//     */
//    fun isDeterministic(): Boolean
//
//    /**
//     * Non-deterministic initialiser.
//     *
//     * @param n the order of the DSA group.
//     * @param random a source of randomness.
//     */
//    fun init(n: BigInteger, random: SecureRandom)
//
//    /**
//     * Deterministic initialiser.
//     *
//     * @param n the order of the DSA group.
//     * @param d the DSA private value.
//     * @param message the message being signed.
//     */
//    fun init(n: BigInteger, d: BigInteger, message: ByteArray)
//
//    /**
//     * Return the next valid value of K.
//     *
//     * @return a K value.
//     */
//    fun nextK(): BigInteger
//}
