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
//class SecP224R1Curve : CurveAbstractFp(q) {
//    protected var infinity: SecP224R1Point
//
//    init {
//        infinity = SecP224R1Point(this, null, null)
//        setCurveA(fromBigInteger(
//            BigInteger(
//                1,
//                Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE")
//            )
//        ))
//        setCurveB(fromBigInteger(
//            BigInteger(
//                1,
//                Hex.decode("B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4")
//            )
//        ))
//        setCurveOrder(
//            BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D")))
//        setCurveCofactor(BigInteger.valueOf(1))
//        setCurveCoord(SecP224R1_DEFAULT_COORDS)
//    }
//
//    override fun cloneCurve(): ECCurve {
//        return SecP224R1Curve()
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
//        return SecP224R1FieldElement(x)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecP224R1Point(this, x, y, withCompression)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        zs: Array<ECFieldElement>,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecP224R1Point(this, x, y, zs, withCompression)
//    }
//
//    override fun getNewInfinity(): ECPoint {
//        return infinity
//    }
//
//    companion object {
//        val q = BigInteger(
//            1,
//            Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001")
//        )
//        private const val SecP224R1_DEFAULT_COORDS = COORD_JACOBIAN
//    }
//}
