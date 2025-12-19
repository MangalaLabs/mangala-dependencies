/*
 * ORIGINAL COPYRIGHT:
 * Copyright (c) 2019-2023 AlphaWallet
 * Licensed under the MIT License (MIT).
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
 * ----------------------------------------------------------------
 * SOURCE:
 * Derived from: https://github.com/AlphaWallet/alpha-wallet-android
 *
 * ----------------------------------------------------------------
 * MODIFICATIONS:
 * Modified by Mangala Wallet for Kotlin Multiplatform compatibility.
 * ----------------------------------------------------------------
 */

package com.alphawallet.token.entity;

import java.math.BigInteger;

/**
 * Created by James on 26/03/2018.
 */

public class UnsignedLong extends BigInteger
{
    public UnsignedLong(byte[] byteValue)
    {
        super(1, byteValue);
    }

    static public UnsignedLong create(long value)
    {
        byte[] byteVal = new byte[4];

        for (int i = 0; i < 4; i++) {
            byteVal[i] = (byte) getByteVal(value, 3 - i);
        }
        return new UnsignedLong(byteVal);
    }

    static public UnsignedLong create(BigInteger value)
    {
        byte[] byteVal = new byte[4];

        for (int i = 0; i < 4; i++) {
            byteVal[i] = value.divide(BigInteger.valueOf( 1 << (3-i)*8 )).byteValue();//  (value, 3 - i);
        }
        return new UnsignedLong(byteVal);
    }

    static public byte[] createBytes(long value)
    {
        byte[] byteVal = new byte[4];

        for (int i = 0; i < 4; i++) {
            byteVal[i] = (byte) getByteVal(value, 3 - i);
        }

        return byteVal;
    }

    //select the value 0-255 from each byte of the long
    private static int getByteVal(long value, int p)
    {
        return ((int) ((byte) ((value >> (p*8)) & 0xFF) ));
    }
}
