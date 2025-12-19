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

package org.spongycastle.math.ec.custom.gm

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import org.spongycastle.math.ec.CurveAbstractFp
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.util.encoders.Hex

class SM2P256V1Curve : CurveAbstractFp(q) {
    protected var infinity: SM2P256V1Point

    init {
        infinity = SM2P256V1Point(this, null, null)
        setCurveA(fromBigInteger(
            BigInteger.fromByteArray(

                Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC"),
                Sign.POSITIVE
            )
        ))
        setCurveB(fromBigInteger(
            BigInteger.fromByteArray(

                Hex.decode("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93"),
                Sign.POSITIVE
            )
        ))
        setCurveOrder(BigInteger.fromByteArray(

            Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123"),
            Sign.POSITIVE
        ))
        setCurveCofactor(BigInteger.fromInt(1))
        setCurveCoord(SM2P256V1_DEFAULT_COORDS)
    }

    override fun cloneCurve(): ECCurve {
        return SM2P256V1Curve()
    }

    override fun supportsCoordinateSystem(coord: Int): Boolean {
        return when (coord) {
            COORD_JACOBIAN -> true
            else -> false
        }
    }

    val q: BigInteger
        get() = Companion.q

    override fun getFieldSize(): Int {
        return Companion.q.bitLength()
    }

    override fun fromBigInteger(x: BigInteger?): ECFieldElement {
        return SM2P256V1FieldElement(x)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        withCompression: Boolean
    ): ECPoint {
        return SM2P256V1Point(this, x, y, withCompression)
    }

    override fun createRawPoint(
        x: ECFieldElement?,
        y: ECFieldElement?,
        zs: Array<ECFieldElement>,
        withCompression: Boolean
    ): ECPoint {
        return SM2P256V1Point(this, x, y, zs, withCompression)
    }

    override fun getNewInfinity(): ECPoint {
        return infinity
    }

    companion object {
        val q = BigInteger.fromByteArray(

            Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF"),
            Sign.POSITIVE
        )
        private const val SM2P256V1_DEFAULT_COORDS = COORD_JACOBIAN
    }
}
