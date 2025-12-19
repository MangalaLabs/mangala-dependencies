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

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.bitcoinj.utils.shiftLeft
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.toInt
import kotlin.jvm.JvmField


/**
 * Class representing a simple version of a big decimal. A
 * `SimpleBigDecimal` is basically a
 * [BigInteger] with a few digits on the right of
 * the decimal point. The number of (binary) digits on the right of the decimal
 * point is called the `scale` of the `SimpleBigDecimal`.
 * Unlike in [BigDecimal][java.math.BigDecimal], the scale is not adjusted
 * automatically, but must be set manually. All `SimpleBigDecimal`s
 * taking part in the same arithmetic operation must have equal scale. The
 * result of a multiplication of two `SimpleBigDecimal`s returns a
 * `SimpleBigDecimal` with double scale.
 */
internal class SimpleBigDecimal //extends Number   // not in J2ME - add compatibility class?
    (bigInt: BigInteger, scale: Int) {
    private val bigInt: BigInteger

    /* NON-J2ME compliant.
    public double doubleValue()
    {
        return Double.valueOf(toString()).doubleValue();
    }

    public float floatValue()
    {
        return Float.valueOf(toString()).floatValue();
    }
       */ @JvmField
    val scale: Int

    /**
     * Constructor for `SimpleBigDecimal`. The value of the
     * constructed `SimpleBigDecimal` equals `bigInt /
     * 2<sup>scale</sup>`.
     * @param bigInt The `bigInt` value parameter.
     * @param scale The scale of the constructed `SimpleBigDecimal`.
     */
    init {
        require(scale >= 0) { "scale may not be negative" }
        this.bigInt = bigInt
        this.scale = scale
    }

    private fun checkScale(b: SimpleBigDecimal) {
        require(scale == b.scale) {
            "Only SimpleBigDecimal of " +
                    "same scale allowed in arithmetic operations"
        }
    }

    fun adjustScale(newScale: Int): SimpleBigDecimal {
        require(newScale >= 0) { "scale may not be negative" }
        return if (newScale == scale) {
            this
        } else SimpleBigDecimal(
            bigInt.shiftLeft(newScale - scale),
            newScale
        )
    }

    fun add(b: SimpleBigDecimal): SimpleBigDecimal {
        checkScale(b)
        return SimpleBigDecimal(bigInt.add(b.bigInt), scale)
    }

    fun add(b: BigInteger): SimpleBigDecimal {
        return SimpleBigDecimal(bigInt.add(b.shiftLeft(scale)), scale)
    }

    fun negate(): SimpleBigDecimal {
        return SimpleBigDecimal(bigInt.negate(), scale)
    }

    fun subtract(b: SimpleBigDecimal): SimpleBigDecimal {
        return add(b.negate())
    }

    fun subtract(b: BigInteger): SimpleBigDecimal {
        return SimpleBigDecimal(
            bigInt.subtract(b.shiftLeft(scale)),
            scale
        )
    }

    fun multiply(b: SimpleBigDecimal): SimpleBigDecimal {
        checkScale(b)
        return SimpleBigDecimal(bigInt.multiply(b.bigInt), scale + scale)
    }

    fun multiply(b: BigInteger): SimpleBigDecimal {
        return SimpleBigDecimal(bigInt.multiply(b), scale)
    }

    fun divide(b: SimpleBigDecimal): SimpleBigDecimal {
        checkScale(b)
        val dividend = bigInt.shiftLeft(scale)
        return SimpleBigDecimal(dividend.divide(b.bigInt), scale)
    }

    fun divide(b: BigInteger): SimpleBigDecimal {
        return SimpleBigDecimal(bigInt.divide(b), scale)
    }

    fun shiftLeft(n: Int): SimpleBigDecimal {
        return SimpleBigDecimal(bigInt.shiftLeft(n), scale)
    }

    operator fun compareTo(`val`: SimpleBigDecimal): Int {
        checkScale(`val`)
        return bigInt.compareTo(`val`.bigInt)
    }

    operator fun compareTo(`val`: BigInteger): Int {
        return bigInt.compareTo(`val`.shiftLeft(scale))
    }

    fun floor(): BigInteger {
        return bigInt.shiftRight(scale)
    }

    fun round(): BigInteger {
        val oneHalf = SimpleBigDecimal(ECConstants.ONE, 1)
        return add(oneHalf.adjustScale(scale)).floor()
    }

    fun intValue(): Int {
        return floor().toInt()
    }

    fun longValue(): Long {
        return floor().longValue()
    }

    override fun toString(): String {
        if (scale == 0) {
            return bigInt.toString()
        }
        var floorBigInt = floor()
        var fract = bigInt.subtract(floorBigInt.shiftLeft(scale))
        if (bigInt.signum() == -1) {
            fract = ECConstants.ONE.shiftLeft(scale).subtract(fract)
        }
        if (floorBigInt.signum() == -1 && fract != ECConstants.ZERO) {
            floorBigInt = floorBigInt.add(ECConstants.ONE)
        }
        val leftOfPoint = floorBigInt.toString()
        val fractCharArr = CharArray(scale)
        val fractStr = fract.toString(2)
        val fractLen = fractStr.length
        val zeroes = scale - fractLen
        for (i in 0 until zeroes) {
            fractCharArr[i] = '0'
        }
        for (j in 0 until fractLen) {
            fractCharArr[zeroes + j] = fractStr[j]
        }
        val rightOfPoint = fractCharArr.concatToString()
        val sb = StringBuilder(leftOfPoint)
        sb.append(".")
        sb.append(rightOfPoint)
        return sb.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is SimpleBigDecimal) {
            return false
        }
        val other = o
        return bigInt == other.bigInt && scale == other.scale
    }

    override fun hashCode(): Int {
        return bigInt.hashCode() xor scale
    }

    companion object {
        private const val serialVersionUID = 1L

        /**
         * Returns a `SimpleBigDecimal` representing the same numerical
         * value as `value`.
         * @param value The value of the `SimpleBigDecimal` to be
         * created.
         * @param scale The scale of the `SimpleBigDecimal` to be
         * created.
         * @return The such created `SimpleBigDecimal`.
         */
        fun getInstance(value: BigInteger, scale: Int): SimpleBigDecimal {
            return SimpleBigDecimal(value.shiftLeft(scale), scale)
        }
    }
}
