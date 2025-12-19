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

package org.spongycastle.math.ec.custom.djb

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import org.spongycastle.math.ec.CurveAbstractFp
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.math.raw.Nat256
import org.spongycastle.util.encoders.Hex

class Curve25519 : CurveAbstractFp(q) {
    protected var infinity: Curve25519Point

    init {
        infinity = Curve25519Point(this, null, null)
        setCurveA(fromBigInteger(
            BigInteger.fromByteArray(

                Hex.decode("2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA984914A144"),
                Sign.POSITIVE
            )
        ))
        setCurveB(fromBigInteger(
            BigInteger.fromByteArray(
                Hex.decode("7B425ED097B425ED097B425ED097B425ED097B425ED097B4260B5E9C7710C864"),
                Sign.POSITIVE
            )
        ))
        setCurveOrder(BigInteger.fromByteArray(
            Hex.decode("1000000000000000000000000000000014DEF9DEA2F79CD65812631A5CF5D3ED"),
            Sign.POSITIVE
        ))
        setCurveCofactor(BigInteger.fromInt(8))
        setCurveCoord(Curve25519_DEFAULT_COORDS)
    }

    override fun cloneCurve(): ECCurve {
        return Curve25519()
    }

    override fun supportsCoordinateSystem(coord: Int): Boolean {
        return when (coord) {
            COORD_JACOBIAN_MODIFIED -> true
            else -> false
        }
    }

    fun getQ(): BigInteger{
        return q
    }

    override fun getFieldSize(): Int {
        return q.bitLength()
    }

    override fun fromBigInteger(x: BigInteger?): ECFieldElement {
        return Curve25519FieldElement(x)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        withCompression: Boolean
    ): ECPoint {
        return Curve25519Point(this, x, y, withCompression)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ): ECPoint {
        return Curve25519Point(this, x, y, zs, withCompression)
    }

    override fun getNewInfinity(): ECPoint {
        return infinity
    }

    companion object {
        val q = Nat256.toBigInteger(Curve25519Field.P)
        private const val Curve25519_DEFAULT_COORDS = COORD_JACOBIAN_MODIFIED
    }
}
