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

import android.text.TextUtils;

import com.alphawallet.app.entity.tokendata.TokenGroup;
import com.alphawallet.token.entity.ContractAddress;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmTokenMapping extends RealmObject {

    @PrimaryKey
    public String address;  // ContractAddress String (addr-chainId) for derivative
    public String base;     // Base contract, usually main net (eg DAI)
    public int group;       // Ordinal for enum TokenGroup

    public ContractAddress getBase()
    {
        if (!TextUtils.isEmpty(base))
        {
            return new ContractAddress(base);
        }
        else
        {
            return null;
        }
    }

    public TokenGroup getGroup()
    {
        if (group >= 0 && group < TokenGroup.values().length)
        {
            return TokenGroup.values()[group];
        }
        else
        {
            return TokenGroup.ASSET;
        }
    }
}
