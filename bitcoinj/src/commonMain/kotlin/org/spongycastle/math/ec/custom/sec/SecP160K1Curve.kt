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
//import org.spongycastle.math.ec.ECConstants
//import org.spongycastle.math.ec.ECCurve
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.ECPoint
//import org.spongycastle.util.encoders.Hex
//import java.math.BigInteger
//
//class SecP160K1Curve : CurveAbstractFp(q) {
//    protected var infinity: SecP160K1Point
//
//    init {
//        infinity = SecP160K1Point(this, null, null)
//        setCurveA(fromBigInteger(ECConstants.ZERO))
//        setCurveB(fromBigInteger(BigInteger.valueOf(7)))
//        setCurveOrder(BigInteger(1, Hex.decode("0100000000000000000001B8FA16DFAB9ACA16B6B3")))
//        setCurveCofactor(BigInteger.valueOf(1))
//        setCurveCoord(SECP160K1_DEFAULT_COORDS)
//    }
//
//    override fun cloneCurve(): ECCurve {
//        return SecP160K1Curve()
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
//        return SecP160R2FieldElement(x)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecP160K1Point(this, x, y, withCompression)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        zs: Array<ECFieldElement>,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecP160K1Point(this, x, y, zs, withCompression)
//    }
//
//    override fun getNewInfinity(): ECPoint {
//        return infinity
//    }
//
//    companion object {
//        val q = SecP160R2Curve.q
//        private const val SECP160K1_DEFAULT_COORDS = COORD_JACOBIAN
//    }
//}
