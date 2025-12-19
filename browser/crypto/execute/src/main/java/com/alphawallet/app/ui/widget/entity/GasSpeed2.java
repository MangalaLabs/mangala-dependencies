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

import com.alphawallet.app.entity.EIP1559FeeOracleResult;

import java.math.BigInteger;

/**
 * Created by JB on 20/01/2022.
 */
public class GasSpeed2 implements Parcelable
{
    public final String speed;
    public long seconds;
    public final EIP1559FeeOracleResult gasPrice;

    public GasSpeed2(String speed, long seconds, EIP1559FeeOracleResult gasPrice)
    {
        this.speed = speed;
        this.seconds = seconds;
        this.gasPrice = gasPrice;
    }

    public GasSpeed2(Parcel in)
    {
        speed = in.readString();
        seconds = in.readLong();
        gasPrice = in.readParcelable(EIP1559FeeOracleResult.class.getClassLoader());
    }

    public GasSpeed2(String speed, long seconds, BigInteger gasPrice)
    {
        this.speed = speed;
        this.seconds = seconds;
        this.gasPrice = new EIP1559FeeOracleResult(gasPrice, BigInteger.ZERO, BigInteger.ZERO);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(speed);
        dest.writeLong(seconds);
        dest.writeParcelable(gasPrice, flags);
    }

    public static final Creator<GasSpeed2> CREATOR = new Creator<GasSpeed2>() {
        @Override
        public GasSpeed2 createFromParcel(Parcel in) {
            return new GasSpeed2(in);
        }

        @Override
        public GasSpeed2[] newArray(int size) {
            return new GasSpeed2[size];
        }
    };
}
