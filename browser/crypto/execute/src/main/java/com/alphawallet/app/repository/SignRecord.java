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

package com.alphawallet.app.repository;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alphawallet.app.repository.entity.RealmWCSignElement;
import com.schoolonair.wallet.local.walletconnect.WCSignElement;

import java.nio.charset.StandardCharsets;

/**
 * Created by JB on 9/09/2020.
 */
public class SignRecord implements Parcelable
{
    public final long date;
    public final String type;
    public CharSequence message;

    public SignRecord(WCSignElement e)
    {
        date = e.getSignTime().getTime();
        type = e.getSignType();
        if(e.getSignMessage() != null) {
            message = new String(e.getSignMessage(), StandardCharsets.UTF_8);
        }
    }

    public static final Creator<SignRecord> CREATOR = new Creator<SignRecord>() {
        @Override
        public SignRecord createFromParcel(Parcel in) {
            return new SignRecord(in);
        }

        @Override
        public SignRecord[] newArray(int size) {
            return new SignRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected SignRecord(Parcel in)
    {
        message = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        date = in.readLong();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(message, dest, flags);
        dest.writeLong(date);
        dest.writeString(type);
    }
}
