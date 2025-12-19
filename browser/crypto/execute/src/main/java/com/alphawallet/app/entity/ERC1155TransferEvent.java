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

package com.alphawallet.app.entity;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * Created by JB on 14/07/2021.
 */

public class ERC1155TransferEvent implements Comparable<ERC1155TransferEvent>
{
    public final BigInteger blockNumber;
    public final String to;
    public final String from;
    public final BigInteger tokenId;
    public final BigInteger value;
    public final boolean isReceive;

    public ERC1155TransferEvent(BigInteger blockNumber, String to, String from, BigInteger tokenId, BigInteger value, boolean isReceive)
    {
        this.blockNumber = blockNumber;
        this.to = to;
        this.from = from;
        this.tokenId = tokenId;
        this.value = isReceive ? value : value.negate();
        this.isReceive = isReceive;
    }

    @Override
    public int compareTo(@NotNull ERC1155TransferEvent other)
    {
        return blockNumber.compareTo(other.blockNumber);
    }
}
