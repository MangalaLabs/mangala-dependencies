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
//import org.spongycastle.math.ec.ECConstants
//import org.spongycastle.math.ec.ECCurve
//import org.spongycastle.math.ec.ECFieldElement
//import org.spongycastle.math.ec.ECPoint
//
//class SecT239K1Point : org.spongycastle.math.ec.ECPointAbstractF2m {
//
//    @Deprecated("Use ECCurve.createPoint to construct points")
//    constructor(curve: ECCurve?, x: ECFieldElement?, y: ECFieldElement?) : this(curve, x, y, false)
//
//    @Deprecated("per-point compression property will be removed, refer {@link #getEncoded(boolean)}")
//    constructor(
//        curve: ECCurve?,
//        x: ECFieldElement?,
//        y: ECFieldElement?,
//        withCompression: Boolean
//    ) : super(curve, x, y) {
//        require(x == null == (y == null)) { "Exactly one of the field elements is null" }
//        this.withCompression = withCompression
//    }
//
//    internal constructor(
//        curve: ECCurve?,
//        x: ECFieldElement?,
//        y: ECFieldElement?,
//        zs: Array<ECFieldElement>?,
//        withCompression: Boolean
//    ) : super(curve, x, y, zs) {
//        this.withCompression = withCompression
//    }
//
//    override fun detach(): ECPoint {
//        return SecT239K1Point(null, this.affineXCoord, this.affineYCoord) // earlier JDK
//    }
//
//    override fun getYCoord(): ECFieldElement {
//        val X = x
//        val L = y
//        if (this.isInfinity || X.isZero()) {
//            return L
//        }
//
//        // Y is actually Lambda (X + Y/X) here; convert to affine value on the fly
//        var Y = L.add(X).multiply(X)
//        val Z = zs[0]
//        if (!Z.isOne()) {
//            Y = Y.divide(Z)
//        }
//        return Y
//    }
//
//    override fun getCompressionYTilde(): Boolean {
//        val X = this.rawXCoord
//        if (X.isZero()) {
//            return false
//        }
//        val Y = this.rawYCoord
//
//        // Y is actually Lambda (X + Y/X) here
//        return Y.testBitZero() != X.testBitZero()
//    }
//
//    override fun add(b: ECPoint): ECPoint {
//        if (this.isInfinity) {
//            return b
//        }
//        if (b.isInfinity) {
//            return this
//        }
//        val curve = getCurve()
//        var X1 = x
//        val X2 = b.rawXCoord
//        if (X1.isZero()) {
//            return if (X2.isZero()) {
//                curve.getNewInfinity()
//            } else b.add(this)
//        }
//        val L1 = y
//        val Z1 = zs[0]
//        val L2 = b.rawYCoord
//        val Z2 = b.getZCoord(0)
//        val Z1IsOne = Z1.isOne()
//        var U2 = X2
//        var S2 = L2
//        if (!Z1IsOne) {
//            U2 = U2.multiply(Z1)
//            S2 = S2.multiply(Z1)
//        }
//        val Z2IsOne = Z2.isOne()
//        var U1 = X1
//        var S1 = L1
//        if (!Z2IsOne) {
//            U1 = U1.multiply(Z2)
//            S1 = S1.multiply(Z2)
//        }
//        val A = S1.add(S2)
//        var B = U1.add(U2)
//        if (B.isZero()) {
//            return if (A.isZero()) {
//                twice()
//            } else curve.getNewInfinity()
//        }
//        val X3: ECFieldElement
//        val L3: ECFieldElement
//        var Z3: ECFieldElement
//        if (X2.isZero()) {
//            // TODO This can probably be optimized quite a bit
//            val p = this.normalize()
//            X1 = p.xCoord
//            val Y1 = p.yCoord
//            val L = Y1.add(L2).divide(X1)
//            X3 = L.square().add(L).add(X1)
//            if (X3.isZero()) {
//                return SecT239K1Point(curve, X3, curve.getCB(), withCompression)
//            }
//            val Y3 = L.multiply(X1.add(X3)).add(X3).add(Y1)
//            L3 = Y3.divide(X3).add(X3)
//            Z3 = curve.fromBigInteger(ECConstants.ONE)
//        } else {
//            B = B.square()
//            val AU1 = A.multiply(U1)
//            val AU2 = A.multiply(U2)
//            X3 = AU1.multiply(AU2)
//            if (X3.isZero()) {
//                return SecT239K1Point(curve, X3, curve.getCB(), withCompression)
//            }
//            var ABZ2 = A.multiply(B)
//            if (!Z2IsOne) {
//                ABZ2 = ABZ2.multiply(Z2)
//            }
//            L3 = AU2.add(B).squarePlusProduct(ABZ2, L1.add(Z1))
//            Z3 = ABZ2
//            if (!Z1IsOne) {
//                Z3 = Z3.multiply(Z1)
//            }
//        }
//        return SecT239K1Point(curve, X3, L3, arrayOf(Z3), withCompression)
//    }
//
//    override fun twice(): ECPoint {
//        if (this.isInfinity) {
//            return this
//        }
//        val curve = getCurve()
//        val X1 = x
//        if (X1.isZero()) {
//            // A point with X == 0 is it's own additive inverse
//            return curve.getNewInfinity()
//        }
//        val L1 = y
//        val Z1 = zs[0]
//        val Z1IsOne = Z1.isOne()
//        val Z1Sq = if (Z1IsOne) Z1 else Z1.square()
//        val T: ECFieldElement
//        T = if (Z1IsOne) {
//            L1.square().add(L1)
//        } else {
//            L1.add(Z1).multiply(L1)
//        }
//        if (T.isZero()) {
//            return SecT239K1Point(curve, T, curve.getCB(), withCompression)
//        }
//        val X3 = T.square()
//        val Z3 = if (Z1IsOne) T else T.multiply(Z1Sq)
//        val t1 = L1.add(X1).square()
//        val t2 = if (Z1IsOne) Z1 else Z1Sq.square()
//        val L3 = t1.add(T).add(Z1Sq).multiply(t1).add(t2).add(X3).add(Z3)
//        return SecT239K1Point(curve, X3, L3, arrayOf(Z3), withCompression)
//    }
//
//    override fun twicePlus(b: ECPoint): ECPoint {
//        if (this.isInfinity) {
//            return b
//        }
//        if (b.isInfinity) {
//            return twice()
//        }
//        val curve = getCurve()
//        val X1 = x
//        if (X1.isZero()) {
//            // A point with X == 0 is it's own additive inverse
//            return b
//        }
//
//        // NOTE: twicePlus() only optimized for lambda-affine argument
//        val X2 = b.rawXCoord
//        val Z2 = b.getZCoord(0)
//        if (X2.isZero() || !Z2.isOne()) {
//            return twice().add(b)
//        }
//        val L1 = y
//        val Z1 = zs[0]
//        val L2 = b.rawYCoord
//        val X1Sq = X1.square()
//        val L1Sq = L1.square()
//        val Z1Sq = Z1.square()
//        val L1Z1 = L1.multiply(Z1)
//        val T = L1Sq.add(L1Z1)
//        val L2plus1 = L2.addOne()
//        val A = L2plus1.multiply(Z1Sq).add(L1Sq).multiplyPlusProduct(T, X1Sq, Z1Sq)
//        val X2Z1Sq = X2.multiply(Z1Sq)
//        val B = X2Z1Sq.add(T).square()
//        if (B.isZero()) {
//            return if (A?.isZero() == true) {
//                b.twice()
//            } else curve.getNewInfinity()
//        }
//        if (A?.isZero() == true) {
//            return SecT239K1Point(curve, A, curve.getCB(), withCompression)
//        }
//        val X3 = A?.square()?.multiply(X2Z1Sq)
//        val Z3 = A?.multiply(B)?.multiply(Z1Sq)!!
//        val L3 = A.add(B).square().multiplyPlusProduct(T, L2plus1, Z3)
//        return SecT239K1Point(curve, X3, L3, arrayOf(Z3), withCompression)
//    }
//
//    override fun negate(): ECPoint {
//        if (this.isInfinity) {
//            return this
//        }
//        val X = x
//        if (X.isZero()) {
//            return this
//        }
//
//        // L is actually Lambda (X + Y/X) here
//        val L = y
//        val Z = zs[0]
//        return SecT239K1Point(curve, X, L.add(Z), arrayOf(Z), withCompression)
//    }
//}
