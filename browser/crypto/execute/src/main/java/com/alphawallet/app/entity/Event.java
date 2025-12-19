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

/**
 * Created by JB on 26/03/2020.
 */
public class Event implements Parcelable
{
    private final String eventText;
    private final long timeStamp;
    private final long chainId;

    @Override
    public int describeContents()
    {
        return 0;
    }

    public Event(String eventTxt, long timeStamp, long chainId)
    {
        this.eventText = eventTxt;
        this.timeStamp = timeStamp;
        this.chainId = chainId;
    }

    protected Event(Parcel in)
    {
        eventText = in.readString();
        timeStamp = in.readLong();
        chainId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(eventText);
        dest.writeLong(timeStamp);
        dest.writeLong(chainId);
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>()
    {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public String getEventText()
    {
        return eventText;
    }
    public long getTimeStamp() { return timeStamp; }
    public String getHash()
    {
        String hash = eventText + "-" + timeStamp;
        return String.valueOf(hash.hashCode());
    }
    public long getChainId() { return chainId; }
}
