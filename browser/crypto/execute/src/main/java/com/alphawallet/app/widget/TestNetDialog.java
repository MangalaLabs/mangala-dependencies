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
import android.widget.Button;

import androidx.annotation.NonNull;

import com.alphawallet.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class TestNetDialog extends BottomSheetDialog
{
    private final Button confirmButton;
    private final long newChainId;

    public TestNetDialog(@NonNull Context context, long chainId, TestNetDialogCallback callback)
    {
        super(context);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.layout_dialog_testnet_confirmation);
        confirmButton = findViewById(R.id.enable_testnet_action);
        newChainId = chainId;
        setCallback(callback);
    }

    private void setCallback(TestNetDialogCallback listener)
    {
        confirmButton.setOnClickListener(v -> {
            listener.onTestNetDialogConfirmed(newChainId);
            dismiss();
        });
        setOnCancelListener(v -> listener.onTestNetDialogCancelled());
    }

    public interface TestNetDialogCallback
    {
        void onTestNetDialogClosed();

        void onTestNetDialogConfirmed(long chainId);

        default void onTestNetDialogCancelled()
        {
        }
    }
}
