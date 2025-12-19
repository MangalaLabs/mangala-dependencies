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
import com.mangala.wallet.bitcoinj.utils.getLowestSetBit
import com.mangala.wallet.bitcoinj.utils.modPow
import com.mangala.wallet.bitcoinj.utils.shiftLeft
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.testBit
import org.spongycastle.math.ec.ECConstants.ONE
import org.spongycastle.math.ec.ECConstants.TWO
import org.spongycastle.math.raw.Mod.invert
import org.spongycastle.math.raw.Nat.create
import org.spongycastle.math.raw.Nat.fromBigInteger
import org.spongycastle.math.raw.Nat.toBigInteger
import org.spongycastle.util.randomBigInteger
import kotlin.jvm.JvmStatic

class EFp internal constructor(q: BigInteger, r: BigInteger?, x: BigInteger?) : ECFieldElement() {
    var q: BigInteger
    var r: BigInteger?
    var x: BigInteger

    @Deprecated("Use ECCurve.fromBigInteger to construct field elements")
    constructor(q: BigInteger, x: BigInteger?) : this(q, calculateResidue(q), x)

    init {
        require(!(x == null || x.signum() < 0 || x.compareTo(q) >= 0)) { "x value invalid in Fp field element" }
        this.q = q
        this.r = r
        this.x = x
    }

    override fun toBigInteger(): BigInteger {
        return x
    }

    /**
     * return the field name for this field.
     *
     * @return the string "Fp".
     */
    override fun getFieldName(): String {
        return "Fp"
    }

    override fun getFieldSize(): Int {
        return q.bitLength()
    }

    override fun add(b: ECFieldElement): ECFieldElement {
        return EFp(q, r, modAdd(x, b.toBigInteger()))
    }

    override fun addOne(): ECFieldElement {
        var x2 = x.add(ECConstants.ONE)
        if (x2.compareTo(q) == 0) {
            x2 = ECConstants.ZERO
        }
        return EFp(q, r, x2)
    }

    override fun subtract(b: ECFieldElement): ECFieldElement {
        return EFp(q, r, modSubtract(x, b.toBigInteger()))
    }

    override fun multiply(b: ECFieldElement): ECFieldElement {
        return EFp(q, r, modMult(x, b.toBigInteger()))
    }

    override fun multiplyMinusProduct(
        b: ECFieldElement,
        x: ECFieldElement,
        y: ECFieldElement
    ): ECFieldElement {
        val ax = this.x
        val bx = b.toBigInteger()
        val xx = x.toBigInteger()
        val yx = y.toBigInteger()
        val ab = ax.multiply(bx)
        val xy = xx.multiply(yx)
        return EFp(q, r, modReduce(ab.subtract(xy)))
    }

    override fun multiplyPlusProduct(
        b: ECFieldElement,
        x: ECFieldElement,
        y: ECFieldElement
    ): ECFieldElement {
        val ax = this.x
        val bx = b.toBigInteger()
        val xx = x.toBigInteger()
        val yx = y.toBigInteger()
        val ab = ax.multiply(bx)
        val xy = xx.multiply(yx)
        return EFp(q, r, modReduce(ab.add(xy)))
    }

    override fun divide(b: ECFieldElement): ECFieldElement {
        return EFp(q, r, modMult(x, modInverse(b.toBigInteger())))
    }

    override fun negate(): ECFieldElement {
        return if (x.signum() == 0) this else EFp(q, r, q.subtract(x))
    }

    override fun square(): ECFieldElement {
        return EFp(q, r, modMult(x, x))
    }

    override fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        val ax = this.x
        val xx = x.toBigInteger()
        val yx = y.toBigInteger()
        val aa = ax.multiply(ax)
        val xy = xx.multiply(yx)
        return EFp(q, r, modReduce(aa.subtract(xy)))
    }

    override fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        val ax = this.x
        val xx = x.toBigInteger()
        val yx = y.toBigInteger()
        val aa = ax.multiply(ax)
        val xy = xx.multiply(yx)
        return EFp(q, r, modReduce(aa.add(xy)))
    }

    override fun invert(): ECFieldElement {
        // TODO Modular inversion can be faster for a (Generalized) Mersenne Prime.
        return EFp(q, r, modInverse(x))
    }
    // D.1.4 91
    /**
     * return a sqrt root - the routine verifies that the calculation
     * returns the right value - if none exists it returns null.
     */
    override fun sqrt(): ECFieldElement? {
        if (this.isZero() || this.isOne()) // earlier JDK compatibility
        {
            return this
        }
        if (!q.testBit(0)) {
            throw RuntimeException("not done yet")
        }

        // note: even though this class implements ECConstants don't be tempted to
        // remove the explicit declaration, some J2ME environments don't cope.
        if (q.testBit(1)) // q == 4m + 3
        {
            val e = q.shiftRight(2).add(ECConstants.ONE)
            return checkSqrt(EFp(q, r, x.modPow(e, q)))!!
        }
        if (q.testBit(2)) // q == 8m + 5
        {
            val t1 = x.modPow(q.shiftRight(3), q)
            val t2 = modMult(t1, x)
            val t3 = modMult(t2, t1)
            if (t3 == ECConstants.ONE) {
                return checkSqrt(EFp(q, r, t2))!!
            }

            // TODO This is constant and could be precomputed
            val t4 = ECConstants.TWO.modPow(q.shiftRight(2), q)
            val y = modMult(t2, t4)
            return checkSqrt(EFp(q, r, y))!!
        }

        // q == 8m + 1
        val legendreExponent = q.shiftRight(1)
        if (x.modPow(legendreExponent, q) != ECConstants.ONE) {
            return null
        }
        val X = x
        val fourX = modDouble(modDouble(X))
        val k = legendreExponent.add(ECConstants.ONE)
        val qMinusOne = q.subtract(ECConstants.ONE)
        var U: BigInteger
        var V: BigInteger
        do {
            var P: BigInteger
            do {
                P = randomBigInteger(q.bitLength())
            } while (P.compareTo(q) >= 0
                || modReduce(P.multiply(P).subtract(fourX)).modPow(legendreExponent, q) != qMinusOne
            )
            val result = lucasSequence(P, X, k)
            U = result[0]
            V = result[1]
            if (modMult(V, V) == fourX) {
                return EFp(q, r, modHalfAbs(V))
            }
        } while (U == ECConstants.ONE || U == qMinusOne)
        return null
    }

    private fun checkSqrt(z: ECFieldElement): ECFieldElement? {
        return if (z.square() == this) z else null
    }

    private fun lucasSequence(
        P: BigInteger,
        Q: BigInteger,
        k: BigInteger
    ): Array<BigInteger> {
        // TODO Research and apply "common-multiplicand multiplication here"
        val n = k.bitLength()
        val s = k.getLowestSetBit()

        // assert k.testBit(s);
        var Uh = ONE
        var Vl = TWO
        var Vh = P
        var Ql = ONE
        var Qh = ONE
        for (j in n - 1 downTo s + 1) {
            Ql = modMult(Ql, Qh)
            if (k.testBit(j)) {
                Qh = modMult(Ql, Q)
                Uh = modMult(Uh, Vh)
                Vl = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)))
                Vh = modReduce(Vh.multiply(Vh).subtract(Qh.shiftLeft(1)))
            } else {
                Qh = Ql
                Uh = modReduce(Uh.multiply(Vl).subtract(Ql))
                Vh = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)))
                Vl = modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)))
            }
        }
        Ql = modMult(Ql, Qh)
        Qh = modMult(Ql, Q)
        Uh = modReduce(Uh.multiply(Vl).subtract(Ql))
        Vl = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)))
        Ql = modMult(Ql, Qh)
        for (j in 1..s) {
            Uh = modMult(Uh, Vl)
            Vl = modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)))
            Ql = modMult(Ql, Ql)
        }
        return arrayOf(Uh, Vl)
    }

    protected fun modAdd(x1: BigInteger, x2: BigInteger): BigInteger {
        var x3 = x1.add(x2)
        if (x3.compareTo(q) >= 0) {
            x3 = x3.subtract(q)
        }
        return x3
    }

    protected fun modDouble(x: BigInteger): BigInteger {
        var _2x = x.shiftLeft(1)
        if (_2x.compareTo(q) >= 0) {
            _2x = _2x.subtract(q)
        }
        return _2x
    }

    protected fun modHalf(x: BigInteger): BigInteger {
        var x = x
        if (x.testBit(0)) {
            x = q.add(x)
        }
        return x.shiftRight(1)
    }

    protected fun modHalfAbs(x: BigInteger): BigInteger {
        var x = x
        if (x.testBit(0)) {
            x = q.subtract(x)
        }
        return x.shiftRight(1)
    }

    protected fun modInverse(x: BigInteger): BigInteger {
        val bits = getFieldSize()
        val len = bits + 31 shr 5
        val p = fromBigInteger(bits, q)
        val n = fromBigInteger(bits, x!!)
        val z = create(len)
        invert(p, n, z)
        return toBigInteger(len, z)
    }

    protected fun modMult(x1: BigInteger, x2: BigInteger): BigInteger {
        return modReduce(x1.multiply(x2))
    }

    protected fun modReduce(x: BigInteger): BigInteger {
        var x = x
        if (r != null) {
            val negative = x.signum() < 0
            if (negative) {
                x = x.abs()
            }
            val qLen = q.bitLength()
            val rIsOne = r == ONE
            while (x.bitLength() > qLen + 1) {
                var u = x.shiftRight(qLen)
                val v = x.subtract(u.shiftLeft(qLen))
                if (!rIsOne) {
                    u = u.multiply(r!!)
                }
                x = u.add(v)
            }
            while (x.compareTo(q) >= 0) {
                x = x.subtract(q)
            }
            if (negative && x.signum() != 0) {
                x = q.subtract(x)
            }
        } else {
            x = x.mod(q)
        }
        return x
    }

    protected fun modSubtract(x1: BigInteger, x2: BigInteger): BigInteger {
        var x3 = x1.subtract(x2)
        if (x3.signum() < 0) {
            x3 = x3.add(q)
        }
        return x3
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is EFp) {
            return false
        }
        val o = other
        return q == o.q && x == o.x
    }

    override fun hashCode(): Int {
        return q.hashCode() xor x.hashCode()
    }

    companion object {
        @JvmStatic
        fun calculateResidue(p: BigInteger): BigInteger? {
            val bitLength = p.bitLength()
            if (bitLength >= 96) {
                val firstWord = p.shiftRight(bitLength - 64)
                if (firstWord.longValue() == -1L) {
                    return ONE.shiftLeft(bitLength).subtract(p)
                }
            }
            return null
        }
    }
}
