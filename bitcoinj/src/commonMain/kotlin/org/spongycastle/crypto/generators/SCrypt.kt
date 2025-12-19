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

package org.spongycastle.crypto.generators

import org.spongycastle.crypto.PBEParametersGenerator
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.engines.Salsa20Engine
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Arrays.fill
import org.spongycastle.util.Pack.intToLittleEndian
import org.spongycastle.util.Pack.littleEndianToInt

/**
 * Implementation of the scrypt a password-based key derivation function.
 *
 *
 * Scrypt was created by Colin Percival and is specified in [draft-josefsson-scrypt-kd](http://tools.ietf.org/html/draft-josefsson-scrypt-kdf-01)
 *
 */
object SCrypt {
    /**
     * Generate a key using the scrypt key derivation function.
     *
     * @param P the bytes of the pass phrase.
     * @param S the salt to use for this invocation.
     * @param N CPU/Memory cost parameter. Must be larger than 1, a power of 2 and less than
     * `2^(128 * r / 8)`.
     * @param r the block size, must be >= 1.
     * @param p Parallelization parameter. Must be a positive integer less than or equal to
     * `Integer.MAX_VALUE / (128 * r * 8)`.
     *
     * @param dkLen the length of the key to generate.
     * @return the generated key.
     */
    fun generate(P: ByteArray?, S: ByteArray?, N: Int, r: Int, p: Int, dkLen: Int): ByteArray {
        if (P == null) {
            throw IllegalArgumentException("Passphrase P must be provided.")
        }
        if (S == null) {
            throw IllegalArgumentException("Salt S must be provided.")
        }
        if (N <= 1) {
            throw IllegalArgumentException("Cost parameter N must be > 1.")
        }
        // Only value of r that cost (as an int) could be exceeded for is 1
        if (r == 1 && N > 65536) {
            throw IllegalArgumentException("Cost parameter N must be > 1 and < 65536.")
        }
        if (r < 1) {
            throw IllegalArgumentException("Block size r must be >= 1.")
        }
        val maxParallel = Int.MAX_VALUE / (128 * r * 8)
        if (p < 1 || p > maxParallel) {
            throw IllegalArgumentException(
                "Parallelisation parameter p must be >= 1 and <= " + maxParallel
                        + " (based on block size r of " + r + ")"
            )
        }
        if (dkLen < 1) {
            throw IllegalArgumentException("Generated key length dkLen must be >= 1.")
        }
        return MFcrypt(P, S, N, r, p, dkLen)
    }

    private fun MFcrypt(P: ByteArray, S: ByteArray, N: Int, r: Int, p: Int, dkLen: Int): ByteArray {
        val MFLenBytes = r * 128
        val bytes = SingleIterationPBKDF2(P, S, p * MFLenBytes)

        var B: IntArray? = null

        try {
            val BLen = bytes.size ushr 2
            B = IntArray(BLen)

            littleEndianToInt(bytes, 0, B)

            val MFLenWords = MFLenBytes ushr 2
            var BOff = 0
            while (BOff < BLen) {
                // TODO These can be done in parallel threads
                SMix(B, BOff, N, r)
                BOff += MFLenWords
            }

            intToLittleEndian(B, bytes, 0)

            return SingleIterationPBKDF2(P, bytes, dkLen)
        } finally {
            Clear(bytes)
            Clear(B)
        }
    }

    private fun SingleIterationPBKDF2(P: ByteArray, S: ByteArray, dkLen: Int): ByteArray {
        val pGen: PBEParametersGenerator = PKCS5S2ParametersGenerator(SHA256Digest())
        pGen.init(P, S, 1)
        val key = pGen.generateDerivedMacParameters(dkLen * 8) as KeyParameter
        return key.key
    }

    private fun SMix(B: IntArray, BOff: Int, N: Int, r: Int) {
        val BCount = r * 32

        val blockX1 = IntArray(16)
        val blockX2 = IntArray(16)
        val blockY = IntArray(BCount)

        val X = IntArray(BCount)
        val V = arrayOfNulls<IntArray>(N)

        try {
            B.copyInto(
                destination = X,
                destinationOffset = 0,
                startIndex = BOff,
                endIndex = BOff + BCount
            )

            for (i in 0 until N) {
                V[i] = clone(X)
                BlockMix(X, blockX1, blockX2, blockY, r)
            }

            val mask = N - 1
            for (i in 0 until N) {
                val j = X[BCount - 16] and mask
                Xor(X, V[j], 0, X)
                BlockMix(X, blockX1, blockX2, blockY, r)
            }

            X.copyInto(destination = B, destinationOffset = BOff, startIndex = 0, endIndex = BCount)
        } finally {
            ClearAll(V)
            ClearAll(arrayOf(X, blockX1, blockX2, blockY))
        }
    }

    private fun BlockMix(B: IntArray, X1: IntArray, X2: IntArray, Y: IntArray, r: Int) {
        B.copyInto(
            destination = X1,
            destinationOffset = 0,
            startIndex = B.size - 16,
            endIndex = B.size
        )

        var BOff = 0
        var YOff = 0
        val halfLen = B.size ushr 1

        for (i in 2 * r downTo 1) {
            Xor(X1, B, BOff, X2)

            Salsa20Engine.salsaCore(8, X2, X1)
            X1.copyInto(destination = Y, destinationOffset = YOff, startIndex = 0, endIndex = 16)

            YOff = halfLen + BOff - YOff
            BOff += 16
        }

        Y.copyInto(destination = B, destinationOffset = 0, startIndex = 0, endIndex = Y.size)
    }

    private fun Xor(a: IntArray, b: IntArray?, bOff: Int, output: IntArray) {
        for (i in output.indices.reversed()) {
            output[i] = a[i] xor b!![bOff + i]
        }
    }

    private fun Clear(array: ByteArray?) {
        if (array != null) {
            fill(array, 0.toByte())
        }
    }

    private fun Clear(array: IntArray?) {
        if (array != null) {
            fill(array, 0)
        }
    }

    private fun ClearAll(arrays: Array<IntArray?>) {
        for (i in arrays.indices) {
            Clear(arrays[i])
        }
    }
}
