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

package org.spongycastle.crypto//package org.spongycastle.crypto
//
//import com.ionspin.kotlin.bignum.integer.BigInteger
//
//
///**
// * interface for classes implementing algorithms modeled similar to the Digital Signature Alorithm.
// */
//interface DSA {
//    /**
//     * initialise the signer for signature generation or signature
//     * verification.
//     *
//     * @param forSigning true if we are generating a signature, false
//     * otherwise.
//     * @param param key parameters for signature generation.
//     */
//    fun init(forSigning: Boolean, param: CipherParameters)
//
//    /**
//     * sign the passed in message (usually the output of a hash function).
//     *
//     * @param message the message to be signed.
//     * @return two big integers representing the r and s values respectively.
//     */
//    fun generateSignature(message: ByteArray): Array<BigInteger>
//
//    /**
//     * verify the message message against the signature values r and s.
//     *
//     * @param message the message that was supposed to have been signed.
//     * @param r the r signature value.
//     * @param s the s signature value.
//     */
//    fun verifySignature(message: ByteArray, r: BigInteger, s: BigInteger): Boolean
//}
