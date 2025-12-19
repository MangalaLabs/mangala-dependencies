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

import com.alphawallet.app.repository.TokenRepository;
import com.alphawallet.token.tools.Numeric;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * Created by JB on 21/10/2020.
 */

public class EtherscanEvent
{
    public String blockNumber;
    public long timeStamp;
    public String hash;
    public int nonce;
    String blockHash;
    public String from;
    public String contractAddress;
    public String to;
    public String tokenID;
    public String value;
    public String tokenName;
    public String tokenSymbol;
    public String tokenDecimal;
    public String input;
    String gas;
    String gasPrice;
    String gasUsed;

    public Transaction createTransaction(@NotNull NetworkInfo networkInfo)
    {
        BigInteger valueBI = BigInteger.ZERO;
        if (value != null && value.length() > 0 && Character.isDigit(value.charAt(0)))
        {
            valueBI = new BigInteger(value);
        }

        String input = Numeric.toHexString(TokenRepository.createTokenTransferData(to, valueBI)); //write the input to the transaction to ensure this is correctly handled elsewhere in the wallet

        return new Transaction(hash, "0", blockNumber, timeStamp, nonce, from, contractAddress, "0", gas, gasPrice, input,
                gasUsed, networkInfo.chainId, false);
    }

    public Transaction createNFTTransaction(@NotNull NetworkInfo networkInfo)
    {
        BigInteger tokenId = BigInteger.ONE;
        try
        {
            tokenId = new BigInteger(tokenID);
        }
        catch (Exception e)
        {
            //no action, default to '1'
        }

        String input = Numeric.toHexString(TokenRepository.createERC721TransferFunction(from, to, contractAddress, tokenId)); //write the input to the transaction to ensure this is correctly handled elsewhere in the wallet

        return new Transaction(hash, "0", blockNumber, timeStamp, nonce, from, contractAddress, "0", gas, gasPrice, input,
                gasUsed, networkInfo.chainId, false);
    }
}
