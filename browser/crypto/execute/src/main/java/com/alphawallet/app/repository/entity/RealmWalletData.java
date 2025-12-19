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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by James on 8/11/2018.
 * Stormbird in Singapore
 */
public class RealmWalletData extends RealmObject
{
    private static final int DISMISS_WARNING_IN_SETTINGS_MASK = 0xFFFFFFFE;

    @PrimaryKey
    private String address;
    private String ENSName;
    private String balance;
    private String name;
    private long lastWarning;
    private String ENSAvatar;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getENSName()
    {
        return ENSName;
    }

    public void setENSName(String ENSName)
    {
        this.ENSName = ENSName;
    }

    public String getBalance()
    {
        return balance;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastWarning()
    {
        return lastWarning;
    }
    public void setLastWarning(long lastWarning)
    {
        this.lastWarning = lastWarning & DISMISS_WARNING_IN_SETTINGS_MASK;
    }
    public boolean getIsDismissedInSettings() { return (lastWarning & 0x1) == 1; }
    public void setIsDismissedInSettings(boolean isDismissed) { lastWarning = (lastWarning&DISMISS_WARNING_IN_SETTINGS_MASK) + (isDismissed ? 0x1 : 0x0); }

    public String getENSAvatar()
    {
        return ENSAvatar;
    }

    public void setENSAvatar(String ENSAvatar)
    {
        this.ENSAvatar = ENSAvatar;
    }
}
