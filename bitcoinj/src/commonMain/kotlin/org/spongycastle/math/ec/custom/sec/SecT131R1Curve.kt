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
//class SecT131R1Curve : CurveAbstractF2m(131, 2, 3, 8) {
//    protected var infinity: SecT131R1Point
//
//    init {
//        infinity = SecT131R1Point(this, null, null)
//        setCurveA(fromBigInteger(BigInteger(1, Hex.decode("07A11B09A76B562144418FF3FF8C2570B8"))))
//        setCurveB(fromBigInteger(BigInteger(1, Hex.decode("0217C05610884B63B9C6C7291678F9D341"))))
//        setCurveOrder(BigInteger(1, Hex.decode("0400000000000000023123953A9464B54D")))
//        setCurveCofactor(BigInteger.valueOf(2))
//        setCurveCoord(SecT131R1_DEFAULT_COORDS)
//    }
//
//    override fun cloneCurve(): ECCurve {
//        return SecT131R1Curve()
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
//        return 131
//    }
//
//    override fun fromBigInteger(x: BigInteger): ECFieldElement {
//        return SecT131FieldElement(x)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecT131R1Point(this, x, y, withCompression)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        zs: Array<ECFieldElement>,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecT131R1Point(this, x, y, zs, withCompression)
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
//        get() = 131
//    val isTrinomial: Boolean
//        get() = false
//    val k1: Int
//        get() = 2
//    val k2: Int
//        get() = 3
//    val k3: Int
//        get() = 8
//
//    companion object {
//        private const val SecT131R1_DEFAULT_COORDS = COORD_LAMBDA_PROJECTIVE
//    }
//}
