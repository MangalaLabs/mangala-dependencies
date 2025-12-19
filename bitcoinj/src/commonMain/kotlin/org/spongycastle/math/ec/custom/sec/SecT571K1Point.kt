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
//import org.spongycastle.math.ec.custom.sec.SecT571Field.add
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiplyAddToExt
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiplyPrecomp
//import org.spongycastle.math.ec.custom.sec.SecT571Field.precompMultiplicand
//import org.spongycastle.math.ec.custom.sec.SecT571Field.reduce
//import org.spongycastle.math.ec.custom.sec.SecT571Field.square
//import org.spongycastle.math.ec.custom.sec.SecT571Field.squareAddToExt
//import org.spongycastle.math.raw.Nat576
//
//class SecT571K1Point : org.spongycastle.math.ec.ECPointAbstractF2m {
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
//        return SecT571K1Point(null, this.affineXCoord, this.affineYCoord) // earlier JDK
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
//        var X1 = x as SecT571FieldElement
//        val X2 = b.rawXCoord as SecT571FieldElement
//        if (X1.isZero()) {
//            return if (X2.isZero()) {
//                curve.getNewInfinity()
//            } else b.add(this)
//        }
//        val L1 = y as SecT571FieldElement
//        val Z1 = zs[0] as SecT571FieldElement
//        val L2 = b.rawYCoord as SecT571FieldElement
//        val Z2 = b.getZCoord(0) as SecT571FieldElement
//        val t1 = Nat576.create64()
//        val t2 = Nat576.create64()
//        val t3 = Nat576.create64()
//        val t4 = Nat576.create64()
//        val Z1Precomp = if (Z1.isOne()) null else precompMultiplicand(Z1.x)
//        var U2: LongArray?
//        var S2: LongArray?
//        if (Z1Precomp == null) {
//            U2 = X2.x
//            S2 = L2.x
//        } else {
//            multiplyPrecomp(X2.x, Z1Precomp, t2.also { U2 = it })
//            multiplyPrecomp(L2.x, Z1Precomp, t4.also { S2 = it })
//        }
//        val Z2Precomp = if (Z2.isOne()) null else precompMultiplicand(Z2.x)
//        var U1: LongArray?
//        var S1: LongArray?
//        if (Z2Precomp == null) {
//            U1 = X1.x
//            S1 = L1.x
//        } else {
//            multiplyPrecomp(X1.x, Z2Precomp, t1.also { U1 = it })
//            multiplyPrecomp(L1.x, Z2Precomp, t3.also { S1 = it })
//        }
//        add(S1!!, S2!!, t3)
//        add(U1!!, U2!!, t4)
//        if (Nat576.isZero64(t4)) {
//            return if (Nat576.isZero64(t3)) {
//                twice()
//            } else curve.getNewInfinity()
//        }
//        val X3: SecT571FieldElement
//        val L3: SecT571FieldElement
//        val Z3: SecT571FieldElement
//        if (X2.isZero()) {
//            // TODO This can probably be optimized quite a bit
//            val p = this.normalize()
//            X1 = p.xCoord as SecT571FieldElement
//            val Y1 = p.yCoord
//            val Y2: ECFieldElement = L2
//            val L = Y1.add(Y2).divide(X1)
//            X3 = L.square().add(L).add(X1) as SecT571FieldElement
//            if (X3.isZero()) {
//                return SecT571K1Point(curve, X3, curve.getCB(), withCompression)
//            }
//            val Y3 = L.multiply(X1.add(X3)).add(X3).add(Y1)
//            L3 = Y3.divide(X3).add(X3) as SecT571FieldElement
//            Z3 = curve.fromBigInteger(ECConstants.ONE) as SecT571FieldElement
//        } else {
//            square(t4, t4)
//            val APrecomp = precompMultiplicand(t3)
//            multiplyPrecomp(U1!!, APrecomp, t1)
//            multiplyPrecomp(U2!!, APrecomp, t2)
//            X3 = SecT571FieldElement(t1)
//            multiply(t1, t2, X3.x)
//            if (X3.isZero()) {
//                return SecT571K1Point(curve, X3, curve.getCB(), withCompression)
//            }
//            Z3 = SecT571FieldElement(t3)
//            multiplyPrecomp(t4, APrecomp, Z3.x)
//            if (Z2Precomp != null) {
//                multiplyPrecomp(Z3.x, Z2Precomp, Z3.x)
//            }
//            val tt = Nat576.createExt64()
//            add(t2, t4, t4)
//            squareAddToExt(t4, tt)
//            add(L1.x, Z1.x, t4)
//            multiplyAddToExt(t4, Z3.x, tt)
//            L3 = SecT571FieldElement(t4)
//            reduce(tt, L3.x)
//            if (Z1Precomp != null) {
//                multiplyPrecomp(Z3.x, Z1Precomp, Z3.x)
//            }
//        }
//        return SecT571K1Point(curve, X3, L3, arrayOf(Z3), withCompression)
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
//            return SecT571K1Point(curve, T, curve.getCB(), withCompression)
//        }
//        val X3 = T.square()
//        val Z3 = if (Z1IsOne) T else T.multiply(Z1Sq)
//        val t1 = L1.add(X1).square()
//        val t2 = if (Z1IsOne) Z1 else Z1Sq.square()
//        val L3 = t1.add(T).add(Z1Sq).multiply(t1).add(t2).add(X3).add(Z3)
//        return SecT571K1Point(curve, X3, L3, arrayOf(Z3), withCompression)
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
//            return SecT571K1Point(curve, A, curve.getCB(), withCompression)
//        }
//        val X3 = A?.square()?.multiply(X2Z1Sq)
//        val Z3 = A?.multiply(B)?.multiply(Z1Sq)!!
//        val L3 = A.add(B).square().multiplyPlusProduct(T, L2plus1, Z3)
//        return SecT571K1Point(curve, X3, L3, arrayOf(Z3), withCompression)
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
//        return SecT571K1Point(curve, X, L.add(Z), arrayOf(Z), withCompression)
//    }
//}
