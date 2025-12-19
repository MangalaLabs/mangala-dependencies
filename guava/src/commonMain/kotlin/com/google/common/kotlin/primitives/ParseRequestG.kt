/*
 * Copyright (C) 2008 The Guava Authors
 * Copyright (C) 2023-2025 Mangala Wallet
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
 * Modified from original source: https://github.com/google/guava
 */

package com.google.common.kotlin.primitives

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * A string to be parsed as a number and the radix to interpret it in.
 */
internal class ParseRequestG private constructor(@JvmField val rawValue: String, @JvmField val radix: Int) {
    companion object {
        @JvmStatic
        fun fromString(stringValue: String): ParseRequestG {
            if (stringValue.length == 0) {
                throw NumberFormatException("empty string")
            }

            // Handle radix specifier if present
            val rawValue: String
            val radix: Int
            val firstChar = stringValue[0]
            if (stringValue.startsWith("0x") || stringValue.startsWith("0X")) {
                rawValue = stringValue.substring(2)
                radix = 16
            } else if (firstChar == '#') {
                rawValue = stringValue.substring(1)
                radix = 16
            } else if (firstChar == '0' && stringValue.length > 1) {
                rawValue = stringValue.substring(1)
                radix = 8
            } else {
                rawValue = stringValue
                radix = 10
            }
            return ParseRequestG(rawValue, radix)
        }
    }
}
