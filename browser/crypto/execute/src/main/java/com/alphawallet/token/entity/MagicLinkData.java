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
import java.util.ArrayList;
import java.util.List;

public class MagicLinkData
{
    public long expiry;
    public byte[] prefix;
    public BigInteger nonce;
    public double price;
    public BigInteger priceWei;
    public List<BigInteger> tokenIds;
    public int[] indices;
    public BigInteger amount;
    public int ticketStart;
    public int ticketCount;
    public String contractAddress;
    public byte[] signature = new byte[65];
    public byte[] message;
    public String ownerAddress;
    public String contractName;
    public byte contractType;
    public long chainId;

    public List<BigInteger> balanceInfo = null;

    public boolean isValidOrder()
    {
        //check this order is not corrupt
        //first check the owner address - we should already have called getOwnerKey
        boolean isValid = true;

        if (this.ownerAddress == null || this.ownerAddress.length() < 20) isValid = false;
        if (this.contractAddress == null || this.contractAddress.length() < 20) isValid = false;
        if (this.message == null) isValid = false;

        return isValid;
    }

    public boolean balanceChange(List<BigInteger> balance)
    {
        //compare two balances
        //quick return, if sizes are different there's a change
        if (balanceInfo == null)
        {
            balanceInfo = new ArrayList<>(); //initialise the balance list
            return true;
        }
        if (balance.size() != balanceInfo.size()) return true;

        List<BigInteger> oldBalance = new ArrayList<>(balanceInfo);
        List<BigInteger> newBalance = new ArrayList<>(balance);

        oldBalance.removeAll(balanceInfo);
        newBalance.removeAll(balance);

        return (oldBalance.size() != 0 || newBalance.size() != 0);
    }
}
