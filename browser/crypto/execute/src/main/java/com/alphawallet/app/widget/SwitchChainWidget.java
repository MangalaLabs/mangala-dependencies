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
import android.widget.TextView;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.NetworkInfo;

public class SwitchChainWidget extends LinearLayout
{
    private final TokenIcon oldChainLogo;
    private final TokenIcon newChainLogo;
    private final ChainName oldChainName;
    private final ChainName newChainName;
    private final TextView textMessage;

    public SwitchChainWidget(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);

        inflate(context, R.layout.item_switch_chain, this);
        oldChainLogo = findViewById(R.id.logo_old);
        newChainLogo = findViewById(R.id.logo_new);
        oldChainName = findViewById(R.id.name_old_chain);
        newChainName = findViewById(R.id.name_new_chain);
        textMessage = findViewById(R.id.text_message);
    }

    public void setupSwitchChainData(NetworkInfo oldNetwork, NetworkInfo newNetwork)
    {
        String message = getContext().getString(R.string.request_change_chain, newNetwork.name, String.valueOf(newNetwork.chainId));
        if (newNetwork.hasRealValue() && !oldNetwork.hasRealValue())
        {
            message += "\n" + getContext().getString(R.string.warning_switch_to_main);
        }
        else if (!newNetwork.hasRealValue() && oldNetwork.hasRealValue())
        {
            message += "\n" + getContext().getString(R.string.warning_switching_to_test);
        }

        oldChainLogo.bindData(oldNetwork.chainId);
        newChainLogo.bindData(newNetwork.chainId);
        oldChainName.setChainID(oldNetwork.chainId);
        newChainName.setChainID(newNetwork.chainId);
        textMessage.setText(message);
    }

}
