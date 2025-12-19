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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.alphawallet.app.C;

public class HomeReceiver extends BroadcastReceiver
{
    private final HomeCommsInterface homeCommsInterface;
    public HomeReceiver(Activity ctx, HomeCommsInterface homeCommsInterface)
    {
        ctx.registerReceiver(this, new IntentFilter(C.DOWNLOAD_READY));
        ctx.registerReceiver(this, new IntentFilter(C.REQUEST_NOTIFICATION_ACCESS));
        ctx.registerReceiver(this, new IntentFilter(C.BACKUP_WALLET_SUCCESS));
        ctx.registerReceiver(this, new IntentFilter(C.WALLET_CONNECT_REQUEST));
        this.homeCommsInterface = homeCommsInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        switch (intent.getAction())
        {
            case C.DOWNLOAD_READY:
                String message = bundle.getString("Version");
                homeCommsInterface.downloadReady(message);
                break;
            case C.REQUEST_NOTIFICATION_ACCESS:
                homeCommsInterface.requestNotificationPermission();
                break;
            case C.BACKUP_WALLET_SUCCESS:
                String keyAddress = bundle.getString("Key");
                homeCommsInterface.backupSuccess(keyAddress);
                break;
            case C.WALLET_CONNECT_REQUEST:
                String sessionId = bundle.getString("sessionid");
                homeCommsInterface.openWalletConnect(sessionId);
            default:
                break;
        }
    }
}
