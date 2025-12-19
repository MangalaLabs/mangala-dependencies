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

package org.spongycastle.asn1

interface BERTags {
    companion object {
        const val BOOLEAN = 0x01
        const val INTEGER = 0x02
        const val BIT_STRING = 0x03
        const val OCTET_STRING = 0x04
        const val NULL = 0x05
        const val OBJECT_IDENTIFIER = 0x06
        const val EXTERNAL = 0x08
        const val ENUMERATED = 0x0a // decimal 10
        const val SEQUENCE = 0x10 // decimal 16
        const val SEQUENCE_OF =
            0x10 // for completeness - used to model a SEQUENCE of the same type.
        const val SET = 0x11 // decimal 17
        const val SET_OF = 0x11 // for completeness - used to model a SET of the same type.
        const val NUMERIC_STRING = 0x12 // decimal 18
        const val PRINTABLE_STRING = 0x13 // decimal 19
        const val T61_STRING = 0x14 // decimal 20
        const val VIDEOTEX_STRING = 0x15 // decimal 21
        const val IA5_STRING = 0x16 // decimal 22
        const val UTC_TIME = 0x17 // decimal 23
        const val GENERALIZED_TIME = 0x18 // decimal 24
        const val GRAPHIC_STRING = 0x19 // decimal 25
        const val VISIBLE_STRING = 0x1a // decimal 26
        const val GENERAL_STRING = 0x1b // decimal 27
        const val UNIVERSAL_STRING = 0x1c // decimal 28
        const val BMP_STRING = 0x1e // decimal 30
        const val UTF8_STRING = 0x0c // decimal 12
        const val CONSTRUCTED = 0x20 // decimal 32
        const val APPLICATION = 0x40 // decimal 64
        const val TAGGED = 0x80 // decimal 128
    }
}
