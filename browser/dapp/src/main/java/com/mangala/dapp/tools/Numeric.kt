/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.mangala.dapp.tools

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Arrays

/**
 *
 * Message codec functions.
 *
 *
 * Implementation as per https://github.com/ethereum/wiki/wiki/JSON-RPC#hex-value-encoding
 */
object Numeric {
    private const val HEX_PREFIX = "0x"
    private fun isValidHexQuantity(value: String?): Boolean {
        if (value == null) {
            return false
        }
        return if (value.length < 3) {
            false
        } else value.startsWith(HEX_PREFIX)
    }

    @JvmStatic
    fun cleanHexPrefix(input: String): String {
        return if (containsHexPrefix(input)) {
            input.substring(2)
        } else {
            input
        }
    }

    fun prependHexPrefix(input: String): String {
        return if (!containsHexPrefix(input)) {
            HEX_PREFIX + input
        } else {
            input
        }
    }

    fun containsHexPrefix(input: String): Boolean {
        return input.length > 1 && input[0] == '0' && input[1] == 'x'
    }

    fun toBigInt(value: ByteArray?, offset: Int, length: Int): BigInteger {
        return toBigInt(Arrays.copyOfRange(value, offset, offset + length))
    }

    fun toBigInt(value: ByteArray?): BigInteger {
        return BigInteger(1, value)
    }

    fun toBigInt(hexValue: String): BigInteger {
        val cleanValue = cleanHexPrefix(hexValue)
        return toBigIntNoPrefix(cleanValue)
    }

    fun toBigIntNoPrefix(hexValue: String?): BigInteger {
        return BigInteger(hexValue, 16)
    }

    fun toHexStringWithPrefix(value: BigInteger): String {
        return HEX_PREFIX + value.toString(16)
    }

    fun toHexStringNoPrefix(value: BigInteger): String {
        return value.toString(16)
    }

    fun toHexStringNoPrefix(input: ByteArray): String {
        return toHexString(input, 0, input.size, false)
    }

    fun toHexStringWithPrefixZeroPadded(value: BigInteger, size: Int): String {
        return toHexStringZeroPadded(value, size, true)
    }

    fun toHexStringNoPrefixZeroPadded(value: BigInteger, size: Int): String {
        return toHexStringZeroPadded(value, size, false)
    }

    private fun toHexStringZeroPadded(value: BigInteger, size: Int, withPrefix: Boolean): String {
        var result = toHexStringNoPrefix(value)
        val length = result.length
        if (length > size) {
            throw UnsupportedOperationException(
                "Value " + result + "is larger then length " + size
            )
        } else if (value.signum() < 0) {
            throw UnsupportedOperationException("Value cannot be negative")
        }
        val sb: java.lang.StringBuilder = StringBuilder()
        for (i in 0 until size - length) sb.append("0")
        if (length < size) {
            result = sb.toString() + result
        }
        return if (withPrefix) {
            HEX_PREFIX + result
        } else {
            result
        }
    }

    fun toBytesPadded(value: BigInteger, length: Int): ByteArray {
        val result = ByteArray(length)
        val bytes = value.toByteArray()
        val bytesLength: Int
        val srcOffset: Int
        if (bytes[0].toInt() == 0) {
            bytesLength = bytes.size - 1
            srcOffset = 1
        } else {
            bytesLength = bytes.size
            srcOffset = 0
        }
        if (bytesLength > length) {
            throw RuntimeException("Input is too large to put in byte array of size $length")
        }
        val destOffset = length - bytesLength
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength)
        return result
    }

    @JvmStatic
    fun hexStringToByteArray(input: String): ByteArray {
        val cleanInput = cleanHexPrefix(input)
        val len = cleanInput.length
        if (len == 0) {
            return byteArrayOf()
        }
        val data: ByteArray
        val startIdx: Int
        if (len % 2 != 0) {
            data = ByteArray(len / 2 + 1)
            data[0] = (cleanInput[0].digitToIntOrNull(16) ?: -1).toByte()
            startIdx = 1
        } else {
            data = ByteArray(len / 2)
            startIdx = 0
        }
        var i = startIdx
        while (i < len) {
            data[(i + 1) / 2] = ((cleanInput[i].digitToIntOrNull(16) ?: -1 shl 4)
            + cleanInput[i + 1].digitToIntOrNull(16)!! ?: -1).toByte()
            i += 2
        }
        return data
    }

    @JvmStatic
    fun toHexString(
        input: ByteArray,
        offset: Int = 0,
        length: Int = input.size,
        withPrefix: Boolean = true
    ): String {
        val stringBuilder: java.lang.StringBuilder = StringBuilder()
        if (withPrefix) {
            stringBuilder.append("0x")
        }
        for (i in offset until offset + length) {
            stringBuilder.append(String.format("%02x", input[i].toInt() and 0xFF))
        }
        return stringBuilder.toString()
    }

    fun asByte(m: Int, n: Int): Byte {
        return (m shl 4 or n).toByte()
    }

    fun isIntegerValue(value: BigDecimal): Boolean {
        return value.signum() == 0 || value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0
    }
}
