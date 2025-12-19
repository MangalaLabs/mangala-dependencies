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

package org.spongycastle.crypto//package org.spongycastle.crypto

/**
 * The base interface for implementations of message authentication codes (MACs).
 */
interface Mac {
    /**
     * Initialise the MAC.
     *
     * @param params the key and other data required by the MAC.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    @Throws(IllegalArgumentException::class)
    fun init(params: CipherParameters)

    /**
     * Return the name of the algorithm the MAC implements.
     *
     * @return the name of the algorithm the MAC implements.
     */
    fun getAlgorithmName(): String

    /**
     * Return the block size for this MAC (in bytes).
     *
     * @return the block size for this MAC in bytes.
     */
    fun getMacSize(): Int

    /**
     * add a single byte to the mac for processing.
     *
     * @param input the byte to be processed.
     * @exception IllegalStateException if the MAC is not initialised.
     */
    @Throws(IllegalStateException::class)
    fun update(input: Byte)

    /**
     * @param in the array containing the input.
     * @param inOff the index in the array the data begins at.
     * @param len the length of the input starting at inOff.
     * @exception IllegalStateException if the MAC is not initialised.
     * @exception DataLengthException if there isn't enough data in in.
     */
    @Throws(DataLengthException::class, IllegalStateException::class)
    fun update(input: ByteArray, inOff: Int, len: Int)

    /**
     * Compute the final stage of the MAC writing the output to the out
     * parameter.
     *
     *
     * doFinal leaves the MAC in the same state it was after the last init.
     *
     * @param out the array the MAC is to be output to.
     * @param outOff the offset into the out buffer the output is to start at.
     * @exception DataLengthException if there isn't enough space in out.
     * @exception IllegalStateException if the MAC is not initialised.
     */
    @Throws(DataLengthException::class, IllegalStateException::class)
    fun doFinal(out: ByteArray, outOff: Int): Int

    /**
     * Reset the MAC. At the end of resetting the MAC should be in the
     * in the same state it was after the last init (if there was one).
     */
    fun reset()
}
