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

package org.spongycastle.crypto.macs

import co.touchlab.stately.collections.ConcurrentMutableMap
import org.spongycastle.crypto.CipherParameters
import org.spongycastle.crypto.Digest
import org.spongycastle.crypto.ExtendedDigest
import org.spongycastle.crypto.Mac
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.util.Integers.valueOf
import org.spongycastle.util.Memoable

/**
 * HMAC implementation based on RFC2104
 *
 * H(K XOR opad, H(K XOR ipad, text))
 */
class HMac
private constructor(
    val underlyingDigest: Digest,
    private val blockLength: Int
) : Mac {
    private val digestSize = underlyingDigest.digestSize
    private var ipadState: Memoable? = null
    private var opadState: Memoable? = null

    private val inputPad = ByteArray(blockLength)
    private val outputBuf = ByteArray(blockLength + digestSize)

    /**
     * Base constructor for one of the standard digest algorithms that the
     * byteLength of the algorithm is know for.
     *
     * @param digest the digest.
     */
    constructor(digest: Digest) : this(digest, getByteLength(digest))

    override fun getAlgorithmName(): String {
        return underlyingDigest.algorithmName + "/HMAC"
    }

    override fun init(
        params: CipherParameters
    ) {
        underlyingDigest.reset()

        val key = (params as KeyParameter).key
        var keyLength = key.size

        if (keyLength > blockLength) {
            underlyingDigest.update(key, 0, keyLength)
            underlyingDigest.doFinal(inputPad, 0)

            keyLength = digestSize
        } else {
            key.copyInto(
                destination = inputPad,
                destinationOffset = 0,
                startIndex = 0,
                endIndex = keyLength
            )
        }

        for (i in keyLength until inputPad.size) {
            inputPad[i] = 0
        }

        inputPad.copyInto(
            destination = outputBuf,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = blockLength
        )

        xorPad(inputPad, blockLength, IPAD)
        xorPad(outputBuf, blockLength, OPAD)

        if (underlyingDigest is Memoable) {
            opadState = (underlyingDigest as Memoable).copy()

            (opadState as Digest).update(outputBuf, 0, blockLength)
        }

        underlyingDigest.update(inputPad, 0, inputPad.size)

        if (underlyingDigest is Memoable) {
            ipadState = (underlyingDigest as Memoable).copy()
        }
    }

    override fun getMacSize(): Int {
        return digestSize
    }

    override fun update(
        `in`: Byte
    ) {
        underlyingDigest.update(`in`)
    }

    override fun update(
        `in`: ByteArray,
        inOff: Int,
        len: Int
    ) {
        underlyingDigest.update(`in`, inOff, len)
    }

    override fun doFinal(
        out: ByteArray,
        outOff: Int
    ): Int {
        underlyingDigest.doFinal(outputBuf, blockLength)

        if (opadState != null) {
            (underlyingDigest as Memoable).reset(opadState!!)
            underlyingDigest.update(outputBuf, blockLength, underlyingDigest.digestSize)
        } else {
            underlyingDigest.update(outputBuf, 0, outputBuf.size)
        }

        val len = underlyingDigest.doFinal(out, outOff)

        for (i in blockLength until outputBuf.size) {
            outputBuf[i] = 0
        }

        if (ipadState != null) {
            (underlyingDigest as Memoable).reset(ipadState!!)
        } else {
            underlyingDigest.update(inputPad, 0, inputPad.size)
        }

        return len
    }

    /**
     * Reset the mac generator.
     */
    override fun reset() {
        /*
         * reset the underlying digest.
         */
        underlyingDigest.reset()

        /*
         * reinitialize the digest.
         */
        underlyingDigest.update(inputPad, 0, inputPad.size)
    }

    companion object {
        private const val IPAD = 0x36.toByte()
        private const val OPAD = 0x5C.toByte()

        private var blockLengths: ConcurrentMutableMap<String, Int> = ConcurrentMutableMap()

        init {
            blockLengths.put("GOST3411", valueOf(32))

            blockLengths.put("MD2", valueOf(16))
            blockLengths.put("MD4", valueOf(64))
            blockLengths.put("MD5", valueOf(64))

            blockLengths.put("RIPEMD128", valueOf(64))
            blockLengths.put("RIPEMD160", valueOf(64))

            blockLengths.put("SHA-1", valueOf(64))
            blockLengths.put("SHA-224", valueOf(64))
            blockLengths.put("SHA-256", valueOf(64))
            blockLengths.put("SHA-384", valueOf(128))
            blockLengths.put("SHA-512", valueOf(128))

            blockLengths.put("Tiger", valueOf(64))
            blockLengths.put("Whirlpool", valueOf(64))
        }

        private fun getByteLength(
            digest: Digest
        ): Int {
            if (digest is ExtendedDigest) {
                return digest.byteLength
            }

            val b = blockLengths.get(digest.algorithmName) as? Int
                ?: throw IllegalArgumentException("unknown digest passed: " + digest.algorithmName)

            return b
        }

        private fun xorPad(pad: ByteArray, len: Int, n: Byte) {
            for (i in 0 until len) {
                pad[i] = (pad[i].toInt() xor n.toInt()).toByte()
            }
        }
    }
}
