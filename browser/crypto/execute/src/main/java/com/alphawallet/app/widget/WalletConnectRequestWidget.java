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

package com.alphawallet.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.alphawallet.app.R;
import com.alphawallet.app.repository.EthereumNetworkBase;
import com.alphawallet.app.ui.widget.entity.WalletConnectWidgetCallback;
import com.trustwallet.walletconnect.models.WCPeerMeta;

import timber.log.Timber;

public class WalletConnectRequestWidget extends LinearLayout {

    private long chainIdOverride;
    private WalletConnectWidgetCallback callback;

    private DialogInfoItem website;
    private DialogInfoItem network;

    public WalletConnectRequestWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflate(context, R.layout.item_wallet_connect_request, this);
        website = findViewById(R.id.info_website);
        network = findViewById(R.id.info_network);
    }

    public void setupWidget(WCPeerMeta wcPeerMeta, long chainId, WalletConnectWidgetCallback callback) {
        Timber.d("setupWidget: ");
        this.chainIdOverride = chainId;
        this.callback = callback;

        website.setLabel(getContext().getString(R.string.website_text));
        website.setMessage(wcPeerMeta.getUrl());

        network.setLabel(getContext().getString(R.string.subtitle_network));
        network.setMessage(EthereumNetworkBase.getShortChainName(chainIdOverride));
        network.setMessageTextColor(EthereumNetworkBase.getChainColour(chainIdOverride));
        network.setActionText(getContext().getString(R.string.edit));

        network.setActionListener(v -> {
            callback.openChainSelection();
        });
    }

    public void updateChain(long chainIdOverride)
    {
        this.chainIdOverride = chainIdOverride;
        network.setMessage(EthereumNetworkBase.getShortChainName(chainIdOverride));
        network.setMessageTextColor(EthereumNetworkBase.getChainColour(chainIdOverride));
    }

    public long getChainIdOverride() {
        return chainIdOverride;
    }
}
