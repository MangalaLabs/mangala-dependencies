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
//import org.spongycastle.math.ec.CurveAbstractF2m
//import org.spongycastle.math.ec.ECCurve
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.ECPoint
//import org.spongycastle.util.encoders.Hex
//import java.math.BigInteger
//
//class SecT113R2Curve : CurveAbstractF2m(113, 9, 0, 0) {
//    protected var infinity: SecT113R2Point
//
//    init {
//        infinity = SecT113R2Point(this, null, null)
//        setCurveA(fromBigInteger(BigInteger(1, Hex.decode("00689918DBEC7E5A0DD6DFC0AA55C7"))))
//        setCurveB(fromBigInteger(BigInteger(1, Hex.decode("0095E9A9EC9B297BD4BF36E059184F"))))
//        setCurveOrder(BigInteger(1, Hex.decode("010000000000000108789B2496AF93")))
//        setCurveCofactor(BigInteger.valueOf(2))
//        setCurveCoord(SecT113R2_DEFAULT_COORDS)
//    }
//
//    override fun cloneCurve(): ECCurve {
//        return SecT113R2Curve()
//    }
//
//    override fun supportsCoordinateSystem(coord: Int): Boolean {
//        return when (coord) {
//            COORD_LAMBDA_PROJECTIVE -> true
//            else -> false
//        }
//    }
//
//    override fun getFieldSize(): Int {
//        return 113
//    }
//
//    override fun fromBigInteger(x: BigInteger): ECFieldElement {
//        return SecT113FieldElement(x)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecT113R2Point(this, x, y, withCompression)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        zs: Array<ECFieldElement>,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecT113R2Point(this, x, y, zs, withCompression)
//    }
//
//    override fun getNewInfinity(): ECPoint {
//        return infinity
//    }
//
//    override fun isKoblitz(): Boolean {
//        return false
//    }
//
//    val m: Int
//        get() = 113
//    val isTrinomial: Boolean
//        get() = true
//    val k1: Int
//        get() = 9
//    val k2: Int
//        get() = 0
//    val k3: Int
//        get() = 0
//
//    companion object {
//        private const val SecT113R2_DEFAULT_COORDS = COORD_LAMBDA_PROJECTIVE
//    }
//}
