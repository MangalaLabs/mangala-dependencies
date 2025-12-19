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

package com.alphawallet.app.entity.walletconnect;

import com.alphawallet.app.repository.entity.RealmWCSession;
import com.schoolonair.wallet.local.walletconnect.WalletConnectSession;

/**
 * Created by JB on 9/09/2020.
 */
public class WalletConnectSessionItem
{
    public final String name;
    public final String url;
    public final String icon;
    public final String sessionId;
    public final String localSessionId;
    public final long chainId;

    public WalletConnectSessionItem(WalletConnectSession s)
    {
//        name = s.getRemotePeerMeta().getName();
//        url = s.getRemotePeerMeta().getUrl();
//        icon = s.getRemotePeerMeta().getIcons().size() > 0 ? s.getRemotePeerMeta().getIcons().get(0) : null;
//        sessionId = s.getSession().getTopic();
        name = "";
        url = "";
        icon = "";
        sessionId = "";

        localSessionId = s.getRemotePeerId();
        chainId = s.getChainId() == 0 ? 1 : s.getChainId(); //older sessions without chainId set must be mainnet
    }
}
