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

package org.bitcoinj.core

import com.google.common.kotlin.base.PreconditionsG
import com.google.common.kotlin.primitives.IntsG
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.soywiz.krypto.SHA256
import com.soywiz.krypto.sha256
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly, allowing it to be used as keys in a
 * map. It also checks that the length is correct and provides a bit more type safety.
 */
class Sha256Hash @Deprecated("") constructor(rawHashBytes: ByteArray) : Comparable<Sha256Hash> {
    /**
     * Returns the internal byte array, without defensively copying. Therefore do NOT modify the returned array.
     */
    val bytes: ByteArray

    /**
     * Use [.wrap] instead.
     */
    init {
        PreconditionsG.checkArgument(rawHashBytes.size == LENGTH)
        bytes = rawHashBytes
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        return if (o == null || this::class != o::class) false else {
            val oBytes = (o as Sha256Hash).bytes
            bytes.equals(oBytes)
        }
    }

    /**
     * Returns the last four bytes of the wrapped hash. This should be unique enough to be a suitable hash code even for
     * blocks, where the goal is to try and get the first bytes to be zeros (i.e. the value as a big integer lower
     * than the target value).
     */
    override fun hashCode(): Int {
        // Use the last 4 bytes, not the first 4 which are often zeros in Bitcoin.
        return IntsG.fromBytes(
            bytes[LENGTH - 4],
            bytes[LENGTH - 3],
            bytes[LENGTH - 2],
            bytes[LENGTH - 1]
        )
    }

    //    public String toString() {
    //        return Utils.HEX.encode(bytes);
    //    }
    override fun toString(): String {
        return ""
    }

    /**
     * Returns the bytes interpreted as a positive integer.
     */
    fun toBigInteger(): BigInteger {
        return BigInteger.fromByteArray(bytes, Sign.POSITIVE)
    }

    val reversedBytes: ByteArray
        /**
         * Returns a reversed copy of the internal byte array.
         */
        get() = Utils.reverseBytes(bytes)

    override fun compareTo(other: Sha256Hash): Int {
        for (i in LENGTH - 1 downTo 0) {
            val thisByte = bytes[i].toInt() and 0xff
            val otherByte = other.bytes[i].toInt() and 0xff
            if (thisByte > otherByte) return 1
            if (thisByte < otherByte) return -1
        }
        return 0
    }

    companion object {
        const val LENGTH = 32 // bytes
        val ZERO_HASH = wrap(ByteArray(LENGTH))
        /**
         * Use [.wrap] instead.
         */
        //    @Deprecated
        //    public Sha256Hash(String hexString) {
        //        checkArgument(hexString.length() == LENGTH * 2);
        //        this.bytes = Utils.HEX.decode(hexString);
        //    }
        /**
         * Creates a new instance that wraps the given hash value.
         *
         * @param rawHashBytes the raw hash bytes to wrap
         * @return a new instance
         * @throws IllegalArgumentException if the given array length is not exactly 32
         */
        @Suppress("deprecation") // the constructor will be made private in the future
        fun wrap(rawHashBytes: ByteArray): Sha256Hash {
            return Sha256Hash(rawHashBytes)
        }
        /**
         * Creates a new instance that wraps the given hash value (represented as a hex string).
         *
         * @param hexString a hash value represented as a hex string
         * @return a new instance
         * @throws IllegalArgumentException if the given string is not a valid
         * hex string, or if it does not represent exactly 32 bytes
         */
        //    public static Sha256Hash wrap(String hexString) {
        //        return wrap(Utils.HEX.decode(hexString));
        //    }
        /**
         * Creates a new instance that wraps the given hash value, but with byte order reversed.
         *
         * @param rawHashBytes the raw hash bytes to wrap
         * @return a new instance
         * @throws IllegalArgumentException if the given array length is not exactly 32
         */
        @Suppress("deprecation") // the constructor will be made private in the future
        fun wrapReversed(rawHashBytes: ByteArray): Sha256Hash {
            return wrap(Utils.reverseBytes(rawHashBytes))
        }

        /** Use [.of] instead: this old name is ambiguous.  */
        @Deprecated("")
        fun create(contents: ByteArray): Sha256Hash {
            return of(contents)
        }

        /**
         * Creates a new instance containing the calculated (one-time) hash of the given bytes.
         *
         * @param contents the bytes on which the hash value is calculated
         * @return a new instance containing the calculated (one-time) hash
         */
        fun of(contents: ByteArray): Sha256Hash {
            return wrap(hash(contents))
        }

        /** Use [.twiceOf] instead: this old name is ambiguous.  */
        @Deprecated("")
        fun createDouble(contents: ByteArray): Sha256Hash {
            return twiceOf(contents)
        }

        /**
         * Creates a new instance containing the hash of the calculated hash of the given bytes.
         *
         * @param contents the bytes on which the hash value is calculated
         * @return a new instance containing the calculated (two-time) hash
         */
        fun twiceOf(contents: ByteArray): Sha256Hash {
            return wrap(hashTwice(contents))
        }
        /**
         * Creates a new instance containing the calculated (one-time) hash of the given file's contents.
         *
         * The file contents are read fully into memory, so this method should only be used with small files.
         *
         * @param file the file on which the hash value is calculated
         * @return a new instance containing the calculated (one-time) hash
         * @throws IOException if an error occurs while reading the file
         */
        //    public static Sha256Hash of(File file) throws IOException {
        //        FileInputStream in = new FileInputStream(file);
        //        try {
        //            return of(ByteStreams.toByteArray(in));
        //        } finally {
        //            in.close();
        //        }
        //    }
        /**
         * Calculates the SHA-256 hash of the given byte range.
         *
         * @param input the array containing the bytes to hash
         * @param offset the offset within the array of the bytes to hash
         * @param length the number of bytes to hash
         * @return the hash (in big-endian order)
         */
        /**
         * Calculates the SHA-256 hash of the given bytes.
         *
         * @param input the bytes to hash
         * @return the hash (in big-endian order)
         */
        @JvmOverloads
        fun hash(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
            val hasher = SHA256()
            hasher.update(input, offset, length)
            return hasher.digest().bytes
        }
        /**
         * Calculates the SHA-256 hash of the given byte range,
         * and then hashes the resulting hash again.
         *
         * @param input the array containing the bytes to hash
         * @param offset the offset within the array of the bytes to hash
         * @param length the number of bytes to hash
         * @return the double-hash (in big-endian order)
         */
        /**
         * Calculates the SHA-256 hash of the given bytes,
         * and then hashes the resulting hash again.
         *
         * @param input the bytes to hash
         * @return the double-hash (in big-endian order)
         */
        @JvmStatic
        @JvmOverloads
        fun hashTwice(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
            val hasher = SHA256()
            hasher.update(input, offset, length)
            val firstHash = hasher.digest().bytes
            return firstHash.sha256().bytes
        }

        /**
         * Calculates the hash of hash on the given byte ranges. This is equivalent to
         * concatenating the two ranges and then passing the result to [.hashTwice].
         */
        fun hashTwice(
            input1: ByteArray, offset1: Int, length1: Int,
            input2: ByteArray, offset2: Int, length2: Int
        ): ByteArray {
            val hasher = SHA256()
            hasher.update(input1, offset1, length1)
            hasher.update(input2, offset2, length2)
            val firstHash = hasher.digest().bytes
            return firstHash.sha256().bytes
        }
    }
}
