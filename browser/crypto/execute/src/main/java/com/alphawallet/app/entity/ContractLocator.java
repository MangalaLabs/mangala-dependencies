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

import android.os.Parcel;
import android.os.Parcelable;

import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.entity.tokens.TokenCardMeta;
import com.alphawallet.app.repository.TokensRealmSource;
import com.alphawallet.token.entity.ContractInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 2/02/2019.
 * Stormbird in Singapore
 */
public class ContractLocator implements Parcelable
{
    public final String address;
    public final long chainId;
    private final ContractType type;

    public ContractLocator(String n, long chainId)
    {
        this.address = n;
        this.chainId = chainId;
        this.type = ContractType.NOT_SET;
    }

    public ContractLocator(String n, long chainId, ContractType t)
    {
        this.address = n;
        this.chainId = chainId;
        this.type = t;
    }

    protected ContractLocator(Parcel in)
    {
        this.address = in.readString();
        this.chainId = in.readLong();
        this.type = ContractType.values()[in.readInt()];
    }

    public boolean equals(Token token)
    {
        return (token != null && address != null && chainId == token.tokenInfo.chainId && address.equalsIgnoreCase(token.getAddress()) );
    }

    public boolean equals(TokenCardMeta token)
    {
        return TokensRealmSource.databaseKey(chainId, address).equalsIgnoreCase(token.tokenId);
    }

    /* replace this with a one-liner use of stream when we up our minSdkVersion to 24 */
    public static ContractLocator[] fromAddresses(String[] addresses, long chainID) {
        ContractLocator[] retval = new ContractLocator[addresses.length];
        for (int i=0; i<addresses.length; i++) {
            retval[i] = new ContractLocator(addresses[i], chainID);
        }
        return retval;
    }

    public static List<ContractLocator> fromContractInfo(ContractInfo cInfo)
    {
        // public Map<Integer, List<String>> addresses = new HashMap<>();
        List<ContractLocator> retVal = new ArrayList<>();
        for (long chainId : cInfo.addresses.keySet())
        {
            for (String addr : cInfo.addresses.get(chainId))
            {
                retVal.add(new ContractLocator(addr, chainId));
            }
        }

        return retVal;
    }

    public static final Creator<ContractLocator> CREATOR = new Creator<ContractLocator>() {
        @Override
        public ContractLocator createFromParcel(Parcel in) {
            return new ContractLocator(in);
        }

        @Override
        public ContractLocator[] newArray(int size) {
            return new ContractLocator[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(address);
        dest.writeLong(chainId);
        dest.writeInt(type.ordinal());
    }
}
