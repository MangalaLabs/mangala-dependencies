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

package com.alphawallet.app.ui.widget.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PriceAlert implements Parcelable {
    private String value;
    private String currency;
    private String token;
    private String address;
    private long chainId;
    private boolean isAbove;
    private boolean enabled;

    public PriceAlert(String currency, String token, String address, long chainId)
    {
        this.currency = currency;
        this.token = token;
        this.isAbove = true;
        this.enabled = true;
        this.address = address;
        this.chainId = chainId;
    }

    protected PriceAlert(Parcel in)
    {
        value = in.readString();
        currency = in.readString();
        token = in.readString();
        isAbove = in.readByte() != 0;
        enabled = in.readByte() != 0;
        address = in.readString();
        chainId = in.readLong();
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public boolean getAbove()
    {
        return isAbove;
    }

    public void setAbove(boolean above)
    {
        this.isAbove = above;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(value);
        dest.writeString(currency);
        dest.writeString(token);
        dest.writeByte((byte) (isAbove ? 1 : 0));
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(address);
        dest.writeLong(chainId);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<PriceAlert> CREATOR = new Creator<PriceAlert>() {
        @Override
        public PriceAlert createFromParcel(Parcel in)
        {
            return new PriceAlert(in);
        }

        @Override
        public PriceAlert[] newArray(int size)
        {
            return new PriceAlert[size];
        }
    };

    public boolean match(Double rate, double currentTokenPrice) {
        return (getAbove() && currentTokenPrice * rate > Double.parseDouble(getValue())) ||
                (!getAbove() && currentTokenPrice * rate < Double.parseDouble(getValue()));
    }
}
