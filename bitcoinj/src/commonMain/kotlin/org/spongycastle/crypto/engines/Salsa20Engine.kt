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

package org.spongycastle.crypto.engines

import org.spongycastle.crypto.CipherParameters
import org.spongycastle.crypto.DataLengthException
import org.spongycastle.crypto.MaxBytesExceededException
import org.spongycastle.crypto.OutputLengthException
import org.spongycastle.crypto.SkippingStreamCipher
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.crypto.params.ParametersWithIV
import org.spongycastle.util.Pack.intToLittleEndian
import org.spongycastle.util.Pack.littleEndianToInt
import org.spongycastle.util.Strings.toByteArray
import kotlin.jvm.JvmOverloads

/**
 * Implementation of Daniel J. Bernstein's Salsa20 stream cipher, Snuffle 2005
 */
class Salsa20Engine @JvmOverloads constructor(rounds: Int = DEFAULT_ROUNDS) : SkippingStreamCipher {
    protected fun packTauOrSigma(keyLength: Int, state: IntArray, stateOffset: Int) {
        val tsOff = (keyLength - 16) / 4
        state[stateOffset] = TAU_SIGMA[tsOff]
        state[stateOffset + 1] = TAU_SIGMA[tsOff + 1]
        state[stateOffset + 2] = TAU_SIGMA[tsOff + 2]
        state[stateOffset + 3] = TAU_SIGMA[tsOff + 3]
    }

    protected var rounds: Int

    /*
     * variables to hold the state of the engine
     * during encryption and decryption
     */
    private var index = 0
    protected var engineState: IntArray = IntArray(STATE_SIZE) // state
    protected var x: IntArray = IntArray(STATE_SIZE) // internal buffer
    private val keyStream = ByteArray(STATE_SIZE * 4) // expanded state, 64 bytes
    private var initialised = false

    /*
     * internal counter
     */
    private var cW0 = 0
    private var cW1 = 0
    private var cW2 = 0

    /**
     * Creates a Salsa20 engine with a specific number of rounds.
     * @param rounds the number of rounds (must be an even number).
     */
    /**
     * Creates a 20 round Salsa20 engine.
     */
    init {
        if (rounds <= 0 || (rounds and 1) != 0) {
            throw IllegalArgumentException("'rounds' must be a positive, even number")
        }

        this.rounds = rounds
    }

    /**
     * initialise a Salsa20 cipher.
     *
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    override fun init(
        forEncryption: Boolean,
        params: CipherParameters
    ) {
        /*
        * Salsa20 encryption and decryption is completely
        * symmetrical, so the 'forEncryption' is
        * irrelevant. (Like 90% of stream ciphers)
        */

        if (params !is ParametersWithIV) {
            throw IllegalArgumentException(algorithmName + " Init parameters must include an IV")
        }

        val ivParams: ParametersWithIV = params as ParametersWithIV

        val iv: ByteArray = ivParams.iV
        if (iv == null || iv.size != nonceSize) {
            throw IllegalArgumentException(
                algorithmName + " requires exactly " + nonceSize
                        + " bytes of IV"
            )
        }

        val keyParam: CipherParameters = ivParams.parameters
        if (keyParam == null) {
            if (!initialised) {
                throw IllegalStateException(algorithmName + " KeyParameter can not be null for first initialisation")
            }

            setKey(null, iv)
        } else if (keyParam is KeyParameter) {
            setKey(keyParam.key, iv)
        } else {
            throw IllegalArgumentException(algorithmName + " Init parameters must contain a KeyParameter (or null for re-init)")
        }

        reset()

        initialised = true
    }

    protected val nonceSize: Int
        get() = 8

    override val algorithmName: String
        get() {
            var name = "Salsa20"
            if (rounds != DEFAULT_ROUNDS) {
                name += "/$rounds"
            }
            return name
        }

    override fun returnByte(`in`: Byte): Byte {
        if (limitExceeded()) {
            throw MaxBytesExceededException("2^70 byte limit per IV; Change IV")
        }

        val out = (keyStream[index].toInt() xor `in`.toInt()).toByte()
        index = (index + 1) and 63

        if (index == 0) {
            advanceCounter()
            generateKeyStream(keyStream)
        }

        return out
    }

    protected fun advanceCounter(diff: Long) {
        val hi = (diff ushr 32).toInt()
        val lo = diff.toInt()

        if (hi > 0) {
            engineState[9] += hi
        }

        val oldState = engineState[8]

        engineState[8] += lo

        if (oldState != 0 && engineState[8] < oldState) {
            engineState[9]++
        }
    }

    protected fun advanceCounter() {
        if (++engineState[8] == 0) {
            ++engineState[9]
        }
    }

    protected fun retreatCounter(diff: Long) {
        val hi = (diff ushr 32).toInt()
        val lo = diff.toInt()

        if (hi != 0) {
            if ((engineState[9].toLong() and 0xffffffffL) >= (hi.toLong() and 0xffffffffL)) {
                engineState[9] -= hi
            } else {
                throw IllegalStateException("attempt to reduce counter past zero.")
            }
        }

        if ((engineState[8].toLong() and 0xffffffffL) >= (lo.toLong() and 0xffffffffL)) {
            engineState[8] -= lo
        } else {
            if (engineState[9] != 0) {
                --engineState[9]
                engineState[8] -= lo
            } else {
                throw IllegalStateException("attempt to reduce counter past zero.")
            }
        }
    }

    protected fun retreatCounter() {
        if (engineState[8] == 0 && engineState[9] == 0) {
            throw IllegalStateException("attempt to reduce counter past zero.")
        }

        if (--engineState[8] == -1) {
            --engineState[9]
        }
    }

    override fun processBytes(
        `in`: ByteArray,
        inOff: Int,
        len: Int,
        out: ByteArray,
        outOff: Int
    ): Int {
        if (!initialised) {
            throw IllegalStateException(algorithmName + " not initialised")
        }

        if ((inOff + len) > `in`.size) {
            throw DataLengthException("input buffer too short")
        }

        if ((outOff + len) > out.size) {
            throw OutputLengthException("output buffer too short")
        }

        if (limitExceeded(len)) {
            throw MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV")
        }

        for (i in 0 until len) {
            out[i + outOff] = (keyStream[index].toInt() xor `in`[i + inOff].toInt()).toByte()
            index = (index + 1) and 63

            if (index == 0) {
                advanceCounter()
                generateKeyStream(keyStream)
            }
        }

        return len
    }

    override fun skip(numberOfBytes: Long): Long {
        if (numberOfBytes >= 0) {
            var remaining = numberOfBytes

            if (remaining >= 64) {
                val count = remaining / 64

                advanceCounter(count)

                remaining -= count * 64
            }

            val oldIndex = index

            index = (index + remaining.toInt()) and 63

            if (index < oldIndex) {
                advanceCounter()
            }
        } else {
            var remaining = -numberOfBytes

            if (remaining >= 64) {
                val count = remaining / 64

                retreatCounter(count)

                remaining -= count * 64
            }

            for (i in 0 until remaining) {
                if (index == 0) {
                    retreatCounter()
                }

                index = (index - 1) and 63
            }
        }

        generateKeyStream(keyStream)

        return numberOfBytes
    }

    override fun seekTo(position: Long): Long {
        reset()

        return skip(position)
    }

    override val position: Long
        get() = counter * 64 + index

    override fun reset() {
        index = 0
        resetLimitCounter()
        resetCounter()

        generateKeyStream(keyStream)
    }

    protected val counter: Long
        get() = (engineState.get(9).toLong() shl 32) or (engineState.get(8)
            .toLong() and 0xffffffffL)

    protected fun resetCounter() {
        engineState[9] = 0
        engineState[8] = engineState[9]
    }

    protected fun setKey(keyBytes: ByteArray?, ivBytes: ByteArray?) {
        if (keyBytes != null) {
            if ((keyBytes.size != 16) && (keyBytes.size != 32)) {
                throw IllegalArgumentException(algorithmName + " requires 128 bit or 256 bit key")
            }

            val tsOff = (keyBytes.size - 16) / 4
            engineState[0] = TAU_SIGMA[tsOff]
            engineState[5] = TAU_SIGMA[tsOff + 1]
            engineState[10] = TAU_SIGMA[tsOff + 2]
            engineState[15] = TAU_SIGMA[tsOff + 3]

            // Key
            littleEndianToInt(keyBytes, 0, engineState, 1, 4)
            littleEndianToInt(keyBytes, keyBytes.size - 16, engineState, 11, 4)
        }

        // IV
        littleEndianToInt((ivBytes)!!, 0, engineState, 6, 2)
    }

    protected fun generateKeyStream(output: ByteArray?) {
        salsaCore(rounds, engineState, x)
        intToLittleEndian(x, (output)!!, 0)
    }

    private fun resetLimitCounter() {
        cW0 = 0
        cW1 = 0
        cW2 = 0
    }

    private fun limitExceeded(): Boolean {
        if (++cW0 == 0) {
            if (++cW1 == 0) {
                return (++cW2 and 0x20) != 0 // 2^(32 + 32 + 6)
            }
        }

        return false
    }

    /*
     * this relies on the fact len will always be positive.
     */
    private fun limitExceeded(len: Int): Boolean {
        cW0 += len
        if (cW0 < len && cW0 >= 0) {
            if (++cW1 == 0) {
                return (++cW2 and 0x20) != 0 // 2^(32 + 32 + 6)
            }
        }

        return false
    }

    companion object {
        val DEFAULT_ROUNDS: Int = 20

        /** Constants  */
        private val STATE_SIZE = 16 // 16, 32 bit ints = 64 bytes

        private val TAU_SIGMA =
            littleEndianToInt(toByteArray("expand 16-byte k" + "expand 32-byte k"), 0, 8)


        @Deprecated("")
        protected val sigma: ByteArray = toByteArray("expand 32-byte k")
        protected val tau: ByteArray = toByteArray("expand 16-byte k")

        /**
         * Salsa20 function
         *
         * @param   input   input data
         */
        fun salsaCore(rounds: Int, input: IntArray, x: IntArray) {
            if (input.size != 16) {
                throw IllegalArgumentException()
            }
            if (x.size != 16) {
                throw IllegalArgumentException()
            }
            if (rounds % 2 != 0) {
                throw IllegalArgumentException("Number of rounds must be even")
            }

            var x00 = input[0]
            var x01 = input[1]
            var x02 = input[2]
            var x03 = input[3]
            var x04 = input[4]
            var x05 = input[5]
            var x06 = input[6]
            var x07 = input[7]
            var x08 = input[8]
            var x09 = input[9]
            var x10 = input[10]
            var x11 = input[11]
            var x12 = input[12]
            var x13 = input[13]
            var x14 = input[14]
            var x15 = input[15]

            var i = rounds
            while (i > 0) {
                x04 = x04 xor rotl(x00 + x12, 7)
                x08 = x08 xor rotl(x04 + x00, 9)
                x12 = x12 xor rotl(x08 + x04, 13)
                x00 = x00 xor rotl(x12 + x08, 18)
                x09 = x09 xor rotl(x05 + x01, 7)
                x13 = x13 xor rotl(x09 + x05, 9)
                x01 = x01 xor rotl(x13 + x09, 13)
                x05 = x05 xor rotl(x01 + x13, 18)
                x14 = x14 xor rotl(x10 + x06, 7)
                x02 = x02 xor rotl(x14 + x10, 9)
                x06 = x06 xor rotl(x02 + x14, 13)
                x10 = x10 xor rotl(x06 + x02, 18)
                x03 = x03 xor rotl(x15 + x11, 7)
                x07 = x07 xor rotl(x03 + x15, 9)
                x11 = x11 xor rotl(x07 + x03, 13)
                x15 = x15 xor rotl(x11 + x07, 18)

                x01 = x01 xor rotl(x00 + x03, 7)
                x02 = x02 xor rotl(x01 + x00, 9)
                x03 = x03 xor rotl(x02 + x01, 13)
                x00 = x00 xor rotl(x03 + x02, 18)
                x06 = x06 xor rotl(x05 + x04, 7)
                x07 = x07 xor rotl(x06 + x05, 9)
                x04 = x04 xor rotl(x07 + x06, 13)
                x05 = x05 xor rotl(x04 + x07, 18)
                x11 = x11 xor rotl(x10 + x09, 7)
                x08 = x08 xor rotl(x11 + x10, 9)
                x09 = x09 xor rotl(x08 + x11, 13)
                x10 = x10 xor rotl(x09 + x08, 18)
                x12 = x12 xor rotl(x15 + x14, 7)
                x13 = x13 xor rotl(x12 + x15, 9)
                x14 = x14 xor rotl(x13 + x12, 13)
                x15 = x15 xor rotl(x14 + x13, 18)
                i -= 2
            }

            x[0] = x00 + input[0]
            x[1] = x01 + input[1]
            x[2] = x02 + input[2]
            x[3] = x03 + input[3]
            x[4] = x04 + input[4]
            x[5] = x05 + input[5]
            x[6] = x06 + input[6]
            x[7] = x07 + input[7]
            x[8] = x08 + input[8]
            x[9] = x09 + input[9]
            x[10] = x10 + input[10]
            x[11] = x11 + input[11]
            x[12] = x12 + input[12]
            x[13] = x13 + input[13]
            x[14] = x14 + input[14]
            x[15] = x15 + input[15]
        }

        /**
         * Rotate left
         *
         * @param   x   value to rotate
         * @param   y   amount to rotate x
         *
         * @return  rotated x
         */
        protected fun rotl(x: Int, y: Int): Int {
            return (x shl y) or (x ushr -y)
        }
    }
}
