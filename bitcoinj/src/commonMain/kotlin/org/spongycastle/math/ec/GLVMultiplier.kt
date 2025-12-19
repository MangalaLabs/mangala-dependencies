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

package org.spongycastle.math.ec

import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.math.ec.ECAlgorithms.implShamirsTrickWNaf
import org.spongycastle.math.ec.endo.GLVEndomorphism

class GLVMultiplier(curve: ECCurve, glvEndomorphism: GLVEndomorphism) : AbstractECMultiplier() {
    protected val curve: ECCurve
    protected val glvEndomorphism: GLVEndomorphism

    init {
        require(!(curve == null || curve.getCOrder() == null)) { "Need curve with known group order" }
        this.curve = curve
        this.glvEndomorphism = glvEndomorphism
    }

    override fun multiplyPositive(p: ECPoint, k: BigInteger): ECPoint {
        check(curve.equalsCurve(p.getCurve()))
        val n = p.getCurve().getCOrder()
        val ab = glvEndomorphism.decomposeScalar(k.mod(n))
        val a = ab[0]
        val b = ab[1]
        val pointMap = glvEndomorphism.getPointMap()
        return if (glvEndomorphism.hasEfficientPointMap()) {
            implShamirsTrickWNaf(p, a, pointMap, b)
        } else implShamirsTrickWNaf(p, a, pointMap.map(p), b)
    }
}
