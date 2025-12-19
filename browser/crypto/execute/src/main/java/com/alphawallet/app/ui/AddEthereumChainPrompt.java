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

package com.alphawallet.app.ui;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.StandardFunctionInterface;
import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject;
import com.alphawallet.app.widget.FunctionButtonBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class AddEthereumChainPrompt extends BottomSheetDialog implements StandardFunctionInterface {

    interface AddChainListener {
        void onAdd(WalletAddEthereumChainObject chainObject);
    }

    private final WalletAddEthereumChainObject chainObject;

    private final AddChainListener listener;

    public AddEthereumChainPrompt(@NonNull @NotNull Context context, WalletAddEthereumChainObject chainObject, AddChainListener listener) {
        super(context);
        setContentView(R.layout.dialog_add_ethereum_chain);
        this.chainObject = chainObject;
        this.listener = listener;

        TextView message = findViewById(R.id.message);
        message.setText(context.getString(R.string.add_chain_dialog_message, chainObject.chainName, String.valueOf(chainObject.getChainId())));

        FunctionButtonBar functionBar = findViewById(R.id.layoutButtons);
        functionBar.setupFunctions(this, new ArrayList<>(Collections.singletonList(R.string.action_enable_switch_reload)));
        functionBar.revealButtons();
    }

    @Override
    public void handleClick(String action, int id) {
        // add custom chain
        listener.onAdd(chainObject);
    }
}
