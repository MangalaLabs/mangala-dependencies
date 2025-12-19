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

import com.alphawallet.app.service.TickerService;

public class CurrencyItem implements Parcelable
{
    private String code;
    private String name;
    private final String symbol;
    private int flag = -1;
    public boolean isSelected;

    public CurrencyItem(String code, String name, String symbol, int flag) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.flag = flag;
    }

    public CurrencyItem(Parcel in)
    {
        name = in.readString();
        code = in.readString();
        symbol = in.readString();
        flag = in.readInt();
        isSelected = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(symbol);
        dest.writeInt(flag);
        dest.writeInt(isSelected?1:0);
    }

    public static final Creator<CurrencyItem> CREATOR = new Creator<CurrencyItem>() {
        @Override
        public CurrencyItem createFromParcel(Parcel in) {
            return new CurrencyItem(in);
        }

        @Override
        public CurrencyItem[] newArray(int size) {
            return new CurrencyItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getFlag() {
        return flag;
    }

    public String getCurrencyText(double value) {
        return TickerService.getCurrencyWithoutSymbol(value) + " " + getCode();
    }
}
