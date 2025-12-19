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
//import org.spongycastle.math.ec.custom.sec.SecT571Field.addBothTo
//import org.spongycastle.math.ec.custom.sec.SecT571Field.addOne
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiply
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiplyAddToExt
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiplyPrecomp
//import org.spongycastle.math.ec.custom.sec.SecT571Field.multiplyPrecompAddToExt
//import org.spongycastle.math.ec.custom.sec.SecT571Field.precompMultiplicand
//import org.spongycastle.math.ec.custom.sec.SecT571Field.reduce
//import org.spongycastle.math.ec.custom.sec.SecT571Field.square
//import org.spongycastle.math.ec.custom.sec.SecT571Field.squareAddToExt
//import org.spongycastle.math.raw.Nat
//import org.spongycastle.math.raw.Nat576
//
//class SecT571R1Point : org.spongycastle.math.ec.ECPointAbstractF2m {
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
//        return SecT571R1Point(null, affineXCoord, affineYCoord)
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
//            X3 = L.square().add(L).add(X1).addOne() as SecT571FieldElement
//            if (X3.isZero()) {
//                return SecT571R1Point(curve, X3, SecT571R1Curve.SecT571R1_B_SQRT, withCompression)
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
//                return SecT571R1Point(curve, X3, SecT571R1Curve.SecT571R1_B_SQRT, withCompression)
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
//        return SecT571R1Point(curve, X3, L3, arrayOf(Z3), withCompression)
//    }
//
//    override fun twice(): ECPoint {
//        if (this.isInfinity) {
//            return this
//        }
//        val curve = getCurve()
//        val X1 = x as SecT571FieldElement
//        if (X1.isZero()) {
//            // A point with X == 0 is it's own additive inverse
//            return curve.getNewInfinity()
//        }
//        val L1 = y as SecT571FieldElement
//        val Z1 = zs[0] as SecT571FieldElement
//        val t1 = Nat576.create64()
//        val t2 = Nat576.create64()
//        val Z1Precomp = if (Z1.isOne()) null else precompMultiplicand(Z1.x)
//        var L1Z1: LongArray?
//        var Z1Sq: LongArray?
//        if (Z1Precomp == null) {
//            L1Z1 = L1.x
//            Z1Sq = Z1.x
//        } else {
//            multiplyPrecomp(L1.x, Z1Precomp, t1.also { L1Z1 = it })
//            square(Z1.x, t2.also { Z1Sq = it })
//        }
//        val T = Nat576.create64()
//        square(L1.x, T)
//        addBothTo(L1Z1!!, Z1Sq!!, T)
//        if (Nat576.isZero64(T)) {
//            return SecT571R1Point(
//                curve,
//                SecT571FieldElement(T),
//                SecT571R1Curve.SecT571R1_B_SQRT,
//                withCompression
//            )
//        }
//        val tt = Nat576.createExt64()
//        multiplyAddToExt(T, L1Z1, tt)
//        val X3 = SecT571FieldElement(t1)
//        square(T, X3.x)
//        val Z3 = SecT571FieldElement(T)
//        if (Z1Precomp != null) {
//            multiply(Z3.x, Z1Sq, Z3.x)
//        }
//        var X1Z1: LongArray?
//        if (Z1Precomp == null) {
//            X1Z1 = X1.x
//        } else {
//            multiplyPrecomp(X1.x, Z1Precomp, t2.also { X1Z1 = it })
//        }
//        squareAddToExt(X1Z1!!, tt)
//        reduce(tt, t2)
//        addBothTo(X3.x, Z3.x, t2)
//        val L3 = SecT571FieldElement(t2)
//        return SecT571R1Point(curve, X3, L3, arrayOf(Z3), withCompression)
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
//        val X1 = x as SecT571FieldElement
//        if (X1.isZero()) {
//            // A point with X == 0 is it's own additive inverse
//            return b
//        }
//        val X2 = b.rawXCoord as SecT571FieldElement
//        val Z2 = b.getZCoord(0) as SecT571FieldElement
//        if (X2.isZero() || !Z2.isOne()) {
//            return twice().add(b)
//        }
//        val L1 = y as SecT571FieldElement
//        val Z1 = zs[0] as SecT571FieldElement
//        val L2 = b.rawYCoord as SecT571FieldElement
//        val t1 = Nat576.create64()
//        val t2 = Nat576.create64()
//        val t3 = Nat576.create64()
//        val t4 = Nat576.create64()
//        square(X1.x, t1)
//        square(L1.x, t2)
//        square(Z1.x, t3)
//        multiply(L1.x, Z1.x, t4)
//        addBothTo(t3, t2, t4)
//        val Z1SqPrecomp = precompMultiplicand(t3)
//        multiplyPrecomp(L2.x, Z1SqPrecomp, t3)
//        add(t3, t2, t3)
//        val tt = Nat576.createExt64()
//        multiplyAddToExt(t3, t4, tt)
//        multiplyPrecompAddToExt(t1, Z1SqPrecomp, tt)
//        reduce(tt, t3)
//        multiplyPrecomp(X2.x, Z1SqPrecomp, t1)
//        add(t1, t4, t2)
//        square(t2, t2)
//        if (Nat576.isZero64(t2)) {
//            return if (Nat576.isZero64(t3)) {
//                b.twice()
//            } else curve.getNewInfinity()
//        }
//        if (Nat576.isZero64(t3)) {
//            return SecT571R1Point(
//                curve,
//                SecT571FieldElement(t3),
//                SecT571R1Curve.SecT571R1_B_SQRT,
//                withCompression
//            )
//        }
//        val X3 = SecT571FieldElement()
//        square(t3, X3.x)
//        multiply(X3.x, t1, X3.x)
//        val Z3 = SecT571FieldElement(t1)
//        multiply(t3, t2, Z3.x)
//        multiplyPrecomp(Z3.x, Z1SqPrecomp, Z3.x)
//        val L3 = SecT571FieldElement(t2)
//        add(t3, t2, L3.x)
//        square(L3.x, L3.x)
//        Nat.zero64(18, tt)
//        multiplyAddToExt(L3.x, t4, tt)
//        addOne(L2.x, t4)
//        multiplyAddToExt(t4, Z3.x, tt)
//        reduce(tt, L3.x)
//        return SecT571R1Point(
//            curve,
//            X3,
//            L3,
//            arrayOf(Z3),
//            withCompression
//        )
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
//        return SecT571R1Point(curve, X, L.add(Z), arrayOf(Z), withCompression)
//    }
//}
