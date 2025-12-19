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
 * Created by James on 7/05/2019.
 * Stormbird in Sydney
 */
public class TransactionResult
{
    public final BigInteger tokenId;
    public final String method;
    public final String contractAddress;
    public final long contractChainId;
    public String result;
    public long resultTime;
    public final String attrId;

    public TransactionResult(long chainId, String address, BigInteger tokenId, Attribute attr)
    {
        this.contractAddress = address;
        this.contractChainId = chainId;
        this.tokenId = tokenId;
        if (attr.function != null) this.method = attr.function.method;
        else if (attr.event != null) this.method = attr.name; //for event store attribute name
        else this.method = attr.label;
        this.attrId = attr.name;
        result = null;
        resultTime = 0;
    }

    public boolean needsUpdating(long lastTxTime)
    {
        //if contract had new transactions then update, or if last tx was -1 (always check)
        return (resultTime == 0 || ((System.currentTimeMillis() + 5*10*1000) < resultTime) || lastTxTime < 0 || lastTxTime > resultTime);
    }
}
