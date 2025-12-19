/*
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
 * Copyright 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/bitcoinj/bitcoinj
 */

package org.bitcoinj.crypto

//import java.util.Arrays

/**
 *
 * An instance of EncryptedData is a holder for an initialization vector and encrypted bytes. It is typically
 * used to hold encrypted private key bytes.
 *
 *
 * The initialisation vector is random data that is used to initialise the AES block cipher when the
 * private key bytes were encrypted. You need these for decryption.
 */
class EncryptedData(initialisationVector: ByteArray, encryptedBytes: ByteArray) {
//    val initialisationVector: ByteArray
//    val encryptedBytes: ByteArray
//
//    init {
//        this.initialisationVector = Arrays.copyOf(initialisationVector, initialisationVector.size)
//        this.encryptedBytes = Arrays.copyOf(encryptedBytes, encryptedBytes.size)
//    }

    val initialisationVector: ByteArray = initialisationVector.copyOf()
    val encryptedBytes: ByteArray = encryptedBytes.copyOf()

//    override fun equals(o: Any?): Boolean {
//        if (this === o) return true
//        if (o == null || javaClass != o.javaClass) return false
//        val other = o as EncryptedData
//        return Arrays.equals(encryptedBytes, other.encryptedBytes) && Arrays.equals(
//            initialisationVector,
//            other.initialisationVector
//        )
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncryptedData) return false

        return encryptedBytes.contentEquals(other.encryptedBytes) &&
                initialisationVector.contentEquals(other.initialisationVector)
    }


//    override fun hashCode(): Int {
//        return ObjectsG.hashCode(
//            Arrays.hashCode(encryptedBytes),
//            Arrays.hashCode(initialisationVector)
//        )
//    }

    override fun hashCode(): Int {
        var result = encryptedBytes.contentHashCode()
        result = 31 * result + initialisationVector.contentHashCode()
        return result
    }


//    override fun toString(): String {
//        return ("EncryptedData [initialisationVector=" + Arrays.toString(initialisationVector)
//                + ", encryptedPrivateKey=" + Arrays.toString(encryptedBytes) + "]")
//    }

    override fun toString(): String {
        return "EncryptedData(initialisationVector=${initialisationVector.contentToString()}, encryptedBytes=${encryptedBytes.contentToString()})"
    }

}
