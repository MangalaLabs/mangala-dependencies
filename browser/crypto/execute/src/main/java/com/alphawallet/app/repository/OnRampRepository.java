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

import android.content.Context;
import android.net.Uri;

import com.alphawallet.app.C;
import com.alphawallet.app.entity.AnalyticsProperties;
import com.alphawallet.app.entity.OnRampContract;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.service.AnalyticsServiceType;
import com.alphawallet.app.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class OnRampRepository implements OnRampRepositoryType {
    public static final String DEFAULT_PROVIDER = "Ramp";
    private static final String RAMP = "ramp";
    private static final String ONRAMP_CONTRACTS_FILE_NAME = "onramp_contracts.json";

    static
    {
        System.loadLibrary("keys");
    }

    private final Context context;
    private final AnalyticsServiceType analyticsService;

    public OnRampRepository(Context context, AnalyticsServiceType analyticsService)
    {
        this.context = context;
        this.analyticsService = analyticsService;
    }

    public static native String getRampKey();

    @Override
    public String getUri(String address, Token token)
    {
        if (token != null) {
            OnRampContract contract = getContract(token);

            AnalyticsProperties analyticsProperties = new AnalyticsProperties();
            analyticsProperties.setData(contract.getSymbol());
            analyticsService.track(C.AN_USE_ONRAMP, analyticsProperties);

            switch (contract.getProvider().toLowerCase()) {
                case RAMP:
                default:
                    return buildRampUri(address, contract.getSymbol()).toString();
            }
        } else {
            return buildRampUri(address, "").toString();
        }
    }

    @Override
    public OnRampContract getContract(Token token)
    {
        Map<String, OnRampContract> contractMap = getKnownContracts();
        OnRampContract contract = contractMap.get(token.getAddress().toLowerCase());
        if (contract != null) return contract;
        else
        {
            if (token.isEthereum()) return new OnRampContract(token.tokenInfo.symbol);
            else return new OnRampContract();
        }
    }

    private Map<String, OnRampContract> getKnownContracts()
    {
        return new Gson().fromJson(Utils.loadJSONFromAsset(context, ONRAMP_CONTRACTS_FILE_NAME),
                new TypeToken<Map<String, OnRampContract>>() {
                }.getType());
    }

    private Uri buildRampUri(String address, String symbol)
    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("buy.ramp.network")
                .appendQueryParameter("hostApiKey", getRampKey())
                .appendQueryParameter("hostLogoUrl", C.ALPHAWALLET_LOGO_URI)
                .appendQueryParameter("hostAppName", "AlphaWallet")
                .appendQueryParameter("userAddress", address);

        if (!symbol.isEmpty())
        {
            builder.appendQueryParameter("swapAsset", symbol);
        }

        return builder.build();
    }
}
