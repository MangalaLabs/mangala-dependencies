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

import static com.alphawallet.app.repository.EthereumNetworkBase.COVALENT;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.alphawallet.app.repository.EthereumNetworkRepository;
import com.alphawallet.app.util.Utils;

public class NetworkInfo extends com.alphawallet.ethereum.NetworkInfo {
    private final String BLOCKSCOUT_API = "blockscout";
    private final String MATIC_API = "polygonscan";
    private final String PALM_API = "explorer.palm";

    public  String backupNodeUrl = null;
    public  String etherscanAPI = null; //This is used by the API call to fetch transactions

    public NetworkInfo(
            String name,
            String symbol,
            String rpcServerUrl,
            String etherscanUrl,
            long chainId,
            String backupNodeUrl,
            String etherscanAPI,
            boolean isCustom) {
        super(name, symbol, rpcServerUrl, etherscanUrl, chainId, isCustom);
        this.backupNodeUrl = backupNodeUrl;
        this.etherscanAPI = etherscanAPI;
    }

    public NetworkInfo(
            String name,
            String symbol,
            String rpcServerUrl,
            String etherscanUrl,
            long chainId,
            String backupNodeUrl,
            String etherscanAPI) {
        super(name, symbol, rpcServerUrl, etherscanUrl, chainId, false);
        this.backupNodeUrl = backupNodeUrl;
        this.etherscanAPI = etherscanAPI;
    }

    public String getShortName()
    {
        int index = this.name.indexOf(" (Test)");
        if (index > 0) return this.name.substring(0, index);
        else if (this.name.length() > 10) return this.symbol;
        else return this.name;
    }

    public boolean usesSeparateNFTTransferQuery()
    {
        return (etherscanAPI != null && !etherscanAPI.contains(BLOCKSCOUT_API)
                && !etherscanAPI.contains(COVALENT) && !etherscanAPI.contains(PALM_API));
    }

    @Nullable
    public Uri getEtherscanUri(String transactionHash) {
        if (etherscanUrl != null)
        {
            return Uri.parse(etherscanUrl)
                    .buildUpon()
                    .appendEncodedPath(transactionHash)
                    .build();
        }
        else
        {
            return Uri.EMPTY;
        }
    }

    public Uri getEtherscanAddressUri(String value)
    {
        if (etherscanUrl != null)
        {
            String explorer = etherscanUrl;
            if (Utils.isAddressValid(value))
            {
                explorer = explorer.substring(0, explorer.lastIndexOf("tx/"));
                explorer += "address/";
            }
            else if (!Utils.isTransactionHash(value))
            {
                return Uri.EMPTY;
            }

            return Uri.parse(explorer)
                    .buildUpon()
                    .appendEncodedPath(value)
                    .build();
        }
        else
        {
            return Uri.EMPTY;
        }
    }

    public boolean hasRealValue()
    {
        return EthereumNetworkRepository.hasRealValue(this.chainId);
    }
}
