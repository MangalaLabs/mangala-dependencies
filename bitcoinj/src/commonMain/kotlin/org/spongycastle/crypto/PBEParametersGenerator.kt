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

import org.spongycastle.util.Strings

abstract class PBEParametersGenerator protected constructor() {
    /**
     * return the password byte array.
     *
     * @return the password byte array.
     */
    lateinit var password: ByteArray
        protected set

    /**
     * return the salt byte array.
     *
     * @return the salt byte array.
     */
    lateinit var salt: ByteArray
        protected set

    /**
     * return the iteration count.
     *
     * @return the iteration count.
     */
    var iterationCount: Int = 0
        protected set

    /**
     * initialise the PBE generator.
     *
     * @param password the password converted into bytes (see below).
     * @param salt the salt to be mixed with the password.
     * @param iterationCount the number of iterations the "mixing" function
     * is to be applied for.
     */
    fun init(
        password: ByteArray,
        salt: ByteArray,
        iterationCount: Int
    ) {
        this.password = password
        this.salt = salt
        this.iterationCount = iterationCount
    }

    /**
     * generate derived parameters for a key of length keySize.
     *
     * @param keySize the length, in bits, of the key required.
     * @return a parameters object representing a key.
     */
    abstract fun generateDerivedParameters(keySize: Int): CipherParameters?

    /**
     * generate derived parameters for a key of length keySize, and
     * an initialisation vector (IV) of length ivSize.
     *
     * @param keySize the length, in bits, of the key required.
     * @param ivSize the length, in bits, of the iv required.
     * @return a parameters object representing a key and an IV.
     */
    abstract fun generateDerivedParameters(keySize: Int, ivSize: Int): CipherParameters?

    /**
     * generate derived parameters for a key of length keySize, specifically
     * for use with a MAC.
     *
     * @param keySize the length, in bits, of the key required.
     * @return a parameters object representing a key.
     */
    abstract fun generateDerivedMacParameters(keySize: Int): CipherParameters?

    companion object {
        /**
         * converts a password to a byte array according to the scheme in
         * PKCS5 (ascii, no padding)
         *
         * @param password a character array representing the password.
         * @return a byte array representing the password.
         */
        fun PKCS5PasswordToBytes(
            password: CharArray?
        ): ByteArray {
            if (password != null) {
                val bytes = ByteArray(password.size)

                for (i in bytes.indices) {
                    bytes[i] = password[i].code.toByte()
                }

                return bytes
            } else {
                return ByteArray(0)
            }
        }

        /**
         * converts a password to a byte array according to the scheme in
         * PKCS5 (UTF-8, no padding)
         *
         * @param password a character array representing the password.
         * @return a byte array representing the password.
         */
        fun PKCS5PasswordToUTF8Bytes(
            password: CharArray?
        ): ByteArray {
            return if (password != null) {
                Strings.toUTF8ByteArray(password)
            } else {
                ByteArray(0)
            }
        }

        /**
         * converts a password to a byte array according to the scheme in
         * PKCS12 (unicode, big endian, 2 zero pad bytes at the end).
         *
         * @param password a character array representing the password.
         * @return a byte array representing the password.
         */
        fun PKCS12PasswordToBytes(
            password: CharArray?
        ): ByteArray {
            if (password != null && password.size > 0) {
                // +1 for extra 2 pad bytes.
                val bytes = ByteArray((password.size + 1) * 2)

                for (i in password.indices) {
                    bytes[i * 2] = (password[i].code ushr 8).toByte()
                    bytes[i * 2 + 1] = password[i].code.toByte()
                }

                return bytes
            } else {
                return ByteArray(0)
            }
        }
    }
}
