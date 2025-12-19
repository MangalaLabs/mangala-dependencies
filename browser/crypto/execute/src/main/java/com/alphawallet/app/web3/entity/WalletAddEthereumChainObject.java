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

package com.alphawallet.app.web3.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alphawallet.token.tools.Numeric;

import java.math.BigInteger;

import timber.log.Timber;

/**
 * Created by JB on 28/07/21
 */

public class WalletAddEthereumChainObject implements Parcelable
{
    public NativeCurrency nativeCurrency;
    public String[] blockExplorerUrls;
    public String chainName;
    public String chainType; //ignore this
    public String chainId; //this is a hex number with "0x" prefix. If it is without "0x", process it as dec
    public String[] rpcUrls;

    public WalletAddEthereumChainObject()
    {
    }

    public long getChainId()
    {
        try
        {
            if (Numeric.containsHexPrefix(chainId))
            {
                return Numeric.toBigInt(chainId).longValue();
            }
            else
            {
                return new BigInteger(chainId).longValue();
            }
        }
        catch (NumberFormatException e)
        {
            Timber.e(e);
            return (0);
        }
    }

    protected WalletAddEthereumChainObject(Parcel in)
    {
        nativeCurrency = NativeCurrency.CREATOR.createFromParcel(in);
        blockExplorerUrls = in.readInt() == 1 ? in.createStringArray() : null;
        chainName = in.readString();
        chainId = in.readString();
        rpcUrls = in.readInt() == 1 ? in.createStringArray() : null;
    }

    public static final Creator<WalletAddEthereumChainObject> CREATOR = new Creator<WalletAddEthereumChainObject>()
    {
        @Override
        public WalletAddEthereumChainObject createFromParcel(Parcel in)
        {
            return new WalletAddEthereumChainObject(in);
        }

        @Override
        public WalletAddEthereumChainObject[] newArray(int size)
        {
            return new WalletAddEthereumChainObject[size];
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
        nativeCurrency.writeToParcel(dest, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeInt(blockExplorerUrls == null ? 0 : 1);
        if (blockExplorerUrls != null)
        {
            dest.writeStringArray(blockExplorerUrls);
        }
        dest.writeString(chainName);
        dest.writeString(chainId);
        dest.writeInt(rpcUrls == null ? 0 : 1);
        if (rpcUrls != null)
        {
            dest.writeStringArray(rpcUrls);
        }
    }
}
