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

package org.spongycastle.crypto

/**
 * the interface stream ciphers conform to.
 */
interface StreamCipher {
    /**
     * Initialise the cipher.
     *
     * @param forEncryption if true the cipher is initialised for
     * encryption, if false for decryption.
     * @param params the key and other data required by the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    @Throws(IllegalArgumentException::class)
    fun init(forEncryption: Boolean, params: CipherParameters)

    /**
     * Return the name of the algorithm the cipher implements.
     *
     * @return the name of the algorithm the cipher implements.
     */
    val algorithmName: String?

    /**
     * encrypt/decrypt a single byte returning the result.
     *
     * @param in the byte to be processed.
     * @return the result of processing the input byte.
     */
    fun returnByte(`in`: Byte): Byte

    /**
     * process a block of bytes from in putting the result into out.
     *
     * @param in the input byte array.
     * @param inOff the offset into the in array where the data to be processed starts.
     * @param len the number of bytes to be processed.
     * @param out the output buffer the processed bytes go into.
     * @param outOff the offset into the output byte array the processed data starts at.
     * @return the number of bytes produced - should always be len.
     * @exception DataLengthException if the output buffer is too small.
     */
    @Throws(DataLengthException::class)
    fun processBytes(`in`: ByteArray, inOff: Int, len: Int, out: ByteArray, outOff: Int): Int

    /**
     * reset the cipher. This leaves it in the same state
     * it was at after the last init (if there was one).
     */
    fun reset()
}
