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

package org.spongycastle.math.ec//package org.spongycastle.math.ec
//
//import com.ionspin.kotlin.bignum.integer.BigInteger
//import com.mangala.wallet.utils.getLowestSetBit
//import com.mangala.wallet.utils.testBit
//
//
//class ZSignedDigitR2LMultiplier : AbstractECMultiplier() {
//    /**
//     * 'Zeroless' Signed Digit Right-to-Left.
//     */
//    override fun multiplyPositive(p: ECPoint, k: BigInteger): ECPoint {
//        var R0 = p!!.getCurve().getNewInfinity()
//        var R1 = p
//        val n = k!!.bitLength()
//        val s = k.getLowestSetBit()
//        R1 = R1.timesPow2(s)!!
//        var i = s
//        while (++i < n) {
//            R0 = R0.add(if (k.testBit(i)) R1 else R1!!.negate())!!
//            R1 = R1!!.twice()!!
//        }
//        R0 = R0.add(R1)!!
//        return R0
//    }
//}
