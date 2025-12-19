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

package org.spongycastle.crypto.digests

import org.spongycastle.util.Memoable
import org.spongycastle.util.Pack.bigEndianToInt
import org.spongycastle.util.Pack.intToBigEndian


/**
 * implementation of SHA-1 as outlined in "Handbook of Applied Cryptography", pages 346 - 349.
 *
 * It is interesting to ponder why the, apart from the extra IV, the other difference here from MD5
 * is the "endianness" of the word processing!
 */
class SHA1Digest : GeneralDigest, EncodableDigest {
    private var H1 = 0
    private var H2 = 0
    private var H3 = 0
    private var H4 = 0
    private var H5 = 0

    private val X = IntArray(80)
    private var xOff = 0

    /**
     * Standard constructor
     */
    constructor() {
        reset()
    }

    /**
     * Copy constructor.  This will copy the state of the provided
     * message digest.
     */
    constructor(t: SHA1Digest) : super(t) {
        copyIn(t)
    }

    /**
     * State constructor - create a digest initialised with the state of a previous one.
     *
     * @param encodedState the encoded state from the originating digest.
     */
    constructor(encodedState: ByteArray) : super(encodedState) {
        H1 = bigEndianToInt(encodedState, 16)
        H2 = bigEndianToInt(encodedState, 20)
        H3 = bigEndianToInt(encodedState, 24)
        H4 = bigEndianToInt(encodedState, 28)
        H5 = bigEndianToInt(encodedState, 32)

        xOff = bigEndianToInt(encodedState, 36)
        for (i in 0 until xOff) {
            X[i] = bigEndianToInt(encodedState, 40 + (i * 4))
        }
    }

    private fun copyIn(t: SHA1Digest) {
        H1 = t.H1
        H2 = t.H2
        H3 = t.H3
        H4 = t.H4
        H5 = t.H5

        t.X.copyInto(destination = X, destinationOffset = 0, startIndex = 0, endIndex = t.X.size)
        xOff = t.xOff
    }

    override val algorithmName: String
        get() = "SHA-1"

    override fun processWord(
        `in`: ByteArray,
        inOff: Int
    ) {
        // Note: Inlined for performance
//        X[xOff] = Pack.bigEndianToInt(in, inOff);
        var inOff = inOff
        var n = `in`[inOff].toInt() shl 24
        n = n or ((`in`[++inOff].toInt() and 0xff) shl 16)
        n = n or ((`in`[++inOff].toInt() and 0xff) shl 8)
        n = n or (`in`[++inOff].toInt() and 0xff)
        X[xOff] = n

        if (++xOff == 16) {
            processBlock()
        }
    }

    override fun processLength(
        bitLength: Long
    ) {
        if (xOff > 14) {
            processBlock()
        }

        X[14] = (bitLength ushr 32).toInt()
        X[15] = (bitLength and 0xffffffffL).toInt()
    }

    override fun doFinal(
        out: ByteArray?,
        outOff: Int
    ): Int {
        finish()

        intToBigEndian(H1, out!!, outOff)
        intToBigEndian(H2, out, outOff + 4)
        intToBigEndian(H3, out, outOff + 8)
        intToBigEndian(H4, out, outOff + 12)
        intToBigEndian(H5, out, outOff + 16)

        reset()

        return digestSize
    }

    /**
     * reset the chaining variables
     */
    override fun reset() {
        super.reset()

        H1 = 0x67452301
        H2 = -0x10325477
        H3 = -0x67452302
        H4 = 0x10325476
        H5 = -0x3c2d1e10

        xOff = 0
        for (i in X.indices) {
            X[i] = 0
        }
    }

    private fun f(
        u: Int,
        v: Int,
        w: Int
    ): Int {
        return ((u and v) or ((u.inv()) and w))
    }

    private fun h(
        u: Int,
        v: Int,
        w: Int
    ): Int {
        return (u xor v xor w)
    }

    private fun g(
        u: Int,
        v: Int,
        w: Int
    ): Int {
        return ((u and v) or (u and w) or (v and w))
    }

    override fun processBlock() {
        //
        // expand 16 word block into 80 word block.
        //
        for (i in 16..79) {
            val t = X[i - 3] xor X[i - 8] xor X[i - 14] xor X[i - 16]
            X[i] = t shl 1 or (t ushr 31)
        }

        //
        // set up working variables.
        //
        var A = H1
        var B = H2
        var C = H3
        var D = H4
        var E = H5

        //
        // round 1
        //
        var idx = 0

        for (j in 0..3) {
            // E = rotateLeft(A, 5) + f(B, C, D) + E + X[idx++] + Y1
            // B = rotateLeft(B, 30)
            E += (A shl 5 or (A ushr 27)) + f(B, C, D) + X[idx++] + Y1
            B = B shl 30 or (B ushr 2)

            D += (E shl 5 or (E ushr 27)) + f(A, B, C) + X[idx++] + Y1
            A = A shl 30 or (A ushr 2)

            C += (D shl 5 or (D ushr 27)) + f(E, A, B) + X[idx++] + Y1
            E = E shl 30 or (E ushr 2)

            B += (C shl 5 or (C ushr 27)) + f(D, E, A) + X[idx++] + Y1
            D = D shl 30 or (D ushr 2)

            A += (B shl 5 or (B ushr 27)) + f(C, D, E) + X[idx++] + Y1
            C = C shl 30 or (C ushr 2)
        }


        //
        // round 2
        //
        for (j in 0..3) {
            // E = rotateLeft(A, 5) + h(B, C, D) + E + X[idx++] + Y2
            // B = rotateLeft(B, 30)
            E += (A shl 5 or (A ushr 27)) + h(B, C, D) + X[idx++] + Y2
            B = B shl 30 or (B ushr 2)

            D += (E shl 5 or (E ushr 27)) + h(A, B, C) + X[idx++] + Y2
            A = A shl 30 or (A ushr 2)

            C += (D shl 5 or (D ushr 27)) + h(E, A, B) + X[idx++] + Y2
            E = E shl 30 or (E ushr 2)

            B += (C shl 5 or (C ushr 27)) + h(D, E, A) + X[idx++] + Y2
            D = D shl 30 or (D ushr 2)

            A += (B shl 5 or (B ushr 27)) + h(C, D, E) + X[idx++] + Y2
            C = C shl 30 or (C ushr 2)
        }


        //
        // round 3
        //
        for (j in 0..3) {
            // E = rotateLeft(A, 5) + g(B, C, D) + E + X[idx++] + Y3
            // B = rotateLeft(B, 30)
            E += (A shl 5 or (A ushr 27)) + g(B, C, D) + X[idx++] + Y3
            B = B shl 30 or (B ushr 2)

            D += (E shl 5 or (E ushr 27)) + g(A, B, C) + X[idx++] + Y3
            A = A shl 30 or (A ushr 2)

            C += (D shl 5 or (D ushr 27)) + g(E, A, B) + X[idx++] + Y3
            E = E shl 30 or (E ushr 2)

            B += (C shl 5 or (C ushr 27)) + g(D, E, A) + X[idx++] + Y3
            D = D shl 30 or (D ushr 2)

            A += (B shl 5 or (B ushr 27)) + g(C, D, E) + X[idx++] + Y3
            C = C shl 30 or (C ushr 2)
        }

        //
        // round 4
        //
        for (j in 0..3) {
            // E = rotateLeft(A, 5) + h(B, C, D) + E + X[idx++] + Y4
            // B = rotateLeft(B, 30)
            E += (A shl 5 or (A ushr 27)) + h(B, C, D) + X[idx++] + Y4
            B = B shl 30 or (B ushr 2)

            D += (E shl 5 or (E ushr 27)) + h(A, B, C) + X[idx++] + Y4
            A = A shl 30 or (A ushr 2)

            C += (D shl 5 or (D ushr 27)) + h(E, A, B) + X[idx++] + Y4
            E = E shl 30 or (E ushr 2)

            B += (C shl 5 or (C ushr 27)) + h(D, E, A) + X[idx++] + Y4
            D = D shl 30 or (D ushr 2)

            A += (B shl 5 or (B ushr 27)) + h(C, D, E) + X[idx++] + Y4
            C = C shl 30 or (C ushr 2)
        }


        H1 += A
        H2 += B
        H3 += C
        H4 += D
        H5 += E

        //
        // reset start of the buffer.
        //
        xOff = 0
        for (i in 0..15) {
            X[i] = 0
        }
    }

    override fun copy(): Memoable {
        return SHA1Digest(this)
    }

    override fun reset(other: Memoable) {
        val d = other as SHA1Digest

        super.copyIn(d)
        copyIn(d)
    }

    override val encodedState: ByteArray
        get() {
            val state = ByteArray(40 + xOff * 4)

            super.populateState(state)

            intToBigEndian(H1, state, 16)
            intToBigEndian(H2, state, 20)
            intToBigEndian(H3, state, 24)
            intToBigEndian(H4, state, 28)
            intToBigEndian(H5, state, 32)
            intToBigEndian(xOff, state, 36)

            for (i in 0 until xOff) {
                intToBigEndian(X[i], state, 40 + (i * 4))
            }

            return state
        }

    override val digestSize: Int = 20

    companion object {

        //
        // Additive constants
        //
        private const val Y1 = 0x5a827999
        private const val Y2 = 0x6ed9eba1
        private const val Y3 = -0x70e44324
        private const val Y4 = -0x359d3e2a
    }
}
