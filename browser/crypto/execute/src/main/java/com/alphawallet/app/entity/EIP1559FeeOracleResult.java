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

import com.alphawallet.app.util.BalanceUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by JB on 20/01/2022.
 */
public class EIP1559FeeOracleResult implements Parcelable
{
    public final BigInteger maxFeePerGas;
    public final BigInteger maxPriorityFeePerGas;
    public final BigInteger baseFee;

    public EIP1559FeeOracleResult(BigInteger maxFee, BigInteger maxPriority, BigInteger base)
    {
        maxFeePerGas = minOneGwei(maxFee);
        maxPriorityFeePerGas = minOneGwei(maxPriority);
        baseFee = base;
    }

    public EIP1559FeeOracleResult(EIP1559FeeOracleResult r)
    {
        maxFeePerGas = r.maxFeePerGas;
        maxPriorityFeePerGas = r.maxPriorityFeePerGas;
        baseFee = r.baseFee;
    }

    protected EIP1559FeeOracleResult(Parcel in)
    {
        maxFeePerGas = new BigInteger(in.readString(), 16);
        maxPriorityFeePerGas = new BigInteger(in.readString(), 16);
        baseFee = new BigInteger(in.readString(), 16);
    }

    public static final Creator<EIP1559FeeOracleResult> CREATOR = new Creator<EIP1559FeeOracleResult>() {
        @Override
        public EIP1559FeeOracleResult createFromParcel(Parcel in) {
            return new EIP1559FeeOracleResult(in);
        }

        @Override
        public EIP1559FeeOracleResult[] newArray(int size) {
            return new EIP1559FeeOracleResult[size];
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
        dest.writeString(maxFeePerGas.toString(16));
        dest.writeString(maxPriorityFeePerGas.toString(16));
        dest.writeString(baseFee.toString(16));
    }

    // Returns minimum 1 Gwei
    private BigInteger minOneGwei(BigInteger input)
    {
        return input.max(BalanceUtils.gweiToWei(BigDecimal.ONE));
    }
}
