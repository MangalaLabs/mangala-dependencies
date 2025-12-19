/*
 * Copyright (c) 2019-2023 AlphaWallet
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
 * Modified from original source: https://github.com/AlphaWallet/alpha-wallet-android
 */

package com.alphawallet.app.walletconnect.util

import com.alphawallet.app.walletconnect.entity.InvalidHmacException
import com.alphawallet.app.walletconnect.entity.WCEncryptionPayload
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object WCCipher {
    private const val CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding"
    private const val MAC_ALGORITHM = "HmacSHA256"

    fun encrypt(data: ByteArray, key: ByteArray): WCEncryptionPayload {
        val iv = randomBytes(16)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encryptedData = cipher.doFinal(data)
        val hmac = computeHmac(
                data = encryptedData,
                iv = iv,
                key = key
        )

        return WCEncryptionPayload(
                data = encryptedData.toHexString(),
                iv = iv.toHexString(),
                hmac = hmac
        )
    }

    fun decrypt(payload: WCEncryptionPayload, key: ByteArray): ByteArray {
        val data = payload.data.toByteArray()
        val iv = payload.iv.toByteArray()

        val computedHmac = computeHmac(
                data = data,
                iv = iv,
                key = key
        )

        if (computedHmac != payload.hmac.lowercase()) {
            throw InvalidHmacException()
        }

        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        return cipher.doFinal(data)
    }

    private fun computeHmac(data: ByteArray, iv: ByteArray, key: ByteArray): String {
        val mac = Mac.getInstance(MAC_ALGORITHM)
        val payload = data + iv
        mac.init(SecretKeySpec(key, MAC_ALGORITHM))
        return mac.doFinal(payload).toHexString()
    }

    private fun randomBytes(size: Int): ByteArray {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)

        return bytes
    }
}
