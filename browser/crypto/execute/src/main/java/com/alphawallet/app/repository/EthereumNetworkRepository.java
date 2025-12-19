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

import static com.alphawallet.ethereum.EthereumNetworkBase.MAINNET_ID;
import static com.alphawallet.ethereum.EthereumNetworkBase.XDAI_ID;

import android.content.Context;

import com.alphawallet.app.entity.ContractLocator;
import com.alphawallet.app.entity.KnownContract;
import com.alphawallet.app.entity.NetworkInfo;
import com.alphawallet.app.entity.UnknownToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EthereumNetworkRepository extends EthereumNetworkBase
{
    private final Context context;
    private final HashMap<String, ContractLocator> popularTokens = new HashMap<>();

    public EthereumNetworkRepository(PreferenceRepositoryType preferenceRepository, Context ctx)
    {
        super(preferenceRepository, new NetworkInfo[0], true);
        context = ctx;
    }

    public static String getNodeURLByNetworkId(long networkId) {
        return EthereumNetworkBase.getNodeURLByNetworkId(networkId);
    }

    public boolean getIsPopularToken(long chainId, String address)
    {
        return popularTokens.containsKey(address.toLowerCase());
    }

    /* can't turn this one into one-liners like every other function
     * in this file, without making either EthereumNetworkBase or
     * ContractResult import android (therefore preventing their use
     * in non-Android projects) or introducing a new trivial
     * interface/class */
    public List<ContractLocator> getAllKnownContracts(List<Long> networkFilters)
    {
        if (popularTokens.size() == 0)
        {
            buildPopularTokenMap(networkFilters);
        }

        return new ArrayList<>(popularTokens.values());
    }

    //Note: There is an issue with this method - if a contract is the same address on XDAI and MAINNET_ID it needs to be refactored
    private void buildPopularTokenMap(List<Long> networkFilters)
    {
        KnownContract knownContract = readContracts();
        if (knownContract == null) return;

        if (networkFilters == null || networkFilters.contains(MAINNET_ID))
        {
            for (UnknownToken unknownToken: knownContract.getMainNet())
            {
                popularTokens.put(unknownToken.address.toLowerCase(), new ContractLocator(unknownToken.address, MAINNET_ID));
            }
        }
        if (networkFilters == null || networkFilters.contains(XDAI_ID))
        {
            for (UnknownToken unknownToken: knownContract.getXDAI())
            {
                popularTokens.put(unknownToken.address.toLowerCase(), new ContractLocator(unknownToken.address, XDAI_ID));
            }
        }
    }

    @Override
    public KnownContract readContracts()
    {
        String jsonString;
        try
        {
            InputStream is = context.getAssets().open("known_contract.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            return null;
        }

        return new Gson().fromJson(jsonString, KnownContract.class);
    }
}
