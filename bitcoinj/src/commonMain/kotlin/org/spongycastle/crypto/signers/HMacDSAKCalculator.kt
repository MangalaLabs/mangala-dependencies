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
//import com.ionspin.kotlin.bignum.integer.Sign
//import org.spongycastle.crypto.Digest
//import org.spongycastle.crypto.macs.HMac
//import org.spongycastle.crypto.params.KeyParameter
//import org.spongycastle.util.Arrays
//import org.spongycastle.util.BigIntegers
//import java.security.SecureRandom
//
///**
// * A deterministic K calculator based on the algorithm in section 3.2 of RFC 6979.
// */
//class HMacDSAKCalculator(digest: Digest?) : DSAKCalculator {
//    private val hMac: HMac
//    private val K: ByteArray
//    private val V: ByteArray
//    private var n: BigInteger? = null
//
//    /**
//     * Base constructor.
//     *
//     * @param digest digest to build the HMAC on.
//     */
//    init {
//        hMac = HMac(digest!!)
//        V = ByteArray(hMac.getMacSize())
//        K = ByteArray(hMac.getMacSize())
//    }
//
//    override fun isDeterministic(): Boolean {
//        return true
//    }
//
//    override fun init(n: BigInteger, random: SecureRandom) {
//        throw IllegalStateException("Operation not supported")
//    }
//
//    override fun init(n: BigInteger, d: BigInteger, message: ByteArray) {
//        this.n = n
//        Arrays.fill(V, 0x01.toByte())
//        Arrays.fill(K, 0.toByte())
//        val x = ByteArray((n.bitLength() + 7) / 8)
//        val dVal = BigIntegers.asUnsignedByteArray(d)
//        System.arraycopy(dVal, 0, x, x.size - dVal.size, dVal.size)
//        val m = ByteArray((n.bitLength() + 7) / 8)
//        var mInt = bitsToInt(message)
//        if (mInt.compareTo(n) >= 0) {
//            mInt = mInt.subtract(n)
//        }
//        val mVal = BigIntegers.asUnsignedByteArray(mInt)
//        System.arraycopy(mVal, 0, m, m.size - mVal.size, mVal.size)
//        hMac.init(KeyParameter(K))
//        hMac.update(V, 0, V.size)
//        hMac.update(0x00.toByte())
//        hMac.update(x, 0, x.size)
//        hMac.update(m, 0, m.size)
//        hMac.doFinal(K, 0)
//        hMac.init(KeyParameter(K))
//        hMac.update(V, 0, V.size)
//        hMac.doFinal(V, 0)
//        hMac.update(V, 0, V.size)
//        hMac.update(0x01.toByte())
//        hMac.update(x, 0, x.size)
//        hMac.update(m, 0, m.size)
//        hMac.doFinal(K, 0)
//        hMac.init(KeyParameter(K))
//        hMac.update(V, 0, V.size)
//        hMac.doFinal(V, 0)
//    }
//
//    override fun nextK(): BigInteger {
//        val t = ByteArray((n!!.bitLength() + 7) / 8)
//        while (true) {
//            var tOff = 0
//            while (tOff < t.size) {
//                hMac.update(V, 0, V.size)
//                hMac.doFinal(V, 0)
//                val len = min(t.size - tOff, V.size)
//                System.arraycopy(V, 0, t, tOff, len)
//                tOff += len
//            }
//            val k = bitsToInt(t)
//            if (k.compareTo(ZERO) > 0 && k.compareTo(n!!) < 0) {
//                return k
//            }
//            hMac.update(V, 0, V.size)
//            hMac.update(0x00.toByte())
//            hMac.doFinal(K, 0)
//            hMac.init(KeyParameter(K))
//            hMac.update(V, 0, V.size)
//            hMac.doFinal(V, 0)
//        }
//    }
//
////    private fun bitsToInt(t: ByteArray): BigInteger {
////        var v = BigInteger(1, t)
////        if (t.size * 8 > n!!.bitLength()) {
////            v = v.shiftRight(t.size * 8 - n!!.bitLength())
////        }
////        return v
////    }
//
//    private fun bitsToInt(t: ByteArray): BigInteger {
//        var v = BigInteger.fromUByteArray(t.toUByteArray(), Sign.POSITIVE)
//        val nBitLength = n!!.bitLength()
//        val tBitLength = t.size * 8
//
//        if (tBitLength > nBitLength) {
//            // Calculate how many bits we need to shift (right shift by division)
//            val shiftBits = tBitLength - nBitLength
//            // Equivalent of 2^shiftBits
//            val divisor = BigInteger.TWO.pow(shiftBits)
//            // Perform the shift by division
//            v = v.divide(divisor)
//        }
//
//        return v
//    }
//
//    companion object {
//        private val ZERO = BigInteger.ZERO
//    }
//}
