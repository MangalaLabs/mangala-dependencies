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

package org.spongycastle.crypto

import org.spongycastle.crypto.params.AsymmetricKeyParameter

/**
 * a holding class for public/private parameter pairs.
 */
class AsymmetricCipherKeyPair {
    /**
     * return the public key parameters.
     *
     * @return the public key parameters.
     */
    var public: AsymmetricKeyParameter
        private set

    /**
     * return the private key parameters.
     *
     * @return the private key parameters.
     */
    var private: AsymmetricKeyParameter
        private set

    /**
     * basic constructor.
     *
     * @param publicParam a public key parameters object.
     * @param privateParam the corresponding private key parameters.
     */
    constructor(
        publicParam: AsymmetricKeyParameter,
        privateParam: AsymmetricKeyParameter
    ) {
        public = publicParam
        private = privateParam
    }

    /**
     * basic constructor.
     *
     * @param publicParam a public key parameters object.
     * @param privateParam the corresponding private key parameters.
     */
    @Deprecated("use AsymmetricKeyParameter")
    constructor(
        publicParam: CipherParameters,
        privateParam: CipherParameters
    ) {
        public = publicParam as AsymmetricKeyParameter
        private = privateParam as AsymmetricKeyParameter
    }
}
