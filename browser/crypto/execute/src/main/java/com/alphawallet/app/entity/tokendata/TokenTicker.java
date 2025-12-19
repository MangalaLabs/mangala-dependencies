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

package com.alphawallet.app.entity.tokendata;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TokenTicker implements Parcelable {
    public final String price;
    public final String priceSymbol;
    @SerializedName("percent_change_24h")
    public final String percentChange24h;
    public final String image;
    public final long updateTime;

    public TokenTicker()
    {
        price = "0";
        percentChange24h = "0.0";
        image = "";
        priceSymbol = "USD";
        updateTime = 0;
    }

    public TokenTicker(long uTime) //blank
    {
        price = "";
        percentChange24h = "";
        image = "";
        priceSymbol = "";
        updateTime = uTime;
    }

    public TokenTicker(String price, String percentChange24h, String priceSymbol, String image, long updateTime) {
        this.price = price;
        this.percentChange24h = percentChange24h;
        this.image = image;
        this.priceSymbol = priceSymbol;
        this.updateTime = updateTime;
    }

    private TokenTicker(Parcel in) {
        price = in.readString();
        percentChange24h = in.readString();
        image = in.readString();
        priceSymbol = in.readString();
        updateTime = in.readLong();
    }

    public static final Creator<TokenTicker> CREATOR = new Creator<TokenTicker>() {
        @Override
        public TokenTicker createFromParcel(Parcel in) {
            return new TokenTicker(in);
        }

        @Override
        public TokenTicker[] newArray(int size) {
            return new TokenTicker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(price);
        dest.writeString(percentChange24h);
        dest.writeString(image);
        dest.writeString(priceSymbol);
        dest.writeLong(updateTime);
    }
}
