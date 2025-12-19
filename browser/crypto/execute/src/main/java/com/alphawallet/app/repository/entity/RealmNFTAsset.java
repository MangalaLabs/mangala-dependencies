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

package com.alphawallet.app.repository.entity;

import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.repository.TokensRealmSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JB on 10/07/2021.
 */
public class RealmNFTAsset extends RealmObject
{
    @PrimaryKey
    private String tokenIdAddr; //format is addr-chainId-tokenId

    private String metaData; //store as a JSON blob
    private String balance;  //for ERC1155

    public String getTokenId()
    {
        String[] str = tokenIdAddr.split("-");
        return str[str.length - 1];
    }

    public void setMetaData(String metaData)
    {
        this.metaData = metaData;
    }

    public String getMetaData()
    {
        return metaData;
    }

    public static String databaseKey(Token token, BigInteger tokenId)
    {
        return TokensRealmSource.databaseKey(token) + "-" + tokenId.toString();
    }

    public void setBalance(BigDecimal balance)
    {
        this.balance = balance.toString();
    }

    public BigDecimal getBalance()
    {
        if (this.balance != null)
        {
            return new BigDecimal(balance);
        }
        else
        {
            return BigDecimal.ZERO;
        }
    }
}
