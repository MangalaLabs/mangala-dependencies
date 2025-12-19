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

package org.spongycastle.math.raw//package org.spongycastle.math.raw
//
//import org.spongycastle.math.raw.Nat.addTo
//import org.spongycastle.math.raw.Nat.addWordAt
//import org.spongycastle.math.raw.Nat.subFrom
//import org.spongycastle.math.raw.Nat192.addTo
//import org.spongycastle.math.raw.Nat192.addToEachOther
//import org.spongycastle.math.raw.Nat192.create
//import org.spongycastle.math.raw.Nat192.createExt
//import org.spongycastle.math.raw.Nat192.diff
//import org.spongycastle.math.raw.Nat192.mul
//import org.spongycastle.math.raw.Nat192.square
//
//object Nat384 {
//    fun mul(x: IntArray?, y: IntArray?, zz: IntArray?) {
//        Nat192.mul(x!!, y!!, zz!!)
//        mul(x, 6, y, 6, zz, 12)
//        var c18 = addToEachOther(zz, 6, zz, 12)
//        val c12 = c18 + addTo(zz, 0, zz, 6, 0)
//        c18 += addTo(zz, 18, zz, 12, c12)
//        val dx = create()
//        val dy = create()
//        val neg = diff(x, 6, x, 0, dx, 0) != diff(y, 6, y, 0, dy, 0)
//        val tt = createExt()
//        Nat192.mul(dx, dy, tt)
//        c18 += if (neg) addTo(12, tt, 0, zz, 6) else subFrom(12, tt, 0, zz, 6)
//        addWordAt(24, c18, zz, 18)
//    }
//
//    fun square(x: IntArray?, zz: IntArray?) {
//        Nat192.square(x!!, zz!!)
//        square(x, 6, zz, 12)
//        var c18 = addToEachOther(zz, 6, zz, 12)
//        val c12 = c18 + addTo(zz, 0, zz, 6, 0)
//        c18 += addTo(zz, 18, zz, 12, c12)
//        val dx = create()
//        diff(x, 6, x, 0, dx, 0)
//        val tt = createExt()
//        Nat192.square(dx, tt)
//        c18 += subFrom(12, tt, 0, zz, 6)
//        addWordAt(24, c18, zz, 18)
//    }
//}
