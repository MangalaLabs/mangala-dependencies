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

package org.spongycastle.math.ec

/**
 * Class holding precomputation data for the WNAF (Window Non-Adjacent Form)
 * algorithm.
 */
class WNafPreCompInfo : PreCompInfo {
    /**
     * Array holding the precomputed `ECPoint`s used for a Window
     * NAF multiplication.
     */
    private var mPreComp: Array<ECPoint?>? = null

    /**
     * Array holding the negations of the precomputed `ECPoint`s used
     * for a Window NAF multiplication.
     */
    private var mPreCompNeg: Array<ECPoint?>? = null

    /**
     * Holds an `ECPoint` representing twice(this). Used for the
     * Window NAF multiplication to create or extend the precomputed values.
     */
    private var mTwice: ECPoint? = null

    fun getPreComp(): Array<ECPoint?>? {
        return mPreComp
    }

    fun setPreComp(mPreComp: Array<ECPoint?>?) {
        this.mPreComp = mPreComp
    }

    fun getPreCompNeg(): Array<ECPoint?>? {
        return mPreCompNeg
    }

    fun setPreCompNeg(mPreCompNeg: Array<ECPoint?>?) {
        this.mPreCompNeg = mPreCompNeg
    }

    fun getTwice(): ECPoint? {
        return mTwice
    }

    fun setTwice(mTwice: ECPoint?) {
        this.mTwice = mTwice
    }
}
