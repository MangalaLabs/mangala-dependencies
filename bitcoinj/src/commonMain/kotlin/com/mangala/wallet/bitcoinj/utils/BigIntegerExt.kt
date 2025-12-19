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

package com.mangala.wallet.bitcoinj.utils

import com.ionspin.kotlin.bignum.integer.BigInteger

internal fun BigInteger.shiftRight(n: Int): BigInteger {
    // BigInteger representation of 2
    val two = BigInteger.fromInt(2)

    // Calculate 2^n
    val divisor = two.pow(n)

    // Divide this BigInteger by 2^n to achieve right shift
    return this.divide(divisor)
}

internal fun BigInteger.shiftLeft(n: Int): BigInteger {
    // BigInteger representation of 2
    val two = BigInteger.fromInt(2)

    // Calculate 2^n
    val multiplier = two.pow(n)

    // Multiply this BigInteger by 2^n to achieve left shift
    return this.multiply(multiplier)
}

internal fun BigInteger.toInt(): Int {
    val bytes = this.toByteArray()
    if (bytes.isEmpty()) return 0 // Equivalent to check if BigInteger is zero

    // Ensure there are at least 4 bytes to avoid IndexOutOfBoundsException
    val safeBytes = bytes.copyOfRange(maxOf(bytes.size - 4, 0), bytes.size)
    var result = 0

    // Convert the last 4 bytes (or fewer if the number is smaller) to an Int
    for (byte in safeBytes) {
        result = (result shl 8) or (byte.toInt() and 0xFF)
    }

    return result
}

fun BigInteger.testBit(n: Int): Boolean {
    val two = BigInteger.fromInt(2)
    // Calculate 2^n
    val powerOfTwo = two.pow(n)
    // Calculate 2^(n+1)
    val powerOfTwoPlusOne = two.pow(n + 1)

    // Calculate this mod 2^(n+1)
    val modResult = this.mod(powerOfTwoPlusOne)

    // If modResult >= 2^n, the bit is set
    return modResult >= powerOfTwo
}

fun BigInteger.getLowestSetBit(): Int {
    if (this == BigInteger.ZERO) return -1

    // Convert to binary string representation
    val binaryString = this.toString(2)

    // Find the index of the rightmost '1'
    val lowestSetBitIndex = binaryString.lastIndexOf('1')

    // Return the number of bits to the right of the rightmost '1'
    return if (lowestSetBitIndex != -1) binaryString.length - 1 - lowestSetBitIndex else -1
}


internal fun BigInteger.modPow(exponent: BigInteger, modulus: BigInteger): BigInteger {
    // Basic validation
    require(modulus > BigInteger.ZERO) { "Modulus must be positive." }
    require(exponent >= BigInteger.ZERO) { "Exponent must be non-negative." }

    // Edge cases
    if (modulus == BigInteger.ONE) return BigInteger.ZERO
    if (exponent == BigInteger.ZERO) return BigInteger.ONE

    var result = BigInteger.ONE
    var base = this % modulus
    var exp = exponent

    while (exp > BigInteger.ZERO) {
        if (exp % BigInteger.TWO != BigInteger.ZERO) {
            result = (result * base) % modulus
        }
        exp /= BigInteger.TWO
        base = (base * base) % modulus
    }

    return result
}

fun BigInteger.bitCount(): Int {
    var count = 0
    // Convert BigInteger to binary string representation.
    val binaryString = this.toString(2)
    // Count the number of '1's in the string.
    binaryString.forEach { char ->
        if (char == '1') count++
    }
    return count
}

fun BigInteger.clearBit(n: Int): BigInteger {
    if (n < 0) throw ArithmeticException("Negative bit address")

    // Create a bitmask with only the nth bit set.
    val mask = BigInteger.ONE.shiftLeft(n)

    // Invert the mask to have all bits set except the nth bit.
    val invertedMask = mask.not()

    // Perform a bitwise AND with the inverted mask to clear the nth bit.
    return this.and(invertedMask)
}
