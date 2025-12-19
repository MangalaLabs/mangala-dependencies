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

import org.spongycastle.crypto.CipherParameters
import org.spongycastle.crypto.Digest
import org.spongycastle.crypto.Mac
import org.spongycastle.crypto.PBEParametersGenerator
import org.spongycastle.crypto.macs.HMac
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.crypto.params.ParametersWithIV
import org.spongycastle.crypto.util.DigestFactory
import org.spongycastle.util.Arrays.copyOfRange
import kotlin.jvm.JvmOverloads

/**
 * Generator for PBE derived keys and ivs as defined by PKCS 5 V2.0 Scheme 2.
 * This generator uses a SHA-1 HMac as the calculation function.
 *
 *
 * The document this implementation is based on can be found at
 * [
 * RSA's PKCS5 Page](http://www.rsasecurity.com/rsalabs/pkcs/pkcs-5/index.html)
 */
class PKCS5S2ParametersGenerator
@JvmOverloads constructor(digest: Digest = DigestFactory.createSHA1()) : PBEParametersGenerator() {
    private val hMac: Mac
    private val state: ByteArray

    /**
     * construct a PKCS5 Scheme 2 Parameters generator.
     */
    init {
        hMac = HMac(digest)
        state = ByteArray(hMac.getMacSize())
    }

    private fun F(
        S: ByteArray?,
        c: Int,
        iBuf: ByteArray,
        out: ByteArray,
        outOff: Int
    ) {
        if (c == 0) {
            throw IllegalArgumentException("iteration count must be at least 1.")
        }

        if (S != null) {
            hMac.update(S, 0, S.size)
        }

        hMac.update(iBuf, 0, iBuf.size)
        hMac.doFinal(state, 0)

        state.copyInto(
            destination = out,
            destinationOffset = outOff,
            startIndex = 0,
            endIndex = state.size
        )

        for (count in 1 until c) {
            hMac.update(state, 0, state.size)
            hMac.doFinal(state, 0)

            for (j in state.indices) {
                out[outOff + j] = (out[outOff + j].toInt() xor state[j].toInt()).toByte()
            }
        }
    }

    private fun generateDerivedKey(
        dkLen: Int
    ): ByteArray {
        val hLen: Int = hMac.getMacSize()
        val l = (dkLen + hLen - 1) / hLen
        val iBuf = ByteArray(4)
        val outBytes = ByteArray(l * hLen)
        var outPos = 0

        val param: CipherParameters = KeyParameter(password)

        hMac.init(param)

        for (i in 1..l) {
            // Increment the value in 'iBuf'
            var pos = 3
            while ((++iBuf[pos]).toInt() == 0) {
                --pos
            }

            F(salt, iterationCount, iBuf, outBytes, outPos)
            outPos += hLen
        }

        return outBytes
    }

    /**
     * Generate a key parameter derived from the password, salt, and iteration
     * count we are currently initialised with.
     *
     * @param keySize the size of the key we want (in bits)
     * @return a KeyParameter object.
     */
    override fun generateDerivedParameters(
        keySize: Int
    ): CipherParameters? {
        var keySize = keySize
        keySize = keySize / 8

        val dKey = copyOfRange(generateDerivedKey(keySize), 0, keySize)

        return KeyParameter(dKey, 0, keySize)
    }

    /**
     * Generate a key with initialisation vector parameter derived from
     * the password, salt, and iteration count we are currently initialised
     * with.
     *
     * @param keySize the size of the key we want (in bits)
     * @param ivSize the size of the iv we want (in bits)
     * @return a ParametersWithIV object.
     */
    override fun generateDerivedParameters(
        keySize: Int,
        ivSize: Int
    ): CipherParameters? {
        var keySize = keySize
        var ivSize = ivSize
        keySize = keySize / 8
        ivSize = ivSize / 8

        val dKey = generateDerivedKey(keySize + ivSize)

        return ParametersWithIV(KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize)
    }

    /**
     * Generate a key parameter for use with a MAC derived from the password,
     * salt, and iteration count we are currently initialised with.
     *
     * @param keySize the size of the key we want (in bits)
     * @return a KeyParameter object.
     */
    override fun generateDerivedMacParameters(
        keySize: Int
    ): CipherParameters? {
        return generateDerivedParameters(keySize)
    }
}
