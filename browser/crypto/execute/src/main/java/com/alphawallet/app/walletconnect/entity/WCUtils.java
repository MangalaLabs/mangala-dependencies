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

package com.alphawallet.app.walletconnect.entity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.alphawallet.app.C;
import com.alphawallet.app.R;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.entity.WalletConnectActions;
import com.alphawallet.app.repository.entity.RealmWCSession;
import com.alphawallet.app.service.WalletConnectService;
import com.alphawallet.app.walletconnect.WCClient;
import com.trustwallet.walletconnect.models.WCPeerMeta;
import com.trustwallet.walletconnect.models.session.WCSession;

import java.util.ArrayList;
import java.util.Collections;

public abstract class WCUtils
{
    public static void startServiceLocal(Context context, ServiceConnection connection, WalletConnectActions action)
    {
        Intent i = new Intent(context, WalletConnectService.class);
        i.setAction(String.valueOf(action.ordinal()));
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        if (!isInBackground)
        {
            context.startService(i);
            if (connection != null) context.bindService(i, connection, Context.BIND_ABOVE_CLIENT);
        }
    }

    public static WCClient createWalletConnectSession(String name, String address, WCSession session, String peerId, String remotePeerId)
    {
        WCClient client = new WCClient();

        WCPeerMeta peerMeta = new WCPeerMeta(
                name,
                C.ALPHAWALLET_WEB,
                address,
                new ArrayList<>(Collections.singleton(C.ALPHAWALLET_LOGO_URI))
        );

        client.connect(session, peerMeta, peerId, remotePeerId);

        return client;
    }
}
