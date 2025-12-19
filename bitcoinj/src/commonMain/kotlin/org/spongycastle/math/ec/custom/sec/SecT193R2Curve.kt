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
//class SecT193R2Curve : CurveAbstractF2m(193, 15, 0, 0) {
//    protected var infinity: SecT193R2Point
//
//    init {
//        infinity = SecT193R2Point(this, null, null)
//        setCurveA(fromBigInteger(
//            BigInteger(
//                1,
//                Hex.decode("0163F35A5137C2CE3EA6ED8667190B0BC43ECD69977702709B")
//            )
//        ))
//        setCurveB(fromBigInteger(
//            BigInteger(
//                1,
//                Hex.decode("00C9BB9E8927D4D64C377E2AB2856A5B16E3EFB7F61D4316AE")
//            )
//        ))
//        setCurveOrder(BigInteger(1, Hex.decode("010000000000000000000000015AAB561B005413CCD4EE99D5")))
//        setCurveCofactor(BigInteger.valueOf(2))
//        setCurveCoord(SecT193R2_DEFAULT_COORDS)
//    }
//
//    override fun cloneCurve(): ECCurve {
//        return SecT193R2Curve()
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
//        return 193
//    }
//
//    override fun fromBigInteger(x: BigInteger): ECFieldElement {
//        return SecT193FieldElement(x)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecT193R2Point(this, x, y, withCompression)
//    }
//
//    override fun createRawPoint(
//        x: ECFieldElement,
//        y: ECFieldElement,
//        zs: Array<ECFieldElement>,
//        withCompression: Boolean
//    ): ECPoint {
//        return SecT193R2Point(this, x, y, zs, withCompression)
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
//        get() = 193
//    val isTrinomial: Boolean
//        get() = true
//    val k1: Int
//        get() = 15
//    val k2: Int
//        get() = 0
//    val k3: Int
//        get() = 0
//
//    companion object {
//        private const val SecT193R2_DEFAULT_COORDS = COORD_LAMBDA_PROJECTIVE
//    }
//}
