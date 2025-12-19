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

package org.spongycastle.math.ec.custom.sec//package org.spongycastle.math.ec.custom.sec
//
//import org.spongycastle.math.ec.CurveAbstractFp
//import org.spongycastle.math.ec.ECCurve
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.ECPoint
//import org.spongycastle.util.encoders.Hex
//import java.math.BigInteger
//
//class SecP128R1Curve : CurveAbstractFp(q) {
//    protected var infinity: SecP128R1Point
//
//    init {
//        infinity = SecP128R1Point(this, null, null)
//        setCurveA(fromBigInteger(
//            BigInteger(
//                1,
//                Hex.decode("FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFC")
//            )
//        ))
//        setCurveB(fromBigInteger(
//            BigInteger(
//                1,
//                Hex.decode("E87579C11079F43DD824993C2CEE5ED3")
//            )
//        ))
//        setCurveOrder(BigInteger(1, Hex.decode("FFFFFFFE0000000075A30D1B9038A115")))
//        setCurveCofactor(BigInteger.valueOf(1))
//        setCurveCoord(SecP128R1_DEFAULT_COORDS)
//    }
//
//    override fun cloneCurve(): ECCurve {
//        return SecP128R1Curve()
//    }
//
//    override fun supportsCoordinateSystem(coord: Int): Boolean {
//        return when (coord) {
//            COORD_JACOBIAN -> true
//            else -> false
//        }
//    }
//
//    val q: BigInteger
//        get() = Companion.q
//
//    override fun getFieldSize(): Int {
//        return Companion.q.bitLength()
//    }
//
//    override fun fromBigInteger(x: BigInteger): ECFieldElement {
//        return SecP128R1FieldElement(x)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecP128R1Point(this, x, y, withCompression)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        zs: Array<ECFieldElement>,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecP128R1Point(this, x, y, zs, withCompression)
//    }
//
//    override fun getNewInfinity(): ECPoint {
//        return infinity
//    }
//
//    companion object {
//        val q = BigInteger(
//            1,
//            Hex.decode("FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFF")
//        )
//        private const val SecP128R1_DEFAULT_COORDS = COORD_JACOBIAN
//    }
//}
