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

import com.alphawallet.app.entity.WalletType;
import com.alphawallet.app.service.KeyService;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmKeyType extends RealmObject
{
    @PrimaryKey
    private String address;
    private byte type;
    private byte authLevel;
    private long lastBackup;
    private long dateAdded;
    private String modulus; //Added for future possibility that we use HD key modulus, so DB doesn't need to be re-initialised

    public String getAddress()
    {
        return address;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public WalletType getType() { return WalletType.values()[type]; }
    public void setType(WalletType type) { this.type = (byte)type.ordinal(); }

    public KeyService.AuthenticationLevel getAuthLevel() { return KeyService.AuthenticationLevel.values()[authLevel]; }
    public void setAuthLevel(KeyService.AuthenticationLevel authLevel) { this.authLevel = (byte)authLevel.ordinal(); }

    public long getLastBackup()
    {
        return lastBackup;
    }
    public void setLastBackup(long lastBackup)
    {
        this.lastBackup = lastBackup;
    }

    public long getDateAdded()
    {
        return dateAdded;
    }
    public void setDateAdded(long dateAdded)
    {
        this.dateAdded = dateAdded;
    }

    public String getKeyModulus()
    {
        return modulus;
    }

    public void setKeyModulus(String modulus)
    {
        this.modulus = modulus;
    }
}
