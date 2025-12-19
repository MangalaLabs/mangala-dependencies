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

package com.alphawallet.app.viewmodel;


import androidx.lifecycle.MutableLiveData;

import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.service.TokensService;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.realm.Realm;


public class GasSettingsViewModel extends BaseViewModel {
    private final TokensService tokensService;

    private final MutableLiveData<BigInteger> gasPrice = new MutableLiveData<>();
    private final MutableLiveData<BigInteger> gasLimit = new MutableLiveData<>();


    public GasSettingsViewModel(TokensService svs) {
        this.tokensService = svs;
        gasPrice.setValue(BigInteger.ZERO);
        gasLimit.setValue(BigInteger.ZERO);
    }

    public Realm getTickerRealm()
    {
        return tokensService.getTickerRealmInstance();
    }

    public MutableLiveData<BigInteger> gasPrice() {
        return gasPrice;
    }

    public MutableLiveData<BigInteger> gasLimit() {
        return gasLimit;
    }

    public BigDecimal networkFee() {
        return new BigDecimal(gasPrice.getValue().multiply(gasLimit.getValue()));
    }

    public Token getBaseCurrencyToken(long chainId)
    {
        return tokensService.getToken(chainId, tokensService.getCurrentAddress());
    }
}
