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

/**
 * Created by James on 3/12/2018.
 * Stormbird in Singapore
 */

import java.util.UUID;

/**
 * Cut down version of transaction which is used to populate the Transaction view adapter data.
 * The actual transaction data is retrieved just-in-time from the database when the user looks at the transaction
 * This saves a lot of memory - especially for a contract with a huge amount of transactions.
 */

public class TransactionMeta extends ActivityMeta
{
    public final boolean isPending;
    public final String contractAddress;
    public final long chainId;

    public TransactionMeta(String hash, long timeStamp, String contractAddress, long chainId, String blockNumber)
    {
        super(timeStamp, hash);
        this.isPending = blockNumber.equals("0") || blockNumber.equals("-2");
        this.contractAddress = contractAddress;
        this.chainId = chainId;
    }

    public long getUID()
    {
        return UUID.nameUUIDFromBytes((this.hash + "t").getBytes()).getMostSignificantBits();
    }
}
